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
package org.eclipse.gef4.fx.examples.snippets;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import javafx.stage.Window;
import javafx.util.Duration;

public class ScrollPaneEx extends Region {

	private Pane canvas;
	private Translate scrollTranslate = new Translate();
	private Group contentGroup;
	private Affine viewportTransform = new Affine();
	private Group scrollbarGroup;
	private ScrollBar horizontalScrollBar;
	private ScrollBar verticalScrollBar;
	private double[] currentScrollableBounds = new double[] { 0d, 0d, 0d, 0d };
	private ChangeListener<Number> widthChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldWidth, Number newWidth) {
			updateScrollbarVisibilities();
		}
	};
	private ChangeListener<Number> heightChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldHeight, Number newHeight) {
			updateScrollbarVisibilities();
		}
	};
	private ChangeListener<Bounds> canvasBoundsInLocalChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldBounds, Bounds newBounds) {
			updateScrollbarVisibilities();
		}
	};
	private ChangeListener<? super Bounds> contentBoundsInParentListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			updateScrollbarVisibilities();
		}
	};

	public ScrollPaneEx() {
		getChildren().addAll(getCanvas(), getScrollbarGroup());
		getCanvas().boundsInLocalProperty().addListener(
				canvasBoundsInLocalChangeListener);
		widthProperty().addListener(widthChangeListener);
		heightProperty().addListener(heightChangeListener);
	}

	protected Pane createCanvas() {
		Pane canvas = new Pane();
		canvas.getChildren().add(getContentGroup());
		canvas.getTransforms().add(scrollTranslate);
		return canvas;
	}

	private Group createContentGroup() {
		Group g = new Group();
		g.getTransforms().add(viewportTransform);
		g.boundsInParentProperty().addListener(contentBoundsInParentListener);
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
		horizontalScrollBar.prefWidthProperty().bind(
				widthProperty().subtract(verticalScrollBar.widthProperty()));

		// bind horizontal y position
		horizontalScrollBar.layoutYProperty()
				.bind(heightProperty().subtract(
						horizontalScrollBar.heightProperty()));

		// bind vertical size
		verticalScrollBar.prefHeightProperty()
				.bind(heightProperty().subtract(
						horizontalScrollBar.heightProperty()));

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
				// updateScrollableBounds();
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
							double hv = (newValue.doubleValue() - horizontalScrollBar
									.getMin())
									/ (horizontalScrollBar.getMax() - horizontalScrollBar
											.getMin());
							scrollToHv(hv);
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
							double vv = (newValue.doubleValue() - verticalScrollBar
									.getMin())
									/ (verticalScrollBar.getMax() - verticalScrollBar
											.getMin());
							scrollToVv(vv);
						}
					}
				});

		// update scrollbars on mouse release
		EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				updateScrollbarVisibilities();
			}
		};
		horizontalScrollBar.setOnMouseReleased(mouseReleasedHandler);
		verticalScrollBar.setOnMouseReleased(mouseReleasedHandler);

		return new Group(horizontalScrollBar, verticalScrollBar);
	}

	public Pane getCanvas() {
		if (canvas == null) {
			canvas = createCanvas();
		}
		return canvas;
	}

	public double[] getContentBoundsInScrollPane() {
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

	public Group getContentGroup() {
		if (contentGroup == null) {
			contentGroup = createContentGroup();
		}
		return contentGroup;
	}

	public double[] getScrollableBoundsInLocal() {
		double[] cb = getContentBoundsInScrollPane();
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

	public Group getScrollbarGroup() {
		if (scrollbarGroup == null) {
			scrollbarGroup = createScrollbarGroup();
		}
		return scrollbarGroup;
	}

	public Affine getViewportTransform() {
		return viewportTransform;
	}

	private double lerp(double min, double max, double ratio) {
		return min + ratio * (max - min);
	}

	public Point2D localToScreen(double localX, double localY) {
		Scene scene = getScene();
		Window window = scene.getWindow();
		Point2D local = localToScene(localX, localY);
		Point2D screen = new Point2D(window.getX() + scene.getX()
				+ local.getX(), window.getY() + scene.getY() + local.getY());
		return screen;
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
		node.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!node.isHover()) {
					fadeOut.run();
				}
			}
		});
	}

	public void scrollToHv(double newValue) {
		getCanvas().setTranslateX(
				-lerp(currentScrollableBounds[0], currentScrollableBounds[2]
						- getWidth(), newValue));
	}

	public void scrollToVv(double newValue) {
		getCanvas().setTranslateY(
				-lerp(currentScrollableBounds[1], currentScrollableBounds[3]
						- getHeight(), newValue));
	}

	public void setViewportTransform(Affine tx) {
		viewportTransform.setMxx(tx.getMxx());
		viewportTransform.setMxy(tx.getMxy());
		viewportTransform.setMyx(tx.getMyx());
		viewportTransform.setMyy(tx.getMyy());
		viewportTransform.setTx(tx.getTx());
		viewportTransform.setTy(tx.getTy());
	}

	private void updateScrollableBounds() {
		// double oldMinX = currentScrollableBounds[0];
		// double oldMinY = currentScrollableBounds[1];
		// double oldMaxX = currentScrollableBounds[2];
		// double oldMaxY = currentScrollableBounds[3];

		double[] bounds = getScrollableBoundsInLocal();
		for (int i = 0; i < bounds.length; i++) {
			currentScrollableBounds[i] = bounds[i];
		}

		// Point2D min = getCanvas().parentToLocal(0, 0);
		// Point2D max = getCanvas().parentToLocal(getWidth(), getHeight());
		// System.out.println("pre [" + min.getX() + ":" + min.getY() + "]["
		// + max.getX() + ":" + max.getY() + "]");

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

		boolean wasVisible = horizontalScrollBar.isVisible();
		// horizontalScrollBar.setVisible(false);
		// double x = min.getX() + (max.getX() - min.getX());
		// double hv = (x - horizontalScrollBar.getMin())
		// / (horizontalScrollBar.getMax() - horizontalScrollBar.getMin());
		// horizontalScrollBar.setValue(hv);
		horizontalScrollBar.setVisible(wasVisible);
	}

	private void updateScrollbarVisibilities() {
		double[] contentBounds = getContentBoundsInScrollPane();

		// show/hide horizontal scrollbar
		if (contentBounds[0] < 0 || contentBounds[2] > getWidth()) {
			if (!horizontalScrollBar.isVisible()) {
				horizontalScrollBar.setVisible(true);
			}
		} else {
			horizontalScrollBar.setVisible(false);
		}

		// show/hide vertical scrollbar
		if (contentBounds[1] < 0 || contentBounds[3] > getHeight()) {
			if (!verticalScrollBar.isVisible()) {
				verticalScrollBar.setVisible(true);
			}
		} else {
			verticalScrollBar.setVisible(false);
		}

		updateScrollableBounds();
	}

}
