/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator
import org.eclipse.jface.text.Region
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil
import org.eclipse.xtext.ui.editor.hover.AbstractEObjectHover
import org.eclipse.xtext.ui.editor.hover.IEObjectHover
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControlInput
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotHoverTests extends AbstractEditorTest {

	@Inject IEObjectHover eObjectHover

	@Test
	def void edge_color() {
		val text = '''
			digraph {
				1->2[
					color=green4
				]
			}
		'''
		
		val textUnderHover = "green4"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	@Test
	def void edge_color_custom_global_color_scheme() {
		val text = '''
			digraph {
				a->b[
					colorscheme=blues3
					color=2
				]
			}
		'''
		
		val textUnderHover = "2"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	@Test
	def void edge_color_custom_local_color_scheme() {
		val text = '''
			digraph {
				a->b[
					color="/blues3/2"
				]
			}
		'''
		
		val textUnderHover = "2"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void edge_color_rgb_value() {
		val text = '''
			digraph {
				a->b[
					color="#fdc086"
				]
			}
		'''
		
		val textUnderHover = "#fdc086"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void edge_fillcolor() {
		val text = '''
			digraph {
				1->2[
					fillcolor=blueviolet
				]
			}
		'''
		
		val textUnderHover = "blueviolet"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void edge_fontcolor() {
		val text = '''
			digraph {
				1->2[
					label=edgeLabel
					fontcolor=bisque2
				]
			}
		'''
		
		val textUnderHover = "bisque2"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	@Test
	def void edge_labelfontcolor() {
		val text = '''
			digraph {
				1->2[
					headlabel=edgeHeadLabel
					taillabel=edgeTailLabel
					labelfontcolor=cadetblue
				]
			}
		'''
		
		val textUnderHover = "cadetblue"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void graph_bgcolor() {
		val text = '''
			digraph {
				bgcolor=darkgoldenrod2
				1
			}
		'''
		
		val textUnderHover = "darkgoldenrod2"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void graph_fontcolor() {
		val text = '''
			digraph {
				fontcolor=firebrick
				label=graphLabel
				1
			}
		'''
		
		val textUnderHover = "firebrick"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void node_color() {
		val text = '''
			digraph {
				1[color=aquamarine4]
			}
		'''
		
		val textUnderHover = "aquamarine4"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}

	@Test
	def void node_fillcolor() {
		val text = '''
			digraph {
				1[
					fillcolor=yellow
					style=filled
				]
			}
		'''
		
		val textUnderHover = "yellow"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	@Test
	def void node_fontcolor() {
		val text = '''
			digraph {
				1[
					fontcolor=goldenrod
				]
			}
		'''
		
		val textUnderHover = "goldenrod"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	@Test
	def void cluster_color() {
		val text = '''
			digraph {
				1
				subgraph cluster1{
					2
					color=honeydew3
				}
			}
		'''
		
		val textUnderHover = "honeydew3"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	@Test
	def void cluster_fillcolor() {
		val text = '''
			digraph {
				1
				subgraph cluster1{
					2
					fillcolor=indigo
					style=filled
				}
			}
		'''
		
		val textUnderHover = "indigo"
		
		val expected = '''
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
		'''
		
		text.assertHoveringResult(textUnderHover, expected)
	}
	
	override protected getEditorId() {
		DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT
	}
	
	private def assertHoveringResult(String text, String textUnderHover, String expected){
		val file = IResourcesSetupUtil.createFile("test/test.dot", text)
		val editor = openEditor(file)

		val viewer = editor.internalSourceViewer

		val region = new Region(text.indexOf(textUnderHover),
				textUnderHover.length())

		val hoverInfo = (eObjectHover as AbstractEObjectHover).getHoverInfo2(viewer, region) as XtextBrowserInformationControlInput

		val actual = hoverInfo.html.table
		
		expected.assertEquals(actual)
	}
	
	private def table(String html) {
		val beginIndex = html.indexOf("<table")
		val endIndex = html.indexOf("</body>")
		html.substring(beginIndex, endIndex)
	}
}
