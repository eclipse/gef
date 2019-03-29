/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #530699)
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.util.List
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.SubMonitor
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.jface.text.Position
import org.eclipse.jface.text.TextSelection
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.eclipse.xtext.ui.editor.occurrences.IOccurrenceComputer
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotMarkingOccurrencesTests extends AbstractEditorTest {

	@Inject extension IOccurrenceComputer
	@Inject XtextEditorInfo editorInfo

	override protected getEditorId() {
		editorInfo.editorId
	}

	@Test def marking_occurrences001() {
		val text = '''
			graph {
				1;2
				1--2
			}
		'''
		text.verifyOccurrences(text.first("1"), #[text.first("1"), text.second("1")])
	}

	@Test def marking_occurrences002() {
		val text = '''
			graph {
				1;2
				1--2
			}
		'''
		text.verifyOccurrences(text.second("1"), #[text.first("1"), text.second("1")])
	}

	@Test def marking_occurrences003() {
		val text = '''
			graph {
				1
				2
				1--2
			}
		'''
		text.verifyOccurrences(text.first("2"), #[text.first("2"), text.second("2")])
	}

	@Test def marking_occurrences004() {
		val text = '''
			graph {
				1
				2
				1--2
			}
		'''
		text.verifyOccurrences(text.second("2"), #[text.first("2"), text.second("2")])
	}

	@Test def marking_occurrences005() {
		val text = '''
			graph {
				1
				2
				4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.first("4"), #[text.first("4"), text.second("4")])
	}

	@Test def marking_occurrences006() {
		val text = '''
			graph {
				1;
				2;
				4;
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.second("4"), #[text.first("4"), text.second("4")])
	}

	@Test def marking_occurrences007() {
		val text = '''
			graph {
				1;2;4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.first("1"), #[text.first("1"), text.second("1"), text.third("1")])
	}

	@Test def marking_occurrences008() {
		val text = '''
			graph {
				1;2;4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.second("1"), #[text.first("1"), text.second("1"), text.third("1")])
	}

	@Test def marking_occurrences009() {
		val text = '''
			graph {
				1;
				2;
				4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.third("1"), #[text.first("1"), text.second("1"), text.third("1")])
	}

	private def verifyOccurrences(CharSequence content, int selectionOffset, List<Integer> expected) {
		val editor = openEditor(DotTestUtils.createTestFile(content.toString))
		val selection = new TextSelection(selectionOffset, 1)

		val annotationMap = editor.createAnnotationMap(selection, SubMonitor.convert(new NullProgressMonitor))
		assertEquals("The number of the marked occurrences does not match: ", expected.size, annotationMap.size)
		// sort the annotation map values ascending according to their offset
		val sortedAnnotationPositions = annotationMap.values.sortWith[Position o1, Position o2|o1.offset - o2.offset]
		var i = 0
		for (Position position : sortedAnnotationPositions) {
			assertEquals("The position offset does not match: ", expected.get(i), position.offset)
			assertEquals("The position length does not match: ", 1, position.length)
			i++
		}
	}

	private def first(String text, String substring) {
		text.indexOf(substring)
	}

	private def second(String text, String substring) {
		text.indexOf(substring, text.first(substring) + 1)
	}

	private def third(String text, String substring) {
		text.indexOf(substring, text.second(substring) + 1)
	}
}
