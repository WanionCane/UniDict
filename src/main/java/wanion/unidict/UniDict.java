package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import wanion.lib.common.Dependencies;
import wanion.lib.module.ModuleHandler;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.proxy.CommonProxy;
import wanion.unidict.resource.ResourceHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static wanion.unidict.common.Reference.*;

@SuppressWarnings("unused")
@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, acceptedMinecraftVersions = MC_VERSION, dependencies = DEPENDENCIES)
public final class UniDict
{
	@Mod.Instance(MOD_ID)
	public static UniDict instance;

	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
	public static CommonProxy proxy;

	private static Logger logger;

	public static Logger getLogger()
	{
		return logger;
	}

	public static Dependencies<IDependency> getDependencies()
	{
		return proxy.dependencies;
	}

	public static ResourceHandler getResourceHandler()
	{
		return proxy.dependencies.get(ResourceHandler.class);
	}

	public static UniOreDictionary getUniOreDictionary()
	{
		return proxy.dependencies.get(UniOreDictionary.class);
	}

	public static Config getConfig()
	{
		return Config.INSTANCE;
	}

	public static UniDictAPI getAPI()
	{
		return proxy.dependencies.get(UniDictAPI.class);
	}

	public static ModuleHandler getModuleHandler()
	{
		return proxy.moduleHandler;
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
		proxy.clean();
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