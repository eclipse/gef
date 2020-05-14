/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.common.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class CollectionUtilsTests {

	private static List<Integer> list(Integer... integers) {
		return new ArrayList<>(Arrays.asList(integers));
	}

	private ObservableList<Integer> list;
	@SuppressWarnings("unchecked")
	private List<Integer>[] expectation = new List[1];

	@Before
	public void before_computePreviousList() {
		list = FXCollections.observableArrayList(list(10, 20, 30));
		expectation[0] = list(10, 20, 30);
		list.addListener(new ListChangeListener<Integer>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Integer> c) {
				List<? extends Integer> previousList = CollectionUtils
						.getPreviousContents(c);
				assertEquals(expectation[0], previousList);
			}
		});
	}

	@Test
	public void test_computePreviousList_add() {
		// append
		list.add(40);
		expectation[0] = list(10, 20, 30, 40);

		// prepend
		list.add(0, 5);
		expectation[0] = list(5, 10, 20, 30, 40);

		// insert
		list.add(2, 15);
		expectation[0] = list(5, 10, 15, 20, 30, 40);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_addAll() {
		// append collection
		list.addAll(list(40, 50, 60));
		expectation[0] = list(10, 20, 30, 40, 50, 60);

		// prepend collection
		list.addAll(0, list(0, 5));
		expectation[0] = list(0, 5, 10, 20, 30, 40, 50, 60);

		// insert collection
		list.addAll(3, list(15, 17));
		expectation[0] = list(0, 5, 10, 15, 17, 20, 30, 40, 50, 60);

		// append array
		list.addAll(61, 62, 63);
		expectation[0] = list(0, 5, 10, 15, 17, 20, 30, 40, 50, 60, 61, 62, 63);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_clear() {
		// clear 3 elements
		list.clear();
		expectation[0] = list();

		// clear empty list
		list.clear();
		expectation[0] = list();

		// add some values
		list.addAll(1, 2, 3, 4, 5, 6, 7);
		expectation[0] = list(1, 2, 3, 4, 5, 6, 7);

		// clear again
		list.clear();
		expectation[0] = list();

		// add value to test the last statement
		list.add(1);
	}

	@Test
	public void test_computePreviousList_remove() {
		// add some more values, so that we have things to remove
		list.addAll(40, 50, 60);
		expectation[0] = list(10, 20, 30, 40, 50, 60);

		// remove first by index
		list.remove(0);
		expectation[0] = list(20, 30, 40, 50, 60);

		// remove last by index
		list.remove(list.size() - 1);
		expectation[0] = list(20, 30, 40, 50);

		// remove middle by index
		list.remove(1);
		expectation[0] = list(20, 40, 50);

		// add some more values, so that we have things to remove
		list.addAll(60, 70, 80);
		expectation[0] = list(20, 40, 50, 60, 70, 80);

		// remove first by object
		list.remove(new Integer(20));
		expectation[0] = list(40, 50, 60, 70, 80);

		// remove last by object
		list.remove(new Integer(80));
		expectation[0] = list(40, 50, 60, 70);

		// remove middle by object
		list.remove(new Integer(60));
		expectation[0] = list(40, 50, 70);

		// add some more values, so that we have things to remove
		list.addAll(1, 2, 3, 4, 5);
		expectation[0] = list(40, 50, 70, 1, 2, 3, 4, 5);

		// remove first and second elements
		list.remove(0, 2);
		expectation[0] = list(70, 1, 2, 3, 4, 5);

		// remove last and second last elements
		list.remove(4, 6);
		expectation[0] = list(70, 1, 2, 3);

		// remove middle elements
		list.remove(1, 3);
		expectation[0] = list(70, 3);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_removeAll() {
		// add some more values, so that we have things to remove
		list.addAll(40, 50, 60);
		expectation[0] = list(10, 20, 30, 40, 50, 60);

		// remove start via collection
		list.removeAll(list(10, 20));
		expectation[0] = list(30, 40, 50, 60);

		// remove end via collection
		list.removeAll(list(50, 60));
		expectation[0] = list(30, 40);

		// remove all via collection
		list.removeAll(list(30, 40));
		expectation[0] = list();

		// add some more values, so that we have things to remove
		list.addAll(1, 2, 3, 4, 5, 6, 7, 8);
		expectation[0] = list(1, 2, 3, 4, 5, 6, 7, 8);

		// remove start via array
		list.removeAll(1, 2);
		expectation[0] = list(3, 4, 5, 6, 7, 8);

		// remove end via array
		list.removeAll(7, 8);
		expectation[0] = list(3, 4, 5, 6);

		// remove all via array
		list.removeAll(3, 4, 5, 6);
		expectation[0] = list();

		// add some more values, so that we have things to remove
		list.addAll(1, 2, 3, 4, 5, 6, 7, 8);
		expectation[0] = list(1, 2, 3, 4, 5, 6, 7, 8);

		// remove with gaps via collection
		list.removeAll(list(1, 3, 4, 6, 8));
		expectation[0] = list(2, 5, 7);

		// add some more values, so that we have things to remove
		list.addAll(8, 9, 10, 11, 12);
		expectation[0] = list(2, 5, 7, 8, 9, 10, 11, 12);

		// remove with gaps via array
		list.removeAll(2, 5, 8, 11, 12);
		expectation[0] = list(7, 9, 10);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_retainAll() {
		// add more values
		list.addAll(40, 50, 60);
		expectation[0] = list(10, 20, 30, 40, 50, 60);

		// retain even numbers via collection with non-existing elements
		list.retainAll(list(0, 20, 40, 60, 100));
		expectation[0] = list(20, 40, 60);

		// retain first element only via collection with non-existing elements
		list.retainAll(5, 10, 15, 20);
		expectation[0] = list(20);

		// add more values
		list.addAll(30, 31, 32, 33, 34, 35, 36);
		expectation[0] = list(20, 30, 31, 32, 33, 34, 35, 36);

		// retain odd numbers via collection with existing elements only
		list.retainAll(list(31, 33, 35));
		expectation[0] = list(31, 33, 35);

		// add more values
		list.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9);
		expectation[0] = list(31, 33, 35, 1, 2, 3, 4, 5, 6, 7, 8, 9);

		// retain elements via array with non-existing elements
		list.retainAll(list(30, 32, 34, 35, 1, 2, 3, 4, 5, 6, 7, 8, 9, 60, 70));
		expectation[0] = list(35, 1, 2, 3, 4, 5, 6, 7, 8, 9);

		// retain all but the first element via array with non-existing elements
		list.retainAll(list(1, 2, 3, 4, 5, 6, 7, 8, 9));
		expectation[0] = list(1, 2, 3, 4, 5, 6, 7, 8, 9);

		// retain elements via array with existing elements only
		list.retainAll(list(4, 5, 6));
		expectation[0] = list(4, 5, 6);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_set() {
		// set start
		list.set(0, 15);
		expectation[0] = list(15, 20, 30);

		// set end
		list.set(2, 35);
		expectation[0] = list(15, 20, 35);

		// set middle
		list.set(1, 25);
		expectation[0] = list(15, 25, 35);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_setAll() {
		// set all array, clear
		list.setAll();
		expectation[0] = list();

		// set all array, add
		list.setAll(1, 2, 3);
		expectation[0] = list(1, 2, 3);

		// set all array, set
		list.setAll(4, 5, 6);
		expectation[0] = list(4, 5, 6);

		// set all collection, clear
		list.setAll(Collections.<Integer> emptyList());
		expectation[0] = list();

		// set all collection, add
		list.setAll(list(1, 2, 3));
		expectation[0] = list(1, 2, 3);

		// set all collection, set
		list.setAll(list(4, 5, 6));
		expectation[0] = list(4, 5, 6);

		// clear list to test the last statement
		list.clear();
	}

	@Test
	public void test_computePreviousList_sort() {
		// fill with unsorted values
		list.setAll(1, 9, 2, 8, 3, 7, 4, 6, 5);
		expectation[0] = list(1, 9, 2, 8, 3, 7, 4, 6, 5);

		// sort ascending
		FXCollections.sort(list);
		expectation[0] = list(1, 2, 3, 4, 5, 6, 7, 8, 9);

		// fill with unsorted values
		list.setAll(1, 9, 2, 8, 3, 7, 4, 6, 5);
		expectation[0] = list(1, 9, 2, 8, 3, 7, 4, 6, 5);

		// TODO sort descending
		FXCollections.sort(list, new Comparator<Integer>() {
			@Override
			public int compare(Integer lhs, Integer rhs) {
				return lhs.compareTo(rhs);
			}
		});
		expectation[0] = list(9, 8, 7, 6, 5, 4, 3, 2, 1);
	}

}
