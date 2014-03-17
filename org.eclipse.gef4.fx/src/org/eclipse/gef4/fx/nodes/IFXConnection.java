package org.eclipse.gef4.fx.nodes;

import java.util.List;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.Point;

public interface IFXConnection {

	/**
	 * Returns an unmodifiable list of way points.
	 * 
	 * @return
	 */
	public abstract List<Point> getWayPoints();

	public abstract void setWayPoints(List<Point> wayPoints);

	public abstract void setWayPoints(Point... wayPoints);

	public abstract void setWayPoint(int index, Point wayPoint);

	public abstract void removeWayPoint(int index);

	public abstract void addWayPoint(int index, Point wayPoint);

	public abstract IFXAnchor getEndAnchor();

	public abstract void loosenStartAnchor();

	public abstract void loosenEndAnchor();

	public abstract Point getStartPoint();

	public abstract Point getEndPoint();

	public abstract void setEndAnchor(IFXAnchor endAnchor);

	public abstract IFXAnchor getStartAnchor();

	public abstract void setStartAnchor(IFXAnchor startAnchor);

	public abstract Point[] getPoints();

}