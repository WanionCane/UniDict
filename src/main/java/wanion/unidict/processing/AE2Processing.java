package wanion.unidict.processing;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

final class AE2Processing extends AbstractProcessingThread
{
	AE2Processing()
	{
		super("Applied Energistics 2");
	}

	@Override
	public String call()
	{
		return threadName + "The world of energistics is now even more powerful.";
	}
}