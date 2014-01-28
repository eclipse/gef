package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

public class FXGeometricCurveElement extends AbstractFXGeometricVisualElement<ICurve> {

	public FXGeometricCurveElement(ICurve curve) {
		super(curve);
	}

}
