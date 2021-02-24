package wanion.unidict.integration;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import mods.railcraft.api.crafting.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RailCraftIntegration extends AbstractIntegrationThread {
    private Class<?> metalRollingRecipe;
    private Field metalRollingTrueRecipe;

    private Class<?> rockCrusherOutputEntry;
    private Field rockCrusherOutputItem;

    public RailCraftIntegration() {
        super("Rail Craft");
        try {
            metalRollingRecipe = Class.forName("mods.railcraft.common.util.crafting.RollingMachineCrafter$RollingRecipe");
            metalRollingTrueRecipe = metalRollingRecipe.getDeclaredField("recipe");
            metalRollingTrueRecipe.setAccessible(true);
            rockCrusherOutputEntry = Class.forName("mods.railcraft.common.util.crafting.RockCrusherCrafter$OutputEntry");
            rockCrusherOutputItem = rockCrusherOutputEntry.getDeclaredField("output");
            rockCrusherOutputItem.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            logger.error("Could not find railcraft field!");
            e.printStackTrace();
        }
    }

    @Override
    public String call() {
        try {
            fixBlastFurnaceRecipes();
            if (metalRollingRecipe != null && metalRollingTrueRecipe != null) {
                try {
                    fixMetalRollingRecipes();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (rockCrusherOutputEntry != null && rockCrusherOutputItem != null)
                fixRockCrushingRecipes();
        }
        catch (Exception e) {
            logger.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The rails seem more viable.";
    }

    private void fixBlastFurnaceRecipes() {
        final IBlastFurnaceCrafter iBlastFurnaceCrafter = Crafters.blastFurnace();
        final Iterator<IBlastFurnaceCrafter.IRecipe> blastFurnaceRecipeIterator = iBlastFurnaceCrafter.getRecipes().iterator();
        final List<IBlastFurnaceCrafter.IBlastFurnaceRecipeBuilder> newRecipes = new ArrayList<>();
        while (blastFurnaceRecipeIterator.hasNext()) {
            final IBlastFurnaceCrafter.IRecipe recipe = blastFurnaceRecipeIterator.next();
            final ItemStack correctOutput = resourceHandler.getMainItemStack(recipe.getOutput());
            if (correctOutput == recipe.getOutput())
                continue;

            IBlastFurnaceCrafter.IBlastFurnaceRecipeBuilder res = iBlastFurnaceCrafter.newRecipe(recipe.getInput())
                    .name(recipe.getName())
                    .slagOutput(recipe.getSlagOutput())
                    .output(correctOutput)
                    .time(recipe.getTickTime(correctOutput));
            newRecipes.add(res);

            blastFurnaceRecipeIterator.remove();
        }

        newRecipes.forEach(IBlastFurnaceCrafter.IBlastFurnaceRecipeBuilder::register);
    }

    private void fixRockCrushingRecipes() {
        IRockCrusherCrafter iRockCrusherCrafter = Crafters.rockCrusher();
        for (IRockCrusherCrafter.IRecipe recipe : iRockCrusherCrafter.getRecipes()) {
            for (IOutputEntry output : recipe.getOutputs()) {
                if (!rockCrusherOutputEntry.isInstance(output))
                    continue;
                try {
                    rockCrusherOutputItem.set(output,
                            resourceHandler.getMainItemStack(((ItemStack) rockCrusherOutputItem.get(output))));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fixMetalRollingRecipes() throws IllegalAccessException {
        final IRollingMachineCrafter iRollingMachineCrafter = Crafters.rollingMachine();
        final List<IRollingMachineCrafter.IRollingRecipe> toRemove = new ArrayList<>();
        final List<IRollingMachineCrafter.IRollingRecipe> recipes =  iRollingMachineCrafter.getRecipes();
        for (IRollingMachineCrafter.IRollingRecipe recipe : new ArrayList<>(recipes)) {
            final ItemStack correctOutput = resourceHandler.getMainItemStack(recipe.getRecipeOutput());
            if (correctOutput == recipe.getRecipeOutput())
                continue;

            if (metalRollingRecipe.isInstance(recipe)) {
                final IRecipe trueRecipe = (IRecipe) metalRollingTrueRecipe.get(recipe);

                final IRollingMachineCrafter.IRollingMachineRecipeBuilder newRecipe = iRollingMachineCrafter.newRecipe(correctOutput)
                        .name(correctOutput.toString())
                        .time(recipe.getTickTime());

                if (trueRecipe instanceof ShapedRecipes) {
                    final ShapedRecipes shapedRecipe = (ShapedRecipes) trueRecipe;

                    newRecipe.recipe(new ShapedRecipes(shapedRecipe.getGroup(), shapedRecipe.recipeWidth,
                            shapedRecipe.recipeHeight, shapedRecipe.getIngredients(), correctOutput));
                }
                else if (trueRecipe instanceof ShapelessRecipes) {
                    final ShapelessRecipes shapelessRecipe = (ShapelessRecipes) trueRecipe;

                    newRecipe.recipe(new ShapelessRecipes(shapelessRecipe.getGroup(), correctOutput,
                            shapelessRecipe.getIngredients()));
                }

                toRemove.add(recipe);
            }
        }

        recipes.removeAll(toRemove);
    }
}
