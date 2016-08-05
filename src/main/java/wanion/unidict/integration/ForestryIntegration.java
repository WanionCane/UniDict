package wanion.unidict.integration;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.CarpenterRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;

final class ForestryIntegration extends AbstractIntegrationThread
{
    private Set<ICarpenterRecipe> carpenterRecipes = Util.getField(CarpenterRecipeManager.class, "recipes", null, Set.class);
    private FMLControlledNamespacedRegistry<Item> itemRegistry = MetaItem.itemRegistry;

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
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "All these bees... they can hurt, you know?";
    }

    private void removeBadCarpenterOutputs(@Nonnull final Set<ICarpenterRecipe> carpenterRecipes)
    {
        IDescriptiveRecipe carpenterRecipe;
        for (final Iterator<ICarpenterRecipe> carpenterRecipeIterator = carpenterRecipes.iterator(); carpenterRecipeIterator.hasNext(); )
            if ((carpenterRecipe = carpenterRecipeIterator.next().getCraftingGridRecipe()) != null && resourceHandler.exists(MetaItem.get(carpenterRecipe.getRecipeOutput())))
                carpenterRecipeIterator.remove();
    }

    private void bronzeThings()
    {
        UniResourceContainer ingotBronze = resourceHandler.getContainer("ingotBronze");
        carpenterRecipes.add(new CarpenterRecipe(5, null, null, new ShapedRecipeCustom(ingotBronze.getMainEntry(2), "X  ", "   ", "   ", 'X', new ItemStack(itemRegistry.getObject(new ResourceLocation("forestry:brokenBronzePickaxe"))))));
        carpenterRecipes.add(new CarpenterRecipe(5, null, null, new ShapedRecipeCustom(ingotBronze.getMainEntry(1), "X  ", "   ", "   ", 'X', new ItemStack(itemRegistry.getObject(new ResourceLocation("forestry:brokenBronzeShovel"))))));
    }
}