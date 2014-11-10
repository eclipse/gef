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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editpolicies.AbstractEditPolicy.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractPolicy<VR> implements IPolicy<VR> {

	private IVisualPart<VR> host;
	private final Map<IVisualPart<?>, Boolean> initialRefreshVisual = new HashMap<IVisualPart<?>, Boolean>();

	protected void commit(IPolicy<VR> policy) {
		if (policy != null && policy instanceof ITransactional) {
			IUndoableOperation o = ((ITransactional) policy).commit();
			if (o != null && o.canExecute()) {
				getHost().getRoot().getViewer().getDomain().execute(o);
			}
		}
	}

	protected void disableRefreshVisuals(IVisualPart<?> anchorage) {
		initialRefreshVisual.put(anchorage, anchorage.isRefreshVisual());
	}

	protected void enableRefreshVisuals(IVisualPart<VR> part) {
		part.setRefreshVisual(initialRefreshVisual.remove(part));
	}

	@Override
	public IVisualPart<VR> getAdaptable() {
		return getHost();
	}

	@Override
	public IVisualPart<VR> getHost() {
		return host;
	}

	protected void init(IPolicy<VR> policy) {
		if (policy != null && policy instanceof ITransactional) {
			((ITransactional) policy).init();
		}
	}

	@Override
	public void setAdaptable(IVisualPart<VR> adaptable) {
		this.host = adaptable;
	}

}