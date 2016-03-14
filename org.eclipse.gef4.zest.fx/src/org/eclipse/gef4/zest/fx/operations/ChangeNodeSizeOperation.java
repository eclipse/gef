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
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.zest.fx.ZestProperties;

/**
 * The {@link ChangeNodeSizeOperation} can be used to manipulate the size of a
 * {@link Node}.
 *
 * @author mwienand
 *
 */
public class ChangeNodeSizeOperation extends AbstractOperation implements ITransactionalOperation {

	private Node node;
	private Dimension initialSize;
	private Dimension finalSize;

	/**
	 * Constructs a new {@link ChangeNodeSizeOperation} that can be used to
	 * manipulate the position and size of the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} that is manipulated by this operation.
	 * @param finalSize
	 *            The {@link Dimension} describing the final bounds for the
	 *            given {@link Node}.
	 */
	public ChangeNodeSizeOperation(Node node, Dimension finalSize) {
		super("TransformNode()");
		this.node = node;
		this.finalSize = finalSize;
		initialSize = ZestProperties.getSize(node);
		if (initialSize != null) {
			initialSize = initialSize.getCopy();
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Dimension currentSize = ZestProperties.getSize(node);
		if (finalSize != currentSize && (finalSize == null || !finalSize.equals(currentSize))) {
			ZestProperties.setSize(node, finalSize);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return initialSize == finalSize || (initialSize != null && initialSize.equals(finalSize));
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	/**
	 * Sets the final size for this operation to the given value.
	 *
	 * @param finalSize
	 *            A {@link Dimension} describing the final size for this
	 *            operation.
	 */
	public void setFinalSize(Dimension finalSize) {
		this.finalSize = finalSize;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Dimension currentSize = ZestProperties.getSize(node);
		if (initialSize != currentSize && (initialSize == null || !initialSize.equals(currentSize))) {
			ZestProperties.setSize(node, initialSize);
		}
		return Status.OK_STATUS;
	}

}
