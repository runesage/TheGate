#!/usr/bin/env python3
"""Split a pool of Magic cards into N piles of roughly equal market value.

Prices come from Scryfall (TCGplayer USD by default). Input is a decklist in
the form produced by most export tools:

    1 Vindictive Triumph (FRA) 377 *F*
    2 Verdant Kraken (FRA) 118

i.e. quantity, name, (SET), collector number, and an optional *F* (foil) or
*E* (etched) marker.

Usage:
    python3 pool_splitter.py pool.txt                # 2 piles
    python3 pool_splitter.py pool.txt -n 3           # 3 piles
    python3 pool_splitter.py pool.txt --even-count   # piles also get equal card counts
    pbpaste | python3 pool_splitter.py -             # read from stdin
    python3 pool_splitter.py pool.txt --csv out.csv  # also write a CSV

Only the Python standard library is required.
"""

import argparse
import csv
import json
import random
import re
import sys
import time
import urllib.request

API = "https://api.scryfall.com/cards/collection"
HEADERS = {
    "User-Agent": "PoolSplitter/1.0",
    "Accept": "application/json",
    "Content-Type": "application/json",
}
LINE_RE = re.compile(
    r"^\s*(?P<qty>\d+)x?\s+(?P<name>.+?)\s+\((?P<set>[A-Za-z0-9]+)\)\s+"
    r"(?P<num>\S+)(?:\s+\*(?P<finish>[FE])\*)?\s*$"
)
PRICE_KEYS = {
    "usd": {"nonfoil": "usd", "foil": "usd_foil", "etched": "usd_etched"},
    "eur": {"nonfoil": "eur", "foil": "eur_foil", "etched": "eur_foil"},
}


def parse(text):
    cards, bad = [], []
    for raw in text.splitlines():
        line = raw.strip()
        if not line or line.startswith(("#", "//")):
            continue
        m = LINE_RE.match(line)
        if not m:
            bad.append(raw)
            continue
        finish = {"F": "foil", "E": "etched"}.get(m["finish"], "nonfoil")
        for _ in range(int(m["qty"])):
            cards.append({
                "name": m["name"], "set": m["set"].lower(),
                "num": m["num"], "finish": finish,
            })
    return cards, bad


def fetch(cards):
    """Look up every distinct (set, number) via Scryfall's batch endpoint."""
    keys = sorted({(c["set"], c["num"]) for c in cards})
    found = {}
    for i in range(0, len(keys), 75):
        chunk = keys[i:i + 75]
        body = json.dumps({"identifiers": [
            {"set": s, "collector_number": n} for s, n in chunk]}).encode()
        req = urllib.request.Request(API, data=body, headers=HEADERS)
        with urllib.request.urlopen(req, timeout=30) as r:
            data = json.load(r)
        for card in data["data"]:
            found[(card["set"], card["collector_number"])] = card
        time.sleep(0.1)  # Scryfall asks for 50-100ms between requests
    return found


def price_cards(cards, found, currency):
    warnings = []
    for c in cards:
        sc = found.get((c["set"], c["num"]))
        if sc is None:
            c["price"] = 0.0
            warnings.append(f"NOT FOUND: {c['name']} ({c['set'].upper()}) {c['num']} - priced at 0")
            continue
        if sc["name"].split(" // ")[0].lower() != c["name"].split(" // ")[0].lower():
            warnings.append(f"Name mismatch: list says '{c['name']}', Scryfall says '{sc['name']}'")
        prices = sc.get("prices", {})
        p = prices.get(PRICE_KEYS[currency][c["finish"]])
        if p is None:
            # fall back to any available price for this printing
            for alt in PRICE_KEYS[currency].values():
                if prices.get(alt):
                    p = prices[alt]
                    warnings.append(f"No {c['finish']} price for {c['name']}; used {alt} ({p})")
                    break
        if p is None:
            warnings.append(f"No price at all for {c['name']} - priced at 0")
        c["price"] = float(p or 0)
        c["display"] = sc["name"]
    return warnings


def split(cards, n, even_count, seed=0, restarts=200):
    """Partition cards into n piles minimising the value spread (max - min).

    Greedy largest-first seed, then hill-climb with moves and swaps; repeated
    from randomised starts and the best result kept.
    """
    rng = random.Random(seed)
    cents = [round(c["price"] * 100) for c in cards]
    idx = list(range(len(cards)))
    cap = -(-len(cards) // n) if even_count else None  # ceil

    def score(piles):
        totals = [sum(cents[i] for i in p) for p in piles]
        return max(totals) - min(totals), sum((t - sum(totals) / n) ** 2 for t in totals)

    def greedy(order):
        piles, totals = [[] for _ in range(n)], [0] * n
        for i in order:
            choices = [k for k in range(n) if cap is None or len(piles[k]) < cap]
            k = min(choices, key=lambda k: (totals[k], len(piles[k])))
            piles[k].append(i)
            totals[k] += cents[i]
        return piles

    def improve(piles):
        best = score(piles)
        improved = True
        while improved:
            improved = False
            for a in range(n):
                for b in range(n):
                    if a == b:
                        continue
                    # move one card a -> b
                    if cap is None or len(piles[b]) < cap:
                        if not even_count or len(piles[a]) - 1 >= len(cards) // n:
                            for i in list(piles[a]):
                                piles[a].remove(i); piles[b].append(i)
                                s = score(piles)
                                if s < best:
                                    best, improved = s, True
                                else:
                                    piles[b].remove(i); piles[a].append(i)
                    # swap one card a <-> b
                    for i in list(piles[a]):
                        for j in list(piles[b]):
                            if i not in piles[a] or j not in piles[b]:
                                continue
                            piles[a].remove(i); piles[b].remove(j)
                            piles[a].append(j); piles[b].append(i)
                            s = score(piles)
                            if s < best:
                                best, improved = s, True
                            else:
                                piles[a].remove(j); piles[b].remove(i)
                                piles[a].append(i); piles[b].append(j)
        return piles, best

    order = sorted(idx, key=lambda i: -cents[i])
    best_piles, best_score = improve(greedy(order))
    for _ in range(restarts):
        if best_score[0] == 0:
            break
        o = order[:]
        # perturb: shuffle within small windows of the sorted order
        for k in range(0, len(o), 3):
            seg = o[k:k + 3]; rng.shuffle(seg); o[k:k + 3] = seg
        if rng.random() < 0.3:
            rng.shuffle(o)
        piles, s = improve(greedy(o))
        if s < best_score:
            best_piles, best_score = piles, s
    return best_piles


def fmt_line(c):
    tag = {"foil": " *F*", "etched": " *E*"}.get(c["finish"], "")
    return f"1 {c['name']} ({c['set'].upper()}) {c['num']}{tag}"


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("file", help="decklist file, or - for stdin")
    ap.add_argument("-n", "--piles", type=int, default=2, help="number of people/piles (default 2)")
    ap.add_argument("--even-count", action="store_true", help="also keep card counts equal across piles")
    ap.add_argument("--currency", choices=["usd", "eur"], default="usd")
    ap.add_argument("--seed", type=int, default=0, help="random seed for tie-breaking (default 0)")
    ap.add_argument("--csv", help="write the split to this CSV file")
    args = ap.parse_args()

    text = sys.stdin.read() if args.file == "-" else open(args.file, encoding="utf-8").read()
    cards, bad = parse(text)
    for b in bad:
        print(f"Skipping unparseable line: {b!r}", file=sys.stderr)
    if not cards:
        sys.exit("No cards parsed.")

    found = fetch(cards)
    for w in price_cards(cards, found, args.currency):
        print("WARNING:", w, file=sys.stderr)

    piles = split(cards, args.piles, args.even_count, seed=args.seed)
    sym = "$" if args.currency == "usd" else "€"
    total = sum(c["price"] for c in cards)
    print(f"Pool: {len(cards)} cards, total {sym}{total:.2f}, "
          f"target {sym}{total / args.piles:.2f} per pile\n")

    rows = []
    totals = []
    for k, pile in enumerate(piles, 1):
        pile = sorted(pile, key=lambda i: -cards[i]["price"])
        t = sum(cards[i]["price"] for i in pile)
        totals.append(t)
        print(f"=== Pile {k}: {len(pile)} cards, {sym}{t:.2f} ===")
        for i in pile:
            c = cards[i]
            print(f"  {sym}{c['price']:>8.2f}  {fmt_line(c)}")
            rows.append([k, c["name"], c["set"].upper(), c["num"], c["finish"], f"{c['price']:.2f}"])
        print()
    print(f"Spread between highest and lowest pile: {sym}{max(totals) - min(totals):.2f}")

    if args.csv:
        with open(args.csv, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f)
            w.writerow(["pile", "name", "set", "number", "finish", "price"])
            w.writerows(rows)
        print(f"Wrote {args.csv}")


if __name__ == "__main__":
    main()
