/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation
 *     Zoey Prigge (itemis AG)    - Add cluster content assist support (bug #547639)
 *                                - Implement subgraph template proposals (bug #547841)
 *                                - Add ca support for html color attrs (bug #553575)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.util.List
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.color.DotColors
import org.eclipse.gef.dot.internal.ui.language.contentassist.DotProposalProvider
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.jface.text.templates.TemplateProposal
import org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.ui.IImageHelper
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail

import static extension org.junit.Assert.assertEquals

/**
 * Test cases for the {@link ConfigurableCompletionProposal} and
 * {@link TemplateProposal} of the {@link DotProposalProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotContentAssist2Test extends AbstractContentAssistTest {

	@Inject extension IImageHelper

	static val String IMAGE_DEFAULT = null
	static val IMAGE_ATTRIBUTE = "attribute.png"
	static val IMAGE_ATTRIBUTES = "attributes.png"
	static val IMAGE_EDGE = "edge.png"
	static val IMAGE_GRAPH = "graph.png"
	static val IMAGE_HTML_TAG = "html_tag.png"
	static val IMAGE_SUBGRAPH = "subgraph.png"

	// cursor position marker
	val c = '''<|>'''

	@BeforeClass def static void initializeStatusHandlerRegistry() {
		/**
		 * Initialize the
		 * {@link org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry}
		 * before executing the test cases, otherwise it will be initialized
		 * after the test case executions resulting in a NullPointerException.
		 * For more information, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=460996
		 */
		StatusHandlerRegistry.^default
	}

	@Test def empty() {
		'''
			«c»
		'''.testContentAssistant(#[
				new CompletionProposal("digraph", "digraph", IMAGE_GRAPH),
				new CompletionProposal("graph", "graph", IMAGE_GRAPH),
				new CompletionProposal("strict", "strict", IMAGE_DEFAULT),
				new CompletionProposal("digraph - Insert a template", '''
					digraph {
						
					}''', IMAGE_DEFAULT),
				new CompletionProposal("graph - Insert a template", '''
					graph {
						
					}''', IMAGE_DEFAULT)
			]
		)
	}

	@Test def graph001() {
		'''
			«c»graph {
				
			}
		'''.testContentAssistant(#[
				new CompletionProposal("digraph", "digraph", IMAGE_GRAPH),
				new CompletionProposal("graph", "graph", IMAGE_GRAPH),
				new CompletionProposal("strict", "strict", IMAGE_DEFAULT),
				new CompletionProposal("digraph - Insert a template", '''
					digraph {
						
					}''', IMAGE_DEFAULT),
				new CompletionProposal("graph - Insert a template", '''
					graph {
						
					}''', IMAGE_DEFAULT)
			]
		)
	}

	@Test def graph002() {
		'''
			graph {
				
			}
			«c»
		'''.testContentAssistant(#[
				new CompletionProposal("digraph", "digraph", IMAGE_GRAPH),
				new CompletionProposal("graph", "graph", IMAGE_GRAPH),
				new CompletionProposal("strict", "strict", IMAGE_DEFAULT),
				new CompletionProposal("digraph - Insert a template", '''
					digraph {
						
					}''', IMAGE_DEFAULT),
				new CompletionProposal("graph - Insert a template", '''
					graph {
						
					}''', IMAGE_DEFAULT)
			]
		)
	}

	@Test def digraph() {
		'''
			digraph {
				«c»
			}
		'''.testContentAssistant(#[
			new CompletionProposal("bb: Attribute", "bb=", IMAGE_ATTRIBUTE),
			new CompletionProposal("bgcolor: Attribute", "bgcolor=", IMAGE_ATTRIBUTE),
			new CompletionProposal("clusterrank: Attribute", "clusterrank=", IMAGE_ATTRIBUTE),
			new CompletionProposal("colorscheme: Attribute", "colorscheme=", IMAGE_ATTRIBUTE),
			new CompletionProposal("fontcolor: Attribute", "fontcolor=", IMAGE_ATTRIBUTE),
			new CompletionProposal("fontname: Attribute", "fontname=", IMAGE_ATTRIBUTE),
			new CompletionProposal("fontsize: Attribute", "fontsize=", IMAGE_ATTRIBUTE),
			new CompletionProposal("forcelabels: Attribute", "forcelabels=", IMAGE_ATTRIBUTE),
			new CompletionProposal("id: Attribute", "id=", IMAGE_ATTRIBUTE),
			new CompletionProposal("label: Attribute", "label=", IMAGE_ATTRIBUTE),
			new CompletionProposal("layout: Attribute", "layout=", IMAGE_ATTRIBUTE),
			new CompletionProposal("lp: Attribute", "lp=", IMAGE_ATTRIBUTE),
			new CompletionProposal("nodesep: Attribute", "nodesep=", IMAGE_ATTRIBUTE),
			new CompletionProposal("outputorder: Attribute", "outputorder=", IMAGE_ATTRIBUTE),
			new CompletionProposal("pagedir: Attribute", "pagedir=", IMAGE_ATTRIBUTE),
			new CompletionProposal("rankdir: Attribute", "rankdir=", IMAGE_ATTRIBUTE),
			new CompletionProposal("splines: Attribute", "splines=", IMAGE_ATTRIBUTE),
			new CompletionProposal("style: Attribute", "style=", IMAGE_ATTRIBUTE),
			new CompletionProposal("edge[]: Attributes", "edge[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("graph[]: Attributes", "graph[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("node[]: Attributes", "node[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("subgraph: Subgraph", "subgraph", IMAGE_SUBGRAPH),
			new CompletionProposal("{", "{", IMAGE_DEFAULT),
			new CompletionProposal("}","}", IMAGE_DEFAULT),
			new CompletionProposal("cluster - Insert a template", '''
				subgraph clustername {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("edge - Insert a template", '''source -> target''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph (named) - Insert a template", '''
				subgraph name {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph - Insert a template", '''
				subgraph {
					content
				}''', IMAGE_DEFAULT)
		])
	}

	@Test def multi_edge() {
		'''
			graph {
				1--2«c»
			}
		'''.testContentAssistant(#[
			new CompletionProposal("--: Edge", "--", IMAGE_EDGE),
			new CompletionProposal(":", ":", IMAGE_DEFAULT),
			new CompletionProposal(";", ";", IMAGE_DEFAULT),
			new CompletionProposal("[", "[", IMAGE_DEFAULT),
			new CompletionProposal("edge[]: Attributes", "edge[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("graph[]: Attributes", "graph[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("node[]: Attributes", "node[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("subgraph: Subgraph", "subgraph", IMAGE_SUBGRAPH),
			new CompletionProposal("{", "{", IMAGE_DEFAULT),
			new CompletionProposal("}","}", IMAGE_DEFAULT),
			new CompletionProposal("cluster - Insert a template", '''
				subgraph clustername {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("edge - Insert a template", '''source -- target''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph (named) - Insert a template", '''
				subgraph name {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph - Insert a template", '''
				subgraph {
					content
				}''', IMAGE_DEFAULT)
		])
	}

	@Test def subgraph_directed_edge_template() {
		'''
			digraph {
				{
					«c»
				}
			}
		'''.testContentAssistant(#[
			new CompletionProposal("rank: Attribute", "rank=", IMAGE_ATTRIBUTE),
			new CompletionProposal("edge[]: Attributes", "edge[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("graph[]: Attributes", "graph[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("node[]: Attributes", "node[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("subgraph: Subgraph", "subgraph", IMAGE_SUBGRAPH),
			new CompletionProposal("{", "{", IMAGE_DEFAULT),
			new CompletionProposal("}","}", IMAGE_DEFAULT),
			new CompletionProposal("cluster - Insert a template", '''
				subgraph clustername {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("edge - Insert a template", '''source -> target''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph (named) - Insert a template", '''
				subgraph name {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph - Insert a template", '''
				subgraph {
					content
				}''', IMAGE_DEFAULT)
		])
	}

	@Test def subgraph_undirected_edge_template() {
		'''
			graph {
				{
					«c»
				}
			}
		'''.testContentAssistant(#[
			new CompletionProposal("rank: Attribute", "rank=", IMAGE_ATTRIBUTE),
			new CompletionProposal("edge[]: Attributes", "edge[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("graph[]: Attributes", "graph[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("node[]: Attributes", "node[]", IMAGE_ATTRIBUTES),
			new CompletionProposal("subgraph: Subgraph", "subgraph", IMAGE_SUBGRAPH),
			new CompletionProposal("{", "{", IMAGE_DEFAULT),
			new CompletionProposal("}","}", IMAGE_DEFAULT),
			new CompletionProposal("cluster - Insert a template", '''
				subgraph clustername {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("edge - Insert a template", '''source -- target''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph (named) - Insert a template", '''
				subgraph name {
					content
				}''', IMAGE_DEFAULT),
			new CompletionProposal("subgraph - Insert a template", '''
				subgraph {
					content
				}''', IMAGE_DEFAULT)
			])
	}

	@Test def html_like_label_tags() {
		'''
			graph {
				1 [label=<
					«c»
				>]
			}
		'''.testContentAssistant(#[
			new CompletionProposal("<B></B>: Tag", "<B></B>", IMAGE_HTML_TAG),
			new CompletionProposal("<BR/>: Tag", "<BR/>", IMAGE_HTML_TAG),
			new CompletionProposal("<FONT></FONT>: Tag", "<FONT></FONT>", IMAGE_HTML_TAG),
			new CompletionProposal("<I></I>: Tag", "<I></I>", IMAGE_HTML_TAG),
			new CompletionProposal("<O></O>: Tag", "<O></O>", IMAGE_HTML_TAG),
			new CompletionProposal("<S></S>: Tag", "<S></S>", IMAGE_HTML_TAG),
			new CompletionProposal("<SUB></SUB>: Tag", "<SUB></SUB>", IMAGE_HTML_TAG),
			new CompletionProposal("<SUP></SUP>: Tag", "<SUP></SUP>", IMAGE_HTML_TAG),
			new CompletionProposal("<TABLE></TABLE>: Tag", "<TABLE></TABLE>", IMAGE_HTML_TAG),
			new CompletionProposal("<U></U>: Tag", "<U></U>", IMAGE_HTML_TAG)
		])
	}

	@Test def html_like_label_attributes() {
		'''
			graph {
				1 [label=<
					<TABLE «c»></TABLE>
				>]
			}
		'''.testContentAssistant(#[
			new CompletionProposal("ALIGN: Attribute", 'ALIGN=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("BGCOLOR: Attribute", 'BGCOLOR=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("BORDER: Attribute", 'BORDER=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("CELLBORDER: Attribute", 'CELLBORDER=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("CELLPADDING: Attribute", 'CELLPADDING=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("CELLSPACING: Attribute", 'CELLSPACING=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("COLOR: Attribute", 'COLOR=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("COLUMNS: Attribute", 'COLUMNS=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("FIXEDSIZE: Attribute", 'FIXEDSIZE=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("GRADIENTANGLE: Attribute", 'GRADIENTANGLE=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("HEIGHT: Attribute", 'HEIGHT=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("HREF: Attribute", 'HREF=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("ID: Attribute", 'ID=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("PORT: Attribute", 'PORT=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("ROWS: Attribute", 'ROWS=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("SIDES: Attribute", 'SIDES=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("STYLE: Attribute", 'STYLE=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("TARGET: Attribute", 'TARGET=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("TITLE: Attribute", 'TITLE=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("TOOLTIP: Attribute", 'TOOLTIP=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("VALIGN: Attribute", 'VALIGN=""', IMAGE_ATTRIBUTE),
			new CompletionProposal("WIDTH: Attribute", 'WIDTH=""', IMAGE_ATTRIBUTE)
		])
	}

	@Test def node_color() {
		'''
			graph {
				1[color=«c»]
			}
		'''.computeCompletionProposals.forEach[
				// consider only color names proposals
				if (!"#/".contains(displayString)) {
					// verify that an image (filled by the corresponding color) is generated to the color names
					assertNotNull("Proposal image is missing for the '" + displayString + "' color!", image)
					// verify that a color description (as additional proposal information) is provided to the color names
					val colorScheme = "x11"
					val colorName = displayString
					val colorCode = DotColors.get(colorScheme, colorName)
					val expectedAdditionalProposalInfo = '''
						<table border=1>
							<tr>
								<td><b>color preview</b></td>
								<td><b>color scheme</b></td>
								<td><b>color name</b></td>
								<td><b>color code</b></td>
							</tr>
							<tr>
								<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:«colorCode»;"</div></td>
								<td align="center">«colorScheme»</td>
								<td align="center">«colorName»</td>
								<td align="center">«colorCode»</td>
							</tr>
						</table>
					'''
					assertEquals(
						"Color description as additional proposal information for the '" + displayString + "' color does not match!",
						expectedAdditionalProposalInfo, additionalProposalInfo)
				}
		]
	}

	@Test def html_color() {
		'''
			graph {
				1[label=<<font color="«c»">foo</font>>]
			}
		'''.computeCompletionProposals.forEach[
				// consider only color names proposals
				if (!"#/".contains(displayString)) {
					// verify that an image (filled by the corresponding color) is generated to the color names
					assertNotNull("Proposal image is missing for the '" + displayString + "' color!", image)
					// verify that a color description (as additional proposal information) is provided to the color names
					val colorScheme = "x11"
					val colorName = displayString
					val colorCode = DotColors.get(colorScheme, colorName)
					val expectedAdditionalProposalInfo = '''
						<table border=1>
							<tr>
								<td><b>color preview</b></td>
								<td><b>color scheme</b></td>
								<td><b>color name</b></td>
								<td><b>color code</b></td>
							</tr>
							<tr>
								<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:«colorCode»;"</div></td>
								<td align="center">«colorScheme»</td>
								<td align="center">«colorName»</td>
								<td align="center">«colorCode»</td>
							</tr>
						</table>
					'''
					assertEquals(
						"Color description as additional proposal information for the '" + displayString + "' color does not match!",
						expectedAdditionalProposalInfo, additionalProposalInfo)
				}
		]
	}

	@Test def html_colorscheme() {
		'''
			graph {
				1[label=<<font color="«c»">foo</font>> colorscheme=svg]
			}
		'''.computeCompletionProposals.forEach[
				// consider only color names proposals
				if (!"#/".contains(displayString)) {
					// verify that an image (filled by the corresponding color) is generated to the color names
					assertNotNull("Proposal image is missing for the '" + displayString + "' color!", image)
					// verify that a color description (as additional proposal information) is provided to the color names
					val colorScheme = "svg"
					val colorName = displayString
					val colorCode = DotColors.get(colorScheme, colorName)
					val expectedAdditionalProposalInfo = '''
						<table border=1>
							<tr>
								<td><b>color preview</b></td>
								<td><b>color scheme</b></td>
								<td><b>color name</b></td>
								<td><b>color code</b></td>
							</tr>
							<tr>
								<td border=0 align="center"><div style="border:1px solid black;width:50px;height:16px;background-color:«colorCode»;"</div></td>
								<td align="center">«colorScheme»</td>
								<td align="center">«colorName»</td>
								<td align="center">«colorCode»</td>
							</tr>
						</table>
					'''
					assertEquals(
						"Color description as additional proposal information for the '" + displayString + "' color does not match!",
						expectedAdditionalProposalInfo, additionalProposalInfo)
				}
		]
	}

	@Data
	static class CompletionProposal {
		String displayString
		String replacementString
		String image
	}

	private def testContentAssistant(CharSequence text, List<CompletionProposal> expectedCompletionProposals) {
		text.computeCompletionProposals.assertCompletionProposals(expectedCompletionProposals)
	}

	private def computeCompletionProposals(CharSequence text) {
		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition == -1) {
			fail("Can't locate cursor position symbols '" + c + "' in the input text.")
		}

		val content = text.toString.replace(c, "")

		newBuilder.append(content).computeCompletionProposals(cursorPosition)
	}

	private def assertCompletionProposals(ICompletionProposal[] actualCompletionProposals, List<CompletionProposal> expectedCompletionProposals) {
		assertEquals("The number of completion proposals does not match!", expectedCompletionProposals.size, actualCompletionProposals.size)
		for (i : 0..< actualCompletionProposals.size) {
			val actual = actualCompletionProposals.get(i)
			val expected = expectedCompletionProposals.get(i)
			actual.assertEquals(expected)
		}
	}

	private def dispatch assertEquals(ConfigurableCompletionProposal actual, CompletionProposal expected) {
		expected.displayString.assertEquals(actual.displayString)
		expected.replacementString.assertEquals(actual.replacementString)
		if (expected.image !== null) {
			expected.image.getImage.assertEquals(actual.image)
		}
	}

	private def dispatch assertEquals(TemplateProposal actual, CompletionProposal expected) {
		expected.displayString.assertEquals(actual.displayString)
		expected.replacementString.assertEquals(actual.additionalProposalInfo.convertLineEndings)
		if (expected.image !== null) {
			expected.image.getImage.assertEquals(actual.image)
		}
	}

	private def convertLineEndings(String text) {
		text.replaceAll("\r?\n", System.lineSeparator)
	}
}