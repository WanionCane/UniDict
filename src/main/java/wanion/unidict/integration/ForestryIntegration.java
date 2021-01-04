package wanion.unidict.integration;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.CarpenterRecipeManager;
import forestry.factory.recipes.CentrifugeRecipe;
import forestry.factory.recipes.CentrifugeRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import wanion.lib.common.MetaItem;
import wanion.lib.common.Util;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

final class ForestryIntegration extends AbstractIntegrationThread
{
	private final Set<ICarpenterRecipe> carpenterRecipes = Util.getField(CarpenterRecipeManager.class, "recipes", null, Set.class);

	ForestryIntegration()
	{
		super("Forestry");
	}

	@Override
	public String call()
	{
		try {
			removeBadCarpenterOutputs(carpenterRecipes);

			final UniResourceContainer ingotBronze = resourceHandler.getContainer("Bronze", "ingot");
			if (ingotBronze != null)
				bronzeThings(ingotBronze);

			fixCentrifugeRecipes();
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "All these bees... they can hurt, you know?";
	}

	private void removeBadCarpenterOutputs(@Nonnull final Set<ICarpenterRecipe> carpenterRecipes)
	{
		carpenterRecipes.removeIf(carpenterRecipe -> carpenterRecipe != null && resourceHandler.exists(MetaItem.get(carpenterRecipe.getCraftingGridRecipe().getOutput())));
	}

	private void bronzeThings(@Nonnull final UniResourceContainer ingotBronze)
	{
		Item brokenBronzePickaxe = Item.REGISTRY.getObject(new ResourceLocation("forestry", "brokenBronzePickaxe"));
		if (brokenBronzePickaxe == null)
			brokenBronzePickaxe = Item.REGISTRY.getObject(new ResourceLocation("forestry", "broken_bronze_pickaxe"));

		Item brokenBronzeShovel = Item.REGISTRY.getObject(new ResourceLocation("forestry", "brokenBronzeShovel"));
		if (brokenBronzeShovel == null)
			brokenBronzeShovel = Item.REGISTRY.getObject(new ResourceLocation("forestry", "broken_bronze_shovel"));

		if (brokenBronzePickaxe != null)
			carpenterRecipes.add(new CarpenterRecipe(5, null, ItemStack.EMPTY, new ShapedRecipeCustom(ingotBronze.getMainEntry(2), "X  ", "   ", "   ", 'X', new ItemStack(brokenBronzePickaxe))));
		if (brokenBronzeShovel != null)
			carpenterRecipes.add(new CarpenterRecipe(5, null, ItemStack.EMPTY, new ShapedRecipeCustom(ingotBronze.getMainEntry(), "X  ", "   ", "   ", 'X', new ItemStack(brokenBronzeShovel))));
	}

	private void fixCentrifugeRecipes()
	{
		final Set<ICentrifugeRecipe> centrifugeRecipes = Util.getField(CentrifugeRecipeManager.class, "recipes", null, Set.class);
		if (centrifugeRecipes == null)
			return;
		final List<ICentrifugeRecipe> newRecipes = new ArrayList<>();
		for (final Iterator<ICentrifugeRecipe> centrifugeRecipeIterator = centrifugeRecipes.iterator(); centrifugeRecipeIterator.hasNext(); centrifugeRecipeIterator.remove())
		{
			final ICentrifugeRecipe centrifugeRecipe = centrifugeRecipeIterator.next();
			newRecipes.add(new CentrifugeRecipe(centrifugeRecipe.getProcessingTime(), centrifugeRecipe.getInput(), correctCentrifugeOutput(centrifugeRecipe.getAllProducts())));

		}
		centrifugeRecipes.addAll(newRecipes);
	}

	@Nonnull
	private Map<ItemStack, Float> correctCentrifugeOutput(@Nonnull final Map<ItemStack, Float> outputMap)
	{
		return outputMap.entrySet().stream().collect(Collectors.toMap(entry -> resourceHandler.getMainItemStack(entry.getKey()), Map.Entry::getValue));
	}
}