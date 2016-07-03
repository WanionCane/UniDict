package wanion.unidict.module;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import wanion.unidict.LoadStage;
import wanion.unidict.helper.LogHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractModule
{
    private final Set<Class<? extends AbstractModuleThread>> threadsClasses = new HashSet<>();
    private final EnumMap<LoadStage, List<AbstractModuleThread>> threads = getLoadStageMap();
    private final String moduleName;

    protected AbstractModule(String moduleName)
    {
        this.moduleName = moduleName;
    }

    protected final void add(final AbstractModuleThread moduleThread)
    {
        Class<? extends AbstractModuleThread> moduleThreadClass = moduleThread.getClass();
        if (threadsClasses.contains(moduleThreadClass))
            return;
        threadsClasses.add(moduleThreadClass);
        if (moduleThreadClass.isAnnotationPresent(SpecifiedLoadStage.class))
            add(moduleThreadClass.getAnnotation(SpecifiedLoadStage.class).stage(), moduleThread);
        else
            add(LoadStage.POST_INIT, moduleThread);
    }

    private void add(final LoadStage loadStage, final AbstractModuleThread moduleThread)
    {
        threads.get(loadStage).add(moduleThread);
    }

    protected final boolean isEmpty(final LoadStage loadStage)
    {
        return threads.get(loadStage).isEmpty();
    }

    protected final boolean isEmpty()
    {
        return threadsClasses.isEmpty();
    }

    protected abstract void init();

    protected void preparations() {}

    final void start(@Nonnull final LoadStage loadStage)
    {
        final List<AbstractModuleThread> threadList = threads.get(loadStage);
        final ExecutorService moduleThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            final long initialTime = System.nanoTime();
            final List<Future<String>> futureOfThreads = moduleThreadExecutor.invokeAll(threadList);
            final long took = System.nanoTime() - initialTime;
            for (Future<String> threadModuleSay : futureOfThreads)
                LogHelper.info(threadModuleSay.get());
            LogHelper.info("All " + threadList.size() + " " + moduleName + "s took " + took / 1000000 + "ms to finish.");
        } catch (InterruptedException | ExecutionException e) {
            LogHelper.error("Something really bad happened on: " + moduleName);
            e.printStackTrace();
        }
    }

    private static EnumMap<LoadStage, List<AbstractModuleThread>> getLoadStageMap()
    {
        final EnumMap<LoadStage, List<AbstractModuleThread>> loadStageListEnumMap = new EnumMap<>(LoadStage.class);
        for (final LoadStage loadStage : LoadStage.values())
            loadStageListEnumMap.put(loadStage, new ArrayList<AbstractModuleThread>());
        return loadStageListEnumMap;
    }
}