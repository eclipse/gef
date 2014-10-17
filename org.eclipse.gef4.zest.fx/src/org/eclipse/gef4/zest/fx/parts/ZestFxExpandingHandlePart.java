package org.eclipse.gef4.zest.fx.parts;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;
import org.eclipse.gef4.zest.fx.policies.PruneNodePolicy;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

public class ZestFxExpandingHandlePart extends ZestFxPruningHandlePart {

	public ZestFxExpandingHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		if (!(anchorage instanceof NodeContentPart)) {
			throw new IllegalArgumentException("Anchorage not applicable <"
					+ anchorage + ">. Can only attach to NodeContentPart.");
		}
		super.attachToAnchorageVisual(anchorage, role);
	}

	/**
	 * Creates a "plus" shaped polygon with the given size (s) and width (w).
	 * The coordinates of the polygon are as follows:
	 *
	 * <pre>
	 *    (-w, -s) X-X (w, -s)
	 *             | |
	 *    (-w, -w) | | (w, -w)
	 * (-s, -w) X--X X--X (s, -w)
	 *          |       |
	 *  (-s, w) X--X X--X (s, w)
	 *     (-w, w) | | (w, w)
	 *             | |
	 *     (-w, s) X-X (w, s)
	 * </pre>
	 *
	 * Therefore, the polygon will have a width and height of
	 * <code>2 * size</code>.
	 *
	 * @param size
	 * @param width
	 * @return
	 */
	@Override
	protected Polygon createIcon(double size, double width) {
		Polygon icon = new Polygon(-size, -width, -size, width, -width, width,
				-width, size, width, size, width, width, size, width, size,
				-width, width, -width, width, -size, -width, -size, -width,
				-width);
		icon.setStroke(Color.TRANSPARENT);
		icon.setFill(Color.GREEN);
		return icon;
	}

	@Override
	protected void onClicked(MouseEvent event) {
		SetMultimap<IVisualPart<Node>, String> anchorages = getAnchorages();
		if (anchorages == null || anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node> anchorage = anchorages.keySet().iterator().next();
		SubgraphModel subgraphModel = anchorage.getRoot().getViewer()
				.getDomain().getAdapter(SubgraphModel.class);
		Set<NodeContentPart> containedNodes = subgraphModel
				.getContainedNodes((NodeContentPart) anchorage);
		if (containedNodes != null && !containedNodes.isEmpty()) {
			for (NodeContentPart node : containedNodes) {
				PruneNodePolicy prunePolicy = node
						.getAdapter(PruneNodePolicy.class);
				prunePolicy.unprune();
			}
		}
	}

}
