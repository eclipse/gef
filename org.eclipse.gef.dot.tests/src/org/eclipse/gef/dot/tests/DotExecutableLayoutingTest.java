/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy     (itemis AG) - initial API and implementation
 *     Zoey Gerrit Prigge (itemis AG) - added switch to ignore whitespace
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.gef.dot.internal.DotExecutableUtils;
import org.junit.Test;

public class DotExecutableLayoutingTest extends AbstractDotExecutableTest {

	/**
	 * Execute the following test only as part of the layouting tests (and not
	 * part of the image export tests), because the used font 'ambrosia
	 * Not-Rotated 24' is not available making the image export tests failing.
	 */
	@Test
	public void test_html_like_labels3() {
		test("html_like_labels3.dot");
	}

	@Override
	protected void test(String name) {
		// expected
		String dotExecutableInstalled = "TODO: specify the path to the installed graphviz dot executable here";

		// actual
		String dotExecutableBuild = "TODO: specify the path to the self-build graphviz dot executable here";

		// whitespace differences in Layout output will be ignored if true
		// for testing builds from different build-mechanisms

		boolean ignoreWhitespace = false;

		if (!new File(dotExecutableInstalled).exists()
				|| !new File(dotExecutableBuild).exists()) {
			// ensure that the tests are only executed if the paths are properly
			// defined
			return;
		}

		String expected = dotLayout(dotExecutableInstalled, name);
		String actual = dotLayout(dotExecutableBuild, name);

		if (ignoreWhitespace) {
			assertEquals(expected.replaceAll("\\s+", " "),
					actual.replaceAll("\\s+", " "));
		} else {
			assertEquals(expected, actual);
		}
	}

	private String dotLayout(String dotExecutablePath, String fileName) {
		verifyDotExecutablePath(dotExecutablePath);
		File inputFile = DotTestUtils.file(fileName);
		String[] dotResult = DotExecutableUtils.executeDot(
				new File(dotExecutablePath), true, inputFile, null, null);
		// if (!dotResult[1].isEmpty()) {
		// System.err.println(dotResult[1]);
		// }

		return dotResult[0];
	}

}
