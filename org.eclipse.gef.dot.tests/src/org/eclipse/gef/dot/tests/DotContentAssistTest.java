/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation (bug #498324)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import org.eclipse.gef.dot.internal.parser.DotUiInjectorProvider;
import org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public class DotContentAssistTest extends AbstractContentAssistTest {

	@BeforeClass
	public static void initializeStatusHandlerRegistry() throws Exception {
		/**
		 * Initialize the
		 * org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry before
		 * executing the test cases, otherwise it will be initialized after the
		 * test case executions resulting in a NullPointerException.
		 * 
		 * For more information, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=460996
		 */
		StatusHandlerRegistry.getDefault();
	}

	@Test
	public void empty() throws Exception {
		newBuilder().assertText("strict", "graph", "digraph")
				.applyProposal("strict").expectContent("strict");
	}

	@Test
	public void strict() throws Exception {
		newBuilder().append("strict ").assertText("graph", "digraph")
				.applyProposal("digraph").expectContent("strict digraph");
	}

	@Test
	public void compass_pt_with_portID() throws Exception {
		newBuilder().append("graph {1:portID:  }")
				.assertTextAtCursorPosition(16, ":", "n", "ne", "e", "se", "s",
						"sw", "w", "nw", "c", "_")
				.applyProposal(16, "ne").expectContent("graph {1:portID:ne  }");
	}

	@Test
	public void compass_pt_without_portID() throws Exception {
		newBuilder().append("graph {1: }")
				.assertTextAtCursorPosition(9, ":", "n", "ne", "e", "se", "s",
						"sw", "w", "nw", "c", "_")
				.applyProposal(9, "nw").expectContent("graph {1:nw }");
	}

	@Test
	public void edge_attributes() throws Exception {
		// test global attribute names
		newBuilder().append("graph {edge[]}")
				.assertTextAtCursorPosition(12, "]", "arrowhead", "arrowsize",
						"arrowtail", "dir", "headlabel", "head_lp", "id",
						"label", "lp", "pos", "style", "taillabel", "tail_lp",
						"xlabel", "xlp")
				.applyProposal(12, "arrowhead")
				.expectContent("graph {edge[arrowhead]}");

		// test local attribute names
		newBuilder().append("graph {1--2[  ]}")
				.assertTextAtCursorPosition(13, "]", "arrowhead", "arrowsize",
						"arrowtail", "dir", "headlabel", "head_lp", "id",
						"label", "lp", "pos", "style", "taillabel", "tail_lp",
						"xlabel", "xlp")
				.applyProposal(13, "arrowtail")
				.expectContent("graph {1--2[ arrowtail ]}");

		// test local attribute names with prefix
		newBuilder().append("graph {1--2[ a ]}")
				.assertTextAtCursorPosition(14, "=", "arrowhead", "arrowsize",
						"arrowtail")
				.applyProposal(14, "arrowsize")
				.expectContent("graph {1--2[ arrowsize ]}");
	}

	@Test
	public void edge_arrowhead() throws Exception {
		// test global attribute values
		newBuilder().append("digraph {edge[ arrowhead= ]}")
				.assertTextAtCursorPosition(25, "o", "l", "r", "box", "crow",
						"curve", "icurve", "diamond", "dot", "inv", "none",
						"normal", "tee", "vee")
				.applyProposal(25, "box")
				.expectContent("digraph {edge[ arrowhead=box ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ arrowhead= ]}")
				.assertTextAtCursorPosition(25, "o", "l", "r", "box", "crow",
						"curve", "icurve", "diamond", "dot", "inv", "none",
						"normal", "tee", "vee")
				.applyProposal(25, "diamond")
				.expectContent("digraph {1->2[ arrowhead=diamond ]}");

		// test local attribute values with quotes
		newBuilder().append("digraph {1->2[ arrowhead=\"\" ]}")
				.assertTextAtCursorPosition(26, "o", "l", "r", "box", "crow",
						"curve", "icurve", "diamond", "dot", "inv", "none",
						"normal", "tee", "vee")
				.applyProposal(26, "dot")
				.expectContent("digraph {1->2[ arrowhead=\"dot\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ arrowhead=c ]}")
				.assertTextAtCursorPosition(26, "crow", "curve", ",", ";", "]")
				.applyProposal(26, "crow")
				.expectContent("digraph {1->2[ arrowhead=crow ]}");

		newBuilder().append("digraph {1->2[ arrowhead=o ]}")
				.assertTextAtCursorPosition(26, "o", "l", "r", "box", "diamond",
						"dot", "inv", "normal", ",", ";", "]")
				.applyProposal(26, "box")
				.expectContent("digraph {1->2[ arrowhead=obox ]}");

		newBuilder().append("digraph {1->2[ arrowhead=l ]}")
				.assertTextAtCursorPosition(26, "l", "box", "crow", "curve",
						"icurve", "diamond", "inv", "normal", "tee", "vee", ",",
						";", "]")
				.applyProposal(26, "diamond")
				.expectContent("digraph {1->2[ arrowhead=ldiamond ]}");

		newBuilder().append("digraph {1->2[ arrowhead=ol ]}")
				.assertTextAtCursorPosition(27, "l", "box", "diamond", "inv",
						"normal", ",", ";", "]")
				.applyProposal(27, "diamond")
				.expectContent("digraph {1->2[ arrowhead=oldiamond ]}");

		newBuilder().append("digraph {1->2[ arrowhead=ordia ]}")
				.assertTextAtCursorPosition(30, "diamond", ",", ";", "]")
				.applyProposal(30, "diamond")
				.expectContent("digraph {1->2[ arrowhead=ordiamond ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ arrowhead=\"c ]}")
				.assertTextAtCursorPosition(27, "crow", "curve")
				.applyProposal(27, "curve")
				.expectContent("digraph {1->2[ arrowhead=\"curve ]}");
	}

	@Test
	public void edge_arrowtail() throws Exception {
		// test global attribute values
		newBuilder().append("digraph {edge[ arrowtail= ]}")
				.assertTextAtCursorPosition(25, "o", "l", "r", "box", "crow",
						"curve", "icurve", "diamond", "dot", "inv", "none",
						"normal", "tee", "vee")
				.applyProposal(25, "none")
				.expectContent("digraph {edge[ arrowtail=none ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ arrowtail= ]}")
				.assertTextAtCursorPosition(25, "o", "l", "r", "box", "crow",
						"curve", "icurve", "diamond", "dot", "inv", "none",
						"normal", "tee", "vee")
				.applyProposal(25, "normal")
				.expectContent("digraph {1->2[ arrowtail=normal ]}");

		// test local attribute values with quotes
		newBuilder().append("digraph {1->2[ arrowtail=\"\" ]}")
				.assertTextAtCursorPosition(26, "o", "l", "r", "box", "crow",
						"curve", "icurve", "diamond", "dot", "inv", "none",
						"normal", "tee", "vee")
				.applyProposal(26, "tee")
				.expectContent("digraph {1->2[ arrowtail=\"tee\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ arrowtail=d ]}")
				.assertTextAtCursorPosition(26, "diamond", "dot", ",", ";", "]")
				.applyProposal(26, "diamond")
				.expectContent("digraph {1->2[ arrowtail=diamond ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ arrowtail=\"n\" ]}")
				.assertTextAtCursorPosition(27, "none", "normal")
				.applyProposal(27, "none")
				.expectContent("digraph {1->2[ arrowtail=\"none\" ]}");
	}

	@Test
	public void edge_dir() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ dir= ]}")
				.assertTextAtCursorPosition(17, "forward", "back", "both",
						"none")
				.applyProposal(17, "forward")
				.expectContent("graph {edge[ dir=forward ]}");

		// test local attribute values
		newBuilder().append("graph {1--2[ dir= ]}")
				.assertTextAtCursorPosition(17, "forward", "back", "both",
						"none")
				.applyProposal(17, "back")
				.expectContent("graph {1--2[ dir=back ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1--2[ dir=\"\" ]}")
				.assertTextAtCursorPosition(18, "forward", "back", "both",
						"none")
				.applyProposal(18, "both")
				.expectContent("graph {1--2[ dir=\"both\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1--2[ dir=f ]}")
				.assertTextAtCursorPosition(18, "forward", ",", ";", "]")
				.applyProposal(18, "forward")
				.expectContent("graph {1--2[ dir=forward ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1--2[ dir=\"b\" ]}")
				.assertTextAtCursorPosition(19, "back", "both")
				.applyProposal(19, "both")
				.expectContent("graph {1--2[ dir=\"both\" ]}");
	}

	@Test
	public void edge_headlp() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ head_lp= ]}")
				.assertTextAtCursorPosition(21);

		// test local attribute values
		newBuilder().append("graph {1--2[ head_lp= ]}")
				.assertTextAtCursorPosition(21);

		// no use to test local attribute values with prefix
	}

	@Test
	public void edge_lp() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ lp= ]}")
				.assertTextAtCursorPosition(16);

		// test local attribute values
		newBuilder().append("graph {1--2[ lp= ]}")
				.assertTextAtCursorPosition(16);

		// no use to test local attribute values with prefix
	}

	@Test
	public void edge_pos() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ pos= ]}")
				.assertTextAtCursorPosition(17, "e", "s");

		// test local attribute values
		newBuilder().append("graph {1--2[ pos= ]}")
				.assertTextAtCursorPosition(17, "e", "s");

		// test local attribute values with quotes
		newBuilder().append("graph {1--2[ pos=\"\" ]}")
				.assertTextAtCursorPosition(18, "e", "s");

		// test local attribute values with prefix
		newBuilder().append("graph {1--2[ pos=s ]}")
				.assertTextAtCursorPosition(18, "s", ",", ";", "]");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1--2[ pos=\"e\" ]}")
				.assertTextAtCursorPosition(19, "e", ",");
	}

	@Test
	public void edge_style() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ style= ]}")
				.assertTextAtCursorPosition(19, "bold", "dashed", "dotted",
						"invis", "solid", "tapered")
				.applyProposal(19, "bold")
				.expectContent("graph {edge[ style=bold ]}");

		// test local attribute values
		newBuilder().append("graph {1--2[ style= ]}")
				.assertTextAtCursorPosition(19, "bold", "dashed", "dotted",
						"invis", "solid", "tapered")
				.applyProposal(19, "dashed")
				.expectContent("graph {1--2[ style=dashed ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1--2[ style=\"\" ]}")
				.assertTextAtCursorPosition(20, "bold", "dashed", "dotted",
						"invis", "solid", "tapered")
				.applyProposal(20, "tapered")
				.expectContent("graph {1--2[ style=\"tapered\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1--2[ style=d ]}")
				.assertTextAtCursorPosition(20, "dashed", "dotted", ",", ";",
						"]")
				.applyProposal(20, "dotted")
				.expectContent("graph {1--2[ style=dotted ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1--2[ style=\"d\" ]}")
				.assertTextAtCursorPosition(21, "dashed", "dotted")
				.applyProposal(21, "dashed")
				.expectContent("graph {1--2[ style=\"dashed\" ]}");
	}

	@Test
	public void edge_taillp() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ tail_lp= ]}")
				.assertTextAtCursorPosition(21);

		// test local attribute values
		newBuilder().append("graph {1--2[ tail_lp= ]}")
				.assertTextAtCursorPosition(21);

		// no use to test local attribute values with prefix
	}

	@Test
	public void edge_xlp() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ xlp= ]}")
				.assertTextAtCursorPosition(17);

		// test local attribute values
		newBuilder().append("graph {1--2[ xlp= ]}")
				.assertTextAtCursorPosition(17);

		// no use to test local attribute values with prefix
	}

	@Test
	public void graph_attributes() throws Exception {
		// test global attribute names
		newBuilder().append("graph {graph[]}")
				.assertTextAtCursorPosition(13, "]", "forcelabels", "id",
						"label", "layout", "lp", "rankdir", "splines", "style")
				.applyProposal(13, "forcelabels")
				.expectContent("graph {graph[forcelabels]}");

		// test local attribute names
		newBuilder().append("graph {  }")
				.assertTextAtCursorPosition(8, "edge", "graph", "node",
						"subgraph", "{", "}", "forcelabels", "id", "label",
						"layout", "lp", "rankdir", "splines", "style")
				.applyProposal(8, "rankdir").expectContent("graph { rankdir }");

		// test local attribute names with prefix
		newBuilder().append("graph { la }")
				.assertTextAtCursorPosition(10, "label", "layout", "--", ":",
						";", "=", "[", "{", "}")
				.applyProposal(10, "layout").expectContent("graph { layout }");

		newBuilder().append("digraph { la }")
				.assertTextAtCursorPosition(12, "label", "layout", "->", ":",
						";", "=", "[", "{", "}")
				.applyProposal(12, "label").expectContent("digraph { label }");
	}

	@Test
	public void graph_forcelabels() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ forcelabels= ]}")
				.assertTextAtCursorPosition(26, "true", "false")
				.applyProposal(26, "true")
				.expectContent("graph {graph[ forcelabels=true ]}");

		// test local attribute values
		newBuilder().append("graph { forcelabels= }")
				.assertTextAtCursorPosition(20, "true", "false")
				.applyProposal(20, "false")
				.expectContent("graph { forcelabels=false }");

		// test local attribute values with quotes
		newBuilder().append("graph { forcelabels=\"\" }")
				.assertTextAtCursorPosition(21, "true", "false")
				.applyProposal(21, "true")
				.expectContent("graph { forcelabels=\"true\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { forcelabels=t }")
				.assertTextAtCursorPosition(21, "true", ";", "{", "}")
				.applyProposal(21, "true")
				.expectContent("graph { forcelabels=true }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { forcelabels=\"f\" }")
				.assertTextAtCursorPosition(22, "false")
				.applyProposal(22, "false")
				.expectContent("graph { forcelabels=\"false\" }");
	}

	@Test
	public void graph_layout() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ layout= ]}")
				.assertTextAtCursorPosition(21, "circo", "dot", "fdp", "grid",
						"neato", "osage", "sfdp", "twopi")
				.applyProposal(21, "circo")
				.expectContent("graph {graph[ layout=circo ]}");

		// test local attribute values
		newBuilder().append("graph { layout= }")
				.assertTextAtCursorPosition(15, "circo", "dot", "fdp", "grid",
						"neato", "osage", "sfdp", "twopi")
				.applyProposal(15, "osage")
				.expectContent("graph { layout=osage }");

		// test local attribute values with quotes
		newBuilder().append("graph { layout=\"\" }")
				.assertTextAtCursorPosition(16, "circo", "dot", "fdp", "grid",
						"neato", "osage", "sfdp", "twopi")
				.applyProposal(16, "neato")
				.expectContent("graph { layout=\"neato\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { layout=f }")
				.assertTextAtCursorPosition(16, "fdp", ";", "{", "}")
				.applyProposal(16, "fdp").expectContent("graph { layout=fdp }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { layout=\"t\" }")
				.assertTextAtCursorPosition(17, "twopi")
				.applyProposal(17, "twopi")
				.expectContent("graph { layout=\"twopi\" }");
	}

	@Test
	public void graph_lp() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ lp= ]}")
				.assertTextAtCursorPosition(17);

		// test local attribute values
		newBuilder().append("graph { lp= }").assertTextAtCursorPosition(11);

		// no use to test local attribute values with prefix
	}

	@Test
	public void graph_rankdir() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ rankdir= ]}")
				.assertTextAtCursorPosition(22, "TB", "LR", "BT", "RL")
				.applyProposal(22, "TB")
				.expectContent("graph {graph[ rankdir=TB ]}");

		// test local attribute values
		newBuilder().append("graph { rankdir= }")
				.assertTextAtCursorPosition(16, "TB", "LR", "BT", "RL")
				.applyProposal(16, "LR").expectContent("graph { rankdir=LR }");

		// test local attribute values with quotes
		newBuilder().append("graph { rankdir=\"\" }")
				.assertTextAtCursorPosition(17, "TB", "LR", "BT", "RL")
				.applyProposal(17, "BT")
				.expectContent("graph { rankdir=\"BT\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { rankdir=T }")
				.assertTextAtCursorPosition(17, "TB", ";", "{", "}")
				.applyProposal(17, "TB").expectContent("graph { rankdir=TB }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { rankdir=\"L\" }")
				.assertTextAtCursorPosition(18, "LR").applyProposal(18, "LR")
				.expectContent("graph { rankdir=\"LR\" }");
	}

	@Test
	public void graph_splines() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ splines= ]}")
				.assertTextAtCursorPosition(22, "compound", "curved", "\"\"",
						"false", "line", "none", "ortho", "polyline", "spline",
						"true")
				.applyProposal(22, "ortho")
				.expectContent("graph {graph[ splines=ortho ]}");

		// test local attribute values
		newBuilder().append("graph { splines= }")
				.assertTextAtCursorPosition(16, "compound", "curved", "\"\"",
						"false", "line", "none", "ortho", "polyline", "spline",
						"true")
				.applyProposal(16, "polyline")
				.expectContent("graph { splines=polyline }");

		// test local attribute values with quotes
		newBuilder().append("graph { splines=\"\" }")
				.assertTextAtCursorPosition(17, "compound", "curved", "\"\"",
						"false", "line", "none", "ortho", "polyline", "spline",
						"true")
				.applyProposal(17, "line")
				.expectContent("graph { splines=\"line\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { splines=c }")
				.assertTextAtCursorPosition(17, "compound", "curved", ";", "{",
						"}")
				.applyProposal(17, "compound")
				.expectContent("graph { splines=compound }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { splines=\"c\" }")
				.assertTextAtCursorPosition(18, "compound", "curved")
				.applyProposal(18, "curved")
				.expectContent("graph { splines=\"curved\" }");
	}

	@Test
	public void node_attributes() throws Exception {
		// test global attribute names
		newBuilder().append("graph {node[]}")
				.assertTextAtCursorPosition(12, "]", "distortion", "fixedsize",
						"height", "id", "label", "pos", "shape", "sides",
						"skew", "style", "width", "xlabel", "xlp")
				.applyProposal(12, "distortion")
				.expectContent("graph {node[distortion]}");

		// test local attribute names
		newBuilder().append("graph {1[  ]}")
				.assertTextAtCursorPosition(10, "]", "distortion", "fixedsize",
						"height", "id", "label", "pos", "shape", "sides",
						"skew", "style", "width", "xlabel", "xlp")
				.applyProposal(10, "fixedsize")
				.expectContent("graph {1[ fixedsize ]}");

		// test local attribute names with prefix
		newBuilder().append("graph {1[ s ]}")
				.assertTextAtCursorPosition(11, "=", "shape", "sides", "skew",
						"style")
				.applyProposal(11, "shape").expectContent("graph {1[ shape ]}");
	}

	@Test
	public void node_fixedsize() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ fixedsize= ]}")
				.assertTextAtCursorPosition(23, "true", "false")
				.applyProposal(23, "true")
				.expectContent("graph {node[ fixedsize=true ]}");

		// test local attribute values
		newBuilder().append("graph {1[ fixedsize= ]}")
				.assertTextAtCursorPosition(20, "true", "false")
				.applyProposal(20, "false")
				.expectContent("graph {1[ fixedsize=false ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ fixedsize=\"\" ]}")
				.assertTextAtCursorPosition(21, "true", "false")
				.applyProposal(21, "true")
				.expectContent("graph {1[ fixedsize=\"true\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1[ fixedsize=f ]}")
				.assertTextAtCursorPosition(21, "false", ",", ";", "]")
				.applyProposal(21, "false")
				.expectContent("graph {1[ fixedsize=false ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ fixedsize=\"t\" ]}")
				.assertTextAtCursorPosition(22, "true")
				.applyProposal(22, "true")
				.expectContent("graph {1[ fixedsize=\"true\" ]}");
	}

	@Test
	public void node_pos() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ lp= ]}")
				.assertTextAtCursorPosition(16);

		// test local attribute values
		newBuilder().append("graph {1[ lp= ]}").assertTextAtCursorPosition(13);

		// no use to test local attribute values with prefix
	}

	@Test
	public void node_shape() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ shape= ]}")
				.assertTextAtCursorPosition(19, "Mcircle", "Mdiamond",
						"Mrecord", "Msquare", "assembly", "box", "box3d", "cds",
						"circle", "component", "cylinder", "diamond",
						"doublecircle", "doubleoctagon", "egg", "ellipse",
						"fivepoverhang", "folder", "hexagon", "house",
						"insulator", "invhouse", "invtrapezium", "invtriangle",
						"larrow", "lpromoter", "none", "note", "noverhang",
						"octagon", "oval", "parallelogram", "pentagon", "plain",
						"plaintext", "point", "polygon", "primersite",
						"promoter", "proteasesite", "proteinstab", "rarrow",
						"record", "rect", "rectangle", "restrictionsite",
						"ribosite", "rnastab", "rpromoter", "septagon",
						"signature", "square", "star", "tab", "terminator",
						"threepoverhang", "trapezium", "triangle",
						"tripleoctagon", "underline", "utr")
				.applyProposal(19, "Mcircle")
				.expectContent("graph {node[ shape=Mcircle ]}");

		// test local attribute values
		newBuilder().append("graph {1[ shape= ]}").assertTextAtCursorPosition(
				16, "Mcircle", "Mdiamond", "Mrecord", "Msquare", "assembly",
				"box", "box3d", "cds", "circle", "component", "cylinder",
				"diamond", "doublecircle", "doubleoctagon", "egg", "ellipse",
				"fivepoverhang", "folder", "hexagon", "house", "insulator",
				"invhouse", "invtrapezium", "invtriangle", "larrow",
				"lpromoter", "none", "note", "noverhang", "octagon", "oval",
				"parallelogram", "pentagon", "plain", "plaintext", "point",
				"polygon", "primersite", "promoter", "proteasesite",
				"proteinstab", "rarrow", "record", "rect", "rectangle",
				"restrictionsite", "ribosite", "rnastab", "rpromoter",
				"septagon", "signature", "square", "star", "tab", "terminator",
				"threepoverhang", "trapezium", "triangle", "tripleoctagon",
				"underline", "utr").applyProposal(16, "Mdiamond")
				.expectContent("graph {1[ shape=Mdiamond ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ shape=\"\" ]}")
				.assertTextAtCursorPosition(17, "Mcircle", "Mdiamond",
						"Mrecord", "Msquare", "assembly", "box", "box3d", "cds",
						"circle", "component", "cylinder", "diamond",
						"doublecircle", "doubleoctagon", "egg", "ellipse",
						"fivepoverhang", "folder", "hexagon", "house",
						"insulator", "invhouse", "invtrapezium", "invtriangle",
						"larrow", "lpromoter", "none", "note", "noverhang",
						"octagon", "oval", "parallelogram", "pentagon", "plain",
						"plaintext", "point", "polygon", "primersite",
						"promoter", "proteasesite", "proteinstab", "rarrow",
						"record", "rect", "rectangle", "restrictionsite",
						"ribosite", "rnastab", "rpromoter", "septagon",
						"signature", "square", "star", "tab", "terminator",
						"threepoverhang", "trapezium", "triangle",
						"tripleoctagon", "underline", "utr")
				.applyProposal(17, "Mrecord")
				.expectContent("graph {1[ shape=\"Mrecord\" ]}");

		// test local attribute values with prefix
		// TODO: activate
		// newBuilder().append("graph {1[ shape=pr ]}")
		// .assertTextAtCursorPosition(18, "primersite", "promoter",
		// "proteasesite", "proteinstab", ",", ";", "]")
		// .applyProposal(18, "primersite")
		// .expectContent("graph {1[ shape=primersite ]}");

		// test local attribute values with quotes and prefix
		// TODO: activate
		// newBuilder().append("graph {1[ shape=\"pro\" ]}")
		// .assertTextAtCursorPosition(20, "promoter", "proteasesite",
		// "proteinstab")
		// .applyProposal(20, "proteinstab")
		// .expectContent("graph {1[ shape=\"proteinstab\" ]}");
	}

	@Test
	public void node_style() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ style= ]}")
				.assertTextAtCursorPosition(19, "bold", "dashed", "diagonals",
						"dotted", "filled", "invis", "radial", "rounded",
						"solid", "striped", "wedged")
				.applyProposal(19, "invis")
				.expectContent("graph {node[ style=invis ]}");

		// test local attribute values
		newBuilder().append("graph {1[ style= ]}")
				.assertTextAtCursorPosition(16, "bold", "dashed", "diagonals",
						"dotted", "filled", "invis", "radial", "rounded",
						"solid", "striped", "wedged")
				.applyProposal(16, "radial")
				.expectContent("graph {1[ style=radial ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ style=\"\" ]}")
				.assertTextAtCursorPosition(17, "bold", "dashed", "diagonals",
						"dotted", "filled", "invis", "radial", "rounded",
						"solid", "striped", "wedged")
				.applyProposal(17, "wedged")
				.expectContent("graph {1[ style=\"wedged\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1[ style=d ]}")
				.assertTextAtCursorPosition(17, "dashed", "diagonals", "dotted",
						",", ";", "]")
				.applyProposal(17, "diagonals")
				.expectContent("graph {1[ style=diagonals ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ style=\"s\" ]}")
				.assertTextAtCursorPosition(18, "solid", "striped")
				.applyProposal(18, "solid")
				.expectContent("graph {1[ style=\"solid\" ]}");
	}

	@Test
	public void node_xlp() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ xlp= ]}")
				.assertTextAtCursorPosition(17);

		// test local attribute values
		newBuilder().append("graph {1[ xlp= ]}").assertTextAtCursorPosition(14);

		// no use to test local attribute values with prefix
	}
}
