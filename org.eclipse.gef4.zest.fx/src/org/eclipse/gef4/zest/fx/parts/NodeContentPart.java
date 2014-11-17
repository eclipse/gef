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
package org.eclipse.gef4.zest.fx.parts;

import java.util.Map;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class NodeContentPart extends AbstractFXContentPart<FXLabeledNode> {

	public static final String CSS_CLASS = "node";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_ID = "id";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_IMAGE = "image";
	public static final Object ATTR_TOOLTIP = "tooltip";

	protected IFXAnchor anchor;

	@Override
	protected FXLabeledNode createVisual() {
		FXLabeledNode visual = new FXLabeledNode();
		visual.getStyleClass().add(CSS_CLASS);
		return visual;
	}

	@Override
	public void doRefreshVisual() {
		// currently, the attributes cannot change, therefore we never have to
		// refresh a node here
	}

	@Override
	public IFXAnchor getAnchor(IVisualPart<Node, ? extends Node> anchored) {
		if (anchor == null) {
			anchor = new FXChopBoxAnchor(getVisual());
		}
		return anchor;
	}

	@Override
	public org.eclipse.gef4.graph.Node getContent() {
		return (org.eclipse.gef4.graph.Node) super.getContent();
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

		// set CSS properties
		org.eclipse.gef4.graph.Node node = (org.eclipse.gef4.graph.Node) content;
		Map<String, Object> attrs = node.getAttrs();
		if (attrs.containsKey(ATTR_CLASS)) {
			getVisual().getStyleClass().add((String) attrs.get(ATTR_CLASS));
		}
		if (attrs.containsKey(ATTR_ID)) {
			getVisual().setId((String) attrs.get(ATTR_ID));
		}
		if (attrs.containsKey(ATTR_STYLE)) {
			getVisual().setStyle((String) attrs.get(ATTR_STYLE));
		}

		// set label
		Object label = attrs.get(Attr.Key.LABEL.toString());
		String str = label instanceof String ? (String) label : label == null ? "-" : label.toString();
		getVisual().setLabel(str);

		// set image
		Object imageFileUrl = attrs.get(ATTR_IMAGE);
		if (imageFileUrl instanceof String) {
			getVisual().getImageView().setImage(new Image((String) imageFileUrl));
		}

		// set tooltip
		Object tooltip = attrs.get(ATTR_TOOLTIP);
		if (tooltip instanceof String) {
			Tooltip.install(getVisual(), new Tooltip((String) tooltip));
		}
	}

}
