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
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.common.Util;

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
		final TIntList recipeKeys = Util.getList(recipe.recipeItems.toArray(), resourceHandler);
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

	@Override
	@Nonnull
	public List<Class<? extends ShapelessRecipes>> getShapelessRecipeClasses()
	{
		return Collections.singletonList(ShapelessRecipes.class);
	}

	@Override
	public ShapedOreRecipe getNewShapedRecipe(@Nonnull final ShapedRecipes recipe)
	{
		final Object[] newRecipeInputs = new Object[9];
		final ItemStack[] recipeInputs = recipe.recipeItems;
		int i = 0;
		for (int x = 0; x < recipe.recipeWidth; x++) {
			for (int y = 0; y < recipe.recipeHeight; y++) {
				final ItemStack input = recipeInputs[i++];
				final String bufferOreName = resourceHandler.getContainerName(input);
				newRecipeInputs[y * 3 + x] = bufferOreName != null ? bufferOreName : input;
			}
		}
		return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
	}

	@Override
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final ShapelessRecipes recipe)
	{
		final Object[] newRecipeInputs = new Object[9];
		final List<ItemStack> recipeInputs = recipe.recipeItems;
		String bufferOreName;
		for (int i = 0; i < recipeInputs.size(); i++)
			newRecipeInputs[i] = (bufferOreName = resourceHandler.getContainerName(recipeInputs.get(i))) != null ? bufferOreName : recipeInputs.get(i);
		return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final ShapelessRecipes recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		recipe.recipeItems.forEach(itemStack -> {
			if (itemStack != null) {
				String bufferOreName;
				inputs.add((bufferOreName = uniOreDictionary.getName(itemStack)) != null ? bufferOreName : itemStack);
			}
		});
		return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
	}

	@Override
	public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final ShapedRecipes recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		for (final ItemStack itemStack : recipe.recipeItems) {
			if (itemStack != null) {
				String bufferOreName;
				inputs.add((bufferOreName = uniOreDictionary.getName(itemStack)) != null ? bufferOreName : itemStack);
			}
		}
		return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
	}
}