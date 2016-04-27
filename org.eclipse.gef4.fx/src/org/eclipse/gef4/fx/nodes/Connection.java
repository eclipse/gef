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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.gef4.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef4.common.collections.CollectionUtils;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

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
	private static final String CSS_CLASS_DECORATION = "decoration";

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
	 * specific control points at control point anchorsByKeys.
	 */
	private static final String CONTROL_POINT_ROLE_PREFIX = "controlpoint-";

	// visuals
	// TODO use Node property for curve node (and make it exchangeable)
	private GeometryNode<ICurve> curveNode = new GeometryNode<>();

	private ObjectProperty<Node> startDecorationProperty = new SimpleObjectProperty<>();
	private ObjectProperty<Node> endDecorationProperty = new SimpleObjectProperty<>();

	private ObjectProperty<IConnectionRouter> routerProperty = new SimpleObjectProperty<IConnectionRouter>(
			new StraightRouter());

	private ObjectProperty<IConnectionInterpolator> interpolatorProperty = new SimpleObjectProperty<IConnectionInterpolator>(
			new PolylineInterpolator());

	private ObservableList<IAnchor> anchors = CollectionUtils
			.observableArrayList();
	private ReadOnlyListWrapper<IAnchor> anchorsUnmodifiableProperty = new ReadOnlyListWrapperEx<>(
			FXCollections.unmodifiableObservableList(anchors));

	private ObservableList<Point> points = CollectionUtils
			.observableArrayList();
	private ReadOnlyListWrapper<Point> pointsUnmodifiableProperty = new ReadOnlyListWrapperEx<>(
			FXCollections.unmodifiableObservableList(points));

	// maintain anchors in a map, and their related keys additionally in an
	// ordered set, so we can determine appropriate indexes or anchor keys.
	private Map<AnchorKey, IAnchor> anchorsByKeys = new HashMap<>();
	private TreeSet<AnchorKey> sortedAnchorKeys = new TreeSet<>(
			new Comparator<AnchorKey>() {

				@Override
				public int compare(AnchorKey o1, AnchorKey o2) {
					if (o1.equals(o2)) {
						return 0;
					} else {
						if (getStartAnchorKey().equals(o1)) {
							return -1;
						} else if (getEndAnchorKey().equals(o1)) {
							return 1;
						} else {
							if (getStartAnchorKey().equals(o2)) {
								return 1;
							} else if (getEndAnchorKey().equals(o2)) {
								return -1;
							}
							return getControlAnchorIndex(o1)
									- getControlAnchorIndex(o2);
						}
					}
				}
			});

	// refresh geometry on position changes
	private boolean inRefresh = false;
	private Map<AnchorKey, MapChangeListener<? super AnchorKey, ? super Point>> anchorPCL = new HashMap<>();

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

		curveNode.localToParentTransformProperty()
				.addListener(new ChangeListener<Transform>() {

					@Override
					public void changed(
							ObservableValue<? extends Transform> observable,
							Transform oldValue, Transform newValue) {
						refresh();
					}
				});

		routerProperty.addListener(new ChangeListener<IConnectionRouter>() {
			@Override
			public void changed(
					ObservableValue<? extends IConnectionRouter> observable,
					IConnectionRouter oldValue, IConnectionRouter newValue) {
				refresh();
			}
		});

		interpolatorProperty
				.addListener(new ChangeListener<IConnectionInterpolator>() {
					@Override
					public void changed(
							ObservableValue<? extends IConnectionInterpolator> observable,
							IConnectionInterpolator oldValue,
							IConnectionInterpolator newValue) {
						refresh();
					}
				});

		ChangeListener<Node> decorationListener = new ChangeListener<Node>() {

			final ChangeListener<Bounds> decorationLayoutBoundsListener = new ChangeListener<Bounds>() {
				@Override
				public void changed(
						ObservableValue<? extends Bounds> observable,
						Bounds oldValue, Bounds newValue) {
					// refresh decoration clip in case the layout bounds of
					// the decorations have changed
					refresh();
				}
			};

			@Override
			public void changed(ObservableValue<? extends Node> observable,
					Node oldValue, Node newValue) {
				if (oldValue != null) {
					oldValue.layoutBoundsProperty()
							.removeListener(decorationLayoutBoundsListener);
					ObservableList<String> styleClasses = oldValue
							.getStyleClass();
					if (styleClasses.contains(CSS_CLASS_DECORATION)) {
						styleClasses.remove(CSS_CLASS_DECORATION);
					}
				}
				if (newValue != null) {
					newValue.layoutBoundsProperty()
							.addListener(decorationLayoutBoundsListener);
					ObservableList<String> styleClasses = newValue
							.getStyleClass();
					if (!styleClasses.contains(CSS_CLASS_DECORATION)) {
						styleClasses.add(CSS_CLASS_DECORATION);
					}
				}
				refresh();
			}
		};
		startDecorationProperty.addListener(decorationListener);
		endDecorationProperty.addListener(decorationListener);

		// add the curve node
		getChildren().add(curveNode);
	}

	/**
	 * Inserts the given {@link IAnchor} into the
	 * {@link #anchorsUnmodifiableProperty()} of this {@link Connection}. The
	 * given {@link AnchorKey} is attached to the {@link IAnchor}. Furthermore,
	 * a {@link #createPCL(AnchorKey) PCL} for the {@link AnchorKey} is
	 * registered on the position property of the {@link IAnchor} and the
	 * visualization is {@link #refresh() refreshed}.
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
		if (!anchorsByKeys.containsKey(anchorKey)) {
			if (sortedAnchorKeys.contains(anchorKey)) {
				throw new IllegalStateException(
						"anchorKey is not contained but key is registered in control anchor key map.");

			}
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		List<IAnchor> controlAnchorsToMove = new ArrayList<>();
		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			int controlAnchorIndex = getControlAnchorIndex(anchorKey);
			// remove all control points at a larger index
			int pointCount = sortedAnchorKeys.size();
			for (int i = pointCount - 1; i >= 0; i--) {
				// (temporarily) remove all anchorsByKeys that are to be moved
				// up
				AnchorKey ak = getAnchorKey(i);
				if (!ak.equals(getStartAnchorKey())
						&& !ak.equals(getEndAnchorKey())) {
					if (getControlAnchorIndex(ak) >= controlAnchorIndex) {
						IAnchor a = getAnchor(i);

						unregisterPCL(ak, a);

						controlAnchorsToMove.add(0, a);
						points.remove(getAnchorIndex(ak));
						anchors.remove(getAnchorIndex(ak));

						sortedAnchorKeys.remove(ak);
						anchorsByKeys.remove(ak);

						a.detach(ak);
					}
				}
			}
		}

		// update anchor map and list
		anchorsByKeys.put(anchorKey, anchor);
		sortedAnchorKeys.add(anchorKey);

		// attach anchor key
		anchor.attach(anchorKey);

		// update lists
		anchors.add(getAnchorIndex(anchorKey), anchor);
		points.add(getAnchorIndex(anchorKey),
				FX2Geometry.toPoint(getCurveNode().localToParent(
						Geometry2FX.toFXPoint(anchor.getPosition(anchorKey)))));

		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			int controlIndex = getControlAnchorIndex(anchorKey);
			// re-add all controlpoints at a larger index
			for (int i = 0; i < controlAnchorsToMove.size(); i++) {
				AnchorKey ak = getControlAnchorKey(controlIndex + i + 1);
				IAnchor a = controlAnchorsToMove.get(i);
				sortedAnchorKeys.add(ak);
				anchorsByKeys.put(ak, a);

				a.attach(ak);

				anchors.add(getAnchorIndex(ak), a);
				points.add(getAnchorIndex(ak),
						FX2Geometry.toPoint(getCurveNode().localToParent(
								Geometry2FX.toFXPoint(a.getPosition(ak)))));

				registerPCL(ak, a);
			}
		}
		registerPCL(anchorKey, anchor);
		refresh();
	}

	/**
	 * Adds the given {@link IAnchor} as a control point anchor for the given
	 * index into the {@link #anchorsUnmodifiableProperty()} of this
	 * {@link Connection}.
	 *
	 * @param index
	 *            The position where the {@link IAnchor} is inserted within the
	 *            control point anchorsByKeys of this {@link Connection}.
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
	 * {@link #anchorsUnmodifiableProperty()} of this {@link Connection}.
	 *
	 * @param index
	 *            The position where the {@link IAnchor} is inserted within the
	 *            control point anchorsByKeys of this {@link Connection}.
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
	 * Returns an unmodifiable read-only list property, which contains the
	 * {@link IAnchor}s that determine the start point, control points, and end
	 * point of this {@link Connection}.
	 *
	 * @return An unmodifiable read-only list property containing this
	 *         {@link Connection}'s anchors.
	 */
	public ReadOnlyListProperty<IAnchor> anchorsUnmodifiableProperty() {
		return anchorsUnmodifiableProperty.getReadOnlyProperty();
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
					MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
				if (change.getKey().equals(anchorKey)) {
					if (change.wasAdded() && change.wasRemoved()) {
						points.set(getAnchorIndex(anchorKey),
								FX2Geometry.toPoint(getCurveNode()
										.localToParent(Geometry2FX.toFXPoint(
												change.getValueAdded()))));
					}
					// XXX: As map changes are not atomic, it may be that other
					// points were already updated but the position change is
					// notified later; refresh will update positions as a first
					// step because of this (otherwise the router may obtain
					// stale positions).
					refresh();
				}
			}
		};
	}

	/**
	 * Returns the anchor at the given index. The start anchor will be provided
	 * for <code>index == 0</code>, the end anchor for the last defined index.
	 * Control anchorsByKeys will be returned for all indices in between.
	 *
	 * @param index
	 *            The index of the anchor to retrieve.
	 * @return The anchor at the given index.
	 */
	public IAnchor getAnchor(int index) {
		return anchors.get(index);
	}

	/**
	 * Returns the anchor index for the given {@link AnchorKey}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} for which the anchor index is
	 *            determined.
	 * @return The anchor index for the given {@link AnchorKey}.
	 */
	protected int getAnchorIndex(AnchorKey anchorKey) {
		return new ArrayList<>(sortedAnchorKeys).indexOf(anchorKey);
	}

	/**
	 * Returns the {@link AnchorKey} for the given anchor index, i.e. the
	 * reverse of {@link #getAnchorIndex(AnchorKey)}.
	 *
	 * @param anchorIndex
	 *            The anchor index for which to determine the {@link AnchorKey}.
	 * @return The {@link AnchorKey} for the given anchor index.
	 */
	// TODO: this should not be exposed -> make protected
	public AnchorKey getAnchorKey(int anchorIndex) {
		return new ArrayList<>(sortedAnchorKeys).get(anchorIndex);
	}

	/**
	 * Returns a {@link List} containing the {@link IAnchor}s which are assigned
	 * to this {@link Connection} in the order: start anchor, control point
	 * anchorsByKeys, end anchor.
	 *
	 * @return A {@link List} containing the {@link IAnchor}s which are assigned
	 *         to this {@link Connection}.
	 */
	public ObservableList<IAnchor> getAnchorsUnmodifiable() {
		return anchorsUnmodifiableProperty.get();
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
			return getPoint((int) (getPointsUnmodifiable().size() - 0.5) / 2);
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
		return anchorsByKeys.get(getControlAnchorKey(index));
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
	protected int getControlAnchorIndex(AnchorKey key) {
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
	protected AnchorKey getControlAnchorKey(int index) {
		return new AnchorKey(getCurveNode(), CONTROL_POINT_ROLE_PREFIX + index);
	}

	/**
	 * Returns a {@link List} containing the control {@link IAnchor
	 * anchorsByKeys} currently assigned to this {@link Connection}.
	 *
	 * @return A {@link List} containing the control {@link IAnchor
	 *         anchorsByKeys} currently assigned to this {@link Connection}.
	 */
	public List<IAnchor> getControlAnchors() {
		int controlAnchorsCount = sortedAnchorKeys.size();
		if (sortedAnchorKeys.contains(getStartAnchorKey())) {
			controlAnchorsCount--;
		}
		if (sortedAnchorKeys.contains(getEndAnchorKey())) {
			controlAnchorsCount--;
		}
		List<IAnchor> controlAnchors = new ArrayList<>(controlAnchorsCount);
		for (int i = 0; i < controlAnchorsCount; i++) {
			IAnchor controlAnchor = getControlAnchor(i);
			if (controlAnchor == null) {
				throw new IllegalStateException(
						"control anchor may never be null.");
			}
			controlAnchors.add(controlAnchor);
		}
		return controlAnchors;
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
		return anchorsByKeys.get(getEndAnchorKey());
	}

	/**
	 * Returns the end {@link AnchorKey} for this {@link Connection}. An end
	 * {@link AnchorKey} uses the {@link #getCurveNode() curve node} as its
	 * anchored and <code>"end"</code> as its role.
	 *
	 * @return The end {@link AnchorKey} for this {@link Connection}.
	 */
	// TODO: AnchorKeys should not be exposed -> make protected
	public AnchorKey getEndAnchorKey() {
		return new AnchorKey(getCurveNode(), END_ROLE);
	}

	/**
	 * Returns the end decoration {@link Node} of this {@link Connection}, or
	 * <code>null</code>.
	 *
	 * @return The end decoration {@link Node} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Node getEndDecoration() {
		return endDecorationProperty.get();
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
	 * @see #getPointsUnmodifiable()
	 */
	public Point getPoint(int index) {
		IAnchor anchor = getAnchor(index);
		if (anchor == null) {
			return null;
		}
		if (!anchor.isAttached(getAnchorKey(index))) {
			return null;
		}
		return FX2Geometry.toPoint(getCurveNode().localToParent(Geometry2FX
				.toFXPoint(anchor.getPosition(getAnchorKey(index)))));
	}

	/**
	 * Returns the {@link Point}s constituting this {@link Connection} within
	 * its coordinate system in the order: start point, control points, end
	 * point.
	 *
	 * @return The {@link Point}s constituting this {@link Connection}.
	 */
	public ObservableList<Point> getPointsUnmodifiable() {
		// TODO: this update here should not be necessary; we should replace it
		// with defensive code that fails (because we have missed something if
		// positions are out of sync here)
		updatePoints();
		return pointsUnmodifiableProperty.get();
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
		return anchorsByKeys.get(getStartAnchorKey());
	}

	/**
	 * Returns the start {@link AnchorKey} for this {@link Connection}. A start
	 * {@link AnchorKey} uses the {@link #getCurveNode() curve node} as its
	 * anchored and <code>"start"</code> as its role.
	 *
	 * @return The start {@link AnchorKey} for this {@link Connection}.
	 */
	// TODO: AnchorKeys should not be exposed -> make protected
	public AnchorKey getStartAnchorKey() {
		return new AnchorKey(getCurveNode(), START_ROLE);
	}

	/**
	 * Returns the start decoration {@link Node} of this {@link Connection}, or
	 * <code>null</code>.
	 *
	 * @return The start decoration {@link Node } of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Node getStartDecoration() {
		return startDecorationProperty.get();
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
	 * Return <code>true</code> in case the anchor is bound to an anchorage
	 * unequal to this connection.
	 *
	 * @param anchor
	 *            The anchor to test
	 * @return <code>true</code> if the anchor is connected, <code>false</code>
	 *         otherwise.
	 */
	protected boolean isConnected(IAnchor anchor) {
		return anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != this;
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
		if (index < 0 || index >= getAnchorsUnmodifiable().size()) {
			throw new IllegalArgumentException(
					"The given index is out of bounds.");
		}
		return isConnected(getAnchor(index));
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
		return isConnected(getControlAnchor(index));
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
	public boolean isStartConnected() {
		return isConnected(getStartAnchor());
	}

	/**
	 * Returns an unmodifiable read-only list property, which contains the
	 * points (start, control, end) that constitute this connection.
	 *
	 * @return An unmodifiable read-only list property containing this
	 *         {@link Connection}'s points.
	 */
	public ReadOnlyListProperty<Point> pointsUnmodifiableProperty() {
		return pointsUnmodifiableProperty.getReadOnlyProperty();
	}

	/**
	 * Refreshes the visualization, i.e.
	 * <ol>
	 * <li>determines the {@link #getPointsUnmodifiable() points} constituting
	 * this {@link Connection},</li>
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

		// update points in case they are not in sync yet. This can happen when
		// refresh is called from within a PCL change, but other (related) PCL
		// change have not been processed (map changes are not atomic).
		updatePoints();

		// clear visuals except for the curveNode
		getChildren().retainAll(curveNode);

		// update our anchorsByKeys/points
		if (getRouter() != null) {
			getRouter().route(this);
		} else {
			throw new IllegalStateException(
					"An IConnectionRouter is mandatory for a Connection.");
		}

		// z-order decorations above curve
		if (getStartDecoration() != null) {
			getChildren().add(getStartDecoration());
		}
		if (getEndDecoration() != null) {
			getChildren().add(getEndDecoration());
		}

		// update the curve node, arrange and clip the decorations
		if (getInterpolator() != null) {
			getInterpolator().interpolate(this);
		} else {
			throw new IllegalStateException(
					"An IConnectionInterpolator is mandatory for a Connection.");
		}

		inRefresh = false;
	}

	private void registerPCL(AnchorKey anchorKey, IAnchor anchor) {
		if (!anchorPCL.containsKey(anchorKey)) {
			MapChangeListener<? super AnchorKey, ? super Point> pcl = createPCL(
					anchorKey);
			anchorPCL.put(anchorKey, pcl);
			anchor.positionsUnmodifiableProperty().addListener(pcl);
		}
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
		int controlPointsCount = sortedAnchorKeys.size();
		if (sortedAnchorKeys.contains(getStartAnchorKey())) {
			controlPointsCount--;
		}
		if (sortedAnchorKeys.contains(getEndAnchorKey())) {
			controlPointsCount--;
		}
		for (int i = controlPointsCount - 1; i >= 0; i--) {
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
		if (anchorsByKeys.containsKey(anchorKey)) {
			if (!sortedAnchorKeys.contains(anchorKey)) {
				throw new IllegalStateException(
						"anchorKey is contained but key is not registered in control anchor key map.");
			}
		}

		unregisterPCL(anchorKey, anchor);

		List<IAnchor> controlAnchorsToMove = new ArrayList<>();
		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			int controlAnchorIndex = getControlAnchorIndex(anchorKey);
			// remove all control points at a larger index
			int pointCount = sortedAnchorKeys.size();
			for (int i = pointCount - 1; i >= 0; i--) {
				// (temporarily) remove all anchorsByKeys that are to be moved
				// up
				AnchorKey ak = getAnchorKey(i);
				if (!ak.equals(getStartAnchorKey())
						&& !ak.equals(getEndAnchorKey())) {
					if (getControlAnchorIndex(ak) > controlAnchorIndex) {
						IAnchor a = getAnchor(i);

						unregisterPCL(ak, a);

						controlAnchorsToMove.add(0, a);

						points.remove(getAnchorIndex(ak));
						anchors.remove(getAnchorIndex(ak));

						sortedAnchorKeys.remove(ak);
						anchorsByKeys.remove(ak);

						a.detach(ak);
					}
				}
			}
		}

		points.remove(getAnchorIndex(anchorKey));
		anchors.remove(getAnchorIndex(anchorKey));

		sortedAnchorKeys.remove(anchorKey);
		anchorsByKeys.remove(anchorKey);

		anchor.detach(anchorKey);

		if (!anchorKey.equals(getStartAnchorKey())
				&& !anchorKey.equals(getEndAnchorKey())) {
			int controlIndex = getControlAnchorIndex(anchorKey);
			// re-add all control points at a larger index
			for (int i = 0; i < controlAnchorsToMove.size(); i++) {
				AnchorKey ak = getControlAnchorKey(controlIndex + i);
				IAnchor a = controlAnchorsToMove.get(i);

				sortedAnchorKeys.add(ak);
				anchorsByKeys.put(ak, a);

				a.attach(ak);

				anchors.add(getAnchorIndex(ak), a);
				points.add(getAnchorIndex(ak),
						FX2Geometry.toPoint(getCurveNode().localToParent(
								Geometry2FX.toFXPoint(a.getPosition(ak)))));

				registerPCL(ak, a);
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
		if (index < 0 || index >= getControlPoints().size()) {
			throw new IllegalArgumentException("Index out of range (index: "
					+ index + ", size: " + getControlPoints().size() + ").");
		}

		AnchorKey anchorKey = getControlAnchorKey(index);
		if (!anchorsByKeys.containsKey(anchorKey)) {
			throw new IllegalStateException(
					"Inconsistent state: control anchor key for index " + index
							+ " not registered.");
		}

		IAnchor oldAnchor = anchorsByKeys.get(anchorKey);
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
	 * Replaces all {@link #getAnchorsUnmodifiable() anchorsByKeys} of this
	 * {@link Connection} with the given {@link IAnchor}s, i.e. the first given
	 * {@link IAnchor} replaces the currently assigned start anchor, the last
	 * given {@link IAnchor} replaces the currently assigned end anchor, and the
	 * intermediate {@link IAnchor}s replace the currently assigned control
	 * anchorsByKeys.
	 *
	 * @param anchors
	 *            The new {@link IAnchor}s for this {@link Connection}.
	 * @throws IllegalArgumentException
	 *             when less than 2 {@link IAnchor}s are given.
	 */
	public void setAnchors(List<IAnchor> anchors) {
		if (anchors.size() < 2) {
			throw new IllegalArgumentException(
					"start end end anchorsByKeys have to be provided.");
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
		if (index < 0 || index >= getControlAnchors().size()) {
			throw new IllegalArgumentException("index out of range.");
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		AnchorKey anchorKey = getControlAnchorKey(index);
		IAnchor oldAnchor = anchorsByKeys.get(anchorKey);
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
	 * Replaces all control anchorsByKeys of this {@link Connection} with the
	 * given {@link List} of {@link IAnchor}s.
	 *
	 * @param anchors
	 *            The new control {@link IAnchor}s for this {@link Connection}.
	 */
	public void setControlAnchors(List<IAnchor> anchors) {
		boolean oldInRefresh = inRefresh;
		inRefresh = true;

		removeAllControlPoints();
		for (int i = 0; i < anchors.size(); i++) {
			addControlAnchor(i, anchors.get(i));
		}

		inRefresh = oldInRefresh;
		refresh();
	}

	// TODO: offer setPoints()

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
		// TODO: if the anchor is already a static anchor, we could simply
		// update its reference position
		IAnchor anchor = new StaticAnchor(this, controlPointInLocal);
		setControlAnchor(index, anchor);
	}

	/**
	 * Replaces all control anchorsByKeys of this {@link Connection} with
	 * {@link StaticAnchor}s yielding the given {@link Point}s.
	 *
	 * @param controlPoints
	 *            The new control {@link Point}s for this {@link Connection}.
	 */
	public void setControlPoints(List<Point> controlPoints) {
		int controlSize = getControlAnchors().size();
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
		IAnchor oldAnchor = anchorsByKeys.get(anchorKey);
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
	 * @param decoration
	 *            The new end decoration {@link Node} for this
	 *            {@link Connection}.
	 */
	public void setEndDecoration(Node decoration) {
		endDecorationProperty.set(decoration);
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
		// TODO: if the anchor is already a static anchor, we could simply
		// update its reference position
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
		IAnchor oldAnchor = anchorsByKeys.get(anchorKey);
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
	 * @param decoration
	 *            The new start decoration {@link Node} for this
	 *            {@link Connection}.
	 */
	public void setStartDecoration(Node decoration) {
		startDecorationProperty.set(decoration);
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
		// TODO: if the anchor is already a static anchor, we could simply
		// update its reference position
		IAnchor anchor = new StaticAnchor(this, startPointInLocal);
		setStartAnchor(anchor);
	}

	private void unregisterPCL(AnchorKey anchorKey, IAnchor anchor) {
		if (anchorPCL.containsKey(anchorKey)) {
			anchor.positionsUnmodifiableProperty()
					.removeListener(anchorPCL.remove(anchorKey));
		}
	}

	private void updatePoints() {
		List<Point> computedPoints = new ArrayList<>();
		if (getStartPoint() != null) {
			computedPoints.add(getStartPoint());
		}
		for (Point p : getControlPoints()) {
			computedPoints.add(p);
		}
		if (getEndPoint() != null) {
			computedPoints.add(getEndPoint());
		}
		if (computedPoints.size() != points.size()) {
			throw new IllegalStateException(
					"Computed points and points are out of sync.");
		}
		// update points
		for (int i = 0; i < computedPoints.size(); i++) {
			if (!computedPoints.get(i).equals(points.get(i))) {
				points.set(i, computedPoints.get(i));
			}
		}
	}

}
