package org.eclipse.gef4.mvc.javafx;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXEditDomain extends AbstractEditDomain<Node> {

	@Override
	protected List<ITool<Node>> getDefaultTools() {
		List<ITool<Node>> tools = new ArrayList<ITool<Node>>();
		tools.add(new FXSelectTool());
		tools.add(new FXDragTool());
		return tools;
	}

}
