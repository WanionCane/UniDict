package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import ic2.api.recipe.IRecipeInput;
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
import java.util.Collections;
import java.util.List;

public final class IC2RecipeResearcher implements IRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
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
            } else if (input instanceof IRecipeInput)
                recipeId += MetaItem.get(((IRecipeInput) input).getInputs().get(0));
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
                else if (obj instanceof IRecipeInput)
                    recipeId += MetaItem.get(((IRecipeInput) obj).getInputs().get(0));
            }
        }
        return recipeId;
    }

    @Override
    @Nonnull
    public List<Class<? extends AdvRecipe>> getShapedRecipeClasses()
    {
        return Collections.singletonList(AdvRecipe.class);
    }

    @Override
    @Nonnull
    public List<Class<? extends AdvShapelessRecipe>> getShapelessRecipeClasses()
    {
        return Collections.singletonList(AdvShapelessRecipe.class);
    }

    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final Object[] newRecipeInputs = new Object[9];
        final Object[] recipeInputs = ((AdvRecipe) recipe).input;
        for (int i = 0; i < 9 && i < recipeInputs.length; i++) {
            if (recipeInputs[i] instanceof IRecipeInput) {
                final List<ItemStack> inputs = ((IRecipeInput) recipeInputs[i]).getInputs();
                if (inputs.isEmpty())
                    continue;
                String bufferOreName = uniOreDictionary.getName(inputs);
                newRecipeInputs[i] = bufferOreName != null ? bufferOreName : (bufferOreName = uniOreDictionary.getName(inputs.get(0))) != null ? bufferOreName : inputs.get(0);
            } else {
                String bufferOreName = uniOreDictionary.getName(recipeInputs[i]);
                if (bufferOreName != null)
                    newRecipeInputs[i] = bufferOreName;
                else if (recipeInputs[i] instanceof ItemStack)
                    newRecipeInputs[i] = resourceHandler.getMainItemStack((ItemStack) recipeInputs[i]);
                else if (recipeInputs[i] instanceof List) {
                    final Object obj = ((List) recipeInputs[i]).get(0);
                    if (obj instanceof ItemStack)
                        newRecipeInputs[i] = resourceHandler.getMainItemStack((ItemStack) obj);
                }
            }
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
    }

    @Override
    @Nonnull
    public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> newInputs = new ArrayList<>();
        for (final Object recipeInput : ((AdvShapelessRecipe) recipe).input) {
            if (recipeInput instanceof List) {
                final Object input = ((List) recipeInput).get(0);
                if (input instanceof IRecipeInput) {
                    final List<ItemStack> inputs = ((IRecipeInput) input).getInputs();
                    String bufferOreName = uniOreDictionary.getName(inputs);
                    newInputs.add(bufferOreName != null ? bufferOreName : (bufferOreName = uniOreDictionary.getName(inputs.get(0))) != null ? bufferOreName : inputs.get(0));
                } else if (input instanceof ItemStack) {
                    String bufferOreName = uniOreDictionary.getName(input);
                    newInputs.add(bufferOreName != null ? bufferOreName : input);
                }
            } else {
                String bufferOreName = uniOreDictionary.getName(recipeInput);
                if (bufferOreName != null)
                    newInputs.add(bufferOreName);
                else if (recipeInput instanceof ItemStack)
                    newInputs.add(resourceHandler.getMainItemStack((ItemStack) recipeInput));
            }
        }
        return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newInputs.toArray()));
    }

    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> newInputs = new ArrayList<>();
        for (final Object recipeInput : ((AdvShapelessRecipe) recipe).input) {
            if (recipeInput instanceof List) {
                final Object input = ((List) recipeInput).get(0);
                if (input instanceof IRecipeInput) {
                    final List<ItemStack> inputs = ((IRecipeInput) input).getInputs();
                    String bufferOreName = uniOreDictionary.getName(inputs);
                    newInputs.add(bufferOreName != null ? bufferOreName : (bufferOreName = uniOreDictionary.getName(inputs.get(0))) != null ? bufferOreName : inputs.get(0));
                } else if (input instanceof ItemStack) {
                    String bufferOreName = uniOreDictionary.getName(input);
                    newInputs.add(bufferOreName != null ? bufferOreName : input);
                }
            } else {
                String bufferOreName = uniOreDictionary.getName(recipeInput);
                if (bufferOreName != null)
                    newInputs.add(bufferOreName);
                else if (recipeInput instanceof ItemStack)
                    newInputs.add(resourceHandler.getMainItemStack((ItemStack) recipeInput));
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), newInputs.toArray());
    }

    @Override
    @Nonnull
    public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
    {
        final List<Object> newInputs = new ArrayList<>();
        for (final Object recipeInput : ((AdvRecipe) recipe).input) {
            if (recipeInput instanceof IRecipeInput) {
                final List<ItemStack> inputs = ((IRecipeInput) recipeInput).getInputs();
                String bufferOreName = uniOreDictionary.getName(inputs);
                newInputs.add(bufferOreName != null ? bufferOreName : (bufferOreName = uniOreDictionary.getName(inputs.get(0))) != null ? bufferOreName : inputs.get(0));
            } else {
                String bufferOreName = uniOreDictionary.getName(recipeInput);
                if (bufferOreName != null)
                    newInputs.add(bufferOreName);
                else if (recipeInput instanceof ItemStack)
                    newInputs.add(resourceHandler.getMainItemStack((ItemStack) recipeInput));
                else if (recipeInput instanceof List) {
                    final Object obj = ((List) recipeInput).get(0);
                    if (obj instanceof ItemStack)
                        newInputs.add(resourceHandler.getMainItemStack((ItemStack) obj));
                }
            }
        }
        return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), newInputs.toArray());
    }
}