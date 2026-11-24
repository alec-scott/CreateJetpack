package com.possible_triangle.create_jetpack.compat

import com.zurrtum.create.AllItemTags
import com.zurrtum.create.content.equipment.armor.BacktankUtil
import eu.pb4.trinkets.api.TrinketsApi
import net.minecraft.world.item.ItemStack

// 26.1 port: Trinkets Updated support. The jetpacks go in the "chest/back" slot (data/trinkets/...). Flight Lib finds
// them there for flying; this makes Create's air lookup (BacktankUtil) see pressurized air sources worn in Trinkets
// slots too, so a jetpack in the back slot drains its own air. Cosmetic slots are skipped. Only loaded with Trinkets.
object TrinketsCompat {
    fun register() {
        BacktankUtil.addBacktankSupplier { entity ->
            val stacks = mutableListOf<ItemStack>()
            TrinketsApi.getAttachment(entity).forEach { access, stack ->
                if (stack.`is`(AllItemTags.PRESSURIZED_AIR_SOURCES) && TrinketsApi.canApplyEffects(stack, access, entity)) {
                    stacks += stack
                }
            }
            stacks
        }
    }
}
