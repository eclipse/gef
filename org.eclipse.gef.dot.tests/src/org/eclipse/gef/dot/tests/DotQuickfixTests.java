/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.language.dot.DotAst;
import org.eclipse.gef.dot.internal.ui.language.quickfix.DotQuickfixProvider;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IssueModificationContext;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public class DotQuickfixTests {

	@Inject
	private Injector injector;

	@Inject
	private ParseHelper<DotAst> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Inject
	private DotQuickfixProvider quickfixProvider;

	@Test
	public void edge_arrowhead() {
		String[] deprecatedArrowShapes = { "ediamond", "open", "halfopen",
				"empty", "invempty" };
		String[] validArrowShapes = { "odiamond", "vee", "lvee", "onormal",
				"oinv" };

		// test unquoted attribute value
		for (int i = 0; i < deprecatedArrowShapes.length; i++) {
			String deprecatedArrowShape = deprecatedArrowShapes[i];
			String validArrowShape = validArrowShapes[i];

			String text = "digraph{1->2[arrowhead=" + deprecatedArrowShape
					+ "]}";

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			String[][] expectedQuickfixes = { {
					"Replace '" + deprecatedArrowShape + "' with '"
							+ validArrowShape + "'.",
					"Use valid '" + validArrowShape + "' instead of invalid '"
							+ deprecatedArrowShape + "' edge arrowhead.",
					"digraph{1->2[arrowhead=" + validArrowShape + "]}" } };

			assertQuickfixes(text, expectedQuickfixes);
		}

		// test quoted attribute value
		for (int i = 0; i < deprecatedArrowShapes.length; i++) {
			String deprecatedArrowShape = deprecatedArrowShapes[i];
			String validArrowShape = validArrowShapes[i];

			String text = "digraph{1->2[arrowhead=\"" + deprecatedArrowShape
					+ "\"]}";

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			String[][] expectedQuickfixes = { {
					"Replace '" + deprecatedArrowShape + "' with '"
							+ validArrowShape + "'.",
					"Use valid '" + validArrowShape + "' instead of invalid '"
							+ deprecatedArrowShape + "' edge arrowhead.",
					"digraph{1->2[arrowhead=\"" + validArrowShape + "\"]}" } };

			assertQuickfixes(text, expectedQuickfixes);
		}
	}

	@Test
	public void edge_arrowtail() {
		String[] deprecatedArrowShapes = { "ediamond", "open", "halfopen",
				"empty", "invempty" };
		String[] validArrowShapes = { "odiamond", "vee", "lvee", "onormal",
				"oinv" };

		// test unquoted attribute value
		for (int i = 0; i < deprecatedArrowShapes.length; i++) {
			String deprecatedArrowShape = deprecatedArrowShapes[i];
			String validArrowShape = validArrowShapes[i];

			String text = "digraph{1->2[arrowtail=" + deprecatedArrowShape
					+ "]}";

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			String[][] expectedQuickfixes = { {
					"Replace '" + deprecatedArrowShape + "' with '"
							+ validArrowShape + "'.",
					"Use valid '" + validArrowShape + "' instead of invalid '"
							+ deprecatedArrowShape + "' edge arrowtail.",
					"digraph{1->2[arrowtail=" + validArrowShape + "]}" } };

			assertQuickfixes(text, expectedQuickfixes);
		}

		// test quoted attribute value
		for (int i = 0; i < deprecatedArrowShapes.length; i++) {
			String deprecatedArrowShape = deprecatedArrowShapes[i];
			String validArrowShape = validArrowShapes[i];

			String text = "digraph{1->2[arrowtail=\"" + deprecatedArrowShape
					+ "\"]}";

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			String[][] expectedQuickfixes = { {
					"Replace '" + deprecatedArrowShape + "' with '"
							+ validArrowShape + "'.",
					"Use valid '" + validArrowShape + "' instead of invalid '"
							+ deprecatedArrowShape + "' edge arrowtail.",
					"digraph{1->2[arrowtail=\"" + validArrowShape + "\"]}" } };

			assertQuickfixes(text, expectedQuickfixes);
		}
	}

	@Test
	public void edge_colorscheme() {
		// test unquoted attribute value
		String text = "graph{1--2[colorscheme=foo]}";
		String[][] expectedQuickfixes = new String[expectedDotColorSchemes.length][3];

		for (int i = 0; i < expectedDotColorSchemes.length; i++) {
			String validColorScheme = expectedDotColorSchemes[i];

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			expectedQuickfixes[i] = new String[] {
					"Replace 'foo' with '" + validColorScheme + "'.",
					"Use valid '" + validColorScheme
							+ "' instead of invalid 'foo' colorscheme.",
					"graph{1--2[colorscheme=" + validColorScheme + "]}" };
		}
		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void edge_dir() {
		// test unquoted attribute value
		String text = "graph{1--2[dir=foo]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'forward'.",
						"Use valid 'forward' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=forward]}" },
				{ "Replace 'foo' with 'back'.",
						"Use valid 'back' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=back]}" },
				{ "Replace 'foo' with 'both'.",
						"Use valid 'both' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=both]}" },
				{ "Replace 'foo' with 'none'.",
						"Use valid 'none' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=none]}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{1--2[dir=\"foo\"]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'forward'.",
						"Use valid 'forward' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=\"forward\"]}" },
				{ "Replace 'foo' with 'back'.",
						"Use valid 'back' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=\"back\"]}" },
				{ "Replace 'foo' with 'both'.",
						"Use valid 'both' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=\"both\"]}" },
				{ "Replace 'foo' with 'none'.",
						"Use valid 'none' instead of invalid 'foo' edge dir.",
						"graph{1--2[dir=\"none\"]}" } };

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void edge_style() {
		// test unquoted attribute value
		String text = "graph{1--2[style=foo]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'bold'.",
						"Use valid 'bold' instead of invalid 'foo' edge style.",
						"graph{1--2[style=bold]}" },
				{ "Replace 'foo' with 'dashed'.",
						"Use valid 'dashed' instead of invalid 'foo' edge style.",
						"graph{1--2[style=dashed]}" },
				{ "Replace 'foo' with 'dotted'.",
						"Use valid 'dotted' instead of invalid 'foo' edge style.",
						"graph{1--2[style=dotted]}" },
				{ "Replace 'foo' with 'invis'.",
						"Use valid 'invis' instead of invalid 'foo' edge style.",
						"graph{1--2[style=invis]}" },
				{ "Replace 'foo' with 'solid'.",
						"Use valid 'solid' instead of invalid 'foo' edge style.",
						"graph{1--2[style=solid]}" },
				{ "Replace 'foo' with 'tapered'.",
						"Use valid 'tapered' instead of invalid 'foo' edge style.",
						"graph{1--2[style=tapered]}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{1--2[style=\"foo\"]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'bold'.",
						"Use valid 'bold' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"bold\"]}" },
				{ "Replace 'foo' with 'dashed'.",
						"Use valid 'dashed' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"dashed\"]}" },
				{ "Replace 'foo' with 'dotted'.",
						"Use valid 'dotted' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"dotted\"]}" },
				{ "Replace 'foo' with 'invis'.",
						"Use valid 'invis' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"invis\"]}" },
				{ "Replace 'foo' with 'solid'.",
						"Use valid 'solid' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"solid\"]}" },
				{ "Replace 'foo' with 'tapered'.",
						"Use valid 'tapered' instead of invalid 'foo' edge style.",
						"graph{1--2[style=\"tapered\"]}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test deprecated attribute value
		text = "graph{1--2[style=\"setlinewidth(3)\"]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {};

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void graph_colorscheme() {
		// test unquoted attribute value
		String text = "graph{colorscheme=foo}";
		String[][] expectedQuickfixes = new String[expectedDotColorSchemes.length][3];

		for (int i = 0; i < expectedDotColorSchemes.length; i++) {
			String validColorScheme = expectedDotColorSchemes[i];

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			expectedQuickfixes[i] = new String[] {
					"Replace 'foo' with '" + validColorScheme + "'.",
					"Use valid '" + validColorScheme
							+ "' instead of invalid 'foo' colorscheme.",
					"graph{colorscheme=" + validColorScheme + "}" };
		}
		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void graph_clusterrank() {
		// test unquoted attribute value
		String text = "graph{clusterrank=foo}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'local'.",
						"Use valid 'local' instead of invalid 'foo' graph clusterMode.",
						"graph{clusterrank=local}" },
				{ "Replace 'foo' with 'global'.",
						"Use valid 'global' instead of invalid 'foo' graph clusterMode.",
						"graph{clusterrank=global}" },
				{ "Replace 'foo' with 'none'.",
						"Use valid 'none' instead of invalid 'foo' graph clusterMode.",
						"graph{clusterrank=none}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{clusterrank=\"foo\"}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'local'.",
						"Use valid 'local' instead of invalid 'foo' graph clusterMode.",
						"graph{clusterrank=\"local\"}" },
				{ "Replace 'foo' with 'global'.",
						"Use valid 'global' instead of invalid 'foo' graph clusterMode.",
						"graph{clusterrank=\"global\"}" },
				{ "Replace 'foo' with 'none'.",
						"Use valid 'none' instead of invalid 'foo' graph clusterMode.",
						"graph{clusterrank=\"none\"}" } };

		assertQuickfixes(text, expectedQuickfixes);

	}

	@Test
	public void graph_layout() {
		// test unquoted attribute value
		String text = "graph{layout=foo}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'circo'.",
						"Use valid 'circo' instead of invalid 'foo' graph layout.",
						"graph{layout=circo}" },
				{ "Replace 'foo' with 'dot'.",
						"Use valid 'dot' instead of invalid 'foo' graph layout.",
						"graph{layout=dot}" },
				{ "Replace 'foo' with 'fdp'.",
						"Use valid 'fdp' instead of invalid 'foo' graph layout.",
						"graph{layout=fdp}" },
				{ "Replace 'foo' with 'neato'.",
						"Use valid 'neato' instead of invalid 'foo' graph layout.",
						"graph{layout=neato}" },
				{ "Replace 'foo' with 'osage'.",
						"Use valid 'osage' instead of invalid 'foo' graph layout.",
						"graph{layout=osage}" },
				{ "Replace 'foo' with 'sfdp'.",
						"Use valid 'sfdp' instead of invalid 'foo' graph layout.",
						"graph{layout=sfdp}" },
				{ "Replace 'foo' with 'twopi'.",
						"Use valid 'twopi' instead of invalid 'foo' graph layout.",
						"graph{layout=twopi}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{layout=\"foo\"}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'circo'.",
						"Use valid 'circo' instead of invalid 'foo' graph layout.",
						"graph{layout=\"circo\"}" },
				{ "Replace 'foo' with 'dot'.",
						"Use valid 'dot' instead of invalid 'foo' graph layout.",
						"graph{layout=\"dot\"}" },
				{ "Replace 'foo' with 'fdp'.",
						"Use valid 'fdp' instead of invalid 'foo' graph layout.",
						"graph{layout=\"fdp\"}" },
				{ "Replace 'foo' with 'neato'.",
						"Use valid 'neato' instead of invalid 'foo' graph layout.",
						"graph{layout=\"neato\"}" },
				{ "Replace 'foo' with 'osage'.",
						"Use valid 'osage' instead of invalid 'foo' graph layout.",
						"graph{layout=\"osage\"}" },
				{ "Replace 'foo' with 'sfdp'.",
						"Use valid 'sfdp' instead of invalid 'foo' graph layout.",
						"graph{layout=\"sfdp\"}" },
				{ "Replace 'foo' with 'twopi'.",
						"Use valid 'twopi' instead of invalid 'foo' graph layout.",
						"graph{layout=\"twopi\"}" } };

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void graph_outputorder() {
		// test unquoted attribute value
		String text = "graph{outputorder=foo}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'breadthfirst'.",
						"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.",
						"graph{outputorder=breadthfirst}" },
				{ "Replace 'foo' with 'nodesfirst'.",
						"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.",
						"graph{outputorder=nodesfirst}" },
				{ "Replace 'foo' with 'edgesfirst'.",
						"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.",
						"graph{outputorder=edgesfirst}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{outputorder=\"foo\"}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'breadthfirst'.",
						"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.",
						"graph{outputorder=\"breadthfirst\"}" },
				{ "Replace 'foo' with 'nodesfirst'.",
						"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.",
						"graph{outputorder=\"nodesfirst\"}" },
				{ "Replace 'foo' with 'edgesfirst'.",
						"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.",
						"graph{outputorder=\"edgesfirst\"}" } };

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void graph_pagedir() {
		// test unquoted attribute value
		String text = "graph{pagedir=foo}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'BL'.",
						"Use valid 'BL' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=BL}" },
				{ "Replace 'foo' with 'BR'.",
						"Use valid 'BR' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=BR}" },
				{ "Replace 'foo' with 'TL'.",
						"Use valid 'TL' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=TL}" },
				{ "Replace 'foo' with 'TR'.",
						"Use valid 'TR' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=TR}" },
				{ "Replace 'foo' with 'RB'.",
						"Use valid 'RB' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=RB}" },
				{ "Replace 'foo' with 'RT'.",
						"Use valid 'RT' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=RT}" },
				{ "Replace 'foo' with 'LB'.",
						"Use valid 'LB' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=LB}" },
				{ "Replace 'foo' with 'LT'.",
						"Use valid 'LT' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=LT}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{pagedir=\"foo\"}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'BL'.",
						"Use valid 'BL' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"BL\"}" },
				{ "Replace 'foo' with 'BR'.",
						"Use valid 'BR' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"BR\"}" },
				{ "Replace 'foo' with 'TL'.",
						"Use valid 'TL' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"TL\"}" },
				{ "Replace 'foo' with 'TR'.",
						"Use valid 'TR' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"TR\"}" },
				{ "Replace 'foo' with 'RB'.",
						"Use valid 'RB' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"RB\"}" },
				{ "Replace 'foo' with 'RT'.",
						"Use valid 'RT' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"RT\"}" },
				{ "Replace 'foo' with 'LB'.",
						"Use valid 'LB' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"LB\"}" },
				{ "Replace 'foo' with 'LT'.",
						"Use valid 'LT' instead of invalid 'foo' graph pagedir.",
						"graph{pagedir=\"LT\"}" } };

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void graph_rankdir() {
		// test unquoted attribute value
		String text = "graph{rankdir=foo}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'TB'.",
						"Use valid 'TB' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=TB}" },
				{ "Replace 'foo' with 'LR'.",
						"Use valid 'LR' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=LR}" },
				{ "Replace 'foo' with 'BT'.",
						"Use valid 'BT' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=BT}" },
				{ "Replace 'foo' with 'RL'.",
						"Use valid 'RL' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=RL}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{rankdir=\"foo\"}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'TB'.",
						"Use valid 'TB' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=\"TB\"}" },
				{ "Replace 'foo' with 'LR'.",
						"Use valid 'LR' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=\"LR\"}" },
				{ "Replace 'foo' with 'BT'.",
						"Use valid 'BT' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=\"BT\"}" },
				{ "Replace 'foo' with 'RL'.",
						"Use valid 'RL' instead of invalid 'foo' graph rankdir.",
						"graph{rankdir=\"RL\"}" } };

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void node_colorscheme() {
		// test unquoted attribute value
		String text = "graph{colorscheme=foo}";
		String[][] expectedQuickfixes = new String[expectedDotColorSchemes.length][3];

		for (int i = 0; i < expectedDotColorSchemes.length; i++) {
			String validColorScheme = expectedDotColorSchemes[i];

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			expectedQuickfixes[i] = new String[] {
					"Replace 'foo' with '" + validColorScheme + "'.",
					"Use valid '" + validColorScheme
							+ "' instead of invalid 'foo' colorscheme.",
					"graph{colorscheme=" + validColorScheme + "}" };
		}
		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void node_shape() {
		String[] validNodeShapes = { "box", "polygon", "ellipse", "oval",
				"circle", "point", "egg", "triangle", "plaintext", "plain",
				"diamond", "trapezium", "parallelogram", "house", "pentagon",
				"hexagon", "septagon", "octagon", "doublecircle",
				"doubleoctagon", "tripleoctagon", "invtriangle", "invtrapezium",
				"invhouse", "Mdiamond", "Msquare", "Mcircle", "rect",
				"rectangle", "square", "star", "none", "underline", "cylinder",
				"note", "tab", "folder", "box3d", "component", "promoter",
				"cds", "terminator", "utr", "primersite", "restrictionsite",
				"fivepoverhang", "threepoverhang", "noverhang", "assembly",
				"signature", "insulator", "ribosite", "rnastab", "proteasesite",
				"proteinstab", "rpromoter", "rarrow", "larrow", "lpromoter",
				"record", "Mrecord" };

		// test unquoted attribute value
		String text = "graph{1[shape=foo]}";
		String[][] expectedQuickfixes = new String[validNodeShapes.length][3];

		for (int i = 0; i < validNodeShapes.length; i++) {
			String validNodeShape = validNodeShapes[i];

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			expectedQuickfixes[i] = new String[] {
					"Replace 'foo' with '" + validNodeShape + "'.",
					"Use valid '" + validNodeShape
							+ "' instead of invalid 'foo' node shape.",
					"graph{1[shape=" + validNodeShape + "]}" };
		}
		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{1[shape=\"foo\"]}";
		expectedQuickfixes = new String[validNodeShapes.length][3];

		for (int i = 0; i < validNodeShapes.length; i++) {
			String validNodeShape = validNodeShapes[i];

			// expectedQuickfixes[0]: expected quickfix label
			// expectedQuickfixes[1]: expected quickfix description
			// expectedQuickfixes[2]: expected text after quickfix application
			expectedQuickfixes[i] = new String[] {
					"Replace 'foo' with '" + validNodeShape + "'.",
					"Use valid '" + validNodeShape
							+ "' instead of invalid 'foo' node shape.",
					"graph{1[shape=\"" + validNodeShape + "\"]}" };
		}
		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void node_style() {
		// test unquoted attribute value
		String text = "graph{1[style=foo]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		String[][] expectedQuickfixes = {
				{ "Replace 'foo' with 'bold'.",
						"Use valid 'bold' instead of invalid 'foo' node style.",
						"graph{1[style=bold]}" },
				{ "Replace 'foo' with 'dashed'.",
						"Use valid 'dashed' instead of invalid 'foo' node style.",
						"graph{1[style=dashed]}" },
				{ "Replace 'foo' with 'diagonals'.",
						"Use valid 'diagonals' instead of invalid 'foo' node style.",
						"graph{1[style=diagonals]}" },
				{ "Replace 'foo' with 'dotted'.",
						"Use valid 'dotted' instead of invalid 'foo' node style.",
						"graph{1[style=dotted]}" },
				{ "Replace 'foo' with 'filled'.",
						"Use valid 'filled' instead of invalid 'foo' node style.",
						"graph{1[style=filled]}" },
				{ "Replace 'foo' with 'invis'.",
						"Use valid 'invis' instead of invalid 'foo' node style.",
						"graph{1[style=invis]}" },
				{ "Replace 'foo' with 'radial'.",
						"Use valid 'radial' instead of invalid 'foo' node style.",
						"graph{1[style=radial]}" },
				{ "Replace 'foo' with 'rounded'.",
						"Use valid 'rounded' instead of invalid 'foo' node style.",
						"graph{1[style=rounded]}" },
				{ "Replace 'foo' with 'solid'.",
						"Use valid 'solid' instead of invalid 'foo' node style.",
						"graph{1[style=solid]}" },
				{ "Replace 'foo' with 'striped'.",
						"Use valid 'striped' instead of invalid 'foo' node style.",
						"graph{1[style=striped]}" },
				{ "Replace 'foo' with 'wedged'.",
						"Use valid 'wedged' instead of invalid 'foo' node style.",
						"graph{1[style=wedged]}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test quoted attribute value
		text = "graph{1[style=\"foo\"]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {
				{ "Replace 'foo' with 'bold'.",
						"Use valid 'bold' instead of invalid 'foo' node style.",
						"graph{1[style=\"bold\"]}" },
				{ "Replace 'foo' with 'dashed'.",
						"Use valid 'dashed' instead of invalid 'foo' node style.",
						"graph{1[style=\"dashed\"]}" },
				{ "Replace 'foo' with 'diagonals'.",
						"Use valid 'diagonals' instead of invalid 'foo' node style.",
						"graph{1[style=\"diagonals\"]}" },
				{ "Replace 'foo' with 'dotted'.",
						"Use valid 'dotted' instead of invalid 'foo' node style.",
						"graph{1[style=\"dotted\"]}" },
				{ "Replace 'foo' with 'filled'.",
						"Use valid 'filled' instead of invalid 'foo' node style.",
						"graph{1[style=\"filled\"]}" },
				{ "Replace 'foo' with 'invis'.",
						"Use valid 'invis' instead of invalid 'foo' node style.",
						"graph{1[style=\"invis\"]}" },
				{ "Replace 'foo' with 'radial'.",
						"Use valid 'radial' instead of invalid 'foo' node style.",
						"graph{1[style=\"radial\"]}" },
				{ "Replace 'foo' with 'rounded'.",
						"Use valid 'rounded' instead of invalid 'foo' node style.",
						"graph{1[style=\"rounded\"]}" },
				{ "Replace 'foo' with 'solid'.",
						"Use valid 'solid' instead of invalid 'foo' node style.",
						"graph{1[style=\"solid\"]}" },
				{ "Replace 'foo' with 'striped'.",
						"Use valid 'striped' instead of invalid 'foo' node style.",
						"graph{1[style=\"striped\"]}" },
				{ "Replace 'foo' with 'wedged'.",
						"Use valid 'wedged' instead of invalid 'foo' node style.",
						"graph{1[style=\"wedged\"]}" } };

		assertQuickfixes(text, expectedQuickfixes);

		// test deprecated attribute value
		text = "graph{1[style=\"setlinewidth(4)\"]}";

		// expectedQuickfixes[0]: expected quickfix label
		// expectedQuickfixes[1]: expected quickfix description
		// expectedQuickfixes[2]: expected text after quickfix application
		expectedQuickfixes = new String[][] {};

		assertQuickfixes(text, expectedQuickfixes);
	}

	@Test
	public void subgraph_rank() {
		// TODO: implement
	}

	private void assertQuickfixes(String text, String[][] expected) {
		DotAst dotAst = null;
		try {
			dotAst = parseHelper.parse(text);
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}

		assertNotNull(dotAst);
		List<Issue> issues = validationTestHelper.validate(dotAst);

		assertEquals(1, issues.size());

		List<IssueResolution> issueResolutions = quickfixProvider
				.getResolutions(issues.get(0));

		assertEquals(expected.length, issueResolutions.size());

		for (int i = 0; i < issueResolutions.size(); i++) {
			IssueResolution actual = issueResolutions.get(i);

			String expectedLabel = expected[i][0];
			String expectedDescription = expected[i][1];
			String expectedResult = expected[i][2];

			assertEquals(expectedLabel, actual.getLabel());
			assertEquals(expectedDescription, actual.getDescription());
			assertIssueResolutionEffect(text, expectedResult, actual);
		}
	}

	private void assertIssueResolutionEffect(String originalText,
			String expectedResult, IssueResolution issueResolution) {
		IXtextDocument xtextDocument = null;

		try {
			xtextDocument = getDocument(originalText);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(xtextDocument);

		TestIssueModificationContext modificationContext = new TestIssueModificationContext();
		modificationContext.setDocument(xtextDocument);

		issueResolution = new IssueResolution(issueResolution.getLabel(),
				issueResolution.getDescription(), issueResolution.getImage(),
				modificationContext, issueResolution.getModification(),
				issueResolution.getRelevance());

		issueResolution.apply();

		String actualResult = issueResolution.getModificationContext()
				.getXtextDocument().get();

		assertEquals(expectedResult, actualResult);
	}

	/**
	 * The implementation of the following helper methods are taken from the
	 * org.eclipse.xtext.junit4.ui.ContentAssistProcessorTestBuilder java class.
	 */

	private IXtextDocument getDocument(final String currentModelToParse)
			throws Exception {
		XtextResource xtextResource = doGetResource(
				new StringInputStream(Strings.emptyIfNull(currentModelToParse)),
				URI.createURI("dummy:/example.mydsl")); //$NON-NLS-1$

		return getDocument(xtextResource, currentModelToParse);
	}

	private IXtextDocument getDocument(final XtextResource xtextResource,
			final String model) {
		XtextDocument document = get(XtextDocument.class);
		document.set(model);
		document.setInput(xtextResource);
		DocumentPartitioner partitioner = get(DocumentPartitioner.class);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		return document;
	}

	private <T> T get(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	private XtextResource doGetResource(InputStream in, URI uri)
			throws Exception {
		XtextResourceSet rs = get(XtextResourceSet.class);
		rs.setClasspathURIContext(getClass());
		XtextResource resource = (XtextResource) getResourceFactory()
				.createResource(uri);
		rs.getResources().add(resource);
		resource.load(in, null);
		if (resource instanceof LazyLinkingResource) {
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}
		return resource;
	}

	private IResourceFactory getResourceFactory() {
		return injector.getInstance(IResourceFactory.class);
	}

	private class TestIssueModificationContext
			extends IssueModificationContext {
		private IXtextDocument doc;

		@Override
		public IXtextDocument getXtextDocument() {
			return doc;
		}

		public void setDocument(IXtextDocument doc) {
			this.doc = doc;
		}
	}

	private String[] expectedDotColorSchemes = { "x11", "svg", "accent3",
			"accent4", "accent5", "accent6", "accent7", "accent8", "blues3",
			"blues4", "blues5", "blues6", "blues7", "blues8", "blues9",
			"brbg10", "brbg11", "brbg3", "brbg4", "brbg5", "brbg6", "brbg7",
			"brbg8", "brbg9", "bugn3", "bugn4", "bugn5", "bugn6", "bugn7",
			"bugn8", "bugn9", "bupu3", "bupu4", "bupu5", "bupu6", "bupu7",
			"bupu8", "bupu9", "dark23", "dark24", "dark25", "dark26", "dark27",
			"dark28", "gnbu3", "gnbu4", "gnbu5", "gnbu6", "gnbu7", "gnbu8",
			"gnbu9", "greens3", "greens4", "greens5", "greens6", "greens7",
			"greens8", "greens9", "greys3", "greys4", "greys5", "greys6",
			"greys7", "greys8", "greys9", "oranges3", "oranges4", "oranges5",
			"oranges6", "oranges7", "oranges8", "oranges9", "orrd3", "orrd4",
			"orrd5", "orrd6", "orrd7", "orrd8", "orrd9", "paired10", "paired11",
			"paired12", "paired3", "paired4", "paired5", "paired6", "paired7",
			"paired8", "paired9", "pastel13", "pastel14", "pastel15",
			"pastel16", "pastel17", "pastel18", "pastel19", "pastel23",
			"pastel24", "pastel25", "pastel26", "pastel27", "pastel28",
			"piyg10", "piyg11", "piyg3", "piyg4", "piyg5", "piyg6", "piyg7",
			"piyg8", "piyg9", "prgn10", "prgn11", "prgn3", "prgn4", "prgn5",
			"prgn6", "prgn7", "prgn8", "prgn9", "pubu3", "pubu4", "pubu5",
			"pubu6", "pubu7", "pubu8", "pubu9", "pubugn3", "pubugn4", "pubugn5",
			"pubugn6", "pubugn7", "pubugn8", "pubugn9", "puor10", "puor11",
			"puor3", "puor4", "puor5", "puor6", "puor7", "puor8", "puor9",
			"purd3", "purd4", "purd5", "purd6", "purd7", "purd8", "purd9",
			"purples3", "purples4", "purples5", "purples6", "purples7",
			"purples8", "purples9", "rdbu10", "rdbu11", "rdbu3", "rdbu4",
			"rdbu5", "rdbu6", "rdbu7", "rdbu8", "rdbu9", "rdgy10", "rdgy11",
			"rdgy3", "rdgy4", "rdgy5", "rdgy6", "rdgy7", "rdgy8", "rdgy9",
			"rdpu3", "rdpu4", "rdpu5", "rdpu6", "rdpu7", "rdpu8", "rdpu9",
			"rdylbu10", "rdylbu11", "rdylbu3", "rdylbu4", "rdylbu5", "rdylbu6",
			"rdylbu7", "rdylbu8", "rdylbu9", "rdylgn10", "rdylgn11", "rdylgn3",
			"rdylgn4", "rdylgn5", "rdylgn6", "rdylgn7", "rdylgn8", "rdylgn9",
			"reds3", "reds4", "reds5", "reds6", "reds7", "reds8", "reds9",
			"set13", "set14", "set15", "set16", "set17", "set18", "set19",
			"set23", "set24", "set25", "set26", "set27", "set28", "set310",
			"set311", "set312", "set33", "set34", "set35", "set36", "set37",
			"set38", "set39", "spectral10", "spectral11", "spectral3",
			"spectral4", "spectral5", "spectral6", "spectral7", "spectral8",
			"spectral9", "ylgn3", "ylgn4", "ylgn5", "ylgn6", "ylgn7", "ylgn8",
			"ylgn9", "ylgnbu3", "ylgnbu4", "ylgnbu5", "ylgnbu6", "ylgnbu7",
			"ylgnbu8", "ylgnbu9", "ylorbr3", "ylorbr4", "ylorbr5", "ylorbr6",
			"ylorbr7", "ylorbr8", "ylorbr9", "ylorrd3", "ylorrd4", "ylorrd5",
			"ylorrd6", "ylorrd7", "ylorrd8", "ylorrd9" };
}
