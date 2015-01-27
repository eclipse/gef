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
import javafx.scene.transform.Affine;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.operations.FXResizeNodeOperation;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.parts.FXTransformProvider;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.policies.NodeLayoutPolicy;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class NodeLayoutPolicyTests {

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
			private IContentPart<javafx.scene.Node, ? extends javafx.scene.Node> host;

			@Override
			public IVisualPart<javafx.scene.Node, ? extends javafx.scene.Node> getHost() {
				if (host == null) {
					host = new AbstractFXContentPart<Pane>() {
						{
							setAdapter(
									AdapterKey
											.get(FXResizeRelocatePolicy.class),
									new FXResizeRelocatePolicy() {
										@Override
										public org.eclipse.core.commands.operations.IUndoableOperation commit() {
											return null;
										}
									});
							setAdapter(AdapterKey.get(FXResizePolicy.class),
									new FXResizePolicy() {
										@Override
										public void init() {
											resizeOperation = new FXResizeNodeOperation(
													getHost().getVisual());
											forwardUndoOperation = new ForwardUndoCompositeOperation(
													"Resize");
											forwardUndoOperation
													.add(resizeOperation);
										}
									});
							FXTransformProvider transformProvider = new FXTransformProvider();
							setAdapter(
									AdapterKey
											.get(new TypeToken<Provider<Affine>>() {
											},
													FXTransformPolicy.TRANSFORMATION_PROVIDER_ROLE),
									transformProvider);
							setAdapter(AdapterKey.get(FXTransformPolicy.class),
									new FXTransformPolicy());
							Affine affine = transformProvider.get();
							affine.setTx(location.x);
							affine.setTy(location.y);
						}

						@Override
						protected Pane createVisual() {
							Pane visual = new Pane();
							visual.resize(size.width, size.height);
							return visual;
						}

						@Override
						protected void doRefreshVisual(Pane visual) {
							// nothing to do
						};
					};
					FXRootPart rootPart = new FXRootPart();
					rootPart.setAdaptable(new FXViewer());
					host.setParent(rootPart);
				}
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
		LayoutProperties.setLocation(nodeLayout, location.x, location.y);
		LayoutProperties.setSize(nodeLayout, size.getWidth(), size.getHeight());

		policy.adaptLayoutInformation(nodeLayout);

		javafx.scene.Node visual = policy.getHost().getVisual();
		/*
		 * <i>location</i> is the center, <i>translate-xy</i> is the top left
		 * corner, therefore we expect <code>translate-xy = location - size /
		 * 2</code>.
		 */
		Affine affine = policy.getHost().getAdapter(FXTransformPolicy.class)
				.getNodeTransform();
		assertEquals(location.getTranslated(size.getScaled(-0.5)), new Point(
				affine.getTx(), affine.getTy()));
		assertEquals(size, new Dimension(visual.getLayoutBounds().getWidth(),
				visual.getLayoutBounds().getHeight()));
	}

	@Test
	public void test_provide() {
		final Point location = new Point(10, 20);
		final Dimension size = new Dimension(120, 60);
		NodeLayoutPolicy policy = createPolicy(location, size);

		GraphNodeLayout nodeLayout = createNodeLayout();
		policy.provideLayoutInformation(nodeLayout);

		assertEquals(location, LayoutProperties.getLocation(nodeLayout));
		assertEquals(size, LayoutProperties.getSize(nodeLayout));
	}

}
