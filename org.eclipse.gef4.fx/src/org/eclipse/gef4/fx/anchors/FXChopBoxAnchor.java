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

import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.nodes.FXConnection.FXChopBoxHelper;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

// TODO: Find an appropriate name for this (outline anchor or shape anchor or perimeter anchor)
//       It has nothing to do with a ChopBox, so this does not seem to be intuitive.
public class FXChopBoxAnchor extends AbstractFXAnchor {

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

	private static boolean isValidTransform(AffineTransform t) {
		for (double d : t.getMatrix()) {
			if (Double.isNaN(d)) {
				return false;
			}
		}
		return true;
	}

	private Map<AnchorKey, ReferencePointProvider> referencePointProviders = new HashMap<>();

	private MapChangeListener<AnchorKey, Point> referencePointChangeListener = new MapChangeListener<AnchorKey, Point>() {
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
				if (referencePointProviders.containsKey(change.getKey())) {
					// only recompute position, if one of our own keys changed
					// (FXChopBoxHelper#referencePointProperty() may contain
					// AnchorKeys registered at other anchors as well)
					updatePosition(change.getKey());
				}
			}
		}
	};

	public FXChopBoxAnchor(Node anchorage) {
		super(anchorage);
	}

	/**
	 * Attaches the given {@link AnchorKey} to this {@link FXChopBoxAnchor}.
	 * Requires that an {@link FXChopBoxHelper} can be obtained from the passed
	 * in {@link IAdaptable}.
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
		referencePointProviders.put(key, referencePointProvider);

		// will enforce a re-computation of positions, so we need to have
		// obtained the helper beforehand.
		super.attach(key, info);

		// add listener to reference point changes
		referencePointProvider.referencePointProperty().addListener(
				referencePointChangeListener);
	}

	/**
	 * Recomputes the position of this anchor w.r.t. the given anchored
	 * {@link Node} and the reference point provided for it.
	 *
	 * @param key
	 */
	@Override
	protected Point computePosition(AnchorKey key) {
		Point referencePoint = referencePointProviders.get(key)
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
	 * @param referencePoint
	 *            A reference {@link Point} used for calculation of the anchor
	 *            position, provided within the local coordinate system of the
	 *            to be anchored {@link Node}.
	 * @return Point The anchor position within the local coordinate system of
	 *         the to be anchored {@link Node}.
	 */
	protected Point computePosition(Node anchored, Point referencePoint) {
		/*
		 * The reference shapes/lines/points have to be transformed into the
		 * same coordinate system in order to be able to compute the correct
		 * intersection. We choose the scene coordinate system here. Therefore,
		 * we need access to a local-to-scene-transform for the anchorage and
		 * the anchored.
		 *
		 * Important: JavaFX Node provides a (lazily computed)
		 * local-to-scene-transform property which we could access to get that
		 * transform. Unfortunately, this property is not updated correctly,
		 * i.e. its value can differ from the actual local-to-scene-transform.
		 * This is reflected in the different values of a) the
		 * Node#localToScene(...) method, and b) transforming using the
		 * concatenated local-to-parent-transforms.
		 *
		 * Therefore, we compute the local-to-scene-transform for anchorage and
		 * anchored by concatenating the local-to-parent-transforms in the
		 * hierarchy, respectively.
		 */
		// TODO: provide helper methods for transforming points in FXUtils and
		// replace the code here
		AffineTransform anchorageToSceneTransform = FXUtils
				.getLocalToSceneTx(getAnchorage());
		if (!isValidTransform(anchorageToSceneTransform)) {
			anchorageToSceneTransform = new AffineTransform();
		}

		AffineTransform anchoredToSceneTransform = FXUtils
				.getLocalToSceneTx(anchored);
		if (!isValidTransform(anchoredToSceneTransform)) {
			anchoredToSceneTransform = new AffineTransform();
		}

		// transform into scene coordinates
		Point anchorageReferencePointInScene = anchorageToSceneTransform
				.getTransformed(getAnchorageReferencePoint());
		Point anchoredReferencePointInScene = anchoredToSceneTransform
				.getTransformed(referencePoint);
		IShape anchorageReferenceShapeInScene = getAnchorageReferenceShape()
				.getTransformed(anchorageToSceneTransform);

		// construct reference line
		Line referenceLineInScene = new Line(anchorageReferencePointInScene,
				anchoredReferencePointInScene);

		// compute intersection
		Point nearestIntersectionInScene = anchorageReferenceShapeInScene
				.getOutline().getNearestIntersection(referenceLineInScene,
						anchoredReferencePointInScene);
		if (nearestIntersectionInScene != null) {
			// transform to anchored coordinate system
			return JavaFX2Geometry.toPoint(anchored
					.sceneToLocal(Geometry2JavaFX
							.toFXPoint(nearestIntersectionInScene)));
		}

		// do not fail hard... use center
		return JavaFX2Geometry.toPoint(anchored.sceneToLocal(Geometry2JavaFX
				.toFXPoint(anchorageReferencePointInScene)));
	}

	/**
	 * Detaches the given {@link AnchorKey} from this {@link FXChopBoxAnchor}.
	 * Requires that an {@link FXChopBoxHelper} can be obtained from the passed
	 * in {@link IAdaptable}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be detached.
	 * @param info
	 *            An {@link IAdaptable}, which will be used to obtain an
	 *            {@link ReferencePointProvider} that provides reference points
	 *            for this {@link FXChopBoxAnchor}.
	 *
	 */
	@Override
	public void detach(AnchorKey key, IAdaptable info) {
		ReferencePointProvider helper = info
				.getAdapter(ReferencePointProvider.class);
		if (helper == null) {
			throw new IllegalArgumentException(
					"No FXChopBoxHelper could be obtained via info.");
		}
		if (referencePointProviders.get(key) != helper) {
			throw new IllegalStateException(
					"The passed in FXChopBoxHelper had not been obtained for "
							+ key + " within attach() before.");
		}

		// unregister reference point listener
		helper.referencePointProperty().removeListener(
				referencePointChangeListener);

		super.detach(key, info);

		referencePointProviders.remove(key);
	}

	/**
	 * @return The anchorage reference point within the local coordinate system
	 *         of the anchorage {@link Node}.
	 */
	protected Point getAnchorageReferencePoint() {
		return getAnchorageReferenceShape().getBounds().getCenter();
	}

	/**
	 * Returns the anchorage reference {@link IShape} which is used to compute
	 * the intersection point which is used as the anchor position. By default,
	 * a {@link Rectangle} matching the layout-bounds of the anchorage
	 * {@link Node} is returned. Clients may override this method to use other
	 * geometric shapes instead.
	 *
	 * @return The anchorage reference {@link IShape} within the local
	 *         coordinate system of the anchorage {@link Node}
	 */
	protected IShape getAnchorageReferenceShape() {
		return JavaFX2Geometry.toRectangle(getAnchorage().getLayoutBounds());
	}

}
