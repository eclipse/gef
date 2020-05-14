/*******************************************************************************
 * Copyright (c) 2018, 2019 KDM Analytics Inc. and others
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Kyle Girard (KDM Analytics Inc.) - initial API and implementation
 *     Matthias Wienand (itemis AG)     - Javadoc adjustments
 *
 *******************************************************************************/

package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.mvc.fx.gestures.DragDropGesture;

import javafx.scene.input.DragEvent;

/**
 * An interaction handler that implements the {@link IOnDragDropHandler}
 * interface will be notified about dragOver and dragDrop events by the
 * {@link DragDropGesture} .
 *
 * @author kgirard
 * @since 5.1
 */
public interface IOnDragDropHandler extends IHandler {

	/**
	 * This callback method is invoked when the user performs a dragDropped on
	 * the host.
	 *
	 * @param event
	 *            The original {@link DragEvent}
	 */
	void dragDropped(DragEvent event);

	/**
	 * This callback method is invoked when the user performs a drag that enters
	 * a hosts boundaries.
	 *
	 * @param event
	 *            The original {@link DragEvent}
	 */
	void dragEntered(DragEvent event);

	/**
	 * This callback method is invoked when the user performs a drag that exits
	 * a hosts boundaries.
	 *
	 * @param event
	 *            The original {@link DragEvent}
	 */
	void dragExited(DragEvent event);

	/**
	 * This callback method is invoked when the user performs a dragOver on the
	 * host.
	 *
	 * @param event
	 *            The original {@link DragEvent}
	 */
	void dragOver(DragEvent event);

}
