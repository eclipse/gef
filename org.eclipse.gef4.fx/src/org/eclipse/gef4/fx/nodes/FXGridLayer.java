/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

public class FXGridLayer extends Pane {

	private class GridCanvas extends Canvas {

		public GridCanvas() {
			// Redraw canvas when size changes.
			widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						final ObservableValue<? extends Number> observable,
						final Number oldValue, final Number newValue) {
					repaintGrid();
				}
			});
			heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						final ObservableValue<? extends Number> observable,
						final Number oldValue, final Number newValue) {
					repaintGrid();
				}
			});
		}

		@Override
		public boolean isResizable() {
			return true;
		}

		@Override
		public double prefHeight(final double width) {
			return getHeight();
		}

		@Override
		public double prefWidth(final double height) {
			return getWidth();
		}

		public void repaintGrid() {
			final double width = getWidth();
			final double height = getHeight();

			final GraphicsContext gc = getGraphicsContext2D();
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, width, height);

			// don't paint grid if size is to large (TODO: remove canvas (make
			// invisible)
			if ((width > 2500) || (height > 2500)) {
				return;
			}

			// TODO: extract (unscaled) grid size into properties
			gc.setStroke(Color.GREY);
			final Scale scale = scaleProperty.get();
			for (double x = ((-getParent().getLayoutX()) / scale.getX()) % 10; x < width; x += 10) {
				for (double y = ((-getParent().getLayoutY()) / scale.getY()) % 10; y < height; y += 10) {
					// TODO: use circle
					gc.strokeLine(x, y, x, y);
				}
			}
		}
	}

	private final FXGridLayer.GridCanvas gridCanvas;
	private final SimpleObjectProperty<Scale> scaleProperty = new SimpleObjectProperty<Scale>(
			new Scale());

	public FXGridLayer() {
		final Scale scale = new Scale();
		getTransforms().add(scale);
		scaleProperty.addListener(new ChangeListener<Scale>() {
			@Override
			public void changed(
					final ObservableValue<? extends Scale> observable,
					final Scale oldValue, final Scale newValue) {
				scale.setX(newValue.getX());
				scale.setY(newValue.getY());
			}
		});
		gridCanvas = new GridCanvas();
		getChildren().add(gridCanvas);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		gridCanvas.widthProperty().bind(widthProperty());
		gridCanvas.heightProperty().bind(heightProperty());
		setPickOnBounds(false);
		setMouseTransparent(true);

		layoutXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
					final ObservableValue<? extends Number> observable,
					final Number oldValue, final Number newValue) {
				gridCanvas.repaintGrid();
			}
		});
		layoutYProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
					final ObservableValue<? extends Number> observable,
					final Number oldValue, final Number newValue) {
				gridCanvas.repaintGrid();
			}
		});
	}

	public void bindMinSizeToBounds(
			final ReadOnlyObjectProperty<Bounds> unscaledMinSizeProperty) {
		minWidthProperty().bind(new DoubleBinding() {
			{
				super.bind(unscaledMinSizeProperty, scaleProperty.get()
						.xProperty());
			}

			@Override
			protected double computeValue() {
				if (unscaledMinSizeProperty.get() == null) {
					return 0;
				}
				return Math
						.ceil(((unscaledMinSizeProperty.get().getWidth() / scaleProperty
								.get().getX()) - 1));
			}
		});

		minHeightProperty().bind(new DoubleBinding() {
			{
				super.bind(unscaledMinSizeProperty, scaleProperty.get()
						.yProperty());
			}

			@Override
			protected double computeValue() {
				if (unscaledMinSizeProperty.get() == null) {
					return 0;
				}
				return Math
						.ceil((unscaledMinSizeProperty.get().getHeight() / scaleProperty
								.get().getY()) - 1);
			}
		});
	}

	public void bindPrefSizeToUnionedBounds(
			@SuppressWarnings("unchecked") final ReadOnlyObjectProperty<Bounds>... unscaledBoundsProperties) {

		layoutXProperty().bind(new DoubleBinding() {
			{
				super.bind(unscaledBoundsProperties);
			}

			@Override
			protected double computeValue() {
				double minX = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : unscaledBoundsProperties) {
					minX = Math.min(minX, b.get().getMinX());
				}
				return minX;
			}
		});

		layoutYProperty().bind(new DoubleBinding() {
			{
				super.bind(unscaledBoundsProperties);
			}

			@Override
			protected double computeValue() {
				double minY = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : unscaledBoundsProperties) {
					minY = Math.min(minY, b.get().getMinY());
				}
				return minY;
			}
		});

		prefWidthProperty().bind(new DoubleBinding() {
			{
				final Observable[] observables = new Observable[unscaledBoundsProperties.length + 2];
				observables[0] = layoutXProperty();
				observables[1] = scaleProperty.get().xProperty();
				for (int i = 0; i < unscaledBoundsProperties.length; i++) {
					observables[i + 2] = unscaledBoundsProperties[i];
				}
				super.bind(observables);
			}

			@Override
			protected double computeValue() {
				double maxX = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : unscaledBoundsProperties) {
					maxX = Math.max(maxX, b.get().getMaxX());
				}
				return maxX
						- (layoutXProperty().get() / scaleProperty.get().getX());
			}
		});

		prefHeightProperty().bind(new DoubleBinding() {
			{
				final Observable[] observables = new Observable[unscaledBoundsProperties.length + 2];
				observables[0] = layoutYProperty();
				observables[1] = scaleProperty.get().yProperty();
				for (int i = 0; i < unscaledBoundsProperties.length; i++) {
					observables[i + 2] = unscaledBoundsProperties[i];
				}
				super.bind(observables);
			}

			@Override
			protected double computeValue() {
				double maxY = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : unscaledBoundsProperties) {
					maxY = Math.max(maxY, b.get().getMaxY());
				}
				return maxY
						- (layoutYProperty().get() / scaleProperty.get().getY());
			}
		});

	}

	public void bindToScale(ReadOnlyObjectProperty<Scale> scaleProperty) {
		this.scaleProperty.bind(scaleProperty);
	}

}
