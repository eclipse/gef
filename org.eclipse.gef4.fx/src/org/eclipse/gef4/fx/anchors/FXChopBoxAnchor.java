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
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;

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
		Node anchorage = getAnchorage();
		IGeometry geometry = null;
		if (anchorage instanceof FXGeometryNode) {
			geometry = ((FXGeometryNode<?>) anchorage).getGeometry();
		}
		if (!(geometry instanceof IShape)) {
			// TODO: ICurve, Path
			geometry = JavaFX2Geometry.toRectangle(anchorage.getLayoutBounds());
		}
		return JavaFX2Geometry.toPoint(anchored.sceneToLocal(Geometry2JavaFX
				.toFXPoint(DefaultChopBoxAlgorithm.getInstance()
						.computePositionInScene(anchorage, geometry, anchored,
								referencePoint))));
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

}
