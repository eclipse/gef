/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #531049)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.ui.editor.findrefs.ReferenceQueryExecutor
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotReferenceFinderTests {

	@Inject extension ParseHelper<DotAst>
	@Inject extension ReferenceQueryExecutor

	@Test def testFindingReferences001() {
		'''
			graph {
				1
			}
		'''.testFindReferencesLabel([firstNode], "DOT References to node '1'")
	}

	@Test def testFindingReferences002() {
		'''
			graph {
				1
				2
			}
		'''.testFindReferencesLabel([secondNode], "DOT References to node '2'")
	}

	@Test def testFindingReferences003() {
		'''
			graph {
				1
				1--2
			}
		'''.testFindReferencesLabel([sourceNodeOfFirstEdge], "DOT References to node '1'")
	}

	@Test def testFindingReferences004() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindReferencesLabel([sourceNodeOfSecondEdge], "DOT References to node '3'")
	}

	@Test def testFindingReferences005() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindReferencesLabel([targetNodeOfFirstEdge], "DOT References to node '2'")
	}

	@Test def testFindingReferences006() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindReferencesLabel([targetNodeOfSecondEdge], "DOT References to node '4'")
	}

	private def testFindReferencesLabel(CharSequence it, (DotAst)=>EObject elementProvider, String expectedLabel) {
		val actualLabel = elementProvider.apply(parse).label
		expectedLabel.assertEquals(actualLabel)
	}

	private def getFirstNode(DotAst it) {
		nodeStmts.head.node
	}

	private def getSecondNode(DotAst it) {
		nodeStmts.get(1).node
	}

	private def getSourceNodeOfFirstEdge(DotAst it) {
		edgeStmtNodes.head.node
	}

	private def getSourceNodeOfSecondEdge(DotAst it) {
		edgeStmtNodes.get(1).node
	}

	private def getTargetNodeOfFirstEdge(DotAst it) {
		edgeStmtNodes.head.targetNode
	}

	private def getTargetNodeOfSecondEdge(DotAst it) {
		edgeStmtNodes.get(1).targetNode
	}

	private def nodeStmts(DotAst it) {
		stmts.filter(NodeStmt)
	}

	private def edgeStmtNodes(DotAst it) {
		stmts.filter(EdgeStmtNode)
	}

	private def stmts(DotAst it) {
		graphs.head.stmts
	}

	private def targetNode(EdgeStmtNode it) {
		(edgeRHS.head as EdgeRhsNode).node
	}

}