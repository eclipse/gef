/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.properties.PropertyChangeNotifierSupport;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractTool<VR> implements ITool<VR> {

	/**
	 * A {@link PropertyChangeSupport} that is used as a delegate to notify
	 * listeners about changes to this object. May be used by subclasses to
	 * trigger the notification of listeners.
	 */
	protected PropertyChangeNotifierSupport pcs = new PropertyChangeNotifierSupport(
			this);

	private boolean active = false;
	private IDomain<VR> domain;
	private Map<IViewer<VR>, List<IPolicy<VR>>> activePolicies = new IdentityHashMap<>();

	@Override
	public void activate() {
		if (domain == null) {
			throw new IllegalStateException(
					"The IEditDomain has to be set via setDomain(IDomain) before activation.");
		}

		boolean oldActive = active;
		active = true;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}

		registerListeners();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Clears the list of active policies of this tool for the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to clear the active policies of
	 *            this tool.
	 * @see #getActivePolicies(IViewer)
	 * @see #setActivePolicies(IViewer, Collection)
	 */
	protected void clearActivePolicies(IViewer<VR> viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		activePolicies.remove(viewer);
	}

	@Override
	public void deactivate() {
		unregisterListeners();

		boolean oldActive = active;
		active = false;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	@Override
	public List<? extends IPolicy<VR>> getActivePolicies(IViewer<VR> viewer) {
		if (activePolicies.containsKey(viewer)) {
			return Collections.unmodifiableList(activePolicies.get(viewer));
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public IDomain<VR> getAdaptable() {
		return domain;
	}

	@Override
	public IDomain<VR> getDomain() {
		return getAdaptable();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	/**
	 * This method is called when a valid {@link IDomain} is attached to this
	 * tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void registerListeners() {
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Set the active policies of this tool to the given policies.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to store the active policies of
	 *            this tool.
	 * @param activePolicies
	 *            The active policies of this tool.
	 * @see #clearActivePolicies(IViewer)
	 * @see #getActivePolicies(IViewer)
	 */
	protected void setActivePolicies(IViewer<VR> viewer,
			Collection<? extends IPolicy<VR>> activePolicies) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		if (activePolicies == null) {
			throw new IllegalArgumentException(
					"The given activePolicies may not be null.");
		}
		clearActivePolicies(viewer);
		this.activePolicies.put(viewer, new ArrayList<>(activePolicies));
	}

	@Override
	public void setAdaptable(IDomain<VR> adaptable) {
		if (active) {
			throw new IllegalStateException(
					"The reference to the IDomain may not be changed while the tool is active. Please deactivate the tool before setting the IEditDomain and re-activate it afterwards.");
		}
		this.domain = adaptable;
	}

	/**
	 * This method is called when the attached {@link IDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void unregisterListeners() {
	}

}
