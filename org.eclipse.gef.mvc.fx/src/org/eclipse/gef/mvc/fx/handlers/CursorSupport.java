/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Cursor;
import javafx.scene.Scene;

/**
 * The {@link CursorSupport} provides methods for changing and restoring the
 * mouse cursor. It is designed to be used from within an {@link IHandler}
 * implementation.
 *
 * @author mwienand
 *
 */
public class CursorSupport extends IAdaptable.Bound.Impl<IViewer> {

	/**
	 * The original mouse {@link Cursor}.
	 */
	private Cursor originalCursor;

	/**
	 * Flag to indicate if the mouse cursor was changed. This is necessary to
	 * differentiate between no cursor change and an original null cursor.
	 */
	private boolean isCursorChanged = false;

	/**
	 * Creates a new {@link CursorSupport} that can be used to change and keep
	 * track of the mouse cursor.
	 */
	public CursorSupport() {
	}

	/**
	 * Returns the original mouse {@link Cursor} that is stored by this policy.
	 *
	 * @return The original mouse {@link Cursor} that is stored by this policy.
	 */
	public Cursor getOriginalCursor() {
		return originalCursor;
	}

	/**
	 * Returns <code>true</code> if the mouse cursor was changed by this
	 * support. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the mouse cursor was changed by this
	 *         support, Otherwise <code>false</code>.
	 */
	public boolean isCursorChanged() {
		return isCursorChanged;
	}

	/**
	 * Restores the mouse {@link Cursor} that was replaced by a previous
	 * {@link #storeAndReplaceCursor(Cursor)} call. If the mouse {@link Cursor}
	 * has never been replaced, it is not changed.
	 *
	 * @see #storeAndReplaceCursor(Cursor)
	 */
	public void restoreCursor() {
		if (isCursorChanged) {
			setCursor(originalCursor);
			isCursorChanged = false;
		}
	}

	/**
	 * Sets the given {@link Cursor} as the mouse cursor for the {@link Scene}
	 * of the host visual. Note that this method does not store the original
	 * mouse cursor.
	 *
	 * @param cursor
	 *            The new mouse {@link Cursor}.
	 * @see #storeAndReplaceCursor(Cursor)
	 * @see #restoreCursor()
	 */
	public void setCursor(Cursor cursor) {
		Scene scene = getAdaptable().getRootPart().getVisual().getScene();
		if (cursor != scene.getCursor()) {
			scene.setCursor(cursor);
		}
	}

	/**
	 * Changes the mouse {@link Cursor} to the given value if necessary. If this
	 * method is called for the first time (in general or for the first time
	 * after a call to {@link #restoreCursor()}) the original cursor is stored
	 * so that it can later be restored.
	 *
	 * @param cursor
	 *            The new mouse {@link Cursor}.
	 * @see #restoreCursor()
	 */
	public void storeAndReplaceCursor(Cursor cursor) {
		if (!isCursorChanged) {
			originalCursor = getAdaptable().getRootPart().getVisual().getScene()
					.getCursor();
			isCursorChanged = true;
		}
		setCursor(cursor);
	}
}
