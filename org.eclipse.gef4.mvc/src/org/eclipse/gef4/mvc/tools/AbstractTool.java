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
package org.eclipse.gef4.mvc.tools;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;

/**
 *
 * @author anyssen
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractTool<VR> implements ITool<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private boolean active = false;
	private IDomain<VR> domain;

	@Override
	public void activate() {
		if (domain == null) {
			throw new IllegalStateException(
					"The IEditDomain has to be set via setDomain(IDomain) before activation.");
		}

		boolean oldActive = active;
		active = true;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}

		registerListeners();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	protected void commit(Collection<? extends IPolicy<VR>> policies) {
		ForwardUndoCompositeOperation operation = new ForwardUndoCompositeOperation(
				"Commit");
		for (IPolicy<VR> policy : policies) {
			if (policy instanceof ITransactional) {
				IUndoableOperation o = ((ITransactional) policy).commit();
				if (o != null) {
					operation.add(o);
				}
			}
		}

		IUndoableOperation executeOperation = operation.unwrap();
		if (executeOperation != null && executeOperation.canExecute()) {
			executeOperation(executeOperation);
		}
	}

	@Override
	public void deactivate() {
		unregisterListeners();

		boolean oldActive = active;
		active = false;
		if (oldActive != active) {
			pcs.firePropertyChange(IActivatable.ACTIVE_PROPERTY, oldActive,
					active);
		}
	}

	protected void executeOperation(IUndoableOperation operation) {
		IOperationHistory operationHistory = domain.getOperationHistory();
		operation.addContext(domain.getUndoContext());
		try {
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IDomain<VR> getAdaptable() {
		return domain;
	}

	@Override
	public IDomain<VR> getDomain() {
		return getAdaptable();
	}

	protected void init(Collection<? extends IPolicy<VR>> policies) {
		for (IPolicy<VR> policy : policies) {
			if (policy instanceof ITransactional) {
				((ITransactional) policy).init();
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	/**
	 * This method is called when a valid {@link IDomain} is attached to this
	 * tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void registerListeners() {
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setAdaptable(IDomain<VR> adaptable) {
		if (active) {
			throw new IllegalStateException(
					"The reference to the IDomain may not be changed while the tool is active. Please deactivate the tool before setting the IEditDomain and re-activate it afterwards.");
		}
		this.domain = adaptable;
	}

	/**
	 * This method is called when the attached {@link IDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void unregisterListeners() {
	}
}
