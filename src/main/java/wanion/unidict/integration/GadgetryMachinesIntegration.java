package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import elucent.gadgetry.machines.recipe.GrindingRecipe;

final class GadgetryMachinesIntegration extends AbstractIntegrationThread
{
	GadgetryMachinesIntegration()
	{
		super("Gadgetry Machines");
	}

	@Override
	public String call()
	{
		try {
			GrindingRecipe.recipes.replaceAll(grindingRecipe -> new GrindingRecipe(resourceHandler.getMainItemStack(grindingRecipe.getOutput()), grindingRecipe.inputs.toArray()));
		} catch (Exception e) { logger.error(threadName + e); }
		return threadName + "then the machines! =D";
	}
}