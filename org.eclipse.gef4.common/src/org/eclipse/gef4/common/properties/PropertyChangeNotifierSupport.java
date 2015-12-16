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
 *******************************************************************************/
package org.eclipse.gef4.common.properties;

import java.beans.PropertyChangeSupport;

/**
 * An extension to {@link java.beans.PropertyChangeSupport} that is dedicated to
 * provide support implementation of {@link IPropertyChangeNotifier}.
 * 
 * @author anyssen
 *
 */
public class PropertyChangeNotifierSupport extends PropertyChangeSupport {

	private static final long serialVersionUID = -2361885204235272457L;

	/**
	 * Constructs a new {@link PropertyChangeNotifierSupport} for the given
	 * {@link IPropertyChangeNotifier}.
	 * 
	 * @param source
	 *            The source for which to create a new
	 *            {@link PropertyChangeNotifierSupport}.
	 */
	public PropertyChangeNotifierSupport(IPropertyChangeNotifier source) {
		super(source);
	}

}
