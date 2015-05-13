/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXMarqueeOnDragPolicy extends AbstractFXOnDragPolicy {

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
	private Point2D startPosInRoot;
	private Point2D endPosInRoot;

	// feedback
	private IFeedbackPart<Node, ? extends Node> feedback;

	protected void addFeedback() {
		if (feedback != null) {
			removeFeedback();
		}
		feedback = new AbstractFXFeedbackPart<Rectangle>() {

			@Override
			protected Rectangle createVisual() {
				Rectangle visual = new Rectangle();
				visual.setFill(Color.TRANSPARENT);
				visual.setStroke(FXCircleSegmentHandlePart.DEFAULT_STROKE);
				visual.setStrokeWidth(1);
				visual.setStrokeType(StrokeType.CENTERED);
				visual.getStrokeDashArray().setAll(5d, 5d);
				return visual;
			}

			@Override
			protected void doRefreshVisual(Rectangle visual) {
				IRootPart<Node, ? extends Node> root = getRoot();
				Point2D start = visual.sceneToLocal(root.getVisual()
						.localToScene(startPosInRoot));
				Point2D end = visual.sceneToLocal(root.getVisual()
						.localToScene(endPosInRoot));
				double[] bbox = bbox(start, end);

				// offset x and y by half a pixel to ensure the rectangle gets a
				// hairline stroke
				visual.setX(bbox[0] - 0.5);
				visual.setY(bbox[1] - 0.5);
				visual.setWidth(bbox[2] - bbox[0]);
				visual.setHeight(bbox[3] - bbox[1]);
			}

		};
		getHost().getRoot().addChild(feedback);
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		endPosInRoot = getHost().getRoot().getVisual()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		updateFeedback();
	}

	protected List<IContentPart<Node, ? extends Node>> getParts(List<Node> nodes) {
		List<IContentPart<Node, ? extends Node>> parts = new ArrayList<IContentPart<Node, ? extends Node>>();
		for (Node node : nodes) {
			IVisualPart<Node, ? extends Node> part = getHost().getRoot()
					.getViewer().getVisualPartMap().get(node);
			if (part != null && part instanceof IContentPart) {
				parts.add((IContentPart<Node, ? extends Node>) part);
			}
		}
		return parts;
	}

	@Override
	public void press(MouseEvent e) {
		startPosInRoot = getHost().getRoot().getVisual()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		endPosInRoot = new Point2D(startPosInRoot.getX(), startPosInRoot.getY());
		addFeedback();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		IRootPart<Node, ? extends Node> root = getHost().getRoot();
		Node rootVisual = root.getVisual();
		endPosInRoot = rootVisual.sceneToLocal(e.getSceneX(), e.getSceneY());
		Point2D start = rootVisual.localToScene(startPosInRoot);
		Point2D end = rootVisual.localToScene(endPosInRoot);
		double[] bbox = bbox(start, end);
		List<Node> nodes = findContainedNodes(rootVisual.getScene().getRoot(),
				bbox[0], bbox[1], bbox[2], bbox[3]);

		List<IContentPart<Node, ? extends Node>> parts = getParts(nodes);

		// change selection within selection model
		SelectionModel<Node> selectionModel = root.getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class);
		selectionModel.select(parts);

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

}
