/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.adapt;

/**
 * A pair of {@link Class} key and {@link String} role to register adapters at
 * and retrieve them from {@link IAdaptable}s. Using an {@link AdapterKey}
 * instead of just a {@link Class} key allows to register several adapters under
 * the same key, serving different roles. Nevertheless, adapters can still be
 * accessed in a type-safe manner. To register a default adapter for a certain
 * {@link Class} key, the {@link #DEFAULT_ROLE} may be used.
 * <P>
 * Creating {@link AdapterKey}s is supported by {@link #get(Class, String)} and
 * {@link #get(Class)} respectively, where the latter will use the
 * {@link #DEFAULT_ROLE}.
 * 
 * @author anyssen
 *
 * @param <T>
 *            The type parameter corresponding to the type parameter of the
 *            {@link Class} used as key ({@link #getKey()}).
 */
public class AdapterKey<T> {

	/**
	 * A default role to be used for {@link AdapterKey}s.
	 * 
	 * @see #get(Class)
	 */
	public static final String DEFAULT_ROLE = "default";

	private Class<T> key;
	private String role;

	private AdapterKey(Class<T> key, String role) {
		this.key = key;
		this.role = role;
	}

	/**
	 * Returns the key used by this {@link AdapterKey}.
	 * 
	 * @return The key being used.
	 */
	public Class<T> getKey() {
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

	/**
	 * Creates a new {@link AdapterKey} for the given key and role.
	 * 
	 * @param key
	 *            The key to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @param role
	 *            The role to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @return A new {@link AdapterKey} for the given key and role.
	 */
	public static <T> AdapterKey<T> get(Class<T> key, String role) {
		if (key == null) {
			throw new NullPointerException("Key may not be null.");
		}
		if (role == null) {
			throw new NullPointerException("Role may not be null.");
		}
		return new AdapterKey<T>(key, role);
	}

	/**
	 * Creates a new {@link AdapterKey} for the given key and the
	 * {@link #DEFAULT_ROLE} role.
	 * 
	 * @param key
	 *            The key to use for the newly created {@link AdapterKey}. May
	 *            not be <code>null</code>.
	 * @return A new {@link AdapterKey} for the given key and role.
	 * 
	 * @see #get(Class, String)
	 */
	public static <T> AdapterKey<T> get(Class<T> key) {
		return get(key, DEFAULT_ROLE);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdapterKey<?> other = (AdapterKey<?>) obj;
		return key.equals(other.getKey()) && role.equals(other.role);
	}

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
