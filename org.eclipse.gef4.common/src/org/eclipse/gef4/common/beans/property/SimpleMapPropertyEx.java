/**
 * 
 */
package org.eclipse.gef4.common.beans.property;

import org.eclipse.gef4.common.beans.binding.MapExpressionHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;

/**
 * The {@link SimpleMapPropertyEx} extends the {@link SimpleMapProperty} to fix
 * JavaFX bug https://bugs.openjdk.java.net/browse/JDK-8136465, i.e. it keeps
 * track of all listeners and ensures that remaining listeners are re-added when
 * a listener is removed.
 * 
 * @author anyssen
 * 
 * @param <K>
 *            The key type of the wrapped {@link ObservableMap}.
 * @param <V>
 *            The value type of the wrapped {@link ObservableMap}.
 *
 */
public class SimpleMapPropertyEx<K, V> extends SimpleMapProperty<K, V> {

	private MapExpressionHelperEx<K, V> helper = null;

	/**
	 * Creates a new unnamed map property.
	 */
	public SimpleMapPropertyEx() {
		super();
	}

	/**
	 * Constructs a new {@link SimpleMapPropertyEx} for the given bean and with
	 * the given name.
	 * 
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 */
	public SimpleMapPropertyEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Constructs a new {@link SimpleMapPropertyEx} for the given bean and with
	 * the given name and initial value.
	 * 
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleMapPropertyEx(Object bean, String name,
			ObservableMap<K, V> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Constructs a new unnamed {@link SimpleMapPropertyEx} that is not related
	 * to a bean, with the given initial value.
	 * 
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleMapPropertyEx(ObservableMap<K, V> initialValue) {
		super(initialValue);
	}

	@Override
	public void addListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void addListener(MapChangeListener<? super K, ? super V> listener) {
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	protected void fireValueChangedEvent() {
		if (helper != null) {
			helper.fireValueChangedEvent();
		}
	}

	@Override
	protected void fireValueChangedEvent(
			Change<? extends K, ? extends V> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	@Override
	public void removeListener(
			MapChangeListener<? super K, ? super V> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}
}
