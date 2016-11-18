package wanion.unidict.processing;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import wanion.unidict.UniDict;
import wanion.unidict.resource.UniAttributes;

public final class ProcessingHandler implements UniDict.IDependency
{
	private final TIntObjectMap<UniAttributes> individualStackAttributes = new TIntObjectHashMap<>();

	private ProcessingHandler()
	{
		UniDict.getAPI().resources.parallelStream().forEach(resource -> resource.getChildrenCollection().forEach(uniResourceContainer -> {
			final UniAttributes uniAttributes = new UniAttributes(resource, uniResourceContainer);
			for (final int hash : uniResourceContainer.getHashes())
				individualStackAttributes.put(hash, uniAttributes);
		}));
	}
}