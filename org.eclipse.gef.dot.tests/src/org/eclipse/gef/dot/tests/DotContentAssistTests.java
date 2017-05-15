/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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

import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("restriction")
@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public class DotContentAssistTests extends AbstractContentAssistTest {

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

	private String[] expectedX11ColorNames = { "aliceblue", "antiquewhite",
			"antiquewhite1", "antiquewhite2", "antiquewhite3", "antiquewhite4",
			"aquamarine", "aquamarine1", "aquamarine2", "aquamarine3",
			"aquamarine4", "azure", "azure1", "azure2", "azure3", "azure4",
			"beige", "bisque", "bisque1", "bisque2", "bisque3", "bisque4",
			"black", "blanchedalmond", "blue", "blue1", "blue2", "blue3",
			"blue4", "blueviolet", "brown", "brown1", "brown2", "brown3",
			"brown4", "burlywood", "burlywood1", "burlywood2", "burlywood3",
			"burlywood4", "cadetblue", "cadetblue1", "cadetblue2", "cadetblue3",
			"cadetblue4", "chartreuse", "chartreuse1", "chartreuse2",
			"chartreuse3", "chartreuse4", "chocolate", "chocolate1",
			"chocolate2", "chocolate3", "chocolate4", "coral", "coral1",
			"coral2", "coral3", "coral4", "cornflowerblue", "cornsilk",
			"cornsilk1", "cornsilk2", "cornsilk3", "cornsilk4", "crimson",
			"cyan", "cyan1", "cyan2", "cyan3", "cyan4", "darkgoldenrod",
			"darkgoldenrod1", "darkgoldenrod2", "darkgoldenrod3",
			"darkgoldenrod4", "darkgreen", "darkkhaki", "darkolivegreen",
			"darkolivegreen1", "darkolivegreen2", "darkolivegreen3",
			"darkolivegreen4", "darkorange", "darkorange1", "darkorange2",
			"darkorange3", "darkorange4", "darkorchid", "darkorchid1",
			"darkorchid2", "darkorchid3", "darkorchid4", "darksalmon",
			"darkseagreen", "darkseagreen1", "darkseagreen2", "darkseagreen3",
			"darkseagreen4", "darkslateblue", "darkslategray", "darkslategray1",
			"darkslategray2", "darkslategray3", "darkslategray4",
			"darkslategrey", "darkturquoise", "darkviolet", "deeppink",
			"deeppink1", "deeppink2", "deeppink3", "deeppink4", "deepskyblue",
			"deepskyblue1", "deepskyblue2", "deepskyblue3", "deepskyblue4",
			"dimgray", "dimgrey", "dodgerblue", "dodgerblue1", "dodgerblue2",
			"dodgerblue3", "dodgerblue4", "firebrick", "firebrick1",
			"firebrick2", "firebrick3", "firebrick4", "floralwhite",
			"forestgreen", "gainsboro", "ghostwhite", "gold", "gold1", "gold2",
			"gold3", "gold4", "goldenrod", "goldenrod1", "goldenrod2",
			"goldenrod3", "goldenrod4", "gray", "gray0", "gray1", "gray10",
			"gray100", "gray11", "gray12", "gray13", "gray14", "gray15",
			"gray16", "gray17", "gray18", "gray19", "gray2", "gray20", "gray21",
			"gray22", "gray23", "gray24", "gray25", "gray26", "gray27",
			"gray28", "gray29", "gray3", "gray30", "gray31", "gray32", "gray33",
			"gray34", "gray35", "gray36", "gray37", "gray38", "gray39", "gray4",
			"gray40", "gray41", "gray42", "gray43", "gray44", "gray45",
			"gray46", "gray47", "gray48", "gray49", "gray5", "gray50", "gray51",
			"gray52", "gray53", "gray54", "gray55", "gray56", "gray57",
			"gray58", "gray59", "gray6", "gray60", "gray61", "gray62", "gray63",
			"gray64", "gray65", "gray66", "gray67", "gray68", "gray69", "gray7",
			"gray70", "gray71", "gray72", "gray73", "gray74", "gray75",
			"gray76", "gray77", "gray78", "gray79", "gray8", "gray80", "gray81",
			"gray82", "gray83", "gray84", "gray85", "gray86", "gray87",
			"gray88", "gray89", "gray9", "gray90", "gray91", "gray92", "gray93",
			"gray94", "gray95", "gray96", "gray97", "gray98", "gray99", "green",
			"green1", "green2", "green3", "green4", "greenyellow", "grey",
			"grey0", "grey1", "grey10", "grey100", "grey11", "grey12", "grey13",
			"grey14", "grey15", "grey16", "grey17", "grey18", "grey19", "grey2",
			"grey20", "grey21", "grey22", "grey23", "grey24", "grey25",
			"grey26", "grey27", "grey28", "grey29", "grey3", "grey30", "grey31",
			"grey32", "grey33", "grey34", "grey35", "grey36", "grey37",
			"grey38", "grey39", "grey4", "grey40", "grey41", "grey42", "grey43",
			"grey44", "grey45", "grey46", "grey47", "grey48", "grey49", "grey5",
			"grey50", "grey51", "grey52", "grey53", "grey54", "grey55",
			"grey56", "grey57", "grey58", "grey59", "grey6", "grey60", "grey61",
			"grey62", "grey63", "grey64", "grey65", "grey66", "grey67",
			"grey68", "grey69", "grey7", "grey70", "grey71", "grey72", "grey73",
			"grey74", "grey75", "grey76", "grey77", "grey78", "grey79", "grey8",
			"grey80", "grey81", "grey82", "grey83", "grey84", "grey85",
			"grey86", "grey87", "grey88", "grey89", "grey9", "grey90", "grey91",
			"grey92", "grey93", "grey94", "grey95", "grey96", "grey97",
			"grey98", "grey99", "honeydew", "honeydew1", "honeydew2",
			"honeydew3", "honeydew4", "hotpink", "hotpink1", "hotpink2",
			"hotpink3", "hotpink4", "indianred", "indianred1", "indianred2",
			"indianred3", "indianred4", "indigo", "invis", "ivory", "ivory1",
			"ivory2", "ivory3", "ivory4", "khaki", "khaki1", "khaki2", "khaki3",
			"khaki4", "lavender", "lavenderblush", "lavenderblush1",
			"lavenderblush2", "lavenderblush3", "lavenderblush4", "lawngreen",
			"lemonchiffon", "lemonchiffon1", "lemonchiffon2", "lemonchiffon3",
			"lemonchiffon4", "lightblue", "lightblue1", "lightblue2",
			"lightblue3", "lightblue4", "lightcoral", "lightcyan", "lightcyan1",
			"lightcyan2", "lightcyan3", "lightcyan4", "lightgoldenrod",
			"lightgoldenrod1", "lightgoldenrod2", "lightgoldenrod3",
			"lightgoldenrod4", "lightgoldenrodyellow", "lightgray", "lightgrey",
			"lightpink", "lightpink1", "lightpink2", "lightpink3", "lightpink4",
			"lightsalmon", "lightsalmon1", "lightsalmon2", "lightsalmon3",
			"lightsalmon4", "lightseagreen", "lightskyblue", "lightskyblue1",
			"lightskyblue2", "lightskyblue3", "lightskyblue4", "lightslateblue",
			"lightslategray", "lightslategrey", "lightsteelblue",
			"lightsteelblue1", "lightsteelblue2", "lightsteelblue3",
			"lightsteelblue4", "lightyellow", "lightyellow1", "lightyellow2",
			"lightyellow3", "lightyellow4", "limegreen", "linen", "magenta",
			"magenta1", "magenta2", "magenta3", "magenta4", "maroon", "maroon1",
			"maroon2", "maroon3", "maroon4", "mediumaquamarine", "mediumblue",
			"mediumorchid", "mediumorchid1", "mediumorchid2", "mediumorchid3",
			"mediumorchid4", "mediumpurple", "mediumpurple1", "mediumpurple2",
			"mediumpurple3", "mediumpurple4", "mediumseagreen",
			"mediumslateblue", "mediumspringgreen", "mediumturquoise",
			"mediumvioletred", "midnightblue", "mintcream", "mistyrose",
			"mistyrose1", "mistyrose2", "mistyrose3", "mistyrose4", "moccasin",
			"navajowhite", "navajowhite1", "navajowhite2", "navajowhite3",
			"navajowhite4", "navy", "navyblue", "none", "oldlace", "olivedrab",
			"olivedrab1", "olivedrab2", "olivedrab3", "olivedrab4", "orange",
			"orange1", "orange2", "orange3", "orange4", "orangered",
			"orangered1", "orangered2", "orangered3", "orangered4", "orchid",
			"orchid1", "orchid2", "orchid3", "orchid4", "palegoldenrod",
			"palegreen", "palegreen1", "palegreen2", "palegreen3", "palegreen4",
			"paleturquoise", "paleturquoise1", "paleturquoise2",
			"paleturquoise3", "paleturquoise4", "palevioletred",
			"palevioletred1", "palevioletred2", "palevioletred3",
			"palevioletred4", "papayawhip", "peachpuff", "peachpuff1",
			"peachpuff2", "peachpuff3", "peachpuff4", "peru", "pink", "pink1",
			"pink2", "pink3", "pink4", "plum", "plum1", "plum2", "plum3",
			"plum4", "powderblue", "purple", "purple1", "purple2", "purple3",
			"purple4", "red", "red1", "red2", "red3", "red4", "rosybrown",
			"rosybrown1", "rosybrown2", "rosybrown3", "rosybrown4", "royalblue",
			"royalblue1", "royalblue2", "royalblue3", "royalblue4",
			"saddlebrown", "salmon", "salmon1", "salmon2", "salmon3", "salmon4",
			"sandybrown", "seagreen", "seagreen1", "seagreen2", "seagreen3",
			"seagreen4", "seashell", "seashell1", "seashell2", "seashell3",
			"seashell4", "sienna", "sienna1", "sienna2", "sienna3", "sienna4",
			"skyblue", "skyblue1", "skyblue2", "skyblue3", "skyblue4",
			"slateblue", "slateblue1", "slateblue2", "slateblue3", "slateblue4",
			"slategray", "slategray1", "slategray2", "slategray3", "slategray4",
			"slategrey", "snow", "snow1", "snow2", "snow3", "snow4",
			"springgreen", "springgreen1", "springgreen2", "springgreen3",
			"springgreen4", "steelblue", "steelblue1", "steelblue2",
			"steelblue3", "steelblue4", "tan", "tan1", "tan2", "tan3", "tan4",
			"thistle", "thistle1", "thistle2", "thistle3", "thistle4", "tomato",
			"tomato1", "tomato2", "tomato3", "tomato4", "transparent",
			"turquoise", "turquoise1", "turquoise2", "turquoise3", "turquoise4",
			"violet", "violetred", "violetred1", "violetred2", "violetred3",
			"violetred4", "wheat", "wheat1", "wheat2", "wheat3", "wheat4",
			"white", "whitesmoke", "yellow", "yellow1", "yellow2", "yellow3",
			"yellow4", "yellowgreen" };

	private String[] expectedSvgColorNames = { "aliceblue", "antiquewhite",
			"aqua", "aquamarine", "azure", "beige", "bisque", "black",
			"blanchedalmond", "blue", "blueviolet", "brown", "burlywood",
			"cadetblue", "chartreuse", "chocolate", "coral", "cornflowerblue",
			"cornsilk", "crimson", "cyan", "darkblue", "darkcyan",
			"darkgoldenrod", "darkgray", "darkgreen", "darkgrey", "darkkhaki",
			"darkmagenta", "darkolivegreen", "darkorange", "darkorchid",
			"darkred", "darksalmon", "darkseagreen", "darkslateblue",
			"darkslategray", "darkslategrey", "darkturquoise", "darkviolet",
			"deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue",
			"firebrick", "floralwhite", "forestgreen", "fuchsia", "gainsboro",
			"ghostwhite", "gold", "goldenrod", "gray", "grey", "green",
			"greenyellow", "honeydew", "hotpink", "indianred", "indigo",
			"ivory", "khaki", "lavender", "lavenderblush", "lawngreen",
			"lemonchiffon", "lightblue", "lightcoral", "lightcyan",
			"lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey",
			"lightpink", "lightsalmon", "lightseagreen", "lightskyblue",
			"lightslategray", "lightslategrey", "lightsteelblue", "lightyellow",
			"lime", "limegreen", "linen", "magenta", "maroon",
			"mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple",
			"mediumseagreen", "mediumslateblue", "mediumspringgreen",
			"mediumturquoise", "mediumvioletred", "midnightblue", "mintcream",
			"mistyrose", "moccasin", "navajowhite", "navy", "oldlace", "olive",
			"olivedrab", "orange", "orangered", "orchid", "palegoldenrod",
			"palegreen", "paleturquoise", "palevioletred", "papayawhip",
			"peachpuff", "peru", "pink", "plum", "powderblue", "purple", "red",
			"rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown",
			"seagreen", "seashell", "sienna", "silver", "skyblue", "slateblue",
			"slategray", "slategrey", "snow", "springgreen", "steelblue", "tan",
			"teal", "thistle", "tomato", "turquoise", "violet", "wheat",
			"white", "whitesmoke", "yellow", "yellowgreen" };

	@Test
	public void empty() throws Exception {
		newBuilder().assertText("strict", "graph", "digraph",
				"graph - Insert a template", "digraph - Insert a template")
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
						"arrowtail", "color", "colorscheme", "dir", "fillcolor",
						"fontcolor", "headlabel", "head_lp", "id", "label",
						"labelfontcolor", "lp", "pos", "style", "taillabel",
						"tail_lp", "xlabel", "xlp")
				.applyProposal(12, "arrowhead")
				.expectContent("graph {edge[arrowhead]}");

		// test local attribute names
		newBuilder().append("graph {1--2[  ]}")
				.assertTextAtCursorPosition(13, "]", "arrowhead", "arrowsize",
						"arrowtail", "color", "colorscheme", "dir", "fillcolor",
						"fontcolor", "headlabel", "head_lp", "id", "label",
						"labelfontcolor", "lp", "pos", "style", "taillabel",
						"tail_lp", "xlabel", "xlp")
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
	public void edge_color() throws Exception {
		// test global attribute values
		newBuilder().append("digraph {edge[ color= ]}")
				.assertTextAtCursorPosition(21,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(21, "#")
				.expectContent("digraph {edge[ color=# ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ color= ]}")
				.assertTextAtCursorPosition(21,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(21, "/")
				.expectContent("digraph {1->2[ color=/ ]}");

		// test local attribute values with quotes
		newBuilder().append("digraph {1->2[ color=\"\" ]}")
				.assertTextAtCursorPosition(22,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(22, "#")
				.expectContent("digraph {1->2[ color=\"#\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ color=azure ]}")
				.assertTextAtCursorPosition(26, "azure", "azure1", "azure2",
						"azure3", "azure4", ",", ";", "]")
				.applyProposal(26, "azure1")
				.expectContent("digraph {1->2[ color=azure1 ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ color=\"azure\" ]}")
				.assertTextAtCursorPosition(27, "azure", "azure1", "azure2",
						"azure3", "azure4")
				.applyProposal(27, "azure2")
				.expectContent("digraph {1->2[ color=\"azure2\" ]}");
	}

	@Test
	public void edge_colorscheme() throws Exception {
		// test global attribute values
		String[] expectedDotColorSchemes = DotTestUtils.expectedDotColorSchemes;
		newBuilder().append("digraph {edge[ colorscheme= ]}")
				.assertTextAtCursorPosition(27, expectedDotColorSchemes)
				.applyProposal(27, "x11")
				.expectContent("digraph {edge[ colorscheme=x11 ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ colorscheme= ]}")
				.assertTextAtCursorPosition(27, expectedDotColorSchemes)
				.applyProposal(27, "svg")
				.expectContent("digraph {1->2[ colorscheme=svg ]}");

		// test local attribute values with quotes
		newBuilder().append("digraph {1->2[ colorscheme=\"\" ]}")
				.assertTextAtCursorPosition(28, expectedDotColorSchemes)
				.applyProposal(28, "accent3")
				.expectContent("digraph {1->2[ colorscheme=\"accent3\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ colorscheme=a ]}")
				.assertTextAtCursorPosition(28, "accent3", "accent4", "accent5",
						"accent6", "accent7", "accent8", ",", ";", "]")
				.applyProposal(28, "accent4")
				.expectContent("digraph {1->2[ colorscheme=accent4 ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ colorscheme=\"acc\" ]}")
				.assertTextAtCursorPosition(31, "accent3", "accent4", "accent5",
						"accent6", "accent7", "accent8")
				.applyProposal(31, "accent4")
				.expectContent("digraph {1->2[ colorscheme=\"accent4\" ]}");
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
	public void edge_fillcolor() throws Exception {
		// test global attribute values
		newBuilder().append("digraph {edge[ fillcolor= ]}")
				.assertTextAtCursorPosition(25,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(25, "#")
				.expectContent("digraph {edge[ fillcolor=# ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ fillcolor= ]}")
				.assertTextAtCursorPosition(25,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(25, "/")
				.expectContent("digraph {1->2[ fillcolor=/ ]}");

		// test local attribute values with quotes
		newBuilder().append("digraph {1->2[ fillcolor=\"\" ]}")
				.assertTextAtCursorPosition(26,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(26, "#")
				.expectContent("digraph {1->2[ fillcolor=\"#\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ fillcolor=bisque ]}")
				.assertTextAtCursorPosition(31, "bisque", "bisque1", "bisque2",
						"bisque3", "bisque4", ",", ";", "]")
				.applyProposal(31, "bisque1")
				.expectContent("digraph {1->2[ fillcolor=bisque1 ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ fillcolor=\"bisque\" ]}")
				.assertTextAtCursorPosition(32, "bisque", "bisque1", "bisque2",
						"bisque3", "bisque4")
				.applyProposal(32, "bisque2")
				.expectContent("digraph {1->2[ fillcolor=\"bisque2\" ]}");
	}

	@Test
	public void edge_fontcolor() throws Exception {
		// test global attribute values
		newBuilder().append("digraph {edge[ fontcolor= ]}")
				.assertTextAtCursorPosition(25,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(25, "#")
				.expectContent("digraph {edge[ fontcolor=# ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ fontcolor= ]}")
				.assertTextAtCursorPosition(25,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(25, "/")
				.expectContent("digraph {1->2[ fontcolor=/ ]}");

		// test local attribute values with quotes
		newBuilder().append("digraph {1->2[ fontcolor=\"\" ]}")
				.assertTextAtCursorPosition(26,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(26, "#")
				.expectContent("digraph {1->2[ fontcolor=\"#\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ fontcolor=blue ]}")
				.assertTextAtCursorPosition(29, "blue", "blue1", "blue2",
						"blue3", "blue4", "blueviolet", ",", ";", "]")
				.applyProposal(29, "blue")
				.expectContent("digraph {1->2[ fontcolor=blue ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ fontcolor=\"blue\" ]}")
				.assertTextAtCursorPosition(30, "blue", "blue1", "blue2",
						"blue3", "blue4", "blueviolet")
				.applyProposal(30, "blueviolet")
				.expectContent("digraph {1->2[ fontcolor=\"blueviolet\" ]}");
	}

	@Test
	public void edge_headlabel() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ headlabel= ]}")
				.assertTextAtCursorPosition(23,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("digraph {1->2[ headlabel= ]}")
				.assertTextAtCursorPosition(25,
						"HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("digraph {1->2[ headlabel=<  >]}")
				.assertTextAtCursorPosition(27, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");
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
	public void edge_label() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ label= ]}")
				.assertTextAtCursorPosition(19,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("graph {1--2[ label= ]}")
				.assertTextAtCursorPosition(19,
						"HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("graph {1--2[ label=<  >]}")
				.assertTextAtCursorPosition(21, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");
	}

	@Test
	public void edge_labelfontcolor() throws Exception {
		// test global attribute values
		newBuilder().append("digraph {edge[ colorscheme=svg labelfontcolor= ]}")
				.assertTextAtCursorPosition(46,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(46, "#").expectContent(
						"digraph {edge[ colorscheme=svg labelfontcolor=# ]}");

		// test local attribute values
		newBuilder().append("digraph {1->2[ colorscheme=svg labelfontcolor= ]}")
				.assertTextAtCursorPosition(46,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(46, "/").expectContent(
						"digraph {1->2[ colorscheme=svg labelfontcolor=/ ]}");

		// test local attribute values with quotes
		newBuilder()
				.append("digraph {1->2[ colorscheme=svg labelfontcolor=\"\" ]}")
				.assertTextAtCursorPosition(47,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(47, "#").expectContent(
						"digraph {1->2[ colorscheme=svg labelfontcolor=\"#\" ]}");

		// test local attribute values with prefix
		newBuilder().append("digraph {1->2[ labelfontcolor=gray ]}")
				.assertTextAtCursorPosition(34, "gray", "gray0", "gray1",
						"gray10", "gray100", "gray11", "gray12", "gray13",
						"gray14", "gray15", "gray16", "gray17", "gray18",
						"gray19", "gray2", "gray20", "gray21", "gray22",
						"gray23", "gray24", "gray25", "gray26", "gray27",
						"gray28", "gray29", "gray3", "gray30", "gray31",
						"gray32", "gray33", "gray34", "gray35", "gray36",
						"gray37", "gray38", "gray39", "gray4", "gray40",
						"gray41", "gray42", "gray43", "gray44", "gray45",
						"gray46", "gray47", "gray48", "gray49", "gray5",
						"gray50", "gray51", "gray52", "gray53", "gray54",
						"gray55", "gray56", "gray57", "gray58", "gray59",
						"gray6", "gray60", "gray61", "gray62", "gray63",
						"gray64", "gray65", "gray66", "gray67", "gray68",
						"gray69", "gray7", "gray70", "gray71", "gray72",
						"gray73", "gray74", "gray75", "gray76", "gray77",
						"gray78", "gray79", "gray8", "gray80", "gray81",
						"gray82", "gray83", "gray84", "gray85", "gray86",
						"gray87", "gray88", "gray89", "gray9", "gray90",
						"gray91", "gray92", "gray93", "gray94", "gray95",
						"gray96", "gray97", "gray98", "gray99", ",", ";", "]")
				.applyProposal(34, "gray")
				.expectContent("digraph {1->2[ labelfontcolor=gray ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("digraph {1->2[ labelfontcolor=\"gray\" ]}")
				.assertTextAtCursorPosition(35, "gray", "gray0", "gray1",
						"gray10", "gray100", "gray11", "gray12", "gray13",
						"gray14", "gray15", "gray16", "gray17", "gray18",
						"gray19", "gray2", "gray20", "gray21", "gray22",
						"gray23", "gray24", "gray25", "gray26", "gray27",
						"gray28", "gray29", "gray3", "gray30", "gray31",
						"gray32", "gray33", "gray34", "gray35", "gray36",
						"gray37", "gray38", "gray39", "gray4", "gray40",
						"gray41", "gray42", "gray43", "gray44", "gray45",
						"gray46", "gray47", "gray48", "gray49", "gray5",
						"gray50", "gray51", "gray52", "gray53", "gray54",
						"gray55", "gray56", "gray57", "gray58", "gray59",
						"gray6", "gray60", "gray61", "gray62", "gray63",
						"gray64", "gray65", "gray66", "gray67", "gray68",
						"gray69", "gray7", "gray70", "gray71", "gray72",
						"gray73", "gray74", "gray75", "gray76", "gray77",
						"gray78", "gray79", "gray8", "gray80", "gray81",
						"gray82", "gray83", "gray84", "gray85", "gray86",
						"gray87", "gray88", "gray89", "gray9", "gray90",
						"gray91", "gray92", "gray93", "gray94", "gray95",
						"gray96", "gray97", "gray98", "gray99")
				.applyProposal(35, "gray99")
				.expectContent("digraph {1->2[ labelfontcolor=\"gray99\" ]}");
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
	public void edge_taillabel() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ taillabel= ]}")
				.assertTextAtCursorPosition(23,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("digraph {1->2[ taillabel= ]}")
				.assertTextAtCursorPosition(25,
						"HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("digraph {1->2[ taillabel=<  >]}")
				.assertTextAtCursorPosition(27, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");
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
	public void edge_xlabel() throws Exception {
		// test global attribute values
		newBuilder().append("graph {edge[ xlabel= ]}")
				.assertTextAtCursorPosition(20,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("digraph {1->2[ xlabel= ]}")
				.assertTextAtCursorPosition(22,
						"HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("digraph {1->2[ xlabel=<  >]}")
				.assertTextAtCursorPosition(24, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");
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
				.assertTextAtCursorPosition(13, "]", "bb", "bgcolor",
						"clusterrank", "colorscheme", "fontcolor",
						"forcelabels", "id", "label", "layout", "lp", "nodesep",
						"outputorder", "pagedir", "rankdir", "splines", "style")
				.applyProposal(13, "forcelabels")
				.expectContent("graph {graph[forcelabels]}");

		// test local attribute names
		newBuilder().append("graph {  }")
				.assertTextAtCursorPosition(8, "bb", "bgcolor", "clusterrank",
						"colorscheme", "fontcolor", "edge", "graph", "node",
						"subgraph", "{", "}", "forcelabels", "id", "label",
						"layout", "lp", "nodesep", "outputorder", "pagedir",
						"rankdir", "splines", "style")
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
	public void graph_bgcolor() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ colorscheme=svg bgcolor= ]}")
				.assertTextAtCursorPosition(38,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(38, "aliceblue").expectContent(
						"graph {graph[ colorscheme=svg bgcolor=aliceblue ]}");

		// test local attribute values
		newBuilder().append("graph { colorscheme=svg bgcolor= }")
				.assertTextAtCursorPosition(32,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(32, "aqua")
				.expectContent("graph { colorscheme=svg bgcolor=aqua }");

		// test local attribute values with quotes
		newBuilder().append("graph { colorscheme=svg bgcolor=\"\" }")
				.assertTextAtCursorPosition(33,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(33, "aquamarine").expectContent(
						"graph { colorscheme=svg bgcolor=\"aquamarine\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { colorscheme=svg bgcolor=aqua }")
				.assertTextAtCursorPosition(36, "aqua", "aquamarine", ";", "{",
						"}")
				.applyProposal(36, "aqua")
				.expectContent("graph { colorscheme=svg bgcolor=aqua }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { colorscheme=svg bgcolor=\"dark\" }")
				.assertTextAtCursorPosition(37, "darkblue", "darkcyan",
						"darkgoldenrod", "darkgray", "darkgreen", "darkgrey",
						"darkkhaki", "darkmagenta", "darkolivegreen",
						"darkorange", "darkorchid", "darkred", "darksalmon",
						"darkseagreen", "darkslateblue", "darkslategray",
						"darkslategrey", "darkturquoise", "darkviolet")
				.applyProposal(37, "darkturquoise").expectContent(
						"graph { colorscheme=svg bgcolor=\"darkturquoise\" }");
	}

	@Test
	public void graph_clusterrank() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ clusterrank= ]}")
				.assertTextAtCursorPosition(26, "local", "global", "none")
				.applyProposal(26, "local")
				.expectContent("graph {graph[ clusterrank=local ]}");

		// test local attribute values
		newBuilder().append("graph { clusterrank= }")
				.assertTextAtCursorPosition(20, "local", "global", "none")
				.applyProposal(20, "global")
				.expectContent("graph { clusterrank=global }");

		// test local attribute values with quotes
		newBuilder().append("graph { clusterrank=\"\" }")
				.assertTextAtCursorPosition(21, "local", "global", "none")
				.applyProposal(21, "none")
				.expectContent("graph { clusterrank=\"none\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { clusterrank=l }")
				.assertTextAtCursorPosition(21, "local", ";", "{", "}")
				.applyProposal(21, "local")
				.expectContent("graph { clusterrank=local }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { clusterrank=\"g\" }")
				.assertTextAtCursorPosition(22, "global")
				.applyProposal(22, "global")
				.expectContent("graph { clusterrank=\"global\" }");
	}

	@Test
	public void graph_colorscheme() throws Exception {
		String[] expectedDotColorSchemes = DotTestUtils.expectedDotColorSchemes;
		// test global attribute values
		newBuilder().append("graph {graph[ colorscheme= ]}")
				.assertTextAtCursorPosition(26, expectedDotColorSchemes)
				.applyProposal(26, "svg")
				.expectContent("graph {graph[ colorscheme=svg ]}");

		// test local attribute values
		newBuilder().append("graph { colorscheme= }")
				.assertTextAtCursorPosition(20, expectedDotColorSchemes)
				.applyProposal(20, "svg")
				.expectContent("graph { colorscheme=svg }");

		// test local attribute values with quotes
		newBuilder().append("graph { colorscheme=\"\" }")
				.assertTextAtCursorPosition(21, expectedDotColorSchemes)
				.applyProposal(21, "blues3")
				.expectContent("graph { colorscheme=\"blues3\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { colorscheme=blues }")
				.assertTextAtCursorPosition(25, "blues3", "blues4", "blues5",
						"blues6", "blues7", "blues8", "blues9", ";", "{", "}")
				.applyProposal(25, "blues5")
				.expectContent("graph { colorscheme=blues5 }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { colorscheme=\"blues\" }")
				.assertTextAtCursorPosition(26, "blues3", "blues4", "blues5",
						"blues6", "blues7", "blues8", "blues9")
				.applyProposal(26, "blues9")
				.expectContent("graph { colorscheme=\"blues9\" }");
	}

	@Test
	public void graph_fontcolor() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ colorscheme=brbg11 fontcolor= ]}")
				.assertTextAtCursorPosition(43, "#", "/", "1", "2", "3", "4",
						"5", "6", "7", "8", "9", "10", "11")
				.applyProposal(43, "1").expectContent(
						"graph {graph[ colorscheme=brbg11 fontcolor=1 ]}");

		// test local attribute values
		newBuilder().append("graph { colorscheme=brbg11 fontcolor= }")
				.assertTextAtCursorPosition(37, "#", "/", "1", "2", "3", "4",
						"5", "6", "7", "8", "9", "10", "11")
				.applyProposal(37, "11")
				.expectContent("graph { colorscheme=brbg11 fontcolor=11 }");

		// test local attribute values with quotes
		newBuilder().append("graph { colorscheme=brbg11 fontcolor=\"\" }")
				.assertTextAtCursorPosition(38, "#", "/", "1", "2", "3", "4",
						"5", "6", "7", "8", "9", "10", "11")
				.applyProposal(38, "10")
				.expectContent("graph { colorscheme=brbg11 fontcolor=\"10\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { colorscheme=brbg11 fontcolor=1 }")
				.assertTextAtCursorPosition(38, "1", "10", "11", ",", ";", "{",
						"}", "bb", "bgcolor", "colorscheme", "clusterrank",
						"edge", "fontcolor", "forcelabels", "graph", "id",
						"label", "layout", "lp", "node", "nodesep",
						"outputorder", "pagedir", "rankdir", "splines", "style",
						"subgraph")
				.applyProposal(38, "10")
				.expectContent("graph { colorscheme=brbg11 fontcolor=10 }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { colorscheme=brbg11 fontcolor=\"1\" }")
				.assertTextAtCursorPosition(39, "1", "10", "11", ",")
				.applyProposal(39, "11")
				.expectContent("graph { colorscheme=brbg11 fontcolor=\"11\" }");
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
	public void graph_label() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ label= ]}")
				.assertTextAtCursorPosition(20,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("graph {headlabel= }").assertTextAtCursorPosition(
				17, "HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("graph { label = <  > }")
				.assertTextAtCursorPosition(18, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");

		// test html-like label attribute value
		newBuilder().append("graph { label = < <TABLE ROWS=\"\"></TABLE> > }")
				.assertTextAtCursorPosition(31, "*").applyProposal(31, "*")
				.expectContent(
						"graph { label = < <TABLE ROWS=\"*\"></TABLE> > }");
	}

	@Test
	public void graph_layout() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ layout= ]}")
				.assertTextAtCursorPosition(21, "circo", "dot", "fdp", "neato",
						"osage", "sfdp", "twopi")
				.applyProposal(21, "circo")
				.expectContent("graph {graph[ layout=circo ]}");

		// test local attribute values
		newBuilder().append("graph { layout= }")
				.assertTextAtCursorPosition(15, "circo", "dot", "fdp", "neato",
						"osage", "sfdp", "twopi")
				.applyProposal(15, "osage")
				.expectContent("graph { layout=osage }");

		// test local attribute values with quotes
		newBuilder().append("graph { layout=\"\" }")
				.assertTextAtCursorPosition(16, "circo", "dot", "fdp", "neato",
						"osage", "sfdp", "twopi")
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
	public void graph_outputorder() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ outputorder= ]}")
				.assertTextAtCursorPosition(26, "breadthfirst", "nodesfirst",
						"edgesfirst")
				.applyProposal(26, "breadthfirst")
				.expectContent("graph {graph[ outputorder=breadthfirst ]}");

		// test local attribute values
		newBuilder().append("graph { outputorder= }")
				.assertTextAtCursorPosition(20, "breadthfirst", "nodesfirst",
						"edgesfirst")
				.applyProposal(20, "nodesfirst")
				.expectContent("graph { outputorder=nodesfirst }");

		// test local attribute values with quotes
		newBuilder().append("graph { outputorder=\"\" }")
				.assertTextAtCursorPosition(21, "breadthfirst", "nodesfirst",
						"edgesfirst")
				.applyProposal(21, "edgesfirst")
				.expectContent("graph { outputorder=\"edgesfirst\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { outputorder=b }")
				.assertTextAtCursorPosition(21, "breadthfirst", ";", "{", "}")
				.applyProposal(21, "breadthfirst")
				.expectContent("graph { outputorder=breadthfirst }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { outputorder=\"n\" }")
				.assertTextAtCursorPosition(22, "nodesfirst")
				.applyProposal(22, "nodesfirst")
				.expectContent("graph { outputorder=\"nodesfirst\" }");
	}

	@Test
	public void graph_pagedir() throws Exception {
		// test global attribute values
		newBuilder().append("graph {graph[ pagedir= ]}")
				.assertTextAtCursorPosition(22, "BL", "BR", "TL", "TR", "RB",
						"RT", "LB", "LT")
				.applyProposal(22, "BL")
				.expectContent("graph {graph[ pagedir=BL ]}");

		// test local attribute values
		newBuilder().append("graph { pagedir= }")
				.assertTextAtCursorPosition(16, "BL", "BR", "TL", "TR", "RB",
						"RT", "LB", "LT")
				.applyProposal(16, "BR").expectContent("graph { pagedir=BR }");

		// test local attribute values with quotes
		newBuilder().append("graph { pagedir=\"\" }")
				.assertTextAtCursorPosition(17, "BL", "BR", "TL", "TR", "RB",
						"RT", "LB", "LT")
				.applyProposal(17, "TL")
				.expectContent("graph { pagedir=\"TL\" }");

		// test local attribute values with prefix
		newBuilder().append("graph { pagedir=B }")
				.assertTextAtCursorPosition(17, "BL", "BR", ";", "{", "}")
				.applyProposal(17, "BL").expectContent("graph { pagedir=BL }");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph { pagedir=\"T\" }")
				.assertTextAtCursorPosition(18, "TL", "TR")
				.applyProposal(18, "TL")
				.expectContent("graph { pagedir=\"TL\" }");
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
				.assertTextAtCursorPosition(12, "]", "color", "colorscheme",
						"distortion", "fillcolor", "fixedsize", "fontcolor",
						"height", "id", "label", "pos", "shape", "sides",
						"skew", "style", "width", "xlabel", "xlp")
				.applyProposal(12, "distortion")
				.expectContent("graph {node[distortion]}");

		// test local attribute names
		newBuilder().append("graph {1[  ]}")
				.assertTextAtCursorPosition(10, "]", "color", "colorscheme",
						"distortion", "fillcolor", "fixedsize", "fontcolor",
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
	public void node_color() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ color= ]}")
				.assertTextAtCursorPosition(19,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(19, "#")
				.expectContent("graph {node[ color=# ]}");

		// test local attribute values
		newBuilder().append("graph {1[ color= ]}")
				.assertTextAtCursorPosition(16,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(16, "#").expectContent("graph {1[ color=# ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ color=\"\" ]}")
				.assertTextAtCursorPosition(17,
						combine(expectedX11ColorNames, "#", "/"))
				.applyProposal(17, "#")
				.expectContent("graph {1[ color=\"#\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1[ color=light ]}")
				.assertTextAtCursorPosition(21, "lightblue", "lightblue1",
						"lightblue2", "lightblue3", "lightblue4", "lightcoral",
						"lightcyan", "lightcyan1", "lightcyan2", "lightcyan3",
						"lightcyan4", "lightgoldenrod", "lightgoldenrod1",
						"lightgoldenrod2", "lightgoldenrod3", "lightgoldenrod4",
						"lightgoldenrodyellow", "lightgray", "lightgrey",
						"lightpink", "lightpink1", "lightpink2", "lightpink3",
						"lightpink4", "lightsalmon", "lightsalmon1",
						"lightsalmon2", "lightsalmon3", "lightsalmon4",
						"lightseagreen", "lightskyblue", "lightskyblue1",
						"lightskyblue2", "lightskyblue3", "lightskyblue4",
						"lightslateblue", "lightslategray", "lightslategrey",
						"lightsteelblue", "lightsteelblue1", "lightsteelblue2",
						"lightsteelblue3", "lightsteelblue4", "lightyellow",
						"lightyellow1", "lightyellow2", "lightyellow3",
						"lightyellow4", ",", ";", "]")
				.applyProposal(21, "lightblue")
				.expectContent("graph {1[ color=lightblue ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ color=\"lights\" ]}")
				.assertTextAtCursorPosition(23, "lightsalmon", "lightsalmon1",
						"lightsalmon2", "lightsalmon3", "lightsalmon4",
						"lightseagreen", "lightskyblue", "lightskyblue1",
						"lightskyblue2", "lightskyblue3", "lightskyblue4",
						"lightslateblue", "lightslategray", "lightslategrey",
						"lightsteelblue", "lightsteelblue1", "lightsteelblue2",
						"lightsteelblue3", "lightsteelblue4")
				.applyProposal(23, "lightskyblue")
				.expectContent("graph {1[ color=\"lightskyblue\" ]}");
	}

	@Test
	public void node_colorscheme() throws Exception {
		String[] expectedDotColorSchemes = DotTestUtils.expectedDotColorSchemes;
		// test global attribute values
		newBuilder().append("graph {node[ colorscheme= ]}")
				.assertTextAtCursorPosition(25, expectedDotColorSchemes)
				.applyProposal(25, "brbg10")
				.expectContent("graph {node[ colorscheme=brbg10 ]}");

		// test local attribute values
		newBuilder().append("graph {1[ colorscheme= ]}")
				.assertTextAtCursorPosition(22, expectedDotColorSchemes)
				.applyProposal(22, "brbg11")
				.expectContent("graph {1[ colorscheme=brbg11 ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ colorscheme=\"\" ]}")
				.assertTextAtCursorPosition(23, expectedDotColorSchemes)
				.applyProposal(23, "brbg3")
				.expectContent("graph {1[ colorscheme=\"brbg3\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1[ colorscheme=br ]}")
				.assertTextAtCursorPosition(24, "brbg3", "brbg4", "brbg5",
						"brbg6", "brbg7", "brbg8", "brbg9", "brbg10", "brbg11",
						",", ";", "]")
				.applyProposal(24, "brbg4")
				.expectContent("graph {1[ colorscheme=brbg4 ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ colorscheme=\"brbg\" ]}")
				.assertTextAtCursorPosition(27, "brbg3", "brbg4", "brbg5",
						"brbg6", "brbg7", "brbg8", "brbg9", "brbg10", "brbg11")
				.applyProposal(27, "brbg5")
				.expectContent("graph {1[ colorscheme=\"brbg5\" ]}");
	}

	@Test
	public void node_fillcolor() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ colorscheme=svg fillcolor= ]}")
				.assertTextAtCursorPosition(39,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(39, "#")
				.expectContent("graph {node[ colorscheme=svg fillcolor=# ]}");

		// test local attribute values
		newBuilder().append("graph {1[ colorscheme=svg fillcolor= ]}")
				.assertTextAtCursorPosition(36,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(36, "#")
				.expectContent("graph {1[ colorscheme=svg fillcolor=# ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ colorscheme=svg fillcolor=\"\" ]}")
				.assertTextAtCursorPosition(37,
						combine(expectedSvgColorNames, "#", "/"))
				.applyProposal(37, "#")
				.expectContent("graph {1[ colorscheme=svg fillcolor=\"#\" ]}");

		// test local attribute values with prefix
		newBuilder().append("graph {1[ colorscheme=svg fillcolor=sa ]}")
				.assertTextAtCursorPosition(38, "saddlebrown", "salmon",
						"sandybrown", ",", ";", "]")
				.applyProposal(38, "salmon")
				.expectContent("graph {1[ colorscheme=svg fillcolor=salmon ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ colorscheme=svg fillcolor=\"sa\"]}")
				.assertTextAtCursorPosition(39, "saddlebrown", "salmon",
						"sandybrown")
				.applyProposal(39, "sandybrown").expectContent(
						"graph {1[ colorscheme=svg fillcolor=\"sandybrown\"]}");
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
	public void node_fontcolor() throws Exception {
		String[] expectedDotColorSchemes = DotTestUtils.expectedDotColorSchemes;
		// test global attribute values
		newBuilder().append("graph {node[ fontcolor=/ ]}")
				.assertTextAtCursorPosition(24,
						combine(expectedDotColorSchemes, "/"))
				.applyProposal(24, "accent3")
				.expectContent("graph {node[ fontcolor=/accent3 ]}");

		// test local attribute values
		newBuilder().append("graph {1[ fontcolor=/ ]}")
				.assertTextAtCursorPosition(21,
						combine(expectedDotColorSchemes, "/"))
				.applyProposal(21, "accent3")
				.expectContent("graph {1[ fontcolor=/accent3 ]}");

		// test local attribute values with quotes
		newBuilder().append("graph {1[ fontcolor=\"/accent3/\" ]}")
				.assertTextAtCursorPosition(30, "/", "1", "2", "3")
				.applyProposal(30, "1")
				.expectContent("graph {1[ fontcolor=\"/accent3/1\" ]}"); //

		// test local attribute values with prefix
		newBuilder().append("graph {1[ colorscheme=svg fontcolor=w ]}")
				.assertTextAtCursorPosition(37, "wheat", "white", "whitesmoke",
						",", ";", "]")
				.applyProposal(37, "white")
				.expectContent("graph {1[ colorscheme=svg fontcolor=white ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ fontcolor=\"/accent ]}")
				.assertTextAtCursorPosition(28, "accent3", "accent4", "accent5",
						"accent6", "accent7", "accent8", "/")
				.applyProposal(28, "accent3")
				.expectContent("graph {1[ fontcolor=\"/accent3 ]}");
	}

	@Test
	public void node_label() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ label= ]}")
				.assertTextAtCursorPosition(19,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("graph {1[ label= ]}")
				.assertTextAtCursorPosition(16, "HTMLLabel - Insert a template")
				.applyProposal(16, "HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("graph {1[ label=<  >]}")
				.assertTextAtCursorPosition(18, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");
		
		// test html-like label attribute value
		newBuilder().append("graph {1[ label=< <BR ALIGN=\"\" /> >]}")
				.assertTextAtCursorPosition(29, "CENTER", "LEFT", "RIGHT")
				.applyProposal(29, "CENTER")
				.expectContent("graph {1[ label=< <BR ALIGN=\"CENTER\" /> >]}");
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
		newBuilder().append("graph {1[ shape=pr ]}")
				.assertTextAtCursorPosition(18, "primersite", "promoter",
						"proteasesite", "proteinstab", ",", ";", "]")
				.applyProposal(18, "primersite")
				.expectContent("graph {1[ shape=primersite ]}");

		// test local attribute values with quotes and prefix
		newBuilder().append("graph {1[ shape=\"pro\" ]}")
				.assertTextAtCursorPosition(20, "promoter", "proteasesite",
						"proteinstab")
				.applyProposal(20, "proteinstab")
				.expectContent("graph {1[ shape=\"proteinstab\" ]}");
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
	public void node_xlabel() throws Exception {
		// test global attribute values
		newBuilder().append("graph {node[ xlabel= ]}")
				.assertTextAtCursorPosition(20,
						"HTMLLabel - Insert a template");

		// test local attribute values
		newBuilder().append("graph {1[ xlabel= ]}").assertTextAtCursorPosition(
				17, "HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("graph {1[ xlabel=<  >]}")
				.assertTextAtCursorPosition(19, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");
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

	@Test
	public void subgraph_label() throws Exception {
		newBuilder().append("graph{subgraph cluster{label = }}")
				.assertTextAtCursorPosition(30,
						"HTMLLabel - Insert a template");

		// test html-like label attribute
		newBuilder().append("graph{subgraph cluster{label = <  >}}")
				.assertTextAtCursorPosition(33, "<B></B>", "<BR/>",
						"<FONT></FONT>", "<I></I>", "<O></O>", "<S></S>",
						"<SUB></SUB>", "<SUP></SUP>", "<TABLE></TABLE>",
						"<U></U>");

	}

	private String[] combine(String[] array1, String... array2) {
		String[] both = Stream
				.concat(Arrays.stream(array1), Arrays.stream(array2))
				.toArray(String[]::new);
		return both;
	}
}
