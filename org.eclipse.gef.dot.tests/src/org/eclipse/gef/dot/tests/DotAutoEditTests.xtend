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
import org.eclipse.xtext.junit4.ui.AbstractAutoEditTest
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.addNature
import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.createProject

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotAutoEditTests extends AbstractAutoEditTest {

	@Inject XtextEditorInfo xtextEditorInfo
	@Inject extension FileExtensionProvider

	override setUp() {
		super.setUp
		createTestProjectWithXtextNature
	}

	@Test def testAutoEdit001() {
		'''
			graph |
		'''.testAutoEdit('{', '''
			graph {|}
		''')
	}

	@Test def testAutoEdit002() {
		'''
			digraph |
		'''.testAutoEdit('{', '''
			digraph {|}
		''')
	}

	@Test def testAutoEdit003() {
		'''
			graph {
				node|
			}
		'''.testAutoEdit('[', '''
			graph {
				node[|]
			}
		''')
	}

	@Test def testAutoEdit004() {
		'''
			graph {
				1|
			}
		'''.testAutoEdit('[', '''
			graph {
				1[|]
			}
		''')
	}

	@Test def testAutoEdit005() {
		'''
			digraph {
				1-->2|
			}
		'''.testAutoEdit('[', '''
			digraph {
				1-->2[|]
			}
		''')
	}

	@Test def testAutoEdit006() {
		'''
			graph {
				1[label=|]
			}
		'''.testAutoEdit('"', '''
			graph {
				1[label="|"]
			}
		''')
	}

	@Test def testAutoEdit007() {
		'''
			graph {
				1[label=|]
			}
		'''.testAutoEdit('<', '''
			graph {
				1[label=<|>]
			}
		''')
	}

	@Test def testAutoEdit008() {
		'''
			graph {
				1[label=<|>]
			}
		'''.testAutoEdit('<', '''
			graph {
				1[label=<<|>]
			}
		''')
	}

	@Test def testAutoEdit009() {
		'''
			graph {1[label=<<table|>]}
		'''.testAutoEdit('>', '''
			graph {1[label=<<table>|</table>>]}
		''')
	}

	@Test def testAutoEdit010() {
		'''
			graph {
				1[label=<<table|>]
			}
		'''.testAutoEdit('>', '''
			graph {
				1[label=<<table>|</table>>]
			}
		''')
	}

	@Test def testAutoEdit011() {
		'''
			graph {
				1[label=<
					<table|
				>]
			}
		'''.testAutoEdit('>', '''
			graph {
				1[label=<
					<table>|</table>
				>]
			}
		''')
	}

	@Test def testAutoEdit012() {
		'''
			graph{1[label=<<table><tr|</table>>]}
		'''.testAutoEdit('>', '''
			graph{1[label=<<table><tr>|</tr></table>>]}
		''')
	}

	@Test def testAutoEdit013() {
		'''
			graph {
				1[label=<
					<table><tr|</table>
				>]
			}
		'''.testAutoEdit('>', '''
			graph {
				1[label=<
					<table><tr>|</tr></table>
				>]
			}
		''')
	}

	@Test def testAutoEdit014() {
		'''
			graph {
				1[label=<|]
			}
		'''.testAutoEdit('>', '''
			graph {
				1[label=<>|]
			}
		''')
	}

	@Test def testAutoEdit015() {
		'''
			digraph {
				1[label=<
					<b|
				>]
				1->2
			}
		'''.testAutoEdit('>', '''
			digraph {
				1[label=<
					<b>|</b>
				>]
				1->2
			}
		''')
	}

	@Test def testAutoEdit016() {
		'''
			graph {
				1[label=<
			<!--	comment --|
				>]
			}
		'''.testAutoEdit('>', '''
			graph {
				1[label=<
			<!--	comment -->|
				>]
			}
		''')
	}

	private def testAutoEdit(CharSequence it, char key, CharSequence newContent) {
		// given
		dslFile.
		// when
		press(key).
		// then
		dslFileHasContent(newContent)
	}

	private def dslFile(CharSequence it) {
		toString.openEditor
	}

	private def press(XtextEditor it, char c) {
		pressKey(c)
		it
	}

	private def dslFileHasContent(XtextEditor editor, CharSequence it) {
		toString.assertState(editor)
	}

	private def createTestProjectWithXtextNature() {
		"foo".createProject.addNature(XtextProjectHelper.NATURE_ID)
	}

	override protected getFileExtension() {
		primaryFileExtension
	}

	override protected getEditorId() {
		xtextEditorInfo.editorId
	}

}