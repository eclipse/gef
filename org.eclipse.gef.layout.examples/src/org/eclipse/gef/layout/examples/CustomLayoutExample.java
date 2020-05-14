/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.zest.examples.layout.CustomLayoutExample
 *
 *******************************************************************************/
package org.eclipse.gef.layout.examples;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

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
		super("GEF Layouts - Custom Layout Example");
	}

	@Override
	protected Graph createGraph() {
		// create nodes
		org.eclipse.gef.graph.Node[] nodes = new org.eclipse.gef.graph.Node[] {
				n(LABEL, "Paper"), n(LABEL, "Rock"), n(LABEL, "Scissors"), };

		// create edges
		org.eclipse.gef.graph.Edge[] edges = new org.eclipse.gef.graph.Edge[] {
				e(nodes[0], nodes[1]), e(nodes[1], nodes[2]) };

		return new Graph.Builder().nodes(nodes).edges(edges)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G,
						createLayoutAlgorithm())
				.build();
	}

	private ILayoutAlgorithm createLayoutAlgorithm() {
		ILayoutAlgorithm layoutAlgorithm = new ILayoutAlgorithm() {

			@Override
			public void applyLayout(LayoutContext context, boolean clean) {
				Node[] entitiesToLayout = context.getNodes();
				int totalSteps = entitiesToLayout.length;
				double distance = LayoutProperties.getBounds(context.getGraph())
						.getWidth() / totalSteps;
				int xLocation = 0;

				for (int currentStep = 0; currentStep < entitiesToLayout.length; currentStep++) {
					Node layoutEntity = entitiesToLayout[currentStep];
					LayoutProperties.setLocation(layoutEntity, new Point(
							xLocation,
							/*
							 * LayoutProperties.getLocation( layoutEntity).y
							 */0));
					xLocation += distance;
				}
			}
		};
		return layoutAlgorithm;
	}

}
