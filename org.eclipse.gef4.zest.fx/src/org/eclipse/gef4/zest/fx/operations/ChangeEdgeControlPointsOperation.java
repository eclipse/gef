/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;

/**
 * The {@link ChangeEdgeControlPointsOperation} can be used to manipulate the
 * control points of an {@link Edge}.
 *
 * @author anyssen
 *
 */
public class ChangeEdgeControlPointsOperation extends AbstractOperation implements ITransactionalOperation {

	private EdgeContentPart edgePart;
	private List<Point> initialControlPoints;
	private List<Point> finalControlPoints;

	/**
	 * Constructs a new {@link ChangeEdgeControlPointsOperation} that can be
	 * used to manipulate the position of the given {@link Node}.
	 *
	 * @param edgePart
	 *            The {@link EdgeContentPart} that is manipulated by this
	 *            operation.
	 * @param finalControlPoints
	 *            The control points to set on the given {@link Edge}.
	 */
	public ChangeEdgeControlPointsOperation(EdgeContentPart edgePart, List<Point> finalControlPoints) {
		super("TransformNode()");
		this.edgePart = edgePart;
		this.finalControlPoints = new ArrayList<>(finalControlPoints);
		this.initialControlPoints = new ArrayList<>(ZestProperties.getControlPoints(edgePart.getContent()));
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		List<Point> currentPoints = ZestProperties.getControlPoints(edgePart.getContent());
		if (finalControlPoints != currentPoints
				&& (finalControlPoints == null || !finalControlPoints.equals(currentPoints))) {
			ZestProperties.setControlPoints(edgePart.getContent(), finalControlPoints);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialControlPoints == finalControlPoints
				|| (initialControlPoints != null && initialControlPoints.equals(finalControlPoints));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the control points for this operation to the given value.
	 *
	 * @param finalControlPoints
	 *            The control points to set on the {@link Edge}.
	 */
	public void setFinalControlPoints(List<Point> finalControlPoints) {
		this.finalControlPoints.clear();
		this.finalControlPoints.addAll(finalControlPoints);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		List<Point> currentPoints = ZestProperties.getControlPoints(edgePart.getContent());
		if (initialControlPoints != currentPoints
				&& (initialControlPoints == null || !initialControlPoints.equals(currentPoints))) {
			ZestProperties.setControlPoints(edgePart.getContent(), initialControlPoints);
		}
		return Status.OK_STATUS;
	}

}
