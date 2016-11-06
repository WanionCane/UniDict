package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import wanion.lib.common.Dependencies;
import wanion.lib.module.AbstractModule;
import wanion.lib.module.ModuleHandler;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.SpecificKindItemStackComparator;
import wanion.unidict.integration.IntegrationModule;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniResourceHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;

import static wanion.unidict.common.Reference.*;

@SuppressWarnings("unused")
@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, acceptedMinecraftVersions = MC_VERSION, dependencies = DEPENDENCIES)
public final class UniDict
{
	@Mod.Instance(MOD_ID)
	public static UniDict instance;

	private static Dependencies<IDependency> dependencies = new Dependencies<>();
	private static Logger logger;
	private UniResourceHandler uniResourceHandler = UniResourceHandler.create();
	private ModuleHandler moduleHandler;

	public static Dependencies<IDependency> getDependencies()
	{
		return dependencies;
	}

	public static Logger getLogger()
	{
		return logger;
	}

	public static ResourceHandler getResourceHandler()
	{
		return dependencies.get(ResourceHandler.class);
	}

	public static Config getConfig()
	{
		return dependencies.get(Config.class);
	}

	public static UniDictAPI getAPI()
	{
		return dependencies.get(UniDictAPI.class);
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		moduleHandler = searchForModules(populateModules(new ModuleHandler()), event.getAsmData());
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event)
	{
		uniResourceHandler.init();
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event)
	{
		uniResourceHandler.postInit();
		moduleHandler.startModules(event);
	}

	@Mod.EventHandler
	public void loadComplete(final FMLLoadCompleteEvent event)
	{
		moduleHandler.startModules(event);
		SpecificKindItemStackComparator.nullify();
		uniResourceHandler = null;
		moduleHandler = null;
		dependencies = null;
	}

	private ModuleHandler populateModules(final ModuleHandler moduleHandler)
	{
		if (dependencies.get(Config.class).integrationModule)
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
				logger.error("Cannot load ", asmData.getClassName(), e);
			}
		});
		return moduleHandler;
	}

	@NetworkCheckHandler
	public boolean matchModVersions(final Map<String, String> remoteVersions, final Side side)
	{
		return side == Side.CLIENT ? remoteVersions.containsKey(MOD_ID) : !remoteVersions.containsKey(MOD_ID) || remoteVersions.get(MOD_ID).equals(MOD_VERSION);
	}

	public interface IDependency {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Module {}
}