/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - minor refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.dot.examples;

import java.io.File;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.point.PointFactory;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.GraphCopier;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.application.Application;

/**
 * An example to demonstrate how the native dot executable can be used for
 * auto-layout in the context of Zest. It requires that dotExecutablePath points
 * to a valid native dot executable.
 *
 * @author anyssen
 */
public class DotLayoutExample extends AbstractZestExample {

	public class DotNativeLayout implements ILayoutAlgorithm {

		private String dotExecutablePath = "/usr/local/bin/dot";

		@Override
		public void applyLayout(LayoutContext layoutContext, boolean clean) {
			// Convert a Graph with LayoutAttributes (input model to
			// ILayoutAlgorithm) to a Graph with DotAttributes, which can be
			// exported to a DOT string; transfer node names to be able to
			// retrieve the results
			Graph dotGraph = new GraphCopier(new IAttributeCopier() {

				@Override
				public void copy(IAttributeStore source,
						IAttributeStore target) {
					if (source instanceof Node && target instanceof Node) {
						// convert LayoutProperties#location to
						// DotAttributes#pos
						Point location = LayoutProperties
								.getLocation((Node) source);
						org.eclipse.gef.dot.internal.language.point.Point posParsed = PointFactory.eINSTANCE
								.createPoint();
						posParsed.setX(location.x);
						posParsed.setY(location.y);
						DotAttributes.setPosParsed((Node) target, posParsed);

						// transfer name for identification purpose
						DotAttributes._setName((Node) target,
								(String) ((Node) source).attributesProperty()
										.get(LABEL));
					}
				}
			}).copy(layoutContext.getGraph());

			// set graph type and DOT layout algorithm
			DotAttributes._setType(dotGraph, GraphType.DIGRAPH);
			DotAttributes.setLayoutParsed(dotGraph, Layout.CIRCO);

			// export the Graph with DotAttributs to a DOT string and call the
			// dot executable to add layout info to it
			File tmpFile = DotFileUtils
					.write(new DotExport().exportDot(dotGraph));
			String[] dotResult = DotExecutableUtils.executeDot(
					new File(dotExecutablePath), true, tmpFile, null, null);
			if (!dotResult[1].isEmpty()) {
				System.err.println(dotResult[1]);
			}
			tmpFile.delete();
			Graph layoutedDotGraph = new DotImport().importDot(dotResult[0])
					.get(0);

			// transfer the DOT provided position information back to the input
			// Graph
			for (Node source : layoutedDotGraph.getNodes()) {
				String sourceName = DotAttributes._getName(source);
				for (Node target : layoutContext.getGraph().getNodes()) {
					if (target.getAttributes().get(LABEL).equals(sourceName)) {
						// transfer back (layouted location information)
						org.eclipse.gef.dot.internal.language.point.Point posParsed = DotAttributes
								.getPosParsed(source);
						LayoutProperties.setLocation(target,
								new Point(posParsed.getX(), posParsed.getY()));
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public DotLayoutExample() {
		super("DOT Native Layout");
	}

	@Override
	protected Graph createGraph() {
		String paper = "Paper";
		String rock = "Rock";
		String scissors = "Scissors";
		String spock = "Spock";
		String lizard = "Lizard";
		return new Graph.Builder()
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, new DotNativeLayout())
				.node(paper).attr(LABEL, paper) //
				.node(rock).attr(LABEL, rock) //
				.node(scissors).attr(LABEL, scissors) //
				.node(spock).attr(LABEL, spock) //
				.node(lizard).attr(LABEL, lizard) //
				.edge(paper, spock) //
				.edge(paper, rock) //
				.edge(rock, scissors) //
				.edge(rock, lizard) //
				.edge(scissors, paper) //
				.edge(scissors, lizard) //
				.edge(spock, scissors) //
				.edge(spock, rock) //
				.edge(lizard, spock) //
				.edge(lizard, paper) //
				.build();
	}
}
