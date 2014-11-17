package org.eclipse.gef4.zest.fx.parts;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.policies.PruneNodePolicy;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

public class ZestFxExpandingHandlePart extends ZestFxPruningHandlePart {

	public static final String IMG_EXPAND = "/expandall.gif";
	public static final String IMG_EXPAND_DISABLED = "/expandall_disabled.gif";

	public ZestFxExpandingHandlePart(Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
	}

	@Override
	protected Image getHoverImage() {
		return new Image(IMG_EXPAND);
	}

	@Override
	protected Image getImage() {
		return new Image(IMG_EXPAND_DISABLED);
	}

	@Override
	protected void onClicked(MouseEvent event) {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keySet().iterator().next();
		SubgraphModel subgraphModel = anchorage.getRoot().getViewer().getDomain().getAdapter(SubgraphModel.class);
		Set<NodeContentPart> containedNodes = subgraphModel.getContainedNodes((NodeContentPart) anchorage);
		if (containedNodes != null && !containedNodes.isEmpty()) {
			for (NodeContentPart node : containedNodes) {
				PruneNodePolicy prunePolicy = node.getAdapter(PruneNodePolicy.class);
				prunePolicy.unprune();
			}
		}
	}

}
