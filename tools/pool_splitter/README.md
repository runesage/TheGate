# Pool Splitter

Splits a pool of MTG cards into N piles of roughly equal market value, using
Scryfall prices (TCGplayer USD by default; foil/etched prices used when marked).

Input format (one card per line): `qty Name (SET) number [*F*|*E*]`

```
python3 pool_splitter.py example_pool.txt            # 2 piles
python3 pool_splitter.py example_pool.txt -n 3       # 3 piles
python3 pool_splitter.py example_pool.txt --even-count  # equal card counts too
python3 pool_splitter.py example_pool.txt --currency eur --csv split.csv
pbpaste | python3 pool_splitter.py -                 # from clipboard (macOS)
```

No dependencies beyond Python 3. Warnings (missing cards, missing foil price,
name mismatches) go to stderr.
