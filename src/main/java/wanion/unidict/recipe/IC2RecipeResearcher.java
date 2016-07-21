package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
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

public class IC2RecipeResearcher implements IRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
{
    @Override
    public int getShapedRecipeKey(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        int recipeId = 0;
        for (final Object input : ((AdvRecipe) recipe).input) {
            if (input instanceof ItemStack)
                recipeId += MetaItem.get(resourceHandler.getMainItemStack((ItemStack) input));
            else if (input instanceof List) {
                if (((List) input).isEmpty())
                    continue;
                final Object obj = ((List) input).get(0);
                if (obj instanceof ItemStack)
                    recipeId += MetaItem.get(resourceHandler.getMainItemStack((ItemStack) obj));
            }
        }
        return recipeId;
    }

    @Override
    public int getShapelessRecipeKey(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
    {
        int recipeId = 0;
        for (final Object input : ((AdvShapelessRecipe) recipe).input) {
            if (input instanceof ItemStack)
                recipeId += MetaItem.get(resourceHandler.getMainItemStack((ItemStack) input));
            else if (input instanceof List) {
                if (((List) input).isEmpty())
                    continue;
                final Object obj = ((List) input).get(0);
                if (obj instanceof ItemStack)
                    recipeId += MetaItem.get(resourceHandler.getMainItemStack((ItemStack) obj));
            }
        }
        return recipeId;
    }

    @Override
    @Nonnull
    public Class<AdvRecipe> getShapedRecipeClass()
    {
        return AdvRecipe.class;
    }

    @Override
    @Nonnull
    public Class<AdvShapelessRecipe> getShapelessRecipeClass()
    {
        return AdvShapelessRecipe.class;
    }

    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final Object[] recipeInputs = ((AdvRecipe)recipe).input;
        for (int i = 0; i < 9 && i < recipeInputs.length; i++) {
            String bufferOreName = uniOreDictionary.getName(recipeInputs[i]);
            if (bufferOreName != null)
                newRecipeInputs[i] = bufferOreName;
            else if (newRecipeInputs[i] instanceof ItemStack)
                newRecipeInputs[i] = resourceHandler.getMainItemStack((ItemStack) newRecipeInputs[i]);
            else if(newRecipeInputs[i] instanceof List) {
                final Object obj = ((List) newRecipeInputs[i]).get(0);
                if (obj instanceof ItemStack)
                    newRecipeInputs[i] = resourceHandler.getMainItemStack((ItemStack) obj);
            }
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (final Object recipeInput : ((AdvShapelessRecipe) recipe).input) {
            String bufferOreName = uniOreDictionary.getName(recipeInput);
            if (bufferOreName != null)
                inputs.add(bufferOreName);
            else if (recipeInput instanceof ItemStack)
                inputs.add(resourceHandler.getMainItemStack((ItemStack) recipeInput));
            else if(recipeInput instanceof List) {
                final Object obj = ((List) recipeInput).get(0);
                if (obj instanceof ItemStack)
                    inputs.add(resourceHandler.getMainItemStack((ItemStack) obj));
            }
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(inputs.toArray()));
    }

    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (final Object recipeInput : ((AdvShapelessRecipe) recipe).input) {
            String bufferOreName = uniOreDictionary.getName(recipeInput);
            if (bufferOreName != null)
                inputs.add(bufferOreName);
            else if (recipeInput instanceof ItemStack)
                inputs.add(resourceHandler.getMainItemStack((ItemStack) recipeInput));
            else if(recipeInput instanceof List) {
                final Object obj = ((List) recipeInput).get(0);
                if (obj instanceof ItemStack)
                    inputs.add(resourceHandler.getMainItemStack((ItemStack) obj));
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }

    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> inputs = new ArrayList<>();
        for (final Object recipeInput : ((AdvRecipe) recipe).input) {
            String bufferOreName = uniOreDictionary.getName(recipeInput);
            if (bufferOreName != null)
                inputs.add(bufferOreName);
            else if (recipeInput instanceof ItemStack)
                inputs.add(resourceHandler.getMainItemStack((ItemStack) recipeInput));
            else if(recipeInput instanceof List) {
                final Object obj = ((List) recipeInput).get(0);
                if (obj instanceof ItemStack)
                    inputs.add(resourceHandler.getMainItemStack((ItemStack) obj));
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
    }
}