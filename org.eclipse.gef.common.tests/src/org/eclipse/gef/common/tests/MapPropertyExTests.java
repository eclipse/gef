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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.common.beans.property.SimpleMapPropertyEx;
import org.eclipse.gef.common.tests.ObservableSetMultimapTests.InvalidationExpector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Provider;

import javafx.beans.property.MapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

@RunWith(Parameterized.class)
public class MapPropertyExTests {

	protected static class ChangeExpector<K, V>
			implements ChangeListener<ObservableMap<K, V>> {

		private ObservableValue<ObservableMap<K, V>> source;
		private LinkedList<ObservableMap<K, V>> oldValueQueue = new LinkedList<>();
		private LinkedList<ObservableMap<K, V>> newValueQueue = new LinkedList<>();

		public ChangeExpector(ObservableValue<ObservableMap<K, V>> source) {
			this.source = source;
		}

		public void addExpectation(ObservableMap<K, V> oldValue,
				ObservableMap<K, V> newValue) {
			// We check that the reference to the observable value is correct,
			// thus do not copy the passed in values.
			oldValueQueue.addFirst(oldValue);
			newValueQueue.addFirst(newValue);
		}

		@Override
		public void changed(
				ObservableValue<? extends ObservableMap<K, V>> observable,
				ObservableMap<K, V> oldValue, ObservableMap<K, V> newValue) {
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

	protected static class MapChangeExpector<K, V>
			implements MapChangeListener<K, V> {

		private ObservableMap<K, V> source;
		private LinkedList<K> keyQueue = new LinkedList<>();
		private LinkedList<V> addedValueQueue = new LinkedList<>();
		private LinkedList<V> removedValueQueue = new LinkedList<>();

		public MapChangeExpector(ObservableMap<K, V> source) {
			this.source = source;
		}

		public void addExpectation(K key, V removedValue, V addedValue) {
			keyQueue.addFirst(key);
			addedValueQueue.addFirst(addedValue);
			removedValueQueue.addFirst(removedValue);
		}

		public void check() {
			if (keyQueue.size() > 0) {
				fail("Did not receive " + keyQueue.size()
						+ " expected changes.");
			}
		}

		@Override
		public void onChanged(
				MapChangeListener.Change<? extends K, ? extends V> change) {
			if (keyQueue.size() <= 0) {
				fail("Received unexpected change " + change);
			}

			assertEquals(source, change.getMap());

			// check key
			K expectedKey = keyQueue.pollLast();
			assertEquals(expectedKey, change.getKey());

			// check added values
			V expectedAddedValue = addedValueQueue.pollLast();
			assertEquals(expectedAddedValue, change.getValueAdded());
			if (expectedAddedValue != null) {
				assertTrue(change.wasAdded());
			} else {
				assertFalse(change.wasAdded());
			}

			// check removed values
			V expectedRemovedValue = removedValueQueue.pollLast();
			assertEquals(expectedRemovedValue, change.getValueRemoved());
			if (expectedRemovedValue != null) {
				assertTrue(change.wasRemoved());
			} else {
				assertFalse(change.wasRemoved());
			}

			// check string representation
			if (expectedAddedValue == null && expectedRemovedValue != null) {
				assertEquals("Removed " + expectedRemovedValue + " for key "
						+ expectedKey + ".", change.toString());
			} else if (expectedAddedValue != null
					&& expectedRemovedValue == null) {
				assertEquals("Added " + expectedAddedValue + " for key "
						+ expectedKey + ".", change.toString());
			} else {
				assertEquals("Replaced " + expectedRemovedValue + " by "
						+ expectedAddedValue + " for key " + expectedKey + ".",
						change.toString());
			}
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ new Provider<MapProperty<Integer, String>>() {

					@Override
					public MapProperty<Integer, String> get() {
						// Replacement for SimpleMapProperty which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new SimpleMapPropertyEx<>(FXCollections
								.observableMap(new HashMap<Integer, String>()));
					}
				} }, { new Provider<MapProperty<Integer, String>>() {

					@Override
					public MapProperty<Integer, String> get() {
						// Replacement for ReadOnlyMapWrapper which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new ReadOnlyMapWrapperEx<>(FXCollections
								.observableMap(new HashMap<Integer, String>()));
					}
				} } });
	}

	private Provider<MapProperty<Integer, String>> propertyProvider;

	public MapPropertyExTests(
			Provider<MapProperty<Integer, String>> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	@Test
	public void bidirectionalBinding() {
		MapProperty<Integer, String> property1 = propertyProvider.get();
		MapProperty<Integer, String> property2 = propertyProvider.get();

		// XXX: According to JavaFX contract, a bidirectional binding does not
		// lead to the properties being regarded as bound.
		property2.bindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value of first property
		ObservableMap<Integer, String> newValue = FXCollections
				.observableMap(new HashMap<Integer, String>());
		newValue.put(1, "1-1");
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = FXCollections.observableMap(new HashMap<Integer, String>());
		newValue.put(2, "2-1");
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = FXCollections.observableMap(new HashMap<Integer, String>());
		newValue.put(3, "3-1");
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

	// TODO:change notifications

	@Test
	public void bidirectionalContentBinding() {
		MapProperty<Integer, String> property1 = propertyProvider.get();
		MapProperty<Integer, String> property2 = propertyProvider.get();

		// XXX: According to JavaFX contract, a bidirectional binding does not
		// lead to the properties being regarded as bound.
		property2.bindContentBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value of first property
		ObservableMap<Integer, String> newValue = FXCollections
				.observableMap(new HashMap<Integer, String>());
		newValue.put(1, "1-1");
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = FXCollections.observableMap(new HashMap<Integer, String>());
		newValue.put(2, "2-1");
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindContentBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = FXCollections.observableMap(new HashMap<Integer, String>());
		newValue.put(3, "3-1");
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
		MapProperty<Integer, String> property = propertyProvider.get();

		// register listener
		ChangeExpector<Integer, String> changeListener = null;
		changeListener = new ChangeExpector<>(property);
		property.addListener(changeListener);

		// add second listener (and remove again)
		ChangeExpector<Integer, String> changeListener2 = null;
		changeListener2 = new ChangeExpector<>(property);
		property.addListener(changeListener2);
		property.removeListener(changeListener2);

		ObservableMap<Integer, String> newValue = FXCollections
				.observableMap(new HashMap<Integer, String>());
		changeListener.addExpectation(property.get(), newValue);
		newValue.put(1, "1");
		property.set(newValue);
		changeListener.check();
	}

	/**
	 * Check change notifications for observed value changes are properly fired.
	 */
	@Test
	public void changeNotifications() {
		MapProperty<Integer, String> property = propertyProvider.get();

		// initialize property
		ObservableMap<Integer, String> newValue = FXCollections
				.observableHashMap();
		newValue.put(1, "1");
		newValue.put(2, "2");
		newValue.put(3, "3");
		property.set(newValue);

		// register listener
		InvalidationExpector invalidationListener = new InvalidationExpector();
		MapChangeExpector<Integer, String> mapChangeListener = new MapChangeExpector<>(
				property);
		ChangeExpector<Integer, String> changeListener = new ChangeExpector<>(
				property);
		property.addListener(invalidationListener);
		property.addListener(mapChangeListener);
		property.addListener(changeListener);

		// change property value (disjoint values)
		newValue = FXCollections.observableHashMap();
		newValue.put(4, "4");
		newValue.put(5, "5");
		newValue.put(6, "6");
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		mapChangeListener.addExpectation(1, "1", null);
		mapChangeListener.addExpectation(2, "2", null);
		mapChangeListener.addExpectation(3, "3", null);
		mapChangeListener.addExpectation(4, null, "4");
		mapChangeListener.addExpectation(5, null, "5");
		mapChangeListener.addExpectation(6, null, "6");
		property.set(newValue);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// change property value (overlapping values)
		newValue = FXCollections.observableHashMap();
		newValue.put(5, "55");
		newValue.put(6, "6");
		newValue.put(7, "7");
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		mapChangeListener.addExpectation(4, "4", null);
		mapChangeListener.addExpectation(5, "5", "55");
		mapChangeListener.addExpectation(7, null, "7");
		property.set(newValue);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// change property value (change to null)
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), null);
		mapChangeListener.addExpectation(5, "55", null);
		mapChangeListener.addExpectation(6, "6", null);
		mapChangeListener.addExpectation(7, "7", null);
		property.set(null);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// set to null again (no expectation)
		property.set(null);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// change property value (change from null)
		newValue = FXCollections.observableHashMap();
		newValue.put(1, "1");
		newValue.put(2, "2");
		newValue.put(3, "3");
		invalidationListener.expect(1);
		changeListener.addExpectation(null, newValue);
		mapChangeListener.addExpectation(1, null, "1");
		mapChangeListener.addExpectation(2, null, "2");
		mapChangeListener.addExpectation(3, null, "3");
		property.set(null);
		property.set(newValue);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// set to identical value (no notifications expected)
		property.set(newValue);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// set to equal value (no list change notification expected)
		newValue = FXCollections.observableHashMap();
		newValue.put(1, "1");
		newValue.put(2, "2");
		newValue.put(3, "3");
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		property.set(newValue);
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// change observed value (only invalidation and list change expected)
		// FIXME: ObservableMapWrapper fires an invalidation event for each
		// change to the map (whereas a change of the observable value leads to
		// a single notification). We could provide an own ObservableMap
		// implementation to fix this.
		invalidationListener.expect(3);
		mapChangeListener.addExpectation(1, "1", null);
		mapChangeListener.addExpectation(2, "2", null);
		mapChangeListener.addExpectation(3, "3", null);
		property.get().clear();
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();

		// touch observed value (don't change it)
		property.get().clear();
		invalidationListener.check();
		mapChangeListener.check();
		changeListener.check();
	}
}
