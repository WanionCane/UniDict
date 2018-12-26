package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import net.minecraft.item.ItemStack;
import wanion.lib.common.Util;

import java.util.List;

final class IndustrialForegoingIntegration extends AbstractIntegrationThread
{
	IndustrialForegoingIntegration()
	{
		super("Industrial Foregoing");
	}

	@Override
	public String call()
	{
		try {
			for (final List<LaserDrillEntry> laserDrillEntryList : LaserDrillEntry.LASER_DRILL_ENTRIES)
				laserDrillEntryList.forEach(laserDrillEntry -> Util.setField(LaserDrillEntry.class, "stack", laserDrillEntry, resourceHandler.getMainItemStack(Util.getField(LaserDrillEntry.class, "stack", laserDrillEntry, ItemStack.class))));
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "enhanced Laser Drill focus.";
	}
}