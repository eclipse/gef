package org.eclipse.gef4.mvc.fx.policies;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.parts.AbstractFeedbackPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.DefaultSelectionPolicy;

public class FXMarqueeOnDragPolicy extends AbstractFXDragPolicy {

	public static List<Node> findContainedNodes(Node root, double x0,
			double y0, double x1, double y1) {
		Bounds bounds;
		double bx1, bx0, by1, by0;

		List<Node> containedNodes = new ArrayList<Node>();
		Queue<Node> nodes = new LinkedList<Node>();
		nodes.add(root);

		while (!nodes.isEmpty()) {
			Node current = nodes.remove();

			bounds = current.getBoundsInLocal();
			bounds = current.localToScene(bounds);
			bx1 = bounds.getMaxX();
			bx0 = bounds.getMinX();
			by1 = bounds.getMaxY();
			by0 = bounds.getMinY();

			if (bx1 < x0 || bx0 > x1 || by1 < y0 || by0 > y1) {
				// current node is outside of marquee bounds => dont collect
			} else {
				if (bx0 >= x0 && bx1 <= x1 && by0 >= y0 && by1 <= y1) {
					// current node is fully contained within marquee bounds
					containedNodes.add(current);
				}
				if (current instanceof Parent) {
					// add all children to nodes
					Parent p = (Parent) current;
					nodes.addAll(p.getChildrenUnmodifiable());
				}
			}
		}

		return containedNodes;
	}

	// mouse coordinates
	private double startPosSceneX;
	private double startPosSceneY;
	private double endPosSceneX;
	private double endPosSceneY;

	// marquee bounds
	private double x0, y0, x1, y1;

	// feedback
	private IFeedbackPart<Node> feedback;

	protected void addFeedback() {
		if (feedback != null) {
			removeFeedback();
		}
		feedback = new AbstractFeedbackPart<Node>() {
			private final Rectangle rect = new Rectangle();

			{
				rect.setFill(Color.TRANSPARENT);
				rect.setStroke(new Color(0, 0, 1, 1));
				rect.getStrokeDashArray().setAll(5d, 10d, 15d);
			}

			@Override
			public void attachVisualToAnchorageVisual(
					IVisualPart<Node> anchorage, Node anchorageVisual) {
			}

			@Override
			public void detachVisualFromAnchorageVisual(
					IVisualPart<Node> anchorage, Node anchorageVisual) {
			}

			@Override
			protected void doRefreshVisual() {
				Point2D start = rect.sceneToLocal(x0, y0);
				Point2D end = rect.sceneToLocal(x1, y1);
				rect.setX(start.getX());
				rect.setY(start.getY());
				rect.setWidth(end.getX() - start.getX());
				rect.setHeight(end.getY() - start.getY());
			}

			@Override
			public Node getVisual() {
				return rect;
			}
		};
		getHost().getRoot().addChild(feedback);
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		updateMarquee(e);
		updateFeedback();
	}

	protected List<IVisualPart<Node>> getParts(List<Node> nodes) {
		List<IVisualPart<Node>> parts = new ArrayList<IVisualPart<Node>>();
		for (Node node : nodes) {
			IVisualPart<Node> part = getHost().getRoot().getViewer()
					.getVisualPartMap().get(node);
			if (part != null) {
				parts.add(part);
			}
		}
		return parts;
	}

	@SuppressWarnings("unchecked")
	protected DefaultSelectionPolicy<Node> getSelectionPolicy(
			IVisualPart<Node> part) {
		return part.getAdapter(DefaultSelectionPolicy.class);
	}

	@Override
	public void press(MouseEvent e) {
		x0 = x1 = endPosSceneX = startPosSceneX = e.getSceneX();
		y0 = y1 = endPosSceneY = startPosSceneY = e.getSceneY();
		addFeedback();
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		updateMarquee(e);

		List<Node> nodes = findContainedNodes(getHost().getVisual().getScene()
				.getRoot(), x0, y0, x1, y1);

		List<IVisualPart<Node>> parts = getParts(nodes);

		boolean appendSelection = false;
		for (IVisualPart<Node> part : parts) {
			DefaultSelectionPolicy<Node> selectionPolicy = getSelectionPolicy(part);
			if (selectionPolicy != null) {
				selectionPolicy.select(appendSelection);
				appendSelection = true;
			}
		}

		removeFeedback();
	}

	protected void removeFeedback() {
		getHost().getRoot().removeChild(feedback);
		feedback = null;
	}

	protected void updateFeedback() {
		feedback.refreshVisual();
	}

	private void updateMarquee(MouseEvent e) {
		endPosSceneX = e.getSceneX();
		endPosSceneY = e.getSceneY();

		// arrange marquee bounds so that x0 <= x1 && y0 <= y1
		if (endPosSceneX < startPosSceneX) {
			x0 = endPosSceneX;
			x1 = startPosSceneX;
		} else {
			x0 = startPosSceneX;
			x1 = endPosSceneX;
		}
		if (endPosSceneY < startPosSceneY) {
			y0 = endPosSceneY;
			y1 = startPosSceneY;
		} else {
			y0 = startPosSceneY;
			y1 = endPosSceneY;
		}
	}
}
