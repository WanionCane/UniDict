package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.gson.stream.JsonReader;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.item.ItemStack;
import wanion.unidict.Config;
import wanion.unidict.UniDict;
import wanion.unidict.resource.Resource;
import wanion.unidict.resource.ResourceHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;

import static wanion.lib.common.Util.getModName;
import static wanion.unidict.common.Reference.MOD_ID;
import static wanion.unidict.common.Reference.SLASH;

public final class SpecificKindItemStackComparator implements Comparator<ItemStack>
{
	public static TIntObjectMap<SpecificKindItemStackComparator> kindSpecificComparators = new TIntObjectHashMap<>();
	private static boolean initialized;
	private final TObjectIntMap<String> ownerOfKind;

	private SpecificKindItemStackComparator(@Nonnull final TObjectIntMap<String> ownerOfKind)
	{
		this.ownerOfKind = ownerOfKind;
	}

	public static synchronized SpecificKindItemStackComparator getComparatorFor(final int kind)
	{
		if (!initialized)
			init();
		return kindSpecificComparators.get(kind);
	}

	public static synchronized boolean hasComparatorForKind(final int kind)
	{
		if (!initialized)
			init();
		return kindSpecificComparators.containsKey(kind);
	}

	private static void init()
	{
		final File jsonFile = new File("." + SLASH + "config" + SLASH + MOD_ID + SLASH + "specificKindSorting.json");
		try {
			if (!jsonFile.exists() && !jsonFile.createNewFile()) {
				UniDict.getLogger().error("UniDict couldn't create the specificKindSorting.json file.");
				return;
			}
			try (final JsonReader jsonReader = new JsonReader(new FileReader(jsonFile))) {
				jsonReader.beginArray();
				while (jsonReader.hasNext()) {
					jsonReader.beginObject();
					if (!jsonReader.nextName().equals("kindName")) {
						jsonReader.skipValue();
						jsonReader.endObject();
						jsonReader.endArray();
						continue;
					}
					final String kindName = jsonReader.nextString();
					if (!jsonReader.nextName().equals("modIdPriorityList")) {
						jsonReader.skipValue();
						jsonReader.endObject();
						jsonReader.endArray();
						continue;
					}
					jsonReader.beginArray();
					final TObjectIntMap<String> ownerOfKind = new TObjectIntHashMap<>(10, 1, Integer.MAX_VALUE);
					int i = 0;
					while (jsonReader.hasNext())
						ownerOfKind.put(jsonReader.nextString(), i++);
					if (Resource.kindExists(kindName))
						kindSpecificComparators.put(Resource.getKindOfName(kindName), new SpecificKindItemStackComparator(ownerOfKind));
					jsonReader.endArray();
					jsonReader.endObject();
				}
			}
		} catch (Throwable e) {
			UniDict.getLogger().error("UniDict couldn't read the specificKindSorting.json file.");
			e.printStackTrace();
		}
		initialized = true;
	}

	@Override
	public int compare(@Nonnull final ItemStack itemStack1, @Nonnull final ItemStack itemStack2)
	{
		final String stack1ModName = getModName(itemStack1);
		final Config config = UniDict.getConfig();
		if (config.keepOneEntry && ((!config.keepOneEntryBlackListsAsWhiteLists && config.keepOneEntryModBlackSet.contains(stack1ModName)) || (config.keepOneEntryBlackListsAsWhiteLists && !config.keepOneEntryModBlackSet.contains(stack1ModName))))
			ResourceHandler.addToKeepOneEntryModBlackSet(itemStack1);
		return getIndex(stack1ModName) < getIndex(itemStack2) ? -1 : 0;
	}

	private long getIndex(final ItemStack itemStack)
	{
		return ownerOfKind.get(getModName(itemStack));
	}

	private long getIndex(final String modName)
	{
		return ownerOfKind.get(modName);
	}
}