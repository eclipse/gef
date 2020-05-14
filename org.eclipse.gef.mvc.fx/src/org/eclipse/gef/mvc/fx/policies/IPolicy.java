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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

/**
 * The {@link IPolicy} interface extends
 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound}, i.e. it is bound to an
 * {@link IAdaptable}, its so called {@link #getHost()}.
 *
 * @author anyssen
 *
 */
public interface IPolicy extends IAdaptable.Bound<IVisualPart<? extends Node>> {

	/**
	 * Returns an {@link ITransactionalOperation} that performs all
	 * manipulations applied by the policy since the previous {@link #init()}
	 * call.
	 *
	 * @return An {@link ITransactionalOperation} that performs all
	 *         manipulations applied by the policy since the last
	 *         {@link #init()} call.
	 */
	public ITransactionalOperation commit();

	/**
	 * Returns the host of this {@link IPolicy}, i.e. the {@link IVisualPart}
	 * this {@link IPolicy} is attached to.
	 *
	 * @return The host of this {@link IPolicy}.
	 */
	public default IVisualPart<? extends Node> getHost() {
		return getAdaptable();
	}

	/**
	 * Initializes the policy, so that the policy's "work" methods can be used.
	 * Calling a "work" method while the policy is not initialized will result
	 * in an {@link IllegalStateException}, as well as re-initializing before
	 * committing or rolling back.
	 */
	public void init();

	/**
	 * Puts back this policy into an uninitialized state, reverting any changes
	 * that have been applied via the policy's work methods since the preceding
	 * {@link #init()} call.
	 */
	public void rollback();
}
