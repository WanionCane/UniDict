package wanion.unidict.modconfig;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.lib.common.Util;
import wanion.lib.module.LoadStage;
import wanion.lib.module.SpecifiedLoadStage;

@SpecifiedLoadStage(stage = LoadStage.INIT)
public class GregTechModConfig extends AbstractModConfigThread {
    public GregTechModConfig() {
        super("GregTech");
    }

    @Override
    public String call() {
        Class<?> configHolderClass = null;
        try {
            configHolderClass = Class.forName("gregtech.common.ConfigHolder");
        } catch (ClassNotFoundException e) {
            logger.error("Couldn't find the class: \"gregtech.common.ConfigHolder\".");
        }

        if (configHolderClass != null) {
            Util.setField(configHolderClass, "useCustomModPriorities", null, true);
            Util.setField(configHolderClass, "modPriorities", null, getPreferredMods());
        }

        return threadName + "Fixed GregTech OreDict configuration.";
    }
}
