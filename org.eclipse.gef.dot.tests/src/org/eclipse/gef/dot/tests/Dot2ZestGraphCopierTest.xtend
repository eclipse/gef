/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG)     - initial API and implementation
 *     Zoey Gerrit Prigge (itemis AG) - additional test cases
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import com.google.inject.Inject
import javafx.beans.property.ReadOnlyMapProperty
import javafx.scene.Group
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape
import org.eclipse.gef.dot.internal.DotImport
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.DotProperties
import org.eclipse.gef.dot.internal.ui.conversion.Dot2ZestGraphCopier
import org.eclipse.gef.fx.nodes.GeometryNode
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.gef.zest.fx.ZestProperties
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

/*
 * Test class containing test cases for the {@link Dot2ZestGraphCopier} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class Dot2ZestGraphCopierTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	//@Rule
	//public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule

	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	static extension DotImport dotImport
	static extension Dot2ZestGraphCopier dot2ZestGraphCopier

	@BeforeClass
	def static void beforeClass() {
		dotImport = new DotImport
		
		dot2ZestGraphCopier = new Dot2ZestGraphCopier
		dot2ZestGraphCopier.attributeCopier.options.emulateLayout = false
	}

	@Test def cluster_bgcolor001() {
		'''
			digraph {
				subgraph clusterName {
					bgcolor=yellow;
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-fill: #ffff00;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_bgcolor002() {
		'''
			digraph {
				subgraph clusterName {
					bgcolor=yellow fillcolor=green;
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-fill: #ffff00;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_color() {
		'''
			digraph {
				subgraph clusterName {
					color=yellow;
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke: #ffff00;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_colorscheme() {
		//The colorscheme itself is not a Zest property, but we can check, it is not ignored. Aqua is not a default x11 colour.
		'''
			digraph {
				subgraph clusterName {
					color=aqua colorscheme=svg;
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke: #00ffff;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_fillcolor001() {
		'''
			digraph {
				subgraph clusterName {
					fillcolor=green;
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-fill: #00ff00;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_fillcolor002() {
		'''
			digraph {
				subgraph clusterName {
					graph [fillcolor=green, style=filled];
					2
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-fill: #00ff00;
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_fontcolor() {
		'''
			digraph {
				subgraph clusterName {
					fontcolor=green label=foo;
					1
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-external-label-css-style : -fx-fill: #00ff00;
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_fontname() {
		mockAvailableFonts("Times New Roman")
		'''
			digraph {
				subgraph clusterName {
					label="foo"
					fontname="Times-Bold"
					1
				}
				2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-external-label-css-style : -fx-font-family: "Times New Roman";-fx-font-weight: 700;-fx-font-style: normal;
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_fontsize() {
		'''
			digraph {
				subgraph clusterName {
					label="foo"
					fontsize="4"
					1
				}
				2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-external-label-css-style : -fx-font-size: 4.0;
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_id() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				subgraph clusterName {
					id="EASTEREGG"
					1
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_label() {
		'''
			digraph {
				subgraph clusterName {
					label=green;
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-external-label : green
					element-label : 
					node-rect-css-style : 
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}


	@Test def cluster_lp() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				subgraph clusterName {
					lp="80,80";
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_penwidth001() {
		'''
			digraph {
				subgraph clusterName {
					penwidth=2
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke-width:2.0;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_penwidth002() {
		'''
			digraph {
				subgraph clusterName {
					style=bold
					penwidth=2
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke-width:2.0;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_penwidth003() {
		'''
			digraph {
				subgraph clusterName {
					style=solid
					penwidth=2
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke-width:2.0;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_style001() {
		'''
			digraph {
				subgraph clusterName {
					style=dashed
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke-dash-array: 7 7;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_style002() {
		'''
			digraph {
				subgraph clusterName {
					style=dotted
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke-dash-array: 1 6;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_style003() {
		'''
			digraph {
				subgraph clusterName {
					style=bold
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : -fx-stroke-width:2;
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_style004() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				subgraph clusterName {
					graph [style="rounded"]
					2
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def cluster_tooltip() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				subgraph clusterName {
					tooltip="foo"
					1
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def edge_arrowhead001() {
		'''
			digraph {
				1->2
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
				}
			}
		''')
	}

	@Test def edge_arrowhead002() {
		'''
			digraph {
				1->2[arrowhead=vee]
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Group[
						Polygon[points=[0.0, 0.0, 10.0, -5.0, 6.666666666666667, 0.0, 10.0, 5.0], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
					]
				}
			}
		''')
	}

	@Test def edge_arrowhead003() {
		'''
			digraph {
				1->2[arrowhead=teevee]
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Group[
						Polygon[points=[0.0, -5.0, 2.5, -5.0, 2.5, 5.0, 0.0, 5.0], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
						Polygon[points=[0.0, 0.0, 10.0, -5.0, 6.666666666666667, 0.0, 10.0, 5.0], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
					]
				}
			}
		''')
	}

	@Test def edge_arrowsize() {
		'''
			digraph {
				1->2[arrowsize=50]
				2->3
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 500.0, -166.66666666666666, 500.0, 166.66666666666666], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_arrowtail001() {
		// If no dir is set this attribute is ignored.
		'''
			digraph {
				1->2[arrowtail=vee]
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
				}
			}
		''')
	}

	@Test def edge_arrowtail002() {
		'''
			digraph {
				1->2[arrowtail=box, dir=back]
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-source-decoration : Group[
						Polygon[points=[0.0, 5.0, 0.0, -5.0, 10.0, -5.0, 10.0, 5.0], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
					]
				}
			}
		''')
	}

	@Test def edge_arrowtail003() {
		'''
			digraph {
				1->2[arrowtail=box, dir=both]
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-source-decoration : Group[
						Polygon[points=[0.0, 5.0, 0.0, -5.0, 10.0, -5.0, 10.0, 5.0], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
					]
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #000000;
				}
			}
		''')
	}

	@Test def edge_color() {
		'''
			digraph {
				1->2[color=green]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;-fx-stroke: #00ff00;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_colorscheme() {
		//The colorscheme itself is not a Zest property, but we can check, it is not ignored. Aqua is not a default x11 colour.
		'''
			digraph {
				1->2[color=aqua, colorscheme=svg]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;-fx-stroke: #00ffff;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_dir001() {
		'''
			digraph {
				1->2[dir=back]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-source-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_dir002() {
		'''
			digraph {
				1->2[dir=both]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-source-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_dir003() {
		'''
			digraph {
				1->2[dir=none]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
				}
			}
		''')
	}

	@Test def edge_edgetooltip() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				1->2[edgetooltip="lorem ipsum"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_fillcolor() {
		'''
			digraph {
				1->2[fillcolor=red]
			}
		'''.assertZestConversion(new EdgeDecorationPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff], style: -fx-stroke: #000000;-fx-fill: #ff0000;
				}
			}
		''')
	}

	@Test def edge_fontcolor() {
		'''
			digraph {
				1->2[fontcolor=red, label="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : foo
					element-label-css-style : -fx-fill: #ff0000;
				}
			}
		''')
	}

	@Test def edge_fontname() {
		mockAvailableFonts("Comic Sans")
		'''
			digraph {
				1->2[fontname="Comic Sans", label="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : foo
					element-label-css-style : -fx-font-family: "Comic Sans";-fx-font-weight: 400;-fx-font-style: normal;
				}
			}
		''')
	}

	@Test def edge_fontsize() {
		'''
			digraph {
				1->2[fontsize=5.5, label="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : foo
					element-label-css-style : -fx-font-size: 5.5;
				}
			}
		''')
	}

	@Test def edge_headlabel() {
		'''
			digraph {
				1->2[headlabel="cool"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : cool
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def edge_headlp() {
		'''
			digraph {
				1->2[headlabel="foo", head_lp="80,80"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : foo
					edge-target-label-position : Point(71.0908203125, 72.01953125)
				}
			}
		''')
	}

	@Test def edge_headport() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				1->2[headport=w]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_headtooltip() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				1->2[headtooltip="some text"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_id() {
		'''
			graph {
				1--2[id="edgeID"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					element-css-id : edgeID
				}
			}
		''')
	}

	@Test def edge_label001() {
		// undirected edge label
		'''
			graph {
				1--2[label="edge label"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					element-label : edge label
				}
			}
		''')
	}

	@Test def edge_label002() {
		// undirected edge label indicating that the edge's name becomes its label
		'''
			graph {
				1--2[label="\E"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					element-label : 1--2
				}
			}
		''')
	}

	@Test def edge_label003() {
		// directed edge label
		'''
			digraph {
				1->2[label="EdgeLabel"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : EdgeLabel
				}
			}
		''')
	}

	@Test def edge_label004() {
		// directed edge label indicating that the edge's name becomes its label
		'''
			digraph {
				1->2[label="\E"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : 1->2
				}
			}
		''')
	}

	@Test def edge_label_and_id001() {
		'''
			graph {
				1--2[id="edgeID" label="edgeLabel"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					element-css-id : edgeID
					element-label : edgeLabel
				}
			}
		''')
	}

	@Test def edge_label_and_id002() {
		'''
			graph {
				1--2[id="edgeID" label="\E"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					element-css-id : edgeID
					element-label : 1--2
				}
			}
		''')
	}

	@Test def edge_labelfontcolor() {
		// If unset, the fontcolor value is used.
		'''
			digraph {
				1->2[fontcolor="blue", labelfontcolor=red, label="foo", headlabel="baa"]
				1->3[labelfontcolor=red, headlabel="baa"]
				2->3[fontcolor="blue", label="foo", headlabel="baa"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-fill: #ff0000;
					element-label : foo
					element-label-css-style : -fx-fill: #0000ff;
				}
				Edge2 from Node1 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-fill: #ff0000;
				}
				Edge3 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-fill: #0000ff;
					element-label : foo
					element-label-css-style : -fx-fill: #0000ff;
				}
			}
		''')
	}

	@Test def edge_labelfontname() {
		mockAvailableFonts("Arial", "Comic Sans", "Times New Roman")
		// If unset, the fontcolor value is used.
		'''
			digraph {
				1->2[fontname="Arial", labelfontname="Courier New", label="foo", headlabel="baa"]
				1->3[labelfontname="Times New Roman", headlabel="baa"]
				2->3[fontname="Comic Sans", label="foo", headlabel="baa"]
				3->4[fontname="Times-Bold", label="faa"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : 4
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-font-family: "System";-fx-font-weight: 400;-fx-font-style: normal;
					element-label : foo
					element-label-css-style : -fx-font-family: "Arial";-fx-font-weight: 400;-fx-font-style: normal;
				}
				Edge2 from Node1 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-font-family: "System";-fx-font-weight: 400;-fx-font-style: normal;
				}
				Edge3 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-font-family: "Comic Sans";-fx-font-weight: 400;-fx-font-style: normal;
					element-label : foo
					element-label-css-style : -fx-font-family: "Comic Sans";-fx-font-weight: 400;-fx-font-style: normal;
				}
				Edge4 from Node3 to Node4 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : faa
					element-label-css-style : -fx-font-family: "Times New Roman";-fx-font-weight: 700;-fx-font-style: normal;
				}
			}
		''')
	}

	@Test def edge_labelfontsize() {
		// If unset, the fontcolor value is used.
		'''
			digraph {
				1->2[fontsize=5, labelfontsize=6, label="foo", headlabel="baa"]
				1->3[labelfontsize=7, headlabel="baa"]
				2->3[fontsize=8, label="foo", headlabel="baa"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-font-size: 6.0;
					element-label : foo
					element-label-css-style : -fx-font-size: 5.0;
				}
				Edge2 from Node1 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-font-size: 7.0;
				}
				Edge3 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-target-label : baa
					edge-target-label-css-style : -fx-font-size: 8.0;
					element-label : foo
					element-label-css-style : -fx-font-size: 8.0;
				}
			}
		''')
	}

	@Test def edge_labeltooltip() {
		'''
			digraph {
				1->2[label="foo", labeltooltip="baa"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-label-tooltip : baa
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : foo
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def edge_lp() {
		'''
			digraph {
				1->2[label="foo", lp="80,80"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-label-position : Point(71.0908203125, 72.01953125)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : foo
				}
			}
		''')
	}

	@Test def edge_name() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				1->2[name="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def void edge_penwidth() {
		// color and style=bold can interfere with penwidth, hence these are tested too.
		'''
			digraph {
				1->2[style=bold penwidth=0.5 color=green]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;-fx-stroke: #00ff00;-fx-stroke-width:0.5;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def void edge_pos() {
		// TODO: implement
	}

	@Test def edge_style() {
		'''
			digraph {
				1->2[style=dotted]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 1 7;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_taillabel() {
		'''
			digraph {
				1->2[taillabel="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-source-label : foo
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def edge_taillp() {
		'''
			digraph {
				1->2[taillabel="foo", tail_lp="80,80"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-source-label : foo
					edge-source-label-position : Point(71.0908203125, 72.01953125)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_tailport() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				1->2[tailport="n"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_tailtooltip() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				1->2[tailtooltip="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def edge_tooltip() {
		'''
			digraph {
				1->2[tooltip="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					edge-tooltip : foo
				}
			}
		''')
	}

	@Test def edge_xlabel() {
		'''
			digraph {
				1->2[xlabel="foo"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-external-label : foo
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def edge_xlp() {
		'''
			digraph {
				1->2[xlabel="foo", xlp="80,80"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-external-label : foo
					element-external-label-position : Point(71.0908203125, 72.01953125)
				}
			}
		''')
	}

	@Test def void graph_bb() {
		// TODO: implement
	}

	@Test def graph_bgcolor() {
		// TODO test this attribute differently
		'''
			digraph {
				graph [bgcolor=red];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_clusterrank001() {
		// Note: clusterrank defaults to local; none and global turn the cluster processing off.
		'''
			digraph {
				graph [clusterrank=global];
				subgraph clusterName {
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_clusterrank002() {
		// Note: clusterrank defaults to local; none and global turn the cluster processing off.
		'''
			digraph {
				graph [clusterrank=none];
				subgraph clusterName {
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_clusterrank003() {
		// Note: clusterrank defaults to local; none and global turn the cluster processing off.
		'''
			digraph {
				graph [clusterrank=local];
				subgraph clusterName {
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_clusterrank004() {
		// Note: clusterrank defaults to local; none and global turn the cluster processing off.
		'''
			digraph {
				subgraph clusterName {
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def void graph_colorscheme() {
		// TODO: implement once the issue with attribute support is resolved (bug 540508)
	}

	@Test def graph_fontcolor() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [label="foo", fontcolor=green];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_fontname() {
		//mockAvailableFonts("Times New Roman")
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [label="foo", fontname="Times-Roman"];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_fontsize() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [label="foo", fontsize=13];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_forcelabels001() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		// The tested graph needs to have very close elements for this attribute to have an effect.
		// Note: forcelabels defaults to true.
		'''
			digraph {
				graph[forcelabels=false]
				subgraph {
					graph[rank=same]
					2
					3
				}
				2->3[xlabel="should be ommitted"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-external-label : should be ommitted
				}
			}
		''')
	}

	@Test def graph_forcelabels002() {
		
		'''
			digraph {
				subgraph {
					graph[rank=same]
					2
					3
				}
				2->3[xlabel="should not be ommitted"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-external-label : should not be ommitted
				}
			}
		''')
	}

	@Test def graph_id() {
		'''
			digraph {
				graph [id="someId"];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_label() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [label="foo"];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def void graph_layout() {
		// TODO: implement
	}

	@Test def graph_lp() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [label="foo", lp="80,80"];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_name() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [name="stuff"];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_nodesep() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [nodesep=10];
				1
				2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
		}

	@Test def graph_outputorder001() {
		// This test shows current behaviour, if this attribute is relevant for Gef Dot, the test may have to be adapted.
		'''
			digraph {
				graph [outputorder=breadthfirst];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
		}

	@Test def graph_outputorder002() {
		'''
			digraph {
				graph [outputorder=nodesfirst];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
		}

	@Test def graph_outputorder003() {
		'''
			digraph {
				graph [outputorder=edgesfirst];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir001() {
		// This test shows current behaviour, if this attribute is relevant for Gef Dot, the test may have to be adapted.
		'''
			digraph {
				graph [pagedir=BL];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir002() {
		'''
			digraph {
				graph [pagedir=BR];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir003() {
		'''
			digraph {
				graph [pagedir=TL];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir004() {
		'''
			digraph {
				graph [pagedir=TR];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir005() {
		'''
			digraph {
				graph [pagedir=RB];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir006() {
		'''
			digraph {
				graph [pagedir=RT];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir007() {
		'''
			digraph {
				graph [pagedir=LB];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_pagedir008() {
		'''
			digraph {
				graph [pagedir=LT];
				1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def graph_rankdir001() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			digraph {
				graph [rankdir=LR];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_rankdir002() {
		'''
			digraph {
				graph [rankdir=RL];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_rankdir003() {
		'''
			digraph {
				graph [rankdir=TB];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_rankdir004() {
		'''
			digraph {
				graph [rankdir=BT];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines001() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		
		'''
			digraph {
				graph [splines=""];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-invisible : true
				}
			}
		''')
	}

	@Test def graph_splines002() {
		'''
			digraph {
				graph [splines=none];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-invisible : true
				}
			}
		''')
	}

	@Test def graph_splines003() {
		'''
			digraph {
				graph [splines=false];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines004() {
		'''
			digraph {
				graph [splines=line];
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines005() {
		'''
			digraph {
				graph [splines=curved];
				1->2
				2->3
				3->1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node1 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines006() {
		'''
			digraph {
				graph [splines=polyline];
				1->2
				2->3
				3->1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node1 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines007() {
		'''
			digraph {
				graph [splines=ortho];
				1->2
				2->3
				3->1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node1 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines008() {
		'''
			digraph {
				graph [splines=spline];
				1->2
				2->3
				3->1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node1 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def graph_splines009() {
		'''
			digraph {
				graph [splines=true];
				1->2
				2->3
				3->1
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node1 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def void graph_type() {
		// TODO: implement
	}

	@Test def node_color() {
		'''
			graph {
				1[color="green"]
			}
		'''.assertZestConversion(new NodeShapeWithStylePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0), style: -fx-stroke: #00ff00;
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_colorscheme_fontcolor() {
		'''
			graph {
				1[fontcolor="aqua", colorscheme="svg"]
			}
		'''.assertZestConversion(new NodeShapeWithStylePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					element-label-css-style : -fx-fill: #00ffff;
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0), style: 
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_colorscheme_fillcolor() {
		'''
			graph {
				1[fillcolor="aqua", colorscheme="svg", style=filled]
			}
		'''.assertZestConversion(new NodeShapeWithStylePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0), style: -fx-fill: #00ffff;
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_distortion() {
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		// TODO: use custom pretty printer
		'''
			graph {
				1[shape=rectangle, distortion=20]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_fillcolor() {
		'''
			graph {
				1[fillcolor=blue, style=filled]
			}
		'''.assertZestConversion(new NodeShapeWithStylePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0), style: -fx-fill: #0000ff;
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_fixedsize() {
		'''
			graph {
				1[fixedsize="true", height=1, width=1]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(72.0, 72.0)
				}
			}
		''')
	}

	@Test def node_fontcolor() {
		'''
			graph {
				1[fontcolor="green"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					element-label-css-style : -fx-fill: #00ff00;
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_fontname001() {
		mockAvailableFonts("Arial", "Helvetica")
		'''
			graph {
				1[fontname="Helvetica"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					element-label-css-style : -fx-font-family: "Helvetica";-fx-font-weight: 400;-fx-font-style: normal;
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_fontname002() {
		mockAvailableFonts("Arial")
		'''
			graph {
				1[fontname="Helvetica"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					element-label-css-style : -fx-font-family: "Arial";-fx-font-weight: 400;-fx-font-style: normal;
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_fontname003() {
		mockAvailableFonts("Arial")
		'''
			graph {
				1[fontname="Helvetica", xlabel="Gotcha"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : Gotcha
					element-external-label-css-style : -fx-font-family: "Arial";-fx-font-weight: 400;-fx-font-style: normal;
					element-label : 1
					element-label-css-style : -fx-font-family: "Arial";-fx-font-weight: 400;-fx-font-style: normal;
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_fontsize() {
		'''
			graph {
				1[fontsize=13]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					element-label-css-style : -fx-font-size: 13.0;
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_height() {
		'''
			graph {
				1[height="22"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 1584.0)
				}
			}
		''')
	}

	@Test def node_id() {
		'''
			graph {
				1[id="nodeID"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-css-id : nodeID
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_label001() {
		'''
			graph {
				1[label="node label"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : node label
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_label002() {
		// node label indicating that the node's name becomes its label
		'''
			graph {
				1[label="\N"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def node_label_recordbased001() {
		'''
			graph {
				1[shape=record label="Hi | {Test | Section 2 } | xyz"]
				2[shape=Mrecord label="Hi | Test"]
			}
		'''.assertZestConversion(new RecordBasedShapePrettyPrinter,'''
			Graph {
				Node1 {
					HBox {
						style : -fx-border-style:solid;
						VBox {
							HBox {
								Text {
									text : Hi
								}
							}
						}
						RecordBasedLabelLine
						VBox {
							VBox {
								HBox {
									Text {
										text : Test
									}
								}
							}
							RecordBasedLabelLine
							VBox {
								HBox {
									Text {
										text : Section 2
									}
								}
							}
						}
						RecordBasedLabelLine
						VBox {
							HBox {
								Text {
									text : xyz
								}
							}
						}
					}
					node-size : Dimension(132.0, 42.0)
				}
				Node2 {
					HBox {
						style : -fx-background-radius:10px;-fx-border-radius:10px;-fx-border-style:solid;
						VBox {
							HBox {
								Text {
									text : Hi
								}
							}
						}
						RecordBasedLabelLine
						VBox {
							HBox {
								Text {
									text : Test
								}
							}
						}
					}
					node-size : Dimension(68.0, 36.0)
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def node_label_recordbased002() {
		'''
			graph {
				rankdir=LR
				1[shape=record label="{ Hi | Test } | Section 2 | xyz" fontsize=25 color=green]
				2[shape=Mrecord label="Hi | Test" fontsize=25 color=green]
				4[shape=record label="Hi | Test" style=dashed penwidth=2]
				5[shape=record label="Hi | Test" style=bold]
				6[shape=record label="Hi | Test" style=filled fillcolor=red]
			}
		'''.assertZestConversion(new RecordBasedShapePrettyPrinter,'''
			Graph {
				Node1 {
					element-label-css-style : -fx-font-size: 25.0;
					VBox {
						style : -fx-border-color:#00ff00;-fx-border-style:solid;
						HBox {
							VBox {
								HBox {
									Text {
										style : -fx-font-size: 25.0;
										text : Hi
									}
								}
							}
							RecordBasedLabelLine {
								style : -fx-stroke:#00ff00;
							}
							VBox {
								HBox {
									Text {
										style : -fx-font-size: 25.0;
										text : Test
									}
								}
							}
						}
						RecordBasedLabelLine {
							style : -fx-stroke:#00ff00;
						}
						VBox {
							HBox {
								Text {
									style : -fx-font-size: 25.0;
									text : Section 2
								}
							}
						}
						RecordBasedLabelLine {
							style : -fx-stroke:#00ff00;
						}
						VBox {
							HBox {
								Text {
									style : -fx-font-size: 25.0;
									text : xyz
								}
							}
						}
					}
					node-size : Dimension(109.0, 98.5)
				}
				Node2 {
					element-label-css-style : -fx-font-size: 25.0;
					VBox {
						style : -fx-border-color:#00ff00;-fx-background-radius:10px;-fx-border-radius:10px;-fx-border-style:solid;
						VBox {
							HBox {
								Text {
									style : -fx-font-size: 25.0;
									text : Hi
								}
							}
						}
						RecordBasedLabelLine {
							style : -fx-stroke:#00ff00;
						}
						VBox {
							HBox {
								Text {
									style : -fx-font-size: 25.0;
									text : Test
								}
							}
						}
					}
					node-size : Dimension(59.0, 65.0)
				}
				Node3 {
					VBox {
						style : -fx-border-width:2.0;-fx-border-style:dashed;
						VBox {
							HBox {
								Text {
									text : Hi
								}
							}
						}
						RecordBasedLabelLine {
							style : -fx-stroke-width:2.0;-fx-stroke-dash-array: 7 7;
						}
						VBox {
							HBox {
								Text {
									text : Test
								}
							}
						}
					}
					node-size : Dimension(54.0, 42.0)
				}
				Node4 {
					VBox {
						style : -fx-border-width:2;-fx-border-style:solid;
						VBox {
							HBox {
								Text {
									text : Hi
								}
							}
						}
						RecordBasedLabelLine {
							style : -fx-stroke-width:2;
						}
						VBox {
							HBox {
								Text {
									text : Test
								}
							}
						}
					}
					node-size : Dimension(54.0, 42.0)
				}
				Node5 {
					VBox {
						style : -fx-background-color:#ff0000;-fx-border-style:solid;
						VBox {
							HBox {
								Text {
									text : Hi
								}
							}
						}
						RecordBasedLabelLine
						VBox {
							HBox {
								Text {
									text : Test
								}
							}
						}
					}
					node-size : Dimension(54.0, 41.0)
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def node_label_recordbased003() {
		'''
			graph {
				1[shape=record label=" Hi | Test \n Right \r Left \l Center | Section 2"]
			}
		'''.assertZestConversion(new RecordBasedShapePrettyPrinter,'''
			Graph {
				Node1 {
					HBox {
						style : -fx-border-style:solid;
						VBox {
							HBox {
								Text {
									text : Hi
								}
							}
						}
						RecordBasedLabelLine
						VBox {
							HBox {
								Text {
									text : Test 
								}
							}
							HBox {
								alignment : CENTER_RIGHT
								Text {
									text :  Right 
								}
							}
							HBox {
								alignment : CENTER_LEFT
								Text {
									text :  Left 
								}
							}
							HBox {
								Text {
									text :  Center
								}
							}
						}
						RecordBasedLabelLine
						VBox {
							HBox {
								Text {
									text : Section 2
								}
							}
						}
					}
					node-size : Dimension(152.0, 69.0)
				}
			}
		''')
	}

	@Test def node_label_and_id001() {
		'''
			graph {
				1[id="nodeID" label="nodeLabel"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-css-id : nodeID
					element-label : nodeLabel
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_label_and_id002() {
		'''
			graph {
				1[id="nodeID" label="\N"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-css-id : nodeID
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_penwidth() {
		// color and style=bold can interfere with penwidth, hence these are tested too.
		'''
			graph {
				1[penwidth=4 style=bold color="green"]
			}
		'''.assertZestConversion(new NodeShapeWithStylePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0), style: -fx-stroke: #00ff00;-fx-stroke-width:4.0;
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_pos() {
		'''
			graph {
				1[pos="10,10"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					element-layout-irrelevant : false
					node-position : Point(-17.0, -8.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_shape() {
		'''
			graph PolygonBasedNodeShapes {
				box[shape=box]
				polygon[shape=polygon]
				ellipse[shape=ellipse]
				oval[shape=oval]
				circle[shape=circle]
				point[shape=point]
				egg[shape=egg]
				triangle[shape=triangle]
				plaintext[shape=plaintext]
				plain[shape=plain]
				diamond[shape=diamond]
				trapezium[shape=trapezium]
				parallelogram[shape=parallelogram]
				house[shape=house]
				pentagon[shape=pentagon]
				hexagon[shape=hexagon]
				septagon[shape=septagon]
				octagon[shape=octagon]
				doublecircle[shape=doublecircle]
				doubleoctagon[shape=doubleoctagon]
				tripleoctagon[shape=tripleoctagon]
				invtriangle[shape=invtriangle]
				invtrapezium[shape=invtrapezium]
				invhouse[shape=invhouse]
				Mdiamond[shape=Mdiamond]
				Msquare[shape=Msquare]
				Mcircle[shape=Mcircle]
				rect[shape=rect]
				rectangle[shape=rectangle]
				square[shape=square]
				star[shape=star]
				none[shape=none]
				underline[shape=underline]
				cylinder[shape=cylinder]
				note[shape=note]
				tab[shape=tab]
				folder[shape=folder]
				box3d[shape=box3d]
				component[shape=component]
				promoter[shape=promoter]
				cds[shape=cds]
				terminator[shape=terminator]
				utr[shape=utr]
				primersite[shape=primersite]
				restrictionsite[shape=restrictionsite]
				fivepoverhang[shape=fivepoverhang]
				threepoverhang[shape=threepoverhang]
				noverhang[shape=noverhang]
				assembly[shape=assembly]
				signature[shape=signature]
				insulator[shape=insulator]
				ribosite[shape=ribosite]
				rnastab[shape=rnastab]
				proteasesite[shape=proteasesite]
				proteinstab[shape=proteinstab]
				rpromoter[shape=rpromoter]
				rarrow[shape=rarrow]
				laarrow[shape=larrow]
				lpromoter[shape=lpromoter]
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : box
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : polygon
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : ellipse
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : oval
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node5 {
					element-label : circle
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node6 {
					element-label : point
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node7 {
					element-label : egg
					node-size : Dimension(54.0, 36.0)
				}
				Node8 {
					element-label : triangle
					node-shape : Polygon: (0.0, 50.0) -> (50.0, 0.0) -> (100.0, 50.0) -> (0.0, 50.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node9 {
					element-label : plaintext
					node-shape : DotNoneShape
					node-size : Dimension(54.0, 36.0)
				}
				Node10 {
					element-label : plain
					node-size : Dimension(54.0, 36.0)
				}
				Node11 {
					element-label : diamond
					node-shape : Polygon: (0.0, 50.0) -> (50.0, 0.0) -> (100.0, 50.0) -> (50.0, 100.0) -> (0.0, 50.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node12 {
					element-label : trapezium
					node-shape : Polygon: (0.0, 100.0) -> (25.0, 0.0) -> (75.0, 0.0) -> (100.0, 100.0) -> (0.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node13 {
					element-label : parallelogram
					node-shape : Polygon: (0.0, 100.0) -> (25.0, 0.0) -> (100.0, 0.0) -> (75.0, 100.0) -> (0.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node14 {
					element-label : house
					node-shape : Polygon: (0.0, 100.0) -> (0.0, 40.0) -> (50.0, 0.0) -> (100.0, 40.0) -> (100.0, 100.0) -> (0.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node15 {
					element-label : pentagon
					node-shape : Polygon: (25.0, 100.0) -> (0.0, 40.0) -> (50.0, 0.0) -> (100.0, 40.0) -> (75.0, 100.0) -> (25.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node16 {
					element-label : hexagon
					node-shape : Polygon: (25.0, 100.0) -> (0.0, 50.0) -> (25.0, 0.0) -> (75.0, 0.0) -> (100.0, 50.0) -> (75.0, 100.0) -> (25.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node17 {
					element-label : septagon
					node-shape : Polygon: (0.0, 60.0) -> (15.0, 15.0) -> (50.0, 0.0) -> (85.0, 15.0) -> (100.0, 60.0) -> (75.0, 100.0) -> (25.0, 100.0) -> (0.0, 60.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node18 {
					element-label : octagon
					node-shape : Polygon: (0.0, 70.0) -> (0.0, 30.0) -> (30.0, 0.0) -> (70.0, 0.0) -> (100.0, 30.0) -> (100.0, 70.0) -> (70.0, 100.0) -> (30.0, 100.0) -> (0.0, 70.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node19 {
					dotInnerShapeDistance__n : 5.0
					dotInnerShape__n : Ellipse (0.0, 0.0, 100.0, 100.0)
					element-label : doublecircle
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node20 {
					dotInnerShapeDistance__n : 5.0
					dotInnerShape__n : Polygon: (0.0, 70.0) -> (0.0, 30.0) -> (30.0, 0.0) -> (70.0, 0.0) -> (100.0, 30.0) -> (100.0, 70.0) -> (70.0, 100.0) -> (30.0, 100.0) -> (0.0, 70.0)
					element-label : doubleoctagon
					node-shape : Polygon: (0.0, 70.0) -> (0.0, 30.0) -> (30.0, 0.0) -> (70.0, 0.0) -> (100.0, 30.0) -> (100.0, 70.0) -> (70.0, 100.0) -> (30.0, 100.0) -> (0.0, 70.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node21 {
					element-label : tripleoctagon
					node-size : Dimension(54.0, 36.0)
				}
				Node22 {
					element-label : invtriangle
					node-shape : Polygon: (0.0, 10.0) -> (100.0, 10.0) -> (50.0, 100.0) -> (0.0, 10.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node23 {
					element-label : invtrapezium
					node-shape : Polygon: (0.0, 0.0) -> (100.0, 0.0) -> (75.0, 100.0) -> (25.0, 100.0) -> (0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node24 {
					element-label : invhouse
					node-shape : Polygon: (0.0, 0.0) -> (100.0, 0.0) -> (100.0, 60.0) -> (50.0, 100.0) -> (0.0, 60.0) -> (0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node25 {
					element-label : Mdiamond
					node-size : Dimension(54.0, 36.0)
				}
				Node26 {
					element-label : Msquare
					node-size : Dimension(54.0, 36.0)
				}
				Node27 {
					element-label : Mcircle
					node-size : Dimension(54.0, 36.0)
				}
				Node28 {
					element-label : rect
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node29 {
					element-label : rectangle
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node30 {
					element-label : square
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node31 {
					element-label : star
					node-shape : Polygon: (15.0, 100.0) -> (30.0, 60.0) -> (0.0, 40.0) -> (40.0, 40.0) -> (50.0, 0.0) -> (60.0, 40.0) -> (100.0, 40.0) -> (70.0, 60.0) -> (85.0, 100.0) -> (50.0, 75.0) -> (15.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node32 {
					element-label : none
					node-shape : DotNoneShape
					node-size : Dimension(54.0, 36.0)
				}
				Node33 {
					element-label : underline
					node-size : Dimension(54.0, 36.0)
				}
				Node34 {
					element-label : cylinder
					node-size : Dimension(54.0, 36.0)
				}
				Node35 {
					element-label : note
					node-size : Dimension(54.0, 36.0)
				}
				Node36 {
					element-label : tab
					node-size : Dimension(54.0, 36.0)
				}
				Node37 {
					element-label : folder
					node-shape : Polygon: (0.0, 100.0) -> (0.0, 10.0) -> (50.0, 10.0) -> (55.0, 0.0) -> (95.0, 0.0) -> (100.0, 10.0) -> (100.0, 100.0) -> (0.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node38 {
					element-label : box3d
					node-size : Dimension(54.0, 36.0)
				}
				Node39 {
					element-label : component
					node-size : Dimension(54.0, 36.0)
				}
				Node40 {
					element-label : promoter
					node-size : Dimension(54.0, 36.0)
				}
				Node41 {
					element-label : cds
					node-shape : Polygon: (0.0, 100.0) -> (0.0, 0.0) -> (70.0, 0.0) -> (100.0, 50.0) -> (70.0, 100.0) -> (0.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node42 {
					element-label : terminator
					node-size : Dimension(54.0, 36.0)
				}
				Node43 {
					element-label : utr
					node-size : Dimension(54.0, 36.0)
				}
				Node44 {
					element-label : primersite
					node-size : Dimension(54.0, 36.0)
				}
				Node45 {
					element-label : restrictionsite
					node-size : Dimension(54.0, 36.0)
				}
				Node46 {
					element-label : fivepoverhang
					node-size : Dimension(54.0, 36.0)
				}
				Node47 {
					element-label : threepoverhang
					node-size : Dimension(54.0, 36.0)
				}
				Node48 {
					element-label : noverhang
					node-size : Dimension(54.0, 36.0)
				}
				Node49 {
					element-label : assembly
					node-size : Dimension(54.0, 36.0)
				}
				Node50 {
					element-label : signature
					node-size : Dimension(54.0, 36.0)
				}
				Node51 {
					element-label : insulator
					node-size : Dimension(54.0, 36.0)
				}
				Node52 {
					element-label : ribosite
					node-size : Dimension(54.0, 36.0)
				}
				Node53 {
					element-label : rnastab
					node-size : Dimension(54.0, 36.0)
				}
				Node54 {
					element-label : proteasesite
					node-size : Dimension(54.0, 36.0)
				}
				Node55 {
					element-label : proteinstab
					node-size : Dimension(54.0, 36.0)
				}
				Node56 {
					element-label : rpromoter
					node-shape : Polygon: (0.0, 100.0) -> (0.0, 15.0) -> (60.0, 15.0) -> (60.0, 0.0) -> (100.0, 50.0) -> (60.0, 100.0) -> (60.0, 85.0) -> (30.0, 85.0) -> (30.0, 100.0) -> (0.0, 100.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node57 {
					element-label : rarrow
					node-shape : Polygon: (0.0, 85.0) -> (0.0, 15.0) -> (60.0, 15.0) -> (60.0, 0.0) -> (100.0, 50.0) -> (60.0, 100.0) -> (60.0, 85.0) -> (0.0, 85.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node58 {
					element-label : laarrow
					node-shape : Polygon: (0.0, 50.0) -> (40.0, 0.0) -> (40.0, 15.0) -> (100.0, 15.0) -> (100.0, 85.0) -> (40.0, 85.0) -> (40.0, 100.0) -> (0.0, 50.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node59 {
					element-label : lpromoter
					node-shape : Polygon: (0.0, 50.0) -> (40.0, 0.0) -> (40.0, 15.0) -> (100.0, 15.0) -> (100.0, 100.0) -> (70.0, 100.0) -> (70.0, 85.0) -> (40.0, 85.0) -> (40.0, 100.0) -> (0.0, 50.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_shape_rounded_and_filled_styled() {
		'''
			graph RoundedStyledPolygonBasedNodeShapes {
				node[style=rounded]
				box[shape=box]
				rect[shape=rect]
				rectangle[shape=rectangle]
				square[shape=square]
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : box
					node-shape : RoundedRectangle(0.0, 0.0, 0.0, 0.0, 25.0, 25.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : rect
					node-shape : RoundedRectangle(0.0, 0.0, 0.0, 0.0, 25.0, 25.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : rectangle
					node-shape : RoundedRectangle(0.0, 0.0, 0.0, 0.0, 25.0, 25.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : square
					node-shape : RoundedRectangle(0.0, 0.0, 0.0, 0.0, 25.0, 25.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_sides() {
		// TODO Once the sides attribute is implemented, a customized pretty printer must be used.
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			graph {
				1[shape=polygon, sides=10]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_skew() {
		// TODO Once the sides attribute is implemented, a customized pretty printer must be used.
		// This test shows current behaviour, it needs adaptation once the attribute is supported.
		'''
			graph {
				1[shape=polygon, skew=10]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_style() {
		'''
			graph {
				1[style=dashed]
			}
		'''.assertZestConversion(new NodeShapeWithStylePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0), style: -fx-stroke-dash-array: 7 7;
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_tooltip() {
		'''
			graph {
				1[tooltip="Sports are fun!"]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
					node-tooltip : Sports are fun!
				}
			}
		''')
	}

	@Test def node_width() {
		'''
			graph {
				1[width=100]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(7200.0, 36.0)
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def node_xlp() {
		'''
			graph {
				1[xlp="20,20", xlabel=foo]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-external-label-position : Point(11.0908203125, 12.01953125)
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def node_xlabel() {
		'''
			graph {
				1[xlabel=foo]
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def node_xlabel_with_layout_information() {
		'''
			graph {
				graph [bb="0,51,81,0"];
				node [label="\N"];
				1	 [height=0.5,
					pos="54,33",
					width=0.75,
					xlabel=foo,
					xlp="13.5,7.5"];
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-external-label : foo
					element-external-label-position : Point(4.5908203125, -0.48046875)
					element-label : 1
					element-layout-irrelevant : false
					node-position : Point(27.0, 15.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraph_rank() {
		// TODO check This test shows current behaviour, it needs adaptation once the attribute is supported.
		// If there were no edge in the following test case, the nodes would be on the same rank regardless of the attribute setting.
		'''
			digraph {
				subgraph {
					graph [rank=same]
					2
					3
				}
				2->3
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def simple_graph() {
		val dotGraphPrettyPrinter = new DotGraphPrettyPrinter
		
		val dot = DotTestUtils.simpleGraph
		val zest = dot.copy
		
		// test graph
		zest.test(dotGraphPrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
				}
				Edge2 from Node1 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
				}
			}
		''')
		
		// test node
		zest.nodes.get(0).test(dotGraphPrettyPrinter, '''
			Node1 {
				element-label : 1
				node-shape : GeometryNode
				node-size : Dimension(54.0, 36.0)
			}
		''')
		
		// test edge
		zest.edges.get(0).test(dotGraphPrettyPrinter, '''
			Edge1 from Node1 to Node2 {
				edge-curve : GeometryNode
				edge-curve-css-style : -fx-stroke-line-cap: butt;
			}
		''')
	}

	@Test def directed_graph() {
		val dot = DotTestUtils.simpleDiGraph
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def labeled_graph() {
		val dot = DotTestUtils.labeledGraph
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : one "1"
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : two
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : 4
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : +1
				}
				Edge2 from Node1 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : +2
				}
				Edge3 from Node3 to Node4 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def styled_graph() {
		val dot = DotTestUtils.styledGraph
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : 4
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node5 {
					element-label : 5
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 1 7;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node4 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge4 from Node3 to Node5 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge5 from Node4 to Node5 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def dot_cluster_graph_pretty_print() {
		DotTestUtils.clusteredGraph.test('''
			Graph {
				_type : digraph
				Node1 {
					Graph {
						_name : cluster1
						Node1.1 {
							_name : a
						}
						Node1.2 {
							_name : b
						}
						Edge1.1 from Node1.1 to Node1.2 {
						}
					}
				}
				Node2 {
					Graph {
						_name : cluster2
						Node2.1 {
							_name : p
						}
						Node2.2 {
							_name : q
						}
						Node2.3 {
							_name : r
						}
						Node2.4 {
							_name : s
						}
						Node2.5 {
							_name : t
						}
						Edge2.1 from Node2.1 to Node2.2 {
						}
						Edge2.2 from Node2.2 to Node2.3 {
						}
						Edge2.3 from Node2.3 to Node2.4 {
						}
						Edge2.4 from Node2.4 to Node2.5 {
						}
						Edge2.5 from Node2.5 to Node2.1 {
						}
					}
				}
				Edge1 from Node1.2 to Node2.2 {
				}
				Edge2 from Node2.5 to Node1.1 {
				}
			}
		''')
	}

	@Test def dot_nested_cluster_graph_pretty_print() {
		DotTestUtils.nestedClusteredGraph.test('''
			Graph {
				_type : digraph
				Node1 {
					Graph {
						_name : cluster1
						Node1.1 {
							Graph {
								_name : cluster1_1
								Node1.1.1 {
									_name : a
								}
								Node1.1.2 {
									_name : b
								}
								Edge1.1.1 from Node1.1.1 to Node1.1.2 {
								}
							}
						}
					}
				}
				Node2 {
					Graph {
						_name : cluster2
						Node2.1 {
							_name : p
						}
						Node2.2 {
							_name : q
						}
						Node2.3 {
							_name : r
						}
						Node2.4 {
							_name : s
						}
						Node2.5 {
							_name : t
						}
						Edge2.1 from Node2.1 to Node2.2 {
						}
						Edge2.2 from Node2.2 to Node2.3 {
						}
						Edge2.3 from Node2.3 to Node2.4 {
						}
						Edge2.4 from Node2.4 to Node2.5 {
						}
						Edge2.5 from Node2.5 to Node2.1 {
						}
					}
				}
				Edge1 from Node1.1.2 to Node2.2 {
				}
				Edge2 from Node2.5 to Node1.1.1 {
				}
			}
		''')
	}

	@Test def simple_graph_with_additional_information() {
		val dot = DotTestUtils.simpleGraphWithAdditionalInformation
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : 1
					element-layout-irrelevant : false
					node-position : Point(36.0, 0.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					element-layout-irrelevant : false
					node-position : Point(0.0, 72.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					element-layout-irrelevant : false
					node-position : Point(72.0, 72.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-control-points : [Point(54.65, 35.235), Point(48.835, 46.544), Point(41.11, 61.563), Point(35.304, 72.853)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(35.304, 72.853)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(54.65, 35.235)
				}
				Edge2 from Node1 to Node3 {
					edge-control-points : [Point(71.35, 35.235), Point(77.165, 46.544), Point(84.89, 61.563), Point(90.696, 72.853)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(90.696, 72.853)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(71.35, 35.235)
				}
			}
		''')
	}

	@Test def directed_graph_with_additional_information() {
		val dot = DotTestUtils.simpleDiGraphWithAdditionalInformation
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : 1
					element-layout-irrelevant : false
					node-position : Point(0.0, 0.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					element-layout-irrelevant : false
					node-position : Point(0.0, 72.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					element-layout-irrelevant : false
					node-position : Point(0.0, 144.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-control-points : [Point(27.0, 36.303), Point(27.0, 44.017), Point(27.0, 53.288), Point(27.0, 61.888)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(27.0, 71.896)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(27.0, 36.303)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-control-points : [Point(27.0, 108.3), Point(27.0, 116.02), Point(27.0, 125.29), Point(27.0, 133.89)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(27.0, 143.9)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(27.0, 108.3)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def labeled_graph_with_additional_information() {
		val dot = DotTestUtils.labeledGraphWithAdditionalInformation
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : one "1"
					element-layout-irrelevant : false
					node-position : Point(15.6528, 0.0)
					node-shape : GeometryNode
					node-size : Dimension(76.6944, 36.0)
				}
				Node2 {
					element-label : two
					element-layout-irrelevant : false
					node-position : Point(0.0, 87.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					element-layout-irrelevant : false
					node-position : Point(72.0, 87.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : 4
					element-layout-irrelevant : false
					node-position : Point(72.0, 160.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-control-points : [Point(48.536, 36.201), Point(44.779, 48.03), Point(39.715, 63.97), Point(35.442, 77.422)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(32.364, 87.115)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-label-position : Point(42.6611328125, 53.51953125)
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(48.536, 36.201)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : +1
				}
				Edge2 from Node1 to Node3 {
					edge-control-points : [Point(62.891, 35.793), Point(69.384, 48.058), Point(78.299, 64.898), Point(85.646, 78.776)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(90.433, 87.818)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-label-position : Point(79.6611328125, 53.51953125)
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(62.891, 35.793)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
					element-label : +2
				}
				Edge3 from Node3 to Node4 {
					edge-control-points : [Point(99.0, 123.19), Point(99.0, 131.21), Point(99.0, 140.95), Point(99.0, 149.93)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(99.0, 159.97)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(99.0, 123.19)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def styled_graph_with_additional_information() {
		val dot = DotTestUtils.styledGraphWithAdditionalInformation
		val zest = dot.copy

		zest.test('''
			Graph {
				Node1 {
					element-label : 1
					element-layout-irrelevant : false
					node-position : Point(27.0, 0.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					element-layout-irrelevant : false
					node-position : Point(27.0, 72.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					element-layout-irrelevant : false
					node-position : Point(27.0, 144.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : 4
					element-layout-irrelevant : false
					node-position : Point(0.0, 216.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node5 {
					element-label : 5
					element-layout-irrelevant : false
					node-position : Point(27.0, 288.0)
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-control-points : [Point(54.0, 36.303), Point(54.0, 44.017), Point(54.0, 53.288), Point(54.0, 61.888)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-end-point : Point(54.0, 71.896)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(54.0, 36.303)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-control-points : [Point(54.0, 108.3), Point(54.0, 116.02), Point(54.0, 125.29), Point(54.0, 133.89)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 1 7;
					edge-end-point : Point(54.0, 143.9)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(54.0, 108.3)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node4 {
					edge-control-points : [Point(47.601, 179.59), Point(44.486, 187.66), Point(40.666, 197.57), Point(37.165, 206.65)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-end-point : Point(33.54, 216.04)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(47.601, 179.59)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge4 from Node3 to Node5 {
					edge-control-points : [Point(57.654, 180.09), Point(59.676, 190.43), Point(61.981, 203.91), Point(63.0, 216.0), Point(64.344, 231.94), Point(64.344, 236.06), Point(63.0, 252.0), Point(62.283, 260.5), Point(60.931, 269.69), Point(59.488, 277.99)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-end-point : Point(57.654, 287.91)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(57.654, 180.09)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge5 from Node4 to Node5 {
					edge-control-points : [Point(33.399, 251.59), Point(36.514, 259.66), Point(40.334, 269.57), Point(43.835, 278.65)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(47.46, 288.04)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.conversion.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(33.399, 251.59)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs001() {
		'''
			graph {
				{
					1
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs002() {
		'''
			graph {
				subgraph {
					1
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs003() {
		'''
			graph {
				subgraph cluster1 {
					1
				}
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs004() {
		'''
			digraph {
				1
				{
					2
				}
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs005() {
		'''
			digraph {
				1
				subgraph {
					2
				}
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs006() {
		'''
			digraph {
				1
				subgraph cluster {
					2
				}
				1->2
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs007() {
		'''
			digraph {
				a1
				{
					a2
					{
						a3
					}
				}
				a1 -> a2 -> a3
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : a1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : a2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : a3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node1 to Node2 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs008() {
		'''
			digraph {
				a1
				subgraph cluster {
					a2
					{
						a3
					}
				}
				a1 -> a2 -> a3
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : a1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : a2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : a3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node3 to Node4 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs009() {
		'''
			digraph {
				a1
				{
					a2
					subgraph cluster {
						a3
					}
				}
				a1 -> a2 -> a3
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : a1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : a2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : a3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node2 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node3 to Node4 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs010() {
		'''
			digraph {
				a1
				subgraph cluster1 {
					a2
					subgraph cluster2 {
						a3
					}
				}
				a1 -> a2 -> a3
			}
		'''.assertZestConversion('''
			Graph {
				Node1 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 
					node-rect-css-style : 
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : a1
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : a2
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Node5 {
					element-label : a3
					node-shape : GeometryNode
					node-size : Dimension(54.0, 36.0)
				}
				Edge1 from Node3 to Node4 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node4 to Node5 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Test def subgraphs011() {
		'''
			graph {
				{
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs012() {
		'''
			graph {
				node [shape=box]
				{
					1
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs013() {
		'''
			graph {
				node [shape=box]
				{
					1 [shape=ellipse]
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs014() {
		'''
			graph {
				node [shape=box]
				{
					1 [shape=""]
				}
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	@Test def subgraphs015() {
		'''
			graph {
				node [shape=box]
				{
					1 [shape=""]
					2
				}
				3
				4 [shape=ellipse]
			}
		'''.assertZestConversion(new NodeShapePrettyPrinter, '''
			Graph {
				Node1 {
					element-label : 1
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node2 {
					element-label : 2
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node3 {
					element-label : 3
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
				Node4 {
					element-label : 4
					node-shape : Ellipse (0.0, 0.0, 0.0, 0.0)
					node-size : Dimension(54.0, 36.0)
				}
			}
		''')
	}

	private def assertZestConversion(CharSequence it, CharSequence expectedZestGraphText) {
		assertZestConversion(new DotGraphPrettyPrinter, expectedZestGraphText)
	}

	/**
	 * use a customized {@link DotGraphPrettyPrinter} to provide a better formatted string representation of certain attributes property
	 */
	private def assertZestConversion(CharSequence it, DotGraphPrettyPrinter prettyPrinter, CharSequence expectedZestGraphText) {
		// ensure that the input text can be parsed and the ast can be created
		val dotAst = parse
		dotAst.assertNoErrors
		
		val dotGraph = dotAst.importDot.get(0)
		val zestGraph = dotGraph.copy
		zestGraph.test(prettyPrinter, expectedZestGraphText)
	}

	private def test(Graph actual, CharSequence expected) {
		test(actual, new DotGraphPrettyPrinter, expected)
	}

	private def test(Graph actual, extension DotGraphPrettyPrinter prettyPrinter, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}

	private def test(Node actual, extension DotGraphPrettyPrinter prettyPrinter, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}

	private def test(Edge actual, extension DotGraphPrettyPrinter prettyPrinter, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}

	private def removeObjectIDs(String text) {
		// recognize substrings between '@' and the end of the line
		val nl = System.lineSeparator
		val regex = '''(@[^\\«nl»]*)'''
		
		text.replaceAll(regex, "")
	}

	private def mockAvailableFonts(String... availableFonts) {
		dot2ZestGraphCopier.attributeCopier.fontUtil.systemFontAccess = new DotFontAccessMock(availableFonts)
	}

	private static class NodeShapePrettyPrinter extends DotGraphPrettyPrinter {
		override protected prettyPrint(String attrKey, Object attrValue) {
			return if (#[
				DotProperties.INNER_SHAPE__N,
				ZestProperties.SHAPE__N
			].contains(attrKey) && attrValue instanceof GeometryNode<?>) {
				val node = attrValue as GeometryNode<?>
				'''«attrKey» : «node.geometryProperty.get»'''
			} else {
				super.prettyPrint(attrKey, attrValue)
			}
		}
	}

	private static class RecordBasedShapePrettyPrinter extends DotGraphPrettyPrinter {
		override protected prettyPrint(String attrKey, Object attrValue, String startIndent) {
			return if (#[
				DotProperties.INNER_SHAPE__N,
				ZestProperties.SHAPE__N
			].contains(attrKey) && attrValue instanceof Pane) {
				val node = attrValue as Pane
				'''«(new DotRecordBasedLabelFxPrettyPrinter).prettyPrint(node,startIndent)»'''
			} else {
				super.prettyPrint(attrKey, attrValue, startIndent)
			}
		}
	}

	private static class NodeShapeWithStylePrettyPrinter extends DotGraphPrettyPrinter {
		override protected prettyPrint(String attrKey, Object attrValue) {
			return if (#[
				DotProperties.INNER_SHAPE__N,
				ZestProperties.SHAPE__N
			].contains(attrKey) && attrValue instanceof GeometryNode<?>) {
				val node = attrValue as GeometryNode<?>
				'''«attrKey» : «node.geometryProperty.get», style: «node.style»'''
			} else {
				super.prettyPrint(attrKey, attrValue)
			}
		}
	}

	private static class EdgeDecorationPrettyPrinter extends DotGraphPrettyPrinter {
		String startIndent
		
		override protected String prettyPrint(
			ReadOnlyMapProperty<String, Object> attributesProperty,
			String startIndent) {
				this.startIndent = startIndent
				super.prettyPrint(attributesProperty, startIndent)
		}
		
		override protected prettyPrint(String attrKey, Object attrValue) {
			return if (#[
				ZestProperties.SOURCE_DECORATION__E,
				ZestProperties.TARGET_DECORATION__E
			].contains(attrKey)) {
				switch attrValue {
					Shape : '''«super.prettyPrint(attrKey, attrValue)», style: «attrValue.style»'''
					Group : '''«attrKey» : «attrValue.prettyPrint»''' 
				}
			} else {
				super.prettyPrint(attrKey, attrValue)
			}
		}
		
		private def prettyPrint(Group it) '''
			Group[
				«FOR child : children»
					«startIndent»«child», style: «child.style»
				«ENDFOR»
			«startIndent»]'''
	}
}