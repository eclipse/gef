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

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * @author mwienand
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractTool<VR> implements ITool<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private boolean active = false;
	private IDomain<VR> domain;

	@Override
	public void setAdaptable(IDomain<VR> adaptable) {
		if (active) {
			throw new IllegalStateException(
					"The reference to the IDomain may not be changed while the tool is active. Please deactivate the tool before setting the IEditDomain and re-activate it afterwards.");
		}
		this.domain = adaptable;
	}

	/**
	 * This method is called when a valid {@link IDomain} is attached to this
	 * tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void registerListeners() {
	}

	/**
	 * This method is called when the attached {@link IDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void unregisterListeners() {
	}

	@Override
	public IDomain<VR> getDomain() {
		return getAdaptable();
	}

	@Override
	public IDomain<VR> getAdaptable() {
		return domain;
	}

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
	public boolean isActive() {
		return active;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
