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

/**
 * A utility class to support adaptable-based scoping.
 * 
 * @see AdaptableScope
 * 
 * @author anyssen
 *
 */
public class AdaptableScopes {

	// one adaptable scope for each type of adaptable
	private static final Map<Class<? extends IAdaptable>, AdaptableScope<? extends IAdaptable>> scopes = new HashMap<Class<? extends IAdaptable>, AdaptableScope<? extends IAdaptable>>();

	private AdaptableScopes() {
		// should not be invoked by clients
	}

	/**
	 * Retrieves an {@link AdaptableScope} for the given {@link IAdaptable}
	 * -compliant type.
	 * 
	 * @param type The type of the {@link AdaptableScope}.
	 * @return The static {@link AdaptableScope} instance for the given type.
	 */
	@SuppressWarnings("unchecked")
	public static <A extends IAdaptable> AdaptableScope<A> typed(
			Class<? extends A> type) {
		AdaptableScope<? extends IAdaptable> scope = scopes.get(type);
		if (scope == null) {
			// create singleton scope and register it
			scope = new AdaptableScope<A>(type);
			scopes.put(type, scope);
		}
		return (AdaptableScope<A>) scope;
	}

	/**
	 * Sets the {@link AdaptableScope} for all {@link IAdaptable}-compliant
	 * types (i.e. (super-)classes implementing {@link IAdaptable} and
	 * (super-)interfaces extending {@link IAdaptable}) of the given
	 * {@link IAdaptable} instance to the given instance.
	 * 
	 * @param adaptable
	 *            The {@link IAdaptable} instance to scope to.
	 */
	public static void scopeTo(final IAdaptable adaptable) {
		scopeTo(adaptable.getClass(), adaptable);
	}

	@SuppressWarnings("unchecked")
	private static void scopeTo(Class<? extends IAdaptable> adaptableType,
			final IAdaptable adaptableInstance) {
		// scope the adaptable scope of the given type to the instance
		AdaptableScopes.typed(adaptableType).scopeTo(adaptableInstance);

		// evaluate super classes of the given type
		if (adaptableType.getSuperclass() != null
				&& IAdaptable.class.isAssignableFrom(adaptableType
						.getSuperclass())) {
			scopeTo((Class<? extends IAdaptable>) adaptableType.getSuperclass(),
					adaptableInstance);
		}
		// evaluate interfaces of the given type
		for (Class<?> interfaceType : adaptableType.getInterfaces()) {
			if (IAdaptable.class.isAssignableFrom(interfaceType)) {
				scopeTo((Class<? extends IAdaptable>) interfaceType,
						adaptableInstance);
			}
		}
	}
}
