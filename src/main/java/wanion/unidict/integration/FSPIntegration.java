package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import flaxbeard.steamcraft.api.CrucibleLiquid;
import flaxbeard.steamcraft.api.SteamcraftRegistry;
import flaxbeard.steamcraft.tile.TileEntitySmasher;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.MutablePair;
import wanion.unidict.UniDict;

import java.util.Map;

final class FSPIntegration extends AbstractIntegrationThread
{
    FSPIntegration()
    {
        super("Flaxbeard's Steam Power");
    }

    @Override
    public String call()
    {
        try {
            fixCrucibleRecipes();
            fixRockCrusherRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "All this steam...";
    }

    private void fixCrucibleRecipes()
    {
        for (final MutablePair<CrucibleLiquid, Integer> liquidRecipe : SteamcraftRegistry.liquidRecipes.values()) {
            CrucibleLiquid crucibleLiquid = liquidRecipe.getLeft();
            crucibleLiquid.ingot = resourceHandler.getMainItemStack(crucibleLiquid.ingot);
            crucibleLiquid.nugget = resourceHandler.getMainItemStack(crucibleLiquid.nugget);
            crucibleLiquid.plate = resourceHandler.getMainItemStack(crucibleLiquid.plate);
        }
    }

    private void fixRockCrusherRecipes()
    {
        final Map<String, ItemStack> oreDicts = TileEntitySmasher.REGISTRY.oreDicts;
        for (final String oreDictName : oreDicts.keySet())
            oreDicts.put(oreDictName, resourceHandler.getMainItemStack(oreDicts.get(oreDictName)));
    }
}