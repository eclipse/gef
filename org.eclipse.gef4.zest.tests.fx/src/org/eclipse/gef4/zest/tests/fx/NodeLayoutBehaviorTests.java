/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.tests.fx;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.providers.FXTransformProvider;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.tests.fx.rules.FXApplicationThreadRule;
import org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.junit.Rule;
import org.junit.Test;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.transform.Affine;

public class NodeLayoutBehaviorTests {

	/**
	 * Ensure all tests are executed on the JavaFX application thread (and the
	 * JavaFX toolkit is properly initialized).
	 */
	@Rule
	public FXApplicationThreadRule fxApplicationThreadRule = new FXApplicationThreadRule();

	private GraphNodeLayout createNodeLayout() {
		Node node = new Node.Builder().buildNode();
		Graph graph = new Graph.Builder().nodes(node).build();
		GraphLayoutContext glc = new GraphLayoutContext(graph);
		GraphNodeLayout nodeLayout = new GraphNodeLayout(glc, node);
		return nodeLayout;
	}

	private NodeLayoutBehavior createNodeLayoutBehavior(final Point location, final Dimension size,
			final GraphNodeLayout pNodeLayout) {
		NodeLayoutBehavior behavior = new NodeLayoutBehavior() {
			private NodeContentPart host;

			@Override
			public NodeContentPart getHost() {
				if (host == null) {
					host = new NodeContentPart() {
						{
							setAdapter(new FXResizePolicy());
							FXTransformProvider transformProvider = new FXTransformProvider();
							setAdapter(transformProvider, FXTransformPolicy.TRANSFORM_PROVIDER_KEY.getRole());
							setAdapter(new FXTransformPolicy());
							Affine affine = transformProvider.get();
							affine.setTx(location.x);
							affine.setTy(location.y);
						}

						@Override
						protected Group createVisual() {
							Group visual = super.createVisual();
							if (size != null) {
								// ensure we are resizable
								getNestedChildrenPane().setPrefWidth(10);
								visual.resize(size.width, size.height);
							}
							return visual;
						}

						@Override
						public Node getContent() {
							return new Node();
						}
					};
					FXRootPart rootPart = new FXRootPart();
					FXViewer viewer = new FXViewer();
					viewer.setAdapter(rootPart);
					host.setParent(rootPart);
				}
				return host;
			}

			@Override
			protected GraphNodeLayout getNodeLayout() {
				return pNodeLayout;
			}
		};
		return behavior;
	}

	@Test
	public void test_adapt() {
		GraphNodeLayout nodeLayout = createNodeLayout();
		NodeLayoutBehavior behavior = createNodeLayoutBehavior(new Point(), null, nodeLayout);

		Point location = new Point(1, 5);
		Dimension size = new Dimension(100, 200);
		LayoutProperties.setLocation(nodeLayout, location.x, location.y);
		LayoutProperties.setSize(nodeLayout, size.getWidth(), size.getHeight());

		behavior.adaptLayoutInformation();

		/*
		 * <i>location</i> is the center, <i>translate-xy</i> is the top left
		 * corner, therefore we expect <code>translate-xy = location - size /
		 * 2</code>.
		 */
		Affine affine = Geometry2FX
				.toFXAffine(behavior.getHost().getAdapter(FXTransformPolicy.class).getCurrentNodeTransform());
		// FIXME: as size is not set (in case there are no child nodes), this
		// seems to be invalid
		assertEquals(location.getTranslated(size.getScaled(-0.5)), new Point(affine.getTx(), affine.getTy()));
		// TODO: fixme assertEquals(size, new
		// Dimension(visual.getLayoutBounds().getWidth(),
		// visual.getLayoutBounds().getHeight()));
	}

	@Test
	public void test_provide() {
		final Point location = new Point(10, 20);

		// setup with non-resizable figure
		GraphNodeLayout nodeLayout = createNodeLayout();
		NodeLayoutBehavior behavior = createNodeLayoutBehavior(location, null, nodeLayout);
		Group visual = behavior.getHost().getVisual();

		behavior.provideLayoutInformation();

		assertEquals(visual.isResizable(), LayoutProperties.isResizable(nodeLayout));

		// TODO: check whether this is correct
		Bounds layoutBounds = visual.getLayoutBounds();
		assertEquals(location,
				LayoutProperties.getLocation(nodeLayout).translate(-layoutBounds.getMinX(), -layoutBounds.getMinY()));
		assertEquals(new Dimension(layoutBounds.getWidth(), layoutBounds.getHeight()),
				LayoutProperties.getSize(nodeLayout));

		// TODO: test with resizable figure as well
	}

}
