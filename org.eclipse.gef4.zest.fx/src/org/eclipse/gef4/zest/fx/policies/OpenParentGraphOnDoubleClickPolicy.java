package org.eclipse.gef4.zest.fx.policies;

import java.util.Collections;

import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.zest.fx.parts.GraphRootPart;

public class OpenParentGraphOnDoubleClickPolicy extends AbstractFXClickPolicy {

	@Override
	public void click(MouseEvent e) {
		if (e.getClickCount() == 2) {
			// double click
			ContentModel contentModel = getHost().getViewer().getAdapter(
					ContentModel.class);
			Graph graph = (Graph) contentModel.getContents().get(0);
			if (graph.getNestingNode() != null) {
				Graph parentGraph = graph.getNestingNode().getGraph();
				// cancel dragging
				FXClickDragTool tool = getHost().getRoot().getViewer()
						.getDomain().getAdapter(FXClickDragTool.class);
				tool.cancelDragging();
				// change contents
				contentModel
						.setContents(Collections.singletonList(parentGraph));
			}
		}
	}

	@Override
	public GraphRootPart getHost() {
		return (GraphRootPart) super.getHost();
	}

}
