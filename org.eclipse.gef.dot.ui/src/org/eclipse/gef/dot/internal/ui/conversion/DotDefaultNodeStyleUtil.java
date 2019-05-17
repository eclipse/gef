/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - original implementation, refactoring and initial API
 *     Tamas Miklossy     (itemis AG) - original implementation
 *     Matthias Wienand   (itemis AG) - original implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.colorlist.ColorList;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedShape;
import org.eclipse.gef.dot.internal.language.shape.Shape;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.gef.dot.internal.language.style.StyleItem;
import org.eclipse.gef.graph.Node;

public class DotDefaultNodeStyleUtil implements DotNodeStyleUtil {

	protected final DotColorUtil colorUtil;
	protected final Node dot;

	public DotDefaultNodeStyleUtil(DotColorUtil colorUtil, Node dot) {
		this.colorUtil = colorUtil;
		this.dot = dot;
	}

	@Override
	public StringBuilder computeZestStyle() {
		StringBuilder zestStyle = new StringBuilder();

		// color
		Color dotColor = colorAttribute();
		String dotColorScheme = colorschemeAttribute();
		String javaFxColor = colorUtil.computeZestColor(dotColorScheme,
				dotColor);
		// penwidth
		Double penwidth = penwidthAttribute();

		if (isNoneShape(shapeAttribute())) {
			zestStyle.append("-fx-stroke: none;"); //$NON-NLS-1$
		} else {
			if (javaFxColor != null) {
				zestStyle.append(strokeColorFxCssString()); // $NON-NLS-1$
				zestStyle.append(javaFxColor);
				zestStyle.append(";"); //$NON-NLS-1$
			}

			if (penwidth != null) {
				zestStyle.append(strokeWidthFxCssString()); // $NON-NLS-1$
				zestStyle.append(penwidth);
				zestStyle.append(";"); //$NON-NLS-1$
			}
		}

		// style
		Style style = styleAttribute();
		if (style != null) {
			for (StyleItem styleItem : style.getStyleItems()) {
				NodeStyle nodeStyle = NodeStyle.get(styleItem.getName());
				if (nodeStyle != null) {
					addNodeStyle(zestStyle, nodeStyle, penwidth == null);
				}
			}
		}

		// fillcolor: evaluate only if the node style is set to 'filled'.
		if (hasStyle(NodeStyle.FILLED)) {
			Color dotFillColor = null;
			ColorList fillColor = fillcolorAttribute();
			if (fillColor != null && !fillColor.getColorValues().isEmpty()) {
				// TODO: add support for colorList
				dotFillColor = fillColor.getColorValues().get(0).getColor();
			} else {
				// if the style is filled, but fillcolor is not specified, use
				// the color attribute value. If neither the fillcolor nor the
				// color attribute is specified, used the default value.
				dotFillColor = dotColor != null ? dotColor
						: DotColors.getDefaultNodeFillColor();
			}
			String javaFxFillColor = colorUtil.computeZestColor(dotColorScheme,
					dotFillColor);
			if (javaFxFillColor != null) {
				zestStyle.append(fillFxCssString() + javaFxFillColor + ";"); //$NON-NLS-1$
			}
		}

		return zestStyle;
	}

	@Override
	public boolean hasStyle(NodeStyle nodeStyle) {
		Style nodeStyleParsed = styleAttribute();
		if (nodeStyleParsed != null) {
			for (StyleItem styleItem : nodeStyleParsed.getStyleItems()) {
				if (styleItem.getName().equals(nodeStyle.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isNoneShape(Shape dotShape) {
		if (dotShape != null) {
			EObject shape = dotShape.getShape();
			if (shape instanceof PolygonBasedShape) {
				return ((PolygonBasedShape) shape)
						.getShape() == PolygonBasedNodeShape.NONE;
			}
		}

		return false;
	}

	protected void addNodeStyle(StringBuilder zestStyle, NodeStyle style,
			boolean penwidthUnset) {
		// in case of polygon-based nodes (default) shapes use 'stroke'
		switch (style) {
		case BOLD:
			if (penwidthUnset)
				zestStyle.append("-fx-stroke-width:2;"); //$NON-NLS-1$
			break;
		case DASHED:
			zestStyle.append("-fx-stroke-dash-array: 7 7;"); //$NON-NLS-1$
			break;
		case DIAGONALS:
			// TODO: add support for 'diagonals' styled nodes
			break;
		case DOTTED:
			zestStyle.append("-fx-stroke-dash-array: 1 6;"); //$NON-NLS-1$
			break;
		case RADIAL:
			// TODO: add support for 'radial' styled nodes
			break;
		case ROUNDED:
			// TODO: add support for 'rounded' styled nodes
			break;
		case SOLID:
			zestStyle.append("-fx-stroke-width: 1;"); //$NON-NLS-1$
			break;
		case STRIPED:
			// TODO: add support for 'striped' styled nodes
			break;
		case WEDGED:
			// TODO: add support for 'wedged' styled nodes
			break;
		}
	}

	protected String fillFxCssString() {
		return "-fx-fill: "; //$NON-NLS-1$
	}

	protected String strokeWidthFxCssString() {
		return "-fx-stroke-width:"; //$NON-NLS-1$
	}

	protected String strokeColorFxCssString() {
		return "-fx-stroke: "; //$NON-NLS-1$
	}

	protected Shape shapeAttribute() {
		return DotAttributes.getShapeParsed(dot);
	}

	protected String colorschemeAttribute() {
		return DotAttributes.getColorscheme(dot);
	}

	protected Color colorAttribute() {
		return DotAttributes.getColorParsed(dot);
	}

	protected Double penwidthAttribute() {
		return DotAttributes.getPenwidthParsed(dot);
	}

	protected ColorList fillcolorAttribute() {
		return DotAttributes.getFillcolorParsed(dot);
	}

	protected Style styleAttribute() {
		return DotAttributes.getStyleParsed(dot);
	}
}
