/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.HidingModel;

import javafx.scene.Node;

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
public abstract class AbstractHidingBehavior extends AbstractBehavior<Node> {

	private PropertyChangeListener hidingModelListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (HidingModel.HIDDEN_PROPERTY.equals(evt.getPropertyName())) {
				onHidingModelChange();
			}
		}
	};

	private boolean isHidden;

	@Override
	public void activate() {
		super.activate();
		// register for change notifications regarding hidden nodes
		HidingModel hidingModel = getHidingModel();
		hidingModel.addPropertyChangeListener(hidingModelListener);
	}

	@Override
	public void deactivate() {
		HidingModel hidingModel = getHidingModel();
		hidingModel.removePropertyChangeListener(hidingModelListener);
		super.deactivate();
	}

	/**
	 * Returns <code>true</code> if the {@link #getHost() host} is currently
	 * hidden. Otherwise, returns <code>false</code>.
	 *
	 * @return <code>true</code> if the {@link #getHost() host} is currently
	 *         hidden, otherwise <code>false</code>.
	 */
	protected abstract boolean determineHiddenStatus();

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
	 */
	protected void onHidingModelChange() {
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
