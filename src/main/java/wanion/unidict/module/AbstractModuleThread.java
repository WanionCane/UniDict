package wanion.unidict.module;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import java.util.concurrent.Callable;

public abstract class AbstractModuleThread implements Callable<String>
{
	protected final String threadName;

	public AbstractModuleThread(String threadName, String moduleName)
	{
		this.threadName = threadName + " " + moduleName + ": ";
	}
}