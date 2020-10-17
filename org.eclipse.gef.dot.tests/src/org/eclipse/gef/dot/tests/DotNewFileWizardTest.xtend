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
import org.eclipse.core.resources.IProject
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.jface.wizard.Wizard
import org.eclipse.jface.wizard.WizardDialog
import org.eclipse.ui.IWorkbench
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.testing.AbstractWorkbenchTest
import org.eclipse.xtext.ui.wizard.template.TemplateFileInfo
import org.eclipse.xtext.ui.wizard.template.TemplateNewFileWizard
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.*

/**
 * The implementation of this class is mainly taken from the
 * https://github.com/LorenzoBettini/edelta/blob/master/edelta.parent/edelta.ui.tests/src/edelta/ui/tests/EdeltaNewProjectWizardTest.xtend
 * class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotNewFileWizardTest extends AbstractWorkbenchTest {

	@Inject Provider<DotTestableNewFileWizard> wizardProvider
	@Inject extension FileExtensionProvider

	val static PROJECT_NAME = "DotTest"
	val static FILE_NAME = "test"

	static IProject project

	val TIMEOUT = 500

	@Test def creating_a_new_dot_file_without_selecting_a_template() {
		// Given
		assertWorkspaceIsEmpty
		project = PROJECT_NAME.createProject
		project.addNature(XtextProjectHelper.NATURE_ID)
		assertNoErrorsInWorkspace

		// When
		val wizard = wizardProvider.get
		wizard.init(workbench, new StructuredSelection)
		wizard.createAndFinishWizardDialog

		// Then
		fileHasContent('''
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

		assertNoErrorsInWorkspace
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

	private def assertWorkspaceIsEmpty() {
		root.projects.isEmpty.assertTrue
	}

	private def fileHasContent(String expected) {
		val fileName = FILE_NAME + "." + primaryFileExtension
		val file = project.getFile(fileName)
		val fileExists = file.exists
		assertTrue("The file '" + fileName+ "' cannot be found!", fileExists)
		val actual = file.fileToString
		assertEquals(expected, actual)
	}

	/**
	 * Manually select the project in the Project Explorer and
	 * set the file name (usually set in the wizard dialog)
	 */
	static class DotTestableNewFileWizard extends TemplateNewFileWizard {

		override init(IWorkbench workbench, IStructuredSelection selection) {
			super.init(workbench, new StructuredSelection(project))
		}

		override getFileInfo() {
			val fileInfo = super.fileInfo
			fileInfo.name = FILE_NAME
			fileInfo
		}

		private def setName(TemplateFileInfo templateFileInfo, String name) {
			val field = templateFileInfo.class.getDeclaredField("name")
			field.setAccessible(true)
			field.set(templateFileInfo, name)
		}
	}
}