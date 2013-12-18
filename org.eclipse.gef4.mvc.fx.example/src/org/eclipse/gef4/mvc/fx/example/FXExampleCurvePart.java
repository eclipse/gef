package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.mvc.fx.AbstractFXEdgeContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleCurvePart extends AbstractFXEdgeContentPart {

	private GeometryNode<ICurve> visual;

	public FXExampleCurvePart() {
		visual = new GeometryNode<ICurve>();
	}

	@Override
	public ICurve getModel() {
		return (ICurve) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof ICurve)) {
			throw new IllegalArgumentException(
					"Only ICurve models are supported.");
		}
		super.setModel(model);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		ICurve curve = getModel();
		if (visual.getGeometry() != curve) {
			visual.setGeometry(curve);
		}
	}

	@Override
	protected void linkAnchoredVisual(IVisualPart<Node> anchored) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unlinkAnchoredVisual(IVisualPart<Node> anchored) {
		// TODO Auto-generated method stub
		
	}
}
