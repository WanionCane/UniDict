package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import tbsc.techy.recipe.CrusherRecipes;
import wanion.unidict.UniDict;

import java.util.Map;

final class TechyIntegration extends AbstractIntegrationThread
{
    TechyIntegration()
    {
        super("Techy");
    }

    @Override
    public String call()
    {
        try {
            fixCrusherRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "What? something was wrong with Techy Crusher? =O";
    }

    private void fixCrusherRecipes()
    {
        final Map<ImmutableTriple<ItemStack, ItemStack, Integer>, Float> experienceMap = CrusherRecipes.instance().getExperienceMap();
        final Map<ImmutableTriple<ItemStack, ItemStack, Integer>, Integer> energyMap = CrusherRecipes.instance().getEnergyMap();
        CrusherRecipes.instance().getRecipeMap().entrySet().forEach(recipe -> {
            final ImmutableTriple<ItemStack, ItemStack, Integer> tripleOutput = recipe.getValue();
            final Float experiencePerRecipe = experienceMap.remove(tripleOutput);
            final Integer energyRequired = energyMap.remove(tripleOutput);
            final ImmutableTriple<ItemStack, ItemStack, Integer> newOutput = ImmutableTriple.of(resourceHandler.getMainItemStack(tripleOutput.getLeft()), resourceHandler.getMainItemStack(tripleOutput.getMiddle()), tripleOutput.right);
            experienceMap.put(newOutput, experiencePerRecipe);
            energyMap.put(newOutput, energyRequired);
            recipe.setValue(newOutput);
        });
    }
}