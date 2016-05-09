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

package org.eclipse.gef4.dot.tests

class DotSampleGraphs {
	
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
			edge[label="TEXT"]
			1--2
		}
	'''
	
	public static val GLOBAL_NODE_LABEL_AD_HOC_NODES = '''
		graph {
			node[label="TEXT"]
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
 * Sample dot graphs with local/global/override dot attributes
 ************************************************************************************************************
 */
	
	public static val NODE_LABEL_LOCAL = '''
		graph {
			1[label="Node1"]
		}
	'''
	
	public static val NODE_LABEL_GLOBAL = '''
		graph {
			node[label="Node1"]
			1
		}
	'''	
	
	public static val EDGE_LABEL_LOCAL = '''
		graph {
			1;2;
			1--2[label="Edge1"]
		}
	'''
	
	public static val EDGE_LABEL_GLOBAL = '''
		graph {
			edge[label="Edge1"]
			1;2
			1--2
		}
	'''
	
	public static val EDGE_STYLE_GLOBAL = '''
		graph {
			edge[style=dashed]
			1;2
			1--2
		}
	'''
	
	public static val EDGE_STYLE_LOCAL = '''
		graph {
			1;2;
			1->2[style=dashed]
		}
	'''
}