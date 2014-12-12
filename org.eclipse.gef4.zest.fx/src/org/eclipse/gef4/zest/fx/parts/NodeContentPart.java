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

import java.util.Arrays;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart.FXLabeledNode;

public class NodeContentPart extends AbstractFXContentPart<FXLabeledNode> {

	public class FXLabeledNode extends Group {

		public static final String DEFAULT_LABEL = "-";

		protected Rectangle box = new Rectangle();
		protected HBox hbox = new HBox();
		protected Text text = new Text();
		protected ImageView imageView = new ImageView();
		protected double padding = 5;

		{
			setAutoSizeChildren(false);
			hbox.getChildren().addAll(imageView, text);
			getChildren().addAll(box, hbox);
			box.setFill(new LinearGradient(0, 0, 1, 1, true,
					CycleMethod.REFLECT, Arrays.asList(new Stop(0, new Color(1,
							1, 1, 1)))));
			box.setStroke(new Color(0, 0, 0, 1));
			text.setTextOrigin(VPos.TOP);
			text.setText(DEFAULT_LABEL);
			text.boundsInLocalProperty().addListener(
					new ChangeListener<Bounds>() {
						@Override
						public void changed(
								ObservableValue<? extends Bounds> observable,
								Bounds oldBounds, Bounds newBounds) {
							hbox.autosize();
						}
					});
			imageView.setImage(null);
			imageView.boundsInLocalProperty().addListener(
					new ChangeListener<Bounds>() {
						@Override
						public void changed(
								ObservableValue<? extends Bounds> observable,
								Bounds oldBounds, Bounds newBounds) {
							hbox.autosize();
						}
					});
			hbox.layoutBoundsProperty().addListener(
					new ChangeListener<Bounds>() {
						@Override
						public void changed(
								ObservableValue<? extends Bounds> arg0,
								Bounds arg1, Bounds arg2) {
							refreshLayout();
						}
					});
		}

		public ImageView getImageView() {
			return imageView;
		}

		protected void refreshLayout() {
			hbox.setTranslateX(padding);
			hbox.setTranslateY(padding);
			box.setWidth(hbox.getWidth() + 2 * padding);
			box.setHeight(hbox.getHeight() + 2 * padding);
		}

		public void setLabel(String label) {
			text.setText(label);
		}

	}

	public static final String CSS_CLASS = "node";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_ID = "id";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_IMAGE = "image";
	public static final Object ATTR_TOOLTIP = "tooltip";

	@Override
	protected FXLabeledNode createVisual() {
		FXLabeledNode visual = new FXLabeledNode();
		visual.getStyleClass().add(CSS_CLASS);
		return visual;
	}

	@Override
	public void doRefreshVisual(FXLabeledNode visual) {
		// currently, the attributes cannot change, therefore we never have to
		// refresh a node here
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
		String str = label instanceof String ? (String) label
				: label == null ? "-" : label.toString();
		getVisual().setLabel(str);

		// set image
		Object imageFileUrl = attrs.get(ATTR_IMAGE);
		if (imageFileUrl instanceof String) {
			getVisual().getImageView().setImage(
					new Image((String) imageFileUrl));
		}

		// set tooltip
		Object tooltip = attrs.get(ATTR_TOOLTIP);
		if (tooltip instanceof String) {
			Tooltip.install(getVisual(), new Tooltip((String) tooltip));
		}
	}

}
