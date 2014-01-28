package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.example.parts.FXExampleCurvePart;
import org.eclipse.gef4.mvc.fx.parts.FXBoxHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

public class FXExampleHandlePartFactory implements IHandlePartFactory<Node> {

	@Override
	public List<IHandlePart<Node>> createSelectionHandleParts(
			List<IContentPart<Node>> selection) {
		if (selection.isEmpty()) 
			return Collections.emptyList();
		
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();
		
		IContentPart<Node> contentPart = selection.get(0);
		if (contentPart instanceof FXExampleCurvePart) {
		} else {
			handleParts.add(new FXBoxHandlePart(selection, Pos.TOP_LEFT));
			handleParts.add(new FXBoxHandlePart(selection, Pos.TOP_RIGHT));
			handleParts.add(new FXBoxHandlePart(selection, Pos.BOTTOM_RIGHT));
			handleParts.add(new FXBoxHandlePart(selection, Pos.BOTTOM_LEFT));
		}
		
		return handleParts;
	}
	
	@Override
	public List<IHandlePart<Node>> createFocusHandleParts(
			IContentPart<Node> focused) {
		return Collections.emptyList();
	}
	
	@Override
	public List<IHandlePart<Node>> createHoverHandleParts(
			IContentPart<Node> hovered) {
		return Collections.emptyList();
	}

}
