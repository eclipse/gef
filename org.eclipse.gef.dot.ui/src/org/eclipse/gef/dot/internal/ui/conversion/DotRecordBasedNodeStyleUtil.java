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
 *     Zoey Gerrit Prigge (itemis AG) - original implementation, refactoring and initial API
 *     Tamas Miklossy     (itemis AG) - original implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.shape.RecordBasedNodeShape;
import org.eclipse.gef.dot.internal.language.shape.RecordBasedShape;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.gef.dot.internal.language.style.StyleItem;
import org.eclipse.gef.graph.Node;

public class DotRecordBasedNodeStyleUtil extends DotDefaultNodeStyleUtil {

	boolean hasBorderStyle = false;

	public DotRecordBasedNodeStyleUtil(DotColorUtil colorUtil, Node dot) {
		super(colorUtil, dot);
	}

	@Override
	public StringBuilder computeZestStyle() {
		StringBuilder zestShapeStyle = super.computeZestStyle();

		RecordBasedNodeShape recordBasedShape = ((RecordBasedShape) DotAttributes
				.getShapeParsed(dot).getShape()).getShape();

		// Mrecord shape has rounded edges (for border and fill)
		if (RecordBasedNodeShape.MRECORD.equals(recordBasedShape))
			zestShapeStyle.append(
					"-fx-background-radius:10px;-fx-border-radius:10px;"); //$NON-NLS-1$

		// If a border is set, we don't change this, but per default, there
		// is a solid border in graphviz
		if (!hasBorderStyle) // $NON-NLS-1$
			zestShapeStyle.append("-fx-border-style:solid;"); //$NON-NLS-1$

		return zestShapeStyle;
	}

	public StringBuilder computeLineStyle() {
		StringBuilder zestStyle = new StringBuilder();
		// color
		Color dotColor = DotAttributes.getColorParsed(dot);
		String dotColorScheme = DotAttributes.getColorscheme(dot);
		String javaFxColor = colorUtil.computeZestColor(dotColorScheme,
				dotColor);
		if (javaFxColor != null) {
			zestStyle.append("-fx-stroke:" + javaFxColor + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// penwidth
		Double penwidth = DotAttributes.getPenwidthParsed(dot);
		if (penwidth != null) {
			zestStyle.append("-fx-stroke-width:"); //$NON-NLS-1$
			zestStyle.append(penwidth);
			zestStyle.append(";"); //$NON-NLS-1$
		}

		Style style = DotAttributes.getStyleParsed(dot);
		if (style != null) {
			for (StyleItem styleItem : style.getStyleItems()) {
				NodeStyle nodeStyle = NodeStyle.get(styleItem.getName());
				if (nodeStyle != null) {
					super.addNodeStyle(zestStyle, nodeStyle, penwidth == null);
				}
			}
		}

		return zestStyle;
	}

	@Override
	protected void addNodeStyle(StringBuilder zestStyle, NodeStyle style,
			boolean penwidthUnset) {
		// the node style needs use different JavaFX css attributes
		// in case of record based nodes shapes use 'border'
		switch (style) {
		case BOLD:
			if (penwidthUnset)
				zestStyle.append("-fx-border-width:2;"); //$NON-NLS-1$
			break;
		case DASHED:
			zestStyle.append("-fx-border-style:dashed;"); //$NON-NLS-1$
			hasBorderStyle = true;
			break;
		case DIAGONALS:
			// TODO: add support for 'diagonals' styled nodes
			break;
		case DOTTED:
			zestStyle.append("-fx-border-style:dotted;"); //$NON-NLS-1$
			hasBorderStyle = true;
			break;
		case RADIAL:
			// TODO: add support for 'radial' styled nodes
			break;
		case ROUNDED:
			// TODO: add support for 'rounded' styled nodes
			break;
		case SOLID:
			zestStyle.append("-fx-border-style:solid;"); //$NON-NLS-1$
			hasBorderStyle = true;
			break;
		case STRIPED:
			// TODO: add support for 'striped' styled nodes
			break;
		case WEDGED:
			// TODO: add support for 'wedged' styled nodes
			break;
		}
	}

	@Override
	protected String strokeWidthFxCssString() {
		return "-fx-border-width:"; //$NON-NLS-1$
	}

	@Override
	protected String strokeColorFxCssString() {
		return "-fx-border-color:"; //$NON-NLS-1$
	}

	@Override
	protected String fillFxCssString() {
		return "-fx-background-color:"; //$NON-NLS-1$
	}
}
