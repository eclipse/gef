/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.adapt.inject;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.common.adapt.IAdaptable;

import com.google.common.collect.MapMaker;
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
 * respective {@link IAdaptable} instance.
 *
 * The scoped provider being returned by {@link #scope(Key, Provider)} will
 * always (recycle) objects from this set of scoped objects, only creating new
 * instances if a respective instance is not already contained in the set of
 * scoped objects. It will always refer to the set of scoped objects bound to
 * the {@link IAdaptable} instance the scope was switched to last, preserving
 * the set of objects instances for all other {@link IAdaptable} instances.
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
class AdaptableScope<A extends IAdaptable> implements Scope {

	// Maintain a set of scoped instances per adaptable instance
	// XXX: As the scoped instances need to be shared by scopes, to which the
	// type of the adaptable instance is applicable, we need to use a static
	// field here. The scope method will ensure that only entered scopes really
	// accesses the field.
	// XXX: Use a map with weak keys so they can be properly garbage collected;
	// Note that 'identity' equivalence will be used by default when weak keys
	// are
	// selected. This is crucial here!
	private static Map<IAdaptable, Map<Key<?>, Object>> scopedInstances = new MapMaker()
			.weakKeys().makeMap();

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
	 * the scope is bound to another {@link IAdaptable} instance or left (see
	 * {@link #leave(IAdaptable)}).
	 *
	 * @param adaptable
	 *            The {@link IAdaptable} instance to enter (and bind) this
	 *            {@link AdaptableScope} for.
	 */
	public void enter(A adaptable) {
		// System.out.println(
		// "Entering " + this + " of " + type + " for " + instance + ".");
		this.adaptable = adaptable;
	}

	/**
	 * Leaves this scope for the given {@link IAdaptable} instance, resulting in
	 * unbinding this scope from it and clearing the set of scoped objects
	 * maintained for it.
	 * <p>
	 * The scope may not be switched back to the {@link IAdaptable} instance
	 * before having been re-entered for it (see {@link #enter(IAdaptable)}).
	 *
	 * @param adaptable
	 *            The {@link IAdaptable} instance to (unbind and) leave this
	 *            {@link AdaptableScope} for.
	 */
	public void leave(A adaptable) {
		// System.out.println(
		// "Leaving " + this + " of " + type + " for " + instance + ".");
		this.adaptable = null;
	}

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T get() {
				if (adaptable == null) {
					// System.err.println("Scope " + AdaptableScope.this + " of
					// "
					// + type + " was not entered");
					throw new IllegalStateException(key
							+ " is scoped to adaptable '" + type
							+ "', for which no scope has been activated. You can only scope adapters in a scope of a transitive adaptable.");
				} else {
					// obtain the map of scoped instances for the given
					// adaptable
					if (!scopedInstances.containsKey(adaptable)) {
						scopedInstances.put(adaptable,
								new HashMap<Key<?>, Object>());
					}
					Map<Key<?>, Object> scope = scopedInstances.get(adaptable);
					// retrieve a cached instance from the scope (if it exists)
					// FIXME: We need to process all scopes of the key's
					// superclasses and interfaces here to retrieve an instance.
					Object instance = scope.get(key);
					if (instance == null) {
						// obtain a new instance
						instance = unscoped.get();
						// keep track of the instance (for later access)
						if (instance != null) {
							scope.put(key, instance);
						}
					}
					return (T) instance;
				}
			}
		};
	}
}
