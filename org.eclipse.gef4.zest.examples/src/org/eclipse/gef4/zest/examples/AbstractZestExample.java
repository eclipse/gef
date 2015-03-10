/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.examples;

import java.util.Collections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Edge.Builder;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXStageSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class AbstractZestExample extends Application {

	private static int id = 0;
	protected static final String ID = ZestProperties.ELEMENT_CSS_ID;
	protected static final String LABEL = ZestProperties.ELEMENT_LABEL;
	protected static final String CSS_CLASS = ZestProperties.ELEMENT_CSS_CLASS;
	protected static final String LAYOUT_IRRELEVANT = ZestProperties.ELEMENT_LAYOUT_IRRELEVANT;

	protected static String genId() {
		return Integer.toString(id++);
	}

	protected static Edge e(org.eclipse.gef4.graph.Node n,
			org.eclipse.gef4.graph.Node m, Object... attr) {
		String label = (String) n.getAttrs().get(LABEL)
				+ (String) m.getAttrs().get(LABEL);
		Builder builder = new Edge.Builder(n, m).attr(LABEL, label).attr(ID,
				genId());
		for (int i = 0; i < attr.length; i += 2) {
			builder.attr(attr[i].toString(), attr[i + 1]);
		}
		return builder.build();
	}

	protected static Edge e(Graph graph, org.eclipse.gef4.graph.Node n,
			org.eclipse.gef4.graph.Node m, Object... attr) {
		Edge edge = e(n, m, attr);
		edge.setGraph(graph);
		graph.getEdges().add(edge);
		return edge;
	}

	protected static org.eclipse.gef4.graph.Node n(Object... attr) {
		org.eclipse.gef4.graph.Node.Builder builder = new org.eclipse.gef4.graph.Node.Builder();
		String id = genId();
		builder.attr(ID, id).attr(LABEL, id);
		for (int i = 0; i < attr.length; i += 2) {
			builder.attr(attr[i].toString(), attr[i + 1]);
		}
		return builder.build();
	}

	protected static org.eclipse.gef4.graph.Node n(Graph graph, Object... attr) {
		Node node = n(attr);
		node.setGraph(graph);
		graph.getNodes().add(node);
		return node;
	}

	private String title;
	protected FXDomain domain;
	protected FXViewer viewer;
	protected Graph graph;

	public AbstractZestExample(String title) {
		this.title = title;
	}

	protected abstract Graph createGraph();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// configure application
		Injector injector = Guice.createInjector(createModule());
		domain = injector.getInstance(FXDomain.class);
		viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXStageSceneContainer(primaryStage));

		primaryStage.setResizable(true);
		primaryStage.setWidth(getStageWidth());
		primaryStage.setHeight(getStageHeight());

		// activate domain only after viewers have been hooked
		domain.activate();

		// set contents in the JavaFX application thread because it alters the
		// scene graph
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				graph = createGraph();
				viewer.getAdapter(ContentModel.class).setContents(
						Collections.singletonList(graph));
			}
		});

		customizeUi(viewer.getScrollPane());

		primaryStage.setTitle(title);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	protected int getStageHeight() {
		return 480;
	}

	protected int getStageWidth() {
		return 640;
	}

	protected void customizeUi(ScrollPaneEx scrollPane) {
	}

	protected Module createModule() {
		return new ZestFxModule();
	}

}