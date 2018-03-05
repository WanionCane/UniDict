package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import elucent.gadgetry.core.recipe.AlloyRecipe;

final class GadgetryCoreIntegration extends AbstractIntegrationThread
{
	GadgetryCoreIntegration()
	{
		super("Gadgetry Core");
	}

	@Override
	public String call()
	{
		try {
			AlloyRecipe.recipes.replaceAll(alloyRecipe -> new AlloyRecipe(resourceHandler.getMainItemStack(alloyRecipe.getOutput()), alloyRecipe.inputs.toArray()));
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "first we fix the core;";
	}
}