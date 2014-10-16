/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXStageSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ZestFXExampleApplication extends Application {

	private static Graph build09() {
		// create nodes "0" to "9"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(
				n(Key.LABEL.toString(), "0", "tooltip", "zero"),
				n(Key.LABEL.toString(), "1", "tooltip", "one"),
				n(Key.LABEL.toString(), "2", "tooltip", "two"),
				n(Key.LABEL.toString(), "3", "tooltip", "three"),
				n(Key.LABEL.toString(), "4", "tooltip", "four"),
				n(Key.LABEL.toString(), "5", "tooltip", "five"),
				n(Key.LABEL.toString(), "6", "tooltip", "six"),
				n(Key.LABEL.toString(), "7", "tooltip", "seven"),
				n(Key.LABEL.toString(), "8", "tooltip", "eight"),
				n(Key.LABEL.toString(), "9", "tooltip", "nine")));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(9)),
				e(nodes.get(1), nodes.get(8)), e(nodes.get(2), nodes.get(7)),
				e(nodes.get(3), nodes.get(6)), e(nodes.get(4), nodes.get(5)),
				e(nodes.get(0), nodes.get(4)), e(nodes.get(1), nodes.get(6)),
				e(nodes.get(2), nodes.get(8)), e(nodes.get(3), nodes.get(5)),
				e(nodes.get(4), nodes.get(7)), e(nodes.get(5), nodes.get(1))));

		// default: directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(Graph.Attr.Key.GRAPH_TYPE.toString(),
				Graph.Attr.Value.GRAPH_DIRECTED);
		return new Graph(attrs, nodes, edges);
	}

	private static Edge e(org.eclipse.gef4.graph.Node n,
			org.eclipse.gef4.graph.Node m) {
		String label = (String) n.getAttrs().get(Key.LABEL.toString())
				+ (String) m.getAttrs().get(Key.LABEL.toString());
		return new Edge.Builder(n, m).attr(Key.LABEL, label).build();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	private static org.eclipse.gef4.graph.Node n(String... attr) {
		org.eclipse.gef4.graph.Node.Builder builder = new org.eclipse.gef4.graph.Node.Builder();
		for (int i = 0; i < attr.length; i += 2) {
			builder.attr(attr[i], attr[i + 1]);
		}
		return builder.build();
	}

	public static Graph DEFAULT_GRAPH = build09();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// TODO: inject domain
		Injector injector = Guice.createInjector(new ZestFxExampleModule());
		FXDomain domain = new FXDomain();
		injector.injectMembers(domain);

		final FXViewer viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXStageSceneContainer(primaryStage));

		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);

		// activate domain only after viewers have been hooked
		domain.activate();

		viewer.getAdapter(ContentModel.class).setContents(
				Collections.singletonList(DEFAULT_GRAPH));

		viewer.getAdapter(ViewportModel.class)
				.setWidth(primaryStage.getWidth());
		viewer.getAdapter(ViewportModel.class).setHeight(
				primaryStage.getHeight());

		primaryStage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, final Number newValue) {
				if (newValue != null) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							viewer.getAdapter(ViewportModel.class).setWidth(
									newValue.doubleValue());
						}
					});
				}
			}
		});
		primaryStage.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, final Number newValue) {
				if (newValue != null) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							viewer.getAdapter(ViewportModel.class).setHeight(
									newValue.doubleValue());
						}

					});
				}
			}
		});

		primaryStage.setTitle("GEF4 Zest.FX Example");
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
