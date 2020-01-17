/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
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
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditor
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.addNature

/*
 * Test cases for the {@link DotEditor} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotEditorTest extends AbstractEditorTest {
	
	@Inject XtextEditorInfo editorInfo
	@Inject extension FileExtensionProvider
	
	@Test def dot_graph_view_is_present_in_the_show_in_context_menu() {
		'''
			digraph {
				1[shape=circle]
			}
		'''.testThatShowInDotGraphContextMenuIsShown
	}
	
	private def testThatShowInDotGraphContextMenuIsShown(CharSequence it) {
		// given
		dslFile.
		// when
		openEditor.
		// then
		showInDotGraphViewIsShown
	}
	
	private def dslFile(CharSequence text) {
		val file = IResourcesSetupUtil.createFile(projectName + "/" + fileName + "." + fileExtension, text.toString)

		/*
		 * TODO: find a better (with good performance) solution
		 * to set the Xtext nature on the test project.
		 */
		val project = file.project
		if(!project.hasNature(XtextProjectHelper.NATURE_ID)) {
			project.addNature(XtextProjectHelper.NATURE_ID)
		}

		file
	}
	
	private def showInDotGraphViewIsShown(XtextEditor editor) {
		val dotEditor = editor as DotEditor
		val showInTargetIds = dotEditor.showInTargetIds
		
		val actual = showInTargetIds.sort.join(System.lineSeparator)
		val expected = "org.eclipse.gef.dot.internal.ui.DotGraphView" // id of the GEF DOT Graph view
		val message = "Cannot find the DOT Graph view in the 'Show In' context menu of the DOT Editor !"
		
		assertEquals(message, expected, actual)
	}
	
	private def String getProjectName() {
		"DOTEditorTest"
	}

	private def String getFileName() {
		"test"
	}

	private def String getFileExtension() {
		primaryFileExtension
	}
	
	override protected getEditorId() {
		editorInfo.getEditorId
	}
	
}