package org.eclipse.gef4.swtfx.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SwtFXControlAdapter<T extends Control> extends Region {

	private static final int[] FORWARD_EVENT_TYPES = new int[] {
			SWT.HardKeyDown, SWT.HardKeyUp, SWT.KeyDown, SWT.KeyUp,
			SWT.Gesture, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel, SWT.Move, SWT.Traverse,
			SWT.Verify };
	private static final int[] FOCUS_EVENT_TYPES = new int[] { SWT.FocusIn,
			SWT.FocusOut };

	protected static SwtFXCanvas getSwtFXCanvas(Control control) {
		Control candidate = control;
		while (candidate != null) {
			candidate = candidate.getParent();
			if (candidate instanceof SwtFXCanvas) {
				return (SwtFXCanvas) candidate;
			}
		}
		return null;
	}

	private T control;
	private Listener swtForwardListener;
	private Listener swtFocusListener;
	private ChangeListener<Boolean> focusChangeListener;

	public SwtFXControlAdapter() {
		super();
		// forward JavaFX focus events to SWT control
		focusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean hadFocus, Boolean hasFocus) {
				if (getControl() != null && !hadFocus && hasFocus) {
					getControl().forceFocus();
				}
			}
		};
		focusedProperty().addListener(focusChangeListener);
	}

	public SwtFXControlAdapter(T control) {
		setControl(control);
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

	public void dispose() {
		focusedProperty().removeListener(focusChangeListener);
	}

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
		SwtFXCanvas swtFXCanvas = getSwtFXCanvas(control);
		if (swtFXCanvas == null) {
			throw new IllegalArgumentException(
					"Control needs to be hooked to SwtFXCanvas before passing in here.");
		}
		registerEventForwarding(swtFXCanvas);
		registerFocusForwarding();
	}

	protected void registerEventForwarding(final SwtFXCanvas newCanvas) {
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

	protected void registerFocusForwarding() {
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

	public void setControl(T control) {
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
		unregisterEventForwarding();
		unregisterFocusForwarding();
	}

	protected void unregisterEventForwarding() {
		for (int eventType : FORWARD_EVENT_TYPES) {
			control.removeListener(eventType, swtForwardListener);
		}
		swtForwardListener = null;
	}

	protected void unregisterFocusForwarding() {
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