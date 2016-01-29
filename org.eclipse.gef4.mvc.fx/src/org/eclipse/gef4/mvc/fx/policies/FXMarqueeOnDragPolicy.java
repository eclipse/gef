/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - contribution for Bugzilla #451852
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * The {@link FXMarqueeOnDragPolicy} is an {@link IFXOnDragPolicy} that performs
 * marquee selection when the mouse is dragged. The start and end position of
 * the mouse span a marquee area. Everything within that area will be selected.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXMarqueeOnDragPolicy extends AbstractFXInteractionPolicy
		implements IFXOnDragPolicy {

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

	/**
	 * Returns a {@link List} of all {@link Node}s that are descendants of the
	 * given root {@link Node} and fully contained within the bounds specified
	 * by <code>[x0, y0, x1, y1]</code>.
	 *
	 * @param root
	 *            The root {@link Node}.
	 * @param x0
	 *            The minimum x-coordinate.
	 * @param y0
	 *            The minimum y-coordinate.
	 * @param x1
	 *            The maximum x-coordinate.
	 * @param y1
	 *            The maximum y-coordinate.
	 * @return A {@link List} containing all {@link Node}s that are descendants
	 *         of the given root {@link Node} and fully contained within the
	 *         specified bounds.
	 */
	// TODO: move to utility
	public static List<Node> findContainedNodes(Node root, double x0, double y0,
			double x1, double y1) {
		Bounds bounds;
		double bx1, bx0, by1, by0;

		List<Node> containedNodes = new ArrayList<>();
		Queue<Node> nodes = new LinkedList<>();
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

	private CursorSupport cursorSupport = new CursorSupport(this);

	// stores upon press() if the press-drag-release gesture is invalid
	private boolean invalidGesture = false;

	// mouse coordinates
	private Point2D startPosInRoot;
	private Point2D endPosInRoot;

	// feedback
	private IFeedbackPart<Node, ? extends Node> feedback;

	/**
	 * Adds a feedback rectangle to the root part of the {@link #getHost() host}
	 * . The rectangle will show the marquee area.
	 */
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
				Point2D start = visual.sceneToLocal(
						root.getVisual().localToScene(startPosInRoot));
				Point2D end = visual.sceneToLocal(
						root.getVisual().localToScene(endPosInRoot));
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
		if (invalidGesture) {
			return;
		}

		endPosInRoot = getHost().getRoot().getVisual()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		updateFeedback();
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that are
	 * corresponding to the given {@link List} of {@link Node}s.
	 *
	 * @param nodes
	 *            The {@link List} of {@link Node}s for which the corresponding
	 *            {@link IContentPart}s are returned.
	 * @return A {@link List} containing all {@link IContentPart}s that are
	 *         corresponding to the given {@link Node}s.
	 */
	protected List<IContentPart<Node, ? extends Node>> getParts(
			List<Node> nodes) {
		List<IContentPart<Node, ? extends Node>> parts = new ArrayList<>();
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		for (Node node : nodes) {
			IVisualPart<Node, ? extends Node> part = FXPartUtils
					.retrieveVisualPart(viewer, node);
			if (part != null && part instanceof IContentPart
					&& !parts.contains(part)) {
				parts.add((IContentPart<Node, ? extends Node>) part);
			}
		}
		return parts;
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	@Override
	public void press(MouseEvent e) {
		if (e.getTarget() instanceof Node) {
			Node node = (Node) e.getTarget();
			IVisualPart<Node, ? extends Node> nodePart = FXPartUtils
					.retrieveVisualPart(getHost().getRoot().getViewer(), node);
			invalidGesture = nodePart != getHost().getRoot();
		}
		if (invalidGesture) {
			return;
		}

		startPosInRoot = getHost().getRoot().getVisual()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		endPosInRoot = new Point2D(startPosInRoot.getX(),
				startPosInRoot.getY());
		addFeedback();
	}

	@SuppressWarnings("serial")
	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			return;
		}

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
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});
		selectionModel.prependToSelection(parts);

		removeFeedback();
	}

	/**
	 * Removes the feedback rectangle.
	 */
	protected void removeFeedback() {
		if (feedback != null) {
			getHost().getRoot().removeChild(feedback);
			feedback = null;
		}
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	/**
	 * Updates the feedback rectangle.
	 */
	protected void updateFeedback() {
		if (feedback != null) {
			feedback.refreshVisual();
		}
	}

}
