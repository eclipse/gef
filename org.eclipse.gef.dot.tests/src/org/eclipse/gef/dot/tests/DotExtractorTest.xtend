/*******************************************************************************
 * Copyright (c) 2009, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *     Fabian Steeg - initial API and implementation; see bug 277380
 *     Alexander Nyßen (itemis AG) - rename refactoring
 *     Tamas Miklossy  (itemis AG) - minor refactoring
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.internal.DotExtractor
import org.junit.Assert
import org.junit.Test

/**
 * Tests for the {@link DotExtractor}.
 * 
 * @author Fabian Steeg (fsteeg)
 */
class DotExtractorTest {

	@Test def dot_extraction_01() {
		'''
			/** 
				Javadoc stuff 
					graph name {
						a;
						b;
						a--b
					} 
				and more 
			*/
	 	'''.assertExtractedTo('''
			graph name {
						a;
						b;
						a--b
					}
		''')
	}

	@Test def dot_extraction_02() {
		'''
			/** Javadoc stuff 
			graph long_name {
				a;
				b;
				a--b
			} and more */
		'''.assertExtractedTo('''
			graph long_name {
				a;
				b;
				a--b
			}
		''')
	}

	@Test def dot_extraction_03() {
		'''
			/* Java block comment 
				stuff 
			digraph {
				a;
				b;
				a->b
			} and more */'''
		.assertExtractedTo('''
			digraph {
				a;
				b;
				a->b
			}
		''')
	}

	@Test def dot_extraction_04() {
		'''
			Stuff about a graph and then 
			graph {
				a;
				b;
				a--b
			} and more 
		'''.assertExtractedTo('''
			graph {
				a;
				b;
				a--b
			}
		''')
	}

	@Test def dot_extraction_05() {
		'''
			Stuff about a graph and then with breaks 
				graph{
					a
					b
					a--b
				} and more 
		'''.assertExtractedTo('''
			graph{
					a
					b
					a--b
				}
		''')
	}

	@Test def dot_extraction_06() {
		'''
			Stuff about a graph and then digraph{a;b;a->b} and more 
		'''.assertExtractedTo('''
			digraph{a;b;a->b}
		''')
	}

	@Test def dot_extraction_07() {
		'''
			Stuff about a graph and then 
			digraph {
				subgraph cluster_0 {
					1->2
				}; 
				1->3
			} and more 
		'''.assertExtractedTo('''
			digraph {
				subgraph cluster_0 {
					1->2
				}; 
				1->3
			}
		''')
	}

	@Test def dot_extraction_08() {
		'''
			Stuff about a graph then 
				graph {
					node[shape=record];
					1[label="{Text|Text}"]
				} and more
		'''.assertExtractedTo('''
			graph {
					node[shape=record];
					1[label="{Text|Text}"]
				}
		''')
	}

	private def assertExtractedTo(CharSequence embedded, CharSequence expected) {
		var extracted = new DotExtractor(embedded.toString).dotString
		Assert.assertEquals(String.format("Incorrect DOT extraction for '%s';", embedded), expected.toString.trim, extracted)
	}
}
