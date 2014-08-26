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
package org.eclipse.gef4.mvc.fx.example.policies;

import java.util.List;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.parts.AbstractFXGeometricElementPart;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.policies.IDeleteContentChildrenPolicy;

// TODO: only valid for FXGeometricModelPart (see #getHost())
public class FXExampleDeleteContentChildrenPolicy extends AbstractPolicy<Node>
implements IDeleteContentChildrenPolicy<Node> {

	public static class Operation extends AbstractOperation {
		private final AbstractFXGeometricElement<?> content;
		private final List<AbstractFXGeometricElement<? extends IGeometry>> shapeVisuals;

		public Operation(AbstractFXGeometricElementPart part) {
			super("DeleteContentChildren");
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

	@Override
	public IUndoableOperation getDeleteOperation(IContentPart<Node> child) {
		return new Operation((AbstractFXGeometricElementPart) child);
	}

	@Override
	public FXGeometricModelPart getHost() {
		return (FXGeometricModelPart) super.getHost();
	}

}
