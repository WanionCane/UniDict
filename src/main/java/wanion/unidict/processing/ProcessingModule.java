package wanion.unidict.processing;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import wanion.lib.module.AbstractModule;

public final class ProcessingModule extends AbstractModule
{
	public ProcessingModule()
	{
		super("Processing", Class::newInstance);
	}

	@Override
	protected void init()
	{

	}
}