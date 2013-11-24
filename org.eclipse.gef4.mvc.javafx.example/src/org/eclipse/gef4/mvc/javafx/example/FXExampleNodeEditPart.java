package org.eclipse.gef4.mvc.javafx.example;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.mvc.aspects.relocate.AbstractRelocatePolicy;
import org.eclipse.gef4.mvc.aspects.resize.AbstractResizePolicy;
import org.eclipse.gef4.mvc.aspects.selection.AbstractSelectionPolicy;
import org.eclipse.gef4.mvc.javafx.AbstractFXNodeContentPart;
import org.eclipse.gef4.mvc.javafx.FXRelocatePolicy;
import org.eclipse.gef4.mvc.javafx.FXResizePolicy;
import org.eclipse.gef4.mvc.javafx.FXSelectionPolicy;

public class FXExampleNodeEditPart extends AbstractFXNodeContentPart {

	private Rectangle visual;

	public FXExampleNodeEditPart() {
		visual =  new Rectangle();
		visual.setFill(Color.RED);
		installEditPolicy(AbstractSelectionPolicy.class, new FXSelectionPolicy());
		installEditPolicy(AbstractRelocatePolicy.class, new FXRelocatePolicy());
		installEditPolicy(AbstractResizePolicy.class, new FXResizePolicy());
	}
	
	@Override
	public Rectangle2D getModel() {
		return (Rectangle2D)super.getModel();
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		visual.setLayoutX(getModel().getMinX());
		visual.setLayoutY(getModel().getMinY());
		visual.setWidth(getModel().getWidth());
		visual.setHeight(getModel().getHeight());
	}

	@Override
	protected boolean isModelObject(Object model) {
		return true;
	}

	@Override
	protected boolean isModelLink(Object model) {
		return false;
	}

}
