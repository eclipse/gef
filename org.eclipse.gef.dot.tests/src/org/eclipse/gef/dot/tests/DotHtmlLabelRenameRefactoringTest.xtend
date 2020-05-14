/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage
import org.eclipse.gef.dot.tests.ui.DotHtmlLabelUiInjectorProvider
import org.eclipse.ui.actions.WorkspaceModifyOperation
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.resource.IResourceFactory
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.ui.refactoring.impl.RenameElementProcessor
import org.eclipse.xtext.ui.refactoring.ui.IRenameElementContext
import org.eclipse.xtext.ui.testing.AbstractEditorTest
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.DotFileUtils.read
import static org.eclipse.gef.dot.tests.DotTestUtils.createTestProjectWithXtextNature

import static extension org.eclipse.emf.common.util.URI.createPlatformResourceURI
import static extension org.eclipse.emf.ecore.util.EcoreUtil.getURI
import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelUiInjectorProvider)
class DotHtmlLabelRenameRefactoringTest extends AbstractEditorTest {

	@Inject IResourceFactory resourceFactory
	@Inject extension FileExtensionProvider
	@Inject extension ParseHelper<HtmlLabel>
	@Inject extension Provider<RenameElementProcessor>

	override setUp() {
		super.setUp
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("dothtmllabel", resourceFactory)
		createTestProjectWithXtextNature
	}

	@Test def rename_refactoring001() {
		'''
			<B>text</B>
		'''.
		testRenameRefactoring([firstHtmlTag], "B" -> "I", '''
			<I>text</I>
		''')
	}

	@Test def rename_refactoring002() {
		'''
			<SUB>text</SUB>
		'''.
		testRenameRefactoring([firstHtmlTag], "SUB" -> "SUP", '''
			<SUP>text</SUP>
		''')
	}

	@Test def rename_refactoring003() {
		'''
			<HR/>
		'''.
		testRenameRefactoring([firstHtmlTag], "HR" -> "VR", '''
			<VR/>
		''')
	}

	@Test def rename_refactoring004() {
		'''
			<B><I>text</I></B>
		'''.
		testRenameRefactoring([firstHtmlTag], "B" -> "O", '''
			<O><I>text</I></O>
		''')
	}

	private def testRenameRefactoring(CharSequence it, (HtmlLabel)=>HtmlTag element, Pair<String, String> names, CharSequence newContent) {
		// given
		dslFile.
		// when
		rename(target(element), names).
		// then
		dslFileHasContent(newContent)
	}

	private def dslFile(CharSequence it) {
		toString.createTestFile(primaryFileExtension)
	}

	private def target(CharSequence it, extension (HtmlLabel)=>HtmlTag elementProvider) {
		parse.apply
	}

	private def rename(IFile testFile, HtmlTag targetElement, Pair<String, String> names) {
		waitForBuild

		// ensure the right element is selected
		val currentName = names.key
		assertEquals("Wrong target element is selected!", currentName, targetElement.name)

		val newName = names.value

		val targetElementFragment = targetElement.URI.fragment
		val targetElementURI = testFile.fullPath.toString.createPlatformResourceURI(true).appendFragment(targetElementFragment)

		val processor = get
		processor.initialize(new IRenameElementContext.Impl(targetElementURI, HtmllabelPackage.Literals.HTML_TAG))
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

	private def getFirstHtmlTag(HtmlLabel it) {
		parts.filter[tag!==null].map[tag].head
	}

	private def waitForBuild() {
		ResourcesPlugin.workspace.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor)
	}
}
