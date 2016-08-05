package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.unidict.module.AbstractModule;

import static wanion.unidict.Config.*;

public final class IntegrationModule extends AbstractModule
{
    public IntegrationModule()
    {
        super("Integration", Class::newInstance);
    }

    @Override
    protected void init()
    {
        if (craftingIntegration)
            manager.add(CraftingIntegration.class);
        if (furnaceIntegration)
            manager.add(FurnaceIntegration.class);
        //if (oreGenIntegration)
        //    manager.add(OreGenIntegration.class);
        if (abyssalCraft)
            manager.add(AbyssalCraftIntegration.class);
        if (baseMetalsIntegration)
            manager.add(BaseMetalsIntegration.class);
        if (bloodMagicIntegration)
            manager.add(BloodMagicIntegration.class);
        if (calculatorIntegration)
            manager.add(CalculatorIntegration.class);
        if (enderIOIntegration)
            manager.add(EnderIOIntegration.class);
        if (forestryIntegration)
            manager.add(ForestryIntegration.class);
        if (foundryIntegration)
            manager.add(FoundryIntegration.class);
        if (ic2Integration)
            manager.add(IC2Integration.class);
        if (mekanismIntegration)
            manager.add(MekanismIntegration.class);
        if (techRebornIntegration)
            manager.add(TechRebornIntegration.class);
        if (techyIntegration)
            manager.add(TechyIntegration.class);
    }
}