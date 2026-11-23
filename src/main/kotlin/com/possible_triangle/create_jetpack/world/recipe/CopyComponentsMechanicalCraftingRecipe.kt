package com.possible_triangle.create_jetpack.world.recipe

import com.google.common.collect.MapMaker
import com.zurrtum.create.content.equipment.armor.BacktankItem
import com.zurrtum.create.content.kinetics.crafter.MechanicalCraftingRecipe
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeSerializer
import java.util.Collections

/**
 * 26.1 port: Create Fly's MechanicalCraftingRecipe is a final record, so it cannot be subclassed as upstream did.
 * Instead, this serializer reads a normal mechanical crafting recipe and remembers the instance; the
 * MechanicalCraftingRecipeMixin then copies the backtank's components onto the result for remembered recipes.
 * The recipe keeps Create's own type and serializer once loaded, so the mechanical crafter and recipe viewers
 * treat it as an ordinary mechanical crafting recipe.
 */
object CopyComponentsMechanicalCraftingRecipe {
    // Identity-based and weak: recipes from a previous datapack reload are dropped automatically
    private val COPYING: MutableSet<MechanicalCraftingRecipe> =
        Collections.newSetFromMap(MapMaker().weakKeys().makeMap())

    @JvmField
    val SERIALIZER: RecipeSerializer<MechanicalCraftingRecipe> =
        RecipeSerializer(
            MechanicalCraftingRecipe.MAP_CODEC.xmap(::mark) { it },
            MechanicalCraftingRecipe.STREAM_CODEC.map(::mark) { it },
        )

    private fun mark(recipe: MechanicalCraftingRecipe): MechanicalCraftingRecipe {
        COPYING.add(recipe)
        return recipe
    }

    @JvmStatic
    fun copiesComponents(recipe: MechanicalCraftingRecipe) = recipe in COPYING

    private fun findUpgradable(input: CraftingInput): ItemStack? {
        for (i in 0 until input.size()) {
            val stack = input.getItem(i)
            if (stack.item is BacktankItem) return stack
        }
        return null
    }

    @JvmStatic
    fun copyComponents(
        input: CraftingInput,
        result: ItemStack,
    ) {
        // Upstream copied the whole component map. On 26.1 that map also carries item-defining prototype components
        // (item model, equippable, attributes...), so only the stack's own changes (air, enchantments, name, ...) are
        // copied, which is what upstream's copy amounted to on 1.21.1.
        findUpgradable(input)?.let { result.applyComponents(it.componentsPatch) }
    }
}
