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

import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXTypePolicy;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

public class PruneOnTypePolicy extends AbstractFXTypePolicy {

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	@Override
	public void pressed(KeyEvent event) {
		KeyCode keyCode = event.getCode();
		if (KeyCode.P.equals(keyCode)) {
			prune();
		} else if (KeyCode.E.equals(keyCode)) {
			unprune();
		}
	}

	protected void prune() {
		PruneNodePolicy prunePolicy = getHost().getAdapter(
				PruneNodePolicy.class);
		prunePolicy.prune();
	}

	@Override
	public void released(KeyEvent event) {
	}

	protected void unprune() {
		SubgraphModel subgraphModel = getHost().getRoot().getViewer()
				.getDomain().getAdapter(SubgraphModel.class);
		Set<NodeContentPart> containedNodes = subgraphModel
				.getContainedNodes(getHost());
		if (containedNodes != null && !containedNodes.isEmpty()) {
			for (NodeContentPart node : containedNodes) {
				PruneNodePolicy prunePolicy = node
						.getAdapter(PruneNodePolicy.class);
				prunePolicy.unprune();
			}
		}
	}

}
