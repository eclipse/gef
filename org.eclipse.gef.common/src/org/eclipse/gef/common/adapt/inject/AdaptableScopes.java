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
import java.util.Stack;

import org.eclipse.gef.common.adapt.IAdaptable;

/**
 * A utility class to support adaptable-based scoping. It will recursively enter
 * and leave all transitive adaptable scopes (reachable by navigating the
 * adaptable chain) for a given adaptable. An internal stack is maintained, so
 * only the last {@link #enter(IAdaptable) entered} scope may be
 * {@link #leave(IAdaptable) left}. When leaving a scope, the previous state is
 * restored, i.e. the last entered scope will be entered again.
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
	// FIXME: Only scopes for raw runtime types should be maintained, i.e.
	// scopes for interfaces and abstract base types should not be maintained.
	private static final Map<Class<? extends IAdaptable>, AdaptableScope<? extends IAdaptable>> scopes = new HashMap<>();
	private static final Stack<IAdaptable> history = new Stack<>();

	/**
	 * Transitively enters the {@link AdaptableScope} of all
	 * {@link IAdaptable}-compliant types (i.e. (super-)classes implementing
	 * {@link IAdaptable} and (super-)interfaces extending {@link IAdaptable})
	 * of the given {@link IAdaptable} for the given {@link IAdaptable}.
	 *
	 * @param adaptable
	 *            The {@link IAdaptable} instance, for whose types to enter the
	 *            {@link AdaptableScope}s with the instance.
	 */
	static void enter(final IAdaptable adaptable) {
		if (adaptable == null) {
			throw new IllegalArgumentException(
					"The given IAdaptable may not be null.");
		}

		// String indent = new String(new char[2 *
		// history.size()]).replace('\0',
		// '-');
		// System.out
		// .println(indent + "Entering all typed scopes for " + adaptable);

		history.push(adaptable);
		processEnter(adaptable);
	}

	/**
	 * Transitively leaves the {@link AdaptableScope} of all
	 * {@link IAdaptable}-compliant types (i.e. (super-)classes implementing
	 * {@link IAdaptable} and (super-)interfaces extending {@link IAdaptable})
	 * of the given {@link IAdaptable} for the given {@link IAdaptable}.
	 *
	 * @param adaptable
	 *            The {@link IAdaptable} instance, for whose types to leave the
	 *            {@link AdaptableScope}s with the instance. It has to be the
	 *            adaptable for which a scope was last entered (and is used only
	 *            to check the contract is preserved) .
	 */
	static void leave(final IAdaptable adaptable) {
		if (adaptable == null) {
			throw new IllegalArgumentException(
					"The given IAdaptable may not be null.");
		}
		if (history.pop() != adaptable) {
			throw new IllegalArgumentException(
					"Only last entered scope may be left");
		}

		processLeave(adaptable);
		if (!history.isEmpty()) {
			// XXX: re-enter previous scope to restore state before last
			// enter
			processEnter(history.peek());
		}

		// String indent = new String(new char[2 *
		// history.size()]).replace('\0',
		// '-');
		// System.out.println(indent + "Left all typed scopes for " +
		// adaptable);
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

	private static void processEnter(final IAdaptable adaptable) {
		// XXX: If the given adaptable is an adapter itself, recursively
		// enter the scope of the adaptable it is bound to.
		if (adaptable instanceof IAdaptable.Bound) {
			IAdaptable boundTo = ((IAdaptable.Bound<?>) adaptable)
					.getAdaptable();
			if (boundTo != null) {
				processEnter(boundTo);
			}
		}

		process(adaptable.getClass(), adaptable, new ScopeProcessor() {
			@Override
			public void process(Class<? extends IAdaptable> adaptableType,
					IAdaptable adaptableInstance) {
				AdaptableScopes.<IAdaptable> typed(adaptableType)
						.enter(adaptableInstance);
			}
		});
	}

	private static void processLeave(final IAdaptable adaptable) {
		process(adaptable.getClass(), adaptable, new ScopeProcessor() {
			@Override
			public void process(Class<? extends IAdaptable> adaptableType,
					IAdaptable adaptableInstance) {
				AdaptableScopes.<IAdaptable> typed(adaptableType)
						.leave(adaptableInstance);
			}
		});

		// XXX: If the given adaptable is an adapter itself, recursively
		// leave the scope the adaptable it is bound to.
		if (adaptable instanceof IAdaptable.Bound) {
			IAdaptable boundTo = ((IAdaptable.Bound<?>) adaptable)
					.getAdaptable();
			if (boundTo != null) {
				processLeave(boundTo);
			}
		}
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

	private AdaptableScopes() {
		// should not be invoked by clients
	}
}
