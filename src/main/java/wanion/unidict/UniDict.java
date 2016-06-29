package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.common.Dependencies;
import wanion.unidict.resource.ResourceHandler;
import wanion.unidict.resource.UniResourceHandler;

import java.util.Map;

import static wanion.unidict.common.Reference.*;

@SuppressWarnings("unused")
@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, acceptedMinecraftVersions = MC_VERSION,dependencies = "after:*")
public final class UniDict
{
    @Mod.Instance(MOD_ID)
    public static UniDict instance;

    private static Dependencies<IDependence> dependencies = new Dependencies<>();
    private UniResourceHandler uniResourceHandler = UniResourceHandler.create();

    public static Dependencies<IDependence> getDependencies()
    {
        return dependencies;
    }

    public static UniDictAPI getAPI()
    {
        return dependencies.get(UniDictAPI.class);
    }

    public static ResourceHandler getResourceHandler()
    {
        return dependencies.get(ResourceHandler.class);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Config.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        uniResourceHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        uniResourceHandler.postInit();
        new ModuleHandler().startModules();
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        uniResourceHandler = null;
        dependencies = null;
    }

    @NetworkCheckHandler
    public boolean matchModVersions(Map<String, String> remoteVersions, Side side)
    {
        return remoteVersions.containsKey(MOD_ID) && remoteVersions.get(MOD_ID).equals(MOD_VERSION);
    }

    public interface IDependence {}
}