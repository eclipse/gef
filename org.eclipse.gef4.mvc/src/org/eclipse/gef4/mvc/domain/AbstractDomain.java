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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.inject.AdaptableScope;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.common.inject.AdapterMapInjector;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

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

	/**
	 * A {@link PropertyChangeSupport} that is used as a delegate to notify
	 * listeners about changes to this object. May be used by subclasses to
	 * trigger the notification of listeners.
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private ActivatableSupport acs = new ActivatableSupport(this, pcs);
	private AdaptableSupport<IDomain<VR>> ads = new AdaptableSupport<IDomain<VR>>(
			this, pcs);

	private IOperationHistory operationHistory;
	private IUndoContext undoContext;
	private ForwardUndoCompositeOperation transaction;
	private Set<ITool<VR>> transactionContext = new HashSet<ITool<VR>>();
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
	}

	@Override
	public void activate() {
		if (!acs.isActive()) {
			acs.activate();
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void closeExecutionTransaction(ITool<VR> tool) {
		transactionContext.remove(tool);

		// check if the transaction has an effect (or is empty)
		if (transaction != null && !transaction.getOperations().isEmpty()) {
			// adjust the label
			transaction.setLabel(transaction.getOperations().iterator().next()
					.getLabel());
			// successfully close operation
			getOperationHistory().closeOperation(true, true,
					IOperationHistory.EXECUTE);
		} else {
			getOperationHistory().closeOperation(true, false,
					IOperationHistory.EXECUTE);
		}

		if (!transactionContext.isEmpty()) {
			// open new transaction for the remaining tools in the transaction
			// context
			transaction = createExecutionTransaction();
		} else {
			transaction = null;
		}
	}

	/**
	 * Creates a {@link ForwardUndoCompositeOperation} which is used to store
	 * the operations within an execution transaction. The operation is opened
	 * on the {@link #getOperationHistory() operation history}.
	 *
	 * @return A new {@link ForwardUndoCompositeOperation} which is configured
	 *         to store the operations within an execution transaction.
	 */
	protected ForwardUndoCompositeOperation createExecutionTransaction() {
		ForwardUndoCompositeOperation transaction = new ForwardUndoCompositeOperation(
				"Transaction");
		transaction.addContext(getUndoContext());
		getOperationHistory().openOperation(transaction,
				IOperationHistory.EXECUTE);
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
	public void execute(IUndoableOperation operation) {
		IOperationHistory operationHistory = getOperationHistory();
		// IMPORTANT: if we have an open transaction in the domain, we should
		// not add an undo context, because our operation will be added to the
		// transaction (which has the undo context).
		if (!isExecutionTransactionOpen()) {
			operation.addContext(getUndoContext());
		}
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T> T getAdapter(AdapterKey<? super T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<? super T> classKey) {
		return ads.<T> getAdapter(classKey);
	}

	@Override
	public <T> T getAdapter(TypeToken<? super T> key) {
		return ads.getAdapter(key);
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
		if (!isExecutionTransactionOpen()) {
			transaction = createExecutionTransaction();
		}
		transactionContext.add(tool);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public <T> void setAdapter(AdapterKey<? super T> key, T adapter) {
		ads.setAdapter(key, adapter);
	}

	@Override
	public <T> void setAdapter(Class<? super T> key, T adapter) {
		ads.setAdapter(key, adapter);
	}

	@Override
	public <T> void setAdapter(TypeToken<? super T> key, T adapter) {
		ads.setAdapter(key, adapter);
	}

	/**
	 * Adds the set of adapters to this {@link IAdaptable}. This method should
	 * not be used by clients but is intended for injection of adapters by means
	 * of an {@link AdapterMapInjector}.
	 * <p>
	 * <b>IMPORTANT: If sub-classes override this method, they will have to
	 * transfer the inject annotation to the overwritten method to ensure that
	 * adapter map injection is still functional.</b>
	 *
	 * @param adaptersWithKeys
	 *            The adapters to add to this {@link IAdaptable} (by means of
	 *            injection)
	 * @see AdapterMap
	 */
	@Inject(optional = true)
	public void setAdapters(
			@AdapterMap Map<AdapterKey<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractDomain) with those injected by Guice
		ads.setAdapters(adaptersWithKeys, false);
	}

	/**
	 * Sets the {@link IOperationHistory} that is used by this
	 * {@link AbstractDomain} to the given value. Operation history listeners
	 * are un-/registered accordingly.
	 *
	 * @param stack
	 *            The new {@link IOperationHistory} for this domain.
	 */
	@Inject
	public void setOperationHistory(IOperationHistory stack) {
		if (operationHistory != null && operationHistory != stack) {
			operationHistory
					.removeOperationHistoryListener(operationHistoryListener);
		}
		if (operationHistory != stack) {
			operationHistory = stack;
			if (operationHistory != null) {
				operationHistory
						.addOperationHistoryListener(operationHistoryListener);
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
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<? super T> key) {
		return ads.unsetAdapter(key);
	}

}
