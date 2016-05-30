/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan Köhnlein (itemis AG) - initial API and implementation (#427106)
 *     Alexander Nyßen (itemis AG) - filter for emulated scroll and pan gesture events (#430940)
 *     Matthias Wienand (itemis AG) - forward horizontal mouse wheel events (#483742)
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.swt.gestures;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.sun.javafx.tk.TKSceneListener;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventType;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;

/**
 * The {@link SWT2FXEventConverter} forwards SWT events to JavaFX. It is
 * necessary because the {@link FXCanvas} does not forward gesture events and
 * horizontal mouse wheel events.
 *
 * @author Jan Koehnlein
 * @author anyssen
 * @author mwienand
 */
// FIXME [JDK-internal] TKSceneListener, which is used to compensate
// https://bugs.openjdk.java.net/browse/JDK-8143596, is not public API. We need
// to remove it as soon as FXCanvas supports touch events properly.
public class SWT2FXEventConverter {

	/**
	 * Represents the current {@link GestureState} of touch gesture interaction.
	 */
	protected class GestureState {
		/**
		 * The {@link GestureStateType} determines the currently performed touch
		 * gesture.
		 */
		GestureStateType type;

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
		 * Constructs a new {@link GestureState} of the given
		 * {@link GestureStateType}.
		 *
		 * @param type
		 *            The {@link #type} for this {@link GestureState}.
		 */
		public GestureState(GestureStateType type) {
			this.type = type;
		}
	}

	/**
	 * Determines the touch gesture which is currently performed.
	 */
	enum GestureStateType {
		/**
		 * Indicates that no touch gesture is performed.
		 */
		IDLE,

		/**
		 * Indicates that a "scrolling" touch gesture is performed.
		 */
		SCROLLING,

		/**
		 * Indicates that a "rotation" touch gesture is performed.
		 */
		ROTATING,

		/**
		 * Indicates that a "zooming" touch gesture is performed.
		 */
		ZOOMING;
	}

	/**
	 * The {@link ISceneRunnable} interface provides a callback method that is
	 * invoked in a privileged runnable on the JavaFX application thread. The
	 * callback is provided with a {@link TKSceneListener} that can be used to
	 * send events to JavaFX.
	 *
	 * @author mwienand
	 *
	 */
	protected interface ISceneRunnable {

		/**
		 * Callback method that is called in a privileged runnable on the JavaFX
		 * application thread.
		 *
		 * @param sceneListener
		 *            {@link TKSceneListener} that can be used to send events to
		 *            JavaFX.
		 */
		public void run(TKSceneListener sceneListener);

	}

	private FXCanvas canvas;
	private Display display;
	private GestureState currentGestureState;

	// filter for gesture events
	private GestureListener gestureListener = new GestureListener() {
		@Override
		public void gesture(GestureEvent event) {
			sendGestureEventToFx(event);
		}
	};

	// filter for mouse wheel events
	private Listener emulatedMouseWheelEventFilter = new Listener() {
		@Override
		public void handleEvent(final Event event) {
			if (event.widget == canvas) {
				if (display.getTouchEnabled() && lastPanGestureEvent != null
						&& lastPanGestureEvent.x == event.x
						&& lastPanGestureEvent.y == event.y) {
					// This mouse wheel event is synthesized from a pan
					// gesture event and thus has to be ignored.
					event.type = SWT.None;
				} else if (event.type == SWT.MouseHorizontalWheel) {
					// XXX: Horizontal mouse wheel events (not synthesized
					// from pan gesture events) are not forwarded by the
					// original FXCanvas while vertical events are.
					// send to JavaFX
					sendMouseHorizontalWheelEventToFx(event);
				}
			}
		}
	};

	// filter for pan gesture events emulated from mouse wheel interaction
	private Listener emulatedPanGestureEventFilter = new Listener() {
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

	// used to keep track of the last (valid) pan gesture event
	private Event lastPanGestureEvent;

	/**
	 * Registers event forwarding for the given {@link FXCanvas}.
	 *
	 * @param canvas
	 *            The {@link FXCanvas} for which event forwarding is registered.
	 */
	public SWT2FXEventConverter(final FXCanvas canvas) {
		this.canvas = canvas;
		this.currentGestureState = new GestureState(GestureStateType.IDLE);

		canvas.addGestureListener(gestureListener);

		display = canvas.getDisplay();

		// Fix for #430940: We need to register a filter for SWT.Gesture events
		// for three purposes:
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
		display.addFilter(SWT.Gesture, emulatedPanGestureEventFilter);

		// Fix for #430940: On touch devices, SWT seems to emulate mouse
		// wheel events from PAN gesture events. As we already transform
		// the original PAN gesture events into proper JavaFX scroll events
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
		//
		// Fix for #483742: Forward horizontal mouse wheel events to JavaFX.
		display.addFilter(SWT.MouseVerticalWheel,
				emulatedMouseWheelEventFilter);
		display.addFilter(SWT.MouseHorizontalWheel,
				emulatedMouseWheelEventFilter);
	}

	/**
	 * Changes the internal {@link GestureState} of the currently performed
	 * touch gesture and sends the appropriate events to JavaFX.
	 *
	 * @param newStateType
	 *            The new {@link GestureStateType}.
	 * @param event
	 *            The {@link GestureEvent} which was performed.
	 * @param sceneListener
	 *            The {@link TKSceneListener} to which the corresponding JavaFX
	 *            event is send.
	 * @return <code>true</code> when the {@link GestureStateType} is changed,
	 *         otherwise <code>false</code>.
	 */
	protected boolean changeState(GestureStateType newStateType,
			GestureEvent event, TKSceneListener sceneListener) {
		if (newStateType != currentGestureState.type) {
			switch (currentGestureState.type) {
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
			currentGestureState = new GestureState(newStateType);
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
	 * {@link SWT2FXEventConverter} was created.
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
		canvas.removeGestureListener(gestureListener);
		canvas = null;
	}

	private boolean isAlt(final Event event) {
		return (event.stateMask & SWT.ALT) != 0;
	}

	private boolean isAlt(final GestureEvent event) {
		return (event.stateMask & SWT.ALT) != 0;
	}

	private boolean isControl(final Event event) {
		return (event.stateMask & SWT.CONTROL) != 0;
	}

	private boolean isControl(final GestureEvent event) {
		return (event.stateMask & SWT.CONTROL) != 0;
	}

	private boolean isMeta(final Event event) {
		return (event.stateMask & SWT.COMMAND) != 0;
	}

	private boolean isMeta(final GestureEvent event) {
		return (event.stateMask & SWT.COMMAND) != 0;
	}

	private boolean isShift(final Event event) {
		return (event.stateMask & SWT.SHIFT) != 0;
	}

	private boolean isShift(final GestureEvent event) {
		return (event.stateMask & SWT.SHIFT) != 0;
	}

	/**
	 * Schedules the given {@link ISceneRunnable} for execution in a privileged
	 * runnable on the JavaFX application thread.
	 *
	 * @param sr
	 *            The {@link ISceneRunnable} that will be executed in a
	 *            privileged runnable on the JavaFX application thread.
	 */
	protected void scheduleSceneRunnable(final ISceneRunnable sr) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Object scenePeer = ReflectionUtils
						.getPrivateFieldValue(canvas, "scenePeer");
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
					@Override
					public Void run() {
						TKSceneListener sceneListener = ReflectionUtils
								.getPrivateFieldValue(scenePeer,
										"sceneListener");
						if (sceneListener == null) {
							return null;
						}
						sr.run(sceneListener);
						return null;
					}
				}, (AccessControlContext) ReflectionUtils
						.getPrivateFieldValue(scenePeer, "accessCtrlCtx"));
			}
		});
	}

	/**
	 * Converts the given {@link GestureEvent} to a corresponding JavaFX event
	 * and sends it to the JavaFX scene graph of the {@link FXCanvas} which is
	 * associated with this {@link SWT2FXEventConverter}.
	 *
	 * @param event
	 *            The {@link GestureEvent} to send to JavaFX.
	 */
	protected void sendGestureEventToFx(final GestureEvent event) {
		scheduleSceneRunnable(new ISceneRunnable() {
			@Override
			public void run(TKSceneListener sceneListener) {
				switch (event.detail) {
				case SWT.GESTURE_BEGIN:
					break;
				case SWT.GESTURE_END:
					changeState(GestureStateType.IDLE, event, sceneListener);
					break;
				case SWT.GESTURE_MAGNIFY:
					changeState(GestureStateType.ZOOMING, event, sceneListener);
					break;
				case SWT.GESTURE_PAN:
					changeState(GestureStateType.SCROLLING, event,
							sceneListener);
					break;
				case SWT.GESTURE_ROTATE:
					changeState(GestureStateType.ROTATING, event,
							sceneListener);
					break;
				case SWT.GESTURE_SWIPE:
					changeState(GestureStateType.IDLE, event, sceneListener);
				}
			}
		});
	}

	private void sendHorizontalScrollEvent(final Event event,
			TKSceneListener sceneListener) {
		// compute absolute screen coordinates
		final Point screenPosition = canvas.toDisplay(event.x, event.y);

		// generate scroll event: scrollX, scrollY, totalScrollX, totalScrollY,
		// xMultiplier, yMultiplier, touchCount, scrollTextX, scrollTextY,
		// defaultTextX, defaultTextY, x, y, screenX, screenY, modifiers (shift,
		// control, alt, meta), direct, inertia
		sceneListener.scrollEvent(ScrollEvent.SCROLL, event.count > 0 ? 8 : -8,
				0, 0, 0, 5.0, 5.0, 0, 0, 0, 0, 0, event.x, event.y,
				screenPosition.x, screenPosition.y, isShift(event),
				isControl(event), isAlt(event), isMeta(event), false, false);
	}

	/**
	 * Forwards a given {@link SWT#MouseHorizontalWheel} event to JavaFX.
	 *
	 * @param event
	 *            The {@link SWT#MouseHorizontalWheel} event that is forwarded
	 *            to JavaFX.
	 */
	protected void sendMouseHorizontalWheelEventToFx(final Event event) {
		scheduleSceneRunnable(new ISceneRunnable() {
			@Override
			public void run(TKSceneListener sceneListener) {
				sendHorizontalScrollEvent(event, sceneListener);
			}
		});
	}

	private void sendRotateEvent(EventType<RotateEvent> fxEventType,
			final GestureEvent event, TKSceneListener sceneListener) {
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		double rotation = (fxEventType == RotateEvent.ROTATION_FINISHED)
				? currentGestureState.lastRotation : -event.rotation;
		// System.out.println(fxEventType + " " + rotation);
		sceneListener.rotateEvent(fxEventType,
				rotation - currentGestureState.lastRotation, // rotation
				rotation, // totalRotation
				event.x, event.y, // x, y
				screenPosition.x, screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false, // direct
				false); // inertia
		currentGestureState.lastRotation = rotation;
	}

	private void sendScrollEvent(EventType<ScrollEvent> fxEventType,
			final GestureEvent event, TKSceneListener sceneListener) {
		currentGestureState.totalScrollX += event.xDirection;
		currentGestureState.totalScrollY += event.yDirection;
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		// System.out.println(fxEventType + " " + screenPosition);

		// XXX: Due to bug #481331, up to SWT 4.6 M4, the scroll directions were
		// inverted on the Mac. To achieve backwards compatibility when using
		// earlier SWT versions, we compensate this here if an earlier SWT
		// version is used.
		double multiplierCorrection = 1;
		if ("cocoa".equals(SWT.getPlatform()) && SWT.getVersion() < 4600) {
			multiplierCorrection = -1;
		}
		sceneListener.scrollEvent(fxEventType, event.xDirection,
				event.yDirection, // scrollX, scrollY
				0, 0, // totalScrollX, totalScrollY
				multiplierCorrection * 5.0, multiplierCorrection * 5.0, // xMultiplier,
																		// yMultiplier
				0, // touchCount
				0, 0, // scrollTextX, scrollTextY
				0, 0, // defaultTextX, defaultTextY
				event.x, event.y, // x, y
				screenPosition.x, screenPosition.y, // screenX, screenY
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
		sceneListener.swipeEvent(fxEventType, 0, // touch
				event.x, event.y, // x, y
				screenPosition.x, screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false); // direct
	}

	private void sendZoomEvent(EventType<ZoomEvent> fxEventType,
			final GestureEvent event, TKSceneListener sceneListener) {
		Point screenPosition = canvas.toDisplay(event.x, event.y);
		double magnification = (fxEventType == ZoomEvent.ZOOM_FINISHED)
				? currentGestureState.lastZoomFactor : event.magnification;
		// System.out.println(fxEventType + " " + magnification);
		sceneListener.zoomEvent(fxEventType,
				magnification / currentGestureState.lastZoomFactor, // zoom
																	// factor
				magnification, // totalZoomFactor
				event.x, event.y, // x, y
				screenPosition.x, screenPosition.y, // screenX, screenY
				isShift(event), isControl(event), isAlt(event), isMeta(event),
				false, // direct
				false); // inertia
		currentGestureState.lastZoomFactor = magnification;
	}

}