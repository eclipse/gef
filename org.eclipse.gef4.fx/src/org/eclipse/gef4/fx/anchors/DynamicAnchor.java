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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Path.Segment;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
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
// TODO: Find an appropriate name for this (outline anchor or shape anchor or
// perimeter anchor, or dynamic anchor)
// It has nothing to do with a ChopBox, so this does not seem to be intuitive.
public class DynamicAnchor extends AbstractAnchor {

	/**
	 * Abstract base class for {@link IComputationStrategy computation
	 * strategies} that are based on the outline of the anchorage reference
	 * geometry.
	 */
	public static abstract class AbstractComputationStrategy
			implements IComputationStrategy {
		/**
		 * Creates a geometry representing the outline of the given {@link Node}
		 * .
		 *
		 * @param node
		 *            The node to infer an outline geometry for.
		 * @return An {@link IGeometry} from which the outline may be retrieved.
		 */
		public static IGeometry getOutlineGeometry(Node node) {
			IGeometry geometry = null;
			// TODO: Refactor that control flow is hanled via exceptions
			try {
				geometry = NodeUtils.getGeometricOutline(node);
			} catch (IllegalArgumentException e) {
			}

			// resize to layout-bounds to include stroke if not a curve
			if (geometry instanceof IShape) {
				return NodeUtils.getResizedToShapeBounds(node, geometry);
			}

			// fall back to layout-bounds
			if (geometry == null) {
				geometry = FX2Geometry.toRectangle(node.getLayoutBounds());
			}
			return geometry;
		}

		/**
		 * Determines the outline of the given {@link IGeometry}, represented as
		 * a list of {@link ICurve}s.
		 *
		 * @param geometry
		 *            The anchorage geometry.
		 * @return A list of {@link ICurve}s representing the outline of the
		 *         given {@link IGeometry}.
		 */
		// TODO: Move to GEF4 Geometry
		public static List<ICurve> getOutlineSegments(IGeometry geometry) {
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

		/**
		 * Determines the anchorage reference geometry based on the given
		 * anchorage visual. For an {@link GeometryNode}, the corresponding
		 * geometry is returned, in case of an {@link IShape} resized to include
		 * the stroke. Otherwise, a {@link Rectangle} representing the
		 * layout-bounds of the visual is returned.
		 *
		 * @param anchorage
		 *            The anchorage visual.
		 * @return The anchorage reference geometry within the local coordinate
		 *         system of the given anchorage visual.
		 */
		// TODO: move to somewhere appropriate
		protected IGeometry getAnchorageReferenceGeometryInLocal(
				Node anchorage) {
			return getOutlineGeometry(anchorage);
		}

		/**
		 * Returns the anchorage reference geometry within the scene coordinate
		 * system.
		 *
		 * @param anchorage
		 *            The anchorage visual.
		 * @return The anchorage reference geometry within the global scene
		 *         coordinate system.
		 */
		// TODO: Move to somewhere appropriate
		protected IGeometry getAnchorageReferenceGeometryInScene(
				Node anchorage) {
			return NodeUtils.localToScene(anchorage,
					getAnchorageReferenceGeometryInLocal(anchorage));
		}
	}

	/**
	 * The {@link IComputationStrategy} is responsible for computing anchor
	 * positions based on an anchorage {@link Node}, an anchored {@link Node},
	 * and an anchored reference position (
	 * {@link #computePositionInScene(Node, Node, Point)}).
	 */
	public interface IComputationStrategy {

		/**
		 * Computes an anchor position based on the given anchorage visual,
		 * anchored visual, and anchored reference point.
		 *
		 * @param anchorage
		 *            The anchorage visual.
		 * @param anchored
		 *            The anchored visual.
		 * @param anchoredReferencePointInLocal
		 *            The anchored reference point within the local coordinate
		 *            system of the anchored visual.
		 * @return The anchor position.
		 */
		Point computePositionInScene(Node anchorage, Node anchored,
				Point anchoredReferencePointInLocal);

	}

	/**
	 * A {@link IReferencePointProvider} needs to be provided as default adapter
	 * (see {@link AdapterKey#get(Class)}) on the {@link IAdaptable} info that
	 * gets passed into {@link DynamicAnchor#attach(AnchorKey, IAdaptable)} and
	 * {@link DynamicAnchor#detach(AnchorKey, IAdaptable)}. The
	 * {@link IReferencePointProvider} has to provide a reference point for each
	 * {@link AdapterKey} that is attached to the {@link DynamicAnchor}. It will
	 * be used when computing anchor positions for the respective
	 * {@link AnchorKey}.
	 *
	 * @author anyssen
	 *
	 */
	public interface IReferencePointProvider {

		/**
		 * A simple {@link IReferencePointProvider} implementation that allows
		 * to statically set reference points for {@link AnchorKey}s.
		 *
		 */
		public class Impl implements IReferencePointProvider {

			private ReadOnlyMapWrapper<AnchorKey, Point> referencePointProperty = new ReadOnlyMapWrapperEx<>(
					FXCollections.<AnchorKey, Point> observableHashMap());

			/**
			 * Sets or updates the reference point for the given
			 * {@link AnchorKey}.
			 *
			 * @param anchorKey
			 *            The {@link AnchorKey} for which the reference point is
			 *            to be set or updated.
			 * @param referencePoint
			 *            The new reference point to set.
			 */
			public void put(AnchorKey anchorKey, Point referencePoint) {
				referencePointProperty.put(anchorKey, referencePoint);
			}

			@Override
			public ReadOnlyMapProperty<AnchorKey, Point> referencePointProperty() {
				return referencePointProperty.getReadOnlyProperty();
			}
		}

		/**
		 * Provides a {@link ReadOnlyMapProperty} that stores positions (in the
		 * local coordinate system of the anchored {@link Node}) for all
		 * attached {@link AnchorKey}s.
		 *
		 * @return A {@link ReadOnlyMapProperty} that stores positions (in the
		 *         local coordinate system of the anchored {@link Node}) for all
		 *         attached {@link AnchorKey}s.
		 */
		public abstract ReadOnlyMapProperty<AnchorKey, Point> referencePointProperty();

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

		private static final double TOLERANCE = 0.2;

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
			// line
			// with the respective y-coordinate inside its bounds; then
			// computing
			// the nearest intersection on the curve
			Rectangle bounds = curve.getBounds();
			Line line = new Line(bounds.getX(), reference.y,
					bounds.getX() + bounds.getWidth(), reference.y);
			return getNearestOrthogonalProjection(curve, reference, line);
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
				double parameter = l.getParameterAt(projection);
				if (parameter < TOLERANCE) {
					parameter = TOLERANCE;
				} else if (parameter > 1 - TOLERANCE) {
					parameter = 1 - TOLERANCE;
				}
				projection = l.get(parameter);
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
				// TODO: respect TOLERANCE
				return nearest;
			} else if (curve.intersects(line)) {
				// TODO: respect TOLERANCE
				return Point.nearest(reference, curve.getIntersections(line));
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
				// be
				// minimal (if it exists)
				return getVerticalProjection(curve, reference);
			} else {
				Point nearestVerticalProjection = getVerticalProjection(curve,
						reference);
				if (nearestVerticalProjection == null) {
					// if there is no vertical projection, the horizontal one
					// has to
					// be minimal
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
			return getNearestOrthogonalProjection(curve, reference, line);
		}

		@Override
		public Point computePositionInScene(Node anchorage, Node anchored,
				Point anchoredReferencePointInLocal) {
			// anchored reference point
			Point anchoredReferencePointInScene = NodeUtils
					.localToScene(anchored, anchoredReferencePointInLocal);

			// anchorage reference geometry
			IGeometry anchorageReferenceGeometryInScene = getAnchorageReferenceGeometryInScene(
					anchorage);

			// compute horizontal or vertical projection on outline segments.
			List<ICurve> anchorageReferenceGeometryOutlineSegmentsInScene = getOutlineSegments(
					anchorageReferenceGeometryInScene);

			Point nearestOrthogonalProjectionInScene = null;
			double nearestOrthogonalProjectionDistance = Double.MAX_VALUE;
			for (ICurve segment : anchorageReferenceGeometryOutlineSegmentsInScene) {
				// determine nearest orthogonal projection of each curve
				Point projection = getOrthogonalProjection(segment,
						anchoredReferencePointInScene);
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
				return nearestOrthogonalProjectionInScene;
			} else {
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
	 * <li>Compute the anchorage reference geometry based on its visual (
	 * {@link #getAnchorageReferenceGeometryInLocal(Node)}).</li>
	 * <li>Compute an anchorage reference position based on its geometry (
	 * {@link #computeAnchorageReferencePointInLocal(Node, IGeometry, Point)} ).
	 * </li>
	 * <li>Transform this reference position into the coordinate system of the
	 * scene (
	 * {@link #computeAnchorageReferencePointInScene(Node, IGeometry, Point)} ).
	 * </li>
	 * <li>Connect anchored and anchorage reference positions.</li>
	 * <li>Compute the intersection of the connection and the outline of the
	 * anchorage geometry ({@link #getOutlineSegments(IGeometry)}).</li>
	 * </ol>
	 */
	// TODO: Refactor algorithm to no longer use an internal reference point
	// (see bug #488353).
	public static class ProjectionStrategy extends AbstractComputationStrategy {

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
		// TODO: remove this method. The reference point should not be required.
		// Instead the nearest projection should be computed as outline in bug
		// #488353.
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
					Point nearestVertex = getNearestVertex(boundsCenterInLocal,
							shape);
					if (nearestVertex != null) {
						return nearestVertex;
					} else {
						throw new IllegalArgumentException(
								"The given IShape does not provide any vertices.");
					}
				}
			} else if (geometryInLocal instanceof ICurve) {
				return getNearestVertex(anchoredReferencePointInAnchorageLocal,
						(ICurve) geometryInLocal);
			} else if (geometryInLocal instanceof Path) {
				// in case of a Path we can pick the vertex nearest
				// to the center point
				Point boundsCenterInLocal = geometryInLocal.getBounds()
						.getCenter();
				if (geometryInLocal.contains(boundsCenterInLocal)) {
					return boundsCenterInLocal;
				} else {
					Point nearestVertex = getNearestVertex(boundsCenterInLocal,
							(Path) geometryInLocal);
					if (nearestVertex != null) {
						return nearestVertex;
					} else {
						throw new IllegalArgumentException(
								"The given Path does not provide any vertices.");
					}
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
			return NodeUtils.localToScene(anchorage,
					computeAnchorageReferencePointInLocal(anchorage,
							geometryInLocal,
							new Point(
									anchoredReferencePointInAnchorageLocal
											.getX(),
									anchoredReferencePointInAnchorageLocal
											.getY())));
		}

		@Override
		public Point computePositionInScene(Node anchorage, Node anchored,
				Point anchoredReferencePointInLocal) {
			IGeometry anchorageReferenceGeometryInLocal = getAnchorageReferenceGeometryInLocal(
					anchorage);

			Point anchoredReferencePointInScene = NodeUtils
					.localToScene(anchored, anchoredReferencePointInLocal);

			Point anchorageReferencePointInScene = computeAnchorageReferencePointInScene(
					anchorage, anchorageReferenceGeometryInLocal,
					anchoredReferencePointInScene);

			Line referenceLineInScene = new Line(anchorageReferencePointInScene,
					anchoredReferencePointInScene);

			IGeometry anchorageGeometryInScene = NodeUtils
					.localToScene(anchorage, anchorageReferenceGeometryInLocal);
			List<ICurve> anchorageOutlinesInScene = getOutlineSegments(
					anchorageGeometryInScene);

			Point nearestProjectionInScene = null;
			double nearestDistance = 0d;
			for (ICurve anchorageOutlineInScene : anchorageOutlinesInScene) {
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

			// in case of emergency, return the anchorage reference point
			return anchorageReferencePointInScene;
		}

		/**
		 * Determines the vertex of the given {@link ICurve} which is nearest to
		 * the given center {@link Point}. The vertices for the {@link ICurve}
		 * are computed via its bezier curve approximation. For all
		 * {@link BezierCurve}s that are part of the approximation, the start
		 * point, middle point, and end point is considered as a vertex.
		 *
		 * @param boundsCenter
		 *            The ideal anchorage reference position.
		 * @param curve
		 *            The anchorage geometry.
		 * @return The <i>curve</i> vertex nearest to the given
		 *         <i>boundsCenter</i>.
		 */
		protected Point getNearestVertex(Point boundsCenter, ICurve curve) {
			Set<Point> vertices = new HashSet<>();
			// put start, mid, end points of beziers into vertices list
			BezierCurve[] beziers = curve.toBezier();
			for (BezierCurve bezier : beziers) {
				// TODO implement algorithm to determine nearest point on
				// curve
				for (double t = 0; t <= 1d; t += 1 / 64d) {
					vertices.add(bezier.get(t));
				}
			}
			if (vertices.isEmpty()) {
				// could not find vertices
				return null;
			}
			// return vertex nearest to bounds center
			Point[] vi = vertices.toArray(new Point[] {});
			Point nearest = vi[0];
			double nearestDistance = boundsCenter.getDistance(nearest);
			for (int i = 1; i < vi.length; i++) {
				double distance = boundsCenter.getDistance(vi[i]);
				if (distance < nearestDistance) {
					nearest = vi[i];
					nearestDistance = distance;
				}
			}
			return nearest;
		}

		/**
		 * Determines the vertex of the given {@link IShape} which is nearest to
		 * the given center {@link Point}.
		 *
		 * @param boundsCenter
		 *            The ideal anchorage reference position.
		 * @param shape
		 *            The anchorage geometry.
		 * @return The <i>shape</i> vertex nearest to the given
		 *         <i>boundsCenter</i>.
		 */
		protected Point getNearestVertex(Point boundsCenter, IShape shape) {
			ICurve[] outlineSegments = shape.getOutlineSegments();
			if (outlineSegments.length == 0) {
				return null;
			}
			// find vertex nearest to boundsCenter
			Point nearestVertex = outlineSegments[0].getP1();
			double minDistance = boundsCenter.getDistance(nearestVertex);
			for (int i = 1; i < outlineSegments.length; i++) {
				Point v = outlineSegments[i].getP1();
				double d = boundsCenter.getDistance(v);
				if (d < minDistance) {
					nearestVertex = v;
					minDistance = d;
				}
			}
			return nearestVertex;
		}

		/**
		 * Determines the vertex of the given {@link Path} which is nearest to
		 * the given center {@link Point}.
		 *
		 * @param boundsCenter
		 *            The ideal anchorage reference position.
		 * @param path
		 *            The anchorage geometry.
		 * @return The vertex of the given {@link Path} that is nearest to the
		 *         given {@link Point}.
		 */
		protected Point getNearestVertex(Point boundsCenter, Path path) {
			Segment[] segments = path.getSegments();
			if (segments.length < 1) {
				return null;
			}
			Point nearestVertex = null;
			double minDistance = 0d;
			for (int i = 0; i < segments.length; i++) {
				Point[] points = segments[i].getPoints();
				if (points.length > 0) {
					if (nearestVertex == null) {
						nearestVertex = points[0].getCopy();
						minDistance = boundsCenter.getDistance(nearestVertex);
					} else {
						double distance = boundsCenter.getDistance(points[0]);
						if (distance < minDistance) {
							nearestVertex = points[0].getCopy();
							minDistance = distance;
						}
					}
				}
			}
			return null;
		}
	}

	/**
	 * The name of the {@link #computationStrategyProperty() computation
	 * strategy property}.
	 */
	public static final String COMPUTATION_STRATEGY_PROPERTY = "computationStrategy";

	private static final IComputationStrategy DEFAULT_COMPUTATION_STRATEGY = new ProjectionStrategy();

	private Map<AnchorKey, IReferencePointProvider> anchoredReferencePointProviders = new HashMap<>();

	private MapChangeListener<AnchorKey, Point> anchoredReferencePointsChangeListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			if (change.wasAdded()) {
				// Do some defensive checks here. However, if we run into null
				// key or value here, this will be an inconsistency of the
				// ChopBoxHelper#referencePointProperty()
				if (change.getKey() == null) {
					throw new IllegalStateException(
							"Attempt to put <null> key into reference point map!");
				}
				if (change.getValueAdded() == null) {
					throw new IllegalStateException(
							"Attempt to put <null> value into reference point map!");
				}
				if (anchoredReferencePointProviders
						.containsKey(change.getKey())) {
					// only recompute position, if one of our own keys changed
					// (ChopBoxHelper#referencePointProperty() may contain
					// AnchorKeys registered at other anchors as well)
					updatePosition(change.getKey());
				}
			}
		}
	};

	private ObjectProperty<IComputationStrategy> computationStrategyProperty;

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual.
	 * Uses the default computation strategy ( {@link ProjectionStrategy} ).
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 */
	public DynamicAnchor(Node anchorage) {
		super(anchorage);
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
		setComputationStrategy(computationStrategy);
	}

	/**
	 * Attaches the given {@link AnchorKey} to this {@link DynamicAnchor}.
	 * Requires that an {@link IReferencePointProvider} can be obtained from the
	 * passed in {@link IAdaptable}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be attached.
	 * @param info
	 *            An {@link IAdaptable}, which will be used to obtain an
	 *            {@link IReferencePointProvider} that provides reference points
	 *            for this {@link DynamicAnchor}.
	 *
	 */
	@Override
	public void attach(AnchorKey key, IAdaptable info) {
		IReferencePointProvider referencePointProvider = info
				.getAdapter(IReferencePointProvider.class);
		if (referencePointProvider == null) {
			throw new IllegalArgumentException(
					"No IReferencePointProvider could be obtained via info.");
		}

		// we need to keep track of it, otherwise we will not be able to access
		// the reference point information (in case of other changes).
		anchoredReferencePointProviders.put(key, referencePointProvider);

		// will enforce a re-computation of positions, so we need to have
		// obtained the helper beforehand.
		super.attach(key, info);

		// add listener to reference point changes
		referencePointProvider.referencePointProperty()
				.addListener(anchoredReferencePointsChangeListener);
	}

	/**
	 * Returns a writable object property for the {@link IComputationStrategy}
	 * used by this {@link DynamicAnchor}.
	 *
	 * @return A writable property.
	 */
	public ObjectProperty<IComputationStrategy> computationStrategyProperty() {
		if (computationStrategyProperty == null) {
			computationStrategyProperty = new SimpleObjectProperty<>(this,
					COMPUTATION_STRATEGY_PROPERTY,
					DEFAULT_COMPUTATION_STRATEGY);
		}
		return computationStrategyProperty;
	}

	/**
	 * Recomputes the position for the given attached {@link AnchorKey} by
	 * retrieving a reference position via the {@link IReferencePointProvider}
	 * that was obtained when attaching the {@link AnchorKey} (
	 * {@link #attach(AnchorKey, IAdaptable)}).
	 *
	 * @param key
	 *            The {@link AnchorKey} for which to compute an anchor position.
	 */
	@Override
	protected Point computePosition(AnchorKey key) {
		Point referencePoint = anchoredReferencePointProviders.get(key)
				.referencePointProperty().get(key);
		if (referencePoint == null) {
			throw new IllegalStateException(
					"The IReferencePointProvider does not provide a reference point for this key: "
							+ key);
		}
		return computePosition(key.getAnchored(), referencePoint);
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
	 * @return Point The anchor position within the local coordinate system of
	 *         the to be anchored {@link Node}.
	 */
	public Point computePosition(Node anchored,
			Point anchoredReferencePointInLocal) {
		return FX2Geometry.toPoint(anchored.sceneToLocal(Geometry2FX.toFXPoint(
				getComputationStrategy().computePositionInScene(getAnchorage(),
						anchored, anchoredReferencePointInLocal))));
	}

	/**
	 * Detaches the given {@link AnchorKey} from this {@link DynamicAnchor}.
	 * Requires that an {@link IReferencePointProvider} can be obtained from the
	 * passed in {@link IAdaptable}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be detached.
	 * @param info
	 *            An {@link IAdaptable}, which will be used to obtain an
	 *            {@link IReferencePointProvider} that provides reference points
	 *            for this {@link DynamicAnchor}.
	 */
	@Override
	public void detach(AnchorKey key, IAdaptable info) {
		IReferencePointProvider helper = info
				.getAdapter(IReferencePointProvider.class);
		if (helper == null) {
			throw new IllegalArgumentException(
					"No ChopBoxHelper could be obtained via info.");
		}
		if (anchoredReferencePointProviders.get(key) != helper) {
			throw new IllegalStateException(
					"The passed in ChopBoxHelper had not been obtained for "
							+ key + " within attach() before.");
		}

		// unregister reference point listener
		helper.referencePointProperty()
				.removeListener(anchoredReferencePointsChangeListener);

		super.detach(key, info);

		anchoredReferencePointProviders.remove(key);
	}

	/**
	 * Returns the computation strategy used by this {@link DynamicAnchor}.
	 *
	 * @return The computation strategy being used.
	 */
	public IComputationStrategy getComputationStrategy() {
		return computationStrategyProperty == null
				? DEFAULT_COMPUTATION_STRATEGY
				: computationStrategyProperty().get();
	}

	/**
	 * Sets the given {@link IComputationStrategy} for this
	 * {@link DynamicAnchor}.
	 *
	 * @param computationStrategy
	 *            The new {@link IComputationStrategy} to use.
	 */
	public void setComputationStrategy(
			IComputationStrategy computationStrategy) {
		computationStrategyProperty().set(computationStrategy);
	}

}
