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

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Inject;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractDomain<VR> implements IDomain<VR> {

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private AdaptableSupport<IDomain<VR>> ads = new AdaptableSupport<IDomain<VR>>(
			this, pcs);

	private ActivatableSupport<IDomain<VR>> acs = new ActivatableSupport<IDomain<VR>>(
			this, pcs);

	private IOperationHistory operationHistory;
	private IUndoContext undoContext;

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
	public void deactivate() {
		if (acs.isActive()) {
			acs.deactivate();
		}
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<? super T> classKey) {
		return ads.<T> getAdapter(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<?> classKey) {
		return ads.getAdapters(classKey);
	}

	@Override
	public IOperationHistory getOperationHistory() {
		return operationHistory;
	}

	public Map<AdapterKey<? extends ITool<VR>>, ITool<VR>> getTools() {
		return ads.getAdapters(ITool.class);
	}

	@Override
	public IUndoContext getUndoContext() {
		return undoContext;
	}

	public Map<AdapterKey<? extends IViewer<VR>>, IViewer<VR>> getViewers() {
		return ads.getAdapters(IViewer.class);
	}

	@Override
	public boolean isActive() {
		return acs.isActive();
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public <T> void setAdapter(AdapterKey<T> key, T adapter) {
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
	public <T> T unsetAdapter(AdapterKey<T> key) {
		return ads.unsetAdapter(key);
	}
}
