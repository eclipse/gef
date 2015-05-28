/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan Köhnlein (itemis AG) - initial API and implementation (#427106)
 *     Alexander Nyßen (itemis AG) - filter for emulated scroll and pan gesture events (#430940)
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.ui.gestures;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventType;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.sun.javafx.tk.TKSceneListener;

/**
 * A gesture listener that converts and transfers SWT {@link GestureEvent}s to
 * an {@link FXCanvas}.
 *
 * @author Jan Koehnlein
 * @author anyssen
 */
public class SwtToFXGestureConverter implements GestureListener {

	/**
	 * Stores the state of touchpad events.
	 */
	protected class State {
		/**
		 * The {@link StateType} determines the currently performed touchpad
		 * gesture.
		 */
		StateType type;

		/**
		 * The total horizontal scroll distance, initially set to <code>0</code>
		 * .
		 */
		double totalScrollX = 0;

		/**
		 * The total vertical scroll distance, initially set to <code>0</code>.
		 */
		double totalScrollY = 0;

		/**
		 * The most recent (accumulated) zoom factor, initially set to
		 * <code>1</code>.
		 */
		double lastZoomFactor = 1;

		/**
		 * The most recent (accumulated) rotation (in degrees), initially set to
		 * <code>0</code>.
		 */
		double lastRotation = 0;

		/**
		 * Constructs a new {@link State} of the given {@link StateType}.
		 *
		 * @param type
		 *            The {@link #type} for this {@link State}.
		 */
		public State(StateType type) {
			this.type = type;
		}
	}

	/**
	 * Determines the touchpad gesture which is currently performed.
	 */
	enum StateType {
		/**
		 * Indicates that no touchpad gesture is performed.
		 */
		IDLE,

		/**
		 * Indicates that a "scrolling" touchpad gesture is performed.
		 */
		SCROLLING,

		/**
		 * Indicates that a "rotation" touchpad gesture is performed.
		 */
		ROTATING,

		/**
		 * Indicates that a "zooming" touchpad gesture is performed.
		 */
		ZOOMING;
	}

	private FXCanvas canvas;

	private State currentState;

	// filter for mouse wheel event emulated from pan gesture interaction
	private Listener emulatedMouseWheelEventFilter;
	// filter for pan gesture events emulated from mouse wheel interaction
	private Listener emulatedPanGestureEventFilter;

	// used to keep track of the last (valid) pan gesture event
	private Event lastPanGestureEvent;

	/**
	 * Registers event forwarding for the given {@link FXCanvas}.
	 *
	 * @param canvas
	 *            The {@link FXCanvas} for which event forwarding is registered.
	 */
	public SwtToFXGestureConverter(final FXCanvas canvas) {
		this.canvas = canvas;
		this.currentState = new State(StateType.IDLE);

		canvas.addGestureListener(this);

		Display display = canvas.getDisplay();

		// Fix for #430940: We need to register a filter for SWT.Gesture events
		// for two purposes:
		//
		// 1) Sort out pan gesture events if they are only emulated from mouse
		// wheel events (in which case - at least on the Mac - they do not seem
		// to specify an xDirection and yDirection), as these are not
		// transformed into valid JavaFX scroll events (x-delta and y-delta will
		// always be 0). These will occur if the user actually uses the mouse
		// wheel for interaction, in which case a proper JavaFX scroll event
		// will already be created from the MouseWheel event.
		// TODO: Check other platforms than Mac
		//
		// 2) Keep track of the last (valid) pan gesture event to be able to
		// sort out mouse wheel events that are emulated from gesture events
		// (by means of a MouseWheelListener)
		emulatedPanGestureEventFilter = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.widget == canvas) {
					if (event.detail == SWT.GESTURE_PAN) {
						if (event.xDirection == 0 && event.yDirection == 0) {
							event.type = SWT.None;
						} else {
							lastPanGestureEvent = event;
						}
					}
				}
			}
		};
		display.addFilter(SWT.Gesture, emulatedPanGestureEventFilter);

		if (display.getTouchEnabled()) {
			// Fix for #430940: On touch devices, SWT seems to emulate mouse
			// wheel events from PAN gesture events. As we already transform the
			// original PAN gesture events into proper JavaFX scroll events
			// (with a step-width of 5), the emulated mouse wheel events, which
			// would also transferred into JavaFX scroll events by FXCanvas
			// (with a step-width of 40), only disturb, which is why we want to
			// filter them out here.
			//
			// As these emulated mouse wheel events cannot be differentiated
			// from valid mouse wheel events based on their field values, use
			// the approach to keep track of the last (valid) pan gesture event
			// and sort out those mouse wheel events that use the same x and y
			// position.
			emulatedMouseWheelEventFilter = new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (event.widget == canvas) {
						if (lastPanGestureEvent != null
								&& lastPanGestureEvent.x == event.x
								&& lastPanGestureEvent.y == event.y) {
							event.type = SWT.None;
						}
					}
				}
			};
			display.addFilter(SWT.MouseVerticalWheel,
					emulatedMouseWheelEventFilter);
			display.addFilter(SWT.MouseHorizontalWheel,
					emulatedMouseWheelEventFilter);
		}
	}

	/**
	 * Changes the internal {@link State} of the currently performed touchpad
	 * gesture and sends the appropriate events to JavaFX.
	 *
	 * @param newStateType
	 *            The new {@link StateType}.
	 * @param event
	 *            The {@link GestureEvent} which was performed.
	 * @param sceneListener
	 *            The {@link TKSceneListener} to which the corresponding JavaFX
	 *            event is send.
	 * @return <code>true</code> when the {@link StateType} is changed,
	 *         otherwise <code>false</code>.
	 */
	protected boolean changeState(StateType newStateType, GestureEvent event,
			TKSceneListener sceneListener) {
		if (newStateType != currentState.type) {
			switch (currentState.type) {
			case SCROLLING:
				sendScrollEvent(ScrollEvent.SCROLL_FINISHED, event,
						sceneListener);
				break;
			case ROTATING:
				sendRotateEvent(RotateEvent.ROTATION_FINISHED, event,
						sceneListener);
				break;
			case ZOOMING:
				sendZoomEvent(ZoomEvent.ZOOM_FINISHED, event, sceneListener);
				break;
			default:
				// do nothing
			}
			switch (newStateType) {
			case SCROLLING:
				sendScrollEvent(ScrollEvent.SCROLL_STARTED, event,
						sceneListener);
				break;
			case ROTATING:
				sendRotateEvent(RotateEvent.ROTATION_STARTED, event,
						sceneListener);
				break;
			case ZOOMING:
				sendZoomEvent(ZoomEvent.ZOOM_STARTED, event, sceneListener);
				break;
			case IDLE:
				if (event.detail == SWT.GESTURE_SWIPE) {
					sendSwipeEvent(event, sceneListener);
				}
				break;
			default:
				// do nothing
			}
			currentState = new State(newStateType);
			return true;
		}
		switch (newStateType) {
		case SCROLLING:
			sendScrollEvent(ScrollEvent.SCROLL, event, sceneListener);
			break;
		case ROTATING:
			sendRotateEvent(RotateEvent.ROTATE, event, sceneListener);
			break;
		case ZOOMING:
			sendZoomEvent(ZoomEvent.ZOOM, event, sceneListener);
			break;
		case IDLE:
			if (event.detail == SWT.GESTURE_SWIPE) {
				sendSwipeEvent(event, sceneListener);
			}
		default:
			// do nothing
		}
		return false;
	}

	/**
	 * Unregisters event forwarding from the {@link FXCanvas} for which this
	 * {@link SwtToFXGestureConverter} was created.
	 */
	public void dispose() {
		Display display = canvas.getDisplay();
		if (emulatedPanGestureEventFilter != null) {
			display.removeFilter(SWT.Gesture, emulatedPanGestureEventFilter);
		}
		if (emulatedMouseWheelEventFilter != null) {
			display.removeFilter(SWT.MouseVerticalWheel,
					emulatedMouseWheelEventFilter);
			display.removeFilter(SWT.MouseHorizontalWheel,
					emulatedMouseWheelEventFilter);
		}
		canvas.removeGestureListener(this);
		canvas = null;
	}

	@Override
	public void gesture(GestureEvent event) {
		sendGestureEventToFX(event);
	}

	private boolean isAlt(final GestureEvent event) {
		return (event.stateMask & SWT.ALT) != 0;
	}

	private boolean isControl(final GestureEvent event) {
		return (event.stateMask & SWT.CONTROL) != 0;
	}

	private boolean isMeta(final GestureEvent event) {
		return (event.stateMask & SWT.COMMAND) != 0;
	}

	private boolean isShift(final GestureEvent event) {
		return (event.stateMask & SWT.SHIFT) != 0;
	}

	/**
	 * Converts the given {@link GestureEvent} to a corresponding JavaFX event
	 * and sends it to the JavaFX scene graph of the {@link FXCanvas} which is
	 * associated with this {@link SwtToFXGestureConverter}.
	 *
	 * @param event
	 *            The {@link GestureEvent} to send to JavaFX.
	 */
	protected void sendGestureEventToFX(final GestureEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Object scenePeer = ReflectionUtils.getPrivateFieldValue(
						canvas, "scenePeer");
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
					@Override
					public Void run() {
						TKSceneListener sceneListener = ReflectionUtils
								.getPrivateFieldValue(scenePeer,
										"sceneListener");
						if (sceneListener == null) {
							return null;
						}
						switch (event.detail) {
						case SWT.GESTURE_BEGIN:
							break;
						case SWT.GESTURE_END:
							changeState(StateType.IDLE, event, sceneListener);
							break;
						case SWT.GESTURE_MAGNIFY:
							changeState(StateType.ZOOMING, event, sceneListener);
							break;
						case SWT.GESTURE_PAN:
							changeState(StateType.SCROLLING, event,
									sceneListener);
							break;
						case SWT.GESTURE_ROTATE:
							changeState(StateType.ROTATING, event,
									sceneListener);
							break;
						case SWT.GESTURE_SWIPE:
							changeState(StateType.IDLE, event, sceneListener);
						}
						return null;
					}

				}, (AccessControlContext) ReflectionUtils.getPrivateFieldValue(
						scenePeer, "accessCtrlCtx"));
			}
		});
	}

	private void sendRotateEvent(EventType<RotateEvent> fxEventType,
			final GestureEvent event, TKSceneListener sceneListener) {
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		double rotation = (fxEventType == RotateEvent.ROTATION_FINISHED) ? currentState.lastRotation
				: -event.rotation;
		// System.out.println(fxEventType + " " + rotation);
		sceneListener.rotateEvent(fxEventType,
				rotation - currentState.lastRotation, // rotation
				rotation, // totalRotation
				event.x,
				event.y, // x, y
				screenPosition.x,
				screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false, // direct
				false); // inertia
		currentState.lastRotation = rotation;
	}

	private void sendScrollEvent(EventType<ScrollEvent> fxEventType,
			final GestureEvent event, TKSceneListener sceneListener) {
		currentState.totalScrollX += event.xDirection;
		currentState.totalScrollY += event.yDirection;
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		// System.out.println(fxEventType + " " + screenPosition);

		// only forward scroll events that are not also emulated
		sceneListener.scrollEvent(fxEventType,
				event.xDirection,
				event.yDirection, // scrollX, scrollY
				0,
				0, // totalScrollX, totalScrollY
				-5.0,
				-5.0, // xMultiplier, yMultiplier
				0, // touchCount
				0,
				0, // scrollTextX, scrollTextY
				0,
				0, // defaultTextX, defaultTextY
				event.x,
				event.y, // x, y
				screenPosition.x,
				screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false, // direct
				false); // inertia
	}

	private void sendSwipeEvent(final GestureEvent event,
			TKSceneListener sceneListener) {
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		EventType<SwipeEvent> fxEventType = null;
		if (event.yDirection > 0) {
			fxEventType = SwipeEvent.SWIPE_DOWN;
		} else if (event.yDirection < 0) {
			fxEventType = SwipeEvent.SWIPE_UP;
		} else if (event.xDirection > 0) {
			fxEventType = SwipeEvent.SWIPE_RIGHT;
		} else if (event.xDirection < 0) {
			fxEventType = SwipeEvent.SWIPE_LEFT;
		}
		// System.out.println(fxEventType.toString());
		sceneListener.swipeEvent(fxEventType,
				0, // touch
				event.x,
				event.y, // x, y
				screenPosition.x,
				screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false); // direct
	}

	private void sendZoomEvent(EventType<ZoomEvent> fxEventType,
			final GestureEvent event, TKSceneListener sceneListener) {
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		double magnification = (fxEventType == ZoomEvent.ZOOM_FINISHED) ? currentState.lastZoomFactor
				: event.magnification;
		// System.out.println(fxEventType + " " + magnification);
		sceneListener.zoomEvent(fxEventType,
				magnification / currentState.lastZoomFactor, // zoom factor
				magnification, // totalZoomFactor
				event.x,
				event.y, // x, y
				screenPosition.x,
				screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false, // direct
				false); // inertia
		currentState.lastZoomFactor = magnification;
	}

}