package wanion.unidict.api.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import wanion.lib.common.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.UniJEIPlugin;

import javax.annotation.Nonnull;
import java.util.Iterator;

public final class TConUniHelper
{
	private TConUniHelper() {}

	public static void removeCast(@Nonnull final ItemStack removeTarget)
	{
		final int targetHash = MetaItem.get(removeTarget);
		ICastingRecipe castingRecipe;
		RecipeMatch recipeMatch;
		for (final Iterator<ICastingRecipe> castingRecipeIterator = TinkerRegistry.getAllTableCastingRecipes().iterator(); castingRecipeIterator.hasNext(); )
			if ((castingRecipe = castingRecipeIterator.next()) != null && castingRecipe instanceof CastingRecipe && (MetaItem.get(((CastingRecipe) castingRecipe).getResult()) == targetHash || ((recipeMatch = ((CastingRecipe) castingRecipe).cast) != null && MetaItem.getSet(recipeMatch.getInputs()).contains(targetHash))))
				castingRecipeIterator.remove();
		if (UniDict.getConfig().autoHideInJEI)
			UniJEIPlugin.hide(removeTarget);
	}
}