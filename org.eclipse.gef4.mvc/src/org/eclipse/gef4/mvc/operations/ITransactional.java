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
 * used to manipulate its host in between those calls. The policy returns an
 * {@link IUndoableOperation} upon {@link #commit()} which performs the
 * manipulations.
 *
 * @author anyssen
 *
 */
public interface ITransactional {

	/**
	 * Returns an {@link IUndoableOperation} that performs all manipulations
	 * applied by the policy since the last {@link #init()} call.
	 *
	 * @return An {@link IUndoableOperation} that performs all manipulations
	 *         applied by the policy since the last {@link #init()} call.
	 */
	public abstract IUndoableOperation commit();

	/**
	 * Initializes the policy.
	 */
	public abstract void init();

}