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
import ic2.core.item.recipe.AdvRecipe;
import ic2.core.item.recipe.AdvRecipeBase;
import ic2.core.item.recipe.AdvShapelessRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.common.Reference;
import wanion.unidict.common.Util;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IC2CRecipeResearcher extends AbstractRecipeResearcher<AdvRecipe, AdvShapelessRecipe>
{
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
	public ShapedOreRecipe getNewShapedRecipe(@Nonnull final AdvRecipe advRecipe)
	{
		return getNewShapedRecipe(advRecipe, resourceHandler);
	}

	@Override
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final AdvShapelessRecipe advShapelessRecipe)
	{
		return getNewShapedRecipe(advShapelessRecipe, resourceHandler);
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final AdvShapelessRecipe advShapelessRecipe)
	{
		return getNewShapelessRecipe(advShapelessRecipe, resourceHandler);
	}

	@Override
	public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final AdvRecipe advRecipe)
	{
		return getNewShapelessRecipe(advRecipe, resourceHandler);
	}

	public static ShapedOreRecipe getNewShapedRecipe(@Nonnull final AdvRecipeBase advRecipeBase, @Nonnull final ResourceHandler resourceHandler)
	{
		final UniResourceContainer outputContainer = resourceHandler.getContainer(advRecipeBase.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final Object[] newRecipeInputs = getNewShapedRecipeInputs(advRecipeBase, resourceHandler);
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = advRecipeBase.getRecipeOutput().getCount());
		final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
	}

	public static Object[] getNewShapedRecipeInputs(@Nonnull final AdvRecipeBase advRecipeBase, @Nonnull final ResourceHandler resourceHandler)
	{
		final List<Ingredient> recipeInputs = advRecipeBase.getIngredients();
		final int width = advRecipeBase.getRecipeLength(), height = advRecipeBase.getRecipeHeight(), root = width > height ? width : height;
		final Object[] newRecipeInputs = new Object[root * root];
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++, i++) {
				final Ingredient ingredient = i < recipeInputs.size() ? recipeInputs.get(i) : null;
				if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
					final ItemStack itemStack = ingredient.getMatchingStacks()[0];
					final UniResourceContainer container = resourceHandler.getContainer(itemStack);
					newRecipeInputs[y * root + x] = container != null ? (itemStacksOnly ? container.getMainEntry(itemStack) : container.name) : itemStack;
				}
			}
		}
		return newRecipeInputs;
	}

	public static ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final AdvRecipeBase advRecipeBase, @Nonnull final ResourceHandler resourceHandler)
	{
		final UniResourceContainer outputContainer = resourceHandler.getContainer(advRecipeBase.getRecipeOutput());
		if (outputContainer == null)
			return null;
		final List<Object> inputs = getNewShapelessRecipeInputs(advRecipeBase, resourceHandler);
		final int outputSize;
		final ItemStack outputStack = outputContainer.getMainEntry(outputSize = advRecipeBase.getRecipeOutput().getCount());
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + inputs.size()), outputStack, inputs.toArray());

	}

	public static List<Object> getNewShapelessRecipeInputs(@Nonnull final AdvRecipeBase advRecipeBase, @Nonnull final ResourceHandler resourceHandler)
	{
		final List<Object> inputs = new ArrayList<>();
		if (itemStacksOnly) {
			for (final Ingredient ingredient : advRecipeBase.getIngredients())
				if (ingredient != null && ingredient.getMatchingStacks().length > 0)
					inputs.add(resourceHandler.getMainItemStack(ingredient.getMatchingStacks()[0]));
		} else {
			for (final Ingredient ingredient : advRecipeBase.getIngredients()) {
				if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
					final ItemStack input = ingredient.getMatchingStacks()[0];
					final UniResourceContainer container = resourceHandler.getContainer(input);
					inputs.add(container != null ? container.name : input);
				}
			}
		}
		return inputs;
	}
}