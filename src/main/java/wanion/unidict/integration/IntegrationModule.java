package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraftforge.common.config.Configuration;
import wanion.lib.module.AbstractModule;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.common.Reference;

import java.io.File;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class IntegrationModule extends AbstractModule
{
	public IntegrationModule()
	{
		super("Integration", Class::newInstance);
	}

	@Override
	protected void init()
	{
		final Configuration config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_ID + SLASH + "IntegrationModule.cfg"));
		for (final IntegrationEnum integrationEnum : IntegrationEnum.values())
			if (config.get("enabledIntegrations", integrationEnum.name(), integrationEnum.enabledByDefault).getBoolean() && isModLoaded(integrationEnum.modId))
				manager.add(integrationEnum.integrationClass);
		if (config.hasChanged())
			config.save();
	}
}