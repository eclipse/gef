/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.adapt;

import com.google.inject.Provider;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Abstract base class of providers being bound to an
 * {@link org.eclipse.gef4.common.adapt.IAdaptable.Bound}.
 *
 * @author anyssen
 *
 * @param <T>
 *            The type provided by this {@link Provider}.
 * @param <A>
 *            The adaptable type this adapter is bound to as adapter.
 */
public abstract class AbstractBoundProvider<T, A extends IAdaptable>
		implements Provider<T>, IAdaptable.Bound<A> {

	private ReadOnlyObjectWrapper<A> adaptableProperty = new ReadOnlyObjectWrapper<>();

	@Override
	public ReadOnlyObjectProperty<A> adaptableProperty() {
		return adaptableProperty.getReadOnlyProperty();
	}

	@Override
	public A getAdaptable() {
		return adaptableProperty.get();
	}

	@Override
	public void setAdaptable(A adaptable) {
		adaptableProperty.set(adaptable);
	}

}
