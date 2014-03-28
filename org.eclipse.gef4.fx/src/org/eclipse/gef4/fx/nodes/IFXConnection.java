package org.eclipse.gef4.fx.nodes;

import java.util.List;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.Point;

public interface IFXConnection {

	/**
	 * Anchor context information specifying the type of anchor changed (start,
	 * end, way-point).
	 */
	public static final String ANCHOR_CONTEXT_TYPE = "type";

	/**
	 * Anchor context information identifying the concrete anchor changed (index
	 * of way-point). This information is not necessary for start/end anchors.
	 */
	public static final String ANCHOR_CONTEXT_INDEX = "index";
	
	/**
	 * Returns the {@link IFXAnchor} which specifies the start position of this connection.
	 * @return the {@link IFXAnchor} which specifies the start position of this connection
	 */
	public IFXAnchor getStartAnchor();
	
	/**
	 * Returns the {@link IFXAnchor} which specifies the end position of this connection.
	 * @return the {@link IFXAnchor} which specifies the end position of this connection
	 */
	public IFXAnchor getEndAnchor();
	
	/**
	 * Changes the start anchor of this connection to the passed-in {@link IFXAnchor}.
	 */
	public void setStartAnchor(IFXAnchor startAnchor);
	
	/**
	 * Changes the end anchor of this connection to the passed-in {@link IFXAnchor}.
	 */
	public void setEndAnchor(IFXAnchor endAnchor);
	
	/**
	 * Returns an unmodifiable list of {@link IFXAnchor}s which specify the positions of this connection's way points.
	 * @return an unmodifiable list of {@link IFXAnchor}s which specify the positions of this connection's way points
	 */
	public List<IFXAnchor> getWayPointAnchors();
	
	/**
	 * Sets the way point anchor at the given index to the given {@link IFXAnchor}.
	 * @param index
	 * @param wayPointAnchor
	 */
	public void setWayPointAnchor(int index, IFXAnchor wayPointAnchor);
	
	/**
	 * Inserts a new way point (anchor) at the specified index.
	 * @param index
	 * @param wayPointAnchor
	 */
	public void addWayPointAnchor(int index, IFXAnchor wayPointAnchor);
	
	/**
	 * Removes the specified way point from this connection.
	 * @param index
	 */
	public void removeWayPoint(int index);
	
	/**
	 * Removes all way points from this connection.
	 */
	public void removeAllWayPoints();

	/**
	 * Returns an unmodifiable list of way points (not their anchors).
	 * @return an unmodifiable list of way points
	 */
	public List<Point> getWayPoints();

	/**
	 * Sets all way point anchors of this connection to static anchors pointing to the given list of points.
	 * @param wayPoints
	 */
	public void setWayPoints(List<Point> wayPoints);

	/**
	 * Sets the specified way point anchor to a static anchor pointing to the given position.
	 * @param index
	 * @param wayPoint
	 */
	public void setWayPoint(int index, Point wayPoint);

	/**
	 * Adds a new static anchored way point at the specified position.
	 * @param index
	 * @param wayPoint
	 */
	public void addWayPoint(int index, Point wayPoint);

	/**
	 * Returns the start point (not anchor) of this connection.
	 * @return the start point of this connection
	 */
	public Point getStartPoint();
	
	/**
	 * Sets the start point anchor of this connection to a static anchor pointing to the given startPoint.
	 * @param startPoint
	 */
	public void setStartPoint(Point startPoint);

	/**
	 * Returns the end point (not anchor) of this connection.
	 * @return the end point of this connection
	 */
	public Point getEndPoint();
	
	/**
	 * Sets the end point anchor of this connection to a static anchor pointing to the given endPoint.
	 * @param endPoint
	 */
	public void setEndPoint(Point endPoint);

	/**
	 * Returns an array containing all points (not anchors) constituting this connection, i.e. start point, way points, and end point.
	 * @return an array containing all points constituting this connection
	 */
	public Point[] getPoints();

}