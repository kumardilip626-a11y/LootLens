# LootLens

LootLens is a lightweight Fabric 1.21.1 chest GUI enhancement mod.

## Features

- **Live chest search** — type an item name and non-matching chest slots are hidden.
- **Gold search outline** — keeps the vanilla-style GUI while making search easy to spot.
- **Three-dot contents menu** — opens a quick summary beside the chest GUI.
- **Item totals** — shows each item name/count plus the total number of items in the container.
- **Crash-safe storage behavior** — this build does **not** force vanilla chest or ender-chest inventories to 54 slots.

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16.0 or newer
- Fabric API
- Java 21

## Build

```bash
./gradlew build
```

The built JAR is written to `build/libs/`.

## Install

Put LootLens and Fabric API in `.minecraft/mods`, then launch Fabric 1.21.1.

## License

MIT
