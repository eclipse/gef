package org.eclipse.gef4.common.attributes;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;

/**
 * An {@link IAttributeStore} allows to store and retrieve values of named
 * attributes.
 *
 */
public interface IAttributeStore {

	/**
	 * The name of the {@link #attributesProperty() attributes property}.
	 */
	public String ATTRIBUTES_PROPERTY = "attributes";

	/**
	 * Returns a map property of attributes, mapped to their names.
	 *
	 * @return A map property containing the attributes.
	 */
	public ReadOnlyMapProperty<String, Object> attributesProperty();

	/**
	 * Returns a map of attributes, mapped to their keys.
	 * 
	 * @return A map containing the attributes.
	 */
	public ObservableMap<String, Object> getAttributes();

}