package org.eclipse.gef4.mvc.fx.example;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleCurvePart extends AbstractFXContentPart implements PropertyChangeListener {

	private GeometryNode<ICurve> visual;
	private List<IAnchor<Node>> anchors = new ArrayList<IAnchor<Node>>();

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
	public void attachVisualToAnchorageVisual(IAnchor<Node> anchor) {
		anchors.add(anchor);
		anchor.addPropertyChangeListener(this);
	}

	@Override
	public void detachVisualFromAnchorageVisual(IAnchor<Node> anchor) {
		anchors.remove(anchor);
		anchor.removePropertyChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(IAnchor.POSITION_PROPERTY)){
			updateModel();
			refreshVisual();
		}
	}

	private void updateModel() {
		Line line = (Line) getModel();
		if(anchors.size() == 2){
			// 
		}
		System.out.println("anchors changed");
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		return null;
	}

}
