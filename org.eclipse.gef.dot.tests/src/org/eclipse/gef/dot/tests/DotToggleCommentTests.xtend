/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotToggleCommentTests extends AbstractEditorTest {

	@Inject XtextEditorInfo xtextEditorInfo

	@Test def toggling_comment_001() {
		'''
			graph {
			}
		'''.testToggleCommentAction('''
			graph {
		''', '''
			//graph {
			}
		''')
	}

	@Test def toggling_comment_002() {
		'''
			graph {
			}
		'''.testToggleCommentAction("}", '''
			graph {
			//}
		''')
	}

	@Test def toggling_comment_003() {
		'''
			graph {
			}
		'''.testToggleCommentAction('''
			graph {
			}
		''', '''
			//graph {
			//}
		''')
	}

	@Test def toggling_comment_004() {
		'''
			//graph {
			//}
		'''.testToggleCommentAction('''
			//graph {
			//}
		''', '''
			graph {
			}
		''')
	}

	@Test def toggling_comment_005() {
		'''
			//graph {
			//}
		'''.testToggleCommentAction('''
			//graph {
		''', '''
			graph {
			//}
		''')
	}

	private def testToggleCommentAction(CharSequence it, String text, String newContent) {
		// given
		dslFile.openEditor.select(text).
		// when
		toggleComment.
		// then
		dslFileHasContent(newContent)
	}

	private def dslFile(CharSequence content) {
		//IResourcesSetupUtil.createFile(projectName, fileName, fileExtension, content)
		DotTestUtils.createTestFile(content.toString)
	}

	private def select(XtextEditor editor, String text) {
		val editorContent = editor.document.get
		val start = editorContent.indexOf(text)
		val length = text.length
		
		if(start == -1) {
			fail('''
				The input text
				«editorContent»
				must contain the text '«text»' that is to be selected!
			''')
		}
		
		editor.selectAndReveal(start, length)
		
		editor
	}

	private def toggleComment(XtextEditor xtextEditor) {
		xtextEditor.getAction("ToggleComment").run
		xtextEditor
	}

	private def dslFileHasContent(XtextEditor editor, CharSequence expectedContent) {
		val actualContent = editor.document.get
		expectedContent.assertEquals(actualContent)
	}

	override protected getEditorId() {
		xtextEditorInfo.editorId
	}
}