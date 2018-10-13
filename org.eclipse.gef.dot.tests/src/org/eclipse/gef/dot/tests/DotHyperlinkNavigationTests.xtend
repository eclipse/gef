/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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
import com.google.inject.Injector
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.NodeId
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils
import org.eclipse.jface.text.hyperlink.IHyperlink
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper
import org.eclipse.xtext.ui.editor.hyperlinking.XtextHyperlink
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotHyperlinkNavigationTests {

	@Inject Injector injector
	@Inject extension IHyperlinkHelper

	@Test def hyperlink_left_side_of_an_edge() {
		'''
			graph {1;2 1--2}
		'''.verifyHyperlinkOn("1")
	}

	@Test def hyperlink_right_side_of_an_edge() {
		'''
			graph {
				1;2
				1--2
			}
		'''.verifyHyperlinkOn("2")
	}

	private def verifyHyperlinkOn(CharSequence content, String nodeIdName) {
		// given
		val text = content.toString
		val resource = text.xtextResource

		// when
		val hyperlink = resource.hyperlink(text.hyperlinkSourceOffset(nodeIdName))

		// then
		val expectedSourceOffset = text.hyperlinkSourceOffset(nodeIdName)
		val expectedSourceLength = nodeIdName.length
		hyperlink.verify(expectedSourceOffset, expectedSourceLength)

		val expectedTargetNodeIdOffset = text.hyperlinkTargetOffset(nodeIdName)
		val expectedTargetNodeIdLength = nodeIdName.length
		val expectedTargetNodeIdName = nodeIdName
		hyperlink.targetNodeId(resource).
		verify(expectedTargetNodeIdName, expectedTargetNodeIdOffset, expectedTargetNodeIdLength)
	}

	private def xtextResource(String text) {
		DotEditorUtils.getXtextResource(injector, text)
	}

	private def hyperlink(XtextResource resource, int offset) {
		val hyperlinks = resource.createHyperlinksByOffset(offset, false)
		
		1.assertEquals(hyperlinks.length)
		hyperlinks.get(0) as XtextHyperlink
	}

	private def verify(IHyperlink xtextHyperlink, int offset, int length) {
		val hyperlinkRegion = xtextHyperlink.hyperlinkRegion
		offset.assertEquals(hyperlinkRegion.offset)
		length.assertEquals(hyperlinkRegion.length)
	}

	private def verify(NodeId targetNodeId, String targetNodeIdName, int offset, int length) {
		targetNodeIdName.assertEquals(targetNodeId.name.toString)

		val compositeNode = NodeModelUtils.findActualNodeFor(targetNodeId)
		offset.assertEquals(compositeNode.totalOffset)
		length.assertEquals(compositeNode.totalLength)
	}

	private def targetNodeId(XtextHyperlink hyperlink, Resource resource) {
		resource.resourceSet.getEObject(hyperlink.URI, true) as NodeId
	}

	private def hyperlinkSourceOffset(String text, String subString) {
		text.lastIndexOf(subString)
	}

	private def hyperlinkTargetOffset(String text, String subString) {
		text.indexOf(subString)
	}
}
