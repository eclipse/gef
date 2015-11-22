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

import java.util.Set;

import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.policies.AbstractTransactionPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.models.HidingModel;
import org.eclipse.gef4.zest.fx.operations.HideOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Node;

/**
 * The {@link ShowHiddenNeighborsPolicy} can be installed on
 * {@link NodeContentPart} to show its hidden neighbours
 *
 * @author mwienand
 *
 */
public class ShowHiddenNeighborsPolicy extends AbstractTransactionPolicy<Node> {

	@Override
	protected ITransactionalOperation createOperation() {
		return new ReverseUndoCompositeOperation("Show Hidden Neighbours");
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	/**
	 * Executes an operation on the history that shows the {@link #getHost()
	 * host} {@link NodeContentPart}.
	 */
	public void showHiddenNeighbors() {
		checkInitialized();
		IViewer<javafx.scene.Node> viewer = getHost().getRoot().getViewer();
		HidingModel hidingModel = viewer.getAdapter(HidingModel.class);
		Set<org.eclipse.gef4.graph.Node> hiddenNeighbors = hidingModel.getHiddenNeighbors(getHost().getContent());
		if (hiddenNeighbors != null && !hiddenNeighbors.isEmpty()) {
			for (org.eclipse.gef4.graph.Node node : hiddenNeighbors) {
				((AbstractCompositeOperation) getOperation())
						.add(new HideOperation(viewer, (NodeContentPart) viewer.getContentPartMap().get(node), true));
			}
		}
		locallyExecuteOperation();
	}

}
