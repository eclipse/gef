/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.DotImport
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.ui.Dot2ZestAttributesConverter
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.zest.fx.ZestProperties.*
import static extension org.junit.Assert.*

/*
 * Test cases for the {@link Dot2ZestAttributesConverter#convertAttributes(Edge, Edge)} method.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class Dot2ZestEdgeAttributesConversionTests {
	
	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper
	
	extension DotImport = new DotImport
	extension TestableDot2ZestAttributesConverter = new TestableDot2ZestAttributesConverter

	@BeforeClass
	def static before() {
		DotTestUtils.registerDotSubgrammarPackages
	}

	@Test def edge_style001() {
		'''
			digraph {
				1->2
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
		''')
	}

	@Test def edge_style002() {
		'''
			digraph {
				1->2[color=green]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
			-fx-stroke: #00ff00;
		''')
	}
	
	@Test def edge_style003() {
		'''
			graph {
				1--2 [style=bold]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-width: 2;
		''')
	}
	
	@Test def edge_style004() {
		'''
			graph {
				1--2 [style=dashed]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-dash-array: 7 7;
		''')
	}
	
	@Test def edge_style005() {
		'''
			graph {
				1--2 [style=dotted]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-dash-array: 1 7;
		''')
	}
	
	@Test def edge_style006() {
		'''
			graph {
				1--2 [style=solid]
			}
		'''.assertEdgeStyle('''
			-fx-stroke-line-cap: butt;
		''')
	}
	
	private def assertEdgeStyle(CharSequence dotText, String expected) {
		val actual = dotText.firstEdge.convert.curveCssStyle.split
		expected.assertEquals(actual)
	}
	
	private def firstEdge(CharSequence dotText) {
		dotText.graph.edges.head
	}
	
	private def graph(CharSequence dotText) {
		// ensure that the input text can be parsed and the ast can be created
		val dotAst = dotText.parse
		dotAst.assertNoErrors
		
		dotAst.importDot.get(0)
	}
	
	private def convert(Edge dotEdge) {
		val zestEdge = new Edge(new Node, new Node)
		dotEdge.convertAttributes(zestEdge)
		zestEdge
	}

	private def split(String text) {
		text.replaceAll(";", ";" + System.lineSeparator)
	}
	
	private static class TestableDot2ZestAttributesConverter extends Dot2ZestAttributesConverter {
		
		// extend the method visibility from 'protected' to 'public'
		override convertAttributes(Edge dot, Edge zest) {
			super.convertAttributes(dot, zest)
		}
		
	}
}