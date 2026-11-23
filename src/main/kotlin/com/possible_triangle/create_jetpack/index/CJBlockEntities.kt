package com.possible_triangle.create_jetpack.index

import com.possible_triangle.create_jetpack.CJConstants.MOD_ID
import com.possible_triangle.create_jetpack.world.block.JetpackBlockEntity
import com.zurrtum.create.AllBlockEntityTypes
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.entity.BlockEntityType

object CJBlockEntities {
    @JvmField
    val JETPACK_BLOCK_ENTITY: BlockEntityType<JetpackBlockEntity> =
        Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "jetpack"),
            FabricBlockEntityTypeBuilder.create(::JetpackBlockEntity, CJBlocks.JETPACK, CJBlocks.NETHERITE_JETPACK).build(),
        )

    internal fun register() {
        // Create Fly's BacktankBlockEntity constructor always passes create:backtank to BlockEntity, which validates
        // the block state against it. JetpackBlockEntity reports its own type afterwards (see there).
        CJBlocks.all.forEach { AllBlockEntityTypes.BACKTANK.addValidBlock(it) }
    }
}
