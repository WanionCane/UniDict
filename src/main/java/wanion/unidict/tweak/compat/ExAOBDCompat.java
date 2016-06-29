package wanion.unidict.tweak.compat;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import exnihilo.items.ores.ItemOre;
import exnihilo.registries.helpers.SiftingResult;
import exnihilo.utils.ItemInfo;
import ganymedes01.aobd.items.AOBDItem;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import wanion.unidict.common.SmelteryFluidWrapper;
import wanion.unidict.tweak.ExNihiloTweak;

import java.util.List;

public final class ExAOBDCompat extends ExNihiloTweak
{
    @Override
    protected void searchForThingsThatCanMelt()
    {
        for (ItemInfo itemInfoKey : sieveRegistry.keySet()) {
            final List<SiftingResult> results = sieveRegistry.get(itemInfoKey);
            for (SiftingResult result : results) {
                if (!(result.item instanceof ItemOre || result.item instanceof AOBDItem))
                    continue;
                String materialName;
                if (result.item instanceof AOBDItem)
                    materialName = WordUtils.uncapitalize(((AOBDItem) result.item).getOre().name());
                else
                    materialName = WordUtils.uncapitalize(pattern.matcher(result.item.getUnlocalizedName()).replaceAll(""));
                if (!canMelt.containsKey(materialName))
                    canMelt.put(materialName, new SmelteryFluidWrapper(materialName, 72));
                canMelt.get(materialName).meltingList.add(new ItemStack(result.item, 1, result.meta));
            }
        }
    }
}