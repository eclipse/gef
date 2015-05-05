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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart.
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.activate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.IAdaptable;

/**
 * A support class to manage the active state for a source {@link IActivatable}.
 * It offers all methods defined by {@link IActivatable}, while not formally
 * implementing the interface, and can thus be used by a source
 * {@link IActivatable} as a delegate.
 * <p>
 * In addition to the source {@link IActivatable} a
 * {@link PropertyChangeSupport} is expected during construction. It will be
 * used to fire {@link PropertyChangeEvent}s when the active state changes, i.e.
 * whenever a call to {@link #activate()} or {@link #deactivate()} results in a
 * state change. {@link IActivatable#ACTIVE_PROPERTY} will be used as the
 * property name within all those events.
 * <p>
 * If the given {@link IActivatable} is also {@link IAdaptable}, all calls to
 * {@link #activate()} and {@link #deactivate()} will be forwarded to all
 * adapters registered at the {@link IActivatable} at that moment. However, the
 * {@link ActivatableSupport} will not register a property change listener on
 * the {@link IAdaptable} to get notified about newly set or unset adapters, so
 * they will not be automatically activated/deactivated. The source
 * {@link IActivatable} may use an {@link AdaptableSupport} as a second delegate
 * for this purpose.
 * 
 * @author anyssen
 *
 */
public class ActivatableSupport {

	private boolean isActive = false;
	private IActivatable source;
	private PropertyChangeSupport pcs;

	/**
	 * Creates a new {@link ActivatableSupport} for the given source
	 * {@link IActivatable} and a related {@link PropertyChangeSupport}.
	 * 
	 * @param source
	 *            The {@link IActivatable} that encloses the to be created
	 *            {@link ActivatableSupport}, delegating calls to it. May not be
	 *            <code>null</code>
	 * @param pcs
	 *            An {@link PropertyChangeSupport}, which will be used to fire
	 *            {@link PropertyChangeEvent}'s during state changes. May not be
	 *            <code>null</code>
	 */
	public ActivatableSupport(IActivatable source, PropertyChangeSupport pcs) {
		if (source == null) {
			throw new IllegalArgumentException("source may not be null.");
		}
		if (pcs == null) {
			throw new IllegalArgumentException("pcs may not be null.");
		}
		this.source = source;
		this.pcs = pcs;
	}

	/**
	 * Reports whether this {@link ActivatableSupport} is active or inactive.
	 * 
	 * @return {@code true} in case the {@link ActivatableSupport} is active,
	 *         {@code false} otherwise.
	 * 
	 * @see IActivatable#isActive()
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Activates this {@link ActivatableSupport} if it is not yet active.
	 * 
	 * Will first adjust the (internal) active state, then fire a
	 * {@link PropertyChangeEvent}, and will finally activate any
	 * {@link IActivatable} adapters, being registered at the source
	 * {@link IActivatable}.
	 * 
	 * @see IActivatable#activate()
	 */
	public void activate() {
		if (!isActive) {
			isActive = true;
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, false, true);

			// activate all adapters, if the IActivatable is also an IAdaptable
			activateAdapters();
		}
	}

	private void activateAdapters() {
		if (source instanceof IAdaptable) {
			for (IActivatable adapter : ((IAdaptable) source)
					.<IActivatable> getAdapters(IActivatable.class).values()) {
				adapter.activate();
			}
		}
	}

	private void deactivateAdapters() {
		if (source instanceof IAdaptable) {
			for (IActivatable adapter : ((IAdaptable) source)
					.<IActivatable> getAdapters(IActivatable.class).values()) {
				adapter.deactivate();
			}
		}
	}

	/**
	 * Deactivates this {@link ActivatableSupport} if it is not yet inactive.
	 * 
	 * Will first deactivate any {@link IActivatable} adapters, being registered
	 * at the source {@link IActivatable}, then adjust the (internal) active
	 * state, and finally fire a {@link PropertyChangeEvent}.
	 * 
	 * @see IActivatable#deactivate()
	 */
	public void deactivate() {
		if (isActive) {
			// deactivate all adapters, if the IActivatable is also an
			// IAdaptable
			deactivateAdapters();

			isActive = false;
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, true, false);
		}
	}

}
