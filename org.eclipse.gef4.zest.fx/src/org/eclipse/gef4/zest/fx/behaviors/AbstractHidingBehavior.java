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

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.zest.fx.models.HidingModel;

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

	protected abstract boolean determineHiddenStatus();

	protected HidingModel getHidingModel() {
		return getHost().getRoot().getViewer().getAdapter(HidingModel.class);
	}

	protected void hide() {
		// hide host
		getHost().getVisual().setVisible(false);
		getHost().getVisual().setMouseTransparent(true);
	}

	protected boolean isHidden() {
		return isHidden;
	}

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

	protected void show() {
		// show host
		getHost().getVisual().setVisible(true);
		getHost().getVisual().setMouseTransparent(false);
	}

}
