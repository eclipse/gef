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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * An {@link ITransactional} policy consists of an initialization part (
 * {@link #init()}) and a commit part ({@link #commit()}). The policy can be
 * used to manipulate its host in-between those calls. The policy returns an
 * {@link ITransactionalOperation} upon {@link #commit()} which performs the
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
	 * The {@link NoOperation} does not have an effect. It can be returned
	 * within {@link ITransactional#commit()} if no changes have to be applied.
	 *
	 * @author mwienand
	 *
	 */
	static class NoOperation extends AbstractOperation
			implements ITransactionalOperation {
		private NoOperation() {
			super("No-Operation");
		}

		@Override
		public boolean canExecute() {
			return true;
		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.OK_STATUS;
		}

		@Override
		public boolean isNoOp() {
			return true;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.OK_STATUS;
		}
	}

	/**
	 * The {@link UnexecutableOperation} cannot be executed.
	 *
	 * @author anyssen
	 *
	 */
	static class UnexecutableOperation extends AbstractOperation
			implements ITransactionalOperation {
		private UnexecutableOperation() {
			super("Unexecutable");
		}

		@Override
		public boolean canExecute() {
			return false;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return null;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return null;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return null;
		}
	}

	/**
	 * This {@link ITransactionalOperation} does not have an effect. It can be
	 * returned by {@link ITransactional#commit()} if no changes have to be
	 * applied.
	 */
	public static ITransactionalOperation NOOP = new NoOperation();

	/**
	 * This {@link ITransactionalOperation} cannot be executed.
	 */
	public static ITransactionalOperation UNEXECUTABLE = new UnexecutableOperation();

	/**
	 * Returns an {@link ITransactionalOperation} that performs all
	 * manipulations applied by the policy since the last {@link #init()} call.
	 * When called multiple times in sequence, only the first call will yield an
	 * operation, the subsequent calls will yield <code>null</code>.
	 * <p>
	 * You can return {@link #NOOP} if no changes have to be applied. The domain
	 * will filter out all {@link #NOOP}s before their execution.
	 * <p>
	 * You can return {@link #UNEXECUTABLE} to prevent any composite operation
	 * that contains the {@link #UNEXECUTABLE} from execution.
	 *
	 * @return An {@link ITransactionalOperation} that performs all
	 *         manipulations applied by the policy since the last
	 *         {@link #init()} call.
	 */
	public abstract ITransactionalOperation commit();

	/**
	 * Initializes the policy, so that the policy's "work" methods can be used.
	 * Calling a "work" method while the policy is not initialized will result
	 * in an {@link IllegalStateException}. It is safe to call {@link #init()}
	 * multiple times in sequence.
	 */
	public abstract void init();

}