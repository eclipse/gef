package org.eclipse.gef4.fx.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.MapChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXConnection<T extends IGeometry> extends
		FXGeometryNode<T> implements IFXConnection {

	private IFXAnchor startAnchor = new FXStaticAnchor(this, new Point());
	private IFXAnchor endAnchor = new FXStaticAnchor(this, new Point());
	private List<IFXAnchor> wayPointAnchors = new ArrayList<IFXAnchor>();

	private MapChangeListener<Node, Point> startPosCL = createStartPositionListener();
	private MapChangeListener<Node, Point> endPosCL = createEndPositionListener();
	private MapChangeListener<Node, Point> wayPosCL = createWayPositionListener();

	@Override
	public IFXAnchor getStartAnchor() {
		return startAnchor;
	}

	@Override
	public IFXAnchor getEndAnchor() {
		return endAnchor;
	}

	@Override
	public void setStartAnchor(IFXAnchor startAnchor) {
		this.startAnchor.positionProperty().removeListener(startPosCL);
		this.startAnchor = startAnchor;
		startAnchor.positionProperty().addListener(startPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public void setEndAnchor(IFXAnchor endAnchor) {
		this.endAnchor.positionProperty().removeListener(endPosCL);
		this.endAnchor = endAnchor;
		endAnchor.positionProperty().addListener(endPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public List<IFXAnchor> getWayPointAnchors() {
		return Collections.unmodifiableList(wayPointAnchors);
	}

	@Override
	public void setWayPointAnchor(int index, IFXAnchor wayPointAnchor) {
		wayPointAnchors.get(index).positionProperty().removeListener(wayPosCL);
		wayPointAnchors.set(index, wayPointAnchor);
		wayPointAnchor.positionProperty().addListener(wayPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public void addWayPointAnchor(int index, IFXAnchor wayPointAnchor) {
		wayPointAnchors.add(index, wayPointAnchor);
		wayPointAnchor.positionProperty().addListener(wayPosCL);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public void removeWayPoint(int index) {
		IFXAnchor anchor = wayPointAnchors.get(index);
		anchor.positionProperty().removeListener(wayPosCL);
		wayPointAnchors.remove(index);
		refreshReferencePoints();
		refreshGeometry();
	}

	@Override
	public List<Point> getWayPoints() {
		List<Point> wayPoints = new ArrayList<Point>(wayPointAnchors.size());
		for (int i = 0; i < wayPointAnchors.size(); i++) {
			wayPoints.add(wayPointAnchors.get(i).getPosition(this));
		}
		return wayPoints;
	}

	@Override
	public void setWayPoints(List<Point> wayPoints) {
		removeAllWayPoints();
		for (Point wp : wayPoints) {
			addWayPoint(wayPointAnchors.size(), wp);
		}
	}
	
	@Override
	public void removeAllWayPoints() {
		for (int i = wayPointAnchors.size() - 1; i >= 0; i--) {
			removeWayPoint(i);
		}
	}

	@Override
	public void setWayPoint(int index, Point wayPoint) {
		setWayPointAnchor(index, new FXStaticAnchor(this, wayPoint));
	}

	@Override
	public void addWayPoint(int index, Point wayPoint) {
		addWayPointAnchor(index, new FXStaticAnchor(this, wayPoint));
	}

	@Override
	public Point getStartPoint() {
		return startAnchor.getPosition(this);
	}

	@Override
	public void setStartPoint(Point startPoint) {
		setStartAnchor(new FXStaticAnchor(this, startPoint));
	}

	@Override
	public Point getEndPoint() {
		return endAnchor.getPosition(this);
	}

	@Override
	public void setEndPoint(Point endPoint) {
		setEndAnchor(new FXStaticAnchor(this, endPoint));
	}

	@Override
	public Point[] getPoints() {
		List<Point> wayPoints = getWayPoints();
		Point[] points = new Point[wayPoints.size() + 2];
		
		points[0] = getStartPoint();
		int i = 1;
		for (Point wp : wayPoints)
			points[i++] = wp;
		points[points.length - 1] = getEndPoint();
		
		return points;
	}

	protected void refreshGeometry() {
		setGeometry(computeGeometry());
	}

	public abstract T computeGeometry();
	
	/**
	 * Updates the start and end anchor reference points after computing them
	 * using {@link #computeReferencePoints()}.
	 */
	protected void refreshReferencePoints() {
		Point[] referencePoints = computeReferencePoints();
		if (!(startAnchor instanceof FXStaticAnchor)) {
			startAnchor.setReferencePoint(this, referencePoints[0]);
		}
		if (!(endAnchor instanceof FXStaticAnchor)) {
			endAnchor.setReferencePoint(this, referencePoints[1]);
		}
	}

	/**
	 * Returns a {@link Point} array containing reference points for the start
	 * and end anchors.
	 * 
	 * @return
	 */
	public Point[] computeReferencePoints() {
		// compute start/end point in local coordinate space
		Point start = getStartPoint();
		Point end = getEndPoint();

		// find reference points
		Point startReference = end;
		Point endReference = start;

		// first uncontained way point is start reference
		Node startNode = startAnchor.getAnchorageNode();
		if (startNode != null) {
			for (Point p : getWayPoints()) {
				Point2D local = startNode.sceneToLocal(localToScene(p.x, p.y));
				if (!startNode.contains(local)) {
					startReference = p;
					break;
				}
			}
		}

		// last uncontained way point is end reference
		Node endNode = endAnchor.getAnchorageNode();
		if (endNode != null) {
			for (Point p : getWayPoints()) {
				Point2D local = endNode.sceneToLocal(localToScene(p.x, p.y));
				if (!endNode.contains(local)) {
					endReference = p;
				}
			}
		}

		return new Point[] { startReference, endReference };
	}

	private MapChangeListener<Node, Point> createStartPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == AbstractFXConnection.this) {
					Point[] referencePoints = computeReferencePoints();
					if (!(endAnchor instanceof FXStaticAnchor)) {
						endAnchor.setReferencePoint(AbstractFXConnection.this,
								referencePoints[1]);
					}
					refreshGeometry();
				}
			}
		};
	}

	private MapChangeListener<Node, Point> createEndPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == AbstractFXConnection.this) {
					Point[] referencePoints = computeReferencePoints();
					if (!(startAnchor instanceof FXStaticAnchor)) {
						startAnchor.setReferencePoint(
								AbstractFXConnection.this, referencePoints[0]);
					}
					refreshGeometry();
				}
			}
		};
	}
	
	private MapChangeListener<Node, Point> createWayPositionListener() {
		return new MapChangeListener<Node, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
				Node anchored = change.getKey();
				if (anchored == AbstractFXConnection.this) {
					refreshReferencePoints();
					refreshGeometry();
				}
			}
		};
	}

}
