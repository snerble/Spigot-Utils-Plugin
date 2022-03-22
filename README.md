# Spigot-Utils-Plugin
First attempt at a spigotmc plugin for Minecraft 1.16.5. 

- [Spigot-Utils-Plugin](#spigot-utils-plugin)
	- [What is it](#what-is-it)
	- [Commands](#commands)
	- [Features](#features)

## What is it

This plugin provides various utility commands, detailed [here](#commands). These commands are mainly to simplify things *(e.g. /daytime)* or to provide quality-of-life improvements *(e.g. /autotorch)*.

This plugin will likely change drastically and frequently. Future changes to things such as the plugin structure, plugin name or repository name should be expected.

**Note:**
- The plugin name *'Utils'* is a placeholder and is subject to change.
- The features in this plugin may be split into submodules.

## Commands

Currently, this plugin provides the following commands:

Command|Description
:---|:---
`/autocraft`|Manages the autocraft feature.
`/autotorch`|Manages the autotorch feature.
`/daytime`|Advances time to the next morning.
`/setcolor`|Sets the color of this plugin's chat messages.
`/slimechunks`|Lists all slime chunks in a given radius.
`/spawn`|Teleports the player to their spawn point.
`/tc`|Automatically cuts down the rest of a tree.
`/xpMult`|Configures the multiplier for the amount of xp picked up by players.

**Note**: A help command is yet to be implemented.

## Features

Besides commands, this plugin also has 'active' features which are usually triggered by events. These features include:

Feature|Description
:---|:---
AutoCraft|When enabled, automatically crafts certain items when possible.
AutoTorch|When enabled, automatically places torches when the light level gets low.

More features are planned for the future, including a feature that limits how many items you can pick up when enabled, allowing the player to leave any cobblestone on the ground for instance. These features will be implemented once suitable command templates have been decided and implemented *(which hasn't happened yet because it turns out to be kinda difficult to make a convenient template when your list.add and list.remove have different arguments)*.
