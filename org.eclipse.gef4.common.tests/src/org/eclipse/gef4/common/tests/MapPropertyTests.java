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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.common.properties.KeyedPropertyChangeEvent;
import org.eclipse.gef4.common.properties.MapProperty;
import org.junit.Test;

public class MapPropertyTests {

	private static class ExpectingKeyedObjectObserver
			extends ExpectingMapObserver {

		private Object key;

		public ExpectingKeyedObjectObserver(String propertyName) {
			super(propertyName);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			assertTrue(evt instanceof KeyedPropertyChangeEvent);
			assertEquals(key, ((KeyedPropertyChangeEvent) evt).getKey());
		}

		public void setExpectation(Object key, Object oldValue,
				Object newValue) {
			super.setExpectation(oldValue, newValue);
			this.key = key;
		}
	}

	private static class ExpectingMapObserver
			implements PropertyChangeListener {

		private String propertyName;
		private Object newValue;
		private Object oldValue;

		public ExpectingMapObserver(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			assertEquals(propertyName, evt.getPropertyName());
			assertEquals(oldValue, evt.getOldValue());
			assertEquals(newValue, evt.getNewValue());
		}

		public void setExpectation(Object oldValue, Object newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
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
		ExpectingMapObserver obs = new ExpectingMapObserver("test");
		map.addPropertyChangeListener(obs);

		assertTrue(map.isEmpty());

		// put
		obs.setExpectation(Collections.emptyMap(), map("a", 1, "b", 2, "c", 3));
		map.putAll(map("b", 2, "c", 3, "a", 1));

		// put and replace
		obs.setExpectation(map("a", 1, "b", 2, "c", 3),
				map("a", 0, "b", 2, "c", 3, "d", 4, "e", 5));
		map.putAll(map("a", 0, "d", 4, "e", 5));

		// clear
		obs.setExpectation(map("a", 0, "b", 2, "c", 3, "d", 4, "e", 5),
				Collections.emptyMap());
		map.clear();

		assertTrue(map.isEmpty());
	}

	@Test
	public void test_putRemove_single() {
		MapProperty<String, Integer> map = new MapProperty<>(new Object(),
				"test");
		ExpectingKeyedObjectObserver obs = new ExpectingKeyedObjectObserver(
				"test");
		map.addPropertyChangeListener(obs);

		assertTrue(map.isEmpty());

		// put
		obs.setExpectation("a", null, 1);
		map.put("a", 1);

		obs.setExpectation("b", null, 2);
		map.put("b", 2);

		// replace
		obs.setExpectation("a", 1, 0);
		map.put("a", 0);

		// remove
		obs.setExpectation("a", 0, null);
		map.remove("a");

		obs.setExpectation("b", 2, null);
		map.remove("b");

		assertTrue(map.isEmpty());
	}

}
