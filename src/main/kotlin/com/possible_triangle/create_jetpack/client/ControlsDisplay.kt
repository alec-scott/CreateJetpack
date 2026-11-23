package com.possible_triangle.create_jetpack.client

import com.possible_triangle.create_jetpack.CJConstants.MOD_ID
import com.possible_triangle.create_jetpack.config.Configs
import com.possible_triangle.flightlib.api.ControlType
import com.possible_triangle.flightlib.api.FlightKey
import com.possible_triangle.flightlib.api.IFlightApi
import com.possible_triangle.flightlib.api.IJetpack
import com.zurrtum.create.content.equipment.armor.BacktankUtil
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec2
import kotlin.math.ceil

// 26.1 port: a Fabric HUD element (upstream: NeoForge gui layer above the hotbar). The GUI pose is a 2D matrix stack
// now, blits take a render pipeline (which also handles blending) and text colours need an alpha channel.
object ControlsDisplay : HudElement {
    private val controls = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/controls.png")
    private val airIndicator = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/air_indicator.png")

    private fun spritePos(index: Int): Vec2 =
        Vec2(
            index % 2 * 16F,
            index / 2 * 16F,
        )

    private val ICONS =
        mapOf<FlightKey, (IJetpack.Context) -> ControlType>(
            FlightKey.TOGGLE_ACTIVE to { it.jetpack.activeType(it) },
            FlightKey.TOGGLE_HOVER to { it.jetpack.hoverType(it) },
        )

    override fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        tracker: DeltaTracker,
    ) {
        val mc = Minecraft.getInstance()
        if (!Configs.CLIENT.showOverlay.get()) return
        if (mc.options.hideGui) return
        val player = mc.player ?: return
        val context = IFlightApi.INSTANCE.findJetpack(player) ?: return

        val margin = 6
        val scale =
            Configs.CLIENT.overlayScale
                .get()
                .toFloat()
        val spriteWidth = 16 + margin

        val startX =
            Configs.CLIENT.overlayX
                .get()
                .let {
                    if (it >= 0) {
                        it
                    } else {
                        graphics.guiWidth() + it - 50
                    }
                }.let { it / scale }
                .toInt()

        val startY =
            Configs.CLIENT.overlayY
                .get()
                .let {
                    if (it >= 0) {
                        it
                    } else {
                        graphics.guiHeight() + it - 24
                    }
                }.let { it / scale }
                .toInt()

        fun renderSprite(
            index: Int,
            x: Int,
        ) {
            val sprite = spritePos(index)
            graphics.pose().scale(scale, scale)
            graphics.blit(RenderPipelines.GUI_TEXTURED, controls, startX + x, startY, sprite.x, sprite.y, 16, 16, 32, 32)
        }

        val engineActive = FlightKey.TOGGLE_ACTIVE.isPressed(player)

        val renderedIcons =
            ICONS
                .filterKeys { it == FlightKey.TOGGLE_ACTIVE || engineActive }
                .filterValues { getType -> getType(context) == ControlType.TOGGLE }
                .keys
                .mapIndexed { index, key ->
                    graphics.pose().pushMatrix()

                    val active = key.isPressed(player)
                    renderSprite(index + if (active) 0 else 2, spriteWidth * index)

                    val textScale = 0.5F
                    graphics.pose().scale(textScale, textScale)
                    val textMargin = (startX + 8 + spriteWidth * index) * (1 / textScale)
                    val text = Component.translatable("overlay.flightlib.control.${key.name.lowercase()}")
                    val color = if (active) 0xFFFFFFFF.toInt() else 0xFFBBBBBB.toInt()
                    graphics.centeredText(mc.font, text, textMargin.toInt(), startY * 2 + 36, color)
                    graphics.pose().popMatrix()
                }.count()

        if (engineActive) {
            graphics.pose().pushMatrix()

            graphics.pose().scale(scale, scale)

            val barWidth = 5

            fun renderBar(
                index: Int,
                barHeight: Int = 16,
                spriteOffset: Int = 0,
            ) {
                graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    airIndicator,
                    startX + spriteWidth * renderedIcons,
                    startY + (19 - barHeight) - spriteOffset,
                    (barWidth * index).toFloat(),
                    16F - barHeight - spriteOffset,
                    barWidth,
                    barHeight,
                    16,
                    16,
                )
            }

            val blink = player.level().gameTime % 20 < 5
            val airSource = BacktankUtil.getAllWithAir(player).firstOrNull() ?: ItemStack.EMPTY
            val maxAir = BacktankUtil.maxAir(airSource)
            val air = BacktankUtil.getAir(airSource)
            val barHeight = ceil((air * 14.0) / maxAir).toInt()
            val shrinking = IFlightApi.INSTANCE.currentAction(context) != null

            renderBar(1)
            if (shrinking && barHeight > 0 && blink) {
                renderBar(0, barHeight - 1, 1)
            } else {
                renderBar(0, barHeight, 1)
            }

            graphics.pose().popMatrix()
        }
    }
}
