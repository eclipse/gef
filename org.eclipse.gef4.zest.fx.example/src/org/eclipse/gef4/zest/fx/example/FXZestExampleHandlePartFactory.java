package org.eclipse.gef4.zest.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

import com.google.inject.Provider;

public class FXZestExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	@Override
	public IHandlePart<Node> createShapeSelectionHandlePart(
			IContentPart<Node> targetPart,
			Provider<IGeometry> handleGeometryProvider, int vertexIndex) {
		IHandlePart<Node> part = super.createShapeSelectionHandlePart(
				targetPart, handleGeometryProvider, vertexIndex);
		// TODO: the corresponding NodeContentPart does not provide resizing yet
		part.setAdapter(AbstractFXDragPolicy.class,
				new FXResizeRelocateOnHandleDragPolicy(
						toReferencePoint(vertexIndex)));
		return part;
	}

	private ReferencePoint toReferencePoint(int vertexIndex) {
		switch (vertexIndex) {
		case 0:
			return ReferencePoint.TOP_LEFT;
		case 1:
			return ReferencePoint.TOP_RIGHT;
		case 2:
			return ReferencePoint.BOTTOM_RIGHT;
		case 3:
			return ReferencePoint.BOTTOM_LEFT;
		default:
			throw new IllegalStateException("Unsupported vertex index ("
					+ vertexIndex + "), expected 0 to 3.");
		}
	}

}
