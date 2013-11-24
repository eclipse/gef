package org.eclipse.gef4.mvc.javafx.example;

import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IEdgeContentPart;
import org.eclipse.gef4.mvc.parts.INodeContentPart;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;

public class FXExampleNodePartFactory implements IContentPartFactory<Node> {

	@Override
	public INodeContentPart<Node> createNodeContentPart(IContentPart<Node> parent,
			Object model) {
		if (model instanceof Rectangle2D) {
			return new FXExampleNodeEditPart();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public IEdgeContentPart<Node> createEdgeContentPart(
			IContentPart<Node> parent, Object model) {
		return null;
	}

	@Override
	public IContentPart<Node> createRootContentPart(IRootVisualPart<Node> root,
			Object model) {
		if (model instanceof List) {
			return new FXExampleContainerNodeEditPart();
		} else {
			throw new IllegalArgumentException();
		}
	}

}
