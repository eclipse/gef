/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.policies.AbstractTransactionPolicy;
import org.eclipse.gef4.zest.fx.operations.ShowHiddenNeighborsOperation;
import org.eclipse.gef4.zest.fx.parts.NodePart;

import javafx.scene.Node;

/**
 * The {@link ShowHiddenNeighborsPolicy} can be installed on
 * {@link NodePart} to show its hidden neighbours
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsPolicy extends AbstractTransactionPolicy<Node> {

	@Override
	protected ITransactionalOperation createOperation() {
		return new ReverseUndoCompositeOperation("ShowHiddenNeighbours");
	}

	@Override
	public NodePart getHost() {
		return (NodePart) super.getHost();
	}

	/**
	 * Executes an operation on the history that shows the {@link #getHost()
	 * host} {@link NodePart}.
	 */
	public void showHiddenNeighbors() {
		checkInitialized();
		((AbstractCompositeOperation) getOperation())
				.add(new ShowHiddenNeighborsOperation(getHost().getRoot().getViewer(), getHost()));
		locallyExecuteOperation();
	}

}
