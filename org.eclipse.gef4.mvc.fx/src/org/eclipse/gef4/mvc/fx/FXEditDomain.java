package org.eclipse.gef4.mvc.fx;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.tools.CompositeAndTool;
import org.eclipse.gef4.mvc.tools.HandleTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXEditDomain extends AbstractEditDomain<Node> {

	@Override
	protected ITool<Node> getDefaultTool() {
		FXEventTargetCompositeXorTool defaultTool = new FXEventTargetCompositeXorTool();
		defaultTool.addContentTools(new FXSelectionTool(), new HandleTool<Node>(), new FXRelocateTool());
		defaultTool.addHandleTools(new FXResizeTool());
		defaultTool.addVisualTools(new FXSelectionTool(), new HandleTool<Node>());
		return defaultTool;
	}

}
