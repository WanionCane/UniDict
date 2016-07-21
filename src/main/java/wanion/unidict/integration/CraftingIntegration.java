package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.collect.Lists;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.crafting.IRecipe;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.recipe.ForgeRecipeResearcher;
import wanion.unidict.recipe.IC2RecipeResearcher;
import wanion.unidict.recipe.IRecipeResearcher;
import wanion.unidict.recipe.VanillaRecipeResearcher;
import wanion.unidict.resource.UniResourceContainer;

import java.util.*;

final class CraftingIntegration extends AbstractIntegrationThread
{
    private final UniOreDictionary uniOreDictionary = UniDict.getDependencies().get(UniOreDictionary.class);
    private final List<IRecipe> recipes = RecipeHelper.recipes;
    private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapedResearcherMap = new IdentityHashMap<>();
    private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapelessResearcherMap = new IdentityHashMap<>();

    CraftingIntegration()
    {
        super("Crafting");
        final List<IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> researcherList = new ArrayList<>();
        researcherList.add(new VanillaRecipeResearcher());
        researcherList.add(new ForgeRecipeResearcher());
        if (Config.ic2)
            researcherList.add(new IC2RecipeResearcher());
        researcherList.forEach(researcher -> {
            researcher.getShapedRecipeClasses().forEach(shapedRecipeClass -> shapedResearcherMap.put(shapedRecipeClass, researcher));
            researcher.getShapelessRecipeClasses().forEach(shapelessRecipeClass -> shapelessResearcherMap.put(shapelessRecipeClass, researcher));
        });
    }

    @Override
    public String call()
    {
        try {
            doTheResearch();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); e.printStackTrace(); }
        return threadName + "Why so many recipes? I had to deal with a lot of recipes.";
    }

    private void doTheResearch()
    {
        final Map<UniResourceContainer, TIntObjectMap<List<IRecipe>>> smartRecipeMap = new THashMap<>();
        IRecipe bufferRecipe;
        UniResourceContainer bufferContainer;
        for (final Iterator<IRecipe> recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
            boolean isShapeless = false;
            if ((bufferRecipe = recipeIterator.next()) == null || (bufferContainer = resourceHandler.getContainer(bufferRecipe.getRecipeOutput())) == null || !(shapedResearcherMap.containsKey(bufferRecipe.getClass()) || (isShapeless = shapelessResearcherMap.containsKey(bufferRecipe.getClass()))))
                continue;
            final int recipeKey = !isShapeless ? shapedResearcherMap.get(bufferRecipe.getClass()).getShapedRecipeKey(bufferRecipe, resourceHandler) : shapelessResearcherMap.get(bufferRecipe.getClass()).getShapelessRecipeKey(bufferRecipe, resourceHandler);
            if (recipeKey != 0 && bufferRecipe.getRecipeSize() > 0) {
                final TIntObjectMap<List<IRecipe>> evenSmarterRecipeMap;
                if (!smartRecipeMap.containsKey(bufferContainer))
                    smartRecipeMap.put(bufferContainer, evenSmarterRecipeMap = new TIntObjectHashMap<>());
                else evenSmarterRecipeMap = smartRecipeMap.get(bufferContainer);
                if (!evenSmarterRecipeMap.containsKey(recipeKey))
                    evenSmarterRecipeMap.put(recipeKey, Lists.newArrayList(bufferRecipe));
                else evenSmarterRecipeMap.get(recipeKey).add(bufferRecipe);
            }
            recipeIterator.remove();
        }
        smartRecipeMap.forEach((container, evenSmartRecipeMap) -> evenSmartRecipeMap.forEachValue(recipeList -> {
                    final IRecipe recipe = recipeList.get(0);
                    final boolean isShapeless = shapelessResearcherMap.containsKey(recipe.getClass());
                    final IRecipeResearcher<? extends IRecipe, ? extends IRecipe> recipeResearcher = !isShapeless ? shapedResearcherMap.get(recipe.getClass()) : shapelessResearcherMap.get(recipe.getClass());
                    if (recipe.getRecipeSize() == 9)
                        recipes.add(isShapeless ? recipeResearcher.getNewShapedFromShapelessRecipe(recipe, resourceHandler, uniOreDictionary) : recipeResearcher.getNewShapedRecipe(recipe, resourceHandler, uniOreDictionary));
                    else if (recipe.getRecipeSize() == 1)
                        recipes.add(isShapeless ? recipeResearcher.getNewShapelessRecipe(recipe, resourceHandler, uniOreDictionary) : recipeResearcher.getNewShapelessFromShapedRecipe(recipe, resourceHandler, uniOreDictionary));
                    else
                        recipes.add(isShapeless ? recipeResearcher.getNewShapelessRecipe(recipe, resourceHandler, uniOreDictionary) : recipeResearcher.getNewShapedRecipe(recipe, resourceHandler, uniOreDictionary));
                    return true;
                })
        );
    }
}