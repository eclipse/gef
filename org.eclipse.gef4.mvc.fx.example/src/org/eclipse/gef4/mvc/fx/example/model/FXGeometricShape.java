package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;

public class FXGeometricShape extends
		AbstractFXGeometricElement<IShape> {

	// TODO: move to superclass
	public List<AbstractFXGeometricElement<? extends IGeometry>> anchored = new ArrayList<AbstractFXGeometricElement<? extends IGeometry>>();

	public Dimension offset;
	public Color fill;
	public Effect effect;

	@SafeVarargs
	public FXGeometricShape(IShape shape, AffineTransform transform, Color fill,
			Effect effect, AbstractFXGeometricElement<? extends IGeometry>... anchords) {
		super(shape, transform);
		this.fill = fill;
		this.effect = effect;
		this.anchored.addAll(Arrays.asList(anchords));
	}

}
