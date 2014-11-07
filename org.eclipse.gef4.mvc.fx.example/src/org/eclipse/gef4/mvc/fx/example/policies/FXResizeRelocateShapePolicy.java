package org.eclipse.gef4.mvc.fx.example.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;

public class FXResizeRelocateShapePolicy extends
		FXResizeRelocatePolicy {

	@Override
	public IUndoableOperation commit() {
		final IUndoableOperation updateVisualOperation = super.commit();
		if (updateVisualOperation == null) {
			return null;
		}

		// commit changes to model
		final FXGeometricShape hostContent = getHost().getContent();
		FXGeometryNode<IShape> hostVisual = getHost().getVisual();
		final IShape newGeometry = hostVisual.getGeometry();
		final IShape oldGeometry = hostContent.getGeometry();

		final AffineTransform oldTransform = hostContent.getTransform();
		final AffineTransform newTransform = new AffineTransform(1, 0, 0,
				1, hostVisual.getLayoutX(), hostVisual.getLayoutY());

		final IUndoableOperation updateModelOperation = new AbstractOperation(
				"Update Model") {

			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				hostContent.setGeometry(newGeometry);
				hostContent.setTransform(newTransform);
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
				hostContent.setTransform(oldTransform);
				return Status.OK_STATUS;
			}
		};
		// compose both operations
		IUndoableOperation compositeOperation = new AbstractCompositeOperation(
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

	@Override
	public void init() {
		super.init();
	}
}