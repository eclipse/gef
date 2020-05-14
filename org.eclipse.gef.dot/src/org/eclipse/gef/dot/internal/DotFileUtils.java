/*******************************************************************************
 * Copyright (c) 2009, 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg    - initial API and implementation (bug #277380)
 *     Tamas Miklossy  - usage of platform specific line separators (bug #490118)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Static helper methods for working with files.
 *
 * @author Fabian Steeg (fsteeg)
 */
public final class DotFileUtils {

	private DotFileUtils() {
		/* Enforce non-instantiability */
	}

	/**
	 * @param url
	 *            The URL to resolve (can be workspace-relative)
	 * @return The file corresponding to the given URL
	 */
	public static File resolve(final URL url) {
		File resultFile = null;
		URL resolved = url;
		/*
		 * If we don't check the protocol here, the FileLocator throws a
		 * NullPointerException if the URL is a normal file URL.
		 */
		if (!url.getProtocol().equals("file")) { //$NON-NLS-1$
			throw new IllegalArgumentException("Unsupported protocol: " //$NON-NLS-1$
					+ url.getProtocol());
		}
		try {
			resultFile = new File(resolved.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return resultFile;
	}

	/**
	 * @param text
	 *            The string to write out to a temp file
	 * @return The temp file containing the given string
	 */
	public static File write(final String text) {
		try {
			return write(text, File.createTempFile("tmp", ".dot")); //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param text
	 *            The string to write out to a file
	 * @param destination
	 *            The file to write the string to
	 * @return The file containing the given string
	 */
	public static File write(final String text, final File destination) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(destination), "UTF-8"));
			writer.write(text);
			writer.flush();
			writer.close();
			return destination;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param file
	 *            The file to read into a string
	 * @return The string containing the contents of the given file
	 */
	public static String read(final File file) {
		try {
			return read(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Reads a string from the given input stream.
	 *
	 * @param is
	 *            The input stream to read.
	 * @return The contents of the input stream as a {@link String}
	 * @throws IOException
	 *             In case I/O exceptions occurred.
	 */
	public static String read(InputStream is) throws IOException {
		String lineSeparator = System.lineSeparator();
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, "UTF-8"));
		String line = reader.readLine();
		while (line != null) {
			builder.append(line).append(lineSeparator);
			line = reader.readLine();
		}
		reader.close();
		return builder.toString();
	}
}
