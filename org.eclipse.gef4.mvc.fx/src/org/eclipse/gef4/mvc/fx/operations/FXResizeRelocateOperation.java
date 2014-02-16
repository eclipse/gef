package org.eclipse.gef4.mvc.fx.operations;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;

public class FXResizeRelocateOperation extends AbstractOperation {

	private Node visual;
	private Point newLocation;
	private Dimension newSize;
	private Point oldLocation;
	private Dimension oldSize;

	public FXResizeRelocateOperation(String label, Node visual,
			Point oldLocation, Dimension oldSize, Point newLocation,
			Dimension newSize) {
		super(label);
		this.visual = visual;
		this.oldLocation = oldLocation;
		this.oldSize = oldSize;
		this.newLocation = newLocation;
		this.newSize = newSize;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		visual.setLayoutX(newLocation.x);
		visual.setLayoutY(newLocation.y);
		visual.resize(newSize.width, newSize.height);
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
		visual.setLayoutX(oldLocation.x);
		visual.setLayoutY(oldLocation.y);
		visual.resize(oldSize.width, oldSize.height);
		return Status.OK_STATUS;
	}

}
