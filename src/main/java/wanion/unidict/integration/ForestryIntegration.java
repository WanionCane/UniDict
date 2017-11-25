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
import net.minecraft.util.registry.RegistryNamespaced;
import wanion.lib.common.MetaItem;
import wanion.lib.common.Util;
import wanion.unidict.UniDict;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.*;

final class ForestryIntegration extends AbstractIntegrationThread
{
	private Set<ICarpenterRecipe> carpenterRecipes = Util.getField(CarpenterRecipeManager.class, "recipes", null, Set.class);
	private static final RegistryNamespaced<ResourceLocation, Item> itemRegistry = Item.REGISTRY;

	ForestryIntegration()
	{
		super("Forestry");
	}

	@Override
	public String call()
	{
		try {
			removeBadCarpenterOutputs(carpenterRecipes);
			if (resourceHandler.containerExists("ingotBronze"))
				bronzeThings();
			fixCentrifugeRecipes();
		} catch (Exception e) { UniDict.getLogger().error(threadName + e); }
		return threadName + "All these bees... they can hurt, you know?";
	}

	private void removeBadCarpenterOutputs(@Nonnull final Set<ICarpenterRecipe> carpenterRecipes)
	{
		carpenterRecipes.removeIf(carpenterRecipe -> carpenterRecipe != null && resourceHandler.exists(MetaItem.get(carpenterRecipe.getCraftingGridRecipe().getOutput())));
	}

	private void bronzeThings()
	{
		UniResourceContainer ingotBronze = resourceHandler.getContainer("ingotBronze");
		final Item brokenBronzePickaxe = itemRegistry.getObject(new ResourceLocation("forestry", "brokenBronzePickaxe"));
		final Item brokenBronzeShovel = itemRegistry.getObject(new ResourceLocation("forestry", "brokenBronzeShovel"));
		if (brokenBronzePickaxe != null)
			carpenterRecipes.add(new CarpenterRecipe(5, null, ItemStack.EMPTY, new ShapedRecipeCustom(ingotBronze.getMainEntry(2), "X  ", "   ", "   ", 'X', new ItemStack(brokenBronzePickaxe))));
		if (brokenBronzeShovel != null)
			carpenterRecipes.add(new CarpenterRecipe(5, null, ItemStack.EMPTY, new ShapedRecipeCustom(ingotBronze.getMainEntry(1), "X  ", "   ", "   ", 'X', new ItemStack(brokenBronzeShovel))));
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
		final Map<ItemStack, Float> newOutputMap = new HashMap<>();
		outputMap.forEach((output, chance) -> newOutputMap.put(resourceHandler.getMainItemStack(output), chance));
		return newOutputMap;
	}
}