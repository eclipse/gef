package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.domain.FXEditDomain;
import org.eclipse.gef4.mvc.fx.tools.FXEventTargetCompositeXorTool;
import org.eclipse.gef4.mvc.fx.tools.FXRelocateTool;
import org.eclipse.gef4.mvc.fx.tools.FXResizeTool;
import org.eclipse.gef4.mvc.fx.tools.FXSelectionTool;
import org.eclipse.gef4.mvc.tools.HandleTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class ExampleDomain extends FXEditDomain {

	@Override
	protected ITool<Node> getDefaultTool() {
		FXEventTargetCompositeXorTool defaultTool = new FXEventTargetCompositeXorTool();
		defaultTool.addContentTools(new FXSelectionTool(),
				new HandleTool<Node>(), new FXRelocateTool());
		SelectionXorTool handleXor = new SelectionXorTool();
		handleXor.bindToolToType(FXExampleShapePart.class, new FXResizeTool());
		handleXor.bindToolToType(FXExampleCurvePart.class, new FXBendTool());
		defaultTool.addHandleTools(handleXor);
		defaultTool.addVisualTools(new FXSelectionTool(),
				new HandleTool<Node>());
		return defaultTool;
	}
	
}
