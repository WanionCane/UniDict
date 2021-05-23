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
import ic2.core.recipe.RecipeInputOreDict;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.common.MetaItem;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.common.Reference;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IC2RecipeResearcher extends AbstractRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
{
	private final Item ic2ForgeHammer = Item.REGISTRY.getObject(new ResourceLocation("ic2", "forge_hammer"));

	@Override
	public int getShapedRecipeKey(@Nonnull AdvRecipe recipe)
	{
		final TIntList recipeKeys = new TIntArrayList();
		int recipeKey = 0;
		List<ItemStack> bufferInput;
		for (final IRecipeInput input : recipe.input)
			if (!(bufferInput = input.getInputs()).isEmpty())
				recipeKeys.add(MetaItem.get(resourceHandler.getMainItemStack(bufferInput.get(0))));
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	public int getShapelessRecipeKey(@Nonnull AdvShapelessRecipe recipe)
	{
		final TIntList recipeKeys = new TIntArrayList();
		int recipeKey = 0;
		List<ItemStack> bufferInput;
		for (final IRecipeInput input : recipe.input)
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
	public ShapedOreRecipe getNewShapedRecipe(@Nonnull final AdvRecipe recipe)
	{
		final int width = recipe.getRecipeWidth(), height = recipe.getRecipeHeight(), root = Math.max(width, height);
		final Object[] newRecipeInputs = new Object[root * root];
		final IRecipeInput[] recipeInputs = recipe.input;
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++, i++) {
				if (i >= recipeInputs.length)
					continue;
				if (itemStacksOnly) {
					final List<ItemStack> inputs = recipeInputs[i].getInputs();
					if (!inputs.isEmpty())
						newRecipeInputs[y * root + x] = resourceHandler.getMainItemStack(inputs.get(0));
				}
				else {
					final IRecipeInput input = recipeInputs[i];
					String oreName = input instanceof RecipeInputOreDict ? ((RecipeInputOreDict) input).input : null;
					if (oreName == null) {
						final boolean notEmpty = !input.getInputs().isEmpty();
						oreName = notEmpty ? uniOreDictionary.getName(input.getInputs().get(0)) : null;
						if (oreName != null)
							newRecipeInputs[y * root + x] = oreName;
						else if (notEmpty)
							newRecipeInputs[y * root + x] = input.getInputs().get(0);
					} else
						newRecipeInputs[y * root + x] = oreName;
				}
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
	}

	@Override
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final AdvShapelessRecipe recipe)
	{
		final Object[] newRecipeInputs = new Object[9];
		final IRecipeInput[] recipeInputs = recipe.input;
		if (itemStacksOnly) {
			for (int i = 0; i < recipeInputs.length; i++) {
				final List<ItemStack> inputs = recipeInputs[i].getInputs();
				if (!inputs.isEmpty())
					newRecipeInputs[i] = resourceHandler.getMainItemStack(inputs.get(0));
			}
		} else {
			for (int i = 0; i < recipeInputs.length; i++) {
				final IRecipeInput input = recipeInputs[i];
				String oreName = input instanceof RecipeInputOreDict ? ((RecipeInputOreDict) input).input : null;
				if (oreName == null) {
					final boolean notEmpty = !input.getInputs().isEmpty();
					oreName = notEmpty ? uniOreDictionary.getName(input.getInputs().get(0)) : null;
					if (oreName != null)
						newRecipeInputs[i] = oreName;
					else if (notEmpty)
						newRecipeInputs[i] = input.getInputs().get(0);
				} else
					newRecipeInputs[i] = oreName;
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final AdvShapelessRecipe recipe)
	{
		final List<Object> newInputs = new ArrayList<>();
		if (itemStacksOnly) {
			for (final IRecipeInput recipeInput : recipe.input) {
				final List<ItemStack> inputs = recipeInput.getInputs();
				if (!inputs.isEmpty())
					newInputs.add(resourceHandler.getMainItemStack(inputs.get(0)));
			}
		} else {
			for (final IRecipeInput recipeInput : recipe.input) {
				String oreName = recipeInput instanceof RecipeInputOreDict ? ((RecipeInputOreDict) recipeInput).input : null;
				if (oreName == null) {
					final boolean notEmpty = !recipeInput.getInputs().isEmpty();
					oreName = notEmpty ? uniOreDictionary.getName(recipeInput.getInputs().get(0)) : null;
					if (oreName != null)
						newInputs.add(oreName);
					else if (notEmpty)
						newInputs.add(recipeInput.getInputs().get(0));
				} else {
					if (oreName.equals("craftingToolForgeHammer") && ic2ForgeHammer != null)
						newInputs.add(new ItemStack(ic2ForgeHammer, 1, OreDictionary.WILDCARD_VALUE));
					else
						newInputs.add(oreName);
				}
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + newInputs.size()), outputStack, newInputs.toArray());
	}

	@Override
	public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final AdvRecipe recipe)
	{
		final List<Object> newInputs = new ArrayList<>();
		if (itemStacksOnly) {
			for (final IRecipeInput recipeInput : recipe.input) {
				final List<ItemStack> inputs = recipeInput.getInputs();
				if (!inputs.isEmpty())
					newInputs.add(resourceHandler.getMainItemStack(inputs.get(0)));
			}
		} else {
			for (final IRecipeInput recipeInput : recipe.input) {
				String oreName = recipeInput instanceof RecipeInputOreDict ? ((RecipeInputOreDict) recipeInput).input : null;
				if (oreName == null) {
					final boolean notEmpty = !recipeInput.getInputs().isEmpty();
					oreName = notEmpty ? uniOreDictionary.getName(recipeInput.getInputs().get(0)) : null;
					if (oreName != null)
						newInputs.add(oreName);
					else if (notEmpty)
						newInputs.add(recipeInput.getInputs().get(0));
				} else
					newInputs.add(oreName);
			}
		}
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + newInputs.size()), outputStack, newInputs.toArray());
	}

	@Override
	public void postProcess() { }
}
