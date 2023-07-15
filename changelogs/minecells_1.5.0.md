# MineCells 1.5.0

## **IMPORTANT**

- If you are updating from an old version, make sure to reset the dimensions of Mine Cells! Follow the guide [here](https://github.com/mim1q/MineCells/wiki/Updating-Mine-Cells)

## Portal overhaul

- Removed old circular dimension portals
- They have been replaced with Doorways that can be broken, crafted and placed elsewhere (only in the Overworld)
- A tooltip is shown that tells you if you have already visited the Prisoners' Quarters in the area you're in.
- Added new Teleporters that look similar to the old portals **(Not available without cheats yet!)**
- Added a new portal room in the Promenade of The Condemned, tweaked all old portal rooms.
- Added a new Overworld Portal structure that takes you to the Prisoners' Quarters! 
  Has a chance to spawn an enemy not seen before in survival - the Inquisitor

## Blocks

- Added missing blocks for the Prison Stone and Putrid Wood set:
- Prison Stone Pressure Plate, Prison Stone Button,
- Putrid Pressure Plate, Putrid Button, Putrid Sign

## Spawner Runes

- Spawner runes have been reworked. They are now entities, so after they're activated you can place stuff in their place.
- Only fire once per player by default. Can be changed in the config (TODO)

## Technical

- Added owo-lib as a dependency
- New `/minecells:print_data`, `/minecells:sync_data`, `/minecells:clear_data` commands
- A lot of other technical changes behind the scenes
