package org.eclipse.gef4.common.beans.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.sun.javafx.binding.MapExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;

/**
 * A replacement for {@link MapExpressionHelper} that fixes
 * https://bugs.openjdk.java.net/browse/JDK-8136465.
 * 
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableMapValue}.
 * @param <V>
 *            The value type of the {@link ObservableMapValue}.
 */
public class MapExpressionHelperEx<K, V> extends MapExpressionHelper<K, V> {

	private List<ChangeListener<? super ObservableMap<K, V>>> changeListeners;
	private List<InvalidationListener> invalidationListeners;
	private List<MapChangeListener<? super K, ? super V>> mapChangeListeners;
	private boolean locked = false;
	private MapExpressionHelper<K, V> delegate = null;

	/**
	 * Creates as new {@link MapExpressionHelperEx} for the given observable.
	 * 
	 * @param observable
	 *            The {@link ObservableMapValue} to create the helper for.
	 */
	public MapExpressionHelperEx(ObservableMapValue<K, V> observable) {
		super(observable);
	}

	@Override
	public MapExpressionHelper<K, V> addListener(
			InvalidationListener listener) {
		if (invalidationListeners == null) {
			invalidationListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners
		// are added during notifications); as we only create a new multi-set in
		// the locked case, memory should not be waisted.
		if (locked) {
			invalidationListeners = new ArrayList<>(invalidationListeners);
		}
		delegate = MapExpressionHelper.addListener(delegate, observable,
				listener);
		invalidationListeners.add(listener);
		return this;
	}

	@Override
	public MapExpressionHelper<K, V> removeListener(
			InvalidationListener listener) {
		if(invalidationListeners == null){
			invalidationListeners = new ArrayList<>();
		}
		
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners
		// are added during notifications); as we only create a new multi-set in
		// the locked case, memory should not be waisted.
		if (locked) {
			invalidationListeners = new ArrayList<>(invalidationListeners);
		}

		for (InvalidationListener l : invalidationListeners) {
			delegate = MapExpressionHelper.removeListener(delegate, l);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken; as such remove() may not be used.
		for (Iterator<InvalidationListener> iterator = invalidationListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		for (InvalidationListener l : invalidationListeners) {
			delegate = MapExpressionHelper.addListener(delegate, observable, l);
		}
		if (invalidationListeners.isEmpty()) {
			invalidationListeners = null;
		}
		return this;
	}

	@Override
	public MapExpressionHelper<K, V> addListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if (changeListeners == null) {
			changeListeners = new ArrayList<>();;
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners
		// are added during notifications); as we only create a new multi-set in
		// the locked case, memory should not be waisted.
		if (locked) {
			changeListeners = new ArrayList<>(changeListeners);
		}
		delegate = MapExpressionHelper.addListener(delegate, observable,
				listener);
		changeListeners.add(listener);
		return this;
	}

	@Override
	public MapExpressionHelper<K, V> removeListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if(changeListeners == null){
			changeListeners = new ArrayList<>();
		}
		
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners
		// are added during notifications); as we only create a new multi-set in
		// the locked case, memory should not be waisted.
		if (locked) {
			changeListeners = new ArrayList<>(changeListeners);
		}

		for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
			delegate = MapExpressionHelper.removeListener(delegate, l);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken; as such remove() may not be used.
		for (Iterator<ChangeListener<? super ObservableMap<K, V>>> iterator = changeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
			delegate = MapExpressionHelper.addListener(delegate, observable, l);
		}
		if (changeListeners.isEmpty()) {
			changeListeners = null;
		}
		return this;
	}

	@Override
	public MapExpressionHelper<K, V> addListener(
			MapChangeListener<? super K, ? super V> listener) {
		if (mapChangeListeners == null) {
			mapChangeListeners = new ArrayList<>();;
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners
		// are added during notifications); as we only create a new multi-set in
		// the locked case, memory should not be waisted.
		if (locked) {
			mapChangeListeners = new ArrayList<>(mapChangeListeners);
		}
		delegate = MapExpressionHelper.addListener(delegate, observable,
				listener);
		mapChangeListeners.add(listener);
		return this;
	}

	@Override
	public MapExpressionHelper<K, V> removeListener(
			MapChangeListener<? super K, ? super V> listener) {
		if(mapChangeListeners == null){
			mapChangeListeners = new ArrayList<>();
		}
		
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners
		// are added during notifications); as we only create a new multi-set in
		// the locked case, memory should not be waisted.
		if (locked) {
			mapChangeListeners = new ArrayList<>(mapChangeListeners);
		}

		for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
			delegate = MapExpressionHelper.removeListener(delegate, l);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken; as such remove() may not be used.
		for (Iterator<MapChangeListener<? super K, ? super V>> iterator = mapChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
			delegate = MapExpressionHelper.addListener(delegate, observable, l);
		}
		if (mapChangeListeners.isEmpty()) {
			mapChangeListeners = null;
		}
		return this;
	}

	@Override
	public void fireValueChangedEvent() {
		MapExpressionHelperEx.fireValueChangedEvent(delegate);
	}

	@Override
	public void fireValueChangedEvent(
			Change<? extends K, ? extends V> change) {
		MapExpressionHelperEx.fireValueChangedEvent(delegate, change);
	}

}
