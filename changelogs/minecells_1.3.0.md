# Mine Cells - Dead Cells Mod 1.3.0

## IMPORTANT

- If you are updating from a previous version, make sure to reset the dimensions of Mine Cells! Follow the guide [here](https://github.com/mim1q/MineCells/wiki/Updating-Mine-Cells)

## Blocks

- **Prison Stone**, **Prison Cobblestone**, **Prison Bricks** and **Small Prison Bricks**: each with their respective 
  stairs, slabs and walls
- Doors, trapdoors, fences and fence gates for **Putrid Wood**
- **Spawner Rune** - alternative to a spawner. It spawns mobs when a player is nearby and waits for the mobs to be 
  killed before spawning another

## Generation

- Completely overhauled **Prison dungeon generation**
  - Much smaller, easier to navigate through and better optimized!
- **Insufferable Crypt** - new dimension, biome and dungeon (all in one)
  - The Conjunctivius's Room was moved there

## Conjunctivius Boss

- Replaced the `conjunctivius_unbreakable` tag with `conjunctivius_breakable`, so the blocks are now opt-in rather than opt-out
- Hard-coded some of Conjunctivius's parameters to make it less prone to getting bugged
- Conjunctivius can now pass through blocks when doing the charge attack

## Portals

- Changed the way portals choose where they should teleport a player
- Portals now display the name of their target dimension above themselves

## Game rules

- New Gamerule: `minecells.suffocationFix` (`false` by default) - teleports players out of a dimension if they were going to suffocate in bedrock

## Other

- Added a name for the Elevator (with a tip included)