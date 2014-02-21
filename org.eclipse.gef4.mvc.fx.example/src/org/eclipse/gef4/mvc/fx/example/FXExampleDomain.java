/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.tools.FXDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXPinchTool;
import org.eclipse.gef4.mvc.fx.tools.FXSelectionTool;
import org.eclipse.gef4.mvc.tools.CompositeAndTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXExampleDomain extends FXDomain {

	@Override
	protected ITool<Node> getDefaultTool() {
		CompositeAndTool<Node> baseTool = new CompositeAndTool<Node>();
		baseTool.add(new FXSelectionTool()); // TODO use drag tool
		baseTool.add(new FXDragTool());
		baseTool.add(new FXHoverTool());
		baseTool.add(new FXPinchTool());
		return baseTool;
	}

}
