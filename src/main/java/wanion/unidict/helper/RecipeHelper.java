package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.UniJEIPlugin;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.api.helper.FoundryUniHelper;
import wanion.unidict.api.helper.TConUniHelper;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniResourceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static wanion.unidict.Config.gearRecipesUsesIngotsInsteadOfPlates;
import static wanion.unidict.Config.useBaseMetalsShapeForGears;

public class RecipeHelper
{
    public static final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

    public static void init()
    {
        final ResourceHandler resourceHandler = UniDict.getDependencies().get(ResourceHandler.class);
        final TIntSet gearHashes = Resource.kindExists("gear") ? createGearHashSet() : null;
        if (gearHashes == null)
            inspectionOfRecipes(resourceHandler);
        else {
            final FMLControlledNamespacedRegistry<Item> itemRegistry = MetaItem.itemRegistry;
            if (Config.gearRecipesRequiresSomeGear) {
                if (Config.foundry) {
                    final ItemStack foundryMoldStack = new ItemStack(itemRegistry.getObject(new ResourceLocation("foundry:mold")), 1, 2);
                    FoundryUniHelper.removeMold(foundryMoldStack);
                    FoundryUniHelper.removeMoldRecipe(foundryMoldStack);
                    final int hash = MetaItem.get(foundryMoldStack);
                    gearHashes.add(hash);
                    if (Config.autoHideInJEI)
                        UniJEIPlugin.hide(foundryMoldStack);
                }
                if (Config.tinkersConstruct) {
                    final ItemStack gearCast = new ItemStack(itemRegistry.getObject(new ResourceLocation("tconstruct:cast_custom")), 1, 4);
                    TConUniHelper.removeCast(gearCast);
                    if (Config.foundry)
                        FoundryUniHelper.removeCast(gearCast);
                }
            }
            specialInspectionOfRecipes(resourceHandler, gearHashes);
        }
    }

    private static TIntSet createGearHashSet()
    {
        final long gear = Resource.getKindOfName("gear");
        final long ingot = Resource.getKindOfName("ingot");
        final long plate = Resource.getKindOfName("plate");
        final long rod = Resource.getKindOfName("rod");
        final UniDictAPI uniDictAPI = UniDict.getAPI();
        if (useBaseMetalsShapeForGears)
            return new TIntHashSet(MetaItem.getSet((gearRecipesUsesIngotsInsteadOfPlates) ? uniDictAPI.getResources(gear, ingot, rod) : uniDictAPI.getResources(gear, plate, rod), gear));
        else
            return new TIntHashSet(MetaItem.getSet((gearRecipesUsesIngotsInsteadOfPlates) ? uniDictAPI.getResources(gear, ingot) : uniDictAPI.getResources(gear, ingot, plate), gear));
    }

    public static void singleWayCompressionRecipe(List<Resource> smallerAndBiggerResources, long smaller, long bigger)
    {
        smallerAndBiggerResources.forEach(r -> recipes.add(new ShapedOreRecipe(r.getChild(bigger).getMainEntry(), "SSS", "SSS", "SSS", 'S', r.getChild(smaller).name)));
    }

    public static void resourcesToCompressionRecipes(Collection<Resource> resources, long... smallerToBigger)
    {
        UniResourceContainer smaller, bigger;
        for (Resource resource : resources)
            for (int i = 0; i < smallerToBigger.length - 1; i++)
                if ((smaller = resource.getChild(smallerToBigger[i])) != null && (bigger = resource.getChild(smallerToBigger[i + 1])) != null)
                    createCompressionRecipe(smaller, bigger);
    }

    private static void createCompressionRecipe(UniResourceContainer smaller, UniResourceContainer bigger)
    {
        recipes.add(new ShapedOreRecipe(bigger.getMainEntry(), "SSS", "SSS", "SSS", 'S', smaller.name));
        recipes.add(new ShapelessOreRecipe(smaller.getMainEntry(9), bigger.name));
    }

    private static void inspectionOfRecipes(ResourceHandler resourceHandler)
    {
        for (Iterator<IRecipe> recipesIterator = recipes.iterator(); recipesIterator.hasNext(); )
            if (resourceHandler.exists(MetaItem.get(recipesIterator.next().getRecipeOutput())))
                recipesIterator.remove();
    }

    private static void specialInspectionOfRecipes(ResourceHandler resourceHandler, TIntSet gearHashes)
    {
        int hash;
        for (Iterator<IRecipe> recipesIterator = recipes.iterator(); recipesIterator.hasNext(); )
            if (resourceHandler.exists(hash = MetaItem.get(recipesIterator.next().getRecipeOutput())) || gearHashes.contains(hash))
                recipesIterator.remove();
    }
}