# Mine Cells - Dead Cells Mod 1.2.1 alpha

## New block and game mechanic - the Cell Forge!

- Currently only avilable in creative mode
- Lets you combine items and cells to craft new items
- You can create your own recipes using datapacks! (see the [wiki entry](https://github.com/mim1q/MineCells/wiki/Custom-Cell-Forge-recipes) for more info)

## Cells

- Cell drop reenabled
- Two new config options added:
  - `allMobsDropCells` - if set to true, all living entities will drop cells
  - `cellDropWhitelist` - a list of mobs (excluding Mine Cells mobs) that will drop cells (if `allMobsDropCells` is set to false)

### `/cells` command

- `/cells get <player>` - displays the amount of cells the player has
- `/cells set <player> <amount>` - sets the amount of cells the player has
- `/cells give <player> <amount>` - adds the specified amount of cells to the player
- `/cells take <player> <amount>` - removes the specified amount of cells from the player (fails if the player doesn't have enough cells)