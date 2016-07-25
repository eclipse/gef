/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.adapt.inject;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.common.adapt.IAdaptable;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * A Guice {@link Scope} that is bound to an {@link IAdaptable}-compliant type
 * and has to be scoped to a respective instance of that type.
 * <p>
 * After the scope has been entered for an {@link IAdaptable} instance (which
 * automatically switches the scope to this {@link IAdaptable} instance), the
 * {@link AdaptableScope} will maintain a set of scoped objects for the
 * respective {@link IAdaptable} instance. The scoped provider being returned by
 * {@link #scope(Key, Provider)} will always (recycle) objects from this set of
 * scoped objects, only creating new instances if a respective instance is not
 * already contained in the set of scoped objects.
 * <p>
 * The {@link AdaptableScope} may be switched between all {@link IAdaptable}
 * instances, for which it has been entered before (upon entering, it will
 * automatically switch to the instance it has been entered for). The scoped
 * provider will always refer to the set of scoped objects bound to the
 * {@link IAdaptable} instance the scope was switched to last, preserving the
 * set of objects instances for all other {@link IAdaptable} instances.
 * <p>
 * Leaving the scope for an {@link IAdaptable} instance will clear the set of
 * scoped objects for this {@link IAdaptable} instance, so no scoped objects may
 * be recycled afterwards. The {@link AdaptableScope} will have to be re-entered
 * for the {@link IAdaptable} instance after it has been left for it.
 *
 * @author anyssen
 *
 * @param <A>
 *            The type of {@link IAdaptable} this {@link Scope} is bound to.
 */
public class AdaptableScope<A extends IAdaptable> implements Scope {

	// hold a set of scoped instances per adaptable instance
	private Map<IAdaptable, Map<Key<?>, Object>> scopedInstances = new HashMap<>();

	private A adaptable = null;
	private Class<? extends A> type;

	/**
	 * Creates a new {@link AdaptableScope} for the given {@link IAdaptable}
	 * type.
	 *
	 * @param type
	 *            The {@link IAdaptable} type this scope is responsible for.
	 */
	public AdaptableScope(Class<? extends A> type) {
		this.type = type;
	}

	/**
	 * Enters this scope for the given {@link IAdaptable} instance and binds the
	 * scope to it, so that the {@link Provider} returned by
	 * {@link #scope(Key, Provider)} will return (recycled) instances from a set
	 * of scoped objects maintained for the {@link IAdaptable} instance, until
	 * the scope is entered and/or bound to another {@link IAdaptable} instance
	 * (see {@link #switchTo(IAdaptable)}) or left (see
	 * {@link #leave(IAdaptable)}) for it.
	 *
	 * @param instance
	 *            The {@link IAdaptable} instance to enter (and bind) this
	 *            {@link AdaptableScope} for.
	 */
	public void enter(A instance) {
		if (!scopedInstances.containsKey(instance)) {
			scopedInstances.put(instance, new HashMap<Key<?>, Object>());
		}
		this.adaptable = instance;
	}

	/**
	 * Leaves this scope for the given {@link IAdaptable} instance, resulting in
	 * unbinding this scope from it and clearing the set of scoped objects
	 * maintained for it.
	 * <p>
	 * The scope may not be switched back to the {@link IAdaptable} instance
	 * before having been re-entered for it (see {@link #enter(IAdaptable)}).
	 *
	 * @param instance
	 *            The {@link IAdaptable} instance to (unbind and) leave this
	 *            {@link AdaptableScope} for.
	 */
	public void leave(A instance) {
		if (scopedInstances.containsKey(instance)) {
			scopedInstances.remove(instance);
		}
		this.adaptable = null;
	}

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T get() {
				if (adaptable == null) {
					throw new IllegalStateException("AdaptableScope for type '"
							+ type
							+ "' is not yet bound to an adaptable instance.");
				} else {
					// obtain the map of scoped instances for the given
					// adaptable
					Map<Key<?>, Object> scope = scopedInstances.get(adaptable);

					// retrieve a cached instance from the scope (if it exists)
					Object instance = scope.get(key);
					if (instance == null) {
						// obtain a new instance
						instance = unscoped.get();
						// keep track of the instance (for later access)
						if (instance != null) {
							scope.put(key, instance);
						}
						// System.out.println("Created (scoped) instance for "
						// + key + "in " + type + " scope for " + adaptable);
					} else {
						// System.out.println("Recycling (scoped) instance for "
						// + key + "in " + type + " scope for " + adaptable);
					}
					return (T) instance;
				}
			}
		};
	}

	/**
	 * Switches this scope to be bound to the given {@link IAdaptable} instance,
	 * so that the {@link Provider} returned by {@link #scope(Key, Provider)}
	 * will afterwards return (recycled) instances from the set of scoped
	 * objects that is maintained for the respective {@link IAdaptable}
	 * instance, until the scope is bound to another {@link IAdaptable} instance
	 * ({@link #switchTo(IAdaptable)}) or left ( {@link #leave(IAdaptable)}) for
	 * the given instance.
	 * <p>
	 * When switching the scope to another {@link IAdaptable} instance, the set
	 * of objects maintained for previously bound {@link IAdaptable} instances
	 * is preserved (unless the scope is left for them) and will be re-used
	 * after switching back to the respective {@link IAdaptable} instance.
	 * <p>
	 * Before switching the scope to an {@link IAdaptable} instance, the scope
	 * has to be initially entered ({@link #enter(IAdaptable)}) for it, which
	 * will automatically bind the scope to it.
	 *
	 * @param instance
	 *            The {@link IAdaptable} instance to bind this
	 *            {@link AdaptableScope} to.
	 */
	public void switchTo(A instance) {
		// System.out.println("Switching scope for type " + type + " from " +
		// this.adaptable + " to " + instance);
		this.adaptable = instance;
	}
}
