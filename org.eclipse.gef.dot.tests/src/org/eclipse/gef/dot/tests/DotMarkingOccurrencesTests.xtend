/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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
import java.util.Collections
import java.util.List
import java.util.Map
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.SubMonitor
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator
import org.eclipse.jface.text.Position
import org.eclipse.jface.text.TextSelection
import org.eclipse.jface.text.source.Annotation
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.editor.occurrences.IOccurrenceComputer
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotMarkingOccurrencesTests extends AbstractEditorTest {

	@Inject extension IOccurrenceComputer

	override String getEditorId() {
		DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
	}

	@Test def void testMarkingOccurrences01() {
		val text = '''
			graph {
				1;2
				1--2
			}
		'''
		text.verifyOccurrences(text.first("1"), #[text.second("1")])
	}

	@Test def void testMarkingOccurrences02() {
		val text = '''
			graph {
				1;2
				1--2
			}
		'''
		text.verifyOccurrences(text.second("1"), #[text.first("1")])
	}

	@Test def void testMarkingOccurrences03() {
		val text = '''
			graph {
				1
				2
				1--2
			}
		'''
		text.verifyOccurrences(text.first("2"), #[text.second("2")])
	}

	@Test def void testMarkingOccurrences04() {
		val text = '''
			graph {
				1
				2
				1--2
			}
		'''
		text.verifyOccurrences(text.second("2"), #[text.first("2")])
	}

	@Test def void testMarkingOccurrences05() {
		val text = '''
			graph {
				1
				2
				4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.first("4"), #[text.second("4")])
	}

	@Test def void testMarkingOccurrences06() {
		val text = '''
			graph {
				1;
				2;
				4;
				1--2
				1--4
			}
		'''
		text.verifyOccurrences( text.second("4"), #[text.first("4")])
	}

	@Test def void testMarkingOccurrences07() {
		val text = '''
			graph {
				1;2;4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.first("1"), #[text.second("1"), text.third("1")])
	}

	@Test def void testMarkingOccurrences08() {
		val text = '''
			graph {
				1;2;4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.second("1"), #[text.first("1"), text.third("1")])
	}

	@Test def void testMarkingOccurrences09() {
		val text = '''
			graph {
				1;
				2;
				4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences(text.third("1"), #[text.first("1"), text.second("1")])
	}

	def private void verifyOccurrences(CharSequence content, int selectionOffset, List<Integer> expected) {
		var XtextEditor editor = null
		try {
			editor = openEditor(DotTestUtils.createTestFile(content.toString))
		} catch (Exception e) {
			e.printStackTrace
			fail(e.message)
		}

		val selection = new TextSelection(selectionOffset, 1)
		val annotationMap = editor.createAnnotationMap(selection, SubMonitor.convert(new NullProgressMonitor))
		assertEquals("The number of the marked occurrences does not match: ", expected.size, annotationMap.size)
		// sort the annotation map values ascending according to their offset
		val sortedAnnotationPositions = sortAnnotationMap(annotationMap)
		var i = 0
		for (Position position : sortedAnnotationPositions) {
			assertEquals("The position offset does not match: ", expected.get(i), position.offset)
			assertEquals("The position length does not match: ", 1, position.length)
			i++
		}
	}

	def private List<Position> sortAnnotationMap(Map<Annotation, Position> annotationMap) {
		val List<Position> sortedAnnotationPositions = newArrayList(annotationMap.values)
		Collections.sort(sortedAnnotationPositions, [Position o1, Position o2|o1.offset - o2.offset])
		sortedAnnotationPositions
	}
	
	def private first(String text, String substring){
		text.indexOf(substring)
	}	
	
	def private second(String text, String substring){
		text.indexOf(substring, text.first(substring) + 1)
	}
	
	def private third(String text, String substring){
		text.indexOf(substring, text.second(substring) + 1)
	}	
}
