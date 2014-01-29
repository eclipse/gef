package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.domain.FXEditDomain;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleCurvePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleShapePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXWayPointHandlePart;
import org.eclipse.gef4.mvc.fx.example.tools.FXHandleDragTool;
import org.eclipse.gef4.mvc.fx.example.tools.MouseHoverXorTool;
import org.eclipse.gef4.mvc.fx.parts.FXBoxHandlePart;
import org.eclipse.gef4.mvc.fx.tools.FXEventTargetCompositeXorTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXRelocateTool;
import org.eclipse.gef4.mvc.fx.tools.FXResizeTool;
import org.eclipse.gef4.mvc.fx.tools.FXSelectionTool;
import org.eclipse.gef4.mvc.tools.BoxSelectionHandleTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXExampleDomain extends FXEditDomain {

	@Override
	protected ITool<Node> getDefaultTool() {
		// CompositeAndTool<Node> baseTool = new CompositeAndTool<Node>();
		// baseTool.add(new FXSelectionTool());
		// baseTool.add(new BoxSelectionHandleTool<Node>());
		// baseTool.add(new FXRelocateTool());
		//
		// SelectionXorTool handleXor = new SelectionXorTool();
		// handleXor.bindToolToType(FXExampleShapePart.class, new
		// FXResizeTool());
		// handleXor.bindToolToType(FXExampleCurvePart.class, new FXBendTool());
		//
		// baseTool.add(handleXor);
		//
		FXEventTargetCompositeXorTool defaultTool = new FXEventTargetCompositeXorTool();
		BoxSelectionHandleTool<Node> boxHandleTool = new BoxSelectionHandleTool<Node>();
		FXSelectionTool selectionTool = new FXSelectionTool();
		FXHoverTool hoverTool = new FXHoverTool();
		defaultTool.addContentTools(selectionTool, hoverTool, boxHandleTool,
				new FXRelocateTool());
		MouseHoverXorTool handleXor = new MouseHoverXorTool();
		handleXor.bindToolToType(FXBoxHandlePart.class, new FXResizeTool());
		handleXor.bindToolToType(FXWayPointHandlePart.class,
				new FXHandleDragTool());
		defaultTool.addHandleTools(handleXor);
		defaultTool.addVisualTools(selectionTool, hoverTool, boxHandleTool);
		return defaultTool;
	}

}
