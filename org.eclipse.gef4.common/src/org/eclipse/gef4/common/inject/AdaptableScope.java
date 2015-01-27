/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.inject;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * A Guice {@link Scope} that is bound to an {@link IAdaptable}-compliant type
 * and has to be scoped to a respective instance of that type.
 * 
 * @author nyssen
 *
 * @param <A>
 *            The type of {@link IAdaptable} this {@link Scope} is bound to.
 */
public class AdaptableScope<A extends IAdaptable> implements Scope {

	// hold a set of scoped instances per adaptable instance
	private Map<IAdaptable, Map<Key<?>, Object>> scopedInstances = new HashMap<IAdaptable, Map<Key<?>, Object>>();
	
	
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
	 * Binds this scope to the given {@link IAdaptable} instance.
	 * 
	 * @param instance
	 *            The instance to bind this {@link AdaptableScope} to.
	 */
	public void scopeTo(A instance) {
		this.adaptable = instance;
	}

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T get() {
				if (adaptable == null) {
					throw new IllegalStateException(
							"AdaptableScope for type + " + type + " is not yet bound to an adaptable instance.");
				} else {
					// obtain the map of scoped instances for the given
					// adaptable
					Map<Key<?>, Object> scope = scopedInstances.get(adaptable);
					if (scope == null) {
						scope = new HashMap<Key<?>, Object>();
						scopedInstances.put(adaptable, scope);
					}

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
}
