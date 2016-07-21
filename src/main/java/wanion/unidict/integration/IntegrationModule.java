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
        if (chestIntegration)
            manager.add(ChestIntegration.class);
        if (furnaceIntegration)
            manager.add(FurnaceIntegration.class);
        if (abyssalCraft)
            manager.add(AbyssalCraftIntegration.class);
        if (ae2Integration)
            manager.add(AE2Integration.class);
        if (electricalAgeIntegration)
            manager.add(ElectricalAgeIntegration.class);
        if (enderIOIntegration)
            manager.add(EnderIOIntegration.class);
        if (forestryIntegration)
            manager.add(ForestryIntegration.class);
        if (foundryIntegration)
            manager.add(FoundryIntegration.class);
        if (fspIntegration)
            manager.add(FSPIntegration.class);
        if (hydrauliCraftIntegration)
            manager.add(HydraulicraftIntegration.class);
        if (ic2Integration)
            manager.add(IC2Integration.class);
        if (ieIntegration)
            manager.add(IEIntegration.class);
        if (magnetiCraftIntegration)
            manager.add(MagneticraftIntegration.class);
        if (mekanismIntegration)
            manager.add(MekanismIntegration.class);
        if (railCraftIntegration)
            manager.add(RailcraftIntegration.class);
        if (teIntegration)
            manager.add(TEIntegration.class);
    }
}