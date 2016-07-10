package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;

public class VanillaResearcher implements IRecipeResearcher<ShapedRecipes, ShapelessRecipes>
{
    @Override
    public int getShapedRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapedRecipes)recipe).recipeItems, resourceHandler);
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return MetaItem.getCumulative(((ShapelessRecipes)recipe).recipeItems.toArray(), resourceHandler);
    }

    @Nonnull
    @Override
    public Class<ShapedRecipes> getShapedRecipeClass()
    {
        return ShapedRecipes.class;
    }

    @Nonnull
    @Override
    public Class<ShapelessRecipes> getShapelessRecipeClass()
    {
        return ShapelessRecipes.class;
    }

    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }

    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }

    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }

    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }
}