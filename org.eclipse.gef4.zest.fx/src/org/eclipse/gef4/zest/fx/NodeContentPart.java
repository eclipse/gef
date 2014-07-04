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
package org.eclipse.gef4.zest.fx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXLabeledNode;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;

public class NodeContentPart extends AbstractFXContentPart {

	public static final String CSS_CLASS = "node";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_ID = "id";

	protected GraphNodeLayout nodeLayout;
	protected IFXAnchor anchor;

	protected ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds bounds) {
			if (!isAdaptLayout && nodeLayout != null) {
				nodeLayout.setSize(visual.getBoxWidth(), visual.getBoxHeight());
			}
		}
	};

	private org.eclipse.gef4.graph.Node node; // FIXME: same as 'content'
	private FXLabeledNode visual = new FXLabeledNode();
	private boolean isAdaptLayout;

	{
		visual.layoutBoundsProperty().addListener(boundsChangeListener);
		visual.getStyleClass().add(CSS_CLASS);
	}

	private Runnable adaptLayout = new Runnable() {
		@Override
		public void run() {
			adaptLayout();
		}
	};

	private PropertyChangeListener layoutContextListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ILayoutModel.LAYOUT_CONTEXT_PROPERTY.equals(evt
					.getPropertyName())) {
				// remove old flush changes listener
				Object old = evt.getOldValue();
				if (old instanceof GraphLayoutContext) {
					((GraphLayoutContext) old)
							.removeOnFlushChanges(adaptLayout);
				}

				GraphLayoutContext layoutContext = (GraphLayoutContext) getViewer()
						.getDomain().getAdapter(ILayoutModel.class)
						.getLayoutContext();
				if (layoutContext != null) {
					// provide layout information
					initNodeLayout(layoutContext);
					// register flush changes listener
					layoutContext.addOnFlushChanges(adaptLayout);
				}
			}
		}
	};

	public NodeContentPart(org.eclipse.gef4.graph.Node content) {
		node = content;
		Map<String, Object> attrs = node.getAttrs();
		if (attrs.containsKey(ATTR_CLASS)) {
			visual.getStyleClass().add((String) attrs.get(ATTR_CLASS));
		}
		if (attrs.containsKey(ATTR_ID)) {
			visual.setId((String) attrs.get(ATTR_ID));
		}

		// interaction policies
		setAdapter(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
				new FXRelocateOnDragPolicy());

		// transaction policies
		setAdapter(FXResizeRelocatePolicy.class, new FXResizeRelocatePolicy());
	}

	@Override
	public void activate() {
		super.activate();
		getViewer().getDomain().getAdapter(ILayoutModel.class)
				.addPropertyChangeListener(layoutContextListener);
	}

	public void adaptLayout() {
		isAdaptLayout = true;
		visual.setLayoutX(nodeLayout.getLocation().x);
		visual.setLayoutY(nodeLayout.getLocation().y);
		visual.setBoxWidth(nodeLayout.getSize().width);
		visual.setBoxHeight(nodeLayout.getSize().height);
		isAdaptLayout = false;
	}

	@Override
	public void doRefreshVisual() {
		Object label = node.getAttrs().get(Attr.Key.LABEL.toString());
		String str = label instanceof String ? (String) label
				: label == null ? "-" : label.toString();
		visual.setLabel(str);
	}

	@Override
	public IFXAnchor getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			// TODO: when to dispose the anchor properly??
			anchor = new FXChopBoxAnchor(getVisual());
		}
		return anchor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> getContentAnchored() {
		if (getParent() != null) {
			Object content = getViewer().getContentModel().getContents().get(0);
			if (!(content instanceof Graph)) {
				throw new IllegalStateException(
						"Wrong content! Expected <Graph> but got <"
								+ content.getClass() + ">.");
			}
			List<Edge> edges = ((Graph) content).getEdges();
			List<Edge> anchored = new ArrayList<Edge>();
			for (Edge e : edges) {
				if (e.getTarget() == node || e.getSource() == node) {
					anchored.add(e);
				}
			}
			return (List) anchored;
		}
		return super.getContentAnchored();
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	protected void initNodeLayout(GraphLayoutContext layoutContext) {
		nodeLayout = layoutContext.getNodeLayout(node);
		nodeLayout.setLocation(visual.getLayoutX(), visual.getLayoutY());
		nodeLayout.setSize(visual.getBoxWidth(), visual.getBoxHeight());
	}

}
