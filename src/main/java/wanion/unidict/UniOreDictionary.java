package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import wanion.unidict.UniDict.IDependence;
import wanion.unidict.common.Util;

import javax.annotation.RegEx;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class UniOreDictionary implements IDependence
{
    private static Map<String, Integer> nameToId = Util.getField(OreDictionary.class, "nameToId", null, Map.class);
    private static List<String> idToName = Util.getField(OreDictionary.class, "idToName", null, List.class);
    private static List<List<ItemStack>> idToStack = Util.getField(OreDictionary.class, "idToStack", null, List.class);
    private static List<List<ItemStack>> idToStackUn = Util.getField(OreDictionary.class, "idToStackUn", null, List.class);
    private static Map<Integer, List<Integer>> stackToId = Util.getField(OreDictionary.class, "stackToId", null, Map.class);

    private final Map<Object, String> someThingToName = new HashMap<>();

    public static List<ItemStack> get(final String oreDictName)
    {
        return get(nameToId.get(oreDictName));
    }

    public static List<ItemStack> get(final Integer oreDictId)
    {
        return checkId(oreDictId) ? idToStack.get(oreDictId) : null;
    }

    public static List<ItemStack> getUn(final Integer oreDictId)
    {
        return checkId(oreDictId) ? idToStackUn.get(oreDictId) : null;
    }

    public static ItemStack getFirstEntry(final String oreDictName)
    {
        final List<ItemStack> oreList = get(oreDictName);
        return (oreList != null && !oreDictName.isEmpty()) ? oreList.get(0).copy() : null;
    }

    public static ItemStack getLastEntry(final String oreDictName)
    {
        final List<ItemStack> oreList = get(oreDictName);
        return (oreList != null && !oreDictName.isEmpty()) ? oreList.get(oreList.size() - 1).copy() : null;
    }

    public static Set<ItemStack> get(final Collection<String> oreDictNames)
    {
        final Set<ItemStack> itemStacks = new HashSet<>();
        for (String name : oreDictNames)
            itemStacks.addAll(idToStack.get(nameToId.get(name)));
        return itemStacks;
    }

    public static List<ItemStack> get(final ItemStack thing)
    {
        final int thingId = MetaItem.get(thing);
        if (stackToId.containsKey(thingId))
            return idToStack.get(stackToId.get(thingId).get(0));
        return null;
    }

    public static List<Matcher> getThoseThatMatches(@RegEx final String regex)
    {
        return getThoseThatMatches(Pattern.compile(regex));
    }

    public static List<Matcher> getThoseThatMatches(final Pattern pattern)
    {
        final List<Matcher> matcherList = new ArrayList<>();
        for (String name : nameToId.keySet()) {
            Matcher matcher = pattern.matcher(name);
            if (matcher.find())
                matcherList.add(matcher);
        }
        return matcherList;
    }

    public static void removeFromElsewhere(final String oreDictName)
    {
        final ItemStack mainEntry = getFirstEntry(oreDictName);
        final int[] ids = OreDictionary.getOreIDs(mainEntry);
        if (mainEntry == null || ids.length == 0)
            return;
        final int mainEntryHash = MetaItem.get(mainEntry);
        final int oreDictId = getId(oreDictName);
        for (int id : ids) {
            if (oreDictId == id)
                continue;
            final List<ItemStack> oreDictEntries = get(id);
            if (oreDictEntries == null)
                continue;
            for (Iterator<ItemStack> oreDictEntriesIterator = oreDictEntries.iterator(); oreDictEntriesIterator.hasNext(); )
                if (mainEntryHash == MetaItem.get(oreDictEntriesIterator.next()))
                    oreDictEntriesIterator.remove();
        }
    }

    public static Integer getId(final String oreDictName)
    {
        return nameToId.get(oreDictName);
    }

    private static String getName(final Integer oreDictId)
    {
        return idToName.get(oreDictId);
    }

    private static boolean checkId(final Integer oreDictId)
    {
        return oreDictId != null && oreDictId <= idToStack.size();
    }

    public void prepare()
    {
        if (!someThingToName.isEmpty())
            return;
        for (String oreDictName : nameToId.keySet()) {
            final Integer oreDictId = nameToId.get(oreDictName);
            final List<ItemStack> entries = getUn(oreDictId);
            for (int hash : MetaItem.getArray(entries))
                if (!someThingToName.containsKey(hash))
                    someThingToName.put(hash, oreDictName);
            someThingToName.put(entries, oreDictName);
        }
    }

    public String getName(final Object thing)
    {
        if (thing instanceof ItemStack)
            return someThingToName.get(MetaItem.get((ItemStack) thing));
        else
            return someThingToName.get(thing);
    }
}