/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
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

import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotFoldingTest extends AbstractFoldingTest {

	@Test def comments() {
		'''
			[>graph {
				// This is a C++-style single line comment.
				
			[>	/*
				 * This is a C++-style
				 * multi line comment.
				 */<]
				 
				 # This is considered as a line output from C-preprocessor and discarded.
			}<]
		'''.testFoldingRegions
	}

	@Test def graph_with_one_node() {
		'''
			[>graph {
				1
			}<]
		'''.testFoldingRegions
	}

	@Test def clusters() {
		'''
			[>digraph {
			[>	subgraph cluster1 {
					a;
					b;
					a -> b;
				}<]
			[>	subgraph cluster2 {
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
				}<]
				b -> q;
				t -> a;
			}<]
		'''.testFoldingRegions
	}

	@Test def graph_label_HTML_like_01() {
		'''
			[>graph {
			[>	label =
					<
			[>			<FONT POINT-SIZE="24.0">
							line3
						</FONT><]
					><]
			}<]
		'''.testFoldingRegions
	}

	@Test def graph_label_HTML_like_02() {
		'''
			[>graph {
			[>	label =
					<
			[>			<!--
							Html label with custom font
						--><]
			[>			<font color="green">
			[>				<table>
			[>					<tr>
									<td>text</td>
								</tr><]
							</table><]
						</font><]
					><]
			}<]
		'''.testFoldingRegions
	}

	@Test def attribute_list_01() {
		'''
			[>graph {
			[>	node[
				]<]
			}<]
		'''.testFoldingRegions
	}

	@Test def attribute_list_02() {
		'''
			[>graph {
			[>	node
			[>	[
				]<]<]
			}<]
		'''.testFoldingRegions
	}

	@Test def attribute_list_03() {
		'''
			[>graph {
			[>	1--2[
					color = "blue;0.75:red;0.25"
				]<]
			}<]
		'''.testFoldingRegions
	}

	@Test def attribute_list_04() {
		'''
			[>graph {
			[>	1[
					style=filled
					fillcolor="red;0.25:blue;0.75"
				]<]
			}<]
		'''.testFoldingRegions
	}

	@Test def incomplete_attribute_statement() {
		'''graph {1[color= ]}'''.testFoldingRegions
	}

	@Test def incomplete_attribute_statement_with_line_breaks() {
		'''
			[>graph {
				1[color=]
			}<]
		'''.testFoldingRegions
	}
}
