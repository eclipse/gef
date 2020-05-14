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
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.beans.binding;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import org.eclipse.gef.common.collections.MultisetChangeListener;
import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.common.collections.SetMultimapChangeListener;

import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import javafx.beans.WeakListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.SetBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * A utility class that augments {@link Bindings} with functionality related to
 * {@link Multiset} and {@link SetMultimap}.
 *
 * @author anyssen
 *
 */
public class BindingUtils {

	private static class BidirectionalMultisetContentBinding<E>
			implements MultisetChangeListener<E>, WeakListener {

		private final WeakReference<ObservableMultiset<E>> multiset1Ref;
		private final WeakReference<ObservableMultiset<E>> multiset2Ref;

		private boolean updating = false;

		public BidirectionalMultisetContentBinding(
				ObservableMultiset<E> multiset1,
				ObservableMultiset<E> multiset2) {
			multiset1Ref = new WeakReference<>(multiset1);
			multiset2Ref = new WeakReference<>(multiset2);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}

			if (other == null
					|| !(other instanceof BidirectionalMultisetContentBinding)) {
				return false;
			}

			try {
				BidirectionalMultisetContentBinding<E> otherBinding = (BidirectionalMultisetContentBinding<E>) other;

				ObservableMultiset<E> multiset1 = multiset1Ref.get();
				ObservableMultiset<E> multiset2 = multiset2Ref.get();
				ObservableMultiset<E> otherMultiset1 = otherBinding.multiset1Ref
						.get();
				ObservableMultiset<E> otherMultiset2 = otherBinding.multiset2Ref
						.get();

				// The actual direction of the bidirectional binding is not
				// significant, thus we can ignore it here
				return (((multiset1 == otherMultiset1)
						&& (multiset2 == otherMultiset2))
						|| ((multiset1 == otherMultiset2)
								&& (multiset2 == otherMultiset1)));
			} catch (ClassCastException e) {
				return false;
			}
		}

		@Override
		public int hashCode() {
			// XXX: As we rely on equality to remove a binding again, we have to
			// ensure the hash code of two bindings that target the same
			// properties is the same (which we do by using a constant).
			return 0;
		}

		@Override
		public void onChanged(Change<? extends E> change) {
			if (!updating) {
				final ObservableMultiset<E> multiset1 = multiset1Ref.get();
				final ObservableMultiset<E> multiset2 = multiset2Ref.get();
				if ((multiset1 == null) || (multiset2 == null)) {
					if (multiset1 != null) {
						multiset1.removeListener(this);
					}
					if (multiset2 != null) {
						multiset2.removeListener(this);
					}
				} else {
					try {
						updating = true;
						final Multiset<E> destination = multiset1 == change
								.getMultiset() ? multiset2 : multiset1;
						// we use replaceValues() to perform an atomic change
						// here (and thus don't use the added and removed values
						// from the change)
						while (change.next()) {
							destination.setCount(change.getElement(),
									destination.count(change.getElement()),
									destination.count(change.getElement())
											+ change.getAddCount()
											- change.getRemoveCount());
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return (multiset1Ref.get() == null) || (multiset2Ref.get() == null);
		}
	}

	private static class BidirectionalSetMultimapContentBinding<K, V>
			implements SetMultimapChangeListener<K, V>, WeakListener {

		private final WeakReference<ObservableSetMultimap<K, V>> setMultimap1Ref;
		private final WeakReference<ObservableSetMultimap<K, V>> setMultimap2Ref;

		private boolean updating = false;

		public BidirectionalSetMultimapContentBinding(
				ObservableSetMultimap<K, V> setMultimap1,
				ObservableSetMultimap<K, V> setMultimap2) {
			setMultimap1Ref = new WeakReference<>(setMultimap1);
			setMultimap2Ref = new WeakReference<>(setMultimap2);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}

			if (other == null
					|| !(other instanceof BidirectionalSetMultimapContentBinding)) {
				return false;
			}

			try {
				BidirectionalSetMultimapContentBinding<K, V> otherBinding = (BidirectionalSetMultimapContentBinding<K, V>) other;

				ObservableSetMultimap<K, V> setMultimap1 = setMultimap1Ref
						.get();
				ObservableSetMultimap<K, V> setMultimap2 = setMultimap2Ref
						.get();
				ObservableSetMultimap<K, V> otherSetMultimap1 = otherBinding.setMultimap1Ref
						.get();
				ObservableSetMultimap<K, V> otherSetMultimap2 = otherBinding.setMultimap2Ref
						.get();

				// The actual direction of the bidirectional binding is not
				// significant, thus we can ignore it here
				return (((setMultimap1 == otherSetMultimap1)
						&& (setMultimap2 == otherSetMultimap2))
						|| ((setMultimap1 == otherSetMultimap2)
								&& (setMultimap2 == otherSetMultimap1)));
			} catch (ClassCastException e) {
				return false;
			}
		}

		@Override
		public int hashCode() {
			// XXX: As we rely on equality to remove a binding again, we have to
			// ensure the hash code of two bindings that target the same
			// properties is the same (which we do by using a constant).
			return 0;
		}

		@Override
		public void onChanged(Change<? extends K, ? extends V> change) {
			if (!updating) {
				final ObservableSetMultimap<K, V> setMultimap1 = setMultimap1Ref
						.get();
				final ObservableSetMultimap<K, V> setMultimap2 = setMultimap2Ref
						.get();
				if ((setMultimap1 == null) || (setMultimap2 == null)) {
					if (setMultimap1 != null) {
						setMultimap1.removeListener(this);
					}
					if (setMultimap2 != null) {
						setMultimap2.removeListener(this);
					}
				} else {
					try {
						updating = true;
						final SetMultimap<K, V> source = setMultimap1 == change
								.getSetMultimap() ? setMultimap1 : setMultimap2;
						final SetMultimap<K, V> destination = setMultimap1 == change
								.getSetMultimap() ? setMultimap2 : setMultimap1;
						// we use replaceValues() to perform an atomic change
						// here (and thus don't use the added and removed values
						// from the change)
						while (change.next()) {
							destination.replaceValues(change.getKey(),
									new HashSet<>(source.get(change.getKey())));
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return (setMultimap1Ref.get() == null)
					|| (setMultimap2Ref.get() == null);
		}
	}

	private static class UnidirectionalMultisetContentBinding<E>
			implements MultisetChangeListener<E>, WeakListener {

		private final WeakReference<Multiset<E>> multisetRef;

		public UnidirectionalMultisetContentBinding(Multiset<E> multiset) {
			this.multisetRef = new WeakReference<>(multiset);
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}

			if (other == null
					|| !(other instanceof UnidirectionalMultisetContentBinding)) {
				return false;
			}

			try {
				@SuppressWarnings("unchecked")
				UnidirectionalMultisetContentBinding<E> otherBinding = ((UnidirectionalMultisetContentBinding<E>) other);
				Multiset<E> multiset = multisetRef.get();
				Multiset<E> otherMultiset = otherBinding.multisetRef.get();
				return multiset == otherMultiset;
			} catch (ClassCastException e) {
				return false;
			}
		}

		@Override
		public int hashCode() {
			// XXX: As we rely on equality to remove a binding again, we have to
			// ensure the hash code of two bindings that target the same
			// property is the same (which we do by using a constant).
			return 0;
		}

		@Override
		public void onChanged(Change<? extends E> change) {
			// This cast is safe, as a
			// UnidirectionalSetMultimapContentBinding<K, V> will only be used
			// for a SetMultimap<K, V>.
			while (change.next()) {
				final Multiset<E> destination = multisetRef.get();
				if (destination == null) {
					change.getMultiset().removeListener(this);
				} else {
					// we use replaceValues() to perform an atomic change here
					// (and
					// thus don't use the added and removed values from the
					// change)
					destination.setCount(change.getElement(),
							destination.count(change.getElement()),
							destination.count(change.getElement())
									+ change.getAddCount()
									- change.getRemoveCount());
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return multisetRef.get() == null;
		}
	}

	private static class UnidirectionalSetMultimapContentBinding<K, V>
			implements SetMultimapChangeListener<K, V>, WeakListener {

		private final WeakReference<SetMultimap<K, V>> setMultimapRef;

		public UnidirectionalSetMultimapContentBinding(
				SetMultimap<K, V> setMultimap) {
			this.setMultimapRef = new WeakReference<>(setMultimap);
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}

			if (other == null
					|| !(other instanceof UnidirectionalSetMultimapContentBinding)) {
				return false;
			}

			try {
				@SuppressWarnings("unchecked")
				UnidirectionalSetMultimapContentBinding<K, V> otherBinding = ((UnidirectionalSetMultimapContentBinding<K, V>) other);
				SetMultimap<K, V> setMultimap = setMultimapRef.get();
				SetMultimap<K, V> otherSetMultimap = otherBinding.setMultimapRef
						.get();
				return setMultimap == otherSetMultimap;
			} catch (ClassCastException e) {
				return false;
			}
		}

		@Override
		public int hashCode() {
			// XXX: As we rely on equality to remove a binding again, we have to
			// ensure the hash code of two bindings that target the same
			// property is the same (which we do by using a constant).
			return 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onChanged(Change<? extends K, ? extends V> change) {
			// This cast is safe, as a
			// UnidirectionalSetMultimapContentBinding<K, V> will only be used
			// for a SetMultimap<K, V>.
			final SetMultimap<K, V> source = (SetMultimap<K, V>) change
					.getSetMultimap();
			while (change.next()) {
				final SetMultimap<K, V> destination = setMultimapRef.get();
				if (destination == null) {
					change.getSetMultimap().removeListener(this);
				} else {
					// we use replaceValues() to perform an atomic change here
					// (and
					// thus don't use the added and removed values from the
					// change)
					destination.replaceValues(change.getKey(),
							new HashSet<>(source.get(change.getKey())));
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return setMultimapRef.get() == null;
		}
	}

	/**
	 * Creates a unidirectional content binding from the given source
	 * {@link Multiset} to the given target {@link ObservableMultiset}.
	 *
	 * @param <E>
	 *            The element type of the given {@link Multiset} and
	 *            {@link ObservableMultiset}.
	 * @param source
	 *            The {@link Multiset} whose content to update when the given
	 *            {@link ObservableMultiset} changes.
	 * @param target
	 *            The {@link ObservableMultiset} whose content is to be
	 *            observed.
	 */
	public static <E> void bindContent(Multiset<E> source,
			ObservableMultiset<? extends E> target) {
		if (source == null) {
			throw new NullPointerException("Cannot bind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot bind to null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException("Cannot bind source to itself.");
		}

		if (source instanceof ObservableMultiset) {
			// ensure we use an atomic operation in case the source multiset is
			// observable.
			((ObservableMultiset<E>) source).replaceAll(target);
		} else {
			source.clear();
			source.addAll(target);
		}

		final UnidirectionalMultisetContentBinding<E> contentBinding = new UnidirectionalMultisetContentBinding<>(
				source);
		// clear any previous bindings
		target.removeListener(contentBinding);
		// add new binding as listener
		target.addListener(contentBinding);
	}

	/**
	 * Creates a unidirectional content binding from the given source
	 * {@link SetMultimap} to the given target {@link ObservableSetMultimap}.
	 *
	 * @param <K>
	 *            The key type of the given {@link SetMultimap} and
	 *            {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the given {@link SetMultimap} and
	 *            {@link ObservableSetMultimap}.
	 * @param source
	 *            The {@link SetMultimap} whose content to update when the given
	 *            {@link ObservableSetMultimap} changes.
	 * @param target
	 *            The {@link ObservableSetMultimap} whose content is to be
	 *            observed.
	 */
	public static <K, V> void bindContent(SetMultimap<K, V> source,
			ObservableSetMultimap<? extends K, ? extends V> target) {
		if (source == null) {
			throw new NullPointerException("Cannot bind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot bind to null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException("Cannot bind source to itself.");
		}

		if (source instanceof ObservableSetMultimap) {
			// ensure we use an atomic operation in case the source set-multimap
			// is
			// observable.
			((ObservableSetMultimap<K, V>) source).replaceAll(target);
		} else {
			source.clear();
			source.putAll(target);
		}

		final UnidirectionalSetMultimapContentBinding<K, V> contentBinding = new UnidirectionalSetMultimapContentBinding<>(
				source);
		// clear any previous bindings
		target.removeListener(contentBinding);
		// add new binding as listener
		target.addListener(contentBinding);
	}

	/**
	 * Creates a bidirectional content binding between the given
	 * {@link ObservableMultiset ObservableMultisets}.
	 *
	 * @param <E>
	 *            The element type of the given {@link ObservableMultiset
	 *            ObservableMultisets}.
	 * @param source
	 *            The first participant of the bidirectional binding. Its
	 *            contents will be initially replaced with that of the second
	 *            participant before both are synchronized.
	 * @param target
	 *            The second participant of the bidirectional binding. Its
	 *            contents will be initially taken to update the contents of the
	 *            first participant before both are synchronized.
	 */
	public static <E> void bindContentBidirectional(
			ObservableMultiset<E> source, ObservableMultiset<E> target) {
		if (source == null) {
			throw new NullPointerException("Cannot bind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot bind to null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException("Cannot bind source to itself.");
		}

		source.replaceAll(target);

		final BidirectionalMultisetContentBinding<E> contentBinding = new BidirectionalMultisetContentBinding<>(
				source, target);

		// clear any previous bindings
		source.removeListener(contentBinding);
		target.removeListener(contentBinding);
		// add new binding as listener
		source.addListener(contentBinding);
		target.addListener(contentBinding);
	}

	/**
	 * Creates a unidirectional content binding between the given
	 * {@link ObservableSetMultimap ObservableSetMultimaps}.
	 *
	 * @param <K>
	 *            The key type of the given {@link ObservableSetMultimap
	 *            ObservableSetMultimaps}.
	 * @param <V>
	 *            The value type of the given {@link ObservableSetMultimap
	 *            ObservableSetMultimaps}.
	 * @param source
	 *            The first participant of the bidirectional binding. Its
	 *            contents will be initially replaced with that of the second
	 *            participant before both are synchronized.
	 * @param target
	 *            The second participant of the bidirectional binding. Its
	 *            contents will be initially taken to update the contents of the
	 *            first participant before both are synchronized.
	 */
	public static <K, V> void bindContentBidirectional(
			ObservableSetMultimap<K, V> source,
			ObservableSetMultimap<K, V> target) {
		if (source == null) {
			throw new NullPointerException("Cannot bind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot bind to null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException("Cannot bind source to itself.");
		}

		source.replaceAll(target);

		final BidirectionalSetMultimapContentBinding<K, V> contentBinding = new BidirectionalSetMultimapContentBinding<>(
				source, target);
		// clear any previous bindings
		source.removeListener(contentBinding);
		target.removeListener(contentBinding);
		// add new binding as listener
		source.addListener(contentBinding);
		target.addListener(contentBinding);
	}

	/**
	 * Removes an existing content binding from the given source
	 * {@link Multiset} to the given target {@link ObservableMultiset}.
	 *
	 * @param <E>
	 *            The element types of the {@link Multiset} and
	 *            {@link ObservableMultiset}.
	 * @param source
	 *            The {@link Multiset} whose content should no longer be updated
	 *            when the given {@link ObservableMultiset} changes.
	 * @param target
	 *            The {@link ObservableMultiset} whose content is no longer to
	 *            be observed.
	 */
	public static <E> void unbindContent(Multiset<E> source,
			ObservableMultiset<? extends E> target) {
		if (source == null) {
			throw new NullPointerException("Cannot unbind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot unbind from null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException(
					"Cannot unbind source to itself.");
		}

		target.removeListener(
				new UnidirectionalMultisetContentBinding<>(source));
	}

	/**
	 * Removes an existing unidirectional content binding from the given source
	 * {@link SetMultimap} to the given target {@link ObservableSetMultimap}.
	 *
	 * @param <K>
	 *            The key type of the given {@link SetMultimap} and
	 *            {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the given {@link SetMultimap} and
	 *            {@link ObservableSetMultimap}.
	 * @param source
	 *            The {@link SetMultimap} whose content is no longer to update
	 *            when the given {@link ObservableSetMultimap} changes.
	 * @param target
	 *            The {@link ObservableSetMultimap} whose content is no longer
	 *            to be observed.
	 */
	public static <K, V> void unbindContent(SetMultimap<K, V> source,
			ObservableSetMultimap<? extends K, ? extends V> target) {
		if (source == null) {
			throw new NullPointerException("Cannot unbind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot unbind from null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException(
					"Cannot unbind source to itself.");
		}

		target.removeListener(
				new UnidirectionalSetMultimapContentBinding<>(source));
	}

	/**
	 * Removes a bidirectional content binding between the given
	 * {@link ObservableMultiset ObservableMultisets}. .
	 *
	 * @param <E>
	 *            The element type of the given {@link ObservableMultiset
	 *            ObservableMultisets}.
	 * @param multiset1
	 *            The first participant of the bidirectional binding.
	 * @param multiset2
	 *            The second participant of the bidirectional binding.
	 */
	public static <E> void unbindContentBidirectional(
			ObservableMultiset<E> multiset1, ObservableMultiset<E> multiset2) {
		if (multiset1 == null) {
			throw new NullPointerException("Cannot bind null value.");
		}
		if (multiset2 == null) {
			throw new NullPointerException("Cannot bind to null value.");
		}
		if (multiset1 == multiset2) {
			throw new IllegalArgumentException("Cannot bind source to itself.");
		}
		final BidirectionalMultisetContentBinding<E> contentBinding = new BidirectionalMultisetContentBinding<>(
				multiset1, multiset2);
		multiset1.removeListener(contentBinding);
		multiset2.removeListener(contentBinding);
	}

	/**
	 * Removes a bidirectional content binding between the given
	 * {@link ObservableSetMultimap ObservableSetMultimaps}.
	 *
	 * @param <K>
	 *            The key type of the given {@link ObservableSetMultimap
	 *            ObservableSetMultimaps}.
	 * @param <V>
	 *            The value type of the given {@link ObservableSetMultimap
	 *            ObservableSetMultimaps}.
	 * @param source
	 *            The first participant of the bidirectional binding.
	 * @param target
	 *            The second participant of the bidirectional binding.
	 */
	public static <K, V> void unbindContentBidirectional(
			ObservableSetMultimap<K, V> source,
			ObservableSetMultimap<K, V> target) {
		if (source == null) {
			throw new NullPointerException("Cannot bind null value.");
		}
		if (target == null) {
			throw new NullPointerException("Cannot bind to null value.");
		}
		if (source == target) {
			throw new IllegalArgumentException("Cannot bind source to itself.");
		}

		final BidirectionalSetMultimapContentBinding<K, V> contentBinding = new BidirectionalSetMultimapContentBinding<>(
				source, target);
		source.removeListener(contentBinding);
		target.removeListener(contentBinding);
	}

	/**
	 * Creates a new {@link ObjectBinding} that contains the values mapped to
	 * the specified key.
	 *
	 * @param setMultimap
	 *            The {@link ObservableSetMultimap} from which the values are to
	 *            be retrieved.
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 *
	 *
	 * @param key
	 *            the key of the mapping
	 * @return A new {@code ObjectBinding}.
	 */
	public static <K, V> SetBinding<V> valuesAt(
			final ObservableSetMultimap<K, V> setMultimap, final K key) {
		if (setMultimap == null) {
			throw new UnsupportedOperationException(
					"setMultimap may not be null.");
		}

		return new SetBinding<V>() {
			{
				super.bind(setMultimap);
			}

			@Override
			protected ObservableSet<V> computeValue() {
				return FXCollections.observableSet(setMultimap.get(key));
			}

			@Override
			public void dispose() {
				super.unbind(setMultimap);
			}

			@Override
			public ObservableList<?> getDependencies() {
				return FXCollections.singletonObservableList(setMultimap);
			}
		};
	}

	/**
	 * Creates a new {@link ObjectBinding} that contains the values mapped to
	 * the specified key.
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 *
	 * @param setMultimap
	 *            The {@link ObservableSetMultimap} from which the values are to
	 *            be retrieved.
	 * @param key
	 *            the key of the mapping
	 * @return A new {@code ObjectBinding}.
	 */
	public static <K, V> SetBinding<V> valuesAt(
			final ObservableSetMultimap<K, V> setMultimap,
			final ObservableValue<K> key) {
		if (setMultimap == null) {
			throw new UnsupportedOperationException(
					"setMultimap may not be null.");
		}
		if (key == null) {
			throw new UnsupportedOperationException("key may not be null");
		}
		return new SetBinding<V>() {
			{
				super.bind(setMultimap);
			}

			@Override
			protected ObservableSet<V> computeValue() {
				return FXCollections
						.observableSet(setMultimap.get(key.getValue()));
			}

			@Override
			public void dispose() {
				super.unbind(setMultimap);
			}

			@Override
			public ObservableList<?> getDependencies() {
				return FXCollections.unmodifiableObservableList(
						FXCollections.observableArrayList(setMultimap, key));
			}
		};
	}
}
