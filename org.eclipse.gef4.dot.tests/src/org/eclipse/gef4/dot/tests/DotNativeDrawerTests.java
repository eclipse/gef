/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.gef4.dot.DotExport;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.internal.dot.DotNativeDrawer;
import org.eclipse.gef4.internal.dot.DotFileUtils;
import org.eclipse.gef4.internal.dot.ui.DotDirStore;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Tests for the {@link DotExport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotNativeDrawerTests extends DotTemplateTests {

	private static String dotDir = null;

	@BeforeClass
	public static void setup() throws IOException {
		dotDir = dotBinDir();
	}

	/**
	 * @return The directory containing the local Graphviz DOT executable, as
	 *         specified in the test.properties file
	 */
	public static String dotBinDir() {
		if (dotDir == null) {
			Properties props = new Properties();
			InputStream stream = DotNativeDrawerTests.class
					.getResourceAsStream("test.properties"); //$NON-NLS-1$
			if (stream == null) {
				System.err
						.println("Could not load the test.properties file in directory of " //$NON-NLS-1$
								+ DotNativeDrawerTests.class.getSimpleName());
			} else
				try {
					props.load(stream);
					/*
					 * Path to the local Graphviz folder containing the dot
					 * executable file:
					 */
					dotDir = props.getProperty(DotDirStore.DOT_PATH_PREF_KEY);
					if (dotDir == null || dotDir.trim().length() == 0) {
						System.err
								.printf("Graphviz DOT directory not set in test.properties file under '%s' key.\n",//$NON-NLS-1$
										DotDirStore.DOT_PATH_PREF_KEY);
					} else
						stream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
		}
		return dotDir;
	}

	@Override
	protected void testDotGeneration(final Graph graph) {
		/*
		 * The DotExport class wraps the simple DotTemplate class, so when we
		 * test DotExport, we also run the test in the test superclass:
		 */
		if (dotDir != null) {
			super.testDotGeneration(graph);
			File dotFile = DotFileUtils.write(new DotExport(graph)
					.toDotString());
			File image = DotNativeDrawer.renderImage(new File(dotDir), dotFile,
					"pdf", null); //$NON-NLS-1$
			Assert.assertNotNull("Image must not be null", image); //$NON-NLS-1$
			System.out.println("Created image: " + image); //$NON-NLS-1$
			Assert.assertTrue("Image must exist", image.exists()); //$NON-NLS-1$
		}
	}

}
