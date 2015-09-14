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
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

public class FXResizeShapePolicy extends FXResizePolicy {

	private class ModelOperation extends AbstractOperation
			implements ITransactionalOperation {
		private final FXGeometricShape hostContent;
		private final IShape oldGeometry;
		private final IShape newGeometry;

		public ModelOperation(FXGeometricShape hostContent, IShape oldGeometry,
				IShape newGeometry) {
			super("Update Model");
			this.hostContent = hostContent;
			this.oldGeometry = oldGeometry;
			this.newGeometry = newGeometry;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			hostContent.setGeometry(newGeometry);
			return Status.OK_STATUS;
		}

		@Override
		public boolean isNoOp() {
			return oldGeometry == newGeometry
					|| (oldGeometry != null && oldGeometry.equals(newGeometry));
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
	}

	@Override
	public ITransactionalOperation commit() {
		final ITransactionalOperation updateVisualOperation = super.commit();
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
		final ITransactionalOperation updateModelOperation = new ModelOperation(
				hostContent, oldGeometry, newGeometry);
		// compose operations
		ForwardUndoCompositeOperation compositeOperation = new ForwardUndoCompositeOperation(
				updateVisualOperation.getLabel()) {
			{
				add(updateVisualOperation);
				add(updateModelOperation);
			}
		};

		return compositeOperation.unwrap(true);
	}

	@Override
	public FXGeometricShapePart getHost() {
		return (FXGeometricShapePart) super.getHost();
	}

}
