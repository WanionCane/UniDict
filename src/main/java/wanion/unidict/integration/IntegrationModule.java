package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.text.WordUtils;
import wanion.lib.module.AbstractModule;
import wanion.unidict.UniDict;
import wanion.unidict.common.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import static net.minecraftforge.fml.common.Loader.isModLoaded;
import static wanion.unidict.common.Reference.SLASH;

public final class IntegrationModule extends AbstractModule implements UniDict.IDependency
{
	private final Set<Class<AbstractIntegrationThread>> MOD_INTEGRATIONS = new LinkedHashSet<>();

	public IntegrationModule()
	{
		super("Integration", Class::newInstance);
	}

	public static IntegrationModule getIntegrationModule()
	{
		return UniDict.getDependencies().get(IntegrationModule.class);
	}

	public void registerIntegration(@Nonnull final Class<AbstractIntegrationThread> integrationClassToRegister)
	{
		if (!MOD_INTEGRATIONS.contains(integrationClassToRegister))
			MOD_INTEGRATIONS.add(integrationClassToRegister);
	}

	@Override
	protected void init()
	{
		final Configuration config = new Configuration(new File("." + SLASH + "config" + SLASH + Reference.MOD_ID + SLASH + "IntegrationModule.cfg"));
		for (final Integration integration : Integration.values())
			if (config.get("Integrations", WordUtils.capitalizeFully(integration.name().replace("_", " ")).replace(" ", ""), integration.enabledByDefault).getBoolean() && (integration.modId == null || isModLoaded(integration.modId)))
				manager.add(integration.integrationClass);
		MOD_INTEGRATIONS.forEach(manager::add);
		if (config.hasChanged())
			config.save();
	}

	private enum Integration
	{
		CRAFTING(CraftingIntegration.class),
		FURNACE(FurnaceIntegration.class),
		ABYSSAL_CRAFT("abyssalcraft", AbyssalCraftIntegration.class),
		ADVANCED_ROCKETRY("advancedrocketry", AdvancedRocketryIntegration.class),
		APPLIED_ENERGISTICS_2("appliedenergistics2", AE2Integration.class),
		BASE_METALS("basemetals", BaseMetalsIntegration.class),
		BLOOD_MAGIC("bloodmagic", BloodMagicIntegration.class),
		EMBERS("embers", EmbersIntegration.class),
		ENDER_IO("enderio", EnderIOIntegration.class),
		EXTREME_ENERGY("meem", ExtremeEnergyIntegration.class),
		FORESTRY("forestry", ForestryIntegration.class),
		FORGE_CRAFT("forgecraft", ForgeCraftIntegration.class),
		GADGETRY_CORE("gadgetrycore", GadgetryCoreIntegration.class),
		GADGETRY_MACHINES("gadgetrymachines", GadgetryMachinesIntegration.class),
		IMMERSIVE_ENGINEERING("immersiveengineering", IEIntegration.class),
		INDUSTRIAL_CRAFT_2("ic2", IC2Integration.class),
		INDUSTRIAL_FOREGOING("industrialforegoing", IndustrialForegoingIntegration.class),
		MAGNETICRAFT("magneticraft", MagneticraftIntegration.class),
		MEKANISM("mekanism", MekanismIntegration.class),
		TECH_REBORN("techreborn", TechRebornIntegration.class),
		THERMAL_EXPANSION("thermalexpansion", TEIntegration.class);

		private final String modId;
		private final Class<? extends AbstractIntegrationThread> integrationClass;
		private final boolean enabledByDefault;

		Integration(@Nonnull final Class<? extends AbstractIntegrationThread> integrationClass)
		{
			this(null, integrationClass);
		}

		Integration(@Nullable final String modId, @Nonnull final Class<? extends AbstractIntegrationThread> integrationClass)
		{
			this.modId = modId;
			this.integrationClass = integrationClass;
			this.enabledByDefault = true;
		}

		Integration(@Nullable final String modId, @Nonnull final Class<? extends AbstractIntegrationThread> integrationClass, final boolean enabledByDefault)
		{
			this.modId = modId;
			this.integrationClass = integrationClass;
			this.enabledByDefault = enabledByDefault;
		}
	}
}