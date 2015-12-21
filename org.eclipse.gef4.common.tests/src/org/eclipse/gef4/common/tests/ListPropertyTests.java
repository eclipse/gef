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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.properties.ListProperty;
import org.junit.Test;

public class ListPropertyTests {

	private static class ExpectingIndexedObjectObserver
			extends ExpectingListObserver {

		private int index;

		public ExpectingIndexedObjectObserver(String propertyName) {
			super(propertyName);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			assertTrue(evt instanceof IndexedPropertyChangeEvent);
			assertEquals(index, ((IndexedPropertyChangeEvent) evt).getIndex());
		}

		public void setExpectation(int index, Object oldValue,
				Object newValue) {
			super.setExpectation(oldValue, newValue);
			this.index = index;
		}
	}

	private static class ExpectingListObserver
			implements PropertyChangeListener {

		private String propertyName;
		private Object newValue;
		private Object oldValue;

		public ExpectingListObserver(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			assertEquals(propertyName, evt.getPropertyName());
			assertEquals(oldValue, evt.getOldValue());
			assertEquals(newValue, evt.getNewValue());
		}

		public void setExpectation(Object oldValue, Object newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}

	@Test
	public void test_addRemove_multi() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingListObserver obs = new ExpectingListObserver("test");
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(Collections.emptyList(),
				Arrays.asList(1, 2, 3, 4, 5, 6));
		list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));

		obs.setExpectation(Arrays.asList(1, 2, 3, 4, 5, 6),
				Arrays.asList(2, 4, 6));
		list.removeAll(Arrays.asList(1, 3, 5));

		obs.setExpectation(Arrays.asList(2, 4, 6),
				Arrays.asList(2, 1, 3, 5, 4, 6));
		list.addAll(1, Arrays.asList(1, 3, 5));

		obs.setExpectation(Arrays.asList(2, 1, 3, 5, 4, 6),
				Collections.<Integer> emptyList());
		list.clear();

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_addRemove_single() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingIndexedObjectObserver obs = new ExpectingIndexedObjectObserver(
				"test");
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(0, null, 1);
		list.add(1);

		obs.setExpectation(0, 1, null);
		list.remove(new Integer(1));

		obs.setExpectation(0, null, 2);
		list.add(2);

		obs.setExpectation(1, null, 3);
		list.add(3);

		obs.setExpectation(0, 2, null);
		list.remove(0);

		obs.setExpectation(0, 3, null);
		list.remove(0);

		obs.setExpectation(0, null, 4);
		list.add(0, 4);

		obs.setExpectation(0, null, 5);
		list.add(0, 5);

		obs.setExpectation(2, null, 6);
		list.add(2, 6);

		obs.setExpectation(1, 4, null);
		list.remove(1);

		obs.setExpectation(1, 6, null);
		list.remove(new Integer(6));

		obs.setExpectation(0, 5, null);
		list.remove(new Integer(5));

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_set() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingIndexedObjectObserver obs = new ExpectingIndexedObjectObserver(
				"test");
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(0, null, 10);
		list.add(10);

		obs.setExpectation(0, 10, 20);
		list.set(0, 20);

		obs.setExpectation(0, 20, null);
		list.remove(0);

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_subList() {
		ListProperty<Integer> list = new ListProperty<>(new Object(), "test");
		ExpectingListObserver obs = new ExpectingListObserver("test");
		list.addPropertyChangeListener(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(Collections.emptyList(),
				Arrays.asList(1, 2, 3, 4, 5));
		list.addAll(Arrays.asList(1, 2, 3, 4, 5));

		List<Integer> subList = list.subList(1, 4);
		obs.setExpectation(Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(1, 3, 3, 4, 5));
		subList.set(0, 3);

		obs.setExpectation(Arrays.asList(1, 3, 3, 4, 5),
				Arrays.asList(1, 3, 3, 4, 4, 5));
		subList.add(4);

		obs.setExpectation(Arrays.asList(1, 3, 3, 4, 4, 5),
				Arrays.asList(1, 5));
		subList.clear();

		obs.setExpectation(Arrays.asList(1, 5),
				Collections.<Integer> emptyList());
		list.clear();

		assertTrue(list.isEmpty());
	}

}
