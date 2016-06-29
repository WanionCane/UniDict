package wanion.unidict.helper;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import com.mark719.magicalcrops.config.ConfigCrafting;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import wanion.unidict.UniDict.IDependence;

public final class MagicalCropsHelper implements IDependence
{
    public final TObjectIntMap<String> magicalEssence = new TObjectIntHashMap<>();

    public MagicalCropsHelper()
    {
        magicalEssence.put("Iron", ConfigCrafting.outPutIron);
        magicalEssence.put("Gold", ConfigCrafting.outPutGold);
        magicalEssence.put("Aluminum", ConfigCrafting.outPutAluminium);
        magicalEssence.put("Ardite", ConfigCrafting.outPutAlumite);
        magicalEssence.put("Cobalt", ConfigCrafting.outPutCobalt);
        magicalEssence.put("Copper", ConfigCrafting.outPutCopper);
        magicalEssence.put("Lead", ConfigCrafting.outPutLead);
        magicalEssence.put("Nickel", ConfigCrafting.outPutNickel);
        magicalEssence.put("Osmium", ConfigCrafting.outPutOsmium);
        magicalEssence.put("Silver", ConfigCrafting.outPutSilver);
        magicalEssence.put("Tin", ConfigCrafting.outPutTin);
        magicalEssence.put("Yellorite", ConfigCrafting.outPutYellorite);
        magicalEssence.put("Platinum", ConfigCrafting.outPutPlatinum);
        magicalEssence.put("Alumite", ConfigCrafting.outPutAlumite);
        magicalEssence.put("Bronze", ConfigCrafting.outPutBronze);
        magicalEssence.put("Electrum", ConfigCrafting.outPutElectrum);
        magicalEssence.put("Enderium", ConfigCrafting.outPutElectrum);
        magicalEssence.put("Invar", ConfigCrafting.outPutInvar);
        magicalEssence.put("Lumium", ConfigCrafting.outPutLumium);
        magicalEssence.put("Manasteel", ConfigCrafting.outPutManasteel);
        magicalEssence.put("Manyullyn", ConfigCrafting.outPutManyullyn);
        magicalEssence.put("Signalum", ConfigCrafting.outPutSignalum);
        magicalEssence.put("Steel", ConfigCrafting.outPutSteel);
        magicalEssence.put("Terrasteel", ConfigCrafting.outPutTerrasteel);
        magicalEssence.put("Ardite", ConfigCrafting.outPutArdite);
        magicalEssence.put("Diamond", ConfigCrafting.outPutDiamond);
        magicalEssence.put("Emerald", ConfigCrafting.outPutEmerald);
        magicalEssence.put("Ruby", ConfigCrafting.outPutRuby);
        magicalEssence.put("Peridot", ConfigCrafting.outPutPeridot);
        magicalEssence.put("Saphire", ConfigCrafting.outPutSapphire);
    }
}
