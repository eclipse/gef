package org.eclipse.gef4.mvc.fx.example.model;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;

public class FXGeometricShape extends AbstractFXGeometricElement<IShape> {

	public Color fill;

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color fill, Effect effect) {
		this(shape, transform, new Color(0, 0, 0, 1), 1.0, fill, effect);
	}

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color stroke, double strokeWidth, Color fill, Effect effect) {
		super(shape, transform, stroke, strokeWidth, effect);
		this.fill = fill;
	}

}
