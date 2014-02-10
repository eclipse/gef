package org.eclipse.gef4.mvc.fx.example.model;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;

public class FXGeometricShape extends AbstractFXGeometricElement<IShape> {

	public Color fill;
	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color fill, Effect effect) {
		super(shape, transform);
		this.fill = fill;
		this.effect = effect;
	}

}
