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
import wanion.unidict.UniOreDictionary;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final IRecipeInput[] recipeInputs = ((AdvRecipe)recipe).input;
        String bufferOreName;
        for (int i = 0; i < 9; i++) {
            List<ItemStack> input = i < recipeInputs.length ? recipeInputs[i].getInputs() : null;
            newRecipeInputs[i] = input != null ? ((bufferOreName = uniOreDictionary.getName(input)) != null) ? bufferOreName : input.get(0) : null;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @Nonnull
    @Override
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final IRecipeInput[] recipeInputs = ((AdvShapelessRecipe)recipe).input;
        String bufferOreName;
        for (int i = 0; i < recipeInputs.length; i++) {
            List<ItemStack> input = recipeInputs[i].getInputs();
            newRecipeInputs[i] = ((bufferOreName = uniOreDictionary.getName(input)) != null) ? bufferOreName : (input != null) ? input.get(0) : null;
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (final IRecipeInput recipeInput : ((AdvShapelessRecipe) recipe).input) {
            String bufferOreName = uniOreDictionary.getName(recipeInput.getInputs());
            if (bufferOreName != null)
                inputs.add(bufferOreName);
            else if (!recipeInput.getInputs().isEmpty())
                if ((bufferOreName = uniOreDictionary.getName(recipeInput.getInputs().get(0))) != null)
                    inputs.add(bufferOreName);
                else inputs.add(recipeInput.getInputs().get(0));
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }

    @Nonnull
    @Override
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (final IRecipeInput recipeInput : ((AdvRecipe) recipe).input) {
            String bufferOreName = uniOreDictionary.getName(recipeInput.getInputs());
            if (bufferOreName != null)
                inputs.add(bufferOreName);
            else if (!recipeInput.getInputs().isEmpty())
                if ((bufferOreName = uniOreDictionary.getName(recipeInput.getInputs().get(0))) != null)
                    inputs.add(bufferOreName);
                else inputs.add(recipeInput.getInputs().get(0));
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }
}