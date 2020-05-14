/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author sschwieb
 *
 */
public class TypeCollector {

	private static String stopWords;

	public static List<Type> getData(File file, String encoding) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		BufferedReader br = new BufferedReader(new InputStreamReader(bis, encoding));
		StringBuffer text = new StringBuffer();
		String s;
		while ((s = br.readLine()) != null) {
			text.append(s + "\n");
		}
		br.close();
		Set<String> stops = new HashSet<>();
		if (stopWords != null) {
			bis = new BufferedInputStream(new FileInputStream(stopWords));
			br = new BufferedReader(new InputStreamReader(bis, encoding));
			while ((s = br.readLine()) != null) {
				stops.add(s.toLowerCase().trim());
			}
			br.close();
		}
		BreakIterator iterator = BreakIterator.getWordInstance(Locale.getDefault());
		String txt = text.toString();
		iterator.setText(txt);
		final Map<String, Integer> strings = new HashMap<>();
		int boundary = iterator.first();
		int lastBoundary = iterator.first();
		while (boundary != BreakIterator.DONE) {
			boundary = iterator.next();
			if (boundary != -1) {
				String string = txt.substring(lastBoundary, boundary).trim();
				if (string.length() != 0) {
					if (!Character.isLetter(string.charAt(string.length() - 1))) {
						string = string.substring(0, string.length() - 1);
					}
					if (stops.contains(string.toLowerCase()) || string.trim().length() <= 1) {
						lastBoundary = boundary;
						continue;
					}
					Integer count = strings.get(string);
					if (count == null) {
						strings.put(string, 1);
					} else {
						count = count + 1;
						strings.put(string, count);
					}
				}
			}
			lastBoundary = boundary;
		}
		return getMostImportantTypes(strings);
	}

	private static List<Type> getMostImportantTypes(final Map<String, Integer> strings) {
		List<Type> types = new ArrayList<>();
		Iterator<Entry<String, Integer>> iterator = strings.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next();
			Type type = new Type(entry.getKey(), entry.getValue());
			types.add(type);
		}
		List<Type> sorted = new ArrayList<>(types);
		Collections.sort(sorted, new Comparator<Type>() {

			@Override
			public int compare(Type o1, Type o2) {
				return o2.getOccurrences() - o1.getOccurrences();
			}
		});
		return sorted;
	}

	public static void setStopwords(String sourceFile) {
		stopWords = sourceFile;
	}
}
