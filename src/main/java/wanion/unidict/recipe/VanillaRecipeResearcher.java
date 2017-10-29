package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.common.Reference;
import wanion.unidict.common.Util;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VanillaRecipeResearcher extends AbstractRecipeResearcher<ShapedRecipes, ShapelessRecipes>
{
	@Override
	public int getShapedRecipeKey(@Nonnull final ShapedRecipes recipe)
	{
		final TIntList recipeKeys = Util.getList(recipe.recipeItems, resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	public int getShapelessRecipeKey(@Nonnull final ShapelessRecipes recipe)
	{
		final TIntList recipeKeys = Util.getList(recipe.recipeItems, resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	@Nonnull
	public List<Class<? extends ShapedRecipes>> getShapedRecipeClasses()
	{
		return Collections.singletonList(ShapedRecipes.class);
	}

	@Nonnull
	@Override
	public List<Class<? extends ShapelessRecipes>> getShapelessRecipeClasses()
	{
		return Collections.singletonList(ShapelessRecipes.class);
	}

	@Override
	public ShapedOreRecipe getNewShapedRecipe(@Nonnull final ShapedRecipes recipe)
	{
		final Ingredient[] recipeInputs = (Ingredient[]) recipe.getIngredients().toArray();
		final Object[] newRecipeInputs = new Object[9];
		for (int i = 0; i < 9; i++) {
			final Ingredient ingredient = i < recipeInputs.length ? recipeInputs[i] : null;
			if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
				final ItemStack itemStack = ingredient.getMatchingStacks()[0];
				final UniResourceContainer container = resourceHandler.getContainer(itemStack);
				newRecipeInputs[i] = container != null ? (itemStacksOnly ? container.getMainEntry(itemStack) : container.name) : itemStack;
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		final Object[] actualNewInputs = RecipeHelper.rawShapeToShape(newRecipeInputs);
		final StringBuilder actualName = new StringBuilder(outputSize + "x_" + outputContainer.name + "_shape:");
		for (int i = 0; i < 3; i++)
			actualName.append(actualNewInputs[i]);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, actualName.toString()), outputStack, actualNewInputs);
	}

	@Override
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final ShapelessRecipes recipe)
	{
		final Ingredient[] recipeInputs = (Ingredient[]) recipe.getIngredients().toArray();
		final Object[] newRecipeInputs = new Object[recipeInputs.length];
		for (int i = 0; i < 9; i++) {
			final Ingredient ingredient = i < recipeInputs.length ? recipeInputs[i] : null;
			if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
				final ItemStack itemStack = ingredient.getMatchingStacks()[0];
				final UniResourceContainer container = resourceHandler.getContainer(itemStack);
				newRecipeInputs[i] = container != null ? (itemStacksOnly ? container.getMainEntry(itemStack) : container.name) : itemStack;
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		final Object[] actualNewInputs = RecipeHelper.rawShapeToShape(newRecipeInputs);
		final StringBuilder actualName = new StringBuilder(outputSize + "x_" + outputContainer.name + "_");
		for (int i = 0; i < 3; i++)
			actualName.append(actualNewInputs[i]);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, actualName.toString()), outputStack, actualNewInputs);
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final ShapelessRecipes recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		if (itemStacksOnly) {
			recipe.getIngredients().forEach(ingredient -> {
				if (ingredient != null && ingredient.getMatchingStacks().length > 0)
					inputs.add(resourceHandler.getMainItemStack(ingredient.getMatchingStacks()[0]));
			});
		} else {
			recipe.getIngredients().forEach(ingredient -> {
				if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
					final ItemStack input = ingredient.getMatchingStacks()[0];
					final UniResourceContainer container = resourceHandler.getContainer(input);
					inputs.add(container != null ? container.name : input);
				}
			});
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputSize + "x_" + outputContainer.name + "_size:" + inputs.size()), outputStack, inputs.toArray());
	}

	@Override
	public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final ShapedRecipes recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		if (itemStacksOnly) {
			for (final Ingredient ingredient : recipe.getIngredients())
				if (ingredient != null && ingredient.getMatchingStacks().length > 1)
					inputs.add(resourceHandler.getMainItemStack(ingredient.getMatchingStacks()[0]));
		} else {
			for (final Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient != null && ingredient.getMatchingStacks().length > 1) {
					final ItemStack input = ingredient.getMatchingStacks()[0];
					final UniResourceContainer container = resourceHandler.getContainer(input);
					inputs.add(container != null ? container.name : input);
				}
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputSize + "x_" + outputContainer.name + "_size:" + inputs.size()), outputStack, inputs.toArray());
	}
}