/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Tamas Miklossy (itemis AG)     - initial API and implementation
 *    Zoey Gerrit Prigge (itemis AG) - introduced textPartition (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.DotHtmlLabelUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.editor.DotHtmlLabelTerminalsTokenTypeToPartitionMapper
import org.eclipse.jface.text.IDocument
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.parser.antlr.ITokenDefProvider
import org.eclipse.xtext.ui.editor.model.ITokenTypeToPartitionTypeMapper
import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelUiInjectorProvider)
class DotHtmlLabelTokenTypeToPartitionMapperTest {

	@Inject extension ITokenDefProvider
	@Inject extension ITokenTypeToPartitionTypeMapper

	@Test def RULE_HTML_COMMENT() { "RULE_HTML_COMMENT".hasCommentPartition }

	@Test def RULE_TAG_START_CLOSE() { "RULE_TAG_START_CLOSE".hasDefaultPartition }

	@Test def RULE_TAG_START() { "RULE_TAG_START".hasDefaultPartition }

	@Test def RULE_TAG_END() { "RULE_TAG_END".hasDefaultPartition }

	@Test def RULE_TAG_END_CLOSE() { "RULE_TAG_END_CLOSE".hasDefaultPartition }

	@Test def RULE_ASSIGN() { "RULE_ASSIGN".hasDefaultPartition }

	@Test def RULE_ATTR_VALUE() { "RULE_ATTR_VALUE".hasStringLiteralPartition }

	@Test def RULE_ID() { "RULE_ID".hasDefaultPartition }

	@Test def RULE_WS() { "RULE_WS".hasDefaultPartition }

	@Test def RULE_TEXT() { "RULE_TEXT".hasTextPartition }

	private def hasDefaultPartition(String tokenName) {
		tokenName.hasPartition(IDocument.DEFAULT_CONTENT_TYPE)
	}

	private def hasStringLiteralPartition(String tokenName) {
		tokenName.hasPartition(TerminalsTokenTypeToPartitionMapper.STRING_LITERAL_PARTITION)
	}

	private def hasCommentPartition(String tokenName) {
		tokenName.hasPartition(TerminalsTokenTypeToPartitionMapper.COMMENT_PARTITION)
	}

	private def hasTextPartition(String tokenName) {
		tokenName.hasPartition(DotHtmlLabelTerminalsTokenTypeToPartitionMapper.TEXT_PARTITION)
	}

	private def hasPartition(String tokenName, String expectedPartition) {
		val actualPartition = tokenName.tokenType.partitionType
		expectedPartition.assertEquals(actualPartition)
	}

	private def tokenType(String tokenName) {
		tokenDefMap.entrySet().findFirst(e|e.value==tokenName).key
	}

}
