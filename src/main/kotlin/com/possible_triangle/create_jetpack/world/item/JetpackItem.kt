package com.possible_triangle.create_jetpack.world.item

import com.possible_triangle.create_jetpack.config.Configs
import com.possible_triangle.flightlib.api.ControlType
import com.possible_triangle.flightlib.api.FlightAction
import com.possible_triangle.flightlib.api.IJetpack
import com.possible_triangle.flightlib.api.IJetpack.Context
import com.possible_triangle.flightlib.api.sources.CuriosSource
import com.possible_triangle.flightlib.api.sources.EquipmentSource
import com.zurrtum.create.content.equipment.armor.BacktankItem
import com.zurrtum.create.content.equipment.armor.BacktankUtil
import com.zurrtum.create.foundation.item.LayeredArmorItem
import com.zurrtum.create.infrastructure.particle.AirParticleData
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3

// 26.1 port: Create Fly's BacktankItem is a BlockItem carrying an equippable component (armour material, slot and
// equipment asset come from AllArmorMaterials.chest(...) in CJItems), instead of an ArmorItem plus placeable item.
// Enchantment filtering moved to CJContent (Fabric EnchantmentEvents).
open class JetpackItem(
    block: Block,
    properties: Properties,
) : BacktankItem(block, properties),
    IJetpack {
    override fun hoverSpeed(context: Context): Double = Configs.SERVER.hoverSpeed

    override fun verticalSpeed(context: Context): Double = Configs.SERVER.verticalSpeed

    override fun activeType(context: Context): ControlType = ControlType.TOGGLE

    override fun hoverType(context: Context): ControlType = ControlType.TOGGLE

    override fun horizontalSpeed(context: Context): Double = Configs.SERVER.horizontalSpeed

    override fun acceleration(context: Context): Double = Configs.SERVER.acceleration

    override fun swimModifier(context: Context): Double = Configs.SERVER.swimModifier

    override fun elytraBoost(): Double = Configs.SERVER.elytraBoost

    private val thrusters =
        listOf(-0.35, 0.35).map { offset ->
            Vec3(offset, 0.7, -0.5)
        }

    override fun getThrusters(context: Context) = thrusters

    override fun onUse(
        context: Context,
        action: FlightAction,
    ) {
        if (context.level.gameTime % 20 != 0L) return
        BacktankUtil.canAbsorbDamage(context.entity, secondsPerTank(action))
    }

    private fun secondsPerTank(action: FlightAction): Int =
        if (action == FlightAction.HOVER || action == FlightAction.DOWN) {
            Configs.SERVER.secondsPerTankHover
        } else {
            Configs.SERVER.secondsPerTank
        }

    override fun isValid(context: Context): Boolean =
        when (val source = context.source) {
            is EquipmentSource -> source.slot == EquipmentSlot.CHEST
            is CuriosSource -> true
            else -> false
        }

    override fun isUsable(context: Context): Boolean {
        val tank = BacktankUtil.getAllWithAir(context.entity).firstOrNull() ?: return false
        val air = BacktankUtil.getAir(tank)
        val cost = BacktankUtil.maxAirWithoutEnchants().toFloat() / secondsPerTank(FlightAction.UP)
        return air >= cost
    }

    override fun createParticles(): ParticleOptions =
        if (Configs.CLIENT.spawnSnowParticles) {
            ParticleTypes.SNOWFLAKE
        } else {
            AirParticleData(0F, 0.01F)
        }

    override fun heightAboveGroundLimit(context: Context): Int? = Configs.SERVER.heightAboveGroundLimit

    class Layered(
        block: Block,
        properties: Properties,
        private val layer: Identifier,
    ) : JetpackItem(block, properties),
        LayeredArmorItem {
        override fun getLayerTexture(): Identifier = layer
    }
}
