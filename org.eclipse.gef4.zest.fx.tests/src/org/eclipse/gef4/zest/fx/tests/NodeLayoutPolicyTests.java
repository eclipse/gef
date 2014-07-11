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
package org.eclipse.gef4.zest.fx.tests;

import static org.junit.Assert.assertEquals;
import javafx.scene.layout.Pane;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.PropertiesHelper;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.NodeLayoutPolicy;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.junit.Test;

public class NodeLayoutPolicyTests {

	@Test
	public void test_provide() {
		final Point location = new Point(10, 20);
		final Dimension size = new Dimension(120, 60);
		NodeLayoutPolicy policy = createPolicy(location, size);

		GraphNodeLayout nodeLayout = createNodeLayout();
		policy.provideLayoutInformation(nodeLayout);

		assertEquals(location, PropertiesHelper.getLocation(nodeLayout));
		assertEquals(size, PropertiesHelper.getSize(nodeLayout));
	}

	private GraphNodeLayout createNodeLayout() {
		Node node = new Node.Builder().build();
		Graph graph = new Graph.Builder().nodes(node).build();
		GraphLayoutContext glc = new GraphLayoutContext(graph);
		GraphNodeLayout nodeLayout = new GraphNodeLayout(glc, node);
		return nodeLayout;
	}

	private NodeLayoutPolicy createPolicy(final Point location,
			final Dimension size) {
		NodeLayoutPolicy policy = new NodeLayoutPolicy() {
			private IContentPart<javafx.scene.Node> host;
			
			@Override
			public IVisualPart<javafx.scene.Node> getHost() {
				if (host == null)
					host = new AbstractFXContentPart() {
						private Pane visual;

						{
							setAdapter(FXResizeRelocatePolicy.class, new FXResizeRelocatePolicy() {
								@Override
								public IUndoableOperation commit() {
									return null;
								}
							});
						}
						
						@Override
						public javafx.scene.Node getVisual() {
							if (visual == null) {
								visual = new Pane();
								visual.setLayoutX(location.x);
								visual.setLayoutY(location.y);
								visual.resize(size.width, size.height);
							}
							return visual;
						}

						@Override
						protected void doRefreshVisual() {
						}
					};
				return host;
			}
		};
		return policy;
	}

	@Test
	public void test_adapt() {
		NodeLayoutPolicy policy = createPolicy(new Point(), new Dimension());

		Point location = new Point(1, 5);
		Dimension size = new Dimension(100, 200);
		GraphNodeLayout nodeLayout = createNodeLayout();
		PropertiesHelper.setLocation(nodeLayout, location.x, location.y);
		PropertiesHelper.setSize(nodeLayout, size.getWidth(), size.getHeight());

		policy.adaptLayoutInformation(nodeLayout);
		
		javafx.scene.Node visual = policy.getHost().getVisual();
		assertEquals(location,
				new Point(visual.getLayoutX(), visual.getLayoutY()));
		assertEquals(size, new Dimension(visual.getLayoutBounds().getWidth(),
				visual.getLayoutBounds().getHeight()));
	}

}
