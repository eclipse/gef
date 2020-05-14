/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.gef.common.beans.property.ReadOnlySetWrapperEx;
import org.eclipse.gef.common.beans.property.SimpleSetPropertyEx;
import org.eclipse.gef.common.tests.ObservableSetMultimapTests.InvalidationExpector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Provider;

import javafx.beans.property.SetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

@RunWith(Parameterized.class)
public class SetPropertyExTests {

	protected static class ChangeExpector<E>
			implements ChangeListener<ObservableSet<E>> {

		private ObservableValue<ObservableSet<E>> source;
		private LinkedList<ObservableSet<E>> oldValueQueue = new LinkedList<>();
		private LinkedList<ObservableSet<E>> newValueQueue = new LinkedList<>();

		public ChangeExpector(ObservableValue<ObservableSet<E>> source) {
			this.source = source;
		}

		public void addExpectation(ObservableSet<E> oldValue,
				ObservableSet<E> newValue) {
			// We check that the reference to the observable value is correct,
			// thus do not copy the passed in values.
			oldValueQueue.addFirst(oldValue);
			newValueQueue.addFirst(newValue);
		}

		@Override
		public void changed(
				ObservableValue<? extends ObservableSet<E>> observable,
				ObservableSet<E> oldValue, ObservableSet<E> newValue) {
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

	protected static class SetChangeExpector<E>
			implements SetChangeListener<E> {

		private ObservableSet<E> source;
		private LinkedList<E> addedElementQueue = new LinkedList<>();
		private LinkedList<E> removedElementQueue = new LinkedList<>();

		public SetChangeExpector(ObservableSet<E> source) {
			this.source = source;
		}

		public void addExpectation(E removedElement, E addedElement) {
			addedElementQueue.addFirst(addedElement);
			removedElementQueue.addFirst(removedElement);
		}

		public void check() {
			if (addedElementQueue.size() > 0) {
				fail("Did not receive " + addedElementQueue.size()
						+ " expected changes.");
			}
		}

		@Override
		public void onChanged(SetChangeListener.Change<? extends E> change) {
			if (addedElementQueue.size() <= 0) {
				fail("Received unexpected change " + change);
			}

			assertEquals(source, change.getSet());

			// check added element
			E expectedAddedElement = addedElementQueue.pollLast();
			assertEquals(expectedAddedElement, change.getElementAdded());
			if (expectedAddedElement != null) {
				assertTrue(change.wasAdded());
			} else {
				assertFalse(change.wasAdded());
			}

			// check removed values
			E expectedRemovedElement = removedElementQueue.pollLast();
			assertEquals(expectedRemovedElement, change.getElementRemoved());
			if (expectedRemovedElement != null) {
				assertTrue(change.wasRemoved());
			} else {
				assertFalse(change.wasRemoved());
			}

			// check string representation
			if (expectedRemovedElement != null) {
				assertEquals("Removed " + expectedRemovedElement + ".",
						change.toString());
			} else {
				assertEquals("Added " + expectedAddedElement + ".",
						change.toString());
			}
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] { { new Provider<SetProperty<Integer>>() {

					@Override
					public SetProperty<Integer> get() {
						return new SimpleSetPropertyEx<>(FXCollections
								.observableSet(new HashSet<Integer>()));
					}
				} }, { new Provider<SetProperty<Integer>>() {

					@Override
					public SetProperty<Integer> get() {
						// Replacement for ReadOnlySetWrapper which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new ReadOnlySetWrapperEx<>(FXCollections
								.observableSet(new HashSet<Integer>()));
					}
				} } });
	}

	private Provider<SetProperty<Integer>> propertyProvider;

	public SetPropertyExTests(Provider<SetProperty<Integer>> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	@Test
	public void bidirectionalBinding() {
		SetProperty<Integer> property1 = propertyProvider.get();
		SetProperty<Integer> property2 = propertyProvider.get();

		// XXX: According to JavaFX contract, a bidirectional binding does not
		// lead to the properties being regarded as bound.
		property2.bindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value of first property
		ObservableSet<Integer> newValue = FXCollections
				.observableSet(new HashSet<Integer>());
		newValue.add(1);
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = FXCollections.observableSet(new HashSet<Integer>());
		newValue.add(2);
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = FXCollections.observableSet(new HashSet<Integer>());
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
		SetProperty<Integer> property = propertyProvider.get();

		// register listener
		ChangeExpector<Integer> changeListener = null;
		changeListener = new ChangeExpector<>(property);
		property.addListener(changeListener);

		// add second listener (and remove again)
		ChangeExpector<Integer> changeListener2 = null;
		changeListener2 = new ChangeExpector<>(property);
		property.addListener(changeListener2);
		property.removeListener(changeListener2);

		ObservableSet<Integer> newValue = FXCollections
				.observableSet(new HashSet<Integer>());
		changeListener.addExpectation(property.get(), newValue);
		newValue.add(1);
		property.set(newValue);
		changeListener.check();
	}

	/**
	 * Check change notifications for observed value changes are properly fired.
	 */
	@Test
	public void changeNotifications() {
		SetProperty<Integer> property = propertyProvider.get();

		// initialize property
		ObservableSet<Integer> newValue = FXCollections.observableSet();
		newValue.add(1);
		newValue.add(2);
		newValue.add(3);
		property.set(newValue);

		// register listener
		InvalidationExpector invalidationListener = new InvalidationExpector();
		SetChangeExpector<Integer> setChangeListener = new SetChangeExpector<>(
				property);
		ChangeExpector<Integer> changeListener = new ChangeExpector<>(property);
		property.addListener(invalidationListener);
		property.addListener(setChangeListener);
		property.addListener(changeListener);

		// change property value (disjoint values)
		newValue = FXCollections.observableSet();
		newValue.add(4);
		newValue.add(5);
		newValue.add(6);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		setChangeListener.addExpectation(1, null);
		setChangeListener.addExpectation(2, null);
		setChangeListener.addExpectation(3, null);
		setChangeListener.addExpectation(null, 4);
		setChangeListener.addExpectation(null, 5);
		setChangeListener.addExpectation(null, 6);
		property.set(newValue);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// change property value (overlapping values)
		newValue = FXCollections.observableSet();
		newValue.add(5);
		newValue.add(6);
		newValue.add(7);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		setChangeListener.addExpectation(4, null);
		setChangeListener.addExpectation(null, 7);
		property.set(newValue);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// change property value (change to null)
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), null);
		setChangeListener.addExpectation(5, null);
		setChangeListener.addExpectation(6, null);
		setChangeListener.addExpectation(7, null);
		property.set(null);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// set to null again (no expectation)
		property.set(null);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// change property value (change from null)
		newValue = FXCollections.observableSet();
		newValue.add(1);
		newValue.add(2);
		newValue.add(3);
		invalidationListener.expect(1);
		changeListener.addExpectation(null, newValue);
		setChangeListener.addExpectation(null, 1);
		setChangeListener.addExpectation(null, 2);
		setChangeListener.addExpectation(null, 3);
		property.set(null);
		property.set(newValue);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// set to identical value (no notifications expected)
		property.set(newValue);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// set to equal value (no list change notification expected)
		newValue = FXCollections.observableSet();
		newValue.add(1);
		newValue.add(2);
		newValue.add(3);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		property.set(newValue);
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// change observed value (only invalidation and list change expected)
		// FIXME: ObservableMapWrapper fires an invalidation event for each
		// change to the map (whereas a change of the observable value leads to
		// a single notification). We could provide an own ObservableMap
		// implementation to fix this.
		invalidationListener.expect(3);
		setChangeListener.addExpectation(1, null);
		setChangeListener.addExpectation(2, null);
		setChangeListener.addExpectation(3, null);
		property.get().clear();
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();

		// touch observed value (don't change it)
		property.get().clear();
		invalidationListener.check();
		setChangeListener.check();
		changeListener.check();
	}
}
