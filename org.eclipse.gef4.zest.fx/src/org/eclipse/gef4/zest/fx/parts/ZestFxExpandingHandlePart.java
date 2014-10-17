package org.eclipse.gef4.zest.fx.parts;

import javafx.scene.shape.Polygon;

import org.eclipse.gef4.geometry.planar.BezierCurve;

import com.google.inject.Provider;

public class ZestFxExpandingHandlePart extends ZestFxPruningHandlePart {

	public ZestFxExpandingHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
	}

	/**
	 * Creates a "plus" shaped polygon with the given size (s) and width (w).
	 * The coordinates of the polygon are as follows:
	 *
	 * <pre>
	 *    (-w, -s) X-X (w, -s)
	 *             | |
	 *    (-w, -w) | | (w, -w)
	 * (-s, -w) X--X X--X (s, -w)
	 *          |       |
	 *  (-s, w) X--X X--X (s, w)
	 *     (-w, w) | | (w, w)
	 *             | |
	 *     (-w, s) X-X (w, s)
	 * </pre>
	 *
	 * Therefore, the polygon will have a width and height of
	 * <code>2 * size</code>.
	 *
	 * @param size
	 * @param width
	 * @return
	 */
	@Override
	protected Polygon createIcon(double size, double width) {
		return new Polygon(-size, -width, -size, width, -width, width, -width,
				size, width, size, width, width, size, width, size, -width,
				width, -width, width, -size, -width, -size, -width, -width);
	}

}
