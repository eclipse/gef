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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import org.eclipse.gef4.common.beans.binding.MultisetExpressionHelper;
import org.eclipse.gef4.common.beans.property.ReadOnlyMultisetWrapper;
import org.eclipse.gef4.common.beans.property.SimpleMultisetProperty;
import org.eclipse.gef4.common.collections.MultisetChangeListener;
import org.eclipse.gef4.common.collections.ObservableMultiset;
import org.eclipse.gef4.common.collections.ObservableMultisetWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Provider;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Tests for correct behavior of {@link ObservableMultiset} implementations,
 * including respective {@link ObservableValue observable values}, as well as
 * related {@link MultisetChangeListener} and {@link MultisetExpressionHelper}
 * helper classes. Concrete implementations are tested by parameterizing the
 * test with a respective Provider, which is done for
 * {@link ObservableMultisetWrapper} as well as {@link SimpleMultisetProperty}
 * and {@link ReadOnlyMultisetWrapper}.
 * <p>
 * Ensures that correct behavior of the underlying {@link Multiset} is preserved
 * and that {@link InvalidationListener} and {@link MultisetChangeListener}, as
 * well as {@link ChangeListener} (in case of observable values) are notified
 * properly.
 * <p>
 * Test strategy is to use a backup {@link Multiset} on which to apply the same
 * operations as on the two be tested {@link ObservableMultiset}, so that same
 * behavior is ensured.
 *
 * @author anyssen
 *
 */
@RunWith(Parameterized.class)
public class ObservableMultisetTests {

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

	protected static class MultisetChangeExpector<E>
			implements MultisetChangeListener<E> {

		private ObservableMultiset<E> source;
		private LinkedList<E> elementQueue = new LinkedList<>();
		private LinkedList<Integer> addedCountQueue = new LinkedList<>();
		private LinkedList<Integer> removedCountQueue = new LinkedList<>();

		public MultisetChangeExpector(ObservableMultiset<E> source) {
			this.source = source;
		}

		public void addExpectation(E element, int removedCount,
				int addedCount) {
			elementQueue.addFirst(element);
			removedCountQueue.addFirst(removedCount);
			addedCountQueue.addFirst(addedCount);
		}

		public void check() {
			if (elementQueue.size() > 0) {
				fail("Did not receive " + elementQueue.size()
						+ " expected changes.");
			}
		}

		@Override
		public void onChanged(
				org.eclipse.gef4.common.collections.MultisetChangeListener.Change<? extends E> change) {
			if (elementQueue.size() <= 0) {
				fail("Received unexpected change " + change);
			}

			assertEquals(source, change.getMultiset());

			// check element
			E expectedElement = elementQueue.pollLast();
			assertEquals(expectedElement, change.getElement());

			// check added values
			int expectedAddCount = addedCountQueue.pollLast();
			assertEquals(expectedAddCount, change.getAddCount());

			// check removed values
			int expectedRemoveCount = removedCountQueue.pollLast();
			assertEquals(expectedRemoveCount, change.getRemoveCount());

			// check string representation
			if (expectedAddCount > 0) {
				assertEquals("Added " + expectedElement + " " + expectedAddCount
						+ " times.", change.toString());
			} else {
				assertEquals("Removed " + expectedElement + " "
						+ expectedRemoveCount + " times.", change.toString());
			}
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] { { new Provider<ObservableMultiset<Integer>>() {
					@Override
					public ObservableMultiset<Integer> get() {
						// test ObservableMultisetWrapper as the 'default'
						// implementation of ObservableSetMultimap
						return new ObservableMultisetWrapper<>(
								HashMultiset.<Integer> create());
					}
				} },

						{ new Provider<ObservableMultiset<Integer>>() {

							@Override
							public ObservableMultiset<Integer> get() {
								// test SimpleMultisetProperty, which is the
								// 'default' implementation of the related
								// ObservableValue.
								return new SimpleMultisetProperty<>(
										new ObservableMultisetWrapper<>(
												HashMultiset
														.<Integer> create()));
							}
						} }, { new Provider<ObservableMultiset<Integer>>() {

							@Override
							public ObservableMultiset<Integer> get() {
								// test ReadOnlyMultisetWrapper, which is the
								// 'default' implementation of the related
								// read-only support.
								return new ReadOnlyMultisetWrapper<>(
										new ObservableMultisetWrapper<>(
												HashMultiset
														.<Integer> create()));
							}
						} } });
	}

	private ObservableMultiset<Integer> observable;
	private Provider<ObservableMultiset<Integer>> observableProvider;

	public ObservableMultisetTests(
			Provider<ObservableMultiset<Integer>> sourceProvider) {
		this.observableProvider = sourceProvider;
	}

	@Test
	public void add() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// add a single value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		assertEquals(backupMultiset.add(1), observable.add(1));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// add a second occurrence of the same value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		assertEquals(backupMultiset.add(1), observable.add(1));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// add a different value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(2, 0, 1);
		assertEquals(backupMultiset.add(2), observable.add(2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void add_withCount() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// add zero occurrences (no change expected)
		assertEquals(backupMultiset.add(1, 0), observable.remove(1, 0));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// add a single value multiple times
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(5, 0, 5);
		assertEquals(backupMultiset.add(5, 5), observable.add(5, 5));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// add a value zero times (no events should occur)
		assertEquals(backupMultiset.add(1, 0), observable.add(1, 0));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void addAll() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// add a collection with three values
		invalidationListener.expect(3);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		multisetChangeListener.addExpectation(2, 0, 2);
		multisetChangeListener.addExpectation(3, 0, 3);
		Multiset<Integer> toAdd = HashMultiset.create();
		toAdd.add(1);
		toAdd.add(2, 2);
		toAdd.add(3, 3);
		assertEquals(backupMultiset.addAll(toAdd), observable.addAll(toAdd));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// add another collection with three values
		invalidationListener.expect(2);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(2, 0, 2);
		multisetChangeListener.addExpectation(4, 0, 3);
		toAdd = HashMultiset.create();
		toAdd.add(2, 2);
		toAdd.add(4, 3);
		assertEquals(backupMultiset.addAll(toAdd), observable.addAll(toAdd));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Before
	public void before() {
		observable = observableProvider.get();
	}

	protected void check(ObservableMultiset<Integer> observable,
			Multiset<Integer> backupMultiset) {
		assertEquals(backupMultiset, observable);
		if (observable instanceof ReadOnlyMultisetWrapper) {
			assertEquals(backupMultiset,
					((ReadOnlyMultisetWrapper<Integer>) observable)
							.getReadOnlyProperty().get());
		}
	}

	@Test
	public void clear() {
		// initialize multiset with some values
		observable.add(1, 1);
		observable.add(2, 2);
		observable.add(3, 3);

		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		backupMultiset.add(1, 1);
		backupMultiset.add(2, 2);
		backupMultiset.add(3, 3);
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// clear
		invalidationListener.expect(3);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 1, 0);
		multisetChangeListener.addExpectation(2, 2, 0);
		multisetChangeListener.addExpectation(3, 3, 0);
		observable.clear();
		backupMultiset.clear();
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
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
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			ChangeListener<ObservableMultiset<Integer>> changeListener = new ChangeListener<ObservableMultiset<Integer>>() {

				@Override
				public void changed(
						ObservableValue<? extends ObservableMultiset<Integer>> observable,
						ObservableMultiset<Integer> oldValue,
						ObservableMultiset<Integer> newValue) {
					// unregister ourselves
					observable.removeListener(this);

					// register ourselves (again)
					observable.addListener(this);
				}
			};
			observableValue.addListener(changeListener);
		}
		MultisetChangeListener<Integer> multisetChangeListener = new MultisetChangeListener<Integer>() {

			@Override
			public void onChanged(
					org.eclipse.gef4.common.collections.MultisetChangeListener.Change<? extends Integer> change) {
				// unregister ourselves
				change.getMultiset().removeListener(this);

				// register ourselves (again)
				change.getMultiset().addListener(this);
			}
		};
		observable.addListener(multisetChangeListener);

		// ensure no concurrent modification exceptions result
		observable.add(1);
	}

	@Test
	public void listenersRegisteredMoreThanOnce() {
		// register listeners (twice)
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
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
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
			observableValue.addListener(changeListener);
			// add and remove should have no effect
			ChangeListener<ObservableMultiset<Integer>> changeListener2 = new ChangeListener<ObservableMultiset<Integer>>() {

				@Override
				public void changed(
						ObservableValue<? extends ObservableMultiset<Integer>> observable,
						ObservableMultiset<Integer> oldValue,
						ObservableMultiset<Integer> newValue) {
					// ignore
				}
			};
			observableValue.addListener(changeListener2);
			observableValue.removeListener(changeListener2);
		}
		observable.addListener(multisetChangeListener);
		observable.addListener(multisetChangeListener);
		// add and remove should have no effect
		// add and remove should have no effect
		MultisetChangeListener<Integer> multisetChangeListener2 = new MultisetChangeListener<Integer>() {

			@Override
			public void onChanged(
					org.eclipse.gef4.common.collections.MultisetChangeListener.Change<? extends Integer> change) {
				// ignore
			}
		};
		observable.addListener(multisetChangeListener2);
		observable.removeListener(multisetChangeListener2);

		// perform add
		invalidationListener.expect(2);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		multisetChangeListener.addExpectation(1, 0, 1);
		assertTrue(observable.add(1));
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove single listener occurrence
		observable.removeListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			observableValue.removeListener(changeListener);
		}
		observable.removeListener(multisetChangeListener);

		// perform another add
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		assertTrue(observable.add(1));
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove listeners and ensure no notifications are received
		observable.removeListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			observableValue.removeListener(changeListener);
		}
		observable.removeListener(multisetChangeListener);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void remove() {
		// initialize multiset with some values
		observable.add(1, 1);
		observable.add(2, 2);
		observable.add(3, 3);

		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		backupMultiset.add(1, 1);
		backupMultiset.add(2, 2);
		backupMultiset.add(3, 3);
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// remove (first occurrence of) value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(2, 1, 0);
		assertEquals(backupMultiset.remove(2), observable.remove(2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove (second occurrence of) value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(2, 1, 0);
		assertEquals(backupMultiset.remove(2), observable.remove(2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove not contained value (no change expected)
		assertEquals(backupMultiset.remove(2), observable.remove(2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void remove_withCount() {
		// initialize multiset with some values
		observable.add(1, 1);
		observable.add(2, 2);
		observable.add(3, 3);

		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		backupMultiset.add(1, 1);
		backupMultiset.add(2, 2);
		backupMultiset.add(3, 3);
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// remove zero occurrences (no change expected)
		assertEquals(backupMultiset.remove(3, 0), observable.remove(3, 0));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove (two occurrences of) value
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(3, 2, 0);
		assertEquals(backupMultiset.remove(3, 2), observable.remove(3, 2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove more occurrences than contained (change contains fewer
		// occurrences)
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(3, 1, 0);
		assertEquals(backupMultiset.remove(3, 2), observable.remove(3, 2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// remove not contained value (no change expected)
		assertEquals(backupMultiset.remove(3, 1), observable.remove(3, 1));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void removeAll() {
		// initialize multiset with some values
		observable.add(1, 1);
		observable.add(2, 2);
		observable.add(3, 3);

		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		backupMultiset.add(1, 1);
		backupMultiset.add(2, 2);
		backupMultiset.add(3, 3);
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// remove collection
		invalidationListener.expect(2);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 1, 0);
		// all occurrences of 2 will be removed, even if toRemove contains fewer
		// occurrences.
		multisetChangeListener.addExpectation(2, 2, 0);
		Multiset<Integer> toRemove = HashMultiset.create();
		toRemove.add(1);
		toRemove.add(2, 1);
		toRemove.add(4, 4);
		assertEquals(backupMultiset.removeAll(toRemove),
				observable.removeAll(toRemove));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void retainAll() {
		// initialize multiset with some values
		observable.add(1, 1);
		observable.add(2, 2);
		observable.add(3, 3);

		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		backupMultiset.add(1, 1);
		backupMultiset.add(2, 2);
		backupMultiset.add(3, 3);
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// remove collection
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(3, 3, 0);
		Multiset<Integer> toRetain = HashMultiset.create();
		toRetain.add(1);
		toRetain.add(2, 1);
		toRetain.add(4, 4);
		assertEquals(backupMultiset.retainAll(toRetain),
				observable.retainAll(toRetain));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void setCount() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// set count for non contained element
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		assertEquals(backupMultiset.setCount(1, 1), observable.setCount(1, 1));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// increase count for already contained element
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 3);
		assertEquals(backupMultiset.setCount(1, 4), observable.setCount(1, 4));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// decrease count for already contained element
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 2, 0);
		assertEquals(backupMultiset.setCount(1, 2), observable.setCount(1, 2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	@Test
	public void setCount_withOld() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ChangeExpector<Integer> changeListener = null;
		MultisetChangeExpector<Integer> multisetChangeListener = new MultisetChangeExpector<>(
				observable);
		observable.addListener(invalidationListener);
		if (observable instanceof ObservableValue) {
			// register change listener as well
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener = new ChangeExpector<>(observableValue);
			observableValue.addListener(changeListener);
		}
		observable.addListener(multisetChangeListener);

		// set count for non contained element
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 2);
		assertEquals(backupMultiset.setCount(1, 0, 2),
				observable.setCount(1, 0, 2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// set count to increase occurrences
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 0, 1);
		assertEquals(backupMultiset.setCount(1, 2, 3),
				observable.setCount(1, 2, 3));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// set count to decrease occurrences
		invalidationListener.expect(1);
		if (observable instanceof ObservableValue) {
			// old and new value are the same, as the observable value of the
			// property has not been exchanged (but only its contents has been
			// changed); thus we may use the current value also as newValue.
			@SuppressWarnings("unchecked")
			ObservableValue<ObservableMultiset<Integer>> observableValue = (ObservableValue<ObservableMultiset<Integer>>) observable;
			changeListener.addExpectation(observableValue.getValue(),
					observableValue.getValue());
		}
		multisetChangeListener.addExpectation(1, 1, 0);
		assertEquals(backupMultiset.setCount(1, 3, 2),
				observable.setCount(1, 3, 2));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();

		// set count where old value is not met (no change expected)
		assertEquals(backupMultiset.setCount(1, 4, 3),
				observable.setCount(1, 4, 3));
		check(observable, backupMultiset);
		invalidationListener.check();
		if (observable instanceof ObservableValue) {
			changeListener.check();
		}
		multisetChangeListener.check();
	}

	/**
	 * Confirm {@link ObservableMultiset} works as expected even if no listeners
	 * are registered.
	 */
	@Test
	public void withoutListeners() {
		// add
		assertTrue(observable.add(1));
		assertTrue(observable.add(1));
		assertTrue(observable.add(1));
		assertTrue(observable.add(2));
		assertTrue(observable.add(2));
		assertEquals(3, observable.count(1));
		assertEquals(2, observable.count(2));

		// clear
		observable.clear();
		assertTrue(observable.isEmpty());
	}

}
