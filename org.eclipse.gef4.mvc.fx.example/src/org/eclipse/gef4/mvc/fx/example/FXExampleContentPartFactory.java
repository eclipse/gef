package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleCurvePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleModelPart;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleShapePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;

public class FXExampleContentPartFactory implements IContentPartFactory<Node> {

	@Override
	public IContentPart<Node> createRootContentPart(IRootVisualPart<Node> root,
			Object model) {
		if (model instanceof FXGeometricModel) {
			return new FXExampleModelPart();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public IContentPart<Node> createChildContentPart(IContentPart<Node> parent,
			Object model) {
		if (model instanceof FXGeometricShape) {
			return new FXExampleShapePart();
		} else if (model instanceof FXGeometricCurve) {
			return new FXExampleCurvePart();
		} else {
			throw new IllegalArgumentException(model.getClass().toString());
		}
	}

}
