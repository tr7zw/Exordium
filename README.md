# FasterGUI

__Render the HUD and screens at a lower framerate to speed up what's really important: the worldrendering.__

Renders the HUD at a lower fixed Framerate(configurable in the settings), freeing up cpu and gpu time for the world rendering. There is no good reason to render the hotbar at 100+ fps.

## Compatibility

__This is still work in progress software! There will be issues, please report them!__

### Affected vanilla features

- Overlays except Vignette(Pumpkin, freezing, spyglass, portal...)
- Hotbar(all parts of it)
- Crosshair
- Bossbars
- Debug Screen(F3)
- Titles
- Scoreboard
- Chat

### Tested and working with

- Sodium
- Iris
- Optifine
- AppleSkin
- Better Ping Display
- Chat Heads
- Detail Armro Bar
- wthit(What the hell is that?)
- JourneyMap
- ToroHealth Damage Indicator

### Not compatible

- VulkanMod

### Screen setting notice

This feature is still not done(disabled by default) and has mainly transparency issues. It heavily speeds up mods like REI(Roughly Enough Items), but with visual issues.

## License

This project is licensed under [``tr7zw Protective License``](LICENSE-EntityCulling).
This license does not allow others to distribute the software/derivative works(in source or binary form).
You have to contact the author to get permission for redistribution. (For example: Modpacks(that are not hosted on CurseForge), "Clients", mod hosting sites).
Keep in mind that [Githubs TOS](https://docs.github.com/en/github/site-policy/github-terms-of-service#d-user-generated-content) and [Overwolfs TOS](https://www.overwolf.com/legal/terms/) apply at their respective places. This (among other things) means you don't need to ask to include the mod in a CurseForge Modpack and that by contributing code it explicitly gets the same license as the repository.
