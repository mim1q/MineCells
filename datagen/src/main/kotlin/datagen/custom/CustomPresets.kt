package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.common.CommonDropPresets
import tada.lib.resources.blockstate.BlockState
import tada.lib.resources.blockstate.BlockStateModel
import tada.lib.resources.blockstate.BlockStateModel.Rotation
import tada.lib.resources.model.ParentedModel
import tada.lib.util.Id

object CustomPresets {
  fun torch(id: String, texture: String = id) = Preset {
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
}