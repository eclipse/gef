package org.eclipse.gef4.mvc.fx.parts;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;

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
		AbstractFXHandlePart<Rectangle> implements
		Comparable<AbstractFXCornerHandlePart<? extends Node>> {

	private final Provider<IGeometry> handleGeometryProvider;
	private final Pos pos;

	public AbstractFXCornerHandlePart(
			Provider<IGeometry> handleGeometryProvider, Pos pos) {
		this.handleGeometryProvider = handleGeometryProvider;
		this.pos = pos;
	}

	@Override
	public int compareTo(AbstractFXCornerHandlePart<? extends Node> o) {
		// if we are bound to the same anchorages, we may compare positions,
		// otherwise we are not comparable
		if (!getAnchorages().equals(o.getAnchorages())) {
			throw new IllegalArgumentException(
					"Can only compare FXBoxHandles that are bound to the same anchorages.");
		}
		return pos.compareTo(o.pos);
	}

	protected org.eclipse.gef4.geometry.planar.Rectangle getHandleGeometry() {
		// TODO: we have to ensure we can also work with rotated rectangles
		// (i.e. polygons) properly (i.e. place the handles in the rotated end
		// point locations)
		return FXUtils.sceneToLocal(getVisual().getParent(),
				handleGeometryProvider.get()).getBounds();
	}

	public Pos getPos() {
		return pos;
	}

	protected double getXInset() {
		double xInset = getVisual().getWidth() / 2.0;
		return xInset;
	}

	protected double getYInset() {
		double yInset = getVisual().getHeight() / 2.0;
		return yInset;
	}

}