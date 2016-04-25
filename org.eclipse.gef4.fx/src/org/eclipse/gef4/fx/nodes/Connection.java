/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen  (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.utils.Geometry2Shape;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * A (binary) {@link Connection} is a visual curve, whose appearance is defined
 * through a single start and end point, and a set of control points, which may
 * be 'connected', i.e. be attached to an {@link IAnchor}. The exact curve shape
 * is determined by an {@link IConnectionRouter}, which is responsible of
 * computing an {@link ICurve} geometry for a given {@link Connection} (which is
 * then rendered using a {@link GeometryNode}).
 * <p>
 * Whether the control points are interpreted as way points (that lie on the
 * curve) or as 'real' control points depends on the
 * {@link IConnectionInterpolator}. While {@link PolylineInterpolator} and
 * {@link PolyBezierInterpolator} interpret control points to be way points,
 * other routers may e.g. interpret them as the control points of a
 * {@link BezierCurve}.
 * <P>
 * In addition to the curve shape, the visual appearance of a {@link Connection}
 * can be controlled via start and end decorations. They will be rendered
 * 'on-top' of the curve shape and the curve shape will be properly clipped at
 * the decorations (so it does not paint through).
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class Connection extends Group {

	/**
	 * CSS class assigned to decoration visuals.
	 */
	public static final String CSS_CLASS_DECORATION = "decoration";

	/**
	 * The <i>id</i> used to identify the start point of this connection at the
	 * start anchor.
	 */
	private static final String START_ROLE = "start";

	/**
	 * The <i>id</i> used to identify the end point of this connection at the
	 * end anchor.
	 */
	private static final String END_ROLE = "end";

	/**
	 * Prefix for the default <i>ids</i> used by this connection to identify
	 * specific control points at control point anchors.
	 */
	private static final String CONTROL_POINT_ROLE_PREFIX = "controlpoint-";

	// visuals
	private GeometryNode<ICurve> curveNode = new GeometryNode<>();

	// TODO: use properties (JavaFX Property) for decorations
	private Shape startDecoration = null;

	private Shape endDecoration = null;
	private ObjectProperty<IConnectionRouter> routerProperty = new SimpleObjectProperty<IConnectionRouter>(
			new StraightRouter());

	private ObjectProperty<IConnectionInterpolator> interpolatorProperty = new SimpleObjectProperty<IConnectionInterpolator>(
			new PolylineInterpolator());

	private ReadOnlyMapWrapper<AnchorKey, IAnchor> anchorsProperty = new ReadOnlyMapWrapperEx<>(
			FXCollections.<AnchorKey, IAnchor> observableHashMap());

	// control anchors are kept in an ordered set (sorted by their indices)
	private SortedSet<AnchorKey> controlAnchorKeys = new TreeSet<>(
			new Comparator<AnchorKey>() {

				@Override
				public int compare(AnchorKey o1, AnchorKey o2) {
					int o1Index = getControlAnchorIndex(o1);
					int o2Index = getControlAnchorIndex(o2);
					return o1Index - o2Index;
				}
			});
	// refresh geometry on position changes
	private boolean inRefresh = false;
	private Map<AnchorKey, MapChangeListener<? super AnchorKey, ? super Point>> anchorPCL = new HashMap<>();

	// refresh on decoration bounds changes (stroke width)
	private ChangeListener<Bounds> decorationLayoutBoundsListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			// refresh decoration clip in case the layout bounds of
			// the decorations have changed
			// TODO: optimize that only the decorations are refreshed in this
			// case
			refresh();
		}
	};

	/**
	 * Constructs a new {@link Connection} whose start and end point are set to
	 * <code>null</code>.
	 */
	public Connection() {
		// disable resizing children which would change their layout positions
		// in some cases
		setAutoSizeChildren(false);

		// ensure connection does not paint further than geometric end points
		// getCurveNode().setStrokeLineCap(StrokeLineCap.BUTT);

		// add the curve node
		getChildren().add(curveNode);
	}

	/**
	 * Inserts the given {@link IAnchor} into the {@link #anchorsProperty()} of
	 * this {@link Connection}. The given {@link AnchorKey} is attached to the
	 * {@link IAnchor}. Furthermore, a {@link #createPCL(AnchorKey) PCL} for the
	 * {@link AnchorKey} is registered on the position property of the
	 * {@link IAnchor} and the visualization is {@link #refresh() refreshed}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} under which the {@link IAnchor} is to be
	 *            registered.
	 * @param anchor
	 *            The {@link IAnchor} which is inserted.
	 */
	protected void addAnchor(AnchorKey anchorKey, IAnchor anchor) {
		if (anchorKey == null) {
			throw new IllegalArgumentException("anchorKey may not be null.");
		}
		if (anchorKey.getAnchored() != getCurveNode()) {
			throw new IllegalArgumentException(
					"anchorKey may only be anchored to curve node");
		}
		if (!anchorsProperty.containsKey(anchorKey)) {
			if (!getStartAnchorKey().equals(anchorKey)
					&& !getEndAnchorKey().equals(anchorKey)
					&& controlAnchorKeys.contains(anchorKey)) {
				throw new IllegalStateException(
						"anchorKey is not contained but key is registered in control anchor key map.");

			}
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		/*
		 * XXX: The anchor is put into the map before attaching it, so that
		 * listeners on the map can register position change listeners on the
		 * anchor (but cannot query its position, yet).
		 */
		// controlpoints to move are kept in reverse order
		List<IAnchor> controlAnchorsToMove = new ArrayList<>();
		if (!anchorKey.equals(

				getStartAnchorKey()) && !anchorKey.equals(getEndAnchorKey())) {
			int controlIndex = getControlAnchorIndex(anchorKey);
			// remove all controlpoints at a larger index
			int controlPointCount = controlAnchorKeys.size();
			for (int i = controlPointCount - 1; i >= controlIndex; i--) {
				// (temporarily) remove all anchors that are to be moved up
				AnchorKey ak = getControlAnchorKey(i);
				IAnchor a = getControlAnchor(i);
				controlAnchorsToMove.add(0, a);
				controlAnchorKeys.remove(ak);
				anchorsProperty.remove(ak);
				a.detach(ak);
			}
			controlAnchorKeys.add(anchorKey);
		}
		anchorsProperty.put(anchorKey, anchor);
		anchor.attach(anchorKey);

		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			int controlIndex = getControlAnchorIndex(anchorKey);
			// re-add all controlpoints at a larger index
			for (int i = 0; i < controlAnchorsToMove.size(); i++) {
				AnchorKey ak = getControlAnchorKey(controlIndex + i + 1);
				IAnchor a = controlAnchorsToMove.get(i);
				controlAnchorKeys.add(ak);
				anchorsProperty.put(ak, a);
				a.attach(ak);
			}
		}

		if (!anchorPCL.containsKey(anchorKey)) {
			MapChangeListener<? super AnchorKey, ? super Point> pcl = createPCL(
					anchorKey);
			anchorPCL.put(anchorKey, pcl);
			anchor.positionsUnmodifiableProperty().addListener(pcl);
		}
		refresh();
	}

	/**
	 * Adds the given {@link IAnchor} as a control point anchor for the given
	 * index into the {@link #anchorsProperty()} of this {@link Connection}.
	 *
	 * @param index
	 *            The position where the {@link IAnchor} is inserted within the
	 *            control point anchors of this {@link Connection}.
	 * @param anchor
	 *            The {@link IAnchor} which determines the position of the
	 *            corresponding control point.
	 */
	public void addControlAnchor(int index, IAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}
		addAnchor(getControlAnchorKey(index), anchor);
	}

	/**
	 * Adds an {@link StaticAnchor} yielding the given {@link Point} as a
	 * control point anchor for the given index into the
	 * {@link #anchorsProperty()} of this {@link Connection}.
	 *
	 * @param index
	 *            The position where the {@link IAnchor} is inserted within the
	 *            control point anchors of this {@link Connection}.
	 * @param controlPointInLocal
	 *            The position for the specified control point.
	 */
	public void addControlPoint(int index, Point controlPointInLocal) {
		if (controlPointInLocal == null) {
			controlPointInLocal = new Point();
		}
		IAnchor anchor = new StaticAnchor(this, controlPointInLocal);
		addControlAnchor(index, anchor);
	}

	/**
	 * Returns the {@link ReadOnlyMapProperty} which stores the
	 * {@link AnchorKey}s and corresponding {@link IAnchor}s which determine the
	 * start point, control points, and end point of this {@link Connection}.
	 *
	 * @return The {@link ReadOnlyMapProperty} which stores the
	 *         {@link AnchorKey}s and corresponding {@link IAnchor}s which
	 *         determine the start point, control points, and end point of this
	 *         {@link Connection}
	 */
	protected ReadOnlyMapProperty<AnchorKey, IAnchor> anchorsProperty() {
		return anchorsProperty.getReadOnlyProperty();
	}

	/**
	 * Arranges the given decoration according to the passed-in values.
	 *
	 * @param decoration
	 *            The decoration {@link Node} to arrange.
	 * @param start
	 *            The offset for the decoration visual.
	 * @param direction
	 *            The direction of the {@link Connection} at the point where the
	 *            decoration is arranged.
	 */
	protected void arrangeDecoration(Shape decoration, Point start,
			Vector direction) {
		decoration.getTransforms().clear();

		// arrange on start of curve.
		decoration.getTransforms().add(new Translate(start.x, start.y));

		// arrange on curve direction
		if (!direction.isNull()) {
			Angle angleCW = new Vector(1, 0).getAngleCW(direction);
			decoration.getTransforms().add(new Rotate(angleCW.deg(), 0, 0));
		}

		// compensate stroke (ensure decoration 'ends' at curve end).
		decoration.getTransforms().add(
				new Translate(-NodeUtils.getShapeBounds(decoration).getX(), 0));
	}

	/**
	 * Updates the end decoration of this {@link Connection}.
	 */
	protected void arrangeEndDecoration() {
		if (endDecoration == null) {
			return;
		}

		// determine curve end point and curve end direction
		Point endPoint = getEndPoint();
		ICurve curve = getCurveNode().getGeometry();
		if (curve == null || endPoint == null) {
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

		arrangeDecoration(endDecoration, endPoint, endDirection);
	}

	/**
	 * Updates the start decoration of this {@link Connection}.
	 */
	protected void arrangeStartDecoration() {
		if (startDecoration == null) {
			return;
		}

		// determine curve start point and curve start direction
		Point startPoint = getStartPoint();
		ICurve curve = getCurveNode().getGeometry();
		if (curve == null || startPoint == null) {
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
		arrangeDecoration(startDecoration, startPoint, curveStartDirection);
	}

	/**
	 * Adjusts the curveClip so that the curve node does not paint through the
	 * given decoration.
	 *
	 * @param curveClip
	 *            A shape that represents the clip of the curve node,
	 *            interpreted in scene coordinates.
	 * @param decoration
	 *            The decoration to clip the curve node from.
	 * @return A shape representing the resulting clip, interpreted in scene
	 *         coordinates.
	 */
	protected Shape clipAtDecoration(Shape curveClip, Shape decoration) {
		// first intersect curve shape with decoration layout bounds,
		// then subtract the curve shape from the result, and the decoration
		// from that
		Path decorationShapeBounds = new Path(
				Geometry2Shape.toPathElements(NodeUtils
						.localToScene(decoration,
								NodeUtils.getShapeBounds(decoration))
						.toPath()));
		decorationShapeBounds.setFill(Color.RED);
		Shape clip = Shape.intersect(decorationShapeBounds,
				curveNode.getGeometricShape());
		clip = Shape.subtract(clip, decoration);
		clip = Shape.subtract(curveClip, clip);
		return clip;
	}

	/**
	 * Creates a position change listener (PCL) which {@link #refresh()
	 * refreshes} this {@link Connection} upon anchor position changes
	 * corresponding to the given {@link AnchorKey}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} for which a position change will trigger
	 *            a {@link #refresh()} with the returned PCL.
	 * @return A position change listener to {@link #refresh() refresh} this
	 *         {@link Connection} when the position for the given
	 *         {@link AnchorKey} changes.
	 */
	protected MapChangeListener<? super AnchorKey, ? super Point> createPCL(
			final AnchorKey anchorKey) {
		return new MapChangeListener<AnchorKey, Point>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
				if (change.getKey().equals(anchorKey)) {
					refresh();
				}
			}
		};
	}

	/**
	 * Returns the anchor at the given index. The start anchor will be provided
	 * for <code>index == 0</code>, the end anchor for the last defined index.
	 * Control anchors will be returned for all indices in between.
	 *
	 * @param index
	 *            The index of the anchor to retrieve.
	 * @return The anchor at the given index.
	 */
	public IAnchor getAnchor(int index) {
		if (index == 0) {
			return getStartAnchor();
		} else if (index == controlAnchorKeys.size() + 1) {
			return getEndAnchor();
		} else {
			return getControlAnchor(index - 1);
		}
	}

	/**
	 * Returns the anchor index for the given {@link AnchorKey} which is:
	 * <ul>
	 * <li><code>0</code> for the {@link #getStartAnchorKey() start anchor key}
	 * </li>
	 * <li>{@link #getAnchors()}<code>.size() - 1</code> for the
	 * {@link #getEndAnchorKey() end anchor key}</li>
	 * <li>{@link #getControlAnchorIndex(AnchorKey)}<code> + 1</code> for
	 * control point anchor keys</li>
	 * </ul>
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} for which the anchor index is
	 *            determined.
	 * @return The anchor index for the given {@link AnchorKey}.
	 */
	public int getAnchorIndex(AnchorKey anchorKey) {
		if (anchorKey.equals(getStartAnchorKey())) {
			return 0;
		} else if (anchorKey.equals(getEndAnchorKey())) {
			return getAnchors().size() - 1;
		} else {
			return getControlAnchorIndex(anchorKey) + 1;
		}
	}

	/**
	 * Returns the {@link AnchorKey} for the given anchor index, i.e. the
	 * reverse of {@link #getAnchorIndex(AnchorKey)}.
	 *
	 * @param anchorIndex
	 *            The anchor index for which to determine the {@link AnchorKey}.
	 * @return The {@link AnchorKey} for the given anchor index.
	 */
	public AnchorKey getAnchorKey(int anchorIndex) {
		if (anchorIndex < 0 || anchorIndex >= getAnchors().size()) {
			throw new IllegalArgumentException(
					"The given anchor index is out of bounds.");
		}

		if (anchorIndex == 0) {
			return getStartAnchorKey();
		} else if (anchorIndex == getAnchors().size() - 1) {
			return getEndAnchorKey();
		} else {
			return getControlAnchorKey(anchorIndex - 1);
		}
	}

	/**
	 * Returns a {@link List} containing the {@link IAnchor}s which are assigned
	 * to this {@link Connection} in the order: start anchor, control point
	 * anchors, end anchor.
	 *
	 * @return A {@link List} containing the {@link IAnchor}s which are assigned
	 *         to this {@link Connection}.
	 */
	public List<IAnchor> getAnchors() {
		int controlPointCount = controlAnchorKeys.size();
		List<IAnchor> anchors = new ArrayList<>(controlPointCount + 2);

		// start anchor
		IAnchor startAnchor = getStartAnchor();
		if (startAnchor == null) {
			throw new IllegalStateException("Start anchor may never be null.");
		}
		anchors.add(startAnchor);

		// control anchors
		anchors.addAll(getControlAnchors());

		// end anchor
		IAnchor endAnchor = getEndAnchor();
		if (endAnchor == null) {
			throw new IllegalStateException("End anchor may never be null.");
		}
		anchors.add(endAnchor);

		return anchors;
	}

	/**
	 * Computes the 'logical' center point of the {@link Connection}, which is
	 * the middle control point position (in case the curve consists of an even
	 * number of segment) or the middle point of the middle segment.
	 *
	 * @return The logical center of this {@link Connection}.
	 */
	public Point getCenter() {
		BezierCurve[] bezierCurves = getCurveNode().getGeometry().toBezier();
		if (bezierCurves.length % 2 == 0) {
			return getPoint((int) (getPoints().size() - 0.5) / 2);
		} else {
			return bezierCurves[bezierCurves.length / 2].get(0.5);
		}
	}

	/**
	 * Returns the control {@link IAnchor anchor} for the given control anchor
	 * index which is currently assigned, or <code>null</code> if no control
	 * {@link IAnchor anchor} is assigned for that index.
	 *
	 * @param index
	 *            The control anchor index determining which control
	 *            {@link IAnchor anchor} to return.
	 * @return The control {@link IAnchor anchor} for the given index, or
	 *         <code>null</code>.
	 */
	public IAnchor getControlAnchor(int index) {
		return anchorsProperty.get(getControlAnchorKey(index));
	}

	/**
	 * Returns the control anchor index for the given {@link AnchorKey}, i.e.
	 * <code>0</code> for the first control {@link IAnchor anchor},
	 * <code>1</code> for the seconds, etc.
	 *
	 * @param key
	 *            The {@link AnchorKey} whose control anchor index is returned.
	 * @return The control anchor index for the given {@link AnchorKey}.
	 * @throws IllegalArgumentException
	 *             when there currently is no control {@link IAnchor anchor}
	 *             assigned to this {@link Connection} for the given
	 *             {@link AnchorKey}.
	 */
	public int getControlAnchorIndex(AnchorKey key) {
		if (!key.getId().startsWith(CONTROL_POINT_ROLE_PREFIX)) {
			throw new IllegalArgumentException(
					"Given AnchorKey " + key + " is no control anchor key.");
		}
		int index = Integer.parseInt(
				key.getId().substring(CONTROL_POINT_ROLE_PREFIX.length()));
		return index;
	}

	/**
	 * Returns the {@link AnchorKey} for the given control anchor index.
	 *
	 * @param index
	 *            The control anchor index for which the {@link AnchorKey} is
	 *            returned.
	 * @return The {@link AnchorKey} for the given control anchor index.
	 */
	public AnchorKey getControlAnchorKey(int index) {
		return new AnchorKey(getCurveNode(), CONTROL_POINT_ROLE_PREFIX + index);
	}

	/**
	 * Returns a {@link List} containing the control {@link IAnchor anchors}
	 * currently assigned to this {@link Connection}.
	 *
	 * @return A {@link List} containing the control {@link IAnchor anchors}
	 *         currently assigned to this {@link Connection}.
	 */
	public List<IAnchor> getControlAnchors() {
		int controlPointsCount = controlAnchorKeys.size();
		List<IAnchor> controlPointAnchors = new ArrayList<>(controlPointsCount);
		for (int i = 0; i < controlPointsCount; i++) {
			IAnchor controlAnchor = getControlAnchor(i);
			if (controlAnchor == null) {
				throw new IllegalStateException(
						"control anchor may never be null.");
			}
			controlPointAnchors.add(controlAnchor);
		}
		return controlPointAnchors;
	}

	/**
	 * Returns the control {@link Point} for the given control anchor index
	 * within the coordinate system of this {@link Connection} which is
	 * determined by querying the anchor position for the corresponding
	 * {@link #getControlAnchor(int) control anchor}, or <code>null</code> if no
	 * {@link #getControlAnchor(int) control anchor} is assigned for the given
	 * index.
	 *
	 * @param index
	 *            The control anchor index for which to return the anchor
	 *            position.
	 * @return The start {@link Point} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Point getControlPoint(int index) {
		IAnchor anchor = getControlAnchor(index);
		if (anchor == null) {
			throw new IllegalArgumentException(
					"No controlpoint at index " + index);
		}
		if (!anchor.isAttached(getControlAnchorKey(index))) {
			return null;
		}
		return FX2Geometry.toPoint(getCurveNode().localToParent(Geometry2FX
				.toFXPoint(anchor.getPosition(getControlAnchorKey(index)))));
	}

	/**
	 * Returns a {@link List} containing the control {@link Point}s of this
	 * {@link Connection}.
	 *
	 * @return A {@link List} containing the control {@link Point}s of this
	 *         {@link Connection}.
	 */
	public List<Point> getControlPoints() {
		int controlPointCount = getControlAnchors().size();
		List<Point> controlPoints = new ArrayList<>(controlPointCount);
		for (int i = 0; i < controlPointCount; i++) {
			controlPoints.add(getControlPoint(i));
		}
		return controlPoints;
	}

	/**
	 * Returns the {@link GeometryNode} which displays the curve geometry.
	 *
	 * @return The {@link GeometryNode} which displays the curve geometry.
	 */
	public GeometryNode<ICurve> getCurveNode() {
		return curveNode;
	}

	/**
	 * Returns the currently assigned end {@link IAnchor anchor}, or
	 * <code>null</code> if no end {@link IAnchor anchor} is assigned.
	 *
	 * @return The currently assigned end {@link IAnchor anchor}, or
	 *         <code>null</code>.
	 */
	public IAnchor getEndAnchor() {
		return anchorsProperty.get(getEndAnchorKey());
	}

	/**
	 * Returns the end {@link AnchorKey} for this {@link Connection}. An end
	 * {@link AnchorKey} uses the {@link #getCurveNode() curve node} as its
	 * anchored and <code>"end"</code> as its role.
	 *
	 * @return The end {@link AnchorKey} for this {@link Connection}.
	 */
	public AnchorKey getEndAnchorKey() {
		return new AnchorKey(getCurveNode(), END_ROLE);
	}

	/**
	 * Returns the end decoration {@link Shape} of this {@link Connection}, or
	 * <code>null</code>.
	 *
	 * @return The end decoration {@link Shape} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Shape getEndDecoration() {
		return endDecoration;
	}

	/**
	 * Returns the end {@link Point} of this {@link Connection} within its
	 * coordinate system which is determined by querying the anchor position for
	 * the {@link #getEndAnchorKey() end anchor key}, or <code>null</code> when
	 * no {@link #getEndAnchor() end anchor} is assigned.
	 *
	 * @return The end {@link Point} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Point getEndPoint() {
		IAnchor anchor = getEndAnchor();
		if (anchor == null) {
			return null;
		}
		if (!anchor.isAttached(getEndAnchorKey())) {
			return null;
		}
		return FX2Geometry.toPoint(getCurveNode().localToParent(
				Geometry2FX.toFXPoint(anchor.getPosition(getEndAnchorKey()))));
	}

	/**
	 * Returns the {@link IConnectionInterpolator} of this {@link Connection}.
	 *
	 * @return The {@link IConnectionInterpolator} of this {@link Connection}.
	 */
	public IConnectionInterpolator getInterpolator() {
		return interpolatorProperty.get();
	}

	/**
	 * Returns the point at the given index. The start point will be provided
	 * for <code>index == 0</code>, the end point for the last defined index.
	 * Control points will be returned for all indices in between.
	 *
	 * @param index
	 *            The index of the point to retrieve.
	 * @return The point at the given index.
	 *
	 * @see #getPoints()
	 */
	public Point getPoint(int index) {
		if (index == 0) {
			return getStartPoint();
		} else if (index == controlAnchorKeys.size() + 1) {
			return getEndPoint();
		} else {
			return getControlPoint(index - 1);
		}
	}

	/**
	 * Returns the {@link Point}s constituting this {@link Connection} within
	 * its coordinate system in the order: start point, control points, end
	 * point. They are determined by querying the corresponding anchor
	 * positions. In case not all anchors are assigned, an empty array is
	 * returned.
	 *
	 * @return The {@link Point}s constituting this {@link Connection}.
	 */
	public List<Point> getPoints() {
		int controlPointCount = controlAnchorKeys.size();
		Point[] points = new Point[controlPointCount + 2];

		points[0] = getStartPoint();
		if (points[0] == null) {
			return Collections.emptyList();
		}

		for (int i = 0; i < controlPointCount; i++) {
			points[i + 1] = getControlPoint(i);
			if (points[i + 1] == null) {
				return Collections.emptyList();
			}
		}

		points[points.length - 1] = getEndPoint();
		if (points[points.length - 1] == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(points);
	}

	/**
	 * Returns the {@link IConnectionRouter} of this {@link Connection}.
	 *
	 * @return The {@link IConnectionRouter} of this {@link Connection}.
	 */
	public IConnectionRouter getRouter() {
		return routerProperty.get();
	}

	/**
	 * Returns the currently assigned start {@link IAnchor anchor}, or
	 * <code>null</code> if no start {@link IAnchor anchor} is assigned.
	 *
	 * @return The currently assigned start {@link IAnchor anchor}, or
	 *         <code>null</code>.
	 */
	public IAnchor getStartAnchor() {
		return anchorsProperty.get(getStartAnchorKey());
	}

	/**
	 * Returns the start {@link AnchorKey} for this {@link Connection}. A start
	 * {@link AnchorKey} uses the {@link #getCurveNode() curve node} as its
	 * anchored and <code>"start"</code> as its role.
	 *
	 * @return The start {@link AnchorKey} for this {@link Connection}.
	 */
	public AnchorKey getStartAnchorKey() {
		return new AnchorKey(getCurveNode(), START_ROLE);
	}

	/**
	 * Returns the start decoration {@link Shape} of this {@link Connection}, or
	 * <code>null</code>.
	 *
	 * @return The start decoration {@link Shape } of this {@link Connection},
	 *         or <code>null</code>.
	 */
	public Shape getStartDecoration() {
		return startDecoration;
	}

	/**
	 * Returns the start {@link Point} of this {@link Connection} within its
	 * coordinate system which is determined by querying the anchor position for
	 * the {@link #getStartAnchorKey() start anchor key}, or <code>null</code>
	 * when no {@link #getStartAnchor() start anchor} is assigned.
	 *
	 * @return The start {@link Point} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Point getStartPoint() {
		IAnchor anchor = getStartAnchor();
		if (anchor == null) {
			return null;
		}
		if (!anchor.isAttached(getStartAnchorKey())) {
			return null;
		}
		return FX2Geometry.toPoint(getCurveNode().localToParent(Geometry2FX
				.toFXPoint(anchor.getPosition(getStartAnchorKey()))));
	}

	/**
	 * Returns the {@link IConnectionInterpolator} property.
	 *
	 * @return The {@link IConnectionInterpolator} property.
	 */
	public ObjectProperty<IConnectionInterpolator> interpolatorProperty() {
		return interpolatorProperty;
	}

	/**
	 * Returns whether the (start, end, or control) anchor at the respective
	 * index is connected.
	 *
	 * @param index
	 *            The index, referring to the start, end, or a control point.
	 * @return <code>true</code> if the anchor at the given index is connected,
	 *         <code>false</code> otherwise.
	 */
	public boolean isConnected(int index) {
		if (index < 0 || index >= getAnchors().size()) {
			throw new IllegalArgumentException(
					"The given index is out of bounds.");
		}
		if (index == 0) {
			return isStartConnected();
		} else if (index == getAnchors().size() - 1) {
			return isEndConnected();
		} else {
			return isControlConnected(index - 1);
		}
	}

	/**
	 * Returns <code>true</code> if the currently assigned
	 * {@link #getControlAnchor(int) control anchor} for the given index is
	 * bound to an anchorage. Otherwise returns <code>false</code>.
	 *
	 * @param index
	 *            The control anchor index of the control anchor to test for
	 *            connectedness.
	 * @return <code>true</code> if the currently assigned
	 *         {@link #getControlAnchor(int) control anchor} for the given index
	 *         is bound to an anchorage, otherwise <code>false</code>.
	 */
	public boolean isControlConnected(int index) {
		IAnchor anchor = getControlAnchor(index);
		return anchor.getAnchorage() != null && anchor.getAnchorage() != this;
	}

	/**
	 * Returns <code>true</code> if the currently assigned
	 * {@link #getEndAnchor() end anchor} is bound to an anchorage. Otherwise
	 * returns <code>false</code>.
	 *
	 * @return <code>true</code> if the currently assigned
	 *         {@link #getEndAnchor() end anchor} is bound to an anchorage,
	 *         otherwise <code>false</code>.
	 */
	public boolean isEndConnected() {
		IAnchor anchor = getEndAnchor();
		return anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != this;
	}

	/**
	 * Returns <code>true</code> if the currently assigned
	 * {@link #getStartAnchor() start anchor} is bound to an anchorage.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the currently assigned
	 *         {@link #getStartAnchor() start anchor} is bound to an anchorage,
	 *         otherwise <code>false</code>.
	 */
	public boolean isStartConnected() {
		IAnchor anchor = getStartAnchor();
		return anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != this;
	}

	/**
	 * Refreshes the visualization, i.e.
	 * <ol>
	 * <li>determines the {@link #getPoints() points} constituting this
	 * {@link Connection},</li>
	 * <li>computes an {@link ICurve} geometry through those {@link Point}s
	 * using the {@link IConnectionRouter} of this {@link Connection},</li>
	 * <li>replaces the geometry of the {@link #getCurveNode() curve node} with
	 * that {@link ICurve},</li>
	 * <li>arranges the {@link #getStartDecoration() start decoration} and
	 * {@link #getEndDecoration() end decoration} of this {@link Connection}.
	 * </li>
	 * </ol>
	 */
	protected void refresh() {
		// TODO: Guarding should be prevented by disabling listener while
		// refreshing
		// guard against recomputing the curve while recomputing the curve
		if (inRefresh) {
			return;
		}
		inRefresh = true;

		// TODO: guard against router value being null
		getRouter().route(this);
		ICurve newGeometry = getInterpolator().interpolate(this);

		// clear visuals except for the curveNode
		getChildren().retainAll(curveNode);

		// compute new curve (this can lead to another refreshGeometry() call
		// which is not executed)
		if (!newGeometry.equals(curveNode.getGeometry())) {
			// TODO: we need to prevent positions are re-calculated as a result
			// of the changed geometry. -> the static anchors should not update
			// their positions because of layout bounds changes.
			// System.out.println("New geometry: " + newGeometry);
			curveNode.setGeometry(newGeometry);
		}

		// z-order decorations above curve
		if (startDecoration != null) {
			getChildren().add(startDecoration);
			arrangeStartDecoration();
		}
		if (endDecoration != null) {
			getChildren().add(endDecoration);
			arrangeEndDecoration();
		}

		if (!newGeometry.getBounds().isEmpty()
				&& (startDecoration != null || endDecoration != null)) {
			// XXX Use scene coordinates, as the clip node does not provide a
			// parent
			Bounds layoutBounds = curveNode
					.localToScene(curveNode.getLayoutBounds());
			Shape clip = new Rectangle(layoutBounds.getMinX(),
					layoutBounds.getMinY(), layoutBounds.getWidth(),
					layoutBounds.getHeight());
			clip.setFill(Color.RED);
			if (startDecoration != null) {
				clip = clipAtDecoration(clip, startDecoration);
			}
			if (endDecoration != null) {
				clip = clipAtDecoration(clip, endDecoration);
			}
			// XXX: All CAG operations deliver result shapes that reflect areas
			// in scene coordinates.
			clip.getTransforms().add(Geometry2FX
					.toFXAffine(NodeUtils.getSceneToLocalTx(curveNode)));
			curveNode.setClip(clip);
		} else {
			curveNode.setClip(null);
		}
		inRefresh = false;
	}

	/**
	 * Removes all control points of this {@link Connection}.
	 */
	public void removeAllControlAnchors() {
		removeAllControlPoints();
	}

	/**
	 * Removes all control points of this {@link Connection}.
	 */
	public void removeAllControlPoints() {
		for (int i = controlAnchorKeys.size() - 1; i >= 0; i--) {
			removeControlPoint(i);
		}
	}

	/**
	 * Removes the given {@link AnchorKey} (and corresponding {@link IAnchor})
	 * from this {@link Connection}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} to remove.
	 * @param anchor
	 *            The corresponding {@link IAnchor}.
	 */
	protected void removeAnchor(AnchorKey anchorKey, IAnchor anchor) {
		if (anchorKey == null) {
			throw new IllegalArgumentException("anchorKey may not be null.");
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}
		if (anchorsProperty.containsKey(anchorKey)) {
			if (!anchorKey.equals(getStartAnchorKey())
					&& !anchorKey.equals(getEndAnchorKey())
					&& !controlAnchorKeys.contains(anchorKey)) {
				throw new IllegalStateException(
						"anchorKey is contained but key is not registered in control anchor key map.");
			}
		}

		if (anchorPCL.containsKey(anchorKey)) {
			anchor.positionsUnmodifiableProperty()
					.removeListener(anchorPCL.remove(anchorKey));
		}
		/*
		 * XXX: detach() after removing from the anchors-map, so that listeners
		 * on the anchors-map can retrieve the anchor position.
		 */
		List<IAnchor> controlAnchorsToMove = new ArrayList<>();
		int controlIndex = -1;
		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			// remove all control anchors at a larger index
			controlIndex = getControlAnchorIndex(anchorKey);
			int controlPointCount = controlAnchorKeys.size();
			for (int i = controlPointCount - 1; i > controlIndex; i--) {
				// (temporarily) remove all anchors that are to be moved down
				AnchorKey ak = getControlAnchorKey(i);
				IAnchor a = anchorsProperty.remove(ak);
				controlAnchorKeys.remove(ak);
				controlAnchorsToMove.add(0, a);
				a.detach(ak);
			}
			controlAnchorKeys.remove(anchorKey);
		}
		anchorsProperty.remove(anchorKey);
		anchor.detach(anchorKey);

		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			// re-add all control points at a larger index
			for (int i = 0; i < controlAnchorsToMove.size(); i++) {
				AnchorKey ak = getControlAnchorKey(controlIndex + i);
				IAnchor a = controlAnchorsToMove.get(i);
				controlAnchorKeys.add(ak);
				anchorsProperty.put(ak, a);
				a.attach(ak);
			}
		}
		refresh();
	}

	/**
	 * Removes the control anchor specified by the given index from this
	 * {@link Connection}.
	 *
	 * @param index
	 *            The index specifying which control anchor to remove.
	 */
	public void removeControlAnchor(int index) {
		removeControlPoint(index);
	}

	/**
	 * Removes the control point specified by the given control anchor index
	 * from this {@link Connection}.
	 *
	 * @param index
	 *            The control anchor index specifying which control point to
	 *            remove.
	 */
	public void removeControlPoint(int index) {
		// check index out of range
		if (index < 0 || index >= controlAnchorKeys.size()) {
			throw new IllegalArgumentException("Index out of range (index: "
					+ index + ", size: " + controlAnchorKeys.size() + ").");
		}

		AnchorKey anchorKey = getControlAnchorKey(index);
		if (!anchorsProperty.containsKey(anchorKey)) {
			throw new IllegalStateException(
					"Inconsistent state: control anchor key for index " + index
							+ " not registered.");
		}

		IAnchor oldAnchor = anchorsProperty.get(anchorKey);
		if (oldAnchor == null) {
			throw new IllegalStateException(
					"Inconsistent state: control anchor for index " + index
							+ " is null.");
		}
		removeAnchor(anchorKey, oldAnchor);
	}

	/**
	 * Returns a writable property containing the {@link IConnectionRouter} of
	 * this connection.
	 *
	 * @return A writable property providing the {@link IConnectionRouter} used
	 *         by this connection.
	 */
	public ObjectProperty<IConnectionRouter> routerProperty() {
		return routerProperty;
	}

	/**
	 * Replaces all {@link #getAnchors() anchors} of this {@link Connection}
	 * with the given {@link IAnchor}s, i.e. the first given {@link IAnchor}
	 * replaces the currently assigned start anchor, the last given
	 * {@link IAnchor} replaces the currently assigned end anchor, and the
	 * intermediate {@link IAnchor}s replace the currently assigned control
	 * anchors.
	 *
	 * @param anchors
	 *            The new {@link IAnchor}s for this {@link Connection}.
	 * @throws IllegalArgumentException
	 *             when less than 2 {@link IAnchor}s are given.
	 */
	public void setAnchors(List<IAnchor> anchors) {
		if (anchors.size() < 2) {
			throw new IllegalArgumentException(
					"start end end anchors have to be provided.");
		}

		// prevent refresh before all points are properly set
		boolean oldInRefresh = inRefresh;
		inRefresh = true;
		setStartAnchor(anchors.get(0));
		if (anchors.size() > 2) {
			setControlAnchors(anchors.subList(1, anchors.size() - 1));
		} else {
			removeAllControlPoints();
		}
		setEndAnchor(anchors.get(anchors.size() - 1));
		inRefresh = oldInRefresh;
		refresh();
	}

	/**
	 * Sets the control anchor for the given control anchor index to the given
	 * {@link IAnchor}.
	 *
	 * @param index
	 *            The control anchor index of the control anchor to replace.
	 * @param anchor
	 *            The new control {@link IAnchor} for that index.
	 */
	public void setControlAnchor(int index, IAnchor anchor) {
		if (index < 0 || index >= controlAnchorKeys.size()) {
			throw new IllegalArgumentException("index out of range.");
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		AnchorKey anchorKey = getControlAnchorKey(index);
		IAnchor oldAnchor = anchorsProperty.get(anchorKey);
		if (oldAnchor != anchor) {
			if (oldAnchor != null) {
				// suppress refresh, as addAnchor() will cause a refresh as well
				boolean oldInRefresh = inRefresh;
				inRefresh = true;
				removeAnchor(anchorKey, oldAnchor);
				inRefresh = oldInRefresh;
			}
			addAnchor(anchorKey, anchor);
		}
	}

	/**
	 * Replaces all control anchors of this {@link Connection} with the given
	 * {@link List} of {@link IAnchor}s.
	 *
	 * @param anchors
	 *            The new control {@link IAnchor}s for this {@link Connection}.
	 */
	public void setControlAnchors(List<IAnchor> anchors) {
		boolean oldInRefresh = inRefresh;
		inRefresh = true;

		// remove control points (starting with last to prevent reordering)
		for (int i = controlAnchorKeys.size() - 1; i >= 0; i--) {
			removeControlPoint(i);
		}

		// // perform some defensive check
		// if (!controlAnchorKeys.isEmpty()) {
		// throw new IllegalStateException("Remove did not succeed.");
		// }

		for (int i = 0; i < anchors.size(); i++) {
			addControlAnchor(i, anchors.get(i));
		}

		// // perform some defensive check
		// if (controlAnchorKeys.size() != anchors.size()) {
		// throw new IllegalStateException("Add did not succeed.");
		// }

		inRefresh = oldInRefresh;
		refresh();
	}

	/**
	 * Sets the control anchor for the given control anchor index to an
	 * {@link StaticAnchor} which yields the given {@link Point}.
	 *
	 * @param index
	 *            The control anchor index of the control anchor to replace.
	 * @param controlPointInLocal
	 *            The new control {@link Point} for that index.
	 */
	public void setControlPoint(int index, Point controlPointInLocal) {
		if (controlPointInLocal == null) {
			controlPointInLocal = new Point();
		}
		IAnchor anchor = new StaticAnchor(this, controlPointInLocal);
		setControlAnchor(index, anchor);
	}

	/**
	 * Replaces all control anchors of this {@link Connection} with
	 * {@link StaticAnchor}s yielding the given {@link Point}s.
	 *
	 * @param controlPoints
	 *            The new control {@link Point}s for this {@link Connection}.
	 */
	public void setControlPoints(List<Point> controlPoints) {
		int controlSize = controlAnchorKeys.size();
		boolean oldInRefresh = inRefresh;
		inRefresh = true;
		int i = 0;
		for (; i < controlSize && i < controlPoints.size(); i++) {
			setControlPoint(i, controlPoints.get(i));
		}
		for (; i < controlPoints.size(); i++) {
			addControlPoint(i, controlPoints.get(i));
		}
		int initialRemovalIndex = i;
		for (; i < controlSize; i++) {
			removeControlPoint(controlSize - 1 - (i - initialRemovalIndex));
		}
		inRefresh = oldInRefresh;
		refresh();
	}

	/**
	 * Sets the end {@link IAnchor} of this {@link Connection} to the given
	 * value.
	 *
	 * @param anchor
	 *            The new end {@link IAnchor} for this {@link Connection}.
	 */
	public void setEndAnchor(IAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		AnchorKey anchorKey = getEndAnchorKey();
		IAnchor oldAnchor = anchorsProperty.get(anchorKey);
		if (oldAnchor != anchor) {
			if (oldAnchor != null) {
				// suppress refresh, as addAnchor() will cause a refresh as well
				boolean oldInRefresh = inRefresh;
				inRefresh = true;
				removeAnchor(anchorKey, oldAnchor);
				inRefresh = oldInRefresh;
			}
			addAnchor(anchorKey, anchor);
		}
	}

	/**
	 * Sets the end decoration {@link Node} of this {@link Connection} to the
	 * given value.
	 *
	 * @param endDeco
	 *            The new end decoration {@link Node} for this
	 *            {@link Connection}.
	 */
	public void setEndDecoration(Shape endDeco) {
		if (endDecoration != null) {
			endDecoration.layoutBoundsProperty()
					.removeListener(decorationLayoutBoundsListener);
		}
		endDecoration = endDeco;
		if (endDecoration != null) {
			endDecoration.layoutBoundsProperty()
					.addListener(decorationLayoutBoundsListener);

			ObservableList<String> styleClasses = endDecoration.getStyleClass();
			if (!styleClasses.contains(CSS_CLASS_DECORATION)) {
				styleClasses.add(CSS_CLASS_DECORATION);
			}
		}
		refresh();
	}

	/**
	 * Sets the {@link #setEndAnchor(IAnchor) end anchor} of this
	 * {@link Connection} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param endPointInLocal
	 *            The new end {@link Point} for this {@link Connection}.
	 */
	public void setEndPoint(Point endPointInLocal) {
		if (endPointInLocal == null) {
			endPointInLocal = new Point();
		}
		IAnchor anchor = new StaticAnchor(this, endPointInLocal);
		setEndAnchor(anchor);
	}

	/**
	 * Sets the {@link IConnectionInterpolator} of this {@link Connection} to
	 * the given {@link IConnectionInterpolator}.
	 *
	 * @param interpolator
	 *            The new {@link IConnectionInterpolator} for this
	 *            {@link Connection}.
	 */
	public void setInterpolator(IConnectionInterpolator interpolator) {
		interpolatorProperty.set(interpolator);
	}

	/**
	 * Sets the {@link IConnectionRouter} of this {@link Connection} to the
	 * given value.
	 *
	 * @param router
	 *            The new {@link IConnectionRouter} for this {@link Connection}.
	 */
	public void setRouter(IConnectionRouter router) {
		routerProperty.set(router);
		refresh();
	}

	/**
	 * Sets the start {@link IAnchor} of this {@link Connection} to the given
	 * value.
	 *
	 * @param anchor
	 *            The new start {@link IAnchor} for this {@link Connection}.
	 */
	public void setStartAnchor(IAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		AnchorKey anchorKey = getStartAnchorKey();
		IAnchor oldAnchor = anchorsProperty.get(anchorKey);
		if (oldAnchor != anchor) {
			if (oldAnchor != null) {
				// suppress refresh, as addAnchor() will cause a refresh as well
				boolean oldInRefresh = inRefresh;
				inRefresh = true;
				removeAnchor(anchorKey, oldAnchor);
				inRefresh = oldInRefresh;
			}
			addAnchor(anchorKey, anchor);
		}
	}

	/**
	 * Sets the start decoration {@link Node} of this {@link Connection} to the
	 * given value.
	 *
	 * @param startDeco
	 *            The new start decoration {@link Node} for this
	 *            {@link Connection}.
	 */
	public void setStartDecoration(Shape startDeco) {
		if (startDecoration != null) {
			startDecoration.layoutBoundsProperty()
					.removeListener(decorationLayoutBoundsListener);
		}
		startDecoration = startDeco;
		if (startDecoration != null) {
			startDecoration.layoutBoundsProperty()
					.addListener(decorationLayoutBoundsListener);

			ObservableList<String> styleClasses = startDecoration
					.getStyleClass();
			if (!styleClasses.contains(CSS_CLASS_DECORATION)) {
				styleClasses.add(CSS_CLASS_DECORATION);
			}
		}
		refresh();
	}

	/**
	 * Sets the {@link #setStartAnchor(IAnchor) start anchor} of this
	 * {@link Connection} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param startPointInLocal
	 *            The new start {@link Point} for this {@link Connection}.
	 */
	public void setStartPoint(Point startPointInLocal) {
		if (startPointInLocal == null) {
			startPointInLocal = new Point();
		}
		// TODO: reuse static anchor, just update reference position -> need to
		// make that changeable in StaticAnchor
		IAnchor anchor = new StaticAnchor(this, startPointInLocal);
		setStartAnchor(anchor);
	}

}
