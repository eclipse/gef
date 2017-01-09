/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.jface.action.IAction;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;

/**
 *
 * @author mwienand
 *
 */
public class FitToViewportLockAction extends FitToViewportAction {

	private ChangeListener<? super Number> sizeChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			onSizeChanged();
		}
	};

	/**
	 *
	 */
	public FitToViewportLockAction() {
		super("Fit-To-Viewport Lock", IAction.AS_CHECK_BOX,
				MvcFxUiBundle.getDefault().getImageRegistry().getDescriptor(
						MvcFxUiBundle.IMG_ICONS_FIT_TO_VIEWPORT_LOCK));
	}

	@Override
	protected void activate() {
		super.activate();
		if (isChecked()) {
			lock();
		}
	}

	@Override
	protected void deactivate() {
		unlock();
		super.deactivate();
	}

	/**
	 *
	 */
	protected void lock() {
		// register viewport size listeners
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			InfiniteCanvas infiniteCanvas = (InfiniteCanvas) canvas;
			infiniteCanvas.widthProperty().addListener(sizeChangeListener);
			infiniteCanvas.heightProperty().addListener(sizeChangeListener);
		}
	}

	/**
	 *
	 */
	protected void onSizeChanged() {
		if (isEnabled()) {
			runWithEvent(null);
		}
	}

	@Override
	public void setChecked(boolean checked) {
		if (isActive()) {
			if (isChecked() && !checked) {
				unlock();
			} else if (!isChecked() && checked) {
				lock();
			}
		}
		super.setChecked(checked);
	}

	/**
	 *
	 */
	protected void unlock() {
		// unregister viewport size listeners
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			InfiniteCanvas infiniteCanvas = (InfiniteCanvas) canvas;
			infiniteCanvas.widthProperty().removeListener(sizeChangeListener);
			infiniteCanvas.heightProperty().removeListener(sizeChangeListener);
		}
	}
}
