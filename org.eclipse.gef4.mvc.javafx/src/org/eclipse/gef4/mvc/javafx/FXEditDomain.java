package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.tools.CompositeTool;
import org.eclipse.gef4.mvc.tools.HandleTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXEditDomain extends AbstractEditDomain<Node> {

	@Override
	protected ITool<Node> getDefaultTool() {
		CompositeTool<Node> defaultTool = new CompositeTool<>();
		defaultTool.add(new FXSelectionTool());
		defaultTool.add(new FXRelocateTool());
		defaultTool.add(new HandleTool<Node>());
		return defaultTool;
	}

}
