/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     Zoey Prigge    (itemis AG) - html like label hovering (bug #549412)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.lang.reflect.Method
import org.eclipse.core.resources.IFile
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.jface.text.ITextHover
import org.eclipse.jface.text.ITextHoverExtension2
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.eclipse.xtext.ui.editor.XtextSourceViewer
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControlInput
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotHoverTest extends AbstractEditorTest {

	@Inject XtextEditorInfo editorInfo

	@Test def edge_color() {
		'''
			digraph {
				1->2[
					color=green4
				]
			}
		'''.assertHoveringResult("green4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#008b00;"</div></td>
					<td align="center">x11</td>
					<td align="center">green4</td>
					<td align="center">#008b00</td>
				</tr>
			</table>
		''')
	}

	@Test def edge_color_custom_global_color_scheme() {
		'''
			digraph {
				a->b[
					colorscheme=blues3
					color=2
				]
			}
		'''.assertHoveringResult("2", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#9ecae1;"</div></td>
					<td align="center">blues3</td>
					<td align="center">2</td>
					<td align="center">#9ecae1</td>
				</tr>
			</table>
		''')
	}

	@Test def edge_color_custom_local_color_scheme() {
		'''
			digraph {
				a->b[
					color="/blues3/2"
				]
			}
		'''.assertHoveringResult("2", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#9ecae1;"</div></td>
					<td align="center">blues3</td>
					<td align="center">2</td>
					<td align="center">#9ecae1</td>
				</tr>
			</table>
		''')
	}

	@Test def edge_color_rgb_value() {
		'''
			digraph {
				a->b[
					color="#fdc086"
				]
			}
		'''.assertHoveringResult("#fdc086", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#fdc086;"</div></td>
					<td align="center">#fdc086</td>
				</tr>
			</table>
		''')
	}

	@Test def edge_fillcolor() {
		'''
			digraph {
				1->2[
					fillcolor=blueviolet
				]
			}
		'''.assertHoveringResult("blueviolet", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#8a2be2;"</div></td>
					<td align="center">x11</td>
					<td align="center">blueviolet</td>
					<td align="center">#8a2be2</td>
				</tr>
			</table>
		''')
	}

	@Test def edge_fontcolor() {
		'''
			digraph {
				1->2[
					label=edgeLabel
					fontcolor=bisque2
				]
			}
		'''.assertHoveringResult("bisque2", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#eed5b7;"</div></td>
					<td align="center">x11</td>
					<td align="center">bisque2</td>
					<td align="center">#eed5b7</td>
				</tr>
			</table>
		''')
	}

	@Test def edge_labelfontcolor() {
		'''
			digraph {
				1->2[
					headlabel=edgeHeadLabel
					taillabel=edgeTailLabel
					labelfontcolor=cadetblue
				]
			}
		'''.assertHoveringResult("cadetblue", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#5f9ea0;"</div></td>
					<td align="center">x11</td>
					<td align="center">cadetblue</td>
					<td align="center">#5f9ea0</td>
				</tr>
			</table>
		''')
	}

	@Test def graph_bgcolor() {
		'''
			digraph {
				bgcolor=darkgoldenrod2
				1
			}
		'''.assertHoveringResult("darkgoldenrod2", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#eead0e;"</div></td>
					<td align="center">x11</td>
					<td align="center">darkgoldenrod2</td>
					<td align="center">#eead0e</td>
				</tr>
			</table>
		''')
	}

	@Test def graph_fontcolor() {
		'''
			digraph {
				fontcolor=firebrick
				label=graphLabel
				1
			}
		'''.assertHoveringResult("firebrick", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#b22222;"</div></td>
					<td align="center">x11</td>
					<td align="center">firebrick</td>
					<td align="center">#b22222</td>
				</tr>
			</table>
		''')
	}

	@Test def node_color() {
		'''
			digraph {
				1[color=aquamarine4]
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	@Test def node_fillcolor() {
		'''
			digraph {
				1[
					fillcolor=yellow
					style=filled
				]
			}
		'''.assertHoveringResult("yellow", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#ffff00;"</div></td>
					<td align="center">x11</td>
					<td align="center">yellow</td>
					<td align="center">#ffff00</td>
				</tr>
			</table>
		''')
	}

	@Test def node_fontcolor() {
		'''
			digraph {
				1[
					fontcolor=goldenrod
				]
			}
		'''.assertHoveringResult("goldenrod", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#daa520;"</div></td>
					<td align="center">x11</td>
					<td align="center">goldenrod</td>
					<td align="center">#daa520</td>
				</tr>
			</table>
		''')
	}

	@Test def cluster_color() {
		'''
			digraph {
				1
				subgraph cluster1{
					2
					color=honeydew3
				}
			}
		'''.assertHoveringResult("honeydew3", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#c1cdc1;"</div></td>
					<td align="center">x11</td>
					<td align="center">honeydew3</td>
					<td align="center">#c1cdc1</td>
				</tr>
			</table>
		''')
	}

	@Test def cluster_fillcolor() {
		'''
			digraph {
				1
				subgraph cluster1{
					2
					fillcolor=indigo
					style=filled
				}
			}
		'''.assertHoveringResult("indigo",  '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#4b0082;"</div></td>
					<td align="center">x11</td>
					<td align="center">indigo</td>
					<td align="center">#4b0082</td>
				</tr>
			</table>
		''')
	}

	@Test def cluster_bgcolor() {
		'''
			graph {
				subgraph cluster_0 {
					colorscheme=accent3 bgcolor=2
					1
				}
			}
		'''.assertHoveringResult("2",  '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#beaed4;"</div></td>
					<td align="center">accent3</td>
					<td align="center">2</td>
					<td align="center">#beaed4</td>
				</tr>
			</table>
		''')
	}

	@Test def html_font_color() {
		'''
			digraph {
				1[label=<<font color="aquamarine4"></font>>]
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	@Test def html_font_colorscheme() {
		'''
			digraph {
				1[colorscheme=svg,label=<<font color="aqua"></font>>]
			}
		'''.assertHoveringResult("aqua", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#00ffff;"</div></td>
					<td align="center">svg</td>
					<td align="center">aqua</td>
					<td align="center">#00ffff</td>
				</tr>
			</table>
		''')
	}

	@Test def html_font_colorscheme_notfound() {
		'''
			digraph {
				1[label=<<font color="aqua"></font>>]
			}
		'''.assertHoveringResult("aqua", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:null;"</div></td>
					<td align="center">x11</td>
					<td align="center">aqua</td>
					<td align="center">null</td>
				</tr>
			</table>
		''')
	}

	@Test def html_table_color() {
		'''
			digraph {
				1[label=<<table color="green"><tr><td>foo</td></tr></table>>]
			}
		'''.assertHoveringResult("green", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#00ff00;"</div></td>
					<td align="center">x11</td>
					<td align="center">green</td>
					<td align="center">#00ff00</td>
				</tr>
			</table>
		''')
	}

	@Test def html_table_bgcolor() {
		'''
			digraph {
				1[label=<<table bgcolor="red"><tr><td>foo</td></tr></table>>]
			}
		'''.assertHoveringResult("red", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#ff0000;"</div></td>
					<td align="center">x11</td>
					<td align="center">red</td>
					<td align="center">#ff0000</td>
				</tr>
			</table>
		''')
	}

	@Test def html_td_color() {
		'''
			digraph {
				1[label=<<table><tr><td color="blue">foo</td></tr></table>>]
			}
		'''.assertHoveringResult("blue", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#0000ff;"</div></td>
					<td align="center">x11</td>
					<td align="center">blue</td>
					<td align="center">#0000ff</td>
				</tr>
			</table>
		''')
	}

	@Test def html_td_bgcolor() {
		'''
			digraph {
				1[label=<<table><tr><td bgcolor="pink">foo</td></tr></table>>]
			}
		'''.assertHoveringResult("pink", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#ffc0cb;"</div></td>
					<td align="center">x11</td>
					<td align="center">pink</td>
					<td align="center">#ffc0cb</td>
				</tr>
			</table>
		''')
	}

	@Test def html_edge_taillabel() {
		'''
			digraph {
				1->2[taillabel=<<font color="aquamarine4"></font>>]
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	@Test def html_edge_headlabel() {
		'''
			digraph {
				1->2[headlabel=<<font color="aquamarine4"></font>>]
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	@Test def html_edge_xlabel() {
		'''
			digraph {
				1->2[xlabel=<<font color="aquamarine4"></font>>]
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	@Test def html_node_xlabel() {
		'''
			digraph {
				1[xlabel=<<font color="aquamarine4"></font>>]
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	@Test def html_cluster_label() {
		'''
			graph {
				subgraph cluster_0 {
					label=<<font color="aquamarine4"></font>>
					1
				}
			}
		'''.assertHoveringResult("aquamarine4", '''
			<table border=1>
				<tr>
					<td><b>color preview</b></td>
					<td><b>color scheme</b></td>
					<td><b>color name</b></td>
					<td><b>color code</b></td>
				</tr>
				<tr>
					<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:#458b74;"</div></td>
					<td align="center">x11</td>
					<td align="center">aquamarine4</td>
					<td align="center">#458b74</td>
				</tr>
			</table>
		''')
	}

	private def assertHoveringResult(CharSequence it, String textUnderHover, String expected) {
		// given
		dslFile.
		// when
		hoveringOver(textUnderHover).
		// then
		hoverPopupHasContent(expected)
	}

	private def dslFile(CharSequence it) {
		toString.createTestFile
	}

	private def hoveringOver(IFile testFile, String textUnderHover) {
		val editor = testFile.openEditor
		val viewer = editor.internalSourceViewer as XtextSourceViewer
		val offset = viewer.document.get.indexOf(textUnderHover) + textUnderHover.length/2;
		//we need to access text hover set in SourceViewerConfiguration
		val hover = viewer.getTextHover(offset)
		val region = hover.getHoverRegion(viewer, offset)
		val hoverInfo = (hover as ITextHoverExtension2).getHoverInfo2(viewer, region)
		hoverInfo as XtextBrowserInformationControlInput
	}

	private def hoverPopupHasContent(XtextBrowserInformationControlInput hoverInfo, String expected) {
		val actual = hoverInfo.html.table
		expected.assertEquals(actual)
	}

	private def table(String html) {
		val beginIndex = html.indexOf("<table")
		val endIndex = html.indexOf("</body>")
		html.substring(beginIndex, endIndex)
	}

	override protected getEditorId() {
		editorInfo.editorId
	}

	/*
	 * SourceViewer getTextHover(int offset) is protected, 
	 * hence for testing purposes we have to use reflection for access,
	 * as the method is accessible to subclasses, it is unlikely to be removed from API,
	 * so this should be safe.
	 */
	private def getTextHover(XtextSourceViewer viewer, int offset) {
		val getTextHover = XtextSourceViewer.methodByName("getTextHover", int);
		getTextHover.invoke(viewer, offset) as ITextHover
	}

	private def Method methodByName(Class<?> strategyClass, String name, Class<?>... args) {
		var Method method;
		try {
			method = strategyClass.getDeclaredMethod(name, args);
		} catch (NoSuchMethodException e) {
			var Class<?> superClass = strategyClass.getSuperclass();
			if (superClass !== null)
				return methodByName(strategyClass.getSuperclass(), name, args);
			return null;
		} catch (SecurityException e) {
			fail(e.toString)
		}
		method.setAccessible(true);
		return method;
	}
}
