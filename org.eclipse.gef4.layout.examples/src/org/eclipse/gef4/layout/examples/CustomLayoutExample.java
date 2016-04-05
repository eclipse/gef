/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.CustomLayoutExample
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.LayoutContext;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestProperties;

import javafx.application.Application;

/**
 * This snippet shows how to create a custom layout. All the work is done in the
 * applyLayoutInternal Method.
 *
 * @author irbull
 * @authoer anyssen
 *
 */
public class CustomLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public CustomLayoutExample() {
		super("GEF4 Layouts - Custom Layout Example");
	}

	@Override
	protected Graph createGraph() {
		// create nodes
		org.eclipse.gef4.graph.Node[] nodes = new org.eclipse.gef4.graph.Node[] {
				n(LABEL, "Paper"), n(LABEL, "Rock"), n(LABEL, "Scissors"), };

		// create edges
		org.eclipse.gef4.graph.Edge[] edges = new org.eclipse.gef4.graph.Edge[] {
				e(nodes[0], nodes[1]), e(nodes[1], nodes[2]) };

		return new Graph.Builder().nodes(nodes).edges(edges)
				.attr(ZestProperties.GRAPH_LAYOUT_ALGORITHM,
						createLayoutAlgorithm())
				.build();
	}

	private ILayoutAlgorithm createLayoutAlgorithm() {
		ILayoutAlgorithm layoutAlgorithm = new ILayoutAlgorithm() {
			private LayoutContext context;

			@Override
			public void applyLayout(boolean clean) {
				Node[] entitiesToLayout = context.getNodes();
				int totalSteps = entitiesToLayout.length;
				double distance = LayoutProperties.getBounds(context.getGraph())
						.getWidth() / totalSteps;
				int xLocation = 0;

				for (int currentStep = 0; currentStep < entitiesToLayout.length; currentStep++) {
					Node layoutEntity = entitiesToLayout[currentStep];
					LayoutProperties.setLocation(layoutEntity, xLocation,
							/*
							 * LayoutProperties.getLocation( layoutEntity).y
							 */0);
					xLocation += distance;
				}
			}

			@Override
			public LayoutContext getLayoutContext() {
				return context;
			}

			@Override
			public void setLayoutContext(LayoutContext context) {
				this.context = context;
			}
		};
		return layoutAlgorithm;
	}

}
