/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtext.formatting.INodeModelFormatter
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.resource.XtextResource
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotFormatterTests {
	
	@Inject extension ParseHelper<DotAst>
	@Inject extension INodeModelFormatter

	@Test def testFormatting001(){
		'''
			graph {}
		'''.assertFormattedAs('''
			graph {
			}
		''')
	}
	
	@Test def testFormatting002(){
		'''
			digraph {}
		'''.assertFormattedAs('''
			digraph {
			}
		''')
	}
	
	@Test def testFormatting003(){
		'''
			graph{}
		'''.assertFormattedAs('''
			graph {
			}
		''')
	}
	
	@Test def testFormatting004(){
		'''
			digraph{}
		'''.assertFormattedAs('''
			digraph {
			}
		''')
	}
	
	@Test def testFormatting005(){
		'''
			digraph{1}
		'''.assertFormattedAs('''
			digraph {
				1
			}
		''')
	}

	@Test def testFormatting006(){
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
	
	@Test def testFormatting007(){
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
	
	@Test def testFormatting008(){
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
	
	@Test def testFormatting009(){
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
	
	@Test def testFormatting010(){
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
	
	@Test def testFormatting011(){
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

	@Test def testFormatting012(){
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
	
	@Test def testFormatting013(){
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
	
	@Test def testFormatting014(){
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
	
	@Test def testFormatting015(){
		'''
			digraph{randir="LR"}
		'''.assertFormattedAs('''
			digraph {
				randir="LR"
			}
		''')
	}
	
	@Test def testFormatting016(){
		'''
			digraph{graph[]}
		'''.assertFormattedAs('''
			digraph {
				graph[ ]
			}
		''')
	}
	
	@Test def testFormatting017(){
		'''
			digraph{node[]}
		'''.assertFormattedAs('''
			digraph {
				node[ ]
			}
		''')
	}
	
	@Test def testFormatting018(){
		'''
			digraph{edge[]}
		'''.assertFormattedAs('''
			digraph {
				edge[ ]
			}
		''')
	}
	
	@Test def testFormatting019(){
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
	
	@Test def testFormatting020(){
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
	
	@Test def testFormatting021(){
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

	@Test def testFormatting022(){
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

	@Test def testFormatting023(){
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

	@Test def testFormatting024(){
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

	@Test def testFormatting025(){
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

	@Test def testFormatting026(){
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
	
	private def assertFormattedAs(CharSequence input, CharSequence expected){
		expected.toString.trim.assertEquals(input.formattedText)
	}
	
	private def formattedText(CharSequence unformattedText){
		val rootNode = (unformattedText.parse.eResource as XtextResource).parseResult.rootNode
		rootNode.format(0, unformattedText.length).formattedText
	}
}