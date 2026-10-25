# The Gate

A Stargate plugin for Paper servers: build gates, dial them from a DHD, and
walk through to another gate in the same world, another world, or another
server behind a Velocity proxy.

This is a revival of [The_Gate](https://github.com/Badading/The_Gate) by
EndMySuffering, donner and lukas123313, updated for modern Paper. See
[NOTICE.md](NOTICE.md) for credits and license terms.

## Requirements

- Paper 26.1.2
- Java 25

No other plugins are needed. PacketEvents is bundled inside the jar.

## Building

```sh
./gradlew build
```

The plugin jar is written to `build/libs/TheGate-<version>.jar`. Gradle
downloads a Java 25 toolchain automatically if one is not installed. Every
push also builds the jar on GitHub Actions, where it can be downloaded from
the run's artifacts.

## Installing

Copy the jar into the server's `plugins` folder and start the server. The
plugin creates `plugins/The_Gate/config.yml` and `lang.yml`, and saves gates
to a SQLite file by default.

## Velocity networks

Gates can link servers behind a Velocity proxy. All backend servers share
one MySQL or MariaDB database, and the proxy moves players between servers
when they walk through a gate.

1. **Velocity** (`velocity.toml`): use modern forwarding and keep the
   BungeeCord plugin message channel enabled (it is on by default). The
   plugin uses that channel to ask the proxy to move players.

   ```toml
   player-info-forwarding-mode = "MODERN"
   bungee-plugin-message-channel = true

   [servers]
   survival = "127.0.0.1:25566"
   creative = "127.0.0.1:25567"
   ```

2. **Each Paper backend** (`config/paper-global.yml`): enable Velocity
   support with the secret from Velocity's `forwarding.secret`.

   ```yaml
   proxies:
     velocity:
       enabled: true
       online-mode: true
       secret: '<contents of forwarding.secret>'
   ```

3. **The Gate on each backend** (`plugins/The_Gate/config.yml`): point every
   server at the same database, turn on network mode, and set `ServerName`
   to that server's name in `velocity.toml`.

   ```yaml
   PluginSettings:
     Savefile: MYSQL
     Username: gate
     Password: <password>
     Path: //127.0.0.1:3306/the_gate

   BungeeSettings:
     UseBungee: TRUE
     ServerName: survival
   ```

   The settings are still named `BungeeSettings` for compatibility with
   existing configs. They work with Velocity.

When a gate is created, changed or removed, the other servers are told
through the proxy and can dial it right away. Those messages travel over
player connections, so a server with nobody online catches up when its first
player joins. The periodic database sync (`AutoSyncDatabase`, every 10
minutes by default) and `/thegate syncdb` remain as fallbacks.
