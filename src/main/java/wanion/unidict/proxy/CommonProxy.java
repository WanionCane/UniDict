package wanion.unidict.proxy;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import wanion.lib.common.Dependencies;
import wanion.lib.module.AbstractModule;
import wanion.lib.module.ModuleHandler;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.common.SpecificEntryItemStackComparator;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.integration.IntegrationModule;
import wanion.unidict.resource.UniResourceHandler;

import java.util.Set;

public class CommonProxy
{
	private UniResourceHandler uniResourceHandler = null;
	public Dependencies<UniDict.IDependency> dependencies = new Dependencies<>();
	public ModuleHandler moduleHandler = null;

	public void preInit(final FMLPreInitializationEvent event)
	{
		moduleHandler = searchForModules(populateModules(new ModuleHandler()), event.getAsmData());
	}

	public void init()
	{
		(uniResourceHandler = new UniResourceHandler()).init();
	}

	public void postInit(final FMLPostInitializationEvent event)
	{
		uniResourceHandler.postInit();
		moduleHandler.startModules(event);
		final ForgeRegistry<IRecipe> recipeRegistry = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);
		UniDict.getConfig().recipesToRemove.forEach(recipeRegistry::remove);
	}

	private ModuleHandler populateModules(final ModuleHandler moduleHandler)
	{
		final Config config = UniDict.getConfig();
		if (!config.libraryMode && config.integrationModule)
			moduleHandler.addModule(new IntegrationModule());
		return moduleHandler;
	}

	private ModuleHandler searchForModules(final ModuleHandler moduleHandler, final ASMDataTable asmDataTable)
	{
		final Set<ASMDataTable.ASMData> modules = asmDataTable.getAll("wanion.unidict.UniDict$Module");
		modules.forEach(asmData -> {
			try {
				final Class<?> mayBeAModule = Class.forName(asmData.getClassName());
				if (mayBeAModule.getSuperclass().isAssignableFrom(AbstractModule.class))
					moduleHandler.addModule((AbstractModule) mayBeAModule.newInstance());
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				UniDict.getLogger().error("Cannot load ", asmData.getClassName(), e);
			}
		});
		return moduleHandler;
	}

	public void clean()
	{
		uniResourceHandler = null;
		moduleHandler = null;
		SpecificKindItemStackComparator.kindSpecificComparators = null;
		SpecificEntryItemStackComparator.entrySpecificComparators = null;
	}
}