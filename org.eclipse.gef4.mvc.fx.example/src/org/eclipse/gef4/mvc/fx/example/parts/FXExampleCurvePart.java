package org.eclipse.gef4.mvc.fx.example.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractAnchorPointPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractNewAnchorPointPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverFeedbackByEffectPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionFeedbackByEffectPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractHoverFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleCurvePart extends AbstractFXExampleElementPart implements
		PropertyChangeListener {

	private GeometryNode<ICurve> visual;
	private List<IAnchor<Node>> anchors = new ArrayList<IAnchor<Node>>();

	public FXExampleCurvePart() {
		visual = new GeometryNode<ICurve>();
		installEditPolicy(AbstractSelectionFeedbackPolicy.class,
				new FXSelectionFeedbackByEffectPolicy() {
					@Override
					public List<IHandlePart<Node>> createHandles() {
						ArrayList<IHandlePart<Node>> handles = new ArrayList<IHandlePart<Node>>();
						List<Point> wayPoints = getModel().getWayPoints();
						int i = 0;
						for (Point wayPoint : wayPoints) {
							handles.add(new FXAnchorPointHandlePart(
									(IContentPart<Node>) getHost(), i++, wayPoint));
						}
						return handles;
					}
				});
		installEditPolicy(AbstractHoverFeedbackPolicy.class,
				new FXHoverFeedbackByEffectPolicy() {
					@Override
					public List<IHandlePart<Node>> createHandles() {
						ArrayList<IHandlePart<Node>> handles = new ArrayList<IHandlePart<Node>>();
						int i = 0;
						for (Line line : ((Polyline) visual.getGeometry()).getCurves()) {
							Point midPoint = line.get(0.5);
							handles.add(new FXInsertAnchorPointHandlePart(
									(IContentPart<Node>) getHost(), i++, midPoint));
						}
						return handles;
					}
				});
		installEditPolicy(AbstractNewAnchorPointPolicy.class, new AbstractNewAnchorPointPolicy() {
			private List<Point> wayPoints = new ArrayList<Point>();
			
			@Override
			public void moveAnchorPoint(int wayPointIndex, Point p) {
				Point point = wayPoints.get(wayPointIndex);
				point.x = p.x;
				point.y = p.y;
				refreshVisualWith(wayPoints);
			}
			
			@Override
			public void initAnchorPoint(int wayPointIndex, Point p) {
				FXGeometricCurve model = getModel();
				List<Point> points = model.getWayPoints();
				wayPoints.clear();
				wayPoints.addAll(points);
				wayPoints.add(wayPointIndex, new Point(p));
			}
			
			@Override
			public void commitAnchorPoint(int wayPointIndex, Point p) {
				Point point = wayPoints.get(wayPointIndex);
				point.x = p.x;
				point.y = p.y;
				getModel().addWayPoint(wayPointIndex, point);
			}
		});
		installEditPolicy(AbstractAnchorPointPolicy.class, new AbstractAnchorPointPolicy() {
			private List<Point> wayPoints = new ArrayList<Point>();
			
			@Override
			public void initAnchorPoint(int wayPointIndex) {
				wayPoints.clear();
				wayPoints.addAll(getModel().getWayPoints());
			}
			
			@Override
			public void moveAnchorPoint(int wayPointIndex, Point p) {
				Point point = wayPoints.get(wayPointIndex);
				point.x = p.x;
				point.y = p.y;
				refreshVisualWith(wayPoints);
			}
			
			@Override
			public void commitAnchorPoint(int wayPointIndex, Point p) {
				getModel().setWayPoint(wayPointIndex, p);
			}
		});
	}

	@Override
	public FXGeometricCurve getModel() {
		return (FXGeometricCurve) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof FXGeometricCurve)) {
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
		// TODO: compare way points to identify if we need to refresh
		// use anchors as start and end point
		FXGeometricCurve curveVisual = getModel();
		List<Point> wayPoints = curveVisual.getWayPoints();
		refreshVisualWith(wayPoints);
	}
	
	private void refreshVisualWith(List<Point> wayPoints) {
		Point[] startEnd = computeStartEnd();
		ArrayList<Point> points = new ArrayList<Point>(wayPoints.size() + 2);
		points.add(startEnd[0]);
		points.addAll(wayPoints);
		points.add(startEnd[1]);
		visual.setGeometry(new Polyline(points.toArray(new Point[0])));
	}

	private Point[] computeStartEnd() {
		Node startNode = anchors.get(0).getAnchorage();
		Node endNode = anchors.get(1).getAnchorage();

		// compute reference points in local coordinate space
		Point startReference = JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						endNode.localToScene(endNode.getBoundsInLocal())))
				.getCenter();
		Point endReference = JavaFX2Geometry.toRectangle(
				getVisual().sceneToLocal(
						startNode.localToScene(startNode.getBoundsInLocal())))
				.getCenter();

		// compute new anchor positions
		Point start = anchors.get(0).getPosition(this.getVisual(),
				startReference);
		Point end = anchors.get(1).getPosition(this.getVisual(), endReference);
		
		Point[] startEnd = new Point[] { start, end };
		return startEnd;
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
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(IAnchor.REPRESH)) {
			if (anchors.size() == 2) {
				refreshVisual();
			}
		}
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		// System.out.println("getAnchor() == null ?!");
		return null;
	}

}
