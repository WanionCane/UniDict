package wanion.unidict.recipe;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.crafting.IRecipe;
import wanion.lib.recipe.IRecipeResearcher;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.resource.ResourceHandler;

abstract class AbstractRecipeResearcher<S extends IRecipe, L extends IRecipe> implements IRecipeResearcher<S, L>
{
	protected final ResourceHandler resourceHandler = UniDict.getResourceHandler();
	protected final UniOreDictionary uniOreDictionary = UniDict.getUniOreDictionary();
	protected final boolean itemStacksOnly = UniDict.getConfig().registerTheIngredientsOfTheNewRecipesAsItemStacks;
}