/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.internal.dot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Class for drawing dot graphs by calling the dot executable.
 * 
 * @author Fabian Steeg (fsteeg)
 */
final class DotDrawer {
	private DotDrawer() {/* Enforce non-instantiability */
	}

	static File renderImage(final File dotExecutableDir,
			final File dotInputFile, final String format,
			final String imageResultFile) {
		String outputFormat = "-T" + format; //$NON-NLS-1$
		String resultFile = imageResultFile == null ? dotInputFile.getName()
				+ "." + format : imageResultFile; //$NON-NLS-1$
		String dotFile = dotInputFile.getName();
		String inputFolder = new File(dotInputFile.getParent())
				.getAbsolutePath() + File.separator;
		String outputFolder = imageResultFile == null ? inputFolder : new File(
				new File(imageResultFile).getAbsolutePath()).getParentFile()
				.getAbsolutePath()
				+ File.separator;
		String dotExecutable = "dot" + (runningOnWindows() ? ".exe" : ""); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		String[] commands = new String[] {
				dotExecutableDir.getAbsolutePath() + File.separator
						+ dotExecutable, outputFormat, "-o", //$NON-NLS-1$
				outputFolder + resultFile, inputFolder + dotFile };
		call(commands);
		return new File(outputFolder, resultFile);
	}

	private static void call(final String[] commands) {
		System.out.print("Calling: " + Arrays.asList(commands)); //$NON-NLS-1$
		Runtime runtime = Runtime.getRuntime();
		Process p = null;
		try {
			p = runtime.exec(commands);
			p.waitFor();
		} catch (Exception x) {
			x.printStackTrace();
		}
		System.out
				.println(", " + "resulted in exit status" + ": " + p.exitValue()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		String errors = read(p.getErrorStream());
		String output = read(p.getInputStream());
		if (errors.trim().length() > 0) {
			System.err.println("Errors from dot call: " + errors);
		}
		if (output.trim().length() > 0) {
			System.out.println("Output from dot call: " + output);
		}
	}

	private static String read(InputStream s) {
		StringBuilder builder = new StringBuilder();
		try {
			int current = -1;
			while ((current = s.read()) != -1) {
				builder.append((char) current);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	private static boolean runningOnWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win"); //$NON-NLS-1$//$NON-NLS-2$
	}

}
