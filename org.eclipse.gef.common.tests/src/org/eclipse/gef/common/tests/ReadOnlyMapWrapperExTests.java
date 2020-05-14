/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.common.tests.MapPropertyExTests.MapChangeExpector;
import org.junit.Test;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.FXCollections;

public class ReadOnlyMapWrapperExTests {

	@Test
	public void readOnlyWrapperChangeNotifications() {
		ReadOnlyMapWrapperEx<String, Integer> mapWrapper = new ReadOnlyMapWrapperEx<>(
				FXCollections.observableHashMap());
		ReadOnlyMapProperty<String, Integer> roProperty = mapWrapper
				.getReadOnlyProperty();
		MapChangeExpector<String, Integer> mapChangeListener = new MapChangeExpector<>(
				roProperty);
		roProperty.addListener(mapChangeListener);
		mapChangeListener.addExpectation("key1", null, 1);
		roProperty.put("key1", 1);
		mapChangeListener.check();
		mapChangeListener.addExpectation("key2", null, 2);
		roProperty.put("key2", 2);
		mapChangeListener.check();
		mapChangeListener.addExpectation("key1", 1, null);
		roProperty.remove("key1");
		mapChangeListener.check();
		mapChangeListener.addExpectation("key2", 2, null);
		roProperty.remove("key2");
		mapChangeListener.check();
	}
}
