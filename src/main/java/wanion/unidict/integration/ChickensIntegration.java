package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.setycz.chickens.registry.ChickensRegistry;

final class ChickensIntegration extends AbstractIntegrationThread
{
	ChickensIntegration()
	{
		super("Chickens");
	}

	@Override
	public String call()
	{
		try {
			ChickensRegistry.getItems().forEach(chickensRegistryItem -> {
				chickensRegistryItem.setLayItem(resourceHandler.getMainItemStack(chickensRegistryItem.createLayItem()));
				chickensRegistryItem.setDropItem(resourceHandler.getMainItemStack(chickensRegistryItem.createDropItem()));
			});
		} catch (Exception e) {
			logger.error(threadName + e);
			e.printStackTrace();
		}
		return threadName + "changed Chicken's DNA to make the eggs give the right things when hatched.";
	}
}
