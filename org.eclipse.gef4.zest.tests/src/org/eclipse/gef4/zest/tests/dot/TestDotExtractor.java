/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.zest.internal.dot.DotExtractor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotExtractor}.
 * 
 * @author Fabian Steeg (fsteeg)
 */
@SuppressWarnings("serial")
public class TestDotExtractor {
	/* Testing input and output values: */
	Map<String, String> values = new HashMap<String, String>() {
		{
			put("/** Javadoc stuff graph name{a;b;a->b} and more */", //$NON-NLS-1$
					"graph name{a;b;a->b}"); //$NON-NLS-1$
			put("/** Javadoc stuff graph long_name{a;b;a->b} and more */", //$NON-NLS-1$
					"graph long_name{a;b;a->b}"); //$NON-NLS-1$
			put("/* Java block comment \n stuff digraph{a;b;a->b} and more */", //$NON-NLS-1$
					"digraph{a;b;a->b}"); //$NON-NLS-1$
			put("Stuff about a graph and then graph{a;b;a->b} and more ", //$NON-NLS-1$
					"graph{a;b;a->b}"); //$NON-NLS-1$
			put("Stuff about a graph and then with breaks graph{a\nb\na->b} and more ", //$NON-NLS-1$
			"graph{a\nb\na->b}"); //$NON-NLS-1$
			put("Stuff about a graph and then digraph{a;b;a->b} and more ", //$NON-NLS-1$
					"digraph{a;b;a->b}"); //$NON-NLS-1$
			put("Stuff about a graph and then digraph{subgraph cluster_0{1->2}; 1->3} and more ", //$NON-NLS-1$
			"digraph{subgraph cluster_0{1->2}; 1->3}"); //$NON-NLS-1$
			put("Stuff about a graph then graph{node[shape=record];1[label=\"{Text|Text}\"]} and more", //$NON-NLS-1$
			"graph{node[shape=record];1[label=\"{Text|Text}\"]}"); //$NON-NLS-1$
		}
	};

	@Test
	public void extractDot() {
		for (String input : values.keySet()) {
			String expected = values.get(input);
			String output = new DotExtractor(input).getDotString();
			Assert.assertEquals(/*
								 * String.format(
								 * "Incorrect DOT extraction for '%s';", input),
								 */expected, output);
		}
	}
}
