package org.eclipse.gef4.mvc.fx;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.tools.CompositeAndTool;
import org.eclipse.gef4.mvc.tools.HandleTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXEditDomain extends AbstractEditDomain<Node> {

	@Override
	protected ITool<Node> getDefaultTool() {
		CompositeAndTool<Node> defaultTool = new CompositeAndTool<>();
		defaultTool.add(new FXSelectionTool());
		defaultTool.add(new FXRelocateTool());
		defaultTool.add(new HandleTool<Node>());
		return defaultTool;
	}

}
