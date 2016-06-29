package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Dependencies<D>
{
    private final Map<Class, D> dependencies = new IdentityHashMap<>();
    private final Map<Class, DependenceWatcher<? extends D>> dependenciesWatchers = new IdentityHashMap<>();

    public Dependencies() {}

    public final <I extends D> void add(Class<I> typeClass)
    {
        if (dependencies.containsKey(typeClass))
            return;
        DependenceWatcher<? extends D> dependenceWatcher = dependenciesWatchers.get(typeClass);
        if (dependenceWatcher != null) {
            add(dependenceWatcher.instantiate());
            return;
        }
        try {
            Constructor constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            add((I) constructor.newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public final <I extends D> void add(I instance)
    {
        Class typeClass = instance.getClass();
        if (dependencies.containsKey(typeClass))
            return;
        dependencies.put(typeClass, instance);
    }

    public final <I extends D> I get(Class<I> typeClass)
    {
        I instance = (I) dependencies.get(typeClass);
        if (instance != null)
            return instance;
        add(typeClass);
        return (I) dependencies.get(typeClass);
    }

    public final boolean contains(Class<? extends D> typeClass)
    {
        return dependencies.containsKey(typeClass);
    }

    public final void subscribe(DependenceWatcher<? extends D> dependenceWatcher)
    {
        if (!dependenciesWatchers.containsKey(dependenceWatcher.dependenceClass))
            dependenciesWatchers.put(dependenceWatcher.dependenceClass, dependenceWatcher);
    }

    public abstract class DependenceWatcher<W extends D>
    {
        private final Class<W> dependenceClass;

        protected DependenceWatcher()
        {
            this.dependenceClass = (Class<W>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        public abstract W instantiate();
    }
}