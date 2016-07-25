/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ForwardingSet;
import com.sun.javafx.collections.ObservableSetWrapper;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * An {@link UnmodifiableObservableSetWrapper} is an
 * {@link ObservableSetWrapper} that prevents manipulations to the backing data
 * structure by throwing {@link UnsupportedOperationException}s when any of the
 * modification methods is called.
 *
 * @author mwienand
 *
 * @param <E>
 *            Type parameter for the contained elements.
 */
// TODO: This class can be removed as soon as we drop support for JavaSE-1.7
class UnmodifiableObservableSetWrapper<E> extends ForwardingSet<E>
		implements ObservableSet<E> {

	private SetListenerHelperEx<E> helper = new SetListenerHelperEx<>(this);
	private Set<E> backingSet;

	/**
	 * Creates a new {@link UnmodifiableObservableSetWrapper} that is backed by
	 * the given {@link Set}.
	 *
	 * @param backingSet
	 *            The {@link Set} that is backing this
	 *            {@link UnmodifiableObservableSetWrapper}.
	 */
	public UnmodifiableObservableSetWrapper(Set<E> backingSet) {
		this.backingSet = backingSet;
	}

	@Override
	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper.addListener(listener);
	}

	@Override
	public void addListener(SetChangeListener<? super E> listener) {
		helper.addListener(listener);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Set<E> delegate() {
		return backingSet;
	}

	@Override
	public Iterator<E> iterator() {
		return super.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper.removeListener(listener);
	}

	@Override
	public void removeListener(SetChangeListener<? super E> listener) {
		helper.removeListener(listener);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

}
