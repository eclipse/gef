/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.properties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An {@link IPropertyChangeNotifier} provides facilities to register and
 * unregister {@link PropertyChangeListener}s. It is responsible of notifying
 * them about property changes.
 * <p>
 * Any client implementing this interface may internally use an
 * {@link PropertyChangeSupport} as a delegate to easily realize the required
 * functionality.
 * 
 * @author mwienand
 * @author anyssen
 *
 */
public interface IPropertyChangeNotifier {

	// TODO: allow to register for single property events only, by adding
	// additional register methods from PropertyChangeSupport.

	/**
	 * Register a {@link PropertyChangeListener} at this
	 * {@link IPropertyChangeNotifier}.
	 * 
	 * @param listener
	 *            The {@link PropertyChangeListener} to register.
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Unregister an already registered {@link PropertyChangeListener} from this
	 * {@link IPropertyChangeNotifier}.
	 * 
	 * @param listener
	 *            The {@link PropertyChangeListener} to unregister.
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

}
