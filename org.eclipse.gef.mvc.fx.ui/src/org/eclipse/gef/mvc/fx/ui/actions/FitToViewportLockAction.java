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

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.tools.ITool;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.TransformChangedEvent;

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

	private boolean boundsChanged = false;
	private boolean sizeChanged = false;
	private boolean offsetChanged = false;
	private double savedContentBoundsWidth = 0d;
	private double savedContentBoundsHeight = 0d;
	private ReadOnlyObjectProperty<Bounds> contentBoundsProperty;
	private Affine contentTransform;
	private InfiniteCanvas infiniteCanvas;
	private boolean running;
	private EventHandler<TransformChangedEvent> trafoChangeListener = new EventHandler<TransformChangedEvent>() {
		@Override
		public void handle(TransformChangedEvent event) {
			// unlock when the user manually zooms
			setChecked(false);
		}
	};
	private ChangeListener<? super Bounds> contentBoundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			boolean transaction = false;
			if (!boundsChanged) {
				// determine if a transaction is running
				for (ITool tool : getViewer().getDomain().getTools().values()) {
					if (getViewer().getDomain()
							.isExecutionTransactionOpen(tool)) {
						transaction = true;
						break;
					}
				}
				if (transaction) {
					boundsChanged = true;
				} else {
					// immediately fit-to-viewport if no transaction is running
					onSizeChanged();
				}
			}
		}
	};
	private ChangeListener<? super Number> scrollOffsetChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			offsetChanged = true;
		}
	};
	private ChangeListener<? super Number> sizeChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			// fit-to-viewport when the user resizes the viewport
			sizeChanged = true;
			onSizeChanged();
		}
	};
	private IOperationHistoryListener historyListener = new IOperationHistoryListener() {
		@Override
		public void historyNotification(OperationHistoryEvent event) {
			if (event.getEventType() == OperationHistoryEvent.OPERATION_ADDED) {
				// flush changes
				Bounds contentBounds = infiniteCanvas.getContentBounds();
				if (offsetChanged && (!boundsChanged || (contentBounds
						.getWidth() == savedContentBoundsWidth
						&& contentBounds
								.getHeight() == savedContentBoundsHeight))) {
					// unlock upon manual scrolling
					setChecked(false);
				} else if (boundsChanged && !sizeChanged) {
					// fit-to-viewport otherwise
					onSizeChanged();
				}
				// reset state
				boundsChanged = false;
				sizeChanged = false;
				offsetChanged = false;
			}
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
	 * Disables all viewport listeners that react to scroll offset, viewport
	 * transformation, or viewport-/scrollable-/content-bounds changes.
	 */
	protected void disableViewportListeners() {
		infiniteCanvas.horizontalScrollOffsetProperty()
				.removeListener(scrollOffsetChangeListener);
		infiniteCanvas.verticalScrollOffsetProperty()
				.removeListener(scrollOffsetChangeListener);
		contentTransform.removeEventHandler(
				TransformChangedEvent.TRANSFORM_CHANGED, trafoChangeListener);
		contentBoundsProperty.removeListener(contentBoundsChangeListener);
	}

	/**
	 * Enables all viewport listeners that react to scroll offset, viewport
	 * transformation, or viewport-/scrollable-/content-bounds changes.
	 * <p>
	 * Moreover, stores the content bounds size, so that the size can later be
	 * tested for changes.
	 */
	protected void enableViewportListeners() {
		infiniteCanvas.horizontalScrollOffsetProperty()
				.addListener(scrollOffsetChangeListener);
		infiniteCanvas.verticalScrollOffsetProperty()
				.addListener(scrollOffsetChangeListener);
		contentTransform.addEventHandler(
				TransformChangedEvent.TRANSFORM_CHANGED, trafoChangeListener);
		contentBoundsProperty.addListener(contentBoundsChangeListener);
		// save content bounds to detect scrolling
		savedContentBoundsWidth = infiniteCanvas.getContentBounds().getWidth();
		savedContentBoundsHeight = infiniteCanvas.getContentBounds()
				.getHeight();
	}

	/**
	 * This method is called when this action needs to observe the viewport size
	 * in order to perform fit-to-viewport if the viewport size changes.
	 */
	protected void lock() {
		// register history listener
		((HistoricizingDomain) getViewer().getDomain()).getOperationHistory()
				.addOperationHistoryListener(historyListener);
		// register viewport size listeners
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			infiniteCanvas = (InfiniteCanvas) canvas;
			contentTransform = infiniteCanvas.getContentTransform();
			contentBoundsProperty = infiniteCanvas.contentBoundsProperty();
			enableViewportListeners();
			infiniteCanvas.widthProperty().addListener(sizeChangeListener);
			infiniteCanvas.heightProperty().addListener(sizeChangeListener);
		}
	}

	/**
	 * This method is called when the viewport size was changed. It performs
	 * fit-to-viewport if this action is enabled.
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
	public void runWithEvent(Event event) {
		// FIXME: Prevent re-entrance by properly disabling listeners instead of
		// guarding against re-entrance here by using the 'running' flag.
		if (this.running) {
			return;
		}
		this.running = true;
		if (isChecked()) {
			disableViewportListeners();
			super.runWithEvent(event);
			enableViewportListeners();
		}
		this.running = false;
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
		// remove history listener
		((HistoricizingDomain) getViewer().getDomain()).getOperationHistory()
				.removeOperationHistoryListener(historyListener);
		// unregister viewport size listeners
		if (infiniteCanvas != null) {
			disableViewportListeners();
			infiniteCanvas.widthProperty().removeListener(sizeChangeListener);
			infiniteCanvas.heightProperty().removeListener(sizeChangeListener);
		}
		// reset state
		boundsChanged = false;
		sizeChanged = false;
		offsetChanged = false;
	}

	@Override
	protected void unregister() {
		unlock();
		super.unregister();
	}
}
