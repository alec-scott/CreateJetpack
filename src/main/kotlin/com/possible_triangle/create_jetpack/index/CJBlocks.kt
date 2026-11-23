package com.possible_triangle.create_jetpack.index

import com.possible_triangle.create_jetpack.CJConstants.MOD_ID
import com.possible_triangle.create_jetpack.world.block.JetpackBlock
import com.zurrtum.create.AllBlocks
import com.zurrtum.create.api.stress.BlockStressValues
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour

object CJBlocks {
    // Same base properties Create Fly uses for its copper / netherite backtanks
    @JvmField
    val JETPACK: JetpackBlock = register("jetpack", BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK))

    @JvmField
    val NETHERITE_JETPACK: JetpackBlock =
        register("netherite_jetpack", BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK))

    val all: List<Block> get() = listOf(JETPACK, NETHERITE_JETPACK)

    private fun register(
        name: String,
        properties: BlockBehaviour.Properties,
    ): JetpackBlock {
        val key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name))
        val block = Registry.register(BuiltInRegistries.BLOCK, key, JetpackBlock(properties.setId(key)))
        BlockStressValues.IMPACTS.register(block) { BlockStressValues.getImpact(AllBlocks.COPPER_BACKTANK) }
        return block
    }

    internal fun register() {
        // Load this class
    }
}
