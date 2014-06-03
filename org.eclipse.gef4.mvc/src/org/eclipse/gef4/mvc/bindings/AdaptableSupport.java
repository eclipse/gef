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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.IActivatable;

/**
 * 
 * @author anyssen
 *
 * @param <A>
 *            The type of sourceAdaptable supported by this class. If passed-in
 *            adapters implement the {@link IAdaptable.Bound} interface, the
 *            generic type parameter of {@link IAdaptable.Bound} has to match
 *            this one.
 */
public class AdaptableSupport<A extends IAdaptable> {

	private A sourceAdaptable;
	private Map<Class<?>, Object> adapters;

	public AdaptableSupport(A sourceAdaptable) {
		this.sourceAdaptable = sourceAdaptable;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> key) {
		if (adapters == null) {
			return null;
		}
		return (T) adapters.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> void setAdapter(T adapter) {
		setAdapter((Class<T>) adapter.getClass(), adapter);
	}

	@SuppressWarnings("unchecked")
	public <T> void setAdapter(Class<T> key, T adapter) {
		if (adapters == null) {
			adapters = new HashMap<Class<?>, Object>();
		}
		adapters.put(key, adapter);
		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(sourceAdaptable);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T unsetAdapter(Class<T> key) {
		if (adapters == null)
			return null;
		Object adapter = adapters.remove(key);
		if (adapter != null) {
			if (adapter instanceof IActivatable) {
				((IActivatable) adapter).deactivate();
			}
			if (adapter instanceof IAdaptable.Bound) {
				((IAdaptable.Bound<A>) adapter).setAdaptable(null);
			}
		}
		if (adapters.size() == 0) {
			adapters = null;
		}
		return (T) adapter;
	}

	public Map<Class<?>, Object> getAdapters() {
		if (adapters == null) {
			return Collections.emptyMap();
		}
		return adapters;
	}

}
