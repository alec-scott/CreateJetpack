package com.possible_triangle.create_jetpack.mixins;

import com.possible_triangle.create_jetpack.world.recipe.CopyComponentsMechanicalCraftingRecipe;
import com.zurrtum.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.1 port: stands in for upstream's CopyComponentsMechanicalCraftingRecipe.assemble override, since Create Fly's
 * MechanicalCraftingRecipe is a final record. See CopyComponentsMechanicalCraftingRecipe.
 */
@Mixin(MechanicalCraftingRecipe.class)
public class MechanicalCraftingRecipeMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void create_jetpack$copyComponents(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        var self = (MechanicalCraftingRecipe) (Object) this;
        if (CopyComponentsMechanicalCraftingRecipe.copiesComponents(self)) {
            CopyComponentsMechanicalCraftingRecipe.copyComponents(input, cir.getReturnValue());
        }
    }

}
