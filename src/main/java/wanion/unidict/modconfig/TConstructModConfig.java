package wanion.unidict.modconfig;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;
import wanion.lib.module.LoadStage;
import wanion.lib.module.SpecifiedLoadStage;

import java.util.Map;

@SpecifiedLoadStage(stage = LoadStage.INIT)
public class TConstructModConfig extends AbstractModConfigThread {
    public TConstructModConfig() {
        super("TConstruct");
    }

    @Override
    public String call() {
        Class<?> recipeUtilClass = null;
        try {
            recipeUtilClass = Class.forName("slimeknights.tconstruct.library.utils.RecipeUtil");
        } catch (ClassNotFoundException e) {
            logger.error("Couldn't find the class: \"slimeknights.tconstruct.library.utils.RecipeUtil\".");
        }

        if (recipeUtilClass != null){
            Util.setField(recipeUtilClass, "orePreferences", null, getPreferredMods());
            final Map<String, ItemStack> preferenceCache = Util.getField(recipeUtilClass, "preferenceCache", null, Map.class);
            if (preferenceCache != null && preferenceCache.size() > 0)
                preferenceCache.clear();
        }

        return threadName + "Fixed TConstruct OreDict configuration.";
    }
}
