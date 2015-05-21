/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - major refactorings
 *******************************************************************************/
package org.eclipse.gef4.fx.ui.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The FXControlAdapter can be used to embed SWT controls into a JavaFX scene
 * graph.
 *
 * @author mwienand
 *
 * @param <T>
 *            The SWT Control class which is wrapped by this
 *            {@link FXControlAdapter}.
 */
public class FXControlAdapter<T extends Control> extends Region {

	private static final int[] FORWARD_SWT_EVENT_TYPES = new int[] {
			SWT.HardKeyDown, SWT.HardKeyUp, SWT.KeyDown, SWT.KeyUp,
			SWT.Gesture, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel, SWT.Move, SWT.Traverse,
			SWT.Verify, SWT.FocusIn };

	private FXCanvas canvas;
	private T control;
	private Listener swtToFXEventForwardingListener;

	private ChangeListener<Scene> sceneChangeListener;
	private ChangeListener<Window> sceneWindowChangeListener;
	private ChangeListener<Boolean> focusChangeListener;

	private IControlFactory<T> controlFactory;

	/**
	 * Creates a new {@link FXControlAdapter} which uses the given
	 * {@link IControlFactory} for the creation of the SWT {@link Control}.
	 *
	 * @param controlFactory
	 *            The {@link IControlFactory} to use to create the SWT
	 *            {@link Control}.
	 */
	public FXControlAdapter(IControlFactory<T> controlFactory) {
		// lazy creation of control in case canvas is obtained
		this.controlFactory = controlFactory;
		init();
	}

	/**
	 * Creates a new {@link FXControlAdapter} which wraps the given SWT
	 * {@link Control}.
	 *
	 * @param control
	 *            The SWT {@link Control} to wrap in this
	 *            {@link FXControlAdapter}.
	 */
	public FXControlAdapter(T control) {
		// detect SwtFXCanvas via given control
		canvas = getFXCanvas(control);
		if (canvas == null) {
			throw new IllegalArgumentException(
					"Control has to be parented by SwtFXCanvas.");
		}
		// assign control and register listeners
		setControl(control);
		init();
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computePrefHeight(width);
	}

	@Override
	protected double computeMaxWidth(double height) {
		return computePrefWidth(height);
	}

	@Override
	protected double computeMinHeight(double width) {
		return computePrefHeight(width);
	}

	@Override
	protected double computeMinWidth(double height) {
		return computePrefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		if (control == null) {
			return 0;
		}
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
	}

	@Override
	protected double computePrefWidth(double height) {
		if (control == null) {
			return 0;
		}
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	}

	/**
	 * Deactivates this {@link FXControlAdapter}, so that the SWT
	 * {@link Control} will not be re-created when the {@link FXCanvas} changes.
	 */
	public void dispose() {
		unregisterListeners();
	}

	/**
	 * We do not manage children. Therefore, it is illegal to alter the children
	 * list in any way.
	 */
	@Override
	protected ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}

	/**
	 * Returns the SWT {@link Control} that is wrapped by this
	 * {@link FXControlAdapter}.
	 *
	 * @return The SWT {@link Control} that is wrapped by this
	 *         {@link FXControlAdapter}.
	 */
	public T getControl() {
		return control;
	}

	protected FXCanvas getFXCanvas(Control control) {
		Control candidate = control;
		while (candidate != null) {
			candidate = candidate.getParent();
			if (candidate instanceof FXCanvas) {
				return (FXCanvas) candidate;
			}
		}
		return null;
	}

	protected FXCanvas getFXCanvas(Node node) {
		if (node == null) {
			return null;
		}
		return getFXCanvas(node.getScene());
	}

	protected FXCanvas getFXCanvas(Scene scene) {
		if (scene != null) {
			return getFXCanvas(scene.getWindow());
		}
		return null;
	}

	protected FXCanvas getFXCanvas(Window window) {
		if (window != null) {
			// Obtain FXCanvas by accessing outer class
			// of
			// FXCanvas$HostContainer
			FXCanvas canvas = ReflectionUtils.getPrivateFieldValue(
					ReflectionUtils.<Object> getPrivateFieldValue(window,
							"host"), "this$0");
			return canvas;
		}
		return null;
	}

	/**
	 * Used to register special listeners on the specific {@link Control}.
	 *
	 * @param control
	 *            The SWT {@link Control} that is wrapped by this
	 *            {@link FXControlAdapter}.
	 */
	protected void hookControl(T control) {
		FXCanvas swtFXCanvas = getFXCanvas(control);
		if (swtFXCanvas == null || swtFXCanvas != canvas) {
			throw new IllegalArgumentException(
					"Control needs to be hooked to the same canvas as this adapter.");
		}

		// register SWT listeners to forward events to JavaFX
		registerSwtToFXEventForwarders(swtFXCanvas);
	}

	protected void init() {
		// by default, be part of focus traversal cycle
		focusTraversableProperty().set(true);

		// register listeners
		registerListeners();
	}

	protected void registerListeners() {
		focusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean hadFocus, Boolean hasFocus) {
				// if we obtained focus from JavaFX and the SWT control is not
				// focused, forward the focus.
				if (control != null && !control.isFocusControl() && !hadFocus
						&& hasFocus) {
					control.forceFocus();
				}
			}
		};
		focusedProperty().addListener(focusChangeListener);

		sceneChangeListener = new ChangeListener<Scene>() {

			@Override
			public void changed(ObservableValue<? extends Scene> observable,
					Scene oldValue, Scene newValue) {

				// if the scene changed, see if we can obtain an SwtFXCanvas
				setCanvas(getFXCanvas(newValue));

				// register/unregister listener to detect SwtFXCanvas changes of
				// new scene
				if (oldValue != null) {
					oldValue.windowProperty().removeListener(
							sceneWindowChangeListener);
					sceneWindowChangeListener = null;
				}
				if (newValue != null) {
					sceneWindowChangeListener = new ChangeListener<Window>() {

						@Override
						public void changed(
								ObservableValue<? extends Window> observable,
								Window oldValue, Window newValue) {
							setCanvas(getFXCanvas(newValue));
						}
					};
					newValue.windowProperty().addListener(
							sceneWindowChangeListener);
				}
			}
		};
		sceneProperty().addListener(sceneChangeListener);
	}

	protected void registerSwtToFXEventForwarders(final FXCanvas newCanvas) {
		swtToFXEventForwardingListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.FocusIn:
					requestFocus();
					break;
				default:
					Point location = control.getLocation();
					event.x += location.x;
					event.y += location.y;
					newCanvas.notifyListeners(event.type, event);
				}
			}
		};
		for (int eventType : FORWARD_SWT_EVENT_TYPES) {
			control.addListener(eventType, swtToFXEventForwardingListener);
		}
	}

	@Override
	public void relocate(double paramDouble1, double paramDouble2) {
		super.relocate(paramDouble1, paramDouble2);
		updateSwtBounds();
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		updateSwtBounds();
	}

	protected void setCanvas(FXCanvas newCanvas) {
		// if we do not have a control factory, we are bound to an existing
		// control and will not be able to handle canvas changes
		if (controlFactory == null) {
			if (newCanvas != null && this.canvas != newCanvas) {
				throw new IllegalArgumentException(
						"May not bind this adapter to another SwtFXCanvas than that of the adapted control.");
			}
		} else {
			// use control factory to dispose/create controls as needed upon
			// canvas
			// changes
			FXCanvas oldCanvas = this.canvas;
			if (oldCanvas != null && oldCanvas != newCanvas) {
				T oldControl = getControl();
				setControl(null);
				oldControl.dispose();
				oldControl = null;
			}
			this.canvas = newCanvas;
			if (newCanvas != null && oldCanvas != newCanvas) {
				T newControl = controlFactory.createControl(newCanvas);
				setControl(newControl);
			}
		}
	}

	protected void setControl(T control) {
		T oldControl = this.control;
		if (oldControl != null) {
			unhookControl(oldControl);
		}
		this.control = control;
		if (control != null) {
			hookControl(control);
		}
	}

	/**
	 * Used to unregister special listeners from the specific {@link Control}.
	 */
	protected void unhookControl(T control) {
		unregisterSwtToFXEventForwarders();
	}

	protected void unregisterListeners() {
		sceneProperty().removeListener(sceneChangeListener);
		focusedProperty().removeListener(focusChangeListener);
	}

	protected void unregisterSwtToFXEventForwarders() {
		for (int eventType : FORWARD_SWT_EVENT_TYPES) {
			control.removeListener(eventType, swtToFXEventForwardingListener);
		}
		swtToFXEventForwardingListener = null;
	}

	public void updateSwtBounds() {
		if (control == null) {
			return;
		}

		Bounds bounds = localToScene(getLayoutBounds());

		control.setBounds((int) Math.ceil(bounds.getMinX()),
				(int) Math.ceil(bounds.getMinY()),
				(int) Math.ceil(bounds.getWidth()),
				(int) Math.ceil(bounds.getHeight()));
	}

}