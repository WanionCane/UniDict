package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import wanion.unidict.helper.LogHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractModule
{
    private final Set<Class> threadsClasses = new HashSet<>();
    private final List<AbstractModuleThread> threads = new ArrayList<>();
    private final String moduleName;

    protected AbstractModule(String moduleName)
    {
        this.moduleName = moduleName;
    }

    protected final void add(AbstractModuleThread moduleThread)
    {
        Class moduleThreadClass = moduleThread.getClass();
        if (threadsClasses.contains(moduleThreadClass))
            return;
        threadsClasses.add(moduleThreadClass);
        threads.add(moduleThread);
    }

    protected final boolean isEmpty()
    {
        return threads.isEmpty();
    }

    protected abstract void init();

    protected void preparations() {}

    final void start()
    {
        ExecutorService moduleThreadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            long initialTime = System.nanoTime();
            List<Future<String>> futureOfThreads = moduleThreadExecutor.invokeAll(threads);
            long took = System.nanoTime() - initialTime;
            for (Future<String> threadModuleSay : futureOfThreads)
                LogHelper.info(threadModuleSay.get());
            LogHelper.info("All " + threads.size() + " " + moduleName + "s took " + took / 1000000 + "ms to finish.");
        } catch (InterruptedException | ExecutionException e) {
            LogHelper.error("Something really bad happened.");
            e.printStackTrace();
        }
    }
}