/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link DynamicAnchor} computes anchor positions based on a reference
 * position per anchored and one reference position for the anchorage. The
 * anchoreds' reference positions are provided when
 * {@link #attach(AnchorKey, IAdaptable) attaching} an {@link AnchorKey}. The
 * computation is carried out by a {@link IComputationStrategy}. The default
 * computation strategy ({@link ProjectionStrategy}) will connect anchored and
 * anchorage reference position and compute the intersection with the outline of
 * the anchorage.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class DynamicAnchor extends AbstractAnchor {

	/**
	 * Abstract base class for {@link IComputationStrategy computation
	 * strategies} that are based on the outline of the anchorage reference
	 * geometry.
	 */
	public static abstract class AbstractComputationStrategy
			implements IComputationStrategy {

		/**
		 * Determines the outline of the given {@link IGeometry}, represented as
		 * a list of {@link ICurve}s.
		 *
		 * @param geometry
		 *            The anchorage geometry.
		 * @return A list of {@link ICurve}s representing the outline of the
		 *         given {@link IGeometry}.
		 */
		// TODO: Move to GEF4 Geometry?
		protected static List<ICurve> getOutlineSegments(IGeometry geometry) {
			if (geometry instanceof IShape) {
				return Collections
						.singletonList(((IShape) geometry).getOutline());
			} else if (geometry instanceof ICurve) {
				return Collections.singletonList((ICurve) geometry);
			} else if (geometry instanceof Path) {
				return ((Path) geometry).getOutlines();
			} else {
				throw new IllegalStateException(
						"The transformed geometry is neither an ICurve nor an IShape.");
			}
		}
	}

	/**
	 *
	 */
	public static class ChopBoxStrategy extends ProjectionStrategy {
		/**
		 * Computes the anchorage reference position within the coordinate
		 * system of the given {@link IGeometry}. For an {@link IShape}
		 * geometry, the center is used if it is contained within the shape,
		 * otherwise, the vertex nearest to the center is used as the reference
		 * position. For an {@link ICurve} geometry, the first point is used as
		 * the reference position.
		 *
		 * @param anchorage
		 *            The anchorage visual.
		 * @param geometryInLocal
		 *            The anchorage geometry within the local coordinate system
		 *            of the anchorage visual.
		 * @param anchoredReferencePointInAnchorageLocal
		 *            Refernce point of the anchored for which to determine the
		 *            anchorage reference point. Within the local coordinate
		 *            system of the anchorage.
		 * @return A position within the given {@link IGeometry}.
		 */
		public Point computeAnchorageReferencePointInLocal(Node anchorage,
				IGeometry geometryInLocal,
				Point anchoredReferencePointInAnchorageLocal) {
			if (geometryInLocal instanceof IShape) {
				IShape shape = (IShape) geometryInLocal;
				// in case of an IShape we can pick the bounds center if it
				// is contained, or the vertex nearest to the center point
				Point boundsCenterInLocal = geometryInLocal.getBounds()
						.getCenter();
				if (shape.contains(boundsCenterInLocal)) {
					return boundsCenterInLocal;
				} else {
					return null;
				}
			} else if (geometryInLocal instanceof ICurve) {
				return null;
			} else if (geometryInLocal instanceof Path) {
				// in case of a Path we can pick the vertex nearest
				// to the center point
				Point boundsCenterInLocal = geometryInLocal.getBounds()
						.getCenter();
				if (geometryInLocal.contains(boundsCenterInLocal)) {
					return boundsCenterInLocal;
				} else {
					return null;
				}
			} else {
				throw new IllegalArgumentException("Unknwon IGeometry: <"
						+ geometryInLocal.getClass() + ">.");
			}
		}

		/**
		 * Computes the anchorage reference position in scene coordinates, based
		 * on the given anchorage geometry.
		 *
		 * @see #computeAnchorageReferencePointInLocal(Node, IGeometry, Point)
		 * @param anchorage
		 *            The anchorage visual.
		 * @param geometryInLocal
		 *            The anchorage geometry within the coordinate system of the
		 *            anchorage visual.
		 * @param anchoredReferencePointInScene
		 *            The reference {@link Point} of the anchored for which the
		 *            anchorage reference {@link Point} is to be determined.
		 * @return The anchorage reference position.
		 */
		protected Point computeAnchorageReferencePointInScene(Node anchorage,
				IGeometry geometryInLocal,
				Point anchoredReferencePointInScene) {
			Point2D anchoredReferencePointInAnchorageLocal = anchorage
					.sceneToLocal(anchoredReferencePointInScene.x,
							anchoredReferencePointInScene.y);
			Point anchorageReferencePointInLocal = computeAnchorageReferencePointInLocal(
					anchorage, geometryInLocal,
					new Point(anchoredReferencePointInAnchorageLocal.getX(),
							anchoredReferencePointInAnchorageLocal.getY()));
			if (anchorageReferencePointInLocal == null) {
				return null;
			}
			return NodeUtils.localToScene(anchorage,
					anchorageReferencePointInLocal);
		}

		@Override
		public Point computePositionInScene(Node anchorage,
				IGeometry anchorageReferenceGeometryInLocal, Node anchored,
				Point anchoredReferencePointInLocal) {
			Point anchoredReferencePointInScene = NodeUtils
					.localToScene(anchored, anchoredReferencePointInLocal);

			Point anchorageReferencePointInScene = computeAnchorageReferencePointInScene(
					anchorage, anchorageReferenceGeometryInLocal,
					anchoredReferencePointInScene);

			if (anchorageReferencePointInScene == null) {
				return super.computePositionInScene(anchorage,
						anchorageReferenceGeometryInLocal, anchored,
						anchoredReferencePointInLocal);
			}

			IGeometry anchorageGeometryInScene = NodeUtils
					.localToScene(anchorage, anchorageReferenceGeometryInLocal);
			List<ICurve> anchorageOutlinesInScene = getOutlineSegments(
					anchorageGeometryInScene);

			Line referenceLineInScene = new Line(anchorageReferencePointInScene,
					anchoredReferencePointInScene);

			Point nearestProjectionInScene = null;
			double nearestDistance = 0d;
			for (ICurve anchorageOutlineInScene : anchorageOutlinesInScene) {
				// if the reference point is already on the outline, we may
				// directly use it
				if (anchorageOutlineInScene
						.contains(anchoredReferencePointInScene)) {
					return anchoredReferencePointInScene;
				}
				Point[] intersections = anchorageOutlineInScene
						.getIntersections(referenceLineInScene);
				if (intersections.length > 0) {
					Point nearestIntersection = Point.nearest(
							anchoredReferencePointInScene, intersections);
					double distance = anchoredReferencePointInScene
							.getDistance(nearestIntersection);
					if (nearestProjectionInScene == null
							|| distance < nearestDistance) {
						nearestProjectionInScene = nearestIntersection;
						nearestDistance = distance;
					}
				}
			}

			if (nearestProjectionInScene != null) {
				return nearestProjectionInScene;
			}
			return super.computePositionInScene(anchorage,
					anchorageReferenceGeometryInLocal, anchored,
					anchoredReferencePointInLocal);
		}
	}

	/**
	 * The {@link IComputationStrategy} is responsible for computing anchor
	 * positions based on an anchorage {@link Node}, an anchorage reference
	 * {@link IGeometry}, an anchored {@link Node}, and an anchored reference
	 * position ( {@link #computePositionInScene(Node, IGeometry, Node, Point)}
	 * ).
	 */
	public interface IComputationStrategy {

		/**
		 * Computes an anchor position based on the given anchorage visual,
		 * anchored visual, and anchored reference point.
		 *
		 * @param anchorage
		 *            The anchorage visual.
		 * @param anchorageReferenceGeometryInLocal
		 *            The anchorage reference geometry within the local
		 *            coordinate system of the anchorage visual.
		 * @param anchored
		 *            The anchored visual.
		 * @param anchoredReferencePointInLocal
		 *            The anchored reference point within the local coordinate
		 *            system of the anchored visual.
		 * @return The anchor position.
		 */
		Point computePositionInScene(Node anchorage,
				IGeometry anchorageReferenceGeometryInLocal, Node anchored,
				Point anchoredReferencePointInLocal);
	}

	/**
	 * An {@link IComputationStrategy} that computes anchor position by
	 * orthogonally projecting the respective anchored reference point to the
	 * outline of the anchorage reference geometry so that the respective point
	 * has minimal distance to the anchored reference point and resembles the
	 * same x- (vertical projection) or y-coordinate (horizontal projection).
	 */
	public static class OrthogonalProjectionStrategy
			extends AbstractComputationStrategy {

		/**
		 * Returns a point on the {@link ICurve} for which holds that its
		 * y-coordinate is the same as that of the given reference point, and
		 * its distance to the given reference point is minimal (i.e. there is
		 * no other point with the same y-coordinate that has a smaller
		 * distance), if such a point exists.
		 *
		 * @param curve
		 *            The {@link ICurve} to test. The returned {@link Point} has
		 *            to be contained by it.
		 *
		 * @param reference
		 *            The reference point which is used to determine the
		 *            distance.
		 * @return The point on the {@link ICurve} that is horizontally nearest
		 *         to the given reference point.
		 */
		private static Point getHorizontalProjection(ICurve curve,
				Point reference) {
			// Determine points on curve with same y-coordinate; by computing a
			// line with the respective y-coordinate inside its bounds; then
			// computing the nearest intersection on the curve
			Rectangle bounds = curve.getBounds();
			Line line = new Line(bounds.getX(), reference.y,
					bounds.getX() + bounds.getWidth(), reference.y);
			Point projection = getNearestOrthogonalProjection(curve, reference,
					line);
			// a horizontal projection is constant in y, therefore, we can
			// ensure that the projection has the same y coordinate as the
			// reference
			if (projection != null) {
				projection.y = reference.y;
			}
			return projection;
		}

		/**
		 * Returns the nearest projection onto the given geometry's rectangular
		 * bounds. Will ensure that parameter values on the respective outline
		 * fall into 0.2 .. 0.8.
		 *
		 * @param g
		 *            The {@link IGeometry} whose bounds to use.
		 * @param p
		 *            The {@link Point} to project.
		 * @return The nearest point on the outline segment that lies within the
		 *         parameter range 0.2 .. 0.8.
		 */
		// TODO: Make private
		public static Point getNearestBoundsProjection(IGeometry g, Point p) {
			Line[] outlineSegments = g.getBounds().getOutlineSegments();
			Point nearestProjection = null;
			double nearestDistance = 0;
			for (Line l : outlineSegments) {
				Point projection = l.getProjection(p);
				double distance = p.getDistance(projection);
				if (nearestProjection == null || distance < nearestDistance) {
					nearestDistance = distance;
					nearestProjection = projection;
				}
			}
			return nearestProjection;
		}

		private static Point getNearestOrthogonalProjection(ICurve curve,
				Point reference, Line line) {
			if (curve.overlaps(line)) {
				ICurve[] overlaps = curve.getOverlaps(line);
				// XXX: All overlaps have to be lines since a line can only
				// overlap with another line. As such, it is sufficient to check
				// the start and end points of the overlaps.
				Point nearest = null;
				double distance = 0;
				for (ICurve overlap : overlaps) {
					Point currentNearest = Point.nearest(reference,
							new Point[] { overlap.getP1(), overlap.getP2() });
					double currentDistance = reference
							.getDistance(currentNearest);
					if (nearest == null || currentDistance < distance) {
						nearest = currentNearest;
						distance = currentDistance;
					}
				}
				return nearest;
			} else if (curve.intersects(line)) {
				Point nearest = Point.nearest(reference,
						curve.getIntersections(line));
				return nearest;
			}
			// no point found for the given y-coordinate
			return null;
		}

		/**
		 * Returns a point on the {@link ICurve} for which holds that its
		 * x-coordinate or y-coordinate is the same as that of the given
		 * reference point, and its distance to the given reference point is
		 * minimal (i.e. there is no other point with the same x-coordinate or
		 * y-coordinate that has a smaller distance).
		 *
		 * @param curve
		 *            The {@link ICurve} to test. The returned {@link Point} has
		 *            to be contained by it.
		 *
		 * @param reference
		 *            The reference point which is used to determine the
		 *            distance.
		 * @return The point on the {@link ICurve} that is horizontally or
		 *         vertically nearest to the given reference point.
		 */
		private static Point getOrthogonalProjection(ICurve curve,
				Point reference) {
			Point nearestHorizonalProjection = getHorizontalProjection(curve,
					reference);
			if (nearestHorizonalProjection == null) {
				// if there is no horizontal projection, the vertical one has to
				// be minimal (if it exists)
				return getVerticalProjection(curve, reference);
			} else {
				Point nearestVerticalProjection = getVerticalProjection(curve,
						reference);
				if (nearestVerticalProjection == null) {
					// if there is no vertical projection, the horizontal one
					// has to be minimal
					return nearestHorizonalProjection;
				} else {
					// compute whether horizontal or vertical is minimal
					double horizontalDistance = nearestHorizonalProjection
							.getDistance(reference);
					double verticalDistance = nearestVerticalProjection
							.getDistance(reference);
					if (horizontalDistance <= verticalDistance) {
						return nearestHorizonalProjection;
					}
					return nearestVerticalProjection;
				}
			}
		}

		/**
		 * Returns a point on the {@link ICurve} for which holds that its
		 * x-coordinate is the same as that of the given reference point, and
		 * its distance to the given reference point is minimal (i.e. there is
		 * no other point with the same x-coordinate that has a smaller
		 * distance), if such a point exists.
		 *
		 * @param curve
		 *            The {@link ICurve} to test. The returned {@link Point} has
		 *            to be contained by it.
		 *
		 * @param reference
		 *            The reference point which is used to determine the
		 *            distance.
		 * @return The point on the {@link ICurve} that is vertically nearest to
		 *         the given reference point.
		 */
		private static Point getVerticalProjection(ICurve curve,
				Point reference) {
			// Determine points on curve with same x-coordinate; by computing a
			// line with the respective x-coordinate inside its bounds; then
			// computing the nearest intersection on the curve
			Rectangle bounds = curve.getBounds();
			Line line = new Line(reference.x, bounds.getY(), reference.x,
					bounds.getY() + bounds.getHeight());
			Point projection = getNearestOrthogonalProjection(curve, reference,
					line);
			// a vertical projection is constant in x, therefore, we can
			// ensure that the projection has the same x coordinate as the
			// reference
			if (projection != null) {
				projection.x = reference.x;
			}
			return projection;
		}

		@Override
		public Point computePositionInScene(Node anchorage,
				IGeometry anchorageReferenceGeometryInLocal, Node anchored,
				Point anchoredReferencePointInLocal) {
			// anchored reference point
			Point anchoredReferencePointInScene = NodeUtils
					.localToScene(anchored, anchoredReferencePointInLocal);

			// anchorage reference geometry
			IGeometry anchorageReferenceGeometryInScene = NodeUtils
					.localToScene(anchorage, anchorageReferenceGeometryInLocal);

			if (anchoredReferencePointInLocal.y == -88
					&& anchorageReferenceGeometryInScene.getBounds()
							.getHeight() > 20) {
				System.out.println("anchored reference point = "
						+ anchoredReferencePointInLocal);
				System.out.println("anchorage reference geometry bounds = "
						+ anchorageReferenceGeometryInScene.getBounds());
			}

			// compute horizontal or vertical projection on outline segments.
			List<ICurve> anchorageReferenceGeometryOutlineSegmentsInScene = getOutlineSegments(
					anchorageReferenceGeometryInScene);

			Point nearestOrthogonalProjectionInScene = null;
			double nearestOrthogonalProjectionDistance = Double.MAX_VALUE;
			for (ICurve segment : anchorageReferenceGeometryOutlineSegmentsInScene) {
				// determine nearest orthogonal projection of each curve
				Point projection = getOrthogonalProjection(segment,
						anchoredReferencePointInScene);
				System.out.println(":: " + segment);
				System.out.println("::-> " + projection);
				if (projection != null) {
					double distance = projection
							.getDistance(anchoredReferencePointInScene);
					if (nearestOrthogonalProjectionInScene == null
							|| distance < nearestOrthogonalProjectionDistance) {
						nearestOrthogonalProjectionInScene = projection;
						nearestOrthogonalProjectionDistance = distance;
					}
				}
			}

			if (nearestOrthogonalProjectionInScene != null) {
				System.out.println("nearest projection = "
						+ nearestOrthogonalProjectionInScene);
				return nearestOrthogonalProjectionInScene;
			} else {
				System.out.println("BOUNDS FALLBACK");
				// TODO: fall back to closes point (we could extend
				// ProjectionStrategy and call super here)
				return getNearestBoundsProjection(
						anchorageReferenceGeometryInScene,
						anchoredReferencePointInScene);
			}
		}
	}

	/**
	 * An {@link IComputationStrategy} that computes anchor position by
	 * projecting the respective anchored reference point to the outline of the
	 * anchorage reference geometry so that the respective point has minimal
	 * distance to the anchored reference point.
	 *
	 * In detail, the computation is done as follows:
	 * <ol>
	 * <li>Compute the anchorage outlines (in scene) based on the anchorage
	 * reference geometry,using {@link #getOutlineSegments(IGeometry)}.</li>
	 * <li>Transform the given anchored reference point to scene coordinates.
	 * </li>
	 * <li>Project the anchored reference point (in scene) onto the anchorage
	 * outlines.</li>
	 * <li>Return the nearest projection to the anchored reference point.</li>
	 * </ol>
	 */
	public static class ProjectionStrategy extends AbstractComputationStrategy {
		/**
		 * Computes the anchorage reference position in scene coordinates, based
		 * on the given anchorage outlines and the given anchored reference
		 * point.
		 *
		 * @param anchorageOutlinesInScene
		 *            A list of {@link ICurve}s that describe the outline of the
		 *            anchorage.
		 * @param anchoredReferencePointInScene
		 *            The reference {@link Point} of the anchored for which the
		 *            anchorage reference {@link Point} is to be determined.
		 * @return The anchorage reference position.
		 */
		protected Point computeNearestProjectionInScene(
				List<ICurve> anchorageOutlinesInScene,
				Point anchoredReferencePointInScene) {
			Point[] projections = new Point[anchorageOutlinesInScene.size()];
			for (int i = 0; i < anchorageOutlinesInScene.size(); i++) {
				ICurve c = anchorageOutlinesInScene.get(i);
				projections[i] = c.getProjection(anchoredReferencePointInScene);
			}
			return Point.nearest(anchoredReferencePointInScene, projections);
		}

		@Override
		public Point computePositionInScene(Node anchorage,
				IGeometry anchorageReferenceGeometryInLocal, Node anchored,
				Point anchoredReferencePointInLocal) {
			// determine anchorage geometry in scene
			IGeometry anchorageGeometryInScene = NodeUtils
					.localToScene(anchorage, anchorageReferenceGeometryInLocal);

			// determine anchorage outlines in scene
			List<ICurve> anchorageOutlinesInScene = getOutlineSegments(
					anchorageGeometryInScene);

			// transform anchored reference point to scene
			Point anchoredReferencePointInScene = NodeUtils
					.localToScene(anchored, anchoredReferencePointInLocal);

			// compute nearest projection of the anchored reference point on the
			// anchorage outlines
			return computeNearestProjectionInScene(anchorageOutlinesInScene,
					anchoredReferencePointInScene);
		}
	}

	/**
	 * The name of the {@link #defaultComputationStrategyProperty() computation
	 * strategy property}.
	 */
	public static final String DEFAULT_COMPUTATION_STRATEGY_PROPERTY = "defaultComputationStrategy";

	private static final IComputationStrategy DEFAULT_COMPUTATION_STRATEGY = new ChopBoxStrategy();
	private ObjectProperty<IComputationStrategy> defaultComputationStrategyProperty;
	private ReadOnlyMapWrapperEx<AnchorKey, IComputationStrategy> computationStrategyProperty = new ReadOnlyMapWrapperEx<>(
			FXCollections
					.<AnchorKey, IComputationStrategy> observableHashMap());
	private ReadOnlyMapWrapperEx<AnchorKey, Point> anchoredReferencePointsProperty = new ReadOnlyMapWrapperEx<>(
			FXCollections.<AnchorKey, Point> observableHashMap());

	private MapChangeListener<AnchorKey, Point> referencePointChangeListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			if (change.wasAdded()) {
				// prevent null from being put into the map
				if (change.getKey() == null) {
					throw new IllegalStateException(
							"Attempt to put <null> key into reference point map!");
				}
				if (change.getValueAdded() == null) {
					throw new IllegalStateException(
							"Attempt to put <null> value into reference point map!");
				}
				if (getKeys().containsKey(change.getKey().getAnchored())
						&& getKeys().get(change.getKey().getAnchored())
								.contains(change.getKey())) {
					updatePosition(change.getKey());
				}
			}
		}
	};

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual.
	 * Uses the default computation strategy ( {@link ProjectionStrategy} ).
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 */
	public DynamicAnchor(Node anchorage) {
		super(anchorage);
		anchoredReferencePointsProperty
				.addListener(referencePointChangeListener);
	}

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual
	 * using the given {@link IComputationStrategy}.
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} to use.
	 */
	public DynamicAnchor(Node anchorage,
			IComputationStrategy computationStrategy) {
		super(anchorage);
		setDefaultComputationStrategy(computationStrategy);
		anchoredReferencePointsProperty
				.addListener(referencePointChangeListener);
	}

	/**
	 * Provides a {@link ReadOnlyMapProperty} that stores positions (in the
	 * local coordinate system of the anchored {@link Node}) for all attached
	 * {@link AnchorKey}s.
	 *
	 * @return A {@link ReadOnlyMapProperty} that stores positions (in the local
	 *         coordinate system of the anchored {@link Node}) for all attached
	 *         {@link AnchorKey}s.
	 */
	public ReadOnlyMapProperty<AnchorKey, Point> anchoredReferencePointsProperty() {
		return anchoredReferencePointsProperty.getReadOnlyProperty();
	}

	/**
	 * Returns a {@link ReadOnlyMapProperty} that stores the individual
	 * {@link IComputationStrategy} for each {@link AnchorKey}.
	 *
	 * @return A {@link ReadOnlyMapProperty} that stores the individual
	 *         {@link IComputationStrategy} for each {@link AnchorKey}.
	 */
	public ReadOnlyMapProperty<AnchorKey, IComputationStrategy> computationStrategyProperty() {
		return computationStrategyProperty.getReadOnlyProperty();
	}

	/**
	 * Recomputes the position for the given attached {@link AnchorKey}.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which to compute an anchor position.
	 */
	@Override
	protected Point computePosition(AnchorKey key) {
		return computePosition(key.getAnchored(),
				getAnchoredReferencePoint(key), getComputationStrategy(key));
	}

	/**
	 * Computes the point of intersection between the outline of the anchorage
	 * reference shape and the line through the reference points of anchorage
	 * and anchored.
	 *
	 * @param anchored
	 *            The to be anchored {@link Node} for which the anchor position
	 *            is to be determined.
	 * @param anchoredReferencePointInLocal
	 *            A reference {@link Point} used for calculation of the anchor
	 *            position, provided within the local coordinate system of the
	 *            to be anchored {@link Node}.
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} that is used to compute the
	 *            position based on the given reference point.
	 * @return Point The anchor position within the local coordinate system of
	 *         the to be anchored {@link Node}.
	 */
	public Point computePosition(Node anchored,
			Point anchoredReferencePointInLocal,
			IComputationStrategy computationStrategy) {
		return FX2Geometry.toPoint(anchored.sceneToLocal(Geometry2FX.toFXPoint(
				computationStrategy.computePositionInScene(getAnchorage(),
						getAnchorageReferenceGeometry(), anchored,
						anchoredReferencePointInLocal))));
	}

	/**
	 * Returns a writable object property for the {@link IComputationStrategy}
	 * used by this {@link DynamicAnchor}.
	 *
	 * @return A writable property.
	 */
	public ObjectProperty<IComputationStrategy> defaultComputationStrategyProperty() {
		if (defaultComputationStrategyProperty == null) {
			defaultComputationStrategyProperty = new SimpleObjectProperty<>(
					this, DEFAULT_COMPUTATION_STRATEGY_PROPERTY,
					DEFAULT_COMPUTATION_STRATEGY);
		}
		return defaultComputationStrategyProperty;
	}

	/**
	 * Returns the anchorage reference {@link IGeometry geometry} that is to be
	 * used for computations by this {@link DynamicAnchor}'s
	 * {@link IComputationStrategy computation strategy}, specified within the
	 * local coordinate system of the anchorage.
	 *
	 * @return The anchorage reference geometry to be used for computations,
	 *         which by default is the shape's outline geometry.
	 */
	public IGeometry getAnchorageReferenceGeometry() {
		return NodeUtils.getShapeOutline(getAnchorage());
	}

	/**
	 * Returns the reference {@link Point} for the given {@link AnchorKey}.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which to determine the reference
	 *            {@link Point}.
	 * @return The reference {@link Point} for the given {@link AnchorKey} in
	 *         the local coordinate system of the key's
	 *         {@link AnchorKey#getAnchored() anchored}.
	 */
	public Point getAnchoredReferencePoint(AnchorKey key) {
		Point referencePoint = anchoredReferencePointsProperty().get(key);
		if (referencePoint == null) {
			referencePoint = new Point(0, 0);
		}
		return referencePoint;
	}

	/**
	 * Returns the {@link IComputationStrategy} that is used by this
	 * {@link DynamicAnchor} to compute the position for the given
	 * {@link AnchorKey}. If no {@link IComputationStrategy} was explicitly set
	 * for the given {@link AnchorKey}, then the
	 * {@link #getDefaultComputationStrategy()} is returned.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which the
	 *            {@link IComputationStrategy} is determined.
	 * @return The {@link IComputationStrategy} that is used by this
	 *         {@link DynamicAnchor} to compute the position for the given
	 *         {@link AnchorKey}.
	 */
	public IComputationStrategy getComputationStrategy(AnchorKey key) {
		if (computationStrategyProperty.containsKey(key)) {
			return computationStrategyProperty.get(key);
		}
		return getDefaultComputationStrategy();
	}

	/**
	 * Returns the default {@link IComputationStrategy} used by this
	 * {@link DynamicAnchor} when no {@link IComputationStrategy} is explicitly
	 * set for an {@link AnchorKey}.
	 *
	 * @return The default {@link IComputationStrategy}.
	 */
	public IComputationStrategy getDefaultComputationStrategy() {
		return defaultComputationStrategyProperty == null
				? DEFAULT_COMPUTATION_STRATEGY
				: defaultComputationStrategyProperty().get();
	}

	/**
	 * Sets the anchored reference point for the given {@link AnchorKey}.
	 *
	 * @param key
	 *            The key for which to set the reference point.
	 * @param referencePoint
	 *            The reference point to set.
	 */
	public void setAnchoredReferencePoint(AnchorKey key, Point referencePoint) {
		anchoredReferencePointsProperty.put(key, referencePoint);
	}

	/**
	 * Sets the given {@link IComputationStrategy} to be used by this
	 * {@link DynamicAnchor} to compute the position for the given
	 * {@link AnchorKey}.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which the given
	 *            {@link IComputationStrategy} will be used to compute its
	 *            position.
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} that will be used to compute
	 *            positions for the given {@link AnchorKey}.
	 */
	public void setComputationStrategy(AnchorKey key,
			IComputationStrategy computationStrategy) {
		if (computationStrategy == null) {
			computationStrategyProperty.remove(key);
		} else {
			computationStrategyProperty.put(key, computationStrategy);
		}
	}

	/**
	 * Sets the given {@link IComputationStrategy} for this
	 * {@link DynamicAnchor} as the default strategy.
	 *
	 * @param computationStrategy
	 *            The new default {@link IComputationStrategy}.
	 */
	public void setDefaultComputationStrategy(
			IComputationStrategy computationStrategy) {
		defaultComputationStrategyProperty().set(computationStrategy);
	}

}
