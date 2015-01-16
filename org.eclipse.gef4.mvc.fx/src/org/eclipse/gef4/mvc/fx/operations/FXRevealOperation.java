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

public class FXRevealOperation extends AbstractOperation {

	private IVisualPart<Node, ? extends Node> part = null;
	private double tx = 0d;
	private double ty = 0d;

	public FXRevealOperation(IVisualPart<Node, ? extends Node> part) {
		super("Reveal");
		this.part = part;
		ViewportModel viewportModel = part.getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		tx = viewportModel.getTranslateX();
		ty = viewportModel.getTranslateY();
	}

	public FXRevealOperation(IVisualPart<Node, ? extends Node> part,
			double initialTranslateX, double initialTranslateY) {
		super("Reveal");
		this.part = part;
		tx = initialTranslateX;
		ty = initialTranslateY;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		part.getRoot().getViewer().reveal(part);
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
		ViewportModel viewportModel = part.getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		viewportModel.setTranslateX(tx);
		viewportModel.setTranslateY(ty);
		return Status.OK_STATUS;
	}

}
