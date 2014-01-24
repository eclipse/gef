package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXBoxHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

public class FXExampleHandlePartFactory implements IHandlePartFactory<Node> {

	@Override
	public List<IHandlePart<Node>> createHandleParts(
			List<IContentPart<Node>> selection) {
		if (selection.isEmpty()) 
			return Collections.emptyList();
		
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();
		
		IContentPart<Node> contentPart = selection.get(0);
		if (contentPart instanceof FXExampleCurvePart) {
			// generate handle parts for all beziers
			FXExampleCurvePart cp = (FXExampleCurvePart) contentPart;
			List<Point> anchorPoints = cp.getAnchorPoints();
			anchorPoints.clear();
			BezierCurve[] beziers = cp.getModel().toBezier();
			int p = 0;
			for (int i = 0; i < beziers.length; i++) {
				anchorPoints.add(beziers[i].get(0.5));
				handleParts.add(new FXBendHandlePart(contentPart, p++));
				if (i != beziers.length - 1) {
					anchorPoints.add(beziers[i].getP2());
					handleParts.add(new FXBendHandlePart(contentPart, p++));
				}
			}
		} else {
			handleParts.add(new FXBoxHandlePart(selection, Pos.TOP_LEFT));
			handleParts.add(new FXBoxHandlePart(selection, Pos.TOP_RIGHT));
			handleParts.add(new FXBoxHandlePart(selection, Pos.BOTTOM_RIGHT));
			handleParts.add(new FXBoxHandlePart(selection, Pos.BOTTOM_LEFT));
		}
		
		return handleParts;
	}

}
