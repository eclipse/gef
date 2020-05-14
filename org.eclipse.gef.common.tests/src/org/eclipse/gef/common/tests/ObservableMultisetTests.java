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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import org.eclipse.gef.common.beans.binding.MultisetExpressionHelper;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetWrapper;
import org.eclipse.gef.common.beans.property.SimpleMultisetProperty;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.MultisetChangeListener;
import org.eclipse.gef.common.collections.ObservableMultiset;
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
		private LinkedList<LinkedList<E>> elementQueue = new LinkedList<>();
		private LinkedList<LinkedList<Integer>> addedCountQueue = new LinkedList<>();
		private LinkedList<LinkedList<Integer>> removedCountQueue = new LinkedList<>();

		public MultisetChangeExpector(ObservableMultiset<E> source) {
			this.source = source;
		}

		public void addAtomicExpectation() {
			elementQueue.addFirst(new LinkedList<E>());
			addedCountQueue.addFirst(new LinkedList<Integer>());
			removedCountQueue.addFirst(new LinkedList<Integer>());
		}

		public void addElementaryExpection(E element, int removedCount,
				int addedCount) {
			if (elementQueue.size() <= 0) {
				throw new IllegalArgumentException(
						"Add atomic expectation first.");
			}
			elementQueue.getFirst().addFirst(element);
			removedCountQueue.getFirst().addFirst(removedCount);
			addedCountQueue.getFirst().addFirst(addedCount);
		}

		public void check() {
			if (elementQueue.size() > 0) {
				fail("Did not receive " + elementQueue.size()
						+ " expected changes.");
			}
		}

		@Override
		public void onChanged(
				org.eclipse.gef.common.collections.MultisetChangeListener.Change<? extends E> change) {
			if (elementQueue.size() <= 0) {
				fail("Received unexpected atomic change " + change);
			}

			LinkedList<E> elementaryElementsQueue = elementQueue.pollLast();
			LinkedList<Integer> elementaryAddedCountQueue = addedCountQueue
					.pollLast();
			LinkedList<Integer> elementaryRemovedCountQueue = removedCountQueue
					.pollLast();

			assertEquals(source, change.getMultiset());

			StringBuffer expectedString = new StringBuffer();
			while (change.next()) {
				if (elementaryElementsQueue.size() <= 0) {
					fail("Did not expect another elementary change");
				}
				// check element
				E expectedElement = elementaryElementsQueue.pollLast();
				assertEquals(expectedElement, change.getElement());

				// check added values
				int expectedAddCount = elementaryAddedCountQueue.pollLast();
				assertEquals(expectedAddCount, change.getAddCount());

				// check removed values
				int expectedRemoveCount = elementaryRemovedCountQueue
						.pollLast();
				assertEquals(expectedRemoveCount, change.getRemoveCount());

				// check string representation
				if (!expectedString.toString().isEmpty()) {
					expectedString.append(" ");
				}
				if (expectedAddCount > 0) {
					expectedString.append("Added " + expectedAddCount
							+ " occurrences of " + expectedElement + ".");
				} else {
					expectedString.append("Removed " + expectedRemoveCount
							+ " occurrences of " + expectedElement + ".");
				}
			}
			if (elementaryElementsQueue.size() > 0) {
				fail("Did not receive " + elementaryElementsQueue.size()
						+ " expected elementary changes.");
			}
			assertEquals(expectedString.toString(), change.toString());
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
						return CollectionUtils.observableHashMultiset();
					}
				} },

						{ new Provider<ObservableMultiset<Integer>>() {

							@Override
							public ObservableMultiset<Integer> get() {
								// test SimpleMultisetProperty, which is the
								// 'default' implementation of the related
								// ObservableValue.
								return new SimpleMultisetProperty<>(
										CollectionUtils
												.<Integer> observableHashMultiset());
							}
						} }, { new Provider<ObservableMultiset<Integer>>() {

							@Override
							public ObservableMultiset<Integer> get() {
								// test ReadOnlyMultisetWrapper, which is the
								// 'default' implementation of the related
								// read-only support.
								return new ReadOnlyMultisetWrapper<>(
										CollectionUtils
												.<Integer> observableHashMultiset());
							}
						} } });
	}

	private ObservableMultiset<Integer> observable;
	private Provider<ObservableMultiset<Integer>> observableProvider;
	private InvalidationExpector invalidationListener;
	private MultisetChangeExpector<Integer> multisetChangeListener;

	public ObservableMultisetTests(
			Provider<ObservableMultiset<Integer>> sourceProvider) {
		this.observableProvider = sourceProvider;
	}

	@Test
	public void add() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		registerListeners();

		// add a single value
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		assertEquals(backupMultiset.add(1), observable.add(1));
		check(observable, backupMultiset);
		checkListeners();

		// add a second occurrence of the same value
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		assertEquals(backupMultiset.add(1), observable.add(1));
		check(observable, backupMultiset);
		checkListeners();

		// add a different value
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(2, 0, 1);
		assertEquals(backupMultiset.add(2), observable.add(2));
		check(observable, backupMultiset);
		checkListeners();
	}

	@Test
	public void add_withCount() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		registerListeners();

		// add zero occurrences (no change expected)
		assertEquals(backupMultiset.add(1, 0), observable.remove(1, 0));
		check(observable, backupMultiset);
		invalidationListener.check();
		multisetChangeListener.check();

		// add a single value multiple times
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(5, 0, 5);
		assertEquals(backupMultiset.add(5, 5), observable.add(5, 5));
		check(observable, backupMultiset);
		checkListeners();

		// add a value zero times (no events should occur)
		assertEquals(backupMultiset.add(1, 0), observable.add(1, 0));
		check(observable, backupMultiset);
		checkListeners();
	}

	@Test
	public void addAll() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		registerListeners();

		// add a collection with three values
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		multisetChangeListener.addElementaryExpection(2, 0, 2);
		multisetChangeListener.addElementaryExpection(3, 0, 3);
		Multiset<Integer> toAdd = HashMultiset.create();
		toAdd.add(1);
		toAdd.add(2, 2);
		toAdd.add(3, 3);
		assertEquals(backupMultiset.addAll(toAdd), observable.addAll(toAdd));
		check(observable, backupMultiset);
		checkListeners();

		// add another collection with three values
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(2, 0, 2);
		multisetChangeListener.addElementaryExpection(4, 0, 3);
		toAdd = HashMultiset.create();
		toAdd.add(2, 2);
		toAdd.add(4, 3);
		assertEquals(backupMultiset.addAll(toAdd), observable.addAll(toAdd));
		check(observable, backupMultiset);
		checkListeners();
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

	protected void checkListeners() {
		invalidationListener.check();
		multisetChangeListener.check();
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
		registerListeners();

		// clear
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 1, 0);
		multisetChangeListener.addElementaryExpection(2, 2, 0);
		multisetChangeListener.addElementaryExpection(3, 3, 0);
		observable.clear();
		backupMultiset.clear();
		check(observable, backupMultiset);
		checkListeners();

		// clear again (while already empty)
		invalidationListener.expect(0);
		observable.clear();
		check(observable, backupMultiset);
		checkListeners();
	}

	@Test
	public void listenersNotProperlyIterating() {
		MultisetChangeListener<Integer> multisetChangeListener = new MultisetChangeListener<Integer>() {

			@Override
			public void onChanged(
					org.eclipse.gef.common.collections.MultisetChangeListener.Change<? extends Integer> change) {
				// initially cursor is left of first change
				try {
					// call getElement() without next
					change.getElement();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getElement() can be called.",
							e.getMessage());
				}
				try {
					// call getAddCount() without next
					change.getAddCount();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getAddCount() can be called.",
							e.getMessage());
				}
				try {
					// call getRemoveCount() without next
					change.getRemoveCount();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getRemoveCount() can be called.",
							e.getMessage());
				}
				// put cursor right of last change
				while (change.next()) {
				}
				change.next();
				try {
					// call getElement() without next
					change.getElement();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getElement() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getAddCount() without next
					change.getAddCount();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getAddCount() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getRemoveCount() without next
					change.getRemoveCount();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getRemoveCount() if next() returned true.",
							e.getMessage());
				}
			}
		};
		observable.addListener(multisetChangeListener);

		// ensure no concurrent modification exceptions result
		observable.add(1);
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
					org.eclipse.gef.common.collections.MultisetChangeListener.Change<? extends Integer> change) {
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
		observable.addListener(multisetChangeListener);
		observable.addListener(multisetChangeListener);
		// add and remove should have no effect
		// add and remove should have no effect
		MultisetChangeListener<Integer> multisetChangeListener2 = new MultisetChangeListener<Integer>() {

			@Override
			public void onChanged(
					org.eclipse.gef.common.collections.MultisetChangeListener.Change<? extends Integer> change) {
				// ignore
			}
		};
		observable.addListener(multisetChangeListener2);
		observable.removeListener(multisetChangeListener2);

		// perform add
		invalidationListener.expect(2);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		assertTrue(observable.add(1));
		invalidationListener.check();
		multisetChangeListener.check();

		// remove single listener occurrence
		observable.removeListener(invalidationListener);
		observable.removeListener(multisetChangeListener);

		// perform another add
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		assertTrue(observable.add(1));
		invalidationListener.check();
		multisetChangeListener.check();

		// remove listeners and ensure no notifications are received
		observable.removeListener(invalidationListener);
		observable.removeListener(multisetChangeListener);
		invalidationListener.check();
		multisetChangeListener.check();
	}

	protected void registerListeners() {
		invalidationListener = new InvalidationExpector();
		multisetChangeListener = new MultisetChangeExpector<>(observable);
		observable.addListener(invalidationListener);
		observable.addListener(multisetChangeListener);
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
		registerListeners();

		// remove (first occurrence of) value
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(2, 1, 0);
		assertEquals(backupMultiset.remove(2), observable.remove(2));
		check(observable, backupMultiset);
		checkListeners();

		// remove (second occurrence of) value
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(2, 1, 0);
		assertEquals(backupMultiset.remove(2), observable.remove(2));
		check(observable, backupMultiset);
		checkListeners();

		// remove not contained value (no change expected)
		assertEquals(backupMultiset.remove(2), observable.remove(2));
		check(observable, backupMultiset);
		checkListeners();
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
		registerListeners();

		// remove zero occurrences (no change expected)
		assertEquals(backupMultiset.remove(3, 0), observable.remove(3, 0));
		check(observable, backupMultiset);
		checkListeners();

		// remove (two occurrences of) value
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(3, 2, 0);
		assertEquals(backupMultiset.remove(3, 2), observable.remove(3, 2));
		check(observable, backupMultiset);
		checkListeners();

		// remove more occurrences than contained (change contains fewer
		// occurrences)
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(3, 1, 0);
		assertEquals(backupMultiset.remove(3, 2), observable.remove(3, 2));
		check(observable, backupMultiset);
		checkListeners();

		// remove not contained value (no change expected)
		assertEquals(backupMultiset.remove(3, 1), observable.remove(3, 1));
		check(observable, backupMultiset);
		checkListeners();
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
		registerListeners();

		// remove collection
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 1, 0);
		// all occurrences of 2 will be removed, even if toRemove contains fewer
		// occurrences.
		multisetChangeListener.addElementaryExpection(2, 2, 0);
		Multiset<Integer> toRemove = HashMultiset.create();
		toRemove.add(1);
		toRemove.add(2, 1);
		toRemove.add(4, 4);
		assertEquals(backupMultiset.removeAll(toRemove),
				observable.removeAll(toRemove));
		check(observable, backupMultiset);
		checkListeners();
	}

	@Test
	public void replaceAll() {
		// initialize multiset with some values
		observable.add(1, 1);
		observable.add(2, 2);
		observable.add(3, 3);
		observable.add(4, 4);

		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		backupMultiset.add(1, 1);
		backupMultiset.add(2, 2);
		backupMultiset.add(3, 3);
		backupMultiset.add(4, 4);
		check(observable, backupMultiset);

		// register listeners
		registerListeners();

		// replaceAll
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(2, 1, 0); // decrease
																// count
		multisetChangeListener.addElementaryExpection(4, 4, 0); // remove
		multisetChangeListener.addElementaryExpection(3, 0, 3); // increase
																// count
		multisetChangeListener.addElementaryExpection(5, 0, 5); // add

		Multiset<Integer> toReplace = HashMultiset.create();
		toReplace.add(1);
		toReplace.add(2, 1);
		toReplace.add(3, 6);
		toReplace.add(5, 5);

		observable.replaceAll(toReplace);
		backupMultiset.clear();
		backupMultiset.addAll(toReplace);
		check(observable, backupMultiset);
		checkListeners();

		// replace with same contents (should not have any effect)
		invalidationListener.expect(0);
		observable.replaceAll(toReplace);
		check(observable, backupMultiset);
		checkListeners();
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
		registerListeners();

		// remove collection
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(3, 3, 0);
		Multiset<Integer> toRetain = HashMultiset.create();
		toRetain.add(1);
		toRetain.add(2, 1);
		toRetain.add(4, 4);
		assertEquals(backupMultiset.retainAll(toRetain),
				observable.retainAll(toRetain));
		check(observable, backupMultiset);
		checkListeners();
	}

	@Test
	public void setCount() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		registerListeners();

		// set count for non contained element
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		assertEquals(backupMultiset.setCount(1, 1), observable.setCount(1, 1));
		check(observable, backupMultiset);
		checkListeners();

		// increase count for already contained element
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 3);
		assertEquals(backupMultiset.setCount(1, 4), observable.setCount(1, 4));
		check(observable, backupMultiset);
		checkListeners();

		// decrease count for already contained element
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 2, 0);
		assertEquals(backupMultiset.setCount(1, 2), observable.setCount(1, 2));
		check(observable, backupMultiset);
		checkListeners();
	}

	@Test
	public void setCount_withOld() {
		// prepare backup multiset
		Multiset<Integer> backupMultiset = HashMultiset.create();
		check(observable, backupMultiset);

		// register listeners
		registerListeners();

		// set count for non contained element
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 2);
		assertEquals(backupMultiset.setCount(1, 0, 2),
				observable.setCount(1, 0, 2));
		check(observable, backupMultiset);
		checkListeners();

		// set count to increase occurrences
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 0, 1);
		assertEquals(backupMultiset.setCount(1, 2, 3),
				observable.setCount(1, 2, 3));
		check(observable, backupMultiset);
		checkListeners();

		// set count to decrease occurrences
		invalidationListener.expect(1);
		multisetChangeListener.addAtomicExpectation();
		multisetChangeListener.addElementaryExpection(1, 1, 0);
		assertEquals(backupMultiset.setCount(1, 3, 2),
				observable.setCount(1, 3, 2));
		check(observable, backupMultiset);
		checkListeners();

		// set count where old value is not met (no change expected)
		assertEquals(backupMultiset.setCount(1, 4, 3),
				observable.setCount(1, 4, 3));
		check(observable, backupMultiset);
		checkListeners();
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
