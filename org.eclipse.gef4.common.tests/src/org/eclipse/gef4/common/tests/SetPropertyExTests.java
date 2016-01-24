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
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.gef4.common.beans.property.ReadOnlySetWrapperEx;
import org.eclipse.gef4.common.beans.property.SimpleSetPropertyEx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Provider;
import com.sun.javafx.collections.ObservableSetWrapper;

import javafx.beans.property.SetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;

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

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] { { new Provider<SetProperty<Integer>>() {

					@Override
					public SetProperty<Integer> get() {
						return new SimpleSetPropertyEx<>(
								new ObservableSetWrapper<>(
										new HashSet<Integer>()));
					}
				} }, { new Provider<SetProperty<Integer>>() {

					@Override
					public SetProperty<Integer> get() {
						// Replacement for ReadOnlySetWrapper which fixes
						// https://bugs.openjdk.java.net/browse/JDK-8136465)
						return new ReadOnlySetWrapperEx<>(
								new ObservableSetWrapper<>(
										new HashSet<Integer>()));
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
		ObservableSet<Integer> newValue = new ObservableSetWrapper<>(
				new HashSet<Integer>());
		newValue.add(1);
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = new ObservableSetWrapper<>(new HashSet<Integer>());
		newValue.add(2);
		property2.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// unbind (ensure values are no longer synchronized)
		property2.unbindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = new ObservableSetWrapper<>(new HashSet<Integer>());
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

		ObservableSet<Integer> newValue = new ObservableSetWrapper<>(
				new HashSet<Integer>());
		changeListener.addExpectation(property.get(), newValue);
		newValue.add(1);
		property.set(newValue);
		changeListener.check();
	}
}
