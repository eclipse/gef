/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef.mvc.fx.tools.DefaultTargetPolicyResolver;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.GridModel;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link AbstractFXInteractionPolicy} extends the
 * {@link AbstractInteractionPolicy} and binds its visual root parameter to
 * {@link Node}. It provides two convenience methods that can be used to guard
 * interaction policies from processing events that are intended to be processed
 * by other policies. This is necessary because the
 * {@link DefaultTargetPolicyResolver} iterates the entire visual part hierarchy
 * of the visual that receives the input event and sends the event to all
 * suitable policies on the way.
 * <ul>
 * <li>{@link #isRegistered(EventTarget)}
 * <li>{@link #isRegisteredForHost(EventTarget)}
 * </ul>
 * For example, if a policy should only process events if its host is the
 * explicit event target, the following guard can be implemented within the
 * policy's callback methods (example for an {@link IFXOnHoverPolicy}):
 *
 * <pre>
 * public void hover(MouseEvent e) {
 * 	// do nothing in case there is an explicit event target
 * 	if (isRegistered(e.getTarget()) &amp;&amp; !isRegisteredForHost(e.getTarget())) {
 * 		return;
 * 	}
 * 	// ...
 * }
 * </pre>
 *
 * @author mwienand
 *
 */
public class AbstractFXInteractionPolicy
		extends AbstractInteractionPolicy<Node> {

	/**
	 * Snaps the given position (in scene coordinates) to a grid position. The
	 * grid positions are specified by the given {@link GridModel} and the given
	 * cell size fractions.
	 *
	 * @param sceneX
	 *            The x-coordinate of the current position (in scene
	 *            coordinates).
	 * @param sceneY
	 *            The y-coordinate of the current position (in scene
	 *            coordinates).
	 * @param gridModel
	 *            The {@link GridModel} that specifies the grid positions.
	 * @param gridCellWidthFraction
	 *            The cell width fraction that determines if the x-coordinate is
	 *            snapped to full (1.0), halve (0.5), etc. grid positions.
	 * @param gridCellHeightFraction
	 *            The cell height fraction that determines if the y-coordinate
	 *            is snapped to full (1.0), halve (0.5), etc. grid positions.
	 * @param gridLocalVisual
	 *            A visual within the coordinate system where grid positions are
	 *            at <code>(n * grid-cell-width, m * grid-cell-height)</code>.
	 * @return The resulting snapped position in scene coordinates.
	 */
	public static Point snapToGrid(final double sceneX, final double sceneY,
			GridModel gridModel, final double gridCellWidthFraction,
			final double gridCellHeightFraction, Node gridLocalVisual) {
		// do nothing if snap to grid is disabled
		if (!gridModel.isSnapToGrid()) {
			return new Point(sceneX, sceneY);
		}
		// transform to grid local coordinates
		Point2D gridLocalPosition = gridLocalVisual.sceneToLocal(sceneX,
				sceneY);
		// snap to grid
		double gcw = gridCellWidthFraction * gridModel.getGridCellWidth();
		int xs = (int) (gridLocalPosition.getX() / gcw);
		double gch = gridCellHeightFraction * gridModel.getGridCellHeight();
		int ys = (int) (gridLocalPosition.getY() / gch);
		double nx = xs * gcw;
		double ny = ys * gch;
		// transform to scene coordinates
		Point2D newPositionInScene = gridLocalVisual.localToScene(nx, ny);
		return new Point(newPositionInScene.getX(), newPositionInScene.getY());
	}

	/**
	 * Determines a visual within the given {@link IViewer} in which grid
	 * positions are at
	 * <code>(n * grid-cell-width, m * grid-cell-height)</code>. Per default,
	 * the content group of the {@link InfiniteCanvas} is returned for an
	 * {@link FXViewer}. For other {@link IViewer} implementations, the visual
	 * of the root part is used.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to determine a grid-local
	 *            visual.
	 * @return A grid-local visual for the given {@link IViewer}.
	 */
	protected Node getGridLocalVisual(IViewer<Node> viewer) {
		return viewer instanceof FXViewer
				? ((FXViewer) viewer).getCanvas().getContentGroup()
				: viewer.getRootPart().getVisual();
	}

	/**
	 * Returns the horizontal granularity for "snap-to-grid" where
	 * <code>1</code> means it will snap to integer grid positions.
	 *
	 * @return The horizontal granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityX() {
		return 1;
	}

	/**
	 * Returns the vertical granularity for "snap-to-grid" where <code>1</code>
	 * means it will snap to integer grid positions.
	 *
	 * @return The vertical granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityY() {
		return 1;
	}

	/**
	 * Returns <code>true</code> if the given {@link EventTarget} is registered
	 * in the visual-part-map. Otherwise returns <code>false</code>.
	 *
	 * @param eventTarget
	 *            The {@link EventTarget} that is tested.
	 * @return <code>true</code> if the given {@link EventTarget} is registered
	 *         in the visual-part-map, otherwise <code>false</code>.
	 */
	protected boolean isRegistered(EventTarget eventTarget) {
		IVisualPart<Node, ? extends Node> host = getHost();
		if (host.getRoot() == null || host.getRoot().getViewer() == null) {
			// host is not in visual-part-hierarchy or not in viewer
			return false;
		}
		IViewer<Node> viewer = host.getRoot().getViewer();
		if (eventTarget instanceof Node) {
			return FXPartUtils.retrieveVisualPart(viewer,
					(Node) eventTarget) != viewer.getRootPart();
		}
		// eventTarget is a Scene
		return false;
	}

	/**
	 * Returns <code>true</code> if the given {@link EventTarget} is registered
	 * in the visual-part-map for the {@link #getHost() host} of this
	 * {@link AbstractInteractionPolicy}. Otherwise returns <code>false</code>.
	 *
	 * @param eventTarget
	 *            The {@link EventTarget} that is tested.
	 * @return <code>true</code> if the given {@link EventTarget} is registered
	 *         in the visual-part-map for the host of this policy, otherwise
	 *         <code>false</code>.
	 */
	protected boolean isRegisteredForHost(EventTarget eventTarget) {
		IVisualPart<Node, ? extends Node> host = getHost();
		if (host.getRoot() == null || host.getRoot().getViewer() == null) {
			// host is not in visual-part-hierarchy or not in viewer
			return false;
		}
		IViewer<Node> viewer = host.getRoot().getViewer();
		if (eventTarget instanceof Node) {
			return FXPartUtils.retrieveVisualPart(viewer,
					(Node) eventTarget) == host;
		}
		// eventTarget is a Scene
		return false;
	}

	/**
	 * Snaps the given position (in scene coordinates) to a grid position (in
	 * scene coordinates).
	 *
	 * @param viewer
	 *            The {@link IViewer} in which the the position is snapped.
	 * @param sceneX
	 *            Original position's x-coordinate.
	 * @param sceneY
	 *            Original position's y-coordinate.
	 * @return The snapped position in scene coordinates.
	 */
	protected Point snapToGrid(IViewer<Node> viewer, double sceneX,
			double sceneY) {
		return snapToGrid(sceneX, sceneY,
				viewer.<GridModel> getAdapter(GridModel.class),
				getSnapToGridGranularityX(), getSnapToGridGranularityY(),
				getGridLocalVisual(viewer));
	}

}
