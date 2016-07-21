package wanion.unidict.module;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.common.event.FMLStateEvent;
import gnu.trove.map.hash.THashMap;
import wanion.unidict.LoadStage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ModuleHandler
{
    private final Set<Class<? extends AbstractModule>> moduleClasses = new HashSet<>();
    private final Map<AbstractModule, AbstractModule.Manager> modules = new THashMap<>();

    public void startModules(final FMLStateEvent event)
    {
        final LoadStage loadStage = (event != null) ? LoadStage.getStage(event.getClass()) : null;
        if (modules.isEmpty() || loadStage == null)
            return;
        modules.entrySet().forEach(e -> {
            final AbstractModule module = e.getKey();
            if (e.getValue() == null)
                e.setValue(module.manager);
            final AbstractModule.Manager manager = e.getValue();
            if (manager.isEmpty())
                module.init();
            if (!manager.isEmpty(loadStage))
                module.start(loadStage, manager);
        });
    }

    public void addModule(final AbstractModule module)
    {
        Class<? extends AbstractModule> moduleClass = (module != null) ? module.getClass() : null;
        if (moduleClass == null || moduleClasses.contains(moduleClass))
            return;
        moduleClasses.add(moduleClass);
        modules.put(module, null);
    }
}