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
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.core.viewers.GraphViewer
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.jface;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.embed.swt.FXCanvas;

import org.eclipse.gef4.fx.ui.canvas.FXCanvasEx;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.ui.parts.SelectionForwarder;
import org.eclipse.gef4.mvc.fx.ui.viewer.FXCanvasSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class ZestContentViewer extends ContentViewer {

	// TODO: inject canvasFactory
	// @Inject
	// private IFXCanvasFactory canvasFactory;

	private FXCanvas canvas;
	private FXDomain domain;
	private FXViewer viewer;
	private ISelection selection;
	private SelectionForwarder<javafx.scene.Node> selectionForwarder;
	private Composite parent;

	public ZestContentViewer(Composite parent, int style) {
		this.parent = parent;
	}

	protected FXCanvas createCanvas(final Composite parent) {
		// TODO: inject canvasFactory
		// return canvasFactory.createCanvas(parent);
		return new FXCanvasEx(parent, SWT.NONE);
	}

	public void createControl() {
		// create injector
		Injector injector = Guice.createInjector(createModule());
		injector.injectMembers(this);

		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);

		// inject domain
		domain = injector.getInstance(FXDomain.class);

		// hook viewer
		viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXCanvasSceneContainer(canvas));

		// report canvas size changes to the ViewportModel
		canvas.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				Rectangle bounds = canvas.getBounds();
				viewer.getAdapter(ViewportModel.class).setWidth(bounds.width);
				viewer.getAdapter(ViewportModel.class).setHeight(bounds.height);
			}
		});

		// activate domain
		domain.activate();

		// register listener to provide selection to workbench
		selectionForwarder = new SelectionForwarder<javafx.scene.Node>(this,
				viewer);
	}

	protected Graph createGraph(IContentProvider contentProvider,
			ILabelProvider labelProvider) {
		Graph graph = new Graph();
		if (contentProvider instanceof IGraphNodeContentProvider) {
			IGraphNodeContentProvider graphNodeProvider = (IGraphNodeContentProvider) contentProvider;
			Object[] nodes = graphNodeProvider.getNodes();
			createNodesAndEdges(graphNodeProvider, labelProvider, graph, nodes);
		}
		return graph;
	}

	protected Module createModule() {
		// TODO:
		// return Modules.override(new ZestFxUiModule()).with(new
		// ZestFxModule());
		return new ZestFxModule();
	}

	protected Node createNode(Map<Object, Node> contentToGraphMap, Object node,
			IGraphNodeContentProvider graphNodeProvider,
			ILabelProvider labelProvider) {
		// do not create the same node twice
		if (contentToGraphMap.containsKey(node)) {
			return contentToGraphMap.get(node);
		}

		Node graphNode = new Node();
		contentToGraphMap.put(node, graphNode);

		// retrieve label
		String label = labelProvider.getText(node);
		// Image icon = labelProvider.getImage(node);

		// transfer label information into node properties
		graphNode.getAttrs().put(ZestProperties.ELEMENT_LABEL, label);
		// graphNode.getAttrs().put(NodeContentPart.ATTR_IMAGE, icon);

		// TODO: color, etc.
		// if (labelProvider instanceof IColorProvider) {
		// }

		// create nested graph (optional)
		if (graphNodeProvider instanceof INestedGraphContentProvider) {
			INestedGraphContentProvider nestedGraphProvider = (INestedGraphContentProvider) graphNodeProvider;
			Graph graph = new Graph();
			Object[] nodes = nestedGraphProvider.getChildren(graphNode);
			createNodesAndEdges(graphNodeProvider, labelProvider, graph, nodes);
			graph.setNestingNode(graphNode);
		}

		return graphNode;
	}

	/**
	 * Creates graph {@link Node nodes} and {@link Edge edges} from the given
	 * array of <i>nodes</i>.
	 *
	 * @param contentProvider
	 *            This viewer's {@link IContentProvider} for convenience.
	 * @param labelProvider
	 *            This viewer's {@link ILabelProvider} for convenience.
	 * @param graph
	 *            The {@link Graph} for which nodes and edges are created.
	 * @param graphNodeProvider
	 *            The
	 * @param nodes
	 * @param contentToNodeMap
	 */
	protected void createNodesAndEdges(
			IGraphNodeContentProvider contentProvider,
			ILabelProvider labelProvider, Graph graph, Object[] nodes) {
		// map content elements to created nodes so we can access them when
		// creating the edges
		Map<Object, Node> contentToNodeMap = new HashMap<Object, Node>();
		// create nodes
		for (Object node : nodes) {
			Node graphNode = createNode(contentToNodeMap, node,
					contentProvider, labelProvider);
			graph.getNodes().add(graphNode);
			graphNode.setGraph(graph);
		}
		// create edges
		for (Object node : nodes) {
			Node sourceNode = contentToNodeMap.get(node);
			for (Object target : contentProvider.getConnectedTo(node)) {
				Edge edge = new Edge(sourceNode, contentToNodeMap.get(target));
				// TODO: createEdge() which sets "label", "directed", "weight"
				graph.getEdges().add(edge);
				edge.setGraph(graph);
			}
		}
	}

	@Override
	public Control getControl() {
		return canvas;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		return (ILabelProvider) super.getLabelProvider();
	}

	public ILayoutAlgorithm getLayoutAlgorithm() {
		return domain
				.getAdapter(LayoutModel.class)
				.getLayoutContext(
						(Graph) viewer.getAdapter(ContentModel.class)
								.getContents().get(0))
				.getStaticLayoutAlgorithm();
	}

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	protected void handleDispose(DisposeEvent event) {
		if (selectionForwarder != null) {
			selectionForwarder.dispose();
			selectionForwarder = null;
		}

		domain.deactivate();
		domain.dispose();

		super.handleDispose(event);
	}

	@Override
	protected void inputChanged(Object input, Object oldInput) {
		if (input != oldInput && (input == null || !input.equals(oldInput))) {
			refresh();
		}
	}

	@Override
	public void refresh() {
		viewer.getAdapter(ContentModel.class).setContents(
				Collections.singletonList(createGraph(getContentProvider(),
						getLabelProvider())));
	}

	public void setLayoutAlgorithm(ILayoutAlgorithm layoutAlgorithm) {
		domain.getAdapter(LayoutModel.class)
				.getLayoutContext(
						(Graph) viewer.getAdapter(ContentModel.class)
								.getContents().get(0))
				.setStaticLayoutAlgorithm(layoutAlgorithm);
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		if (this.selection != selection
				&& (this.selection == null || !this.selection.equals(selection))) {
			this.selection = selection;
			fireSelectionChanged(new SelectionChangedEvent(this, selection));
			if (reveal) {
				if (selection instanceof StructuredSelection) {
					// TODO: Find a better place to parse an ISelection.
					StructuredSelection sel = (StructuredSelection) selection;
					if (!sel.isEmpty()) {
						for (Object content : sel.toArray()) {
							IContentPart<javafx.scene.Node, ? extends javafx.scene.Node> part = viewer
									.getContentPartMap().get(content);
							if (part != null) {
								viewer.reveal(part);
							}
						}
					}
				}
			}
		}
	}

}
