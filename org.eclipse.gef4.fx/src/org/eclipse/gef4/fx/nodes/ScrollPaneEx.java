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

import javafx.animation.FadeTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
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
 * "viewport transformation" which is applied to the contents group. Scrolling
 * is done independently of this viewport transformation, using the translate-x
 * and translate-y properties of the scrolled pane.
 * <p>
 * The ScrollPaneEx computes two bounds: a) the contents-bounds, and b) the
 * scrollable bounds. The contents-bounds are the bounds of the contents group
 * within the ScrollPaneEx's coordinate system. The scrollable bounds are at
 * least as big as the contents-bounds but also include the viewport, i.e. any
 * empty space that is currently visible.
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
	private Affine viewportTransform = new Affine();
	private double[] currentScrollableBounds = new double[] { 0d, 0d, 0d, 0d };
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
	private ChangeListener<Bounds> canvasBoundsInLocalChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldBounds, Bounds newBounds) {
			updateScrollbars();
		}
	};
	private ChangeListener<? super Bounds> contentBoundsInParentChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			updateScrollbars();
		}
	};
	private ObjectBinding<Bounds> contentBoundsBinding;
	private ObjectBinding<Bounds> scrollableBoundsBinding;

	public ScrollPaneEx() {
		getChildren().addAll(getScrolledPane(), getScrollbarGroup());
		getScrolledPane().boundsInLocalProperty().addListener(
				canvasBoundsInLocalChangeListener);
		widthProperty().addListener(widthChangeListener);
		heightProperty().addListener(heightChangeListener);
		contentBoundsBinding = new ObjectBinding<Bounds>() {
			{
				bind(contentGroup.boundsInParentProperty());
			}

			@Override
			protected Bounds computeValue() {
				double[] bb = computeContentBoundsInLocal();
				return new BoundingBox(bb[0], bb[1], bb[2] - bb[0], bb[3]
						- bb[1]);
			}
		};
		scrollableBoundsBinding = new ObjectBinding<Bounds>() {
			{
				bind(contentGroup.boundsInParentProperty(), widthProperty(),
						heightProperty());
			}

			@Override
			protected Bounds computeValue() {
				double[] bb = computeScrollableBoundsInLocal();
				return new BoundingBox(bb[0], bb[1], bb[2] - bb[0], bb[3]
						- bb[1]);
			}
		};
	}

	public double[] computeContentBoundsInLocal() {
		Bounds diagramBoundsInCanvas = contentGroup.getBoundsInParent();
		double minX = diagramBoundsInCanvas.getMinX();
		double maxX = diagramBoundsInCanvas.getMaxX();
		double minY = diagramBoundsInCanvas.getMinY();
		double maxY = diagramBoundsInCanvas.getMaxY();

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
	public double computeHv(double tx) {
		return lerp(
				horizontalScrollBar.getMin(),
				horizontalScrollBar.getMax(),
				norm(currentScrollableBounds[0], currentScrollableBounds[2]
						- getWidth(), -tx));
	}

	public double[] computeScrollableBoundsInLocal() {
		double[] cb = computeContentBoundsInLocal();
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
	public double computeTx(double hv) {
		return -lerp(
				currentScrollableBounds[0],
				currentScrollableBounds[2] - getWidth(),
				norm(horizontalScrollBar.getMin(),
						horizontalScrollBar.getMax(), hv));
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
	public double computeTy(double vv) {
		return -lerp(
				currentScrollableBounds[1],
				currentScrollableBounds[3] - getHeight(),
				norm(verticalScrollBar.getMin(), verticalScrollBar.getMax(), vv));
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
	public double computeVv(double ty) {
		return lerp(
				verticalScrollBar.getMin(),
				verticalScrollBar.getMax(),
				norm(currentScrollableBounds[1], currentScrollableBounds[3]
						- getHeight(), -ty));
	}

	protected Group createContentGroup() {
		Group g = new Group();
		g.getTransforms().add(viewportTransform);
		g.boundsInParentProperty().addListener(
				contentBoundsInParentChangeListener);
		return g;
	}

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
				return verticalScrollBar.isVisible() ? verticalScrollBar
						.getWidth() : 0;
			}
		};
		horizontalScrollBar.prefWidthProperty().bind(
				widthProperty().subtract(vWidth));

		// bind horizontal y position
		horizontalScrollBar.layoutYProperty()
				.bind(heightProperty().subtract(
						horizontalScrollBar.heightProperty()));

		// bind vertical size
		DoubleBinding hHeight = new DoubleBinding() {
			{
				bind(horizontalScrollBar.visibleProperty(),
						horizontalScrollBar.heightProperty());
			}

			@Override
			protected double computeValue() {
				return horizontalScrollBar.isVisible() ? horizontalScrollBar
						.getHeight() : 0;
			}
		};
		verticalScrollBar.prefHeightProperty().bind(
				heightProperty().subtract(hHeight));

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
		horizontalScrollBar.valueProperty().addListener(
				new ChangeListener<Number>() {
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
		verticalScrollBar.valueProperty().addListener(
				new ChangeListener<Number>() {
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

	protected Pane createScrolledPane() {
		Pane canvas = new Pane();
		canvas.getChildren().add(getContentGroup());
		return canvas;
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

	public Bounds getBoundsInViewport(Node child) {
		return sceneToLocal(child.localToScene(child.getBoundsInLocal()));
	}

	public ObjectBinding<Bounds> getContentBoundsBinding() {
		return contentBoundsBinding;
	}

	public Group getContentGroup() {
		if (contentGroup == null) {
			contentGroup = createContentGroup();
		}
		return contentGroup;
	}

	public ScrollBar getHorizontalScrollBar() {
		return horizontalScrollBar;
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

	public ObjectBinding<Bounds> getScrollableBoundsBinding() {
		return scrollableBoundsBinding;
	}

	public Group getScrollbarGroup() {
		if (scrollbarGroup == null) {
			scrollbarGroup = createScrollbarGroup();
		}
		return scrollbarGroup;
	}

	public Pane getScrolledPane() {
		if (scrolledPane == null) {
			scrolledPane = createScrolledPane();
		}
		return scrolledPane;
	}

	public double getScrollOffsetX() {
		return getScrolledPane().getTranslateX();
	}

	public double getScrollOffsetY() {
		return getScrolledPane().getTranslateY();
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

	public ScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}

	/**
	 * Returns the transformation that is applied to the
	 * {@link #getContentGroup()}.
	 *
	 * @return The transformation that is applied to the
	 *         {@link #getContentGroup()}.
	 */
	public Affine getViewportTransform() {
		return viewportTransform;
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

	public double lerpHvRatio(double hvRatio) {
		return lerp(horizontalScrollBar.getMin(), horizontalScrollBar.getMax(),
				hvRatio);
	}

	public double lerpVvRatio(double vvRatio) {
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

	public double normHv(double hv) {
		return norm(horizontalScrollBar.getMin(), horizontalScrollBar.getMax(),
				hv);
	}

	public double normVv(double vv) {
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
		// TODO: guarantee that scrollbars fade out when they are not in use
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
		node.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!node.isHover()) {
					fadeOut.run();
				}
			}
		});
	}

	/**
	 * Ensure that the specified child {@link Node} is visible to the user by
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
				setScrollOffsetY(getScrollOffsetY() - bounds.getMinY());
			} else if (bounds.getMaxY() > getHeight()) {
				setScrollOffsetY(getScrollOffsetY() + getHeight()
						- bounds.getMaxY());
			}
		}

		if (bounds.getWidth() <= getWidth()) {
			if (bounds.getMinX() < 0) {
				setScrollOffsetX(getScrollOffsetX() - bounds.getMinX());
			} else if (bounds.getMaxX() > getWidth()) {
				setScrollOffsetX(getScrollOffsetX() + getWidth()
						- bounds.getMaxX());
			}
		}
	}

	public void setScrollOffsetX(double scrollOffsetX) {
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

	public void setScrollOffsetY(double scrollOffsetY) {
		updateScrollbars();
		double vv = computeVv(scrollOffsetY);
		if (vv < verticalScrollBar.getMin() || vv > verticalScrollBar.getMax()) {
			throw new IllegalArgumentException(
					"Vertical scrolling offset outside range ["
							+ verticalScrollBar.getMin() + ";"
							+ verticalScrollBar.getMax() + "]");
		}
		getScrolledPane().setTranslateY(scrollOffsetY);
	}

	public void setViewportTransform(Affine tx) {
		viewportTransform.setMxx(tx.getMxx());
		viewportTransform.setMxy(tx.getMxy());
		viewportTransform.setMyx(tx.getMyx());
		viewportTransform.setMyy(tx.getMyy());
		viewportTransform.setTx(tx.getTx());
		viewportTransform.setTy(tx.getTy());
		updateScrollbars();
		// update bounds
		contentBoundsBinding.invalidate();
		scrollableBoundsBinding.invalidate();
	}

	protected void updateScrollbars() {
		// show/hide scrollbars
		double[] contentBounds = computeContentBoundsInLocal();
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
		double[] bounds = computeScrollableBoundsInLocal();
		for (int i = 0; i < bounds.length; i++) {
			currentScrollableBounds[i] = bounds[i];
		}

		// update scrollbar ranges
		horizontalScrollBar.setMin(currentScrollableBounds[0]);
		horizontalScrollBar.setMax(currentScrollableBounds[2]);
		horizontalScrollBar.setVisibleAmount(getWidth());
		horizontalScrollBar.setBlockIncrement(getWidth() / 2);
		horizontalScrollBar.setUnitIncrement(getWidth() / 10);
		verticalScrollBar.setMin(currentScrollableBounds[1]);
		verticalScrollBar.setMax(currentScrollableBounds[3]);
		verticalScrollBar.setVisibleAmount(getHeight());
		verticalScrollBar.setBlockIncrement(getHeight() / 2);
		verticalScrollBar.setUnitIncrement(getHeight() / 10);

		// compute scrollbar values from canvas translation (in case the
		// scrollbar values are incorrect)
		horizontalScrollBar.setValue(computeHv(getScrolledPane()
				.getTranslateX()));
		verticalScrollBar
				.setValue(computeVv(getScrolledPane().getTranslateY()));
	}

}
