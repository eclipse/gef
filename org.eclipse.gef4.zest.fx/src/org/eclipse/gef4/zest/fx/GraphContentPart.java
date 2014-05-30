package org.eclipse.gef4.zest.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;

public class GraphContentPart extends AbstractFXContentPart {

	private Group group = new Group();

	{
		group.setAutoSizeChildren(false);
	}

	public GraphContentPart(Graph content) {
	}

	@Override
	public List<Object> getContentChildren() {
		List<Object> children = new ArrayList<Object>();
		children.addAll(((Graph) getContent()).getNodes());
		children.addAll(((Graph) getContent()).getEdges());
		return children;
	}

	@Override
	public Node getVisual() {
		return group;
	}

	@Override
	public void refreshVisual() {
		// nothing to do
	}

}
