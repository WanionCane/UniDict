package wanion.unidict.processing;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.lib.module.AbstractModuleThread;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;

import javax.annotation.Nonnull;

abstract class AbstractProcessingThread extends AbstractModuleThread
{
	protected final UniDictAPI uniDictAPI = UniDict.getAPI();

	AbstractProcessingThread(@Nonnull final String processingName)
	{
		super(processingName, "Processing");
	}
}