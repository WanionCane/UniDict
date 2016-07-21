package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VanillaRecipeResearcher implements IRecipeResearcher<ShapedRecipes, ShapelessRecipes>
{
    @Override
    public int getShapedRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapedRecipes) recipe).recipeItems, resourceHandler);
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapelessRecipes) recipe).recipeItems.toArray(), resourceHandler);
    }

    @Override
    @Nonnull
    public Class<ShapedRecipes> getShapedRecipeClass()
    {
        return ShapedRecipes.class;
    }

    @Override
    @Nonnull
    public Class<ShapelessRecipes> getShapelessRecipeClass()
    {
        return ShapelessRecipes.class;
    }

    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final ItemStack[] recipeInputs = ((ShapedRecipes) recipe).recipeItems;
        for (int i = 0; i < 9; i++) {
            final ItemStack input = i < recipeInputs.length ? recipeInputs[i] : null;
            final String bufferOreName = resourceHandler.getContainerName(input);
            newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : input : null;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final List<ItemStack> recipeInputs = ((ShapelessRecipes) recipe).recipeItems;
        String bufferOreName;
        for (int i = 0; i < recipeInputs.size(); i++)
            newRecipeInputs[i] = (bufferOreName = resourceHandler.getContainerName(recipeInputs.get(i))) != null ? bufferOreName : recipeInputs.get(i);
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        ((ShapelessRecipes) recipe).recipeItems.forEach(itemStack -> {
            if (itemStack != null) {
                String bufferOreName;
                inputs.add((bufferOreName = uniOreDictionary.getName(itemStack)) != null ? bufferOreName : itemStack);
            }
        });
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }

    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (final ItemStack itemStack : ((ShapedRecipes) recipe).recipeItems) {
            if (itemStack != null) {
                String bufferOreName;
                inputs.add((bufferOreName = uniOreDictionary.getName(itemStack)) != null ? bufferOreName : itemStack);
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }
}