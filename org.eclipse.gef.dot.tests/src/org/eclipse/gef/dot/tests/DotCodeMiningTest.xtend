/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test cases for the {@link org.eclipse.gef.dot.internal.ui.language.codemining.DotCodeMiningProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotCodeMiningTest extends AbstractCodeMiningTest {

	@Test def code_mining_001() {
		'''
			graph {}
		'''.testCodeMining('''
			0 nodes | 0 edges
			graph {}
		''')
	}

	@Test def code_mining_002() {
		'''
			digraph {}
		'''.testCodeMining('''
			0 nodes | 0 edges
			digraph {}
		''')
	}

	@Test def code_mining_003() {
		'''
			// Comment
			graph {}
		'''.testCodeMining('''
			// Comment
			0 nodes | 0 edges
			graph {}
		''')
	}

	@Test def code_mining_004() {
		'''
			// Comment
			digraph {}
		'''.testCodeMining('''
			// Comment
			0 nodes | 0 edges
			digraph {}
		''')
	}

	@Test def code_mining_005() {
		'''
			/* Comment */
			graph {}
		'''.testCodeMining('''
			/* Comment */
			0 nodes | 0 edges
			graph {}
		''')
	}

	@Test def code_mining_006() {
		'''
			/* Comment */
			digraph {}
		'''.testCodeMining('''
			/* Comment */
			0 nodes | 0 edges
			digraph {}
		''')
	}

	@Test def code_mining_007() {
		'''
			/*
			 * Comment
			 */
			graph {}
		'''.testCodeMining('''
			/*
			 * Comment
			 */
			0 nodes | 0 edges
			graph {}
		''')
	}

	@Test def code_mining_008() {
		'''
			/*
			 * Comment
			 */
			digraph {}
		'''.testCodeMining('''
			/*
			 * Comment
			 */
			0 nodes | 0 edges
			digraph {}
		''')
	}

	@Test def code_mining_009() {
		'''
			graph {
				1
			}
		'''.testCodeMining('''
			1 node | 0 edges
			graph {
				1
			}
		''')
	}

	@Test def code_mining_010() {
		'''
			digraph {
				1
			}
		'''.testCodeMining('''
			1 node | 0 edges
			digraph {
				1
			}
		''')
	}

	@Test def code_mining_011() {
		'''
			graph {
				1
				2
			}
		'''.testCodeMining('''
			2 nodes | 0 edges
			graph {
				1
				2
			}
		''')
	}

	@Test def code_mining_012() {
		'''
			digraph {
				1
				2
			}
		'''.testCodeMining('''
			2 nodes | 0 edges
			digraph {
				1
				2
			}
		''')
	}

	@Test def code_mining_013() {
		'''
			graph {
				1
				2
				1--2
			}
		'''.testCodeMining('''
			2 nodes | 1 edge
			graph {
				1
				2
				1--2
			}
		''')
	}

	@Test def code_mining_014() {
		'''
			digraph {
				1
				2
				1->2
			}
		'''.testCodeMining('''
			2 nodes | 1 edge
			digraph {
				1
				2
				1->2
			}
		''')
	}

	@Test def code_mining_015() {
		'''
			graph {
				1
				2
				3
				1--2
				2--3
			}
		'''.testCodeMining('''
			3 nodes | 2 edges
			graph {
				1
				2
				3
				1--2
				2--3
			}
		''')
	}

	@Test def code_mining_016() {
		'''
			digraph {
				1
				2
				3
				1->2
				2->3
			}
		'''.testCodeMining('''
			3 nodes | 2 edges
			digraph {
				1
				2
				3
				1->2
				2->3
			}
		''')
	}

	@Test def code_mining_017() {
		'''
			graph {}
			digraph {}
		'''.testCodeMining('''
			0 nodes | 0 edges
			graph {}
			0 nodes | 0 edges
			digraph {}
		''')
	}

	@Test def code_mining_018() {
		'''
			graph {}

			digraph {}
		'''.testCodeMining('''
			0 nodes | 0 edges
			graph {}

			0 nodes | 0 edges
			digraph {}
		''')
	}

	@Test def code_mining_019() {
		'''
			digraph finite_state_machine {
				rankdir=LR;
				size="8,5"
				node [shape = doublecircle]; LR_0 LR_3 LR_4 LR_8;
				node [shape = circle];
				LR_0 -> LR_2 [ label = "SS(B)" ];
				LR_0 -> LR_1 [ label = "SS(S)" ];
				LR_1 -> LR_3 [ label = "S($end)" ];
				LR_2 -> LR_6 [ label = "SS(b)" ];
				LR_2 -> LR_5 [ label = "SS(a)" ];
				LR_2 -> LR_4 [ label = "S(A)" ];
				LR_5 -> LR_7 [ label = "S(b)" ];
				LR_5 -> LR_5 [ label = "S(a)" ];
				LR_6 -> LR_6 [ label = "S(b)" ];
				LR_6 -> LR_5 [ label = "S(a)" ];
				LR_7 -> LR_8 [ label = "S(b)" ];
				LR_7 -> LR_5 [ label = "S(a)" ];
				LR_8 -> LR_6 [ label = "S(b)" ];
				LR_8 -> LR_5 [ label = "S(a)" ];
			}
		'''.testCodeMining('''
			9 nodes | 14 edges
			digraph finite_state_machine {
				rankdir=LR;
				size="8,5"
				node [shape = doublecircle]; LR_0 LR_3 LR_4 LR_8;
				node [shape = circle];
				LR_0 -> LR_2 [ label = "SS(B)" ];
				LR_0 -> LR_1 [ label = "SS(S)" ];
				LR_1 -> LR_3 [ label = "S($end)" ];
				LR_2 -> LR_6 [ label = "SS(b)" ];
				LR_2 -> LR_5 [ label = "SS(a)" ];
				LR_2 -> LR_4 [ label = "S(A)" ];
				LR_5 -> LR_7 [ label = "S(b)" ];
				LR_5 -> LR_5 [ label = "S(a)" ];
				LR_6 -> LR_6 [ label = "S(b)" ];
				LR_6 -> LR_5 [ label = "S(a)" ];
				LR_7 -> LR_8 [ label = "S(b)" ];
				LR_7 -> LR_5 [ label = "S(a)" ];
				LR_8 -> LR_6 [ label = "S(b)" ];
				LR_8 -> LR_5 [ label = "S(a)" ];
			}
		''')
	}

	@Test def code_mining_020() {
		'''
			digraph {
				node[shape=square style="bold, filled" fillcolor="orange"]
				
				// nodes
				1[label="+" tooltip=3]
				2[label="1" tooltip=1 shape=circle]
				3[label="2" tooltip=2 shape=circle]
				
				// forward edges
				edge[arrowhead=vee]
				1->2
				1->3
				
				// backward edges
				{
					edge[arrowhead=normal color=green4 style=dashed]
					2->1[label=1]
					3->1[label=2]
				}
			}
		'''.testCodeMining('''
			3 nodes | 4 edges
			digraph {
				node[shape=square style="bold, filled" fillcolor="orange"]
				
				// nodes
				1[label="+" tooltip=3]
				2[label="1" tooltip=1 shape=circle]
				3[label="2" tooltip=2 shape=circle]
				
				// forward edges
				edge[arrowhead=vee]
				1->2
				1->3
				
				// backward edges
				{
					edge[arrowhead=normal color=green4 style=dashed]
					2->1[label=1]
					3->1[label=2]
				}
			}
		''')
	}
}
