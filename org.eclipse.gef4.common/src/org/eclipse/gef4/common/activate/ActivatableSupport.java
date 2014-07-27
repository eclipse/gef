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

import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.adapt.IAdaptable;

/**
 * Support class to manage the state or an {@link IActivatable}. If the given
 * {@link IActivatable} is also {@link IAdaptable}, it will accordingly
 * activate/deactivate the related {@link IActivatable} adapters.
 * 
 * @author anyssen
 * 
 * @param <A>
 *            The type of {@link IActivatable} supported by this class.
 * 
 *
 */
public class ActivatableSupport<A extends IActivatable> {

	private boolean isActive = false;
	private A activatable;
	private PropertyChangeSupport pcs;

	public ActivatableSupport(A activatable, PropertyChangeSupport pcs) {
		this.activatable = activatable;
		this.pcs = pcs;
	}

	public boolean isActive() {
		return isActive;
	}

	public void activate() {
		if (!isActive) {
			isActive = true;
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, false, true);

			// activate all adapters, if the IActivatable is also an IAdaptable
			activateAdapters();
		}
	}

	private void activateAdapters() {
		if (activatable instanceof IAdaptable) {
			for (IActivatable adapter : ((IAdaptable) activatable)
					.<IActivatable> getAdapters(IActivatable.class).values()) {
				adapter.activate();
			}
		}
	}

	private void deactivateAdapters() {
		if (activatable instanceof IAdaptable) {
			for (IActivatable adapter : ((IAdaptable) activatable)
					.<IActivatable> getAdapters(IActivatable.class).values()) {
				adapter.deactivate();
			}
		}
	}

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
