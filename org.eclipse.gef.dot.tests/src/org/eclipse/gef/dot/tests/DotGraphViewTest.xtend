/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
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
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.expressions.EvaluationContext
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.gef.dot.internal.ui.DotGraphView
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.ui.ISources
import org.eclipse.ui.IWorkbenchCommandConstants
import org.eclipse.ui.commands.ICommandService
import org.eclipse.ui.services.IServiceLocator
import org.eclipse.xtext.resource.FileExtensionProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.XtextProjectHelper
import org.eclipse.xtext.ui.editor.XtextEditor
import org.eclipse.xtext.ui.testing.AbstractEditorTest
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil.addNature

/*
 * Test cases for the {@link DotGraphView} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotGraphViewTest extends AbstractEditorTest {

	static val DOT_GRAPH_VIEW_ID = "org.eclipse.gef.dot.internal.ui.DotGraphView"

	@Inject extension FileExtensionProvider

	@Test def show_in_dot_graph_view() {
		'''
			digraph {
				1[shape=circle]
			}
		'''.testShowInDotGraphView
	}

	private def testShowInDotGraphView(CharSequence it) {
		// given
		dslFile.openEditor.
		// when
		showInDotGraphView
		// then
		dotGraphViewIsPresent
	}

	private def IFile dslFile(CharSequence text) {
		val file = IResourcesSetupUtil.createFile(projectName + "/" + fileName + "." + fileExtension, text.toString)

		/*
		 * TODO: find a better (with good performance) solution
		 * to set the Xtext nature on the test project.
		 */
		val project = file.project
		if(!project.hasNature(XtextProjectHelper.NATURE_ID)) {
			project.addNature(XtextProjectHelper.NATURE_ID)
		}

		file
	}

	private def showInDotGraphView(XtextEditor dotEditor) {
		dotGraphViewIsNotPresent
		val showInExecutionEvent = dotEditor.showInExecutionEvent
		showInCommand.executeWithChecks(showInExecutionEvent)
	}

	private def getShowInExecutionEvent(XtextEditor dotEditor) {
		/**
		 * See https://stackoverflow.com/questions/34182727/how-can-i-unit-test-eclipse-command-handlers
		 */
		val parameters = new HashMap<String, String> => [
			put(IWorkbenchCommandConstants.NAVIGATE_SHOW_IN_PARM_TARGET, DOT_GRAPH_VIEW_ID)
		]

		val context = new EvaluationContext(null, new Object) => [
			addVariable(ISources.ACTIVE_WORKBENCH_WINDOW_NAME, workbenchWindow)
			addVariable(ISources.SHOW_IN_INPUT, dotEditor.editorInput)
		]

		new ExecutionEvent( null, parameters, null, context )
	}

	private def getShowInCommand() {
		/**
		 * See https://stackoverflow.com/questions/34182727/how-can-i-unit-test-eclipse-command-handlers
		 */
		val IServiceLocator serviceLocator = workbench
		val ICommandService commandService = serviceLocator.getService(ICommandService) as ICommandService
		commandService.getCommand(IWorkbenchCommandConstants.NAVIGATE_SHOW_IN)
	}

	private def dotGraphViewIsPresent() {
		assertPresenceOfDotGraphView(true)
	}

	private def dotGraphViewIsNotPresent() {
		assertPresenceOfDotGraphView(false)
	}

	private def assertPresenceOfDotGraphView(boolean expected) {
		val views = activePage.viewReferences
		val viewIDs = views.map[id]

		val actual = viewIDs.contains(DOT_GRAPH_VIEW_ID)
		var message = "The available views are: " + System.lineSeparator + viewIDs.sort.join(System.lineSeparator)
		if(expected) {
			message = "The DOT Graph view is not present, but it should be. " + message
		} else {
			message = "The DOT Graph view is present, but it should not be. " + message
		}
		Assert.assertEquals(message, expected, actual)
	}

	private def getProjectName() {
		"DOTGraphViewTest"
	}

	private def getFileName() {
		"test"
	}

	private def getFileExtension() {
		primaryFileExtension
	}

	override tearDown() {
		activePage.resetPerspective

		dotGraphViewIsNotPresent
		/**
		 * The Eclipse workspace needs to be explicitly saved after the test execution
		 * otherwise, the test case executions are resulting in a NullPointerException.
		 * For more information, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=460996
		 */
		ResourcesPlugin.workspace.save(true, null)

		super.tearDown
	}
}