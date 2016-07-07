package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import wanion.unidict.module.AbstractModule;

import javax.annotation.Nonnull;

import static wanion.unidict.Config.*;

public final class IntegrationModule extends AbstractModule
{
    public IntegrationModule()
    {
        super("Integration");
    }

    @Override
    @Nonnull
    protected Manager getAdder()
    {
        return new Manager(Class::newInstance);
    }

    @Override
    protected void init(@Nonnull final Manager manager)
    {
        if (craftingIntegration)
            manager.add(CraftingIntegration.class);
        if (furnaceIntegration)
            manager.add(FurnaceIntegration.class);
        if (abyssalCraft)
            manager.add(AbyssalCraftIntegration.class);
        if (baseMetalsIntegration)
            manager.add(BaseMetalsIntegration.class);
        if (enderIOIntegration)
            manager.add(EnderIOIntegration.class);
        if (foundryIntegration)
            manager.add(FoundryIntegration.class);
        if (ic2Integration)
            manager.add(IC2Integration.class);
        if (techRebornIntegration)
            manager.add(TechRebornIntegration.class);
    }
}