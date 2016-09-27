package wanion.unidict.common;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import javax.annotation.Nonnull;
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

	public final <I extends D> void add(final Class<? extends I> typeClass)
	{
		if (dependencies.containsKey(typeClass))
			return;
		final DependenceWatcher<? extends D> dependenceWatcher = dependenciesWatchers.get(typeClass);
		if (dependenceWatcher != null) {
			add(dependenceWatcher.instantiate());
			return;
		}
		try {
			final Constructor constructor = typeClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			add((I) constructor.newInstance());
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public final <I extends D> void add(final I instance)
	{
		final Class typeClass = instance.getClass();
		if (dependencies.containsKey(typeClass))
			return;
		dependencies.put(typeClass, instance);
	}

	public final <I extends D> I get(final Class<I> typeClass)
	{
		final I instance = (I) dependencies.get(typeClass);
		if (instance != null)
			return instance;
		add(typeClass);
		return (I) dependencies.get(typeClass);
	}

	public final boolean contains(final Class<? extends D> typeClass)
	{
		return dependencies.containsKey(typeClass);
	}

	public final void subscribe(final DependenceWatcher<? extends D> dependenceWatcher)
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

		@Nonnull
		public abstract W instantiate();
	}
}