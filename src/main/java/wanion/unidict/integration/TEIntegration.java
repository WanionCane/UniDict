package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import net.minecraft.item.ItemStack;
import wanion.unidict.LoadStage;
import wanion.unidict.UniDict;
import wanion.unidict.common.Util;
import wanion.unidict.module.SpecifiedLoadStage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@SpecifiedLoadStage(stage = LoadStage.LOAD_COMPLETE)
final class TEIntegration extends AbstractIntegrationThread
{
    TEIntegration()
    {
        super("Thermal Expansion");
    }

    @Override
    public String call()
    {
        try {
            fixRedstoneFurnaceRecipes();
            fixPulverizerRecipes();
            fixInductionSmelterRecipes();
        } catch (Exception e) { UniDict.getLogger().error(threadName + e); }
        return threadName + "The world seems to be more thermally involved.";
    }

    private void fixRedstoneFurnaceRecipes()
    {
        final Map<FurnaceManager.ComparableItemStackFurnace, FurnaceManager.RecipeFurnace> recipeMap = Util.getField(FurnaceManager.class, "recipeMap", null, Map.class);
        if (recipeMap == null)
            return;
        Constructor<FurnaceManager.RecipeFurnace> redstoneFurnaceRecipeConstructor = null;
        try {
            redstoneFurnaceRecipeConstructor = FurnaceManager.RecipeFurnace.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, int.class);
            redstoneFurnaceRecipeConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) { e.printStackTrace(); }
        if (redstoneFurnaceRecipeConstructor == null)
            return;
        for (FurnaceManager.ComparableItemStackFurnace recipeMapKey : recipeMap.keySet()) {
            final FurnaceManager.RecipeFurnace redstoneFurnaceRecipe = recipeMap.get(recipeMapKey);
            final ItemStack correctOutput = resourceHandler.getMainItemStack(redstoneFurnaceRecipe.getOutput());
            if (correctOutput == redstoneFurnaceRecipe.getOutput())
                continue;
            try {
                recipeMap.put(recipeMapKey, redstoneFurnaceRecipeConstructor.newInstance(redstoneFurnaceRecipe.getInput(), correctOutput, redstoneFurnaceRecipe.getEnergy()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void fixPulverizerRecipes()
    {
        final Map<PulverizerManager.ComparableItemStackPulverizer, PulverizerManager.RecipePulverizer> recipeMap = Util.getField(PulverizerManager.class, "recipeMap", null, Map.class);
        if (recipeMap == null)
            return;
        Constructor<PulverizerManager.RecipePulverizer> pulverizerRecipeConstructor = null;
        try {
            pulverizerRecipeConstructor = PulverizerManager.RecipePulverizer.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, int.class, int.class);
            pulverizerRecipeConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) { e.printStackTrace(); }
        if (pulverizerRecipeConstructor == null)
            return;
        for (PulverizerManager.ComparableItemStackPulverizer recipeMapKey : recipeMap.keySet()) {
            final PulverizerManager.RecipePulverizer pulverizerRecipe = recipeMap.get(recipeMapKey);
            final ItemStack correctOutput = resourceHandler.getMainItemStack(pulverizerRecipe.getPrimaryOutput());
            final ItemStack correctSecondaryOutput = resourceHandler.getMainItemStack(pulverizerRecipe.getSecondaryOutput());
            if (correctOutput == pulverizerRecipe.getPrimaryOutput() && correctSecondaryOutput == pulverizerRecipe.getSecondaryOutput())
                continue;
            try {
                recipeMap.put(recipeMapKey, pulverizerRecipeConstructor.newInstance(pulverizerRecipe.getInput(), correctOutput, correctSecondaryOutput, pulverizerRecipe.getSecondaryOutputChance(), pulverizerRecipe.getEnergy()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void fixInductionSmelterRecipes()
    {
        final Map<List<SmelterManager.ComparableItemStackSmelter>, SmelterManager.RecipeSmelter> recipeMap = Util.getField(SmelterManager.class, "recipeMap", null, Map.class);
        if (recipeMap == null)
            return;
        Constructor<SmelterManager.RecipeSmelter> smelterRecipeConstructor = null;
        try {
            smelterRecipeConstructor = SmelterManager.RecipeSmelter.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class, int.class);
            smelterRecipeConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) { e.printStackTrace(); }
        if (smelterRecipeConstructor == null)
            return;
        for (final List<SmelterManager.ComparableItemStackSmelter> recipeMapKey : recipeMap.keySet()) {
            final SmelterManager.RecipeSmelter smelterRecipe = recipeMap.get(recipeMapKey);
            final ItemStack correctOutput = resourceHandler.getMainItemStack(smelterRecipe.getPrimaryOutput());
            final ItemStack correctSecondaryOutput = resourceHandler.getMainItemStack(smelterRecipe.getSecondaryOutput());
            if (correctOutput == smelterRecipe.getPrimaryOutput() && correctSecondaryOutput == smelterRecipe.getSecondaryOutput())
                continue;
            try {
                recipeMap.put(recipeMapKey, smelterRecipeConstructor.newInstance(smelterRecipe.getPrimaryInput(), smelterRecipe.getSecondaryInput(), correctOutput, correctSecondaryOutput, smelterRecipe.getSecondaryOutputChance(), smelterRecipe.getEnergy()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}