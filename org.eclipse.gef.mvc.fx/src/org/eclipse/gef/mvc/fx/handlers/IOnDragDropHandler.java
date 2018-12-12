/*******************************************************************************
 * Copyright (c) 2018 KDM Analytics Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kyle Girard (KDM Analytics Inc.) - initial API and implementation
 *
 *******************************************************************************/

package com.kdma.brm.riskmap.handlers;

import org.eclipse.gef.mvc.fx.handlers.IHandler;
import com.kdma.brm.riskmap.gestures.DragDropGesture;
import javafx.scene.input.DragEvent;

/**
 * An interaction handler that implements the {@link IOnDragDropHandler} interface will be notified
 * about dragOver and dragDrop events by the {@link DragDropGesture} .
 *
 * @author kgirard
 *
 */
public interface IOnDragDropHandler extends IHandler {

	/**
	 * This callback method is invoked when the user performs a drag that enters a hosts boundaries.
	 * 
	 * @param event
	 */
	void dragEntered(DragEvent event);

	/**
	 * This callback method is invoked when the user performs a drag that exits a hosts boundaries.
	 * 
	 * @param event
	 */
	void dragExited(DragEvent event);

	/**
	 * This callback method is invoked when the user performs a dragOver on the host
	 *
	 * @param event
	 */
	void dragOver(DragEvent event);

	/**
	 * This callback method is invoked when the user performs a dragDropped on the host
	 *
	 * @param event
	 */
	void dragDropped(DragEvent event);

}
