/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.common.adapt;

import org.eclipse.gef.common.adapt.inject.AdapterMaps;

import com.google.common.reflect.TypeToken;

/**
 * A pair of {@link Class} key and {@link String} role to register adapters at
 * and retrieve them from {@link IAdaptable}s. Using an {@link AdapterKey}
 * instead of just a {@link Class} or {@link TypeToken} key allows to register
 * several adapters under the same key, serving different roles. Nevertheless,
 * adapters can still be accessed in a type-safe manner. To register a default
 * adapter for a certain {@link Class} or {@link TypeToken} key, the
 * {@link #DEFAULT_ROLE} may be used.
 * <P>
 * Creating {@link AdapterKey}s is supported by {@link #get(Class, String)} and
 * {@link #get(TypeToken, String)}, as well as {@link #get(Class)} and
 * {@link #get(TypeToken)} respectively, where the latter two will use the
 * {@link #DEFAULT_ROLE}.
 *
 * @author anyssen
 *
 * @param <T>
 *            The type parameter corresponding to the type parameter of the
 *            {@link Class} used as key ({@link #getKey()}).
 */
public class AdapterKey<T> implements Comparable<AdapterKey<T>> {

	/**
	 * A default role to be used for {@link AdapterKey}s.
	 *
	 * @see #get(Class)
	 */
	public static final String DEFAULT_ROLE = "default";

	/**
	 * Returns an {@link AdapterKey} with no type key and the 'default' role,
	 * which can only be used in adapter map bindings. See {@link AdapterMaps}.
	 *
	 * @return An AdapterKey without type key, using the 'default' role.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static AdapterKey<?> defaultRole() {
		return get((Class) null, DEFAULT_ROLE);
	}

	/**
	 * Creates a new {@link AdapterKey} for the given raw type key and the
	 * {@link #DEFAULT_ROLE} role, which can be used to retrieve an adapter from
	 * an IAdaptable.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The key to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @return A new {@link AdapterKey} for the given key and role.
	 *
	 * @see #get(Class, String)
	 */
	public static <T> AdapterKey<T> get(Class<T> key) {
		return get(TypeToken.of(key), DEFAULT_ROLE);
	}

	/**
	 * Creates a new {@link AdapterKey} for the given key and role.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The key to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @param role
	 *            The role to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @return A new {@link AdapterKey} for the given key and role.
	 */
	public static <T> AdapterKey<T> get(Class<T> key, String role) {
		// key may be null (in case we use AdapterKey) in bindings
		if (role == null) {
			throw new NullPointerException("Role may not be null.");
		}
		return new AdapterKey<>(key == null ? null : TypeToken.of(key), role);
	}

	/**
	 * Creates a new {@link AdapterKey} for the given type key and the
	 * {@link #DEFAULT_ROLE} role, which can be used to retrieve an adapter from
	 * an IAdaptable.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The key to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @return A new {@link AdapterKey} for the given key and role.
	 *
	 * @see #get(TypeToken, String)
	 */
	public static <T> AdapterKey<T> get(TypeToken<T> key) {
		return get(key, DEFAULT_ROLE);
	}

	/**
	 * Creates a new {@link AdapterKey} for the given key and role, which can be
	 * used to retrieve an adapter from an IAdaptable.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The key to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @param role
	 *            The role to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @return A new {@link AdapterKey} for the given key and role.
	 */
	public static <T> AdapterKey<T> get(TypeToken<T> key, String role) {
		// key may be null (in case we use AdapterKey) in bindings
		if (role == null) {
			throw new NullPointerException("Role may not be null.");
		}
		return new AdapterKey<>(key, role);
	}

	/**
	 * Returns an {@link AdapterKey} with no type key and the given role, which
	 * can only be used in adapter map bindings. See {@link AdapterMaps}.
	 *
	 * @param role
	 *            The role to use.
	 *
	 * @return An AdapterKey without type key, using the given role.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static AdapterKey<?> role(String role) {
		return get((Class) null, role);
	}

	private TypeToken<T> key;

	private String role;

	private AdapterKey(TypeToken<T> typeKey, String role) {
		this.key = typeKey;
		this.role = role;
	}

	@Override
	public int compareTo(AdapterKey<T> o) {
		if (key == null) {
			throw new IllegalArgumentException(
					"An AdapterKey that is used for binding cannot be compared.");
		}
		// primarily sort by role
		if (role.equals(o.getRole())) {
			// secondarily sort by type key
			return key.toString().compareTo(o.getKey().toString());
		} else {
			return role.compareTo(o.getRole());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AdapterKey<?> other = (AdapterKey<?>) obj;
		if (key == null) {
			// XXX: In case the map binder used for adapter map injection does
			// not permit duplicates (see MapBinder#permitDuplicates()), we can
			// access the linked bindings of the adapter map binder to infer the
			// actual adapter type (when it refers to a constructor binding),
			// allowing us to omit the adapter type information from the
			// AdapterKey of the map binding.
			//
			// However, in case we are omitting the type key from the
			// AdapterKey, we have to ensure AdapterKeys without type keys are
			// never equal to others (because map binder would otherwise detect
			// duplicates).
			return false;
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (role == null) {
			if (other.role != null) {
				return false;
			}
		} else if (!role.equals(other.role)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the key used by this {@link AdapterKey}.
	 *
	 * @return The key being used.
	 */
	public TypeToken<T> getKey() {
		return key;
	}

	/**
	 * Returns the role used by this {@link AdapterKey}.
	 *
	 * @return The role being used.
	 */
	public String getRole() {
		return role;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "AdapterKey(" + key + ", " + role + ")";
	}

}
