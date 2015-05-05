/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;

// TODO: Find an appropriate name for this (outline anchor or shape anchor or perimeter anchor)
//       It has nothing to do with a ChopBox, so this does not seem to be intuitive.
public class FXChopBoxAnchor extends AbstractFXAnchor {

	public interface ComputationStrategy {

		public class Impl implements ComputationStrategy {

			// TODO: reduce visibility
			public Point computeAnchorageReferencePointInLocal(Node node,
					IGeometry geometryInLocal) {
				// TODO: we cannot handle Path yet
				if (!(geometryInLocal instanceof IShape)
						&& !(geometryInLocal instanceof ICurve)) {
					throw new IllegalArgumentException(
							"The given IGeometry is neither an IShape nor an ICurve.");
				}

				// determine the bounds center
				Point boundsCenterInLocal = geometryInLocal.getBounds()
						.getCenter();

				// if the bounds center is contained, it is good enough as a
				// reference point
				if (!geometryInLocal.contains(boundsCenterInLocal)) {
					// otherwise we have to search for another reference point
					if (geometryInLocal instanceof IShape) {
						// in case of an IShape we can pick the vertex nearest
						// to
						// the center point
						Point nearestVertex = getNearestVertex(
								boundsCenterInLocal, (IShape) geometryInLocal);
						if (nearestVertex != null) {
							return nearestVertex;
						} else {
							throw new IllegalArgumentException(
									"The given IShape does not provide any vertices.");
						}
					} else {
						// TODO: Which point shall we use for curves?
						return ((ICurve) geometryInLocal).getP1();
					}
				} else {
					return boundsCenterInLocal;
				}
			}

			protected Point computeAnchorageReferencePointInScene(Node node,
					IGeometry geometryInLocal) {
				return FXUtils.localToScene(node,
						computeAnchorageReferencePointInLocal(node,
								geometryInLocal));
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.gef4.fx.anchors.AnchorageReferenceComputationStrategy
			 * # computePositionInScene(javafx.scene.Node,
			 * org.eclipse.gef4.geometry.planar.IGeometry, javafx.scene.Node,
			 * org.eclipse.gef4.geometry.planar.Point)
			 */
			@Override
			public Point computePositionInScene(Node anchorage, Node anchored,
					Point anchoredReferencePointInLocal) {
				IGeometry anchorageReferenceGeometryInLocal = getAnchorageReferenceGeometryInLocal(
						anchorage);

				Point anchoredReferencePointInScene = FXUtils
						.localToScene(anchored, anchoredReferencePointInLocal);

				Point anchorageReferencePointInScene = computeAnchorageReferencePointInScene(
						anchorage, anchorageReferenceGeometryInLocal);

				Line referenceLineInScene = new Line(
						anchorageReferencePointInScene,
						anchoredReferencePointInScene);

				IGeometry anchorageGeometryInScene = FXUtils.localToScene(
						anchorage, anchorageReferenceGeometryInLocal);
				ICurve anchorageOutlineInScene = getOutline(
						anchorageGeometryInScene);

				Point nearestIntersectionInScene = anchorageOutlineInScene
						.getNearestIntersection(referenceLineInScene,
								anchoredReferencePointInScene);
				if (nearestIntersectionInScene != null) {
					return nearestIntersectionInScene;
				}

				// in case of emergency, return the anchorage reference point
				return anchorageReferencePointInScene;
			}

			protected IGeometry getAnchorageReferenceGeometryInLocal(
					Node anchorage) {
				IGeometry geometry = null;
				if (anchorage instanceof FXGeometryNode) {
					geometry = ((FXGeometryNode<?>) anchorage).getGeometry();
				}
				if (!(geometry instanceof IShape)) {
					// TODO: ICurve, Path
					geometry = JavaFX2Geometry
							.toRectangle(anchorage.getLayoutBounds());
				}
				return geometry;
			}

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

			protected ICurve getOutline(IGeometry geometry) {
				// TODO: we cannot handle Path yet
				if (!(geometry instanceof IShape)
						&& !(geometry instanceof ICurve)) {
					throw new IllegalArgumentException(
							"The given IGeometry is neither an ICurve nor an IShape.");
				}

				if (geometry instanceof IShape) {
					return ((IShape) geometry).getOutline();
				} else if (geometry instanceof ICurve) {
					return (ICurve) geometry;
				} else {
					throw new IllegalStateException(
							"The transformed geometry is neither an ICurve nor an IShape.");
				}
			}

		}

		Point computePositionInScene(Node anchorage, Node anchored,
				Point anchoredReferencePointInLocal);

	}

	/**
	 * A {@link ReferencePointProvider} needs to be provided as default adapter
	 * (see {@link AdapterKey#get(Class)}) on the {@link IAdaptable} info that
	 * gets passed into {@link FXChopBoxAnchor#attach(AnchorKey, IAdaptable)}
	 * and {@link FXChopBoxAnchor#detach(AnchorKey, IAdaptable)}. The
	 * {@link ReferencePointProvider} has to provide a reference point for each
	 * {@link AdapterKey} that is attached to the {@link FXChopBoxAnchor}. It
	 * will be used when computing anchor positions for the respective
	 * {@link AnchorKey}.
	 *
	 * @author anyssen
	 *
	 */
	public interface ReferencePointProvider {

		/**
		 * Provides a read-only (map) property with positions (in local
		 * coordinates of the anchored {@link Node}) for all attached
		 * {@link AnchorKey}s.
		 *
		 * @return A read-only (map) property storing reference positions for
		 *         all {@link AnchorKey}s attached to the
		 *         {@link FXChopBoxAnchor}s it is forwarded to.
		 */
		public abstract ReadOnlyMapWrapper<AnchorKey, Point> referencePointProperty();

	}

	private Map<AnchorKey, ReferencePointProvider> anchoredReferencePointProviders = new HashMap<>();

	private MapChangeListener<AnchorKey, Point> anchoredReferencePointsChangeListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			if (change.wasAdded()) {
				// Do some defensive checks here. However, if we run into null
				// key or value here, this will be an inconsistency of the
				// FXChopBoxHelper#referencePointProperty()
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
					// (FXChopBoxHelper#referencePointProperty() may contain
					// AnchorKeys registered at other anchors as well)
					updatePosition(change.getKey());
				}
			}
		}
	};

	private ComputationStrategy computationStrategy;

	public FXChopBoxAnchor(Node anchorage) {
		this(anchorage, new ComputationStrategy.Impl());
	}

	public FXChopBoxAnchor(Node anchorage,
			ComputationStrategy computationStrategy) {
		super(anchorage);
		this.computationStrategy = computationStrategy;
	}

	/**
	 * Attaches the given {@link AnchorKey} to this {@link FXChopBoxAnchor}.
	 * Requires that an {@link ReferencePointProvider} can be obtained from the
	 * passed in {@link IAdaptable}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be attached.
	 * @param info
	 *            An {@link IAdaptable}, which will be used to obtain an
	 *            {@link ReferencePointProvider} that provides reference points
	 *            for this {@link FXChopBoxAnchor}.
	 *
	 */
	@Override
	public void attach(AnchorKey key, IAdaptable info) {
		ReferencePointProvider referencePointProvider = info
				.getAdapter(ReferencePointProvider.class);
		if (referencePointProvider == null) {
			throw new IllegalArgumentException(
					"No ReferencePointProvider could be obtained via info.");
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
	 * Recomputes the position for the given attached {@link AnchorKey} by
	 * retrieving a reference position via the {@link ReferencePointProvider}
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
					"The ReferencePointProvider does not provide a reference point for this key: "
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
	protected Point computePosition(Node anchored,
			Point anchoredReferencePointInLocal) {
		return JavaFX2Geometry.toPoint(anchored
				.sceneToLocal(Geometry2JavaFX.toFXPoint(computationStrategy
						.computePositionInScene(getAnchorage(), anchored,
								anchoredReferencePointInLocal))));
	}

	/**
	 * Detaches the given {@link AnchorKey} from this {@link FXChopBoxAnchor}.
	 * Requires that an {@link ReferencePointProvider} can be obtained from the
	 * passed in {@link IAdaptable}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be detached.
	 * @param info
	 *            An {@link IAdaptable}, which will be used to obtain an
	 *            {@link ReferencePointProvider} that provides reference points
	 *            for this {@link FXChopBoxAnchor}.
	 */
	@Override
	public void detach(AnchorKey key, IAdaptable info) {
		ReferencePointProvider helper = info
				.getAdapter(ReferencePointProvider.class);
		if (helper == null) {
			throw new IllegalArgumentException(
					"No FXChopBoxHelper could be obtained via info.");
		}
		if (anchoredReferencePointProviders.get(key) != helper) {
			throw new IllegalStateException(
					"The passed in FXChopBoxHelper had not been obtained for "
							+ key + " within attach() before.");
		}

		// unregister reference point listener
		helper.referencePointProperty()
				.removeListener(anchoredReferencePointsChangeListener);

		super.detach(key, info);

		anchoredReferencePointProviders.remove(key);
	}

}
