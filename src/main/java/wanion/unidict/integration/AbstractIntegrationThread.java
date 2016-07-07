package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import wanion.unidict.LoadStage;
import wanion.unidict.UniDict;
import wanion.unidict.module.AbstractModuleThread;
import wanion.unidict.module.SpecifiedLoadStage;
import wanion.unidict.resource.ResourceHandler;

@SpecifiedLoadStage(stage = LoadStage.POST_INIT)
abstract class AbstractIntegrationThread extends AbstractModuleThread
{
    protected final ResourceHandler resourceHandler = UniDict.getResourceHandler();

    AbstractIntegrationThread(String integrationName)
    {
        super(integrationName, "Integration");
    }
}