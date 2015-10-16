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
import javafx.scene.transform.Affine;
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

		protected void repaintGrid() {
			final double width = getWidth();
			final double height = getHeight();

			final GraphicsContext gc = getGraphicsContext2D();
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, width, height);

			final Scale scale = new Scale(gridTransformProperty.get().getMxx(),
					gridTransformProperty.get().getMyy());
			// don't paint grid points if size is to large
			if (((width / scale.getX())
					* (height / scale.getY()) > GRID_THRESHOLD)) {
				return;
			}

			gc.setFill(Color.GREY);

			double scaledGridCellWidth = gridCellWidthProperty.get()
					* scale.getX();
			double scaledGridCellHeight = gridCellHeightProperty.get()
					* scale.getY();
			for (double x = -(getParent().getLayoutX()
					- gridTransformProperty.get().getTx())
					% scaledGridCellWidth; x < width; x += scaledGridCellWidth) {
				for (double y = -(getParent().getLayoutY()
						- gridTransformProperty.get().getTy())
						% scaledGridCellHeight; y < height; y += scaledGridCellHeight) {
					gc.fillRect(Math.floor(x) - 0.5 * scale.getX(),
							Math.floor(y) - 0.5 * scale.getY(), scale.getX(),
							scale.getY());
				}
			}
		}
	}

	private final FXGridLayer.GridCanvas gridCanvas;

	private final SimpleObjectProperty<Affine> gridTransformProperty = new SimpleObjectProperty<Affine>(
			new Affine());

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

		final ChangeListener<Number> repaintListener = new ChangeListener<Number>() {
			@Override
			public void changed(
					final ObservableValue<? extends Number> observable,
					final Number oldValue, final Number newValue) {
				gridCanvas.repaintGrid();
			}
		};
		Affine gridTransform = gridTransformProperty().get();
		gridTransform.txProperty().addListener(repaintListener);
		gridTransform.tyProperty().addListener(repaintListener);
		gridTransform.mxxProperty().addListener(repaintListener);
		gridTransform.myyProperty().addListener(repaintListener);
		gridTransformProperty.addListener(new ChangeListener<Affine>() {

			@Override
			public void changed(ObservableValue<? extends Affine> observable,
					Affine oldValue, Affine newValue) {
				oldValue.txProperty().removeListener(repaintListener);
				oldValue.tyProperty().removeListener(repaintListener);
				oldValue.mxxProperty().removeListener(repaintListener);
				oldValue.myyProperty().removeListener(repaintListener);
				newValue.txProperty().addListener(repaintListener);
				newValue.tyProperty().addListener(repaintListener);
				newValue.mxxProperty().addListener(repaintListener);
				newValue.myyProperty().addListener(repaintListener);
				gridCanvas.repaintGrid();
			}

		});
		gridCellWidthProperty.addListener(repaintListener);
		gridCellHeightProperty.addListener(repaintListener);
		layoutXProperty().addListener(repaintListener);
		layoutYProperty().addListener(repaintListener);
	}

	/**
	 * Binds the minimum size of this {@link FXGridLayer} to the given property.
	 *
	 * @param bounds
	 *            The {@link Bounds} property which determines the minimum size
	 *            for this {@link FXGridLayer}.
	 */
	public void bindBounds(final ReadOnlyObjectProperty<Bounds> bounds) {
		layoutXProperty().bind(new DoubleBinding() {
			{
				super.bind(bounds);
			}

			@Override
			protected double computeValue() {
				return Math.min(0, bounds.get().getMinX());
			}
		});
		layoutYProperty().bind(new DoubleBinding() {
			{
				super.bind(bounds);
			}

			@Override
			protected double computeValue() {
				return Math.min(0, bounds.get().getMinY());
			}
		});
		minWidthProperty().bind(new DoubleBinding() {
			{
				super.bind(bounds, gridTransformProperty.get().mxxProperty());
			}

			@Override
			protected double computeValue() {
				if (bounds.get() == null) {
					return 0;
				}
				return bounds.get().getWidth();
			}
		});
		minHeightProperty().bind(new DoubleBinding() {
			{
				super.bind(bounds, gridTransformProperty.get().myyProperty());
			}

			@Override
			protected double computeValue() {
				if (bounds.get() == null) {
					return 0;
				}
				return bounds.get().getHeight();
			}
		});
	}

	/**
	 * Returns the {@link Affine} transform property of this {@link FXGridLayer}
	 * . This can be used to adapt the grid layer to zooming changes within the
	 * content which is above the grid.
	 *
	 * @return The {@link Affine} transform property of this {@link FXGridLayer}
	 *         .
	 */
	public ObjectProperty<Affine> gridTransformProperty() {
		return gridTransformProperty;
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
