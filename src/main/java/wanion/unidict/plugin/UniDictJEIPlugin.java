package wanion.unidict.plugin;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;
import wanion.lib.common.MetaItem;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public final class UniDictJEIPlugin implements IModPlugin
{
	private static final List<ItemStack> stacksToHideList = Collections.synchronizedList(new ArrayList<>());

	public static void hide(final ItemStack itemStack)
	{
		stacksToHideList.add(itemStack);
	}

	@Override
	public void register(@Nonnull final IModRegistry iModRegistry)
	{
		if (stacksToHideList.isEmpty())
			return;
		UniDict.getLogger().info("Hiding items from JEI...");
		final IIngredientBlacklist iIngredientBlacklist = iModRegistry.getJeiHelpers().getIngredientBlacklist();
		List<Integer> blackList =
				Util.stringListToItemStackList(UniDict.getConfig().hideInJEIItemBlackSet).stream().map(MetaItem::get).collect(Collectors.toList());
		for (ItemStack itemStack : stacksToHideList) {
			if (!blackList.contains(MetaItem.get(itemStack)))
				iIngredientBlacklist.addIngredientToBlacklist(itemStack);
		}
	}
}