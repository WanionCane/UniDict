package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import com.google.common.collect.Lists;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import wanion.unidict.Config;
import wanion.unidict.helper.RecipeHelper;
import wanion.unidict.recipe.ForgeResearcher;
import wanion.unidict.recipe.IC2Researcher;
import wanion.unidict.recipe.IRecipeResearcher;
import wanion.unidict.recipe.VanillaResearcher;
import wanion.unidict.resource.UniResourceContainer;

import javax.annotation.Nonnull;
import java.util.*;

final class CraftingIntegration extends AbstractIntegrationThread
{
    private final List<IRecipe> recipes = RecipeHelper.recipes;
    private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapedResearcherMap = new THashMap<>();
    private final Map<Class<? extends IRecipe>, IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> shapelessResearcherMap = new THashMap<>();

    CraftingIntegration()
    {
        super("Crafting");
        final List<IRecipeResearcher<? extends IRecipe, ? extends IRecipe>> researcherList = new ArrayList<>();
        researcherList.add(new VanillaResearcher());
        researcherList.add(new ForgeResearcher());
        if (Config.ic2)
            researcherList.add(new IC2Researcher());
        researcherList.forEach(researcher -> {
            shapedResearcherMap.put(researcher.getShapedRecipeClass(), researcher);
            shapelessResearcherMap.put(researcher.getShapelessRecipeClass(), researcher);
        });
    }

    @Override
    public String call()
    {
        experimentalRecipeResearcher();
        return threadName + "Why so many recipes? I had to deal with a lot of recipes.";
    }

    private void experimentalRecipeResearcher()
    {
        final Map<UniResourceContainer, TIntObjectMap<List<IRecipe>>> smartRecipeMap = new THashMap<>();
        IRecipe bufferRecipe;
        UniResourceContainer bufferContainer;
        for (final Iterator<IRecipe> recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
            boolean isShapeless = false;
            if ((bufferRecipe = recipeIterator.next()) == null || (bufferContainer = resourceHandler.getContainer(bufferRecipe.getRecipeOutput())) == null || !(shapedResearcherMap.containsKey(bufferRecipe.getClass()) || (isShapeless = shapelessResearcherMap.containsKey(bufferRecipe.getClass()))))
                continue;
            final int recipeKey = !isShapeless ? shapedResearcherMap.get(bufferRecipe.getClass()).getShapedRecipeKey(bufferRecipe, resourceHandler) : shapelessResearcherMap.get(bufferRecipe.getClass()).getShapelessRecipeKey(bufferRecipe, resourceHandler);
            final TIntObjectMap<List<IRecipe>> evenSmarterRecipeMap;
            if (!smartRecipeMap.containsKey(bufferContainer))
                smartRecipeMap.put(bufferContainer, evenSmarterRecipeMap = new TIntObjectHashMap<>());
            else evenSmarterRecipeMap = smartRecipeMap.get(bufferContainer);
            if (!evenSmarterRecipeMap.containsKey(recipeKey))
                evenSmarterRecipeMap.put(recipeKey, Lists.newArrayList(bufferRecipe));
            else evenSmarterRecipeMap.get(recipeKey).add(bufferRecipe);
            recipeIterator.remove();
        }
        smartRecipeMap.forEach((container, evenSmartRecipeMap) -> {
            final Comparator<IRecipe> recipeComparator = new RecipeComparator(container.getComparator());
            evenSmartRecipeMap.forEachValue(recipeList -> {
                recipeList.sort(recipeComparator);
                final IRecipe recipe = recipeList.get(0);
                final boolean isShapeless = shapelessResearcherMap.containsKey(recipe.getClass());
                final IRecipeResearcher<? extends IRecipe, ? extends IRecipe> recipeResearcher = !isShapeless ? shapedResearcherMap.get(recipe.getClass()) : shapelessResearcherMap.get(recipe.getClass());
                if (recipe.getRecipeSize() == 9)
                    if (isShapeless)
                        recipes.add(recipeResearcher.getNewShapedFromShapelessRecipe(recipe, resourceHandler));
                    else
                        recipes.add(recipeResearcher.getNewShapedRecipe(recipe, resourceHandler));
                else if (recipe.getRecipeSize() == 1)
                    if (!isShapeless)
                        recipes.add(recipeResearcher.getNewShapelessFromShapedRecipe(recipe, resourceHandler));
                    else
                        recipes.add(recipeResearcher.getNewShapelessRecipe(recipe, resourceHandler));
                else if (!isShapeless)
                    recipes.add(recipeResearcher.getNewShapedRecipe(recipe, resourceHandler));
                else
                    recipes.add(recipeResearcher.getNewShapelessRecipe(recipe, resourceHandler));
                return true;
            });
        });
    }

    private static final class RecipeComparator implements Comparator<IRecipe>
    {
        private final Comparator<ItemStack> itemStackComparator;

        private RecipeComparator(@Nonnull final Comparator<ItemStack> itemStackComparator)
        {
            this.itemStackComparator = itemStackComparator;
        }

        @Override
        public int compare(IRecipe recipe1, IRecipe recipe2)
        {
            return itemStackComparator.compare(recipe1.getRecipeOutput(), recipe2.getRecipeOutput());
        }
    }
}