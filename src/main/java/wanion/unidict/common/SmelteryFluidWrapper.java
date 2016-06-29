package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.text.WordUtils;
import tconstruct.library.crafting.Smeltery;
import wanion.unidict.UniOreDictionary;

import java.util.ArrayList;
import java.util.List;

public class SmelteryFluidWrapper
{
    public final String name;
    public final int pointOfFusion;
    public final Block block;
    public final int blockMeta;
    public final List<ItemStack> meltingList = new ArrayList<>();
    private final int amount;
    private Fluid fluid;

    public SmelteryFluidWrapper(String name, int amount)
    {
        this.name = name;
        Fluid fluid = FluidRegistry.getFluid(name + ".molten");
        if (fluid == null)
            fluid = FluidRegistry.getFluid(name);
        this.fluid = fluid;
        this.amount = amount;
        final ItemStack blockStack = UniOreDictionary.getFirstEntry("block" + WordUtils.capitalize(this.name));
        if (blockStack != null) {
            this.block = (blockStack.getItem() instanceof ItemBlock) ? ((ItemBlock) blockStack.getItem()).field_150939_a : null;
            this.blockMeta = blockStack.getItemDamage();
            this.pointOfFusion = (int) Math.round(Smeltery.getLiquifyTemperature(this.block, this.blockMeta) * 0.60);
        } else {
            this.block = null;
            this.blockMeta = 0;
            this.pointOfFusion = 0;
        }
    }

    public void setFluid(String fluidName)
    {
        final Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid != null)
            this.fluid = fluid;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    public void setFluid(Fluid fluid)
    {
        this.fluid = fluid;
    }

    public boolean valid()
    {
        return fluid != null && block != null && !meltingList.isEmpty();
    }

    public FluidStack getFluidStack()
    {
        return new FluidStack(fluid, amount);
    }
}