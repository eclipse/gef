/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.domain;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.adapt.inject.AdaptableScope;
import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.common.adapt.inject.InjectAdapters;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;

/**
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractDomain<VR> implements IDomain<VR> {

	private static final int DEFAULT_UNDO_LIMIT = 128;
	private ActivatableSupport acs = new ActivatableSupport(this);
	private AdaptableSupport<IDomain<VR>> ads = new AdaptableSupport<IDomain<VR>>(
			this);

	private IOperationHistory operationHistory;
	private IUndoContext undoContext;
	private AbstractCompositeOperation transaction;
	private Set<ITool<VR>> transactionContext = new HashSet<>();
	private IOperationHistoryListener operationHistoryListener = new IOperationHistoryListener() {
		@Override
		public void historyNotification(OperationHistoryEvent event) {
			if (event.getEventType() == OperationHistoryEvent.ABOUT_TO_UNDO) {
				if (!transactionContext.isEmpty() && transaction != null) {
					if (transaction.getOperations().isEmpty()) {
						for (ITool<VR> tool : transactionContext) {
							closeExecutionTransaction(tool);
						}
					} else {
						throw new IllegalStateException(
								"Cannot perform UNDO while a currently open execution transaction contains operations.");
					}
				}
			}
		}
	};

	/**
	 * Creates a new {@link AbstractDomain} instance, setting the
	 * {@link AdaptableScope} for each of its {@link IAdaptable}-compliant types
	 * (super classes implementing {@link IAdaptable} and super-interfaces
	 * extending {@link IAdaptable}) to the newly created instance (see
	 * AdaptableScopes#scopeTo(IAdaptable)).
	 */
	public AbstractDomain() {
		AdaptableScopes.enter(this);

		// XXX: Observable collections wrap notification of listeners into a
		// try/catch block and report all exceptions to the registered
		// UncaughtExceptionHandler. We do not want to have exceptions silently
		// captured anywhere in the framework, thus register a handler here
		// (that may of course be overwritten by clients) that
		// re-throws all silently captured exceptions.
		Thread.currentThread()
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						e.printStackTrace();
						if (e instanceof RuntimeException) {
							throw (RuntimeException) e;
						} else {
							throw new RuntimeException(e);
						}
					}
				});
	}

	@Override
	public void activate() {
		if (!acs.isActive()) {
			acs.activate();
		}
	}

	@Override
	public ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return ads.adaptersProperty();
	}

	@Override
	public void closeExecutionTransaction(ITool<VR> tool) {
		// if (!transactionContext.contains(tool)) {
		// throw new IllegalStateException(
		// "No transaction active for tool " + tool + ".");
		// }
		if (!transactionContext.contains(tool)) {
			return; // TODO: is this legal?
		}

		// remove tool from the transaction context and close the transaction in
		// case the tool was the last one
		if (transactionContext.size() == 1
				&& transactionContext.contains(tool)) {
			// Close transaction by adding it to the operation history in case
			// it has an effect; all its nested operations have already been
			// executed, thus it does not have to be executed again
			if (transaction == null) {
				throw new IllegalStateException(
						"No transaction is currently active, while the transaction context sill contained tool "
								+ tool + ".");
			}
			if (!transaction.getOperations().isEmpty()) {
				// adjust the label of the transaction
				transaction.setLabel(transaction.getOperations().iterator()
						.next().getLabel());
				getOperationHistory().add(transaction);
			}
			transaction = null;
		}
		transactionContext.remove(tool);
	}

	/**
	 * Creates a {@link ForwardUndoCompositeOperation} which is used to store
	 * the operations within an execution transaction. The operation is opened
	 * on the {@link #getOperationHistory() operation history}.
	 *
	 * @return A new {@link ForwardUndoCompositeOperation} which is configured
	 *         to store the operations within an execution transaction.
	 */
	protected AbstractCompositeOperation createExecutionTransaction() {
		ReverseUndoCompositeOperation transaction = new ReverseUndoCompositeOperation(
				"Transaction");
		transaction.addContext(getUndoContext());
		return transaction;
	}

	@Override
	public void deactivate() {
		if (acs.isActive()) {
			acs.deactivate();
		}
	}

	@Override
	public void dispose() {
		// dispose operation history
		operationHistory.dispose(undoContext, true, true, true);

		// clear adaptable scope
		AdaptableScopes.leave(this);

		// dispose adapter store
		ads.dispose();
	}

	@Override
	public void execute(IUndoableOperation operation)
			throws ExecutionException {
		// reduce composite operations
		if (operation instanceof AbstractCompositeOperation) {
			operation = ((AbstractCompositeOperation) operation).unwrap(true);
		}
		// do not execute NoOps
		if (operation instanceof ITransactionalOperation) {
			if (((ITransactionalOperation) operation).isNoOp()) {
				return;
			}
		}
		// check if we can execute operation
		if (!operation.canExecute()) {
			throw new IllegalArgumentException("Operation cannot be executed.");
		}
		if (transaction != null) {
			// execute operation locally and add it to the current transaction
			operation.execute(new NullProgressMonitor(), null);
			transaction.add(operation);
		} else {
			// exectue operation directly on operation history
			operation.addContext(getUndoContext());
			getOperationHistory().execute(operation, new NullProgressMonitor(),
					null);
		}
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<T> classKey) {
		return ads.<T> getAdapter(classKey);
	}

	@Override
	public <T> T getAdapter(TypeToken<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> AdapterKey<T> getAdapterKey(T adapter) {
		return ads.getAdapterKey(adapter);
	}

	@Override
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		return ads.getAdapters();
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> classKey) {
		return ads.getAdapters(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		return ads.getAdapters(key);
	}

	@Override
	public IOperationHistory getOperationHistory() {
		return operationHistory;
	}

	@Override
	public Map<AdapterKey<? extends ITool<VR>>, ITool<VR>> getTools() {
		return ads.getAdapters(ITool.class);
	}

	@Override
	public IUndoContext getUndoContext() {
		return undoContext;
	}

	@Override
	public Map<AdapterKey<? extends IViewer<VR>>, IViewer<VR>> getViewers() {
		return ads.getAdapters(IViewer.class);
	}

	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Returns <code>true</code> if an execution transaction is currently open.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if an execution transaction is currently open,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isExecutionTransactionOpen() {
		return transaction != null;
	}

	@Override
	public boolean isExecutionTransactionOpen(ITool<VR> tool) {
		return transactionContext.contains(tool);
	}

	@Override
	public void openExecutionTransaction(ITool<VR> tool) {
		// if (transactionContext.contains(tool)) {
		// throw new IllegalStateException(
		// "A transaction is already active for tool " + tool + ".");
		// }
		// Create a new transaction in case the tool is the first one to open a
		// transaction.
		if (transactionContext.contains(tool)) {
			return; // TODO: is this legal??
		}
		transactionContext.add(tool);
		if (transactionContext.size() == 1
				&& transactionContext.contains(tool)) {
			if (transaction != null) {
				throw new IllegalStateException(
						"A transaction is already active, while this is the first tool within the transaction context.");
			}
			transaction = createExecutionTransaction();
		}
	}

	@Override
	public <T> void setAdapter(T adapter) {
		ads.setAdapter(adapter);
	}

	@Override
	public <T> void setAdapter(T adapter, String role) {
		ads.setAdapter(adapter, role);
	}

	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
		ads.setAdapter(adapterType, adapter);
	}

	@InjectAdapters
	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role) {
		ads.setAdapter(adapterType, adapter, role);
	}

	/**
	 * Sets the {@link IOperationHistory} that is used by this
	 * {@link AbstractDomain} to the given value. Operation history listeners
	 * are un-/registered accordingly.
	 *
	 * @param operationHistory
	 *            The new {@link IOperationHistory} for this domain.
	 */
	@Inject
	public void setOperationHistory(IOperationHistory operationHistory) {
		if (this.operationHistory != null
				&& this.operationHistory != operationHistory) {
			this.operationHistory
					.removeOperationHistoryListener(operationHistoryListener);
		}
		if (this.operationHistory != operationHistory) {
			this.operationHistory = operationHistory;
			if (this.operationHistory != null) {
				this.operationHistory
						.addOperationHistoryListener(operationHistoryListener);
				if (undoContext != null) {
					this.operationHistory.setLimit(undoContext,
							DEFAULT_UNDO_LIMIT);
				}
			}
		}
	}

	/**
	 * Sets the {@link IUndoContext} that is used by this {@link AbstractDomain}
	 * to the given value.
	 *
	 * @param undoContext
	 *            The new {@link IUndoContext} for this domain.
	 */
	@Inject
	public void setUndoContext(IUndoContext undoContext) {
		this.undoContext = undoContext;
		if (operationHistory != null && undoContext != null) {
			operationHistory.setLimit(undoContext, DEFAULT_UNDO_LIMIT);
		}
	}

	@Override
	public <T> void unsetAdapter(T adapter) {
		ads.unsetAdapter(adapter);
	}

}
