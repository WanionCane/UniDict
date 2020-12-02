package wanion.unidict.proxy;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import wanion.lib.common.Dependencies;
import wanion.lib.common.Util;
import wanion.lib.module.AbstractModule;
import wanion.lib.module.ModuleHandler;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.common.SpecificEntryItemStackComparator;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.integration.AbstractIntegrationThread;
import wanion.unidict.integration.IntegrationModule;
import wanion.unidict.plugin.crafttweaker.UniDictCraftTweakerPlugin;
import wanion.unidict.resource.UniResourceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CommonProxy
{
	private UniResourceHandler uniResourceHandler = null;
	public Dependencies<UniDict.IDependency> dependencies = new Dependencies<>();
	public ModuleHandler moduleHandler = null;

	public void preInit(final FMLPreInitializationEvent event)
	{
		moduleHandler = searchForModules(populateModules(new ModuleHandler()), event.getAsmData());
		searchForIntegrations(event.getAsmData());
		if (Loader.isModLoaded("crafttweaker"))
			UniDictCraftTweakerPlugin.preInit();
		(uniResourceHandler = new UniResourceHandler()).preInit();
		moduleHandler.startModules(event);
	}

	public void init(final FMLInitializationEvent event)
	{
		uniResourceHandler.init(event);
		if (Loader.isModLoaded("tconstruct"))
			fixTCon();
		if (Loader.isModLoaded("crafttweaker"))
			UniDictCraftTweakerPlugin.init();
		moduleHandler.startModules(event);
	}

	// sorry KnightMiner.
	// for now, we are completely overriding the setting "S:orePreference" from tconstruct.cfg
	private void fixTCon()
	{
		Class<?> recipeUtilClass = null;
		try {
			recipeUtilClass = Class.forName("slimeknights.tconstruct.library.utils.RecipeUtil");
		} catch (ClassNotFoundException e) {
			UniDict.getLogger().error("Couldn't find the class: \"slimeknights.tconstruct.library.utils.RecipeUtil\".");
		}
		if (recipeUtilClass == null)
			return;
		Util.setField(recipeUtilClass, "orePreferences", null, new String[]{});
		final Map<String, ItemStack> preferenceCache = Util.getField(recipeUtilClass, "preferenceCache", null, Map.class);
		if (preferenceCache != null && preferenceCache.size() > 0)
			preferenceCache.clear();
	}

	public void postInit(final FMLPostInitializationEvent event)
	{
		uniResourceHandler.postInit(event);
		moduleHandler.startModules(event);
		proccessRecipesToRemove();
	}

	private void proccessRecipesToRemove() {
		final ForgeRegistry<IRecipe> recipeRegistry = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);

		final List<ResourceLocation> toRemove = new ArrayList<>();
		UniDict.getConfig().recipesToRemove.forEach(recipeStr -> {
			recipeStr = recipeStr.replace("\"", "");
			if (UniDict.getConfig().treatRecipesToRemoveAsRegex) {
				final Pattern pattern = Pattern.compile(recipeStr);

				recipeRegistry.forEach(recipe -> {
					ResourceLocation recipeName;
					if (recipe != null && (recipeName = recipe.getRegistryName()) != null && pattern.matcher(recipeName.toString()).matches())
						toRemove.add(recipeName);
				});
			}
			else {
				final int separator = recipeStr.indexOf(':');
				if (separator > 0)
					toRemove.add(new ResourceLocation(recipeStr.substring(0, separator), recipeStr.substring(separator + 1)));
			}
		});
		toRemove.forEach(recipeRegistry::remove);
	}

	public void loadComplete(final FMLLoadCompleteEvent event)
	{
		moduleHandler.startModules(event);
	}

	private ModuleHandler populateModules(final ModuleHandler moduleHandler)
	{
		final Config config = UniDict.getConfig();
		if (!config.libraryMode && config.integrationModule)
			moduleHandler.addModule(IntegrationModule.getIntegrationModule());
		return moduleHandler;
	}

	private ModuleHandler searchForModules(final ModuleHandler moduleHandler, final ASMDataTable asmDataTable)
	{
		if (UniDict.getConfig().libraryMode)
			return moduleHandler;
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

	@SuppressWarnings("unchecked")
	private void searchForIntegrations(final ASMDataTable asmDataTable)
	{
		if (UniDict.getConfig().libraryMode)
			return;
		final Set<ASMDataTable.ASMData> modules = asmDataTable.getAll("wanion.unidict.UniDict$Integration");
		modules.forEach(asmData -> {
			try {
				final Class<?> mayBeAIntegration = Class.forName(asmData.getClassName());
				if (mayBeAIntegration.getSuperclass().isAssignableFrom(AbstractIntegrationThread.class))
					IntegrationModule.getIntegrationModule().registerIntegration((Class<AbstractIntegrationThread>) mayBeAIntegration);
			} catch (ClassNotFoundException e) {
				UniDict.getLogger().error("Cannot load ", asmData.getClassName(), e);
			}
		});
	}

	public void clean()
	{
		uniResourceHandler = null;
		moduleHandler = null;
		dependencies = null;
		SpecificKindItemStackComparator.kindSpecificComparators = null;
		SpecificEntryItemStackComparator.entrySpecificComparators = null;
	}
}