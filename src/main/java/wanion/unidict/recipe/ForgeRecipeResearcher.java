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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.UniDict;
import wanion.unidict.common.Reference;
import wanion.unidict.common.Util;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ForgeRecipeResearcher extends AbstractRecipeResearcher<ShapedOreRecipe, ShapelessOreRecipe>
{
	private final Field oresField;

	public ForgeRecipeResearcher()
	{
		Field dummyOresField = null;
		try {
			dummyOresField = OreIngredient.class.getDeclaredField("ores");
			dummyOresField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		oresField = dummyOresField;
	}

	@Override
	public int getShapedRecipeKey(@Nonnull final ShapedOreRecipe recipe)
	{
		final TIntList recipeKeys = Util.getList(recipe.getIngredients().toArray(), resourceHandler);
		int recipeKey = 0;
		recipeKeys.sort();
		for (final TIntIterator recipeKeysIterator = recipeKeys.iterator(); recipeKeysIterator.hasNext(); )
			recipeKey += 31 * recipeKeysIterator.next();
		return recipeKey;
	}

	@Override
	public int getShapelessRecipeKey(@Nonnull final ShapelessOreRecipe recipe)
	{
		final TIntList recipeKeys = Util.getList(recipe.getIngredients().toArray(), resourceHandler);
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
		final List<Ingredient> recipeInputs = recipe.getIngredients();
		final int width = recipe.getRecipeWidth(), height = recipe.getRecipeHeight(), root = width > height ? width : height;
		final Object[] newRecipeInputs = new Object[root * root];
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++, i++) {
				final Ingredient ingredient = i < recipeInputs.size() ? recipeInputs.get(i) : null;
				if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
					final ItemStack itemStack = ingredient.getMatchingStacks()[0];
					final UniResourceContainer container = resourceHandler.getContainer(itemStack);
					newRecipeInputs[y * root + x] = container != null ? (itemStacksOnly ? container.getMainEntry(itemStack) : container.name) : itemStack;
				} else if (ingredient instanceof OreIngredient) {
					try {
						newRecipeInputs[y * root + x] = uniOreDictionary.getName(oresField.get(ingredient));
					} catch (IllegalAccessException e) { e.printStackTrace(); }
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
	public ShapedOreRecipe getNewShapedFromShapelessRecipe(@Nonnull final ShapelessOreRecipe recipe)
	{
		final List<Ingredient> recipeInputs = recipe.getIngredients();
		final Object[] newRecipeInputs = new Object[9];
		for (int i = 0; i < 9; i++) {
			final Ingredient ingredient = i < recipeInputs.size() ? recipeInputs.get(i) : null;
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
		final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
		return new ShapedOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_shape." + newRecipeAttributes.shape), outputStack, newRecipeAttributes.actualShape);
	}

	@Override
	public ShapelessOreRecipe getNewShapelessRecipe(@Nonnull final ShapelessOreRecipe recipe)
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
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + inputs.size()), outputStack, inputs.toArray());
	}

	@Override
	public ShapelessOreRecipe getNewShapelessFromShapedRecipe(@Nonnull final ShapedOreRecipe recipe)
	{
		final List<Object> inputs = new ArrayList<>();
		if (itemStacksOnly) {
			for (final Ingredient ingredient : recipe.getIngredients())
				if (ingredient != null && ingredient.getMatchingStacks().length > 0)
					inputs.add(resourceHandler.getMainItemStack(ingredient.getMatchingStacks()[0]));
		} else {
			for (final Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
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
		return new ShapelessOreRecipe(new ResourceLocation(Reference.MOD_ID, outputContainer.name + "_x" + outputSize + "_size." + inputs.size()), outputStack, inputs.toArray());
	}
}