/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - API adjustments
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditDomain.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.domain;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.gestures.IGesture;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.policies.IPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;

/**
 * A domain represents the collective state of a MVC application. It brings
 * together a set of {@link IViewer}s and related {@link IGesture}s to interact
 * with these. It also holds a reference to the {@link IOperationHistory} and
 * {@link UndoContext} used by all {@link IGesture} as well as {@link IPolicy}s
 * (in the {@link IViewer}s) to execute {@link IUndoableOperation}s.
 *
 * @author anyssen
 *
 */
public interface IDomain extends IAdaptable, IActivatable, IDisposable {

	/**
	 * The adapter role for the content viewer.
	 */
	public static final String CONTENT_VIEWER_ROLE = "contentViewer";

	/**
	 * Closes the active execution transaction, removes the given
	 * {@link IGesture} from the transaction context, and opens a new execution
	 * transaction if there are any gestures remaining in the context.
	 *
	 * @param gesture
	 *            The {@link IGesture} that should be removed from the
	 *            transaction context.
	 * @see #openExecutionTransaction(IGesture)
	 */
	public void closeExecutionTransaction(IGesture gesture);

	/**
	 * Executes the given {@link IUndoableOperation}.
	 *
	 * @param operation
	 *            The {@link IUndoableOperation} to be executed on the
	 *            {@link IOperationHistory} of this {@link IDomain}.
	 * @param monitor
	 *            An {@link IProgressMonitor} used to indicate progress. May be
	 *            <code>null</code>.
	 * @throws ExecutionException
	 *             In case an exception occurred during the execution of the
	 *             operation.
	 */
	public void execute(ITransactionalOperation operation,
			IProgressMonitor monitor) throws ExecutionException;

	/**
	 * Returns the {@link IGesture}s registered at this {@link IDomain} (via
	 * {@link #setAdapter(TypeToken, Object)}) with the {@link AdapterKey}s used
	 * for registration.
	 *
	 * @return A {@link Map} containing the registered {@link IGesture}s mapped
	 *         to their respective {@link AdapterKey}s.
	 *
	 * @see IAdaptable#setAdapter(TypeToken, Object)
	 * @since 5.1
	 */
	public Map<AdapterKey<? extends IGesture>, IGesture> getGestures();

	/**
	 * Returns the {@link IGesture}s registered at this {@link IDomain} (via
	 * {@link #setAdapter(TypeToken, Object)}) with the {@link AdapterKey}s used
	 * for registration.
	 *
	 * @return A {@link Map} containing the registered {@link IGesture}s mapped
	 *         to their respective {@link AdapterKey}s.
	 *
	 * @see IAdaptable#setAdapter(TypeToken, Object)
	 *
	 * @deprecated Will be removed in 6.0.0. Please use {@link #getGestures()}
	 *             instead.
	 */
	@Deprecated
	public Map<AdapterKey<? extends IGesture>, IGesture> getTools();

	/**
	 * Returns the {@link IViewer}s registered at this {@link IDomain} (via
	 * {@link #setAdapter(TypeToken, Object)}) with the {@link AdapterKey}s used
	 * for registration.
	 *
	 * @return A {@link Map} containing the registered {@link IViewer}s mapped
	 *         to their respective {@link AdapterKey}s.
	 *
	 * @see IAdaptable#setAdapter(TypeToken, Object)
	 */
	public Map<AdapterKey<? extends IViewer>, IViewer> getViewers();

	/**
	 * Returns <code>true</code> if the given {@link IGesture} is taking part in
	 * the currently open execution transaction. Otherwise returns
	 * <code>false</code>.
	 *
	 * @param gesture
	 *            The {@link IGesture} that is checked.
	 * @return <code>true</code> if the given {@link IGesture} is taking part in
	 *         the currently open execution transaction, otherwise
	 *         <code>false</code>.
	 */
	public boolean isExecutionTransactionOpen(IGesture gesture);

	/**
	 * Opens a new transaction or adds the given {@link IGesture} to the
	 * currently opened transaction for executing operations (via
	 * {@link #execute(ITransactionalOperation, IProgressMonitor)}) on the
	 * {@link IOperationHistory} used by this {@link IDomain}.
	 *
	 * @param gesture
	 *            The {@link IGesture} starting/joining the transaction.
	 */
	public void openExecutionTransaction(IGesture gesture);
}
