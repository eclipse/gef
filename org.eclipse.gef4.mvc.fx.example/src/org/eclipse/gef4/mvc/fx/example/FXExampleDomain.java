package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.domain.FXEditDomain;
import org.eclipse.gef4.mvc.fx.tools.FXDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXRelocateSelectedTool;
import org.eclipse.gef4.mvc.fx.tools.FXSelectionTool;
import org.eclipse.gef4.mvc.tools.CompositeAndTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXExampleDomain extends FXEditDomain {

	@Override
	protected ITool<Node> getDefaultTool() {
		CompositeAndTool<Node> baseTool = new CompositeAndTool<Node>();
		baseTool.add(new FXSelectionTool()); // TODO use drag tool
		// baseTool.add(new BoxSelectionHandleTool<Node>()); // TODO: policy in
		// // root visual
		baseTool.add(new FXRelocateSelectedTool()); // TODO use drag tool
		// baseTool.add(new FXResizeRelocateViaHandleTool()); // TODO implement
		// via
		// // policy in handles
		baseTool.add(new FXDragTool());
		baseTool.add(new FXHoverTool());
		return baseTool;
	}

}
