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
package org.eclipse.gef4.zest.fx.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.ui.MvcFxUiModule;
import org.eclipse.gef4.mvc.fx.ui.view.FXView;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.zest.fx.ContentPartFactory;
import org.eclipse.gef4.zest.fx.DefaultLayoutModel;
import org.eclipse.gef4.zest.fx.GraphRootPart;
import org.eclipse.gef4.zest.fx.ILayoutModel;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Guice;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

public class ZestFXExampleView extends FXView {

	public ZestFXExampleView() {
		super(Guice.createInjector(Modules.override(new MvcFxModule(){
			@Override
			protected void configure() {
				super.configure();
				bindIContentPartFactory();
			}
			
			protected void bindIContentPartFactory() {
				binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
				}).annotatedWith(Names.named("AbstractViewer"))
						.to(ContentPartFactory.class);
			}

			@Override
			protected void bindAbstractDomainAdapters(
					MapBinder<Class<?>, Object> adapterMapBinder) {
				super.bindAbstractDomainAdapters(adapterMapBinder);
				adapterMapBinder.addBinding(ILayoutModel.class).to(
						DefaultLayoutModel.class);
			}

			@Override
			protected void bindFXRootPart() {
				binder().bind(new TypeLiteral<IRootPart<Node>>() {
				}).annotatedWith(Names.named("AbstractViewer"))
						.to(GraphRootPart.class);
			}
		}).with(new MvcFxUiModule())));
	}

	public static Graph DEFAULT_GRAPH = build09();

	private static Graph build09() {
		// create nodes "0" to "9"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(n("0"), n("1"), n("2"), n("3"), n("4"),
				n("5"), n("6"), n("7"), n("8"), n("9")));

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

	private static org.eclipse.gef4.graph.Node n(String label) {
		return new org.eclipse.gef4.graph.Node.Builder().attr(Key.LABEL, label)
				.build();
	}

	private Graph graph = DEFAULT_GRAPH;

	@Override
	protected FXCanvas createCanvas(Composite parent) {
		FXCanvas canvas = super.createCanvas(parent);
		canvas.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				Rectangle bounds = getCanvas().getBounds();
				getViewer().getViewportModel().setWidth(bounds.width);
				getViewer().getViewportModel().setHeight(bounds.height);
			}
		});
		return canvas;
	}

	@Override
	protected List<Object> getContents() {
		List<Object> contents = new ArrayList<Object>(1);
		contents.add(graph);
		return contents;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
		getViewer().setContents(getContents());
	}

}
