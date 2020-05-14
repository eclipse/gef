/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG)     - initial API and implementation
 *    Zoey Gerrit Prigge (itemis AG) - introduced htmlStringPartition (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.ui.language.editor.DotTerminalsTokenTypeToPartitionMapper
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.jface.text.IDocument
import org.eclipse.xtext.parser.antlr.ITokenDefProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.editor.model.ITokenTypeToPartitionTypeMapper
import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotTokenTypeToPartitionMapperTest {

	@Inject extension ITokenDefProvider
	@Inject extension ITokenTypeToPartitionTypeMapper

	@Test def subgraph() { "'subgraph'".hasDefaultPartition }

	@Test def digraph() { "'digraph'".hasDefaultPartition }

	@Test def strict() { "'strict'".hasDefaultPartition }

	@Test def graph() { "'graph'".hasDefaultPartition }

	@Test def edge() { "'edge'".hasDefaultPartition }

	@Test def node() { "'node'".hasDefaultPartition }

	@Test def hyphenMinusHyphenMinus() { "'--'".hasDefaultPartition }

	@Test def hyphenMinusGreaterThanSign() { "'->'".hasDefaultPartition }

	@Test def comma() { "','".hasDefaultPartition }

	@Test def colon() { "':'".hasDefaultPartition }

	@Test def semicolon() { "';'".hasDefaultPartition }

	@Test def equalsSign() { "'='".hasDefaultPartition }

	@Test def leftSquareBracket() { "'['".hasDefaultPartition }

	@Test def rightSquareBracket() { "']'".hasDefaultPartition }

	@Test def leftCurlyBracket() { "'{'".hasDefaultPartition }

	@Test def rightCurlyBracket() { "'}'".hasDefaultPartition }

	@Test def RULE_NUMERAL() { "RULE_NUMERAL".hasDefaultPartition }

	@Test def RULE_COMPASS_PT() { "RULE_COMPASS_PT".hasDefaultPartition }

	@Test def RULE_STRING() { "RULE_STRING".hasDefaultPartition }

	@Test def RULE_QUOTED_STRING() { "RULE_QUOTED_STRING".hasStringLiteralPartition }

	@Test def RULE_HTML_TAG_OPEN() { "RULE_HTML_TAG_OPEN".hasDefaultPartition }

	@Test def RULE_HTML_TAG_CLOSE() { "RULE_HTML_TAG_CLOSE".hasDefaultPartition }

	@Test def RULE_HTML_CHARS() { "RULE_HTML_CHARS".hasDefaultPartition }

	@Test def RULE_HTML_STRING() { "RULE_HTML_STRING".hasHtmlStringPartition }

	@Test def RULE_ML_COMMENT() { "RULE_ML_COMMENT".hasCommentPartition }

	@Test def RULE_SL_COMMENT() { "RULE_SL_COMMENT".hasSLCommentPartition }

	@Test def RULE_WS() { "RULE_WS".hasDefaultPartition }

	@Test def RULE_ANY_OTHER() { "RULE_ANY_OTHER".hasDefaultPartition }

	private def hasDefaultPartition(String tokenName) {
		tokenName.hasPartition(IDocument.DEFAULT_CONTENT_TYPE)
	}

	private def hasStringLiteralPartition(String tokenName) {
		tokenName.hasPartition(TerminalsTokenTypeToPartitionMapper.STRING_LITERAL_PARTITION)
	}

	private def hasSLCommentPartition(String tokenName) {
		tokenName.hasPartition(TerminalsTokenTypeToPartitionMapper.SL_COMMENT_PARTITION)
	}

	private def hasCommentPartition(String tokenName) {
		tokenName.hasPartition(TerminalsTokenTypeToPartitionMapper.COMMENT_PARTITION)
	}

	private def hasHtmlStringPartition(String tokenName) {
		tokenName.hasPartition(DotTerminalsTokenTypeToPartitionMapper.HTML_STRING_PARTITION)
	}

	private def hasPartition(String tokenName, String expectedPartition) {
		val actualPartition = tokenName.tokenType.partitionType
		expectedPartition.assertEquals(actualPartition)
	}

	private def tokenType(String tokenName) {
		tokenDefMap.entrySet().findFirst(e|e.value==tokenName).key
	}
}
