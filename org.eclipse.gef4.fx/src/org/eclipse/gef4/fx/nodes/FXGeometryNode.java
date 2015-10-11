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

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IScalable;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.shape.Path;

/**
 * A {@link Path} that can be constructed using an underlying {@link IGeometry}.
 * In contrast to a normal {@link Path}, a {@link FXGeometryNode} is resizable,
 * performing a scale in case the underlying {@link IGeometry} is not directly
 * resizable.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <T>
 *            An {@link IGeometry} used to define this {@link FXGeometryNode}
 */
public class FXGeometryNode<T extends IGeometry> extends Path {

	private ObjectProperty<T> geometryProperty = new SimpleObjectProperty<T>();

	/**
	 * Constructs a new {@link FXGeometryNode} without an {@link IGeometry}.
	 */
	public FXGeometryNode() {
		// update path elements whenever the geometry property is changed
		geometryProperty.addListener(new ChangeListener<T>() {

			@Override
			public void changed(ObservableValue<? extends T> observable,
					T oldValue, T newValue) {
				updatePathElements();
			}
		});
	}

	/**
	 * Constructs a new {@link FXGeometryNode} which displays the given
	 * {@link IGeometry}.
	 *
	 * @param geom
	 *            The {@link IGeometry} to display.
	 */
	public FXGeometryNode(T geom) {
		this();
		setGeometry(geom);
	}

	/**
	 * Provides a {@link Property} holding the geometry of this
	 * {@link FXGeometryNode}.
	 *
	 * @return A (writable) property for the geomtry of this node.
	 */
	public ObjectProperty<T> geometryProperty() {
		return geometryProperty;
	}

	/**
	 * Returns the {@link IGeometry} of this {@link FXGeometryNode}.
	 *
	 * @return The {@link IGeometry} of this {@link FXGeometryNode}.
	 */
	public T getGeometry() {
		return geometryProperty.getValue();
	}

	@Override
	protected Bounds impl_computeLayoutBounds() {
		/*
		 * We have to ensure, that the size that gets passed in to #resize() is
		 * reflected in the layout bounds of this node. As we cannot compensate
		 * the offset between geometric bounds and layout bounds, we set the
		 * geometric bounds to the resize width and height and tweak the layout
		 * bounds here to match the geometric bounds.
		 *
		 * TODO: Re-implement this fix by only using public API, for example, a
		 * Group can be used as the super class. (Bug #443954)
		 */
		return Geometry2JavaFX.toFXBounds(geometryProperty.getValue() == null
				? new Rectangle() : geometryProperty.getValue().getBounds());
	}

	@Override
	public boolean isResizable() {
		return true;
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
		return geometryProperty.getValue().getBounds().getHeight();
	}

	@Override
	public double prefWidth(double height) {
		return geometryProperty.getValue().getBounds().getWidth();
	}

	@SuppressWarnings("unchecked")
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
			return;
		}

		// set the new size, either by resizing or scaling the underlying
		// geometry
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
		updatePathElements();
	}

	/**
	 * Sets the {@link IGeometry} of this {@link FXGeometryNode} to the given
	 * value.
	 *
	 * @param geometry
	 *            The new {@link IGeometry} for this {@link FXGeometryNode}.
	 */
	public void setGeometry(T geometry) {
		this.geometryProperty.setValue(geometry);
	}

	/**
	 * Updates the visual representation (Path) of this GeometryNode. This is
	 * done automatically when setting the geometry. But in case you change
	 * properties of a geometry, you have to call this method in order to update
	 * its visual counter part.
	 */
	private void updatePathElements() {
		getElements().setAll(Geometry2JavaFX
				.toPathElements(geometryProperty.getValue().toPath()));
	}
}
