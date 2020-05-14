/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - Support for focus listener notification
 *
 *******************************************************************************/
package org.eclipse.gef.fx.swt.canvas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.eclipse.gef.common.reflect.ReflectionUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;
import javafx.stage.Window;

/**
 * A replacement of {@link FXCanvas} that fixes the following issues:
 * <ul>
 * <li>JDK-8088147 - [SWT] FXCanvas: implement custom cursors [workaround for
 * JavaSE-1.8 only, as fixed by SWTCursors in JavaSE-1.9]</li>
 * <li>JDK-8161282 - FXCanvas does not forward horizontal mouse scroll events to
 * the embedded scene. [workaround for JavaSE-1.8 only, as fixed by FXCanvas in
 * JavaSE-1.9]</li>
 * <li>JDK-8143596 - FXCanvas does not forward touch gestures to embedded scene.
 * [workaround for JavaSE-1.8 only, as fixed by FXCanvas in JavaSE-1.9]</li>
 * <li>JDK-8159227 - FXCanvas should properly forward consumption state of key
 * events from SWT to embedded scene.</li>
 * <li>JDK-8161587 - FXCanvas does not consistently render the scene graph when
 * long running event handlers are used.</li>
 * <li>JDK-8088862 - Provide possibility to traverse focus out of FX scene.</li>
 * </ul>
 *
 * @author anyssen
 *
 */
public class FXCanvasEx extends FXCanvas {

	// FIXME: Use different EventDispatcher for different scenarios (Java 8 vs.
	// Java 9, and Windows vs. other platforms) for maximum performance.
	private final class EventDispatcherEx implements EventDispatcher {

		private static final int REDRAW_INTERVAL_MILLIS = 40; // i.e. 25 fps
		private EventDispatcher delegate;
		private long lastRedrawMillis = System.currentTimeMillis();
		private org.eclipse.swt.widgets.Event downEvent;

		protected EventDispatcherEx(EventDispatcher delegate) {
			this.delegate = delegate;
		}

		@Override
		public Event dispatchEvent(final Event event,
				final EventDispatchChain tail) {
			if (JAVA_8) {
				// XXX: Ensure key events that result from to be ignored SWT key
				// events (doit == false) are forwarded as consumed
				// (https://bugs.openjdk.java.net/browse/JDK-8159227)
				// TODO: Remove when dropping support for JavaSE-1.8.
				if (event instanceof javafx.scene.input.KeyEvent) {
					org.eclipse.swt.widgets.Event lastDownEvent = unprocessedKeyDownEvents
							.peek();
					if (event.getEventType()
							.equals(javafx.scene.input.KeyEvent.KEY_PRESSED)) {
						if (!lastDownEvent.doit) {
							event.consume();
						}
						// remove key down event and save it so that its doit
						// flag can be checked in case a KEY_TYPED event is
						// generated for it
						downEvent = unprocessedKeyDownEvents.poll();
						// System.out.println("pressed "
						// + ((javafx.scene.input.KeyEvent) event)
						// .getCode()
						// + " :: " + "down="
						// + unprocessedKeyDownEvents.size() + ", up="
						// + unprocessedKeyUpEvents.size());
					} else if (event.getEventType()
							.equals(javafx.scene.input.KeyEvent.KEY_TYPED)) {
						// consume event if last key down event was consumed
						if (!downEvent.doit) {
							event.consume();
						}
						// System.out.println("typed "
						// + ((javafx.scene.input.KeyEvent) event)
						// .getCharacter()
						// + " :: " + "down="
						// + unprocessedKeyDownEvents.size() + ", up="
						// + unprocessedKeyUpEvents.size());
					} else if (event.getEventType()
							.equals(javafx.scene.input.KeyEvent.KEY_RELEASED)) {
						// remove key up event
						org.eclipse.swt.widgets.Event lastUpEvent = unprocessedKeyUpEvents
								.poll();
						if (!lastUpEvent.doit) {
							event.consume();
						}
						// System.out.println("released "
						// + ((javafx.scene.input.KeyEvent) event)
						// .getCode()
						// + " :: " + "down="
						// + unprocessedKeyDownEvents.size() + ", up="
						// + unprocessedKeyUpEvents.size());
					}
				}
			}

			// dispatch the most recent event
			Event returnedEvent = delegate.dispatchEvent(event, tail);

			// update UI (added to fix
			// https://bugs.openjdk.java.net/browse/JDK-8161587)
			long millisNow = System.currentTimeMillis();
			if (millisNow - lastRedrawMillis > REDRAW_INTERVAL_MILLIS) {
				redraw();
				if (WIN32) {
					// XXX: Only call update() on some platforms to prevent a
					// loss of performance while keeping the UI up-to-date.
					update();
				}
				lastRedrawMillis = millisNow;
			}
			// return dispatched event
			return returnedEvent;
		}

		protected EventDispatcher dispose() {
			EventDispatcher d = delegate;
			delegate = null;
			return d;
		}
	}

	/**
	 * The {@link ISceneRunnable} interface provides a callback method that is
	 * invoked in a privileged runnable on the JavaFX application thread. The
	 * callback is provided with a {@link TKSceneListenerWrapper} that can be
	 * used to send events to JavaFX.
	 *
	 * @author mwienand
	 *
	 */
	private interface ISceneRunnable {
		/**
		 * Callback method that is called in a privileged runnable on the JavaFX
		 * application thread.
		 *
		 * @param sceneListener
		 *            The TKSceneListenerWrapper that can be used to send events
		 *            to JavaFX.
		 */
		public void run(TKSceneListenerWrapper sceneListener);
	}

	// XXX: This class is used to wrap a com.sun.javafx.tk.TKSceneListener
	// object, so respective methods can be called on it via reflection without
	// introducing compile-time dependencies.
	// TODO: Remove when dropping support for JavaSE-1.8
	private class TKSceneListenerWrapper {

		private Object tkSceneListener;

		private TKSceneListenerWrapper(Object tkSceneListener) {
			this.tkSceneListener = tkSceneListener;
		}

		public void rotateEvent(EventType<RotateEvent> eventType, double angle,
				double totalAngle, double x, double y, double screenX,
				double screenY, boolean _shiftDown, boolean _controlDown,
				boolean _altDown, boolean _metaDown, boolean _direct,
				boolean _inertia) {
			try {
				Method m = tkSceneListener.getClass().getDeclaredMethod(
						"rotateEvent", EventType.class, double.class,
						double.class, double.class, double.class, double.class,
						double.class, boolean.class, boolean.class,
						boolean.class, boolean.class, boolean.class,
						boolean.class);
				m.setAccessible(true);
				m.invoke(tkSceneListener, eventType, angle, totalAngle, x, y,
						screenX, screenY, _shiftDown, _controlDown, _altDown,
						_metaDown, _direct, _inertia);
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getCause();
				if (targetException instanceof RuntimeException) {
					throw ((RuntimeException) targetException);
				} else {
					targetException.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		public void scrollEvent(EventType<ScrollEvent> eventType,
				double scrollX, double scrollY, double totalScrollX,
				double totalScrollY, double xMultiplier, double yMultiplier,
				int touchCount, int scrollTextX, int scrollTextY,
				int defaultTextX, int defaultTextY, double x, double y,
				double screenX, double screenY, boolean _shiftDown,
				boolean _controlDown, boolean _altDown, boolean _metaDown,
				boolean _direct, boolean _inertia) {
			try {
				Method m = tkSceneListener.getClass().getDeclaredMethod(
						"scrollEvent", EventType.class, double.class,
						double.class, double.class, double.class, double.class,
						double.class, int.class, int.class, int.class,
						int.class, int.class, double.class, double.class,
						double.class, double.class, boolean.class,
						boolean.class, boolean.class, boolean.class,
						boolean.class, boolean.class);
				m.setAccessible(true);
				m.invoke(tkSceneListener, eventType, scrollX, scrollY,
						totalScrollX, totalScrollY, xMultiplier, yMultiplier,
						touchCount, scrollTextX, scrollTextY, defaultTextX,
						defaultTextY, x, y, screenX, screenY, _shiftDown,
						_controlDown, _altDown, _metaDown, _direct, _inertia);
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getCause();
				if (targetException instanceof RuntimeException) {
					throw ((RuntimeException) targetException);
				} else {
					targetException.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		public void swipeEvent(EventType<SwipeEvent> eventType, int touchCount,
				double x, double y, double screenX, double screenY,
				boolean _shiftDown, boolean _controlDown, boolean _altDown,
				boolean _metaDown, boolean _direct) {
			try {
				Method m = tkSceneListener.getClass().getDeclaredMethod(
						"swipeEvent", EventType.class, int.class, double.class,
						double.class, double.class, double.class, boolean.class,
						boolean.class, boolean.class, boolean.class,
						boolean.class);
				m.setAccessible(true);
				m.invoke(tkSceneListener, eventType, touchCount, x, y, screenX,
						screenY, _shiftDown, _controlDown, _altDown, _metaDown,
						_direct);
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getCause();
				if (targetException instanceof RuntimeException) {
					throw ((RuntimeException) targetException);
				} else {
					targetException.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		public void zoomEvent(EventType<ZoomEvent> eventType, double zoomFactor,
				double totalZoomFactor, double x, double y, double screenX,
				double screenY, boolean _shiftDown, boolean _controlDown,
				boolean _altDown, boolean _metaDown, boolean _direct,
				boolean _inertia) {
			try {
				Method m = tkSceneListener.getClass().getDeclaredMethod(
						"zoomEvent", EventType.class, double.class,
						double.class, double.class, double.class, double.class,
						double.class, boolean.class, boolean.class,
						boolean.class, boolean.class, boolean.class,
						boolean.class);
				m.setAccessible(true);
				m.invoke(tkSceneListener, eventType, zoomFactor,
						totalZoomFactor, x, y, screenX, screenY, _shiftDown,
						_controlDown, _altDown, _metaDown, _direct, _inertia);
			} catch (InvocationTargetException e) {
				Throwable targetException = e.getCause();
				if (targetException instanceof RuntimeException) {
					throw ((RuntimeException) targetException);
				} else {
					targetException.printStackTrace();
				}
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	private static final boolean JAVA_8 = System.getProperty("java.version")
			.startsWith("1.8.0");
	private static final boolean WIN32 = SWT.getPlatform().equals("win32");

	/**
	 * Returns the {@link FXCanvas} which contains the given {@link Scene}.
	 * Therefore, it is only valid to call this method for a {@link Scene} which
	 * is embedded into an SWT application via {@link FXCanvas}.
	 *
	 * @param scene
	 *            The {@link Scene} for which to determine the surrounding
	 *            {@link FXCanvas}.
	 * @return The {@link FXCanvas} which contains the given {@link Scene}.
	 */
	public static FXCanvas getFXCanvas(Scene scene) {
		if (scene == null) {
			return null;
		}
		if (JAVA_8) {
			// Obtain FXCanvas by accessing outer class
			// of FXCanvas$HostContainer
			// TODO: Remove this when dropping support for J2SE-1.8
			Window window = scene.getWindow();
			if (window != null) {
				return ReflectionUtils.getPrivateFieldValue(ReflectionUtils
						.<Object> getPrivateFieldValue(window, "host"),
						"this$0");
			}
			return null;
		} else {
			// On J2SE-1.9, retrieve FXCanvas through
			// FXCanvas.getFXCanvas(Scene), which was added for this
			// purpose.
			// TODO: Turn into an explicit call when dropping support
			// for J2SE-1.8.
			try {
				Method m = FXCanvas.class.getDeclaredMethod("getFXCanvas",
						Scene.class);
				return (FXCanvas) m.invoke(null, scene);

			} catch (Exception e) {
				throw new IllegalStateException(
						"Failed to call FXCanvas.getFXCanvas(Scene)", e);
			}
		}
	}

	// XXX: SWTCursors does not support image cursors up to JavaSE-1.9
	// (https://bugs.openjdk.java.net/browse/JDK-8088147); this listener
	// provides a workaround for J2SE-1.8. It relies on JDK internals and may
	// access these only via pure reflection to not introduce any compile-time
	// dependencies (that would not work in a JIGSAW context).
	// TODO: Remove when dropping support for JavaSE-1.8.
	private ChangeListener<Cursor> cursorChangeListener = new ChangeListener<Cursor>() {
		@Override
		public void changed(ObservableValue<? extends Cursor> observable,
				Cursor oldCursor, Cursor newCursor) {
			if (newCursor instanceof ImageCursor) {
				// custom cursor, convert image
				ImageData imageData = SWTFXUtils.fromFXImage(
						((ImageCursor) newCursor).getImage(), null);
				double hotspotX = ((ImageCursor) newCursor).getHotspotX();
				double hotspotY = ((ImageCursor) newCursor).getHotspotY();
				org.eclipse.swt.graphics.Cursor swtCursor = new org.eclipse.swt.graphics.Cursor(
						getDisplay(), imageData, (int) hotspotX,
						(int) hotspotY);
				try {
					Method currentCursorFrameAccessor = Cursor.class
							.getDeclaredMethod("getCurrentFrame",
									new Class[] {});
					currentCursorFrameAccessor.setAccessible(true);
					Object currentCursorFrame = currentCursorFrameAccessor
							.invoke(newCursor, new Object[] {});
					// there is a spelling-mistake in the internal API
					// (setPlatformCursor -> setPlatforCursor)
					Method platformCursorProvider = currentCursorFrame
							.getClass().getMethod("setPlatforCursor",
									new Class[] { Class.class, Object.class });
					platformCursorProvider.setAccessible(true);
					platformCursorProvider.invoke(currentCursorFrame,
							org.eclipse.swt.graphics.Cursor.class, swtCursor);
				} catch (Exception e) {
					System.err.println(
							"Failed to set platform cursor on the current cursor frame.");
					e.printStackTrace();
				}
			}
		}
	};
	private Listener mouseWheelListener = new Listener() {

		@Override
		public void handleEvent(org.eclipse.swt.widgets.Event e) {
			if (!gestureActive
					&& (!panGestureInertiaActive || lastGestureEvent == null
							|| e.time != lastGestureEvent.time)) {
				if (e.type == SWT.MouseVerticalWheel) {
					sendScrollEventToFX(ScrollEvent.SCROLL, 0,
							e.count > 0 ? 1 : -1, e.x, e.y, e.stateMask);
				} else {
					sendScrollEventToFX(ScrollEvent.SCROLL,
							e.count > 0 ? 1 : -1, 0, e.x, e.y, e.stateMask);
				}
			}
		}

		private void sendScrollEventToFX(final EventType<ScrollEvent> eventType,
				double scrollX, double scrollY, int x, int y, int stateMask) {
			// granularity for mouse wheel scroll events is more
			// coarse-grained than for pan gesture events
			final double multiplier = 40.0;
			final Point los = toDisplay(x, y);
			scheduleSceneRunnable(new ISceneRunnable() {
				@Override
				public void run(TKSceneListenerWrapper sceneListener) {
					sceneListener.scrollEvent(eventType, scrollX, scrollY,
							scrollX, scrollY, multiplier, multiplier, 0, 0, 0,
							0, 0, x, y, los.x, los.y,
							(stateMask & SWT.SHIFT) != 0,
							(stateMask & SWT.CONTROL) != 0,
							(stateMask & SWT.ALT) != 0,
							(stateMask & SWT.COMMAND) != 0, false, false);
				}
			});
		}
	};
	// including inertia events)
	private boolean gestureActive = false;
	// true while inertia events of a pan gesture might be processed
	private boolean panGestureInertiaActive = false;

	// the last gesture event that was received (may also be an inertia event)
	private GestureEvent lastGestureEvent;
	private GestureListener gestureListener = new GestureListener() {
		// used to keep track of which (atomic) gestures are enclosed
		private Stack<Integer> nestedGestures = new Stack<>();
		// data used to compute inertia values for pan gesture events (as SWT
		// does not provide these)
		private long inertiaTime = 0;
		private double inertiaXScroll = 0.0;
		private double inertiaYScroll = 0.0;

		// used to compute zoom deltas, which are not provided by SWT
		private double lastTotalZoom = 0.0;
		private double lastTotalAngle = 0.0;

		double totalScrollX = 0;
		double totalScrollY = 0;

		@Override
		public void gesture(GestureEvent gestureEvent) {
			// An SWT gesture may be compound, comprising several MAGNIFY, PAN,
			// and ROTATE events, which are enclosed by a
			// generic BEGIN and END event (while SWIPE events occur without
			// being enclosed).
			// In JavaFX, such a compound gesture is represented through
			// (possibly nested) atomic gestures, which all
			// (again excluding swipe) have their specific START and FINISH
			// events.
			// While a complex SWT gesture is active, we therefore have to
			// generate START events for atomic gestures as
			// needed, finishing them all when the compound SWT gesture ends (in
			// the reverse order they were started),
			// after which we still process inertia events (that only seem to
			// occur for PAN). SWIPE events may simply be
			// forwarded.
			switch (gestureEvent.detail) {
			case SWT.GESTURE_BEGIN:
				// a (complex) gesture has started
				gestureActive = true;
				// we are within an active gesture, so no inertia processing now
				panGestureInertiaActive = false;
				break;
			case SWT.GESTURE_MAGNIFY:
				// emulate the start of an atomic gesture
				if (gestureActive
						&& !nestedGestures.contains(SWT.GESTURE_MAGNIFY)) {
					sendZoomEventToFX(ZoomEvent.ZOOM_STARTED, gestureEvent);
					nestedGestures.push(SWT.GESTURE_MAGNIFY);
				}
				sendZoomEventToFX(ZoomEvent.ZOOM, gestureEvent);
				break;
			case SWT.GESTURE_PAN:
				// emulate the start of an atomic gesture
				if (gestureActive
						&& !nestedGestures.contains(SWT.GESTURE_PAN)) {
					sendScrollEventToFX(ScrollEvent.SCROLL_STARTED,
							gestureEvent.xDirection, gestureEvent.yDirection,
							gestureEvent.x, gestureEvent.y,
							gestureEvent.stateMask, false);
					nestedGestures.push(SWT.GESTURE_PAN);
				}

				// SWT does not flag inertia events and does not allow to
				// distinguish emulated PAN gesture events
				// (resulting from mouse wheel interaction) from native ones
				// (resulting from touch device interaction);
				// as it will always send both, mouse wheel as well as PAN
				// gesture events when using the touch device or
				// the mouse wheel, we can identify native PAN gesture inertia
				// events only based on their temporal relationship
				// to the preceding gesture event.
				if (panGestureInertiaActive
						&& gestureEvent.time > lastGestureEvent.time + 250) {
					panGestureInertiaActive = false;
				}

				if (gestureActive || panGestureInertiaActive) {
					double xDirection = gestureEvent.xDirection;
					double yDirection = gestureEvent.yDirection;

					if (panGestureInertiaActive) {
						// calculate inertia values for scrollX and scrollY, as
						// SWT (at least on MacOSX) provides zero values
						if (xDirection == 0 && yDirection == 0) {
							double delta = Math.max(0.0,
									Math.min(1.0,
											(gestureEvent.time - inertiaTime)
													/ 1500.0));
							xDirection = (1.0 - delta) * inertiaXScroll;
							yDirection = (1.0 - delta) * inertiaYScroll;
						}
					}

					sendScrollEventToFX(ScrollEvent.SCROLL, xDirection,
							yDirection, gestureEvent.x, gestureEvent.y,
							gestureEvent.stateMask, panGestureInertiaActive);
				}
				break;
			case SWT.GESTURE_ROTATE:
				// emulate the start of an atomic gesture
				if (gestureActive
						&& !nestedGestures.contains(SWT.GESTURE_ROTATE)) {
					sendRotateEventToFX(RotateEvent.ROTATION_STARTED,
							gestureEvent);
					nestedGestures.push(SWT.GESTURE_ROTATE);
				}
				sendRotateEventToFX(RotateEvent.ROTATE, gestureEvent);
				break;
			case SWT.GESTURE_SWIPE:
				EventType<SwipeEvent> type = null;
				if (gestureEvent.yDirection > 0) {
					type = SwipeEvent.SWIPE_DOWN;
				} else if (gestureEvent.yDirection < 0) {
					type = SwipeEvent.SWIPE_UP;
				} else if (gestureEvent.xDirection > 0) {
					type = SwipeEvent.SWIPE_RIGHT;
				} else if (gestureEvent.xDirection < 0) {
					type = SwipeEvent.SWIPE_LEFT;
				}
				sendSwipeEventToFX(type, gestureEvent);
				break;
			case SWT.GESTURE_END:
				// finish atomic gesture(s) in reverse order of their start;
				// SWIPE may be ignored,
				// as JavaFX (like SWT) does not recognize it as a gesture
				while (!nestedGestures.isEmpty()) {
					switch (nestedGestures.pop()) {
					case SWT.GESTURE_MAGNIFY:
						sendZoomEventToFX(ZoomEvent.ZOOM_FINISHED,
								gestureEvent);
						break;
					case SWT.GESTURE_PAN:
						sendScrollEventToFX(ScrollEvent.SCROLL_FINISHED,
								gestureEvent.xDirection,
								gestureEvent.yDirection, gestureEvent.x,
								gestureEvent.y, gestureEvent.stateMask, false);
						// use the scroll values of the preceding scroll event
						// to compute values for inertia events
						inertiaXScroll = lastGestureEvent.xDirection;
						inertiaYScroll = lastGestureEvent.yDirection;
						inertiaTime = gestureEvent.time;
						// from now on, inertia events may occur
						panGestureInertiaActive = true;
						break;
					case SWT.GESTURE_ROTATE:
						sendRotateEventToFX(RotateEvent.ROTATION_FINISHED,
								gestureEvent);
						break;
					}
				}
				// compound SWT gesture has ended
				gestureActive = false;
				break;
			default:
				throw new IllegalStateException(
						"Unsupported gesture event type: " + gestureEvent);
			}
			// keep track of currently received gesture event; this is needed to
			// identify inertia events
			lastGestureEvent = gestureEvent;
		}

		private void sendRotateEventToFX(EventType<RotateEvent> eventType,
				GestureEvent gestureEvent) {
			Point los = toDisplay(gestureEvent.x, gestureEvent.y);

			// XXX: SWT uses negative angle values to indicate clockwise
			// rotation, while JavaFX uses positive ones. We thus have to invert
			// the values here
			double[] totalAngle = { -gestureEvent.rotation };
			if (eventType == RotateEvent.ROTATION_STARTED) {
				totalAngle[0] = lastTotalAngle = 0.0;
			} else if (eventType == RotateEvent.ROTATION_FINISHED) {
				// SWT uses 0.0 for final event, while JavaFX still provides a
				// (total) rotation value
				totalAngle[0] = lastTotalAngle;
			}
			final double angle = eventType == RotateEvent.ROTATION_FINISHED
					? 0.0 : totalAngle[0] - lastTotalAngle;
			lastTotalAngle = totalAngle[0];

			scheduleSceneRunnable(new ISceneRunnable() {
				@Override
				public void run(TKSceneListenerWrapper sceneListener) {
					sceneListener.rotateEvent(eventType, angle, totalAngle[0],
							gestureEvent.x, gestureEvent.y, los.x, los.y,
							(gestureEvent.stateMask & SWT.SHIFT) != 0,
							(gestureEvent.stateMask & SWT.CONTROL) != 0,
							(gestureEvent.stateMask & SWT.ALT) != 0,
							(gestureEvent.stateMask & SWT.COMMAND) != 0, false,
							!gestureActive);
				}
			});
		}

		private void sendScrollEventToFX(EventType<ScrollEvent> eventType,
				double scrollX, double scrollY, int x, int y, int stateMask,
				boolean inertia) {
			// up to and including SWT 4.5, direction was inverted for pan
			// gestures on the Mac
			// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=481331)
			final double multiplier = ("cocoa".equals(SWT.getPlatform())
					&& SWT.getVersion() < 4600) ? -5.0 : 5.0;

			if (eventType == ScrollEvent.SCROLL_STARTED) {
				totalScrollX = 0;
				totalScrollY = 0;
			} else if (inertia) {
				// inertia events do not belong to the gesture,
				// thus total scroll is not accumulated
				totalScrollX = scrollX;
				totalScrollY = scrollY;
			} else {
				// accumulate total scroll as long as the gesture occurs
				totalScrollX += scrollX;
				totalScrollY += scrollY;
			}

			final Point los = toDisplay(x, y);
			scheduleSceneRunnable(new ISceneRunnable() {
				@Override
				public void run(TKSceneListenerWrapper sceneListener) {
					sceneListener.scrollEvent(eventType, scrollX, scrollY,
							totalScrollX, totalScrollY, multiplier, multiplier,
							0, 0, 0, 0, 0, x, y, los.x, los.y,
							(stateMask & SWT.SHIFT) != 0,
							(stateMask & SWT.CONTROL) != 0,
							(stateMask & SWT.ALT) != 0,
							(stateMask & SWT.COMMAND) != 0, false, inertia);
				}
			});
		}

		private void sendSwipeEventToFX(EventType<SwipeEvent> eventType,
				GestureEvent gestureEvent) {
			final Point los = toDisplay(gestureEvent.x, gestureEvent.y);

			scheduleSceneRunnable(new ISceneRunnable() {
				@Override
				public void run(TKSceneListenerWrapper sceneListener) {
					sceneListener.swipeEvent(eventType, 0, gestureEvent.x,
							gestureEvent.y, los.x, los.y,
							(gestureEvent.stateMask & SWT.SHIFT) != 0,
							(gestureEvent.stateMask & SWT.CONTROL) != 0,
							(gestureEvent.stateMask & SWT.ALT) != 0,
							(gestureEvent.stateMask & SWT.COMMAND) != 0, false);
				}
			});
		}

		private void sendZoomEventToFX(EventType<ZoomEvent> eventType,
				GestureEvent gestureEvent) {
			Point los = toDisplay(gestureEvent.x, gestureEvent.y);

			double[] totalZoom = new double[] { gestureEvent.magnification };
			if (eventType == ZoomEvent.ZOOM_STARTED) {
				// ensure first event does not provide any zoom yet
				totalZoom[0] = lastTotalZoom = 1.0;
			} else if (eventType == ZoomEvent.ZOOM_FINISHED) {
				// SWT uses 0.0 for final event, while JavaFX still provides a
				// (total) zoom value
				totalZoom[0] = lastTotalZoom;
			}
			final double zoom = eventType == ZoomEvent.ZOOM_FINISHED ? 1.0
					: totalZoom[0] / lastTotalZoom;
			lastTotalZoom = totalZoom[0];

			final boolean inertia = !gestureActive;
			scheduleSceneRunnable(new ISceneRunnable() {
				@Override
				public void run(TKSceneListenerWrapper sceneListener) {
					sceneListener.zoomEvent(eventType, zoom, totalZoom[0],
							gestureEvent.x, gestureEvent.y, los.x, los.y,
							(gestureEvent.stateMask & SWT.SHIFT) != 0,
							(gestureEvent.stateMask & SWT.CONTROL) != 0,
							(gestureEvent.stateMask & SWT.ALT) != 0,
							(gestureEvent.stateMask & SWT.COMMAND) != 0, false,
							inertia);
				}
			});
		}
	};
	private TraverseListener traverseListener = null;
	private DisposeListener disposeListener;
	// XXX: JavaFX does not forward the consumption state of key events to the
	// embedded scene (see https://bugs.openjdk.java.net/browse/JDK-8159227).
	// We use an SWT listener to capture all key events, so our JavaFX event
	// dispatcher replacement can identify the resulting JavaFX key events that
	// result from them and can mark them as consumed.
	// We have to ensure that the (typed) super key listener, which forwards
	// events to the embedded scene, is the last listener (in the list of all
	// key listeners, including untyped ones) that is notified. The manipulation
	// of the doit flag by listeners that got notified later, would otherwise
	// not be recognized.
	private Listener keyListener = new Listener() {
		@Override
		public void handleEvent(org.eclipse.swt.widgets.Event e) {
			if (allSwtKeyEvents.isEmpty()) {
				throw new IllegalStateException(
						"Handler called but filter did not record any events.");
			}

			// dispatch previous events
			while (!sameEvent(allSwtKeyEvents.peek(), e)) {
				org.eclipse.swt.widgets.Event previousEvent = allSwtKeyEvents
						.poll();
				// set doit to false to indicate that the event was already
				// processed
				previousEvent.doit = false;

				if (SWT.KeyDown == previousEvent.type) {
					unprocessedKeyDownEvents.add(previousEvent);
					for (Listener l : new ArrayList<>(keyDownListeners)) {
						l.handleEvent(previousEvent);
					}
					superKeyListener.keyPressed(new KeyEvent(previousEvent));
				} else if (SWT.KeyUp == previousEvent.type) {
					unprocessedKeyUpEvents.add(previousEvent);
					for (Listener l : new ArrayList<>(keyUpListeners)) {
						l.handleEvent(previousEvent);
					}
					superKeyListener.keyReleased(new KeyEvent(previousEvent));
				}
			}

			// remove e from allSwtKeyEvents
			allSwtKeyEvents.poll();

			// dispatch e
			if (SWT.KeyDown == e.type) {
				unprocessedKeyDownEvents.add(e);
				for (Listener l : new ArrayList<>(keyDownListeners)) {
					l.handleEvent(e);
				}
				superKeyListener.keyPressed(new KeyEvent(e));
			} else if (SWT.KeyUp == e.type) {
				unprocessedKeyUpEvents.add(e);
				for (Listener l : new ArrayList<>(keyUpListeners)) {
					l.handleEvent(e);
				}
				superKeyListener.keyReleased(new KeyEvent(e));
			}
		}

		private boolean sameEvent(org.eclipse.swt.widgets.Event e,
				org.eclipse.swt.widgets.Event f) {
			return e.display == f.display && e.widget == f.widget
					&& e.time == f.time && e.data == f.data
					&& e.character == f.character && e.keyCode == f.keyCode
					&& e.keyLocation == f.keyLocation
					&& e.stateMask == f.stateMask && e.doit == f.doit;
		};
	};
	private KeyListener superKeyListener;
	private List<Listener> keyUpListeners = new ArrayList<>();
	private List<Listener> keyDownListeners = new ArrayList<>();
	// keeps track of key events that need to be marked as consumed
	private Queue<org.eclipse.swt.widgets.Event> unprocessedKeyDownEvents = new LinkedList<>();

	private Queue<org.eclipse.swt.widgets.Event> unprocessedKeyUpEvents = new LinkedList<>();

	private Queue<org.eclipse.swt.widgets.Event> allSwtKeyEvents = new LinkedList<>();

	private Listener displayKeyFilter = new Listener() {
		private org.eclipse.swt.widgets.Event copy(
				org.eclipse.swt.widgets.Event event) {
			// create a new SWT Event
			org.eclipse.swt.widgets.Event copy = new org.eclipse.swt.widgets.Event();

			// transfer general attributes
			copy.display = event.display;
			copy.widget = event.widget;
			copy.time = event.time;
			copy.type = event.type;
			copy.doit = event.doit;
			copy.data = event.data;
			// transfer keyboard attributes
			copy.character = event.character;
			copy.keyCode = event.keyCode;
			copy.keyLocation = event.keyLocation;
			copy.stateMask = event.stateMask;

			// ignored attributes
			// copy.button = event.button;
			// copy.count = event.count;
			// copy.detail = event.detail;
			// copy.end = event.end;
			// copy.gc = event.gc;
			// copy.height = event.height;
			// copy.index = event.index;
			// copy.item = event.item;
			// copy.magnification = event.magnification;
			// copy.rotation = event.rotation;
			// copy.segments = event.segments;
			// copy.segmentsChars = event.segmentsChars;
			// copy.start = event.start;
			// copy.text = event.text;
			// copy.touches = event.touches;
			// copy.width = event.width;
			// copy.x = event.x;
			// copy.xDirection = event.xDirection;
			// copy.y = event.y;
			// copy.yDirection = event.yDirection;

			return copy;
		}

		@Override
		public void handleEvent(org.eclipse.swt.widgets.Event event) {
			// XXX: Only consider events which target this FXCanvasEx
			if (event.widget != FXCanvasEx.this) {
				return;
			}
			// XXX: Copy event so that the original event data is not
			// compromised (e.g. "type" and "doit").
			allSwtKeyEvents.add(copy(event));
		}
	};

	/**
	 * Creates a new {@link FXCanvasEx} for the given parent and with the given
	 * style.
	 *
	 * @param parent
	 *            The {@link Composite} to use as parent.
	 * @param style
	 *            A combination of SWT styles to be applied. Note that the
	 *            {@link FXCanvas} constructor will set the
	 *            {@link SWT#NO_BACKGROUND} style before passing it to the
	 *            {@link Canvas} constructor.
	 */
	public FXCanvasEx(Composite parent, int style) {
		super(parent, style);

		// extract original filters
		Object filterTable = ReflectionUtils.getPrivateFieldValue(getDisplay(),
				"filterTable");
		int[] types = ReflectionUtils.getPrivateFieldValue(filterTable,
				"types");
		Listener[] listeners = ReflectionUtils.getPrivateFieldValue(filterTable,
				"listeners");

		// clear filters
		ReflectionUtils.setPrivateFieldValue(getDisplay(), "filterTable", null);

		// hook our own filter
		getDisplay().addFilter(SWT.KeyDown, displayKeyFilter);
		getDisplay().addFilter(SWT.KeyUp, displayKeyFilter);

		// re-add original filters
		for (int i = 0; i < types.length && types[i] != 0; i++) {
			getDisplay().addFilter(types[i], listeners[i]);
		}

		// XXX: As FXCanvas uses a dispose listener, we have to use the same
		// mechanism here
		disposeListener = new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent de) {
				// XXX: unset the scene, so event dispatcher and cursor listener
				// are properly removed;
				// XXX: The super class will also unset the stage as a result of
				// unsetting the scene. The stagePeer will be unset through
				// the host container when the stage is set invisible (which
				// already happens through the dispose listener of the super
				// class). The embedded scene (scenePeer) will be unset through
				// the host container when unsetting the scene above;
				setScene(null);

				getDisplay().removeFilter(SWT.KeyDown, displayKeyFilter);
				getDisplay().removeFilter(SWT.KeyUp, displayKeyFilter);
				displayKeyFilter = null;

				cursorChangeListener = null;

				removeDisposeListener(disposeListener);
				disposeListener = null;

				removeTraverseListener(traverseListener);
				traverseListener = null;

				removeListener(SWT.KeyDown, keyListener);
				removeListener(SWT.KeyUp, keyListener);
				keyListener = null;
				superKeyListener = null;

				if (JAVA_8) {
					removeListener(SWT.MouseHorizontalWheel,
							mouseWheelListener);
					removeListener(SWT.MouseVerticalWheel, mouseWheelListener);
					removeGestureListener(gestureListener);
				}
				mouseWheelListener = null;
				gestureListener = null;
			}
		};
		addDisposeListener(disposeListener);

		// create traverse
		traverseListener = new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if ((e.detail == SWT.TRAVERSE_TAB_NEXT
						|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
						&& (e.stateMask & SWT.CTRL) != 0) {
					e.doit = true;
				}
			}
		};
		addTraverseListener(traverseListener);

		// XXX: Use a delegate to ensure the super key listener is the last one
		// that gets notified, which is required to properly forward the
		// consumption state to the embedded scene (see
		// https://bugs.openjdk.java.net/browse/JDK-8159227).
		addListener(SWT.KeyUp, keyListener);
		addListener(SWT.KeyDown, keyListener);

		if (JAVA_8) {
			addListener(SWT.MouseHorizontalWheel, mouseWheelListener);
			addListener(SWT.MouseVerticalWheel, mouseWheelListener);

			addGestureListener(gestureListener);
		}
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		// XXX: The workaround for JDK-8159227 requires that the typed key
		// listener, registered by the superclass, is ignored.
		if (listener.getClass().getName()
				.startsWith(FXCanvas.class.getName() + "$")) {
			superKeyListener = listener;
		} else {
			super.addKeyListener(listener);
		}
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		// XXX: Overwritten to ensure proper ordering of key listeners,
		// which is required to properly forward the consumption state to
		// the embedded scene (see
		// https://bugs.openjdk.java.net/browse/JDK-8159227).
		if (eventType == SWT.KeyUp) {
			if (listener == keyListener) {
				super.addListener(eventType, listener);
			} else {
				keyUpListeners.add(listener);
			}
		} else if (eventType == SWT.KeyDown) {
			if (listener == keyListener) {
				super.addListener(eventType, listener);
			} else {
				keyDownListeners.add(listener);
			}
		} else {
			super.addListener(eventType, listener);
		}
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		// XXX: The workaround for JDK-8161282 requires, that the typed mouse
		// wheel listener, registered by the JavaSE-1.8 superclass, is ignored;
		// the JavaSE-1.8 workaround and the fix within the JavaSE-1.9
		// superclass both use an untyped listener.
		if (JAVA_8) {
			if (!listener.getClass().getName()
					.startsWith(FXCanvas.class.getName() + "$")) {
				super.addMouseWheelListener(listener);
			}
		} else {
			super.addMouseWheelListener(listener);
		}
	}

	/**
	 * Returns the stage {@link Window} hold by this {@link FXCanvas}.
	 *
	 * @return The stage {@link Window}.
	 */
	public Window getStage() {
		return ReflectionUtils.getPrivateFieldValue(this, "stage");
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		// XXX: The workaround for JDK-8159227 requires that the typed key
		// listener, registered by the superclass, is ignored.
		if (listener.getClass().getName()
				.startsWith(FXCanvas.class.getName() + "$")) {
			superKeyListener = null;
		} else {
			super.removeKeyListener(listener);
		}
	}

	@Override
	public void removeListener(int eventType, Listener listener) {
		// XXX: Overwritten to ensure proper ordering of key listeners,
		// which is required to properly forward the consumption state to
		// the embedded scene (see
		// https://bugs.openjdk.java.net/browse/JDK-8159227).
		if (eventType == SWT.KeyUp) {
			if (listener == keyListener) {
				super.removeListener(eventType, listener);
			} else {
				keyUpListeners.remove(listener);
			}
		} else if (eventType == SWT.KeyDown) {
			if (listener == keyListener) {
				super.removeListener(eventType, listener);
			} else {
				keyDownListeners.remove(listener);
			}
		} else {
			super.removeListener(eventType, listener);
		}
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		// XXX: The workaround for JDK-8161282 requires, that the typed mouse
		// wheel listener, registered by the JavaSE-1.8 superclass, is ignored;
		// the JavaSE-1.8 workaround and the fix within the JavaSE-1.9
		// superclass both use an untyped listener.
		if (JAVA_8) {
			if (!listener.getClass().getName()
					.startsWith(FXCanvas.class.getName() + "$")) {
				super.removeMouseWheelListener(listener);
			}
		} else {
			super.removeMouseWheelListener(listener);
		}
	}

	/**
	 * Schedules the given {@link ISceneRunnable} for execution in a privileged
	 * runnable on the JavaFX application thread.
	 *
	 * @param sr
	 *            The {@link ISceneRunnable} that will be executed in a
	 *            privileged runnable on the JavaFX application thread.
	 */
	private void scheduleSceneRunnable(final ISceneRunnable sr) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Object scenePeer = ReflectionUtils
						.getPrivateFieldValue(FXCanvasEx.this, "scenePeer");
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
					@Override
					public Void run() {
						Object sceneListener = ReflectionUtils
								.getPrivateFieldValue(scenePeer,
										"sceneListener");
						if (sceneListener == null) {
							return null;
						}
						sr.run(new TKSceneListenerWrapper(sceneListener));
						return null;
					}
				}, (AccessControlContext) ReflectionUtils
						.getPrivateFieldValue(scenePeer, "accessCtrlCtx"));
			}
		});
	}

	@Override
	public void setScene(Scene newScene) {
		Scene oldScene = getScene();
		if (oldScene != null) {
			// restore original event dispatcher
			EventDispatcher eventDispatcher = oldScene.getEventDispatcher();
			if (eventDispatcher instanceof EventDispatcherEx) {
				oldScene.setEventDispatcher(
						((EventDispatcherEx) eventDispatcher).dispose());
				// TODO: add listener to property to keep track of changes to
				// event dispatcher that removes our delegate?? (throw an
				// exception?)
			}
			if (JAVA_8) {
				oldScene.cursorProperty().removeListener(cursorChangeListener);
			}
		}
		super.setScene(newScene);
		if (newScene != null) {
			// wrap event dispatcher
			newScene.setEventDispatcher(
					new EventDispatcherEx(newScene.getEventDispatcher()));
			if (JAVA_8) {
				newScene.cursorProperty().addListener(cursorChangeListener);
			}
		}
	}
}
