/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - bug fixing (Bugzilla #517076)
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.geometry.planar.Point;

import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

/**
 * An {@link AbstractSegmentHandlePart} is bound to a segment of a poly-bezier
 * handle geometry, represented by an array of {@link BezierCurve}s. The
 * segmentIndex of the {@link AbstractSegmentHandlePart} identifies that segment
 * (0, 1, 2, ...). The segmentParameter specifies the position of this handle
 * part on the segment (0 = start, 0.5 = mid, 1 = end).
 *
 * @author anyssen
 *
 * @param <N>
 *            The type of visual used by this handle. Needs to be a sub-type of
 *            {@link Node}.
 */
public abstract class AbstractSegmentHandlePart<N extends Node>
		extends AbstractHandlePart<N>
		implements Comparable<AbstractSegmentHandlePart<? extends Node>> {

	private Provider<BezierCurve[]> segmentsProvider;
	private BezierCurve[] segments;
	private int segmentIndex = -1;
	private double segmentParameter = 0.0;

	@Override
	public int compareTo(AbstractSegmentHandlePart<? extends Node> o) {
		// if we are bound to the same anchorages, we may compare segment
		// positions, otherwise we are not comparable
		if (!getAnchoragesUnmodifiable()
				.equals(o.getAnchoragesUnmodifiable())) {
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

	/**
	 * Returns the {@link BezierCurve} at which this handle part is anchored
	 * (depending on segment index), or <code>null</code> if that
	 * {@link BezierCurve} cannot be determined.
	 *
	 * @return The {@link BezierCurve} at which this handle part is anchored, or
	 *         <code>null</code> if that {@link BezierCurve} cannot be
	 *         determined.
	 */
	protected BezierCurve getBezierSegmentInParent() {
		segments = segmentsProvider.get();
		if (segmentIndex < 0 || segmentIndex > segments.length - 1) {
			return null;
		} else {
			return (BezierCurve) NodeUtils.sceneToLocal(getVisual().getParent(),
					segments[segmentIndex]);
		}
	}

	/**
	 * Returns the {@link Color} that is used to fill connected handles.
	 *
	 * @return The {@link Color} that is used to fill connected handles.
	 */
	protected Color getConnectedFill() {
		@SuppressWarnings("serial")
		Provider<Color> connectedColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultSelectionHandlePartFactory.CONNECTED_HANDLE_COLOR_PROVIDER));
		return connectedColorProvider == null
				? DefaultSelectionHandlePartFactory.DEFAULT_CONNECTED_HANDLE_COLOR
				: connectedColorProvider.get();
	}

	/**
	 * Returns the {@link Color} that is used to fill insertion handles.
	 *
	 * @return The {@link Color} that is used to fill insertion handles.
	 */
	protected Color getInsertFill() {
		@SuppressWarnings("serial")
		Provider<Color> connectedColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultSelectionHandlePartFactory.INSERT_HANDLE_COLOR_PROVIDER));
		return connectedColorProvider == null
				? DefaultSelectionHandlePartFactory.DEFAULT_INSERT_HANDLE_COLOR
				: connectedColorProvider.get();
	}

	/**
	 * Returns the {@link Color} that is used to fill movement handles.
	 *
	 * @return The {@link Color} that is used to fill movement handles.
	 */
	protected Color getMoveFill() {
		@SuppressWarnings("serial")
		Provider<Color> connectedColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultSelectionHandlePartFactory.MOVE_HANDLE_COLOR_PROVIDER));
		return connectedColorProvider == null
				? DefaultSelectionHandlePartFactory.DEFAULT_MOVE_HANDLE_COLOR
				: connectedColorProvider.get();
	}

	/**
	 * Returns the position of this {@link AbstractSegmentHandlePart} on the
	 * given segment using the segment parameter that is assigned to this part.
	 *
	 * @param segment
	 *            The {@link BezierCurve} on which the position is evaluated.
	 * @return The position of this part on the given segment using the segment
	 *         parameter of this part.
	 */
	protected Point getPosition(BezierCurve segment) {
		return segment.get(segmentParameter);
	}

	/**
	 * Returns the number of segments that are provided to this part.
	 *
	 * @return The number of segments that are provided to this part.
	 */
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

	/**
	 * Returns the {@link BezierCurve}s that are provided to this part in the
	 * coordinate system of the {@link Scene}.
	 *
	 * @return The {@link BezierCurve}s that are provided to this part in the
	 *         coordinate system of the {@link Scene}.
	 */
	protected BezierCurve[] getSegmentsInScene() {
		return segments;
	}

	/**
	 * Returns the {@link Color} that is used to stroke handles.
	 *
	 * @return The {@link Color} that is used to stroke handles.
	 */
	protected Color getStroke() {
		@SuppressWarnings("serial")
		Provider<Color> connectedColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultSelectionFeedbackPartFactory.PRIMARY_SELECTION_FEEDBACK_COLOR_PROVIDER));
		return connectedColorProvider == null
				? DefaultSelectionFeedbackPartFactory.DEFAULT_PRIMARY_SELECTION_FEEDBACK_COLOR
				: connectedColorProvider.get();
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

	/**
	 * Sets the <code>Provider&lt;BezierCurve[]&gt;</code> for this part to the
	 * given value.
	 *
	 * @param segmentsProvider
	 *            The new <code>Provider&lt;BezierCurve[]&gt;</code> for this
	 *            part.
	 */
	public void setSegmentsProvider(Provider<BezierCurve[]> segmentsProvider) {
		this.segmentsProvider = segmentsProvider;
	}

	/**
	 * Computes the location for this part and relocates its visual to that
	 * location. The visual is made invisible if this part has an invalid index
	 * (out of bounds), i.e. when no location can be computed.
	 *
	 * @param visual
	 *            This part's visual for convenience.
	 */
	protected void updateLocation(N visual) {
		// only update when bound to anchorage
		SetMultimap<IVisualPart<? extends Node>, String> anchorages = getAnchoragesUnmodifiable();
		if (anchorages.keySet().size() < 1) {
			return;
		}

		BezierCurve segmentInParent = getBezierSegmentInParent();
		if (segmentInParent == null) {
			// hide those that have "invalid" index. (this may happen during
			// life-feedback, when a way-point is removed)
			visual.setVisible(false);
		} else {
			visual.setVisible(true);
			Point positionInParent = getPosition(segmentInParent);
			// transform to handle space
			visual.relocate(
					positionInParent.x + visual.getLayoutBounds().getMinX(),
					positionInParent.y + visual.getLayoutBounds().getMinY());
		}
	}
}