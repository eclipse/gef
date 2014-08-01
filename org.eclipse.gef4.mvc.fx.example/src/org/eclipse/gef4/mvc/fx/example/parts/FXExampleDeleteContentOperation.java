/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.List;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXExampleDeleteContentOperation extends
		ForwardUndoCompositeOperation {

	public static class UpdateContentChildrenOperation extends
			AbstractOperation {
		private final AbstractFXGeometricElement<?> content;
		private final List<AbstractFXGeometricElement<? extends IGeometry>> shapeVisuals;

		public UpdateContentChildrenOperation(
				AbstractFXGeometricElementPart part) {
			super("UpdateContentChildren");
			content = part.getContent();
			FXGeometricModelPart parent = (FXGeometricModelPart) part
					.getParent();
			shapeVisuals = parent.getContent().getShapeVisuals();
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			shapeVisuals.remove(content);
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
			shapeVisuals.add(content);
			return Status.OK_STATUS;
		}
	}

	public FXExampleDeleteContentOperation(String label,
			AbstractFXGeometricElementPart part) {
		super(label);
		add(new UpdateContentChildrenOperation(part));
		add(new SynchronizeContentChildrenOperation<Node>("Sync Children",
				(IContentPart<Node>) part.getParent()));
	}

}
