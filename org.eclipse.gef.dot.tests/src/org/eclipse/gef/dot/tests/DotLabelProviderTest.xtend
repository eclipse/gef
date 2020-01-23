/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
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
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.language.dot.AttributeType
import org.eclipse.gef.dot.internal.language.dot.DotFactory
import org.eclipse.gef.dot.internal.language.dot.EdgeOp
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelFactory
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.dot.internal.ui.language.labeling.DotLabelProvider
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.viewers.ILabelProvider
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.IImageHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.assertEquals

/**
 * Test cases for the {@link DotLabelProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotLabelProviderTest {

	@Inject ILabelProvider labelProvider
	@Inject IImageHelper imageHelper
	@Inject extension DotGrammarAccess

	val extension DotFactory = DotFactory.eINSTANCE
	val extension HtmllabelFactory = HtmllabelFactory.eINSTANCE

	@Test def image_DotAst() {
		createDotAst.hasImage('file.png')
	}

	@Test def image_DotGraph() {
		createDotGraph.hasImage('graph.png')
	}

	@Test def image_AttrStmt() {
		createAttrStmt.hasImage('attributes.png')
	}

	@Test def image_AttrList() {
		createAttrList.hasImage('attributes.png')
	}

	@Test def image_Attribute() {
		createAttribute.hasImage('attribute.png')
	}

	@Test def image_NodeStmt() {
		createNodeStmt.hasImage('node.png')
	}

	@Test def image_EdgeStmtNode() {
		createEdgeStmtNode.hasImage('edge.png')
	}

	@Test def image_NodeId() {
		createNodeId.hasImage('id.png')
	}

	@Test def image_Subgraph() {
		createSubgraph.hasImage('subgraph.png')
	}

	@Test def image_EdgeRhs() {
		createEdgeRhs.hasImage('rhs.png')
	}

	@Test def image_HtmlTag() {
		createHtmlTag.hasImage('html_tag.png')
	}

	@Test def image_HtmlAttr() {
		createHtmlAttr.hasImage('attribute.png')
	}

	@Test def image_HtmlContent() {
		createHtmlContent.hasImage('html_text.png')
	}

	@Test def image_keyword_graph() {
		graphTypeAccess.graphGraphKeyword_0_0.hasImage('graph.png')
	}

	@Test def image_keyword_digraph() {
		graphTypeAccess.digraphDigraphKeyword_1_0.hasImage('graph.png')
	}

	@Test def image_keyword_graph_attributes() {
		attributeTypeAccess.graphGraphKeyword_0_0.hasImage('attributes.png')
	}

	@Test def image_keyword_node_attributes() {
		attributeTypeAccess.nodeNodeKeyword_1_0.hasImage('attributes.png')
	}

	@Test def image_keyword_edge_attributes() {
		attributeTypeAccess.edgeEdgeKeyword_2_0.hasImage('attributes.png')
	}

	@Test def image_keyword_subgraph() {
		subgraphAccess.subgraphKeyword_1_0.hasImage('subgraph.png')
	}

	@Test def text_DotAst() {
		val ast = createDotAst
		
		val resource = new XtextResource => [
			contents += ast
		]
		
		resource.URI = URI.createURI("test.dot")
		
		ast.hasText("test.dot: File")
	}

	@Test def text_DotGraph001() {
		createDotGraph.hasText("<?>: Graph")
	}

	@Test def text_DotGraph002() {
		val graph = createDotGraph => [
			name = ID.fromString("TestGraph")
		]
		graph.hasText("TestGraph: Graph")
	}

	@Test def text_DotGraph003() {
		val graph = createDotGraph => [
			name = ID.fromString('"Test Graph"')
		]
		graph.hasText('Test Graph: Graph')
	}

	@Test def text_Subgraph001() {
		createSubgraph.hasText("<?>: Subgraph")
	}

	@Test def text_Subgraph002() {
		val subgraph = createSubgraph => [
			name = ID.fromString("TestSubgraph")
		]
		subgraph.hasText("TestSubgraph: Subgraph")
	}

	@Test def text_Subgraph003() {
		val subgraph = createSubgraph => [
			name = ID.fromString('"Test Subgraph"')
		]
		subgraph.hasText("Test Subgraph: Subgraph")
	}

	@Test def text_NodeStmt001() {
		val nodeStmt = createNodeStmt => [
			node = createNodeId => [
				name = ID.fromString("TestNode")
			]
		]
		nodeStmt.hasText("TestNode: Node")
	}

	@Test def text_NodeStmt002() {
		val nodeStmt = createNodeStmt => [
			node = createNodeId => [
				name = ID.fromString('"Test Node"')
			]
		]
		nodeStmt.hasText('"Test Node": Node')
	}

	@Test def text_EdgeStmtNode001() {
		val edgeStmtNode = createEdgeStmtNode => [
			node = createNodeId => [
				name = ID.fromString('1')
			]
		]
		edgeStmtNode.hasText("<?>: Edges")
	}

	@Test def text_EdgeStmtNode002() {
		val edgeStmtNode = createEdgeStmtNode => [
			node = createNodeId => [
				name = ID.fromString('1')
			]
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.UNDIRECTED
				node = createNodeId => [
					name = ID.fromString('2')
				]
			]
		]
		edgeStmtNode.hasText("1 -- [1 Node]: Edges")
	}

	@Test def text_EdgeStmtNode003() {
		val edgeStmtNode = createEdgeStmtNode => [
			node = createNodeId => [
				name = ID.fromString('1')
			]
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.DIRECTED
				node = createNodeId => [
					name = ID.fromString('2')
				]
			]
		]
		edgeStmtNode.hasText("1 -> [1 Node]: Edges")
	}

	@Test def text_EdgeStmtNode004() {
		val edgeStmtNode = createEdgeStmtNode => [
			node = createNodeId => [
				name = ID.fromString('1')
			]
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.UNDIRECTED
				node = createNodeId => [
					name = ID.fromString('2')
				]
			]
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.UNDIRECTED
				node = createNodeId => [
					name = ID.fromString('3')
				]
			]
		]
		edgeStmtNode.hasText("1 -- [2 Nodes]: Edges")
	}

	@Test def text_EdgeStmtNode005() {
		val edgeStmtNode = createEdgeStmtNode => [
			node = createNodeId => [
				name = ID.fromString('1')
			]
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.DIRECTED
				node = createNodeId => [
					name = ID.fromString('2')
				]
			]
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.DIRECTED
				node = createNodeId => [
					name = ID.fromString('3')
				]
			]
		]
		edgeStmtNode.hasText("1 -> [2 Nodes]: Edges")
	}

	@Test def text_AttrStmt001() {
		val attributeStatement = createAttrStmt => [
			type = AttributeType.GRAPH
		]
		attributeStatement.hasText("graph: Attributes")
	}

	@Test def text_AttrStmt002() {
		val attributeStatement = createAttrStmt => [
			type = AttributeType.NODE
		]
		attributeStatement.hasText("node: Attributes")
	}

	@Test def text_AttrStmt003() {
		val attributeStatement = createAttrStmt => [
			type = AttributeType.EDGE
		]
		attributeStatement.hasText("edge: Attributes")
	}

	@Test def text_Attribute001() {
		val attribute = createAttribute => [
			name = ID.fromString('bgcolor')
			value = ID.fromString('red')
		]
		attribute.hasText("bgcolor = red: Attribute")
	}

	@Test def text_Attribute002() {
		val attribute = createAttribute => [
			name = ID.fromString('bgcolor')
			value = ID.fromString('"red"')
		]
		attribute.hasText('bgcolor = "red": Attribute')
	}

	@Test def text_Attribute003() {
		val attribute = createAttribute => [
			name = ID.fromString('label')
			value = ID.fromString('<<B>boldLabel</B>>')
		]
		attribute.hasText('label = <HTML-Label>: Attribute')
	}

	@Test def text_AttrList001() {
		createAttrList.hasText("0 Attribute: Attributes")
	}

	@Test def text_AttrList002() {
		val attributeList = createAttrList => [
			attributes += createAttribute
		]
		attributeList.hasText("1 Attribute: Attributes")
	}

	@Test def text_AttrList003() {
		val attributeList = createAttrList => [
			attributes += createAttribute
			attributes += createAttribute
		]
		attributeList.hasText("2 Attributes: Attributes")
	}

	@Test def text_NodeId001() {
		val node = createNodeId => [
			name = ID.fromString("TestNode")
		]
		node.hasText("TestNode: Node")
	}

	@Test def text_NodeId002() {
		val node = createNodeId => [
			name = ID.fromString('"Test Node"')
		]
		node.hasText('"Test Node": Node')
	}

	@Test def text_EdgeRhs001() {
		val edgeRhs = createEdgeRhsNode => [
			op = EdgeOp.UNDIRECTED
			node = createNodeId => [
				name = ID.fromString('2')
			]
		]
		edgeRhs.hasText("undirected -- 2: Node")
	}

	@Test def text_EdgeRhs002() {
		val edgeRhs = createEdgeRhsNode => [
			op = EdgeOp.DIRECTED
			node = createNodeId => [
				name = ID.fromString('2')
			]
		]
		edgeRhs.hasText("directed -> 2: Node")
	}

	@Test def text_EdgeRhs003() {
		createEdgeRhsSubgraph.hasText(null)
	}

	@Test def text_HtmlTag001() {
		val htmlTag = createHtmlTag => [
			name = "B"
		]
		htmlTag.hasText("<B>: Tag")
	}

	@Test def text_HtmlTag002() {
		val htmlTag = createHtmlTag => [
			name = "BR"
			selfClosing = true
		]
		htmlTag.hasText("<BR/>: Tag")
	}

	@Test def text_HtmlAttr() {
		val htmlAttribute = createHtmlAttr => [
			name = "ALIGN"
			value = "CENTER"
		]
		htmlAttribute.hasText("ALIGN = CENTER: Attribute")
	}

	@Test def text_HtmlContent001() {
		createHtmlContent.hasText(": Text")
	}

	@Test def text_HtmlContent002() {
		val htmlContent = createHtmlContent => [
			text = ""
		]
		htmlContent.hasText(": Text")
	}

	@Test def text_HtmlContent003() {
		val htmlContent = createHtmlContent => [
			text = "  "
		]
		htmlContent.hasText(": Text")
	}

	@Test def text_HtmlContent004() {
		val htmlContent = createHtmlContent => [
			text = "  abc"
		]
		htmlContent.hasText("abc: Text")
	}

	@Test def text_HtmlContent005() {
		val htmlContent = createHtmlContent => [
			text = "abc "
		]
		htmlContent.hasText("abc: Text")
	}

	@Test def text_HtmlContent006() {
		val htmlContent = createHtmlContent => [
			text = "  abc "
		]
		htmlContent.hasText("abc: Text")
	}

	private def hasImage(EObject eObject, String image) {
		val actual = labelProvider.getImage(eObject)
		val expected = imageHelper.getImage(image)
		expected.assertEquals(actual)
	}

	private def hasText(EObject eObject, String expected) {
		val actual = labelProvider.getText(eObject)
		expected.assertEquals(actual)
	}
}