package com.possible_triangle.create_jetpack.index

import com.possible_triangle.create_jetpack.CJConstants.MOD_ID
import com.possible_triangle.create_jetpack.world.item.JetpackItem
import com.zurrtum.create.AllCreativeModeTabs
import com.zurrtum.create.AllItems
import com.zurrtum.create.content.equipment.armor.AllArmorMaterials
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block

// 26.1 port: Create Fly's backtank is a single BlockItem (no separate "placeable" item), so the jetpacks follow suit
// and the upstream jetpack_placeable / netherite_jetpack_placeable helper items are gone.
object CJItems {
    @JvmField
    val JETPACK: JetpackItem =
        register(
            "jetpack",
            CJBlocks.JETPACK,
            AllArmorMaterials.chest(AllArmorMaterials.COPPER).rarity(Rarity.RARE),
        ) { block, props -> JetpackItem(block, props) }

    @JvmField
    val NETHERITE_JETPACK: JetpackItem =
        register(
            "netherite_jetpack",
            CJBlocks.NETHERITE_JETPACK,
            AllArmorMaterials.chest(AllArmorMaterials.NETHERITE).rarity(Rarity.EPIC).fireResistant(),
        ) { block, props ->
            JetpackItem.Layered(
                block,
                props,
                Identifier.fromNamespaceAndPath("create", "textures/models/armor/netherite_diving_layer.png"),
            )
        }

    private fun <T : Item> register(
        name: String,
        block: Block,
        properties: Item.Properties,
        factory: (Block, Item.Properties) -> T,
    ): T {
        val key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name))
        // item.create_jetpack.* description ids, as upstream (the tooltip lang keys hang off them)
        val item = factory(block, properties.setId(key))
        Registry.register(BuiltInRegistries.ITEM, key, item)
        Item.BY_BLOCK[block] = item
        return item
    }

    internal fun register() {
        CreativeModeTabEvents.modifyOutputEvent(AllCreativeModeTabs.BASE_GROUP).register { output ->
            output.insertAfter(AllItems.NETHERITE_BACKTANK, JETPACK.maxAirStack, NETHERITE_JETPACK.maxAirStack)
        }
    }
}
