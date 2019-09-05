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
 *     Zoey Prigge (itemis AG)    - implement mark occurrence support for attributes (bug #548911)
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
class DotMarkingOccurrencesTest extends AbstractEditorTest {

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
		text.verifyOccurrences("1".occurrence(1), #["1".occurrence(1), "1".occurrence(2)])
	}

	@Test def marking_occurrences002() {
		val text = '''
			graph {
				1;2
				1--2
			}
		'''
		text.verifyOccurrences("1".occurrence(2), #["1".occurrence(1), "1".occurrence(2)])
	}

	@Test def marking_occurrences003() {
		val text = '''
			graph {
				1
				2
				1--2
			}
		'''
		text.verifyOccurrences("2".occurrence(1), #["2".occurrence(1), "2".occurrence(2)])
	}

	@Test def marking_occurrences004() {
		val text = '''
			graph {
				1
				2
				1--2
			}
		'''
		text.verifyOccurrences("2".occurrence(2), #["2".occurrence(1), "2".occurrence(2)])
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
		text.verifyOccurrences("4".occurrence(1), #["4".occurrence(1), "4".occurrence(2)])
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
		text.verifyOccurrences("4".occurrence(2), #["4".occurrence(1), "4".occurrence(2)])
	}

	@Test def marking_occurrences007() {
		val text = '''
			graph {
				1;2;4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences("1".occurrence(1), #["1".occurrence(1), "1".occurrence(2), "1".occurrence(3)])
	}

	@Test def marking_occurrences008() {
		val text = '''
			graph {
				1;2;4
				1--2
				1--4
			}
		'''
		text.verifyOccurrences("1".occurrence(2), #["1".occurrence(1), "1".occurrence(2), "1".occurrence(3)])
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
		text.verifyOccurrences("1".occurrence(3), #["1".occurrence(1), "1".occurrence(2), "1".occurrence(3)])
	}

	@Test def marking_occurrences010() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label="", headlabel="", fontname="Times-Bold", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				4->5 [label=Test, fontname="Helvetica-Bold"];
			}
		'''
		text.verifyOccurrences("\"Helvetica\"".occurrence(3), #["\"Helvetica\"".occurrence(1), "\"Helvetica\"".occurrence(2), "\"Helvetica\"".occurrence(3), "\"Helvetica\"".occurrence(4)])
	}

	@Test def marking_occurrences011() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("10".occurrence(2), #["10".occurrence(1), "10".occurrence(2), "10".occurrence(3), "10.0".occurrence(1)])
	}

	@Test def marking_occurrences012() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("\"dotted, solid, bold\"".occurrence(1), #["\"dotted, solid, bold\"".occurrence(1), "\"dotted,solid,bold\"".occurrence(1)])
	}

	@Test def marking_occurrences013() {
		//black as color
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("black".occurrence(1), #["black".occurrence(1), "\"black\"".occurrence(2)])
	}

	@Test def marking_occurrences014() {
		//black as colorlist
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("\"black\"".occurrence(1), #["\"black\"".occurrence(1), "\"black\"".occurrence(3)])
	}

	@Test def marking_occurrences015() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("fontname".occurrence(1), #["fontname".occurrence(1), "fontname".occurrence(3), "fontname".occurrence(4), "fontname".occurrence(5)])
	}

	@Test def marking_occurrences016() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("labelfontname".occurrence(1), #["labelfontname".occurrence(1)])
	}

	@Test def marking_occurrences017() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("taillabel".occurrence(1), #["taillabel".occurrence(1), "taillabel".occurrence(2)])
	}

	@Test def marking_occurrences018() {
		val text = '''
			digraph G {
				edge [fontname="Helvetica",fontsize=10,labelfontname="Helvetica",labelfontsize=10];
				node [fontname="Helvetica",fontsize=10,shape=plaintext];
				1->2 [taillabel="", label="", headlabel="", fontname="Helvetica", fontcolor=black, fontsize=10.0, color="black", arrowhead=open, style="dotted, solid, bold"];
				2->3 [taillabel="", label=Test, headlabel="", fontname="Times-Bold", fontcolor="black", fontsize=150.0, color="black", arrowhead=open, style="dotted,solid,bold"];
			}
		'''
		text.verifyOccurrences("labelfontname".occurrence(1), #["labelfontname".occurrence(1)])
	}

	private def verifyOccurrences(String content, Pair<String, Integer> selectionOffset, List<Pair<String, Integer>> expected) {
		val editor = openEditor(DotTestUtils.createTestFile(content))
		val selection = new TextSelection(content.nthOccurrence(selectionOffset.key,selectionOffset.value), selectionOffset.key.length)

		val annotationMap = editor.createAnnotationMap(selection, SubMonitor.convert(new NullProgressMonitor))
		assertEquals("The number of the marked occurrences does not match: ", expected.size, annotationMap.size)
		// sort the annotation map values ascending according to their offset
		val sortedAnnotationPositions = annotationMap.values.sortWith[Position o1, Position o2|o1.offset - o2.offset]
		var i = 0
		for (Position position : sortedAnnotationPositions) {
			assertEquals("The position offset does not match: ", content.nthOccurrence(expected.get(i).key, expected.get(i).value), position.offset)
			assertEquals("The position length does not match: ", expected.get(i).key.length, position.length)
			i++
		}
	}

	private def int nthOccurrence(String text, String substring, int n) {
		if (n > 1) {
			text.indexOf(substring, text.nthOccurrence(substring, n-1) + 1);
		} else {
			text.indexOf(substring)
		}
	}

	private def occurrence(String string, int number) {
		return new Pair<String, Integer>(string, number);
	}
}
