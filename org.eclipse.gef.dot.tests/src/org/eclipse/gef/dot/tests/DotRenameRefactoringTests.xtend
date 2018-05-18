/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #530423)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.Provider
import java.io.StringReader
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IncrementalProjectBuilder
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.DotPackage
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeId
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.ltk.core.refactoring.Change
import org.eclipse.ltk.core.refactoring.RefactoringStatus
import org.eclipse.ui.actions.WorkspaceModifyOperation
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.parser.IParseResult
import org.eclipse.xtext.parser.IParser
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.eclipse.xtext.ui.refactoring.impl.RenameElementProcessor
import org.eclipse.xtext.ui.refactoring.ui.IRenameElementContext
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.DotFileUtils.read

import static extension org.eclipse.gef.dot.tests.DotTestUtils.*

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotRenameRefactoringTests extends AbstractEditorTest {
	
	@Inject Provider<RenameElementProcessor> processorProvider
	@Inject XtextEditorInfo editorInfo
	@Inject IParser parser

	override void setUp() throws Exception {
		super.setUp()
		createTestProjectWithXtextNature
	}

	@Test
	def testRenaming01(){
		val initialText = '''
			digraph {
				1
				1->2
			}
		'''
		val targetElement = initialText.firstNode
		val newNodeName = "3"
		val expectedText = '''
			digraph {
				3
				3->2
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}

	@Test
	def testRenaming02(){
		val initialText = '''
			digraph {
				1
				1->2
			}
		'''
		val targetElement = initialText.firstNode
		val newNodeName = "4"
		val expectedText = '''
			digraph {
				4
				4->2
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming03(){
		val initialText = '''
			digraph {
				1
				1->2
			}
		'''
		val targetElement = initialText.sourceNodeOfFirstEdge
		val newNodeName = "3"
		val expectedText = '''
			digraph {
				3
				3->2
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming04(){
		val initialText = '''
			digraph {
				1
				1->2
			}
		'''
		val targetElement = initialText.targetNodeOfFirstEdge
		val newNodeName = "3"
		val expectedText = '''
			digraph {
				1
				1->3
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming05(){
		val initialText = '''
			digraph {
				1
				1->2
				1->3
			}
		'''
		val targetElement = initialText.firstNode
		val newNodeName = "4"
		val expectedText = '''
			digraph {
				4
				4->2
				4->3
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming06(){
		val initialText = '''
			digraph {
				1
				1->2
				1->3
			}
		'''
		val targetElement = initialText.sourceNodeOfFirstEdge
		val newNodeName = "4"
		val expectedText = '''
			digraph {
				4
				4->2
				4->3
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming07(){
		val initialText = '''
			digraph {
				1
				2->1
				1->3
			}
		'''
		val targetElement = initialText.targetNodeOfFirstEdge
		val newNodeName = "5"
		val expectedText = '''
			digraph {
				5
				2->5
				5->3
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming08(){
		val initialText = '''
			digraph {
				1
				1->3
				1->1
			}
		'''
		val targetElement = initialText.sourceNodeOfSecondEdge
		val newNodeName = "2"
		val expectedText = '''
			digraph {
				2
				2->3
				2->2
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming09(){
		val initialText = '''
			graph {
				1--1
			}
		'''
		val targetElement = initialText.sourceNodeOfFirstEdge
		val newNodeName = "2"
		val expectedText = '''
			graph {
				2--2
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming10(){
		val initialText = '''
			graph {
				1--1
			}
		'''
		val targetElement = initialText.targetNodeOfFirstEdge
		val newNodeName = "2"
		val expectedText = '''
			graph {
				2--2
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	@Test
	def testRenaming11(){
		val initialText = '''
			digraph {
				1
				2
				1->3
				1->1
			}
		'''
		val targetElement = initialText.targetNodeOfSecondEdge
		val newNodeName = "4"
		val expectedText = '''
			digraph {
				4
				2
				4->3
				4->4
			}
		'''
		
		initialText.testRenaming(targetElement, newNodeName, expectedText)
	}
	
	private def testRenaming(String initialText, NodeId targetElement, String newNodeName, String expectedTextAfterRenaming){
		val testFile = initialText.createTestFile
		testFile.doRename(targetElement, newNodeName)
		var String actualTextAfterRenaming = read(testFile.contents)
		assertEquals(expectedTextAfterRenaming, actualTextAfterRenaming)
	}

	private def doRename(IFile testFile, NodeId targetElement, String newNodeName) throws Exception {
		waitForBuild(null)
		var String targetElementFragment = EcoreUtil.getURI(targetElement).fragment
		var URI targetElementURI = URI.createPlatformResourceURI(testFile.getFullPath().toString(), true).
			appendFragment(targetElementFragment)
		val Change change = createChange(targetElementURI, newNodeName)
		([IProgressMonitor monitor|change.perform(monitor)] as WorkspaceModifyOperation).run(null)
	}

	private def NodeId getFirstNode(String modelAsText) {
		var DotAst dotAst = modelAsText.dotAst
		var NodeStmt nodeStmt = dotAst.graphs.head.stmts.filter(NodeStmt).head
		nodeStmt.node		
	}
	
	private def NodeId getSourceNodeOfFirstEdge(String modelAsText) {
		var DotAst dotAst = modelAsText.dotAst
		var EdgeStmtNode edgeStmtNode = (dotAst.graphs.head.stmts.filter(EdgeStmtNode).head)
		edgeStmtNode.node		
	}

	private def NodeId getTargetNodeOfFirstEdge(String modelAsText) {
		var DotAst dotAst = modelAsText.dotAst
		var EdgeStmtNode edgeStmtNode = (dotAst.graphs.head.stmts.filter(EdgeStmtNode).head)
		(edgeStmtNode.edgeRHS.head as EdgeRhsNode).node		
	}
	
	private def NodeId getSourceNodeOfSecondEdge(String modelAsText) {
		var DotAst dotAst = modelAsText.dotAst
		var EdgeStmtNode edgeStmtNode = (dotAst.graphs.head.stmts.filter(EdgeStmtNode).get(1))
		edgeStmtNode.node		
	}

	private def NodeId getTargetNodeOfSecondEdge(String modelAsText) {
		var DotAst dotAst = modelAsText.dotAst
		var EdgeStmtNode edgeStmtNode = (dotAst.graphs.head.stmts.filter(EdgeStmtNode).get(1))
		(edgeStmtNode.edgeRHS.head as EdgeRhsNode).node		
	}

	private def getDotAst(String modelAsText) {
		var IParseResult parseResult = parser.parse(new StringReader(modelAsText))
		parseResult.rootASTElement as DotAst
	}

	private def waitForBuild(IProgressMonitor monitor) {
		try {
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder::INCREMENTAL_BUILD, monitor)
		} catch (CoreException e) {
			throw new OperationCanceledException(e.getMessage())
		}
	}

	private def Change createChange(URI targetElementURI, String newName) throws Exception {
		var RenameElementProcessor processor = processorProvider.get()
		processor.initialize(new IRenameElementContext.Impl(targetElementURI, DotPackage.Literals::NODE_ID))
		processor.setNewName(newName)
		var RefactoringStatus initialStatus = processor.checkInitialConditions(new NullProgressMonitor())
		assertTrue("Initial RefactoringStatus is OK", initialStatus.isOK())
		var RefactoringStatus finalStatus = processor.checkFinalConditions(new NullProgressMonitor(), null)
		assertTrue("Final RefactoringStatus is OK", finalStatus.isOK())
		val Change change = processor.createChange(new NullProgressMonitor())
		assertNotNull("RenameElementProcessor created changes", change)
		return change
	}

	override protected getEditorId() {
		editorInfo.editorId
	}
}
