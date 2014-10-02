/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.operations.FXClearInteractionModelsOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.zest.fx.operations.PruneOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

// TODO: only applicable for NodeContentPart
public class PruneNodePolicy extends AbstractPolicy<Node> {

	private boolean pruned = false;

	public boolean isPruned() {
		return pruned;
	}

	public void prune() {
		FXClearInteractionModelsOperation revOp = new FXClearInteractionModelsOperation(
				getHost().getRoot().getViewer());
		revOp.add(PruneOperation.prune((NodeContentPart) getHost()));
		executeOperation(revOp);
	}

	public void unprune() {
		FXClearInteractionModelsOperation revOp = new FXClearInteractionModelsOperation(
				getHost().getRoot().getViewer());
		revOp.add(PruneOperation.unprune((NodeContentPart) getHost()));
		executeOperation(revOp);
	}

}
