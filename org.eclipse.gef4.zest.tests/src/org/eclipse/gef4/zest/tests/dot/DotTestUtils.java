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

import org.junit.Assert;

/**
 * Util class for different tests.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotTestUtils {
	private DotTestUtils() { /* Enforce non-instantiability */
	}

	/**
	 * Wipes (does not delete hidden files and files starting with a '.') the
	 * given output folder used for generated files during testing and makes
	 * sure it contains no files with the given extension.
	 * 
	 * @param location
	 *            The folder to wipe of all files with the given extension
	 * @param suffix
	 *            The extension of the files to delete in the given output
	 *            folder
	 */
	public static void wipeOutput(final File location, final String suffix) {
		String[] files = location.list();
		int deleted = 0;
		if (files != null && files.length > 0) {
			for (String file : files) {
				File deletionCandidate = new File(location, file);
				/*
				 * Relying on hidden is not safe on all platforms, so we double
				 * check so that no .cvsignore files etc. are deleted:
				 */
				if (!deletionCandidate.isHidden()
						&& !deletionCandidate.getName().startsWith(".")) { //$NON-NLS-1$
					boolean delete = deletionCandidate.delete();
					if (delete) {
						deleted++;
					}
				}
			}
			int dotFiles = countFilesWithSuffix(location, suffix);
			Assert.assertEquals(
					"Default output directory should contain no files matching the suffix before tests run;", //$NON-NLS-1$
					0, dotFiles);
			System.out.println(String.format("Deleted %s files in %s", deleted, //$NON-NLS-1$
					location));
		}
	}

	private static int countFilesWithSuffix(final File folder,
			final String suffix) {
		String[] list = folder.list();
		int dotFiles = 0;
		for (String name : list) {
			if (name.endsWith(suffix)) {
				dotFiles++;
			}
		}
		return dotFiles;
	}
}
