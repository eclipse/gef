package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.FXHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

public class FXExampleHandlePartFactory implements IHandlePartFactory<Node> {

	@Override
	public List<IHandlePart<Node>> createHandleParts(
			List<IContentPart<Node>> selection) {
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();
		handleParts.add(new FXHandlePart(selection, Pos.TOP_LEFT));
		handleParts.add(new FXHandlePart(selection, Pos.TOP_RIGHT));
		handleParts.add(new FXHandlePart(selection, Pos.BOTTOM_RIGHT));
		handleParts.add(new FXHandlePart(selection, Pos.BOTTOM_LEFT));
		return handleParts;
	}

}
