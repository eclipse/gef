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

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.ui.language.wizard.DotFileTemplateProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.wizard.template.AbstractFileTemplate
import org.eclipse.xtext.ui.wizard.template.AbstractTemplate
import org.eclipse.xtext.ui.wizard.template.IFileGenerator
import org.eclipse.xtext.ui.wizard.template.StringSelectionTemplateVariable
import org.eclipse.xtext.ui.wizard.template.StringTemplateVariable
import org.eclipse.xtext.ui.wizard.template.TemplateFileInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals

/**
 * Test cases for the {@link DotFileTemplateProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotFileTemplateProviderTest {

	@Inject DotFileTemplateProvider dotFileTemplateProvider

	val static PROJECT_NAME = "DotTest"
	val static FILE_NAME = "test"

	AbstractFileTemplate[] fileTemplates

	@Before def void setup() {
		fileTemplates = dotFileTemplateProvider.fileTemplates
	}

	@Test def correct_number_of_file_templates_are_provided() {
		assertEquals(1, fileTemplates.size)
	}

	@Test def parameterised_dot_file_template_properties() {
		parameterisedDotFileTemplate.hasProperties('''
			Label: DOT Graph
			Description: Create a GEF DOT graph.
		''')
	}

	@Test def parameterised_dot_file_template_variables() {
		parameterisedDotFileTemplate.hasVariables('''
			Graph Type:
			Graph name (optional): Optional name of the graph
			Number of nodes:
		''')
	}

	@Test def parameterised_dot_file_template_generation_001() {
		parameterisedDotFileTemplate.generatesFile("DotTest/test.dot", '''
			/*
			 * This is a graph stub
			 */
			digraph {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				1 [label="label of node \N"]
				// edges
			}
		''')
	}

	@Test def void parameterised_dot_file_template_generation_002() {
		parameterisedDotFileTemplate => [
			graphTypeVariable = "directed graph"
			generatesFile("DotTest/test.dot", '''
				/*
				 * This is a graph stub
				 */
				digraph {
					// global attribute statement
					node [shape="ellipse"]
					// nodes
					1 [label="label of node \N"]
					// edges
				}
			''')
		]
	}

	@Test def parameterised_dot_file_template_generation_003() {
		parameterisedDotFileTemplate.graphTypeVariable = "undirected graph"
		parameterisedDotFileTemplate.generatesFile("DotTest/test.dot", '''
			/*
			 * This is a graph stub
			 */
			graph {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				1 [label="label of node \N"]
				// edges
			}
		''')
	}

	@Test def parameterised_dot_file_template_generation_004() {
		parameterisedDotFileTemplate.graphTypeVariable = "directed graph"
		parameterisedDotFileTemplate.graphNameVariable = "graph_name"
		parameterisedDotFileTemplate.generatesFile("DotTest/test.dot", '''
			/*
			 * This is a graph stub
			 */
			digraph graph_name {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				1 [label="label of node \N"]
				// edges
			}
		''')
	}

	@Test def parameterised_dot_file_template_generation_005() {
		parameterisedDotFileTemplate.graphTypeVariable = "undirected graph"
		parameterisedDotFileTemplate.graphNameVariable = "graph_name"
		parameterisedDotFileTemplate.generatesFile("DotTest/test.dot", '''
			/*
			 * This is a graph stub
			 */
			graph graph_name {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				1 [label="label of node \N"]
				// edges
			}
		''')
	}

	@Test def parameterised_dot_file_template_generation_006() {
		parameterisedDotFileTemplate.nodesVariable = "3"
		parameterisedDotFileTemplate.generatesFile("DotTest/test.dot", '''
			/*
			 * This is a graph stub
			 */
			digraph {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				1 [label="label of node \N"]
				2 [label="label of node \N"]
				3 [label="label of node \N"]
				// edges
				1 -> 2 [label="an edge between nodes \T and \H" fontcolor="red"]
				2 -> 3 [label="an edge between nodes \T and \H" fontcolor="red"]
			}
		''')
	}

	@Test def parameterised_dot_file_template_generation_007() {
		parameterisedDotFileTemplate.graphTypeVariable = "undirected graph"
		parameterisedDotFileTemplate.nodesVariable = "4"
		parameterisedDotFileTemplate.generatesFile("DotTest/test.dot", '''
			/*
			 * This is a graph stub
			 */
			graph {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				1 [label="label of node \N"]
				2 [label="label of node \N"]
				3 [label="label of node \N"]
				4 [label="label of node \N"]
				// edges
				1 -- 2 [label="an edge between nodes \T and \H" fontcolor="red"]
				2 -- 3 [label="an edge between nodes \T and \H" fontcolor="red"]
				3 -- 4 [label="an edge between nodes \T and \H" fontcolor="red"]
			}
		''')
	}

	private def hasProperties(AbstractTemplate template, String expected) {
		val actual = '''
			Label: «template.label»
			Description: «template.description»
		'''
		assertEquals(expected, actual.toString)
	}

	private def hasVariables(AbstractTemplate template, String expected) {
		val actual = '''
			«FOR variable : template.variables»
				«variable.label»«IF variable.description !== null» «variable.description»«ENDIF»
			«ENDFOR»
		'''.toString

		assertEquals(expected, actual)
	}

	private def generatesFile(AbstractFileTemplate fileTemplate, String expectedPath, String expectedContent) {
		fileTemplate.templateInfo = new TemplateFileInfo(PROJECT_NAME, FILE_NAME, fileTemplate)
		fileTemplate.generateFiles(new TestFileGenerator(expectedPath, expectedContent))
	}

	private def parameterisedDotFileTemplate() {
		fileTemplates.head
	}

	private def setTemplateInfo(AbstractFileTemplate template, TemplateFileInfo info) {
		val method = AbstractFileTemplate.getDeclaredMethod("setTemplateInfo", TemplateFileInfo)
		method.setAccessible(true)
		method.invoke(template, info)
	}

	private def setGraphTypeVariable(AbstractTemplate template, String graphType) {
		val field = template.class.getDeclaredField("type")
		field.setAccessible(true)
		val graphTypeVariable = field.get(template) as StringSelectionTemplateVariable
		graphTypeVariable.value = graphType
	}

	private def setGraphNameVariable(AbstractTemplate template, String graphName) {
		val field = template.class.getDeclaredField("graphName")
		field.setAccessible(true)
		val graphNameVariable = field.get(template) as StringTemplateVariable
		graphNameVariable.value = graphName
	}

	private def setNodesVariable(AbstractTemplate template, String numberOfNodes) {
		val field = template.class.getDeclaredField("nodes")
		field.setAccessible(true)
		val nodesVariable = field.get(template) as StringTemplateVariable
		nodesVariable.value = numberOfNodes
	}

	static class TestFileGenerator implements IFileGenerator {

		String expectedPath = null
		String expectedContent = null

		new(String expectedPath, String expectedContent) {
			this.expectedPath = expectedPath
			this.expectedContent = expectedContent
		}

		override generate(CharSequence path, CharSequence content) {
			assertEquals(expectedPath, path.toString)
			assertEquals(expectedContent, content.toString)
		}
	}
}