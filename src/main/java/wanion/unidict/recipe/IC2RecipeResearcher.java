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
import gnu.trove.list.array.TIntArrayList;
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
import java.util.Collections;
import java.util.List;

public class IC2RecipeResearcher implements IRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
{
	@Override
	public int getShapedRecipeKey(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
	{
		final TIntList recipeKeys = new TIntArrayList();
		int recipeKey = 0;
		List<ItemStack> bufferInput;
		for (final IRecipeInput input : ((AdvRecipe) recipe).input)
			if (!(bufferInput = input.getInputs()).isEmpty())
				recipeKeys.add(MetaItem.get(resourceHandler.getMainItemStack(bufferInput.get(0))));
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	public int getShapelessRecipeKey(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler)
	{
		final TIntList recipeKeys = new TIntArrayList();
		int recipeKey = 0;
		List<ItemStack> bufferInput;
		for (final IRecipeInput input : ((AdvShapelessRecipe) recipe).input)
			if (!(bufferInput = input.getInputs()).isEmpty())
				recipeKeys.add(MetaItem.get(resourceHandler.getMainItemStack(bufferInput.get(0))));
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
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
		final IRecipeInput[] recipeInputs = ((AdvRecipe) recipe).input;
		for (int i = 0; i < 9; i++) {
			final List<ItemStack> input = i < recipeInputs.length && !recipeInputs[i].getInputs().isEmpty() ? recipeInputs[i].getInputs() : null;
			final String bufferOreName = input != null ? uniOreDictionary.getName(input) : null;
			String secondaryBufferOreName;
			newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : (secondaryBufferOreName = uniOreDictionary.getName(input.get(0))) != null ? secondaryBufferOreName : null : null;
		}
		return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
	}

	@Override
	@Nonnull
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull IRecipe recipe, @Nonnull final ResourceHandler resourceHandler, @Nonnull final UniOreDictionary uniOreDictionary)
	{
		final Object[] newRecipeInputs = new Object[9];
		final IRecipeInput[] recipeInputs = ((AdvShapelessRecipe) recipe).input;
		for (int i = 0; i < recipeInputs.length; i++) {
			final List<ItemStack> input = i < recipeInputs.length && !recipeInputs[i].getInputs().isEmpty() ? recipeInputs[i].getInputs() : null;
			final String bufferOreName = input != null ? uniOreDictionary.getName(input) : null;
			String secondaryBufferOreName;
			newRecipeInputs[i] = input != null ? bufferOreName != null ? bufferOreName : (secondaryBufferOreName = uniOreDictionary.getName(input.get(0))) != null ? secondaryBufferOreName : null : null;
		}
		return new ShapedOreRecipe(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), RecipeHelper.rawShapeToShape(newRecipeInputs));
	}

	@Override
	@Nonnull
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

	@Override
	@Nonnull
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