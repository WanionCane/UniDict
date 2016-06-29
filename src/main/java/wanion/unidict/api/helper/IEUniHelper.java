package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.helper.NEIHelper;

import java.util.Iterator;

public final class IEUniHelper
{
    private IEUniHelper() {}

    public static void removeMold(ItemStack removeTarget)
    {
        int targetHash = MetaItem.get(removeTarget);
        for (Iterator<MetalPressRecipe> metalPressRecipesIterator = MetalPressRecipe.recipeList.values().iterator(); metalPressRecipesIterator.hasNext(); )
            if (MetaItem.get(metalPressRecipesIterator.next().mold.stack) == targetHash)
                metalPressRecipesIterator.remove();
        if (Config.autoHideInNEI)
            NEIHelper.hide(removeTarget);
    }
}
