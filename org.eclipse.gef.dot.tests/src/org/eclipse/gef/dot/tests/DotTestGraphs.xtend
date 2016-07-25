/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
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
	
	public static val MULTI_EDGE_STATEMENTS = '''
		digraph {
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
	
	public static val QUOTED_LABELS = '''
		graph {
			n1[label="node 1"]
			n2[label="node 2"]
			n1--n2[label="edge 1"]
		}
	'''
	
	
/*
 ************************************************************************************************************
 * Sample dot graphs with different dot attribute valid values
 ************************************************************************************************************
 */

	public static val GRAPH_LAYOUT_DOT = '''
		graph {
			graph[layout=dot]
			1
		}
	'''
	
	public static val GRAPH_LAYOUT_FDP = '''
		graph {
			graph[layout=fdp]
			1
		}
	'''	
	
	public static val GRAPH_LAYOUT_OSAGE = '''
		graph {
			graph[layout=osage]
			1
		}
	'''
	
	public static val GRAPH_LAYOUT_TWOPI = '''
		graph {
			graph[layout=twopi]
			1
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

/*
 ************************************************************************************************************
 * Sample dot graphs with global/local/override dot attributes
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
	
	public static val EDGE_LP_LOCAL = '''
		graph {
			1--2[lp="0.3,0.4"]
			3--4[lp="0.5,0.6"]
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
	
	public static val EDGE_TAIL_LP_LOCAL = '''
		graph {
			1--2[tail_lp="-4.5,-6.7"  ]
			3--4[tail_lp="-8.9,-10.11"]
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
			node[label=Node3]
			1[label=Node4]
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
			1[skew="-7.8"]
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
}