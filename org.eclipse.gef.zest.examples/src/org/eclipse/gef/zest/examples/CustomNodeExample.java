/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.zest.examples.layout.CustomFigureGraphSnippet
 *
 *******************************************************************************/
package org.eclipse.gef.zest.examples;

import java.util.Map;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

public class CustomNodeExample extends AbstractZestExample {

	public static class CustomContentPartFactory
			extends ZestFxContentPartFactory {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<? extends Node> createContentPart(Object content,
				Map<Object, Object> contextMap) {
			if (content instanceof org.eclipse.gef.graph.Node) {
				// create custom node if we find the custom attribute
				org.eclipse.gef.graph.Node n = (org.eclipse.gef.graph.Node) content;
				Object isCustom = n.attributesProperty().get(ATTR_CUSTOM);
				if (isCustom instanceof Boolean && (Boolean) isCustom) {
					CustomNodeContentPart part = new CustomNodeContentPart();
					if (part != null) {
						injector.injectMembers(part);
					}
					return part;
				}
			}
			return super.createContentPart(content, contextMap);
		}
	}

	public static class CustomModule extends ZestFxModule {

		protected void bindIContentPartFactory() {
			binder().bind(IContentPartFactory.class)
					.toInstance(new CustomContentPartFactory());
		}

	}

	public static class CustomNodeContentPart extends NodePart {
		private VBox vbox;
		private Text labelText;

		@Override
		protected Group doCreateVisual() {
			ImageView ian = new ImageView(new javafx.scene.image.Image(
					getClass().getResource("ibull.jpg").toExternalForm()));
			Polyline body = new Polyline(0, 0, 0, 60, 25, 90, 0, 60, -25, 90, 0,
					60, 0, 25, 25, 0, 0, 25, -25, 0);
			body.setTranslateX(ian.getLayoutBounds().getWidth() / 2
					- body.getLayoutBounds().getWidth() / 2 - 5);
			body.setTranslateY(-15);
			labelText = new Text();
			vbox = new VBox();
			vbox.getChildren().addAll(ian, body, labelText);
			return new Group(vbox);
		}

		@Override
		protected Text getLabelText() {
			return labelText;
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	private static final String ATTR_CUSTOM = "isCustom";

	public CustomNodeExample() {
		super("GEF Zest - Custom Node Example");
	}

	@Override
	protected Graph createGraph() {
		Graph g = new Graph();
		e(g, n(g, LABEL, "A", ATTR_CUSTOM, true), n(g, LABEL, "B"));
		g.attributesProperty().put(ZestProperties.LAYOUT_ALGORITHM__G,
				new SugiyamaLayoutAlgorithm());
		return g;
	}

	@Override
	protected Module createModule() {
		return new CustomModule();
	}

}
