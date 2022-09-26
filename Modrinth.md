# Exordium

__Render the GUI and screens at a lower framerate to speed up what's really important: the worldrendering.__

Renders the GUI at a lower fixed framerate (configurable in the settings), freeing up CPU and GPU time for the world rendering. There is no good reason to render the hotbar at 100+ FPS.

## Features

- Render the gui at a lower framerate (configurable in the settings)
- Speedup gui rendering during fade animations (configurable in the settings)
- Render screens at a lower framerate (configurable in the settings)
- Pre-render sign text(glowing signs render multiple times each frame in vanilla)
- Buffer full screen name-tags to reduce the amount of draw calls in game lobbies

## Compatibility

__This is still work in progress software! There will be visual issues/compatibility issues with other mods, please report them on Github!__

### Affected vanilla features

- Overlays (except Vignette)
- Hotbar (all parts of it)
- Crosshair
- Bossbars
- Debug Screen (F3 Menu)
- Titles
- Scoreboard
- Chat
- All Screens(except the main menu)(including the pause menu, inventory, crafting, etc.)

### Tested and working with

- Sodium
- Iris
- Optifine(not recommended/officially supported)
- AppleSkin
- Better Ping Display
- Chat Heads
- Detail Armor Bar
- WTHIT (What the hell is that?)
- JourneyMap
- ToroHealth Damage Indicator
- BetterF3
- Roughly Enough Items(REI)(has small visual issues with enchants)

### Not compatible

- VulkanMod

## FAQ

### Forge support?

Currently not planned, but I might add it in the future.

### Does this work with mod xyz?

I don't know, I haven't tested it with every mod out there. If you find a mod that doesn't work with this mod, please report it on Github.

### Backport to 1.16.5/1.12.2/1.8.9?

No backports are planned. Mainly due to engine limitations in older versions.
