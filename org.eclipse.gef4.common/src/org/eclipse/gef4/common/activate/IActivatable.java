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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;

/**
 * An {@link IActivatable} represents an entity that can be activated (
 * {@link #activate()}) and deactivated ({@link #deactivate()}) as required.
 * <p>
 * The current activation state of an {@link IActivatable} (whether the entity
 * is active or not) can be queried by clients ({@link #isActive()}), and it is
 * expected that an {@link IActivatable} notifies registered
 * {@link PropertyChangeListener}s about changes of its activation state by
 * means of {@link PropertyChangeEvent}s, using the property name
 * {@value #ACTIVE_PROPERTY}.
 * <p>
 * Any client implementing this interface may internally use an
 * {@link ActivatableSupport} as a delegate to easily realize the required
 * functionality.
 * 
 * @author anyssen
 * 
 */
public interface IActivatable extends IPropertyChangeNotifier {

	/**
	 * A key used as {@link PropertyChangeEvent#getPropertyName()} when
	 * notifying about changes of the activation state.
	 */
	public static String ACTIVE_PROPERTY = "active";

	/**
	 * Activates the {@link IActivatable}. It is expected that a call to
	 * {@link IActivatable#isActive()} returns {@code true} after this method
	 * has been called (unless {@link #deactivate()} is called to deactivate the
	 * {@link IActivatable}), and that a {@link PropertyChangeEvent} notifying
	 * about an activation change is send to all registered
	 * {@link PropertyChangeListener}s, if the activation state actually
	 * changed, i.e. the {@link IActivatable} was not active before.
	 */
	public void activate();

	/**
	 * Deactivates the {@link IActivatable}. It is expected that a call to
	 * {@link IActivatable#isActive()} return {@code false} after this method
	 * has been called (unless {{@link #activate()} is called to re-activate the
	 * {@link IAdaptable}, and that a {@link PropertyChangeEvent} notifying
	 * about an activation change is send to all registered
	 * {@link PropertyChangeListener}s, if the activation state actually
	 * changed, i.e. the {@link IActivatable} was active before.
	 */
	public void deactivate();

	/**
	 * Reports whether this {@link IActivatable} is active or inactive.
	 * 
	 * @return {@code true} in case the {@link IActivatable} is active,
	 *         {@code false} otherwise.
	 */
	public boolean isActive();

}