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

import java.lang.reflect.Method;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.fx.swt.gestures.SWT2FXEventConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
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

	private SWT2FXEventConverter gestureConverter;

	private ChangeListener<Cursor> cursorChangeListener = new ChangeListener<Cursor>() {
		@Override
		public void changed(ObservableValue<? extends Cursor> observable,
				Cursor oldCursor, Cursor newCursor) {
			// XXX: SWTCursors does support image cursors yet
			// (https://bugs.openjdk.java.net/browse/JDK-8088147); we compensate
			// this here
			if (newCursor instanceof ImageCursor) {
				// custom cursor, convert image
				ImageData imageData = SWTFXUtils.fromFXImage(
						((ImageCursor) newCursor).getImage(), null);
				double hotspotX = ((ImageCursor) newCursor).getHotspotX();
				double hotspotY = ((ImageCursor) newCursor).getHotspotY();
				org.eclipse.swt.graphics.Cursor swtCursor = new org.eclipse.swt.graphics.Cursor(
						getDisplay(), imageData, (int) hotspotX,
						(int) hotspotY);
				// XXX: Set platform cursor on CursorFrame so that it can be
				// retrieved by FXCanvas' HostContainer (which ultimately sets
				// the cursor on the FXCanvas); unfortunately, this is not
				// possible using public API
				try {
					Method currentCursorFrameAccessor = Cursor.class
							.getDeclaredMethod("getCurrentFrame",
									new Class[] {});
					currentCursorFrameAccessor.setAccessible(true);
					Object currentCursorFrame = currentCursorFrameAccessor
							.invoke(newCursor, new Object[] {});
					Method platformCursorProvider = currentCursorFrame
							.getClass().getMethod("setPlatforCursor",
									new Class[] { Class.class, Object.class });
					platformCursorProvider.setAccessible(true);
					platformCursorProvider.invoke(currentCursorFrame,
							org.eclipse.swt.graphics.Cursor.class, swtCursor);
				} catch (Exception e) {
					throw new IllegalStateException(
							"Failed to set platform cursor on the current cursor frame.",
							e);
				}
			}
		}
	};

	private TraverseListener eclipseUiTraversalListener = new TraverseListener() {
		@Override
		public void keyTraversed(TraverseEvent e) {
			if ((e.detail == SWT.TRAVERSE_TAB_NEXT
					|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
					&& (e.stateMask & SWT.CTRL) != 0) {
				e.doit = true;
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
		addTraverseListener(eclipseUiTraversalListener);
		gestureConverter = new SWT2FXEventConverter(this);
	}

	@Override
	public void dispose() {
		gestureConverter.dispose();
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
			oldScene.cursorProperty().removeListener(cursorChangeListener);
		}
		super.setScene(newScene);
		if (newScene != null) {
			newScene.cursorProperty().addListener(cursorChangeListener);
		}
	}
}
