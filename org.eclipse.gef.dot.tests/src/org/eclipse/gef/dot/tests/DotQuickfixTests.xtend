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
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.language.quickfix.DotQuickfixProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.ui.editor.model.IXtextDocument
import org.eclipse.xtext.ui.editor.model.edit.IssueModificationContext
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils.getDocument
import static extension org.junit.Assert.assertEquals
import static extension org.junit.Assert.assertNotNull

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotQuickfixTests {
	
	@Inject Injector injector
	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper
	@Inject extension DotQuickfixProvider

	@Test def edge_arrowhead() {
		val deprecatedArrowShapes = #["ediamond", "open", "halfopen", "empty", "invempty"]
		val validArrowShapes = #["odiamond", "vee", "lvee", "onormal", "oinv"]

		// test unquoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead=«deprecatedArrowShape»]}'''.assertQuickfixes(#[
				#[
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',									// quickfix label
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',	// quickfix description
					'''digraph{1->2[arrowhead=«validArrowShape»]}'''													// text after quickfix application
				 ]
			 ])
		}

		// test quoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowhead="«deprecatedArrowShape»"]}'''.assertQuickfixes(#[
				#[
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',									// quickfix label
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowhead.''',	// quickfix description
					'''digraph{1->2[arrowhead="«validArrowShape»"]}'''													// text after quickfix application
				 ]
			])
		}
	}

	@Test def edge_arrowtail() {
		val deprecatedArrowShapes = #[ "ediamond", "open", "halfopen", "empty", "invempty" ]
		val validArrowShapes = #[ "odiamond", "vee", "lvee", "onormal", "oinv" ]

		// test unquoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail=«deprecatedArrowShape»]}'''.assertQuickfixes(#[
				#[
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',									// quickfix label
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',	// quickfix description
					'''digraph{1->2[arrowtail=«validArrowShape»]}'''													// text after quickfix application
				]
			])
		}

		// test quoted attribute value
		for (i : 0..< deprecatedArrowShapes.length) {
			val deprecatedArrowShape = deprecatedArrowShapes.get(i)
			val validArrowShape = validArrowShapes.get(i)

			'''digraph{1->2[arrowtail="«deprecatedArrowShape»"]}'''.assertQuickfixes(#[
				#[
					'''Replace '«deprecatedArrowShape»' with '«validArrowShape»'.''',									// quickfix label
					'''Use valid '«validArrowShape»' instead of invalid '«deprecatedArrowShape»' edge arrowtail.''',	// quickfix description
					'''digraph{1->2[arrowtail="«validArrowShape»"]}'''													// text after quickfix application
				]
			])
		}
	}

	@Test
	def edge_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<List<String>> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(#[
				'''Replace 'foo' with '«validColorScheme»'.''',								// quickfix label
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',	// quickfix description
				'''graph{1--2[colorscheme=«validColorScheme»]}'''							// text after quickfix application
			])
		}
		'''graph{1--2[colorscheme=foo]}'''.assertQuickfixes(expectedQuickfixes)
	}

	@Test
	def edge_dir() {
		// test unquoted attribute value
		'''graph{1--2[dir=foo]}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'forward'.",	"Use valid 'forward' instead of invalid 'foo' edge dir.",	"graph{1--2[dir=forward]}"],
			#["Replace 'foo' with 'back'.",		"Use valid 'back' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=back]}"],
			#["Replace 'foo' with 'both'.",		"Use valid 'both' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=both]}"],
			#["Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' edge dir.",		"graph{1--2[dir=none]}"]
		])

		// test quoted attribute value
		'''graph{1--2[dir="foo"]}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'forward'.",	"Use valid 'forward' instead of invalid 'foo' edge dir.",	'''graph{1--2[dir="forward"]}'''],
			#["Replace 'foo' with 'back'.",		"Use valid 'back' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="back"]}'''],
			#["Replace 'foo' with 'both'.",		"Use valid 'both' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="both"]}'''],
			#["Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' edge dir.",		'''graph{1--2[dir="none"]}''']
		])
	}

	@Test
	def edge_style() {
		// test unquoted attribute value
		'''graph{1--2[style=foo]}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	"graph{1--2[style=bold]}"],
			#["Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	"graph{1--2[style=dashed]}"],
			#["Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	"graph{1--2[style=dotted]}"],
			#["Replace 'foo' with 'invis'.",	"Use valid 'invis' instead of invalid 'foo' edge style.",	"graph{1--2[style=invis]}"],
			#["Replace 'foo' with 'solid'.",	"Use valid 'solid' instead of invalid 'foo' edge style.",	"graph{1--2[style=solid]}"],
			#["Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	"graph{1--2[style=tapered]}"]
		])

		// test quoted attribute value
		'''graph{1--2[style="foo"]}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 									text after quickfix application			
			#["Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' edge style.",	'''graph{1--2[style="bold"]}'''],
			#["Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dashed"]}'''],
			#["Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' edge style.",	'''graph{1--2[style="dotted"]}'''],
			#["Replace 'foo' with 'invis'.",	"Use valid 'invis' instead of invalid 'foo' edge style.",	'''graph{1--2[style="invis"]}'''],
			#["Replace 'foo' with 'solid'.",	"Use valid 'solid' instead of invalid 'foo' edge style.",	'''graph{1--2[style="solid"]}'''],
			#["Replace 'foo' with 'tapered'.",	"Use valid 'tapered' instead of invalid 'foo' edge style.",	'''graph{1--2[style="tapered"]}''']
		])

		// test deprecated attribute value
		// TODO: provide quickfixes for deprecated edge style attribute values
		'''graph{1--2[style="setlinewidth(3)"]}'''.assertQuickfixes(#[])
	}

	@Test
	def graph_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<List<String>> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(#[
				'''Replace 'foo' with '«validColorScheme»'.''',								// quickfix label
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''',	// quickfix description
				'''graph{colorscheme=«validColorScheme»}'''])								// text after quickfix application
		}
		'''graph{colorscheme=foo}'''.assertQuickfixes(expectedQuickfixes)
	}

	@Test
	def graph_clusterrank() {
		// test unquoted attribute value
		'''graph{clusterrank=foo}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 											text after quickfix application
			#["Replace 'foo' with 'local'.",	"Use valid 'local' instead of invalid 'foo' graph clusterMode.",	"graph{clusterrank=local}"],
			#["Replace 'foo' with 'global'.",	"Use valid 'global' instead of invalid 'foo' graph clusterMode.",	"graph{clusterrank=global}"],
			#["Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' graph clusterMode.",		"graph{clusterrank=none}"]
		])

		// test quoted attribute value
		'''graph{clusterrank="foo"}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 											text after quickfix application
			#["Replace 'foo' with 'local'.",	"Use valid 'local' instead of invalid 'foo' graph clusterMode.",	'''graph{clusterrank="local"}'''],
			#["Replace 'foo' with 'global'.",	"Use valid 'global' instead of invalid 'foo' graph clusterMode.",	'''graph{clusterrank="global"}'''],
			#["Replace 'foo' with 'none'.",		"Use valid 'none' instead of invalid 'foo' graph clusterMode.",		'''graph{clusterrank="none"}''']
		])
	}

	@Test
	def graph_layout() {
		// test unquoted attribute value
		'''graph{layout=foo}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'circo'.",	"Use valid 'circo' instead of invalid 'foo' graph layout.",	"graph{layout=circo}"],
			#["Replace 'foo' with 'dot'.",		"Use valid 'dot' instead of invalid 'foo' graph layout.",	"graph{layout=dot}"],
			#["Replace 'foo' with 'fdp'.",		"Use valid 'fdp' instead of invalid 'foo' graph layout.",	"graph{layout=fdp}" ],
			#["Replace 'foo' with 'neato'.",	"Use valid 'neato' instead of invalid 'foo' graph layout.",	"graph{layout=neato}" ],
			#["Replace 'foo' with 'osage'.",	"Use valid 'osage' instead of invalid 'foo' graph layout.",	"graph{layout=osage}" ],
			#["Replace 'foo' with 'sfdp'.",		"Use valid 'sfdp' instead of invalid 'foo' graph layout.",	"graph{layout=sfdp}" ],
			#["Replace 'foo' with 'twopi'.",	"Use valid 'twopi' instead of invalid 'foo' graph layout.",	"graph{layout=twopi}"]
		])

		// test quoted attribute value
		'''graph{layout="foo"}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'circo'.",	"Use valid 'circo' instead of invalid 'foo' graph layout.",	'''graph{layout="circo"}'''],
			#["Replace 'foo' with 'dot'.",		"Use valid 'dot' instead of invalid 'foo' graph layout.",	'''graph{layout="dot"}'''],
			#["Replace 'foo' with 'fdp'.",		"Use valid 'fdp' instead of invalid 'foo' graph layout.",	'''graph{layout="fdp"}'''],
			#["Replace 'foo' with 'neato'.",	"Use valid 'neato' instead of invalid 'foo' graph layout.",	'''graph{layout="neato"}'''],
			#["Replace 'foo' with 'osage'.",	"Use valid 'osage' instead of invalid 'foo' graph layout.",	'''graph{layout="osage"}'''],
			#["Replace 'foo' with 'sfdp'.",		"Use valid 'sfdp' instead of invalid 'foo' graph layout.",	'''graph{layout="sfdp"}'''],
			#["Replace 'foo' with 'twopi'.",	"Use valid 'twopi' instead of invalid 'foo' graph layout.",	'''graph{layout="twopi"}''']
		])
	}

	@Test
	def graph_outputorder() {
		// test unquoted attribute value
		'''graph{outputorder=foo}'''.assertQuickfixes(#[
			// quickfix label						quickfix description			   										text after quickfix application
			#["Replace 'foo' with 'breadthfirst'.",	"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=breadthfirst}" ],
			#["Replace 'foo' with 'nodesfirst'.", 	"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=nodesfirst}" ],
			#["Replace 'foo' with 'edgesfirst'.", 	"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.", 	"graph{outputorder=edgesfirst}" ]
		])

		// test quoted attribute value
		'''graph{outputorder="foo"}'''.assertQuickfixes(#[
			// quickfix label						quickfix description				 									text after quickfix application
			#["Replace 'foo' with 'breadthfirst'.",	"Use valid 'breadthfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="breadthfirst"}'''],
			#["Replace 'foo' with 'nodesfirst'.",  	"Use valid 'nodesfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="nodesfirst"}'''],
			#["Replace 'foo' with 'edgesfirst'.", 	"Use valid 'edgesfirst' instead of invalid 'foo' graph outputMode.", 	'''graph{outputorder="edgesfirst"}''']
		])
	}

	@Test
	def graph_pagedir() {
		// test unquoted attribute value
		'''graph{pagedir=foo}'''.assertQuickfixes(#[
			// quickfix label				quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'BL'.", 	"Use valid 'BL' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=BL}"],
			#["Replace 'foo' with 'BR'.", 	"Use valid 'BR' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=BR}"],
			#["Replace 'foo' with 'TL'.", 	"Use valid 'TL' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=TL}"],
			#["Replace 'foo' with 'TR'.", 	"Use valid 'TR' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=TR}"],
			#["Replace 'foo' with 'RB'.", 	"Use valid 'RB' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=RB}"],
			#["Replace 'foo' with 'RT'.", 	"Use valid 'RT' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=RT}"],
			#["Replace 'foo' with 'LB'.", 	"Use valid 'LB' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=LB}"],
			#["Replace 'foo' with 'LT'.", 	"Use valid 'LT' instead of invalid 'foo' graph pagedir.",	"graph{pagedir=LT}"]
		])

		// test quoted attribute value
		'''graph{pagedir="foo"}'''.assertQuickfixes(#[
			// quickfix label				quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'BL'.", "Use valid 'BL' instead of invalid 'foo' graph pagedir.",		'''graph{pagedir="BL"}'''],
			#["Replace 'foo' with 'BR'.", 	"Use valid 'BR' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="BR"}'''],
			#["Replace 'foo' with 'TL'.", 	"Use valid 'TL' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="TL"}'''],
			#["Replace 'foo' with 'TR'.", 	"Use valid 'TR' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="TR"}'''],
			#["Replace 'foo' with 'RB'.", 	"Use valid 'RB' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="RB"}'''],
			#["Replace 'foo' with 'RT'.", 	"Use valid 'RT' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="RT"}'''],
			#["Replace 'foo' with 'LB'.", 	"Use valid 'LB' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="LB"}'''],
			#["Replace 'foo' with 'LT'.", 	"Use valid 'LT' instead of invalid 'foo' graph pagedir.",	'''graph{pagedir="LT"}''']
		])
	}

	@Test
	def graph_rankdir() {
		// test unquoted attribute value
		'''graph{rankdir=foo}'''.assertQuickfixes(#[
			// quickfix label			  quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'TB'.", "Use valid 'TB' instead of invalid 'foo' graph rankdir.", "graph{rankdir=TB}"],
			#["Replace 'foo' with 'LR'.", "Use valid 'LR' instead of invalid 'foo' graph rankdir.", "graph{rankdir=LR}"],
			#["Replace 'foo' with 'BT'.", "Use valid 'BT' instead of invalid 'foo' graph rankdir.", "graph{rankdir=BT}"],
			#["Replace 'foo' with 'RL'.", "Use valid 'RL' instead of invalid 'foo' graph rankdir.", "graph{rankdir=RL}"]
		])

		// test quoted attribute value
		'''graph{rankdir="foo"}'''.assertQuickfixes(#[
			// quickfix label			  quickfix description	 									text after quickfix application
			#["Replace 'foo' with 'TB'.", "Use valid 'TB' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="TB"}'''],
			#["Replace 'foo' with 'LR'.", "Use valid 'LR' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="LR"}'''],
			#["Replace 'foo' with 'BT'.", "Use valid 'BT' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="BT"}'''],
			#["Replace 'foo' with 'RL'.", "Use valid 'RL' instead of invalid 'foo' graph rankdir.", '''graph{rankdir="RL"}''']
		])
	}

	@Test
	def node_colorscheme() {
		// test unquoted attribute value
		val validColorSchemes = DotTestUtils.expectedDotColorSchemes
		val List<List<String>> expectedQuickfixes = newArrayList

		for (validColorScheme : validColorSchemes) {
			expectedQuickfixes.add(#[
				'''Replace 'foo' with '«validColorScheme»'.''', 							// quickfix label
				'''Use valid '«validColorScheme»' instead of invalid 'foo' colorscheme.''', // quickfix description
				'''graph{colorscheme=«validColorScheme»}''' 								// text after quickfix application
			])
		}
		
		'''graph{colorscheme=foo}'''.assertQuickfixes(expectedQuickfixes)
	}

	@Test
	def node_shape() {
		val validNodeShapes = #[ "box", "polygon", "ellipse", "oval", "circle", "point", "egg", "triangle",
				"plaintext", "plain", "diamond", "trapezium", "parallelogram", "house", "pentagon", "hexagon", "septagon",
				"octagon", "doublecircle", "doubleoctagon", "tripleoctagon", "invtriangle", "invtrapezium", "invhouse", "Mdiamond",
				"Msquare", "Mcircle", "rect", "rectangle", "square", "star", "none", "underline", "cylinder", "note", "tab", "folder",
				"box3d", "component", "promoter", "cds", "terminator", "utr", "primersite", "restrictionsite", "fivepoverhang",
				"threepoverhang", "noverhang", "assembly", "signature", "insulator", "ribosite", "rnastab", "proteasesite", "proteinstab",
				"rpromoter", "rarrow", "larrow", "lpromoter", "record", "Mrecord" ]

		// test unquoted attribute value
		var List<List<String>> expectedQuickfixes = newArrayList
		for (validNodeShape : validNodeShapes) {
			expectedQuickfixes.add(#[
				'''Replace 'foo' with '«validNodeShape»'.''', 								// quickfix label
				'''Use valid '«validNodeShape»' instead of invalid 'foo' node shape.''',	// quickfix description
				'''graph{1[shape=«validNodeShape»]}'''										// text after quickfix application
			])
		}
		
		'''graph{1[shape=foo]}'''.assertQuickfixes(expectedQuickfixes)

		// test quoted attribute value
		expectedQuickfixes = newArrayList
		for (validNodeShape : validNodeShapes) {
			expectedQuickfixes.add(#[
				'''Replace 'foo' with '«validNodeShape»'.''',								// quickfix label
				'''Use valid '«validNodeShape»' instead of invalid 'foo' node shape.''',	// quickfix description
				'''graph{1[shape="«validNodeShape»"]}'''									// text after quickfix application
			])
		}
		
		'''graph{1[shape="foo"]}'''.assertQuickfixes(expectedQuickfixes)
	}

	@Test
	def node_style() {
		// test unquoted attribute value
		'''graph{1[style=foo]}'''.assertQuickfixes(#[
			// quickfix label			 		 quickfix description	 										text after quickfix application
			#["Replace 'foo' with 'bold'.",		"Use valid 'bold' instead of invalid 'foo' node style.", 		"graph{1[style=bold]}"],
			#["Replace 'foo' with 'dashed'.",	"Use valid 'dashed' instead of invalid 'foo' node style.", 		"graph{1[style=dashed]}"],
			#["Replace 'foo' with 'diagonals'.","Use valid 'diagonals' instead of invalid 'foo' node style.",	"graph{1[style=diagonals]}"],
			#["Replace 'foo' with 'dotted'.",	"Use valid 'dotted' instead of invalid 'foo' node style.", 		"graph{1[style=dotted]}"],
			#["Replace 'foo' with 'filled'.",	"Use valid 'filled' instead of invalid 'foo' node style.",		"graph{1[style=filled]}"],
			#["Replace 'foo' with 'invis'.",	"Use valid 'invis' instead of invalid 'foo' node style.",		"graph{1[style=invis]}"],
			#["Replace 'foo' with 'radial'.",	"Use valid 'radial' instead of invalid 'foo' node style.",		"graph{1[style=radial]}"],
			#["Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		"graph{1[style=rounded]}"],
			#["Replace 'foo' with 'solid'.",	"Use valid 'solid' instead of invalid 'foo' node style.",		"graph{1[style=solid]}"],
			#["Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		"graph{1[style=striped]}"],
			#["Replace 'foo' with 'wedged'.",	"Use valid 'wedged' instead of invalid 'foo' node style.",		"graph{1[style=wedged]}"]
		])

		// test quoted attribute value
		'''graph{1[style="foo"]}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 										text after quickfix application
			#["Replace 'foo' with 'bold'.", 	"Use valid 'bold' instead of invalid 'foo' node style.", 		'''graph{1[style="bold"]}'''],
			#["Replace 'foo' with 'dashed'.", 	"Use valid 'dashed' instead of invalid 'foo' node style.", 		'''graph{1[style="dashed"]}'''],
			#["Replace 'foo' with 'diagonals'.","Use valid 'diagonals' instead of invalid 'foo' node style.",	'''graph{1[style="diagonals"]}'''],
			#["Replace 'foo' with 'dotted'.", 	"Use valid 'dotted' instead of invalid 'foo' node style.",		'''graph{1[style="dotted"]}'''],
			#["Replace 'foo' with 'filled'.", 	"Use valid 'filled' instead of invalid 'foo' node style.",		'''graph{1[style="filled"]}'''],
			#["Replace 'foo' with 'invis'.", 	"Use valid 'invis' instead of invalid 'foo' node style.",		'''graph{1[style="invis"]}'''],
			#["Replace 'foo' with 'radial'.", 	"Use valid 'radial' instead of invalid 'foo' node style.",		'''graph{1[style="radial"]}'''],
			#["Replace 'foo' with 'rounded'.",	"Use valid 'rounded' instead of invalid 'foo' node style.",		'''graph{1[style="rounded"]}'''],
			#["Replace 'foo' with 'solid'.", 	"Use valid 'solid' instead of invalid 'foo' node style.",		'''graph{1[style="solid"]}'''],
			#["Replace 'foo' with 'striped'.",	"Use valid 'striped' instead of invalid 'foo' node style.",		'''graph{1[style="striped"]}'''],
			#["Replace 'foo' with 'wedged'.", 	"Use valid 'wedged' instead of invalid 'foo' node style.",		'''graph{1[style="wedged"]}''']
		])

		// test deprecated attribute value
		// TODO: provide quickfixes for deprecated node style attribute values
		'''graph{1[style="setlinewidth(4)"]}'''.assertQuickfixes(#[])
	}

	@Test
	def subgraph_rank() {
		// test unquoted attribute value
		'''graph{subgraph{rank=foo}}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 											text after quickfix application
			#["Replace 'foo' with 'same'.",		"Use valid 'same' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=same}}" ],
			#["Replace 'foo' with 'min'.", 		"Use valid 'min' instead of invalid 'foo' subgraph rankType.", 		"graph{subgraph{rank=min}}" ],
			#["Replace 'foo' with 'source'.",	"Use valid 'source' instead of invalid 'foo' subgraph rankType.",	"graph{subgraph{rank=source}}" ],
			#["Replace 'foo' with 'max'.",		"Use valid 'max' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=max}}" ],
			#["Replace 'foo' with 'sink'.",		"Use valid 'sink' instead of invalid 'foo' subgraph rankType.",		"graph{subgraph{rank=sink}}" ]
		])

		// test quoted attribute value
		'''graph{subgraph{rank="foo"}}'''.assertQuickfixes(#[
			// quickfix label					quickfix description	 											text after quickfix application
			#["Replace 'foo' with 'same'.",		"Use valid 'same' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="same"}}'''],
			#["Replace 'foo' with 'min'.",		"Use valid 'min' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="min"}}'''],
			#["Replace 'foo' with 'source'.",	"Use valid 'source' instead of invalid 'foo' subgraph rankType.",	'''graph{subgraph{rank="source"}}'''],
			#["Replace 'foo' with 'max'.",		"Use valid 'max' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="max"}}'''],
			#["Replace 'foo' with 'sink'.",		"Use valid 'sink' instead of invalid 'foo' subgraph rankType.",		'''graph{subgraph{rank="sink"}}''']
		])
	}

	private def assertQuickfixes(CharSequence text, List<List<String>> expected) {
		val issues = text.parse.validate
		1.assertEquals(issues.size)
		val actualIssueResolutions = issues.get(0).getResolutions
		expected.size.assertEquals(actualIssueResolutions.size)
		for (i : 0..< actualIssueResolutions.length) {
			val actualIssueResolution = actualIssueResolutions.get(i)
			val expectedIssueResolution = expected.get(i)
			expectedIssueResolution.get(0).assertEquals(actualIssueResolution.label)
			expectedIssueResolution.get(1).assertEquals(actualIssueResolution.getDescription)
			expectedIssueResolution.get(2).assertIssueResolutionEffect(actualIssueResolution, text.toString)
		}
	}

	private def assertIssueResolutionEffect(String expectedResult, IssueResolution actualIssueResolution, String originalText) {
		val xtextDocument = injector.getDocument(originalText)
		xtextDocument.assertNotNull
		var modificationContext = new TestIssueModificationContext
		modificationContext.setDocument(xtextDocument)
		var actualIssueResolution2 = new IssueResolution(actualIssueResolution.label, actualIssueResolution.description,
			actualIssueResolution.image, modificationContext, actualIssueResolution.modification,
			actualIssueResolution.relevance)
		actualIssueResolution2.apply
		var actualResult = actualIssueResolution2.modificationContext.xtextDocument.get
		expectedResult.assertEquals(actualResult)
	}

	private static class TestIssueModificationContext extends IssueModificationContext {
		IXtextDocument doc

		override IXtextDocument getXtextDocument() {
			return doc
		}

		def setDocument(IXtextDocument doc) {
			this.doc = doc
		}
	}
}
