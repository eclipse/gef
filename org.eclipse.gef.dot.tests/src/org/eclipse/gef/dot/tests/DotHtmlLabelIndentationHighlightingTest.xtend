/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.util.List
import org.eclipse.core.resources.IFile
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.text.IRegion
import org.eclipse.jface.text.Region
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Display
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.testing.AbstractHighlightingTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotHtmlLabelIndentationHighlightingTest extends AbstractHighlightingTest {

	// position marker
	val c = '''<|>'''

	List<IRegion> highlightingRegions = null

	@Test def test001(){
		'''
			graph {
				1 [label = <
				«c»		Text
					>]
			}
		'''.testHtmlIndentationHighlighting
	}

	@Test def test002(){
		'''
			graph {
				1 [
					label = <
					«c»	Text
					>]
			}
		'''.testHtmlIndentationHighlighting
	}

	@Test def test003(){
		'''
			graph {
				1 [label=
					<
					«c»	Text
					>]
			}
		'''.testHtmlIndentationHighlighting
	}

	@Test def test004(){
		'''
			graph {
				1 [label=
					<
					«c»		Text
					>]
			}
		'''.testHtmlIndentationHighlighting
	}

	@Test def test005(){
		'''
			graph {
				1 [label=
					<
					«c»	<TABLE>
					«c»		<TR>
					«c»			<TD>Text</TD>
					«c»		</TR>
					«c»	</TABLE>
					>]
			}
		'''.testHtmlIndentationHighlighting
	}

	@Test def test006(){
		'''
			graph {
				1 [label=
					<
					«c»		<TABLE>
					«c»			<TR>
					«c»				<TD>Text</TD>
					«c»			</TR>
					«c»		</TABLE>
					>]
			}
		'''.testHtmlIndentationHighlighting
	}

	private def testHtmlIndentationHighlighting(CharSequence text) {
		text.calculateHighlightingRegions
		val content = text.toString.replace(c, "")
		testHighlighting(content, null, SWT.NORMAL, 0, 0, 0, 220, 220, 220)
	}

	protected override testHighlighting(StyledText styledText, String text, int fontStyle,
		int foregroundR, int foregroundG, int foregroundB, 
		int backgroundR, int backgroundG, int backgroundB) {

		val expectedForegroundColor = new Color(null, foregroundR, foregroundG, foregroundB)
		val expectedBackgroundColor = new Color(null, backgroundR, backgroundG, backgroundB)

		for(highlightingRegion : highlightingRegions) {
			val offset = highlightingRegion.offset
			
			for (var i = 0; i < highlightingRegion.length; i++) {
				val currentPosition = offset + i
				val character = styledText.getTextRange(currentPosition, 1)
				val styleRange = styledText.getStyleRangeAtOffset(currentPosition)
				if (character.isRelevant) {
					styleRange => [
						assertFontStyle(character, fontStyle)
						assertForegroundColor(character, expectedForegroundColor)
						assertBackgroundColor(character, expectedBackgroundColor)
					]
				} else {
					Assert.fail("Non relevant character '" + character + "' found!")
				}
			}
		}
	}

	override protected isRelevant(String character) {
		// consider only whitespace characters
		Character.isWhitespace(character.charAt(0))
	}

	private def void calculateHighlightingRegions(CharSequence input) {
		highlightingRegions = newArrayList

		val text = input.toString

		var fromIndex = 0

		while (fromIndex < text.lastIndexOf(c)) {
			val first = text.indexOf(c, fromIndex)

			if (first==-1) {
				fail('''Can't locate the first position symbol '«c»' in the input text''')
			}

			val second = text.getIndexOfFirstNonWhitespaceCharacter(first+c.length)
			if (second==-1) {
				fail('''Can't locate non-whitespace characters after the position symbol '«c»' in the input text''')
			}

			val offset = first - (highlightingRegions.length/*-1*/)*c.length
			val length = second - first - c.length
			val highlightingRegion = new Region(offset, length)
			highlightingRegions.add(highlightingRegion)

			fromIndex = second + c.length
		}
	}

	private def int getIndexOfFirstNonWhitespaceCharacter(String text, int fromIndex) {
		for(var i=fromIndex; i<text.length; i++) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return i
			}
		}
		return -1
	}

	/**
	 * This derived class includes the fix
	 * https://github.com/eclipse/xtext-eclipse/commit/9c63ca2ed24050d4fa9fba431f7442f0e903925a
	 * available from Xtext Version 2.18 (Eclipse 2019-06).
	 * TODO: remove this workaround as soon as Eclipse 2019-06 will be the minimum platform supported by GEF DOT.
	 */
	protected override openInEditor(IFile dslFile) {
		val editor = dslFile.openEditor

		waitForEventProcessingWorkaround

		editor.internalSourceViewer.textWidget
	}

	protected def waitForEventProcessingWorkaround() {
		while (Display.^default.readAndDispatch) { }
	}

}