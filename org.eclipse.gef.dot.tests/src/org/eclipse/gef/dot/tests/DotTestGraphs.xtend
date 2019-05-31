/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     Zoey G. Prigge (itemis AG) - implement additional dot attributes (bug #461506)
 * 
 *******************************************************************************/

package org.eclipse.gef.dot.tests

class DotTestGraphs {

	public static val EMPTY = '''
		graph {}
	'''

	public static val EMPTY_DIRECTED = '''
		digraph {}
	'''

	public static val EMPTY_STRICT = '''
		strict graph {}
	'''

	public static val EMPTY_STRICT_DIRECTED = '''
		strict digraph {}
	'''

	public static val EMPTY_WITH_COMMENTS = '''
		graph {
			// This is a C++-style single line comment.
			
			/*
			 * This is a C++-style
			 * multi line comment.
			 */
			 
			 # This is considered as a line output from C-preprocessor and discarded.
		}
	'''

	public static val ONE_NODE = '''
		graph {
			1
		}
	'''

	public static val TWO_NODES = '''
		graph {
			1
			2
		}
	'''

	public static val ONE_EDGE = '''
		graph {
			1--2
		}
	'''	

	public static val ONE_DIRECTED_EDGE = '''
		digraph {
			1->2
		}
	'''

	public static val TWO_EDGES = '''
		graph {
			1--2
			3--4
		}
	'''	

	public static val TWO_NODES_ONE_EDGE = '''
		graph {
			1;2;
			1--2
		}
	'''

	public static val TWO_NODES_ONE_DIRECTED_EDGE = '''
		digraph {
			1;2;
			1->2
		}
	'''	

	public static val TWO_NODES_AND_THREE_EDGES = '''
		graph {
			1;2
			1--2
			2--2
			1--1
		}
	'''

	public static val EMPTY_NODE_ATTRIBUTE_LIST = '''
		graph {
			1[]
		}
	'''

	public static val EMPTY_EDGE_ATTRIBUTE_LIST = '''
		graph {
			1--2[]
		}
	'''

	public static val EMPTY_DIRECTED_EDGE_ATTRIBUTE_LIST = '''
		digraph {
			1->2[]
		}
	'''

	public static val EMPTY_GRAPH_ATTRIBUTE_STATEMENT = '''
		graph {
			graph[]
		}
	'''

	public static val EMPTY_NODE_ATTRIBUTE_STATEMENT = '''
		graph {
			node[]
		}
	'''

	public static val EMPTY_EDGE_ATTRIBUTE_STATEMENT = '''
		graph {
			edge[]
		}
	'''	

	public static val ESCAPED_QUOTES_LABEL = '''
		graph {
			n1[label="node \"1\""]
		}
	'''

	public static val MULTILINE_QUOTED_IDS = '''
		graph {
			n1[label="node\
		 1"]
		}
	'''

	public static val FULLY_QUOTED_IDS = '''
		graph {
			"n1"
			"n2"
			"n1"--"n2"
		}
	'''

	public static val GLOBAL_EDGE_LABEL_AD_HOC_NODES = '''
		graph {
			edge[label=TEXT]
			1--2
		}
	'''

	public static val GLOBAL_NODE_LABEL_AD_HOC_NODES = '''
		graph {
			node[label=TEXT]
			1--2
		}
	'''

	public static val GRAPH_LAYOUT_DOT_HORIZONTAL = '''
		graph {
			graph[layout=dot]
			rankdir=LR
			1
		}
	'''

	public static val HEADER_COMMENT = '''
		/*A header comment*/
		graph {
			1--2
		}
	'''

	public static val IDS_WITH_QUOTES = '''
		graph {
			"node 1"
			"node 2"
		}
	'''

	public static val MULTI_EDGE_STATEMENTS_GLOBAL = '''
		digraph {
			edge[arrowhead=ornormal]
			1->2->3->4
			1->2->3->4
		}
	'''

	public static val MULTI_EDGE_STATEMENTS_LOCAL = '''
		digraph {
			1->2->3->4[arrowhead=ornormal]
			1->2->3->4[arrowhead=olnormal]
		}
	'''

	public static val MULTI_EDGE_STATEMENTS_OVERRIDE = '''
		digraph {
			edge[arrowhead=olnormal]
			1->2->3->4[arrowhead=ornormal]
			1->2->3->4
		}
	'''	

	public static val NEW_LINES_IN_LABELS = '''
		graph {
			n1[label=
		"node
		1"]}
	'''

	public static val NODES_AFTER_EDGES = '''
		graph {
			1--2
			2--3
			2--4
			1[label="node"]
			2
			3
			4
		}
	'''

	public static val NODES_BEFORE_EDGES = '''
		graph {
			1;2;3;4
			1--2
			2--3
			2--4
		}
	'''

	public static val NODES_BEFORE_EDGES_WITH_ATTRIBUTES = '''
		digraph AttributesGraph {
			1;2;3;4
			rankdir=LR
			label="Left-to-Right"
			1->2
			1->3
			2->4
		}
	'''

	public static val DIRECTED_STYLED_GRAPH = '''
		digraph DirectedStyledGraph{
			/* global attributes can be defined to affect all edges*/
			edge[label="Edge" style=dashed]
			1; 2; 3; 4
			1->2
			/* the global attributes can be overridden for a particular edge*/
			2->3[label="Dotted" style=dotted]
			2->4
		}
	'''

	public static val NODE_GROUPS='''
		digraph {
			1->2
			1->3
			node[shape=box]
			edge[style="dashed, bold" arrowhead=odot]
			foo->{ bar baz }
		}
	'''

	public static val QUOTED_LABELS = '''
		graph {
			n1[label="node 1"]
			n2[label="node 2"]
			n1--n2[label="edge 1"]
		}
	'''

	public static val QUOTED_LABELS2 = '''
		graph {
			// the label has to be quoted because otherwise it is recognized as a keyword
			1[label="node" xlabel="Node"]
			
			// the label has to be quoted if it contains whitespaces
			2[label="foo bar"]
			
			// the label does not need to be quoted, but can be
			3[label=foo]
			
			// the label does not need to be quoted, but can be quoted
			4[label="foo"]
		}
	'''

	public static val KEYWORDS = '''
		strict digraph {
			graph[]
			node[]
			edge[]
			
			subgraph{
				
			}
		}
	'''

	public static val PORTS = '''
		graph {
			1:portID
			2:ne
			3:portID2:_
		}
	'''

	public static val COMPASS_POINTS_AS_NODE_NAMES = '''
		graph {
			/*
			 * The allowed compass point values are not keywords,
			 * so these strings can be used elsewhere as ordinary
			 * identifiers and, conversely, the parser will
			 * actually accept any identifier.
			 * 
			 * http://www.graphviz.org/content/dot-language
			 */
			n ne e se s sw w nw c _
		}
	'''

	public static val DEPRECATED_STYLES = '''
		/**
		 * The use of setlinewidth is deprecated;
		 * one should use the penwidth attribute instead.
		 */
		graph {
			1[style="setlinewidth(1)"]
			2[style="setlinewidth(2)"]
			1--2[style="setlinewidth(3)"]
		
			subgraph cluster{
				style="dashed, setlinewidth(4)"
				3[style="setlinewidth(5), dotted"]
			}
		}
	'''

	public static val GLOBAL_EDGE_NODE_COLORSCHEME = '''
		digraph G {
			edge [colorscheme=blues3 color=3]
			node [colorscheme=blues4 fontcolor=4]
			1->2
		}
	'''

/*
 ************************************************************************************************************
 * Test dot graphs with different dot attribute valid values
 ************************************************************************************************************
 */
	public static val GRAPH_LAYOUT_DOT = '''
		digraph {
			graph[layout=dot]
			1;2;3;4;
			1->2; 2->3; 2->4;
		}
	'''

	public static val GRAPH_LAYOUT_FDP = '''
		digraph {
			graph[layout=fdp]
			1;2;3;4;
			1->2; 2->3; 2->4;
		}
	'''

	public static val GRAPH_LAYOUT_OSAGE = '''
		digraph {
			graph[layout=osage]
			1;2;3;4;
			1->2; 2->3; 2->4;
		}
	'''

	public static val GRAPH_LAYOUT_TWOPI = '''
		digraph {
			graph[layout=twopi]
			1;2;3;4;
			1->2; 2->3; 2->4;
		}
	'''

	public static val CLUSTER_BGCOLOR = '''
		graph {
			subgraph clusterName {
				graph [bgcolor=red];
				1
			}
		}
	'''

	public static val CLUSTER_COLOR = '''
		graph {
			subgraph clusterName {
				graph [color=red];
				1
			}
		}
	'''

	public static val CLUSTER_COLORSCHEME = '''
		graph {
			subgraph clusterName {
				graph [colorscheme=svg];
				1
			}
		}
	'''

	public static val CLUSTER_FILLCOLOR = '''
		graph {
			subgraph clusterName {
				graph [fillcolor=red];
				1
			}
		}
	'''

	public static val CLUSTER_FONTCOLOR = '''
		graph {
			subgraph clusterName {
				graph [fontcolor=red];
				1
			}
		}
	'''

	public static val CLUSTER_FONTNAME = '''
		graph {
			subgraph clusterName {
				graph [fontname=Helvetica];
				1
			}
		}
	'''

	public static val CLUSTER_FONTSIZE = '''
		graph {
			subgraph clusterName {
				graph [fontsize=2];
				1
			}
		}
	'''

	public static val CLUSTER_ID = '''
		graph {
			subgraph clusterName {
				graph [id=FOO];
				1
			}
		}
	'''

	public static val CLUSTER_LABEL = '''
		graph {
			subgraph clusterName {
				graph [label=foo];
				1
			}
		}
	'''

	public static val CLUSTER_LP = '''
		graph {
			subgraph clusterName {
				graph [lp="-4.5,-6.7"];
				1
			}
		}
	'''

	public static val CLUSTER_PENWIDTH = '''
		graph {
			subgraph clusterName {
				graph [penwidth=2];
				1
			}
		}
	'''

	public static val CLUSTER_STYLE = '''
		graph {
			subgraph clusterName {
				graph [style=dashed];
				1
			}
		}
	'''

	public static val CLUSTER_TOOLTIP = '''
		graph {
			subgraph clusterName {
				graph [tooltip=foo];
				1
			}
		}
	'''

	public static val GRAPH_RANKDIR_LR = '''
		graph {
			rankdir=LR
			1
		}
	'''

	public static val EDGE_STYLE_INVIS = '''
		digraph {
			1->2[style=invis]
		}
	'''

	public static val COLORLIST_BGCOLOR_G= '''
		graph {
			bgcolor="red;0.75:blue;0.25"
		}
	'''

	public static val COLORLIST_BGCOLOR_C= '''
		graph {
			subgraph cluster1{
				bgcolor = "blue;0.75:red"
				1
			}
		}
	'''

	public static val COLORLIST_COLOR_E= '''
		graph {
			1--2[
				color = "blue;0.75:red;0.25"
			]
		}
	'''

	public static val COLORLIST_FILLCOLOR_N= '''
		graph {
			1[
				style=filled
				fillcolor="red;0.25:blue;0.75"
			]
		}
	'''

	public static val COLORLIST_FILLCOLOR_C= '''
		graph {
			subgraph cluster1{
				style=filled
				fillcolor="red;0.75:blue"
				1
			}
		}
	'''
	
	public static val GRAPH_COLORSCHEME_SVG = '''
		graph {
			colorscheme=svg
			1
		}
	'''
/*
 ************************************************************************************************************
 * Test dot graphs with global/local/override dot attributes
 ************************************************************************************************************
 */
	public static val EDGE_ARROWHEAD_GLOBAL = '''
		digraph {
			edge[arrowhead=crow]
			1->2
			3->4
		}
	'''

	public static val EDGE_ARROWHEAD_LOCAL = '''
		digraph {
			1->2[arrowhead=diamond]
			3->4[arrowhead=dot]
		}
	'''

	public static val EDGE_ARROWHEAD_OVERRIDE = '''
		digraph {
			edge[arrowhead=tee]
			1->2[arrowhead=vee]
			3->4
		}
	'''

	public static val EDGE_ARROWSIZE_GLOBAL = '''
		digraph {
			edge[arrowsize=1.5]
			1->2
			3->4
		}
	'''

	public static val EDGE_ARROWSIZE_LOCAL = '''
		digraph {
			1->2[arrowsize=2.0]
			3->4[arrowsize=2.1]
		}
	'''

	public static val EDGE_ARROWSIZE_OVERRIDE = '''
		digraph {
			edge[arrowsize=2.2]
			1->2[arrowsize=2.3]
			3->4
		}
	'''

	public static val EDGE_ARROWTAIL_GLOBAL = '''
		digraph {
			edge[arrowtail=box]
			1->2
			3->4
		}
	'''

	public static val EDGE_ARROWTAIL_LOCAL = '''
		digraph {
			1->2[arrowtail=lbox]
			3->4[arrowtail=rbox]
		}
	'''

	public static val EDGE_ARROWTAIL_OVERRIDE = '''
		digraph {
			edge[arrowtail=obox]
			1->2[arrowtail=olbox]
			3->4
		}
	'''

	public static val EDGE_COLOR_GLOBAL = '''
		graph {
			edge[color="0.000 0.000 1.000"]
			1--2
			3--4
		}
	'''

	public static val EDGE_COLOR_LOCAL = '''
		graph {
			1--2[color="0.000 0.000 1.000"]
			3--4[color=white]
		}
	'''

	public static val EDGE_COLOR_OVERRIDE = '''
		graph {
			edge[color="0.000 0.000 1.000"]
			1--2[color=white]
			3--4
		}
	'''

	public static val EDGE_COLORSCHEME_GLOBAL = '''
		graph {
			edge[colorscheme=accent3]
			1--2
			3--4
		}
	'''

	public static val EDGE_COLORSCHEME_LOCAL = '''
		graph {
			1--2[colorscheme=accent3]
			3--4[colorscheme=accent4]
		}
	'''

	public static val EDGE_COLORSCHEME_OVERRIDE = '''
		graph {
			edge[colorscheme=accent3]
			1--2[colorscheme=accent4]
			3--4
		}
	'''

	public static val EDGE_DIR_GLOBAL = '''
		digraph {
			edge[dir=forward]
			1->2
			3->4
		}
	'''

	public static val EDGE_DIR_LOCAL = '''
		digraph {
			1->2[dir=forward]
			3->4[dir=back]
		}
	'''

	public static val EDGE_DIR_OVERRIDE = '''
		digraph {
			edge[dir=back]
			1->2[dir=both]
			3->4
		}
	'''

	public static val EDGE_EDGETOOLTIP_GLOBAL = '''
		graph {
			edge[edgetooltip=a]
			1--2
			3--4
		}
	'''

	public static val EDGE_EDGETOOLTIP_LOCAL = '''
		graph {
			1--2[edgetooltip=b]
			3--4[edgetooltip=c]
		}
	'''

	public static val EDGE_EDGETOOLTIP_OVERRIDE = '''
		graph {
			edge[edgetooltip=d]
			1--2[edgetooltip=e]
			3--4
		}
	'''

	public static val EDGE_FILLCOLOR_GLOBAL = '''
		graph {
			edge[fillcolor="0.000 0.000 0.000"]
			1--2
			3--4
		}
	'''

	public static val EDGE_FILLCOLOR_LOCAL = '''
		graph {
			1--2[fillcolor="0.000 0.000 0.000"]
			3--4[fillcolor=black]
		}
	'''

	public static val EDGE_FILLCOLOR_OVERRIDE = '''
		graph {
			edge[fillcolor="0.000 0.000 0.000"]
			1--2[fillcolor=black]
			3--4
		}
	'''

	public static val EDGE_FONTCOLOR_GLOBAL = '''
		graph {
			edge[fontcolor="0.000 1.000 1.000"]
			1--2
			3--4
		}
	'''

	public static val EDGE_FONTCOLOR_LOCAL = '''
		graph {
			1--2[fontcolor="0.000 1.000 1.000"]
			3--4[fontcolor=red]
		}
	'''

	public static val EDGE_FONTCOLOR_OVERRIDE = '''
		graph {
			edge[fontcolor="0.000 1.000 1.000"]
			1--2[fontcolor=red]
			3--4
		}
	'''

	public static val EDGE_FONTNAME_GLOBAL = '''
		graph {
			edge[fontname=Font1]
			1--2
			3--4
		}
	'''

	public static val EDGE_FONTNAME_LOCAL = '''
		graph {
			1--2[fontname=Font1]
			3--4[fontname=Font2]
		}
	'''

	public static val EDGE_FONTNAME_OVERRIDE = '''
		graph {
			edge[fontname=Font4]
			1--2[fontname=Font3]
			3--4
		}
	'''

	public static val EDGE_FONTSIZE_GLOBAL = '''
		graph {
			edge[fontsize=1.1]
			1--2
			3--4
		}
	'''

	public static val EDGE_FONTSIZE_LOCAL = '''
		graph {
			1--2[fontsize=1.1]
			3--4[fontsize=1.2]
		}
	'''

	public static val EDGE_FONTSIZE_OVERRIDE = '''
		graph {
			edge[fontsize=1.4]
			1--2[fontsize=1.3]
			3--4
		}
	'''

	public static val EDGE_HEADLABEL_GLOBAL = '''
		graph {
			edge[headlabel=EdgeHeadLabel1]
			1--2
			3--4
		}
	'''

	public static val EDGE_HEADLABEL_LOCAL = '''
		graph {
			1--2[headlabel=EdgeHeadLabel2]
			3--4[headlabel=EdgeHeadLabel3]
		}
	'''

	public static val EDGE_HEADLABEL_OVERRIDE = '''
		graph {
			edge[headlabel=EdgeHeadLabel4]
			1--2[headlabel=EdgeHeadLabel5]
			3--4
		}
	'''

	public static val EDGE_HEADPORT_GLOBAL = '''
		graph {
			edge[headport="port5:nw"]
			1--2
			3--4
		}
	'''

	public static val EDGE_HEADPORT_LOCAL = '''
		graph {
			1--2[headport="port1:w"]
			3--4[headport="port2:e"]
		}
	'''

	public static val EDGE_HEADPORT_OVERRIDE = '''
		graph {
			edge[headport="port5:nw"]
			1--2[headport="port1:w"]
			3--4
		}
	'''

	public static val EDGE_HEADTOOLTIP_GLOBAL = '''
		digraph {
			edge[headtooltip=a]
			1->2
			3->4
		}
	'''

	public static val EDGE_HEADTOOLTIP_LOCAL = '''
		digraph {
			1->2[headtooltip=b]
			3->4[headtooltip=c]
		}
	'''

	public static val EDGE_HEADTOOLTIP_OVERRIDE = '''
		digraph {
			edge[headtooltip=d]
			1->2[headtooltip=e]
			3->4
		}
	'''

	public static val EDGE_HEAD_LP_LOCAL = '''
		graph {
			1--2[head_lp="2.2,3.3"  ]
			3--4[head_lp="-2.2,-3.3"]
		}
	'''

	public static val EDGE_ID_LOCAL = '''
		graph {
			1--2[id=edgeID2]
			3--4[id=edgeID3]
		}
	'''

	public static val EDGE_LABEL_GLOBAL = '''
		graph {
			edge[label=Edge1]
			1--2
			3--4
		}
	'''

	public static val EDGE_LABEL_LOCAL = '''
		graph {
			1--2[label=Edge1]
			3--4[label=Edge2]
		}
	'''

	public static val EDGE_LABEL_OVERRIDE = '''
		graph {
			edge[label=Edge3]
			1--2[label=Edge4]
			3--4
		}
	'''

	public static val EDGE_LABELFONTCOLOR_GLOBAL = '''
		graph {
			edge[labelfontcolor="0.482 0.714 0.878"]
			1--2
			3--4
		}
	'''

	public static val EDGE_LABELFONTCOLOR_LOCAL = '''
		graph {
			1--2[labelfontcolor="0.482 0.714 0.878"]
			3--4[labelfontcolor=turquoise]
		}
	'''

	public static val EDGE_LABELFONTCOLOR_OVERRIDE = '''
		graph {
			edge[labelfontcolor="0.482 0.714 0.878"]
			1--2[labelfontcolor=turquoise]
			3--4
		}
	'''

	public static val EDGE_LABELFONTNAME_GLOBAL = '''
		graph {
			edge[labelfontname=Font1]
			1--2
			3--4
		}
	'''

	public static val EDGE_LABELFONTNAME_LOCAL = '''
		graph {
			1--2[labelfontname=Font1]
			3--4[labelfontname=Font2]
		}
	'''

	public static val EDGE_LABELFONTNAME_OVERRIDE = '''
		graph {
			edge[labelfontname=Font4]
			1--2[labelfontname=Font3]
			3--4
		}
	'''

	public static val EDGE_LABELFONTSIZE_GLOBAL = '''
		graph {
			edge[labelfontsize=1.1]
			1--2
			3--4
		}
	'''

	public static val EDGE_LABELFONTSIZE_LOCAL = '''
		graph {
			1--2[labelfontsize=1.1]
			3--4[labelfontsize=1.2]
		}
	'''

	public static val EDGE_LABELFONTSIZE_OVERRIDE = '''
		graph {
			edge[labelfontsize=1.4]
			1--2[labelfontsize=1.3]
			3--4
		}
	'''

	public static val EDGE_LABELTOOLTIP_GLOBAL = '''
		graph {
			edge[labeltooltip=a]
			1--2
			3--4
		}
	'''

	public static val EDGE_LABELTOOLTIP_LOCAL = '''
		graph {
			1--2[labeltooltip=b]
			3--4[labeltooltip=c]
		}
	'''

	public static val EDGE_LABELTOOLTIP_OVERRIDE = '''
		graph {
			edge[labeltooltip=d]
			1--2[labeltooltip=e]
			3--4
		}
	'''

	public static val EDGE_LP_LOCAL = '''
		graph {
			1--2[lp="0.3,0.4"]
			3--4[lp="0.5,0.6"]
		}
	'''

	public static val EDGE_PENWIDTH_GLOBAL = '''
		graph {
			edge[penwidth=1.5]
			1--2
			3--4
		}
	'''

	public static val EDGE_PENWIDTH_LOCAL = '''
		graph {
			1--2[penwidth=2.5]
			3--4[penwidth=3.0]
		}
	'''

	public static val EDGE_PENWIDTH_OVERRIDE = '''
		graph {
			edge[penwidth=4.0]
			1--2[penwidth=3.5]
			3--4
		}
	'''

	public static val EDGE_POS_LOCAL = '''
		graph {
			1--2[pos="0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0"]
			3--4[pos="4.0,4.0 5.0,5.0 6.0,6.0 7.0,7.0"]
		}
	'''

	public static val EDGE_STYLE_GLOBAL = '''
		graph {
			edge[style=dashed]
			1--2
			3--4
		}
	'''

	public static val EDGE_STYLE_LOCAL = '''
		graph {
			1--2[style=dashed]
			3--4[style=dotted]
		}
	'''

	public static val EDGE_STYLE_OVERRIDE = '''
		graph {
			edge[style=bold]
			1--2[style="bold, dotted"]
			3--4
		}
	'''

	public static val EDGE_TAILLABEL_GLOBAL = '''
		graph {
			edge[taillabel=EdgeTailLabel1]
			1--2
			3--4
		}
	'''

	public static val EDGE_TAILLABEL_LOCAL = '''
		graph {
			1--2[taillabel=EdgeTailLabel2]
			3--4[taillabel=EdgeTailLabel3]
		}
	'''

	public static val EDGE_TAILLABEL_OVERRIDE = '''
		graph {
			edge[taillabel=EdgeTailLabel4]
			1--2[taillabel=EdgeTailLabel5]
			3--4
		}
	'''

	public static val EDGE_TAILPORT_GLOBAL = '''
		graph {
			edge[tailport="port5:nw"]
			1--2
			3--4
		}
	'''

	public static val EDGE_TAILPORT_LOCAL = '''
		graph {
			1--2[tailport="port1:w"]
			3--4[tailport="port2:e"]
		}
	'''

	public static val EDGE_TAILPORT_OVERRIDE = '''
		graph {
			edge[tailport="port5:nw"]
			1--2[tailport="port1:w"]
			3--4
		}
	'''

	public static val EDGE_TAILTOOLTIP_GLOBAL = '''
		digraph {
			edge[tailtooltip=a]
			1->2
			3->4
		}
	'''

	public static val EDGE_TAILTOOLTIP_LOCAL = '''
		digraph {
			1->2[tailtooltip=b]
			3->4[tailtooltip=c]
		}
	'''

	public static val EDGE_TAILTOOLTIP_OVERRIDE = '''
		digraph {
			edge[tailtooltip=d]
			1->2[tailtooltip=e]
			3->4
		}
	'''

	public static val EDGE_TAIL_LP_LOCAL = '''
		graph {
			1--2[tail_lp="-4.5,-6.7"  ]
			3--4[tail_lp="-8.9,-10.11"]
		}
	'''

	public static val EDGE_TOOLTIP_GLOBAL = '''
		graph {
			edge[tooltip=a]
			1--2
			3--4
		}
	'''

	public static val EDGE_TOOLTIP_LOCAL = '''
		graph {
			1--2[tooltip=b]
			3--4[tooltip=c]
		}
	'''

	public static val EDGE_TOOLTIP_OVERRIDE = '''
		graph {
			edge[tooltip=d]
			1--2[tooltip=e]
			3--4
		}
	'''

	public static val EDGE_XLABEL_GLOBAL = '''
		graph {
			edge[xlabel=EdgeExternalLabel1]
			1--2
			3--4
		}
	'''

	public static val EDGE_XLABEL_LOCAL = '''
		graph {
			1--2[xlabel=EdgeExternalLabel2]
			3--4[xlabel=EdgeExternalLabel3]
		}
	'''

	public static val EDGE_XLABEL_OVERRIDE = '''
		graph {
			edge[xlabel=EdgeExternalLabel4]
			1--2[xlabel=EdgeExternalLabel5]
			3--4
		}
	'''

	public static val EDGE_XLP_LOCAL = '''
		graph {
			1--2[xlp=".3,.4"]
			3--4[xlp=".5,.6"]
		}
	'''

	public static val GRAPH_BGCOLOR_LOCAL = '''
		graph {
			bgcolor=gray
			1
		}
	'''

	public static val GRAPH_FONTCOLOR_GLOBAL = '''
		graph {
			graph[fontcolor=aquamarine]
			1
		}
	'''

	public static val GRAPH_FONTCOLOR_LOCAL = '''
		graph {
			fontcolor=red
			1
		}
	'''

	public static val NODE_COLOR_GLOBAL = '''
		graph {
			node[color="#ffffff"]
			1
			2
		}
	'''

	public static val NODE_COLOR_LOCAL = '''
		graph {
			1[color="#ff0000"]
			2[color="#00ffff"]
		}
	'''

	public static val NODE_COLOR_OVERRIDE = '''
		graph {
			node[color="#ff0000"]
			1[color="#00ff00"]
			2
		}
	'''

	public static val NODE_COLORSCHEME_GLOBAL = '''
		graph {
			node[colorscheme=accent5]
			1
			2
		}
	'''

	public static val NODE_COLORSCHEME_LOCAL = '''
		graph {
			1[colorscheme=accent5]
			2[colorscheme=accent6]
		}
	'''

	public static val NODE_COLORSCHEME_OVERRIDE = '''
		graph {
			node[colorscheme=accent5]
			1[colorscheme=accent6]
			2
		}
	'''

	public static val NODE_DISTORTION_GLOBAL = '''
		graph {
			node[distortion=1.1]
			1
			2
		}
	'''

	public static val NODE_DISTORTION_LOCAL = '''
		graph {
			1[distortion=1.2]
			2[distortion=1.3]
		}
	'''

	public static val NODE_DISTORTION_OVERRIDE = '''
		graph {
			node[distortion=1.4]
			1[distortion=1.5]
			2
		}
	'''

	public static val NODE_FILLCOLOR_GLOBAL = '''
		graph {
			node[fillcolor="0.3 .8 .7"]
			1
			2
		}
	'''

	public static val NODE_FILLCOLOR_LOCAL = '''
		graph {
			1[fillcolor="0.3 .8 .7"]
			2[fillcolor="/bugn9/7"]
		}
	'''

	public static val NODE_FILLCOLOR_OVERRIDE = '''
		graph {
			node[fillcolor="0.3 .8 .7"]
			1[fillcolor="/bugn9/7"]
			2
		}
	'''

	public static val NODE_FIXEDSIZE_GLOBAL = '''
		graph {
			node[fixedsize=true]
			1
			2
		}
	'''

	public static val NODE_FIXEDSIZE_LOCAL = '''
		graph {
			1[fixedsize=true]
			2[fixedsize=false]
		}
	'''

	public static val NODE_FIXEDSIZE_OVERRIDE = '''
		graph {
			node[fixedsize=true]
			1[fixedsize=false]
			2
		}
	'''

	public static val NODE_FONTCOLOR_GLOBAL = '''
		graph {
			node[fontcolor="0.3, .8, .7"]
			1
			2
		}
	'''

	public static val NODE_FONTCOLOR_LOCAL = '''
		graph {
			1[fontcolor="0.3, .8, .7"]
			2[fontcolor="/brbg11/10"]
		}
	'''

	public static val NODE_FONTCOLOR_OVERRIDE = '''
		graph {
			node[fontcolor="0.3, .8, .7"]
			1[fontcolor="/brbg11/10"]
			2
		}
	'''

	public static val NODE_FONTNAME_GLOBAL = '''
		graph {
			node[fontname=Font1]
			1
			2
		}
	'''

	public static val NODE_FONTNAME_LOCAL = '''
		graph {
			1[fontname=Font1]
			2[fontname=Font2]
		}
	'''

	public static val NODE_FONTNAME_OVERRIDE = '''
		graph {
			node[fontname=Font4]
			1[fontname=Font3]
			2
		}
	'''

	public static val NODE_FONTSIZE_GLOBAL = '''
		graph {
			node[fontsize=1.1]
			1
			2
		}
	'''

	public static val NODE_FONTSIZE_LOCAL = '''
		graph {
			1[fontsize=1.1]
			2[fontsize=1.2]
		}
	'''

	public static val NODE_FONTSIZE_OVERRIDE = '''
		graph {
			node[fontsize=1.4]
			1[fontsize=1.3]
			2
		}
	'''

	public static val NODE_HEIGHT_GLOBAL = '''
		graph {
			node[height=1.2]
			1
			2
		}
	'''

	public static val NODE_HEIGHT_LOCAL = '''
		graph {
			1[height=3.4]
			2[height=5.6]
		}
	'''

	public static val NODE_HEIGHT_OVERRIDE = '''
		graph {
			node[height=7.8]
			1[height=9.11]
			2
		}
	'''

	public static val NODE_ID_LOCAL = '''
		graph {
			1[id=NodeID1]
			2[id=NodeID2]
		}
	'''

	public static val NODE_LABEL_GLOBAL = '''
		graph {
			node[label=Node1]
			1
			2
		}
	'''

	public static val NODE_LABEL_LOCAL = '''
		graph {
			1[label=Node1]
			2[label=Node2]
		}
	'''

	public static val NODE_LABEL_OVERRIDE = '''
		graph {
			node[label=Käse]
			1[label=Gültig]
			2
		}
	'''

	public static val NODE_LABEL_OVERRIDE2 = '''
		graph {
			node[label=Käse]
			1[label=Gültig]
			1--2
		}
	'''

	public static val NODE_LABEL_OVERRIDE3 = '''
		graph {
			1--2
			node[label=Node2]
			1
			2[label=Node1]
			3
			node[label=Node3]
			3--4
		}
	'''

	public static val NODE_LABEL_OVERRIDE4 = '''
		digraph {
			node[label="Node"]
			1; 2
			3[label="Leaf"]
			4
			1->2
			2->3
		}
	'''

	public static val NODE_PENWIDTH_GLOBAL = '''
		graph {
			node[penwidth=1.5]
			1
			2
		}
	'''

	public static val NODE_PENWIDTH_LOCAL = '''
		graph {
			1[penwidth=2.5]
			2[penwidth=3.0]
		}
	'''

	public static val NODE_PENWIDTH_OVERRIDE = '''
		graph {
			node[penwidth=4.0]
			1[penwidth=3.5]
			2
		}
	'''

	public static val NODE_POS_LOCAL = '''
		graph {
			1[pos=".1,.2!"]
			2[pos="-0.1,-2.3!"]
		}
	'''

	public static val NODE_SHAPE_GLOBAL = '''
		graph {
			node[shape=box]
			1
			2
		}
	'''

	public static val NODE_SHAPE_LOCAL = '''
		graph {
			1[shape=oval]
			2[shape=house]
		}
	'''

	public static val NODE_SHAPE_OVERRIDE = '''
		graph {
			node[shape=pentagon]
			1[shape=circle]
			2
		}
	'''

	public static val NODE_SIDES_GLOBAL = '''
		graph {
			node[sides=3]
			1
			2
		}
	'''

	public static val NODE_SIDES_LOCAL = '''
		graph {
			1[sides=4]
			2[sides=5]
		}
	'''

	public static val NODE_SIDES_OVERRIDE = '''
		graph {
			node[sides=6]
			1[sides=7]
			2
		}
	'''

	public static val NODE_SKEW_GLOBAL = '''
		graph {
			node[skew=1.2]
			1
			2
		}
	'''

	public static val NODE_SKEW_LOCAL = '''
		graph {
			1[skew=3.4]
			2[skew=5.6]
		}
	'''

	public static val NODE_SKEW_OVERRIDE = '''
		graph {
			node[skew=7.8]
			1[skew=-7.8]
			2
		}
	'''

	public static val NODE_STYLE_GLOBAL = '''
		graph {
			node[style="solid, dashed"]
			1
			2
		}
	'''

	public static val NODE_STYLE_LOCAL = '''
		graph {
			1[style=bold]
			2[style=dotted]
		}
	'''

	public static val NODE_STYLE_OVERRIDE = '''
		graph {
			node[style="bold, filled"]
			1[style=rounded]
			2
		}
	'''

	public static val NODE_TOOLTIP_GLOBAL = '''
		graph {
			node[tooltip=a]
			1
			2
		}
	'''

	public static val NODE_TOOLTIP_LOCAL = '''
		graph {
			1[tooltip=b]
			2[tooltip=c]
		}
	'''

	public static val NODE_TOOLTIP_OVERRIDE = '''
		graph {
			node[tooltip=d]
			1[tooltip=e]
			2
		}
	'''

	public static val NODE_WIDTH_GLOBAL = '''
		graph {
			node[width=1.2]
			1
			2
		}
	'''

	public static val NODE_WIDTH_LOCAL = '''
		graph {
			1[width=3.4]
			2[width=5.6]
		}
	'''

	public static val NODE_WIDTH_OVERRIDE = '''
		graph {
			node[width=7.8]
			1[width=9.11]
			2
		}
	'''

	public static val NODE_XLABEL_GLOBAL = '''
		graph {
			node[xlabel=NodeExternalLabel1]
			1
			2
		}
	'''

	public static val NODE_XLABEL_LOCAL = '''
		graph {
			1[xlabel=NodeExternalLabel2]
			2[xlabel=NodeExternalLabel3]
		}
	'''

	public static val NODE_XLABEL_OVERRIDE = '''
		graph {
			node[xlabel=NodeExternalLabel4]
			1[xlabel=NodeExternalLabel5]
			2
		}
	'''

	public static val NODE_XLP_LOCAL = '''
		graph {
			1[xlp="-0.3,-0.4"]
			2[xlp="-1.5,-1.6"]
		}
	'''

	public static val INCOMPLETE_HTML_LIKE_LABEL = '''
		digraph structs {
			node [shape=plaintext]
			struct [label=<
				<TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0" bgcolor="blue">
					<TR><TD><fonttext2</font></TD></TR>
				</TABLE>>];
			// need something after this to cause error
			struct:w -> struct:e;
		}
	'''

	public static val CLUSTERS = '''
		digraph {
			subgraph cluster1 {
				a; 
				b;
				a -> b;
			}
			subgraph cluster2 {
				p;
				q;
				r; 
				s;
				t;
				p -> q;
				q -> r;
				r -> s;
				s -> t;
				t -> p; 
			}
			b -> q;
			t -> a;
		}
	'''

	public static val CLUSTER_MERGE = '''
		digraph {
			subgraph cluster1 {
				a; 
				b;
				a -> b;
			}
			subgraph cluster1 {
				c;
				d;
				c -> d;
			}
			a -> c;
			b -> d;
		}
	'''

	public static val CLUSTER_SCOPE = '''
		graph {
			node [shape="hexagon", style="filled", fillcolor="blue"];
			{ node [shape="box"]; a; b; }
			{ node [fillcolor="red"]; b; c; }
		}
	'''

/*
 ************************************************************************************************************
 * Test dot graphs with parameterized string templates
 ************************************************************************************************************
 */
	static def CLUSTER_LABEL_HTML_LIKE(String htmlLabel)'''
		graph {
			subgraph cluster {
				label =
					<
						«htmlLabel»
					>
				1 2 3 4
			}
		}
	'''

	static def EDGE_HEADLABEL_HTML_LIKE(String htmlLabel)'''
		graph {
			1--2[
				headlabel=
					<
						«htmlLabel»
					>
			]
		}
	'''

	static def EDGE_LABEL_HTML_LIKE(String htmlLabel)'''
		graph {
			1--2[
				label=
					<
						«htmlLabel»
					>
			]
		}
	'''

	static def EDGE_TAILLABEL_HTML_LIKE(String htmlLabel)'''
		graph {
			1--2[
				taillabel=
					<
						«htmlLabel»
					>
			]
		}
	'''

	static def EDGE_XLABEL_HTML_LIKE(String htmlLabel)'''
		graph {
			1--2[
				xlabel=
					<
						«htmlLabel»
					>
			]
		}
	'''

	static def GRAPH_LABEL_HTML_LIKE(String htmlLabel)'''
		graph {
			label =
				<
					«htmlLabel»
				>
		}
	'''

	static def NODE_LABEL_HTML_LIKE(String htmlLabel)'''
		graph {
				1[label =
					<
						«htmlLabel»
					>
			]
		}
	'''

	static def NODE_XLABEL_HTML_LIKE(String htmlLabel)'''
		graph {
				1[xlabel =
					<
						«htmlLabel»
					>
			]
		}
	'''
}