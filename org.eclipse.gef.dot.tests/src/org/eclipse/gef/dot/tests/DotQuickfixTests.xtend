/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.Injector
import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.gef.dot.internal.DotAttributes
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.ui.editor.model.IXtextDocument
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils.getDocument
import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotQuickfixTests {

	@Inject Injector injector
	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper
	@Inject extension IssueResolutionProvider

	@Test def edge_arrowhead() {
		val deprecatedArrowShapes = #["ediamond", "open", "halfopen", "empty", "invempty"]
		val validArrowShapes = #["odiamond", "vee", "lvee", "onormal", "oinv"]

		// test unquoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead=«deprecatedArrowShape»]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',
					'''digraph{1->2[arrowhead=«validArrowShape»]}'''
				)
			)
		}

		// test quoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead="«deprecatedArrowShape»"]}'''.testQuickfixesOn(DotAttributes.ARROWHEAD__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',
					'''digraph{1->2[arrowhead="«validArrowShape»"]}'''
				)
			)
		}
	}

	@Test def edge_arrowtail() {
		val deprecatedArrowShapes = #[ "ediamond", "open", "halfopen", "empty", "invempty" ]
		val validArrowShapes = #[ "odiamond", "vee", "lvee", "onormal", "oinv" ]

		// test unquoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail=«deprecatedArrowShape»]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',
					'''digraph{1->2[arrowtail=«validArrowShape»]}'''
				)
			)
		}

		// test quoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail="«deprecatedArrowShape»"]}'''.testQuickfixesOn(DotAttributes.ARROWTAIL__E,
				new Quickfix(
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',
					'''digraph{1->2[arrowtail="«validArrowShape»"]}'''
				)
			)
		}
	}

	@Test def edge_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<Quickfix> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validColorScheme»'.''',
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',
				'''graph{1--2[colorscheme=«validColorScheme»]}'''
			))
		}
		'''graph{1--2[colorscheme=foo]}'''.testQuickfixesOn(DotAttributes.COLORSCHEME__GCNE, expectedQuickfixes)
	}

	@Test def edge_dir() {
		// test unquoted attribute value
		'''graph{1--2[dir=foo]}'''.testQuickfixesOn(DotAttributes.DIR__E,
			new Quickfix("Replace 'foo' with 'forward'.",	"Use valid 'forward' instead of invalid 'foo' edge dir.",	"graph{1--2[dir=forward]}"),
			new Quickfix("Replace 'foo' with 'back'.",		"Use valid 'back' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=back]}"),
			new Quickfix("Replace 'foo' with 'both'.",		"Use valid 'both' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=both]}"),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=none]}")
		)

		// test quoted attribute value
		'''graph{1--2[dir="foo"]}'''.testQuickfixesOn(DotAttributes.DIR__E,
			new Quickfix("Replace 'foo' with 'forward'.",	"Use valid 'forward' instead of invalid 'foo' edge dir.",	'''graph{1--2[dir="forward"]}'''),
			new Quickfix("Replace 'foo' with 'back'.",		"Use valid 'back' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="back"]}'''),
			new Quickfix("Replace 'foo' with 'both'.",		"Use valid 'both' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="both"]}'''),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="none"]}''')
		)
	}

	@Test def edge_style() {
		// test unquoted attribute value
		'''graph{1--2[style=foo]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	"graph{1--2[style=bold]}"),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	"graph{1--2[style=dashed]}"),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	"graph{1--2[style=dotted]}"),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' edge style.",	"graph{1--2[style=invis]}"),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' edge style.",	"graph{1--2[style=solid]}"),
			new Quickfix("Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	"graph{1--2[style=tapered]}")
		)

		// test quoted attribute value
		'''graph{1--2[style="foo"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dashed"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dotted"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' edge style.",	'''graph{1--2[style="invis"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' edge style.",	'''graph{1--2[style="solid"]}'''),
			new Quickfix("Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	'''graph{1--2[style="tapered"]}''')
		)

		// test deprecated attribute value
		// TODO: provide quickfixes for deprecated edge style attribute values
		'''graph{1--2[style="setlinewidth(3)"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE)
	}

	@Test def graph_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<Quickfix> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validColorScheme»'.''',
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',
				'''graph{colorscheme=«validColorScheme»}'''
			))
		}
		'''graph{colorscheme=foo}'''.testQuickfixesOn(DotAttributes.COLORSCHEME__GCNE, expectedQuickfixes)
	}

	@Test def graph_clusterrank() {
		// test unquoted attribute value
		'''graph{clusterrank=foo}'''.testQuickfixesOn(DotAttributes.CLUSTERRANK__G,
			new Quickfix("Replace 'foo' with 'local'.",		"Use valid 'local' instead of invalid 'foo' graph clusterMode.",	"graph{clusterrank=local}"),
			new Quickfix("Replace 'foo' with 'global'.",	"Use valid 'global' instead of invalid 'foo' graph clusterMode.",	"graph{clusterrank=global}"),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' graph clusterMode.",		"graph{clusterrank=none}")
		)

		// test quoted attribute value
		'''graph{clusterrank="foo"}'''.testQuickfixesOn(DotAttributes.CLUSTERRANK__G,
			new Quickfix("Replace 'foo' with 'local'.",		"Use valid 'local' instead of invalid 'foo' graph clusterMode.",	'''graph{clusterrank="local"}'''),
			new Quickfix("Replace 'foo' with 'global'.",	"Use valid 'global' instead of invalid 'foo' graph clusterMode.",	'''graph{clusterrank="global"}'''),
			new Quickfix("Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' graph clusterMode.",		'''graph{clusterrank="none"}''')
		)
	}

	@Test def graph_layout() {
		// test unquoted attribute value
		'''graph{layout=foo}'''.testQuickfixesOn(DotAttributes.LAYOUT__G,
			new Quickfix("Replace 'foo' with 'circo'.",		"Use valid 'circo' instead of invalid 'foo' graph layout.",	"graph{layout=circo}"),
			new Quickfix("Replace 'foo' with 'dot'.",		"Use valid 'dot' instead of invalid 'foo' graph layout.",	"graph{layout=dot}"),
			new Quickfix("Replace 'foo' with 'fdp'.",		"Use valid 'fdp' instead of invalid 'foo' graph layout.",	"graph{layout=fdp}"),
			new Quickfix("Replace 'foo' with 'neato'.",		"Use valid 'neato' instead of invalid 'foo' graph layout.",	"graph{layout=neato}"),
			new Quickfix("Replace 'foo' with 'osage'.",		"Use valid 'osage' instead of invalid 'foo' graph layout.",	"graph{layout=osage}"),
			new Quickfix("Replace 'foo' with 'sfdp'.",		"Use valid 'sfdp' instead of invalid 'foo' graph layout.",	"graph{layout=sfdp}"),
			new Quickfix("Replace 'foo' with 'twopi'.",		"Use valid 'twopi' instead of invalid 'foo' graph layout.",	"graph{layout=twopi}")
		)

		// test quoted attribute value
		'''graph{layout="foo"}'''.testQuickfixesOn(DotAttributes.LAYOUT__G,
			new Quickfix("Replace 'foo' with 'circo'.",		"Use valid 'circo' instead of invalid 'foo' graph layout.",	'''graph{layout="circo"}'''),
			new Quickfix("Replace 'foo' with 'dot'.",		"Use valid 'dot' instead of invalid 'foo' graph layout.",	'''graph{layout="dot"}'''),
			new Quickfix("Replace 'foo' with 'fdp'.",		"Use valid 'fdp' instead of invalid 'foo' graph layout.",	'''graph{layout="fdp"}'''),
			new Quickfix("Replace 'foo' with 'neato'.",		"Use valid 'neato' instead of invalid 'foo' graph layout.",	'''graph{layout="neato"}'''),
			new Quickfix("Replace 'foo' with 'osage'.",		"Use valid 'osage' instead of invalid 'foo' graph layout.",	'''graph{layout="osage"}'''),
			new Quickfix("Replace 'foo' with 'sfdp'.",		"Use valid 'sfdp' instead of invalid 'foo' graph layout.",	'''graph{layout="sfdp"}'''),
			new Quickfix("Replace 'foo' with 'twopi'.",		"Use valid 'twopi' instead of invalid 'foo' graph layout.",	'''graph{layout="twopi"}''')
		)
	}

	@Test def graph_outputorder() {
		// test unquoted attribute value
		'''graph{outputorder=foo}'''.testQuickfixesOn(DotAttributes.OUTPUTORDER__G,
			new Quickfix("Replace 'foo' with 'breadthfirst'.",	"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=breadthfirst}"),
			new Quickfix("Replace 'foo' with 'nodesfirst'.", 	"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=nodesfirst}"),
			new Quickfix("Replace 'foo' with 'edgesfirst'.", 	"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=edgesfirst}")
		)

		// test quoted attribute value
		'''graph{outputorder="foo"}'''.testQuickfixesOn(DotAttributes.OUTPUTORDER__G,
			new Quickfix("Replace 'foo' with 'breadthfirst'.",	"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="breadthfirst"}'''),
			new Quickfix("Replace 'foo' with 'nodesfirst'.",  	"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="nodesfirst"}'''),
			new Quickfix("Replace 'foo' with 'edgesfirst'.", 	"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="edgesfirst"}''')
		)
	}

	@Test def graph_pagedir() {
		// test unquoted attribute value
		'''graph{pagedir=foo}'''.testQuickfixesOn(DotAttributes.PAGEDIR__G,
			new Quickfix("Replace 'foo' with 'BL'.", 	"Use valid 'BL' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=BL}"),
			new Quickfix("Replace 'foo' with 'BR'.", 	"Use valid 'BR' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=BR}"),
			new Quickfix("Replace 'foo' with 'TL'.", 	"Use valid 'TL' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=TL}"),
			new Quickfix("Replace 'foo' with 'TR'.", 	"Use valid 'TR' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=TR}"),
			new Quickfix("Replace 'foo' with 'RB'.", 	"Use valid 'RB' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=RB}"),
			new Quickfix("Replace 'foo' with 'RT'.", 	"Use valid 'RT' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=RT}"),
			new Quickfix("Replace 'foo' with 'LB'.", 	"Use valid 'LB' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=LB}"),
			new Quickfix("Replace 'foo' with 'LT'.", 	"Use valid 'LT' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=LT}")
		)

		// test quoted attribute value
		'''graph{pagedir="foo"}'''.testQuickfixesOn(DotAttributes.PAGEDIR__G,
			new Quickfix("Replace 'foo' with 'BL'.", 	"Use valid 'BL' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="BL"}'''),
			new Quickfix("Replace 'foo' with 'BR'.", 	"Use valid 'BR' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="BR"}'''),
			new Quickfix("Replace 'foo' with 'TL'.", 	"Use valid 'TL' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="TL"}'''),
			new Quickfix("Replace 'foo' with 'TR'.", 	"Use valid 'TR' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="TR"}'''),
			new Quickfix("Replace 'foo' with 'RB'.", 	"Use valid 'RB' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="RB"}'''),
			new Quickfix("Replace 'foo' with 'RT'.", 	"Use valid 'RT' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="RT"}'''),
			new Quickfix("Replace 'foo' with 'LB'.", 	"Use valid 'LB' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="LB"}'''),
			new Quickfix("Replace 'foo' with 'LT'.", 	"Use valid 'LT' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="LT"}''')
		)
	}

	@Test def graph_rankdir() {
		// test unquoted attribute value
		'''graph{rankdir=foo}'''.testQuickfixesOn(DotAttributes.RANKDIR__G,
			new Quickfix("Replace 'foo' with 'TB'.", "Use valid 'TB' instead of invalid 'foo' graph rankdir.", "graph{rankdir=TB}"),
			new Quickfix("Replace 'foo' with 'LR'.", "Use valid 'LR' instead of invalid 'foo' graph rankdir.", "graph{rankdir=LR}"),
			new Quickfix("Replace 'foo' with 'BT'.", "Use valid 'BT' instead of invalid 'foo' graph rankdir.", "graph{rankdir=BT}"),
			new Quickfix("Replace 'foo' with 'RL'.", "Use valid 'RL' instead of invalid 'foo' graph rankdir.", "graph{rankdir=RL}")
		)

		// test quoted attribute value
		'''graph{rankdir="foo"}'''.testQuickfixesOn(DotAttributes.RANKDIR__G,
			new Quickfix("Replace 'foo' with 'TB'.", "Use valid 'TB' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="TB"}'''),
			new Quickfix("Replace 'foo' with 'LR'.", "Use valid 'LR' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="LR"}'''),
			new Quickfix("Replace 'foo' with 'BT'.", "Use valid 'BT' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="BT"}'''),
			new Quickfix("Replace 'foo' with 'RL'.", "Use valid 'RL' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="RL"}''')
		)
	}

	@Test def node_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<Quickfix> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validColorScheme»'.''',
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',
				'''graph{node[colorscheme=«validColorScheme»]}'''
			))
		}

		'''graph{node[colorscheme=foo]}'''.testQuickfixesOn(DotAttributes.COLORSCHEME__GCNE, expectedQuickfixes)
	}

	@Test def node_shape() {
		val validNodeShapes = #[ "box", "polygon", "ellipse", "oval", "circle", "point", "egg", "triangle",
				"plaintext", "plain", "diamond", "trapezium", "parallelogram", "house", "pentagon", "hexagon", "septagon",
				"octagon", "doublecircle", "doubleoctagon", "tripleoctagon", "invtriangle", "invtrapezium", "invhouse", "Mdiamond",
				"Msquare", "Mcircle", "rect", "rectangle", "square", "star", "none", "underline", "cylinder", "note", "tab", "folder",
				"box3d", "component", "promoter", "cds", "terminator", "utr", "primersite", "restrictionsite", "fivepoverhang",
				"threepoverhang", "noverhang", "assembly", "signature", "insulator", "ribosite", "rnastab", "proteasesite", "proteinstab",
				"rpromoter", "rarrow", "larrow", "lpromoter", "record", "Mrecord" ]

		// test unquoted attribute value
		val List<Quickfix> expectedQuickfixes = newArrayList
		for (validNodeShape : validNodeShapes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validNodeShape»'.''',
				'''Use valid '«validNodeShape»' instead of invalid 'foo' node shape.''',
				'''graph{1[shape=«validNodeShape»]}'''
			))
		}

		'''graph{1[shape=foo]}'''.testQuickfixesOn(DotAttributes.SHAPE__N,expectedQuickfixes)

		// test quoted attribute value
		expectedQuickfixes.clear
		for (validNodeShape : validNodeShapes) {
			expectedQuickfixes.add(new Quickfix(
				'''Replace 'foo' with '«validNodeShape»'.''',
				'''Use valid '«validNodeShape»' instead of invalid 'foo' node shape.''',
				'''graph{1[shape="«validNodeShape»"]}'''
			))
		}

		'''graph{1[shape="foo"]}'''.testQuickfixesOn(DotAttributes.SHAPE__N, expectedQuickfixes)
	}

	@Test def node_style() {
		// test unquoted attribute value
		'''graph{1[style=foo]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' node style.", 		"graph{1[style=bold]}"),
			new Quickfix("Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' node style.", 		"graph{1[style=dashed]}"),
			new Quickfix("Replace 'foo' with 'diagonals'.",	"Use valid 'diagonals' instead of invalid 'foo' node style.",	"graph{1[style=diagonals]}"),
			new Quickfix("Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' node style.", 		"graph{1[style=dotted]}"),
			new Quickfix("Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' node style.",		"graph{1[style=filled]}"),
			new Quickfix("Replace 'foo' with 'invis'.",		"Use valid 'invis' instead of invalid 'foo' node style.",		"graph{1[style=invis]}"),
			new Quickfix("Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' node style.",		"graph{1[style=radial]}"),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		"graph{1[style=rounded]}"),
			new Quickfix("Replace 'foo' with 'solid'.",		"Use valid 'solid' instead of invalid 'foo' node style.",		"graph{1[style=solid]}"),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		"graph{1[style=striped]}"),
			new Quickfix("Replace 'foo' with 'wedged'.",	"Use valid 'wedged' instead of invalid 'foo' node style.",		"graph{1[style=wedged]}")
		)

		// test quoted attribute value
		'''graph{1[style="foo"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE,
			new Quickfix("Replace 'foo' with 'bold'.", 		"Use valid 'bold' instead of invalid 'foo' node style.", 		'''graph{1[style="bold"]}'''),
			new Quickfix("Replace 'foo' with 'dashed'.", 	"Use valid 'dashed' instead of invalid 'foo' node style.", 		'''graph{1[style="dashed"]}'''),
			new Quickfix("Replace 'foo' with 'diagonals'.",	"Use valid 'diagonals' instead of invalid 'foo' node style.",	'''graph{1[style="diagonals"]}'''),
			new Quickfix("Replace 'foo' with 'dotted'.", 	"Use valid 'dotted' instead of invalid 'foo' node style.",		'''graph{1[style="dotted"]}'''),
			new Quickfix("Replace 'foo' with 'filled'.", 	"Use valid 'filled' instead of invalid 'foo' node style.",		'''graph{1[style="filled"]}'''),
			new Quickfix("Replace 'foo' with 'invis'.", 	"Use valid 'invis' instead of invalid 'foo' node style.",		'''graph{1[style="invis"]}'''),
			new Quickfix("Replace 'foo' with 'radial'.", 	"Use valid 'radial' instead of invalid 'foo' node style.",		'''graph{1[style="radial"]}'''),
			new Quickfix("Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		'''graph{1[style="rounded"]}'''),
			new Quickfix("Replace 'foo' with 'solid'.", 	"Use valid 'solid' instead of invalid 'foo' node style.",		'''graph{1[style="solid"]}'''),
			new Quickfix("Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		'''graph{1[style="striped"]}'''),
			new Quickfix("Replace 'foo' with 'wedged'.", 	"Use valid 'wedged' instead of invalid 'foo' node style.",		'''graph{1[style="wedged"]}''')
		)

		// test deprecated attribute value
		// TODO: provide quickfixes for deprecated node style attribute values
		'''graph{1[style="setlinewidth(4)"]}'''.testQuickfixesOn(DotAttributes.STYLE__GCNE)
	}

	@Test def subgraph_rank() {
		// test unquoted attribute value
		'''graph{subgraph{rank=foo}}'''.testQuickfixesOn(DotAttributes.RANK__S,
			new Quickfix("Replace 'foo' with 'same'.",		"Use valid 'same' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=same}}"),
			new Quickfix("Replace 'foo' with 'min'.", 		"Use valid 'min' instead of invalid 'foo' subgraph rankType.", 		"graph{subgraph{rank=min}}"),
			new Quickfix("Replace 'foo' with 'source'.",	"Use valid 'source' instead of invalid 'foo' subgraph rankType.",	"graph{subgraph{rank=source}}"),
			new Quickfix("Replace 'foo' with 'max'.",		"Use valid 'max' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=max}}"),
			new Quickfix("Replace 'foo' with 'sink'.",		"Use valid 'sink' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=sink}}")
		)

		// test quoted attribute value
		'''graph{subgraph{rank="foo"}}'''.testQuickfixesOn(DotAttributes.RANK__S,
			new Quickfix("Replace 'foo' with 'same'.",		"Use valid 'same' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="same"}}'''),
			new Quickfix("Replace 'foo' with 'min'.",		"Use valid 'min' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="min"}}'''),
			new Quickfix("Replace 'foo' with 'source'.",	"Use valid 'source' instead of invalid 'foo' subgraph rankType.",	'''graph{subgraph{rank="source"}}'''),
			new Quickfix("Replace 'foo' with 'max'.",		"Use valid 'max' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="max"}}'''),
			new Quickfix("Replace 'foo' with 'sink'.",		"Use valid 'sink' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="sink"}}''')
		)
	}

	/**
	  * Test that the expected quickfixes are offered on a given validation issue in a given DSL text.
	  * 
	  * @param it The initial DSL text.
	  * @param expected The quickfixes that are expected to be offered on the given <code>issueCode</code>.
	  * Each expected quickfix should be described by the following triple:
	  * <ol>
	  * 	<li>the quickfix label</li>
	  * 	<li>the quickfix description</li>
	  * 	<li>the DSL text after the quickfix application</li>
	  * </ol>
	  */
	private def testQuickfixesOn(CharSequence it, String issueCode, Quickfix... expected) {
		val issue = getValidationIssue(issueCode)
		val actualIssueResolutions = issue.getResolutions
		assertEquals("The number of quickfixes does not match!", expected.size, actualIssueResolutions.size)
		for (i : 0..< actualIssueResolutions.size) {
			val actualIssueResolution = actualIssueResolutions.get(i)
			val expectedIssueResolution = expected.get(i)
			expectedIssueResolution.label.assertEquals(actualIssueResolution.label)
			expectedIssueResolution.description.assertEquals(actualIssueResolution.getDescription)
			expectedIssueResolution.result.assertIssueResolutionResult(actualIssueResolution, toString)
		}
	}

	private def getValidationIssue(CharSequence it, String issueCode) {
		val issues = parse.validate
		val issueCandidates = issues.filter[code == issueCode]
		assertEquals("There should be one '" + issueCode + "' validation issue!", 1, issueCandidates.size)
		issueCandidates.head
	}

	private def assertIssueResolutionResult(String expectedResult, IssueResolution actualIssueResolution, String originalText) {
		/*
		 * manually create an IModificationContext with a XtextDocument and call the
		 * apply method of the actualIssueResolution with that IModificationContext
		 */
		val document = injector.getDocument(originalText)
		val modificationContext = new TestModificationContext
		modificationContext.document = document
		new IssueResolution(actualIssueResolution.label, actualIssueResolution.description,
			actualIssueResolution.image, modificationContext, actualIssueResolution.modification,
			actualIssueResolution.relevance).apply
		val actualResult = document.get
		expectedResult.assertEquals(actualResult)
	}

	private static class TestModificationContext implements IModificationContext {
		IXtextDocument doc

		override getXtextDocument() {
			doc
		}

		override getXtextDocument(URI uri) {
			doc
		}

		def setDocument(IXtextDocument doc) {
			this.doc = doc
		}

	}

	@Data
	private static class Quickfix {
		String label
		String description
		String result
	}
}
