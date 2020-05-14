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
package org.eclipse.gef.common.attributes;

/**
 * Responsible of transferring attributes from a source {@link IAttributeStore}
 * to a target {@link IAttributeStore}. Transferring may be done in an arbitrary
 * manner, including no transfer, shallow copy (i.e. transfer values by
 * identity), deep copy, or even a conversion.
 *
 * @author anyssen
 */
public interface IAttributeCopier {

	/**
	 * An {@link IAttributeCopier} that shallowly copies attributes from the
	 * source to the target store, i.e. the attribute values are transferred (by
	 * identity).
	 */
	public static final IAttributeCopier SHALLOW_COPY = new IAttributeCopier() {

		@Override
		public void copy(IAttributeStore source,
				IAttributeStore target) {
			target.attributesProperty().putAll(source.getAttributes());
		}
	};

	/**
	 * An {@link IAttributeCopier} that does not copy any attributes.
	 */
	public static final IAttributeCopier NULL_COPY = new IAttributeCopier() {

		@Override
		public void copy(IAttributeStore source,
				IAttributeStore target) {
			// do nothing
		}
	};

	/**
	 * Transfers attributes from the source {@link IAttributeStore} to the
	 * target {@link IAttributeStore}.
	 *
	 * @param source
	 *            The source {@link IAttributeStore} to transfer attributes
	 *            from.
	 * @param target
	 *            The target {@link IAttributeStore} to transfer attributes to.
	 */
	public void copy(IAttributeStore source, IAttributeStore target);

}
