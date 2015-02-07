/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.operations;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXRevealOperation extends AbstractOperation {

	private double tx = 0d;
	private double ty = 0d;

	private IViewer<Node> viewer;
	private ViewportModel viewportModel;
	private IVisualPart<Node, ? extends Node> part = null;

	public FXRevealOperation(IVisualPart<Node, ? extends Node> part) {
		super("Reveal");
		this.part = part;
		viewer = part.getRoot().getViewer();
		viewportModel = viewer.getAdapter(ViewportModel.class);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// store the viewport translation
		tx = viewportModel.getTranslateX();
		ty = viewportModel.getTranslateY();
		viewer.reveal(part);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// restore the viewport translation
		viewportModel.setTranslateX(tx);
		viewportModel.setTranslateY(ty);
		return Status.OK_STATUS;
	}

}
