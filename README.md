# Simple XP Config

- Forge 1.16.5
- Commissioned by *viiizee*  

Makes sources of experience points more configureable. The first time you open a world, it will generate the default config files which do not change vanilla behaviour. 

## Spawner Mobs

- edit the file `world/serverconfig/xpspawnercontrol-server.toml`

Allows you to stop mobs spawned by mob spawners from dropping experience. The config file has two values. 

- `isBlacklist`: Whether the entity list is a blacklist (true) or a whitelist (false) of removing experience drops
- `entityList`: A list of all of the entity registry names which this mod will affect (uses json formating, e.g. ["minecraft:zombie"])

This functionality is a direct port of [Xp Spawner Control](https://www.curseforge.com/minecraft/mc-mods/xp-spawner-control) (1.12, GPL) by bright_spark

## Block Breaking

- edit the file `world/serverconfig/blockxpdrops.json`

Lets you add xp drops to certain blocks. These xp drops will not apply when you are using silk touch.

The file is a list of json objects representing xp drop rules.
- `blocks`: list of the registry names of blocks this rule should apply to
- `amount`: the amount of xp to drop
- `chance`: the probability for that xp to drop when a block meeting the conditions is broken. A decimal number where 1 means always and 0 means never (defaults to 1). 
- `state`: an object with the block state properties that must be true for this rule to apply. Only supports integer properties (like crops `age`, cauldren/composter `level`, cake `bites`, turtle `eggs`). This can be left off to ignore the blockstate. 

Example that makes grown crops drop xp sometimes:

```json
[
  {
    "blocks": ["minecraft:potatoes", "minecraft:carrots", "minecraft:wheat"],
    "amount": 5,
    "chance": 0.33,
    "state": {
      "age": 7
    }
  },
  {
    "blocks": ["minecraft:nether_wart", "minecraft:beetroots"],
    "amount": 3,
    "chance": 0.5,
    "state": {
      "age": 3
    }
  }
]
```