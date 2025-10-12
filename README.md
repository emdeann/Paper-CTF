# Capture the Flag - Paper

A basic Paper plugin for hosting a game of Capture the Flag. It requires no configuration before use.

## Build Steps

This project uses the [Gradle](<https://gradle.org/>) build system. Build the plugin JAR with:

```bash
./gradlew build
```

You can also use

```bash
./gradlew run
```

to build the project and immediately use it in a locally hosted server. (If you choose this option, you will need to
agree to the EULA in `run/` after running for the first time).

## Compatibility

The plugin is tested on the latest stable Paper version, 1.21.8.

## Quick Start

There are a few ingame steps to start a CTF game:

1. An admin (opped player) must run `/ctf setflag red` and `/ctf setflag blue`. This will set the base location for each
   team to the executor's location.
2. All players who wish to join should run `/ctf join red` or `/ctf join blue`. `/ctf leave` can be used to leave a team
   if needed.
3. Once both teams have players, run `/ctf start` to start the game.
4. If needed, run `/ctf stop` to prematurely stop the game.