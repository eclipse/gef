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
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.SelectionPolicy;

public class FXMarqueeOnDragPolicy extends AbstractFXDragPolicy {

	private static double[] bbox(Point2D start, Point2D end) {
		double bbox[] = { start.getX(), start.getY(), end.getX(), end.getY() };
		double tmp;
		if (bbox[0] > bbox[2]) {
			tmp = bbox[0];
			bbox[0] = bbox[2];
			bbox[2] = tmp;
		}
		if (bbox[1] > bbox[3]) {
			tmp = bbox[1];
			bbox[1] = bbox[3];
			bbox[3] = tmp;
		}
		return bbox;
	}

	// TODO: move to utility
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
	private Point2D startPosInFeedbackLayer;
	private Point2D endPosInFeedbackLayer;

	// feedback
	private IFeedbackPart<Node> feedback;

	protected void addFeedback() {
		if (feedback != null) {
			removeFeedback();
		}
		feedback = new AbstractFXFeedbackPart() {
			private final Rectangle rect = new Rectangle();

			{
				rect.setFill(Color.TRANSPARENT);
				rect.setStroke(FXSegmentHandlePart.STROKE_DARK_BLUE);
				rect.setStrokeWidth(1);
				rect.setStrokeType(StrokeType.CENTERED);
				rect.getStrokeDashArray().setAll(5d, 5d);
			}

			@Override
			protected void doRefreshVisual() {
				FXRootPart root = (FXRootPart) getRoot();
				Point2D start = rect.sceneToLocal(root.getFeedbackLayer()
						.localToScene(startPosInFeedbackLayer));
				Point2D end = rect.sceneToLocal(root.getFeedbackLayer()
						.localToScene(endPosInFeedbackLayer));
				double[] bbox = bbox(start, end);

				// offset x and y by half a pixel to ensure the rectangle gets a
				// hairline stroke
				rect.setX(bbox[0] - 0.5);
				rect.setY(bbox[1] - 0.5);
				rect.setWidth(bbox[2] - bbox[0]);
				rect.setHeight(bbox[3] - bbox[1]);
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
		updateMarquee(delta);
		updateFeedback();
	}

	// TODO: move to utility
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
	protected SelectionPolicy<Node> getSelectionPolicy(
			IVisualPart<Node> part) {
		return part.getAdapter(SelectionPolicy.class);
	}

	@Override
	public void press(MouseEvent e) {
		FXRootPart root = (FXRootPart) getHost().getRoot();
		startPosInFeedbackLayer = root.getFeedbackLayer().sceneToLocal(
				e.getSceneX(), e.getSceneY());
		endPosInFeedbackLayer = new Point2D(startPosInFeedbackLayer.getX(),
				startPosInFeedbackLayer.getY());
		addFeedback();
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		updateMarquee(delta);

		FXRootPart root = (FXRootPart) getHost().getRoot();
		Point2D start = root.getFeedbackLayer().localToScene(
				startPosInFeedbackLayer);
		Point2D end = root.getFeedbackLayer().localToScene(
				endPosInFeedbackLayer);
		double[] bbox = bbox(start, end);
		List<Node> nodes = findContainedNodes(getHost().getVisual().getScene()
				.getRoot(), bbox[0], bbox[1], bbox[2], bbox[3]);

		List<IVisualPart<Node>> parts = getParts(nodes);

		boolean appendSelection = false;
		for (IVisualPart<Node> part : parts) {
			SelectionPolicy<Node> selectionPolicy = getSelectionPolicy(part);
			if (selectionPolicy != null) {
				selectionPolicy.select(appendSelection);
				appendSelection = true;
			}
		}

		removeFeedback();
	}

	protected void removeFeedback() {
		if (feedback != null) {
			getHost().getRoot().removeChild(feedback);
			feedback = null;
		}
	}

	protected void updateFeedback() {
		feedback.refreshVisual();
	}

	private void updateMarquee(Dimension delta) {
		endPosInFeedbackLayer = new Point2D(startPosInFeedbackLayer.getX()
				+ delta.width, startPosInFeedbackLayer.getY() + delta.height);
	}

}
