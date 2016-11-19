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
lexer grammar CustomHighlightingInternalDotLexer;

@header {
package org.eclipse.gef.dot.internal.language.ui.highlighting.antlr.lexer;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

@members {
    boolean htmlMode = false;
    boolean tagMode = false;
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

RULE_NUMERAL : { !htmlMode }?=> ('-'? '.' ('0'..'9')+|('0'..'9')+ ('.' ('0'..'9')*)?);

RULE_COMPASS_PT : { !htmlMode }?=> ('n'|'ne'|'e'|'se'|'s'|'sw'|'w'|'nw'|'c'|'_');

RULE_STRING : { !htmlMode }?=> ('a'..'z'|'A'..'Z'|'\u0080'..'\u00FF'|'_') ('a'..'z'|'A'..'Z'|'\u0080'..'\u00FF'|'_'|'0'..'9')*;

RULE_QUOTED_STRING : { !htmlMode }?=> '"' ('\\' '"'|~('"'))* '"';

RULE_HTML_STRING : { !htmlMode }?=> '<' { htmlMode = true; } HTML_CONTENT* '>' { htmlMode = false; };

fragment HTML_CONTENT : { htmlMode }?=> (HTML_TAG | HTML_PCDATA) ;

fragment HTML_TAG : { htmlMode }?=> HTML_TAG_START_OPEN HTML_TAG_DATA ( HTML_TAG_EMPTY_CLOSE | HTML_TAG_CLOSE (HTML_CONTENT)* HTML_TAG_END_OPEN HTML_TAG_DATA HTML_TAG_CLOSE);

fragment HTML_TAG_START_OPEN : { htmlMode && !tagMode }?=> '<' { tagMode = true; };

fragment HTML_TAG_END_OPEN : { htmlMode && !tagMode }?=> '<''/' { tagMode = true; };

fragment HTML_TAG_CLOSE : { htmlMode && tagMode }?=> '>' { tagMode = false; } ;

fragment HTML_TAG_EMPTY_CLOSE : { htmlMode && tagMode }?=> '/''>' { tagMode = false; } ;

fragment HTML_TAG_DATA : { htmlMode && tagMode }?=>  ~('/') ({ input.LA(1) != '>' && (input.LA(1) != '/' || input.LA(2) != '>')}?=> ~('>'))*;

fragment HTML_PCDATA : { htmlMode && !tagMode }?=> (~('<'|'>'))+ ;

RULE_ML_COMMENT : { !htmlMode }?=> '/*' ( options {greedy=false;} : . )* '*/';

RULE_SL_COMMENT : { !htmlMode }?=> ('//'|'#') ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_WS : { !htmlMode }?=> (' '|'\t'|'\r'|'\n'|'\f')+;
 
RULE_ANY_OTHER : .;
