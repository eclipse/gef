/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.operations.IUndoableOperation;

/**
 * An {@link ITransactional} policy consists of an initialization part (
 * {@link #init()}) and a commit part ({@link #commit()}). The policy can be
 * used to manipulate its host in-between those calls. The policy returns an
 * {@link IUndoableOperation} upon {@link #commit()} which performs the
 * manipulations.
 * <p>
 * Note, that an {@link ITransactional} policy is safe against multiple
 * initialization/commitment in sequence. However, only the first
 * {@link #commit()} call will return an operation (subsequent calls will return
 * <code>null</code>).
 * <p>
 * If an {@link ITransactional} policy is not initialized, it should throw an
 * {@link IllegalStateException} if any of its "work" methods are called.
 *
 * @author anyssen
 *
 */
public interface ITransactional {

	/**
	 * Returns an {@link IUndoableOperation} that performs all manipulations
	 * applied by the policy since the last {@link #init()} call. When called
	 * multiple times in sequence, only the first call will yield an operation,
	 * the subsequent calls will yield <code>null</code>.
	 *
	 * @return An {@link IUndoableOperation} that performs all manipulations
	 *         applied by the policy since the last {@link #init()} call.
	 */
	public abstract IUndoableOperation commit();

	/**
	 * Initializes the policy, so that the policy's "work" methods can be used.
	 * Calling a "work" method while the policy is not initialized will result
	 * in an {@link IllegalStateException}. It is safe to call {@link #init()}
	 * multiple times in sequence.
	 */
	public abstract void init();

}