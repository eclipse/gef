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
package org.eclipse.gef4.fx.swt.canvas;

import java.lang.reflect.Method;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.fx.swt.gestures.SWT2FXEventConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

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

	private final class RedrawingEventDispatcher implements EventDispatcher {

		private static final int REDRAW_INTERVAL_MILLIS = 40; // i.e. 25 fps

		private EventDispatcher delegate;

		private long lastRedrawMillis = System.currentTimeMillis();

		protected RedrawingEventDispatcher(EventDispatcher delegate) {
			this.delegate = delegate;
		}

		@Override
		public Event dispatchEvent(final Event event,
				final EventDispatchChain tail) {
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

	private ChangeListener<Cursor> cursorChangeListener = new ChangeListener<Cursor>() {
		@Override
		public void changed(ObservableValue<? extends Cursor> observable,
				Cursor oldCursor, Cursor newCursor) {
			// XXX: SWTCursors does support image cursors yet
			// (https://bugs.openjdk.java.net/browse/JDK-8088147); we compensate
			// this here (using JDK-internal API)
			if (newCursor instanceof ImageCursor) {
				// custom cursor, convert image
				ImageData imageData = SWTFXUtils.fromFXImage(
						((ImageCursor) newCursor).getImage(), null);
				double hotspotX = ((ImageCursor) newCursor).getHotspotX();
				double hotspotY = ((ImageCursor) newCursor).getHotspotY();
				org.eclipse.swt.graphics.Cursor swtCursor = new org.eclipse.swt.graphics.Cursor(
						getDisplay(), imageData, (int) hotspotX,
						(int) hotspotY);
				// FIXME [JDK-internal]: Set platform cursor on CursorFrame so
				// that it can be retrieved by FXCanvas' HostContainer (which
				// ultimately sets the cursor on the FXCanvas); unfortunately,
				// this is not possible using public API.
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

		gestureConverter = new SWT2FXEventConverter(this);
	}

	@Override
	public void dispose() {
		// TODO: remove (logic has been put into dispose listener)
		super.dispose();
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
	public void setScene(Scene newScene) {
		Scene oldScene = getScene();
		if (oldScene != null) {
			// restore original event dispatcher
			EventDispatcher eventDispatcher = oldScene.getEventDispatcher();
			if (eventDispatcher instanceof RedrawingEventDispatcher) {
				oldScene.setEventDispatcher(
						((RedrawingEventDispatcher) eventDispatcher).dispose());
				oldScene.cursorProperty().removeListener(cursorChangeListener);
			}
		}
		super.setScene(newScene);
		if (newScene != null) {
			// wrap event dispatcher
			newScene.setEventDispatcher(new RedrawingEventDispatcher(
					newScene.getEventDispatcher()));
			newScene.cursorProperty().addListener(cursorChangeListener);
		}
	}
}
