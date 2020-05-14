/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - major refactorings
 *******************************************************************************/
package org.eclipse.gef.fx.swt.controls;

import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Window;

/**
 * The FXControlAdapter can be used to embed SWT controls into a JavaFX scene
 * graph.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <T>
 *            The SWT Control class which is wrapped by this
 *            {@link FXControlAdapter}.
 */
public class FXControlAdapter<T extends Control> extends Region {

	/**
	 * The {@link FXControlAdapter.IControlFactory} can be used in conjunction
	 * with {@link FXControlAdapter} to create the wrapped SWT {@link Control}
	 * when the surrounding {@link FXCanvas} changes.
	 *
	 * @author anyssen
	 *
	 * @param <T>
	 *            The kind of {@link Control} to be created by this factory
	 */
	public interface IControlFactory<T extends Control> {

		/**
		 * Creates the {@link Control} as a child of the given {@link Composite}
		 * .
		 *
		 * @param parent
		 *            The {@link Composite} in which to create the
		 *            {@link Control}.
		 * @return The new {@link Control}.
		 */
		public T createControl(Composite parent);

	}

	private static final int[] FORWARD_SWT_EVENT_TYPES = new int[] {
			SWT.HardKeyDown, SWT.HardKeyUp, SWT.KeyDown, SWT.KeyUp, SWT.Gesture,
			SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
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

	/**
	 * Returns the first {@link FXCanvas} which is found by walking up the
	 * widget hierarchy of the given {@link Control}. If no {@link FXCanvas} can
	 * be found, <code>null</code> is returned.
	 *
	 * @param control
	 *            The {@link Control} for which to identify the surrounding
	 *            {@link FXCanvas}.
	 * @return The first {@link FXCanvas} which is found by walking up the
	 *         widget hierarchy of the given {@link Control}, or
	 *         <code>null</code>.
	 */
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

	/**
	 * Returns the {@link FXCanvas} which embeds the {@link Scene} which
	 * contains the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which the embedding {@link FXCanvas} is
	 *            determined.
	 * @return The {@link FXCanvas} which embeds the {@link Scene} which
	 *         contains the given {@link Node}.
	 */
	protected FXCanvas getFXCanvas(Node node) {
		if (node == null) {
			return null;
		}
		return FXCanvasEx.getFXCanvas(node.getScene());
	}

	/**
	 * Hooks the given {@link Control} into the JavaFX scene graph, for example,
	 * registering event forwarding from SWT to JavaFX.
	 *
	 * @see #registerSwtToFXEventForwarders(FXCanvas)
	 * @param control
	 *            The {@link Control} which is wrapped by this
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

	/**
	 * Initializes this {@link FXControlAdapter}. Per default, this
	 * {@link FXControlAdapter} is added to the focus traversal cycle and JavaFX
	 * listeners are registered for forwarding JavaFX state to SWT.
	 *
	 * @see #registerListeners()
	 */
	protected void init() {
		// by default, be part of focus traversal cycle
		focusTraversableProperty().set(true);

		// register listeners
		registerListeners();
	}

	/**
	 * Registers JavaFX listeners for forwarding JavaFX state to SWT. Among
	 * other things, this registers a listener for {@link Scene} changes which
	 * will then hook the SWT {@link Control} to the {@link FXCanvas} of the new
	 * {@link Scene}.
	 *
	 * @see #unregisterListeners()
	 * @see #setCanvas(FXCanvas)
	 */
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

				// if the scene changed, see if we can obtain an FXCanvasEx
				setCanvas(FXCanvasEx.getFXCanvas(newValue));

				// register/unregister listener to detect FXCanvasEx changes of
				// new scene
				if (oldValue != null) {
					oldValue.windowProperty()
							.removeListener(sceneWindowChangeListener);
					sceneWindowChangeListener = null;
				}
				if (newValue != null) {
					sceneWindowChangeListener = new ChangeListener<Window>() {
						@Override
						public void changed(
								ObservableValue<? extends Window> observable,
								Window oldValue, Window newValue) {
							setCanvas(
									newValue != null
											? FXCanvasEx.getFXCanvas(
													newValue.getScene())
											: null);
						}
					};
					newValue.windowProperty()
							.addListener(sceneWindowChangeListener);
				}
			}
		};
		sceneProperty().addListener(sceneChangeListener);
	}

	/**
	 * Registers SWT to JavaFX event forwarders for the given {@link FXCanvas}.
	 *
	 * @see #unregisterSwtToFXEventForwarders()
	 * @param newCanvas
	 *            The {@link FXCanvas} for which event forwarding is registered.
	 */
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

	/**
	 * Changes the {@link FXCanvas} in which the {@link Control} is hooked. An
	 * {@link IControlFactory} has to be available for re-creating the
	 * {@link Control} within the new {@link FXCanvas}, otherwise an exception
	 * is thrown.
	 *
	 * @see #setControl(Control)
	 * @param newCanvas
	 *            The new {@link FXCanvas} for the {@link Control}.
	 * @throws IllegalArgumentException
	 *             when the {@link FXCanvas} is changed, but no
	 *             {@link IControlFactory} is available.
	 */
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
			// canvas changes
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

	/**
	 * Sets the {@link Control} of this {@link FXControlAdapter} to the given
	 * value and {@link #hookControl(Control) hooks} or
	 * {@link #unhookControl(Control) unhooks} the {@link Control},
	 * respectively.
	 *
	 * @see #hookControl(Control)
	 * @see #unhookControl(Control)
	 * @param control
	 *            The new {@link Control} for this {@link FXControlAdapter}.
	 */
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
	 * Unhooks the given {@link Control} from the JavaFX scene graph, for
	 * example, unregistering event forwarding from SWT to JavaFX.
	 *
	 * @see #hookControl(Control)
	 * @see #unregisterSwtToFXEventForwarders()
	 * @param control
	 *            The {@link Control} which is wrapped by this
	 *            {@link FXControlAdapter}.
	 */
	protected void unhookControl(T control) {
		unregisterSwtToFXEventForwarders();
	}

	/**
	 * Unregisters the listeners which have previously been registered during
	 * {@link #registerListeners()}.
	 */
	protected void unregisterListeners() {
		sceneProperty().removeListener(sceneChangeListener);
		focusedProperty().removeListener(focusChangeListener);
	}

	/**
	 * Unregisters the event forwarders which have previously been registered
	 * during {@link #registerSwtToFXEventForwarders(FXCanvas)}.
	 */
	protected void unregisterSwtToFXEventForwarders() {
		for (int eventType : FORWARD_SWT_EVENT_TYPES) {
			control.removeListener(eventType, swtToFXEventForwardingListener);
		}
		swtToFXEventForwardingListener = null;
	}

	/**
	 * Updates the {@link Control#setBounds(int, int, int, int) bounds} of the
	 * {@link Control} which is wrapped by this {@link FXControlAdapter}. This
	 * method is automatically called when this {@link FXControlAdapter} is
	 * {@link #relocate(double, double) relocated} or
	 * {@link #resize(double, double) resized}.
	 */
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