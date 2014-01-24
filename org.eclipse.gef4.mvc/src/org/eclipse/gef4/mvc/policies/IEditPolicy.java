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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPolicy.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public interface IEditPolicy<V> extends IActivatable {

	/**
	 * @return the <i>host</i> EditPart on which this policy is installed.
	 */
	IVisualPart<V> getHost();

	/**
	 * Sets the host in which this EditPolicy is installed.
	 * 
	 * @param editpart
	 *            the host EditPart
	 */
	void setHost(IVisualPart<V> editpart);

}
