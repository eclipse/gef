/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
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

import org.eclipse.gef4.common.adapt.AdaptableSupport;

/**
 * An {@link IPropertyStore} allows to store and retrieve values of named
 * properties, notifying registered {@link PropertyChangeListener}s about all
 * (real) changes of property values, i.e. when a value gets set, unset, or
 * changed to a different value.
 * <p>
 * Any client implementing this interface may internally use an
 * {@link AdaptableSupport} as a delegate to easily realize the required
 * functionality.
 * 
 * @author mwienand
 * @author anyssen
 */
public interface IPropertyStore extends IPropertyChangeNotifier {

	/**
	 * Sets the value of the property specified by <i>name</i> with the passed-in
	 * <i>value</i>.
	 * 
	 * @param name
	 *            The name of the property whose value is to set/update.
	 * @param value
	 *            The property value.
	 */
	public void setProperty(String name, Object value);

	/**
	 * Returns the value of the property specified by <i>name</i>.
	 * 
	 * @param name
	 *            The name of the property whose value is to retrieve.
	 * @return The property value.
	 */
	public Object getProperty(String name);

}
