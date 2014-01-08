package org.eclipse.gef4.mvc.fx;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.tools.HandleTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXEditDomain extends AbstractEditDomain<Node> {

	@Override
	protected ITool<Node> getDefaultTool() {
		FXEventTargetCompositeXorTool defaultTool = new FXEventTargetCompositeXorTool();
		// TODO: handle tools seems to be odd here, as it does not depend on
		// mouse events, but on property change events (selection model inside
		// viewer)
		// we should fix that we also listen to these changes and have
		// respective tools for it
		// TODO: also key events have to be processed...
		// tools may react to all kinds of events as well
		// TODO: may these tools remain active until a next event determines another tool,
		// e.g. the handle tool does not have to pop itself...
		defaultTool.addContentTools(new FXSelectionTool(),
				new HandleTool<Node>(), new FXRelocateTool());
		defaultTool.addHandleTools(new FXResizeTool());
		defaultTool.addVisualTools(new FXSelectionTool(),
				new HandleTool<Node>());
		return defaultTool;
	}

}
