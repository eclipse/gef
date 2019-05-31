/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.gestures.IGesture;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

import javafx.application.Platform;
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

	private ReadOnlyObjectProperty<Bounds> contentBoundsProperty;
	private Affine contentTransform;
	private InfiniteCanvas infiniteCanvas;
	private boolean needsExec = false;
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
			needsExec = true;
		}
	};
	private ChangeListener<? super Number> scrollOffsetChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			// unlock when the user manually scrolls
			setChecked(false);
		}
	};
	private ChangeListener<? super Number> sizeChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			needsExec = true;
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
		infiniteCanvas.widthProperty().removeListener(sizeChangeListener);
		infiniteCanvas.heightProperty().removeListener(sizeChangeListener);
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
		infiniteCanvas.widthProperty().addListener(sizeChangeListener);
		infiniteCanvas.heightProperty().addListener(sizeChangeListener);

		IDomain domain = getViewer().getDomain();

		if (domain instanceof HistoricizingDomain) {
			IOperationHistory history = ((HistoricizingDomain) domain)
					.getOperationHistory();
			history.addOperationHistoryListener(ev -> {
				if (needsExec) {
					if (ev.getEventType() == OperationHistoryEvent.OPERATION_ADDED
							|| ev.getEventType() == OperationHistoryEvent.OPERATION_REMOVED) {
						needsExec = false;
						Platform.runLater(() -> Platform
								.runLater(() -> Platform.runLater(() -> {
									runIfEnabled();
								})));
					}
				}
			});
		}
	}

	/**
	 * This method is called when this action needs to observe the viewport size
	 * in order to perform fit-to-viewport if the viewport size changes.
	 */
	protected void lock() {
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			infiniteCanvas = (InfiniteCanvas) canvas;
			contentTransform = infiniteCanvas.getContentTransform();
			contentBoundsProperty = infiniteCanvas.contentBoundsProperty();
			enableViewportListeners();
		}
	}

	/**
	 * This method is invoked in response to content bounds changes.
	 * <p>
	 * It either unlocks this action or performs the fit-to-viewport action
	 * depending on whether user interaction is in progress (unlock) or not
	 * (fit-to-viewport).
	 *
	 * @since 5.1
	 */
	protected void onContentBoundsChanged() {
		IDomain domain = getViewer().getDomain();
		Collection<IGesture> gestures = domain.getGestures().values();
		boolean isTransactionOpen = false;
		for (Iterator<IGesture> it = gestures.iterator(); !isTransactionOpen
				&& it.hasNext();) {
			IGesture gesture = (IGesture) it.next();
			isTransactionOpen = domain.isExecutionTransactionOpen(gesture);
		}
		// prevent fit-to-viewport during interaction
		if (!isTransactionOpen) {
			runIfEnabled();
		}
	}

	/**
	 * This method is called when the viewport size was changed. It performs
	 * fit-to-viewport if this action is enabled.
	 */
	protected void onSizeChanged() {
		// XXX: should only be called when locked
		runIfEnabled();
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

	/**
	 * Tests if {@link #isEnabled()} returns true, and only if that is the case,
	 * invokes {@link #runWithEvent(Event)} passing <code>null</code> for the
	 * Event parameter.
	 *
	 * @since 5.1
	 */
	protected void runIfEnabled() {
		// XXX: should only be called when locked
		if (isEnabled()) {
			runWithEvent(null);
		}
	}

	@Override
	public void runWithEvent(Event event) {
		needsExec = false;
		if (isChecked()) {
			disableViewportListeners();
			super.runWithEvent(event);
			enableViewportListeners();
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
		if (infiniteCanvas != null) {
			disableViewportListeners();
		}
	}

	@Override
	protected void unregister() {
		unlock();
		super.unregister();
	}
}
