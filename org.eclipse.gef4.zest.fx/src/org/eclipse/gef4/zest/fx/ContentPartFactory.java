package org.eclipse.gef4.zest.fx;

import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;

public class ContentPartFactory implements IContentPartFactory<Node> {

	@Override
	public IContentPart<Node> createContentPart(Object content,
			IBehavior contextBehavior, Map contextMap) {
		if (content instanceof Graph) {
			return new GraphContentPart((Graph) content);
		} else if (content instanceof org.eclipse.gef4.graph.Node) {
			return new NodeContentPart((org.eclipse.gef4.graph.Node) content);
		} else if (content instanceof Edge) {
			return new EdgeContentPart((Edge) content);
		}
		return null;
	}
}
