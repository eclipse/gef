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
 * The {@link FitToViewportLockAction} is a specialized
 * {@link FitToViewportAction} that implements toggle functionality, i.e. when
 * checked, this action will perform fit-to-viewport for every viewport size
 * change until it is unchecked again.
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
	 * Constructs a new {@link FitToViewportLockAction}.
	 */
	public FitToViewportLockAction() {
		super("Fit-To-Viewport Lock", IAction.AS_CHECK_BOX,
				MvcFxUiBundle.getDefault().getImageRegistry().getDescriptor(
						MvcFxUiBundle.IMG_ICONS_FIT_TO_VIEWPORT_LOCK));
	}

	/**
	 * This method is called when this action needs to observe the viewport size
	 * in order to perform fit-to-viewport if the viewport size changes.
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
	 * This method is called when the viewport size was changed.
	 */
	protected void onSizeChanged() {
		// only called when locked
		if (isEnabled()) {
			runWithEvent(null);
		}
	}

	@Override
	protected void register() {
		super.register();
		if (isChecked()) {
			lock();
			// initial fit-to-viewport
			runWithEvent(null);
		}
	}

	@Override
	public void setChecked(boolean checked) {
		if (isEnabled()) {
			if (isChecked() && !checked) {
				unlock();
			} else if (!isChecked() && checked) {
				lock();
			}
		}
		super.setChecked(checked);
	}

	/**
	 * This method is called when this action does no longer need to observe the
	 * viewport size, because no further fit-to-viewport should be performed if
	 * the viewport size changes.
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

	@Override
	protected void unregister() {
		unlock();
		super.unregister();
	}
}
