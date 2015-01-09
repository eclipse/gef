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

public class ScrollPaneEx extends Region {

	private Group scrollbarGroup;
	private ScrollBar horizontalScrollBar;
	private ScrollBar verticalScrollBar;
	private Pane canvas;
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
		getChildren().addAll(getCanvas(), getScrollbarGroup());
		getCanvas().boundsInLocalProperty().addListener(
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

		Point2D minInScrolled = getCanvas().localToParent(minX, minY);
		double realMinX = minInScrolled.getX();
		double realMinY = minInScrolled.getY();
		double realMaxX = realMinX + (maxX - minX);
		double realMaxY = realMinY + (maxY - minY);

		return new double[] { realMinX, realMinY, realMaxX, realMaxY };
	}

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

	public double computeTx(double hv) {
		return -lerp(
				currentScrollableBounds[0],
				currentScrollableBounds[2] - getWidth(),
				norm(horizontalScrollBar.getMin(),
						horizontalScrollBar.getMax(), hv));
	}

	public double computeTy(double vv) {
		return -lerp(
				currentScrollableBounds[1],
				currentScrollableBounds[3] - getHeight(),
				norm(verticalScrollBar.getMin(), verticalScrollBar.getMax(), vv));
	}

	public double computeVv(double ty) {
		return lerp(
				verticalScrollBar.getMin(),
				verticalScrollBar.getMax(),
				norm(currentScrollableBounds[1], currentScrollableBounds[3]
						- getHeight(), -ty));
	}

	protected Pane createCanvas() {
		Pane canvas = new Pane();
		canvas.getChildren().add(getContentGroup());
		return canvas;
	}

	protected Group createContentGroup() {
		Group g = new Group();
		g.getTransforms().add(viewportTransform);
		g.boundsInParentProperty().addListener(
				contentBoundsInParentChangeListener);
		return g;
	}

	protected Group createHudGroup() {
		return new Group();
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
							getCanvas().setTranslateX(
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
							getCanvas().setTranslateY(
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

	public double getBottomExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMaxY() - getHeight();
	}

	public Bounds getBoundsInViewport(Node child) {
		Bounds bounds = canvas.localToParent(child.getBoundsInParent());
		return bounds;
	}

	public Pane getCanvas() {
		if (canvas == null) {
			canvas = createCanvas();
		}
		return canvas;
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

	public double getLeftExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return -bounds.getMinX();
	}

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

	public double getScrollOffsetX() {
		return getCanvas().getTranslateX();
	}

	public double getScrollOffsetY() {
		return getCanvas().getTranslateY();
	}

	public double getTopExcess(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return -bounds.getMinY();
	}

	public ScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}

	public Affine getViewportTransform() {
		return viewportTransform;
	}

	public boolean isFullyVisible(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMinX() >= 0 && bounds.getMaxX() <= getWidth()
				&& bounds.getMinY() >= 0 && bounds.getMaxY() <= getHeight();
	}

	public boolean isVisible(Node child) {
		Bounds bounds = getBoundsInViewport(child);
		return bounds.getMaxX() >= 0 && bounds.getMinX() <= getWidth()
				&& bounds.getMaxY() >= 0 && bounds.getMinY() <= getHeight();
	}

	private double lerp(double min, double max, double ratio) {
		double d = min + ratio * (max - min);
		return Double.isNaN(d) ? 0 : d;
	}

	private double norm(double min, double max, double value) {
		double d = (value - min) / (max - min);
		return Double.isNaN(d) ? 0 : d;
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
		getCanvas().setTranslateX(scrollOffsetX);
	}

	public void setScrollOffsetY(double scrollOffsetY) {
		getCanvas().setTranslateY(scrollOffsetY);
	}

	public void setViewportTransform(Affine tx) {
		viewportTransform.setMxx(tx.getMxx());
		viewportTransform.setMxy(tx.getMxy());
		viewportTransform.setMyx(tx.getMyx());
		viewportTransform.setMyy(tx.getMyy());
		viewportTransform.setTx(tx.getTx());
		viewportTransform.setTy(tx.getTy());
		updateScrollbars();
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
		horizontalScrollBar.setValue(computeHv(getCanvas().getTranslateX()));
		verticalScrollBar.setValue(computeVv(getCanvas().getTranslateY()));
	}

}
