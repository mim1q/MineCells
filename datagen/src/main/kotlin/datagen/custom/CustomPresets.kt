package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.common.CommonDropPresets
import tada.lib.presets.common.CommonModelPresets
import tada.lib.resources.blockstate.BlockState
import tada.lib.resources.blockstate.BlockStateModel
import tada.lib.resources.blockstate.BlockStateModel.Rotation
import tada.lib.resources.model.ParentedModel
import tada.lib.util.Id

object CustomPresets {
  fun torch(id: String, texture: String) = Preset {
    val (ns, name) = Id(id)
    add("${name}_torch", ParentedModel.block("minecells:block/template/colored_torch").texture("flame", Id(texture).toString()))
    add("${name}_torch_standing", ParentedModel.block("minecells:block/template/colored_torch_standing").texture("flame", Id(texture).toString()))
    add("${name}_torch", BlockState.create {
      variant("facing=north, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.NONE))
      variant("facing=east, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.CW_90))
      variant("facing=south, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.CW_180))
      variant("facing=west, standing=false", BlockStateModel("$ns:block/${name}_torch", yRot = Rotation.CW_270))
      variant("standing=true", BlockStateModel("$ns:block/${name}_torch_standing", yRot = Rotation.NONE))
    })
    add("${name}_torch", ParentedModel.item("$ns:block/${name}_torch_standing"))
    add(CommonDropPresets.simpleDrop("$ns:${name}_torch"))
  }

  fun corpse(id: String, models: Boolean = false) = Preset {
    val (ns, name) = Id(id)
    listOf("hanged_", "").forEach {
      if (models) {
        add("$it$name", ParentedModel.block("minecells:block/${it}corpse").texture("0", "$ns:block/$name"))
      }
      add(CommonModelPresets.horizontallyRotateableBlock("$ns:$it$name"))
    }
  }

  fun grassBlock(id: String, stoneId: String) = Preset {
    val (ns, name) = Id(id)
    val (sNs, sName) = Id(stoneId)
    add(name, ParentedModel.block("minecraft:block/grass_block")
      .texture("particle", "$sNs:block/$sName")
      .texture("bottom", "$sNs:block/$sName")
      .texture("side", "$sNs:block/$sName")
      .texture("top", "minecraft:block/grass_block_top")
      .texture("overlay", "$ns:block/${name}_overlay"))
    add(name, BlockState.createSingle("$ns:block/$name"))
    add(CommonModelPresets.itemBlockModel(id))
    add(CommonDropPresets.silkTouchDrop(id, stoneId, id))
  }
}