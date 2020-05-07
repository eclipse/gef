/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse def License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.testing.AbstractOutlineTest
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.content

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotOutlineViewTest extends AbstractOutlineTest {

	@Test def empty_graph() {
		DotTestGraphs.EMPTY.assertAllLabels('''
		test.dot: File
			<?>: Graph
		''')
	}

	@Test def subgraph() {
		DotTestGraphs.CLUSTER_SCOPE.assertAllLabels('''
		test.dot: File
			<?>: Graph
				node: Attributes
					shape = "hexagon": Attribute
					style = "filled": Attribute
					fillcolor = "blue": Attribute
				<?>: Subgraph
					node: Attributes
						shape = "box": Attribute
					a: Node
					b: Node
				<?>: Subgraph
					node: Attributes
						fillcolor = "red": Attribute
					b: Node
					c: Node
		''')
	}

	@Test def complete_edge() {
		'''
			graph {
				1--2
			}
		'''.assertAllLabels('''
			test.dot: File
				<?>: Graph
					1 -- [1 Node]: Edges
						1: Node
						undirected -- 2: Node
		''')
	}

	@Test def incomplete_edge() {
		// The outline view should be able to cope with incomplete statements
		'''
			graph {
				1--
			}
		'''.assertAllLabels('''
			test.dot: File
				<?>: Graph
					<?>: Edges
						1: Node
		''')
	}

	@Test def incomplete_html_like_labels() {
		// The outline view should be able to cope with incomplete statements
		'''
			graph {
				1[label = <</TABLE>>]
			}
		'''.assertAllLabels('''
		test.dot: File
			<?>: Graph
				1: Node
					1: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
		''')
	}

	@Test def html_like_labels001() {
		"html_like_labels1.dot".testFile('''
		test.dot: File
			structs: Graph
				node: Attributes
					shape = plaintext: Attribute
				struct1: Node
					struct1: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								BORDER = "0": Attribute
								CELLBORDER = "1": Attribute
								CELLSPACING = "0": Attribute
								<TR>: Tag
									<TD>: Tag
										left: Text
									<TD>: Tag
										PORT = "f1": Attribute
										mid dle: Text
									<TD>: Tag
										PORT = "f2": Attribute
										right: Text
				struct2: Node
					struct2: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								BORDER = "0": Attribute
								CELLBORDER = "1": Attribute
								CELLSPACING = "0": Attribute
								<TR>: Tag
									<TD>: Tag
										PORT = "f0": Attribute
										one: Text
									<TD>: Tag
										two: Text
				struct3: Node
					struct3: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								BORDER = "0": Attribute
								CELLBORDER = "1": Attribute
								CELLSPACING = "0": Attribute
								CELLPADDING = "4": Attribute
								<TR>: Tag
									<TD>: Tag
										ROWSPAN = "3": Attribute
										hello: Text
										<BR/>: Tag
										world: Text
									<TD>: Tag
										COLSPAN = "3": Attribute
										b: Text
									<TD>: Tag
										ROWSPAN = "3": Attribute
										g: Text
									<TD>: Tag
										ROWSPAN = "3": Attribute
										h: Text
								<TR>: Tag
									<TD>: Tag
										c: Text
									<TD>: Tag
										PORT = "here": Attribute
										d: Text
									<TD>: Tag
										e: Text
								<TR>: Tag
									<TD>: Tag
										COLSPAN = "3": Attribute
										f: Text
				struct1 -> [1 Node]: Edges
					struct1: Node
					directed -> struct2: Node
				struct1 -> [1 Node]: Edges
					struct1: Node
					directed -> struct3: Node
		''')
	}

	@Test def html_like_labels002() {
		"html_like_labels2.dot".testFile('''
		test.dot: File
			G: Graph
				rankdir = LR: Attribute
				node: Attributes
					shape = plaintext: Attribute
				a: Node
					a: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								BORDER = "0": Attribute
								CELLBORDER = "1": Attribute
								CELLSPACING = "0": Attribute
								<TR>: Tag
									<TD>: Tag
										ROWSPAN = "3": Attribute
										BGCOLOR = "yellow": Attribute
										class: Text
								<TR>: Tag
									<TD>: Tag
										PORT = "here": Attribute
										BGCOLOR = "lightblue": Attribute
										qualifier: Text
				b: Node
					b: Node
					3 Attributes: Attributes
						shape = ellipse: Attribute
						style = filled: Attribute
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								BGCOLOR = "bisque": Attribute
								<TR>: Tag
									<TD>: Tag
										COLSPAN = "3": Attribute
										elephant: Text
									<TD>: Tag
										ROWSPAN = "2": Attribute
										BGCOLOR = "chartreuse": Attribute
										VALIGN = "bottom": Attribute
										ALIGN = "right": Attribute
										two: Text
								<TR>: Tag
									<TD>: Tag
										COLSPAN = "2": Attribute
										ROWSPAN = "2": Attribute
										<TABLE>: Tag
											BGCOLOR = "grey": Attribute
											<TR>: Tag
												<TD>: Tag
													corn: Text
											<TR>: Tag
												<TD>: Tag
													BGCOLOR = "yellow": Attribute
													c: Text
											<TR>: Tag
												<TD>: Tag
													f: Text
									<TD>: Tag
										BGCOLOR = "white": Attribute
										penguin: Text
								<TR>: Tag
									<TD>: Tag
										COLSPAN = "2": Attribute
										BORDER = "4": Attribute
										ALIGN = "right": Attribute
										PORT = "there": Attribute
										4: Text
				c: Node
					c: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							long line 1: Text
							<BR/>: Tag
							line 2: Text
							<BR/>: Tag
								ALIGN = "LEFT": Attribute
							line 3: Text
							<BR/>: Tag
								ALIGN = "RIGHT": Attribute
				<?>: Subgraph
					rank = same: Attribute
					b: Node
					c: Node
				a -> [1 Node]: Edges
					a: Node
					directed -> b: Node
					2 Attributes: Attributes
						dir = both: Attribute
						arrowtail = diamond: Attribute
				c -> [1 Node]: Edges
					c: Node
					directed -> b: Node
				d: Node
					d: Node
					1 Attribute: Attributes
						shape = triangle: Attribute
				d -> [1 Node]: Edges
					d: Node
					directed -> c: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								<TR>: Tag
									<TD>: Tag
										BGCOLOR = "red": Attribute
										WIDTH = "10": Attribute
									<TD>: Tag
										Edge labels: Text
										<BR/>: Tag
										also: Text
									<TD>: Tag
										BGCOLOR = "blue": Attribute
										WIDTH = "10": Attribute
		''')
	}

	@Test def html_like_labels003() {
		"html_like_labels3.dot".testFile('''
		test.dot: File
			structs: Graph
				node: Attributes
					shape = plaintext: Attribute
				struct1: Node
					struct1: Node
					1 Attribute: Attributes
						label = <HTML-Label>: Attribute
							<TABLE>: Tag
								<TR>: Tag
									<TD>: Tag
										line 1: Text
									<TD>: Tag
										BGCOLOR = "blue": Attribute
										<FONT>: Tag
											COLOR = "white": Attribute
											line2: Text
									<TD>: Tag
										BGCOLOR = "gray": Attribute
										<FONT>: Tag
											POINT-SIZE = "24.0": Attribute
											line3: Text
									<TD>: Tag
										BGCOLOR = "yellow": Attribute
										<FONT>: Tag
											POINT-SIZE = "24.0": Attribute
											FACE = "ambrosia": Attribute
											line4: Text
									<TD>: Tag
										<TABLE>: Tag
											CELLPADDING = "0": Attribute
											BORDER = "0": Attribute
											CELLSPACING = "0": Attribute
											<TR>: Tag
												<TD>: Tag
													<FONT>: Tag
														COLOR = "green": Attribute
														Mixed: Text
												<TD>: Tag
													<FONT>: Tag
														COLOR = "red": Attribute
														fonts: Text
		''')
	}

	@Test def html_like_labels004() {
		"html_like_labels4.dot".testFile('''
		test.dot: File
			<?>: Graph
				tee: Node
					tee: Node
					3 Attributes: Attributes
						shape = none: Attribute
						margin = 0: Attribute
						label = <HTML-Label>: Attribute
							<table>: Tag
								border = "0": Attribute
								cellspacing = "0": Attribute
								cellborder = "1": Attribute
								<tr>: Tag
									<td>: Tag
										width = "9": Attribute
										height = "9": Attribute
										fixedsize = "true": Attribute
										style = "invis": Attribute
									<td>: Tag
										width = "9": Attribute
										height = "9": Attribute
										fixedsize = "true": Attribute
										sides = "ltr": Attribute
									<td>: Tag
										width = "9": Attribute
										height = "9": Attribute
										fixedsize = "true": Attribute
										style = "invis": Attribute
								<tr>: Tag
									<td>: Tag
										width = "9": Attribute
										height = "9": Attribute
										fixedsize = "true": Attribute
										sides = "tlb": Attribute
									<td>: Tag
										width = "9": Attribute
										height = "9": Attribute
										fixedsize = "true": Attribute
										sides = "b": Attribute
									<td>: Tag
										width = "9": Attribute
										height = "9": Attribute
										fixedsize = "true": Attribute
										sides = "brt": Attribute
		''')
	}

	private def testFile(String fileName, CharSequence expected) {
		fileName.content.assertAllLabels(expected)
	}

	// use tabs instead of spaces for indentation
	override protected indent(StringBuffer buffer, int tabs) {
		for (var i = 0; i < tabs/2; i++) {
			buffer.append("\t")
		}
	}
}
