/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.tests;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.common.notify.IMapObserver;
import org.eclipse.gef4.common.notify.ObservableMap;
import org.junit.Test;

public class ObservableMapTests {

	private static class ExpectingMapObserver<K, V>
			implements IMapObserver<K, V> {
		private Map<K, V> expectationNew = Collections.emptyMap();
		private Map<K, V> expectationOld = Collections.emptyMap();

		@Override
		public void afterChange(ObservableMap<K, V> observableMap,
				Map<K, V> previousMap) {
			assertTrue(expectationOld.equals(previousMap));
			assertTrue(expectationNew.equals(observableMap));
		}

		public void setExpectation(Map<K, V> elements) {
			expectationOld = expectationNew;
			expectationNew = elements;
		}
	}

	private Map<String, Integer> map(Object... pairs) {
		HashMap<String, Integer> map = new HashMap<>();
		for (int i = 0; i < pairs.length; i += 2) {
			String key = (String) pairs[i];
			Integer value = (Integer) pairs[i + 1];
			map.put(key, value);
		}
		return map;
	}

	@Test
	public void test_putRemove_multi() {
		ObservableMap<String, Integer> map = new ObservableMap<>();
		ExpectingMapObserver<String, Integer> obs = new ExpectingMapObserver<>();
		map.addMapObserver(obs);

		assertTrue(map.isEmpty());

		// put
		obs.setExpectation(map("a", 1, "b", 2, "c", 3));
		map.putAll(map("b", 2, "c", 3, "a", 1));

		// put and replace
		obs.setExpectation(map("a", 0, "b", 2, "c", 3, "d", 4, "e", 5));
		map.putAll(map("a", 0, "d", 4, "e", 5));

		// clear
		obs.setExpectation(Collections.<String, Integer> emptyMap());
		map.clear();

		assertTrue(map.isEmpty());
	}

	@Test
	public void test_putRemove_single() {
		ObservableMap<String, Integer> map = new ObservableMap<>();
		ExpectingMapObserver<String, Integer> obs = new ExpectingMapObserver<>();
		map.addMapObserver(obs);

		assertTrue(map.isEmpty());

		// put
		obs.setExpectation(map("a", 1));
		map.put("a", 1);

		obs.setExpectation(map("a", 1, "b", 2));
		map.put("b", 2);

		// replace
		obs.setExpectation(map("a", 0, "b", 2));
		map.put("a", 0);

		// remove
		obs.setExpectation(map("b", 2));
		map.remove("a");

		obs.setExpectation(Collections.<String, Integer> emptyMap());
		map.remove("b");

		assertTrue(map.isEmpty());
	}

}
