/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import java.io.File;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.internal.dot.DotImport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;

/**
 * Util class for the tests of the {@link DotImport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotImportTestUtils {
	private DotImportTestUtils() { /* Enforce non-instantiability */
	}

	static final String RESOURCES_TESTS = "resources/tests/"; //$NON-NLS-1$

	static void importFrom(final File dotFile) {
		Assert.assertTrue("DOT input file must exist: " + dotFile, //$NON-NLS-1$
				dotFile.exists());
		Graph zest = new DotImport(dotFile).newGraphInstance(new Shell(),
				SWT.NONE);
		Assert.assertNotNull("Resulting graph must not be null", zest); //$NON-NLS-1$
		System.out.println(String.format(
				"Transformed DOT in '%s' to Zest graph '%s'", dotFile, zest)); //$NON-NLS-1$
	}
}
