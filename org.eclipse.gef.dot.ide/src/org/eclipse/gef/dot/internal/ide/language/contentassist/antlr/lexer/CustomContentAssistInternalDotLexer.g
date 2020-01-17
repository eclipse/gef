/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
lexer grammar CustomContentAssistInternalDotLexer;

@header {
package org.eclipse.gef.dot.internal.ide.language.contentassist.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer;
}

@members {
    boolean htmlMode = false;
    int htmlTags = 0;
}

Subgraph : { !htmlMode }?=>('S'|'s')('U'|'u')('B'|'b')('G'|'g')('R'|'r')('A'|'a')('P'|'p')('H'|'h');

Digraph : { !htmlMode }?=>('D'|'d')('I'|'i')('G'|'g')('R'|'r')('A'|'a')('P'|'p')('H'|'h');

Strict : { !htmlMode }?=>('S'|'s')('T'|'t')('R'|'r')('I'|'i')('C'|'c')('T'|'t');

Graph : { !htmlMode }?=>('G'|'g')('R'|'r')('A'|'a')('P'|'p')('H'|'h');

Edge : { !htmlMode }?=>('E'|'e')('D'|'d')('G'|'g')('E'|'e');

Node : { !htmlMode }?=>('N'|'n')('O'|'o')('D'|'d')('E'|'e');

HyphenMinusHyphenMinus : { !htmlMode }?=>'-''-';

HyphenMinusGreaterThanSign : { !htmlMode }?=>'-''>';

Comma : { !htmlMode }?=>',';

Colon : { !htmlMode }?=>':';

Semicolon : { !htmlMode }?=>';';

EqualsSign : { !htmlMode }?=>'=';

LeftSquareBracket : { !htmlMode }?=>'[';

RightSquareBracket : { !htmlMode }?=>']';

LeftCurlyBracket : { !htmlMode }?=>'{';

RightCurlyBracket : { !htmlMode }?=>'}';

RULE_NUMERAL : { !htmlMode }?=> '-'? ('.' ('0'..'9')+ | ('0'..'9')+ ('.' ('0'..'9')*)?);

RULE_COMPASS_PT : { !htmlMode }?=> ('n'|'ne'|'e'|'se'|'s'|'sw'|'w'|'nw'|'c'|'_');

RULE_STRING : { !htmlMode }?=> ('a'..'z'|'A'..'Z'|'\u0080'..'\u00FF'|'_') ('a'..'z'|'A'..'Z'|'\u0080'..'\u00FF'|'_'|'0'..'9')*;

RULE_QUOTED_STRING : { !htmlMode }?=> '"' ('\\' '"'|~('"'))* '"';

RULE_HTML_STRING : { !htmlMode }?=> '<' { htmlMode = true; } (HTML_TAG_OPEN | HTML_TAG_CLOSE | HTML_CHARS)* '>' { htmlMode = false; };

fragment HTML_TAG_OPEN : { htmlMode }?=> '<' { htmlTags++; };

fragment HTML_TAG_CLOSE : { htmlMode && htmlTags > 0 }?=> '>' { htmlTags--; };

fragment HTML_CHARS : { htmlMode }?=> (~('<'|'>'))+;

RULE_ML_COMMENT : { !htmlMode }?=> '/*' ( options {greedy=false;} : . )* '*/';

RULE_SL_COMMENT : { !htmlMode }?=> ('//'|'#') ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_WS : { !htmlMode }?=> (' '|'\t'|'\r'|'\n'|'\f')+;
 
RULE_ANY_OTHER : .;
