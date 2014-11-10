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

import org.eclipse.gef4.mvc.operations.ClearHoverFocusSelectionOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.zest.fx.operations.PruneOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

// TODO: only applicable for NodeContentPart
public class PruneNodePolicy extends AbstractPolicy<Node> {

	public void prune() {
		ClearHoverFocusSelectionOperation revOp = new ClearHoverFocusSelectionOperation(getHost().getRoot().getViewer());
		revOp.add(PruneOperation.prune((NodeContentPart) getHost()));
		getHost().getRoot().getViewer().getDomain().execute(revOp);
	}

	public void unprune() {
		ClearHoverFocusSelectionOperation revOp = new ClearHoverFocusSelectionOperation(getHost().getRoot().getViewer());
		revOp.add(PruneOperation.unprune((NodeContentPart) getHost()));
		getHost().getRoot().getViewer().getDomain().execute(revOp);
	}

}
