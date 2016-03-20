/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.graph.Node;
import org.junit.Test;

public class DotPointTests {

	@Test
	public void node_pos() {
		Node n = new Node.Builder().buildNode();

		// set valid values
		DotAttributes.setPos(n, "47, 11");
		DotAttributes.setPos(n, "34.5, 45.3!");

		// set invalid values
		try {
			DotAttributes.setPos(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'pos' to '47x, 11': no viable alternative at character 'x'",
					e.getMessage());
		}
	}

	@Test
	public void node_height() {
		Node n = new Node.Builder().buildNode();

		// set valid values
		DotAttributes.setHeight(n, "0.56");
		DotAttributes.setHeight(n, "76");

		// set invalid values
		try {
			DotAttributes.setHeight(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'height' to '47x, 11': parsing as double failed.",
					e.getMessage());
		}
	}

	@Test
	public void node_width() {
		Node n = new Node.Builder().buildNode();

		// set valid values
		DotAttributes.setWidth(n, "0.56");
		DotAttributes.setWidth(n, "76");

		// set invalid values
		try {
			DotAttributes.setWidth(n, "47x, 11");
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Cannot set node attribute 'width' to '47x, 11': parsing as double failed.",
					e.getMessage());
		}
	}
}
