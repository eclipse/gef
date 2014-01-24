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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditDomain.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public abstract class AbstractSelectionPolicy<V> extends AbstractEditPolicy<V> {

	public abstract void selectPrimary();

	public abstract void selectSecondary();

	public abstract void deselect();
	
}
