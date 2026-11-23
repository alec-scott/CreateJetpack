package com.possible_triangle.create_jetpack.client

import com.possible_triangle.create_jetpack.CJConstants.MOD_ID
import com.possible_triangle.create_jetpack.config.SyncConfigMessage
import com.possible_triangle.create_jetpack.index.CJBlockEntities
import com.possible_triangle.create_jetpack.index.CJBlocks
import com.possible_triangle.create_jetpack.index.CJItems
import com.possible_triangle.create_jetpack.world.block.JetpackBlockEntity
import com.zurrtum.create.client.AllBlockEntityRenders
import com.zurrtum.create.client.AllItemTooltips
import com.zurrtum.create.client.AllPartialModels
import com.zurrtum.create.client.content.equipment.armor.BacktankRenderer
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual
import com.zurrtum.create.client.flywheel.lib.model.Models
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.resources.Identifier

object CJClient {
    @JvmStatic
    fun register() {
        ClientPlayNetworking.registerGlobalReceiver(SyncConfigMessage.TYPE.type()) { message, _ -> message.handle() }

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.HOTBAR,
            Identifier.fromNamespaceAndPath(MOD_ID, "jetpack_controls"),
            ControlsDisplay,
        )

        // Create-style item descriptions (upstream: Registrate tooltip modifier factory)
        AllItemTooltips.register(CJItems.JETPACK)
        AllItemTooltips.register(CJItems.NETHERITE_JETPACK)

        // As upstream: Create's backtank renderer, and a rotating shaft visual matching the jetpack's material
        AllBlockEntityRenders.visual(
            CJBlockEntities.JETPACK_BLOCK_ENTITY,
            ::BacktankRenderer,
            SimpleBlockEntityVisualizer.Factory<JetpackBlockEntity> { ctx, be, partialTick ->
                val shaft =
                    if (be.blockState.`is`(CJBlocks.NETHERITE_JETPACK)) {
                        AllPartialModels.NETHERITE_BACKTANK_SHAFT
                    } else {
                        AllPartialModels.COPPER_BACKTANK_SHAFT
                    }
                SingleAxisRotatingVisual(ctx, be, partialTick, Models.chunkPartial(shaft))
            },
        )
    }
}
