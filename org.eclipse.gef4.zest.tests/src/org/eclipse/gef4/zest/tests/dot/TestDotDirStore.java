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

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef4.zest.internal.dot.DotDirStore;
import org.eclipse.gef4.zest.internal.dot.DotUiActivator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link DotDirStore}.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestDotDirStore {
	@Before
	public void setup() {
		if (!Platform.isRunning()) {
			Assert.fail("Please run as JUnit Plug-in test"); //$NON-NLS-1$
		}
		Assert.assertNotNull(
				"TestImageExport.DOT_DIR should point to the directory containing the local Graphviz DOT executable;", //$NON-NLS-1$
				TestImageExport.dotBinDir());
	}

	@Test
	public void askForDotDir() {
		/*
		 * Setting the value to the empty string here blocks UI every time, but
		 * tests asking even if clearing workspace is disabled:
		 */
		DotUiActivator.getDefault().getPreferenceStore()
				.setValue(DotDirStore.DOTPATH_KEY, TestImageExport.dotBinDir());
		/* If not set, the DOT dir is requested: */
		check(DotDirStore.getDotDirPath());
	}

	@Test
	public void getDotDirFromPrefs() {
		/* If set, the DOT dir is returned: */
		check(DotDirStore.getDotDirPath());
	}

	public void check(final String path) {
		Assert.assertNotNull("Path to dot directory should not be null", path); //$NON-NLS-1$
		System.out.println("Dot directory: " + path); //$NON-NLS-1$
		Assert.assertTrue("Dot path should not be empty", //$NON-NLS-1$
				path.trim().length() > 0);
		Assert.assertTrue("Dot directory should exist", new File(path).exists()); //$NON-NLS-1$
		Assert.assertTrue("Dot path should point to a directory", //$NON-NLS-1$
				new File(path).isDirectory());
	}
}
