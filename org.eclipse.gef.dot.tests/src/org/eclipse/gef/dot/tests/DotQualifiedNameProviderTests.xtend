/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #545441)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertNotNull

import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotQualifiedNameProviderTests {

	@Inject extension ParseHelper<DotAst>
	@Inject extension IQualifiedNameProvider

	@Test def unquoted_node_in_unnamed_graph() {
		'''
			graph {
				1
			}
		'''.firstNode.hasQualifiedName("1")
	}

	@Test def unquoted_node_in_named_graph() {
		'''
			graph TestGraph {
				1
			}
		'''.firstNode.hasQualifiedName("1")
	}

	@Test def quoted_node_in_unnamed_graph() {
		'''
			graph {
				"node name"
			}
		'''.firstNode.hasQualifiedName("node name")
	}

	@Test def quoted_node_in_named_graph() {
		'''
			graph TestGraph {
				"node name"
			}
		'''.firstNode.hasQualifiedName("node name")
	}

	private def getFirstNode(CharSequence text) {
		text.parse.graphs.head.stmts.filter(NodeStmt).head.node
	}

	private def hasQualifiedName(EObject eObject, String expected) {
		val actual = eObject.fullyQualifiedName
		assertNotNull("Qualified name cannot be determined!", actual)
		expected.assertEquals(actual.toString)
	}

}