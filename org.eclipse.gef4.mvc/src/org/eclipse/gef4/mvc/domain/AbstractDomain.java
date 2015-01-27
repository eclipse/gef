/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.inject.AdaptableScope;
import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractDomain<VR> implements IDomain<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private AdaptableSupport<IDomain<VR>> ads = new AdaptableSupport<IDomain<VR>>(
			this, pcs);

	private ActivatableSupport<IDomain<VR>> acs = new ActivatableSupport<IDomain<VR>>(
			this, pcs);

	private IOperationHistory operationHistory;
	private IUndoContext undoContext;
	private ForwardUndoCompositeOperation transaction;

	/**
	 * Creates a new {@link AbstractDomain} instance, setting the
	 * {@link AdaptableScope} for each of its {@link IAdaptable}-compliant types
	 * (super classes implementing {@link IAdaptable} and super-interfaces
	 * extending {@link IAdaptable}) to the newly created instance (see
	 * AdaptableScopes#scopeTo(IAdaptable)).
	 */
	public AbstractDomain() {
		AdaptableScopes.scopeTo(this);
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
	public void closeExecutionTransaction() {
		// check if the transaction has an effect (or is empty)
		if (!transaction.getOperations().isEmpty()) {
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
		transaction = null;
	}

	@Override
	public void deactivate() {
		if (acs.isActive()) {
			acs.deactivate();
		}
	}

	@Override
	public void execute(IUndoableOperation operation) {
		IOperationHistory operationHistory = getOperationHistory();
		// IMPORTANT: if we have an open transaction in the domain, we should
		// not add an undo context, because our operation will be added to the
		// transaction (which has the undo context).
		if (!isTransactionOpen()) {
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

	private boolean isTransactionOpen() {
		return transaction != null;
	}

	@Override
	public void openExecutionTransaction() {
		transaction = new ForwardUndoCompositeOperation("Transaction");
		transaction.addContext(getUndoContext());
		getOperationHistory().openOperation(transaction,
				IOperationHistory.EXECUTE);
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

	@Inject(optional = true)
	// IMPORTANT: if sub-classes override, they will have to transfer the inject
	// annotation.
	public void setAdapters(
			@AdapterMap Map<AdapterKey<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractDomain) with those injected by Guice
		ads.setAdapters(adaptersWithKeys, false);
	}

	@Inject
	public void setOperationHistory(IOperationHistory stack) {
		operationHistory = stack;
	}

	@Inject
	public void setUndoContext(IUndoContext undoContext) {
		this.undoContext = undoContext;
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<? super T> key) {
		return ads.unsetAdapter(key);
	}
}
