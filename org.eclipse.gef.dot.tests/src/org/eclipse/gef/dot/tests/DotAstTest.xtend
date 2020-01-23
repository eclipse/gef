/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.content
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotAstTest {

	@Inject extension ParseHelper<DotAst>

	@Inject extension DotEObjectFormatter

	@Test def empty_graph() {
		DotTestGraphs.EMPTY.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
						]
					}
				]
			}
		''')
	}

	@Test def empty_directed_graph() {
		DotTestGraphs.EMPTY_DIRECTED.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = null
						stmts = [
						]
					}
				]
			}
		''')
	}

	@Test def empty_strict_graph() {
		DotTestGraphs.EMPTY_STRICT.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'true'
						type = 'graph'
						name = null
						stmts = [
						]
					}
				]
			}
		''')
	}

	@Test def empty_strict_directed_graph() {
		DotTestGraphs.EMPTY_STRICT_DIRECTED.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'true'
						type = 'digraph'
						name = null
						stmts = [
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_one_node() {
		DotTestGraphs.ONE_NODE.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							NodeStmt {
								node = NodeId {
									name = '1'
									port = null
								}
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_one_node_and_empty_node_attribute_list() {
		DotTestGraphs.EMPTY_NODE_ATTRIBUTE_LIST.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							NodeStmt {
								node = NodeId {
									name = '1'
									port = null
								}
								attrLists = [
									AttrList {
										attributes = [
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_one_edge() {
		DotTestGraphs.ONE_EDGE.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '--'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def directed_graph_with_one_edge() {
		DotTestGraphs.ONE_DIRECTED_EDGE.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = null
						stmts = [
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_one_edge_and_empty_edge_attribute_list() {
		DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_LIST.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '--'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def directed_graph_with_one_edge_and_empty_edge_attribute_list() {
		DotTestGraphs.EMPTY_DIRECTED_EDGE_ATTRIBUTE_LIST.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = null
						stmts = [
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_empty_graph_attribute_statement() {
		DotTestGraphs.EMPTY_GRAPH_ATTRIBUTE_STATEMENT.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							AttrStmt {
								type = 'graph'
								attrLists = [
									AttrList {
										attributes = [
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_empty_node_attribute_statement() {
		DotTestGraphs.EMPTY_NODE_ATTRIBUTE_STATEMENT.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							AttrStmt {
								type = 'node'
								attrLists = [
									AttrList {
										attributes = [
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def graph_with_empty_edge_attribute_statement() {
		DotTestGraphs.EMPTY_EDGE_ATTRIBUTE_STATEMENT.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = null
						stmts = [
							AttrStmt {
								type = 'edge'
								attrLists = [
									AttrList {
										attributes = [
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def test_node_groups() {
		DotTestGraphs.NODE_GROUPS.assertDotAst('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = null
						stmts = [
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '3'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
							AttrStmt {
								type = 'node'
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'shape'
												value = 'box'
											}
										]
									}
								]
							}
							AttrStmt {
								type = 'edge'
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'style'
												value = '"dashed, bold"'
											}
											Attribute {
												name = 'arrowhead'
												value = 'odot'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = 'foo'
									port = null
								}
								edgeRHS = [
									EdgeRhsSubgraph {
										op = '->'
										subgraph = Subgraph {
											name = null
											stmts = [
												NodeStmt {
													node = NodeId {
														name = 'bar'
														port = null
													}
													attrLists = [
													]
												}
												NodeStmt {
													node = NodeId {
														name = 'baz'
														port = null
													}
													attrLists = [
													]
												}
											]
										}
									}
								]
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def labeled_graph() {
		"labeled_graph.dot".assertDotAstFromFile('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = 'LabeledGraph'
						stmts = [
							NodeStmt {
								node = NodeId {
									name = '1'
									port = null
								}
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'label'
												value = '"one \"1\""'
											}
										]
									}
								]
							}
							NodeStmt {
								node = NodeId {
									name = '2'
									port = null
								}
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'label'
												value = 'two'
											}
										]
									}
								]
							}
							NodeStmt {
								node = NodeId {
									name = '3'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '4'
									port = null
								}
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'label'
												value = '"+1"'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '3'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'label'
												value = '"+2"'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '3'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '4'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def simple_digraph() {
		"simple_digraph.dot".assertDotAstFromFile('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = 'SimpleDigraph'
						stmts = [
							NodeStmt {
								node = NodeId {
									name = '1'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '2'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '3'
									port = null
								}
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '2'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '3'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def simple_graph() {
		"simple_graph.dot".assertDotAstFromFile('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'graph'
						name = 'SimpleGraph'
						stmts = [
							NodeStmt {
								node = NodeId {
									name = '1'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '2'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '3'
									port = null
								}
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '--'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '--'
										node = NodeId {
											name = '3'
											port = null
										}
									}
								]
								attrLists = [
								]
							}
						]
					}
				]
			}
		''')
	}

	@Test def styled_graph() {
		"styled_graph.dot".assertDotAstFromFile('''
			DotAst {
				graphs = [
					DotGraph {
						strict = 'false'
						type = 'digraph'
						name = 'StyledGraph'
						stmts = [
							Attribute {
								name = 'layout'
								value = 'dot'
							}
							NodeStmt {
								node = NodeId {
									name = '1'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '2'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '3'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '4'
									port = null
								}
								attrLists = [
								]
							}
							NodeStmt {
								node = NodeId {
									name = '5'
									port = null
								}
								attrLists = [
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '1'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '2'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'style'
												value = 'dashed'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '2'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '3'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'style'
												value = 'dotted'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '3'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '4'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'style'
												value = 'dashed'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '3'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '5'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'style'
												value = 'dashed'
											}
										]
									}
								]
							}
							EdgeStmtNode {
								node = NodeId {
									name = '4'
									port = null
								}
								edgeRHS = [
									EdgeRhsNode {
										op = '->'
										node = NodeId {
											name = '5'
											port = null
										}
									}
								]
								attrLists = [
									AttrList {
										attributes = [
											Attribute {
												name = 'style'
												value = 'solid'
											}
										]
									}
								]
							}
						]
					}
				]
			}
		''')
	}

	private def assertDotAstFromFile(String fileName, CharSequence expected) {
		fileName.content.assertDotAst(expected)
	}

	private def assertDotAst(CharSequence modelAsText,
			CharSequence expected) {
		val dotAst = modelAsText.parse
		val dotAstString = dotAst.apply
		expected.toString.assertEquals(dotAstString)
	}
}