/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * The {@link AbstractGesture} can be used as a base class for {@link IGesture}
 * implementations.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractGesture implements IGesture {

	private ActivatableSupport acs = new ActivatableSupport(this);
	private ReadOnlyObjectWrapper<IDomain> domainProperty = new ReadOnlyObjectWrapper<>();
	private Map<IViewer, List<IHandler>> activeHandlers = new IdentityHashMap<>();

	@Override
	public final void activate() {
		acs.activate(null, this::doActivate);
	}

	@Override
	public final ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IDomain> adaptableProperty() {
		return domainProperty.getReadOnlyProperty();
	}

	/**
	 * Clears the list of active handlers of this gesture for the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to clear the active handlers of
	 *            this gesture.
	 * @see #getActiveHandlers(IViewer)
	 * @see #setActiveHandlers(IViewer, Collection)
	 */
	protected void clearActiveHandlers(IViewer viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		activeHandlers.remove(viewer);
	}

	@Override
	public final void deactivate() {
		acs.deactivate(this::doDeactivate, null);
	}

	/**
	 * This method is called when a valid {@link IDomain} is attached to this
	 * gesture so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void doActivate() {
	}

	/**
	 * This method is called when the attached {@link IDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void doDeactivate() {
	}

	@Override
	public List<? extends IHandler> getActiveHandlers(IViewer viewer) {
		if (activeHandlers.containsKey(viewer)) {
			return Collections.unmodifiableList(activeHandlers.get(viewer));
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public IDomain getAdaptable() {
		return domainProperty.get();
	}

	@Override
	public IDomain getDomain() {
		return getAdaptable();
	}

	/**
	 * Returns the {@link IHandlerResolver} of the {@link IDomain}.
	 *
	 * @return the {@link IHandlerResolver} of the {@link IDomain}.
	 */
	protected IHandlerResolver getHandlerResolver() {
		return getDomain().getAdapter(IHandlerResolver.class);
	}

	@Override
	public final boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Set the active handlers of this gesture to the given handlers.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to store the active handlers of
	 *            this gesture.
	 * @param activeHandlers
	 *            The active handlers of this gesture.
	 * @see #clearActiveHandlers(IViewer)
	 * @see #getActiveHandlers(IViewer)
	 */
	protected void setActiveHandlers(IViewer viewer,
			Collection<? extends IHandler> activeHandlers) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		if (activeHandlers == null) {
			throw new IllegalArgumentException(
					"The given activePolicies may not be null.");
		}
		for (IHandler ap : activeHandlers) {
			if (ap.getHost().getViewer() != viewer) {
				throw new IllegalArgumentException(
						"Resolved handler is not hosted within viewer.");
			}
		}
		clearActiveHandlers(viewer);
		this.activeHandlers.put(viewer, new ArrayList<>(activeHandlers));
	}

	@Override
	public void setAdaptable(IDomain adaptable) {
		if (isActive()) {
			throw new IllegalStateException(
					"The reference to the IDomain may not be changed while the gesture is active. Please deactivate the gesture before setting the IEditDomain and re-activate it afterwards.");
		}
		domainProperty.set(adaptable);
	}
}
