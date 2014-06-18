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
 * An {@link IAdaptable} allows to register, unregister, and retrieve
 * (registered) adapters under a given class key. If a to be registered adapter
 * implements the {@link Bound} interface, it is expected that the
 * {@link IAdaptable} on which the adapter is registered binds the adapter to
 * itself via {@link Bound#setAdaptable(IAdaptable)} within
 * {@link #setAdapter(Object)}, and accordingly unbinds it (setAdaptable(null))
 * within {@link #unsetAdapter(Class)}.
 * 
 * @author anyssen
 *
 */
public interface IAdaptable {

	public <T> T getAdapter(Class<T> key);

	public <T> void setAdapter(Class<T> key, T adapter);

	// public void setAdapters(Map<Class<?>, Object> adaptersWithKeys);

	public <T> T unsetAdapter(Class<T> key);

	/**
	 * To be implemented by an adapter to indicate that it intends to be bounded
	 * to the respective {@link IAdaptable} it is registered at.
	 *
	 * @param <A>
	 */
	public static interface Bound<A extends IAdaptable> {
		public A getAdaptable();

		void setAdaptable(A adaptable);
	}
}
