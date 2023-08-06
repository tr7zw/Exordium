<img src="https://raw.githubusercontent.com/tr7zw/Exordium/1.19/Shared/src/main/resources/assets/exordium/icon.png" align="right" width=200>

# Exordium

Exordium is designed to render the GUI and screens at a lower framerate, prioritizing the speed of the world rendering.

[![Chat with me on Discord](https://tr7zw.dev/curse/Discord-long.png)](https://discord.gg/2wKH8yeThf)
[![Support me on Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/tr7zw)

## Features
- Render the GUI at a lower framerate (configurable in the settings).
- Speed up GUI rendering during fade animations (configurable in the settings).
- Render screens at a lower framerate (configurable in the settings).
- Pre-render sign text (glowing signs render multiple times each frame in vanilla).
- Buffer full screen name-tags to reduce the number of draw calls in-game lobbies.

## Compatibility
**Note:** This is a work-in-progress software. Visual and compatibility issues may arise with other mods.

### Affected vanilla features:
- Overlays (except Vignette)
- Hotbar (all parts of it)
- Crosshair
- Bossbars
- Debug Screen (F3 Menu)
- Titles
- Scoreboard
- Chat
- All Screens (except the main menu) including the pause menu, inventory, crafting, etc.

### Tested and works with:
- Sodium
- Iris
- Optifine (not recommended/officially supported)
- AppleSkin
- Better Ping Display
- Chat Heads
- Detail Armor Bar
- WTHIT (What the hell is that?)
- JourneyMap
- ToroHealth Damage Indicator
- BetterF3

### Not compatible with:
- VulkanMod
- High-resolution fonts on signs (disable buffering in the config)
- Caxton (same issue as above)
- Inventorio
- MiniHUD (bug on their side)
- Canvas
- Many other mods that render their own GUI elements.

## FAQ
**Forge support?**  
No.

**Does this work with mod XYZ?**  
It may or may not. Please report incompatible mods on GitHub.

**Backport to older versions?**  
No backports are planned. The mod supports Minecraft versions 1.18.0-1.18.2 and 1.19.1-1.20.1 and is made for the Fabric mod loader.

## Screen setting notice
This feature is currently in development (disabled by default) and may have some transparency issues. This can notably speed up mods like REI, but visual discrepancies may be present.

## Downloads
- [Curseforge](https://www.curseforge.com/minecraft/mc-mods/exordium)
- [Modrinth](https://modrinth.com/mod/exordium)

## License
Exordium is licensed under the [tr7zw Protective License](https://github.com/tr7zw/Exordium/blob/1.20/LICENSE). You must contact the author for redistribution permissions outside of the terms provided by [GitHub TOS](https://docs.github.com/en/github/site-policy/github-terms-of-service#d-user-generated-content) and [Overwolf's TOS](https://www.overwolf.com/legal/terms/). This includes, but is not limited to, redistributing in modpacks not hosted on CurseForge, other clients, or mod hosting sites. Contributions to this project inherit the project's existing license.
