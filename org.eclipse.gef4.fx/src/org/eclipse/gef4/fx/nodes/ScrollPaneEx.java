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
 *     Alexander Nyßen (itemis AG)  - refactorings and cleanups
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.Arrays;

import javafx.animation.FadeTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

/**
 * A {@link ScrollPaneEx} provides a scrollable {@link Pane} in which contents
 * can be placed. {@link ScrollBar}s are automatically added to the ScrollPaneEx
 * when its contents exceed the viewport.
 * <p>
 * On the top level, a ScrollPaneEx consists of a "scrolled pane" and a
 * "scrollbars group". Inside of the scrolled pane, a "contents group" contains
 * all user nodes, which can be scrolled by the ScrollPaneEx. The scrollbars
 * group contains the horizontal and vertical scrollbars. It is rendered above
 * the scrolled pane.
 * <p>
 * In order to rotate or scale the viewport, you can access a
 * "content transformation" which is applied to the contents group. Scrolling is
 * done independently of this transformation, using the translate-x and
 * translate-y properties of the scrolled pane, which corresponds to the
 * horizontal and vertical scroll offset.
 * <p>
 * The ScrollPaneEx computes two bounds: a) the contents-bounds, and b) the
 * scrollable bounds. The contents-bounds are the bounds of the contents group
 * within the ScrollPaneEx's coordinate system. The scrollable bounds are at
 * least as big as the contents-bounds but also include the complete viewport,
 * i.e. any empty space that is currently visible.
 * <p>
 * The ScrollPaneEx provides the scroll position in multiple formats: a) the
 * values of the scrollbars (depends on scrollable bounds), b) the ratios of the
 * scrollbars (in range <code>[0;1]</code>), and c) the translation values of
 * the scrolled pane. You can use the various <code>compute()</code>,
 * <code>lerp()</code>, and <code>norm()</code> methods to convert from one
 * format to the other.
 */
public class ScrollPaneEx extends Region {

	private Group scrollbarGroup;
	private ScrollBar horizontalScrollBar;
	private ScrollBar verticalScrollBar;

	private Pane scrolledPane;
	private Group contentGroup;

	private ReadOnlyObjectWrapper<Affine> contentTransformProperty = new ReadOnlyObjectWrapper<Affine>(
			new Affine());

	// content and scrollable bounds are cached
	private double[] contentBounds = new double[] { 0d, 0d, 0d, 0d };
	private double[] scrollableBounds = new double[] { 0d, 0d, 0d, 0d };
	private ObjectBinding<Bounds> scrollableBoundsBinding;
	private ObjectBinding<Bounds> contentBoundsBinding;
	private ReadOnlyObjectWrapper<Bounds> scrollableBoundsProperty;
	private ReadOnlyObjectWrapper<Bounds> contentBoundsProperty;

	private ChangeListener<Number> widthChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldWidth, Number newWidth) {
			updateScrollbars();
		}
	};
	private ChangeListener<Number> heightChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldHeight, Number newHeight) {
			updateScrollbars();
		}
	};
	private ChangeListener<Bounds> scrolledPaneBoundsInLocalChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldBounds, Bounds newBounds) {
			updateScrollbars();
		}
	};
	private ChangeListener<? super Bounds> contentGroupBoundsInParentChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			updateScrollbars();
		}
	};

	/**
	 * Constructs a new {@link ScrollPaneEx}.
	 */
	public ScrollPaneEx() {
		getChildren().addAll(getScrolledPane(), getScrollbarGroup());

		widthProperty().addListener(widthChangeListener);
		heightProperty().addListener(heightChangeListener);

		contentBoundsBinding = new ObjectBinding<Bounds>() {
			@Override
			protected Bounds computeValue() {
				return new BoundingBox(contentBounds[0], contentBounds[1],
						contentBounds[2] - contentBounds[0],
						contentBounds[3] - contentBounds[1]);
			}
		};
		contentBoundsProperty = new ReadOnlyObjectWrapper<Bounds>() {
			{
				bind(contentBoundsBinding);
			}
		};

		scrollableBoundsBinding = new ObjectBinding<Bounds>() {
			@Override
			protected Bounds computeValue() {
				return new BoundingBox(scrollableBounds[0], scrollableBounds[1],
						scrollableBounds[2] - scrollableBounds[0],
						scrollableBounds[3] - scrollableBounds[1]);
			}
		};
		scrollableBoundsProperty = new ReadOnlyObjectWrapper<Bounds>() {
			{
				bind(scrollableBoundsBinding);
			}
		};
	}

	/**
	 * Computes the bounds <code>[min-x, min-y, max-x, max-y]</code> surrounding
	 * the {@link #getContentGroup() content group} within the coordinate system
	 * of this {@link ScrollPaneEx}.
	 *
	 * @return The bounds <code>[min-x, min-y, max-x, max-y]</code> surrounding
	 *         the {@link #getContentGroup() content group} within the
	 *         coordinate system of this {@link ScrollPaneEx}.
	 */
	protected double[] computeContentBoundsInLocal() {
		Bounds contentBoundsInScrolledPane = contentGroup.getBoundsInParent();
		double minX = contentBoundsInScrolledPane.getMinX();
		double maxX = contentBoundsInScrolledPane.getMaxX();
		double minY = contentBoundsInScrolledPane.getMinY();
		double maxY = contentBoundsInScrolledPane.getMaxY();

		Point2D minInScrolled = getScrolledPane().localToParent(minX, minY);
		double realMinX = minInScrolled.getX();
		double realMinY = minInScrolled.getY();
		double realMaxX = realMinX + (maxX - minX);
		double realMaxY = realMinY + (maxY - minY);

		return new double[] { realMinX, realMinY, realMaxX, realMaxY };
	}

	/**
	 * Converts a horizontal translation distance into the corresponding
	 * horizontal scrollbar value.
	 *
	 * @param tx
	 *            The horizontal translation distance.
	 * @return The horizontal scrollbar value corresponding to the given
	 *         translation.
	 */
	protected double computeHv(double tx) {
		return lerp(horizontalScrollBar.getMin(), horizontalScrollBar.getMax(),
				norm(scrollableBounds[0], scrollableBounds[2] - getWidth(),
						-tx));
	}

	/**
	 * Computes and returns the bounds of the scrollable area within this
	 * {@link ScrollPaneEx}.
	 *
	 * @return The bounds of the scrollable area, i.e.
	 *         <code>[minx, miny, maxx, maxy]</code>.
	 */
	protected double[] computeScrollableBoundsInLocal() {
		double[] cb = Arrays.copyOf(contentBounds, contentBounds.length);
		Bounds db = contentGroup.getBoundsInParent();

		// factor in the viewport extending the content bounds
		if (cb[0] < 0) {
			cb[0] = 0;
		}
		if (cb[1] < 0) {
			cb[1] = 0;
		}
		if (cb[2] > getWidth()) {
			cb[2] = 0;
		} else {
			cb[2] = getWidth() - cb[2];
		}
		if (cb[3] > getHeight()) {
			cb[3] = 0;
		} else {
			cb[3] = getHeight() - cb[3];
		}

		return new double[] { db.getMinX() - cb[0], db.getMinY() - cb[1],
				db.getMaxX() + cb[2], db.getMaxY() + cb[3] };
	}

	/**
	 * Converts a horizontal scrollbar value into the corresponding horizontal
	 * translation distance.
	 *
	 * @param hv
	 *            The horizontal scrollbar value.
	 * @return The horizontal translation distance corresponding to the given
	 *         scrollbar value.
	 */
	protected double computeTx(double hv) {
		return -lerp(scrollableBounds[0], scrollableBounds[2] - getWidth(),
				norm(horizontalScrollBar.getMin(), horizontalScrollBar.getMax(),
						hv));
	}

	/**
	 * Converts a vertical scrollbar value into the corresponding vertical
	 * translation distance.
	 *
	 * @param vv
	 *            The vertical scrollbar value.
	 * @return The vertical translation distance corresponding to the given
	 *         scrollbar value.
	 */
	protected double computeTy(double vv) {
		return -lerp(scrollableBounds[1], scrollableBounds[3] - getHeight(),
				norm(verticalScrollBar.getMin(), verticalScrollBar.getMax(),
						vv));
	}

	/**
	 * Converts a vertical translation distance into the corresponding vertical
	 * scrollbar value.
	 *
	 * @param ty
	 *            The vertical translation distance.
	 * @return The vertical scrollbar value corresponding to the given
	 *         translation.
	 */
	protected double computeVv(double ty) {
		return lerp(verticalScrollBar.getMin(), verticalScrollBar.getMax(),
				norm(scrollableBounds[1], scrollableBounds[3] - getHeight(),
						-ty));
	}

	/**
	 * Provides the visual bounds of the content group in the local coordinate
	 * system of this {@link ScrollPaneEx}
	 *
	 * @return The bounds of the content group, i.e.
	 *         <code>minx, miny, maxx, maxy</code>.
	 */
	public ReadOnlyObjectProperty<Bounds> contentBoundsProperty() {
		return contentBoundsProperty.getReadOnlyProperty();
	}

	/**
	 * Returns the viewport transform as a (read-only) property.
	 *
	 * @return The viewport transform as {@link ReadOnlyObjectProperty}.
	 */
	public ReadOnlyObjectProperty<Affine> contentTransformProperty() {
		return contentTransformProperty.getReadOnlyProperty();
	}

	/**
	 * Creates the {@link Group} designated for holding the scrolled content.
	 * The {@link #getContentTransform() viewport transform} is added to the
	 * transforms list of that {@link Group}.
	 *
	 * @return The {@link Group} designated for holding the scrolled content.
	 */
	protected Group createContentGroup() {
		Group g = new Group();
		g.getTransforms().add(contentTransformProperty.get());
		g.boundsInParentProperty()
				.addListener(contentGroupBoundsInParentChangeListener);
		return g;
	}

	/**
	 * Creates the {@link Group} designated for holding the scrollbars and
	 * places the scrollbars in it. Furthermore, event listeners are registered
	 * to update the scroll offset upon scrollbar movement.
	 *
	 * @return The {@link Group} designated for holding the scrollbars.
	 */
	protected Group createScrollbarGroup() {
		horizontalScrollBar = new ScrollBar();
		horizontalScrollBar.setVisible(false);
		horizontalScrollBar.setOpacity(0.5);

		verticalScrollBar = new ScrollBar();
		verticalScrollBar.setOrientation(Orientation.VERTICAL);
		verticalScrollBar.setVisible(false);
		verticalScrollBar.setOpacity(0.5);

		// bind horizontal size
		DoubleBinding vWidth = new DoubleBinding() {
			{
				bind(verticalScrollBar.visibleProperty(),
						verticalScrollBar.widthProperty());
			}

			@Override
			protected double computeValue() {
				return verticalScrollBar.isVisible()
						? verticalScrollBar.getWidth() : 0;
			}
		};
		horizontalScrollBar.prefWidthProperty()
				.bind(widthProperty().subtract(vWidth));

		// bind horizontal y position
		horizontalScrollBar.layoutYProperty().bind(heightProperty()
				.subtract(horizontalScrollBar.heightProperty()));

		// bind vertical size
		DoubleBinding hHeight = new DoubleBinding() {
			{
				bind(horizontalScrollBar.visibleProperty(),
						horizontalScrollBar.heightProperty());
			}

			@Override
			protected double computeValue() {
				return horizontalScrollBar.isVisible()
						? horizontalScrollBar.getHeight() : 0;
			}
		};
		verticalScrollBar.prefHeightProperty()
				.bind(heightProperty().subtract(hHeight));

		// bind vertical x position
		verticalScrollBar.layoutXProperty().bind(
				widthProperty().subtract(verticalScrollBar.widthProperty()));

		// fade in/out on mouse enter/exit
		registerInOutTransitions(horizontalScrollBar);
		registerInOutTransitions(verticalScrollBar);

		// update scrollable bounds on mouse press
		EventHandler<MouseEvent> mousePressFilter = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				updateScrollbars();
			}
		};
		horizontalScrollBar.addEventFilter(MouseEvent.MOUSE_PRESSED,
				mousePressFilter);
		verticalScrollBar.addEventFilter(MouseEvent.MOUSE_PRESSED,
				mousePressFilter);

		// translate on scroll
		horizontalScrollBar.valueProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						if (horizontalScrollBar.isVisible()) {
							getScrolledPane().setTranslateX(
									computeTx(newValue.doubleValue()));
						}
					}
				});
		verticalScrollBar.valueProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						if (verticalScrollBar.isVisible()) {
							getScrolledPane().setTranslateY(
									computeTy(newValue.doubleValue()));
						}
					}
				});

		// update scrollbars on mouse release
		EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				updateScrollbars();
			}
		};
		horizontalScrollBar.setOnMouseReleased(mouseReleasedHandler);
		verticalScrollBar.setOnMouseReleased(mouseReleasedHandler);

		return new Group(horizontalScrollBar, verticalScrollBar);
	}

	/**
	 * Creates the {@link Pane} which is translated when scrolling and inserts
	 * the {@link #getContentGroup() content group} into it. Therefore, the
	 * {@link #getContentTransform() viewport transform} does not influence the
	 * scroll offset.
	 *
	 * @return The {@link Pane} which is translated when scrolling.
	 */
	protected Pane createScrolledPane() {
		Pane scrolledPane = new Pane();
		scrolledPane.getChildren().add(getContentGroup());
		scrolledPane.boundsInLocalProperty()
				.addListener(scrolledPaneBoundsInLocalChangeListener);
		return scrolledPane;
	}

	/**
	 * Returns the amount of units the given child {@link Node} exceeds the
	 * bottom side of the viewport.
	 *
	 * @param child
	 *            A direct or indirect child {@link Node} of this
	 *            {@link ScrollPaneEx}.
	 * @return The amount of units the given child {@link Node} exceeds the
	 *         bottom side of the viewport.
	 */
	public double getBottomExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMaxY() - getHeight();
	}

	/**
	 * Transforms the bounds-in-local of the given {@link Node} into the
	 * coordinate system of this {@link ScrollPaneEx}, i.e. the viewport
	 * coordinate system.
	 *
	 * @param child
	 *            The {@link Node} whose bounds-in-local are transformed.
	 * @return The new, transformed {@link Bounds}.
	 */
	public Bounds getBoundsInViewport(Node child) {
		return sceneToLocal(child.localToScene(child.getBoundsInLocal()));
	}

	/**
	 * Returns the {@link Group} designated for holding the scrolled content.
	 *
	 * @return The {@link Group} designated for holding the scrolled content.
	 */
	public Group getContentGroup() {
		if (contentGroup == null) {
			contentGroup = createContentGroup();
		}
		return contentGroup;
	}

	/**
	 * Returns the transformation that is applied to the
	 * {@link #getContentGroup() content group}.
	 *
	 * @return The transformation that is applied to the
	 *         {@link #getContentGroup() content group}.
	 */
	public Affine getContentTransform() {
		return contentTransformProperty.get();
	}

	/**
	 * Returns the horizontal {@link ScrollBar}.
	 *
	 * @return The horizontal {@link ScrollBar}.
	 */
	public ScrollBar getHorizontalScrollBar() {
		return horizontalScrollBar;
	}

	/**
	 * Returns the current horizontal scroll offset.
	 *
	 * @return The current horizontal scroll offset.
	 */
	public double getHorizontalScrollOffset() {
		return getScrolledPane().getTranslateX();
	}

	/**
	 * Returns the amount of units the given child {@link Node} exceeds the left
	 * side of the viewport.
	 *
	 * @param child
	 *            A direct or indirect child {@link Node} of this
	 *            {@link ScrollPaneEx}.
	 * @return The amount of units the given child {@link Node} exceeds the left
	 *         side of the viewport.
	 */
	public double getLeftExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return -bounds.getMinX();
	}

	/**
	 * Returns the amount of units the given child {@link Node} exceeds the
	 * right side of the viewport.
	 *
	 * @param child
	 *            A direct or indirect child {@link Node} of this
	 *            {@link ScrollPaneEx}.
	 * @return The amount of units the given child {@link Node} exceeds the
	 *         right side of the viewport.
	 */
	public double getRightExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMaxX() - getWidth();
	}

	/**
	 * Returns the {@link Group} designated for holding the {@link ScrollBar}s.
	 *
	 * @return The {@link Group} designated for holding the {@link ScrollBar}s.
	 */
	public Group getScrollbarGroup() {
		if (scrollbarGroup == null) {
			scrollbarGroup = createScrollbarGroup();
		}
		return scrollbarGroup;
	}

	/**
	 * Returns the {@link Pane} which is translated when scrolling. This
	 * {@link Pane} contains the {@link #getContentGroup() content group},
	 * therefore, the {@link #getContentTransform() viewport transform} does not
	 * influence the scroll offset.
	 *
	 * @return The {@link Pane} which is translated when scrolling.
	 */
	public Pane getScrolledPane() {
		if (scrolledPane == null) {
			scrolledPane = createScrolledPane();
		}
		return scrolledPane;
	}

	/**
	 * Returns the amount of units the given child {@link Node} exceeds the top
	 * side of the viewport.
	 *
	 * @param child
	 *            A direct or indirect child {@link Node} of this
	 *            {@link ScrollPaneEx}.
	 * @return The amount of units the given child {@link Node} exceeds the top
	 *         side of the viewport.
	 */
	public double getTopExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return -bounds.getMinY();
	}

	/**
	 * Returns the vertical {@link ScrollBar}.
	 *
	 * @return The vertical {@link ScrollBar}.
	 */
	public ScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}

	/**
	 * Returns the current vertical scroll offset.
	 *
	 * @return The current vertical scroll offset.
	 */
	public double getVerticalScrollOffset() {
		return getScrolledPane().getTranslateY();
	}

	/**
	 * Returns the horizontal scroll offset as a property.
	 *
	 * @return A {@link DoubleProperty} representing the horizontal scroll
	 *         offset.
	 */
	public DoubleProperty horizontalScrollOffsetProperty() {
		return getScrolledPane().translateXProperty();
	}

	/**
	 * Returns <code>true</code> when the given direct or indirect child
	 * {@link Node} is fully visible, i.e. does not exceed the viewport in any
	 * direction. Otherwise returns <code>false</code>.
	 *
	 * @param child
	 *            A direct or indirect child {@link Node} of this
	 *            {@link ScrollPaneEx}.
	 * @return <code>true</code> when the given child {@link Node} is fully
	 *         visible, otherwise <code>false</code>.
	 */
	public boolean isFullyVisible(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMinX() >= 0 && bounds.getMaxX() <= getWidth()
				&& bounds.getMinY() >= 0 && bounds.getMaxY() <= getHeight();
	}

	/**
	 * Returns <code>true</code> when the given direct or indirect child
	 * {@link Node} is visible, i.e. does not exceed the viewport in all
	 * directions. Otherwise returns <code>false</code>.
	 *
	 * @param child
	 *            A direct or indirect child {@link Node} of this
	 *            {@link ScrollPaneEx}.
	 * @return <code>true</code> when the given child {@link Node} is fully
	 *         visible, otherwise <code>false</code>.
	 */
	public boolean isVisible(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMaxX() >= 0 && bounds.getMinX() <= getWidth()
				&& bounds.getMaxY() >= 0 && bounds.getMinY() <= getHeight();
	}

	/**
	 * Linear interpolation between <i>min</i> and <i>max</i> at the given
	 * <i>ratio</i>. Returns the interpolated value in the interval
	 * <code>[min;max]</code>.
	 *
	 * @param min
	 *            The lower interval bound.
	 * @param max
	 *            The upper interval bound.
	 * @param ratio
	 *            A value in the interval <code>[0;1]</code>.
	 * @return The interpolated value.
	 */
	private double lerp(double min, double max, double ratio) {
		double d = (1 - ratio) * min + ratio * max;
		return Double.isNaN(d) ? 0 : Math.min(max, Math.max(min, d));
	}

	/**
	 * Linear intERPolation (LERP) of the given horizontal ratio (in range
	 * <code>[0;1]</code>) onto the horizontal scroll offset range.
	 *
	 * @param hvRatio
	 *            The horizontal ratio to lerp (in range <code>[0;1]</code>).
	 * @return The corresponding value on the {@link #getHorizontalScrollBar()
	 *         horizontal scrollbar}.
	 */
	protected double lerpHvRatio(double hvRatio) {
		return lerp(horizontalScrollBar.getMin(), horizontalScrollBar.getMax(),
				hvRatio);
	}

	/**
	 * Linear intERPolation (LERP) of the given vertical ratio (in range
	 * <code>[0;1]</code>) onto the vertical scroll offset range.
	 *
	 * @param vvRatio
	 *            The vertical ratio to lerp (in range <code>[0;1]</code>).
	 * @return The corresponding value on the {@link #getVerticalScrollBar()
	 *         vertical scrollbar}.
	 */
	protected double lerpVvRatio(double vvRatio) {
		return lerp(verticalScrollBar.getMin(), verticalScrollBar.getMax(),
				vvRatio);
	}

	/**
	 * Normalizes a given <i>value</i> which is in range <code>[min;max]</code>
	 * to range <code>[0;1]</code>.
	 *
	 * @param min
	 *            The lower bound of the range.
	 * @param max
	 *            The upper bound of the range.
	 * @param value
	 *            The value in the range.
	 * @return The normalized value (in range <code>[0;1]</code>).
	 */
	private double norm(double min, double max, double value) {
		double d = (value - min) / (max - min);
		return Double.isNaN(d) ? 0 : Math.min(1, Math.max(0, d));
	}

	/**
	 * Normalizes the given horizontal scroll offset to a ratio in the range
	 * <code>[0;1]</code>.
	 *
	 * @param hv
	 *            The horizontal scroll offset to normalize.
	 * @return The normalized ratio in the range <code>[0;1]</code>.
	 */
	protected double normHv(double hv) {
		return norm(horizontalScrollBar.getMin(), horizontalScrollBar.getMax(),
				hv);
	}

	/**
	 * Normalizes the given vertical scroll offset to a ratio in the range
	 * <code>[0;1]</code>.
	 *
	 * @param vv
	 *            The vertical scroll offset to normalize.
	 * @return The normalized ratio in the range <code>[0;1]</code>.
	 */
	protected double normVv(double vv) {
		return norm(verticalScrollBar.getMin(), verticalScrollBar.getMax(), vv);
	}

	private void registerInOutTransitions(final Node node) {
		// create transitions
		final FadeTransition fadeInTransition = new FadeTransition(
				Duration.millis(200), node);
		fadeInTransition.setToValue(1.0);
		final FadeTransition fadeOutTransition = new FadeTransition(
				Duration.millis(200), node);
		fadeOutTransition.setToValue(0.5);

		// create actions
		final Runnable fadeIn = new Runnable() {
			@Override
			public void run() {
				fadeOutTransition.stop();
				fadeInTransition.playFromStart();
			}
		};
		final Runnable fadeOut = new Runnable() {
			@Override
			public void run() {
				fadeInTransition.stop();
				fadeOutTransition.playFromStart();
			}
		};

		// register event handlers
		node.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				fadeIn.run();
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!node.isPressed()) {
					fadeOut.run();
				}
			}
		});
		node.pressedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (oldValue.booleanValue() && !newValue.booleanValue()) {
					fadeOut.run();
				}
			}
		});
	}

	/**
	 * Ensures that the specified child {@link Node} is visible to the user by
	 * scrolling to its position. The effect and style of the node are taken
	 * into consideration. After revealing a node, it will be fully visible, if
	 * it fits within the current viewport bounds.
	 * <p>
	 * When the child node's left side is left to the viewport, it will touch
	 * the left border of the viewport after revealing. When the child node's
	 * right side is right to the viewport, it will touch the right border of
	 * the viewport after revealing. When the child node's top side is above the
	 * viewport, it will touch the top border of the viewport after revealing.
	 * When the child node's bottom side is below the viewport, it will touch
	 * the bottom border of the viewport after revealing.
	 * <p>
	 * TODO: When the child node does not fit within the viewport bounds, it is
	 * not revealed.
	 *
	 * @param child
	 *            The child {@link Node} to reveal.
	 */
	public void reveal(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		if (bounds.getHeight() <= getHeight()) {
			if (bounds.getMinY() < 0) {
				setVerticalScrollOffset(
						getVerticalScrollOffset() - bounds.getMinY());
			} else if (bounds.getMaxY() > getHeight()) {
				setVerticalScrollOffset(getVerticalScrollOffset() + getHeight()
						- bounds.getMaxY());
			}
		}

		if (bounds.getWidth() <= getWidth()) {
			if (bounds.getMinX() < 0) {
				setHorizontalScrollOffset(
						getHorizontalScrollOffset() - bounds.getMinX());
			} else if (bounds.getMaxX() > getWidth()) {
				setHorizontalScrollOffset(getHorizontalScrollOffset()
						+ getWidth() - bounds.getMaxX());
			}
		}
	}

	/**
	 * Returns the bounds of the scrollable area in local coordinates of this
	 * {@link ScrollPaneEx}. The scrollable area corresponds to the visual
	 * bounds of the content group, which is expanded to cover at least the area
	 * of this {@link ScrollPaneEx} (i.e. the viewport) if necessary. It is
	 * thereby also the area that can be navigated via the scroll bars.
	 *
	 * @return The bounds of the scrollable area, i.e.
	 *         <code>minx, miny, maxx, maxy</code>.
	 */
	public ReadOnlyObjectProperty<Bounds> scrollableBoundsProperty() {
		return scrollableBoundsProperty.getReadOnlyProperty();
	}

	/**
	 * Sets the transformation matrix of the {@link #getContentTransform()
	 * viewport transform} to the values specified by the given {@link Affine}.
	 *
	 * @param tx
	 *            The {@link Affine} determining the new
	 *            {@link #getContentTransform() viewport transform}.
	 */
	public void setContentTransform(Affine tx) {
		Affine viewportTransform = contentTransformProperty.get();
		viewportTransform.setMxx(tx.getMxx());
		viewportTransform.setMxy(tx.getMxy());
		viewportTransform.setMyx(tx.getMyx());
		viewportTransform.setMyy(tx.getMyy());
		viewportTransform.setTx(tx.getTx());
		viewportTransform.setTy(tx.getTy());

		updateScrollbars();
	}

	/**
	 * Sets the horizontal scroll offset to the given value.
	 *
	 * @param scrollOffsetX
	 *            The new horizontal scroll offset.
	 * @throws IllegalArgumentException
	 *             when the given horizontal scroll offset is outside of the
	 *             value range of the {@link #getHorizontalScrollBar()
	 *             horizontal scrollbar}.
	 */
	public void setHorizontalScrollOffset(double scrollOffsetX) {
		updateScrollbars();
		double hv = computeHv(scrollOffsetX);
		if (hv < horizontalScrollBar.getMin()
				|| hv > horizontalScrollBar.getMax()) {
			throw new IllegalArgumentException(
					"Horizontal scrolling offset outside range ["
							+ horizontalScrollBar.getMin() + ";"
							+ horizontalScrollBar.getMax() + "]");
		}
		getScrolledPane().setTranslateX(scrollOffsetX);
	}

	/**
	 * Sets the vertical scroll offset to the given value.
	 *
	 * @param scrollOffsetY
	 *            The new vertical scroll offset.
	 * @throws IllegalArgumentException
	 *             when the given vertical scroll offset is outside of the value
	 *             range of the {@link #getVerticalScrollBar() vertical
	 *             scrollbar}.
	 */
	public void setVerticalScrollOffset(double scrollOffsetY) {
		updateScrollbars();
		double vv = computeVv(scrollOffsetY);
		if (vv < verticalScrollBar.getMin()
				|| vv > verticalScrollBar.getMax()) {
			throw new IllegalArgumentException(
					"Vertical scrolling offset outside range ["
							+ verticalScrollBar.getMin() + ";"
							+ verticalScrollBar.getMax() + "]");
		}
		getScrolledPane().setTranslateY(scrollOffsetY);
	}

	/**
	 * Updates the {@link ScrollBar}s' visibilities, value ranges and value
	 * increments based on the {@link #computeContentBoundsInLocal() content
	 * bounds} and the {@link #computeScrollableBoundsInLocal() scrollable
	 * bounds}. The update is not done if any of the {@link ScrollBar}s is
	 * currently in use.
	 */
	protected void updateScrollbars() {
		// do not update while a scrollbar is pressed, so that the scrollable
		// area does not change while using a scrollbar
		if (horizontalScrollBar.isPressed() || verticalScrollBar.isPressed()) {
			return;
		}

		// determine current content bounds
		contentBounds = computeContentBoundsInLocal();
		contentBoundsBinding.invalidate();

		// show/hide scrollbars
		if (contentBounds[0] < 0 || contentBounds[2] > getWidth()) {
			horizontalScrollBar.setVisible(true);
		} else {
			horizontalScrollBar.setVisible(false);
		}
		if (contentBounds[1] < 0 || contentBounds[3] > getHeight()) {
			verticalScrollBar.setVisible(true);
		} else {
			verticalScrollBar.setVisible(false);
		}

		// determine current scrollable bounds
		scrollableBounds = computeScrollableBoundsInLocal();
		scrollableBoundsBinding.invalidate();

		// update scrollbar ranges
		horizontalScrollBar.setMin(scrollableBounds[0]);
		horizontalScrollBar.setMax(scrollableBounds[2]);
		horizontalScrollBar.setVisibleAmount(getWidth());
		horizontalScrollBar.setBlockIncrement(getWidth() / 2);
		horizontalScrollBar.setUnitIncrement(getWidth() / 10);
		verticalScrollBar.setMin(scrollableBounds[1]);
		verticalScrollBar.setMax(scrollableBounds[3]);
		verticalScrollBar.setVisibleAmount(getHeight());
		verticalScrollBar.setBlockIncrement(getHeight() / 2);
		verticalScrollBar.setUnitIncrement(getHeight() / 10);

		// compute scrollbar values from canvas translation (in case the
		// scrollbar values are incorrect)
		horizontalScrollBar
				.setValue(computeHv(getScrolledPane().getTranslateX()));
		verticalScrollBar
				.setValue(computeVv(getScrolledPane().getTranslateY()));
	}

	/**
	 * Returns the vertical scroll offset as a property.
	 *
	 * @return A {@link DoubleProperty} representing the vertical scroll offset.
	 */
	public DoubleProperty verticalScrollOffsetProperty() {
		return getScrolledPane().translateYProperty();
	}

}
