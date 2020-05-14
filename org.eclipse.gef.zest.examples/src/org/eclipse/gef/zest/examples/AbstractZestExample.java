/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.examples;

import java.util.Collections;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Edge.Builder;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class AbstractZestExample extends Application {

	private static int id = 0;
	protected static final String ID = ZestProperties.CSS_ID__NE;
	protected static final String LABEL = ZestProperties.LABEL__NE;
	protected static final String CSS_CLASS = ZestProperties.CSS_CLASS__NE;
	protected static final String LAYOUT_IRRELEVANT = ZestProperties.LAYOUT_IRRELEVANT__NE;

	protected static String genId() {
		return Integer.toString(id++);
	}

	protected static Edge e(org.eclipse.gef.graph.Node n,
			org.eclipse.gef.graph.Node m, Object... attr) {
		String label = (String) n.attributesProperty().get(LABEL)
				+ (String) m.attributesProperty().get(LABEL);
		Builder builder = new Edge.Builder(n, m).attr(LABEL, label).attr(ID,
				genId());
		for (int i = 0; i < attr.length; i += 2) {
			builder.attr(attr[i].toString(), attr[i + 1]);
		}
		return builder.buildEdge();
	}

	protected static Edge e(Graph graph, org.eclipse.gef.graph.Node n,
			org.eclipse.gef.graph.Node m, Object... attr) {
		Edge edge = e(n, m, attr);
		graph.getEdges().add(edge);
		return edge;
	}

	protected static org.eclipse.gef.graph.Node n(Object... attr) {
		org.eclipse.gef.graph.Node.Builder builder = new org.eclipse.gef.graph.Node.Builder();
		String id = genId();
		builder.attr(ID, id).attr(LABEL, id);
		for (int i = 0; i < attr.length; i += 2) {
			builder.attr(attr[i].toString(), attr[i + 1]);
		}
		return builder.buildNode();
	}

	protected static org.eclipse.gef.graph.Node n(Graph graph, Object... attr) {
		Node node = n(attr);
		graph.getNodes().add(node);
		return node;
	}

	private String title;
	protected IDomain domain;
	protected IViewer viewer;
	protected Graph graph;

	public AbstractZestExample(String title) {
		this.title = title;
	}

	protected abstract Graph createGraph();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// create graph
		graph = createGraph();

		// configure application
		Injector injector = Guice.createInjector(createModule());
		domain = injector.getInstance(IDomain.class);
		viewer = domain.getAdapter(
				AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
		primaryStage.setScene(createScene(viewer));

		primaryStage.setResizable(true);
		primaryStage.setWidth(getStageWidth());
		primaryStage.setHeight(getStageHeight());
		primaryStage.setTitle(title);
		primaryStage.show();

		// activate domain only after viewers have been hooked
		domain.activate();

		// set contents in the JavaFX application thread because it alters the
		// scene graph
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				viewer.getContents().setAll(Collections.singletonList(graph));
			}
		});
	}

	protected Scene createScene(IViewer viewer) {
		return new Scene(((IViewer) viewer).getCanvas());
	}

	protected int getStageHeight() {
		return 480;
	}

	protected int getStageWidth() {
		return 640;
	}

	protected Module createModule() {
		return new ZestFxModule();
	}

}