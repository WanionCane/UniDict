package wanion.unidict.module;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraftforge.fml.common.event.FMLStateEvent;
import wanion.unidict.LoadStage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ModuleHandler
{
    private final Set<Class<? extends AbstractModule>> moduleClasses = new HashSet<>();
    private final Map<AbstractModule, AbstractModule.Manager> modules = new HashMap<>();

    public void startModules(final FMLStateEvent event)
    {
        final LoadStage loadStage = (event != null) ? LoadStage.getStage(event.getClass()) : null;
        if (modules.isEmpty() || loadStage == null)
            return;
        modules.entrySet().forEach(e -> {
            final AbstractModule module = e.getKey();
            if (e.getValue() == null)
                e.setValue(module.getAdder());
            final AbstractModule.Manager manager = e.getValue();
            if (manager.isEmpty())
                module.init(manager);
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