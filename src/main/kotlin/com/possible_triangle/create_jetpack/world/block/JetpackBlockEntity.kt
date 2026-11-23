package com.possible_triangle.create_jetpack.world.block

import com.possible_triangle.create_jetpack.index.CJBlockEntities
import com.zurrtum.create.content.equipment.armor.BacktankBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

/**
 * 26.1 port: upstream constructed Create's BacktankBlockEntity with its own block entity type. Create Fly's constructor
 * hard-codes create:backtank, so this subclass reports create_jetpack:jetpack instead (saving, renderers, visuals and
 * tickers all go through getType()).
 */
class JetpackBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : BacktankBlockEntity(pos, state) {
    override fun getType(): BlockEntityType<*> = CJBlockEntities.JETPACK_BLOCK_ENTITY

    override fun typeHolder(): Holder<BlockEntityType<*>> = CJBlockEntities.JETPACK_BLOCK_ENTITY.builtInRegistryHolder()

    override fun isValidBlockState(state: BlockState): Boolean = CJBlockEntities.JETPACK_BLOCK_ENTITY.isValid(state)
}
