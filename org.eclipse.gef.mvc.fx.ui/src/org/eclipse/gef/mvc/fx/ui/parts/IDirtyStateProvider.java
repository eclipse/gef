/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.ui.parts;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.ui.ISaveablePart;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * A delegate support that can be used by {@link ISaveablePart}s to maintain
 * their dirty state based on an {@link IOperationHistory}.
 *
 * @author anyssen
 *
 */
public interface IDirtyStateProvider {

	/**
	 * A read-only property that provides the current dirty state.
	 *
	 * @return A read-only boolean property.
	 */
	public ReadOnlyBooleanProperty dirtyProperty();

	/**
	 * Returns the dirty state of this support.
	 *
	 * @return <code>true</code> if the source is dirty, <code>false</code>
	 *         otherwise.
	 */
	boolean isDirty();

	/**
	 * Notifies the provider to mark the current state as being unchanged with
	 * respect to the saved state.
	 */
	void markNonDirty();

}