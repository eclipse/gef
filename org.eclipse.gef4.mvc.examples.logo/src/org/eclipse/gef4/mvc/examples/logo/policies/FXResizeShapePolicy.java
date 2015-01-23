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
package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;

public class FXResizeShapePolicy extends FXResizePolicy {

	@Override
	public IUndoableOperation commit() {
		final IUndoableOperation updateVisualOperation = super.commit();
		if (updateVisualOperation == null) {
			return null;
		}

		// determine old and new geometries
		final FXGeometricShapePart host = getHost();
		final FXGeometricShape hostContent = host.getContent();
		FXGeometryNode<IShape> hostVisual = host.getVisual();
		final IShape oldGeometry = hostContent.getGeometry();
		final IShape newGeometry = hostVisual.getGeometry();

		// create operation to write the changes to the model
		final IUndoableOperation updateModelOperation = new AbstractOperation(
				"Update Model") {

			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				hostContent.setGeometry(newGeometry);
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
				hostContent.setGeometry(oldGeometry);
				return Status.OK_STATUS;
			}
		};
		// compose operations
		IUndoableOperation compositeOperation = new ForwardUndoCompositeOperation(
				updateVisualOperation.getLabel()) {
			{
				add(updateVisualOperation);
				add(updateModelOperation);
			}
		};

		return compositeOperation;
	}

	@Override
	public FXGeometricShapePart getHost() {
		return (FXGeometricShapePart) super.getHost();
	}

}
