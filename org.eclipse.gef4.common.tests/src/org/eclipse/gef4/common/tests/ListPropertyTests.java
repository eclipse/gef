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
package org.eclipse.gef4.common.tests;

import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.properties.ListProperty;
import org.junit.Test;

public class ListPropertyTests {

	private static class ExpectingListObserver<T>
			implements PropertyChangeListener {
		private List<T> expectationNew = Collections.emptyList();
		private List<T> expectationOld = Collections.emptyList();

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			assertTrue(expectationOld.equals(evt.getOldValue()));
			assertTrue(expectationNew.equals(evt.getNewValue()));
		}

		public void setExpectation(List<T> elements) {
			expectationOld = expectationNew;
			expectationNew = elements;
		}
	}

	private static class ExpectingObjectObserver<T>
			implements PropertyChangeListener {
		private Object expectationNew = null;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			assertTrue(expectationNew == null ? evt.getNewValue() == null
					: expectationNew.equals(evt.getNewValue()));
		}

		public void setExpectation(Object expectation) {
			expectationNew = expectation;
		}
	}

	@Test
	public void test_addRemove_multi() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingListObserver<Integer> obs = new ExpectingListObserver<>();
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(Arrays.asList(1, 2, 3, 4, 5, 6));
		list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));

		obs.setExpectation(Arrays.asList(2, 4, 6));
		list.removeAll(Arrays.asList(1, 3, 5));

		obs.setExpectation(Arrays.asList(2, 1, 3, 5, 4, 6));
		list.addAll(1, Arrays.asList(1, 3, 5));

		obs.setExpectation(Collections.<Integer> emptyList());
		list.clear();

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_addRemove_single() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingObjectObserver<Integer> obs = new ExpectingObjectObserver<>();
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(1);
		list.add(1);

		obs.setExpectation(null);
		list.remove(new Integer(1));

		obs.setExpectation(2);
		list.add(2);

		obs.setExpectation(3);
		list.add(3);

		obs.setExpectation(null);
		list.remove(0);

		obs.setExpectation(null);
		list.remove(0);

		obs.setExpectation(4);
		list.add(0, 4);

		obs.setExpectation(5);
		list.add(0, 5);

		obs.setExpectation(6);
		list.add(2, 6);

		obs.setExpectation(null);
		list.remove(1);

		obs.setExpectation(null);
		list.remove(new Integer(6));

		obs.setExpectation(null);
		list.remove(new Integer(5));

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_set() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingObjectObserver<Integer> obs = new ExpectingObjectObserver<>();
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(new Integer(10));
		list.add(10);

		obs.setExpectation(new Integer(20));
		list.set(0, 20);

		obs.setExpectation(null);
		list.remove(0);

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_subList() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingListObserver<Integer> obs = new ExpectingListObserver<>();
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(Arrays.asList(1, 2, 3, 4, 5));
		list.addAll(Arrays.asList(1, 2, 3, 4, 5));

		List<Integer> subList = list.subList(1, 4);
		obs.setExpectation(Arrays.asList(1, 3, 3, 4, 5));
		subList.set(0, 3);

		obs.setExpectation(Arrays.asList(1, 3, 3, 4, 4, 5));
		subList.add(4);

		obs.setExpectation(Arrays.asList(1, 5));
		subList.clear();

		obs.setExpectation(Collections.<Integer> emptyList());
		list.clear();

		assertTrue(list.isEmpty());
	}

}
