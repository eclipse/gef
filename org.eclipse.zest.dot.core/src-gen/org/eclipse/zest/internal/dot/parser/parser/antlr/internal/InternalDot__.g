lexer grammar InternalDot;
@header {
package org.eclipse.zest.internal.dot.parser.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

T12 : 'strict' ;
T13 : '{' ;
T14 : '}' ;
T15 : ';' ;
T16 : '=' ;
T17 : '[' ;
T18 : ']' ;
T19 : ',' ;
T20 : 'subgraph' ;
T21 : '->' ;
T22 : '--' ;
T23 : 'graph' ;
T24 : 'digraph' ;
T25 : 'node' ;
T26 : 'edge' ;

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1290
RULE_DOT_ID : ('^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*|'"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~('"'))* '"'|'-'? ('.' ('0'..'9')+|('0'..'9')+ ('.' ('0'..'9')*)?));

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1292
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1294
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1296
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1298
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1300
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1302
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g" 1304
RULE_ANY_OTHER : .;


