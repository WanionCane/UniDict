package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import javax.annotation.Nonnull;

enum IntegrationEnum
{
	CRAFTING("Forge", CraftingIntegration.class),
	FURNACE("Forge", FurnaceIntegration.class),
	ABYSSAL_CRAFT("abyssalcraft", AbyssalCraftIntegration.class),
	ADVANCED_ROCKETRY("advancedRocketry", AdvancedRocketryIntegration.class),
	APPLIED_ENERGISTICS_2("appliedenergistics2", AE2Integration.class),
	BASE_METALS("basemetals", BaseMetalsIntegration.class),
	BLOOD_MAGIC("BloodMagic", BloodMagicIntegration.class),
	CALCULATOR("Calculator", CalculatorIntegration.class),
	EMBERS("embers", EmbersIntegration.class),
	ENDER_IO("EnderIO", EnderIOIntegration.class),
	FORESTRY("forestry", ForestryIntegration.class),
	FOUNDRY("foundry", FoundryIntegration.class),
	INDUSTRIAL_CRAFT_2("IC2", IC2Integration.class),
	IMMERSIVE_ENGINEERING("immersiveengineering", IEIntegration.class),
	MEKANISM("Mekanism", MekanismIntegration.class),
	MODULAR_MACHINES("modularmachines", ModularMachinesIntegration.class),
	RAIL_CRAFT("Railcraft", RailcraftIntegration.class),
	REFINED_STORAGE("refinedstorage", RefinedStorageIntegration.class, false),
	TECH_REBORN("techreborn", TechRebornIntegration.class),
	WATER_POWER("waterpower", WaterPowerIntegration.class);

	final String modId;
	final Class<? extends AbstractIntegrationThread> integrationClass;
	final boolean enabledByDefault;

	IntegrationEnum(@Nonnull final String modId, @Nonnull final Class<? extends AbstractIntegrationThread> integrationClass)
	{
		this.modId = modId;
		this.integrationClass = integrationClass;
		this.enabledByDefault = true;
	}

	IntegrationEnum(@Nonnull final String modId, @Nonnull final Class<? extends AbstractIntegrationThread> integrationClass, final boolean enabledByDefault)
	{
		this.modId = modId;
		this.integrationClass = integrationClass;
		this.enabledByDefault = enabledByDefault;
	}
}