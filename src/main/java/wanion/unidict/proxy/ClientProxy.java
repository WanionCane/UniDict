package wanion.unidict.proxy;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.client.util.RecipeBookClient;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import wanion.unidict.UniDict;

public class ClientProxy extends CommonProxy {
	@Override
	public void postInit(final FMLPostInitializationEvent event)
	{
		super.postInit(event);

		try {
			RecipeBookClient.rebuildTable();
			UniDict.getLogger().info("Fixed the Recipe Book");
		}
		catch (Exception e){
			UniDict.getLogger().error("Failed to fix Recipe Book");
			e.printStackTrace();
		}
	}
}