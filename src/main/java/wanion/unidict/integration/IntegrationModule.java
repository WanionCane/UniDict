package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.module.AbstractModule;

public final class IntegrationModule extends AbstractModule
{
	public IntegrationModule()
	{
		super("Integration", Class::newInstance);
	}

	@Override
	protected void init()
	{
		final Config config = UniDict.getConfig();
		if (config.craftingIntegration)
			manager.add(CraftingIntegration.class);
		if (config.furnaceIntegration)
			manager.add(FurnaceIntegration.class);
		if (config.abyssalCraft)
			manager.add(AbyssalCraftIntegration.class);
		if (config.baseMetalsIntegration)
			manager.add(BaseMetalsIntegration.class);
		if (config.bloodMagicIntegration)
			manager.add(BloodMagicIntegration.class);
		if (config.calculatorIntegration)
			manager.add(CalculatorIntegration.class);
		if (config.enderIOIntegration)
			manager.add(EnderIOIntegration.class);
		if (config.forestryIntegration)
			manager.add(ForestryIntegration.class);
		if (config.foundryIntegration)
			manager.add(FoundryIntegration.class);
		if (config.ic2Integration)
			manager.add(IC2Integration.class);
		if (config.ieIntegration)
			manager.add(IEIntegration.class);
		if (config.mekanismIntegration)
			manager.add(MekanismIntegration.class);
		if (config.techRebornIntegration)
			manager.add(TechRebornIntegration.class);
	}
}