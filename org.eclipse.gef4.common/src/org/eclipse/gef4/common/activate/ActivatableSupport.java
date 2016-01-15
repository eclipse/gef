/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.IAdaptable;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

/**
 * A support class to manage the activeProperty state for a source {@link IActivatable}.
 * It offers all methods defined by {@link IActivatable}, while not formally
 * implementing the interface, and can thus be used by a source
 * {@link IActivatable} as a delegate.
 * <p>
 * If the given {@link IActivatable} is also {@link IAdaptable}, all calls to
 * {@link #activate()} and {@link #deactivate()} will be forwarded to all
 * adapters registered at the {@link IActivatable} at that moment. However, the
 * {@link ActivatableSupport} will not register a change listener on the
 * {@link IAdaptable} to get notified about newly set or unset adapters, so they
 * will not be automatically activated/deactivated. The source
 * {@link IActivatable} may use an {@link AdaptableSupport} as a second delegate
 * for this purpose.
 * 
 * @author anyssen
 *
 */
public class ActivatableSupport {

	private ReadOnlyBooleanWrapper activeProperty = null;
	private IActivatable source;

	/**
	 * Creates a new {@link ActivatableSupport} for the given source
	 * {@link IActivatable} and a related {@link PropertyChangeSupport}.
	 * 
	 * @param source
	 *            The {@link IActivatable} that encloses the to be created
	 *            {@link ActivatableSupport}, delegating calls to it. May not be
	 *            <code>null</code>
	 */
	public ActivatableSupport(IActivatable source) {
		if (source == null) {
			throw new IllegalArgumentException("source may not be null.");
		}
		this.source = source;
		this.activeProperty = new ReadOnlyBooleanWrapper(source, IActivatable.ACTIVE_PROPERTY,
				false);
	}

	/**
	 * Returns a {@link ReadOnlyBooleanProperty} that reflects the activeProperty state
	 * of this {@link ActivatableSupport}.
	 * 
	 * @return A read-only boolean {@link Property} representing the activeProperty
	 *         state.
	 */
	public ReadOnlyBooleanProperty activeProperty() {
		return activeProperty.getReadOnlyProperty();
	}

	/**
	 * Reports whether this {@link ActivatableSupport} is activeProperty or inactive.
	 * 
	 * @return {@code true} in case the {@link ActivatableSupport} is activeProperty,
	 *         {@code false} otherwise.
	 * 
	 * @see IActivatable#isActive()
	 */
	public boolean isActive() {
		return activeProperty.get();
	}

	/**
	 * Activates this {@link ActivatableSupport} if it is not yet activeProperty.
	 * 
	 * Will first adjust the activeProperty state, then activate any
	 * {@link IActivatable} adapters, being registered at the source
	 * {@link IActivatable}.
	 * 
	 * @see IActivatable#activate()
	 */
	public void activate() {
		if (!isActive()) {
			activeProperty.set(true);

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
	 * at the source {@link IActivatable}, then adjust the activeProperty state.
	 * 
	 * @see IActivatable#deactivate()
	 */
	public void deactivate() {
		if (isActive()) {
			// deactivate all adapters, if the IActivatable is also an
			// IAdaptable
			deactivateAdapters();

			activeProperty.set(false);
		}
	}

}
