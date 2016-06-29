package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import gnu.trove.set.TIntSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.api.helper.FoundryUniHelper;
import wanion.unidict.api.helper.FurnaceUniHelper;
import wanion.unidict.api.helper.IEUniHelper;
import wanion.unidict.api.helper.TConUniHelper;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniResourceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RecipeHelper
{
    @SuppressWarnings("unchecked")
    public static final List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

    public static void init()
    {
        ResourceHandler resourceHandler = UniDict.getDependencies().get(ResourceHandler.class);
        TIntSet gearHashes = (Config.gearRecipesRequiresSomeGear && Resource.kindExists("gear")) ? createGearHashSet() : null;
        if (gearHashes == null)
            inspectionOfRecipes(resourceHandler);
        else {
            FMLControlledNamespacedRegistry<Item> itemRegistry = GameData.getItemRegistry();
            if (Config.foundry) {
                final Item foundryMold = itemRegistry.getRaw("foundry:foundryMold");
                FoundryUniHelper.removeMold(new ItemStack(foundryMold, 1, 22));
                ItemStack foundryMoldStack = new ItemStack(foundryMold, 1, 23);
                int hash = MetaItem.get(foundryMoldStack);
                gearHashes.add(hash);
                UniDict.getDependencies().get(FurnaceUniHelper.class).remove(hash);
                if (Config.autoHideInNEI)
                    NEIHelper.hide(foundryMoldStack);
            }
            if (Config.ieIntegration) {
                ItemStack ieGearMold = new ItemStack(itemRegistry.getRaw("ImmersiveEngineering:mold"), 1, 1);
                gearHashes.add(MetaItem.get(ieGearMold));
                IEUniHelper.removeMold(ieGearMold);
            }
            if (Config.tinkersConstruct) {
                ItemStack gearCast = new ItemStack(itemRegistry.getRaw("TConstruct:gearCast"));
                TConUniHelper.removeCast(gearCast);
                if (Config.foundry)
                    FoundryUniHelper.removeCast(gearCast);
            }
            specialInspectionOfRecipes(resourceHandler, gearHashes);
        }
    }

    private static TIntSet createGearHashSet()
    {
        int gear = Resource.getKindOfName("gear");
        return MetaItem.getSet(UniDict.getAPI().getResources(gear, Resource.getKindOfName("ingot")), gear);
    }

    public static void singleWayCompressionRecipe(List<Resource> smallerAndBiggerResources, int smaller, int bigger)
    {
        for (Resource resource : smallerAndBiggerResources)
            recipes.add(new ShapedOreRecipe(resource.getChild(bigger).getMainEntry(), "SSS", "SSS", "SSS", 'S', resource.getChild(smaller).name));
    }

    public static void resourcesToCompressionRecipes(Collection<Resource> resources, int... smallerToBigger)
    {
        UniResourceContainer smaller, bigger;
        for (Resource resource : resources)
            for (int i = 0; i < smallerToBigger.length - 1; i++)
                if ((smaller = resource.getChild(smallerToBigger[i])) != null && (bigger = resource.getChild(smallerToBigger[i + 1])) != null)
                    createCompressionRecipe(smaller, bigger);
    }

    private static void createCompressionRecipe(UniResourceContainer bigger, UniResourceContainer smaller)
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