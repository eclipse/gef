/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart.
 *
 *******************************************************************************/
package org.eclipse.gef.common.activate;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

/**
 * A support class to manage the activeProperty state for a source
 * {@link IActivatable}. It offers all methods defined by {@link IActivatable},
 * while not formally implementing the interface, and can thus be used by a
 * source {@link IActivatable} as a delegate.
 *
 * @author anyssen
 *
 */
public class ActivatableSupport {

	private ReadOnlyBooleanWrapper activeProperty = null;

	/**
	 * Creates a new {@link ActivatableSupport} for the given source
	 * {@link IActivatable}.
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
		this.activeProperty = new ReadOnlyBooleanWrapper(source,
				IActivatable.ACTIVE_PROPERTY, false);
	}

	/**
	 * Activates this {@link ActivatableSupport} if it is not yet active.
	 *
	 * @param preActivationCallback
	 *            An optional callback that is executed before the active state
	 *            is set.
	 * @param postActivationCallback
	 *            An optional callback that is executed after the active state
	 *            has been set.
	 *
	 * @see IActivatable#activate()
	 */
	public void activate(Runnable preActivationCallback,
			Runnable postActivationCallback) {
		if (!isActive()) {
			if (preActivationCallback != null) {
				preActivationCallback.run();
			}
			activeProperty.set(true);
			if (postActivationCallback != null) {
				postActivationCallback.run();
			}
		}
	}

	/**
	 * Returns a {@link ReadOnlyBooleanProperty} that reflects the
	 * activeProperty state of this {@link ActivatableSupport}.
	 *
	 * @return A read-only boolean {@link Property} representing the
	 *         activeProperty state.
	 */
	public ReadOnlyBooleanProperty activeProperty() {
		return activeProperty.getReadOnlyProperty();
	}

	/**
	 * Deactivates this {@link ActivatableSupport} if it is not yet inactive.
	 *
	 * @param preDeactivationCallback
	 *            An optional callback that is executed before the active state
	 *            is unset.
	 * @param postDeactivationCallback
	 *            An optional callback that is executed after the active state
	 *            has been unset.
	 *
	 * @see IActivatable#deactivate()
	 */
	public void deactivate(Runnable preDeactivationCallback,
			Runnable postDeactivationCallback) {
		if (isActive()) {
			if (preDeactivationCallback != null) {
				preDeactivationCallback.run();
			}
			activeProperty.set(false);
			if (postDeactivationCallback != null) {
				postDeactivationCallback.run();
			}
		}
	}

	/**
	 * Reports whether this {@link ActivatableSupport} is activeProperty or
	 * inactive.
	 *
	 * @return {@code true} in case the {@link ActivatableSupport} is
	 *         activeProperty, {@code false} otherwise.
	 *
	 * @see IActivatable#isActive()
	 */
	public boolean isActive() {
		return activeProperty.get();
	}

}
