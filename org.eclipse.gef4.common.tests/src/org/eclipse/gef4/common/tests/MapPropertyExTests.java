/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.common.beans.property.SimpleMapPropertyEx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Provider;
import com.sun.javafx.collections.ObservableMapWrapper;

import javafx.beans.property.MapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ new Provider<MapProperty<Integer, String>>() {

					@Override
					public MapProperty<Integer, String> get() {
						// Replacement for SimpleMapProperty which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new SimpleMapPropertyEx<>(
								new ObservableMapWrapper<>(
										new HashMap<Integer, String>()));
					}
				} }, { new Provider<MapProperty<Integer, String>>() {

					@Override
					public MapProperty<Integer, String> get() {
						// Replacement for ReadOnlyMapWrapper which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new ReadOnlyMapWrapperEx<>(
								new ObservableMapWrapper<>(
										new HashMap<Integer, String>()));
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
		ObservableMap<Integer, String> newValue = new ObservableMapWrapper<>(
				new HashMap<Integer, String>());
		newValue.put(1, "1-1");
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = new ObservableMapWrapper<>(new HashMap<Integer, String>());
		newValue.put(2, "2-1");
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = new ObservableMapWrapper<>(new HashMap<Integer, String>());
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
	public void bidirectionalContentBinding() {
		MapProperty<Integer, String> property1 = propertyProvider.get();
		MapProperty<Integer, String> property2 = propertyProvider.get();

		// XXX: According to JavaFX contract, a bidirectional binding does not
		// lead to the properties being regarded as bound.
		property2.bindContentBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value of first property
		ObservableMap<Integer, String> newValue = new ObservableMapWrapper<>(
				new HashMap<Integer, String>());
		newValue.put(1, "1-1");
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = new ObservableMapWrapper<>(new HashMap<Integer, String>());
		newValue.put(2, "2-1");
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindContentBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = new ObservableMapWrapper<>(new HashMap<Integer, String>());
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

		ObservableMap<Integer, String> newValue = new ObservableMapWrapper<>(
				new HashMap<Integer, String>());
		changeListener.addExpectation(property.get(), newValue);
		newValue.put(1, "1");
		property.set(newValue);
		changeListener.check();
	}
}
