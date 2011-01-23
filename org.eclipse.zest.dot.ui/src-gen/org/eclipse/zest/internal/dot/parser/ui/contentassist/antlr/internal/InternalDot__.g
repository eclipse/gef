lexer grammar InternalDot;
@header {
package org.eclipse.zest.internal.dot.parser.ui.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;
}

T12 : '->' ;
T13 : '--' ;
T14 : 'graph' ;
T15 : 'digraph' ;
T16 : 'node' ;
T17 : 'edge' ;
T18 : '{' ;
T19 : '}' ;
T20 : ';' ;
T21 : '=' ;
T22 : '[' ;
T23 : ']' ;
T24 : ',' ;
T25 : 'subgraph' ;
T26 : 'strict' ;

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2344
RULE_DOT_ID : ('^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*|'"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~('"'))* '"'|'-'? ('.' ('0'..'9')+|('0'..'9')+ ('.' ('0'..'9')*)?));

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2346
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2348
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2350
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2352
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2354
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2356
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g" 2358
RULE_ANY_OTHER : .;


