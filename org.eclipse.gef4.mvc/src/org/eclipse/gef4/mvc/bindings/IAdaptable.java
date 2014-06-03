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
package org.eclipse.gef4.mvc.bindings;

/**
 *
 * @author anyssen
 *
 */
public interface IAdaptable {
	
	public <T> T getAdapter(Class<T> key);
	
	public <T> void setAdapter(T adapter);
	
	public <T> void setAdapter(Class<T> key, T adapter);

	public <T> T unsetAdapter(Class<T> key);
	
	public static interface Bound<A extends IAdaptable> {
		public A getAdaptable();
		void setAdaptable(A adaptable);
	}
}
