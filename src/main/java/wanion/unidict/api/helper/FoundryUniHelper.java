package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import exter.foundry.api.recipe.ICastingRecipe;
import exter.foundry.api.recipe.IMoldRecipe;
import exter.foundry.recipes.manager.CastingRecipeManager;
import exter.foundry.recipes.manager.MoldRecipeManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.MetaItem;
import wanion.unidict.UniJEIPlugin;

import java.util.Iterator;

public final class FoundryUniHelper
{
    private FoundryUniHelper() {}

    public static void removeMold(final ItemStack removeTarget)
    {
        final int targetHash = MetaItem.get(removeTarget);
        for (Iterator<ICastingRecipe> castingRecipeIterator = CastingRecipeManager.instance.recipes.iterator(); castingRecipeIterator.hasNext(); )
            if (MetaItem.get(castingRecipeIterator.next().getMold()) == targetHash)
                castingRecipeIterator.remove();
        if (Config.autoHideInJEI)
            UniJEIPlugin.hide(removeTarget);
    }

    public static void removeCast(final ItemStack removeTarget)
    {
        final int targetHash = MetaItem.get(removeTarget);
        ICastingRecipe bufferRecipe;
        for (Iterator<ICastingRecipe> castingRecipeIterator = CastingRecipeManager.instance.recipes.iterator(); castingRecipeIterator.hasNext(); )
            if ((bufferRecipe = castingRecipeIterator.next()) != null && (MetaItem.get(bufferRecipe.getMold()) == targetHash || MetaItem.get(bufferRecipe.getOutput()) == targetHash))
                castingRecipeIterator.remove();
        if (Config.autoHideInJEI)
            UniJEIPlugin.hide(removeTarget);
    }

    public static void removeMoldRecipe(final ItemStack removeTarget)
    {
        final int targetHash = MetaItem.get(removeTarget);
        IMoldRecipe bufferRecipe;
        for (Iterator<IMoldRecipe> moldRecipeIterator = MoldRecipeManager.instance.recipes.iterator(); moldRecipeIterator.hasNext(); )
            if ((bufferRecipe = moldRecipeIterator.next()) != null && MetaItem.get(bufferRecipe.getOutput()) == targetHash)
                moldRecipeIterator.remove();
    }
}
