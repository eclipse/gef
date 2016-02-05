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
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

/**
 * The {@link ChangeNodeBoundsOperation} can be used to manipulate the position
 * and size of a {@link Node}.
 *
 * @author mwienand
 *
 */
public class ChangeNodeBoundsOperation extends AbstractOperation implements ITransactionalOperation {

	private NodeContentPart nodePart;
	private Rectangle initialBounds;
	private Rectangle finalBounds;

	/**
	 * Constructs a new {@link ChangeNodeBoundsOperation} that can be used to
	 * manipulate the position and size of the given {@link Node}.
	 *
	 * @param nodePart
	 *            The {@link Node} that is manipulated by this operation.
	 * @param finalBounds
	 *            The {@link Rectangle} describing the final bounds for the
	 *            given {@link Node}.
	 */
	public ChangeNodeBoundsOperation(NodeContentPart nodePart, Rectangle finalBounds) {
		super("TransformNode()");
		this.nodePart = nodePart;
		this.finalBounds = finalBounds;
		initialBounds = ZestProperties.getBounds(nodePart.getContent());
		if (initialBounds == null) {
			initialBounds = FX2Geometry.toRectangle(nodePart.getVisual().getLayoutBounds());
		} else {
			initialBounds = initialBounds.getCopy();
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (!finalBounds.equals(ZestProperties.getBounds(nodePart.getContent()))) {
			ZestProperties.setBounds(nodePart.getContent(), finalBounds);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialBounds.equals(finalBounds);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the final bounds for this operation to the given value.
	 *
	 * @param finalBounds
	 *            A {@link Rectangle} describing the final bounds for this
	 *            operation.
	 */
	public void setFinalBounds(Rectangle finalBounds) {
		this.finalBounds = finalBounds;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (!initialBounds.equals(ZestProperties.getBounds(nodePart.getContent()))) {
			ZestProperties.setBounds(nodePart.getContent(), initialBounds);
		}
		return Status.OK_STATUS;
	}

}
