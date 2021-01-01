package wanion.unidict.modconfig;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.text.WordUtils;
import wanion.lib.module.AbstractModule;
import wanion.unidict.UniDict;
import wanion.unidict.common.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

import static wanion.unidict.common.Reference.SLASH;

public class ModConfigModule extends AbstractModule implements UniDict.IDependency {
    public ModConfigModule() { super("Mod Config", Class::newInstance); }

    public static ModConfigModule getModConfigModule()
    {
        return UniDict.getDependencies().get(ModConfigModule.class);
    }

    @Override
    protected void init() {
        final Configuration config =
                new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_ID + SLASH + "ModConfigModule.cfg"));

        for (final ModConfigModule.ModConfig integration : ModConfigModule.ModConfig.values()) {
            String configName = WordUtils.capitalizeFully(integration.name().replace("_", ""));
            if (config.get("ModConfigs", configName, true).getBoolean() && Loader.isModLoaded(integration.modId)) {
                manager.add(integration.modConfigClass);
            }
        }

        if (config.hasChanged())
            config.save();
    }

    private enum ModConfig {
        TCONSTRUCT("tconstruct", TConstructModConfig.class);

        private final String modId;
        private final Class<? extends AbstractModConfigThread> modConfigClass;

        ModConfig(@Nullable final String modId, @Nonnull final Class<? extends AbstractModConfigThread> modConfigClass) {
            this.modId = modId;
            this.modConfigClass = modConfigClass;
        }
    }
}
