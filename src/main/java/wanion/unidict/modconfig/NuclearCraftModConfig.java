package wanion.unidict.modconfig;

import wanion.lib.common.Util;
import wanion.lib.module.LoadStage;
import wanion.lib.module.SpecifiedLoadStage;

@SpecifiedLoadStage(stage = LoadStage.PRE_INIT)
public class NuclearCraftModConfig extends AbstractModConfigThread  {
    public NuclearCraftModConfig() {
        super("NuclearCraft");
    }

    @Override
    public String call() {
        Class<?> ncConfigClass = null;
        try {
            ncConfigClass = Class.forName("nc.config.NCConfig");
        } catch (ClassNotFoundException e) {
            logger.error("Couldn't find the class: \"nc.config.NCConfig\".");
        }

        if (ncConfigClass != null) {
            Util.setField(ncConfigClass, "ore_dict_priority", null, getPreferredMods());
            Util.setField(ncConfigClass, "ore_dict_priority_bool", null, true);
            Util.setField(ncConfigClass, "ore_dict_raw_material_recipes", null, true);
        }

        return threadName + "Fixed NuclearCraft OreDict configuration.";
    }
}
