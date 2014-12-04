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

public interface IPropertyStore extends IPropertyChangeNotifier {

	/**
	 * Sets the value of the property specified by <i>key</i> with the passed-in
	 * <i>value</i>.
	 * 
	 * @param name
	 *            property name
	 * @param value
	 *            property value
	 */
	public void setProperty(String name, Object value);

	/**
	 * Returns the value of the property specified by <i>key</i>.
	 * 
	 * @param name
	 *            property name
	 * @return property value
	 */
	public Object getProperty(String name);

}
