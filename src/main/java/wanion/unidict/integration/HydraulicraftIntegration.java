package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import k4unl.minecraft.Hydraulicraft.api.recipes.FluidShapelessOreRecipe;
import k4unl.minecraft.Hydraulicraft.api.recipes.IFluidRecipe;
import k4unl.minecraft.Hydraulicraft.lib.recipes.HydraulicRecipes;
import net.minecraft.item.ItemStack;
import wanion.unidict.UniDict;
import wanion.unidict.UniOreDictionary;
import wanion.unidict.common.FixedSizeList;
import wanion.unidict.helper.LogHelper;
import wanion.unidict.helper.ResourceHelper;
import wanion.unidict.resource.Resource;

import java.util.Iterator;
import java.util.List;

final class HydraulicraftIntegration extends AbstractIntegrationThread
{
    private final ResourceHelper resourceHelper = UniDict.getDependencies().get(ResourceHelper.class);
    private final UniOreDictionary uniOreDictionary = UniDict.getDependencies().get(UniOreDictionary.class);
    private final int ore = Resource.getKindOfName("ore");
    private final int chunk = Resource.getKindOfName("chunk");
    private final int dust = Resource.getKindOfName("dust");

    HydraulicraftIntegration()
    {
        super("Hydraulicraft");
        try {
            uniOreDictionary.prepare();
            if (ore > 0)
                resourceHelper.prepare(ore);
            if (chunk > 0)
                resourceHelper.prepare(chunk);
            if (dust > 0)
                resourceHelper.prepare(dust);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public String call()
    {
        try {
            fixHydrulicraftRecipes(HydraulicRecipes.getCrusherRecipes(), false);
            fixHydrulicraftRecipes(HydraulicRecipes.getWasherRecipes(), true);
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "All this pressure...";
    }

    private void fixHydrulicraftRecipes(List<IFluidRecipe> hydrauliCraftRecipeList, boolean onlyOutputItemStack)
    {
        List<IFluidRecipe> newRecipes = new FixedSizeList<>(hydrauliCraftRecipeList.size());
        for (Iterator<IFluidRecipe> fluidRecipeIterator = hydrauliCraftRecipeList.iterator(); fluidRecipeIterator.hasNext(); )
        {
            IFluidRecipe fluidRecipe = fluidRecipeIterator.next();
            if (!(fluidRecipe instanceof FluidShapelessOreRecipe))
                continue;
            ItemStack correctOutput = resourceHandler.getMainItemStack(fluidRecipe.getRecipeOutput());
            Object[] inputArray = fluidRecipe.getInputItems();
            Object input = inputArray[0];
            int inputKind = resourceHelper.get(input);
            int outputKind = resourceHelper.get(correctOutput);
            if (inputKind == ore && outputKind == chunk)
                correctOutput.stackSize = 1;
            else if (inputKind == chunk && outputKind == dust)
                correctOutput.stackSize = 2;
            if (!onlyOutputItemStack)
                newRecipes.add(new FluidShapelessOreRecipe(correctOutput, (input instanceof List) ? uniOreDictionary.getName(input) : (resourceHandler.exists((ItemStack) input) ? resourceHandler.getContainerName((ItemStack) input) : input)).setPressure(fluidRecipe.getPressure()).setCraftingTime(fluidRecipe.getCraftingTime()));
            else
                newRecipes.add(new FluidShapelessOreRecipe(correctOutput, (input instanceof List) ? resourceHandler.getMainItemStack((ItemStack) ((List) input).get(0)) : resourceHandler.getMainItemStack((ItemStack) input)).setPressure(fluidRecipe.getPressure()).setCraftingTime(fluidRecipe.getCraftingTime()));
            fluidRecipeIterator.remove();
        }
        hydrauliCraftRecipeList.addAll(newRecipes);
    }
}