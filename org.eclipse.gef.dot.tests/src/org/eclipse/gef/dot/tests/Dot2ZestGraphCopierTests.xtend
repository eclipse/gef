/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.DotImport
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.Dot2ZestGraphCopier
import org.eclipse.gef.dot.internal.ui.DotNodePart
import org.eclipse.gef.fx.nodes.GeometryNode
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.gef.zest.fx.ZestProperties
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Before
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
class Dot2ZestGraphCopierTests {

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
	extension DotGraphPrettyPrinter prettyPrinter

	@BeforeClass
	def static void beforeClass() {
		dotImport = new DotImport
		
		dot2ZestGraphCopier = new Dot2ZestGraphCopier
		dot2ZestGraphCopier.attributeCopier.options.emulateLayout = false
	}
	
	@Before
	def void before() {
		prettyPrinter = new DotGraphPrettyPrinter
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
	
	@Test def edge_label() {
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
	
	@Test def edge_label_and_id() {
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
	
	@Test def node_label() {
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
	
	@Test def node_label_and_id() {
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
	
	@Test def node_shape() {
		// use a customized pretty printer to provide a better formatted string representation of certain attributes property
		prettyPrinter = new DotGraphPrettyPrinter {
			
			override protected prettyPrint(String attrKey, Object attrValue) {
				return if (#[
					DotNodePart.DOT_PROPERTY_INNER_SHAPE__N,
					ZestProperties.SHAPE__N
				].contains(attrKey) && attrValue instanceof GeometryNode<?>) {
					val geometry = (attrValue as GeometryNode<?>).geometryProperty.get
					attrKey + " : " + geometry
				} else {
					super.prettyPrint(attrKey, attrValue)
				}
			}
		}
		
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
		'''.assertZestConversion('''
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
					node-shape : Rectangle: (0.0, 0.0, 0.0, 0.0)
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
		// use a customized pretty printer to provide a better formatted string representation of certain attributes property
		prettyPrinter = new DotGraphPrettyPrinter {
			
			override protected prettyPrint(String attrKey, Object attrValue) {
				if (attrKey == ZestProperties.SHAPE__N
							&& attrValue instanceof GeometryNode<?>) {
					val geometry = (attrValue as GeometryNode<?>).geometryProperty.get
					return attrKey + " : " + geometry
				} else {
					return super.prettyPrint(attrKey, attrValue)
				}
			}
		}
		
		'''
			graph RoundedStyledPolygonBasedNodeShapes {
				node[style=rounded]
				box[shape=box]
				rect[shape=rect]
				rectangle[shape=rectangle]
				square[shape=square]
			}
		'''.assertZestConversion('''
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
	
	@Test def node_xlabel(){
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
	@Test def node_xlabel_with_layout_information(){
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

	@Test def simpleGraph() {
		val dot = DotTestUtils.simpleGraph
		val zest = dot.copy

		// test graph
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
				}
				Edge2 from Node1 to Node3 {
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
				}
			}
		''')
		
		// test node
		zest.nodes.get(0).test('''
			Node1 {
				element-label : 1
				node-shape : GeometryNode
				node-size : Dimension(54.0, 36.0)
			}
		''')
		
		// test edge
		zest.edges.get(0).test('''
			Edge1 from Node1 to Node2 {
				edge-curve : GeometryNode
				edge-curve-css-style : -fx-stroke-line-cap: butt;
			}
		''')
	}

	@Test def directedGraph() {
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

	@Test def labeledGraph() {
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

	@Test def styledGraph() {
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
	
	@Test def dotClusterGraphPrettyPrintTest() {
		val dot = DotTestUtils.clusteredGraph
		'''
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
		'''.toString.assertEquals(prettyPrinter.prettyPrint(dot))
	}
	
	@Test def dotNestedClusterGraphPrettyPrintTest() {
		val dot = DotTestUtils.nestedClusteredGraph
		'''
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
		'''.toString.assertEquals(prettyPrinter.prettyPrint(dot))
	}
	
	@Test def simpleGraphWithAdditionalInformation() {
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
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(54.65, 35.235)
				}
				Edge2 from Node1 to Node3 {
					edge-control-points : [Point(71.35, 35.235), Point(77.165, 46.544), Point(84.89, 61.563), Point(90.696, 72.853)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(90.696, 72.853)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(71.35, 35.235)
				}
			}
		''')
	}

	@Test def directedGraphWithAdditionalInformation() {
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
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(27.0, 36.303)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-control-points : [Point(27.0, 108.3), Point(27.0, 116.02), Point(27.0, 125.29), Point(27.0, 133.89)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(27.0, 143.9)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(27.0, 108.3)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}

	@Ignore("Failing on Travis/Jenkins")
	@Test def labeledGraphWithAdditionalInformation() {
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
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-label-position : Point(42.6611328125, 53.51953125)
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(48.536, 36.201)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff, stroke=0x000000ff, strokeWidth=1.0]
					element-label : +1
				}
				Edge2 from Node1 to Node3 {
					edge-control-points : [Point(62.891, 35.793), Point(69.384, 48.058), Point(78.299, 64.898), Point(85.646, 78.776)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(90.433, 87.818)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-label-position : Point(79.6611328125, 53.51953125)
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(62.891, 35.793)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff, stroke=0x000000ff, strokeWidth=1.0]
					element-label : +2
				}
				Edge3 from Node3 to Node4 {
					edge-control-points : [Point(99.0, 123.19), Point(99.0, 131.21), Point(99.0, 140.95), Point(99.0, 149.93)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(99.0, 159.97)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(99.0, 123.19)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff, stroke=0x000000ff, strokeWidth=1.0]
				}
			}
		''')
	}

	@Test def styledGraphWithAdditionalInformation() {
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
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(54.0, 36.303)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge2 from Node2 to Node3 {
					edge-control-points : [Point(54.0, 108.3), Point(54.0, 116.02), Point(54.0, 125.29), Point(54.0, 133.89)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 1 7;
					edge-end-point : Point(54.0, 143.9)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(54.0, 108.3)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge3 from Node3 to Node4 {
					edge-control-points : [Point(47.601, 179.59), Point(44.486, 187.66), Point(40.666, 197.57), Point(37.165, 206.65)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-end-point : Point(33.54, 216.04)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(47.601, 179.59)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge4 from Node3 to Node5 {
					edge-control-points : [Point(57.654, 180.09), Point(59.676, 190.43), Point(61.981, 203.91), Point(63.0, 216.0), Point(64.344, 231.94), Point(64.344, 236.06), Point(63.0, 252.0), Point(62.283, 260.5), Point(60.931, 269.69), Point(59.488, 277.99)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-dash-array: 7 7;
					edge-end-point : Point(57.654, 287.91)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(57.654, 180.09)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
				Edge5 from Node4 to Node5 {
					edge-control-points : [Point(33.399, 251.59), Point(36.514, 259.66), Point(40.334, 269.57), Point(43.835, 278.65)]
					edge-curve : GeometryNode
					edge-curve-css-style : -fx-stroke-line-cap: butt;
					edge-end-point : Point(47.46, 288.04)
					edge-interpolator : org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
					edge-router : org.eclipse.gef.fx.nodes.StraightRouter
					edge-start-point : Point(33.399, 251.59)
					edge-target-decoration : Polygon[points=[0.0, 0.0, 10.0, -3.3333333333333335, 10.0, 3.3333333333333335], fill=0x000000ff]
				}
			}
		''')
	}
	
	@Test def subgraph() {
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
	
	private def assertZestConversion(CharSequence dotText, CharSequence expectedZestGraphText) {
		// ensure that the input text can be parsed and the ast can be created
		val dotAst = dotText.parse
		dotAst.assertNoErrors
		
		val dotGraph = dotAst.importDot.get(0)
		val zestGraph = dotGraph.copy
		zestGraph.test(expectedZestGraphText)
	}

	private def test(Graph actual, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}
	
	private def test(Node actual, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}
	
	private def test(Edge actual, CharSequence expected) {
		// compare the string representation removing the objectIDs
		expected.toString.assertEquals(actual.prettyPrint.removeObjectIDs)
	}
	
	private def removeObjectIDs(String text){
		// recognize substrings between '@' and the end of the line
		val nl = System.lineSeparator
		val regex = '''(@[^\\«nl»]*)'''
		
		text.replaceAll(regex, "")
	}
}