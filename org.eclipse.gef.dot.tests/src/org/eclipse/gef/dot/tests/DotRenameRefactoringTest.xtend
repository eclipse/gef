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
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IncrementalProjectBuilder
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.DotPackage
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.ui.actions.WorkspaceModifyOperation
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.eclipse.xtext.ui.refactoring.impl.RenameElementProcessor
import org.eclipse.xtext.ui.refactoring.ui.IRenameElementContext
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.DotFileUtils.read
import static org.eclipse.gef.dot.tests.DotTestUtils.createTestProjectWithXtextNature

import static extension org.eclipse.emf.common.util.URI.createPlatformResourceURI
import static extension org.eclipse.emf.ecore.util.EcoreUtil.getURI
import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotRenameRefactoringTest extends AbstractEditorTest {

	@Inject XtextEditorInfo editorInfo
	@Inject extension ParseHelper<DotAst>
	@Inject extension Provider<RenameElementProcessor>

	override setUp() {
		super.setUp
		createTestProjectWithXtextNature
	}

	@Test def rename_refactoring01() {
		'''
			graph {
				1
			}
		'''.
		testRenameRefactoring([firstNode], "2", '''
			graph {
				2
			}
		''')
	}

	@Test def rename_refactoring02() {
		'''
			digraph {
				1
				1->2
			}
		'''.
		testRenameRefactoring([firstNode], "3", '''
			digraph {
				3
				3->2
			}
		''')
	}

	@Test def rename_refactoring03() {
		'''
			digraph {
				1
				1->2
			}
		'''.
		testRenameRefactoring([sourceNodeOfFirstEdge], "3", '''
			digraph {
				3
				3->2
			}
		''')
	}

	@Test def rename_refactoring04() {
		'''
			digraph {
				1
				1->2
			}
		'''.
		testRenameRefactoring([firstNode], "4", '''
			digraph {
				4
				4->2
			}
		''')
	}

	@Test def rename_refactoring05() {
		'''
			digraph {
				1
				1->2
			}
		'''.
		testRenameRefactoring([targetNodeOfFirstEdge], "3", '''
			digraph {
				1
				1->3
			}
		''')
	}

	@Test def rename_refactoring06() {
		'''
			digraph {
				1
				1->2
				1->3
			}
		'''.
		testRenameRefactoring([firstNode], "4", '''
			digraph {
				4
				4->2
				4->3
			}
		''')
	}

	@Test def rename_refactoring07() {
		'''
			digraph {
				1
				1->2
				1->3
			}
		'''.
		testRenameRefactoring([sourceNodeOfFirstEdge], "4", '''
			digraph {
				4
				4->2
				4->3
			}
		''')
	}

	@Test def rename_refactoring08() {
		'''
			digraph {
				1
				2->1
				1->3
			}
		'''.
		testRenameRefactoring([targetNodeOfFirstEdge], "5", '''
			digraph {
				5
				2->5
				5->3
			}
		''')
	}

	@Test def rename_refactoring09() {
		'''
			digraph {
				1
				1->3
				1->1
			}
		'''.
		testRenameRefactoring([sourceNodeOfSecondEdge], "2", '''
			digraph {
				2
				2->3
				2->2
			}
		''')
	}

	@Test def rename_refactoring10() {
		'''
			graph {
				1--1
			}
		'''.
		testRenameRefactoring([sourceNodeOfFirstEdge], "2", '''
			graph {
				2--2
			}
		''')
	}

	@Test def rename_refactoring11() {
		'''
			graph {
				1--1
			}
		'''.
		testRenameRefactoring([targetNodeOfFirstEdge], "2", '''
			graph {
				2--2
			}
		''')
	}

	@Test def rename_refactoring12() {
		'''
			digraph {
				1
				2
				1->3
				1->1
			}
		'''.
		testRenameRefactoring([targetNodeOfSecondEdge], "4", '''
			digraph {
				4
				2
				4->3
				4->4
			}
		''')
	}

	private def testRenameRefactoring(CharSequence it, (DotAst)=>EObject element, String newName, CharSequence newContent) {
		// given
		dslFile.
		// when
		rename(target(element), newName).
		// then
		dslFileHasContent(newContent)
	}

	private def dslFile(CharSequence it) {
		toString.createTestFile
	}

	private def target(CharSequence it, extension (DotAst)=>EObject elementProvider) {
		parse.apply
	}

	private def rename(IFile testFile, EObject targetElement, String newName) {
		waitForBuild
		val targetElementFragment = targetElement.URI.fragment
		val targetElementURI = testFile.fullPath.toString.createPlatformResourceURI(true).appendFragment(targetElementFragment)

		val processor = get
		processor.initialize(new IRenameElementContext.Impl(targetElementURI, DotPackage.Literals.NODE_ID))
		processor.newName = newName

		val initialStatus = processor.checkInitialConditions(new NullProgressMonitor)
		assertTrue("Initial RefactoringStatus is OK", initialStatus.isOK)

		val finalStatus = processor.checkFinalConditions(new NullProgressMonitor, null)
		assertTrue("Final RefactoringStatus is OK", finalStatus.isOK)

		val change = processor.createChange(new NullProgressMonitor)
		assertNotNull("RenameElementProcessor created changes", change)

		val operation = [IProgressMonitor monitor|change.perform(monitor)] as WorkspaceModifyOperation
		operation.run(null)
		testFile
	}

	private def dslFileHasContent(IFile it, CharSequence expectedText) {
		expectedText.toString.assertEquals(read(contents))
	}

	private def getFirstNode(DotAst it) {
		nodeStmts.head.node
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

	private def waitForBuild() {
		ResourcesPlugin.workspace.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor)
	}

	override protected getEditorId() {
		editorInfo.editorId
	}
}
