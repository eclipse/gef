/*******************************************************************************
 * Copyright (c) 2009, 2016 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Refactoring of preferences (bug #446639)
 *                                - minor refactorings
 *     Darius Jockel (itemis AG)  - Added tests for calling dot with large 
 *                                  input files #492395
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.ui.GraphvizPreferencePage;
import org.eclipse.gef.graph.Graph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the {@link DotExecutableUtils} class.
 * 
 * @author Fabian Steeg (fsteeg)
 * @author Tamas Miklossy
 * @author Darius Jockel
 */
public class DotExecutableUtilsTests {

	private static String dotExecutablePath = null;

	@BeforeClass
	public static void setup() throws IOException {
		dotExecutablePath = getDotExecutablePath();
	}

	@Test
	public void simpleGraph() {
		testDotGeneration(DotTestUtils.getSimpleGraph(),
				"arrowshapes_direction_both.dot");
	}

	@Test
	public void directedGraph() {
		testDotGeneration(DotTestUtils.getSimpleDiGraph(),
				"simple_digraph.dot");
	}

	@Test
	public void labeledGraph() {
		testDotGeneration(DotTestUtils.getLabeledGraph(), "labeled_graph.dot");
	}

	@Test
	public void styledGraph() {
		testDotGeneration(DotTestUtils.getStyledGraph(), "styled_graph.dot");
	}

	@Test(timeout = 2000)
	public void testComplexDot() throws Exception {
		if (dotExecutablePath != null) {
			File dotFile = new File(DotTestUtils.RESOURCES_TESTS
					+ "arrowshapes_direction_both.dot");
			assertTrue(dotFile.exists());
			String[] dotResult = DotExecutableUtils.executeDot(
					new File(dotExecutablePath), true, dotFile, null, null);
			assertNotNull("Result should not be null", dotResult);
		}
	}

	@Test
	public void testSupportedExportFormatCalculation() {
		if (dotExecutablePath != null) {
			String[] expectedExportFormats = { "bmp", "canon", "cmap", "cmapx",
					"cmapx_np", "dot", "emf", "emfplus", "eps", "fig", "gd",
					"gd2", "gif", "gv", "imap", "imap_np", "ismap", "jpe",
					"jpeg", "jpg", "metafile", "pdf", "pic", "plain",
					"plain-ext", "png", "pov", "ps", "ps2", "svg", "svgz",
					"tif", "tiff", "tk", "vml", "vmlz", "vrml", "wbmp", "xdot",
					"xdot1.2", "xdot1.4" };

			String[] actualExportFormats = DotExecutableUtils
					.getSupportedExportFormats(dotExecutablePath);

			// compare the string representation of the arrays to get better
			// feedback in case of failing test cases
			Assert.assertEquals(Arrays.toString(expectedExportFormats),
					Arrays.toString(actualExportFormats));
		}
	}

	private void testDotGeneration(final Graph graph, String fileName) {
		if (dotExecutablePath != null) {
			File dotFile = DotFileUtils.write(new DotExport().exportDot(graph));
			File image = DotExecutableUtils.renderImage(
					new File(dotExecutablePath), dotFile, "pdf", null, null); //$NON-NLS-1$
			Assert.assertNotNull("Image must not be null", image); //$NON-NLS-1$
			System.out.println("Created image: " + image); //$NON-NLS-1$
			Assert.assertTrue("Image must exist", image.exists()); //$NON-NLS-1$
		}
	}

	/**
	 * @return The path of the local Graphviz DOT executable, as specified in
	 *         the test.properties file
	 */
	private static String getDotExecutablePath() {
		if (dotExecutablePath == null) {
			Properties props = new Properties();
			InputStream stream = DotExecutableUtilsTests.class
					.getResourceAsStream("test.properties"); //$NON-NLS-1$
			if (stream == null) {
				System.err.println(
						"Could not load the test.properties file in directory of " //$NON-NLS-1$
								+ DotExecutableUtilsTests.class
										.getSimpleName());
			} else
				try {
					props.load(stream);
					/*
					 * Path to the local Graphviz DOT executable file
					 */
					dotExecutablePath = props.getProperty(
							GraphvizPreferencePage.DOT_PATH_PREF_KEY);
					if (dotExecutablePath == null
							|| dotExecutablePath.trim().length() == 0) {
						System.err.printf(
								"Graphviz DOT executable path not set in test.properties file under '%s' key.\n", //$NON-NLS-1$
								GraphvizPreferencePage.DOT_PATH_PREF_KEY);
					} else
						stream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
		}
		return dotExecutablePath;
	}

}
