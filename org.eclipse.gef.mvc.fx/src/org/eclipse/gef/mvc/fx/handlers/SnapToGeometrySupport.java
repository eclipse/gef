/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.ISnappablePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link SnapToGeometrySupport} can be used by {@link IHandler}s in order
 * to perform snapping.
 */
public class SnapToGeometrySupport
		extends IAdaptable.Bound.Impl<IVisualPart<? extends Node>> {

	private IContentPart<? extends Node> snappedPart;
	private List<SnappingLocation> xLocations = new ArrayList<>();
	private List<SnappingLocation> yLocations = new ArrayList<>();

	/**
	 * The IS_NOT_SNAPPED {@link Predicate} tests if the given
	 * {@link IVisualPart} is not the currently snapped part.
	 */
	protected Predicate<IVisualPart<? extends Node>> IS_NOT_SNAPPED = (p) -> {
		return p != snappedPart;
	};

	/**
	 * The IS_SNAPPABLE {@link Predicate} tests if the given {@link IVisualPart}
	 * is implementing the {@link ISnappablePart} interface.
	 */
	protected Predicate<IVisualPart<? extends Node>> IS_SNAPPABLE = (p) -> {
		return p instanceof ISnappablePart;

	};

	/**
	 * The IS_VISIBLE {@link Predicate} tests if the given {@link IVisualPart}
	 * is fully visible within the viewport.
	 */
	protected Predicate<IVisualPart<? extends Node>> IS_VISIBLE = (p) -> {
		// get viewport
		InfiniteCanvas canvas = (InfiniteCanvas) getAdaptable().getRoot()
				.getViewer().getCanvas();
		// no snapping feedback for parts outside the viewport
		Bounds boundsInCanvas = canvas.sceneToLocal(
				p.getVisual().localToScene(p.getVisual().getLayoutBounds()));
		if (boundsInCanvas.getMinX() > canvas.getWidth()
				|| boundsInCanvas.getMinY() > canvas.getHeight()
				|| boundsInCanvas.getMaxX() < 0
				|| boundsInCanvas.getMaxY() < 0) {
			return false;
		}
		return true;
	};

	/**
	 * The IS_VISUAL_CONNECTION {@link Predicate} tests if the visual of the
	 * given {@link IVisualPart} is of type {@link Connection}.
	 */
	protected Predicate<IVisualPart<? extends Node>> IS_ORTHO_CONNECTION = (
			p) -> {
		return p.getVisual() instanceof Connection
				&& ((Connection) p.getVisual())
						.getRouter() instanceof OrthogonalRouter;
	};

	/**
	 * The IS_LEAF {@link Predicate} tests if the given {@link IVisualPart} has
	 * no children.
	 */
	protected Predicate<IVisualPart<? extends Node>> IS_LEAF = (p) -> {
		return p.getChildrenUnmodifiable().isEmpty();
	};

	/**
	 *
	 */
	protected void findConnectionLeafSnappingLocations() {
		List<ISnappablePart<? extends Node>> connectionLeafParts = PartUtils
				.filterParts(
						getAdaptable().getRoot().getViewer().getContentPartMap()
								.values(),
						IS_NOT_SNAPPED.and(IS_SNAPPABLE).and(IS_LEAF)
								.and(IS_ORTHO_CONNECTION).and(IS_VISIBLE));

		// compute snapping locations
		// TODO: keep lists sorted by location
		for (ISnappablePart<? extends Node> contextPart : connectionLeafParts) {
			// filter out edges which the snappedPart is connected to
			if (contextPart.getAnchoragesUnmodifiable()
					.containsKey(snappedPart)) {
				continue;
			}
			xLocations.addAll(contextPart.getVerticalSnappingLocations());
			yLocations.addAll(contextPart.getHorizontalSnappingLocations());
		}
	}

	/**
	 *
	 */
	protected void findNonConnectionLeafSnappingLocations() {
		// determine currently rendered NodeParts
		List<ISnappablePart<? extends Node>> nodeParts = PartUtils.filterParts(
				getAdaptable().getRoot().getViewer().getContentPartMap()
						.values(),
				IS_NOT_SNAPPED.and(IS_SNAPPABLE).and(IS_LEAF)
						.and(IS_ORTHO_CONNECTION.negate()).and(IS_VISIBLE));

		// compute snapping locations
		// TODO: keep lists sorted by location
		for (ISnappablePart<? extends Node> contextPart : nodeParts) {
			xLocations.addAll(contextPart.getVerticalSnappingLocations());
			yLocations.addAll(contextPart.getHorizontalSnappingLocations());
		}
	}

	/**
	 * Returns the distance at which snapping is performed, i.e. the
	 * to-be-snapped location needs to be no further away from a snapping
	 * location than this distance in order to be snapped to the snapping
	 * location.
	 *
	 * @return The distance at which snapping is performed.
	 */
	protected double getSnapDistance() {
		return 10d;
	}

	/**
	 * Returns a {@link Dimension} specifying the translation that needs to be
	 * applied to the given location so that it is snapped according to the
	 * rules of this {@link SnapToGeometrySupport}.
	 *
	 * @param x
	 *            The horizontal coordinate of the location that is snapped.
	 * @param y
	 *            The vertical coordinate of the location that is snapped.
	 * @return The {@link Dimension} specifying the snap translation, or
	 *         <code>null</code> if not snapped.
	 */
	public Dimension snap(double x, double y) {
		// find nearest snapping location
		// TODO: binary search
		double minDistance = 0d;
		SnappingLocation snappingLocation = null;
		for (SnappingLocation xl : xLocations) {
			double locationX = xl.getPositionInScene();
			double distance = x - locationX;
			if (Math.abs(distance) < getSnapDistance()
					&& (snappingLocation == null
							|| Math.abs(distance) < Math.abs(minDistance))) {
				minDistance = distance;
				snappingLocation = xl;
			}
		}
		// compute snapping offset
		Dimension snappingOffset = null;
		if (snappingLocation != null) {
			snappingOffset = new Dimension(minDistance, 0);
		}
		// find nearest snapping location
		// TODO: binary search
		minDistance = 0d;
		snappingLocation = null;
		for (SnappingLocation xl : xLocations) {
			double locationX = xl.getPositionInScene();
			double distance = x - locationX;
			if (Math.abs(distance) < getSnapDistance()
					&& (snappingLocation == null
							|| Math.abs(distance) < Math.abs(minDistance))) {
				minDistance = distance;
				snappingLocation = xl;
			}
		}
		// compute snapping offset
		if (snappingLocation != null) {
			if (snappingOffset != null) {
				snappingOffset.setHeight(minDistance);
			} else {
				snappingOffset = new Dimension(0, minDistance);
			}
		}
		return snappingOffset;
	}

	/**
	 * Returns a {@link Dimension} containing the snap-to-location offset for
	 * the currently snapped part. The given {@link Bounds} (in scene
	 * coordinates) is tested for min, center, and max snapping.
	 *
	 * @param boundsInScene
	 *            The {@link Bounds} of the snapped part within the scene
	 *            coordinate system.
	 * @return A {@link Dimension} containing the snap-to-location offset.
	 */
	public Dimension snapToLocation(Bounds boundsInScene) {
		Dimension minSnapOffset = snap(boundsInScene.getMinX(),
				boundsInScene.getMinY());
		Dimension maxSnapOffset = snap(boundsInScene.getMaxX(),
				boundsInScene.getMaxY());
		Dimension centerSnapOffset = snap(
				boundsInScene.getMinX() + 0.5 * boundsInScene.getWidth(),
				boundsInScene.getMinY() + 0.5 * boundsInScene.getHeight());

		// compute source locations (min, center, max)
		double minX = boundsInScene.getMinX();
		double minY = boundsInScene.getMinY();
		double centerX = minX + boundsInScene.getWidth() / 2;
		double centerY = minY + boundsInScene.getHeight() / 2;
		double maxX = boundsInScene.getMaxX();
		double maxY = boundsInScene.getMaxY();

		// find nearest snapping location
		// TODO: binary search
		double minDistance = 0d;
		SnappingLocation snappingLocation = null;
		for (SnappingLocation xl : xLocations) {
			double locationX = xl.getPositionInScene();
			double deltaMin = minX - locationX;
			double deltaCenter = centerX - locationX;
			double deltaMax = maxX - locationX;
			double distance = 0d;
			if (Math.abs(deltaMin) < Math.abs(deltaCenter)
					&& Math.abs(deltaMin) < Math.abs(deltaMax)) {
				distance = deltaMin;
			} else if (Math.abs(deltaCenter) < Math.abs(deltaMin)
					&& Math.abs(deltaCenter) < Math.abs(deltaMax)) {
				distance = deltaCenter;
			} else if (Math.abs(deltaMax) < Math.abs(deltaMin)
					&& Math.abs(deltaMax) < Math.abs(deltaCenter)) {
				distance = deltaMax;
			}
			if (Math.abs(distance) < 10 && (snappingLocation == null
					|| Math.abs(distance) < Math.abs(minDistance))) {
				// snaps
				minDistance = distance;
				snappingLocation = xl;
			}
		}
		// compute snapping offset
		Dimension snappingOffset = snappingLocation == null
				? new Dimension(0, 0) : new Dimension(minDistance, 0);

		// TODO: binary search
		minDistance = 0d;
		snappingLocation = null;
		for (SnappingLocation yl : yLocations) {
			double locationY = yl.getPositionInScene();
			double deltaMin = minY - locationY;
			double deltaCenter = centerY - locationY;
			double deltaMax = maxY - locationY;
			double distance = 0d;
			if (Math.abs(deltaMin) < Math.abs(deltaCenter)
					&& Math.abs(deltaMin) < Math.abs(deltaMax)) {
				distance = deltaMin;
			} else if (Math.abs(deltaCenter) < Math.abs(deltaMin)
					&& Math.abs(deltaCenter) < Math.abs(deltaMax)) {
				distance = deltaCenter;
			} else if (Math.abs(deltaMax) < Math.abs(deltaMin)
					&& Math.abs(deltaMax) < Math.abs(deltaCenter)) {
				distance = deltaMax;
			}
			if (Math.abs(distance) < 10 && (snappingLocation == null
					|| Math.abs(distance) < Math.abs(minDistance))) {
				// snaps
				minDistance = distance;
				snappingLocation = yl;
			}
		}
		// expand snapping offset
		if (snappingLocation != null) {
			snappingOffset.setHeight(minDistance);
		}

		// compute new min, center, and max positions
		double newMinX = boundsInScene.getMinX() - snappingOffset.width;
		double newMinY = boundsInScene.getMinY() - snappingOffset.height;
		double newCenterX = newMinX + boundsInScene.getWidth() / 2;
		double newCenterY = newMinY + boundsInScene.getHeight() / 2;
		double newMaxX = boundsInScene.getMaxX() - snappingOffset.width;
		double newMaxY = boundsInScene.getMaxY() - snappingOffset.height;

		// copy currently established snapping locations to model
		final List<SnappingLocation> snappingLocations = new ArrayList<>();
		// TODO: binary search
		for (SnappingLocation xl : xLocations) {
			double locationX = xl.getPositionInScene();
			double distance = Math.min(Math.abs(newMinX - locationX),
					Math.abs(newCenterX - locationX));
			distance = Math.min(distance, Math.abs(newMaxX - locationX));
			if (distance < 1) {
				snappingLocations.add(xl.getCopy());
			}
		}
		// TODO: binary search
		for (SnappingLocation yl : yLocations) {
			double locationY = yl.getPositionInScene();
			double distance = Math.min(Math.abs(newMinY - locationY),
					Math.abs(newCenterY - locationY));
			distance = Math.min(distance, Math.abs(newMaxY - locationY));
			if (distance < 1) {
				snappingLocations.add(yl.getCopy());
			}
		}

		// update SnapToLocationModel
		getAdaptable().getRoot().getViewer().getAdapter(SnappingModel.class)
				.setSnappingLocations(snappingLocations);

		// transform snapping offset to snappedPart's parent
		Point2D startInParent = snappedPart.getVisual().getParent()
				.sceneToLocal(minX, minY);
		Point2D endInParent = snappedPart.getVisual().getParent()
				.sceneToLocal(newMinX, newMinY);

		return new Dimension(endInParent.getX() - startInParent.getX(),
				endInParent.getY() - startInParent.getY());
	}

	/**
	 * Returns a {@link Dimension} containing the snap-to-location offset for
	 * the currently snapped part. The given {@link Bounds} (in scene
	 * coordinates) is tested for min, center, and max snapping.
	 *
	 * @param orientation
	 *            The {@link Orientation} of the {@link SnappingLocation}s to
	 *            snap to.
	 * @param positionInScene
	 *            The position coordinate of the snapped part.
	 * @return A {@link Dimension} containing the snap-to-location offset.
	 */
	public Dimension snapToLocation(Orientation orientation,
			double positionInScene) {
		// find nearest snapping location
		double minDistance = 0d;
		SnappingLocation snappingLocation = null;
		Dimension snappingOffset = new Dimension();
		// TODO: binary search
		if (orientation == Orientation.VERTICAL) {
			for (SnappingLocation xl : xLocations) {
				double locationX = xl.getPositionInScene();
				double distance = positionInScene - locationX;
				if (Math.abs(distance) < 10 && (snappingLocation == null
						|| Math.abs(distance) < Math.abs(minDistance))) {
					// snaps
					minDistance = distance;
					snappingLocation = xl;
				}
			}
			// compute snapping offset
			if (snappingLocation != null) {
				snappingOffset.setWidth(minDistance);
			}
		} else {
			for (SnappingLocation yl : yLocations) {
				double locationY = yl.getPositionInScene();
				double distance = positionInScene - locationY;
				if (Math.abs(distance) < 10 && (snappingLocation == null
						|| Math.abs(distance) < Math.abs(minDistance))) {
					// snaps
					minDistance = distance;
					snappingLocation = yl;
				}
			}
			// expand snapping offset
			if (snappingLocation != null) {
				snappingOffset.setHeight(minDistance);
			}
		}

		// nothing to do when not snapping
		if (snappingLocation == null) {
			getAdaptable().getRoot().getViewer().getAdapter(SnappingModel.class)
					.setSnappingLocations(
							Collections.<SnappingLocation> emptyList());
			return new Dimension();
		}

		// copy currently established snapping locations to model
		final List<SnappingLocation> snappingLocations = new ArrayList<>();
		// TODO: binary search
		if (orientation == Orientation.VERTICAL) {
			for (SnappingLocation xl : xLocations) {
				double locationX = xl.getPositionInScene();
				double distance = Math
						.abs(snappingLocation.getPositionInScene() - locationX);
				if (distance < 1) {
					snappingLocations.add(xl.getCopy());
				}
			}
		} else {
			for (SnappingLocation yl : yLocations) {
				double locationY = yl.getPositionInScene();
				double distance = Math
						.abs(snappingLocation.getPositionInScene() - locationY);
				if (distance < 1) {
					snappingLocations.add(yl.getCopy());
				}
			}
		}

		// update SnapToLocationModel
		getAdaptable().getRoot().getViewer().getAdapter(SnappingModel.class)
				.setSnappingLocations(snappingLocations);

		// transform snapping offset to snappedPart's parent
		Point2D startPoint = Orientation.VERTICAL == orientation
				? new Point2D(positionInScene, 0)
				: new Point2D(0, positionInScene);
		Point2D endPoint = Orientation.VERTICAL == orientation
				? new Point2D(snappingLocation.getPositionInScene(), 0)
				: new Point2D(0, snappingLocation.getPositionInScene());
		Point2D startInParent = snappedPart.getVisual().getParent()
				.sceneToLocal(startPoint);
		Point2D endInParent = snappedPart.getVisual().getParent()
				.sceneToLocal(endPoint);

		if (Orientation.VERTICAL == orientation) {
			return new Dimension(endInParent.getX() - startInParent.getX(), 0);
		} else {
			return new Dimension(0, endInParent.getY() - startInParent.getY());
		}
	}

	/**
	 * Identifies and stores all possible snapping locations for the given
	 * target part.
	 *
	 * @param snappedPart
	 *            The {@link IContentPart} that might be snapped.
	 */
	public void startSnapping(IContentPart<? extends Node> snappedPart) {
		// save the snapped part
		this.snappedPart = snappedPart;
		findNonConnectionLeafSnappingLocations();
		findConnectionLeafSnappingLocations();
	}

	/**
	 * Clears the snapping locations and the SnapToLocationModel.
	 */
	public void stopSnapping() {
		SnappingModel snappingModel = getAdaptable().getRoot().getViewer()
				.getAdapter(SnappingModel.class);
		snappingModel.setSnappingLocations(
				Collections.<SnappingLocation> emptyList());
		xLocations.clear();
		yLocations.clear();
	}

}
