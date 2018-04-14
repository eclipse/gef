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
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator
import org.eclipse.jface.text.ITextSelection
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.doubleClicking.LexerTokenAndCharacterPairAwareStrategy
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile
import org.junit.Ignore

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotEditorDoubleClickingTests extends AbstractEditorTest {
	
	@Inject extension LexerTokenAndCharacterPairAwareStrategy
	
	// symbols indicating the current cursor position
	val c = '''<|>'''
	
	override protected getEditorId() {
		DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
	}

	@Test def void empty_graph() {
		'''
			grap«c»h {
			}
		'''.assertTextSelectedAfterDoubleClicking('''graph''')
	}
	
	@Test def void empty_digraph() {
		'''
			d«c»igraph {
			}
		'''.assertTextSelectedAfterDoubleClicking('''digraph''')
	}
	
	@Ignore("activate as soon as solution for bug #532244 has been implemented")	
	@Test def void node_html_label_001() {
		'''
			graph {
				1[label=<
					<table>
						<tr>
							<td align="center">Cate«c»gory</td>
						</tr>
					</table>
				>]
			}
		'''.assertTextSelectedAfterDoubleClicking('''Category''')
	}
	
	@Ignore("activate as soon as solution for bug #532244 has been implemented")
	@Test def void node_record_label_001() {
		'''
			graph{
				1[shape=record label=" text1 | text«c»2 "]
			}
		'''.assertTextSelectedAfterDoubleClicking('''text2''')
	}
	
	@Ignore("activate as soon as solution for bug #532244 has been implemented")
	@Test def void node_style_001() {
		'''
			graph{
				1[style=" bo«c»ld, dotted "]
			}
		'''.assertTextSelectedAfterDoubleClicking('''bold''')
	}
	
	@Ignore("activate as soon as solution for bug #532244 has been implemented")
	@Test def void node_style_002() {
		'''
			graph{
				1[style=" bold, dot«c»ted "]
			}
		'''.assertTextSelectedAfterDoubleClicking('''dotted''')
	}
	
	/**
	  * @param it The text representing the input dot content.
	  * 	The text should contain special symbols indicating the current cursor position.
	  * 
	  * @param expected The text that is expected to be selected after double clicking.
	  */
	def private void assertTextSelectedAfterDoubleClicking(CharSequence it, String expected) {
		
		content.openDotEditor
		
		.doubleClick(cursorPosition)
		
		.assertSelectedText(expected)
	}

	private def getContent(CharSequence text) {
		text.toString.replace(c, "")
	}
	
	private def openDotEditor(String content){
		var XtextEditor editor = null
		try {
			editor = content.createTestFile.openEditor
		} catch (Exception e) {
			e.printStackTrace
			fail(e.message)
		}
		editor
	}
	
	private def int getCursorPosition(CharSequence text) {
		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition==-1){
			fail('''
				The input text
				«text»
				should contain the «c» symbols to indicating the current cursor position!
			''')
		}
		cursorPosition
	}
	
	private def doubleClick(XtextEditor dotEditor, int cursorPosition) {
		val viewer = dotEditor.internalSourceViewer
		
		// set the cursor position		
		viewer.setSelectedRange(cursorPosition, 0)
		
		// double click on the cursor position
		viewer.doubleClicked
		
		dotEditor
	}
	
	private def assertSelectedText(XtextEditor dotEditor, CharSequence expectedSelectedText) {
		val actualSelectedText = (dotEditor.selectionProvider.selection as ITextSelection).text
		expectedSelectedText.assertEquals(actualSelectedText)
	}
}