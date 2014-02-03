package org.eclipse.gef4.mvc.fx.domain;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractDomain;
import org.eclipse.gef4.mvc.fx.tools.FXDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXSelectionTool;
import org.eclipse.gef4.mvc.tools.CompositeAndTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXDomain extends AbstractDomain<Node> {

	@Override
	protected ITool<Node> getDefaultTool() {
		CompositeAndTool<Node> baseTool = new CompositeAndTool<Node>();
		baseTool.add(new FXSelectionTool()); // TODO use drag tool
		baseTool.add(new FXHoverTool());
		baseTool.add(new FXDragTool());
		return baseTool;
	}

}
