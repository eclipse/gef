/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

/**
 * The {@link FXGridLayer} can be used as a background layer which displays a
 * uniform grid.
 *
 * @author anyssen
 *
 */
public class FXGridLayer extends Pane {

	private class GridCanvas extends Canvas {

		private static final int GRID_THRESHOLD = 5000000;

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

			final Scale scale = gridScaleProperty.get();
			// don't paint grid points if size is to large
			if (((width / scale.getX()) * (height / scale.getY()) > GRID_THRESHOLD)) {
				return;
			}

			gc.setFill(Color.GREY);

			double scaledGridCellWidth = gridCellWidthProperty.get()
					* scale.getX();
			double scaledGridCellHeight = gridCellHeightProperty.get()
					* scale.getY();
			for (double x = (-getParent().getLayoutX()) % scaledGridCellWidth; x < width; x += scaledGridCellWidth) {
				for (double y = (-getParent().getLayoutY())
						% scaledGridCellHeight; y < height; y += scaledGridCellHeight) {
					gc.fillRect(Math.floor(x) - 0.5 * scale.getX(),
							Math.floor(y) - 0.5 * scale.getY(), scale.getX(),
							scale.getY());
				}
			}
		}
	}

	private final FXGridLayer.GridCanvas gridCanvas;

	private final SimpleObjectProperty<Scale> gridScaleProperty = new SimpleObjectProperty<Scale>(
			new Scale());

	private final DoubleProperty gridCellHeightProperty = new SimpleDoubleProperty(
			10);
	private final DoubleProperty gridCellWidthProperty = new SimpleDoubleProperty(
			10);

	/**
	 * Constructs a new {@link FXGridLayer}.
	 */
	public FXGridLayer() {
		gridCanvas = new GridCanvas();
		gridCanvas.setManaged(false);
		getChildren().add(gridCanvas);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		gridCanvas.widthProperty().bind(widthProperty());
		gridCanvas.heightProperty().bind(heightProperty());
		setPickOnBounds(false);
		setMouseTransparent(true);

		gridScaleProperty.addListener(new ChangeListener<Scale>() {

			@Override
			public void changed(ObservableValue<? extends Scale> observable,
					Scale oldValue, Scale newValue) {
				gridCanvas.repaintGrid();
			}

		});
		gridCellWidthProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
					final ObservableValue<? extends Number> observable,
					final Number oldValue, final Number newValue) {
				gridCanvas.repaintGrid();
			}
		});
		gridCellHeightProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
					final ObservableValue<? extends Number> observable,
					final Number oldValue, final Number newValue) {
				gridCanvas.repaintGrid();
			}
		});
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

	/**
	 * Binds the minimum size of this {@link FXGridLayer} to the given property.
	 *
	 * @param minSizeProperty
	 *            The {@link Bounds} property which determines the minimum size
	 *            for this {@link FXGridLayer}.
	 */
	public void bindMinSizeToBounds(
			final ReadOnlyObjectProperty<Bounds> minSizeProperty) {
		minWidthProperty().bind(new DoubleBinding() {
			{
				super.bind(minSizeProperty, gridScaleProperty.get().xProperty());
			}

			@Override
			protected double computeValue() {
				if (minSizeProperty.get() == null) {
					return 0;
				}
				return minSizeProperty.get().getWidth();
			}
		});

		minHeightProperty().bind(new DoubleBinding() {
			{
				super.bind(minSizeProperty, gridScaleProperty.get().yProperty());
			}

			@Override
			protected double computeValue() {
				if (minSizeProperty.get() == null) {
					return 0;
				}
				return minSizeProperty.get().getHeight();
			}
		});
	}

	/**
	 * Binds the preferred size of this {@link FXGridLayer} to the maximum of
	 * the given properties.
	 *
	 * @param boundsProperties
	 *            The {@link Bounds} properties which determine the preferred
	 *            size for this {@link FXGridLayer}.
	 */
	public void bindPrefSizeToUnionedBounds(
			@SuppressWarnings("unchecked") final ReadOnlyObjectProperty<Bounds>... boundsProperties) {
		layoutXProperty().bind(new DoubleBinding() {
			{
				super.bind(boundsProperties);
			}

			@Override
			protected double computeValue() {
				double minX = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : boundsProperties) {
					minX = Math.min(minX, b.get().getMinX());
				}
				return minX;
			}
		});
		layoutYProperty().bind(new DoubleBinding() {
			{
				super.bind(boundsProperties);
			}

			@Override
			protected double computeValue() {
				double minY = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : boundsProperties) {
					minY = Math.min(minY, b.get().getMinY());
				}
				return minY;
			}
		});
		prefWidthProperty().bind(new DoubleBinding() {
			{
				super.bind(boundsProperties);
			}

			@Override
			protected double computeValue() {
				double minX = 0;
				double maxX = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : boundsProperties) {
					Bounds bounds = b.get();
					minX = Math.min(minX, bounds.getMinX());
					maxX = Math.max(maxX, bounds.getMaxX());
				}
				// fill up to viewport width
				return maxX - minX;
			}
		});
		prefHeightProperty().bind(new DoubleBinding() {
			{
				super.bind(boundsProperties);
			}

			@Override
			protected double computeValue() {
				double minY = 0;
				double maxY = 0;
				for (final ReadOnlyObjectProperty<Bounds> b : boundsProperties) {
					Bounds bounds = b.get();
					minY = Math.min(minY, bounds.getMinY());
					maxY = Math.max(maxY, bounds.getMaxY());
				}
				// fill up to viewport height
				return maxY - minY;
			}
		});
	}

	/**
	 * Returns the {@link Scale} property of this {@link FXGridLayer}. This can
	 * be used to adapt the grid layer to zooming changes within the content
	 * which is above the grid.
	 *
	 * @return The {@link Scale} property of this {@link FXGridLayer}.
	 */
	public ObjectProperty<Scale> gridScaleProperty() {
		return gridScaleProperty;
	}

	/**
	 * Sets the grid cell height to the given value.
	 *
	 * @param height
	 *            The new grid cell height.
	 */
	public void setGridHeight(double height) {
		gridCellHeightProperty.set(height);
	}

	/**
	 * Sets the grid cell width to the given value.
	 *
	 * @param width
	 *            The new grid cell width.
	 */
	public void setGridWidth(double width) {
		gridCellWidthProperty.set(width);
	}

}
