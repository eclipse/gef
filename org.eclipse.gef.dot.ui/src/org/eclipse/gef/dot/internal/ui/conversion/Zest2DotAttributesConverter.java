/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen  (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.point.PointFactory;
import org.eclipse.gef.dot.internal.ui.conversion.Dot2ZestAttributesConverter.Options;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.geometry.Bounds;
import javafx.scene.text.Text;

public class Zest2DotAttributesConverter implements IAttributeCopier {

	private Options options;

	public Options options() {
		if (options == null) {
			options = new Options();
		}
		return options;
	}

	protected void convertAttributes(Graph zest, Graph dot) {
	}

	protected void convertAttributes(Edge zest, Edge dot) {
		String zestCssId = ZestProperties.getCssId(zest);
		if (zestCssId != null) {
			DotAttributes.setId(dot, zestCssId);
		}

		// label
		String zestLabel = ZestProperties.getLabel(zest);
		if (zestLabel != null) {
			DotAttributes.setLabel(dot, zestLabel);
		}

		// external label (xlabel)
		String zestExternalLabel = ZestProperties.getExternalLabel(zest);
		if (zestExternalLabel != null) {
			DotAttributes.setXlabel(dot, zestExternalLabel);
		}

		// tail label
		String zestSourceLabel = ZestProperties.getSourceLabel(zest);
		if (zestSourceLabel != null) {
			DotAttributes.setTaillabel(dot, zestSourceLabel);
		}

		// head label
		String zestTargetLabel = ZestProperties.getTargetLabel(zest);
		if (zestTargetLabel != null) {
			DotAttributes.setHeadlabel(dot, zestTargetLabel);
		}

		// TODO: positions of labels
	}

	protected void convertAttributes(Node zest, Node dot) {
		// id
		String zestCssId = ZestProperties.getCssId(zest);
		if (zestCssId != null) {
			DotAttributes.setId(dot, zestCssId);
		}

		// label
		String zestLabel = ZestProperties.getLabel(zest);
		if (zestLabel != null) {
			DotAttributes.setLabel(dot, zestLabel);
		}

		// external label (xlabel)
		String zestExternalLabel = ZestProperties.getExternalLabel(zest);
		if (zestExternalLabel != null) {
			DotAttributes.setXlabel(dot, zestExternalLabel);
		}

		// Convert position and size; as node position is interpreted as center,
		// we need to know the size in order to infer correct zest positions

		Point zestPosition = ZestProperties.getPosition(zest);
		Dimension zestSize = ZestProperties.getSize(zest);
		if (zestSize != null) {
			// dot default scaling is 72 DPI
			// TODO: if dpi option is set, we should probably use it!
			double dotWidth = zestSize.width / 72; // inches
			double dotHeight = zestSize.height / 72; // inches
			DotAttributes.setWidth(dot, Double.toString(dotWidth));
			DotAttributes.setHeight(dot, Double.toString(dotHeight));

			if (zestPosition != null && !options().ignorePositions) {
				// node position is interpreted as center of node in Dot, and
				// top-left in Zest
				org.eclipse.gef.dot.internal.language.point.Point dotPos = PointFactory.eINSTANCE
						.createPoint();
				dotPos.setX(zestPosition.x - zestSize.width / 2);
				dotPos.setY((options().invertYAxis ? -1 : 1)
						* (zestPosition.y - zestSize.height / 2));
				dotPos.setInputOnly(Boolean.TRUE
						.equals(ZestProperties.getLayoutIrrelevant(zest)));
				DotAttributes.setPosParsed(dot, dotPos);
			}
		}

		// external label position (xlp)
		Point zestExternalLabelPosition = ZestProperties
				.getExternalLabelPosition(zest);
		if (zestExternalLabel != null && zestExternalLabelPosition != null
				&& !options().ignorePositions) {
			org.eclipse.gef.dot.internal.language.point.Point dotXlp = PointFactory.eINSTANCE
					.createPoint();
			Bounds labelSize = new Text(zestExternalLabel).getLayoutBounds();
			dotXlp.setX(zestExternalLabelPosition.x - labelSize.getWidth() / 2);
			dotXlp.setY((options().invertYAxis ? -1 : 1)
					* (zestExternalLabelPosition.y
							- labelSize.getHeight() / 2));
			DotAttributes.setXlpParsed(dot, dotXlp);
		}
	}

	@Override
	public void copy(IAttributeStore source, IAttributeStore target) {
		if (source instanceof Node && target instanceof Node) {
			convertAttributes((Node) source, (Node) target);
		} else if (source instanceof Edge && target instanceof Edge) {
			convertAttributes((Edge) source, (Edge) target);
		} else if (source instanceof Graph && target instanceof Graph) {
			convertAttributes((Graph) source, (Graph) target);
		} else {
			throw new IllegalArgumentException();
		}
	}

}
