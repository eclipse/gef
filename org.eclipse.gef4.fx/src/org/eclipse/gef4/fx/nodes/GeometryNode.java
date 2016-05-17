/*******************************************************************************
 * Copyright (c) 2013, 2014 itemis AG and others.
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

import org.eclipse.gef4.fx.utils.Geometry2Shape;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IScalable;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.ITranslatable;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * A {@link GeometryNode} is a {@link Node} which can be constructed using an
 * underlying {@link IGeometry}. It is comparable to a {@link Shape}, while a
 * {@link GeometryNode} in contrast can be resized. Furthermore, the geometric
 * bounds of a {@link GeometryNode} can be virtually extended for the purpose of
 * mouse hit-testing to realize a 'clickable area'.
 * <p>
 * Technically, a {@link GeometryNode} is a {@link Region} that internally holds
 * a {@link Path geometric shape}, which is updated to reflect the given
 * {@link IGeometry}, and to which all visual properties are delegated. The
 * 'clickable' area is realized by a transparent, non-mouse transparent overlay
 * that uses the same {@link IGeometry}, extended by the
 * {@link #clickableAreaWidthProperty() clickable area width}.
 * <p>
 * Please note that because {@link IGeometry} does not support change
 * notifications itself, changes to the underlying {@link IGeometry} will not be
 * recognized by the {@link GeometryNode} unless the {@link #geometryProperty()
 * geometry property} is changed.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <T>
 *            An {@link IGeometry} used to define the geometric shape of this
 *            {@link GeometryNode}
 */
public class GeometryNode<T extends IGeometry> extends Region {

	private static final double GEOMETRIC_SHAPE_MIN_WIDTH = 0.01;
	private static final double GEOMETRIC_SHAPE_MIN_HEIGHT = 0.01;

	private Path geometricShape = new Path();
	private Path clickableAreaShape = null;
	private DoubleProperty clickableAreaWidth = new SimpleDoubleProperty();
	private ObjectProperty<T> geometryProperty = new SimpleObjectProperty<>();

	private ChangeListener<T> geometryChangeListener = new ChangeListener<T>() {
		@Override
		public void changed(ObservableValue<? extends T> observable, T oldValue,
				T newValue) {
			if (newValue != null) {
				widthProperty().removeListener(widthListener);
				heightProperty().removeListener(heightListener);
				layoutXProperty().removeListener(layoutXListener);
				layoutYProperty().removeListener(layoutYListener);

				// XXX: We need to clear the size caches; even if we use
				// computed sizes in the following, if not doing so the super
				// call will use stale values.
				requestLayout();

				// update layoutX, layoutY, as well as layout bounds
				GeometryNode.super.resize(computePrefWidth(newValue),
						computePrefHeight(newValue));
				GeometryNode.super.relocate(
						newValue.getBounds().getX() - getStrokeOffset()
								- getInsets().getLeft(),
						newValue.getBounds().getY() - getStrokeOffset()
								- getInsets().getTop());

				widthProperty().addListener(widthListener);
				heightProperty().addListener(heightListener);
				layoutXProperty().addListener(layoutXListener);
				layoutYProperty().addListener(layoutYListener);

				// update visuals to reflect changes
				updateShapes();
			}
		}
	};
	private ChangeListener<Number> widthListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			geometryProperty.removeListener(geometryChangeListener);
			resizeGeometryToMatchLayoutBoundsSize(newValue.doubleValue(),
					getHeight());
			geometryProperty.addListener(geometryChangeListener);
		}
	};
	private ChangeListener<Number> heightListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			geometryProperty.removeListener(geometryChangeListener);
			resizeGeometryToMatchLayoutBoundsSize(getWidth(),
					newValue.doubleValue());
			geometryProperty.addListener(geometryChangeListener);
		}
	};
	private ChangeListener<Number> layoutXListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			geometryProperty.removeListener(geometryChangeListener);
			relocateGeometryToMatchLayoutXY(newValue.doubleValue(),
					getLayoutY());
			geometryProperty.addListener(geometryChangeListener);
		}
	};
	private ChangeListener<Number> layoutYListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			geometryProperty.removeListener(geometryChangeListener);
			relocateGeometryToMatchLayoutXY(getLayoutX(),
					newValue.doubleValue());
			geometryProperty.addListener(geometryChangeListener);
		}
	};

	/**
	 * Constructs a new {@link GeometryNode} without an {@link IGeometry}.
	 */
	public GeometryNode() {
		// ensure only our children are mouse-sensitive
		setPickOnBounds(false);

		setGeometricShape(geometricShape);

		// update path elements whenever the geometry property is changed
		geometryProperty.addListener(geometryChangeListener);

		// stroke width and type affect the layout bounds, so we have to react
		// to changes
		strokeWidthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				T geometry = geometryProperty.get();
				if (geometry == null) {
					return;
				}

				resize(prefWidth(-1), prefHeight(-1));
				Rectangle geometricBounds = geometry.getBounds();
				relocate(
						geometricBounds.getX() - getStrokeOffset()
								- getInsets().getLeft(),
						geometricBounds.getY() - getStrokeOffset()
								- getInsets().getTop());
			}
		});
		strokeTypeProperty().addListener(new ChangeListener<StrokeType>() {

			@Override
			public void changed(
					ObservableValue<? extends StrokeType> observable,
					StrokeType oldValue, StrokeType newValue) {
				T geometry = geometryProperty.get();
				if (geometry == null) {
					return;
				}

				resize(prefWidth(-1), prefHeight(-1));
				Rectangle geometricBounds = geometry.getBounds();
				relocate(
						geometricBounds.getX() - getStrokeOffset()
								- getInsets().getLeft(),
						geometricBounds.getY() - getStrokeOffset()
								- getInsets().getTop());
			}
		});

		// // TODO: enable for JavaFX-8 (using reflection until we drop support
		// for java-1.7)
		// insetsProperty().addListener(new ChangeListener<Insets>() {
		//
		// @Override
		// public void changed(ObservableValue<? extends Insets> observable,
		// Insets oldValue, Insets newValue) {
		// resize(prefWidth(-1), prefHeight(-1));
		// }
		// });

		// resize geometry in case width and height change
		widthProperty().addListener(widthListener);
		heightProperty().addListener(heightListener);

		// relocate geometry in case layoutX, layoutY change
		layoutXProperty().addListener(layoutXListener);
		layoutYProperty().addListener(layoutYListener);
	}

	/**
	 * Constructs a new {@link GeometryNode} which displays the given
	 * {@link IGeometry}.
	 *
	 * @param geom
	 *            The {@link IGeometry} to display.
	 */
	public GeometryNode(T geom) {
		this();
		setGeometry(geom);
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
	public DoubleProperty clickableAreaWidthProperty() {
		return clickableAreaWidth;
	}

	private double computeGeometryMinHeight(T geometry) {
		return geometry instanceof IShape ? GEOMETRIC_SHAPE_MIN_HEIGHT : 0;
	}

	private double computeGeometryMinWidth(T geometry) {
		return geometry instanceof IShape ? GEOMETRIC_SHAPE_MIN_WIDTH : 0;
	}

	@Override
	protected double computeMinHeight(double width) {
		return computeMinHeight(geometryProperty.get());
	}

	private double computeMinHeight(T geometry) {
		return computeGeometryMinHeight(geometry) + 2 * getStrokeOffset()
				+ getInsets().getTop() + getInsets().getBottom();
	}

	@Override
	protected double computeMinWidth(double height) {
		return computeMinWidth(geometryProperty.get());
	}

	private double computeMinWidth(T geometry) {
		return computeGeometryMinWidth(geometry) + 2 * getStrokeOffset()
				+ getInsets().getLeft() + getInsets().getRight();
	}

	@Override
	protected double computePrefHeight(double width) {
		return computePrefHeight(geometryProperty.get());
	}

	private double computePrefHeight(T geometry) {
		double geometricPrefHeight = Math.max(
				geometry != null ? geometry.getBounds().getHeight() : 0,
				computeGeometryMinHeight(geometry));
		return geometricPrefHeight + 2 * getStrokeOffset()
				+ getInsets().getTop() + getInsets().getBottom();
	}

	@Override
	protected double computePrefWidth(double height) {
		return computePrefWidth(geometryProperty.get());
	}

	private double computePrefWidth(T geometry) {
		double geometricPrefWidth = Math.max(
				geometry != null ? geometry.getBounds().getWidth() : 0,
				computeGeometryMinWidth(geometry));
		return geometricPrefWidth + 2 * getStrokeOffset()
				+ getInsets().getLeft() + getInsets().getRight();
	}

	/**
	 * Provides a {@link Property} holding the fill that is applied to the
	 * {@link Path} internally used by this {@link GeometryNode}.
	 *
	 * @return A (writable) property for the fill of this node.
	 * @see javafx.scene.shape.Shape#fillProperty()
	 */
	public final ObjectProperty<Paint> fillProperty() {
		return geometricShape.fillProperty();
	}

	/**
	 * Provides a {@link Property} holding the fill rule to apply for this
	 * {@link GeometryNode}.
	 *
	 * @return A (writable) property for the fill rule of this node.
	 * @see javafx.scene.shape.Path#fillRuleProperty()
	 */
	public final ObjectProperty<FillRule> fillRuleProperty() {
		return geometricShape.fillRuleProperty();
	}

	/**
	 * Provides a {@link Property} holding the geometry of this
	 * {@link GeometryNode}.
	 *
	 * @return A (writable) property for the geometry of this node.
	 */
	public ObjectProperty<T> geometryProperty() {
		return geometryProperty;
	}

	/**
	 * Retrieves the value of the clickable area width property (
	 * {@link #clickableAreaWidthProperty()}).
	 *
	 * @return The current value of the {@link #clickableAreaWidthProperty()}.
	 */
	public double getClickableAreaWidth() {
		return clickableAreaWidth.get();
	}

	/**
	 * Retrieves the value of the fill property.
	 *
	 * @return The value of the fill property.
	 *
	 * @see javafx.scene.shape.Shape#getFill()
	 */
	public final Paint getFill() {
		return geometricShape.getFill();
	}

	/**
	 * Retrieves the value of the fill rule property.
	 *
	 * @return The value of the fill rule property.
	 *
	 * @see javafx.scene.shape.Path#getFillRule()
	 */
	public final FillRule getFillRule() {
		return geometricShape.getFillRule();
	}

	/**
	 * Returns the {@link Shape} that is used as a delegate to render the
	 * geometry of this {@link GeometryNode}.
	 *
	 * @return The geometric shape used by this {@link GeometryNode}.
	 */
	protected Path getGeometricShape() {
		return geometricShape;
	}

	/**
	 * Retrieves the value of the geometry property.
	 *
	 * @return The value of the geometry property.
	 */
	public T getGeometry() {
		return geometryProperty.get();
	}

	private PathElement[] getPathElements() {
		return Geometry2Shape.toPathElements(geometryProperty.get().toPath());
	}

	/**
	 * Retrieves the value of the stroke property.
	 *
	 * @return The value of the stroke property.
	 *
	 * @see javafx.scene.shape.Shape#getStroke()
	 */
	public final Paint getStroke() {
		return geometricShape.getStroke();
	}

	/**
	 * Retrieves the value of the stroke dash array property.
	 *
	 * @return The value of the stroke dash array property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeDashArray()
	 */
	public final ObservableList<Double> getStrokeDashArray() {
		return geometricShape.getStrokeDashArray();
	}

	/**
	 * Retrieves the value of the stroke dash offset property.
	 *
	 * @return The value of the stroke dash offset property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeDashOffset()
	 */
	public final double getStrokeDashOffset() {
		return geometricShape.getStrokeDashOffset();
	}

	/**
	 * Retrieves the value of the stroke line cap property.
	 *
	 * @return The value of the stroke line cap property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeLineCap()
	 */
	public final StrokeLineCap getStrokeLineCap() {
		return geometricShape.getStrokeLineCap();
	}

	/**
	 * Retrieves the value of the stroke line join property.
	 *
	 * @return The value of the stroke line join property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeLineJoin()
	 */
	public final StrokeLineJoin getStrokeLineJoin() {
		return geometricShape.getStrokeLineJoin();
	}

	/**
	 * Retrieves the value of the stroke miter limit property.
	 *
	 * @return The value of the stroke miter limit property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeMiterLimit()
	 */
	public final double getStrokeMiterLimit() {
		return geometricShape.getStrokeMiterLimit();
	}

	private double getStrokeOffset() {
		double offset = 0;
		if (geometricShape.getStroke() != null
				&& geometricShape.getStrokeType() != StrokeType.INSIDE) {
			offset = (geometricShape.getStrokeType() == StrokeType.CENTERED
					? 0.5 : 1) * geometricShape.getStrokeWidth();
		}
		return offset;
	}

	/**
	 * Retrieves the value of the stroke type property.
	 *
	 * @return The value of the stroke type property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeType()
	 */
	public final StrokeType getStrokeType() {
		return geometricShape.getStrokeType();
	}

	/**
	 * Retrieves the value of the stroke width property.
	 *
	 * @return The value of the stroke width property.
	 *
	 * @see javafx.scene.shape.Shape#getStrokeWidth()
	 */
	public final double getStrokeWidth() {
		return geometricShape.getStrokeWidth();
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	/**
	 * Retrieves the value of the smooth property.
	 *
	 * @return The value of the smooth property.
	 * @see javafx.scene.shape.Shape#isSmooth()
	 */
	public final boolean isSmooth() {
		return geometricShape.isSmooth();
	}

	@Override
	public void relocate(double x, double y) {
		// prevent unnecessary updates
		layoutXProperty().removeListener(layoutXListener);
		layoutYProperty().removeListener(layoutYListener);
		super.relocate(x, y);
		layoutXProperty().addListener(layoutXListener);
		layoutYProperty().addListener(layoutYListener);

		relocateGeometryToMatchLayoutXY(x, y);
	}

	/**
	 * Relocates the {@link #geometryProperty() geometry}.
	 *
	 * @param x
	 *            The new x coordinate
	 * @param y
	 *            The new y coordinate
	 */
	@SuppressWarnings("unchecked")
	public void relocateGeometry(double x, double y) {
		T geometry = geometryProperty.getValue();
		Rectangle geometryBounds = geometry.getBounds();
		if (geometry instanceof ITranslatable) {
			geometryProperty.set(((ITranslatable<T>) geometry).getTranslated(
					x - geometryBounds.getX(), y - geometryBounds.getY()));
		} else {
			geometryProperty.set((T) geometry.getTransformed(
					new AffineTransform().translate((x - geometryBounds.getX()),
							(y - geometryBounds.getY()))));
		}
	}

	private void relocateGeometryToMatchLayoutXY(double layoutX,
			double layoutY) {
		// guard against null geometry
		T geometry = geometryProperty.get();
		if (geometry == null) {
			return;
		}

		// geometry has to reflect final position relative to layout bounds,
		// which are based on (0, 0)
		geometryProperty.removeListener(geometryChangeListener);
		relocateGeometry(layoutX + getStrokeOffset() + getInsets().getLeft(),
				layoutY + getStrokeOffset() + getInsets().getTop());
		geometryProperty.addListener(geometryChangeListener);
		updateShapes();
	}

	@Override
	public void resize(double width, double height) {
		if (width < minWidth(-1)) {
			throw new IllegalArgumentException(
					"Cannot resize below mininmal width " + minWidth(-1)
							+ ", so " + width + " is no valid width");
		}
		if (height < minHeight(-1)) {
			throw new IllegalArgumentException(
					"Cannot resize below mininmal height " + minHeight(-1)
							+ ", so " + height + " is no valid height");
		}

		// prevent unnecessary updates
		widthProperty().removeListener(widthListener);
		heightProperty().removeListener(heightListener);
		super.resize(width, height);
		widthProperty().addListener(widthListener);
		heightProperty().addListener(heightListener);

		resizeGeometryToMatchLayoutBoundsSize(width, height);
	}

	/**
	 * Resizes the {@link #geometryProperty()} to the given width and height.
	 *
	 * @param width
	 *            The new width.
	 * @param height
	 *            The new height.
	 */
	@SuppressWarnings("unchecked")
	public void resizeGeometry(double width, double height) {
		T geometry = geometryProperty.getValue();
		double geometryMinWidth = computeGeometryMinWidth(geometry);
		if (width < geometryMinWidth) {
			throw new IllegalArgumentException(
					"Cannot resize geometry below " + geometryMinWidth + ", so "
							+ width + " is no valid width.");
		}
		double geometryMinHeight = computeGeometryMinHeight(geometry);
		if (height < geometryMinHeight) {
			throw new IllegalArgumentException(
					"Cannot resize geometry below " + geometryMinHeight
							+ ", so " + height + " is no valid height.");
		}
		if (geometry instanceof Rectangle) {
			geometryProperty.set((T) ((Rectangle) geometry).getCopy()
					.setSize(width, height));
		} else if (geometry instanceof RoundedRectangle) {
			geometryProperty.set((T) ((RoundedRectangle) geometry).getCopy()
					.setSize(width, height));
		} else if (geometry instanceof Ellipse) {
			geometryProperty.set(
					(T) ((Ellipse) geometry).getCopy().setSize(width, height));
		} else if (geometry instanceof Pie) {
			geometryProperty
					.set((T) ((Pie) geometry).getCopy().setSize(width, height));
		} else if (geometry instanceof Arc) {
			geometryProperty
					.set((T) ((Arc) geometry).getCopy().setSize(width, height));
		} else {
			Rectangle geometricBounds = geometry.getBounds();
			double sx = geometricBounds.getWidth() == 0 ? 1
					: width / geometricBounds.getWidth();
			double sy = geometricBounds.getHeight() == 0 ? 1
					: height / geometricBounds.getHeight();
			if (geometry instanceof IScalable) {
				// Line, Polyline, PolyBezier, BezierCurve, CubicCurve,
				// QuadraticCurve, Polygon, CurvedPolygon, Region, and Ring are
				// not directly resizable but scalable
				geometryProperty.set(((IScalable<T>) geometry).getScaled(sx, sy,
						geometricBounds.getX(), geometricBounds.getY()));
			} else {
				// apply transform to path
				Point boundsOrigin = new Point(geometricBounds.getX(),
						geometricBounds.getY());
				geometryProperty.setValue((T) geometry
						.getTransformed(new AffineTransform(1, 0, 0, 1,
								-boundsOrigin.x, -boundsOrigin.y))
						.getTransformed(new AffineTransform(sx, 0, 0, sy, 0, 0))
						.getTransformed(new AffineTransform(1, 0, 0, 1,
								boundsOrigin.x, boundsOrigin.y)));
			}
		}
	}

	private void resizeGeometryToMatchLayoutBoundsSize(double layoutBoundsWidth,
			double layoutBoundsHeight) {

		// guard against null geometry
		// TODO: check if required
		T geometry = geometryProperty.get();
		if (geometry == null) {
			return;
		}

		// Disable listening to geometry changes while determine new geometry
		// size (to match given visual bounds size)
		geometryProperty.removeListener(geometryChangeListener);

		// System.out.println("Resizing to " + width + ", " + height);

		// the target width/height for the layout bounds (of the geometric
		// shape) is without the insets and stroke
		double strokeOffset = getStrokeOffset();
		double geometryWidth = layoutBoundsWidth - getInsets().getLeft()
				- getInsets().getRight() - 2 * strokeOffset;
		double geometryMinWidth = computeGeometryMinWidth(geometry);
		if (geometryWidth < geometryMinWidth) {
			geometryWidth = geometryMinWidth;
		}
		double geometryHeight = layoutBoundsHeight - getInsets().getTop()
				- getInsets().getBottom() - 2 * strokeOffset;
		double geometryMinHeight = computeGeometryMinHeight(geometry);
		if (geometryHeight < geometryMinHeight) {
			geometryHeight = geometryMinHeight;
		}
		// System.out.println(
		// "Resize Geometry to " + geometryWidth + ", " + geometryHeight);
		resizeGeometry(geometryWidth, geometryHeight);
		// System.out.println("... " + geometryProperty.get().getBounds());

		// update geometry of underlying path (which should invalidate the
		// layout bounds)
		geometryProperty.addListener(geometryChangeListener);
		updateShapes();
	}

	/**
	 * Sets the value of the property {@link #clickableAreaWidthProperty()
	 * clickable area width} property.
	 *
	 * @param clickableAreaWidth
	 *            The new value of the {@link #clickableAreaWidthProperty()
	 *            clickable area width} property.
	 */
	public void setClickableAreaWidth(double clickableAreaWidth) {
		this.clickableAreaWidth.set(clickableAreaWidth);
	}

	/**
	 * Sets the value of the fill property.
	 *
	 * @param value
	 *            The new value of the fill property.
	 *
	 * @see javafx.scene.shape.Shape#setFill(javafx.scene.paint.Paint)
	 */
	public final void setFill(Paint value) {
		geometricShape.setFill(value);
	}

	/**
	 * Sets the value of the fill rule property.
	 *
	 * @param value
	 *            The new value of the fill rule property.
	 *
	 * @see javafx.scene.shape.Path#setFillRule(javafx.scene.shape.FillRule)
	 */
	public final void setFillRule(FillRule value) {
		geometricShape.setFillRule(value);
	}

	/**
	 * Sets the geometric shape used by this {@link GeometryNode}.
	 *
	 * @param geometricShape
	 *            The geometric shape.
	 */
	protected void setGeometricShape(final Path geometricShape) {
		// add geometric shape
		getChildren().add(geometricShape);

		// Unfortunately those methods in Node that are responsible for handling
		// CSS style (getStyleClass(), getStyle()) are final, thus cannot be
		// delegated to the geometric shape. As Parent does not support CSS
		// styling itself, we can at least 'forward' them.
		getStyleClass().addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends String> c) {
				// delegate style classes to geometric shape
				while (c.next()) {
					if (c.wasPermutated() || c.wasUpdated()) {
						geometricShape.getStyleClass().clear();
						geometricShape.getStyleClass().addAll(getStyleClass());
					} else {
						geometricShape.getStyleClass()
								.removeAll(c.getRemoved());
						geometricShape.getStyleClass()
								.addAll(c.getAddedSubList());
					}
				}
			}
		});
		styleProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				geometricShape.setStyle(newValue);
			}
		});

		// ensure clickable area is added/removed as needed
		clickableAreaWidth.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (newValue != null
						&& newValue.doubleValue() > geometricShape
								.getStrokeWidth()
						&& clickableAreaShape == null
						&& geometryProperty.getValue() != null) {
					// create and configure clickable area shape
					clickableAreaShape = new Path(Geometry2Shape.toPathElements(
							geometryProperty.getValue().toPath()));
					clickableAreaShape
							.setId("clickable area of GeometryNode " + this);
					clickableAreaShape.setStroke(Color.TRANSPARENT);
					clickableAreaShape.setMouseTransparent(false);
					clickableAreaShape.strokeWidthProperty()
							.bind(clickableAreaWidthProperty());
					// add clickable area and binding only if its really used
					getChildren().add(clickableAreaShape);
				} else if ((newValue == null || newValue
						.doubleValue() <= geometricShape.getStrokeWidth())
						&& clickableAreaShape != null) {
					getChildren().remove(clickableAreaShape);
					clickableAreaShape.strokeWidthProperty().unbind();
					clickableAreaShape = null;
				}
			}
		});
	}

	/**
	 * Sets the {@link IGeometry} of this {@link GeometryNode} to the given
	 * value.
	 *
	 * @param geometry
	 *            The new {@link IGeometry} for this {@link GeometryNode}.
	 */
	public void setGeometry(T geometry) {
		this.geometryProperty.setValue(geometry);
	}

	/**
	 * Sets the value of the smooth property.
	 *
	 * @param value
	 *            The new value of the smooth property.
	 *
	 * @see javafx.scene.shape.Shape#setSmooth(boolean)
	 */
	public final void setSmooth(boolean value) {
		geometricShape.setSmooth(value);
	}

	/**
	 * * Sets the value of the stroke property.
	 *
	 * @param value
	 *            The new value of the stroke property.
	 *
	 * @see javafx.scene.shape.Shape#setStroke(javafx.scene.paint.Paint)
	 */
	public final void setStroke(Paint value) {
		geometricShape.setStroke(value);
	}

	/**
	 * Sets the value of the stroke dash offset property.
	 *
	 * @param value
	 *            The new value of the stroke dash offset property.
	 *
	 * @see javafx.scene.shape.Shape#setStrokeDashOffset(double)
	 */
	public final void setStrokeDashOffset(double value) {
		geometricShape.setStrokeDashOffset(value);
	}

	/**
	 * Sets the value of the stroke line cap property.
	 *
	 * @param value
	 *            The new value of the stroke line cap property.
	 *
	 * @see javafx.scene.shape.Shape#setStrokeLineCap(javafx.scene.shape.StrokeLineCap)
	 */
	public final void setStrokeLineCap(StrokeLineCap value) {
		geometricShape.setStrokeLineCap(value);
	}

	/**
	 * Sets the value of the stroke line join property.
	 *
	 * @param value
	 *            The new value of the stroke line join property.
	 *
	 * @see javafx.scene.shape.Shape#setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin)
	 */
	public final void setStrokeLineJoin(StrokeLineJoin value) {
		geometricShape.setStrokeLineJoin(value);
	}

	/**
	 * Sets the value of the stroke miter limit property.
	 *
	 * @param value
	 *            The new value of the stroke miter limit property.
	 *
	 * @see javafx.scene.shape.Shape#setStrokeMiterLimit(double)
	 */
	public final void setStrokeMiterLimit(double value) {
		geometricShape.setStrokeMiterLimit(value);
	}

	/**
	 * Sets the value of the stroke type property.
	 *
	 * @param value
	 *            The new value of the stroke type property.
	 *
	 * @see javafx.scene.shape.Shape#setStrokeType(javafx.scene.shape.StrokeType)
	 */
	public final void setStrokeType(StrokeType value) {
		geometricShape.setStrokeType(value);
	}

	/**
	 * Sets the value of the stroke width property.
	 *
	 * @param value
	 *            The new value of the stroke width property.
	 *
	 * @see javafx.scene.shape.Shape#setStrokeWidth(double)
	 */
	public final void setStrokeWidth(double value) {
		geometricShape.setStrokeWidth(value);
	}

	/**
	 * Provides a {@link Property} holding the smooth value to apply for this
	 * {@link GeometryNode}.
	 *
	 * @return A (writable) property for the smooth value of this node.
	 *
	 * @see javafx.scene.shape.Shape#smoothProperty()
	 */
	public final BooleanProperty smoothProperty() {
		return geometricShape.smoothProperty();
	}

	/**
	 * Provides a {@link Property} holding the stroke dash offset to apply for
	 * this {@link GeometryNode}.
	 *
	 * @return A (writable) property for the stroke dash offset of this node.
	 *
	 * @see javafx.scene.shape.Shape#strokeDashOffsetProperty()
	 */
	public final DoubleProperty strokeDashOffsetProperty() {
		return geometricShape.strokeDashOffsetProperty();
	}

	/**
	 * Provides a {@link Property} holding the stroke line cap to apply for this
	 * {@link GeometryNode}.
	 *
	 * @return A (writable) property for the stroke line cap of this node.
	 *
	 * @see javafx.scene.shape.Shape#strokeLineCapProperty()
	 */
	public final ObjectProperty<StrokeLineCap> strokeLineCapProperty() {
		return geometricShape.strokeLineCapProperty();
	}

	/**
	 * Provides a {@link Property} holding the stroke line join to apply for
	 * this {@link GeometryNode}.
	 *
	 * @return A (writable) property for the stroke line join of this node.
	 *
	 * @see javafx.scene.shape.Shape#strokeLineJoinProperty()
	 */
	public final ObjectProperty<StrokeLineJoin> strokeLineJoinProperty() {
		return geometricShape.strokeLineJoinProperty();
	}

	/**
	 * Provides a {@link Property} holding the stroke miter limit to apply for
	 * this {@link GeometryNode}.
	 *
	 * @return A (writable) property for the stroke miter limit of this node.
	 *
	 * @see javafx.scene.shape.Shape#strokeMiterLimitProperty()
	 */
	public final DoubleProperty strokeMiterLimitProperty() {
		return geometricShape.strokeMiterLimitProperty();
	}

	/**
	 * Defines parameters of a stroke that is drawn around the outline of a
	 * Shape using the settings of the specified Paint. The default value is
	 * Color.BLACK.
	 *
	 * @return A writable {@link Property} to control the stroke of this
	 *         {@link GeometryNode}.
	 *
	 * @see javafx.scene.shape.Shape#strokeProperty()
	 */
	public final ObjectProperty<Paint> strokeProperty() {
		return geometricShape.strokeProperty();
	}

	/**
	 * Provides a {@link Property} holding the stroke type to apply for this
	 * {@link GeometryNode}.
	 *
	 * @return A (writable) property for the stroke type of this node.
	 *
	 * @see javafx.scene.shape.Shape#strokeTypeProperty()
	 */
	public final ObjectProperty<StrokeType> strokeTypeProperty() {
		return geometricShape.strokeTypeProperty();
	}

	/**
	 * Provides a {@link Property} holding the stroke width to apply for this
	 * {@link GeometryNode}.
	 *
	 * @return A (writable) property for the stroke width of this node.
	 *
	 * @see javafx.scene.shape.Shape#strokeWidthProperty()
	 */
	public final DoubleProperty strokeWidthProperty() {
		return geometricShape.strokeWidthProperty();
	}

	/**
	 * Updates the visual representation (Path) of this GeometryNode. This is
	 * done automatically when setting the geometry. But in case you change
	 * properties of a geometry, you have to call this method in order to update
	 * its visual counter part.
	 */
	private void updateShapes() {
		if (clickableAreaShape != null) {
			updateShapes(geometricShape, clickableAreaShape);
		} else {
			updateShapes(geometricShape);
		}
	}

	private void updateShapes(Path... paths) {
		PathElement[] pathElements = getPathElements();
		for (Path p : paths) {
			p.getElements().setAll(pathElements);
			p.relocate((getWidth() - p.getLayoutBounds().getWidth()) / 2,
					(getHeight() - p.getLayoutBounds().getHeight()) / 2);
		}
	}
}