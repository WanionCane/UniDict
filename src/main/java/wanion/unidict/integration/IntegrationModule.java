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
        if (chestIntegration)
            add(new ChestIntegration());
        if (furnaceIntegration)
            add(new FurnaceIntegration());
        if (abyssalCraft)
            add(new AbyssalCraftIntegration());
        if (ae2Integration)
            add(new AE2Integration());
        if (electricalAgeIntegration)
            add(new ElectricalAgeIntegration());
        if (enderIOIntegration)
            add(new EnderIOIntegration());
        if (forestryIntegration)
            add(new ForestryIntegration());
        if (foundryIntegration)
            add(new FoundryIntegration());
        if (fspIntegration)
            add(new FSPIntegration());
        if (hydrauliCraftIntegration)
            add(new HydraulicraftIntegration());
        if (ic2Integration)
            add(new IC2Integration());
        if (ieIntegration)
            add(new IEIntegration());
        if (magnetiCraftIntegration)
            add(new MagneticraftIntegration());
        if (mekanismIntegration)
            add(new MekanismIntegration());
        if (railCraftIntegration)
            add(new RailcraftIntegration());
        if (teIntegration)
            add(new TEIntegration());
    }
}