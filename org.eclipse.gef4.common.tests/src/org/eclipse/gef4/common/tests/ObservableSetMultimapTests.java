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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.gef4.common.beans.binding.SetMultimapExpressionHelper;
import org.eclipse.gef4.common.beans.property.ReadOnlySetMultimapWrapper;
import org.eclipse.gef4.common.beans.property.SimpleSetMultimapProperty;
import org.eclipse.gef4.common.collections.ObservableSetMultimap;
import org.eclipse.gef4.common.collections.ObservableSetMultimapWrapper;
import org.eclipse.gef4.common.collections.SetMultimapChangeListener;
import org.eclipse.gef4.common.collections.SetMultimapChangeListenerHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Tests for correct behavior of {@link ObservableSetMultimap} implementations,
 * including respective {@link ObservableValue observable values}, as well as
 * related {@link SetMultimapChangeListenerHelper} and
 * {@link SetMultimapExpressionHelper} helper classes. Concrete implementations
 * are tested by parameterizing the test with a respective Provider, which is
 * done for {@link ObservableSetMultimapWrapper} as well as
 * {@link SimpleSetMultimapProperty} and {@link ReadOnlySetMultimapWrapper}.
 * <p>
 * Ensures that correct behavior of the underlying {@link SetMultimap} is
 * preserved and that {@link InvalidationListener} and
 * {@link SetMultimapChangeListener}, as well as {@link ChangeListener} (in case
 * of observable values) are notified properly.
 * <p>
 * Test strategy is to use a backup {@link SetMultimap} on which to apply the
 * same operations as on the two be tested {@link ObservableSetMultimap}, so
 * that same behavior is ensured.
 *
 * @author anyssen
 *
 */
@RunWith(Parameterized.class)
public class ObservableSetMultimapTests {

	protected static class ChangeExpector<K, V>
			implements ChangeListener<ObservableSetMultimap<K, V>> {

		private ObservableValue<ObservableSetMultimap<K, V>> source;
		private LinkedList<ObservableSetMultimap<K, V>> oldValueQueue = new LinkedList<>();
		private LinkedList<ObservableSetMultimap<K, V>> newValueQueue = new LinkedList<>();

		public ChangeExpector(
				ObservableValue<ObservableSetMultimap<K, V>> source) {
			this.source = source;
		}

		public void addExpectation(ObservableSetMultimap<K, V> oldValue,
				ObservableSetMultimap<K, V> newValue) {
			// We check that the reference to the observable value is correct,
			// thus do not copy the passed in values.
			oldValueQueue.addFirst(oldValue);
			newValueQueue.addFirst(newValue);
		}

		@Override
		public void changed(
				ObservableValue<? extends ObservableSetMultimap<K, V>> observable,
				ObservableSetMultimap<K, V> oldValue,
				ObservableSetMultimap<K, V> newValue) {
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

	protected static class InvalidationExpector
			implements InvalidationListener {
		int expect = 0;

		public void check() {
			if (expect > 0) {
				fail("Did not receive " + expect
						+ " expected invalidation event.");
			}
		}

		public void expect(int expext) {
			this.expect = expext;
		}

		@Override
		public void invalidated(Observable observable) {
			if (expect-- <= 0) {
				fail("Did not expect an invalidation event.");
			}
		}
	}

	protected static class SetMultimapChangeExpector<K, V>
			implements SetMultimapChangeListener<K, V> {

		private ObservableSetMultimap<K, V> source;
		private LinkedList<LinkedList<K>> keyQueue = new LinkedList<>();
		private LinkedList<LinkedList<Set<V>>> addedValuesQueue = new LinkedList<>();
		private LinkedList<LinkedList<Set<V>>> removedValuesQueue = new LinkedList<>();

		public SetMultimapChangeExpector(ObservableSetMultimap<K, V> source) {
			this.source = source;
		}

		public void addAtomicExpectation() {
			keyQueue.addFirst(new LinkedList<K>());
			addedValuesQueue.addFirst(new LinkedList<Set<V>>());
			removedValuesQueue.addFirst(new LinkedList<Set<V>>());
		}

		public void addElementaryExpectation(K key, Set<V> removedValues,
				Set<V> addedValues) {
			if (keyQueue.size() <= 0) {
				throw new IllegalArgumentException(
						"Add atomic expectation first.");
			}
			keyQueue.getFirst().addFirst(key);
			addedValuesQueue.getFirst().addFirst(new HashSet<>(addedValues));
			removedValuesQueue.getFirst()
					.addFirst(new HashSet<>(removedValues));
		}

		public void check() {
			if (keyQueue.size() > 0) {
				fail("Did not receive " + keyQueue.size()
						+ " expected changes.");
			}
		}

		@Override
		public void onChanged(
				org.eclipse.gef4.common.collections.SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
			if (keyQueue.size() <= 0) {
				fail("Received unexpected change " + change);
			}

			LinkedList<K> elementaryKeysQueue = keyQueue.pollLast();
			LinkedList<Set<V>> elementaryAddedValuesQueue = addedValuesQueue
					.pollLast();
			LinkedList<Set<V>> elementaryRemovedValuesQueue = removedValuesQueue
					.pollLast();

			assertEquals(source, change.getSetMultimap());

			StringBuffer expectedString = new StringBuffer();
			while (change.next()) {
				if (elementaryKeysQueue.size() <= 0) {
					fail("Did not expect another elementary change");
				}
				// check key
				K expectedKey = elementaryKeysQueue.pollLast();
				assertEquals(expectedKey, change.getKey());

				// check added values
				Set<V> expectedAddedValues = elementaryAddedValuesQueue
						.pollLast();
				assertEquals(expectedAddedValues, change.getValuesAdded());
				if (expectedAddedValues != null
						&& !expectedAddedValues.isEmpty()) {
					assertTrue(change.wasAdded());
				} else {
					assertFalse(change.wasAdded());
				}

				// check removed values
				Set<V> expectedRemovedValues = elementaryRemovedValuesQueue
						.pollLast();
				assertEquals(expectedRemovedValues, change.getValuesRemoved());
				if (expectedRemovedValues != null
						&& !expectedRemovedValues.isEmpty()) {
					assertTrue(change.wasRemoved());
				} else {
					assertFalse(change.wasRemoved());
				}

				// check string representation
				if (!expectedString.toString().isEmpty()) {
					expectedString.append(" ");
				}
				if (expectedAddedValues.isEmpty()
						&& !expectedRemovedValues.isEmpty()) {
					expectedString.append("Removed " + expectedRemovedValues
							+ " for key " + expectedKey + ".");
				} else if (!expectedAddedValues.isEmpty()
						&& expectedRemovedValues.isEmpty()) {
					expectedString.append("Added " + expectedAddedValues
							+ " for key " + expectedKey + ".");
				} else {
					expectedString.append("Replaced " + expectedRemovedValues
							+ " by " + expectedAddedValues + " for key "
							+ expectedKey + ".");
				}
			}
			if (elementaryKeysQueue.size() > 0) {
				fail("Did not receive " + elementaryKeysQueue.size()
						+ " expected elementary changes.");
			}
			assertEquals(expectedString.toString(), change.toString());
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ new Provider<ObservableSetMultimap<Integer, String>>() {
					@Override
					public ObservableSetMultimap<Integer, String> get() {
						// test ObservableSetMultimapWrapper as the 'default'
						// implementation of ObservableSetMultimap
						return new ObservableSetMultimapWrapper<>(
								HashMultimap.<Integer, String> create());
					}
				} }, { new Provider<ObservableSetMultimap<Integer, String>>() {

					@Override
					public ObservableSetMultimap<Integer, String> get() {
						// test SimpleSetMultimapProperty, which is the
						// 'default' implementation of the related
						// ObservableValue.
						return new SimpleSetMultimapProperty<>(
								new ObservableSetMultimapWrapper<>(HashMultimap
										.<Integer, String> create()));
					}
				} }, { new Provider<ObservableSetMultimap<Integer, String>>() {

					@Override
					public ObservableSetMultimap<Integer, String> get() {
						// test ReadOnlySetMultimapWrapper, which is the
						// 'default' implementation of the related
						// read-only support.
						return new ReadOnlySetMultimapWrapper<>(
								new ObservableSetMultimapWrapper<>(HashMultimap
										.<Integer, String> create()));
					}
				} } });
	}

	private ObservableSetMultimap<Integer, String> observable;
	private Provider<ObservableSetMultimap<Integer, String>> observableProvider;

	public ObservableSetMultimapTests(
			Provider<ObservableSetMultimap<Integer, String>> sourceProvider) {
		this.observableProvider = sourceProvider;
	}

	@Before
	public void before() {
		observable = observableProvider.get();
	}

	protected void check(ObservableSetMultimap<Integer, String> observable,
			SetMultimap<Integer, String> backupMap) {
		assertEquals(backupMap, observable);
		if (observable instanceof ReadOnlySetMultimapWrapper) {
			assertEquals(backupMap,
					((ReadOnlySetMultimapWrapper<Integer, String>) observable)
							.getReadOnlyProperty().get());
		}
	}

	@Test
	public void clear() {
		// initialize maps with some values
		observable.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		observable.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		observable.putAll(null, Sets.newHashSet(null, "null"));

		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		backupMap.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		backupMap.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		backupMap.putAll(null, Sets.newHashSet(null, "null"));
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// remove all values
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Sets.newHashSet(null, "null"), Collections.<String> emptySet());
		setMultimapChangeListener.addElementaryExpectation(1,
				Sets.newHashSet("1-1", "1-2", "1-3"),
				Collections.<String> emptySet());
		setMultimapChangeListener.addElementaryExpectation(2,
				Sets.newHashSet("2-1", "2-2", "2-3"),
				Collections.<String> emptySet());
		observable.clear();
		backupMap.clear();
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// clear again (while already empty)
		invalidationListener.expect(0);
		observable.clear();
		backupMap.clear();
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	/**
	 * Checks that its safe (and does not lead to a
	 * {@link ConcurrentModificationException} if a listener registers or
	 * unregisters itself as the result of a notification.
	 */
	@Test
	public void listenersProvokingConcurrentModifications() {
		// add listeners
		InvalidationListener invalidationListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				// unregister ourselves
				observable.removeListener(this);

				// register ourselves (again)
				observable.addListener(this);
			}
		};
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			ChangeListener<ObservableSetMultimap<Integer, String>> changeListener = new ChangeListener<ObservableSetMultimap<Integer, String>>() {

				@Override
				public void changed(
						ObservableValue<? extends ObservableSetMultimap<Integer, String>> observable,
						ObservableSetMultimap<Integer, String> oldValue,
						ObservableSetMultimap<Integer, String> newValue) {
					// unregister ourselves
					observable.removeListener(this);

					// register ourselves (again)
					observable.addListener(this);
				}
			};
			observableValue.addListener(changeListener);
		}
		SetMultimapChangeListener<Integer, String> setMultimapChangeListener = new SetMultimapChangeListener<Integer, String>() {

			@Override
			public void onChanged(
					org.eclipse.gef4.common.collections.SetMultimapChangeListener.Change<? extends Integer, ? extends String> change) {
				// unregister ourselves
				change.getSetMultimap().removeListener(this);

				// register ourselves (again)
				change.getSetMultimap().addListener(this);
			}
		};
		observable.addListener(setMultimapChangeListener);

		// ensure no concurrent modification exceptions result
		observable.put(1, "1-1");
	}

	@Test
	public void listenersRegisteredMoreThanOnce() {
		// register listeners (twice)
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		observable.addListener(invalidationListener);
		// add and remove should have no effect
		InvalidationListener invalidationListener2 = new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				// ignore
			}
		};
		observable.addListener(invalidationListener2);
		observable.removeListener(invalidationListener2);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
			observableValue.addListener(changeListener);
			// add and remove should have no effect
			ChangeListener<ObservableSetMultimap<Integer, String>> changeListener2 = new ChangeListener<ObservableSetMultimap<Integer, String>>() {

				@Override
				public void changed(
						ObservableValue<? extends ObservableSetMultimap<Integer, String>> observable,
						ObservableSetMultimap<Integer, String> oldValue,
						ObservableSetMultimap<Integer, String> newValue) {
					// ignore
				}
			};
			observableValue.addListener(changeListener2);
			observableValue.removeListener(changeListener2);
		}
		observable.addListener(setMultimapChangeListener);
		observable.addListener(setMultimapChangeListener);
		// add and remove should have no effect
		SetMultimapChangeListener<Integer, String> setMultimapChangeListener2 = new SetMultimapChangeListener<Integer, String>() {

			@Override
			public void onChanged(
					org.eclipse.gef4.common.collections.SetMultimapChangeListener.Change<? extends Integer, ? extends String> change) {
				// ignore
			}
		};
		observable.addListener(setMultimapChangeListener2);
		observable.removeListener(setMultimapChangeListener2);

		// perform put
		invalidationListener.expect(2);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Collections.singleton("1"));
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Collections.singleton("1"));
		assertTrue(observable.put(1, "1"));
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// remove single listener occurrence
		observable.removeListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			observableValue.removeListener(changeListener);
		}
		observable.removeListener(setMultimapChangeListener);

		// perform another put
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(2,
				Collections.<String> emptySet(), Collections.singleton("2"));
		assertTrue(observable.put(2, "2"));
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// remove listeners and ensure no notifications are received
		observable.removeListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			observableValue.removeListener(changeListener);
		}
		observable.removeListener(setMultimapChangeListener);
		assertTrue(observable.put(3, "3"));
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void put() {
		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// put a single value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Collections.singleton("1-1"));
		assertEquals(backupMap.put(1, "1-1"), observable.put(1, "1-1"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// put a second value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Collections.singleton("1-2"));
		assertEquals(backupMap.put(1, "1-2"), observable.put(1, "1-2"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// put a different value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(2,
				Collections.<String> emptySet(), Collections.singleton("2"));
		assertEquals(backupMap.put(2, "2"), observable.put(2, "2"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// null key and values are allowed within SetMultimap
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Collections.<String> emptySet(),
				Collections.<String> singleton(null));
		assertEquals(backupMap.put(null, null), observable.put(null, null));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// add a real value to null key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Collections.<String> emptySet(),
				Collections.<String> singleton("null"));
		assertEquals(backupMap.put(null, "null"), observable.put(null, "null"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// put same value again should not yield any notification.
		assertEquals(backupMap.put(2, "2"), observable.put(2, "2"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void putAll_multipleKeys() {
		// prepare backup SetMultimap
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// add distinct values for different keys
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Sets.newHashSet("1-1", "1-2"));
		setMultimapChangeListener.addElementaryExpectation(2,
				Collections.<String> emptySet(), Sets.newHashSet("2-1", "2-2"));
		SetMultimap<Integer, String> toAdd = HashMultimap.create();
		toAdd.putAll(1, Sets.newHashSet("1-1", "1-2"));
		toAdd.putAll(2, Sets.newHashSet("2-1", "2-2"));
		assertEquals(backupMap.putAll(toAdd), observable.putAll(toAdd));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// add new and already registered values for different keys
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Sets.newHashSet("1-3"));
		setMultimapChangeListener.addElementaryExpectation(2,
				Collections.<String> emptySet(), Sets.newHashSet("2-3"));
		toAdd = HashMultimap.create();
		toAdd.putAll(1, Sets.newHashSet("1-2", "1-3"));
		toAdd.putAll(2, Sets.newHashSet("2-2", "2-3"));
		assertEquals(backupMap.putAll(toAdd), observable.putAll(toAdd));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// add already registered values
		toAdd = HashMultimap.create();
		toAdd.putAll(1, Sets.newHashSet("1-2", "1-3"));
		toAdd.putAll(2, Sets.newHashSet("2-2", "2-3"));
		assertEquals(backupMap.putAll(toAdd), observable.putAll(toAdd));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void putAll_singleKey() {
		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// add two new distinct values
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Sets.newHashSet("1-1", "1-2"));
		assertEquals(backupMap.putAll(1, Arrays.asList("1-1", "1-2")),
				observable.putAll(1, Arrays.asList("1-1", "1-2")));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// add a new and an already added value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.<String> emptySet(), Sets.newHashSet("1-3"));
		assertEquals(backupMap.putAll(1, Arrays.asList("1-2", "1-3")),
				observable.putAll(1, Arrays.asList("1-2", "1-3")));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// put already added values
		assertEquals(backupMap.putAll(1, Arrays.asList("1-2", "1-3")),
				observable.putAll(1, Arrays.asList("1-2", "1-3")));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void remove_entry() {
		// initialize maps with some values
		observable.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		observable.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		observable.putAll(null, Sets.newHashSet(null, "null"));

		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		backupMap.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		backupMap.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		backupMap.putAll(null, Sets.newHashSet(null, "null"));
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// remove a Compound value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Collections.singleton("1-1"), Collections.<String> emptySet());
		assertEquals(backupMap.remove(1, "1-1"), observable.remove(1, "1-1"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// remove null value from null key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Collections.<String> singleton(null),
				Collections.<String> emptySet());
		assertEquals(backupMap.remove(null, null),
				observable.remove(null, null));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// remove real value from null key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Collections.<String> singleton("null"),
				Collections.<String> emptySet());
		assertEquals(backupMap.remove(null, "null"),
				observable.remove(null, "null"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// try to remove not contained value
		assertEquals(backupMap.remove(1, "1-1"), observable.remove(1, "1-1"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// try to remove entry with key of wrong type
		assertEquals(backupMap.remove("1", "1-1"),
				observable.remove("1", "1-1"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// try to remove entry with value of wrong type
		assertEquals(backupMap.remove(1, 1), observable.remove(1, 1));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void removeAll_CompoundKey() {
		// initialize maps with some values
		observable.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		observable.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		observable.putAll(null, Sets.newHashSet(null, "null"));

		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		backupMap.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		backupMap.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		backupMap.putAll(null, Sets.newHashSet(null, "null"));
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// remove values for a single key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Sets.newHashSet("1-1", "1-2", "1-3"),
				Collections.<String> emptySet());
		assertEquals(backupMap.removeAll(1), observable.removeAll(1));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// remove values for null key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Sets.newHashSet(null, "null"), Collections.<String> emptySet());
		assertEquals(backupMap.removeAll(null), observable.removeAll(null));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// try to remove values for not contained key
		assertEquals(backupMap.removeAll(4711), observable.removeAll(4711));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// try to remove values of key with wrong type
		assertEquals(backupMap.removeAll("4711"), observable.removeAll("4711"));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void replaceAll() {
		// initialize maps with some values
		observable.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		observable.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		observable.putAll(3, Sets.newHashSet("3-1", "3-2", "3-3"));
		observable.putAll(null, Sets.newHashSet(null, "null"));

		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		backupMap.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		backupMap.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		backupMap.putAll(3, Sets.newHashSet("3-1", "3-2", "3-3"));
		backupMap.putAll(null, Sets.newHashSet(null, "null"));
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// remove all values
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(null,
				Sets.newHashSet(null, "null"), Collections.<String> emptySet()); // removed
																					// null
																					// key
		setMultimapChangeListener.addElementaryExpectation(2,
				Sets.newHashSet("2-1", "2-3"), Collections.<String> emptySet()); // removed
																					// 2-1,
																					// 2-3
		setMultimapChangeListener.addElementaryExpectation(3,
				Sets.newHashSet("3-3"), Sets.newHashSet("3-4")); // removed 3-3,
																	// added 3-4
		setMultimapChangeListener.addElementaryExpectation(4,
				Collections.<String> emptySet(), Sets.newHashSet("4-1"));

		SetMultimap<Integer, String> toReplace = HashMultimap.create();
		toReplace.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3")); // leave
																	// unchanged
		toReplace.putAll(2, Sets.newHashSet("2-2")); // remove values (2-1, 2-3)
		toReplace.putAll(3, Sets.newHashSet("3-1", "3-2", "3-4")); // change
																	// values
																	// (removed
																	// 3-3,
																	// added
																	// 3-4)
		toReplace.putAll(4, Sets.newHashSet("4-1")); // add entry
		observable.replaceAll(toReplace);
		backupMap.clear();
		backupMap.putAll(toReplace);
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// replace with same contents (should not have any effect)
		invalidationListener.expect(0);
		observable.replaceAll(toReplace);
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	@Test
	public void replaceValues() {
		// initialize maps with some values
		observable.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		observable.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		observable.putAll(null, Sets.newHashSet(null, "null"));

		// prepare backup map
		SetMultimap<Integer, String> backupMap = HashMultimap.create();
		backupMap.putAll(1, Sets.newHashSet("1-1", "1-2", "1-3"));
		backupMap.putAll(2, Sets.newHashSet("2-1", "2-2", "2-3"));
		backupMap.putAll(null, Sets.newHashSet(null, "null"));
		check(observable, backupMap);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer, String> changeListener = null;
		SetMultimapChangeExpector<Integer, String> setMultimapChangeListener = new SetMultimapChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(setMultimapChangeListener);

		// replace all values of a specific key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(1,
				Sets.newHashSet("1-1", "1-2", "1-3"),
				Sets.newHashSet("1-4", "1-5", "1-6"));
		assertEquals(
				backupMap.replaceValues(1,
						Sets.newHashSet("1-4", "1-5", "1-6")),
				observable.replaceValues(1,
						Sets.newHashSet("1-4", "1-5", "1-6")));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// use replacement to clear values for a key
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableSetMultimap<Integer, String>> observableValue = (ObservableValue<ObservableSetMultimap<Integer, String>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		setMultimapChangeListener.addAtomicExpectation();
		setMultimapChangeListener.addElementaryExpectation(2,
				Sets.newHashSet("2-1", "2-2", "2-3"),
				Collections.<String> emptySet());
		assertEquals(
				backupMap.replaceValues(2, Collections.<String> emptySet()),
				observable.replaceValues(2, Collections.<String> emptySet()));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();

		// try to replace values for non existing key
		assertEquals(
				backupMap.replaceValues(4711, Sets.newHashSet("4", "7", "1")),
				observable.replaceValues(4711, Sets.newHashSet("4", "7", "1")));
		check(observable, backupMap);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		setMultimapChangeListener.check();
	}

	/**
	 * Confirm {@link ObservableSetMultimap} works as expected even if no
	 * listeners are registered.
	 */
	@Test
	public void withoutListeners() {
		// put
		assertTrue(observable.put(4711, "4"));
		assertTrue(observable.put(4711, "7"));
		assertTrue(observable.put(4711, "1"));
		assertFalse(observable.put(4711, "1"));
		assertEquals(Sets.newHashSet("4", "7", "1", "1"), observable.get(4711));

		// remove all
		observable.removeAll(4711);
		assertTrue(observable.isEmpty());
	}

}
