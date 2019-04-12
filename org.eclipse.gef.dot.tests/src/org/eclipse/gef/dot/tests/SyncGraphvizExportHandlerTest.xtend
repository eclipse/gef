/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
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
import java.util.HashMap
import java.util.List
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.expressions.EvaluationContext
import org.eclipse.core.resources.IFile
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.handlers.SyncGraphvizExportHandler
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.swt.widgets.ToolItem
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.eclipse.xtext.ui.refactoring.ui.SyncUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.addNature

/**
 * Test cases for the {@link SyncGraphvizExportHandler} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class SyncGraphvizExportHandlerTest extends AbstractEditorTest {

	@Inject XtextEditorInfo editorInfo
	@Inject extension FileExtensionProvider
	@Inject extension SyncUtil

	ToolItem syncGraphvizExportToolbarItem

	@Before def void setup() {
		syncGraphvizExportToolbarItem = getDummyToolItem
	}

	@Test def show_graphviz_configuration_dialog() {
		'''
			graph {
				1
			}
		'''.startSyncGraphvizExport
	}

	@Test def show_graphviz_configuration_dialog_and_open_graphviz_preference_page() {
		'''
			graph {
				1
			}
		'''.startSyncGraphvizExportAndOpenGraphvizPreferencePage
	}

	private def startSyncGraphvizExport(CharSequence it) {
		// given
		dslFile.openInEditor
		// when
		syncGraphvizExport(false).
		// then
		graphvizConfigurationDialogIsPresent
		// and
		syncGraphvizExportToolbarItemIsNotSelected
	}

	private def startSyncGraphvizExportAndOpenGraphvizPreferencePage(CharSequence it) {
		// given
		dslFile.openInEditor
		// when
		syncGraphvizExport(true).
		// then
		graphvizPreferencePageIsPresent
		// and
		syncGraphvizExportToolbarItemIsNotSelected
	}

	private def dslFile(CharSequence text) {
		val file = IResourcesSetupUtil.createFile(projectName + "/" + fileName + "." + fileExtension, text.toString)

		/*
		 * TODO: find a better (with good performance) solution
		 * to set the Xtext nature on the test project.
		 */
		val project = file.project
		if (!project.hasNature(XtextProjectHelper.NATURE_ID)) {
			project.addNature(XtextProjectHelper.NATURE_ID)
		}

		file
	}

	private def openInEditor(IFile it) {
		waitForBuild(new NullProgressMonitor)
		val editor = openEditor
		flushPendingEvents
		editor
	}

	private def syncGraphvizExport(boolean openGraphvizPreferencePage) {
		val dialogContent = newArrayList
	
		waitForTheGraphvizConfigurationDialog(dialogContent, openGraphvizPreferencePage)
	
		val result = new SyncGraphvizExportHandler().execute(syncGraphvizExportExecutionEvent)
		result.assertNull
	
		flushPendingEvents
	
		dialogContent
	}

	private def waitForTheGraphvizConfigurationDialog(List<String> dialogContent, boolean clickOnLink) {
		new Thread("Sync Graphviz Export - waiting for the Graphviz configuration dialog to become active") {
			override run() {
				while (getGraphvizConfigurationDialog === null) {
					Thread.sleep(10000)
				}
				getGraphvizConfigurationDialog.display.asyncExec [
					dialogContent  += graphvizConfigurationDialog.text

					val children = graphvizConfigurationDialog.children
					assertEquals("The number of the Graphviz configuration dialog does not match !", 4, children.length)
					val secondChild = children.get(1)
					assertTrue("The second child of the Graphviz configuration dialog should be a link widget", secondChild instanceof Link)
					val link = secondChild as Link
					dialogContent += link.text
					if (clickOnLink) {
						dialogContent.clear
						waitForTheGraphvizPreferencePage(dialogContent)
						link.click
					} else {
						graphvizConfigurationDialog.close
					}
				]
			}
		}.start
	}

	private def waitForTheGraphvizPreferencePage(List<String> dialogContent) {
		new Thread("Sync Graphviz Export - waiting for the Graphviz preference page to become active") {
			override run() {
				while (getGraphvizPreferencePage === null) {
					Thread.sleep(500)
				}
				getGraphvizPreferencePage.display.asyncExec [
					dialogContent += graphvizPreferencePage.text

					val children = graphvizPreferencePage.children
					assertEquals("The number of the Graphviz preference page's children does not match !", 1, children.length)
					graphvizPreferencePage.close
				]
			}
		}.start
	}

	private def click(Link link) {
		link.notifyListeners(SWT.Selection, new Event)
	}

	Shell graphvizConfigurationDialog = null
	private def getGraphvizConfigurationDialog() {
		Display.^default.syncExec [
			"Graphviz is not configured properly".activateShell
			graphvizConfigurationDialog = Display.^default.activeShell
		]
		graphvizConfigurationDialog
	}

	Shell graphvizPreferencePage = null
	private def getGraphvizPreferencePage() {
		Display.^default.syncExec [
			"Preferences".activateShell
			graphvizPreferencePage = Display.^default.activeShell
		]
		graphvizPreferencePage
	}
	
	/*
	 * Ensures that the shell with the given title (if present) is activated.
	 * This is needed to make the tests pass on the CI Linux server. 
	 */
	private def activateShell(String title) {
		Display.^default.shells.findFirst[text == title]?.forceActive
	}

	private def getSyncGraphvizExportExecutionEvent() {
		/**
		 * See https://stackoverflow.com/questions/34182727/how-can-i-unit-test-eclipse-command-handlers
		 */
		val parameters = new HashMap<String, String>
		val trigger = new Event => [widget = syncGraphvizExportToolbarItem]
		val context = new EvaluationContext(null, new Object)

		new ExecutionEvent(null, parameters, trigger, context)
	}

	private def getDummyToolItem() {
		val shell = new Shell
		val toolbar = new ToolBar(shell, SWT.DEFAULT)
		new ToolItem(toolbar, SWT.CHECK) => [selection = true]
	}

	private def graphvizConfigurationDialogIsPresent(List<String> actualGraphvizConfigurationDialogContent) {
		val actualDialogTitle = actualGraphvizConfigurationDialogContent.head
		val actualDialogMessage = actualGraphvizConfigurationDialogContent.last
	
		val expectedDialogTitle = "Graphviz is not configured properly"
		val expectedDialogMessage = "Please specify the location of the 'dot' executable via the <a>Graphviz preference page</a>."

		expectedDialogTitle.assertEquals(actualDialogTitle)
		expectedDialogMessage.assertEquals(actualDialogMessage)
	}

	private def graphvizPreferencePageIsPresent(List<String> actualGraphvizPreferencePageContent) {
		val actualDialogTitle = actualGraphvizPreferencePageContent.head
		val expectedDialogTitle = "Preferences"
		expectedDialogTitle.assertEquals(actualDialogTitle)
	}

	private def syncGraphvizExportToolbarItemIsNotSelected() {
		assertFalse("The sync Graphviz export toolbar item should not be selected if Graphviz is not configured properly !", syncGraphvizExportToolbarItem.selection)
	}

	private def getProjectName() {
		"SyncGraphvizExportHandlerTest"
	}

	private def getFileName() {
		"test"
	}

	private def getFileExtension() {
		primaryFileExtension
	}

	override protected getEditorId() {
		editorInfo.editorId
	}

	private def flushPendingEvents() {
		while(Display.current !== null &&
			!Display.current.isDisposed &&
			 Display.current.readAndDispatch) {
		}
	}

}