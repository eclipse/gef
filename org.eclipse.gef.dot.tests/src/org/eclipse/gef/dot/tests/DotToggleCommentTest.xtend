/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
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

import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.testing.AbstractEditorTest
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.addNature

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotToggleCommentTest extends AbstractEditorTest {

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
		val file = DotTestUtils.createTestFile(content.toString)
		
		val project = file.project
		if(!project.hasNature(XtextProjectHelper.NATURE_ID)) {
			project.addNature(XtextProjectHelper.NATURE_ID)
		} 
		file
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
}