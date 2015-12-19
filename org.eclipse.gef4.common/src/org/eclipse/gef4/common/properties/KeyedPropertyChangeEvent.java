package org.eclipse.gef4.common.properties;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A specific {@link PropertyChangeEvent} that is intended to notify
 * {@link PropertyChangeListener}s about element changes of a map property. It
 * is complementary to an {@link IndexedPropertyChangeEvent} in case of a list
 * property.
 * 
 * @author anyssen
 *
 */
public class KeyedPropertyChangeEvent extends PropertyChangeEvent {

	private static final long serialVersionUID = 7284605241276436117L;
	private Object key;

	/**
	 * Creates a new {@link KeyedPropertyChangeEvent}.
	 *
	 * @param source
	 *            The bean that fired the event.
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 * @param key
	 *            key of the property element that was changed.
	 */
	public KeyedPropertyChangeEvent(Object source, String propertyName,
			Object oldValue, Object newValue, Object key) {
		super(source, propertyName, oldValue, newValue);
		this.key = key;
	}

	/**
	 * Gets the key of the property that was changed.
	 *
	 * @return The key specifying the property element that was changed.
	 */
	public Object getKey() {
		return key;
	}

	@Override
	public String toString() {
		// XXX Unfortunately, appendTo(StringBuilder) has package visibility and
		// may thus not be overwritten. As such, we have to overwrite toString()
		// directly.
		StringBuilder sb = new StringBuilder(getClass().getName());
		String stringValue = super.toString();
		int splitIndex = stringValue.indexOf(";");
		sb.append(stringValue.substring(0, splitIndex));
		sb.append("; key=").append(getKey());
		return sb.append(stringValue.substring(splitIndex)).toString();
	}
}
