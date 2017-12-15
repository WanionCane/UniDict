package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import org.apache.logging.log4j.Logger;
import wanion.lib.module.AbstractModuleThread;
import wanion.lib.module.LoadStage;
import wanion.lib.module.SpecifiedLoadStage;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;

@SpecifiedLoadStage(stage = LoadStage.POST_INIT)
abstract class AbstractIntegrationThread extends AbstractModuleThread
{
	protected final ResourceHandler resourceHandler = UniDict.getResourceHandler();
	protected final Config config = UniDict.getConfig();
	protected final Logger logger = UniDict.getLogger();

	AbstractIntegrationThread(@Nonnull final String integrationName)
	{
		super(integrationName, "Integration");
	}
}