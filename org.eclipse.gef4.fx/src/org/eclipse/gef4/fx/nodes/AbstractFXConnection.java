/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXConnection<T extends ICurve> extends Group
		implements IFXConnection {

	public static final String CSS_CLASS_DECORATION = "decoration";

	// visuals
	private FXGeometryNode<T> curveNode = new FXGeometryNode<T>();

	// TODO: use ReadOnlyObjectWrapper (JavaFX Property) for decorations
	private IFXDecoration startDecoration = null;
	private IFXDecoration endDecoration = null;

	// anchors
	protected ReadOnlyObjectWrapper<AnchorLink> startAnchorLinkProperty = new ReadOnlyObjectWrapper<AnchorLink>(
			null);
	protected ReadOnlyObjectWrapper<AnchorLink> endAnchorLinkProperty = new ReadOnlyObjectWrapper<AnchorLink>(
			null);
	protected ReadOnlyListWrapper<AnchorLink> wayPointAnchorLinksProperty = new ReadOnlyListWrapper<AnchorLink>(
			FXCollections.<AnchorLink> observableArrayList());

	protected boolean isEndConnected = false;
	protected boolean isStartConnected = false;

	// anchor management
	private ReadOnlyObjectWrapper<ChangeListener<? super AnchorLink>> onStartAnchorLinkChangeProperty = new ReadOnlyObjectWrapper<ChangeListener<? super AnchorLink>>(
			null);
	private ReadOnlyObjectWrapper<ChangeListener<? super AnchorLink>> onEndAnchorLinkChangeProperty = new ReadOnlyObjectWrapper<ChangeListener<? super AnchorLink>>(
			null);
	private ReadOnlyObjectWrapper<ListChangeListener<? super AnchorLink>> onWayPointAnchorLinkChangeProperty = new ReadOnlyObjectWrapper<ListChangeListener<? super AnchorLink>>(
			null);

	// refresh geometry on position changes
	protected MapChangeListener<? super AnchorKey, ? super Point> positionChangeListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			refreshGeometry();
		}
	};

	private boolean inRefresh = false;

	{
		// disable resizing children which would change their layout positions
		// in some cases
		setAutoSizeChildren(false);
		setStartPoint(new Point());
		setEndPoint(new Point());
		wayPointAnchorLinksProperty
				.addListener(new ListChangeListener<AnchorLink>() {
					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends AnchorLink> c) {
						refreshGeometry();
					}
				});
	}

	@Override
	public void addWayPoint(int index, Point wayPoint) {
		addWayPointAnchorLink(index,
				FXUtils.createStaticAnchorLink(this, this, wayPoint));
	}

	@Override
	public void addWayPointAnchorLink(int index, AnchorLink wayPointAnchorLink) {
		wayPointAnchorLink.getAnchor().attach(wayPointAnchorLink.getKey());
		wayPointAnchorLinksProperty.add(index, wayPointAnchorLink);
		wayPointAnchorLink.getAnchor().positionProperty()
				.addListener(positionChangeListener);
	}

	/**
	 * Arranges the given decoration according to the passed-in values. Returns
	 * the transformed end point of the arranged decoration.
	 * 
	 * @param deco
	 * @param start
	 * @param direction
	 * @param decoStart
	 * @param decoDirection
	 * @return the transformed end point of the arranged decoration
	 */
	protected Point arrangeDecoration(IFXDecoration deco, Point start,
			Vector direction, Point decoStart, Vector decoDirection) {
		Node visual = deco.getVisual();

		// position
		visual.setLayoutX(start.x);
		visual.setLayoutY(start.y);

		// rotation
		Angle angleCW = null;
		if (!direction.isNull() && !decoDirection.isNull()) {
			angleCW = decoDirection.getAngleCW(direction);
			visual.getTransforms().clear();
			visual.getTransforms().add(new Rotate(angleCW.deg(), 0, 0));
		}

		// return corresponding curve point
		return angleCW == null ? start : start.getTranslated(decoDirection
				.getRotatedCW(angleCW).toPoint());
	}

	protected void arrangeEndDecoration() {
		if (endDecoration == null) {
			return;
		}

		// determine curve end point and curve end direction
		Point endPoint = getEndPoint();
		ICurve curve = getCurveNode().getGeometry();
		if (curve == null) {
			return;
		}

		BezierCurve[] beziers = curve.toBezier();
		if (beziers.length == 0) {
			return;
		}

		BezierCurve endDerivative = beziers[beziers.length - 1].getDerivative();
		Point slope = endDerivative.get(1);
		if (slope.equals(0, 0)) {
			/*
			 * This is the case when beziers[-1] is a degenerated curve where
			 * the last control point equals the end point. As a work around, we
			 * evaluate the derivative at t = 0.99.
			 */
			slope = endDerivative.get(0.99);
		}
		Vector endDirection = new Vector(slope.getNegated());

		// determine decoration start point and decoration direction
		Point decoStartPoint = endDecoration.getLocalStartPoint();
		Point decoEndPoint = endDecoration.getLocalEndPoint();
		Vector decoDirection = new Vector(decoStartPoint, decoEndPoint);

		arrangeDecoration(endDecoration, endPoint, endDirection,
				decoStartPoint, decoDirection);
	}

	protected void arrangeStartDecoration() {
		if (startDecoration == null) {
			return;
		}

		// determine curve start point and curve start direction
		Point startPoint = getStartPoint();
		ICurve curve = getCurveNode().getGeometry();
		if (curve == null) {
			return;
		}

		BezierCurve[] beziers = curve.toBezier();
		if (beziers.length == 0) {
			return;
		}

		BezierCurve startDerivative = beziers[0].getDerivative();
		Point slope = startDerivative.get(0);
		if (slope.equals(0, 0)) {
			/*
			 * This is the case when beziers[0] is a degenerated curve where the
			 * start point equals the first control point. As a work around, we
			 * evaluate the derivative at t = 0.01.
			 */
			slope = startDerivative.get(0.01);
		}
		Vector curveStartDirection = new Vector(slope);

		// determine decoration start point and decoration start direction
		Point decoStartPoint = startDecoration.getLocalStartPoint();
		Point decoEndPoint = startDecoration.getLocalEndPoint();
		Vector decoDirection = new Vector(decoStartPoint, decoEndPoint);

		arrangeDecoration(startDecoration, startPoint, curveStartDirection,
				decoStartPoint, decoDirection);
	}

	public abstract T computeGeometry(Point[] points);

	@Override
	public ReadOnlyObjectProperty<AnchorLink> endAnchorLinkProperty() {
		return endAnchorLinkProperty.getReadOnlyProperty();
	}

	@Override
	public FXGeometryNode<T> getCurveNode() {
		return curveNode;
	}

	@Override
	public AnchorLink getEndAnchorLink() {
		return endAnchorLinkProperty.get();
	}

	@Override
	public IFXDecoration getEndDecoration() {
		return endDecoration;
	}

	@Override
	public Point getEndPoint() {
		AnchorLink link = endAnchorLinkProperty.get();
		return link == null ? null : link.getPosition();
	}

	@Override
	public AnchorLink[] getPointAnchorLinks() {
		int wayPointCount = wayPointAnchorLinksProperty.size();
		AnchorLink[] links = new AnchorLink[wayPointCount + 2];

		links[0] = getStartAnchorLink();
		if (links[0] == null) {
			return new AnchorLink[] {};
		}

		for (int i = 0; i < wayPointCount; i++) {
			links[i + 1] = wayPointAnchorLinksProperty.get(i);
			if (links[i + 1] == null) {
				return new AnchorLink[] {};
			}
		}

		links[links.length - 1] = getEndAnchorLink();
		if (links[links.length - 1] == null) {
			return new AnchorLink[] {};
		}

		return links;
	}

	@Override
	public Point[] getPoints() {
		int wayPointCount = wayPointAnchorLinksProperty.size();
		Point[] points = new Point[wayPointCount + 2];

		points[0] = getStartPoint();
		if (points[0] == null) {
			return new Point[] {};
		}

		for (int i = 0; i < wayPointCount; i++) {
			points[i + 1] = wayPointAnchorLinksProperty.get(i).getPosition();
			if (points[i + 1] == null) {
				return new Point[] {};
			}
		}

		points[points.length - 1] = getEndPoint();
		if (points[points.length - 1] == null) {
			return new Point[] {};
		}

		return points;
	}

	@Override
	public AnchorLink getStartAnchorLink() {
		return startAnchorLinkProperty.get();
	}

	@Override
	public IFXDecoration getStartDecoration() {
		return startDecoration;
	}

	@Override
	public Point getStartPoint() {
		AnchorLink link = startAnchorLinkProperty.get();
		return link == null ? null : link.getPosition();
	}

	@Override
	public Node getVisual() {
		return this;
	}

	@Override
	public Point getWayPoint(int index) {
		AnchorLink link = getWayPointAnchorLink(index);
		return link.getPosition();
	}

	@Override
	public AnchorLink getWayPointAnchorLink(int index) {
		return wayPointAnchorLinksProperty.get(index);
	}

	@Override
	public List<AnchorLink> getWayPointAnchorLinks() {
		return wayPointsProperty().get(); // read-only
	}

	@Override
	public List<Point> getWayPoints() {
		int wayPointsCount = wayPointAnchorLinksProperty.size();
		List<Point> wayPoints = new ArrayList<Point>(wayPointsCount);
		for (int i = 0; i < wayPointsCount; i++) {
			wayPoints.add(getWayPoint(i));
		}
		return wayPoints;
	}

	@Override
	public boolean isEndConnected() {
		return isEndConnected;
	}

	@Override
	public boolean isStartConnected() {
		return isStartConnected;
	}

	@Override
	public boolean isWayPointConnected(int index) {
		return !(getWayPointAnchorLink(index).getAnchor() instanceof FXStaticAnchor);
	}

	@Override
	public ReadOnlyObjectProperty<ChangeListener<? super AnchorLink>> onEndAnchorLinkChangeProperty() {
		return onEndAnchorLinkChangeProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<ChangeListener<? super AnchorLink>> onStartAnchorLinkChangeProperty() {
		return onStartAnchorLinkChangeProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<ListChangeListener<? super AnchorLink>> onWayPointAnchorLinkChangeProperty() {
		return onWayPointAnchorLinkChangeProperty.getReadOnlyProperty();
	}

	protected void refreshGeometry() {
		// guard against recomputing the curve while recomputing the curve
		if (inRefresh) {
			return;
		}
		inRefresh = true;

		// clear current visuals
		getChildren().clear();

		// compute new curve (this can lead to another refreshGeometry() call
		// which is not executed)
		curveNode.setGeometry(computeGeometry(getPoints()));

		// z-order decorations above curve
		getChildren().add(curveNode);
		if (startDecoration != null) {
			getChildren().add(startDecoration.getVisual());
			arrangeStartDecoration();
		}
		if (endDecoration != null) {
			getChildren().add(endDecoration.getVisual());
			arrangeEndDecoration();
		}

		inRefresh = false;
	}

	@Override
	public void removeAllWayPoints() {
		for (int i = wayPointAnchorLinksProperty.size() - 1; i >= 0; i--) {
			removeWayPoint(i);
		}
	}

	@Override
	public void removeWayPoint(int index) {
		// check index out of range
		if (index < 0 || index >= wayPointAnchorLinksProperty.get().size()) {
			throw new IllegalArgumentException("Index out of range (index: "
					+ index + ", size: " + wayPointAnchorLinksProperty.size()
					+ ").");
		}
		AnchorLink oldLink = wayPointAnchorLinksProperty.get(index);
		oldLink.getAnchor().detach(oldLink.getKey());
		wayPointAnchorLinksProperty.remove(index);
	}

	@Override
	public void setEndAnchorLink(AnchorLink endAnchorLink) {
		if (endAnchorLink == null) {
			throw new IllegalArgumentException(
					"The given AnchorLink may not be <null>.");
		}

		// unregister change listener on old link
		AnchorLink oldLink = endAnchorLinkProperty.get();
		ChangeListener<? super AnchorLink> listener = onEndAnchorLinkChangeProperty
				.get();
		if (oldLink != null) {
			endAnchorLinkProperty.get().getAnchor().positionProperty()
					.removeListener(positionChangeListener);
			if (listener != null) {
				endAnchorLinkProperty.removeListener(listener);
			}
			oldLink.getAnchor().detach(oldLink.getKey());
		}

		// set new link and register change listener
		if (listener != null) {
			endAnchorLinkProperty.addListener(listener);
		}
		endAnchorLink.getAnchor().attach(endAnchorLink.getKey());
		endAnchorLinkProperty.set(endAnchorLink);
		isEndConnected = !(endAnchorLink.getAnchor() instanceof FXStaticAnchor);
		endAnchorLinkProperty.get().getAnchor().positionProperty()
				.addListener(positionChangeListener);
		refreshGeometry();
	}

	@Override
	public void setEndDecoration(IFXDecoration endDeco) {
		endDecoration = endDeco;
		if (endDecoration != null) {
			ObservableList<String> styleClasses = endDecoration.getVisual()
					.getStyleClass();
			if (!styleClasses.contains(CSS_CLASS_DECORATION)) {
				styleClasses.add(CSS_CLASS_DECORATION);
			}
		}
		refreshGeometry();
	}

	@Override
	public void setEndPoint(Point p) {
		AnchorKey key = new AnchorKey(this, "END");
		FXStaticAnchor anchor = new FXStaticAnchor(key, p);
		setEndAnchorLink(new AnchorLink(anchor, key));
	}

	@Override
	public void setOnEndAnchorLinkChange(
			ChangeListener<? super AnchorLink> onEndAnchorLinkChange) {
		// unregister old listener
		ChangeListener<? super AnchorLink> oldListener = onEndAnchorLinkChangeProperty
				.get();
		if (oldListener != null) {
			endAnchorLinkProperty.removeListener(oldListener);
		}
		// set property
		onEndAnchorLinkChangeProperty.set(onEndAnchorLinkChange);
		// register new listener
		if (onEndAnchorLinkChange != null) {
			endAnchorLinkProperty.addListener(onEndAnchorLinkChange);
		}
	}

	@Override
	public void setOnStartAnchorLinkChange(
			ChangeListener<? super AnchorLink> onStartAnchorLinkChange) {
		// unregister old listener
		ChangeListener<? super AnchorLink> oldListener = onStartAnchorLinkChangeProperty
				.get();
		if (oldListener != null) {
			startAnchorLinkProperty.removeListener(oldListener);
		}
		// set property
		onStartAnchorLinkChangeProperty.set(onStartAnchorLinkChange);
		// register new listener
		if (onStartAnchorLinkChange != null) {
			startAnchorLinkProperty.addListener(onStartAnchorLinkChange);
		}
	}

	@Override
	public void setOnWayPointAnchorLinkChange(
			ListChangeListener<? super AnchorLink> onWayPointChange) {
		ListChangeListener<? super AnchorLink> oldListener = onWayPointAnchorLinkChangeProperty
				.get();
		if (oldListener != null) {
			wayPointAnchorLinksProperty.removeListener(oldListener);
		}
		onWayPointAnchorLinkChangeProperty.set(onWayPointChange);
		if (onWayPointChange != null) {
			wayPointAnchorLinksProperty.addListener(onWayPointChange);
		}
	}

	@Override
	public void setStartAnchorLink(AnchorLink startAnchorLink) {
		if (startAnchorLink == null) {
			throw new IllegalArgumentException(
					"The given AnchorLink may not be <null>.");
		}

		// unregister change listener on old link
		AnchorLink oldLink = startAnchorLinkProperty.get();
		ChangeListener<? super AnchorLink> listener = onStartAnchorLinkChangeProperty
				.get();
		if (oldLink != null) {
			startAnchorLinkProperty.get().getAnchor().positionProperty()
					.removeListener(positionChangeListener);
			if (listener != null) {
				startAnchorLinkProperty.removeListener(listener);
			}
			oldLink.getAnchor().detach(oldLink.getKey());
		}

		// set new link and register change listener
		if (listener != null) {
			startAnchorLinkProperty.addListener(listener);
		}
		startAnchorLink.getAnchor().attach(startAnchorLink.getKey());
		startAnchorLinkProperty.set(startAnchorLink);
		isStartConnected = !(startAnchorLink.getAnchor() instanceof FXStaticAnchor);
		startAnchorLinkProperty.get().getAnchor().positionProperty()
				.addListener(positionChangeListener);
		refreshGeometry();
	}

	@Override
	public void setStartDecoration(IFXDecoration startDeco) {
		startDecoration = startDeco;
		if (startDecoration != null) {
			ObservableList<String> styleClasses = startDecoration.getVisual()
					.getStyleClass();
			if (!styleClasses.contains(CSS_CLASS_DECORATION)) {
				styleClasses.add(CSS_CLASS_DECORATION);
			}
		}
		refreshGeometry();
	}

	@Override
	public void setStartPoint(Point p) {
		AnchorKey key = new AnchorKey(this, "START");
		FXStaticAnchor anchor = new FXStaticAnchor(key, p);
		setStartAnchorLink(new AnchorLink(anchor, key));
	}

	@Override
	public void setWayPoint(int index, Point wayPoint) {
		setWayPointAnchorLink(index,
				FXUtils.createStaticAnchorLink(this, this, wayPoint));
	}

	@Override
	public void setWayPointAnchorLink(int index, AnchorLink wayPointAnchorLink) {
		AnchorLink oldLink = wayPointAnchorLinksProperty.get(index);
		oldLink.getAnchor().detach(oldLink.getKey());
		wayPointAnchorLink.getAnchor().attach(wayPointAnchorLink.getKey());
		wayPointAnchorLinksProperty.set(index, wayPointAnchorLink);
	}

	@Override
	public void setWayPointAnchorLinks(List<AnchorLink> wayPointAnchorLinks) {
		removeAllWayPoints();
		for (AnchorLink link : wayPointAnchorLinks) {
			addWayPointAnchorLink(this.wayPointAnchorLinksProperty.size(), link);
		}
	}

	@Override
	public void setWayPoints(List<Point> wayPoints) {
		removeAllWayPoints();
		for (Point wp : wayPoints) {
			addWayPoint(this.wayPointAnchorLinksProperty.size(), wp);
		}
	}

	@Override
	public ReadOnlyObjectProperty<AnchorLink> startAnchorLinkProperty() {
		return startAnchorLinkProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyListProperty<AnchorLink> wayPointsProperty() {
		return wayPointAnchorLinksProperty.getReadOnlyProperty();
	}

}
