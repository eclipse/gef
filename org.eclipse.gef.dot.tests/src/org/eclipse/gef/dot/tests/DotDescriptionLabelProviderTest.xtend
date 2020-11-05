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
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.language.dot.DotFactory
import org.eclipse.gef.dot.internal.language.dot.EdgeOp
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.dot.internal.ui.language.labeling.DotDescriptionLabelProvider
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.viewers.ILabelProvider
import org.eclipse.xtext.resource.EObjectDescription
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.IImageHelper
import org.eclipse.xtext.ui.resource.ResourceServiceDescriptionLabelProvider
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals

/**
 * Test cases for the {@link DotDescriptionLabelProvider} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotDescriptionLabelProviderTest {

	@Inject @ResourceServiceDescriptionLabelProvider ILabelProvider labelProvider
	@Inject IImageHelper imageHelper

	val extension DotFactory = DotFactory.eINSTANCE

	@Test def right_class_is_injected() {
		val actual = labelProvider.getClass().name
		val expected = DotDescriptionLabelProvider.name
		assertEquals(expected, actual)
	}

	@Test def image_NodeStmt_NodeId() {
		val nodeId = createNodeId
		createNodeStmt => [node = nodeId]
		nodeId.hasImage('node.png')
	}

	@Test def image_EdgeStmtNode_NodeId() {
		val nodeId = createNodeId
		createEdgeStmtNode => [node = nodeId]
		nodeId.hasImage('edge.png')
	}

	@Test def image_EdgeRhs_NodeId() {
		val nodeId = createNodeId
		createEdgeRhsNode => [node = nodeId]
		nodeId.hasImage('edge.png')
	}

	@Test def text_NodeStmt_NodeId() {
		val nodeId = createNodeId => [name=ID.fromString("1")]
		createNodeStmt => [node = nodeId]
		nodeId.hasText("1: Node")
	}

	@Test def text_EdgeStmtNode_NodeId_001() {
		val nodeId1 = createNodeId => [name=ID.fromString("1")]
		val nodeId2 = createNodeId => [name=ID.fromString("2")]
		createEdgeStmtNode => [
			node = nodeId1
			
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.DIRECTED
				node = nodeId2
			]
		]
		nodeId1.hasText("1 -> 2: Edge")
	}

	@Test def text_EdgeStmtNode_NodeId_002() {
		val nodeId1 = createNodeId => [name=ID.fromString("1")]
		val nodeId2 = createNodeId => [name=ID.fromString("2")]
		createEdgeStmtNode => [
			node = nodeId1
			
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.UNDIRECTED
				node = nodeId2
			]
		]
		nodeId1.hasText("1 -- 2: Edge")
	}

	@Test def text_EdgeRhs_NodeId_001() {
		val nodeId1 = createNodeId => [name=ID.fromString("1")]
		val nodeId2 = createNodeId => [name=ID.fromString("2")]
		createEdgeStmtNode => [
			node = nodeId1
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.DIRECTED
				node = nodeId2
			]
		]
		nodeId2.hasText("1 -> 2: Edge")
	}

	@Test def text_EdgeRhs_NodeId_002() {
		val nodeId1 = createNodeId => [name=ID.fromString("1")]
		val nodeId2 = createNodeId => [name=ID.fromString("2")]
		createEdgeStmtNode => [
			node = nodeId1
			edgeRHS += createEdgeRhsNode => [
				op = EdgeOp.UNDIRECTED
				node = nodeId2
			]
		]
		nodeId2.hasText("1 -- 2: Edge")
	}

	private def hasImage(EObject eObject, String image) {
		val eObjectDescription = EObjectDescription.create("dummy", eObject)
		val actual = labelProvider.getImage(eObjectDescription)
		val expected = imageHelper.getImage(image)
		assertEquals(expected, actual)
	}

	private def hasText(EObject eObject, String expected) {
		val eObjectDescription = EObjectDescription.create("dummy", eObject)
		val actual = labelProvider.getText(eObjectDescription)
		assertEquals(expected, actual)
	}

}