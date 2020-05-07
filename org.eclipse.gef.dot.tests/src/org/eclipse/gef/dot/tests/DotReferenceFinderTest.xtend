/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
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
import java.util.List
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.search.ui.IQueryListener
import org.eclipse.search.ui.ISearchQuery
import org.eclipse.search.ui.ISearchResult
import org.eclipse.search.ui.NewSearchUI
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.editor.findrefs.ReferenceQueryExecutor
import org.eclipse.xtext.ui.editor.findrefs.ReferenceSearchResult
import org.eclipse.xtext.ui.refactoring.ui.SyncUtil
import org.eclipse.xtext.ui.resource.IResourceSetProvider
import org.eclipse.xtext.ui.testing.AbstractEditorTest
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.tests.DotTestUtils.createTestProjectWithXtextNature

import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotReferenceFinderTest extends AbstractEditorTest {

	@Inject extension SyncUtil
	@Inject IResourceSetProvider resourceSetProvider
	@Inject ReferenceQueryExecutor referenceQueryExecutor

	ISearchResult searchResult = null

	override setUp() {
		super.setUp
		createTestProjectWithXtextNature
	}

	@Test def finding_references_001() {
		'''
			graph {
				1
			}
		'''.testFindingReferences([firstNode], "DOT References to node '1' (/dottestproject/test.dot)", #[
			[firstNode]
		])
	}

	@Test def finding_references_002() {
		'''
			graph {
				1
				2
			}
		'''.testFindingReferences([secondNode], "DOT References to node '2' (/dottestproject/test.dot)", #[
			[secondNode]
		])
	}

	@Test def finding_references_003() {
		'''
			graph {
				1
				1--2
			}
		'''.testFindingReferences([firstNode], "DOT References to node '1' (/dottestproject/test.dot)", #[
			[firstNode],
			[sourceNodeOfFirstEdge]
		])
	}

	@Test def finding_references_004() {
		'''
			graph {
				1
				1--2
			}
		'''.testFindingReferences([sourceNodeOfFirstEdge], "DOT References to node '1' (/dottestproject/test.dot)", #[
			[firstNode],
			[sourceNodeOfFirstEdge]
		])
	}

	@Test def finding_references_005() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindingReferences([sourceNodeOfSecondEdge], "DOT References to node '3' (/dottestproject/test.dot)", #[
			[sourceNodeOfSecondEdge]
		])
	}

	@Test def finding_references_006() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindingReferences([targetNodeOfFirstEdge], "DOT References to node '2' (/dottestproject/test.dot)", #[
			[targetNodeOfFirstEdge]
		])
	}

	@Test def finding_references_007() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindingReferences([targetNodeOfSecondEdge], "DOT References to node '4' (/dottestproject/test.dot)", #[
			[targetNodeOfSecondEdge]
		])
	}

	@Test def finding_references_008() {
		'''
			graph {
				1--2
				2--2
			}
		'''.testFindingReferences([targetNodeOfFirstEdge], "DOT References to node '2' (/dottestproject/test.dot)", #[
			[targetNodeOfFirstEdge],
			[sourceNodeOfSecondEdge],
			[targetNodeOfSecondEdge]
		])
	}

	@Test def finding_references_009() {
		'''
			graph {
				1--2
				2--2
			}
		'''.testFindingReferences([sourceNodeOfSecondEdge], "DOT References to node '2' (/dottestproject/test.dot)", #[
			[targetNodeOfFirstEdge],
			[sourceNodeOfSecondEdge],
			[targetNodeOfSecondEdge]
		])
	}

	@Test def finding_references_010() {
		'''
			graph {
				1--2
				2--2
			}
		'''.testFindingReferences([targetNodeOfSecondEdge], "DOT References to node '2' (/dottestproject/test.dot)", #[
			[targetNodeOfFirstEdge],
			[sourceNodeOfSecondEdge],
			[targetNodeOfSecondEdge]
		])
	}

	private def testFindingReferences(CharSequence it, (DotAst)=>EObject element, String label, List<(DotAst)=>EObject> elements) {
		// given
		dslFile.
		// when
		searchingReferencesOn(element).
		// then
		searchViewDisplays(label).
		// and
		searchViewContains(elements)
	}

	private def dslFile(CharSequence it) {
		val testFile = toString.createTestFile

		val project = testFile.project
		val resourceSet = resourceSetProvider.get(project)
		val projectFullPath = project.fullPath.toString

		val uri = URI.createPlatformResourceURI(projectFullPath + "/" + testFile.name , true)
		val resource = resourceSet.createResource(uri)
		resource.load(newHashMap)
		resource
	}

	private def searchingReferencesOn(Resource resource, (DotAst)=>EObject elementProvider) {
		val dotAst = resource.contents.head as DotAst

		val element = elementProvider.apply(dotAst)

		waitForBuild(new NullProgressMonitor)

		referenceQueryExecutor.init(element)

		NewSearchUI.addQueryListener( new IQueryListener() {

			override queryAdded(ISearchQuery query) {
			}

			override queryFinished(ISearchQuery query) {
				searchResult = query.searchResult
			}

			override queryRemoved(ISearchQuery query) {
			}

			override queryStarting(ISearchQuery query) {
			}

		});

		referenceQueryExecutor.execute

		while(searchResult===null) {
			Thread.sleep(100)
		}

		element
	}

	private def searchViewDisplays(EObject element, String label) {
		val actualLabel = referenceQueryExecutor.getLabel(element)
		label.assertEquals(actualLabel)
		element
	}

	private def searchViewContains(EObject element, List<(DotAst)=>EObject> expectedReferences) {
		val matchingReferences = (searchResult as ReferenceSearchResult).matchingReferences

		val resource = element.eResource
		val dotAst = resource.contents.head as DotAst

		val actual = matchingReferences.map[sourceEObjectUri.fragment].sort.join(System.lineSeparator)
		val expected = expectedReferences.map[resource.getURIFragment(apply(dotAst))].sort.join(System.lineSeparator)

		expected.assertEquals(actual)
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

	@AfterClass def static void cleanup() {
		/**
		 * The Eclipse workspace needs to be explicitly saved after the test execution
		 * otherwise, the test case executions are resulting in a NullPointerException.
		 * For more information, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=460996
		 */
		ResourcesPlugin.workspace.save(true, null)
	}
}