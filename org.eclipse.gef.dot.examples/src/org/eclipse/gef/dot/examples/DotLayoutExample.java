/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.examples;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.parser.layout.Layout;
import org.eclipse.gef.dot.internal.parser.point.PointFactory;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
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

		private LayoutContext context;

		@Override
		public void applyLayout(boolean clean) {
			// convert a Graph with LayoutAttributes (input model to
			// ILayoutAlgorithm) to a Graph with DotAttributes, which can be
			// exported to a DOT string; keep track of converted nodes (as a
			// side-effect)
			final Map<Node, String> nodesToNameMap = new HashMap<>();
			IAttributeCopier layout2DotAttributesConverter = new IAttributeCopier() {

				int nodeIndex = 0;

				@Override
				public void copy(IAttributeStore source,
						IAttributeStore target) {
					if (source instanceof Node && target instanceof Node) {
						// convert LayoutProperties#location to
						// DotAttributes#pos
						Point location = LayoutProperties
								.getLocation((Node) source);
						org.eclipse.gef.dot.internal.parser.point.Point posParsed = PointFactory.eINSTANCE
								.createPoint();
						posParsed.setX(location.x);
						posParsed.setY(location.y);
						DotAttributes.setPosParsed((Node) target, posParsed);

						DotAttributes._setName((Node) target,
								Integer.toString(++nodeIndex));
						nodesToNameMap.put((Node) source,
								DotAttributes._getName((Node) target));
					}
				}
			};
			Graph dotGraph = new GraphCopier(layout2DotAttributesConverter)
					.copy(context.getGraph());

			// set graph type
			DotAttributes._setType(dotGraph, DotAttributes._TYPE__G__DIGRAPH);

			// specify layout algorithm
			DotAttributes.setLayout(dotGraph, Layout.CIRCO.toString());

			// export the Graph with DotAttributs to a DOT string and call the
			// dot executable to add layout info to it
			DotExport dotExport = new DotExport();
			String dot = dotExport.exportDot(dotGraph);
			File tmpFile = DotFileUtils.write(dot);
			String[] dotResult = DotExecutableUtils.executeDot(
					new File(dotExecutablePath), true, tmpFile, null, null);
			if (!dotResult[1].isEmpty()) {
				System.err.println(dotResult[1]);
			}
			tmpFile.delete();
			Graph layoutedDotGraph = new DotImport().importDot(dotResult[0]);

			// transfer the DOT provided position information back to the input
			// Graph
			for (Node target : context.getGraph().getNodes()) {
				String nodeName = nodesToNameMap.get(target);
				for (Node source : layoutedDotGraph.getNodes()) {
					if (nodeName.equals(DotAttributes._getName(source))) {
						// convert DotAttributes#pos to
						// LayoutProperties#location
						org.eclipse.gef.dot.internal.parser.point.Point posParsed = DotAttributes
								.getPosParsed(source);
						LayoutProperties.setLocation(target,
								new Point(posParsed.getX(), posParsed.getY()));
						break;
					}
				}
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
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public DotLayoutExample() {
		super("DOT Native Layout");
	}

	@Override
	protected Graph createGraph() {
		Node paper = n(LABEL, "Paper");
		Node rock = n(LABEL, "Rock");
		Node scissors = n(LABEL, "Scissors");
		Node spock = n(LABEL, "Spock");
		Node lizard = n(LABEL, "Lizard");
		Edge paper_disproves_spock = new Edge(paper, spock);
		Edge paper_covers_rock = new Edge(paper, rock);
		Edge rock_crushes_scissors = new Edge(rock, scissors);
		Edge rock_crushes_lizard = new Edge(rock, lizard);
		Edge scissors_cuts_paper = new Edge(scissors, paper);
		Edge scissors_decapitates_lizard = new Edge(scissors, lizard);
		Edge spock_smashes_scissors = new Edge(spock, scissors);
		Edge spock_vaporizes_rock = new Edge(spock, rock);
		Edge lizard_poisons_spock = new Edge(lizard, spock);
		Edge lizard_eats_paper = new Edge(lizard, paper);
		return new Graph.Builder().nodes(paper, rock, scissors, spock, lizard)
				.edges(paper_disproves_spock, paper_covers_rock,
						rock_crushes_scissors, rock_crushes_lizard,
						scissors_cuts_paper, scissors_decapitates_lizard,
						spock_smashes_scissors, spock_vaporizes_rock,
						lizard_poisons_spock, lizard_eats_paper)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G, new DotNativeLayout())
				.build();
	}

}
