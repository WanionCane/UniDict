package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import exter.foundry.api.recipe.ICastingRecipe;
import exter.foundry.recipes.manager.CastingRecipeManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.helper.NEIHelper;

import java.util.Iterator;

public final class FoundryUniHelper
{
    private FoundryUniHelper() {}

    public static void removeMold(ItemStack removeTarget)
    {
        int targetHash = MetaItem.get(removeTarget);
        for (Iterator<ICastingRecipe> castingRecipeIterator = CastingRecipeManager.instance.recipes.iterator(); castingRecipeIterator.hasNext(); )
            if (MetaItem.get(castingRecipeIterator.next().GetInputMold()) == targetHash)
                castingRecipeIterator.remove();
        if (Config.autoHideInNEI)
            NEIHelper.hide(removeTarget);
    }

    public static void removeCast(ItemStack removeTarget)
    {
        int targetHash = MetaItem.get(removeTarget);
        ICastingRecipe bufferRecipe;
        for (Iterator<ICastingRecipe> castingRecipeIterator = CastingRecipeManager.instance.recipes.iterator(); castingRecipeIterator.hasNext(); )
            if ((bufferRecipe = castingRecipeIterator.next()) != null && (MetaItem.get(bufferRecipe.GetInputMold()) == targetHash || MetaItem.get(bufferRecipe.GetOutputItem()) == targetHash))
                castingRecipeIterator.remove();
        if (Config.autoHideInNEI)
            NEIHelper.hide(removeTarget);
    }
}
