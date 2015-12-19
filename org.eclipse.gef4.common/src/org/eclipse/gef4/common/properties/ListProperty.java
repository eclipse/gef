/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen, Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.properties;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ForwardingList;

/**
 * An {@link ListProperty} is a {@link List} that supports notification of
 * {@link PropertyChangeListener}s.
 * 
 * @author anyssen
 * @author mwienand
 *
 * @param <T>
 *            The type of the list elements.
 */
public class ListProperty<T> extends ForwardingList<T>
		implements IPropertyChangeNotifier {

	private PropertyChangeNotifierSupport pcs = null;
	private List<T> backingList = new ArrayList<>();
	private String propertyName;

	/**
	 * Creates a new {@link ListProperty} with the given propertyName.
	 * 
	 * @param sourceBean
	 *            The source bean to use in property change notifications.
	 * 
	 * @param propertyName
	 *            The propertyName of the {@link ListProperty}, which will be
	 *            used when notifying {@link PropertyChangeListener}s.
	 */
	public ListProperty(Object sourceBean, String propertyName) {
		this.pcs = new PropertyChangeNotifierSupport(sourceBean);
		this.propertyName = propertyName;
	}

	@Override
	public void add(int index, T element) {
		super.add(index, element);
		pcs.fireIndexedPropertyChange(propertyName, index, null, element);
	}

	@Override
	public boolean add(T element) {
		boolean changed = super.add(element);
		if (changed) {
			pcs.fireIndexedPropertyChange(propertyName, indexOf(element), null,
					element);
		}
		return changed;
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		List<T> oldValue = delegateCopy();
		if (super.addAll(collection)) {
			pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> elements) {
		List<T> oldValue = delegateCopy();
		if (super.addAll(index, elements)) {
			pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		List<T> oldValue = delegateCopy();
		if (!isEmpty()) {
			super.clear();
			pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
		}
	}

	@Override
	protected List<T> delegate() {
		return backingList;
	}

	/**
	 * Returns a copy of the backing list. This is used for reporting changes.
	 * 
	 * @return A copy of the backing list.
	 */
	protected List<T> delegateCopy() {
		return new ArrayList<>(backingList);
	}

	@Override
	public T remove(int index) {
		T oldValue = super.remove(index);
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, null);
		return oldValue;
	}

	@Override
	public boolean remove(Object element) {
		int index = indexOf(element);
		boolean changed = super.remove(element);
		if (changed) {
			pcs.fireIndexedPropertyChange(propertyName, index, element, null);
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		List<T> oldValue = delegateCopy();
		if (super.removeAll(collection)) {
			pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		List<T> oldValue = delegateCopy();
		if (super.retainAll(collection)) {
			pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
			return true;
		}
		return false;
	}

	@Override
	public T set(int index, T element) {
		T oldValue = super.set(index, element);
		pcs.fireIndexedPropertyChange(propertyName, index, oldValue, element);
		return oldValue;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
