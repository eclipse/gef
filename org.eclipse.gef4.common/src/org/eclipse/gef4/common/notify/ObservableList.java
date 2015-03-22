/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ForwardingList;

/**
 * An {@link ObservableList} maintains a list of {@link IListObserver observers}
 * which are notified whenever the list changes.
 * 
 * @author mwienand
 *
 * @param <T>
 *            The type of the list elements.
 */
public class ObservableList<T> extends ForwardingList<T> {

	private List<IListObserver<T>> observers = new ArrayList<IListObserver<T>>();
	private List<T> backingList = new ArrayList<T>();

	@Override
	public void add(int index, T element) {
		List<T> old = getBackingListCopy();
		super.add(index, element);
		notifyChanged(old);
	}

	@Override
	public boolean add(T element) {
		List<T> old = getBackingListCopy();
		boolean changed = super.add(element);
		if (changed) {
			notifyChanged(old);
		}
		return changed;
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		List<T> old = getBackingListCopy();
		if (super.addAll(collection)) {
			notifyChanged(old);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> elements) {
		List<T> old = getBackingListCopy();
		if (super.addAll(index, elements)) {
			notifyChanged(old);
			return true;
		}
		return false;
	}

	/**
	 * Adds the given {@link IListObserver} to the list of observers, which are
	 * notified whenever this {@link ObservableList} changes.
	 * 
	 * @param listObserver
	 *            The {@link IListObserver} to add.
	 */
	public void addListObserver(IListObserver<T> listObserver) {
		observers.add(listObserver);
	}

	@Override
	public void clear() {
		List<T> old = getBackingListCopy();
		if (!isEmpty()) {
			super.clear();
			notifyChanged(old);
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
	protected List<T> getBackingListCopy() {
		return new ArrayList<T>(backingList);
	}

	/**
	 * Notifies all observers that this list changed.
	 * 
	 * @param old
	 *            A copy of the list elements before the change.
	 */
	protected void notifyChanged(List<T> old) {
		for (IListObserver<T> observer : observers) {
			observer.afterChange(this, old);
		}
	}

	@Override
	public T remove(int index) {
		List<T> old = getBackingListCopy();
		T removed = super.remove(index);
		notifyChanged(old);
		return removed;
	}

	@Override
	public boolean remove(Object object) {
		List<T> old = getBackingListCopy();
		boolean changed = super.remove(object);
		if (changed) {
			notifyChanged(old);
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		List<T> old = getBackingListCopy();
		if (super.removeAll(collection)) {
			notifyChanged(old);
			return true;
		}
		return false;
	}

	/**
	 * Removes the given {@link IListObserver} from the list of observers, which
	 * are notfied whenever this {@link ObservableList} changes.
	 * 
	 * @param listObserver
	 *            The {@link IListObserver} to remove.
	 */
	public void removeListObserver(IListObserver<T> listObserver) {
		observers.remove(listObserver);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		List<T> old = getBackingListCopy();
		if (super.retainAll(collection)) {
			notifyChanged(old);
			return true;
		}
		return false;
	}

	@Override
	public T set(int index, T element) {
		List<T> old = getBackingListCopy();
		T previous = super.set(index, element);
		if (previous != element) {
			notifyChanged(old);
		}
		return previous;
	}

}
