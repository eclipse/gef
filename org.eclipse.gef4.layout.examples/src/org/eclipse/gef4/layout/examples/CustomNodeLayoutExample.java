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
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.CustomFigureGraphSnippet
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import java.util.Map;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

import org.eclipse.gef4.common.inject.AdaptableScopes;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.parts.ContentPartFactory;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

public class CustomNodeLayoutExample extends AbstractZestExample {

	public static class CustomContentPartFactory extends ContentPartFactory {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<Node, ? extends Node> createContentPart(
				Object content, IBehavior<Node> contextBehavior,
				Map<Object, Object> contextMap) {
			if (content instanceof org.eclipse.gef4.graph.Node) {
				org.eclipse.gef4.graph.Node n = (org.eclipse.gef4.graph.Node) content;
				Object isCustom = n.getAttrs().get("isCustom");
				if (isCustom instanceof Boolean && (Boolean) isCustom) {
					CustomNodeContentPart part = new CustomNodeContentPart();
					if (part != null) {
						injector.injectMembers(part);
					}
					return part;
				}
			}
			return super
					.createContentPart(content, contextBehavior, contextMap);
		}
	}

	public static class CustomModule extends ZestFxModule {
		@Override
		protected void bindIContentPartFactory() {
			binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
			}).to(CustomContentPartFactory.class)
					.in(AdaptableScopes.typed(FXViewer.class));
		}
	}

	public static class CustomNodeContentPart extends NodeContentPart {
		private VBox vbox;

		@Override
		protected void autosizeNodeVisual() {
			vbox.autosize();
		}

		@Override
		protected void createNodeVisual(Group group, ImageView iconImageView,
				Text labelText, StackPane nestedContentStackPane) {
			ImageView ian = new ImageView(new Image(getClass().getResource(
					"ibull.jpg").toExternalForm()));
			Polyline body = new Polyline(0, 0, 0, 60, 25, 90, 0, 60, -25, 90,
					0, 60, 0, 25, 25, 0, 0, 25, -25, 0);
			body.setTranslateX(ian.getLayoutBounds().getWidth() / 2
					- body.getLayoutBounds().getWidth() / 2 - 5);
			body.setTranslateY(-15);
			vbox = new VBox();
			vbox.getChildren().addAll(ian, body, labelText, iconImageView,
					nestedContentStackPane);
			group.getChildren().add(vbox);
			labelText.setStroke(Color.BLACK);
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public CustomNodeLayoutExample() {
		super("GEF4 Layouts - Custom Node Example");
	}

	@Override
	protected Graph createGraph() {
		Graph g = new Graph();
		e(g, n(g, LABEL, "A", "isCustom", true), n(g, LABEL, "B"));
		g.getAttrs().put(ZestProperties.GRAPH_LAYOUT,
				new SugiyamaLayoutAlgorithm());
		return g;
	}

	@Override
	protected Module createModule() {
		return new CustomModule();
	}

}
