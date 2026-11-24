package com.possible_triangle.create_jetpack.client.compat

import com.mojang.blaze3d.vertex.PoseStack
import com.possible_triangle.create_jetpack.index.CJItems
import com.zurrtum.create.catnip.math.AngleHelper
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder
import com.zurrtum.create.client.catnip.render.CachedBuffers
import com.zurrtum.create.client.content.equipment.armor.BacktankRenderer
import com.zurrtum.create.content.equipment.armor.BacktankBlock
import com.zurrtum.create.content.equipment.armor.BacktankItem
import eu.pb4.trinkets.api.TrinketSlotAccess
import eu.pb4.trinkets.api.client.TrinketRenderer
import eu.pb4.trinkets.api.client.TrinketRendererRegistry
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.HumanoidRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.core.Direction
import net.minecraft.world.entity.Pose
import net.minecraft.world.item.ItemStack

// 26.1 port: draws a jetpack worn in a Trinkets slot on the player's back, the same way Create Fly's
// BacktankFeatureRenderer draws one worn in the chest slot (block model, spinning cogs, shaft nob, glint).
// Skipped when a backtank or jetpack is already worn in the chest slot, so two never overlap.
object JetpackTrinketRenderer : TrinketRenderer {
    fun register() {
        TrinketRendererRegistry.registerRenderer(CJItems.JETPACK, this)
        TrinketRendererRegistry.registerRenderer(CJItems.NETHERITE_JETPACK, this)
    }

    override fun submit(
        stack: ItemStack,
        slotReference: TrinketSlotAccess,
        contextModel: EntityModel<out LivingEntityRenderState>,
        ms: PoseStack,
        queue: SubmitNodeCollector,
        light: Int,
        state: LivingEntityRenderState,
        limbAngle: Float,
        limbDistance: Float,
    ) {
        val item = stack.item as? BacktankItem ?: return
        if (contextModel !is HumanoidModel<*> || state !is HumanoidRenderState) return
        if (state.pose == Pose.SLEEPING || state.chestEquipment.item is BacktankItem) return

        val blockState = item.block.defaultBlockState().setValue(BacktankBlock.HORIZONTAL_FACING, Direction.SOUTH)
        val backtank = CachedBuffers.block(blockState).light(light).extractRenderState()
        val cogs =
            CachedBuffers.partial(BacktankRenderer.getCogsModel(blockState), blockState)
                .light(light).center().rotateYDegrees(180f).uncenter().translate(0f, 0.40625f, 0.6875f)
                .rotate(AngleHelper.rad((2 * AnimationTickHolder.getRenderTime() % 360).toDouble()), Direction.EAST)
                .translate(0f, -0.40625f, -0.6875f).extractRenderState()
        val nob =
            CachedBuffers.partial(BacktankRenderer.getShaftModel(blockState), blockState)
                .light(light).translate(0f, -0.1875f, 0f).extractRenderState()

        ms.pushPose()
        contextModel.root().translateAndRotate(ms)
        contextModel.body.translateAndRotate(ms)
        ms.translate(-0.5f, 0.625f, 1.0f)
        ms.scale(1f, -1f, -1f)
        backtank.submit(ms, queue)
        cogs.submit(ms, queue)
        nob.submit(ms, queue)
        if (stack.hasFoil()) {
            val glint = RenderTypes.entityGlint()
            backtank.submit(glint, ms, queue)
            cogs.submit(glint, ms, queue)
            nob.submit(glint, ms, queue)
        }
        ms.popPose()
    }
}
