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
import java.util.Collections
import java.util.List
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.search.ui.NewSearchUI
import org.eclipse.search2.internal.ui.SearchView
import org.eclipse.swt.widgets.Display
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.eclipse.xtext.ui.editor.findrefs.ReferenceQueryExecutor
import org.eclipse.xtext.ui.editor.findrefs.ReferenceSearchResult
import org.eclipse.xtext.ui.refactoring.ui.SyncUtil
import org.eclipse.xtext.ui.resource.IResourceSetProvider
import org.eclipse.xtext.xbase.lib.Functions.Function1
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.tests.DotTestUtils.createTestProjectWithXtextNature

import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotReferenceFinderTests extends AbstractEditorTest {

	@Inject extension SyncUtil
	@Inject XtextEditorInfo editorInfo
	@Inject IResourceSetProvider resourceSetProvider
	@Inject ReferenceQueryExecutor referenceQueryExecutor

	override setUp() {
		super.setUp
		createTestProjectWithXtextNature
	}

	@Test def finding_references_001() {
		'''
			graph {
				1
			}
		'''.testFindingReferences([firstNode], "DOT References to node '1' (/dottestproject/test.dot)", list(
			[firstNode]
		))
	}

	@Test def finding_references_002() {
		'''
			graph {
				1
				2
			}
		'''.testFindingReferences([secondNode], "DOT References to node '2' (/dottestproject/test.dot)", list(
			[secondNode]
		))
	}

	@Test def finding_references_003() {
		'''
			graph {
				1
				1--2
			}
		'''.testFindingReferences([firstNode], "DOT References to node '1' (/dottestproject/test.dot)", list(
			[firstNode],
			[sourceNodeOfFirstEdge]
		))
	}

	@Test def finding_references_004() {
		'''
			graph {
				1
				1--2
			}
		'''.testFindingReferences([sourceNodeOfFirstEdge], "DOT References to node '1' (/dottestproject/test.dot)", list(
			[firstNode],
			[sourceNodeOfFirstEdge]
		))
	}

	@Test def finding_references_005() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindingReferences([sourceNodeOfSecondEdge], "DOT References to node '3' (/dottestproject/test.dot)", list(
			[sourceNodeOfSecondEdge]
		))
	}

	@Test def finding_references_006() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindingReferences([targetNodeOfFirstEdge], "DOT References to node '2' (/dottestproject/test.dot)", list(
			[targetNodeOfFirstEdge]
		))
	}

	@Test def finding_references_007() {
		'''
			graph {
				1--2
				3--4
			}
		'''.testFindingReferences([targetNodeOfSecondEdge], "DOT References to node '4' (/dottestproject/test.dot)", list(
			[targetNodeOfSecondEdge]
		))
	}

	@Test def finding_references_008() {
		'''
			graph {
				1--2
				2--2
			}
		'''.testFindingReferences([targetNodeOfFirstEdge], "DOT References to node '2' (/dottestproject/test.dot)", list(
			[targetNodeOfFirstEdge],
			[sourceNodeOfSecondEdge],
			[targetNodeOfSecondEdge]
		))
	}

	@Test def finding_references_009() {
		'''
			graph {
				1--2
				2--2
			}
		'''.testFindingReferences([sourceNodeOfSecondEdge], "DOT References to node '2' (/dottestproject/test.dot)", list(
			[targetNodeOfFirstEdge],
			[sourceNodeOfSecondEdge],
			[targetNodeOfSecondEdge]
		))
	}

	@Test def finding_references_010() {
		'''
			graph {
				1--2
				2--2
			}
		'''.testFindingReferences([targetNodeOfSecondEdge], "DOT References to node '2' (/dottestproject/test.dot)", list(
			[targetNodeOfFirstEdge],
			[sourceNodeOfSecondEdge],
			[targetNodeOfSecondEdge]
		))
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
		
		referenceQueryExecutor.execute
		
		waitForSearchJob
		
		element
	}

	private def searchViewDisplays(EObject element, String label) {
		val actualLabel = referenceQueryExecutor.getLabel(element)
		label.assertEquals(actualLabel)
		element
	}

	private def searchViewContains(EObject element, List<(DotAst)=>EObject> expectedReferences) {
		val searchResultView = NewSearchUI.getSearchResultView as SearchView
		val searchResult = searchResultView.currentSearchResult as ReferenceSearchResult
		val matchingReferences = searchResult.matchingReferences
		
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

	private def void waitForSearchJob() {
		Job.jobManager.find(null).findFirst[name.startsWith("DOT References to")]?.join
		waitForEventProcessing
	}

	// TODO: remove this workaround
	private def list(Function1<? super DotAst, ? extends EObject>... initial) {
		Collections.<Function1<? super DotAst, ? extends EObject>>unmodifiableList(CollectionLiterals.<Function1<? super DotAst, ? extends EObject>>newArrayList(initial))
	}

	private def void waitForEventProcessing() {
		while(Display.^default.readAndDispatch) {}
	}

	override protected getEditorId() {
		editorInfo.editorId
	}
}