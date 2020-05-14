/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy   (itemis AG) - avoid lexing in infinite loop (bug #529703)
 *
 *******************************************************************************/
lexer grammar CustomContentAssistInternalDotHtmlLabelLexer;

@header {
package org.eclipse.gef.dot.internal.ide.language.contentassist.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import.
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.gef.dot.internal.ide.language.contentassist.antlr.lexer.Lexer;
}

@members {
    boolean tagMode = false;
}

RULE_HTML_COMMENT: { !tagMode }?=> ( '<!--' (~('-')|'-' ~('-')|'-' '-' ~('>'))* '-->' );

RULE_TAG_START_CLOSE: ( '</' ) { tagMode = true; };
RULE_TAG_START      : ( '<'  ) { tagMode = true; };
RULE_TAG_END        : {  tagMode }?=> ( '>'  ) { tagMode = false; };
RULE_TAG_END_CLOSE  : {  tagMode }?=> ( '/>' ) { tagMode = false; };

RULE_ASSIGN    : { tagMode }?=> ( '=' );
RULE_ATTR_VALUE: { tagMode }?=> ('"' ~('"')* '"'|'\'' ~('\'')* '\'');
RULE_ID        : { tagMode }?=> ( ('_'|'a'..'z'|'A'..'Z') ('_'|'-'|'a'..'z'|'A'..'Z'|'0'..'9')* );
RULE_WS        : { tagMode }?=> ( (' '|'\t'|'\n'|'\r'|'\f')+ );

RULE_TEXT: { !tagMode }?=> ( ~('<')+ );