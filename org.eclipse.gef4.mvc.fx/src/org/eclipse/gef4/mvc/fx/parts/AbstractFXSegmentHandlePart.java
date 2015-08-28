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
package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

/**
 * An {@link AbstractFXSegmentHandlePart} is bound to a segment of a poly-bezier
 * handle geometry, represented by an array of {@link BezierCurve}s. The
 * segmentIndex of the {@link AbstractFXSegmentHandlePart} identifies that
 * segment (0, 1, 2, ...). The segmentParameter specifies the position of this
 * handle part on the segment (0 = start, 0.5 = mid, 1 = end).
 *
 * @author anyssen
 *
 * @param <N>
 *            The type of visual used by this handle. Needs to be a sub-type of
 *            {@link Node}.
 */
public abstract class AbstractFXSegmentHandlePart<N extends Node>
		extends AbstractFXHandlePart<N>
		implements Comparable<AbstractFXSegmentHandlePart<? extends Node>> {

	private Provider<BezierCurve[]> segmentsProvider;
	private BezierCurve[] segments;
	private int segmentIndex = -1;
	private double segmentParameter = 0.0;

	@Override
	public int compareTo(AbstractFXSegmentHandlePart<? extends Node> o) {
		// if we are bound to the same anchorages, we may compare segment
		// positions, otherwise we are not comparable
		if (!getAnchorages().equals(o.getAnchorages())) {
			throw new IllegalArgumentException(
					"Can only compare FXSegmentHandleParts that are bound to the same anchorages.");
		}
		return (int) ((100 * getSegmentIndex() + 10 * getSegmentParameter())
				- (100 * o.getSegmentIndex() + 10 * o.getSegmentParameter()));
	}

	@Override
	public void doRefreshVisual(N visual) {
		updateLocation(visual);
	}

	protected Point getPosition(BezierCurve segment) {
		return segment.get(segmentParameter);
	}

	// TODO: Make protected
	public int getSegmentCount() {
		return segments == null ? 0 : segments.length;
	}

	/**
	 * The segmentIndex specifies the segment of the IGeometry provided by the
	 * handle geometry provider on which this selection handle part is
	 * positioned.
	 *
	 * For a shape geometry, segments are determined by the
	 * {@link IShape#getOutlineSegments()} method.
	 *
	 * For a curve geometry, segments are determined by the
	 * {@link ICurve#toBezier()} method.
	 *
	 * The exact position on the segment is specified by the
	 * {@link #getSegmentParameter() segmentParameter}.
	 *
	 * @return segmentIndex
	 */
	public int getSegmentIndex() {
		return segmentIndex;
	}

	/**
	 * The segmentParameter is a value between 0 and 1. It determines the final
	 * point on the segment which this selection handle part belongs to.
	 *
	 * @return segmentParameter
	 */
	public double getSegmentParameter() {
		return segmentParameter;
	}

	protected BezierCurve[] getSegmentsInScene() {
		return segments;
	}

	/**
	 * Sets the segment index and refreshes the visual.
	 *
	 * @param segmentIndex
	 *            The segment index to set.
	 * @see #getSegmentIndex()
	 */
	public void setSegmentIndex(int segmentIndex) {
		int oldSegmentIndex = this.segmentIndex;
		this.segmentIndex = segmentIndex;
		if (oldSegmentIndex != segmentIndex) {
			refreshVisual();
		}
	}

	/**
	 * Sets the segment parameter and refreshes the visual.
	 *
	 * @param segmentParameter
	 *            The segment parameter to set.
	 * @see #getSegmentParameter()
	 */
	public void setSegmentParameter(double segmentParameter) {
		double oldSegmentParameter = this.segmentParameter;
		this.segmentParameter = segmentParameter;
		if (oldSegmentParameter != segmentParameter) {
			refreshVisual();
		}
	}

	public void setSegmentsProvider(Provider<BezierCurve[]> segmentsProvider) {
		this.segmentsProvider = segmentsProvider;
	}

	protected void updateLocation(N visual) {
		// only update when bound to anchorage
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (anchorages.keySet().size() < 1) {
			return;
		}

		segments = segmentsProvider.get();
		if (segmentIndex < 0 || segmentIndex > segments.length - 1) {
			// hide those that have "invalid" index. (this may happen during
			// life-feedback, when a way-point is removed)
			visual.setVisible(false);
		} else {
			visual.setVisible(true);

			// get new position (in parent coordinate space)
			BezierCurve segmentInParent = (BezierCurve) FXUtils
					.sceneToLocal(visual.getParent(), segments[segmentIndex]);
			Point positionInParent = getPosition(segmentInParent);

			// transform to handle space
			visual.relocate(
					positionInParent.x + visual.getLayoutBounds().getMinX(),
					positionInParent.y + visual.getLayoutBounds().getMinY());
		}
	}

}