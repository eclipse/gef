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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.beans.property.SimpleListPropertyEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ListListenerHelperEx.AtomicChange;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Provider;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Tests for correct behavior of {@link ObservableList} implementations,
 * including respective {@link ObservableValue observable values}and
 * {@link ListChangeListenerEx} helper classes. Concrete implementations are
 * tested by parameterizing the test with a respective Provider, which is done
 * for {@link ObservableListWrapperEx} as well as {@link SimpleListPropertyEx}
 * and {@link ReadOnlyListWrapperEx}.
 * <p>
 * Ensures that correct behavior of the underlying {@link List} is preserved and
 * that {@link InvalidationListener} and {@link ListChangeListener}, as well as
 * {@link ChangeListener} (in case of observable values) are notified properly.
 * <p>
 * Test strategy is to use a backup {@link List} on which to apply the same
 * operations as on the two be tested {@link ObservableList}, so that same
 * behavior is ensured.
 *
 * @author anyssen
 *
 */
@RunWith(Parameterized.class)
public class ObservableListTests {

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

	protected static class ListChangeExpector<E>
			implements ListChangeListener<E> {

		private ObservableList<E> source;
		private LinkedList<LinkedList<List<E>>> addedElementsQueue = new LinkedList<>();
		private LinkedList<LinkedList<List<E>>> removedElementsQueue = new LinkedList<>();
		private LinkedList<LinkedList<int[]>> permutationsQueue = new LinkedList<>();
		private LinkedList<LinkedList<Integer>> fromQueue = new LinkedList<>();
		private LinkedList<LinkedList<Integer>> toQueue = new LinkedList<>();
		private ArrayList<E> previousValue;

		public ListChangeExpector(ObservableList<E> source) {
			this.source = source;
		}

		public void addAtomicExpectation() {
			addedElementsQueue.addFirst(new LinkedList<List<E>>());
			removedElementsQueue.addFirst(new LinkedList<List<E>>());
			permutationsQueue.addFirst(new LinkedList<int[]>());
			fromQueue.addFirst(new LinkedList<Integer>());
			toQueue.addFirst(new LinkedList<Integer>());

			// capture previous value
			this.previousValue = new ArrayList<>(source);
		}

		public void addElementaryExpectation(List<E> removedElements,
				List<E> addedElements, int[] permutations, int from, int to) {
			if (addedElementsQueue.size() <= 0) {
				throw new IllegalArgumentException(
						"Add atomic expectation first.");
			}
			removedElementsQueue.getFirst().addFirst(removedElements);
			addedElementsQueue.getFirst().addFirst(addedElements);
			permutationsQueue.getFirst().addFirst(permutations);
			fromQueue.getFirst().addFirst(from);
			toQueue.getFirst().addFirst(to);
		}

		public void check() {
			if (addedElementsQueue.size() > 0) {
				fail("Did not receive " + addedElementsQueue.size()
						+ " expected changes.");
			}
		}

		@Override
		public void onChanged(ListChangeListener.Change<? extends E> change) {
			if (addedElementsQueue.size() <= 0) {
				fail("Received unexpected atomic change " + change);
			}

			LinkedList<List<E>> elementaryRemovedElementsQueue = removedElementsQueue
					.pollLast();
			LinkedList<List<E>> elementaryAddedElementsQueue = addedElementsQueue
					.pollLast();
			LinkedList<int[]> elementaryPermutationsQueue = permutationsQueue
					.pollLast();
			LinkedList<Integer> elementaryFrom = fromQueue.pollLast();
			LinkedList<Integer> elementaryTo = toQueue.pollLast();

			assertEquals(source, change.getList());

			StringBuffer expectedString = new StringBuffer();
			while (change.next()) {
				if (elementaryAddedElementsQueue.size() <= 0) {
					fail("Did not expect another elementary change, but received "
							+ change);
				}
				// check removed
				List<E> expectedRemovedElements = elementaryRemovedElementsQueue
						.pollLast();
				if (expectedRemovedElements != null) {
					assertTrue(change.wasRemoved());
					assertEquals(expectedRemovedElements, change.getRemoved());
					assertEquals(expectedRemovedElements.size(),
							change.getRemovedSize());
				} else {
					assertFalse(change.wasRemoved());
				}

				// check added
				List<E> expectedAddedElements = elementaryAddedElementsQueue
						.pollLast();
				if (expectedAddedElements != null) {
					assertTrue(change.wasAdded());
					if (expectedRemovedElements != null) {
						assertTrue(change.wasReplaced());
					}
					assertEquals(expectedAddedElements,
							change.getAddedSubList());
					assertEquals(expectedAddedElements.size(),
							change.getAddedSize());
				} else {
					assertFalse(change.wasAdded());
				}

				// check permutations
				int[] expectedPermutations = elementaryPermutationsQueue
						.pollLast();
				if (expectedPermutations != null) {
					assertTrue(change.wasPermutated());
					assertArrayEquals(expectedPermutations,
							CollectionUtils.getPermutation(change));
					for (int i = 0; i < expectedPermutations.length; i++) {
						assertEquals(expectedPermutations[i],
								change.getPermutation(i));
					}
				} else {
					assertFalse(change.wasPermutated());
				}

				// check from
				int from = elementaryFrom.pollLast();
				assertEquals(from, change.getFrom());

				// check to
				int to = elementaryTo.pollLast();
				assertEquals(to, change.getTo());

				// check string representation
				if (!expectedString.toString().isEmpty()) {
					expectedString.append(" ");
				}
				if (expectedAddedElements != null
						&& expectedRemovedElements != null) {
					expectedString.append("Replaced" + expectedRemovedElements
							+ " by " + expectedAddedElements + " at "
							+ change.getFrom() + ".");
				} else if (expectedAddedElements != null) {
					expectedString.append("Added" + expectedAddedElements
							+ " at " + change.getFrom() + ".");
				} else if (expectedRemovedElements != null) {
					expectedString.append("Removed" + expectedRemovedElements
							+ " at " + change.getFrom() + ".");
				} else {
					expectedString.append("Permutated by "
							+ Arrays.toString(
									CollectionUtils.getPermutation(change))
							+ ".");
				}
			}
			if (elementaryAddedElementsQueue.size() > 0) {
				fail("Did not receive " + elementaryAddedElementsQueue.size()
						+ " expected elementary changes.");
			}

			// check string representation of change (only in case of atomic
			// change)
			if (change instanceof AtomicChange) {
				assertEquals(expectedString.toString(), change.toString());
			}

			// check previous value of change (only in case of JavaFX change)
			if (!(change instanceof AtomicChange)) {
				assertEquals(previousValue,
						CollectionUtils.getPreviousContents(change));
			}
		}
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] { { new Provider<ObservableList<Integer>>() {
					@Override
					public ObservableList<Integer> get() {
						return CollectionUtils
								.observableList(new ArrayList<Integer>());

					}
				} }, { new Provider<ObservableList<Integer>>() {

					@Override
					public ObservableList<Integer> get() {
						// test JavaFX behavior
						return FXCollections.observableArrayList();
					}
				} }, { new Provider<ObservableList<Integer>>() {

					@Override
					public ObservableList<Integer> get() {
						// test SimpleListPropertyEx, which is the
						// 'default' implementation of the related
						// ObservableValue.
						return new SimpleListPropertyEx<>(CollectionUtils
								.observableList(new ArrayList<Integer>()));
					}
				} },

						{ new Provider<ObservableList<Integer>>() {

							@Override
							public ObservableList<Integer> get() {
								return new SimpleListProperty<>(FXCollections
										.<Integer> observableArrayList());
							}
						} },

						{ new Provider<ObservableList<Integer>>() {

							@Override
							public ObservableList<Integer> get() {
								// test ReadOnlyListWrapperEx, which is the
								// 'default' implementation of the related
								// read-only support.
								return new ReadOnlyListWrapperEx<>(
										CollectionUtils.observableList(
												new ArrayList<Integer>()));

							}
						} }/*
							 * , { new Provider<ObservableList<Integer>>() {
							 *
							 * @Override public ObservableList<Integer> get() {
							 * return new ReadOnlyListWrapper<>(
							 * FXCollections.<Integer> observableArrayList()); }
							 * } }
							 */ });
	}

	private ObservableList<Integer> observable;
	private Provider<ObservableList<Integer>> observableProvider;
	private InvalidationExpector invalidationListener;
	private ListChangeExpector<Integer> listChangeListener;

	public ObservableListTests(
			Provider<ObservableList<Integer>> sourceProvider) {
		this.observableProvider = sourceProvider;
	}

	@Test
	public void add() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		registerListeners();

		// add a single value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Collections.<Integer> singletonList(1), null, 0, 1);
		assertEquals(backupList.add(1), observable.add(1));
		check(observable, backupList);
		checkListeners();

		// add a different value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Collections.<Integer> singletonList(2), null, 1, 2);
		assertEquals(backupList.add(2), observable.add(2));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void add_with_index() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		observable.addAll(Arrays.asList(1, 2, 3));
		backupList.addAll(Arrays.asList(1, 2, 3));

		// register listeners
		registerListeners();

		// add a single value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Collections.<Integer> singletonList(4), null, 2, 3);
		backupList.add(2, 4);
		observable.add(2, 4);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void addAll() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		// register listeners
		registerListeners();

		// add a single value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Arrays.asList(1, 2, 3, 4, 5), null, 0, 5);
		backupList.addAll(Arrays.asList(1, 2, 3, 4, 5));
		observable.addAll(Arrays.asList(1, 2, 3, 4, 5));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void addAll_varargs() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		// register listeners
		registerListeners();

		// add a single value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Arrays.asList(1, 2, 3, 4, 5), null, 0, 5);
		backupList.addAll(Arrays.asList(1, 2, 3, 4, 5));
		observable.addAll(1, 2, 3, 4, 5);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void addAll_with_index() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		observable.addAll(Arrays.asList(1, 2, 3));
		backupList.addAll(Arrays.asList(1, 2, 3));

		// register listeners
		registerListeners();

		// add a single value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Arrays.asList(1, 2, 3, 4, 5), null, 3, 8);
		backupList.addAll(3, Arrays.asList(1, 2, 3, 4, 5));
		observable.addAll(3, Arrays.asList(1, 2, 3, 4, 5));
		check(observable, backupList);
		checkListeners();
	}

	@Before
	public void before() {
		observable = observableProvider.get();
	}

	protected void check(ObservableList<Integer> observable,
			List<Integer> backupList) {
		assertEquals(backupList, observable);
		if (observable instanceof ReadOnlyListWrapperEx) {
			assertEquals(backupList,
					((ReadOnlyListWrapperEx<Integer>) observable)
							.getReadOnlyProperty().get());
		}
	}

	protected void checkListeners() {
		invalidationListener.check();
		listChangeListener.check();
	}

	@Test
	public void clear() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// clear
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(1, 2, 3),
				null, null, 0, 0);
		observable.clear();
		backupList.clear();
		check(observable, backupList);
		checkListeners();

		// clear again (while already empty)
		observable.clear();
		check(observable, backupList);
		checkListeners();
	}

	/**
	 * Tests that exceptions, which are thrown in the context of listener
	 * notification are captured and forwarded to the UncaughtExceptionHandler
	 * registered for the Thread.
	 */
	@Test
	public void exceptionHandling() throws InterruptedException {
		// invalidation listeners
		final boolean[] caughtException = new boolean[] { false };
		final boolean[] uncaughtException = new boolean[] { false };
		// XXX: Run test in own thread as otherwise junit will already catch the
		// uncaught exception before the registered handler.
		Thread testThread = new Thread() {
			@Override
			public void run() {
				InvalidationListener listener = new InvalidationListener() {

					@Override
					public void invalidated(Observable arg0) {
						throw new IllegalArgumentException(
								"expected invalidation");
					}
				};
				observable.addListener(listener);
				Thread.currentThread().setUncaughtExceptionHandler(
						new UncaughtExceptionHandler() {
							@Override
							public void uncaughtException(Thread t,
									Throwable e) {
								if (e.getMessage()
										.equals("expected invalidation")) {
									caughtException[0] = true;
								}
							}
						});
				try {
					observable.add(1);
				} catch (IllegalArgumentException e) {
					if (e.getMessage().equals("exptected invalidation")) {
						uncaughtException[0] = true;
					}
				}
				observable.removeListener(listener);
			}
		};
		testThread.start();
		testThread.join();
		assertTrue(caughtException[0]);
		assertFalse(uncaughtException[0]);

		// list change listeners
		caughtException[0] = false;
		uncaughtException[0] = false;
		testThread = new Thread() {
			@Override
			public void run() {
				ListChangeListener<Integer> listener = new ListChangeListener<Integer>() {

					@Override
					public void onChanged(
							ListChangeListener.Change<? extends Integer> c) {
						throw new IllegalArgumentException(
								"expected list change");
					}

				};
				observable.addListener(listener);
				Thread.currentThread().setUncaughtExceptionHandler(
						new UncaughtExceptionHandler() {
							@Override
							public void uncaughtException(Thread t,
									Throwable e) {
								if (e.getMessage()
										.equals("expected list change")) {
									caughtException[0] = true;
								}
							}
						});
				try {
					observable.add(1);
				} catch (IllegalArgumentException e) {
					if (e.getMessage().equals("exptected list change")) {
						uncaughtException[0] = true;
					}
				}
				observable.removeListener(listener);
			}
		};
		testThread.start();
		testThread.join();
		assertTrue(caughtException[0]);
		assertFalse(uncaughtException[0]);

		// change listeners
		// TODO: change listener notifications are not "guarded" by try/catch.
		// if (observable instanceof ObservableValue) {
		// caughtException[0] = false;
		// uncaughtException[0] = false;
		// testThread = new Thread() {
		// @SuppressWarnings("unchecked")
		// @Override
		// public void run() {
		// ObservableValue<ObservableList<Integer>> observableValue =
		// (ObservableValue<ObservableList<Integer>>) observable;
		// ChangeListener<ObservableList<Integer>> listener = new
		// ChangeListener<ObservableList<Integer>>() {
		//
		// @Override
		// public void changed(
		// ObservableValue<? extends ObservableList<Integer>> observable,
		// ObservableList<Integer> oldValue,
		// ObservableList<Integer> newValue) {
		// throw new IllegalArgumentException(
		// "expected change");
		// }
		// };
		// observableValue.addListener(listener);
		// Thread.currentThread().setUncaughtExceptionHandler(
		// new UncaughtExceptionHandler() {
		// @Override
		// public void uncaughtException(Thread t, Throwable e) {
		// if (e.getMessage().equals("expected change")) {
		// caughtException[0] = true;
		// }
		// }
		// });
		// try {
		// ((ListPropertyBase<Integer>) observableValue)
		// .set(CollectionUtils
		// .observableList(Arrays.asList(3)));
		// } catch (IllegalArgumentException e) {
		// if (e.getMessage().equals("expected change")) {
		// uncaughtException[0] = true;
		// }
		// }
		// observableValue.removeListener(listener);
		// }
		// };
		// testThread.start();
		// testThread.join();
		// assertTrue(caughtException[0]);
		// assertFalse(uncaughtException[0]);
		// }
	}

	@Ignore("See #518221")
	@Test
	public void listenersNotProperlyIterating() {
		// ensure assumption exceptions can be properly handled by JUnit
		Thread.currentThread()
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						if (e instanceof RuntimeException) {
							throw (RuntimeException) e;
						}
					}
				});

		ListChangeListener<Integer> listChangeListener = new ListChangeListener<Integer>() {

			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Integer> change) {

				assumeTrue("Skip for all except ObservableListWrapperEx",
						observable.getClass().getSimpleName()
								.equals("ObservableListWrapperEx"));
				// initially cursor is left of first change
				try {
					// call wasReplaced() without next
					change.wasReplaced();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before wasReplaced() can be called.",
							e.getMessage());
				}
				try {
					// call wasAdded() without next
					change.wasAdded();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before wasAdded() can be called.",
							e.getMessage());
				}
				try {
					// call wasRemoved() without next
					change.wasRemoved();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before wasRemoved() can be called.",
							e.getMessage());
				}
				try {
					// call wasPermutated() without next
					change.wasPermutated();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before wasPermutated() can be called.",
							e.getMessage());
				}
				try {
					// call wasUpdated() without next
					change.wasUpdated();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before wasUpdated() can be called.",
							e.getMessage());
				}

				try {
					// call getAddedSubList() without next
					change.getAddedSubList();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getAddedSubList() can be called.",
							e.getMessage());
				}

				try {
					// call getAddedSize() without next
					change.getAddedSize();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getAddedSize() can be called.",
							e.getMessage());
				}

				try {
					// call getRemoved() without next
					change.getRemoved();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getRemoved() can be called.",
							e.getMessage());
				}

				try {
					// call getRemovedSize() without next
					change.getRemovedSize();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getRemovedSize() can be called.",
							e.getMessage());
				}

				try {
					// call getPermutation(int) without next
					change.getPermutation(0);
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getPermutation(int) can be called.",
							e.getMessage());
				}

				try {
					// call getFrom() without next
					change.getFrom();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getFrom() can be called.",
							e.getMessage());
				}

				try {
					// call getTo() without next
					change.getTo();
					fail("Expect IllegalArgumentException, because next() has not been called.");
				} catch (IllegalStateException e) {
					assertEquals(
							"Need to call next() before getTo() can be called.",
							e.getMessage());
				}

				// put cursor right of last change
				while (change.next()) {
				}

				change.next();
				try {
					// call wasReplaced() without next
					change.wasReplaced();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call wasReplaced() if next() returned true.",
							e.getMessage());
				}
				try {
					// call wasAdded() without next
					change.wasAdded();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call wasAdded() if next() returned true.",
							e.getMessage());
				}
				try {
					// call wasRemoved() without next
					change.wasRemoved();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call wasRemoved() if next() returned true.",
							e.getMessage());
				}
				try {
					// call wasPermutated() without next
					change.wasPermutated();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call wasPermutated() if next() returned true.",
							e.getMessage());
				}
				try {
					// call wasUpdated() without next
					change.wasUpdated();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call wasUpdated() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getAddedSubList() without next
					change.getAddedSubList();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getAddedSubList() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getAddedSize() without next
					change.getAddedSize();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getAddedSize() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getRemoved() without next
					change.getRemoved();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getRemoved() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getRemovedSize() without next
					change.getRemovedSize();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getRemovedSize() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getPermutation(int) without next
					change.getPermutation(0);
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getPermutation(int) if next() returned true.",
							e.getMessage());
				}
				try {
					// call getFrom() without next
					change.getFrom();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getFrom() if next() returned true.",
							e.getMessage());
				}
				try {
					// call getTo() without next
					change.getTo();
					fail("Expect IllegalArgumentException, because next() return value has not been respected.");
				} catch (IllegalStateException e) {
					assertEquals(
							"May only call getTo() if next() returned true.",
							e.getMessage());
				}

			}
		};
		observable.addListener(listChangeListener);

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
			ObservableValue<ObservableList<Integer>> observableValue = (ObservableValue<ObservableList<Integer>>) observable;
			ChangeListener<ObservableList<Integer>> changeListener = new ChangeListener<ObservableList<Integer>>() {

				@Override
				public void changed(
						ObservableValue<? extends ObservableList<Integer>> observable,
						ObservableList<Integer> oldValue,
						ObservableList<Integer> newValue) {
					// unregister ourselves
					observable.removeListener(this);

					// register ourselves (again)
					observable.addListener(this);
				}
			};
			observableValue.addListener(changeListener);
		}
		ListChangeListener<Integer> listChangeListener = new ListChangeListener<Integer>() {

			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Integer> change) {
				// unregister ourselves
				change.getList().removeListener(this);

				// register ourselves (again)
				change.getList().addListener(this);
			}
		};
		observable.addListener(listChangeListener);

		// ensure no concurrent modification exceptions result
		observable.add(1);
	}

	@Test
	public void listenersRegisteredMoreThanOnce() {
		// register listeners (twice)
		InvalidationExpector invalidationListener = new InvalidationExpector();
		ListChangeExpector<Integer> listChangeListener = new ListChangeExpector<>(
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
		observable.addListener(listChangeListener);
		observable.addListener(listChangeListener);
		// add and remove should have no effect
		// add and remove should have no effect
		ListChangeListener<Integer> listChangeListener2 = new ListChangeListener<Integer>() {

			@Override
			public void onChanged(
					ListChangeListener.Change<? extends Integer> change) {
				// ignore
			}
		};
		observable.addListener(listChangeListener2);
		observable.removeListener(listChangeListener2);

		// perform add
		invalidationListener.expect(2);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Collections.singletonList(1), null, 0, 1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Collections.singletonList(1), null, 0, 1);
		assertTrue(observable.add(1));
		invalidationListener.check();
		listChangeListener.check();

		// remove single listener occurrence
		observable.removeListener(invalidationListener);
		observable.removeListener(listChangeListener);

		// perform another add
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null,
				Collections.singletonList(1), null, 1, 2);
		assertTrue(observable.add(1));
		invalidationListener.check();
		listChangeListener.check();

		// remove listeners and ensure no notifications are received
		observable.removeListener(invalidationListener);
		observable.removeListener(listChangeListener);
		invalidationListener.check();
		listChangeListener.check();
	}

	protected void registerListeners() {
		invalidationListener = new InvalidationExpector();
		listChangeListener = new ListChangeExpector<>(observable);
		observable.addListener(invalidationListener);
		observable.addListener(listChangeListener);
	}

	@Test
	public void remove() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// clear
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2), null,
				null, 1, 1);
		observable.remove(1);
		backupList.remove(1);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void remove_from_to() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);
		observable.add(4);
		observable.add(5);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		backupList.add(4);
		backupList.add(5);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// clear
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2, 3), null,
				null, 1, 1);
		observable.remove(1, 3);
		backupList.remove(2);
		backupList.remove(1);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void remove_object() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// clear
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(1), null,
				null, 0, 0);
		observable.remove((Object) 1);
		backupList.remove((Object) 1);
		check(observable, backupList);
		checkListeners();

		// remove not contained element
		invalidationListener.expect(0);
		observable.remove((Object) 5);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void removeAll() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);
		observable.add(4);
		observable.add(5);
		observable.add(6);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		backupList.add(4);
		backupList.add(5);
		backupList.add(6);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// remove all (elements are not continuous)
		invalidationListener.expect(1);
		// we expect two changes, as the deleted elements are not 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2), null,
				null, 1, 1);
		// after the initial delete, 4 is now located at index 2
		listChangeListener.addElementaryExpectation(Arrays.asList(4), null,
				null, 2, 2);
		observable.removeAll(Arrays.asList(4, 2));
		backupList.removeAll(Arrays.asList(4, 2));
		check(observable, backupList);
		checkListeners();

		// remove all (elements are continuous)
		invalidationListener.expect(1);
		// we expect a single change, as the deleted elements are 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(3, 5), null,
				null, 1, 1);
		observable.removeAll(Arrays.asList(5, 3));
		backupList.removeAll(Arrays.asList(5, 3));
		check(observable, backupList);
		checkListeners();

		// remove all (no effect, elements were already removed)
		observable.removeAll(Arrays.asList(5, 3));
		backupList.removeAll(Arrays.asList(5, 3));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void removeAll_varargs() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);
		observable.add(4);
		observable.add(5);
		observable.add(6);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		backupList.add(4);
		backupList.add(5);
		backupList.add(6);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// remove all (elements are not continuous)
		invalidationListener.expect(1);
		// we expect two changes, as the deleted elements are not 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2), null,
				null, 1, 1);
		// after the initial delete, 4 is now located at index 2
		listChangeListener.addElementaryExpectation(Arrays.asList(4), null,
				null, 2, 2);
		observable.removeAll(4, 2);
		backupList.removeAll(Arrays.asList(4, 2));
		check(observable, backupList);
		checkListeners();

		// remove all (elements are continuous)
		invalidationListener.expect(1);
		// we expect a single change, as the deleted elements are 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(3, 5), null,
				null, 1, 1);
		observable.removeAll(5, 3);
		backupList.removeAll(Arrays.asList(5, 3));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void retainAll() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);
		observable.add(4);
		observable.add(5);
		observable.add(6);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		backupList.add(4);
		backupList.add(5);
		backupList.add(6);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// remove all (elements are not continuous)
		invalidationListener.expect(1);
		// we expect two changes, as the deleted elements are not 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2), null,
				null, 1, 1);
		// after the initial delete, 4 is now located at index 2
		listChangeListener.addElementaryExpectation(Arrays.asList(4), null,
				null, 2, 2);
		observable.retainAll(Arrays.asList(1, 3, 5, 6));
		backupList.retainAll(Arrays.asList(1, 3, 5, 6));
		check(observable, backupList);
		checkListeners();

		// remove all (elements are continuous)
		invalidationListener.expect(1);
		// we expect a single change, as the deleted elements are 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(3, 5), null,
				null, 1, 1);
		observable.retainAll(Arrays.asList(1, 6));
		backupList.retainAll(Arrays.asList(1, 6));
		check(observable, backupList);
		checkListeners();

		// no change expected as no further elements are removed
		observable.retainAll(Arrays.asList(1, 6));
		backupList.retainAll(Arrays.asList(1, 6));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void retainAll_varargs() {
		// initialize list with some values
		observable.add(1);
		observable.add(2);
		observable.add(3);
		observable.add(4);
		observable.add(5);
		observable.add(6);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(1);
		backupList.add(2);
		backupList.add(3);
		backupList.add(4);
		backupList.add(5);
		backupList.add(6);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// remove all (elements are not continuous)
		invalidationListener.expect(1);
		// we expect two changes, as the deleted elements are not 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(2), null,
				null, 1, 1);
		// after the initial delete, 4 is now located at index 2
		listChangeListener.addElementaryExpectation(Arrays.asList(4), null,
				null, 2, 2);
		observable.retainAll(1, 3, 5, 6);
		backupList.retainAll(Arrays.asList(1, 3, 5, 6));
		check(observable, backupList);
		checkListeners();

		// remove all (elements are continuous)
		invalidationListener.expect(1);
		// we expect a single change, as the deleted elements are 'continuous'
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(3, 5), null,
				null, 1, 1);
		observable.retainAll(1, 6);
		backupList.retainAll(Arrays.asList(1, 6));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void set_with_index() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		observable.addAll(Arrays.asList(1, 2, 3));
		backupList.addAll(Arrays.asList(1, 2, 3));

		// register listeners
		registerListeners();

		// set different value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(
				Collections.<Integer> singletonList(2),
				Collections.<Integer> singletonList(4), null, 1, 2);
		backupList.set(1, 4);
		observable.set(1, 4);
		check(observable, backupList);
		checkListeners();

		assumeTrue(
				"Skip for all except ObservableListWrapperEx, SimpleListPropertyEx, or ReadOnlyListWrapperEx",
				observable.getClass().getSimpleName()
						.equals("ObservableListWrapperEx")
						|| observable instanceof SimpleListPropertyEx
						|| observable instanceof ReadOnlyListWrapperEx);

		// set same value (no notifications expected)
		backupList.set(1, 4);
		observable.set(1, 4);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void setAll() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		observable.addAll(Arrays.asList(1, 2, 3));
		backupList.addAll(Arrays.asList(1, 2, 3));

		// register listeners
		registerListeners();

		// set different value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(1, 2, 3),
				Arrays.asList(3, 4, 5), null, 0, 3);
		backupList.clear();
		backupList.addAll(Arrays.asList(3, 4, 5));
		observable.setAll(Arrays.asList(3, 4, 5));
		check(observable, backupList);
		checkListeners();

		assumeTrue(
				"Skip for all except ObservableListWrapperEx, SimpleListPropertyEx, or ReadOnlyListWrapperEx",
				observable.getClass().getSimpleName()
						.equals("ObservableListWrapperEx")
						|| observable instanceof SimpleListPropertyEx
						|| observable instanceof ReadOnlyListWrapperEx);

		// set same value (no notifications expected)
		observable.setAll(Arrays.asList(3, 4, 5));
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void setAll_varargs() {
		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		check(observable, backupList);

		observable.addAll(Arrays.asList(1, 2, 3));
		backupList.addAll(Arrays.asList(1, 2, 3));

		// register listeners
		registerListeners();

		// set different value
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(Arrays.asList(1, 2, 3),
				Arrays.asList(3, 4, 5), null, 0, 3);
		backupList.clear();
		backupList.addAll(Arrays.asList(3, 4, 5));
		observable.setAll(3, 4, 5);
		check(observable, backupList);
		checkListeners();

		assumeTrue(
				"Skip for all except ObservableListWrapperEx, SimpleListPropertyEx, or ReadOnlyListWrapperEx",
				observable.getClass().getSimpleName()
						.equals("ObservableListWrapperEx")
						|| observable instanceof SimpleListPropertyEx
						|| observable instanceof ReadOnlyListWrapperEx);

		// set same value (no notifications expected)
		observable.setAll(3, 4, 5);
		check(observable, backupList);
		checkListeners();
	}

	@Test
	public void sort() {
		assumeTrue("Skip for all except ObservableListWrapperEx", observable
				.getClass().getSimpleName().equals("ObservableListWrapperEx"));

		// initialize list with some values
		observable.add(3);
		observable.add(1);
		observable.add(2);

		// prepare backup list
		List<Integer> backupList = new ArrayList<>();
		backupList.add(3);
		backupList.add(1);
		backupList.add(2);
		check(observable, backupList);

		// register listeners
		registerListeners();

		// sort
		invalidationListener.expect(1);
		listChangeListener.addAtomicExpectation();
		listChangeListener.addElementaryExpectation(null, null,
				new int[] { 2, 0, 1 }, 0, 3);
		Collections.sort(observable);
		Collections.sort(backupList);
		check(observable, backupList);
		checkListeners();

		// sort again (while already empty)
		invalidationListener.expect(0);
		Collections.sort(observable);
		check(observable, backupList);
		checkListeners();
	}

	/**
	 * Confirm {@link ObservableList} works as expected even if no listeners are
	 * registered.
	 */
	@Test
	public void withoutListeners() {
		// add
		assertTrue(observable.add(1));
		assertTrue(observable.add(1));
		assertTrue(observable.add(1));
		assertTrue(observable.add(2));
		assertTrue(observable.add(2));
		assertEquals(5, observable.size());

		// clear
		observable.clear();
		assertTrue(observable.isEmpty());
	}

}
