package org.eclipse.gef4.mvc.fx.parts;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.BezierCurve;

import com.google.inject.Provider;

/**
 * An {@link AbstractFXCornerHandlePart} is bound to a corner of a rectilinear
 * geometry.
 *
 * @author nyssen
 *
 * @param <N>
 */
public abstract class AbstractFXCornerHandlePart<N extends Node> extends
		AbstractFXSegmentHandlePart<N> {

	private final Pos pos;

	public AbstractFXCornerHandlePart(Provider<BezierCurve[]> segmentsProvider,
			int segmentIndex, double segmentParameter, Pos position) {
		super(segmentsProvider, segmentIndex, segmentParameter);
		this.pos = position;
	}

	public Pos getPos() {
		return pos;
	}

}