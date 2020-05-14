/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Fabian Steeg (hbz) - initial API and implementation
 *      Tamas Miklossy (itemis AG) - convert the DotExtractor to a top-level class (bug #508579)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The DotExtractor is responsible for extracting the dot graph definitions from
 * arbitrary text files.
 */
public class DotExtractor {

	/**
	 * The DOT graph returned if the input contains no DOT graph substring.
	 */
	private final String NO_DOT = "graph{n1[label=\"no DOT\"]}"; //$NON-NLS-1$
	private String input = NO_DOT;

	/**
	 * @param input
	 *            The string to extract a DOT graph substring from
	 */
	public DotExtractor(final String input) {
		this.input = input;
	}

	/**
	 * @param file
	 *            The file to extract a DOT substring from
	 */
	public DotExtractor(final File file) {
		this(DotFileUtils.read(file));
	}

	/**
	 * @return A DOT string extracted from the input, or the {@code NO_DOT}
	 *         constant, a valid DOT graph
	 */
	public String getDotString() {
		return trimNonDotSuffix(trimNonDotPrefix());
	}

	/**
	 * @return A temporary file containing the DOT string extracted from the
	 *         input, or the {@code NO_DOT} constant, a valid DOT graph
	 */
	public File getDotTempFile() {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tempDotExtractorFile", ".dot"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException e) {
			System.err.println("DotExtractor failed to create temp dot file"); //$NON-NLS-1$
			e.printStackTrace();
		}

		if (tempFile != null) {
			// use try-with-resources to utilize the AutoClosable
			// functionality
			try (BufferedWriter bw = new BufferedWriter(
					new FileWriter(tempFile))) {
				bw.write(getDotString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return tempFile;
	}

	private String trimNonDotPrefix() {
		Matcher m = Pattern.compile("((?:di)?graph\\s*[^{\\s]*\\s*\\{.+)", //$NON-NLS-1$
				Pattern.DOTALL).matcher(input);
		String dotSubstring = m.find() ? m.group(1) : NO_DOT;
		return dotSubstring;
	}

	private String trimNonDotSuffix(String dot) {
		int first = dot.indexOf('{') + 1;
		StringBuilder builder = new StringBuilder(dot.substring(0, first));
		int count = 1; /* we count to include embedded { ... } blocks */
		int index = first;
		while (count > 0 && index < dot.length()) {
			char c = dot.charAt(index);
			builder.append(c);
			count = (c == '{') ? count + 1 : (c == '}') ? count - 1 : count;
			index++;
		}
		return builder.toString().trim();
	}
}
