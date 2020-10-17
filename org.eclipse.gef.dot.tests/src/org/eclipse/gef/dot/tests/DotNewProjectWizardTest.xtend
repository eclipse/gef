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
import com.google.inject.Provider
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.wizard.Wizard
import org.eclipse.jface.wizard.WizardDialog
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.testing.AbstractWorkbenchTest
import org.eclipse.xtext.ui.wizard.template.NewProjectWizardTemplateSelectionPage
import org.eclipse.xtext.ui.wizard.template.TemplateNewProjectWizard
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.*

/**
 * The implementation of this class is mainly taken from the
 * https://github.com/LorenzoBettini/edelta/blob/master/edelta.parent/edelta.ui.tests/src/edelta/ui/tests/EdeltaNewProjectWizardTest.xtend
 * class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotNewProjectWizardTest extends AbstractWorkbenchTest {

	@Inject Provider<DotTestableNewProjectWizard> wizardProvider

	val static PROJECT_NAME = "DotTest"

	val TIMEOUT = 500

	@Test def creating_a_new_empty_dot_project_without_selecting_a_template() {
		// Given
		assertWorkspaceIsEmpty
		
		// When
		val wizard = wizardProvider.get
		wizard.init(workbench, new StructuredSelection)
		wizard.createAndFinishWizardDialog
		
		// Then
		val project = root.getProject(PROJECT_NAME)
		assertTrue(project.exists)
		waitForBuild
		assertNoErrorsInWorkspace
	}

	@Test def empty_project_template() {
		val wizard = wizardProvider.get
		wizard.init(workbench, new StructuredSelection)
		wizard.assertTemplate("Empty Project", "<p><b>Create an empty GEF DOT project.</b></p> <p>This wizard creates an empty GEF DOT project.</p>")
	}

	/**
	 * Create the wizard dialog, open it and press Finish.
	 */
	private def createAndFinishWizardDialog(Wizard wizard) {
		val dialog = new WizardDialog(wizard.shell, wizard) {
			override open() {
				val thread = new Thread("Press Finish") {
					override run() {
						// wait for the shell to become active
						var attempt = 0
						while (shell === null && (attempt++) < 5) {
							println("Waiting for shell to become active")
							Thread.sleep(TIMEOUT)
						}
						shell.display.syncExec[
							wizard.performFinish
							shell.close
						]
						attempt = 0
						while (shell !== null && (attempt++) < 5) {
							println("Waiting for shell to be disposed")
							Thread.sleep(TIMEOUT)
						}
					}
				}
				thread.start
				super.open
			}
		}

		dialog.open
	}

	/**
	 * Create the wizard dialog, open it, press Next to navigate to the template selection page, verifies its label and description and press Finish.
	 */
	var actualTemplateLabel = null
	var actualTemplateDescription = null
	private def assertTemplate(Wizard wizard, String expectedTemplateLabel, String expectedTemplateDescription) {
		val dialog = new WizardDialog(wizard.shell, wizard) {
			override open() {
				val thread = new Thread("Press Finish") {
					override run() {
						// wait for the shell to become active
						var attempt = 0
						while (shell === null && (attempt++) < 5) {
							println("Waiting for shell to become active")
							Thread.sleep(TIMEOUT)
						}
						shell.display.syncExec[
							val templateSelectionPage = wizard.getNextPage(wizard.startingPage) as NewProjectWizardTemplateSelectionPage
							templateSelectionPage.showPage
							val selectedTemplate = templateSelectionPage.selectedTemplate
							actualTemplateLabel = selectedTemplate.label
							actualTemplateDescription = selectedTemplate.description
							wizard.performFinish
							shell.close
						]
						attempt = 0
						while (shell !== null && (attempt++) < 5) {
							println("Waiting for shell to be disposed")
							Thread.sleep(TIMEOUT)
						}
					}
				}
				thread.start
				super.open
			}
		}

		dialog.open

		assertEquals(expectedTemplateLabel, actualTemplateLabel)
		assertEquals(expectedTemplateDescription, actualTemplateDescription)
	}

	private def assertWorkspaceIsEmpty() {
		root.projects.isEmpty.assertTrue
	}

	/**
	 * Manually set the project name (usually set in the wizard dialog)
	 */
	static class DotTestableNewProjectWizard extends TemplateNewProjectWizard {

		override getProjectInfo() {
			val projectInfo = super.projectInfo
			projectInfo.setProjectName(PROJECT_NAME)
			projectInfo
		}
	}
}