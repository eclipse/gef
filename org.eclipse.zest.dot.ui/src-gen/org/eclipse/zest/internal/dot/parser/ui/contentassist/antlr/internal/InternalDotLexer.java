package org.eclipse.zest.internal.dot.parser.ui.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalDotLexer extends Lexer {
    public static final int RULE_ID=5;
    public static final int RULE_ANY_OTHER=11;
    public static final int T26=26;
    public static final int T25=25;
    public static final int Tokens=27;
    public static final int T24=24;
    public static final int EOF=-1;
    public static final int RULE_SL_COMMENT=9;
    public static final int T23=23;
    public static final int T22=22;
    public static final int T21=21;
    public static final int T20=20;
    public static final int RULE_ML_COMMENT=8;
    public static final int RULE_STRING=7;
    public static final int RULE_DOT_ID=4;
    public static final int RULE_INT=6;
    public static final int T12=12;
    public static final int T13=13;
    public static final int T14=14;
    public static final int RULE_WS=10;
    public static final int T15=15;
    public static final int T16=16;
    public static final int T17=17;
    public static final int T18=18;
    public static final int T19=19;
    public InternalDotLexer() {;} 
    public InternalDotLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g"; }

    // $ANTLR start T12
    public final void mT12() throws RecognitionException {
        try {
            int _type = T12;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:10:5: ( '->' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:10:7: '->'
            {
            match("->"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T12

    // $ANTLR start T13
    public final void mT13() throws RecognitionException {
        try {
            int _type = T13;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:11:5: ( '--' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:11:7: '--'
            {
            match("--"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T13

    // $ANTLR start T14
    public final void mT14() throws RecognitionException {
        try {
            int _type = T14;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:12:5: ( 'graph' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:12:7: 'graph'
            {
            match("graph"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T14

    // $ANTLR start T15
    public final void mT15() throws RecognitionException {
        try {
            int _type = T15;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:13:5: ( 'digraph' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:13:7: 'digraph'
            {
            match("digraph"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T15

    // $ANTLR start T16
    public final void mT16() throws RecognitionException {
        try {
            int _type = T16;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:14:5: ( 'node' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:14:7: 'node'
            {
            match("node"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T16

    // $ANTLR start T17
    public final void mT17() throws RecognitionException {
        try {
            int _type = T17;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:15:5: ( 'edge' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:15:7: 'edge'
            {
            match("edge"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T17

    // $ANTLR start T18
    public final void mT18() throws RecognitionException {
        try {
            int _type = T18;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:16:5: ( '{' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:16:7: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T18

    // $ANTLR start T19
    public final void mT19() throws RecognitionException {
        try {
            int _type = T19;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:17:5: ( '}' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:17:7: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T19

    // $ANTLR start T20
    public final void mT20() throws RecognitionException {
        try {
            int _type = T20;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:18:5: ( ';' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:18:7: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T20

    // $ANTLR start T21
    public final void mT21() throws RecognitionException {
        try {
            int _type = T21;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:19:5: ( '=' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:19:7: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T21

    // $ANTLR start T22
    public final void mT22() throws RecognitionException {
        try {
            int _type = T22;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:20:5: ( '[' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:20:7: '['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T22

    // $ANTLR start T23
    public final void mT23() throws RecognitionException {
        try {
            int _type = T23;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:21:5: ( ']' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:21:7: ']'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T23

    // $ANTLR start T24
    public final void mT24() throws RecognitionException {
        try {
            int _type = T24;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:22:5: ( ',' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:22:7: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T24

    // $ANTLR start T25
    public final void mT25() throws RecognitionException {
        try {
            int _type = T25;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:23:5: ( 'subgraph' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:23:7: 'subgraph'
            {
            match("subgraph"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T25

    // $ANTLR start T26
    public final void mT26() throws RecognitionException {
        try {
            int _type = T26;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:24:5: ( 'strict' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:24:7: 'strict'
            {
            match("strict"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T26

    // $ANTLR start RULE_DOT_ID
    public final void mRULE_DOT_ID() throws RecognitionException {
        try {
            int _type = RULE_DOT_ID;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:13: ( ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:15: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:15: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) )
            int alt10=3;
            switch ( input.LA(1) ) {
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '^':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt10=1;
                }
                break;
            case '\"':
                {
                alt10=2;
                }
                break;
            case '-':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("2344:15: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) )", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:16: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:16: ( '^' )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0=='^') ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:16: '^'
                            {
                            match('^'); 

                            }
                            break;

                    }

                    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:45: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:79: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:83: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )*
                    loop3:
                    do {
                        int alt3=3;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0=='\\') ) {
                            int LA3_2 = input.LA(2);

                            if ( (LA3_2=='\"') ) {
                                int LA3_4 = input.LA(3);

                                if ( ((LA3_4>='\u0000' && LA3_4<='\uFFFE')) ) {
                                    alt3=1;
                                }

                                else {
                                    alt3=2;
                                }

                            }
                            else if ( (LA3_2=='\\') ) {
                                alt3=1;
                            }
                            else if ( (LA3_2=='\''||LA3_2=='b'||LA3_2=='f'||LA3_2=='n'||LA3_2=='r'||LA3_2=='t') ) {
                                alt3=1;
                            }
                            else if ( ((LA3_2>='\u0000' && LA3_2<='!')||(LA3_2>='#' && LA3_2<='&')||(LA3_2>='(' && LA3_2<='[')||(LA3_2>=']' && LA3_2<='a')||(LA3_2>='c' && LA3_2<='e')||(LA3_2>='g' && LA3_2<='m')||(LA3_2>='o' && LA3_2<='q')||LA3_2=='s'||(LA3_2>='u' && LA3_2<='\uFFFE')) ) {
                                alt3=2;
                            }


                        }
                        else if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='\uFFFE')) ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:84: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:125: ~ ( '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:138: ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:138: ( '-' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='-') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:138: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }

                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:143: ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='.') ) {
                        alt9=1;
                    }
                    else if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                        alt9=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("2344:143: ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:144: '.' ( '0' .. '9' )+
                            {
                            match('.'); 
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:148: ( '0' .. '9' )+
                            int cnt5=0;
                            loop5:
                            do {
                                int alt5=2;
                                int LA5_0 = input.LA(1);

                                if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                                    alt5=1;
                                }


                                switch (alt5) {
                            	case 1 :
                            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:149: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); 

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt5 >= 1 ) break loop5;
                                        EarlyExitException eee =
                                            new EarlyExitException(5, input);
                                        throw eee;
                                }
                                cnt5++;
                            } while (true);


                            }
                            break;
                        case 2 :
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:160: ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )?
                            {
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:160: ( '0' .. '9' )+
                            int cnt6=0;
                            loop6:
                            do {
                                int alt6=2;
                                int LA6_0 = input.LA(1);

                                if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                                    alt6=1;
                                }


                                switch (alt6) {
                            	case 1 :
                            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:161: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); 

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt6 >= 1 ) break loop6;
                                        EarlyExitException eee =
                                            new EarlyExitException(6, input);
                                        throw eee;
                                }
                                cnt6++;
                            } while (true);

                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:172: ( '.' ( '0' .. '9' )* )?
                            int alt8=2;
                            int LA8_0 = input.LA(1);

                            if ( (LA8_0=='.') ) {
                                alt8=1;
                            }
                            switch (alt8) {
                                case 1 :
                                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:173: '.' ( '0' .. '9' )*
                                    {
                                    match('.'); 
                                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:177: ( '0' .. '9' )*
                                    loop7:
                                    do {
                                        int alt7=2;
                                        int LA7_0 = input.LA(1);

                                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                                            alt7=1;
                                        }


                                        switch (alt7) {
                                    	case 1 :
                                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2344:178: '0' .. '9'
                                    	    {
                                    	    matchRange('0','9'); 

                                    	    }
                                    	    break;

                                    	default :
                                    	    break loop7;
                                        }
                                    } while (true);


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_DOT_ID

    // $ANTLR start RULE_ID
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2346:9: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2346:11: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2346:11: ( '^' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='^') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2346:11: '^'
                    {
                    match('^'); 

                    }
                    break;

            }

            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2346:40: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_ID

    // $ANTLR start RULE_INT
    public final void mRULE_INT() throws RecognitionException {
        try {
            int _type = RULE_INT;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2348:10: ( ( '0' .. '9' )+ )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2348:12: ( '0' .. '9' )+
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2348:12: ( '0' .. '9' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2348:13: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_INT

    // $ANTLR start RULE_STRING
    public final void mRULE_STRING() throws RecognitionException {
        try {
            int _type = RULE_STRING;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:13: ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='\"') ) {
                alt16=1;
            }
            else if ( (LA16_0=='\'') ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2350:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:16: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:20: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop14:
                    do {
                        int alt14=3;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0=='\\') ) {
                            alt14=1;
                        }
                        else if ( ((LA14_0>='\u0000' && LA14_0<='!')||(LA14_0>='#' && LA14_0<='[')||(LA14_0>=']' && LA14_0<='\uFFFE')) ) {
                            alt14=2;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:21: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:62: ~ ( ( '\\\\' | '\"' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:82: '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:87: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop15:
                    do {
                        int alt15=3;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0=='\\') ) {
                            alt15=1;
                        }
                        else if ( ((LA15_0>='\u0000' && LA15_0<='&')||(LA15_0>='(' && LA15_0<='[')||(LA15_0>=']' && LA15_0<='\uFFFE')) ) {
                            alt15=2;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:88: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2350:129: ~ ( ( '\\\\' | '\\'' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_STRING

    // $ANTLR start RULE_ML_COMMENT
    public final void mRULE_ML_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_ML_COMMENT;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2352:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2352:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2352:24: ( options {greedy=false; } : . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='*') ) {
                    int LA17_1 = input.LA(2);

                    if ( (LA17_1=='/') ) {
                        alt17=2;
                    }
                    else if ( ((LA17_1>='\u0000' && LA17_1<='.')||(LA17_1>='0' && LA17_1<='\uFFFE')) ) {
                        alt17=1;
                    }


                }
                else if ( ((LA17_0>='\u0000' && LA17_0<=')')||(LA17_0>='+' && LA17_0<='\uFFFE')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2352:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            match("*/"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_ML_COMMENT

    // $ANTLR start RULE_SL_COMMENT
    public final void mRULE_SL_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_SL_COMMENT;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>='\u0000' && LA18_0<='\t')||(LA18_0>='\u000B' && LA18_0<='\f')||(LA18_0>='\u000E' && LA18_0<='\uFFFE')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:24: ~ ( ( '\\n' | '\\r' ) )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:40: ( ( '\\r' )? '\\n' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='\n'||LA20_0=='\r') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:41: ( '\\r' )? '\\n'
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:41: ( '\\r' )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='\r') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2354:41: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_SL_COMMENT

    // $ANTLR start RULE_WS
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2356:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2356:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2356:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>='\t' && LA21_0<='\n')||LA21_0=='\r'||LA21_0==' ') ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_WS

    // $ANTLR start RULE_ANY_OTHER
    public final void mRULE_ANY_OTHER() throws RecognitionException {
        try {
            int _type = RULE_ANY_OTHER;
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2358:16: ( . )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2358:18: .
            {
            matchAny(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE_ANY_OTHER

    public void mTokens() throws RecognitionException {
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:8: ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | RULE_DOT_ID | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
        int alt22=23;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:10: T12
                {
                mT12(); 

                }
                break;
            case 2 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:14: T13
                {
                mT13(); 

                }
                break;
            case 3 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:18: T14
                {
                mT14(); 

                }
                break;
            case 4 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:22: T15
                {
                mT15(); 

                }
                break;
            case 5 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:26: T16
                {
                mT16(); 

                }
                break;
            case 6 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:30: T17
                {
                mT17(); 

                }
                break;
            case 7 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:34: T18
                {
                mT18(); 

                }
                break;
            case 8 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:38: T19
                {
                mT19(); 

                }
                break;
            case 9 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:42: T20
                {
                mT20(); 

                }
                break;
            case 10 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:46: T21
                {
                mT21(); 

                }
                break;
            case 11 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:50: T22
                {
                mT22(); 

                }
                break;
            case 12 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:54: T23
                {
                mT23(); 

                }
                break;
            case 13 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:58: T24
                {
                mT24(); 

                }
                break;
            case 14 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:62: T25
                {
                mT25(); 

                }
                break;
            case 15 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:66: T26
                {
                mT26(); 

                }
                break;
            case 16 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:70: RULE_DOT_ID
                {
                mRULE_DOT_ID(); 

                }
                break;
            case 17 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:82: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 18 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:90: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 19 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:99: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;
            case 20 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:111: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 21 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:127: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 22 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:143: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 23 :
                // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1:151: RULE_ANY_OTHER
                {
                mRULE_ANY_OTHER(); 

                }
                break;

        }

    }


    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA22_eotS =
        "\1\uffff\1\26\4\31\7\uffff\1\31\1\26\1\31\2\26\1\31\2\26\5\uffff"+
        "\5\31\7\uffff\3\31\3\uffff\1\31\4\uffff\7\31\2\uffff\2\31\1\105"+
        "\1\106\2\31\3\uffff\1\111\1\31\2\uffff\2\31\1\uffff\2\31\1\117\1"+
        "\120\1\31\2\uffff\1\122\1\uffff";
    static final String DFA22_eofS =
        "\123\uffff";
    static final String DFA22_minS =
        "\1\0\1\55\4\60\7\uffff\1\60\1\101\1\60\1\0\2\60\1\0\1\52\5\uffff"+
        "\5\60\7\uffff\3\60\2\0\1\uffff\1\60\4\uffff\6\60\3\0\6\60\1\uffff"+
        "\2\0\2\60\2\uffff\2\60\1\uffff\5\60\2\uffff\1\60\1\uffff";
    static final String DFA22_maxS =
        "\1\ufffe\1\76\4\172\7\uffff\3\172\1\ufffe\2\71\1\ufffe\1\57\5\uffff"+
        "\5\172\7\uffff\3\172\2\ufffe\1\uffff\1\71\4\uffff\6\172\3\ufffe"+
        "\6\172\1\uffff\2\ufffe\2\172\2\uffff\2\172\1\uffff\5\172\2\uffff"+
        "\1\172\1\uffff";
    static final String DFA22_acceptS =
        "\6\uffff\1\7\1\10\1\11\1\12\1\13\1\14\1\15\10\uffff\1\26\1\27\1"+
        "\1\1\2\1\20\5\uffff\1\7\1\10\1\11\1\12\1\13\1\14\1\15\5\uffff\1"+
        "\20\1\uffff\1\23\1\24\1\25\1\26\17\uffff\1\20\4\uffff\1\5\1\6\2"+
        "\uffff\1\3\5\uffff\1\17\1\4\1\uffff\1\16";
    static final String DFA22_specialS =
        "\123\uffff}>";
    static final String[] DFA22_transitionS = {
            "\11\26\2\25\2\26\1\25\22\26\1\25\1\26\1\20\4\26\1\23\4\26\1"+
            "\14\1\1\1\21\1\24\12\22\1\26\1\10\1\26\1\11\3\26\32\17\1\12"+
            "\1\26\1\13\1\16\1\17\1\26\3\17\1\3\1\5\1\17\1\2\6\17\1\4\4\17"+
            "\1\15\7\17\1\6\1\26\1\7\uff81\26",
            "\1\30\1\31\1\uffff\12\31\4\uffff\1\27",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\21\33\1\32\10\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\10\33\1\34\21\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\16\33\1\35\13\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\3\33\1\36\26\33",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\23\33\1\47\1\46\5"+
            "\33",
            "\32\50\4\uffff\1\50\1\uffff\32\50",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\42\52\1\53\71\52\1\51\uffa2\52",
            "\12\31",
            "\12\54",
            "\uffff\55",
            "\1\56\4\uffff\1\57",
            "",
            "",
            "",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\1\61\31\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\6\33\1\62\23\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\3\33\1\63\26\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\6\33\1\64\23\33",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\1\33\1\65\30\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\21\33\1\66\10\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\42\31\1\67\4\31\1\71\64\31\1\70\5\31\1\71\3\31\1\71\7\31\1"+
            "\71\3\31\1\71\1\31\1\71\uff8a\31",
            "\42\52\1\53\71\52\1\51\uffa2\52",
            "",
            "\12\54",
            "",
            "",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\17\33\1\72\12\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\21\33\1\73\10\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\4\33\1\74\25\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\4\33\1\75\25\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\6\33\1\76\23\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\10\33\1\77\21\33",
            "\42\52\1\53\71\52\1\51\uffa2\52",
            "\42\52\1\100\4\52\1\102\64\52\1\101\5\52\1\102\3\52\1\102\7"+
            "\52\1\102\3\52\1\102\1\52\1\102\uff8a\52",
            "\42\52\1\53\71\52\1\51\uffa2\52",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\7\33\1\103\22\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\1\104\31\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\21\33\1\107\10\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\2\33\1\110\27\33",
            "",
            "\42\31\1\67\4\31\1\71\64\31\1\70\5\31\1\71\3\31\1\71\7\31\1"+
            "\71\3\31\1\71\1\31\1\71\uff8a\31",
            "\42\52\1\53\71\52\1\51\uffa2\52",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\17\33\1\112\12\33",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\1\113\31\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\23\33\1\114\6\33",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\7\33\1\115\22\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\17\33\1\116\12\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\7\33\1\121\22\33",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T12 | T13 | T14 | T15 | T16 | T17 | T18 | T19 | T20 | T21 | T22 | T23 | T24 | T25 | T26 | RULE_DOT_ID | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );";
        }
    }
 

}