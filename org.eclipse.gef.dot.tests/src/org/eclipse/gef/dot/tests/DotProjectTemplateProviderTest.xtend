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
import java.util.List
import org.eclipse.gef.dot.internal.ui.language.wizard.DotProjectTemplateProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.util.IProjectFactoryContributor
import org.eclipse.xtext.ui.util.ProjectFactory
import org.eclipse.xtext.ui.util.TextFileContributor
import org.eclipse.xtext.ui.wizard.IExtendedProjectInfo
import org.eclipse.xtext.ui.wizard.template.AbstractProjectTemplate
import org.eclipse.xtext.ui.wizard.template.AbstractTemplate
import org.eclipse.xtext.ui.wizard.template.IProjectGenerator
import org.eclipse.xtext.ui.wizard.template.TemplateProjectInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals

/**
 * Test cases for the {@link DotProjectTemplateProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotProjectTemplateProviderTest {

	@Inject DotProjectTemplateProvider dotProjectTemplateProvider

	AbstractProjectTemplate[] projectTemplates

	@Before def void setup() {
		projectTemplates = dotProjectTemplateProvider.projectTemplates
	}

	@Test def correct_number_of_project_templates_are_provided() {
		assertEquals(2, projectTemplates.size)
	}

	@Test def empty_project_template_properties() {
		emptyProjectTemplate.hasProperties('''
		Label: Empty Project
		Description: <p><b>Create an empty GEF DOT project.</b></p> <p>This wizard creates an empty GEF DOT project.</p>
		''')
	}

	@Test def empty_project_template_variables() {
		emptyProjectTemplate.hasNoVariables
	}

	@Test def empty_project_template_generation() {
		emptyProjectTemplate.generatesProject('''
			Builder IDs: [org.eclipse.xtext.ui.shared.xtextBuilder]
			Project Natures: [org.eclipse.xtext.ui.shared.xtextNature]
			Generated Files:
		''')
	}

	@Test def directed_graph_project_template_properties() {
		directedGraphProjectTemplate.hasProperties('''
		Label: Simple Directed Graph
		Description: <p><b>Simple Directed Graph</b></p> <p>This wizard creates a GEF DOT project containing a sample directed graph. I.e. a graph whose edges are directed.</p>
		''')
	}

	@Test def directed_graph_project_template_variables() {
		directedGraphProjectTemplate.hasNoVariables
	}

	@Test def directed_graph_project_template_generation() {
		directedGraphProjectTemplate.generatesProject('''
			Builder IDs: [org.eclipse.xtext.ui.shared.xtextBuilder]
			Project Natures: [org.eclipse.xtext.ui.shared.xtextNature]
			Generated Files:
				example.dot
					/*
					 * This is an example of a directed graph
					 * To get help creating further graphs use the add file wizard.
					 */
					digraph sampleGraphName {
						1 [label="Hello World"]
						1->2
					}
		''')
	}

	private def emptyProjectTemplate() {
		projectTemplates.head
	}

	private def directedGraphProjectTemplate() {
		projectTemplates.get(1)
	}

	private def hasProperties(AbstractTemplate template, String expected) {
		val actual = '''
			Label: «template.label»
			Description: «template.description»
		'''
		assertEquals(expected, actual.toString)
	}

	private def hasNoVariables(AbstractTemplate template) {
		assertEquals(0, template.variables.size)
	}

	private def generatesProject(AbstractProjectTemplate projectTemplate, String expected) {
		projectTemplate.projectInfo = new TemplateProjectInfo(projectTemplate)
		projectTemplate.generateProjects(new TestProjectGenerator(expected))
	}

	private def setProjectInfo(AbstractProjectTemplate template, IExtendedProjectInfo projectInfo) {
		val method = AbstractProjectTemplate.getDeclaredMethod("setProjectInfo", IExtendedProjectInfo)
		method.setAccessible(true)
		method.invoke(template, projectInfo)
	}

	static class TestProjectGenerator implements IProjectGenerator {

		String expected = null

		new(String expected) {
			this.expected = expected
		}

		override generate(ProjectFactory projectFactory) {
			assertEquals(expected, '''
			Builder IDs: «projectFactory.builderIds»
			Project Natures: «projectFactory.projectNatures»
			Generated Files:
				«FOR contributor : projectFactory.contributors»
					«contributor.fileName»
						«contributor.fileContent»
				«ENDFOR»
			'''.toString)
		}

		private def getContributors(ProjectFactory projectFactory) {
			val field = projectFactory.class.getDeclaredField("contributors")
			field.setAccessible(true)
			val contributors = field.get(projectFactory) as List<IProjectFactoryContributor>
			if (contributors !== null) contributors else newArrayList
		}

		private def dispatch getFileName(IProjectFactoryContributor contributor) {
			throw new IllegalArgumentException(contributor.class + " is not supported.")
		}

		private def dispatch getFileContent(IProjectFactoryContributor contributor) {
			throw new IllegalArgumentException(contributor.class + " is not supported.")
		}

		private def dispatch getFileName(TextFileContributor contributor) {
			val field = contributor.class.getDeclaredField("fileName")
			field.setAccessible(true)
			field.get(contributor) as CharSequence
		}

		private def dispatch getFileContent(TextFileContributor contributor) {
			val field = contributor.class.getDeclaredField("contents")
			field.setAccessible(true)
			field.get(contributor) as CharSequence
		}
	}
}