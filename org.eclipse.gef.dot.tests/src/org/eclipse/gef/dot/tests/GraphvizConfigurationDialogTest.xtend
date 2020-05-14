/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.internal.ui.preferences.GraphvizConfigurationDialog
import org.eclipse.swt.widgets.Link
import org.eclipse.swt.widgets.Shell
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

import static extension org.junit.Assert.assertEquals

/**
 * Test cases for the {@link GraphvizConfigurationDialog} class.
 */
class GraphvizConfigurationDialogTest {

	GraphvizConfigurationDialog dialog = null

	@Before def setup() {
		dialog = new GraphvizConfigurationDialog(new Shell)
		dialog.create
	}

	@Test def void dialog_has_title() {
		"Graphviz is not configured properly".assertEquals(dialog.shell.text)
	}

	@Test def dialog_has_warning_image() {
		dialog.warningImage.assertEquals(dialog.image)
	}

	@Test def dialog_has_message() {
		val link = dialog.shell.children.get(1) as Link
		"Please specify the location of the 'dot' executable via the <a>Graphviz preference page</a>.".assertEquals(link.text)
	}

	@After def tearDown() {
		val result = dialog.close
		assertTrue("The dialog is still open but should have already been closed", result)
	}
}