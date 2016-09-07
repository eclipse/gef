/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - Support for focus listener notification
 *     Jan Köhnlein (itemis AG) - Support for multi-touch gestures (#427106)
 *
 *******************************************************************************/
package org.eclipse.gef.fx.swt.canvas;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.gef.common.reflect.ReflectionUtils;
import org.eclipse.gef.fx.swt.gestures.SWT2FXEventConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * A replacement of {@link FXCanvas} that fixes the following issues:
 * <ul>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8143596 (gesture events not
 * forwarded) and horizontal mouse events not forwarded: fixed by forwarding of
 * missing SWT events to JavaFX through an {@link SWT2FXEventConverter}.</li>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8088147 (image cursors not
 * supported): fixed by adding support for image cursors.</li>
 * </ul>
 *
 * @author anyssen
 *
 */
public class FXCanvasEx extends FXCanvas {

	private final class EventDispatcherEx implements EventDispatcher {

		private static final int REDRAW_INTERVAL_MILLIS = 40; // i.e. 25 fps

		private EventDispatcher delegate;

		private long lastRedrawMillis = System.currentTimeMillis();

		protected EventDispatcherEx(EventDispatcher delegate) {
			this.delegate = delegate;
		}

		@Override
		public Event dispatchEvent(final Event event,
				final EventDispatchChain tail) {
			if (JAVA_8) {
				// XXX: Ensure key events that result from to be ingored SWT key
				// events (doit == false) are forwarded as consumed
				// (https://bugs.openjdk.java.net/browse/JDK-8159227)
				// TODO: Remove when dropping support for JavaSE-1.8.
				if (event instanceof javafx.scene.input.KeyEvent) {
					org.eclipse.swt.widgets.Event lastDownEvent = unprocessedKeyDownEvents
							.peek();
					if (event.getEventType()
							.equals(javafx.scene.input.KeyEvent.KEY_PRESSED)
							&& !lastDownEvent.doit) {
						event.consume();
					} else if (event.getEventType()
							.equals(javafx.scene.input.KeyEvent.KEY_TYPED)
							&& !lastDownEvent.doit) {
						event.consume();
					} else if (event.getEventType()
							.equals(javafx.scene.input.KeyEvent.KEY_RELEASED)) {
						unprocessedKeyDownEvents.poll();
						org.eclipse.swt.widgets.Event lastUpEvent = unprocessedKeyUpEvents
								.poll();
						if (!lastUpEvent.doit) {
							event.consume();
						}
					}
				}
			}

			// dispatch the most recent event
			Event returnedEvent = delegate.dispatchEvent(event, tail);
			// update UI
			long millisNow = System.currentTimeMillis();
			if (millisNow - lastRedrawMillis > REDRAW_INTERVAL_MILLIS) {
				redraw();
				update();
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

	private static final boolean JAVA_8 = System.getProperty("java.version")
			.startsWith("1.8.0");

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

	private SWT2FXEventConverter gestureConverter = null;
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
			if (e.type == SWT.KeyDown) {
				unprocessedKeyDownEvents.add(e);
				for (Listener l : new ArrayList<>(keyDownListeners)) {
					l.handleEvent(e);
				}
				superKeyListener.keyPressed(new KeyEvent(e));
			} else {
				unprocessedKeyUpEvents.add(e);
				for (Listener l : new ArrayList<>(keyUpListeners)) {
					l.handleEvent(e);
				}
				superKeyListener.keyReleased(new KeyEvent(e));
			}
		};
	};
	private KeyListener superKeyListener;
	private List<Listener> keyUpListeners = new ArrayList<>();
	private List<Listener> keyDownListeners = new ArrayList<>();
	// keeps track of key events that need to be marked as consumed
	private Queue<org.eclipse.swt.widgets.Event> unprocessedKeyDownEvents = new LinkedList<>();
	private Queue<org.eclipse.swt.widgets.Event> unprocessedKeyUpEvents = new LinkedList<>();

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
				cursorChangeListener = null;

				removeDisposeListener(disposeListener);
				disposeListener = null;

				removeTraverseListener(traverseListener);
				traverseListener = null;

				removeListener(SWT.KeyDown, keyListener);
				removeListener(SWT.KeyUp, keyListener);
				keyListener = null;
				superKeyListener = null;

				gestureConverter.dispose();
				gestureConverter = null;
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

		gestureConverter = new SWT2FXEventConverter(this);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		// XXX: Overwritten to ensure proper ordering of key listeners,
		// which is required to properly forward the consumption state to
		// the embedded scene (see
		// https://bugs.openjdk.java.net/browse/JDK-8159227).
		if (listener.getClass().getName()
				.startsWith(FXCanvas.class.getName() + "$")) {
			// XXX: Identifying the super key listener from the ordering is
			// not safe, as a subclass might tamper that ordering; we thus
			// use the class name to identify it
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

	/**
	 * Returns the stage {@link Window} hold by this {@link FXCanvas}.
	 *
	 * @return The stage {@link Window}.
	 */
	public Window getStage() {
		return ReflectionUtils.getPrivateFieldValue(this, "stage");
	}

	private void hookScene(Scene newScene) {
		if (JAVA_8) {
			newScene.cursorProperty().addListener(cursorChangeListener);
		}
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		// XXX: Overwritten to ensure proper ordering of key listeners,
		// which is required to properly forward the consumption state to
		// the embedded scene (see
		// https://bugs.openjdk.java.net/browse/JDK-8159227).
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
			unhookScene(oldScene);
		}
		super.setScene(newScene);
		if (newScene != null) {
			// wrap event dispatcher
			newScene.setEventDispatcher(
					new EventDispatcherEx(newScene.getEventDispatcher()));
			hookScene(newScene);
		}
	}

	private void unhookScene(Scene oldScene) {
		if (JAVA_8) {
			oldScene.cursorProperty().removeListener(cursorChangeListener);
		}
	}
}
