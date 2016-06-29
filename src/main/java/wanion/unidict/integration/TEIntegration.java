package wanion.unidict.integration;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import wanion.unidict.Config;
import wanion.unidict.common.Util;
import wanion.unidict.helper.LogHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class TEIntegration extends AbstractIntegrationThread
{
    private final int thermalIndex = Config.ownerOfEveryThing.get("ThermalFoundation");
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
            if (Config.exNihilo && thermalIndex > 0)
                interceptIMC();
        } catch (Exception e) {
            LogHelper.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "The world seems to be more thermally involved.";
    }

    private void fixRedstoneFurnaceRecipes()
    {
        Map<FurnaceManager.ComparableItemStackFurnace, FurnaceManager.RecipeFurnace> recipeMap = Util.getField(FurnaceManager.class, "recipeMap", null, Map.class);
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
            FurnaceManager.RecipeFurnace redstoneFurnaceRecipe = recipeMap.get(recipeMapKey);
            ItemStack correctOutput = resourceHandler.getMainItemStack(redstoneFurnaceRecipe.getOutput());
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
        Map<PulverizerManager.ComparableItemStackPulverizer, PulverizerManager.RecipePulverizer> recipeMap = Util.getField(PulverizerManager.class, "recipeMap", null, Map.class);
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
            PulverizerManager.RecipePulverizer pulverizerRecipe = recipeMap.get(recipeMapKey);
            ItemStack correctOutput = resourceHandler.getMainItemStack(pulverizerRecipe.getPrimaryOutput());
            ItemStack correctSecondaryOutput = resourceHandler.getMainItemStack(pulverizerRecipe.getSecondaryOutput());
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
        Map<List<SmelterManager.ComparableItemStackSmelter>, SmelterManager.RecipeSmelter> recipeMap = Util.getField(SmelterManager.class, "recipeMap", null, Map.class);
        if (recipeMap == null)
            return;
        Constructor<SmelterManager.RecipeSmelter> smelterRecipeConstructor = null;
        try {
            smelterRecipeConstructor = SmelterManager.RecipeSmelter.class.getDeclaredConstructor(ItemStack.class, ItemStack.class, ItemStack.class, ItemStack.class, int.class, int.class);
            smelterRecipeConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) { e.printStackTrace(); }
        if (smelterRecipeConstructor == null)
            return;
        for (List<SmelterManager.ComparableItemStackSmelter> recipeMapKey : recipeMap.keySet()) {
            SmelterManager.RecipeSmelter smelterRecipe = recipeMap.get(recipeMapKey);
            ItemStack correctOutput = resourceHandler.getMainItemStack(smelterRecipe.getPrimaryOutput());
            ItemStack correctSecondaryOutput = resourceHandler.getMainItemStack(smelterRecipe.getSecondaryOutput());
            if (correctOutput == smelterRecipe.getPrimaryOutput() && correctSecondaryOutput == smelterRecipe.getSecondaryOutput())
                continue;
                try {
                recipeMap.put(recipeMapKey, smelterRecipeConstructor.newInstance(smelterRecipe.getPrimaryInput(), smelterRecipe.getSecondaryInput(), correctOutput, correctSecondaryOutput, smelterRecipe.getSecondaryOutputChance(), smelterRecipe.getEnergy()));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void interceptIMC()
    {
        Multimap<String, FMLInterModComms.IMCMessage> allTheMessages = Util.getField(FMLInterModComms.class, "modMessages", FMLCommonHandler.instance(), Multimap.class);
        if (allTheMessages == null)
            return;
        Collection<FMLInterModComms.IMCMessage> imcMessages = allTheMessages.get(FMLCommonHandler.instance().findContainerFor(ThermalExpansion.instance).getModId());
        int exHash = "exnihilo".hashCode();
        for (FMLInterModComms.IMCMessage imcMessage : imcMessages) {
            if (!(imcMessage.getSender().hashCode() == exHash))
                continue;
            NBTTagCompound imcNBT = imcMessage.getNBTValue();
            ItemStack primaryOutput;
            if (imcNBT == null || (primaryOutput = resourceHandler.getMainItemStack(ItemStack.loadItemStackFromNBT(imcNBT.getCompoundTag("primaryOutput")))) == null)
                continue;
            imcNBT.setTag("primaryOutput", primaryOutput.writeToNBT(new NBTTagCompound()));
            if (!imcNBT.hasKey("secondaryOutput"))
                continue;
            ItemStack secondaryOutput = resourceHandler.getMainItemStack(ItemStack.loadItemStackFromNBT(imcNBT.getCompoundTag("secondaryOutput")));
            if (secondaryOutput != null)
                imcNBT.setTag("secondaryOutput", secondaryOutput.writeToNBT(new NBTTagCompound()));
        }
    }
}