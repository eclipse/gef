/*******************************************************************************
 * Copyright (c) 2009, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Refactoring of preferences (bug #446639)
 *                                - conversion from Java to Xtend
 *                                - minor refactorings
 *     Darius Jockel (itemis AG)  - Added tests for calling dot with large
 *                                  input files #492395
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.io.File
import java.util.List
import java.util.Properties
import org.eclipse.gef.dot.internal.DotExecutableUtils
import org.eclipse.gef.dot.internal.ui.preferences.GraphvizPreferencePage
import org.junit.Assert
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test

import static extension org.junit.Assert.*

/**
 * Test cases for the {@link DotExecutableUtils} class.
 *
 * @author Fabian Steeg (fsteeg)
 * @author Tamas Miklossy
 * @author Darius Jockel
 */
class DotExecutableUtilsTest {

	static String dotExecutablePath = null

	@BeforeClass
	def static void setup() {
		dotExecutablePath = getDotExecutablePath
		Assume.assumeTrue(!dotExecutablePath.nullOrEmpty)
	}

	@Test def simple_graph() {
		"simple_graph.dot".testImageExport
	}

	@Test def directed_graph() {
		"simple_digraph.dot".testImageExport
	}

	@Test def labeled_graph() {
		"labeled_graph.dot".testImageExport
	}

	@Test def styled_graph() {
		"styled_graph.dot".testImageExport
	}

	@Test(timeout=2000) def test_complex_dot() {
		"arrowshapes_direction_both.dot".inputFile.executeDot.assertNotNull
	}

	/* This test depends on the installed dot environment.
	 * @Test def supported_export_format_calculation() {
		#[
			"bmp", "canon", "cmap", "cmapx", "cmapx_np", "dot", "emf", "emfplus", "eps", "fig",
			"gd", "gd2", "gif", "gv", "imap", "imap_np", "ismap", "jpe", "jpeg", "jpg", "metafile",
			"pdf", "pic", "plain", "plain-ext", "png", "pov", "ps", "ps2", "svg", "svgz", "tif",
			"tiff", "tk","vml", "vmlz", "vrml", "wbmp", "xdot", "xdot1.2", "xdot1.4"
		].join(System.lineSeparator).assertEquals(supportedExportFormats)
	}*/

	private def testImageExport(String fileName) {
		// given
		val inputFile = fileName.inputFile
		val outputFile = fileName.outputFile
		val outputs = newArrayList("", "")

		// when
		val image = inputFile.renderImage(outputFile, outputs)

		// then
		Assert.assertEquals("The dot executable produced the following errors:", "", outputs.get(1))
		Assert.assertNotNull("Image must not be null", image)
		Assert.assertTrue("Image must exist", image.exists)
		System.out.println('''Created image: «image»''')
	}

	/**
	 * @return The path of the local Graphviz DOT executable, as specified in
	 *         the test.properties file
	 */
	private def static getDotExecutablePath() {
		if (dotExecutablePath === null) {
			val props = new Properties
			val stream = DotExecutableUtilsTest.getResourceAsStream("test.properties")

			if (stream === null) {
				System.err.println('''Could not load the test.properties file in directory of «DotExecutableUtilsTest.simpleName»''')
			} else {
				props.load(stream)
				/*
				 * Path to the local Graphviz DOT executable file
				 */
				dotExecutablePath = props.getProperty(GraphvizPreferencePage.DOT_PATH_PREF_KEY)
				if (dotExecutablePath === null || dotExecutablePath.trim.length === 0) {
					System.err.printf(
						"Graphviz DOT executable path not set in test.properties file under '%s' key.\n",
						GraphvizPreferencePage.DOT_PATH_PREF_KEY)
				} else
					stream.close
				}
			}
		dotExecutablePath
	}

	private def inputFile(String fileName) {
		val dotFile = DotTestUtils.file(fileName)
		dotFile.exists.assertTrue
		dotFile
	}

	private def outputFile(String fileName) {
		File.createTempFile("tmp_" + fileName.substring(0, fileName.lastIndexOf('.')), ".pdf")
	}

	private def supportedExportFormats() {
		DotExecutableUtils.getSupportedExportFormats(dotExecutablePath).join(System.lineSeparator)
	}

	private def executeDot(File dotFile) {
		DotExecutableUtils.executeDot(new File(dotExecutablePath), true, dotFile, null,	null)
	}

	private def renderImage(File inputFile, File outputFile, List<String> outputs) {
		DotExecutableUtils.renderImage(
				new File(dotExecutablePath), inputFile, "pdf", outputFile, outputs)
	}

}
