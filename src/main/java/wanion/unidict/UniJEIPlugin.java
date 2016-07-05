package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class UniJEIPlugin implements IModPlugin
{
    private IItemBlacklist itemBlackList;
    private static final List<ItemStack> stacksToHideList = new ArrayList<>();

    public static void hide(ItemStack itemStack)
    {
        stacksToHideList.add(itemStack);
    }

    @Override
    public void register(@Nonnull IModRegistry iModRegistry)
    {
        itemBlackList = iModRegistry.getJeiHelpers().getItemBlacklist();
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime iJeiRuntime)
    {
        for (ItemStack itemStack : stacksToHideList)
            itemBlackList.addItemToBlacklist(itemStack);
    }
}