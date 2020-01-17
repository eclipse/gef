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
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
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
class DotAutoEditTest extends AbstractAutoEditTest {

	@Inject XtextEditorInfo xtextEditorInfo
	@Inject extension FileExtensionProvider

	override setUp() {
		super.setUp
		createTestProjectWithXtextNature
	}

	@Test def autoedit_001() {
		'''
			graph |
		'''.testAutoEdit('{', '''
			graph {|}
		''')
	}

	@Test def autoedit_002() {
		'''
			digraph |
		'''.testAutoEdit('{', '''
			digraph {|}
		''')
	}

	@Test def autoedit_003() {
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

	@Test def autoedit_004() {
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

	@Test def autoedit_005() {
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

	@Test def autoedit_006() {
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

	@Test def autoedit_007() {
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

	@Test def autoedit_008() {
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

	@Test def autoedit_009() {
		'''
			graph {1[label=<<table|>]}
		'''.testAutoEdit('>', '''
			graph {1[label=<<table>|</table>>]}
		''')
	}

	@Test def autoedit_010() {
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

	@Test def autoedit_011() {
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

	@Test def autoedit_012() {
		'''
			graph{1[label=<<table><tr|</table>>]}
		'''.testAutoEdit('>', '''
			graph{1[label=<<table><tr>|</tr></table>>]}
		''')
	}

	@Test def autoedit_013() {
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

	@Test def autoedit_014() {
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

	@Test def autoedit_015() {
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

	@Test def autoedit_016() {
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