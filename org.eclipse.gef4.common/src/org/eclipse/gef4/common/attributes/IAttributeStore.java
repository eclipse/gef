package org.eclipse.gef4.common.attributes;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;

/**
 * An {@link IAttributeStore} allows to store and retrieve values of named
 * attributes, notifying registered {@link PropertyChangeListener}s about all
 * (real) changes of attribute values, i.e. when a value gets set, unset, or
 * changed to a different value.
 *
 */
public interface IAttributeStore extends IPropertyChangeNotifier {

	/**
	 * The property name that is used to notify change listeners about changes
	 * made to the attributes of this {@link IAttributeStore}. A property change
	 * event for this property will have its old value set to a
	 * <code>Map&lt;String, Object&gt;</code> holding the old attributes and its
	 * new value set to a <code>Map&lt;String, Object&gt;</code> holding the new
	 * attributes.
	 */
	String ATTRIBUTES_PROPERTY = "attributes";

	/**
	 * Returns the map of attributes of this {@link IAttributeStore} by
	 * reference.
	 *
	 * @return The map of attributes of this {@link IAttributeStore} by
	 *         reference.
	 */
	Map<String, Object> getAttributes();

}