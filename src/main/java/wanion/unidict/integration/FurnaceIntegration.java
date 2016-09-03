package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.resource.UniResourceContainer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

final class FurnaceIntegration extends AbstractIntegrationThread
{
    FurnaceIntegration()
    {
        super("Furnace");
    }

    @Override
    public String call()
    {
        try {
            optimizeFurnaceRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "Some things that you smelted appear to be different now.";
    }

    private void optimizeFurnaceRecipes()
    {
        if (!Config.inputReplacement)
            for (final Map.Entry<ItemStack, ItemStack> furnaceRecipe : FurnaceRecipes.instance().getSmeltingList().entrySet())
                furnaceRecipe.setValue(resourceHandler.getMainItemStack(furnaceRecipe.getValue()));
        else {
            final Map<UniResourceContainer, TIntSet> containerKindMap = new IdentityHashMap<>();
            final Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.instance().getSmeltingList();
            final Map<ItemStack, ItemStack> newRecipes = new HashMap<>();
            for (final Iterator<Map.Entry<ItemStack, ItemStack>> furnaceRecipeIterator = furnaceRecipes.entrySet().iterator(); furnaceRecipeIterator.hasNext(); )
            {
                final Map.Entry<ItemStack, ItemStack> furnaceRecipe = furnaceRecipeIterator.next();
                final UniResourceContainer inputContainer = resourceHandler.getContainer(furnaceRecipe.getKey());
                final UniResourceContainer outputContainer = resourceHandler.getContainer(furnaceRecipe.getValue());
                if (outputContainer == null)
                    continue;
                else if (inputContainer == null) {
                    furnaceRecipe.setValue(outputContainer.getMainEntry(furnaceRecipe.getValue().stackSize));
                    continue;
                }
                final int kind = inputContainer.kind;
                if (!containerKindMap.containsKey(outputContainer))
                    containerKindMap.put(outputContainer, new TIntHashSet());
                final TIntSet kindSet = containerKindMap.get(outputContainer);
                if (!kindSet.contains(kind)) {
                    kindSet.add(kind);
                    newRecipes.put(inputContainer.getMainEntry(furnaceRecipe.getKey().stackSize), outputContainer.getMainEntry(furnaceRecipe.getValue().stackSize));
                }
                furnaceRecipeIterator.remove();
            }
            furnaceRecipes.putAll(newRecipes);
        }
    }
}