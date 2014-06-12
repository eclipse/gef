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
package org.eclipse.gef4.mvc.fx.domain;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.domain.AbstractDomain;
import org.eclipse.gef4.mvc.fx.tools.FXDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXSelectionTool;
import org.eclipse.gef4.mvc.fx.tools.FXZoomTool;
import org.eclipse.gef4.mvc.tools.CompositeTool;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXDomain extends AbstractDomain<Node> {

	@Override
	protected ITool<Node> getDefaultTool() {
		CompositeTool<Node> baseTool = new CompositeTool<Node>();
		baseTool.add(new FXSelectionTool()); // TODO use drag tool
		baseTool.add(new FXHoverTool());
		baseTool.add(new FXDragTool());
		baseTool.add(new FXZoomTool());
		return baseTool;
	}

}
