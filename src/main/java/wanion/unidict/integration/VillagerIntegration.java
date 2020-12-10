package wanion.unidict.integration;

/*
 * Created by ElektroKill(https://github.com/ElektroKill).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import net.minecraft.entity.passive.EntityVillager;
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
        if (careerList != null && tradeList != null)
            fixTradeLists();
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
        else if (list instanceof EntityVillager.EmeraldForItems) {
            EntityVillager.EmeraldForItems emeraldForItems = (EntityVillager.EmeraldForItems)list;
            emeraldForItems.buyingItem =
                    resourceHandler.getMainItemStack(new ItemStack(emeraldForItems.buyingItem)).getItem();
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
    }
}