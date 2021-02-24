package wanion.unidict.integration;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.lang.reflect.Field;
import java.util.List;

final class VillagerIntegration extends AbstractIntegrationThread {
    private Field careerList;
    private Field tradeList;

    // Immersive Engineering
    private Class<?> emeraldForItemstack;
    private Field buyingItem;
    private Class<?> itemstackForEmerald;
    private Field sellingItem;

    // RailCraft
    private Class<?> genericTrade;
    private Field sale;
    private Field offers;
    private Field offerObj;

    public VillagerIntegration() {
        super("Villagers");
        try {
            careerList = VillagerRegistry.VillagerProfession.class.getDeclaredField("careers");
            careerList.setAccessible(true);
            tradeList = VillagerRegistry.VillagerCareer.class.getDeclaredField("trades");
            tradeList.setAccessible(true);
            prepareReflectionFromMods();
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            logger.error("Couldn't load villager fields/classes!");
            e.printStackTrace();
        }
    }

    @Override
    public String call() {
        try {
            if (careerList != null && tradeList != null)
                fixTradeLists();
        }
        catch (Exception e) {
            logger.error(threadName + e);
            e.printStackTrace();
        }
        return threadName + "Villagers now offer even better deals.";
    }

    @SuppressWarnings("unchecked")
    private void fixTradeLists() {
        for (VillagerRegistry.VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
            try {
                List<VillagerRegistry.VillagerCareer> careers = (List<VillagerRegistry.VillagerCareer>)careerList.get(profession);
                for (VillagerRegistry.VillagerCareer career : careers) {
                    List<List<EntityVillager.ITradeList>> trades = (List<List<EntityVillager.ITradeList>>)tradeList.get(career);
                    trades.forEach(tradesForLevel -> tradesForLevel.forEach(this::fixTradeList));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void fixTradeList(EntityVillager.ITradeList list) {
        if (list instanceof EntityVillager.ItemAndEmeraldToItem) {
            EntityVillager.ItemAndEmeraldToItem itemAndEmeraldToItem = (EntityVillager.ItemAndEmeraldToItem)list;
            itemAndEmeraldToItem.buyingItemStack =
                    resourceHandler.getMainItemStack(itemAndEmeraldToItem.buyingItemStack);
            itemAndEmeraldToItem.sellingItemstack =
                    resourceHandler.getMainItemStack(itemAndEmeraldToItem.sellingItemstack);
        }
        else if (list instanceof EntityVillager.ListItemForEmeralds) {
            EntityVillager.ListItemForEmeralds listItemForEmeralds = (EntityVillager.ListItemForEmeralds)list;
            listItemForEmeralds.itemToBuy = resourceHandler.getMainItemStack(listItemForEmeralds.itemToBuy);
        }
        else if (emeraldForItemstack != null && emeraldForItemstack.isInstance(list)) {
            try {
                buyingItem.set(list, resourceHandler.getMainItemStack((ItemStack)buyingItem.get(list)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else if (itemstackForEmerald != null && itemstackForEmerald.isInstance(list)) {
            try {
                sellingItem.set(list, resourceHandler.getMainItemStack((ItemStack)sellingItem.get(list)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else if (genericTrade != null && genericTrade.isInstance(list)) {
            try {
                Object saleOffer = sale.get(list);
                offerObj.set(saleOffer,
                        resourceHandler.getMainItemStack(getRailCraftOfferItem(offerObj.get(saleOffer))));

                Object[] sellOffers = (Object[])offers.get(list);
                for (Object sellOffer : sellOffers) {
                    offerObj.set(sellOffer,
                            resourceHandler.getMainItemStack(getRailCraftOfferItem(offerObj.get(sellOffer))));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareReflectionFromMods() throws NoSuchFieldException, ClassNotFoundException {
        if (Loader.isModLoaded("immersiveengineering")){
            emeraldForItemstack = Class.forName("blusunrize.immersiveengineering.common.util.IEVillagerHandler$EmeraldForItemstack");
            buyingItem = emeraldForItemstack.getField("buyingItem");
            buyingItem.setAccessible(true);
            itemstackForEmerald = Class.forName("blusunrize.immersiveengineering.common.util.IEVillagerHandler$ItemstackForEmerald");
            sellingItem = itemstackForEmerald.getField("sellingItem");
            sellingItem.setAccessible(true);
        }

        if (Loader.isModLoaded("railcraft")) {
            genericTrade = Class.forName("mods.railcraft.common.worldgen.VillagerTrades$GenericTrade");
            (sale = genericTrade.getDeclaredField("sale")).setAccessible(true);
            (offers = genericTrade.getDeclaredField("offers")).setAccessible(true);
            (offerObj = Class.forName("mods.railcraft.common.worldgen.VillagerTrades$Offer").getDeclaredField("obj")).setAccessible(true);
        }
    }

    private ItemStack getRailCraftOfferItem(Object obj) {
        if (obj instanceof RailcraftItems) {
            return ((RailcraftItems)obj).getStack();
        } else if (obj instanceof ItemStack) {
            return (ItemStack)obj;
        } else if (obj instanceof Item) {
            return new ItemStack((Item)obj);
        } else if (obj instanceof Block) {
            return new ItemStack((Block)obj);
        }
        return ItemStack.EMPTY;
    }
}
