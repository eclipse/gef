/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx;

import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

public class ZestProperties {

	public static final String NODE_CSS_CLASS = "css-class";

	public static final String NODE_CSS_ID = "css-id";

	public static final String NODE_CSS_STYLE = "css-style";

	public static final String NODE_ICON_URL = "iconUrl";
	// TODO: public static final String NODE_ICON = "icon";

	public static final String NODE_TOOLTIP = "tooltip";

	public static final String NODE_FISHEYE = "fisheye";

	public static final String NODE_LABEL = "label";
	public static final String NODE_LABEL_DEFAULT = "-";

	public static final String EDGE_CSS_CLASS = "css-class";

	public static final String EDGE_CSS_ID = "css-id";

	public static final String EDGE_CSS_STYLE = "css-style";

	public static final String EDGE_STYLE = "style";
	public static final String EDGE_STYLE_DASHED = "dashed";
	public static final String EDGE_STYLE_DOTTED = "dotted";
	public static final String EDGE_STYLE_SOLID = "solid";
	public static final String EDGE_STYLE_DASHDOT = "dashdot";
	public static final String EDGE_STYLE_DASHDOTDOT = "dashdotdot";
	public static final String EDGE_STYLE_DEFAULT = EDGE_STYLE_SOLID;

	public static final String EDGE_LABEL = "label";

	public static final String GRAPH_TYPE = "type";
	public static final String GRAPH_TYPE_DIRECTED = "directed";
	public static final String GRAPH_TYPE_UNDIRECTED = "undirected";

	public static final String GRAPH_LAYOUT = "layout";
	public static final LayoutAlgorithm GRAPH_LAYOUT_DEFAULT = new TreeLayoutAlgorithm();

}
