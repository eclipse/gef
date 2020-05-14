/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.behaviors;

import org.eclipse.gef.mvc.fx.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.models.HidingModel;

import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

/**
 * The {@link AbstractHidingBehavior} registers listeners on the
 * {@link HidingModel} upon activation. When the {@link HidingModel} changes,
 * the hidden status of the {@link #getHost() host} is
 * {@link #determineHiddenStatus() determined}. If the hidden status of the
 * {@link #getHost() host} changed, either {@link #hide()} or {@link #show()}
 * will be called, respectively. By default, the {@link #getHost() host}'s
 * visual's visibility and mouse-transparency are changed depending on the
 * hidden status.
 *
 * @author mwienand
 *
 */
public abstract class AbstractHidingBehavior extends AbstractBehavior {

	private SetChangeListener<org.eclipse.gef.graph.Node> hidingModelObserver = new SetChangeListener<org.eclipse.gef.graph.Node>() {
		@Override
		public void onChanged(SetChangeListener.Change<? extends org.eclipse.gef.graph.Node> change) {
			onHidingModelChange(change);
		}
	};

	private boolean isHidden;

	/**
	 * Returns <code>true</code> if the {@link #getHost() host} is currently
	 * hidden. Otherwise, returns <code>false</code>.
	 *
	 * @return <code>true</code> if the {@link #getHost() host} is currently
	 *         hidden, otherwise <code>false</code>.
	 */
	protected abstract boolean determineHiddenStatus();

	@Override
	protected void doActivate() {
		// register for change notifications regarding hidden nodes
		HidingModel hidingModel = getHidingModel();
		hidingModel.hiddenProperty().addListener(hidingModelObserver);
	}

	@Override
	protected void doDeactivate() {
		HidingModel hidingModel = getHidingModel();
		hidingModel.hiddenProperty().removeListener(hidingModelObserver);
	}

	/**
	 * Returns the {@link HidingModel} that is installed on the {@link IViewer}
	 * of the {@link #getHost() host}.
	 *
	 * @return The {@link HidingModel} that is installed on the {@link IViewer}
	 *         of the {@link #getHost() host}.
	 */
	protected HidingModel getHidingModel() {
		return getHost().getRoot().getViewer().getAdapter(HidingModel.class);
	}

	/**
	 * Hides the {@link #getHost() host}. By default, the {@link #getHost()
	 * host}'s visual's visibility will be set to <code>false</code> and its
	 * mouse-transparency will be set to <code>true</code>.
	 */
	protected void hide() {
		// hide host
		getHost().getVisual().setVisible(false);
		getHost().getVisual().setMouseTransparent(true);
	}

	/**
	 * Returns <code>true</code> if the {@link #getHost() host} is currently
	 * considered to be hidden. Otherwise, returns <code>false</code>.
	 *
	 * @return <code>true</code> if the {@link #getHost() host} is currently
	 *         considered to be hidden, otherwise <code>false</code>.
	 */
	protected boolean isHidden() {
		return isHidden;
	}

	/**
	 * Called upon {@link HidingModel} changes. Determines if the
	 * {@link #getHost() host} is now hidden using
	 * {@link #determineHiddenStatus()} and compares the result with the
	 * previous hidden status. If the {@link #getHost() host} was previously
	 * hidden and is not hidden anymore, {@link #show()} is called. Otherwise,
	 * {@link #hide()} is called.
	 *
	 * @param change
	 *            The change event of the {@link HidingModel}.
	 */
	protected void onHidingModelChange(Change<? extends org.eclipse.gef.graph.Node> change) {
		// check if we have to prune/unprune the host
		boolean wasHidden = isHidden;
		isHidden = determineHiddenStatus();
		if (wasHidden && !isHidden) {
			show();
		} else if (!wasHidden && isHidden) {
			hide();
		}
	}

	/**
	 * Shows the {@link #getHost() host}. By default, the {@link #getHost()
	 * host}'s visual's visibility will be set to <code>true</code> and its
	 * mouse-transparency will be set to <code>false</code>.
	 */
	protected void show() {
		// show host
		getHost().getVisual().setVisible(true);
		getHost().getVisual().setMouseTransparent(false);
	}

}
