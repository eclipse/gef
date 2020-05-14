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
import java.util.LinkedList;

import org.eclipse.gef.common.beans.property.MultisetProperty;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetProperty;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetWrapper;
import org.eclipse.gef.common.beans.property.SimpleMultisetProperty;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.common.tests.ObservableMultisetTests.InvalidationExpector;
import org.eclipse.gef.common.tests.ObservableMultisetTests.MultisetChangeExpector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Provider;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

@RunWith(Parameterized.class)
public class MultisetPropertyTests {

	protected static class ChangeExpector<E>
			implements ChangeListener<ObservableMultiset<E>> {

		private ObservableValue<ObservableMultiset<E>> source;
		private LinkedList<ObservableMultiset<E>> oldValueQueue = new LinkedList<>();
		private LinkedList<ObservableMultiset<E>> newValueQueue = new LinkedList<>();

		public ChangeExpector(ObservableValue<ObservableMultiset<E>> source) {
			this.source = source;
		}

		public void addExpectation(ObservableMultiset<E> oldValue,
				ObservableMultiset<E> newValue) {
			// We check that the reference to the observable value is correct,
			// thus do not copy the passed in values.
			oldValueQueue.addFirst(oldValue);
			newValueQueue.addFirst(newValue);
		}

		@Override
		public void changed(
				ObservableValue<? extends ObservableMultiset<E>> observable,
				ObservableMultiset<E> oldValue,
				ObservableMultiset<E> newValue) {
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
				new Object[][] { { new Provider<MultisetProperty<Integer>>() {

					@Override
					public MultisetProperty<Integer> get() {
						// test SimpleMultisetProperty, which is the
						// 'default' implementation of the related
						// ObservableValue.
						return new SimpleMultisetProperty<>(CollectionUtils
								.<Integer> observableHashMultiset());
					}
				} }, { new Provider<MultisetProperty<Integer>>() {

					@Override
					public MultisetProperty<Integer> get() {
						// test ReadOnlyMultisetWrapper, which is the
						// 'default' implementation of the related
						// read-only property support.
						return new ReadOnlyMultisetWrapper<>(CollectionUtils
								.<Integer> observableHashMultiset());
					}
				} } });
	}

	private Provider<MultisetProperty<Integer>> propertyProvider;

	public MultisetPropertyTests(
			Provider<MultisetProperty<Integer>> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	@Test
	public void bidirectionalBinding() {
		MultisetProperty<Integer> property1 = propertyProvider.get();
		MultisetProperty<Integer> property2 = propertyProvider.get();

		// XXX: According to JavaFX contract, a bidirectional binding does not
		// lead to the properties being regarded as bound.
		property2.bindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value of first property
		ObservableMultiset<Integer> newValue = CollectionUtils
				.observableHashMultiset();
		newValue.add(1, 1);
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// change value of second property
		newValue = CollectionUtils.observableHashMultiset();
		newValue.add(2, 2);
		property2.set(newValue);
		assertEquals(property1, property2);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());

		// unbind (ensure values are no longer synchronized)
		property2.unbindBidirectional(property1);
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());
		newValue = CollectionUtils.observableHashMultiset();
		newValue.add(3, 3);
		property1.set(newValue);
		assertNotEquals(property1, property2);
		assertEquals(newValue, property1.get());
		assertNotEquals(newValue, property2.get());

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
			// exptected
		}

		// unbind from itself (yields IAE)
		try {
			property2.unbindBidirectional(property2);
			fail("Expected IllegalArgumentException because binding to itself is not valid.");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	/**
	 * Test the bidirectional content bindings as offered by
	 * {@link ReadOnlyMultisetProperty}.
	 */
	@Test
	public void bidirectionalContentBinding() {
		MultisetProperty<Integer> property1 = propertyProvider.get();
		MultisetProperty<Integer> property2 = propertyProvider.get();
		ObservableMultiset<Integer> backupMap = CollectionUtils
				.observableHashMultiset();

		property1.bindContentBidirectional(property2);
		// XXX: According to JavaFX contract, a content binding does not lead to
		// the properties being regarded as being bound
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// add
		property1.add(1);
		backupMap.add(1);
		check(property1, property2, backupMap);

		// add_count
		property1.add(1, 2);
		backupMap.add(1, 2);
		check(property1, property2, backupMap);

		// addAll
		Multiset<Integer> toAdd = HashMultiset.create();
		toAdd.add(2, 2);
		toAdd.add(3, 3);
		property1.addAll(toAdd);
		backupMap.addAll(toAdd);
		check(property1, property2, backupMap);

		// remove
		property1.remove(1);
		backupMap.remove(1);
		check(property1, property2, backupMap);

		// remove with count
		property1.remove(2, 2);
		backupMap.remove(2, 2);
		check(property1, property2, backupMap);

		// set count
		property1.setCount(3, 6);
		backupMap.setCount(3, 6);
		check(property1, property2, backupMap);

		// set count with old
		property1.setCount(3, 6, 3);
		backupMap.setCount(3, 6, 3);
		check(property1, property2, backupMap);

		// clear
		property1.clear();
		backupMap.clear();
		check(property1, property2, backupMap);

		// unbind property2, ensure values are no longer synchronized
		property2.unbindContentBidirectional(property1);
		property1.add(1, 4);
		assertNotEquals(property2.get(), property1.get());
		assertNotEquals(property2.sizeProperty().get(),
				property1.sizeProperty().get());
		assertNotEquals(property2.emptyProperty().get(),
				property1.emptyProperty().get());

		// unbind property2 from null (yields NPE)
		try {
			property2.unbindContentBidirectional(null);
			fail("Expected NullPointerException.");
		} catch (NullPointerException e) {
			assertEquals("Cannot bind to null value.", e.getMessage());
		}

		// unbind property2 from itself (yields IAE)
		try {
			property2.unbindContentBidirectional(property2);
			fail("Expected IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot bind source to itself.", e.getMessage());
		}

		// bind property2 to null (yields NPE)
		try {
			property2.bindContentBidirectional(null);
			fail("Expected NullPointerException.");
		} catch (NullPointerException e) {
			assertEquals("Cannot bind to null value.", e.getMessage());
		}

		// bind property2 to itself (yields IAE)
		try {
			property2.bindContentBidirectional(property2);
			fail("Expected IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot bind source to itself.", e.getMessage());
		}
	}

	/**
	 * Check change notifications for observed value changes are properly fired.
	 */
	@Test
	public void changeNotifications() {
		MultisetProperty<Integer> property = propertyProvider.get();

		// initialize property
		property.add(1, 1);
		property.add(2, 2);

		// register listener
		InvalidationExpector invalidationListener = new InvalidationExpector();
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				property);
		ChangeExpector<Integer> changeListener = new ChangeExpector<>(property);
		property.addListener(invalidationListener);
		property.addListener(multisetChangeListener);
		property.addListener(changeListener);

		// change property value (disjoint values)
		ObservableMultiset<Integer> newValue = CollectionUtils
				.observableHashMultiset();
		newValue.add(3, 3);
		newValue.add(4, 4);
		newValue.add(5, 5);
		newValue.add(6, 6);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 1, 0);
		multisetChangeListener.addElementaryExpection(2, 2, 0);
		multisetChangeListener.addElementaryExpection(3, 0, 3);
		multisetChangeListener.addElementaryExpection(4, 0, 4);
		multisetChangeListener.addElementaryExpection(5, 0, 5);
		multisetChangeListener.addElementaryExpection(6, 0, 6);
		property.set(newValue);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// change property value (non-disjoint values)
		newValue = CollectionUtils.observableHashMultiset();
		newValue.add(4, 2);
		newValue.add(5, 5);
		newValue.add(6, 8);
		newValue.add(7, 7);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(3, 3, 0);
		multisetChangeListener.addElementaryExpection(4, 2, 0);
		multisetChangeListener.addElementaryExpection(6, 0, 2);
		multisetChangeListener.addElementaryExpection(7, 0, 7);
		property.set(newValue);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// change property value (change to null)
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), null);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(4, 2, 0);
		multisetChangeListener.addElementaryExpection(5, 5, 0);
		multisetChangeListener.addElementaryExpection(6, 8, 0);
		multisetChangeListener.addElementaryExpection(7, 7, 0);
		property.set(null);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// set to null again (no expectation)
		property.set(null);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// change property value (change from null)
		newValue = CollectionUtils.observableHashMultiset();
		newValue.add(1, 1);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		property.set(newValue);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// set to identical value (no notifications expected)
		property.set(newValue);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// set to equal value (no list change notification expected)
		newValue = CollectionUtils.observableHashMultiset();
		newValue.add(1, 1);
		invalidationListener.expect(1);
		changeListener.addExpectation(property.get(), newValue);
		property.set(newValue);
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// modify value (change equality but not identit)y; no change
		// notification expected
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 1, 0);
		property.get().removeAll(Arrays.asList(1));
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();

		// only touch value (don't change identity nor equality); no
		// notifications expected
		property.get().removeAll(Arrays.asList(1));
		invalidationListener.check();
		multisetChangeListener.check();
		changeListener.check();
	}

	protected void check(MultisetProperty<Integer> property1,
			MultisetProperty<Integer> property2,
			ObservableMultiset<Integer> backupMap) {
		assertEquals(property1, property2);
		assertEquals(property1.get(), property2.get());
		assertEquals(backupMap, property1.get());
		assertEquals(backupMap, property2.get());
		assertEquals(backupMap.size(), property1.sizeProperty().get());
		assertEquals(backupMap.size(), property2.sizeProperty().get());
		assertEquals(backupMap.isEmpty(), property1.emptyProperty().get());
		assertEquals(backupMap.isEmpty(), property2.emptyProperty().get());
	}

	@Test
	public void unidirectionalBinding() {
		MultisetProperty<Integer> property1 = propertyProvider.get();
		MultisetProperty<Integer> property2 = propertyProvider.get();

		// bind properly
		property2.bind(property1);
		assertFalse(property1.isBound());
		assertTrue(property2.isBound());

		// change value of first property
		ObservableMultiset<Integer> newValue = CollectionUtils
				.observableHashMultiset();
		newValue.add(1);
		property1.set(newValue);
		assertEquals(newValue, property1.get());
		assertEquals(newValue, property2.get());
		assertEquals(property1, property2);

		// set value on second (bound) property (yields IAE)
		try {
			property2.set(CollectionUtils.<Integer> observableHashMultiset());
			fail("Expected IllegalArgumentException because property is bound.");
		} catch (IllegalArgumentException e) {
			assertEquals("A bound value cannot be set.", e.getMessage());
		}

		// unbind
		property2.unbind();
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// change value after binding has been removed
		newValue = CollectionUtils.observableHashMultiset();
		newValue.add(3, 3);
		property1.set(newValue);
		assertNotEquals(property1, property2);
		assertEquals(newValue, property1.get());
		assertNotEquals(newValue, property2.get());

		// bind on null (yields NPE)
		try {
			property2.bind(null);
			fail("Expected NullPointerException because binding to null is not valid.");
		} catch (NullPointerException e) {
			assertEquals("Cannot bind to null.", e.getMessage());
		}

		// according to JavaFX, binding on itself does not yield an IAE here
	}

	/**
	 * Test the unidirectional content bindings as offered by
	 * {@link ReadOnlyMultisetProperty}.
	 */
	@Test
	public void unidirectionalContentBinding() {
		MultisetProperty<Integer> property1 = propertyProvider.get();
		MultisetProperty<Integer> property2 = propertyProvider.get();
		ObservableMultiset<Integer> backupMap = CollectionUtils
				.observableHashMultiset();

		property2.bindContent(property1);
		// XXX: According to JavaFX contract, content binding does not lead to
		// the properties being regarded as being bound.
		assertFalse(property1.isBound());
		assertFalse(property2.isBound());

		// add
		property1.add(1);
		backupMap.add(1);
		check(property1, property2, backupMap);

		// add_count
		property1.add(1, 2);
		backupMap.add(1, 2);
		check(property1, property2, backupMap);

		// addAll
		Multiset<Integer> toAdd = HashMultiset.create();
		toAdd.add(2, 2);
		toAdd.add(3, 3);
		property1.addAll(toAdd);
		backupMap.addAll(toAdd);
		check(property1, property2, backupMap);

		// remove
		property1.remove(1);
		backupMap.remove(1);
		check(property1, property2, backupMap);

		// remove with count
		property1.remove(2, 2);
		backupMap.remove(2, 2);
		check(property1, property2, backupMap);

		// set count
		property1.setCount(3, 6);
		backupMap.setCount(3, 6);
		check(property1, property2, backupMap);

		// set count with old
		property1.setCount(3, 6, 3);
		backupMap.setCount(3, 6, 3);
		check(property1, property2, backupMap);

		// clear
		property1.clear();
		backupMap.clear();
		check(property1, property2, backupMap);

		// unbind property2, ensure values are no longer synchronized
		property2.unbindContent(property1);
		property1.add(1);
		assertNotEquals(property2.get(), property1.get());
		assertNotEquals(property2.sizeProperty().get(),
				property1.sizeProperty().get());
		assertNotEquals(property2.emptyProperty().get(),
				property1.emptyProperty().get());

		// unbind property2 from null (yields NPE)
		try {
			property2.unbindContent(null);
			fail("Expected NullPointerException.");
		} catch (NullPointerException e) {
			assertEquals("Cannot unbind from null value.", e.getMessage());
		}

		// unbind property2 from itself (yields IAE)
		try {
			property2.unbindContent(property2);
			fail("Expected IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot unbind source to itself.", e.getMessage());
		}

		// bind property2 to null (yields NPE)
		try {
			property2.bindContent(null);
			fail("Expected NullPointerException.");
		} catch (NullPointerException e) {
			assertEquals("Cannot bind to null value.", e.getMessage());
		}

		// bind property2 to itself (yields IAE)
		try {
			property2.bindContent(property2);
			fail("Expected IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot bind source to itself.", e.getMessage());
		}
	}
}
