/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.anchors;

import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Point;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * An {@link StaticAnchor} provides a position for each {@link AnchorKey}, based
 * on a reference position relative to the anchorage {@link Node}, to which the
 * {@link StaticAnchor} is bound, or based on a (global) static reference
 * position in case the {@link StaticAnchor} is unbound.
 *
 * @author anyssen
 * @author mwienand
 */
public class StaticAnchor extends AbstractAnchor {

	private ObjectProperty<Point> referencePositionProperty = new SimpleObjectProperty<>();

	{
		referencePositionProperty.addListener(new ChangeListener<Point>() {
			@Override
			public void changed(ObservableValue<? extends Point> observable,
					Point oldValue, Point newValue) {
				// recompute positions for all anchor keys
				updatePositions();
			}
		});
	}

	/**
	 * Creates an {@link StaticAnchor} that is bound to the provided anchorage.
	 * It will used the passed in reference position (in the local coordinate
	 * system of the anchorage {@link Node}) to compute positions (see
	 * {@link #positionsUnmodifiableProperty()}) for all attached
	 * {@link AnchorKey}s (in the local coordinate system of the attached
	 * {@link AnchorKey}'s {@link Node} ).
	 * <p>
	 * In case the anchorage {@link Node} or any of its ancestors are changed in
	 * a way that will affect the position, the
	 * {@link #positionsUnmodifiableProperty()} will be updated.
	 *
	 * @param anchorage
	 *            The anchorage {@link Node} to bind this {@link StaticAnchor}
	 *            to.
	 * @param referencePositionInAnchorageLocal
	 *            The position within the local coordinate space of the
	 *            anchorage {@link Node}, which is used to compute the position
	 *            (in scene coordinates) for all attached {@link AnchorKey}s.
	 */
	public StaticAnchor(Node anchorage,
			Point referencePositionInAnchorageLocal) {
		super(anchorage);
		referencePositionProperty.set(referencePositionInAnchorageLocal);
	}

	/**
	 * Creates an {@link StaticAnchor} that is not bound to an anchorage
	 * {@link Node} and will always provide the passed in position (in scene
	 * coordinates) for all attached {@link AnchorKey}s (i.e. anchored
	 * {@link Node}s).
	 *
	 * @param referencePositionInScene
	 *            The position in scene coordinates to be provided for all
	 *            attached {@link AnchorKey}s.
	 */
	public StaticAnchor(Point referencePositionInScene) {
		this(null, referencePositionInScene);
	}

	@Override
	protected Point computePosition(AnchorKey key) {
		// in case an anchorage is set, the position is interpreted to be in its
		// local coordinate system, so transform it into scene coordinates
		Node anchored = key.getAnchored();
		Node anchorage = getAnchorage();
		Point positionInScene = anchorage == null
				? referencePositionProperty.get()
				: FX2Geometry.toPoint(anchorage.localToScene(
						referencePositionProperty.get().x,
						referencePositionProperty.get().y));
		Point positionInAnchoredLocal = FX2Geometry.toPoint(
				anchored.sceneToLocal(positionInScene.x, positionInScene.y));
		return positionInAnchoredLocal;
	}

	/**
	 * Returns the reference position of this {@link StaticAnchor}.
	 *
	 * @return The reference position of this {@link StaticAnchor}.
	 */
	public Point getReferencePosition() {
		return referencePositionProperty.get();
	}

	/**
	 * Returns the {@link ObjectProperty} that manages the reference position of
	 * this {@link StaticAnchor}.
	 *
	 * @return The {@link ObjectProperty} that manages the reference position of
	 *         this {@link StaticAnchor}.
	 */
	public ObjectProperty<Point> referencePositionProperty() {
		return referencePositionProperty;
	}

	/**
	 * Sets the reference position of this {@link StaticAnchor} to the given
	 * value.
	 *
	 * @param referencePosition
	 *            The new reference position for this {@link StaticAnchor}.
	 */
	public void setReferencePosition(Point referencePosition) {
		referencePositionProperty.set(referencePosition);
	}

	@Override
	public String toString() {
		return "StaticAnchor[referencePosition = "
				+ referencePositionProperty.get() + "]";
	}
}