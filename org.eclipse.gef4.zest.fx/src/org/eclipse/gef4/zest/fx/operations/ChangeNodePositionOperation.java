/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link ChangeNodePositionOperation} can be used to manipulate the
 * position of a {@link Node}.
 *
 * @author mwienand
 *
 */
public class ChangeNodePositionOperation extends AbstractOperation implements ITransactionalOperation {

	private NodeContentPart nodePart;
	private Point initialPosition;
	private Point finalPosition;

	/**
	 * Constructs a new {@link ChangeNodePositionOperation} that can be used to
	 * manipulate the position of the given {@link Node}.
	 *
	 * @param nodePart
	 *            The {@link Node} that is manipulated by this operation.
	 * @param finalPosition
	 *            A {@link Point} describing the final position for the given
	 *            {@link Node}.
	 */
	public ChangeNodePositionOperation(NodeContentPart nodePart, Point finalPosition) {
		super("TransformNode()");
		this.nodePart = nodePart;
		this.finalPosition = finalPosition;
		initialPosition = ZestProperties.getPosition(nodePart.getContent());
		if (initialPosition != null) {
			initialPosition = initialPosition.getCopy();
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Point currentPosition = ZestProperties.getPosition(nodePart.getContent());
		if (finalPosition != currentPosition && (finalPosition == null || !finalPosition.equals(currentPosition))) {
			ZestProperties.setPosition(nodePart.getContent(), finalPosition);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialPosition == finalPosition || (initialPosition != null && initialPosition.equals(finalPosition));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the final position for this operation to the given value.
	 *
	 * @param finalPosition
	 *            A {@link Point} describing the final position for this
	 *            operation.
	 */
	public void setFinalPosition(Point finalPosition) {
		this.finalPosition = finalPosition;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Point currentPosition = ZestProperties.getPosition(nodePart.getContent());
		if (initialPosition != currentPosition
				&& (initialPosition == null || !initialPosition.equals(currentPosition))) {
			ZestProperties.setPosition(nodePart.getContent(), initialPosition);
		}
		return Status.OK_STATUS;
	}

}
