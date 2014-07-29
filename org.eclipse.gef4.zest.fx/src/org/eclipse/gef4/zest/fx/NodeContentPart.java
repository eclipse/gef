/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXLabeledNode;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class NodeContentPart extends AbstractFXContentPart {

	public static final String CSS_CLASS = "node";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_ID = "id";
	public static final String ATTR_STYLE = "style";

	protected FXLabeledNode visual = new FXLabeledNode();
	protected IFXAnchor anchor;

	{
		visual.getStyleClass().add(CSS_CLASS);
	}

	@Override
	public void doRefreshVisual() {
		Object label = getContent().getAttrs().get(Attr.Key.LABEL.toString());
		String str = label instanceof String ? (String) label
				: label == null ? "-" : label.toString();
		visual.setLabel(str);
	}

	@Override
	public IFXAnchor getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			anchor = new FXChopBoxAnchor(visual);
		}
		return anchor;
	}

	@Override
	public org.eclipse.gef4.graph.Node getContent() {
		return (org.eclipse.gef4.graph.Node) super.getContent();
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void setContent(Object content) {
		super.setContent(content);
		if (content == null) {
			return;
		}
		if (!(content instanceof org.eclipse.gef4.graph.Node)) {
			throw new IllegalArgumentException("Content of wrong type!");
		}
		org.eclipse.gef4.graph.Node node = (org.eclipse.gef4.graph.Node) content;
		Map<String, Object> attrs = node.getAttrs();
		if (attrs.containsKey(ATTR_CLASS)) {
			visual.getStyleClass().add((String) attrs.get(ATTR_CLASS));
		}
		if (attrs.containsKey(ATTR_ID)) {
			visual.setId((String) attrs.get(ATTR_ID));
		}
		if (attrs.containsKey(ATTR_STYLE)) {
			visual.setStyle((String) attrs.get(ATTR_STYLE));
		}
	}

}
