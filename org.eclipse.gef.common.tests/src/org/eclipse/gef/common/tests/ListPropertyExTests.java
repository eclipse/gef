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
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.beans.property.SimpleListPropertyEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.tests.ObservableListTests.ListChangeExpector;
import org.eclipse.gef.common.tests.ObservableSetMultimapTests.InvalidationExpector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Provider;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@RunWith(Parameterized.class)
public class ListPropertyExTests {

	protected static class ChangeExpector<E>
			implements ChangeListener<ObservableList<E>> {

		private ObservableValue<ObservableList<E>> source;
		private LinkedList<ObservableList<E>> oldValueQueue = new LinkedList<>();
		private LinkedList<ObservableList<E>> newValueQueue = new LinkedList<>();

		public ChangeExpector(ObservableValue<ObservableList<E>> source) {
			this.source = source;
		}

		public void addExpectation(ObservableList<E> oldValue,
				ObservableList<E> newValue) {
			// We check that the reference to the observable value is correct,
			// thus do not copy the passed in values.
			oldValueQueue.addFirst(oldValue);
			newValueQueue.addFirst(newValue);
		}

		@Override
		public void changed(
				ObservableValue<? extends ObservableList<E>> observable,
				ObservableList<E> oldValue, ObservableList<E> newValue) {
			if (oldValueQueue.size() <= 0) {
				fail("Received unexpected change.");
			}
			assertEquals(source, observable);
			assertEquals(oldValueQueue.pollLast(), oldValue);
			assertEquals(newValueQueue.pollLast(), newValue);
		}

		public void check() {
			if (oldValueQueue.size() > 0) {
				fail("Did not receive " + oldValueQueue.size()
						+ " expected changes.");
			}
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] { { new Provider<ListProperty<Integer>>() {

					@Override
					public ListProperty<Integer> get() {
						return new SimpleListPropertyEx<>(FXCollections
								.observableList(new ArrayList<Integer>()));
					}
				} }, { new Provider<ListProperty<Integer>>() {

					@Override
					public ListProperty<Integer> get() {
						// Replacement for ReadOnlySetWrapper which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new ReadOnlyListWrapperEx<>(FXCollections
								.observableList(new ArrayList<Integer>()));
					}
				} } });
	}

	private Provider<ListProperty<Integer>> propertyProvider;

	public ListPropertyExTests(
			Provider<ListProperty<Integer>> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	@Test
	public void bidirectionalBinding() {
		ListProperty<Integer> property1 = propertyProvider.get();
		ListProperty<Integer> property2 = propertyProvider.get();

		// XXX: According to JavaFX contract, a bidirectional binding does not
		// lead to the properties being regarded as bound.
		property2.bindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value of first property
		ObservableList<Integer> newValue = FXCollections.observableArrayList();
		newValue.add(1);
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = FXCollections.observableArrayList();
		newValue.add(2);
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = FXCollections.observableArrayList();
		newValue.add(3);
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertNotEquals(newValue, property2.get());
		assertNotEquals(property1, property2);

		// bind on null (yields NPE)
		try {
			property2.bindBidirectional(null);
			fail("Expected NullPointerException because binding to null is not valid.");
		} catch (NullPointerException e) {
			// expected
		}

		// unbind from null (yields NPE)
		try {
			property2.unbindBidirectional(null);
			fail("Expected NullPointerException because binding to null is not valid.");
		} catch (NullPointerException e) {
			// expected
		}

		// bind on itself (yields IAE)
		try {
			property2.bindBidirectional(property2);
			fail("Expected IllegalArgumentException because binding to itself is not valid.");
		} catch (IllegalArgumentException e) {
			// expected
		}

		// unbind from itself (yields IAE)
		try {
			property2.unbindBidirectional(property2);
			fail("Expected IllegalArgumentException because binding to itself is not valid.");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void changeListenerRegistrationAndDeregistration() {
		ListProperty<Integer> property = propertyProvider.get();

		// register listener
		ChangeExpector<Integer> changeListener = null;
		changeListener = new ChangeExpector<>(property);
		property.addListener(changeListener);

		// add second listener (and remove again)
		ChangeExpector<Integer> changeListener2 = null;
		changeListener2 = new ChangeExpector<>(property);
		property.addListener(changeListener2);
		property.removeListener(changeListener2);

		ObservableList<Integer> newValue = FXCollections.observableArrayList();
		newValue.add(1);
		changeListener.addExpectation(property.get(), newValue);
		property.set(newValue);
		changeListener.check();
	}

	/**
	 * Check change notifications for observed value changes are properly fired.
	 */
	@Test
	public void changeNotifications() {
		ListProperty<Integer> property = propertyProvider.get();

		// initialize property
		property.addAll(Arrays.asList(1, 2, 3));

		// register listener
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ListChangeExpector<Integer> listChangeListener = new ListChangeExpector<>(
				property);
		ChangeExpector<Integer> changeListener = new ChangeExpector<>(property);
		property.addListener(invalidationListener);
		property.addListener(listChangeListener);
		property.addListener(changeListener);

		// change property value (disjoint values)
		ObservableList<Integer> newValue = CollectionUtils
				.observableList(Arrays.asList(4, 5, 6));
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(1, 2, 3),
				Arrays.asList(4, 5, 6), null, 0, 3);
		property.set(newValue);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// change property value (overlapping values)
		newValue = CollectionUtils.observableList(Arrays.asList(5, 6, 7));
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(4, 5, 6),
				Arrays.asList(5, 6, 7), null, 0, 3);
		property.set(newValue);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// change property value (change to null)
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), null);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(5, 6, 7),
				null, null, 0, 0);
		property.set(null);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// set to null again (no expectation)
		property.set(null);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// change property value (change from null)
		newValue = CollectionUtils.observableList(Arrays.asList(1, 2, 3));
		invalidationListener.expect(1);
		changeListener.addExpectation(null, newValue);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Arrays.asList(1, 2, 3), null, 0, 3);
		property.set(newValue);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// set to identical value (no notifications expected)
		property.set(newValue);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// set to equal value (no list change notification expected)
		newValue = CollectionUtils
				.observableList(new ArrayList<>(Arrays.asList(1, 2, 3)));
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		property.set(newValue);
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// change observed value (only invalidation and list change expected)
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2), null,
				null, 1, 1);
		property.get().removeAll(Arrays.asList(2));
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();

		// touch observed value (don't change it)
		property.get().removeAll(Arrays.asList(2));
		invalidationListener.check();
		listChangeListener.check();
		changeListener.check();
	}
}
