/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.zest.fx.models.LayoutModel;

public abstract class AbstractLayoutBehavior extends AbstractBehavior<Node> {

	private Runnable postLayout = new Runnable() {
		@Override
		public void run() {
			postLayout();
		}
	};

	private Runnable preLayout = new Runnable() {
		@Override
		public void run() {
			preLayout();
		}
	};

	@Override
	public void activate() {
		super.activate();
		LayoutModel layoutModel = getLayoutModel();
		layoutModel.schedulePreLayoutPass(preLayout);
		layoutModel.schedulePostLayoutPass(postLayout);
	}

	@Override
	public void deactivate() {
		LayoutModel layoutModel = getLayoutModel();
		layoutModel.unschedulePreLayoutPass(preLayout);
		layoutModel.unschedulePostLayoutPass(postLayout);
		super.deactivate();
	}

	protected abstract LayoutModel getLayoutModel();

	public <T> T getViewerAdapter(Class<T> key) {
		return getHost().getRoot().getViewer().getAdapter(key);
	}

	/**
	 * Called after a layout pass. Should be used to transfer layout information
	 * from the layout model over to the visualization.
	 */
	protected abstract void postLayout();

	/**
	 * Called before a layout pass. Should be used to transfer layout
	 * information from the visualization over to the layout model.
	 */
	protected abstract void preLayout();

}
