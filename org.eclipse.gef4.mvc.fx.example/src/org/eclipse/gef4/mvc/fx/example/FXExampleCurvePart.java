package org.eclipse.gef4.mvc.fx.example;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleCurvePart extends AbstractFXContentPart implements
		PropertyChangeListener {

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
		visual.toBack();
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(IAnchor.REPRESH)) {
			updateModel();
			refreshVisual();
		}
	}

	/*
	 * TODO: - position computation (currently somewhere inside the bounds) -
	 * source / target distinction - updatePathElements() should not be
	 * necessary
	 */
	private void updateModel() {
		if (anchors.size() == 2) {
			// use anchors as start and end point
			Node startNode = anchors.get(0).getAnchorage();
			Node endNode = anchors.get(1).getAnchorage();

			// compute reference points in local coordinate space
			Point startReference = JavaFX2Geometry.toRectangle(
					getVisual().sceneToLocal(
							endNode.localToScene(endNode.getBoundsInLocal())))
					.getCenter();
			Point endReference = JavaFX2Geometry.toRectangle(
					getVisual()
							.sceneToLocal(
									startNode.localToScene(startNode
											.getBoundsInLocal()))).getCenter();

			try {
				// compute new anchor positions
				Point start = anchors.get(0).getPosition(this.getVisual(),
						startReference);
				Point end = anchors.get(1).getPosition(this.getVisual(),
						endReference);

				// update model
				Line line = (Line) getModel();
				line.setP1(start);
				line.setP2(end);
				visual.updatePathElements();
			} catch (IllegalArgumentException e) {
				// When no intersection point can be found by the ChopBoxAnchor,
				// the connection is invisible
				// TODO: handle this proactively
			}
		}
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		return null;
	}

}
