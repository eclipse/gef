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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.notify.IListObserver;
import org.eclipse.gef4.common.notify.ObservableList;
import org.junit.Test;

public class ObservableListTests {

	private static class ExpectingListObserver<T> implements IListObserver<T> {
		private List<T> expectationNew = Collections.emptyList();
		private List<T> expectationOld = Collections.emptyList();

		@Override
		public void afterChange(ObservableList<T> observableList,
				List<T> previousList) {
			assertTrue(expectationOld.equals(previousList));
			assertTrue(expectationNew.equals(observableList));
		}

		public void setExpectation(List<T> elements) {
			expectationOld = expectationNew;
			expectationNew = elements;
		}
	}

	@Test
	public void test_addRemove_multi() {
		ObservableList<Integer> list = new ObservableList<Integer>();
		ExpectingListObserver<Integer> obs = new ExpectingListObserver<Integer>();
		list.addListObserver(obs);

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
		ObservableList<Integer> list = new ObservableList<Integer>();
		ExpectingListObserver<Integer> obs = new ExpectingListObserver<Integer>();
		list.addListObserver(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(Arrays.asList(1));
		list.add(1);

		obs.setExpectation(Collections.<Integer> emptyList());
		list.remove(new Integer(1));

		obs.setExpectation(Arrays.asList(2));
		list.add(2);

		obs.setExpectation(Arrays.asList(2, 3));
		list.add(3);

		obs.setExpectation(Arrays.asList(3));
		list.remove(0);

		obs.setExpectation(Collections.<Integer> emptyList());
		list.remove(0);

		obs.setExpectation(Arrays.asList(4));
		list.add(0, 4);

		obs.setExpectation(Arrays.asList(5, 4));
		list.add(0, 5);

		obs.setExpectation(Arrays.asList(5, 4, 6));
		list.add(2, 6);

		obs.setExpectation(Arrays.asList(5, 6));
		list.remove(1);

		obs.setExpectation(Arrays.asList(5));
		list.remove(new Integer(6));

		obs.setExpectation(Collections.<Integer> emptyList());
		list.remove(new Integer(5));

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_set() {
		ObservableList<Integer> list = new ObservableList<Integer>();
		ExpectingListObserver<Integer> obs = new ExpectingListObserver<Integer>();
		list.addListObserver(obs);

		assertTrue(list.isEmpty());

		obs.setExpectation(Arrays.asList(10));
		list.add(10);

		obs.setExpectation(Arrays.asList(20));
		list.set(0, 20);

		obs.setExpectation(Collections.<Integer> emptyList());
		list.remove(0);

		assertTrue(list.isEmpty());
	}

	@Test
	public void test_subList() {
		ObservableList<Integer> list = new ObservableList<Integer>();
		ExpectingListObserver<Integer> obs = new ExpectingListObserver<Integer>();
		list.addListObserver(obs);

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
