package org.eclipse.gef4.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

public class FXRelocateLinkedOnDragPolicy extends FXRelocateOnDragPolicy {

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		// ensure that linked parts are moved with us during dragging
		return new ArrayList<>(PartUtils.filterParts(
				PartUtils.getAnchoreds(getHost(), "link"), IContentPart.class));
	}

}
