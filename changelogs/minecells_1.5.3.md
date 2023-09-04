# Mine Cells 1.5.3

## PORTED TO 1.20.1 

- Improved the look of the Conjunctivius' Tentacle swinging animaion
- Fixed a bug with Mine Cells teleporting you out of regular dimensions
- Improved the wording of the Prisoners' Quarters Doorway item description

# Mine Cells 1.5.2

- Fixed server-side crash when initializing the Mine Cells item group
- Fixed a mixin conflict crash with Lithium

# Mine Cells 1.5.1

Removed a packet mixin. It should now be fixed by other, network-fix focused mods.

# Mine Cells 1.5.0

## **IMPORTANT**

- Make sure to reset the dimensions of Mine Cells! Follow the guide [here](https://mim1q.dev/minecells/updating/)
- This version has been tested by myself but there may be unknown game-breaking bugs!  
  Proceed with caution and please report any bugs and crashes you see either on Github or through my Discord.

## Portal overhaul

- Removed old circular dimension portals
- They have been replaced with Doorways that can be broken, crafted and placed elsewhere (only in the Overworld)
- A tooltip is shown that tells you if you have already visited the Prisoners' Quarters in the area you're in.
- If you break a doorway, it will remember the area it was placed in.
- Added new Teleporters that look similar to the old portals **(Not available without cheats yet!)**

## Blocks

- Added missing blocks for the Prison Stone and Putrid Wood set:
- Prison Stone Pressure Plate, Prison Stone Button,
- Putrid Pressure Plate, Putrid Button, Putrid Sign
- Added the missing recipes for Putrid Boards
- Elevators can now be powered with hidden redstone (from below or from the side)

## Dimensions

- The dimensions are now divided into 1024x1024 areas, which cannot be crossed.
- A compass now guided you to the entry of your current dimension and area.
- Added red falling leaf particles to Promenade.
- Overhauled structure generation in the Promenade.
- Insufferable Crypt now leads back to the Overworld. The arena is also closed off by two doors that are locked until you kill the boss.
- Added a new portal room in the Promenade of The Condemned, tweaked all old portal rooms.
- Added a new Overworld Portal structure that takes you to the Prisoners' Quarters!
  Has a chance to spawn an enemy not seen before in survival - the Inquisitor

## Spawner Runes

- Spawner runes have been reworked. They are now entities, so after they're activated you can place blocks at their position.
- Only fire once per player. Cooldown between players is not yet implemented!

## Technical

- Added owo-lib as a dependency 
- New `/minecells:print_data`, `/minecells:sync_data`, `/minecells:clear_data` commands
- A few new config options, including an option to disable automatic data wiping or keep the boss entry door unlocked 
- A lot of other technical changes behind the scenes
