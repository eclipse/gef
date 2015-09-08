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
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.fx.ui.canvas.FXCanvasEx;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.ui.parts.SelectionForwarder;
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
import org.eclipse.ui.PlatformUI;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import javafx.embed.swt.FXCanvas;
import javafx.embed.swt.SWTFXUtils;
import javafx.scene.Scene;

/**
 * The {@link ZestContentViewer} is a {@link ContentViewer} that is capable of
 * displaying {@link Graph}s.
 *
 * @author mwienand
 *
 */
public class ZestContentViewer extends ContentViewer {

	private Module module;
	private FXCanvas canvas;
	private FXDomain domain;
	private FXViewer viewer;
	private ISelection selection;
	private SelectionForwarder<javafx.scene.Node> selectionForwarder;
	private ILayoutAlgorithm layoutAlgorithm;
	private Map<Object, Node> contentNodeMap = new IdentityHashMap<Object, Node>();

	/**
	 * Constructs a new {@link ZestContentViewer}. The {@link #createModule()}
	 * method is evaluated to retrieve the {@link Module} that is later used to
	 * create the {@link Injector} that is later used for the injection of
	 * members and the construction of the {@link FXDomain}.
	 */
	public ZestContentViewer() {
		this.module = createModule();
	}

	/**
	 * Constructs a new {@link ZestContentViewer}. The given {@link Module} is
	 * saved so that it can be later used to create an {@link Injector} that is
	 * later used for the injection of members and the construction of the
	 * {@link FXDomain}.
	 *
	 * @param module
	 *            The {@link Module} from which an {@link Injector} is created
	 *            later.
	 */
	public ZestContentViewer(Module module) {
		this.module = module;
	}

	/**
	 * Creates an {@link FXCanvas} inside of the given <i>parent</i>
	 * {@link Composite}. The {@link FXCanvas} serves as the container for the
	 * JavaFX {@link Scene} which renders the contents.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @return An {@link FXCanvas} inside of the given <i>parent</i>.
	 */
	protected FXCanvas createCanvas(final Composite parent) {
		// TODO: expect SWT style
		// TODO: inject canvasFactory
		// return canvasFactory.createCanvas(parent);
		// What about SWT style?
		return new FXCanvasEx(parent, SWT.NONE);
	}

	/**
	 * Creates the control for this {@link ZestContentViewer} inside of the
	 * given <i>parent</i> {@link Composite}.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @param style
	 *            The SWT style for this {@link ZestContentViewer}, currently
	 *            not used.
	 */
	public void createControl(Composite parent, int style) {
		// create injector
		Injector injector = Guice.createInjector(module);
		injector.injectMembers(this);

		// create viewer and canvas only after toolkit has been initialized
		// TODO: pass in SWT style / with factory possible?
		canvas = createCanvas(parent);

		// inject domain
		domain = injector.getInstance(FXDomain.class);

		// hook viewer
		viewer = domain.getAdapter(IViewer.class);
		canvas.setScene(new Scene(viewer.getScrollPane()));

		// activate domain
		domain.activate();

		// register listener to provide selection to workbench
		selectionForwarder = new SelectionForwarder<javafx.scene.Node>(this, viewer);
	}

	/**
	 * Constructs and returns a new {@link Edge} connecting the given
	 * <i>sourceNode</i> and <i>targetNode</i>. If the <i>labelProvider</i>
	 * implements {@link IGraphNodeLabelProvider}, then attributes for the edge
	 * are determined using the
	 * {@link IGraphNodeLabelProvider#getEdgeAttributes(Object, Object)} methods
	 * and inserted into the edge.
	 *
	 * @param labelProvider
	 *            This viewer's {@link ILabelProvider} for convenience.
	 * @param contentSourceNode
	 *            The content element representing the source node of this edge.
	 * @param sourceNode
	 *            The already created source {@link Node} of this edge.
	 * @param contentTargetNode
	 *            The content element representing the target node of this edge.
	 * @param targetNode
	 *            The already created target {@link Node} of this edge.
	 * @return The new {@link Edge}.
	 */
	protected Edge createEdge(ILabelProvider labelProvider, Object contentSourceNode, Node sourceNode,
			Object contentTargetNode, Node targetNode) {
		Edge edge = new Edge(sourceNode, targetNode);
		if (labelProvider instanceof IEdgeDecorationProvider) {
			IEdgeDecorationProvider edgeDecorationProvider = (IEdgeDecorationProvider) labelProvider;
			IFXDecoration sourceDecoration = edgeDecorationProvider.getSourceDecoration(contentSourceNode,
					contentTargetNode);
			if (sourceDecoration != null) {
				ZestProperties.setSourceDecoration(edge, sourceDecoration);
			}
			IFXDecoration targetDecoration = edgeDecorationProvider.getTargetDecoration(contentSourceNode,
					contentTargetNode);
			if (targetDecoration != null) {
				ZestProperties.setTargetDecoration(edge, targetDecoration);
			}
		}
		if (labelProvider instanceof IGraphNodeLabelProvider) {
			IGraphNodeLabelProvider graphNodeLabelProvider = (IGraphNodeLabelProvider) labelProvider;
			Map<String, Object> edgeAttributes = graphNodeLabelProvider.getEdgeAttributes(contentSourceNode,
					contentTargetNode);
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

	/**
	 * Creates the {@link Module} that is used by default for the construction
	 * of an {@link Injector} that will be used for the injection of members
	 * into this {@link ZestContentViewer}.
	 *
	 * @return The {@link Module} that is used by default for the construction
	 *         of an {@link Injector} that will be used for the injection of
	 *         members into this {@link ZestContentViewer}.
	 */
	protected Module createModule() {
		if (PlatformUI.isWorkbenchRunning()) {
			return Modules.override(new ZestFxUiModule()).with(new ZestFxModule());
		} else {
			return new ZestFxModule();
		}
	}

	/**
	 * Creates a {@link Graph} nested in the node represented by the given
	 * <i>contentNestingNode</i>.
	 *
	 * @param contentNestingNode
	 *            The content {@link Object} that represents the nesting node.
	 * @param nestedGraphContentProvider
	 *            This viewer's {@link INestedGraphContentProvider} for
	 *            convenience.
	 * @param labelProvider
	 *            This viewer's {@link ILabelProvider} for convenience.
	 * @return The new {@link Graph}.
	 */
	protected Graph createNestedGraph(Object contentNestingNode, INestedGraphContentProvider nestedGraphContentProvider,
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
		Object[] contentNodes = nestedGraphContentProvider.getChildren(contentNestingNode);
		if (contentNodes != null) {
			createNodesAndEdges(nestedGraphContentProvider, labelProvider, graph, contentNodes);
		}
		return graph;
	}

	/**
	 * Creates a {@link Node} for the specified <i>contentNode</i> using the
	 * {@link IContentProvider} and {@link ILabelProvider}. Moreover, the new
	 * node is put into the given <i>contentToGraphMap</i>.
	 *
	 * @param contentNode
	 *            The content {@link Object} that represents the node.
	 * @param graphContentProvider
	 *            This viewer's {@link IGraphNodeContentProvider} for
	 *            convenience.
	 * @param labelProvider
	 *            This viewer's {@link ILabelProvider} for convenience.
	 * @return The new {@link Node}.
	 */
	protected Node createNode(Object contentNode, IGraphNodeContentProvider graphContentProvider,
			ILabelProvider labelProvider) {
		// do not create the same node twice
		if (contentNodeMap.containsKey(contentNode)) {
			throw new IllegalStateException("A node for content <" + contentNode + "> has already been created.");
		}

		Node node = new Node();
		contentNodeMap.put(contentNode, node);

		// label
		String label = labelProvider.getText(contentNode);
		if (label != null) {
			ZestProperties.setLabel(node, label);
		}

		// icon
		Image icon = labelProvider.getImage(contentNode);
		if (icon != null) {
			ZestProperties.setIcon(node, SWTFXUtils.toFXImage(icon.getImageData(), null));
		}

		// tooltip
		if (labelProvider instanceof IToolTipProvider) {
			IToolTipProvider toolTipProvider = (IToolTipProvider) labelProvider;
			String toolTipText = toolTipProvider.getToolTipText(contentNode);
			if (toolTipText != null) {
				ZestProperties.setTooltip(node, toolTipText);
			}
		}

		String textCssStyle = "";

		// colors
		if (labelProvider instanceof IColorProvider) {
			IColorProvider colorProvider = (IColorProvider) labelProvider;
			Color foreground = colorProvider.getForeground(contentNode);
			Color background = colorProvider.getBackground(contentNode);
			String rectCssStyle = "";
			if (background != null) {
				rectCssStyle = rectCssStyle + "-fx-fill: " + toCssRgb(background) + ";";
			}
			if (foreground != null) {
				rectCssStyle = rectCssStyle + "-fx-stroke: " + toCssRgb(foreground) + ";";
				textCssStyle = "-fx-fill: " + toCssRgb(foreground) + ";";
			}
			if (!rectCssStyle.isEmpty()) {
				ZestProperties.setNodeRectCssStyle(node, rectCssStyle);
			}
		}

		// font
		if (labelProvider instanceof IFontProvider) {
			IFontProvider fontProvider = (IFontProvider) labelProvider;
			Font font = fontProvider.getFont(contentNode);
			FontData[] fontData = font == null ? null : font.getFontData();
			if (fontData != null && fontData.length > 0 && fontData[0] != null) {
				String name = fontData[0].getName();
				int size = fontData[0].getHeight();
				int style = fontData[0].getStyle();

				// TODO: support all SWT font styles
				boolean isBold = (style & SWT.BOLD) != 0;
				boolean isItalic = (style & SWT.ITALIC) != 0;

				textCssStyle = textCssStyle + "-fx-font-family: \"" + name + "\";" + "-fx-font-size: " + size + "pt;";
				if (isItalic) {
					textCssStyle = textCssStyle + "-fx-font-style: italic;";
				}
				if (isBold) {
					textCssStyle = textCssStyle + "-fx-font-weight: bold;";
				}
			}
		}

		ZestProperties.setNodeTextCssStyle(node, textCssStyle);

		// custom attributes
		if (labelProvider instanceof IGraphNodeLabelProvider) {
			IGraphNodeLabelProvider graphNodeLabelProvider = (IGraphNodeLabelProvider) labelProvider;
			Map<String, Object> nodeAttributes = graphNodeLabelProvider.getNodeAttributes(contentNode);
			if (nodeAttributes != null) {
				node.getAttrs().putAll(nodeAttributes);
			}
		}

		// create nested graph (optional)
		if (graphContentProvider instanceof INestedGraphContentProvider) {
			INestedGraphContentProvider nestedGraphProvider = (INestedGraphContentProvider) graphContentProvider;
			if (nestedGraphProvider.hasChildren(contentNode)) {
				Graph graph = createNestedGraph(contentNode, nestedGraphProvider, labelProvider);
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
	 *            This viewer's {@link IGraphNodeContentProvider} for
	 *            convenience.
	 * @param labelProvider
	 *            This viewer's {@link ILabelProvider} for convenience.
	 * @param graph
	 *            The {@link Graph} for which nodes and edges are created.
	 * @param contentNodes
	 *            Content elements which represent nodes that are to be created
	 *            together with the edges between them.
	 */
	protected void createNodesAndEdges(IGraphNodeContentProvider graphContentProvider, ILabelProvider labelProvider,
			Graph graph, Object[] contentNodes) {
		// create nodes
		for (Object node : contentNodes) {
			Node graphNode = createNode(node, graphContentProvider, labelProvider);
			graph.getNodes().add(graphNode);
			graphNode.setGraph(graph);
		}
		// create edges
		for (Object contentSourceNode : contentNodes) {
			Node sourceNode = contentNodeMap.get(contentSourceNode);
			Object[] connectedTo = graphContentProvider.getConnectedTo(contentSourceNode);
			if (connectedTo != null) {
				for (Object contentTargetNode : connectedTo) {
					Node targetNode = contentNodeMap.get(contentTargetNode);
					Edge edge = createEdge(labelProvider, contentSourceNode, sourceNode, contentTargetNode, targetNode);
					graph.getEdges().add(edge);
					edge.setGraph(graph);
				}
			}
		}
	}

	/**
	 * Creates a complete {@link Graph} using the given {@link IContentProvider}
	 * and {@link ILabelProvider}.
	 *
	 * @param contentProvider
	 *            The {@link IContentProvider} for this viewer.
	 * @param labelProvider
	 *            The {@link ILabelProvider} for this viewer.
	 * @return A complete {@link Graph} constructed by using the given
	 *         providers.
	 */
	protected Graph createRootGraph(IContentProvider contentProvider, ILabelProvider labelProvider) {
		Graph graph = createEmptyGraph();
		if (labelProvider instanceof IGraphNodeLabelProvider) {
			IGraphNodeLabelProvider graphNodeLabelProvider = (IGraphNodeLabelProvider) labelProvider;
			Map<String, Object> rootGraphAttributes = graphNodeLabelProvider.getRootGraphAttributes();
			if (rootGraphAttributes != null) {
				graph.getAttrs().putAll(rootGraphAttributes);
			}
		}
		if (contentProvider instanceof IGraphNodeContentProvider) {
			IGraphNodeContentProvider graphNodeProvider = (IGraphNodeContentProvider) contentProvider;
			Object[] nodes = graphNodeProvider.getNodes();
			if (nodes != null) {
				createNodesAndEdges(graphNodeProvider, labelProvider, graph, nodes);
			}
		}
		return graph;
	}

	/**
	 * Returns an unmodifiable view of the content-node-map.
	 *
	 * @return An unmodifiable view of the content-node-map.
	 */
	public Map<Object, Node> getContentNodeMap() {
		return Collections.unmodifiableMap(contentNodeMap);
	}

	@Override
	public FXCanvas getControl() {
		return canvas;
	}

	/**
	 * Returns the {@link FXViewer} that displays the contents.
	 *
	 * @return The {@link FXViewer} that displays the contents.
	 */
	public FXViewer getFXViewer() {
		return viewer;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		return (ILabelProvider) super.getLabelProvider();
	}

	/**
	 * Returns the {@link ILayoutAlgorithm} that is used for laying out the
	 * contents.
	 *
	 * @return The {@link ILayoutAlgorithm} that is used for laying out the
	 *         contents.
	 */
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
		contentNodeMap.clear();
		viewer.getAdapter(ContentModel.class)
				.setContents(Collections.singletonList(createRootGraph(getContentProvider(), getLabelProvider())));
	}

	/**
	 * Changes the {@link ILayoutAlgorithm} that is used for laying out the
	 * contents to the given value.
	 *
	 * @param layoutAlgorithm
	 *            The new {@link ILayoutAlgorithm} to use.
	 */
	public void setLayoutAlgorithm(ILayoutAlgorithm layoutAlgorithm) {
		this.layoutAlgorithm = layoutAlgorithm;
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		if (this.selection != selection && (this.selection == null || !this.selection.equals(selection))) {
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

	/**
	 * Converts the given {@link Color} into a CSS string:
	 * <code>"rgb(red,green,blue)"</code>.
	 *
	 * @param color
	 *            The {@link Color} to convert.
	 * @return The corresponding CSS string.
	 */
	protected String toCssRgb(Color color) {
		return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}

}
