/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
package org.eclipse.gef.common.adapt.inject;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.reflect.Types;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link AdapterMap} annotation.
 *
 * @author anyssen
 *
 */
@SuppressWarnings("all")
class AdapterMapImpl implements AdapterMap, Serializable {

	/**
	 * A context element specified an IAdaptable that is
	 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound} with a specific
	 * role.
	 */
	static class BoundAdapterImpl implements BoundAdapter, Serializable {
		private static final long serialVersionUID = 1L;

		private String adapterType = null;
		private String adapterRole = DEFAULT_ROLE;

		/**
		 * Creates a new {@link BoundAdapterImpl} for the given adapter type.
		 *
		 * @param adapterType
		 *            The type of the context element.
		 */
		public BoundAdapterImpl(String adapterType) {
			TypeToken<?> type = Types.deserialize(adapterType);
			if (!Types.isAssignable(TypeToken.of(IAdaptable.class), type)
					|| !Types.isAssignable(TypeToken.of(IAdaptable.Bound.class),
							type)) {
				throw new IllegalArgumentException(
						"Context element has to be IAdaptable and IAdaptable.Bound, which does not hold for "
								+ type.toString());
			}
			this.adapterType = adapterType;
		}

		/**
		 * Create a new {@link BoundAdapterImpl} for the given adapter type and
		 * role.
		 *
		 * @param adapterType
		 *            The type of the context element.
		 * @param adapterRole
		 *            The role under which the context element is bound.
		 */
		public BoundAdapterImpl(String adapterType, String adapterRole) {
			this(adapterType);
			this.adapterRole = adapterRole;
		}

		@Override
		public String adapterRole() {
			return adapterRole;
		}

		@Override
		public String adapterType() {
			return adapterType;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return BoundAdapter.class;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof BoundAdapter)) {
				return false;
			}

			BoundAdapter other = (BoundAdapter) obj;
			return adapterType.equals(other.adapterType())
					&& adapterRole.equals(other.adapterRole());
		}

		@Override
		public int hashCode() {
			return (127 * "adapterType".hashCode())
					^ adapterType.hashCode() + (127 * "adapterRole".hashCode())
					^ adapterRole.hashCode();
		}

		@Override
		public String toString() {
			return "@" + BoundAdapter.class.getName() + "(adapterType="
					+ adapterType + ", " + "adapterRole=" + adapterRole + ")";
		}
	}

	private static final long serialVersionUID = 1L;

	private Class<? extends IAdaptable> adaptableType;

	private BoundAdapter[] adaptableContext = new BoundAdapter[] {};

	/**
	 * Creates a new {@link AdapterMapImpl} with the given {@link IAdaptable}
	 * type as its value.
	 *
	 * @param adaptableType
	 *            The {@link IAdaptable} type being used as type of this
	 *            {@link AdapterMapImpl}.
	 */
	public AdapterMapImpl(Class<? extends IAdaptable> adaptableType) {
		this.adaptableType = adaptableType;
	}

	/**
	 * Creates a new {@link AdapterMapImpl} with the given {@link IAdaptable}
	 * type and role.
	 *
	 * @param adaptableType
	 *            The {@link IAdaptable} type being used as type of this
	 *            {@link AdapterMapImpl}.
	 * @param adaptableContext
	 *            The {@link String} being used as role of this
	 *            {@link AdapterMapImpl}.
	 */
	public AdapterMapImpl(Class<? extends IAdaptable> adaptableType,
			BoundAdapter... adaptableContext) {
		this.adaptableType = adaptableType;
		this.adaptableContext = adaptableContext;
	}

	@Override
	public BoundAdapter[] adaptableContext() {
		return adaptableContext;
	}

	@Override
	public Class<? extends IAdaptable> adaptableType() {
		return adaptableType;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return AdapterMap.class;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AdapterMap)) {
			return false;
		}

		AdapterMap other = (AdapterMap) obj;
		return adaptableType.equals(other.adaptableType())
				&& Arrays.equals(adaptableContext, other.adaptableContext());
	}

	@Override
	public int hashCode() {
		return (127 * "type".hashCode())
				^ Arrays.hashCode(adaptableContext) + (127 * "role".hashCode())
				^ Arrays.hashCode(adaptableContext);
	}

	@Override
	public String toString() {
		return "@" + AdapterMap.class.getName() + "(adaptableType="
				+ adaptableType + ", " + "adaptableContext="
				+ Arrays.toString(adaptableContext) + ")";
	}
}
