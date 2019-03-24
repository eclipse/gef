/*******************************************************************************
 * Copyright (c) 2018, 2019 itemis AG and others.
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
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.gef.dot.internal.ui.preferences.GraphvizPreferencePage;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractDotExecutableTests {

	@BeforeClass
	public static void setup() throws IOException {
		// dotExecutablePath = getDotExecutablePath();
		// verifyDotExecutablePath(dotExecutablePath);
	}

	protected abstract void test(String inputFileName);

	@Test
	public void test_arrowshapes_deprecated() {
		test("arrowshapes_deprecated.dot");
	}

	@Test
	public void test_arrowshapes_direction_both() {
		test("arrowshapes_direction_both.dot");
	}

	@Test
	public void test_arrowshapes_invalid_modifiers() {
		test("arrowshapes_invalid_modifiers.dot");
	}

	@Test
	public void test_arrowshapes_multiple() {
		test("arrowshapes_multiple.dot");
	}

	@Test
	public void test_arrowshapes_single() {
		test("arrowshapes_single.dot");
	}

	@Test
	public void test_color() {
		test("color.dot");
	}

	@Test
	public void test_colored_graph() {
		test("colored_graph.dot");
	}

	@Test
	public void test_colorscheme() {
		test("colorscheme.dot");
	}

	@Test
	public void test_er() {
		test("er.dot");
	}

	@Test
	public void test_fancy_graph() {
		test("fancy_graph.dot");
	}

	@Test
	public void test_grdfillcolor() {
		test("grdfillcolor.dot");
	}

	@Test
	public void test_grdlinear_angle() {
		test("grdlinear_angle.dot");
	}

	@Test
	public void test_grdlinear_node() {
		test("grdlinear_node.dot");
	}

	@Test
	public void test_grdlinear() {
		test("grdlinear.dot");
	}

	@Test
	public void test_grdradial_angle() {
		test("grdradial_angle.dot");
	}

	@Test
	public void test_grdradial_node() {
		test("grdradial_node.dot");
	}

	@Test
	public void test_grdradial() {
		test("grdradial.dot");
	}

	@Test
	public void test_grdshapes() {
		test("grdshapes.dot");
	}

	@Test
	public void test_html_like_labels1() {
		test("html_like_labels1.dot");
	}

	@Test
	public void test_html_like_labels2() {
		test("html_like_labels2.dot");
	}

	@Test
	public void test_html_like_labels4() {
		test("html_like_labels4.dot");
	}

	@Test
	public void test_labeled_graph() {
		test("labeled_graph.dot");
	}

	@Test
	public void test_nodeshapes_polygon_based() {
		test("nodeshapes_polygon_based.dot");
	}

	@Test
	public void test_philo() {
		test("philo.dot");
	}

	@Test
	public void test_record_shape_node1() {
		test("record_shape_node1.dot");
	}

	@Test
	public void test_simple_digraph() {
		test("simple_digraph.dot");
	}

	@Test
	public void test_simple_graph() {
		test("simple_graph.dot");
	}

	@Test
	public void test_styled_graph() {
		test("styled_graph.dot");
	}

	@Test
	public void test_styled_graph2() {
		test("styled_graph2.dot");
	}

	@Test
	public void test_switch() {
		test("switch.dot");
	}

	protected static String dotExecutablePath = null;

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

	protected static void verifyDotExecutablePath(String dotExecutablePath) {
		boolean isDotExecutableExists = new File(dotExecutablePath).exists();
		assertTrue("Cannot find dot executable under " + dotExecutablePath,
				isDotExecutableExists);
	}

}
