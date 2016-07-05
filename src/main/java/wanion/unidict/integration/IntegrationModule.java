package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import wanion.unidict.module.AbstractModule;

import static wanion.unidict.Config.*;

public final class IntegrationModule extends AbstractModule
{
    public IntegrationModule()
    {
        super("Integration");
    }

    @Override
    protected void init()
    {
        if (craftingIntegration)
            add(new CraftingIntegration());
        if (furnaceIntegration)
            add(new FurnaceIntegration());
        if (abyssalCraft)
            add(new AbyssalCraftIntegration());
        if (baseMetalsIntegration)
            add(new BaseMetalsIntegration());
        if (enderIOIntegration)
            add(new EnderIOIntegration());
        if (foundryIntegration)
            add(new FoundryIntegration());
        if (ic2Integration)
            add(new IC2Integration());
        if (techRebornIntegration)
            add(new TechRebornIntegration());
    }
}