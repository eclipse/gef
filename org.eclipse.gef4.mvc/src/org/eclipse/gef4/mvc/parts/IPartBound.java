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
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;


public interface IPartBound<V> {

	/**
	 * @return the <i>host</i> EditPart on which this policy is installed.
	 */
	public abstract IVisualPart<V> getHost();

	/**
	 * Sets the host in which this EditPolicy is installed.
	 * 
	 * @param editpart
	 *            the host EditPart
	 */
	public abstract void setHost(IVisualPart<V> editpart);

}