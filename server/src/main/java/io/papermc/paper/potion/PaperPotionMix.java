package io.papermc.paper.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;

public record PaperPotionMix(ItemStack result, Ingredient input, Ingredient ingredient) {

    public PaperPotionMix(PotionMix potionMix) {
        this(CraftItemStack.asNMSCopy(potionMix.getResult()), CraftRecipe.toIngredient(potionMix.getInput(), true), CraftRecipe.toIngredient(potionMix.getIngredient(), true));
    }
}
