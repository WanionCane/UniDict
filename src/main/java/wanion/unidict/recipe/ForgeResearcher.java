package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;

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