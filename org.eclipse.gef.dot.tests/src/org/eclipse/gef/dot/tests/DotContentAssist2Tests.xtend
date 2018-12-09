/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.color.DotColors
import org.eclipse.gef.dot.internal.ui.language.contentassist.DotProposalProvider
import org.eclipse.jface.text.templates.TemplateProposal
import org.eclipse.ui.internal.statushandlers.StatusHandlerRegistry
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal
import org.eclipse.xtext.xbase.junit.ui.AbstractContentAssistTest
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail

/**
 * Test cases for the {@link ConfigurableCompletionProposal} and
 * {@link TemplateProposal} of the {@link DotProposalProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotContentAssist2Tests extends AbstractContentAssistTest {

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

	private def computeCompletionProposals(CharSequence text) {
		val cursorPosition = text.toString.indexOf(c)
		if(cursorPosition == -1) {
			fail("Can't locate cursor position symbols '" + c + "' in the input text.")
		}
		
		val content = text.toString.replace(c, "")
		
		newBuilder.append(content).computeCompletionProposals(cursorPosition)
	}
}