package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import wanion.unidict.MetaItem;
import wanion.unidict.UniDict.IDependence;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FurnaceUniHelper implements IDependence
{
    @SuppressWarnings("unchecked")
    private final Map<ItemStack, ItemStack> furnaceRecipes = FurnaceRecipes.smelting().getSmeltingList();
    private final TIntObjectMap<List<ItemStack>> smartFurnaceRecipes = new TIntObjectHashMap<>();

    private FurnaceUniHelper()
    {
        populateSmartFurnaceRecipes();
    }

    public void remove(@Nonnull final ItemStack itemStack)
    {
        remove(MetaItem.get(itemStack));
    }

    public void remove (final int hash)
    {
        final List<ItemStack> furnaceInputStacks = smartFurnaceRecipes.get(hash);
        if (furnaceInputStacks != null) {
            furnaceInputStacks.forEach(furnaceRecipes::remove);
            smartFurnaceRecipes.remove(hash);
        }
    }

    private void populateSmartFurnaceRecipes()
    {
        int hash;
        for (ItemStack furnaceInputStack : furnaceRecipes.keySet()) {
            if ((hash = MetaItem.get(furnaceInputStack)) != 0) {
                final List<ItemStack> furnaceInputStacks = (!smartFurnaceRecipes.containsKey(hash)) ? new ArrayList<ItemStack>() : smartFurnaceRecipes.get(hash);
                furnaceInputStacks.add(furnaceInputStack);
                smartFurnaceRecipes.put(hash, furnaceInputStacks);
            }
        }
    }
}