package wanion.unidict.integration;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.factory.recipes.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import wanion.lib.common.Util;
import wanion.lib.recipe.RecipeAttributes;
import wanion.lib.recipe.RecipeHelper;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

final class ForestryIntegration extends AbstractIntegrationThread
{
	ForestryIntegration() { super("Forestry"); }

	@Override
	public String call() {
		try {
			fixCarpenterRecipes();
			fixCentrifugeRecipes();
			fixSqueezerRecipes();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "All these bees... they can hurt, you know?";
	}

	private void fixCarpenterRecipes() {
		final Set<ICarpenterRecipe> carpenterRecipes = Util.getField(CarpenterRecipeManager.class, "recipes", null, Set.class);
		if (carpenterRecipes == null)
			return;
		final List<ICarpenterRecipe> newRecipes = new ArrayList<>();
		for (final Iterator<ICarpenterRecipe> carpenterRecipeIterator = carpenterRecipes.iterator(); carpenterRecipeIterator.hasNext();) {
			final ICarpenterRecipe carpenterRecipe = carpenterRecipeIterator.next();

			if (carpenterRecipe.getCraftingGridRecipe() instanceof ShapedRecipeCustom) {
				final ShapedRecipeCustom gridRecipe = (ShapedRecipeCustom)carpenterRecipe.getCraftingGridRecipe();

				newRecipes.add(new CarpenterRecipe(carpenterRecipe.getPackagingTime(),
						carpenterRecipe.getFluidResource(), carpenterRecipe.getBox(),  recreateRecipe(gridRecipe)));

				carpenterRecipeIterator.remove();
			}
		}
		carpenterRecipes.addAll(newRecipes);
	}

	private ShapedRecipeCustom recreateRecipe(final ShapedRecipeCustom recipe) {
		final List<Ingredient> recipeInputs = recipe.getIngredients();
		final int width = recipe.getRecipeWidth(), height = recipe.getRecipeHeight(), root = Math.max(width, height);
		final Object[] newRecipeInputs = new Object[root * root];
		for (int y = 0, i = 0; y < height; y++) {
			for (int x = 0; x < width; x++, i++) {
				final Ingredient ingredient = i < recipeInputs.size() ? recipeInputs.get(i) : null;
				if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
					final ItemStack itemStack = ingredient.getMatchingStacks()[0];
					final UniResourceContainer container = resourceHandler.getContainer(itemStack);
					newRecipeInputs[y * root + x] = container != null ? container.name : itemStack;
				}
			}
		}
		final RecipeAttributes newRecipeAttributes = RecipeHelper.rawShapeToShape(newRecipeInputs);
		final ShapedRecipeCustom newRecipe =
				new ShapedRecipeCustom(resourceHandler.getMainItemStack(recipe.getRecipeOutput()), newRecipeAttributes.actualShape);
		newRecipe.setRegistryName(recipe.getGroup());
		return newRecipe;
	}

	private void fixCentrifugeRecipes()
	{
		final Set<ICentrifugeRecipe> centrifugeRecipes = Util.getField(CentrifugeRecipeManager.class, "recipes", null, Set.class);
		if (centrifugeRecipes == null)
			return;
		final List<ICentrifugeRecipe> newRecipes = new ArrayList<>();
		for (final Iterator<ICentrifugeRecipe> centrifugeRecipeIterator = centrifugeRecipes.iterator(); centrifugeRecipeIterator.hasNext();)
		{
			final ICentrifugeRecipe centrifugeRecipe = centrifugeRecipeIterator.next();
			newRecipes.add(new CentrifugeRecipe(centrifugeRecipe.getProcessingTime(), centrifugeRecipe.getInput(), correctCentrifugeOutput(centrifugeRecipe.getAllProducts())));

			centrifugeRecipeIterator.remove();
		}
		centrifugeRecipes.addAll(newRecipes);
	}

	@Nonnull
	private Map<ItemStack, Float> correctCentrifugeOutput(@Nonnull final Map<ItemStack, Float> outputMap)
	{
		return outputMap.entrySet().stream().collect(Collectors.toMap(entry -> resourceHandler.getMainItemStack(entry.getKey()), Map.Entry::getValue));
	}

	private void fixSqueezerRecipes() {
		final ItemStackMap<ISqueezerContainerRecipe> containerRecipes = Util.getField(SqueezerRecipeManager.class,
				"containerRecipes", null, ItemStackMap.class);
		if (containerRecipes == null)
			return;
		final ItemStackMap<ISqueezerContainerRecipe> newContainerRecipes = new ItemStackMap<>();
		for (final Iterator<ISqueezerContainerRecipe> squeezerRecipeIterator = containerRecipes.values().iterator(); squeezerRecipeIterator.hasNext();) {
			final ISqueezerContainerRecipe squeezerRecipe = squeezerRecipeIterator.next();
			newContainerRecipes.put(squeezerRecipe.getEmptyContainer(),
					new SqueezerContainerRecipe(squeezerRecipe.getEmptyContainer(),
							squeezerRecipe.getProcessingTime(),
							resourceHandler.getMainItemStack(squeezerRecipe.getRemnants()),
							squeezerRecipe.getRemnantsChance()));
			squeezerRecipeIterator.remove();
		}

		containerRecipes.putAll(newContainerRecipes);
	}
}