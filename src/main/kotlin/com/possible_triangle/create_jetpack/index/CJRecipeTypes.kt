package com.possible_triangle.create_jetpack.index

import com.possible_triangle.create_jetpack.CJConstants.MOD_ID
import com.possible_triangle.create_jetpack.world.recipe.CopyComponentsMechanicalCraftingRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier

object CJRecipeTypes {
    @JvmField
    val COPY_NBT_MECHANICAL_CRAFTING_SERIALIZER =
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(MOD_ID, "copy_components_mechanical_crafting"),
            CopyComponentsMechanicalCraftingRecipe.SERIALIZER,
        )

    internal fun register() {
        // Load this class
    }
}
