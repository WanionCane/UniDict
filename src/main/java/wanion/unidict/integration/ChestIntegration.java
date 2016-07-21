package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

final class ChestIntegration extends AbstractIntegrationThread
{
    ChestIntegration()
    {
        super("Chest");
    }

    @Override
    public String call()
    {
        String integrationSay = threadName + "Now you can find things that aren't so useless in chests.";
        try {
            final Map<String, ChestGenHooks> chestInfo = Util.getField(ChestGenHooks.class, "chestInfo", null, Map.class);
            if (chestInfo != null)
                chestInfo.values().forEach(this::lootChest);
        } catch (Exception e) {
            integrationSay = threadName + "i can't find any chests! T.T";
            UniDict.getLogger().error(e);
        }
        return integrationSay;
    }

    private void lootChest(@Nonnull final ChestGenHooks chestGenHooks)
    {
        final List<WeightedRandomChestContent> contents = Util.getField(ChestGenHooks.class, "contents", chestGenHooks, List.class);
        if (contents == null)
            return;
        for (final WeightedRandomChestContent someThing : contents) {
            final ItemStack someStack = resourceHandler.getMainItemStack(someThing.theItemId);
            if (someStack != someThing.theItemId)
                someThing.theItemId = someStack;
        }
    }
}