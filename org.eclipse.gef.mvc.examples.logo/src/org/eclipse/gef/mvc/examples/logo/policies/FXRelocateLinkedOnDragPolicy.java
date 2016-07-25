/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.gef.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.PartUtils;

import javafx.scene.Node;

public class FXRelocateLinkedOnDragPolicy
		extends FXTranslateSelectedOnDragPolicy {

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		List<IContentPart<Node, ? extends Node>> selected = super.getTargetParts();

		List<IContentPart<Node, ? extends Node>> linked = new ArrayList<>();
		for (IContentPart<Node, ? extends Node> cp : selected) {
			// ensure that linked parts are moved with us during dragging
			linked.addAll(
					(Collection<? extends IContentPart<Node, ? extends Node>>) new ArrayList<>(
							PartUtils.filterParts(
									PartUtils.getAnchoreds(cp, "link"),
									IContentPart.class)));
		}

		// remove all linked that are selected already (these will be translated
		// via the FXTranslateSelectedOnDragPolicy) already
		SelectionModel<?> selectionModel = getHost().getRoot().getViewer()
				.getAdapter(SelectionModel.class);
		linked.removeAll(selectionModel.getSelectionUnmodifiable());

		return linked;
	}

}
