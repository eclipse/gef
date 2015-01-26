package org.eclipse.gef4.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

public class FXRelocateLinkedOnDragPolicy extends FXRelocateOnDragPolicy {

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		List<IContentPart<Node, ? extends Node>> selected = super
				.getTargetParts();
		List<IContentPart<Node, ? extends Node>> linked = new ArrayList<IContentPart<Node, ? extends Node>>();
		for (IContentPart<Node, ? extends Node> cp : selected) {
			// ensure that linked parts are moved with us during dragging
			linked.addAll((Collection<? extends IContentPart<Node, ? extends Node>>) new ArrayList<>(
					PartUtils.filterParts(PartUtils.getAnchoreds(cp, "link"),
							IContentPart.class)));
		}
		return linked;
	}

}
