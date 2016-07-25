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

/**
 * A utility class to support adaptable-based scoping.
 * 
 * @see AdaptableScope
 * 
 * @author anyssen
 *
 */
public class AdaptableScopes {

	private interface ScopeProcessor {
		public void process(Class<? extends IAdaptable> adaptableType,
				final IAdaptable adaptableInstance);
	}

	// one adaptable scope for each type of adaptable
	private static final Map<Class<? extends IAdaptable>, AdaptableScope<? extends IAdaptable>> scopes = new HashMap<>();

	private AdaptableScopes() {
		// should not be invoked by clients
	}

	/**
	 * Retrieves an {@link AdaptableScope} for the given {@link IAdaptable}
	 * -compliant type.
	 * 
	 * @param <A>
	 *            The {@link IAdaptable} (sub-)type to return an
	 *            {@link AdaptableScope} for.
	 * 
	 * @param type
	 *            The type of the {@link AdaptableScope}.
	 * @return The static {@link AdaptableScope} instance for the given type.
	 */
	@SuppressWarnings("unchecked")
	public static <A extends IAdaptable> AdaptableScope<A> typed(
			Class<? extends A> type) {
		AdaptableScope<? extends IAdaptable> scope = scopes.get(type);
		if (scope == null) {
			// create singleton scope and register it
			scope = new AdaptableScope<>(type);
			scopes.put(type, scope);
		}
		return (AdaptableScope<A>) scope;
	}

	/**
	 * Enters the {@link AdaptableScope} of all {@link IAdaptable}-compliant
	 * types (i.e. (super-)classes implementing {@link IAdaptable} and
	 * (super-)interfaces extending {@link IAdaptable}) of the given
	 * {@link IAdaptable} for the given {@link IAdaptable}.
	 * 
	 * @param adaptable
	 *            The {@link IAdaptable} instance, for whose types to enter the
	 *            {@link AdaptableScope}s with the instance.
	 * 
	 * @see AdaptableScope#enter(IAdaptable)
	 */
	public static void enter(final IAdaptable adaptable) {
		process(adaptable.getClass(), adaptable, new ScopeProcessor() {
			@Override
			public void process(Class<? extends IAdaptable> adaptableType,
					IAdaptable adaptableInstance) {
				AdaptableScopes.typed(adaptableType).enter(adaptableInstance);
			}
		});
	}

	/**
	 * Switches the {@link AdaptableScope} of all {@link IAdaptable}-compliant
	 * types (i.e. (super-)classes implementing {@link IAdaptable} and
	 * (super-)interfaces extending {@link IAdaptable}) of the given
	 * {@link IAdaptable} to the given {@link IAdaptable}.
	 * 
	 * @param adaptable
	 *            The {@link IAdaptable} instance, for whose types to switch the
	 *            {@link AdaptableScope}s to the instance.
	 * 
	 * @see AdaptableScope#switchTo(IAdaptable)
	 */
	public static void switchTo(final IAdaptable adaptable) {
		process(adaptable.getClass(), adaptable, new ScopeProcessor() {
			@Override
			public void process(Class<? extends IAdaptable> adaptableType,
					IAdaptable adaptableInstance) {
				AdaptableScopes.typed(adaptableType)
						.switchTo(adaptableInstance);
			}
		});
	}

	/**
	 * Leaves the {@link AdaptableScope} of all {@link IAdaptable}-compliant
	 * types (i.e. (super-)classes implementing {@link IAdaptable} and
	 * (super-)interfaces extending {@link IAdaptable}) of the given
	 * {@link IAdaptable} for the given {@link IAdaptable}.
	 * 
	 * @param adaptable
	 *            The {@link IAdaptable} instance, for whose types to leave the
	 *            {@link AdaptableScope}s with the instance.
	 * 
	 * @see AdaptableScope#leave(IAdaptable)
	 */
	public static void leave(final IAdaptable adaptable) {
		process(adaptable.getClass(), adaptable, new ScopeProcessor() {
			@Override
			public void process(Class<? extends IAdaptable> adaptableType,
					IAdaptable adaptableInstance) {
				AdaptableScopes.typed(adaptableType).leave(adaptableInstance);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static void process(Class<? extends IAdaptable> adaptableType,
			final IAdaptable adaptableInstance, ScopeProcessor processor) {
		processor.process(adaptableType, adaptableInstance);

		// evaluate super classes of the given type
		if (adaptableType.getSuperclass() != null && IAdaptable.class
				.isAssignableFrom(adaptableType.getSuperclass())) {
			process((Class<? extends IAdaptable>) adaptableType.getSuperclass(),
					adaptableInstance, processor);
		}
		// evaluate interfaces of the given type
		for (Class<?> interfaceType : adaptableType.getInterfaces()) {
			if (IAdaptable.class.isAssignableFrom(interfaceType)) {
				process((Class<? extends IAdaptable>) interfaceType,
						adaptableInstance, processor);
			}
		}
	}
}
