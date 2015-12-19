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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.common.properties.MapProperty;
import org.junit.Test;

public class MapPropertyTests {

	private static class ExpectingMapObserver<K, V>
			implements PropertyChangeListener {

		private Map<K, V> expectationNew = Collections.emptyMap();
		private Map<K, V> expectationOld = Collections.emptyMap();

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			assertTrue(expectationOld.equals(evt.getOldValue()));
			assertTrue(expectationNew.equals(evt.getNewValue()));
		}

		public void setExpectation(Map<K, V> elements) {
			expectationOld = expectationNew;
			expectationNew = elements;
		}
	}

	private static class ExpectingObjectObserver<T>
			implements PropertyChangeListener {
		private Object expectationNew = null;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			assertTrue(expectationNew == null ? evt.getNewValue() == null
					: expectationNew.equals(evt.getNewValue()));
		}

		public void setExpectation(Object expectation) {
			expectationNew = expectation;
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
		MapProperty<String, Integer> map = new MapProperty<>(new Object(),
				"test");
		ExpectingMapObserver<String, Integer> obs = new ExpectingMapObserver<>();
		map.addPropertyChangeListener(obs);

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
		MapProperty<String, Integer> map = new MapProperty<>(new Object(),
				"test");
		ExpectingObjectObserver<Integer> obs = new ExpectingObjectObserver<>();
		map.addPropertyChangeListener(obs);

		assertTrue(map.isEmpty());

		// put
		obs.setExpectation(1);
		map.put("a", 1);

		obs.setExpectation(2);
		map.put("b", 2);

		// replace
		obs.setExpectation(0);
		map.put("a", 0);

		// remove
		obs.setExpectation(null);
		map.remove("a");

		obs.setExpectation(null);
		map.remove("b");

		assertTrue(map.isEmpty());
	}

}
