package com.possible_triangle.create_jetpack

import com.possible_triangle.create_jetpack.compat.TrinketsCompat
import com.possible_triangle.create_jetpack.config.Configs
import net.fabricmc.loader.api.FabricLoader
import com.possible_triangle.create_jetpack.index.CJBlockEntities
import com.possible_triangle.create_jetpack.index.CJBlocks
import com.possible_triangle.create_jetpack.index.CJItems
import com.possible_triangle.create_jetpack.index.CJRecipeTypes
import com.possible_triangle.create_jetpack.world.item.JetpackItem
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents
import net.fabricmc.fabric.api.util.TriState

// 26.1 Fabric port: Create Fly has no Registrate, so content is registered directly (see PORTING-26.1.md).
object CJContent {
    @JvmStatic
    fun register() {
        CJBlocks.register()
        CJItems.register()
        CJBlockEntities.register()
        if (FabricLoader.getInstance().isModLoaded("trinkets")) TrinketsCompat.register()
        CJRecipeTypes.register()

        // Upstream (NeoForge) filtered enchantments through IItemExtension.supportsEnchantment
        EnchantmentEvents.ALLOW_ENCHANTING.register { enchantment, target, _ ->
            if (target.item is JetpackItem && !Configs.SERVER.enchantments.isAllowed(enchantment)) {
                TriState.FALSE
            } else {
                TriState.DEFAULT
            }
        }
    }
}
