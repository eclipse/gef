/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.swtfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractSwtFXControl<T extends Control> extends Region {

	private static final int[] FORWARD_EVENT_TYPES = new int[] {
			SWT.HardKeyDown, SWT.HardKeyUp, SWT.KeyDown, SWT.KeyUp,
			SWT.Gesture, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel, SWT.Move, SWT.Traverse,
			SWT.Verify };

	private static final int[] FOCUS_EVENT_TYPES = new int[] { SWT.FocusIn,
			SWT.FocusOut };

	/**
	 * Retrieves the underlying {@link SwtFXCanvas} from a given {@link Node}.
	 * In case no {@link SwtFXCanvas} can be found, <code>null</code> is
	 * returned.
	 *
	 * @param node
	 * @return the {@link SwtFXCanvas} of the given {@link Node} or
	 *         <code>null</code>
	 */
	protected static SwtFXCanvas getSwtFXCanvas(Node node) {
		if (node == null) {
			return null;
		}
		Scene scene = node.getScene();
		if (scene != null) {
			if (!(scene instanceof SwtFXScene)) {
				throw new IllegalArgumentException();
			}
			SwtFXCanvas fxCanvas = ((SwtFXScene) scene).getFXCanvas();
			return fxCanvas;
		}
		return null;
	}

	private SwtFXCanvas canvas;

	private T control;

	private Listener swtForwardListener;

	private Listener swtFocusListener;

	private ChangeListener<Boolean> focusChangeListener;

	public AbstractSwtFXControl() {
		parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> observable,
					Parent oldValue, Parent newValue) {
				SwtFXCanvas newCanvas = getSwtFXCanvas(newValue);
				canvasChanged(newCanvas);
			}
		});
	}

	protected void canvasChanged(SwtFXCanvas newCanvas) {

		if (this.canvas != null && this.canvas != newCanvas) {
			unhookControl(control);
			unregisterEventForwarding();
			unregisterFocusForwarding();
			control.dispose();
			control = null;
		}
		if (newCanvas != null && this.canvas != newCanvas) {
			control = createControl(newCanvas);
			registerEventForwarding(newCanvas);
			registerFocusForwarding();
			hookControl(control);
		}
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

	abstract protected T createControl(SwtFXCanvas fxCanvas);

	/**
	 * We do not manage children. Therefore, it is illegal to alter the children
	 * list in any way.
	 */
	@Override
	protected ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}

	public T getControl() {
		return control;
	}

	/**
	 * Used to register special listeners on the specific {@link Control}.
	 */
	protected void hookControl(T control) {

	}

	private void registerEventForwarding(final SwtFXCanvas newCanvas) {
		swtForwardListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point location = control.getLocation();
				event.x += location.x;
				event.y += location.y;
				newCanvas.notifyListeners(event.type, event);
			}
		};
		for (int eventType : FORWARD_EVENT_TYPES) {
			control.addListener(eventType, swtForwardListener);
		}
	}

	private void registerFocusForwarding() {
		swtFocusListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.FocusIn:
					requestFocus();
					break;
				case SWT.FocusOut:
					break;
				default:
					throw new IllegalStateException(
							"Unable to handle event of type <" + event.type
									+ ">.");
				}
			}
		};
		for (int eventType : FOCUS_EVENT_TYPES) {
			control.addListener(eventType, swtFocusListener);
		}

		focusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean hadFocus, Boolean hasFocus) {
				if (control != null && !hadFocus && hasFocus) {
					control.forceFocus();
				}
			}
		};
		focusedProperty().addListener(focusChangeListener);
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

	/**
	 * Used to unregister special listeners from the specific {@link Control}.
	 */
	protected void unhookControl(T control) {
	}

	private void unregisterEventForwarding() {
		for (int eventType : FORWARD_EVENT_TYPES) {
			control.removeListener(eventType, swtForwardListener);
		}
		swtForwardListener = null;
	}

	private void unregisterFocusForwarding() {
		for (int eventType : FOCUS_EVENT_TYPES) {
			control.removeListener(eventType, swtFocusListener);
		}
		swtFocusListener = null;
		focusedProperty().removeListener(focusChangeListener);
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