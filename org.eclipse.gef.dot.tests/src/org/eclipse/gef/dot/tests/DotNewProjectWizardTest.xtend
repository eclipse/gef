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
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.eclipse.xtext.ui.wizard.IExtendedProjectInfo
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

	@Inject extension IResourcesSetupUtil
	@Inject Provider<DotTestableNewProjectWizard> wizardProvider
	
	val static TEST_PROJECT_NAME = "DotTestProject"

	@Test def new_project_wizard() {
		// Given
		assertWorkspaceIsEmpty
		
		// When
		val wizard = wizardProvider.get
		wizard.init(workbench, new StructuredSelection)
		createAndFinishWizardDialog(wizard)
		
		// Then
		val project = root.getProject(TEST_PROJECT_NAME)
		assertTrue(project.exists)
		waitForBuild
		assertNoErrorsInWorkspace
	}

	/**
	 * Create the wizard dialog, open it and press Finish.
	 */
	private def int createAndFinishWizardDialog(Wizard wizard) {
		val dialog = new WizardDialog(wizard.shell, wizard) {
			override open() {
				val thread = new Thread("Press Finish") {
					override run() {
						// wait for the shell to become active
						var attempt = 0
						while (shell === null && (attempt++) < 5) {
							println("Waiting for shell to become active")
							Thread.sleep(5000)
						}
						shell.display.syncExec[
							wizard.performFinish
							shell.close
						]
						attempt = 0
						while (shell !== null && (attempt++) < 5) {
							println("Waiting for shell to be disposed")
							Thread.sleep(5000)
						}
					}
				};
				thread.start
				super.open
			}
		};

		dialog.open
	}

	private def assertWorkspaceIsEmpty() {
		root.projects.isEmpty.assertTrue
	}
	
	/**
	 * Manually set the project name (usually set in the dialog text edit)
	 */
	static class DotTestableNewProjectWizard extends TemplateNewProjectWizard {
	
		override IExtendedProjectInfo getProjectInfo() {
			val projectInfo = super.projectInfo
			projectInfo.setProjectName(TEST_PROJECT_NAME)
			projectInfo
		}
	}
}