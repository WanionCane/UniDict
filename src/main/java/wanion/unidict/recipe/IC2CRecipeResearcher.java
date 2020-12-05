package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Lists;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.crafting.IAdvRecipe;
import ic2.api.classic.recipe.crafting.ICraftingRecipeList;
import ic2.api.recipe.IRecipeInput;
import ic2.core.item.recipe.AdvRecipe;
import ic2.core.item.recipe.AdvShapelessRecipe;
import ic2.core.item.recipe.entry.RecipeInputOreDict;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.common.Reference;
import wanion.unidict.common.Util;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class IC2CRecipeResearcher extends AbstractRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
{
	private final Item craftingHammer = Item.REGISTRY.getObject(new ResourceLocation("ic2c_extras", "craftinghammer"));
	private final Field shapelessInput;
	private final Field shapedInput;

	public IC2CRecipeResearcher() {
		try {
			shapelessInput = AdvShapelessRecipe.class.getDeclaredField("input");
			shapelessInput.setAccessible(true);
			shapedInput = AdvRecipe.class.getDeclaredField("input");
			shapedInput.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Couldn't find IC2C fields.");
		}
	}

	@Override
	public int getShapedRecipeKey(@Nonnull final AdvRecipe advRecipe)
	{
		if (advRecipe.isInvisible())
			return 0;
		final TIntList recipeKeys = Util.getList(advRecipe.getIngredients(), resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	public int getShapelessRecipeKey(@Nonnull final AdvShapelessRecipe advShapelessRecipe)
	{
		if (advShapelessRecipe.isInvisible())
			return 0;
		final TIntList recipeKeys = Util.getList(advShapelessRecipe.getIngredients(), resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Nonnull
	@Override
	public List<Class<? extends AdvRecipe>> getShapedRecipeClasses()
	{
		return Collections.singletonList(AdvRecipe.class);
	}

	@Nonnull
	@Override
	public List<Class<? extends AdvShapelessRecipe>> getShapelessRecipeClasses()
	{
		return Collections.singletonList(AdvShapelessRecipe.class);
	}

	@Override
	public ShapedOreRecipe getNewShapedRecipe(@Nonnull final AdvRecipe recipe)
	{
		final Object[] newRecipeInputs = getNewShapedRecipeInputs(recipe);
		final UniResourceContainer outputContainer = resourceHandler.getContainer(recipe.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = recipe.getRecipeOutput().getCount());
		final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final AdvShapelessRecipe recipe)
	{
		try {
			final Object[] newRecipeInputs = new Object[9];
			final List<IRecipeInput> recipeInputs = (List<IRecipeInput>)shapelessInput.get(recipe);
			if (itemStacksOnly) {
				for (int i = 0; i < recipeInputs.size(); i++) {
					final List<ItemStack> inputs = recipeInputs.get(i).getInputs();
					if (!inputs.isEmpty())
						newRecipeInputs[i] = resourceHandler.getMainItemStack(inputs.get(0));
				}
			} else {
				for (int i = 0; i < recipeInputs.size(); i++) {
					final IRecipeInput input = recipeInputs.get(i);
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

		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final AdvShapelessRecipe recipe)
	{
		final List<Object> newInputs = getNewShapelessRecipeInputs(recipe);
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
		try {
			final List<Object> newInputs = new ArrayList<>();
			IRecipeInput[] input = (IRecipeInput[])shapedInput.get(recipe);
			if (itemStacksOnly) {
				for (final IRecipeInput recipeInput : input) {
					final List<ItemStack> inputs = recipeInput.getInputs();
					if (!inputs.isEmpty())
						newInputs.add(resourceHandler.getMainItemStack(inputs.get(0)));
				}
			} else {
				for (final IRecipeInput recipeInput : input) {
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
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object> getNewShapelessRecipeInputs(AdvShapelessRecipe advRecipe) {
		final List<Object> newInputs = new ArrayList<>();
		try {
			final List<IRecipeInput> input = (List<IRecipeInput>)shapelessInput.get(advRecipe);
			if (itemStacksOnly) {
				for (final IRecipeInput recipeInput : input) {
					final List<ItemStack> inputs = recipeInput.getInputs();
					if (!inputs.isEmpty())
						newInputs.add(resourceHandler.getMainItemStack(inputs.get(0)));
				}
			} else {
				for (final IRecipeInput recipeInput : input) {
					String oreName = recipeInput instanceof RecipeInputOreDict ? ((RecipeInputOreDict) recipeInput).input : null;
					if (oreName == null) {
						final boolean notEmpty = !recipeInput.getInputs().isEmpty();
						oreName = notEmpty ? uniOreDictionary.getName(recipeInput.getInputs().get(0)) : null;
						if (oreName != null)
							newInputs.add(oreName);
						else if (notEmpty)
							newInputs.add(recipeInput.getInputs().get(0));
					} else {
						if (oreName.equals("craftingToolForgeHammer") && craftingHammer != null)
							newInputs.add(new ItemStack(craftingHammer, 1, 32767));
						else
							newInputs.add(oreName);
					}
				}
			}
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return newInputs;
	}

	private Object[] getNewShapedRecipeInputs(AdvRecipe advRecipe) {
		final int width = advRecipe.getRecipeLength(), height = advRecipe.getRecipeHeight(), root = Math.max(width, height);
		final Object[] newRecipeInputs = new Object[root * root];
		try {
			final IRecipeInput[] recipeInputs = (IRecipeInput[])shapedInput.get(advRecipe);
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
							final boolean notEmpty = input != null && !input.getInputs().isEmpty();
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
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return newRecipeInputs;
	}

	@Override
	public void postProcess() {
		final ICraftingRecipeList advCrafting = ClassicRecipes.advCrafting;
		final List<IAdvRecipe> advRecipeList = advCrafting.getRecipes();
		final Map<AdvShapelessRecipe, ItemStack> shapelessStackMap = new HashMap<>();
		final Map<AdvRecipe, ItemStack> shapedStackMap = new HashMap<>();
		for (final IAdvRecipe advRecipe : advRecipeList) {
			if (advRecipe == null || !advRecipe.isInvisible())
				continue;

			if (advRecipe instanceof AdvShapelessRecipe) {
				AdvShapelessRecipe shapelessRecipe = (AdvShapelessRecipe) advRecipe;
				final ItemStack output = shapelessRecipe.getRecipeOutput();
				final ItemStack newOutput = resourceHandler.getMainItemStack(output);
				if (output != newOutput)
					shapelessStackMap.put(shapelessRecipe, newOutput);
			}
			else if (advRecipe instanceof AdvRecipe) {
				AdvRecipe shapedRecipe = (AdvRecipe)advRecipe;
				final ItemStack output = shapedRecipe.getRecipeOutput();
				final ItemStack newOutput = resourceHandler.getMainItemStack(output);
				if (output != newOutput)
					shapedStackMap.put(shapedRecipe, newOutput);
			}
		}

		shapelessStackMap.forEach((shapelessRecipe, newOutput) -> {
			final List<Object> newShapelessInputs = getNewShapelessRecipeInputs(shapelessRecipe);
			newShapelessInputs.add(true);
			advCrafting.overrideShapelessRecipe(shapelessRecipe.getRecipeID(), newOutput, newShapelessInputs.toArray());
		});
		shapedStackMap.forEach((shapedRecipe, newOutput) -> {
			final List<Object> newShapedInputs = Lists.newArrayList(RecipeHelper.rawShapeToShape(getNewShapedRecipeInputs(shapedRecipe)).actualShape);
			newShapedInputs.add(true);
			advCrafting.overrideRecipe(shapedRecipe.getRecipeID(), newOutput, newShapedInputs.toArray());
		});
	}
}