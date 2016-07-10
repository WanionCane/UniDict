package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import ic2.api.recipe.IRecipeInput;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.MetaItem;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class IC2Researcher implements IRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
{
    @Override
    public int getShapedRecipeKey(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        int recipeId = 0;
        List<ItemStack> bufferInput;
        for (final IRecipeInput input : ((AdvRecipe)recipe).input)
            if (!(bufferInput = input.getInputs()).isEmpty())
                recipeId += MetaItem.get(resourceHandler.getMainItemStack(bufferInput.get(0)));
        return recipeId;
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        int recipeId = 0;
        List<ItemStack> bufferInput;
        for (final IRecipeInput input : ((AdvShapelessRecipe)recipe).input)
            if (!(bufferInput = input.getInputs()).isEmpty())
                recipeId += MetaItem.get(resourceHandler.getMainItemStack(bufferInput.get(0)));
        return recipeId;
    }

    @Nonnull
    @Override
    public Class<AdvRecipe> getShapedRecipeClass()
    {
        return AdvRecipe.class;
    }

    @Nonnull
    @Override
    public Class<AdvShapelessRecipe> getShapelessRecipeClass()
    {
        return AdvShapelessRecipe.class;
    }

    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }

    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }

    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }

    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        return null;
    }
}