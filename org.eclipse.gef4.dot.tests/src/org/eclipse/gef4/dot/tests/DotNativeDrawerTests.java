/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg, and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Refactoring of preferences (bug #446639)
 *     
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.gef4.dot.internal.DotExport;
import org.eclipse.gef4.dot.internal.DotFileUtils;
import org.eclipse.gef4.dot.internal.DotNativeDrawer;
import org.eclipse.gef4.dot.internal.ui.GraphvizPreferencePage;
import org.eclipse.gef4.graph.Graph;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Tests for the {@link DotExport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 * @author Tamas Miklossy
 */
public class DotNativeDrawerTests extends DotTemplateTests {

	private static String dotExecutablePath = null;

	@BeforeClass
	public static void setup() throws IOException {
		dotExecutablePath = getDotExecutablePath();
	}

	/**
	 * @return The path of the local Graphviz DOT executable, as specified in
	 *         the test.properties file
	 */
	public static String getDotExecutablePath() {
		if (dotExecutablePath == null) {
			Properties props = new Properties();
			InputStream stream = DotNativeDrawerTests.class
					.getResourceAsStream("test.properties"); //$NON-NLS-1$
			if (stream == null) {
				System.err.println(
						"Could not load the test.properties file in directory of " //$NON-NLS-1$
								+ DotNativeDrawerTests.class.getSimpleName());
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

	@Override
	protected void testDotGeneration(final Graph graph) {
		/*
		 * The DotExport class wraps the simple DotTemplate class, so when we
		 * test DotExport, we also run the test in the test superclass:
		 */
		if (dotExecutablePath != null) {
			super.testDotGeneration(graph);
			File dotFile = DotFileUtils
					.write(new DotExport(graph).toDotString());
			File image = DotNativeDrawer.renderImage(
					new File(dotExecutablePath), dotFile, "pdf", null); //$NON-NLS-1$
			Assert.assertNotNull("Image must not be null", image); //$NON-NLS-1$
			System.out.println("Created image: " + image); //$NON-NLS-1$
			Assert.assertTrue("Image must exist", image.exists()); //$NON-NLS-1$
		}
	}

}
