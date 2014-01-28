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

public class FXGeometricShapeElement extends
		AbstractFXGeometricVisualElement<IShape> {

	// TODO: move to superclass
	public List<AbstractFXGeometricVisualElement<? extends IGeometry>> anchored = new ArrayList<AbstractFXGeometricVisualElement<? extends IGeometry>>();

	public Dimension offset;
	public Color fill;
	public Effect effect;

	@SafeVarargs
	public FXGeometricShapeElement(IShape shape, AffineTransform transform, Color fill,
			Effect effect, AbstractFXGeometricVisualElement<? extends IGeometry>... anchords) {
		super(shape, transform);
		this.fill = fill;
		this.effect = effect;
		this.anchored.addAll(Arrays.asList(anchords));
	}

}
