package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ForgeResearcher implements IRecipeResearcher<ShapedOreRecipe, ShapelessOreRecipe>
{
    @Override
    public int getShapedRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapedOreRecipe)recipe).getInput(), resourceHandler);
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapelessOreRecipe)recipe).getInput().toArray(), resourceHandler);
    }

    @Nonnull
    @Override
    public Class<ShapedOreRecipe> getShapedRecipeClass()
    {
        return ShapedOreRecipe.class;
    }

    @Nonnull
    @Override
    public Class<ShapelessOreRecipe> getShapelessRecipeClass()
    {
        return ShapelessOreRecipe.class;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final Object[] recipeInputs = ((ShapedOreRecipe)recipe).getInput();
        String bufferOreName;
        for (int i = 0; i < 9; i++) {
            Object input = i < recipeInputs.length ? recipeInputs[i] : null;
            newRecipeInputs[i] = input != null ? (bufferOreName = uniOreDictionary.getName(input)) != null ? bufferOreName : input : null;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final Object[] recipeInputs = ((ShapelessOreRecipe)recipe).getInput().toArray();
        String bufferOreName;
        for (int i = 0; i < recipeInputs.length; i++) {
            Object input = recipeInputs[i];
            newRecipeInputs[i] = (bufferOreName = uniOreDictionary.getName(input)) != null ? bufferOreName : input;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        ((ShapelessOreRecipe) recipe).getInput().forEach(object -> {
            if (object != null) {
                final String bufferOreName = uniOreDictionary.getName(object);
                if (bufferOreName != null)
                    inputs.add(bufferOreName);
                else if (object instanceof ItemStack)
                    inputs.add(object);
            }
        });
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (Object object : ((ShapedOreRecipe) recipe).getInput()) {
            if (object != null) {
                final String bufferOreName = uniOreDictionary.getName(object);
                if (bufferOreName != null)
                    inputs.add(bufferOreName);
                else if (object instanceof ItemStack)
                    inputs.add(object);
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }
}