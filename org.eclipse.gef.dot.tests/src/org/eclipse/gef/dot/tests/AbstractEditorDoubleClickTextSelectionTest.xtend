/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.jface.text.ITextSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Event
import org.eclipse.ui.texteditor.AbstractTextEditor
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.XtextEditorInfo

/**
 * The implementation of this class is mainly taken from the
 * org.eclipse.xtext.ui.testing.AbstractEditorDoubleClickTextSelectionTest class,
 * available from Xtext Version 2.14 (PHOTON). As long as older platforms are
 * supported, this class should reside here.
 * 
 * TODO: drop this class as soon as PHOTON will be the minimum supported platform.
 */
abstract class AbstractEditorDoubleClickTextSelectionTest extends AbstractEditorTest {

	@Inject XtextEditorInfo xtextEditorInfo
	@Inject extension FileExtensionProvider

	/**
	 * Special symbol indicating the current cursor position.
	 */
	def String c() '''|'''

	/**
	 * @param it - The editor's input text. The text must contain the {@link #c}
	 *	special symbol indicating the current cursor position.
	 * 
	 * @param expected - The text that is expected to be selected after double
	 *	clicking in the Xtext editor on the current cursor position.
	 */
	def assertSelectedTextAfterDoubleClicking(CharSequence it, String expected) {

		content.createFile.openEditor.

		doubleClick(cursorPosition).

		assertSelectedText(expected)
	}

	protected def getContent(CharSequence text) {
		text.toString.replace(c, "")
	}

	protected def createFile(String content) {
		//IResourcesSetupUtil.createFile(projectName, fileName, fileExtension, content)
		DotTestUtils.createTestFile(content)
	}

	protected def String getProjectName() '''Test'''

	protected def String getFileName() '''Foo'''

	protected def getFileExtension() {
		primaryFileExtension
	}

	protected def int getCursorPosition(CharSequence text) {
		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition == -1) {
			fail('''
				The input text
				«text»
				must contain the '«c»' special symbol indicating the current cursor position!
			''')
		}
		cursorPosition
	}

	protected def XtextEditor doubleClick(XtextEditor xtextEditor, int cursorPosition) {
		val viewer = xtextEditor.internalSourceViewer

		// set the cursor position
		viewer.setSelectedRange(cursorPosition, 0)

		// fire a mouse down event with the left mouse button
		viewer.textWidget.notifyListeners(
			SWT.MouseDown,
			new Event => [
				button = 1
			]
		)

		xtextEditor
	}

	protected def assertSelectedText(AbstractTextEditor textEditor, CharSequence expectedSelectedText) {
		val actualSelectedText = (textEditor.selectionProvider.selection as ITextSelection).text
		expectedSelectedText.assertEquals(actualSelectedText)
	}

	override protected getEditorId() {
		xtextEditorInfo.editorId
	}

}
