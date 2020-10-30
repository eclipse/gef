/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.internal.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.utils.Geometry2Shape;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Point;

import com.google.common.collect.Iterables;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

/**
 * A {@link Traverse} is a visual polyline, whose appearance is defined through
 * a single start and end point, and a set of control (i.e. way) points. The
 * start and end points may be 'connected', i.e. be attached to an
 * {@link IAnchor}.
 * <P>
 * In addition to the polyline curve shape, the visual appearance of a
 * {@link Traverse} can be controlled via start and end decorations. They will
 * be rendered 'on-top' of the curveProperty shape and the curveProperty shape
 * will be properly clipped at the decorations (so it does not paint through).
 *
 * @author anyssen
 *
 */
public class Traverse extends Group implements IBendableCurve<Polyline, Shape> {

	private class AnchorMap {

		private List<AnchorKey> anchorKeys = new ArrayList<>();
		private TreeMap<AnchorKey, IAnchor> anchorsByKeys = new TreeMap<>(
				new Comparator<AnchorKey>() {
					@Override
					public int compare(AnchorKey o1, AnchorKey o2) {
						if (o1.getId().equals(o2.getId())) {
							return 0;
						}
						if (START_ROLE.equals(o1.getId())) {
							return -1;
						}
						if (END_ROLE.equals(o1.getId())) {
							return 1;
						}
						if (START_ROLE.equals(o2.getId())) {
							return 1;
						}
						if (END_ROLE.equals(o2.getId())) {
							return -1;
						}
						return Integer.parseInt(o1.getId())
								- Integer.parseInt(o2.getId());
					}
				});
		private ObservableList<IAnchor> anchors = CollectionUtils
				.observableArrayList();

		IAnchor get(AnchorKey anchorKey) {
			return anchorsByKeys.get(anchorKey);
		}

		IAnchor get(int index) {
			if (anchorKeys.isEmpty()) {
				Iterables.addAll(anchorKeys, anchorsByKeys.keySet());
			}
			return anchors.get(index);
		}

		int getIndex(AnchorKey anchorKey) {
			if (anchorKeys.isEmpty()) {
				Iterables.addAll(anchorKeys, anchorsByKeys.keySet());
			}
			return anchorKeys.indexOf(anchorKey);
		}

		IAnchor set(AnchorKey key, IAnchor anchor) {
			anchorKeys.clear(); // clear cache
			IAnchor oldAnchor = anchorsByKeys.put(key, anchor);
			int index = getIndex(key);
			if (anchorKeys.size() > anchors.size()) {
				anchors.add(index, anchor);
			} else {
				anchors.set(index, anchor);
			}
			return oldAnchor;
		}

		int size() {
			return anchors.size();
		}
	}

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

	private Polyline curve = new Polyline(0, 0, 0, 0);
	private ObservableList<Point> points = CollectionUtils
			.observableArrayList(new Point(), new Point());
	private ObjectProperty<Shape> startDecorationProperty = null;
	private ObjectProperty<Shape> endDecorationProperty = null;

	private AnchorKey startAnchorKey = new AnchorKey(curve, START_ROLE);
	private AnchorKey endAnchorKey = new AnchorKey(curve, END_ROLE);
	private Map<AnchorKey, MapChangeListener<? super AnchorKey, ? super Point>> anchorsPCL = new HashMap<>();
	private AnchorMap anchorsByKeys = new AnchorMap();

	private ChangeListener<Node> decorationListener = new ChangeListener<Node>() {

		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldValue, Node newValue) {
			refreshChildren();
			refreshDecorations();
		}
	};

	private ChangeListener<Transform> transformListener = new ChangeListener<Transform>() {
		@Override
		public void changed(ObservableValue<? extends Transform> observable,
				Transform oldValue, Transform newValue) {
			refreshDecorations();
		}
	};

	private ChangeListener<Bounds> boundsListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			refreshDecorations();
		}
	};

	private DoubleProperty clickableAreaWidth = new SimpleDoubleProperty();
	private Polyline clickableAreaShape = null;
	private ListChangeListener<Double> coordinatesListener = new ListChangeListener<Double>() {

		@Override
		public void onChanged(Change<? extends Double> c) {
			// TODO: We could support translating back coordinate changes to
			// point changes.
			throw new IllegalStateException(
					"Direct manipulation of the curve's (Polyline) coordinates are not supported. Manipulate the points of the Traverse instead; the coordinates will get updated as a consequence.");

		}
	};

	/**
	 * Constructs a new {@link Traverse} whose start and end point are set to
	 * <code>(0,0)</code> points .
	 */
	public Traverse() {
		// disable resizing children which would change their layout positions
		// in some cases
		setAutoSizeChildren(false);

		// init curve
		getChildren().add(curve);

		curve.layoutBoundsProperty().addListener(boundsListener);
		curve.localToParentTransformProperty().addListener(transformListener);
		curve.getPoints().addListener(coordinatesListener);

		// initialize anchors
		setStartPoint(new Point());
		setEndPoint(new Point());

		// ensure clickable area is added/removed as needed
		clickableAreaWidth.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				Polyline curve = getCurve();
				if (newValue != null
						&& newValue.doubleValue() > curve.getStrokeWidth()
						&& clickableAreaShape == null) {
					// create and configure clickable area shape
					clickableAreaShape = new Polyline();
					clickableAreaShape.getPoints().addAll(curve.getPoints());
					clickableAreaShape
							.setId("clickable area of GeometryNode " + this);
					clickableAreaShape.setStroke(Color.TRANSPARENT);
					clickableAreaShape.setMouseTransparent(false);
					clickableAreaShape.strokeWidthProperty()
							.bind(clickableAreaWidthProperty());
					// add clickable area and binding only if its really used
				} else if ((newValue == null
						|| newValue.doubleValue() <= curve.getStrokeWidth())
						&& clickableAreaShape != null) {
					clickableAreaShape.strokeWidthProperty().unbind();
					clickableAreaShape = null;
				}
				refreshChildren();
			}
		});
	}

	/**
	 * Inserts a control point with the given coordinates at the specified
	 * control index, i.e. <code>0</code> for the first control point.
	 *
	 * @param index
	 *            The control index at which the control point is inserted.}.
	 * @param controlPoint
	 *            The position for the specified control point.
	 */
	@Override
	public void addControlPoint(int index, Point controlPoint) {
		if (controlPoint == null) {
			throw new IllegalArgumentException("controlPoint may not be null.");
		}
		points.add(index + 1, controlPoint);
		Point p = NodeUtils.parentToLocal(curve, controlPoint);
		addCurveCoordinates(2 * (index + 1), p.x, p.y);
	}

	private void addCurveCoordinates(int index, Double... coordinates) {
		curve.getPoints().addAll(index, Arrays.asList(coordinates));
	}

	// from AbstractInterpolator
	private void arrangeDecoration(Node decoration, Point offset,
			Vector direction) {
		// arrange on start of curve
		AffineTransform transform = new AffineTransform().translate(offset.x,
				offset.y);
		// arrange on curve direction
		if (!direction.isNull()) {
			Angle angleCW = new Vector(1, 0).getAngleCW(direction);
			transform.rotate(angleCW.rad(), 0, 0);
		}
		// compensate stroke (ensure decoration 'ends' at curve end).
		transform.translate(-NodeUtils.getShapeBounds(decoration).getX(), 0);
		// apply transform
		decoration.getTransforms().setAll(Geometry2FX.toFXAffine(transform));
	}

	/**
	 * Returns a (writable) property that controls the width of the clickable
	 * area. The clickable area is a transparent 'fat' curve overlaying the
	 * actual curve and serving as mouse target. It is only used if the value of
	 * the property is greater than the stroke width of the underlying curve.
	 *
	 * @return A property to control the width of the clickable area of this
	 *         connection.
	 */
	@Override
	public DoubleProperty clickableAreaWidthProperty() {
		return clickableAreaWidth;
	}

	// TODO: Copied from AbstractInterpolator
	private Shape clipAtDecoration(Shape curveShape, Shape curveClip,
			Shape decoration) {
		// first intersect curve shape with decoration layout bounds,
		// then subtract the curve shape from the result, and the decoration
		// from that
		Path decorationShapeBounds = new Path(
				Geometry2Shape.toPathElements(NodeUtils
						.localToScene(decoration,
								NodeUtils.getShapeBounds(decoration))
						.toPath()));
		decorationShapeBounds.setFill(Color.RED);
		Shape clip = Shape.intersect(decorationShapeBounds, curveShape);
		clip = Shape.subtract(clip, decoration);
		clip = Shape.subtract(curveClip, clip);
		return clip;
	}

	/**
	 * Creates a position change listener (PCL) which refreshes this
	 * {@link Traverse} upon anchor position changes corresponding to the given
	 * (start or end) {@link AnchorKey}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} for which a position change will trigger
	 *            a refresh.
	 * @return A change listener reacting to position changes of the given
	 *         anchor key.
	 */
	protected MapChangeListener<? super AnchorKey, ? super Point> createPCL(
			final AnchorKey anchorKey) {
		return new MapChangeListener<AnchorKey, Point>() {
			@Override
			public void onChanged(
					MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
				if (change.getKey().equals(anchorKey)) {
					if (change.wasAdded() && change.wasRemoved()) {
						updateCurvePoint(change.getKey());
						refreshDynamicAnchors();
					}
				}
			}
		};
	}

	/**
	 * Returns an {@link ObjectProperty} wrapping the end decoration
	 * {@link Shape}.
	 *
	 * @return A property wrapping the end decoration.
	 */
	public ObjectProperty<Shape> endDecorationProperty() {
		if (endDecorationProperty == null) {
			endDecorationProperty = new SimpleObjectProperty<>();
			endDecorationProperty.addListener(decorationListener);
		}
		return endDecorationProperty;
	}

	/**
	 * Retrieves the value of the clickable area width property (
	 * {@link #clickableAreaWidthProperty()}).
	 *
	 * @return The current value of the {@link #clickableAreaWidthProperty()}.
	 */
	@Override
	public double getClickableAreaWidth() {
		return clickableAreaWidth.get();
	}

	/**
	 * Returns the control {@link Point} for the given control index (i.e.
	 * <code>0</code> for the first control point) within the coordinate system
	 * of this {@link Traverse}.
	 *
	 * @param index
	 *            The control point index for which to return the
	 *            {@link Traverse} position.
	 * @return a {@link Point} representing the control point of the given
	 *         index.
	 */
	@Override
	public Point getControlPoint(int index) {
		if (index + 1 >= curve.getPoints().size() / 2 - 1) {
			// no control points, just start and end points
			return null;
		}
		ObservableList<Double> coordinates = curve.getPoints();
		return NodeUtils.localToParent(curve,
				new Point(coordinates.get(2 * (index + 1)),
						coordinates.get(2 * (index + 1) + 1)));
	}

	/**
	 * Returns a {@link List} containing the control {@link Point}s of this
	 * {@link Traverse}.
	 *
	 * @return A {@link List} containing the control {@link Point}s of this
	 *         {@link Traverse}.
	 */
	@Override
	public List<Point> getControlPoints() {
		List<Point> controlPoints = new ArrayList<>();
		ObservableList<Double> coordinates = curve.getPoints();
		for (int i = 1; i < coordinates.size() / 2 - 1; i++) {
			controlPoints.add(NodeUtils.localToParent(curve, new Point(
					coordinates.get(2 * i), coordinates.get(2 * i + 1))));
		}
		return controlPoints;
	}

	/**
	 * Returns the {@link Polyline} which displays the geometry.
	 *
	 * @return The {@link Polyline} which displays the geometry.
	 */
	@Override
	public Polyline getCurve() {
		return curve;
	}

	/**
	 * Returns the currently assigned end {@link IAnchor anchor}, or
	 * <code>null</code> if no end {@link IAnchor anchor} is assigned.
	 *
	 * @return The currently assigned end {@link IAnchor anchor}, or
	 *         <code>null</code>.
	 */
	@Override
	public IAnchor getEndAnchor() {
		return anchorsByKeys.get(endAnchorKey);
	}

	/**
	 * Returns the end decoration {@link Shape} of this {@link Traverse}, or
	 * <code>null</code>.
	 *
	 * @return The end decoration {@link Shape} of this {@link Traverse}, or
	 *         <code>null</code>.
	 */
	@Override
	public Shape getEndDecoration() {
		if (endDecorationProperty == null) {
			return null;
		}
		return endDecorationProperty.get();
	}

	/**
	 * Returns the end {@link Point} of this {@link Traverse} within its
	 * coordinate system.
	 *
	 * @return The end {@link Point} of this {@link Traverse}, or
	 *         <code>null</code>.
	 */
	@Override
	public Point getEndPoint() {
		ObservableList<Double> coordinates = curve.getPoints();
		return NodeUtils.localToParent(curve,
				new Point(coordinates.get(coordinates.size() - 2),
						coordinates.get(coordinates.size() - 1)));
	}

	/**
	 * Returns the {@link Point} at the given index, within the coordinate
	 * system of this {@link Traverse}.
	 *
	 * @param index
	 *            The index, for which to retrieve the point.
	 * @return The {@link Point} at the given index, within the coordinate
	 *         system of this {@link Traverse}.
	 */
	public Point getPoint(int index) {
		if (points == null) {
			if (index < 0 || index >= curve.getPoints().size()) {
				throw new IndexOutOfBoundsException("Index " + index
						+ " is out of bounds. This traverse has "
						+ this.curve.getPoints().size() / 2);
			}
			return NodeUtils.localToParent(curve,
					new Point(curve.getPoints().get(index * 2),
							curve.getPoints().get(index * 2 + 1)));
		}
		return points.get(index);
	}

	/**
	 * Returns the {@link Point}s constituting this {@link Traverse} within its
	 * coordinate system in the order: start point, control points, end point.
	 *
	 * @return The {@link Point}s constituting this {@link Traverse}, within the
	 *         coordinate system of this {@link Traverse}.
	 */
	@Override
	public ObservableList<Point> getPointsUnmodifiable() {
		return FXCollections.unmodifiableObservableList(points);
	}

	/**
	 * Returns the currently assigned start {@link IAnchor anchor}, or
	 * <code>null</code> if no start {@link IAnchor anchor} is assigned.
	 *
	 * @return The currently assigned start {@link IAnchor anchor}, or
	 *         <code>null</code>.
	 */
	@Override
	public IAnchor getStartAnchor() {
		return anchorsByKeys.get(startAnchorKey);
	}

	/**
	 * Returns the start decoration {@link Node} of this {@link Traverse}, or
	 * <code>null</code>.
	 *
	 * @return The start decoration {@link Node } of this {@link Traverse}, or
	 *         <code>null</code>.
	 */
	@Override
	public Shape getStartDecoration() {
		if (startDecorationProperty == null) {
			return null;
		}
		return startDecorationProperty.get();
	}

	/**
	 * Returns the start {@link Point} of this {@link Traverse} within its
	 * coordinate system.
	 *
	 * @return The start {@link Point} of this {@link Traverse}, or
	 *         <code>null</code>.
	 */
	@Override
	public Point getStartPoint() {
		ObservableList<Double> coordinates = curve.getPoints();
		return NodeUtils.localToParent(curve,
				new Point(coordinates.get(0), coordinates.get(1)));
	}

	/**
	 * Return <code>true</code> in case the anchor is bound to an anchorage
	 * unequal to this connection.
	 *
	 * @param anchor
	 *            The anchor to test
	 * @return <code>true</code> if the anchor is connected, <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean isConnected(IAnchor anchor) {
		return anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != this;
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
	@Override
	public boolean isEndConnected() {
		return isConnected(getEndAnchor());
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
	@Override
	public boolean isStartConnected() {
		return isConnected(getStartAnchor());
	}

	@Override
	public double maxHeight(double width) {
		return Double.MAX_VALUE;
	}

	@Override
	public double maxWidth(double height) {
		return Double.MAX_VALUE;
	}

	@Override
	public double minHeight(double width) {
		return 0d;
	}

	@Override
	public double minWidth(double height) {
		return 0d;
	}

	private void refreshChildren() {
		getChildren().retainAll(curve);
		Node startDecoration = getStartDecoration();
		if (startDecoration != null) {
			getChildren().add(startDecoration);
		}
		Node endDecoration = getEndDecoration();
		if (endDecoration != null) {
			getChildren().add(endDecoration);
		}
		if (clickableAreaShape != null) {
			getChildren().add(clickableAreaShape);
		}
	}

	/**
	 * Refreshes the clip of the curve.
	 */
	protected void refreshClip() {
		Shape startDecoration = getStartDecoration();
		Shape endDecoration = getEndDecoration();
		if (startDecoration != null || endDecoration != null) {
			// create clip
			Bounds visualBounds = curve.localToScene(
					Geometry2FX.toFXBounds(NodeUtils.getShapeBounds(curve)));

			// create clip
			Shape clip = new Rectangle(visualBounds.getMinX(),
					visualBounds.getMinY(), visualBounds.getWidth(),
					visualBounds.getHeight());
			clip.setFill(Color.RED);
			// can only clip Shape decorations
			if (startDecoration != null) {
				clip = clipAtDecoration(curve, clip, startDecoration);
			}
			// can only clip Shape decorations
			if (endDecoration != null) {
				clip = clipAtDecoration(curve, clip, endDecoration);
			}

			// XXX: All CAG operations deliver result shapes that reflect areas
			// in scene coordinates.
			AffineTransform sceneToLocalTx = NodeUtils.getSceneToLocalTx(curve);
			clip.getTransforms().add(Geometry2FX.toFXAffine(sceneToLocalTx));
			// set clip
			curve.setClip(clip);
		} else {
			curve.setClip(null);
		}
	}

	/**
	 * Refreshes the decorations.
	 */
	protected void refreshDecorations() {
		Double[] coordinates = curve.getPoints().toArray(new Double[] {});

		// if we are called during initialization, skip
		if (coordinates.length < 4) {
			return;
		}

		Shape startDecoration = getStartDecoration();
		if (startDecoration != null) {
			Point startPoint = new Point(coordinates[0], coordinates[1]);
			Point refPoint = new Point(coordinates[2], coordinates[3]);
			Vector startDirection = new Vector(startPoint, refPoint);
			arrangeDecoration(startDecoration, startPoint, startDirection);
		}
		Shape endDecoration = getEndDecoration();
		if (endDecoration != null) {
			Point endPoint = new Point(coordinates[coordinates.length - 2],
					coordinates[coordinates.length - 1]);
			Point refPoint = new Point(coordinates[coordinates.length - 4],
					coordinates[coordinates.length - 3]);
			Vector endDirection = new Vector(endPoint, refPoint);
			arrangeDecoration(endDecoration, endPoint, endDirection);
		}

		refreshClip();
	}

	/**
	 * Refreshes the reference points of dynamic (start and end) anchors.
	 */
	protected void refreshDynamicAnchors() {
		ObservableList<Double> coordinates = curve.getPoints();
		if (anchorsByKeys.size() < 2) {
			return;
		}
		for (int i = 0; i < 2; i++) {
			IAnchor anchor = anchorsByKeys.get(i);
			AnchorKey anchorKey = i == 0 ? startAnchorKey : endAnchorKey;
			if (anchor instanceof DynamicAnchor) {
				Point refPoint = null;
				if (coordinates.size() == 4) {
					AnchorKey oppositeAnchorKey = i == 0 ? endAnchorKey
							: startAnchorKey;
					IAnchor oppositeAnchor = anchorsByKeys
							.get(oppositeAnchorKey);
					Node opppsiteAnchorage = oppositeAnchor.getAnchorage();
					if (oppositeAnchor instanceof DynamicAnchor
							&& opppsiteAnchorage != null) {
						// if we have no way points we use the anchorage center
						// of the opposite anchor to make the computation stable
						refPoint = NodeUtils.sceneToLocal(curve,
								NodeUtils.localToScene(opppsiteAnchorage,
										NodeUtils
												.getShapeBounds(
														opppsiteAnchorage)
												.getCenter()));
					}
				}

				if (refPoint == null) {
					// we either have way points or the opposite anchor is no
					// attached dynamic anchor
					int refCoordinatesIndex = (i == 0) ? 2
							: coordinates.size() - 4;
					refPoint = new Point(coordinates.get(refCoordinatesIndex),
							coordinates.get(refCoordinatesIndex + 1));
				}

				AnchoredReferencePoint anchoredReferencePoint = ((DynamicAnchor) anchor)
						.getComputationParameter(anchorKey,
								AnchoredReferencePoint.class);
				if (!refPoint.equals(anchoredReferencePoint.get())) {
					anchoredReferencePoint.set(refPoint);
					updateCurvePoint(anchorKey);
				}
			}
		}
	}

	private void registerPCL(AnchorKey anchorKey, IAnchor anchor) {
		if (!anchorsPCL.containsKey(anchorKey)) {
			MapChangeListener<? super AnchorKey, ? super Point> pcl = createPCL(
					anchorKey);
			anchorsPCL.put(anchorKey, pcl);
			anchor.positionsUnmodifiableProperty().addListener(pcl);
		}
	}

	/**
	 * Removes all control points of this {@link Traverse}.
	 */
	public void removeAllControlPoints() {
		curve.getPoints().remove(2, curve.getPoints().size() - 2);
	}

	/**
	 * Removes the control point specified by the given control index from this
	 * {@link Traverse}.
	 *
	 * @param index
	 *            The control index specifying which control point to remove.
	 */
	@Override
	public void removeControlPoint(int index) {
		curve.getPoints().remove(2 * (index + 1), 2 * (index + 1) + 2);
	}

	/**
	 * Replaces the anchor currently registered for the given {@link AnchorKey}
	 * with the given {@link IAnchor}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} under which the {@link IAnchor} is to be
	 *            registered.
	 * @param anchor
	 *            The {@link IAnchor} which is inserted.
	 * @return The previous anchor registered for the given {@link AnchorKey} or
	 *         <code>null</code>.
	 */
	protected IAnchor setAnchor(AnchorKey anchorKey, IAnchor anchor) {
		if (anchorKey == null) {
			throw new IllegalArgumentException("anchorKey may not be null.");
		}
		if (anchorKey.getAnchored() != curve) {
			throw new IllegalArgumentException(
					"anchorKey may only be anchored to curveProperty node");
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		IAnchor oldAnchor = anchorsByKeys.set(anchorKey, anchor);
		unregisterPCL(anchorKey, oldAnchor);

		// detach anchor key from old anchor
		if (oldAnchor != null) {
			unregisterPCL(anchorKey, oldAnchor);
			oldAnchor.detach(anchorKey);
		}

		// attach anchor key to new anchor
		anchor.attach(anchorKey);

		// update position
		updateCurvePoint(anchorKey);
		registerPCL(anchorKey, anchor);
		refreshDynamicAnchors();
		return anchor;
	}

	/**
	 * Sets the value of the property {@link #clickableAreaWidthProperty()
	 * clickable area width} property.
	 *
	 * @param clickableAreaWidth
	 *            The new value of the {@link #clickableAreaWidthProperty()
	 *            clickable area width} property.
	 */
	@Override
	public void setClickableAreaWidth(double clickableAreaWidth) {
		this.clickableAreaWidth.set(clickableAreaWidth);
	}

	/**
	 * Sets the control anchor for the given control anchor index to an
	 * {@link StaticAnchor} which yields the given {@link Point}.
	 *
	 * @param index
	 *            The control anchor index of the control anchor to replace.
	 * @param controlPoint
	 *            The new control {@link Point} for the respective index within
	 *            local coordinates of the {@link Traverse}.
	 */
	@Override
	public void setControlPoint(int index, Point controlPoint) {
		if (getStartAnchor() == null || getEndAnchor() == null) {
			throw new IllegalStateException(
					"Curve does not have start and end.");
		}
		if (!controlPoint.equals(points.get(index + 1))) {
			points.set(index + 1, controlPoint);
		}
		Point p = NodeUtils.parentToLocal(curve, controlPoint);
		this.setCurveCoordinates(2 * (index + 1), p.x, p.y);
	}

	/**
	 * Replaces all control points of this {@link Traverse} with the given
	 * {@link Point}s.
	 *
	 * @param controlPoints
	 *            The new control {@link Point}s for this {@link Traverse}.
	 */
	@Override
	public void setControlPoints(List<Point> controlPoints) {
		if (anchorsByKeys.size() != 2) {
			throw new IllegalStateException(
					"Curve does not have start and end.");
		}
		Double[] coordinates = new Double[2 * controlPoints.size() + 4];
		List<Point> points = new ArrayList<Point>();
		ObservableList<Double> curvePoints = curve.getPoints();
		coordinates[0] = curvePoints.get(0);
		coordinates[1] = curvePoints.get(1);
		points.add(this.points.get(0));
		for (int i = 0; i < controlPoints.size(); i++) {
			Point cp = controlPoints.get(i);
			points.add(cp);
			Point p = NodeUtils.parentToLocal(curve, cp);
			coordinates[2 * i + 2] = p.x;
			coordinates[2 * i + 3] = p.y;
		}
		coordinates[coordinates.length - 2] = curvePoints
				.get(curvePoints.size() - 2);
		coordinates[coordinates.length - 1] = curvePoints
				.get(curvePoints.size() - 1);
		points.add(this.points.get(this.points.size() - 1));
		this.points.setAll(points);
		setCurveCoordinates(0, coordinates);
	}

	private void setCurveCoordinates(int index, Double... coordinates) {
		// XXX: We try to do the update as minimal as possible here, so only
		// relevant
		// listeners will react and lead to updates; Unfortunately, an atomic
		// change to
		// update only a subset of the coordinates at once is not possible.
		ObservableList<Double> points = curve.getPoints();
		// TODO: disable coordinates listener (if present) and update points
		// array to minimize changes (otherwise a (x,y)-coordinate change would
		// lead to two point changes)
		if (coordinatesListener != null) {
			curve.getPoints().removeListener(coordinatesListener);
		}
		if (coordinates.length > points.size() / 2) {
			Double[] coords = new Double[Math.max(index + coordinates.length,
					points.size())];
			int i = 0;
			for (; i < index; i++) {
				coords[i] = points.get(i);
			}
			for (; i < index + coordinates.length; i++) {
				coords[i] = coordinates[i - index];
			}
			for (; i < points.size(); i++) {
				coords[i] = points.get(i);
			}
			points.setAll(Arrays.asList(coords));
		} else {
			for (int i = 0; i < coordinates.length; i++) {
				if (points.get(index + i) != coordinates[i]) {
					points.set(index + i, coordinates[i]);
				}
			}
		}
		if (coordinatesListener != null) {
			curve.getPoints().addListener(coordinatesListener);
		}
	}

	/**
	 * Sets the end {@link IAnchor} of this {@link Traverse} to the given value.
	 *
	 * @param anchor
	 *            The new end {@link IAnchor} for this {@link Traverse}.
	 */
	@Override
	public void setEndAnchor(IAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}
		setAnchor(endAnchorKey, anchor);
	}

	/**
	 * Sets the end decoration {@link Node} of this {@link Traverse} to the
	 * given value.
	 *
	 * @param decoration
	 *            The new end decoration {@link Node} for this {@link Traverse}.
	 */
	@Override
	public void setEndDecoration(Shape decoration) {
		endDecorationProperty().set(decoration);
	}

	/**
	 * Sets the {@link #setEndAnchor(IAnchor) end anchor} of this
	 * {@link Traverse} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param endPoint
	 *            The new end {@link Point} within local coordinates of the
	 *            {@link Traverse}.
	 */
	@Override
	public void setEndPoint(Point endPoint) {
		if (endPoint == null) {
			throw new IllegalArgumentException("endPoint may not be null.");
		}
		IAnchor anchor = new StaticAnchor(this, endPoint);
		setEndAnchor(anchor);
	}

	/**
	 * Replaces all points of this Traverse. I.e. replaces the currently
	 * assigned start and end anchors with respective {@link StaticAnchor}s and
	 * sets the intermediate control points accordingly.
	 *
	 * @param points
	 *            The new {@link Point}s for this {@link Traverse}.
	 * @throws IllegalArgumentException
	 *             when less than 2 {@link IAnchor}s are given.
	 */
	@Override
	public void setPoints(List<Point> points) {
		if (points.size() < 2) {
			throw new IllegalArgumentException(
					"At least two points have to be provided.");
		}

		// prevent refresh before all points are properly set
		setStartPoint(points.get(0));
		if (points.size() > 2) {
			setControlPoints(points.subList(1, points.size() - 1));
		} else {
			removeAllControlPoints();
		}
		setEndPoint(points.get(points.size() - 1));
	}

	/**
	 * Sets the start {@link IAnchor} of this {@link Traverse} to the given
	 * value.
	 *
	 * @param anchor
	 *            The new start {@link IAnchor} for this {@link Traverse}.
	 */
	@Override
	public void setStartAnchor(IAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}
		setAnchor(startAnchorKey, anchor);
	}

	/**
	 * Sets the start decoration {@link Node} of this {@link Traverse} to the
	 * given value.
	 *
	 * @param decoration
	 *            The new start decoration {@link Node} for this
	 *            {@link Traverse}.
	 */
	@Override
	public void setStartDecoration(Shape decoration) {
		startDecorationProperty().set(decoration);
	}

	/**
	 * Sets the {@link #setStartAnchor(IAnchor) start anchor} of this
	 * {@link Traverse} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param startPoint
	 *            The new start {@link Point} within local coordinates of the
	 *            {@link Traverse}.
	 */
	@Override
	public void setStartPoint(Point startPoint) {
		if (startPoint == null) {
			throw new IllegalArgumentException("startPoint may not be null.");
		}
		IAnchor anchor = new StaticAnchor(this, startPoint);
		setStartAnchor(anchor);
	}

	/**
	 * Returns an {@link ObjectProperty} wrapping the start decoration
	 * {@link Shape}.
	 *
	 * @return An Object Property wrapping the start decoration.
	 */
	public ObjectProperty<Shape> startDecorationProperty() {
		if (startDecorationProperty == null) {
			startDecorationProperty = new SimpleObjectProperty<>();
			startDecorationProperty.addListener(decorationListener);
		}
		return startDecorationProperty;
	}

	private void unregisterPCL(AnchorKey anchorKey, IAnchor anchor) {
		if (anchorsPCL.containsKey(anchorKey)) {
			anchor.positionsUnmodifiableProperty()
					.removeListener(anchorsPCL.remove(anchorKey));
		}
	}

	private void updateCurvePoint(AnchorKey anchorKey) {
		IAnchor anchor = anchorsByKeys.get(anchorKey);
		int index = anchorKey == startAnchorKey ? 0
				: curve.getPoints().size() / 2 - 1;
		Point point = anchor.getPosition(anchorKey);
		Point p = NodeUtils.localToParent(curve, point);
		if (!p.equals(points.get(index))) {
			points.set(index, p);
		}
		setCurveCoordinates(2 * index, point.x, point.y);
	}
}
