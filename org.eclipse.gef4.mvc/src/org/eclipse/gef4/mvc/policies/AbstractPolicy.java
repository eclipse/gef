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
 * Abstract base implementation of {@link IPolicy}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractPolicy<VR> implements IPolicy<VR> {

	private IVisualPart<VR, ? extends VR> host;
	private final Map<IVisualPart<VR, ? extends VR>, Boolean> initialRefreshVisual = new HashMap<IVisualPart<VR, ? extends VR>, Boolean>();

	protected void commit(IPolicy<VR> policy) {
		if (policy != null && policy instanceof ITransactional) {
			IUndoableOperation o = ((ITransactional) policy).commit();
			if (o != null && o.canExecute()) {
				getHost().getRoot().getViewer().getDomain().execute(o);
			}
		}
	}

	/**
	 * Disable that the given {@link IVisualPart} refreshed its visual, if this
	 * was not already the case (see
	 * {@link IVisualPart#setRefreshVisual(boolean)}). Stores the state (whether
	 * the part was still refreshing its visual or not) so it can be restored
	 * later (see {@link #enableRefreshVisuals(IVisualPart)}).
	 *
	 * @param part
	 *            The {@link IVisualPart} whose visual refresh is to be
	 *            disabled.
	 */
	// TODO: rename to storeAndDisableRefreshVisual()
	protected void disableRefreshVisuals(IVisualPart<VR, ? extends VR> part) {
		initialRefreshVisual.put(part, part.isRefreshVisual());
		part.setRefreshVisual(false);
	}

	// TODO: rename to restoreRefreshVisual()
	protected void enableRefreshVisuals(IVisualPart<VR, ? extends VR> part) {
		part.setRefreshVisual(initialRefreshVisual.remove(part));
	}

	@Override
	public IVisualPart<VR, ? extends VR> getAdaptable() {
		return getHost();
	}

	@Override
	public IVisualPart<VR, ? extends VR> getHost() {
		return host;
	}

	protected void init(IPolicy<VR> policy) {
		if (policy != null && policy instanceof ITransactional) {
			((ITransactional) policy).init();
		}
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		this.host = adaptable;
	}

}