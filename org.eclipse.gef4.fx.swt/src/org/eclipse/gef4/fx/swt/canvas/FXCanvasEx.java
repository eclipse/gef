/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.stage.Window;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.fx.swt.gestures.SwtToFXGestureConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A replacement of {@link FXCanvas} that offers the following additional
 * capabilities:
 * <ul>
 * <li>Forwarding of SWT {@link GestureEvent}s to JavaFX (the original FXCanvas
 * simply ignores all those events)</li>
 * <li>Support for notifying SWT {@link FocusListener}s (the original FXCanvas
 * will forward all focus events to the embedded JavaFX stage), so SWT
 * {@link FocusListener} will not be notified</li>
 * <li>Support for setting cursors via JavaFX (i.e. the cursor of the embedded
 * JavaFX stage, its transferred into an SWT cursor on this {@link FXCanvasEx})
 * </li>
 * </ul>
 *
 * @author anyssen
 *
 */
public class FXCanvasEx extends FXCanvas {

	private static Map<Cursor, Integer> CURSOR_FROM_FX_TO_SWT = new HashMap<Cursor, Integer>() {
		private static final long serialVersionUID = 1L;

		{
			// TODO: verify cursors marked with XXX
			put(Cursor.CLOSED_HAND, SWT.CURSOR_HAND); // XXX
			put(Cursor.CROSSHAIR, SWT.CURSOR_CROSS);
			put(Cursor.DISAPPEAR, SWT.CURSOR_NO); // XXX
			put(Cursor.E_RESIZE, SWT.CURSOR_SIZEE);
			put(Cursor.H_RESIZE, SWT.CURSOR_SIZEWE);
			put(Cursor.HAND, SWT.CURSOR_HAND);
			put(Cursor.MOVE, SWT.CURSOR_CROSS); // XXX
			put(Cursor.N_RESIZE, SWT.CURSOR_SIZEN);
			put(Cursor.NE_RESIZE, SWT.CURSOR_SIZENE);
			put(Cursor.NONE, SWT.CURSOR_NO);
			put(Cursor.NW_RESIZE, SWT.CURSOR_SIZENW);
			put(Cursor.OPEN_HAND, SWT.CURSOR_HAND);// XXX
			put(Cursor.S_RESIZE, SWT.CURSOR_SIZES);
			put(Cursor.SE_RESIZE, SWT.CURSOR_SIZESE);
			put(Cursor.SW_RESIZE, SWT.CURSOR_SIZESW);
			put(Cursor.TEXT, SWT.CURSOR_IBEAM);
			put(Cursor.V_RESIZE, SWT.CURSOR_SIZENS);
			put(Cursor.W_RESIZE, SWT.CURSOR_SIZEW);
			put(Cursor.WAIT, SWT.CURSOR_WAIT);
		}
	};

	private SwtToFXGestureConverter gestureConverter;
	private List<FocusListener> focusListeners;
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
				getShell().setCursor(swtCursor);
			} else if (CURSOR_FROM_FX_TO_SWT.containsKey(newCursor)) {
				// standard cursor, look up in map
				getShell().setCursor(new org.eclipse.swt.graphics.Cursor(
						getDisplay(), CURSOR_FROM_FX_TO_SWT.get(newCursor)));
			} else {
				// unknown cursor, use default
				getShell().setCursor(null);
			}
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
		gestureConverter = new SwtToFXGestureConverter(this);

		// add a focus listener to propagate focus events also to FocusListeners
		// registered on this canvas. FXCanvas will not notify them, as all
		// focus events are only forwarded to the embedded stage.
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				for (FocusListener l : focusListeners) {
					l.focusGained(e);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				for (FocusListener l : focusListeners) {
					l.focusLost(e);
				}
			}
		});
		focusListeners = new ArrayList<FocusListener>();
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		if (focusListeners == null) {
			// if we have not created our local field yet, we are within
			// initialization, where focus listeners have to be registered via
			// the super implementation.
			super.addFocusListener(listener);
		} else {
			focusListeners.add(listener);
		}
	}

	@Override
	public void dispose() {
		gestureConverter.dispose();
		focusListeners.clear();
		focusListeners = null;
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
	public void removeFocusListener(FocusListener listener) {
		if (focusListeners == null) {
			super.removeFocusListener(listener);
		} else {
			focusListeners.remove(listener);
		}
	}

	@Override
	public void setCursor(org.eclipse.swt.graphics.Cursor cursor) {
		// Do not overwrite the JavaFX cursor.
	}

	@Override
	public void setScene(Scene newScene) {
		Scene oldScene = getScene();
		if (oldScene != null) {
			oldScene.cursorProperty().removeListener(cursorChangeListener);
		}
		super.setScene(newScene);
		if (newScene != null) {
			newScene.cursorProperty().addListener(cursorChangeListener);
		}
	}

}
