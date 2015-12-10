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

import org.eclipse.gef4.fx.utils.Geometry2JavaFX;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IScalable;
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
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
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
 * Technically, a {@link GeometryNode} is a {@link Parent} that internally holds
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
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <T>
 *            An {@link IGeometry} used to define this {@link GeometryNode}
 */
public class GeometryNode<T extends IGeometry> extends Parent {

	private Path geometricShape = new Path();
	private Path layoutBoundsComputationShape = new Path();
	private Path clickableAreaShape = null;
	private DoubleProperty clickableAreaWidth = new SimpleDoubleProperty();
	private ObjectProperty<T> geometryProperty = new SimpleObjectProperty<>();

	private double resizeWidth = Double.NaN;
	private double resizeHeight = Double.NaN;

	private ChangeListener<T> geometryChangeListener = new ChangeListener<T>() {
		@Override
		public void changed(ObservableValue<? extends T> observable, T oldValue,
				T newValue) {
			resizeWidth = Double.NaN;
			resizeHeight = Double.NaN;
			updateVisuals();
		}
	};

	/**
	 * Constructs a new {@link GeometryNode} without an {@link IGeometry}.
	 */
	public GeometryNode() {
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
						&& clickableAreaShape == null) {
					// create and configure clickable area shape
					clickableAreaShape = new Path(
							Geometry2JavaFX.toPathElements(
									geometryProperty.getValue().toPath()));
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

		// XXX: We cannot directly resize the underlying geometricShape so that
		// its layout bounds reflect a specific width and height. Instead, we
		// have to iterate until the desired layout bounds are obtained. As this
		// would otherwise cause updates, we use a specific shape just for these
		// calculations.
		layoutBoundsComputationShape.strokeProperty()
				.bind(geometricShape.strokeProperty());
		layoutBoundsComputationShape.strokeLineCapProperty()
				.bind(geometricShape.strokeLineCapProperty());
		layoutBoundsComputationShape.strokeLineJoinProperty()
				.bind(geometricShape.strokeLineJoinProperty());
		layoutBoundsComputationShape.strokeMiterLimitProperty()
				.bind(geometricShape.strokeMiterLimitProperty());
		layoutBoundsComputationShape.strokeTypeProperty()
				.bind(geometricShape.strokeTypeProperty());
		layoutBoundsComputationShape.strokeWidthProperty()
				.bind(geometricShape.strokeWidthProperty());
		layoutBoundsComputationShape.fillProperty()
				.bind(geometricShape.fillProperty());
		layoutBoundsComputationShape.smoothProperty()
				.bind(geometricShape.smoothProperty());
		layoutBoundsComputationShape.fillRuleProperty()
				.bind(geometricShape.fillRuleProperty());
				// TODO: bind dash and CSS styling!!

		// update path elements whenever the geometry property is changed
		geometryProperty.addListener(geometryChangeListener);
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
	 * Retrieves the value of the geometry property.
	 *
	 * @return The value of the geometry property.
	 */
	public T getGeometry() {
		return geometryProperty.getValue();
	}

	private PathElement[] getPathElements() {
		return Geometry2JavaFX
				.toPathElements(geometryProperty.getValue().toPath());
	}

	/**
	 * Returns the {@link Shape} that is used as a delegate to render the
	 * geometry of this {@link GeometryNode}.
	 *
	 * @return The geometric shape used by this {@link GeometryNode}.
	 */
	protected Shape getShape() {
		return geometricShape;
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
	protected Bounds impl_computeLayoutBounds() {
		/*
		 * We have to ensure, that the size that gets passed in to #resize() is
		 * reflected in the layout bounds of this node. As we cannot compensate
		 * the offset between geometric bounds and layout bounds in all cases,
		 * we fall back to the values given in resize() in case they differ from
		 * the computed bounds.
		 *
		 * FIXME: Re-implement this fix by only using public API (bug #443954)
		 */
		Bounds layoutBounds = super.impl_computeLayoutBounds();
		double width = Double.isNaN(resizeWidth) ? layoutBounds.getWidth()
				: resizeWidth;
		double height = Double.isNaN(resizeHeight) ? layoutBounds.getHeight()
				: resizeHeight;
		return org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX
				.toFXBounds(new Rectangle(layoutBounds.getMinX(),
						layoutBounds.getMinY(), width, height));
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
	public double maxHeight(double width) {
		return prefHeight(width);
	}

	@Override
	public double maxWidth(double height) {
		return prefWidth(height);
	}

	@Override
	public double minHeight(double width) {
		return prefHeight(width);
	}

	@Override
	public double minWidth(double height) {
		return prefWidth(height);
	}

	@Override
	public double prefHeight(double width) {
		final double result = getLayoutBounds().getHeight();
		return Double.isNaN(result) || result < 0 ? 0 : result;
	}

	@Override
	public double prefWidth(double height) {
		final double result = getLayoutBounds().getWidth();
		return Double.isNaN(result) || result < 0 ? 0 : result;
	}

	@Override
	public void resize(double width, double height) {
		if (width < 0) {
			throw new IllegalArgumentException("Cannot resize: width < 0.");
		}
		if (height < 0) {
			throw new IllegalArgumentException("Cannot resize: height < 0.");
		}

		// prevent unnecessary updates
		Bounds layoutBounds = getLayoutBounds();
		if (layoutBounds.getWidth() == width
				&& layoutBounds.getHeight() == height) {
			// do nothing if layout bounds already match desired width and
			// height
			return;
		}

		// Disable listening to geometry changes while determine new geometry
		// size (to match given visual bounds size)
		geometryProperty.removeListener(geometryChangeListener);

		// determine initial values for geometric size that are as close as
		// possible to what we need for reaching the desired layout bounds.
		double offset = 0;
		if (geometricShape.getStroke() != null
				&& geometricShape.getStrokeType() != StrokeType.INSIDE) {
			// Path reserves 0.5 pixels around, so we substract 1 at the end
			// (Rectangle does not do this)
			offset = -(geometricShape.getStrokeType() == StrokeType.CENTERED ? 1
					: 2) * geometricShape.getStrokeWidth() - 1;
		}
		double geometryWidth = width + offset;
		double geometryHeight = height + offset;
		if (geometryWidth < 0) {
			geometryWidth = 0.00001;
		}
		if (geometryHeight < 0) {
			geometryHeight = 0.00001;
		}
		resizeGeometry(geometryWidth, geometryHeight);
		updateVisuals(layoutBoundsComputationShape);
		layoutBounds = layoutBoundsComputationShape.getLayoutBounds();
		double dw = layoutBounds.getWidth() - width;
		double dh = layoutBounds.getHeight() - height;
		double previousDw = Double.MAX_VALUE;
		double previousDh = Double.MAX_VALUE;
		// System.out
		// .println("Layout bounds difference: (" + dw + ", " + dh + ")");
		while (dw != 0 || dh != 0) {
			if (Math.abs(previousDw) > Math.abs(dw) && Math.abs(dw) > 0.00001) {
				geometryWidth -= dw;
				if (geometryWidth < 0) {
					geometryWidth = 0;
				}
			} else {
				dw = 0;
			}
			if (Math.abs(previousDh) > Math.abs(dh) && Math.abs(dh) > 0.00001) {
				geometryHeight -= dh;
				if (geometryHeight < 0) {
					geometryHeight = 0;
				}
			} else {
				dh = 0;
			}

			resizeGeometry(geometryWidth, geometryHeight);
			updateVisuals(layoutBoundsComputationShape);
			layoutBounds = layoutBoundsComputationShape.getLayoutBounds();

			previousDw = dw;
			if (dw != 0) {
				dw = layoutBounds.getWidth() - width;
			}
			previousDh = dh;
			if (dh != 0) {
				dh = layoutBounds.getHeight() - height;
			}
			// System.out.println(
			// "Layout bounds difference: (" + dw + ", " + dh + ")");
		}

		// cache precise values, so they can be used as layout bounds within
		// impl_computeLayoutBounds(); we will try to compute the geometry
		// as close as possible, but we might not be able to resize it so
		// that the computed layout bounds do precisely match the specified
		// width and height; to not violate the resize contract (that the
		// layout bounds match the specified width and height), we need the
		// precise values.
		if (layoutBounds.getWidth() != width) {
			this.resizeWidth = width;
			// System.err.println(
			// "*** WARNING: Computed layout bounds width of GeometryNode "
			// + this + "(" + layoutBounds.getWidth()
			// + ") do not exactly match specified resize width ("
			// + width
			// + "). Layout bounds will use the specified resize width.");
		} else {
			resizeWidth = Double.NaN;
		}
		if (layoutBounds.getHeight() != height) {
			this.resizeHeight = height;
			// System.err.println(
			// "*** WARNING: Computed layout bounds height of GeometryNode "
			// + this + "(" + layoutBounds.getHeight()
			// + ") do not exactly match specified resize height ("
			// + height
			// + "). Layout bounds will use the specified resize height.");
		} else {
			resizeHeight = Double.NaN;
		}

		// update geometry of underlying path (which should invalidate the
		// layout bounds)
		geometryProperty.addListener(geometryChangeListener);
		updateVisuals(); // will trigger re-computation of layout bounds
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
	protected void resizeGeometry(double width, double height) {
		T geometry = geometryProperty.getValue();
		if (geometry instanceof Rectangle) {
			((Rectangle) geometry).setSize(width, height);
		} else if (geometry instanceof RoundedRectangle) {
			((RoundedRectangle) geometry).setSize(width, height);
		} else if (geometry instanceof Ellipse) {
			((Ellipse) geometry).setSize(width, height);
		} else if (geometry instanceof Pie) {
			((Pie) geometry).setSize(width, height);
		} else if (geometry instanceof Arc) {
			((Arc) geometry).setSize(width, height);
		} else {
			Rectangle geometricBounds = geometry.getBounds();
			double sx = width / geometricBounds.getWidth();
			double sy = height / geometricBounds.getHeight();
			if (geometry instanceof IScalable) {
				// Line, Polyline, PolyBezier, BezierCurve, CubicCurve,
				// QuadraticCurve, Polygon, CurvedPolygon, Region, and Ring are
				// not directly resizable but scalable
				((IScalable<T>) geometry).scale(sx, sy, geometricBounds.getX(),
						geometricBounds.getY());
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
	// TODO: this needs to get called whenever the computed layout bounds change
	private void updateVisuals() {
		if (clickableAreaShape != null) {
			updateVisuals(geometricShape, clickableAreaShape);
		} else {
			updateVisuals(geometricShape);
		}
	}

	private void updateVisuals(Path... paths) {
		PathElement[] pathElements = getPathElements();
		for (Path p : paths) {
			p.getElements().setAll(pathElements);
		}
	}

}