package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import cpw.mods.fml.common.registry.GameData;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.items.ItemCrated;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.CarpenterRecipeManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.api.helper.ForestryUniHelper;
import wanion.unidict.common.Util;
import wanion.unidict.helper.LogHelper;
import wanion.unidict.helper.NEIHelper;
import wanion.unidict.resource.UniResourceContainer;

import java.util.*;

final class ForestryIntegration extends AbstractIntegrationThread
{
    private Set<Integer> thingsToRemove = new HashSet<>(64);
    private Map<UniResourceContainer, ItemCrated> uniDictCrates = new LinkedHashMap<>();
    private Set<ICarpenterRecipe> carpenterRecipes = Util.getField(CarpenterRecipeManager.class, "recipes", null, Set.class);

    ForestryIntegration()
    {
        super("Forestry");
        populateThingsToRemove();
    }

    private void populateThingsToRemove()
    {
        if (resourceHandler.resourceExists("Bronze")) {
            thingsToRemove.add(MetaItem.get(GameData.getItemRegistry().getRaw("Forestry:cratedBronze")));
            thingsToRemove.add(MetaItem.get(GameData.getItemRegistry().getRaw("Forestry:cratedBrass")));
        }
        if (resourceHandler.resourceExists("Tin"))
            thingsToRemove.add(MetaItem.get(GameData.getItemRegistry().getRaw("Forestry:cratedTin")));
        if (resourceHandler.resourceExists("Copper"))
            thingsToRemove.add(MetaItem.get(GameData.getItemRegistry().getRaw("Forestry:cratedCopper")));
        if (resourceHandler.resourceExists("Silver"))
            thingsToRemove.add(MetaItem.get(GameData.getItemRegistry().getRaw("Forestry:cratedSilver")));
    }

    @Override
    public String call()
    {
        try {
            removeBadCarpenterOutputs(carpenterRecipes);
            if (resourceHandler.containerExists("ingotBronze"))
                bronzeThings();
            if (!Config.forestryTweak) {
                createCratesDefault();
                ForestryUniHelper.registerCratesAndCreateRecipes(uniDictCrates);
            } else
                ForestryUniHelper.createCrates("ingot");
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "All these bees... they can hurt, you know?";
    }

    private void removeBadCarpenterOutputs(Set<ICarpenterRecipe> carpenterRecipes)
    {
        for (Iterator<ICarpenterRecipe> carpenterRecipeIterator = carpenterRecipes.iterator(); carpenterRecipeIterator.hasNext(); )
        {
            IDescriptiveRecipe carpenterRecipe = carpenterRecipeIterator.next().getCraftingGridRecipe();
            ItemStack output = carpenterRecipe.getRecipeOutput();
            int id = MetaItem.get(output);
            boolean existsInResource = resourceHandler.exists(id);
            boolean existsInThingsToRemove = thingsToRemove.contains(id);
            if (Config.autoHideInNEI && existsInThingsToRemove)
                NEIHelper.hide(output);
            if (existsInResource || existsInThingsToRemove)
                carpenterRecipeIterator.remove();
        }
    }

    private void bronzeThings()
    {
        UniResourceContainer ingotBronze = resourceHandler.getContainer("ingotBronze");
        carpenterRecipes.add(new CarpenterRecipe(5, null, null, new ShapedRecipeCustom(ingotBronze.getMainEntry(2), "X  ", "   ", "   ", 'X', new ItemStack(GameData.getItemRegistry().getRaw("Forestry:brokenBronzePickaxe")))));
        carpenterRecipes.add(new CarpenterRecipe(5, null, null, new ShapedRecipeCustom(ingotBronze.getMainEntry(1), "X  ", "   ", "   ", 'X', new ItemStack(GameData.getItemRegistry().getRaw("Forestry:brokenBronzeShovel")))));
    }

    private void createCratesDefault()
    {
        UniResourceContainer ingotCopper = resourceHandler.getContainer("ingotCopper");
        UniResourceContainer ingotTin = resourceHandler.getContainer("ingotTin");
        UniResourceContainer ingotSilver = resourceHandler.getContainer("ingotSilver");
        UniResourceContainer ingotBronze = resourceHandler.getContainer("ingotBronze");
        if (ingotCopper != null)
            uniDictCrates.put(ingotCopper, new ItemCrated(ingotCopper.getMainEntry(), true));
        if (ingotTin != null)
            uniDictCrates.put(ingotTin, new ItemCrated(ingotTin.getMainEntry(), true));
        if (ingotSilver != null)
            uniDictCrates.put(ingotSilver, new ItemCrated(ingotSilver.getMainEntry(), true));
        if (ingotBronze != null)
            uniDictCrates.put(ingotBronze, new ItemCrated(ingotBronze.getMainEntry(), true));
    }
}