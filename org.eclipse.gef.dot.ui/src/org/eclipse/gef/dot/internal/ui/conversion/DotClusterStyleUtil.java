/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #541052)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.colorlist.ColorList;
import org.eclipse.gef.dot.internal.language.shape.Shape;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.gef.graph.Node;

public class DotClusterStyleUtil extends DotDefaultNodeStyleUtil {

	public DotClusterStyleUtil(DotColorUtil colorUtil, Node dot) {
		super(colorUtil, dot);
	}

	@Override
	protected Shape shapeAttribute() {
		// Clusters have no shape Attribute
		return null;
	}

	@Override
	protected String colorschemeAttribute() {
		return DotAttributes.getColorscheme(dot.getNestedGraph());
	}

	@Override
	protected Color colorAttribute() {
		return DotAttributes.getColorParsed(dot.getNestedGraph());
	}

	@Override
	protected Double penwidthAttribute() {
		return DotAttributes.getPenwidthParsed(dot.getNestedGraph());
	}

	@Override
	protected ColorList fillcolorAttribute() {
		// in graphviz practice, bgcolor overrides fillcolor, if present
		ColorList bgColor = DotAttributes
				.getBgcolorParsed(dot.getNestedGraph());
		ColorList fillColor = DotAttributes
				.getFillcolorParsed(dot.getNestedGraph());
		return bgColor != null ? bgColor : fillColor;
	}

	@Override
	protected boolean fillCondition() {
		// if fillcolor or bgcolor is set, style needs not be set to filled
		return fillcolorAttribute() != null;
	}

	@Override
	protected Style styleAttribute() {
		return DotAttributes.getStyleParsed(dot.getNestedGraph());
	}
}
