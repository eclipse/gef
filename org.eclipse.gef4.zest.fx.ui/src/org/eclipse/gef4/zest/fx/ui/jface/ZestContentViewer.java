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
import javafx.embed.swt.SWTFXUtils;

import org.eclipse.gef4.fx.nodes.IFXDecoration;
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
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class ZestContentViewer extends ContentViewer {

	private Module module;
	private FXCanvas canvas;
	private FXDomain domain;
	private FXViewer viewer;
	private ISelection selection;
	private SelectionForwarder<javafx.scene.Node> selectionForwarder;
	private ILayoutAlgorithm layoutAlgorithm;

	public ZestContentViewer() {
		this.module = createModule();
	}

	public ZestContentViewer(Module module) {
		this.module = module;
	}

	protected FXCanvas createCanvas(final Composite parent) {
		// TODO: inject canvasFactory
		// return canvasFactory.createCanvas(parent);
		return new FXCanvasEx(parent, SWT.NONE);
	}

	public void createControl(Composite parent, int style) {
		// create injector
		Injector injector = Guice.createInjector(module);
		injector.injectMembers(this);

		// create viewer and canvas only after toolkit has been initialized
		canvas = createCanvas(parent);

		// inject domain
		domain = injector.getInstance(FXDomain.class);

		// hook viewer
		viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXCanvasSceneContainer(canvas));

		// activate domain
		domain.activate();

		// register listener to provide selection to workbench
		selectionForwarder = new SelectionForwarder<javafx.scene.Node>(this,
				viewer);
	}

	/**
	 * Constructs and returns a new {@link Edge} connecting the given
	 * <i>sourceNode</i> and <i>targetNode</i>. If the <i>labelProvider</i>
	 * implements {@link IGraphNodeLabelProvider}, then attributes for the edge
	 * are determined using the
	 * {@link IGraphNodeLabelProvider#getEdgeAttributes(Object, Object)} methods
	 * and inserted into the edge.
	 *
	 * @return A new {@link Edge}, styled with the label provider.
	 */
	protected Edge createEdge(ILabelProvider labelProvider,
			Object contentSourceNode, Node sourceNode,
			Object contentTargetNode, Node targetNode) {
		Edge edge = new Edge(sourceNode, targetNode);
		if (labelProvider instanceof IEdgeDecorationProvider) {
			IEdgeDecorationProvider edgeDecorationProvider = (IEdgeDecorationProvider) labelProvider;
			IFXDecoration sourceDecoration = edgeDecorationProvider
					.getSourceDecoration(contentSourceNode, contentTargetNode);
			IFXDecoration targetDecoration = edgeDecorationProvider
					.getTargetDecoration(contentSourceNode, contentTargetNode);
			ZestProperties.setSourceDecoration(edge, sourceDecoration);
			ZestProperties.setTargetDecoration(edge, targetDecoration);
		}
		if (labelProvider instanceof IGraphNodeLabelProvider) {
			IGraphNodeLabelProvider graphNodeLabelProvider = (IGraphNodeLabelProvider) labelProvider;
			Map<String, Object> edgeAttributes = graphNodeLabelProvider
					.getEdgeAttributes(contentSourceNode, contentTargetNode);
			if (edgeAttributes != null) {
				edge.getAttrs().putAll(edgeAttributes);
			}
		}
		return edge;
	}

	/**
	 * Constructs and returns a new {@link Graph} and inserts default attributes
	 * into it:
	 * <ol>
	 * <li>layout algorithm</li>
	 * </ol>
	 *
	 * @return A new {@link Graph} with default attributes.
	 */
	protected Graph createEmptyGraph() {
		Graph graph = new Graph();
		if (layoutAlgorithm != null) {
			ZestProperties.setLayout(graph, layoutAlgorithm);
		}
		return graph;
	}

	protected Module createModule() {
		if (PlatformUI.isWorkbenchRunning()) {
			return Modules.override(new ZestFxUiModule()).with(
					new ZestFxModule());
		} else {
			return new ZestFxModule();
		}
	}

	/**
	 * Creates a {@link Graph} nested in the node represented by the given
	 * <i>contentNestingNode</i>.
	 *
	 * @param contentNestingNode
	 * @param nestedGraphContentProvider
	 * @param labelProvider
	 * @return
	 */
	protected Graph createNestedGraph(Object contentNestingNode,
			INestedGraphContentProvider nestedGraphContentProvider,
			ILabelProvider labelProvider) {
		Graph graph = createEmptyGraph();
		if (labelProvider instanceof INestedGraphLabelProvider) {
			INestedGraphLabelProvider nestedGraphLabelProvider = (INestedGraphLabelProvider) labelProvider;
			Map<String, Object> nestedGraphAttributes = nestedGraphLabelProvider
					.getNestedGraphAttributes(contentNestingNode);
			if (nestedGraphAttributes != null) {
				graph.getAttrs().putAll(nestedGraphAttributes);
			}
		}
		Object[] contentNodes = nestedGraphContentProvider
				.getChildren(contentNestingNode);
		createNodesAndEdges(nestedGraphContentProvider, labelProvider, graph,
				contentNodes);
		return graph;
	}

	/**
	 * Creates a {@link javafx.scene.Node} for the specified <i>contentNode</i>
	 * using the {@link IContentProvider} and {@link ILabelProvider}. Moreover,
	 * the new node is put into the given <i>contentToGraphMap</i>.
	 *
	 * @param contentToGraphMap
	 * @param contentNode
	 * @param graphContentProvider
	 * @param labelProvider
	 * @return
	 */
	protected Node createNode(Map<Object, Node> contentToGraphMap,
			Object contentNode, IGraphNodeContentProvider graphContentProvider,
			ILabelProvider labelProvider) {
		// do not create the same node twice
		if (contentToGraphMap.containsKey(contentNode)) {
			return contentToGraphMap.get(contentNode);
		}

		Node node = new Node();
		contentToGraphMap.put(contentNode, node);

		// label
		String label = labelProvider.getText(contentNode);
		ZestProperties.setLabel(node, label);

		// icon
		Image icon = labelProvider.getImage(contentNode);
		ZestProperties.setIcon(node,
				SWTFXUtils.toFXImage(icon.getImageData(), null));

		// tooltip
		if (labelProvider instanceof IToolTipProvider) {
			IToolTipProvider toolTipProvider = (IToolTipProvider) labelProvider;
			String toolTipText = toolTipProvider.getToolTipText(contentNode);
			ZestProperties.setTooltip(node, toolTipText);
		}

		String textCssStyle = null;

		// colors
		if (labelProvider instanceof IColorProvider) {
			IColorProvider colorProvider = (IColorProvider) labelProvider;
			Color foreground = colorProvider.getForeground(contentNode);
			Color background = colorProvider.getBackground(contentNode);
			ZestProperties.setNodeRectCssStyle(node, "-fx-fill: "
					+ toCssRgb(background) + "; -fx-stroke: "
					+ toCssRgb(foreground) + ";");
			textCssStyle = "-fx-fill: " + toCssRgb(foreground) + ";";
		}

		// font
		if (labelProvider instanceof IFontProvider) {
			IFontProvider fontProvider = (IFontProvider) labelProvider;
			Font font = fontProvider.getFont(contentNode);

			FontData[] fontData = font.getFontData();

			String name = fontData[0].getName();
			int size = fontData[0].getHeight();
			int style = fontData[0].getStyle();

			// TODO: support all SWT font styles
			boolean isBold = (style & SWT.BOLD) != 0;
			boolean isItalic = (style & SWT.ITALIC) != 0;

			if (textCssStyle == null) {
				textCssStyle = "";
			}
			textCssStyle = textCssStyle + "-fx-font-family: " + name + ";"
					+ "-fx-font-size: " + size + "pt;";
			if (isItalic) {
				textCssStyle = textCssStyle + "-fx-font-style: italic;";
			}
			if (isBold) {
				textCssStyle = textCssStyle + "-fx-font-weight: bold;";
			}
		}

		ZestProperties.setNodeTextCssStyle(node, textCssStyle);

		// custom attributes
		if (labelProvider instanceof IGraphNodeLabelProvider) {
			IGraphNodeLabelProvider graphNodeLabelProvider = (IGraphNodeLabelProvider) labelProvider;
			Map<String, Object> nodeAttributes = graphNodeLabelProvider
					.getNodeAttributes(contentNode);
			if (nodeAttributes != null) {
				node.getAttrs().putAll(nodeAttributes);
			}
		}

		// create nested graph (optional)
		if (graphContentProvider instanceof INestedGraphContentProvider) {
			INestedGraphContentProvider nestedGraphProvider = (INestedGraphContentProvider) graphContentProvider;
			if (nestedGraphProvider.hasChildren(contentNode)) {
				Graph graph = createNestedGraph(contentNode,
						nestedGraphProvider, labelProvider);
				graph.setNestingNode(node);
			}
		}

		return node;
	}

	/**
	 * Creates graph {@link Node nodes} and {@link Edge edges} from the given
	 * array of <i>contentNodes</i>.
	 *
	 * @param graphContentProvider
	 *            This viewer's {@link IContentProvider} for convenience.
	 * @param labelProvider
	 *            This viewer's {@link ILabelProvider} for convenience.
	 * @param graph
	 *            The {@link Graph} for which nodes and edges are created.
	 * @param graphNodeProvider
	 *            The
	 * @param contentNodes
	 * @param contentToNodeMap
	 */
	protected void createNodesAndEdges(
			IGraphNodeContentProvider graphContentProvider,
			ILabelProvider labelProvider, Graph graph, Object[] contentNodes) {
		// map content elements to created nodes so we can access them when
		// creating the edges
		Map<Object, Node> contentToNodeMap = new HashMap<Object, Node>();
		// create nodes
		for (Object node : contentNodes) {
			Node graphNode = createNode(contentToNodeMap, node,
					graphContentProvider, labelProvider);
			graph.getNodes().add(graphNode);
			graphNode.setGraph(graph);
		}
		// create edges
		for (Object contentSourceNode : contentNodes) {
			Node sourceNode = contentToNodeMap.get(contentSourceNode);
			for (Object contentTargetNode : graphContentProvider
					.getConnectedTo(contentSourceNode)) {
				Node targetNode = contentToNodeMap.get(contentTargetNode);
				Edge edge = createEdge(labelProvider, contentSourceNode,
						sourceNode, contentTargetNode, targetNode);
				graph.getEdges().add(edge);
				edge.setGraph(graph);
			}
		}
	}

	/**
	 * Creates the root {@link Graph} using the given {@link IContentProvider}
	 * and {@link ILabelProvider}.
	 *
	 * @param contentProvider
	 * @param labelProvider
	 * @return
	 */
	protected Graph createRootGraph(IContentProvider contentProvider,
			ILabelProvider labelProvider) {
		Graph graph = createEmptyGraph();
		if (labelProvider instanceof IGraphNodeLabelProvider) {
			IGraphNodeLabelProvider graphNodeLabelProvider = (IGraphNodeLabelProvider) labelProvider;
			Map<String, Object> rootGraphAttributes = graphNodeLabelProvider
					.getRootGraphAttributes();
			if (rootGraphAttributes != null) {
				graph.getAttrs().putAll(rootGraphAttributes);
			}
		}
		if (contentProvider instanceof IGraphNodeContentProvider) {
			IGraphNodeContentProvider graphNodeProvider = (IGraphNodeContentProvider) contentProvider;
			Object[] nodes = graphNodeProvider.getNodes();
			createNodesAndEdges(graphNodeProvider, labelProvider, graph, nodes);
		}
		return graph;
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
		return layoutAlgorithm;
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
				Collections.singletonList(createRootGraph(getContentProvider(),
						getLabelProvider())));
	}

	public void setLayoutAlgorithm(ILayoutAlgorithm layoutAlgorithm) {
		this.layoutAlgorithm = layoutAlgorithm;
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

	protected String toCssRgb(Color color) {
		return "rgb(" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + ")";
	}

}
