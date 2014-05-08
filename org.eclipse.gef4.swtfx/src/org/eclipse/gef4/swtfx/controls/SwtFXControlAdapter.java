package org.eclipse.gef4.swtfx.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SwtFXControlAdapter<T extends Control> extends Region {

	private static final int[] FORWARD_SWT_EVENT_TYPES = new int[] {
			SWT.HardKeyDown, SWT.HardKeyUp, SWT.KeyDown, SWT.KeyUp,
			SWT.Gesture, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel, SWT.Move, SWT.Traverse,
			SWT.Verify, SWT.FocusIn };

	private SwtFXCanvas canvas;
	private T control;
	private Listener swtToFXEventForwardingListener;

	private ChangeListener<Scene> sceneChangeListener;
	private ChangeListener<SwtFXCanvas> sceneCanvasChangeListener;
	private ChangeListener<Boolean> focusChangeListener;

	private ISwtFXControlFactory<T> controlFactory;

	public SwtFXControlAdapter(ISwtFXControlFactory<T> controlFactory) {
		// lazy creation of control in case canvas is obtained
		this.controlFactory = controlFactory;
		init();
	}

	public SwtFXControlAdapter(T control) {
		// detect SwtFXCanvas via given control
		canvas = getSwtFXCanvas(control);
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

	public T getControl() {
		return control;
	}

	protected SwtFXCanvas getSwtFXCanvas(Control control) {
		Control candidate = control;
		while (candidate != null) {
			candidate = candidate.getParent();
			if (candidate instanceof SwtFXCanvas) {
				return (SwtFXCanvas) candidate;
			}
		}
		return null;
	}

	protected SwtFXCanvas getSwtFXCanvas(Node node) {
		if (node == null) {
			return null;
		}
		return getSwtFXCanvas(node.getScene());
	}

	protected SwtFXCanvas getSwtFXCanvas(Scene scene) {
		if (scene != null) {
			if (!(scene instanceof SwtFXScene)) {
				throw new IllegalArgumentException();
			}
			SwtFXCanvas fxCanvas = ((SwtFXScene) scene).getFXCanvas();
			return fxCanvas;
		}
		return null;
	}

	/**
	 * Used to register special listeners on the specific {@link Control}.
	 */
	protected void hookControl(T control) {
		SwtFXCanvas swtFXCanvas = getSwtFXCanvas(control);
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
				setCanvas(getSwtFXCanvas(newValue));

				// register/unregister listener to detect SwtFXCanvas changes of
				// new scene
				if (oldValue != null) {
					((SwtFXScene) oldValue).canvasProperty().removeListener(
							sceneCanvasChangeListener);
					sceneCanvasChangeListener = null;
				}
				if (newValue != null) {
					sceneCanvasChangeListener = new ChangeListener<SwtFXCanvas>() {
						@Override
						public void changed(
								ObservableValue<? extends SwtFXCanvas> observable,
								SwtFXCanvas oldValue, SwtFXCanvas newValue) {
							setCanvas(newValue);
						}
					};
					((SwtFXScene) newValue).canvasProperty().addListener(
							sceneCanvasChangeListener);
				}
			}
		};
		sceneProperty().addListener(sceneChangeListener);
	}

	protected void registerSwtToFXEventForwarders(final SwtFXCanvas newCanvas) {
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

	protected void setCanvas(SwtFXCanvas newCanvas) {
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
			SwtFXCanvas oldCanvas = this.canvas;
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