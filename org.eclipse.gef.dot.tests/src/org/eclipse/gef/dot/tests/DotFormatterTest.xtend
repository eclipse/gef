/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #532216)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtext.formatting.INodeModelFormatter
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotFormatterTest {

	@Inject extension ParseHelper<DotAst>
	@Inject extension INodeModelFormatter

	@Test def formatting001() {
		'''
			graph {}
		'''.assertFormattedAs('''
			graph {
			}
		''')
	}

	@Test def formatting002() {
		'''
			digraph {}
		'''.assertFormattedAs('''
			digraph {
			}
		''')
	}

	@Test def formatting003() {
		'''
			graph{}
		'''.assertFormattedAs('''
			graph {
			}
		''')
	}

	@Test def formatting004() {
		'''
			digraph{}
		'''.assertFormattedAs('''
			digraph {
			}
		''')
	}

	@Test def formatting005() {
		'''
			digraph{1}
		'''.assertFormattedAs('''
			digraph {
				1
			}
		''')
	}

	@Test def formatting006() {
		'''
			digraph {
				1
			}
		'''.assertFormattedAs('''
			digraph {
				1
			}
		''')
	}

	@Test def formatting007() {
		'''
			digraph {
				
				1
			}
		'''.assertFormattedAs('''
			digraph {
			
				1
			}
		''')
	}

	@Test def formatting008() {
		'''
			digraph {
				
				
				1
			}
		'''.assertFormattedAs('''
			digraph {
			
				1
			}
		''')
	}

	@Test def formatting009() {
		'''
			digraph {
			1
			}
		'''.assertFormattedAs('''
			digraph {
				1
			}
		''')
	}

	@Test def formatting010() {
		'''
			digraph {
				1
				2
				3
			}
		'''.assertFormattedAs('''
			digraph {
				1
				2
				3
			}
		''')
	}

	@Test def formatting011() {
		'''
			digraph {
				1
				
				
				2
				
				3
			}
		'''.assertFormattedAs('''
			digraph {
				1
			
				2
			
				3
			}
		''')
	}

	@Test def formatting012() {
		'''
			digraph {
				1
				subgraph {3
				4}
			}
		'''.assertFormattedAs('''
			digraph {
				1
			
				subgraph {
					3
					4
				}
			}
		''')
	}

	@Test def formatting013() {
		'''
			digraph {
				1
			subgraph{}
			}
		'''.assertFormattedAs('''
			digraph {
				1
			
				subgraph {
				}
			}
		''')
	}

	@Test def formatting014() {
		'''
			digraph {
				1
			subgraph {3
				4     }
			}
		'''.assertFormattedAs('''
			digraph {
				1
			
				subgraph {
					3
					4
				}
			}
		''')
	}

	@Test def formatting015() {
		'''
			digraph{randir="LR"}
		'''.assertFormattedAs('''
			digraph {
				randir="LR"
			}
		''')
	}

	@Test def formatting016() {
		'''
			digraph{graph[]}
		'''.assertFormattedAs('''
			digraph {
				graph[ ]
			}
		''')
	}

	@Test def formatting017() {
		'''
			digraph{node[]}
		'''.assertFormattedAs('''
			digraph {
				node[ ]
			}
		''')
	}

	@Test def formatting018() {
		'''
			digraph{edge[]}
		'''.assertFormattedAs('''
			digraph {
				edge[ ]
			}
		''')
	}

	@Test def formatting019() {
		'''
			digraph {
				1  ;   
			}
		'''.assertFormattedAs('''
			digraph {
				1;
			}
		''')
	}

	@Test def formatting020() {
		'''
			digraph {
				1[shape=diamond]
			}
		'''.assertFormattedAs('''
			digraph {
				1[ shape=diamond ]
			}
		''')
	}

	@Test def formatting021() {
		'''
			digraph {
				1[shape=circle   ,xlabel="external node label"style="bold, dashed"]
			}
		'''.assertFormattedAs('''
			digraph {
				1[ shape=circle, xlabel="external node label" style="bold, dashed" ]
			}
		''')
	}

	@Test def formatting022() {
		'''
			digraph {
				7 -> 8[ arrowhead=diamond arrowsize=2.0 arrowtail=odiamond dir=both label="diamond" ]
			}
		'''.assertFormattedAs('''
			digraph {
				7 -> 8[ arrowhead=diamond arrowsize=2.0 arrowtail=odiamond dir=both label="diamond" ]
			}
		''')
	}

	@Test def formatting023() {
		'''
			digraph{ subgraph a{} subgraph b{    }}
		'''.assertFormattedAs('''
			digraph {
			
				subgraph a {
				}
			
				subgraph b {
				}
			}
		''')
	}

	@Test def formatting024() {
		'''
			graph{1[label=<<B>bold text</B>>]}
		'''.assertFormattedAs('''
			graph {
				1[ label= < 
					<B>
						bold text
					</B> 
				> ]
			}
		''')
	}

	@Test def formatting025() {
		'''
			graph{1[label= <<B>bold text</B>>]}
		'''.assertFormattedAs('''
			graph {
				1[ label= < 
					<B>
						bold text
					</B> 
				> ]
			}
		''')
	}

	@Test def formatting026() {
		'''
			graph {
				1[ label= <<TABLE><TR><TD>text1</TD><TD>text2</TD></TR></TABLE>>]
			}
		'''.assertFormattedAs('''
			graph {
				1[ label= < 
					<TABLE>
						<TR>
							<TD>
								text1
							</TD>
							<TD>
								text2
							</TD>
						</TR>
					</TABLE> 
				> ]
			}
		''')
	}

	private def assertFormattedAs(CharSequence input, CharSequence expected) {
		expected.toString.trim.assertEquals(input.formattedText)
	}

	private def formattedText(CharSequence unformattedText) {
		val rootNode = (unformattedText.parse.eResource as XtextResource).parseResult.rootNode
		rootNode.format(0, unformattedText.length).formattedText
	}
}