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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ForgeRecipeResearcher extends AbstractRecipeResearcher<ShapedOreRecipe, ShapelessOreRecipe>
{
	@Override
	public int getShapedRecipeKey(@Nonnull final ShapedOreRecipe recipe)
	{
		final TIntList recipeKeys = Util.getList(recipe.getInput(), resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	public int getShapelessRecipeKey(@Nonnull final ShapelessOreRecipe recipe)
	{
		final TIntList recipeKeys = Util.getList(recipe.getInput().toArray(), resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nonnull
	public List<Class<? extends ShapedOreRecipe>> getShapedRecipeClasses()
	{
		Class<? extends ShapedOreRecipe> shapedCustomRecipe = null;
		try {
			if (Loader.isModLoaded("forestry"))
				shapedCustomRecipe = (Class<? extends ShapedOreRecipe>) Class.forName("forestry.core.recipes.ShapedRecipeCustom");
		} catch (ClassNotFoundException e) {
			UniDict.getLogger().error(e);
		}
		return shapedCustomRecipe == null ? Collections.singletonList(ShapedOreRecipe.class) : Arrays.asList(ShapedOreRecipe.class, shapedCustomRecipe);
	}

	@Override
	@Nonnull
	public List<Class<? extends ShapelessOreRecipe>> getShapelessRecipeClasses()
	{
		return Collections.singletonList(ShapelessOreRecipe.class);
	}

	@Override
	public ShapedOreRecipe getNewShapedRecipe(@Nonnull final ShapedOreRecipe recipe)
	{
		final Object[] recipeInputs = recipe.getInput();
		final Object[] newRecipeInputs = new Object[9];
		if (itemStacksOnly) {
			for (int i = 0; i < 9; i++) {
				final Object input = i < recipeInputs.length ? recipeInputs[i] : null;
				newRecipeInputs[i] = input instanceof List && !((List) input).isEmpty() ? ((List) input).get(0) : input instanceof ItemStack ? resourceHandler.getMainItemStack((ItemStack) input) : null;
			}
		} else {
			for (int i = 0; i < 9; i++) {
				final Object input = i < recipeInputs.length ? recipeInputs[i] : null;
				final String bufferOreName = input != null ? input instanceof List ? uniOreDictionary.getName(input) : input instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) input) : null : null;
				newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : input : null;
			}
		}
		return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
	}

	@Override
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final ShapelessOreRecipe recipe)
	{
		final Object[] recipeInputs = recipe.getInput().toArray();
		final Object[] newRecipeInputs = new Object[recipeInputs.length];
		if (itemStacksOnly) {
			for (int i = 0; i < recipeInputs.length; i++) {
				final Object input = i < recipeInputs.length ? recipeInputs[i] : null;
				newRecipeInputs[i] = input instanceof List && !((List) input).isEmpty() ? ((List) input).get(0) : input instanceof ItemStack ? resourceHandler.getMainItemStack((ItemStack) input) : null;
			}
		} else {
			for (int i = 0; i < recipeInputs.length; i++) {
				final Object input = i < recipeInputs.length ? recipeInputs[i] : null;
				final String bufferOreName = input != null ? input instanceof List ? uniOreDictionary.getName(input) : input instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) input) : null : null;
				newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : input : null;
			}
		}
		return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final ShapelessOreRecipe recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		if (itemStacksOnly) {
			recipe.getInput().forEach(object -> {
				if (object != null) {
					if (object instanceof List && !((List) object).isEmpty())
						inputs.add(((List) object).get(0));
					else if (object instanceof ItemStack)
						inputs.add(resourceHandler.getMainItemStack((ItemStack) object));
				}
			});
		} else {
			recipe.getInput().forEach(object -> {
				if (object != null) {
					final String bufferOreName = object instanceof List ? uniOreDictionary.getName(object) : object instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) object) : null;
					if (bufferOreName != null)
						inputs.add(bufferOreName);
					else if (object instanceof ItemStack)
						inputs.add(object);
				}
			});
		}
		return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
	}

	@Override
	public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final ShapedOreRecipe recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		if (itemStacksOnly) {
			for (Object object : recipe.getInput()) {
				if (object != null) {
					if (object instanceof List && !((List) object).isEmpty())
						inputs.add(((List) object).get(0));
					else if (object instanceof ItemStack)
						inputs.add(resourceHandler.getMainItemStack((ItemStack) object));
				}
			}
		} else {
			for (Object object : recipe.getInput()) {
				if (object != null) {
					final String bufferOreName = object instanceof List ? uniOreDictionary.getName(object) : object instanceof ItemStack ? resourceHandler.getContainerName((ItemStack) object) : null;
					if (bufferOreName != null)
						inputs.add(bufferOreName);
					else if (object instanceof ItemStack)
						inputs.add(object);
				}
			}
		}
		return new ShapelessOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), inputs.toArray());
	}
}