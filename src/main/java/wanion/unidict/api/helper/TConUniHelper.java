package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.UniJEIPlugin;

import java.util.Iterator;

public final class TConUniHelper
{
    private TConUniHelper() {}

    public static void removeCast(ItemStack removeTarget)
    {
        int targetHash = MetaItem.get(removeTarget);
        for (Iterator<CastingRecipe> castingRecipeIterator = TinkerRegistry.getAllTableCastingRecipes().iterator(); castingRecipeIterator.hasNext(); )
        {
            CastingRecipe castingRecipe = castingRecipeIterator.next();
            if (MetaItem.get(castingRecipe.getResult()) == targetHash)
                castingRecipeIterator.remove();
        }
        if (Config.autoHideInJEI)
            UniJEIPlugin.hide(removeTarget);
    }
}