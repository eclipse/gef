package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Arrays;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXBendHandlePart extends AbstractFXHandlePart {
	
//	private double parameter = 0.5;
	private int anchorIndex = 0;
	private Rectangle visual;

	public FXBendHandlePart(IContentPart<Node> contentPart) {
		setTargetContentParts(Arrays.asList(contentPart));
		visual = new Rectangle(5, 5);
		visual.setTranslateY(- visual.getHeight() / 2);
		visual.setFill(new LinearGradient(0, 0, 0, 5, true,
				CycleMethod.NO_CYCLE, new Stop[] {
						new Stop(0.0, Color.web("#e4fbff")),
						new Stop(0.5, Color.web("#a5d3fb")),
						new Stop(1.0, Color.web("#d5faff")) }));
		visual.setStroke(Color.web("#5a61af"));
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		IContentPart<Node> part = getTargetContentParts().get(0);
		Bounds bounds = part.getVisual().getLayoutBounds();
//		FXExampleCurvePart cp = (FXExampleCurvePart) part;
//		List<Point> anchorPoints = cp.getAnchorPoints();
//		Point point = anchorPoints.get(anchorIndex);
		visual.setLayoutX(0.5 * (bounds.getMinX() + bounds.getMaxX()));
		visual.setLayoutY(0.5 * (bounds.getMinY() + bounds.getMaxY()));
	}

	public int getAnchorIndex() {
		return anchorIndex;
	}

}
