/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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

import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotOutlineViewTests extends AbstractOutlineTest{

	@Test
	def void testEmptyGraph() {
		DotTestGraphs.EMPTY.assertAllLabels('''
		test.dot: File
		  <?>: Graph
		''')
	}

	@Test
	def void testSubgraph() {
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

	override protected getEditorId() {
		DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
	}
}