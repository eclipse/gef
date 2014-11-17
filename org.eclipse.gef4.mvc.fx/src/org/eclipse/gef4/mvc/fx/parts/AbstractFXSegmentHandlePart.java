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

public abstract class AbstractFXSegmentHandlePart<N extends Node> extends
		AbstractFXHandlePart<N> implements
		Comparable<AbstractFXSegmentHandlePart<? extends Node>> {

	private final Provider<BezierCurve[]> segmentsProvider;
	private BezierCurve[] segments;
	private int segmentIndex = -1;
	private double segmentParameter = 0.0;

	public AbstractFXSegmentHandlePart(
			Provider<BezierCurve[]> segmentsProvider, int segmentIndex,
			double segmentParameter) {
		super();
		this.segmentsProvider = segmentsProvider;
		this.segmentIndex = segmentIndex;
		this.segmentParameter = segmentParameter;
	}

	@Override
	public int compareTo(AbstractFXSegmentHandlePart<? extends Node> o) {
		// if we are bound to the same anchorages, we may compare segment
		// positions, otherwise we are not comparable
		if (!getAnchorages().equals(o.getAnchorages())) {
			throw new IllegalArgumentException(
					"Can only compare FXSegmentHandleParts that are bound to the same anchorages.");
		}
		return (int) ((100 * getSegmentIndex() + 10 * getSegmentParameter()) - (100 * o
				.getSegmentIndex() + 10 * o.getSegmentParameter()));
	}

	@Override
	public void doRefreshVisual(N visual) {
		updateLocation(visual);
	}

	protected Point getPosition(BezierCurve segment) {
		return segment.get(segmentParameter);
	}

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
	 * Sets the segment index. Refreshs the handle visual.
	 *
	 * @param segmentIndex
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
	 * Sets the segment parameter. Refreshs the handle visual.
	 *
	 * @param segmentParameter
	 * @see #getSegmentParameter()
	 */
	public void setSegmentParameter(double segmentParameter) {
		double oldSegmentParameter = this.segmentParameter;
		this.segmentParameter = segmentParameter;
		if (oldSegmentParameter != segmentParameter) {
			refreshVisual();
		}
	}

	protected void updateLocation(N visual) {
		// only update when bound to anchorage
		FXRootPart rootPart = (FXRootPart) getRoot();
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (rootPart == null || anchorages.keySet().size() != 1) {
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
			BezierCurve segmentInParent = (BezierCurve) FXUtils.sceneToLocal(
					visual.getParent(), segments[segmentIndex]);
			Point positionInParent = getPosition(segmentInParent);

			// transform to handle space
			visual.relocate(positionInParent.x
					+ visual.getLayoutBounds().getMinX(), positionInParent.y
					+ visual.getLayoutBounds().getMinY());
		}

	}
}