/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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
package org.eclipse.gef.dot.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractDotExecutableImageExportTest
		extends AbstractDotExecutableTest {

	/**
	 * Execute the following tests only as part of the image export tests (and
	 * not part of the layouting export tests), because the order of the
	 * clusters can differ making the layouting tests failing.
	 */
	@Test
	public void test_clustered_graph() {
		test("clustered_graph.dot");
	}

	@Test
	public void test_grdangles() {
		test("grdangles.dot");
	}

	@Test
	public void test_grdcluster() {
		test("grdcluster.dot");
	}

	@Test
	public void test_grdcolors() {
		test("grdcolors.dot");
	}

	protected void dotImageExport(String fileName, String format) {
		if (dotExecutablePath != null) {
			File inputFile = DotTestUtils.file(fileName);
			File outputFile = null;
			try {
				outputFile = File.createTempFile("tmp_"
						+ fileName.substring(0, fileName.lastIndexOf('.')),
						"." + format);
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail("Cannot create temporary file" + e.getMessage());
			}
			String[] outputs = new String[2];
			File image = DotExecutableUtils.renderImage(
					new File(dotExecutablePath), inputFile, format, // $NON-NLS-1$
					outputFile, outputs);

			Assert.assertEquals(
					"The dot executable produced the following errors:", "",
					outputs[1]);

			Assert.assertNotNull("Image must not be null", image); //$NON-NLS-1$
			System.out.println("Created image: " + image); //$NON-NLS-1$
			Assert.assertTrue("Image must exist", image.exists()); //$NON-NLS-1$
		}
	}

}
