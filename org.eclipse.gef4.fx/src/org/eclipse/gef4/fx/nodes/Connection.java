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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef4.common.beans.property.ReadOnlyListPropertyBaseEx;
import org.eclipse.gef4.common.collections.CollectionUtils;
import org.eclipse.gef4.common.collections.ListListenerHelperEx;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

import com.google.common.collect.Iterators;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.ReadOnlyListProperty;
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
import javafx.scene.transform.Transform;

/**
 * A (binary) {@link Connection} is a visual curveProperty, whose appearance is
 * defined through a single start and end point, and a set of control points,
 * which may be 'connected', i.e. be attached to an {@link IAnchor}. The exact
 * curveProperty shape is determined by an {@link IConnectionRouter}, which is
 * responsible of computing an {@link ICurve} geometry for a given
 * {@link Connection} (which is then rendered using a {@link GeometryNode}).
 * <p>
 * Whether the control points are interpreted as way points (that lie on the
 * curveProperty) or as 'real' control points depends on the
 * {@link IConnectionInterpolator}. While {@link PolylineInterpolator} and
 * {@link PolyBezierInterpolator} interpret control points to be way points,
 * other routers may e.g. interpret them as the control points of a
 * {@link BezierCurve}.
 * <P>
 * In addition to the curveProperty shape, the visual appearance of a
 * {@link Connection} can be controlled via start and end decorations. They will
 * be rendered 'on-top' of the curveProperty shape and the curveProperty shape
 * will be properly clipped at the decorations (so it does not paint through).
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class Connection extends Group {

	private final class AnchorsUnmodifiableProperty
			extends LazyReadOnlyListPropertyBase<IAnchor> {

		@Override
		public void fireValueChangedEvent() {
			super.fireValueChangedEvent();
		}

		@Override
		public ObservableList<IAnchor> get() {
			return getAnchorsUnmodifiable();
		}
	}

	private abstract class LazyReadOnlyListPropertyBase<E>
			extends ReadOnlyListPropertyBaseEx<E> {

		private class EmptyProperty extends ReadOnlyBooleanPropertyBase {

			@Override
			protected void fireValueChangedEvent() {
				super.fireValueChangedEvent();
			}

			@Override
			public boolean get() {
				return isEmpty();
			}

			@Override
			public Object getBean() {
				return LazyReadOnlyListPropertyBase.this;
			}

			@Override
			public String getName() {
				return "empty";
			}
		}

		private class SizeProperty extends ReadOnlyIntegerPropertyBase {
			@Override
			protected void fireValueChangedEvent() {
				super.fireValueChangedEvent();
			}

			@Override
			public int get() {
				return size();
			}

			@Override
			public Object getBean() {
				return LazyReadOnlyListPropertyBase.this;
			}

			@Override
			public String getName() {
				return "size";
			}
		}

		private ReadOnlyBooleanProperty emptyProperty;
		private ReadOnlyIntegerProperty sizeProperty;

		private ObservableList<E> lazyValue = CollectionUtils
				.observableArrayList();

		public LazyReadOnlyListPropertyBase() {
			// lazy listener will forward changes of lazy value, which will be
			// updated within fireValueChangeEvent()
			lazyValue.addListener(new ListChangeListener<E>() {

				@Override
				public void onChanged(
						ListChangeListener.Change<? extends E> c) {
					fireValueChangedEvent(
							new ListListenerHelperEx.AtomicChange<>(get(), c));
				}
			});
		}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {
			if (emptyProperty == null) {
				emptyProperty = new EmptyProperty();
			}
			return emptyProperty;
		}

		@Override
		public void fireValueChangedEvent() {
			// XXX fireValueChangedEvent() is overwritten, so it can be called
			// from within refresh to notify about changes (the list change
			// event will be computed from the old and current value). We thus
			// overwrite getValue() to return a copy (so a change can be
			// computed from oldValue and currentValue). Note that list
			// change events will never be notified via
			// fireValueChangedEvent(Change), as ReadOnlyListPropertyBaseEx is
			// no WritableValue (so we do not have to guard this in addition).
			lazyValue.setAll(get());
		}

		@Override
		public Object getBean() {
			return Connection.this;
		}

		@Override
		public String getName() {
			return "points";
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			if (sizeProperty == null) {
				sizeProperty = new SizeProperty();
			}
			return sizeProperty;
		}
	}

	private final class PointsUnmodifiableProperty
			extends LazyReadOnlyListPropertyBase<Point> {

		@Override
		public ObservableList<Point> get() {
			return getPointsUnmodifiable();
		}

		@Override
		public String getName() {
			return "points";
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

	/**
	 * Prefix for the default <i>ids</i> used by this connection to identify
	 * specific control points at control point anchorsByKeys.
	 */
	private static final String CONTROL_POINT_ROLE_PREFIX = "controlpoint-";

	private ObjectProperty<Node> curveProperty = new SimpleObjectProperty<>();
	private ObjectProperty<Node> startDecorationProperty = null;
	private ObjectProperty<Node> endDecorationProperty = null;
	private ObjectProperty<IConnectionRouter> routerProperty = new SimpleObjectProperty<IConnectionRouter>(
			new StraightRouter());
	private ObjectProperty<IConnectionInterpolator> interpolatorProperty = new SimpleObjectProperty<IConnectionInterpolator>(
			new PolylineInterpolator());

	// XXX: Maintain anchors in a sorted map, so we can use it to determine the
	// mapping between anchor keys and anchor indexes.
	private TreeMap<AnchorKey, IAnchor> anchorsByKeys = new TreeMap<>(
			new Comparator<AnchorKey>() {

				@Override
				public int compare(AnchorKey o1, AnchorKey o2) {
					if (o1.getId().equals(o2.getId())) {
						return 0;
					} else {
						if (getStartAnchorKey().getId().equals(o1.getId())) {
							return -1;
						} else if (getEndAnchorKey().getId()
								.equals(o1.getId())) {
							return 1;
						} else {
							if (getStartAnchorKey().getId()
									.equals(o2.getId())) {
								return 1;
							} else if (getEndAnchorKey().getId()
									.equals(o2.getId())) {
								return -1;
							}
							return getControlAnchorIndex(o1)
									- getControlAnchorIndex(o2);
						}
					}
				}
			});
	private Map<AnchorKey, Point> hintsByKeys = new HashMap<>();

	private ObservableList<IAnchor> anchors = CollectionUtils
			.observableArrayList();
	private ObservableList<Point> points = CollectionUtils
			.observableArrayList();
	private PointsUnmodifiableProperty pointsUnmodifiableProperty = null;
	private AnchorsUnmodifiableProperty anchorsUnmodifiableProperty = null;

	private Map<AnchorKey, MapChangeListener<? super AnchorKey, ? super Point>> anchorsPCL = new HashMap<>();
	private ChangeListener<Node> decorationListener = new ChangeListener<Node>() {

		final ChangeListener<Bounds> decorationLayoutBoundsListener = new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable,
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
			}
			if (newValue != null) {
				newValue.layoutBoundsProperty()
						.addListener(decorationLayoutBoundsListener);
			}
			refresh();
		}
	};
	private boolean inRefresh = false;

	/**
	 * Constructs a new {@link Connection} whose start and end point are set to
	 * <code>null</code>.
	 */
	public Connection() {
		// disable resizing children which would change their layout positions
		// in some cases
		setAutoSizeChildren(false);

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

		curveProperty.addListener(new ChangeListener<Node>() {
			private ChangeListener<Transform> transformListener = new ChangeListener<Transform>() {
				@Override
				public void changed(
						ObservableValue<? extends Transform> observable,
						Transform oldValue, Transform newValue) {
					refresh();
				}
			};

			@Override
			public void changed(ObservableValue<? extends Node> observable,
					Node oldValue, Node newValue) {
				boolean oldInRefresh = inRefresh;
				inRefresh = true;

				if (oldValue != null) {
					getChildren().remove(oldValue);
					oldValue.localToParentTransformProperty()
							.removeListener(transformListener);
				}

				reattachAnchorKeys(oldValue, newValue);

				if (newValue != null) {
					newValue.localToParentTransformProperty()
							.removeListener(transformListener);
					getChildren().add(newValue);
				}
				inRefresh = oldInRefresh;
				refresh();
			}
		});

		// set default curve
		setCurve(new GeometryNode<ICurve>());

		// init start and end points
		setStartPoint(new Point());
		setEndPoint(new Point());
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
		if (anchorKey.getAnchored() != getCurve()) {
			throw new IllegalArgumentException(
					"anchorKey may only be anchored to curveProperty node");
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		AnchorKey startAnchorKey = getStartAnchorKey();
		AnchorKey endAnchorKey = getEndAnchorKey();

		List<IAnchor> controlAnchorsToMove = new ArrayList<>();
		if (!anchorKey.equals(startAnchorKey)
				&& !anchorKey.equals(endAnchorKey)) {
			int controlAnchorIndex = getControlAnchorIndex(anchorKey);
			// remove all control points at a larger index
			int pointCount = anchorsByKeys.size();
			for (int i = pointCount - 1; i >= 0; i--) {
				// (temporarily) remove all anchorsByKeys that are to be moved
				// up
				AnchorKey ak = getAnchorKey(i);
				if (!ak.equals(startAnchorKey) && !ak.equals(endAnchorKey)) {
					if (getControlAnchorIndex(ak) >= controlAnchorIndex) {
						IAnchor a = getAnchor(i);

						unregisterPCL(ak, a);

						controlAnchorsToMove.add(0, a);
						int anchorIndex = getAnchorIndex(ak);
						points.remove(anchorIndex);
						anchors.remove(anchorIndex);

						anchorsByKeys.remove(ak);

						a.detach(ak);
					}
				}
			}
		}

		// update anchor map and list
		anchorsByKeys.put(anchorKey, anchor);

		// attach anchor key
		anchor.attach(anchorKey);

		// update lists
		anchors.add(getAnchorIndex(anchorKey), anchor);
		points.add(getAnchorIndex(anchorKey),
				FX2Geometry.toPoint(getCurve().localToParent(
						Geometry2FX.toFXPoint(anchor.getPosition(anchorKey)))));

		if (!anchorKey.equals(startAnchorKey)
				&& !anchorKey.equals(endAnchorKey)) {
			int controlIndex = getControlAnchorIndex(anchorKey);
			// re-add all control points at a larger index
			for (int i = 0; i < controlAnchorsToMove.size(); i++) {
				AnchorKey ak = getControlAnchorKey(controlIndex + i + 1);
				IAnchor a = controlAnchorsToMove.get(i);

				anchorsByKeys.put(ak, a);

				a.attach(ak);

				int anchorIndex = getAnchorIndex(ak);
				anchors.add(anchorIndex, a);
				points.add(anchorIndex,
						FX2Geometry.toPoint(getCurve().localToParent(
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
	 * @param controlPoint
	 *            The position for the specified control point.
	 */
	public void addControlPoint(int index, Point controlPoint) {
		if (controlPoint == null) {
			throw new IllegalArgumentException("controlPoint may not be null.");
		}
		IAnchor anchor = new StaticAnchor(this, controlPoint);
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
		// property is created lazily to save memory
		if (anchorsUnmodifiableProperty == null) {
			anchorsUnmodifiableProperty = new AnchorsUnmodifiableProperty();
		}
		return anchorsUnmodifiableProperty;
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
						Point newPoint = FX2Geometry
								.toPoint(getCurve().localToParent(Geometry2FX
										.toFXPoint(change.getValueAdded())));
						if (!points.get(getAnchorIndex(anchorKey))
								.equals(newPoint)) {
							points.set(getAnchorIndex(anchorKey), newPoint);
						}
					}
					refresh();
				}
			}
		};
	}

	/**
	 * Returns a property wrapping the curve {@link Node}.
	 *
	 * @return The curve {@link Node} used to visualize the connection.
	 */
	public ObjectProperty<Node> curveProperty() {
		return curveProperty;
	}

	/**
	 * Returns an {@link ObjectProperty} wrapping the end decoration
	 * {@link Node}.
	 *
	 * @return A property wrapping the end decoration.
	 */
	public ObjectProperty<Node> endDecorationProperty() {
		if (endDecorationProperty == null) {
			endDecorationProperty = new SimpleObjectProperty<>();
			endDecorationProperty.addListener(decorationListener);
		}
		return endDecorationProperty;
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
		return anchorsByKeys.get(getAnchorKey(index));
	}

	/**
	 * Returns the anchor index for the given {@link AnchorKey}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} for which the anchor index is
	 *            determined.
	 * @return The anchor index for the given {@link AnchorKey} or
	 *         <code>-1</code> in case the anchor key is not contained.
	 */
	protected int getAnchorIndex(AnchorKey anchorKey) {
		int index = 0;
		Iterator<AnchorKey> iterator = anchorsByKeys.keySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equals(anchorKey)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * Returns the {@link AnchorKey} for the given anchor index, i.e. the
	 * reverse of {@link #getAnchorIndex(AnchorKey)}.
	 *
	 * @param anchorIndex
	 *            The anchor index for which to determine the {@link AnchorKey}.
	 * @return The {@link AnchorKey} for the given anchor index.
	 */
	protected AnchorKey getAnchorKey(int anchorIndex) {
		return Iterators.get(anchorsByKeys.keySet().iterator(), anchorIndex);
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
		return FXCollections.unmodifiableObservableList(anchors);
	}

	/**
	 * Computes the 'logical' center point of the {@link Connection}, which is
	 * the middle control point position (in case the curveProperty consists of
	 * an even number of segment) or the middle point of the middle segment.
	 *
	 * @return The logical center of this {@link Connection}.
	 */
	public Point getCenter() {
		// TODO: we would better delegate this to interpolator, as there we can
		// exchange the logic
		BezierCurve[] bezierCurves = null;
		if (getCurve() instanceof GeometryNode && ((GeometryNode<?>) getCurve())
				.getGeometry() instanceof ICurve) {
			bezierCurves = ((ICurve) ((GeometryNode<?>) getCurve())
					.getGeometry()).toBezier();
		} else {
			bezierCurves = PolyBezier
					.interpolateCubic(
							getPointsUnmodifiable().toArray(new Point[] {}))
					.toBezier();
		}
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
		return new AnchorKey(getCurve(), CONTROL_POINT_ROLE_PREFIX + index);
	}

	/**
	 * Returns a {@link List} containing the control {@link IAnchor
	 * anchorsByKeys} currently assigned to this {@link Connection}.
	 *
	 * @return A {@link List} containing the control {@link IAnchor
	 *         anchorsByKeys} currently assigned to this {@link Connection}.
	 */
	public List<IAnchor> getControlAnchors() {
		int controlAnchorsCount = anchorsByKeys.size();
		if (anchorsByKeys.containsKey(getStartAnchorKey())) {
			controlAnchorsCount--;
		}
		if (anchorsByKeys.containsKey(getEndAnchorKey())) {
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
		return points.get(getAnchorIndex(getControlAnchorKey(index)));
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
	 * Returns the {@link Node} which displays the curveProperty geometry. Will
	 * be a {@link GeometryNode} by default.
	 *
	 * @return The {@link Node} which displays the curveProperty geometry.
	 */
	public Node getCurve() {
		return curveProperty.get();
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
	 * {@link AnchorKey} uses the {@link #getCurve() curveProperty node} as its
	 * anchored and <code>"end"</code> as its role.
	 *
	 * @return The end {@link AnchorKey} for this {@link Connection}.
	 */
	// TODO: AnchorKeys should not be exposed -> make protected
	protected AnchorKey getEndAnchorKey() {
		return new AnchorKey(getCurve(), END_ROLE);
	}

	/**
	 * Returns the end decoration {@link Node} of this {@link Connection}, or
	 * <code>null</code>.
	 *
	 * @return The end decoration {@link Node} of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Node getEndDecoration() {
		if (endDecorationProperty == null) {
			return null;
		}
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
		return points.get(getAnchorIndex(getEndAnchorKey()));
	}

	/**
	 * Returns the currently set end position hint or <code>null</code> if no
	 * hint is present.
	 *
	 * @return The currently set end position hint or <code>null</code> if no
	 *         hint is present.
	 */
	public Point getEndPointHint() {
		AnchorKey endAnchorKey = getEndAnchorKey();
		if (hintsByKeys.containsKey(endAnchorKey)) {
			return hintsByKeys.get(endAnchorKey);
		}
		return null;
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
		return points.get(index);
	}

	/**
	 * Returns the {@link Point}s constituting this {@link Connection} within
	 * its coordinate system in the order: start point, control points, end
	 * point.
	 *
	 * @return The {@link Point}s constituting this {@link Connection}.
	 */
	public ObservableList<Point> getPointsUnmodifiable() {
		return FXCollections.unmodifiableObservableList(points);
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
	 * {@link AnchorKey} uses the {@link #getCurve() curveProperty node} as its
	 * anchored and <code>"start"</code> as its role.
	 *
	 * @return The start {@link AnchorKey} for this {@link Connection}.
	 */
	// TODO: AnchorKeys should not be exposed -> make protected
	protected AnchorKey getStartAnchorKey() {
		return new AnchorKey(getCurve(), START_ROLE);
	}

	/**
	 * Returns the start decoration {@link Node} of this {@link Connection}, or
	 * <code>null</code>.
	 *
	 * @return The start decoration {@link Node } of this {@link Connection}, or
	 *         <code>null</code>.
	 */
	public Node getStartDecoration() {
		if (startDecorationProperty == null) {
			return null;
		}
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
		return points.get(getAnchorIndex(getStartAnchorKey()));
	}

	/**
	 * Returns the currently set start position hint or <code>null</code> if no
	 * hint is present.
	 *
	 * @return The currently set start position hint or <code>null</code> if no
	 *         hint is present.
	 */
	public Point getStartPointHint() {
		AnchorKey startAnchorKey = getStartAnchorKey();
		if (hintsByKeys.containsKey(startAnchorKey)) {
			return hintsByKeys.get(startAnchorKey);
		}
		return null;
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
		// property is created lazily to save memory
		if (pointsUnmodifiableProperty == null) {
			pointsUnmodifiableProperty = new PointsUnmodifiableProperty();
		}
		return pointsUnmodifiableProperty;
	}

	/**
	 * Re-attaches all {@link AnchorKey}s that are managed by this
	 * {@link Connection}.
	 *
	 * @param oldAnchored
	 *            The previous anchored {@link Node}.
	 * @param newAnchored
	 *            The new anchored {@link Node}.
	 */
	protected void reattachAnchorKeys(Node oldAnchored, Node newAnchored) {
		if (oldAnchored == null) {
			// In case the old value was null, we should not have any anchor
			// keys to re-attach.
			if (!anchorsByKeys.isEmpty()) {
				throw new IllegalStateException(
						"Re-attach failed: no previous curve, but anchor keys present.");
			}
			if (!hintsByKeys.isEmpty()) {
				throw new IllegalStateException(
						"Re-attach failed: no previous curve, but anchor keys present.");
			}
			return;
		} else if (newAnchored == null) {
			// In case the new value was null, we should not have any anchor
			// keys to re-attach.
			if (!anchorsByKeys.isEmpty()) {
				throw new IllegalStateException(
						"Re-attach failed: no new curve, but anchor keys present.");
			}
			if (!hintsByKeys.isEmpty()) {
				throw new IllegalStateException(
						"Re-attach failed: no new curve, but anchor keys present.");
			}
			return;
		} else {
			// Re-attach all anchor keys.
			for (AnchorKey oldAk : new ArrayList<>(anchorsByKeys.keySet())) {
				// query anchor for oldAk
				IAnchor anchor = anchorsByKeys.get(oldAk);

				// unregister old anchor key
				unregisterPCL(oldAk, anchor);
				anchorsByKeys.remove(oldAk);
				anchor.detach(oldAk);

				// create anchor key (new curve, same role)
				AnchorKey newAk = new AnchorKey(newAnchored, oldAk.getId());

				// update position hint
				if (hintsByKeys.containsKey(oldAk)) {
					hintsByKeys.put(newAk, hintsByKeys.remove(oldAk));
				}
				// XXX: anchors and points are staying the same, no need to
				// update

				// register new anchor key
				anchorsByKeys.put(newAk, anchor);
				anchor.attach(newAk);
				registerPCL(newAk, anchor);
			}
		}
	}

	/**
	 * Refreshes the visualization, i.e.
	 * <ol>
	 * <li>determines the {@link #getPointsUnmodifiable() points} constituting
	 * this {@link Connection},</li>
	 * <li>computes an {@link ICurve} geometry through those {@link Point}s
	 * using the {@link IConnectionRouter} of this {@link Connection},</li>
	 * <li>replaces the geometry of the {@link #getCurve() curveProperty node}
	 * with that {@link ICurve},</li>
	 * <li>arranges the {@link #getStartDecoration() start decoration} and
	 * {@link #getEndDecoration() end decoration} of this {@link Connection}.
	 * </li>
	 * </ol>
	 */
	protected void refresh() {
		// guard against recomputing the curveProperty while recomputing the
		// curveProperty
		if (inRefresh) {
			return;
		}
		inRefresh = true;

		// clear visuals except for the curveProperty
		getChildren().retainAll(getCurve());

		// update our anchorsByKeys/points
		if (getRouter() != null) {
			getRouter().route(this);
		} else {
			throw new IllegalStateException(
					"An IConnectionRouter is mandatory for a Connection.");
		}

		// z-order decorations above curveProperty
		if (getStartDecoration() != null) {
			getChildren().add(getStartDecoration());
		}
		if (getEndDecoration() != null) {
			getChildren().add(getEndDecoration());
		}

		// update the curveProperty node, arrange and clip the decorations
		if (getInterpolator() != null) {
			getInterpolator().interpolate(this);
		} else {
			throw new IllegalStateException(
					"An IConnectionInterpolator is mandatory for a Connection.");
		}

		// notify properties (which are lazily created)
		if (anchorsUnmodifiableProperty != null) {
			anchorsUnmodifiableProperty.fireValueChangedEvent();
		}
		if (pointsUnmodifiableProperty != null) {
			pointsUnmodifiableProperty.fireValueChangedEvent();
		}

		inRefresh = false;
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
	 * Removes all control points of this {@link Connection}.
	 */
	public void removeAllControlAnchors() {
		removeAllControlPoints();
	}

	/**
	 * Removes all control points of this {@link Connection}.
	 */
	public void removeAllControlPoints() {
		int controlPointsCount = anchorsByKeys.size();
		if (anchorsByKeys.containsKey(getStartAnchorKey())) {
			controlPointsCount--;
		}
		if (anchorsByKeys.containsKey(getEndAnchorKey())) {
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

		AnchorKey startAnchorKey = getStartAnchorKey();
		AnchorKey endAnchorKey = getEndAnchorKey();

		unregisterPCL(anchorKey, anchor);

		List<IAnchor> controlAnchorsToMove = new ArrayList<>();
		if (!anchorKey.equals(startAnchorKey)
				&& !anchorKey.equals(endAnchorKey)) {
			int controlAnchorIndex = getControlAnchorIndex(anchorKey);
			// remove all control points at a larger index
			int pointCount = anchorsByKeys.size();
			for (int i = pointCount - 1; i >= 0; i--) {
				// (temporarily) remove all anchorsByKeys that are to be moved
				// up
				AnchorKey ak = getAnchorKey(i);
				if (!ak.equals(startAnchorKey) && !ak.equals(endAnchorKey)) {
					if (getControlAnchorIndex(ak) > controlAnchorIndex) {
						IAnchor a = getAnchor(i);

						unregisterPCL(ak, a);

						controlAnchorsToMove.add(0, a);

						int anchorIndex = getAnchorIndex(ak);
						points.remove(anchorIndex);
						anchors.remove(anchorIndex);

						anchorsByKeys.remove(ak);

						a.detach(ak);
					}
				}
			}
		}

		points.remove(getAnchorIndex(anchorKey));
		anchors.remove(getAnchorIndex(anchorKey));

		anchorsByKeys.remove(anchorKey);

		anchor.detach(anchorKey);

		if (!anchorKey.equals(startAnchorKey)
				&& !anchorKey.equals(endAnchorKey)) {
			int controlIndex = getControlAnchorIndex(anchorKey);
			// re-add all control points at a larger index
			for (int i = 0; i < controlAnchorsToMove.size(); i++) {
				AnchorKey ak = getControlAnchorKey(controlIndex + i);
				IAnchor a = controlAnchorsToMove.get(i);

				anchorsByKeys.put(ak, a);

				a.attach(ak);

				int anchorIndex = getAnchorIndex(ak);
				anchors.add(anchorIndex, a);
				points.add(anchorIndex,
						FX2Geometry.toPoint(getCurve().localToParent(
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
	 * Replaces the anchor currently registered for the given {@link AnchorKey}
	 * with the given {@link IAnchor}.
	 *
	 * @param anchorKey
	 *            The {@link AnchorKey} under which the {@link IAnchor} is to be
	 *            registered.
	 * @param anchor
	 *            The {@link IAnchor} which is inserted.
	 */
	protected void setAnchor(AnchorKey anchorKey, IAnchor anchor) {
		if (anchorKey == null) {
			throw new IllegalArgumentException("anchorKey may not be null.");
		}
		if (anchorKey.getAnchored() != getCurve()) {
			throw new IllegalArgumentException(
					"anchorKey may only be anchored to curveProperty node");
		}
		if (anchor == null) {
			throw new IllegalArgumentException("anchor may not be null.");
		}

		IAnchor oldAnchor = anchorsByKeys.put(anchorKey, anchor);
		unregisterPCL(anchorKey, oldAnchor);

		// detach anchor key from old anchor
		oldAnchor.detach(anchorKey);

		// attach anchor key to new anchor
		anchor.attach(anchorKey);

		// update anchor
		int anchorIndex = getAnchorIndex(anchorKey);
		anchors.set(anchorIndex, anchor);
		// update position (if changed)
		Point newPosition = FX2Geometry.toPoint(getCurve().localToParent(
				Geometry2FX.toFXPoint(anchor.getPosition(anchorKey))));
		if (!newPosition.equals(points.get(anchorIndex))) {
			points.set(anchorIndex, newPosition);
		}

		registerPCL(anchorKey, anchor);
		refresh();
	}

	/**
	 * Replaces all anchors of this {@link Connection} with the given
	 * {@link IAnchor}s, i.e. the first given {@link IAnchor} replaces the
	 * currently assigned start anchor, the last given {@link IAnchor} replaces
	 * the currently assigned end anchor, and the intermediate {@link IAnchor}s
	 * replace the currently assigned control anchorsByKeys.
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
				setAnchor(anchorKey, anchor);
			} else {
				addAnchor(anchorKey, anchor);
			}
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
		int controlSize = getControlAnchors().size();
		boolean oldInRefresh = inRefresh;
		inRefresh = true;
		int i = 0;
		for (; i < controlSize && i < anchors.size(); i++) {
			setControlAnchor(i, anchors.get(i));
		}
		for (; i < anchors.size(); i++) {
			addControlAnchor(i, anchors.get(i));
		}
		int initialRemovalIndex = i;
		for (; i < controlSize; i++) {
			removeControlAnchor(controlSize - 1 - (i - initialRemovalIndex));
		}
		inRefresh = oldInRefresh;
		refresh();
	}

	/**
	 * Sets the control anchor for the given control anchor index to an
	 * {@link StaticAnchor} which yields the given {@link Point}.
	 *
	 * @param index
	 *            The control anchor index of the control anchor to replace.
	 * @param controlPoint
	 *            The new control {@link Point} for the respective index within
	 *            local coordinates of the {@link Connection}.
	 */
	public void setControlPoint(int index, Point controlPoint) {
		if (controlPoint == null) {
			throw new IllegalArgumentException(
					"control point may not be null.");
		}
		IAnchor anchor = new StaticAnchor(this, controlPoint);
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
	 * Sets the {@link Node} that is used to render the connection.
	 *
	 * @param curve
	 *            The new curveProperty node.
	 */
	public void setCurve(Node curve) {
		this.curveProperty.set(curve);
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
				setAnchor(anchorKey, anchor);
			} else {
				addAnchor(anchorKey, anchor);
			}
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
		endDecorationProperty().set(decoration);
	}

	/**
	 * Sets the {@link #setEndAnchor(IAnchor) end anchor} of this
	 * {@link Connection} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param endPoint
	 *            The new end {@link Point} within local coordinates of the
	 *            {@link Connection}.
	 */
	public void setEndPoint(Point endPoint) {
		if (endPoint == null) {
			throw new IllegalArgumentException("endPoint may not be null.");
		}
		IAnchor anchor = new StaticAnchor(this, endPoint);
		setEndAnchor(anchor);
	}

	/**
	 * Sets the end position hint to the given value.
	 *
	 * @param endPositionHint
	 *            The new end position hint.
	 */
	public void setEndPointHint(Point endPositionHint) {
		AnchorKey endAnchorKey = getEndAnchorKey();
		if (endPositionHint == null) {
			if (hintsByKeys.containsKey(endAnchorKey)) {
				hintsByKeys.remove(endAnchorKey);
			}
		} else {
			hintsByKeys.put(endAnchorKey, endPositionHint);
		}
		refresh();
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
	 * Replaces all anchors of this {@link Connection} with the given
	 * {@link IAnchor}s, i.e. the first given {@link IAnchor} replaces the
	 * currently assigned start anchor, the last given {@link IAnchor} replaces
	 * the currently assigned end anchor, and the intermediate {@link IAnchor}s
	 * replace the currently assigned control anchorsByKeys.
	 *
	 * @param points
	 *            The new {@link Point}s for this {@link Connection}.
	 * @throws IllegalArgumentException
	 *             when less than 2 {@link IAnchor}s are given.
	 */
	public void setPoints(List<Point> points) {
		if (points.size() < 2) {
			throw new IllegalArgumentException(
					"At least two points have to be provided.");
		}

		// prevent refresh before all points are properly set
		boolean oldInRefresh = inRefresh;
		inRefresh = true;
		setStartPoint(points.get(0));
		if (points.size() > 2) {
			setControlPoints(points.subList(1, points.size() - 1));
		} else {
			removeAllControlPoints();
		}
		setEndPoint(points.get(points.size() - 1));
		inRefresh = oldInRefresh;
		refresh();
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
				setAnchor(anchorKey, anchor);
			} else {
				addAnchor(anchorKey, anchor);
			}
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
		startDecorationProperty().set(decoration);
	}

	/**
	 * Sets the {@link #setStartAnchor(IAnchor) start anchor} of this
	 * {@link Connection} to an {@link StaticAnchor} yielding the given
	 * {@link Point}.
	 *
	 * @param startPoint
	 *            The new start {@link Point} within local coordinates of the
	 *            {@link Connection}.
	 */
	public void setStartPoint(Point startPoint) {
		if (startPoint == null) {
			throw new IllegalArgumentException("startPoint may not be null.");
		}
		IAnchor anchor = new StaticAnchor(this, startPoint);
		setStartAnchor(anchor);
	}

	/**
	 * Sets the start position hint to the given value.
	 *
	 * @param startPositionHint
	 *            The new start position hint.
	 */
	public void setStartPointHint(Point startPositionHint) {
		AnchorKey startAnchorKey = getStartAnchorKey();
		if (startPositionHint == null) {
			if (hintsByKeys.containsKey(startAnchorKey)) {
				hintsByKeys.remove(startAnchorKey);
			}
		} else {
			hintsByKeys.put(startAnchorKey, startPositionHint);
		}
		refresh();
	}

	/**
	 * Returns an {@link ObjectProperty} wrapping the start decoration
	 * {@link Node}.
	 *
	 * @return An Object Property wrapping the start decoration.
	 */
	public ObjectProperty<Node> startDecorationProperty() {
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
}
