package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

public class VisualOutlineGeometryProvider implements
		IAdaptable.Bound<IVisualPart<Node>>, Provider<IGeometry> {

	private IVisualPart<Node> host;

	@Override
	public IGeometry get() {
		// return geometry in local coordinates
		return getGeometry(host.getVisual());
	}

	@Override
	public IVisualPart<Node> getAdaptable() {
		return host;
	}

	/**
	 * Returns an {@link IGeometry} representing the outline (or tight) bounds
	 * of the passed in visual {@link Node}, within the local coordinate space
	 * of that {@link Node}.
	 *
	 * @param visual
	 * @return
	 */
	protected IGeometry getGeometry(Node visual) {
		if (visual instanceof IFXConnection) {
			Node curveNode = ((IFXConnection) visual).getCurveNode();
			if (curveNode instanceof FXGeometryNode) {
				return FXUtils.localToParent(curveNode,
						((FXGeometryNode) curveNode).getGeometry());
			}
		} else if (visual instanceof FXGeometryNode) {
			return ((FXGeometryNode) visual).getGeometry();
		}
		return JavaFX2Geometry.toRectangle(visual.getLayoutBounds());
	}

	@Override
	public void setAdaptable(IVisualPart<Node> adaptable) {
		this.host = adaptable;
	}
}
