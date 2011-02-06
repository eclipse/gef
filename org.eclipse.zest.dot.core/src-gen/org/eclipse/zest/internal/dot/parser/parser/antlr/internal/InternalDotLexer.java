package org.eclipse.zest.internal.dot.parser.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalDotLexer extends Lexer {
    public static final int RULE_ID=5;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=11;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int EOF=-1;
    public static final int RULE_SL_COMMENT=9;
    public static final int RULE_ML_COMMENT=8;
    public static final int T__19=19;
    public static final int RULE_STRING=7;
    public static final int T__16=16;
    public static final int RULE_DOT_ID=4;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int RULE_INT=6;
    public static final int RULE_WS=10;

    // delegates
    // delegators

    public InternalDotLexer() {;} 
    public InternalDotLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public InternalDotLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g"; }

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:11:7: ( 'strict' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:11:9: 'strict'
            {
            match("strict"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:12:7: ( '{' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:12:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:13:7: ( '}' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:13:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:14:7: ( ';' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:14:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:15:7: ( '=' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:15:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:16:7: ( '[' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:16:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:17:7: ( ']' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:17:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:18:7: ( ',' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:18:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:19:7: ( 'subgraph' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:19:9: 'subgraph'
            {
            match("subgraph"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:20:7: ( '->' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:20:9: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:21:7: ( '--' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:21:9: '--'
            {
            match("--"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:22:7: ( 'graph' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:22:9: 'graph'
            {
            match("graph"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:23:7: ( 'digraph' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:23:9: 'digraph'
            {
            match("digraph"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:24:7: ( 'node' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:24:9: 'node'
            {
            match("node"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:25:7: ( 'edge' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:25:9: 'edge'
            {
            match("edge"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "RULE_DOT_ID"
    public final void mRULE_DOT_ID() throws RecognitionException {
        try {
            int _type = RULE_DOT_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:13: ( ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:15: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:15: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* | '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"' | ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? ) )
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
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:16: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:16: ( '^' )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0=='^') ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:16: '^'
                            {
                            match('^'); 

                            }
                            break;

                    }

                    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:45: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:79: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:83: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) | ~ ( '\"' ) )*
                    loop3:
                    do {
                        int alt3=3;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0=='\\') ) {
                            int LA3_2 = input.LA(2);

                            if ( (LA3_2=='\"') ) {
                                int LA3_4 = input.LA(3);

                                if ( ((LA3_4>='\u0000' && LA3_4<='\uFFFF')) ) {
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
                            else if ( ((LA3_2>='\u0000' && LA3_2<='!')||(LA3_2>='#' && LA3_2<='&')||(LA3_2>='(' && LA3_2<='[')||(LA3_2>=']' && LA3_2<='a')||(LA3_2>='c' && LA3_2<='e')||(LA3_2>='g' && LA3_2<='m')||(LA3_2>='o' && LA3_2<='q')||LA3_2=='s'||(LA3_2>='u' && LA3_2<='\uFFFF')) ) {
                                alt3=2;
                            }


                        }
                        else if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='\uFFFF')) ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:84: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:125: ~ ( '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


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
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:138: ( '-' )? ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:138: ( '-' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='-') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:138: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }

                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:143: ( '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )
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
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:144: '.' ( '0' .. '9' )+
                            {
                            match('.'); 
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:148: ( '0' .. '9' )+
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
                            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:149: '0' .. '9'
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
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:160: ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )?
                            {
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:160: ( '0' .. '9' )+
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
                            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:161: '0' .. '9'
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

                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:172: ( '.' ( '0' .. '9' )* )?
                            int alt8=2;
                            int LA8_0 = input.LA(1);

                            if ( (LA8_0=='.') ) {
                                alt8=1;
                            }
                            switch (alt8) {
                                case 1 :
                                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:173: '.' ( '0' .. '9' )*
                                    {
                                    match('.'); 
                                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:177: ( '0' .. '9' )*
                                    loop7:
                                    do {
                                        int alt7=2;
                                        int LA7_0 = input.LA(1);

                                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                                            alt7=1;
                                        }


                                        switch (alt7) {
                                    	case 1 :
                                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1082:178: '0' .. '9'
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_DOT_ID"

    // $ANTLR start "RULE_ID"
    public final void mRULE_ID() throws RecognitionException {
        try {
            int _type = RULE_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1084:9: ( ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1084:11: ( '^' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1084:11: ( '^' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='^') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1084:11: '^'
                    {
                    match('^'); 

                    }
                    break;

            }

            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1084:40: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ID"

    // $ANTLR start "RULE_INT"
    public final void mRULE_INT() throws RecognitionException {
        try {
            int _type = RULE_INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1086:10: ( ( '0' .. '9' )+ )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1086:12: ( '0' .. '9' )+
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1086:12: ( '0' .. '9' )+
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
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1086:13: '0' .. '9'
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_INT"

    // $ANTLR start "RULE_STRING"
    public final void mRULE_STRING() throws RecognitionException {
        try {
            int _type = RULE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:13: ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:15: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
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
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:16: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:20: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\"' ) ) )*
                    loop14:
                    do {
                        int alt14=3;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0=='\\') ) {
                            alt14=1;
                        }
                        else if ( ((LA14_0>='\u0000' && LA14_0<='!')||(LA14_0>='#' && LA14_0<='[')||(LA14_0>=']' && LA14_0<='\uFFFF')) ) {
                            alt14=2;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:21: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||(input.LA(1)>='t' && input.LA(1)<='u') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:66: ~ ( ( '\\\\' | '\"' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


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
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:86: '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:91: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )*
                    loop15:
                    do {
                        int alt15=3;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0=='\\') ) {
                            alt15=1;
                        }
                        else if ( ((LA15_0>='\u0000' && LA15_0<='&')||(LA15_0>='(' && LA15_0<='[')||(LA15_0>=']' && LA15_0<='\uFFFF')) ) {
                            alt15=2;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:92: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 
                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||(input.LA(1)>='t' && input.LA(1)<='u') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;
                    	case 2 :
                    	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1088:137: ~ ( ( '\\\\' | '\\'' ) )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_STRING"

    // $ANTLR start "RULE_ML_COMMENT"
    public final void mRULE_ML_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1090:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1090:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1090:24: ( options {greedy=false; } : . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='*') ) {
                    int LA17_1 = input.LA(2);

                    if ( (LA17_1=='/') ) {
                        alt17=2;
                    }
                    else if ( ((LA17_1>='\u0000' && LA17_1<='.')||(LA17_1>='0' && LA17_1<='\uFFFF')) ) {
                        alt17=1;
                    }


                }
                else if ( ((LA17_0>='\u0000' && LA17_0<=')')||(LA17_0>='+' && LA17_0<='\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1090:52: .
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ML_COMMENT"

    // $ANTLR start "RULE_SL_COMMENT"
    public final void mRULE_SL_COMMENT() throws RecognitionException {
        try {
            int _type = RULE_SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:17: ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:19: '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
            {
            match("//"); 

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:24: (~ ( ( '\\n' | '\\r' ) ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>='\u0000' && LA18_0<='\t')||(LA18_0>='\u000B' && LA18_0<='\f')||(LA18_0>='\u000E' && LA18_0<='\uFFFF')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:24: ~ ( ( '\\n' | '\\r' ) )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:40: ( ( '\\r' )? '\\n' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='\n'||LA20_0=='\r') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:41: ( '\\r' )? '\\n'
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:41: ( '\\r' )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='\r') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1092:41: '\\r'
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_SL_COMMENT"

    // $ANTLR start "RULE_WS"
    public final void mRULE_WS() throws RecognitionException {
        try {
            int _type = RULE_WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1094:9: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1094:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1094:11: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_WS"

    // $ANTLR start "RULE_ANY_OTHER"
    public final void mRULE_ANY_OTHER() throws RecognitionException {
        try {
            int _type = RULE_ANY_OTHER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1096:16: ( . )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1096:18: .
            {
            matchAny(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_ANY_OTHER"

    public void mTokens() throws RecognitionException {
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:8: ( T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | RULE_DOT_ID | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
        int alt22=23;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:10: T__12
                {
                mT__12(); 

                }
                break;
            case 2 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:16: T__13
                {
                mT__13(); 

                }
                break;
            case 3 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:22: T__14
                {
                mT__14(); 

                }
                break;
            case 4 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:28: T__15
                {
                mT__15(); 

                }
                break;
            case 5 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:34: T__16
                {
                mT__16(); 

                }
                break;
            case 6 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:40: T__17
                {
                mT__17(); 

                }
                break;
            case 7 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:46: T__18
                {
                mT__18(); 

                }
                break;
            case 8 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:52: T__19
                {
                mT__19(); 

                }
                break;
            case 9 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:58: T__20
                {
                mT__20(); 

                }
                break;
            case 10 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:64: T__21
                {
                mT__21(); 

                }
                break;
            case 11 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:70: T__22
                {
                mT__22(); 

                }
                break;
            case 12 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:76: T__23
                {
                mT__23(); 

                }
                break;
            case 13 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:82: T__24
                {
                mT__24(); 

                }
                break;
            case 14 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:88: T__25
                {
                mT__25(); 

                }
                break;
            case 15 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:94: T__26
                {
                mT__26(); 

                }
                break;
            case 16 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:100: RULE_DOT_ID
                {
                mRULE_DOT_ID(); 

                }
                break;
            case 17 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:112: RULE_ID
                {
                mRULE_ID(); 

                }
                break;
            case 18 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:120: RULE_INT
                {
                mRULE_INT(); 

                }
                break;
            case 19 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:129: RULE_STRING
                {
                mRULE_STRING(); 

                }
                break;
            case 20 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:141: RULE_ML_COMMENT
                {
                mRULE_ML_COMMENT(); 

                }
                break;
            case 21 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:157: RULE_SL_COMMENT
                {
                mRULE_SL_COMMENT(); 

                }
                break;
            case 22 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:173: RULE_WS
                {
                mRULE_WS(); 

                }
                break;
            case 23 :
                // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1:181: RULE_ANY_OTHER
                {
                mRULE_ANY_OTHER(); 

                }
                break;

        }

    }


    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA22_eotS =
        "\1\uffff\1\31\7\uffff\1\26\4\31\1\26\1\31\2\26\1\31\2\26\2\uffff"+
        "\2\31\1\uffff\1\31\11\uffff\5\31\3\uffff\1\31\4\uffff\7\31\3\uffff"+
        "\4\31\1\110\1\111\3\uffff\2\31\1\114\1\31\2\uffff\1\116\1\31\1\uffff"+
        "\1\31\1\uffff\1\31\1\122\1\123\2\uffff";
    static final String DFA22_eofS =
        "\124\uffff";
    static final String DFA22_minS =
        "\1\0\1\60\7\uffff\1\55\4\60\1\101\1\60\1\0\2\60\1\0\1\52\2\uffff"+
        "\2\60\1\uffff\1\60\11\uffff\5\60\2\0\1\uffff\1\60\4\uffff\6\60\4"+
        "\0\6\60\1\uffff\2\0\4\60\2\uffff\2\60\1\uffff\1\60\1\uffff\3\60"+
        "\2\uffff";
    static final String DFA22_maxS =
        "\1\uffff\1\172\7\uffff\1\76\6\172\1\uffff\2\71\1\uffff\1\57\2\uffff"+
        "\2\172\1\uffff\1\172\11\uffff\5\172\2\uffff\1\uffff\1\71\4\uffff"+
        "\6\172\4\uffff\6\172\1\uffff\2\uffff\4\172\2\uffff\2\172\1\uffff"+
        "\1\172\1\uffff\3\172\2\uffff";
    static final String DFA22_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\14\uffff\1\26\1\27\2\uffff"+
        "\1\20\1\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\12\1\13\7\uffff\1\20"+
        "\1\uffff\1\23\1\24\1\25\1\26\20\uffff\1\20\6\uffff\1\16\1\17\2\uffff"+
        "\1\14\1\uffff\1\1\3\uffff\1\15\1\11";
    static final String DFA22_specialS =
        "\1\0\17\uffff\1\1\2\uffff\1\2\25\uffff\1\5\1\6\14\uffff\1\12\1\7"+
        "\1\11\1\4\7\uffff\1\10\1\3\20\uffff}>";
    static final String[] DFA22_transitionS = {
            "\11\26\2\25\2\26\1\25\22\26\1\25\1\26\1\20\4\26\1\23\4\26\1"+
            "\10\1\11\1\21\1\24\12\22\1\26\1\4\1\26\1\5\3\26\32\17\1\6\1"+
            "\26\1\7\1\16\1\17\1\26\3\17\1\13\1\15\1\17\1\12\6\17\1\14\4"+
            "\17\1\1\7\17\1\2\1\26\1\3\uff82\26",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\23\32\1\27\1\30\5"+
            "\32",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\43\1\31\1\uffff\12\31\4\uffff\1\42",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\21\32\1\44\10\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\10\32\1\45\21\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\16\32\1\46\13\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\3\32\1\47\26\32",
            "\32\50\4\uffff\1\50\1\uffff\32\50",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\42\52\1\53\71\52\1\51\uffa3\52",
            "\12\31",
            "\12\54",
            "\0\55",
            "\1\56\4\uffff\1\57",
            "",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\21\32\1\61\10\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\1\32\1\62\30\32",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\1\63\31\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\6\32\1\64\23\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\3\32\1\65\26\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\6\32\1\66\23\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\42\31\1\67\4\31\1\72\64\31\1\71\5\31\1\72\3\31\1\72\7\31\1"+
            "\72\3\31\1\72\1\31\1\72\1\70\uff8a\31",
            "\42\52\1\53\71\52\1\51\uffa3\52",
            "",
            "\12\54",
            "",
            "",
            "",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\10\32\1\73\21\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\6\32\1\74\23\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\17\32\1\75\12\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\21\32\1\76\10\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\4\32\1\77\25\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\4\32\1\100\25\32",
            "\42\52\1\53\71\52\1\51\uffa3\52",
            "\42\52\1\53\71\52\1\51\uffa3\52",
            "\42\52\1\101\4\52\1\103\64\52\1\102\5\52\1\103\3\52\1\103\7"+
            "\52\1\103\3\52\1\103\1\52\1\103\uff8b\52",
            "\42\52\1\53\71\52\1\51\uffa3\52",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\2\32\1\104\27\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\21\32\1\105\10\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\7\32\1\106\22\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\1\107\31\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "",
            "\42\31\1\67\4\31\1\72\64\31\1\71\5\31\1\72\3\31\1\72\7\31\1"+
            "\72\3\31\1\72\1\31\1\72\1\70\uff8a\31",
            "\42\52\1\53\71\52\1\51\uffa3\52",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\23\32\1\112\6\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\1\113\31\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\17\32\1\115\12\32",
            "",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\17\32\1\117\12\32",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\7\32\1\120\22\32",
            "",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\7\32\1\121\22\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "\12\32\7\uffff\32\32\4\uffff\1\32\1\uffff\32\32",
            "",
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
            return "1:1: Tokens : ( T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | RULE_DOT_ID | RULE_ID | RULE_INT | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_0 = input.LA(1);

                        s = -1;
                        if ( (LA22_0=='s') ) {s = 1;}

                        else if ( (LA22_0=='{') ) {s = 2;}

                        else if ( (LA22_0=='}') ) {s = 3;}

                        else if ( (LA22_0==';') ) {s = 4;}

                        else if ( (LA22_0=='=') ) {s = 5;}

                        else if ( (LA22_0=='[') ) {s = 6;}

                        else if ( (LA22_0==']') ) {s = 7;}

                        else if ( (LA22_0==',') ) {s = 8;}

                        else if ( (LA22_0=='-') ) {s = 9;}

                        else if ( (LA22_0=='g') ) {s = 10;}

                        else if ( (LA22_0=='d') ) {s = 11;}

                        else if ( (LA22_0=='n') ) {s = 12;}

                        else if ( (LA22_0=='e') ) {s = 13;}

                        else if ( (LA22_0=='^') ) {s = 14;}

                        else if ( ((LA22_0>='A' && LA22_0<='Z')||LA22_0=='_'||(LA22_0>='a' && LA22_0<='c')||LA22_0=='f'||(LA22_0>='h' && LA22_0<='m')||(LA22_0>='o' && LA22_0<='r')||(LA22_0>='t' && LA22_0<='z')) ) {s = 15;}

                        else if ( (LA22_0=='\"') ) {s = 16;}

                        else if ( (LA22_0=='.') ) {s = 17;}

                        else if ( ((LA22_0>='0' && LA22_0<='9')) ) {s = 18;}

                        else if ( (LA22_0=='\'') ) {s = 19;}

                        else if ( (LA22_0=='/') ) {s = 20;}

                        else if ( ((LA22_0>='\t' && LA22_0<='\n')||LA22_0=='\r'||LA22_0==' ') ) {s = 21;}

                        else if ( ((LA22_0>='\u0000' && LA22_0<='\b')||(LA22_0>='\u000B' && LA22_0<='\f')||(LA22_0>='\u000E' && LA22_0<='\u001F')||LA22_0=='!'||(LA22_0>='#' && LA22_0<='&')||(LA22_0>='(' && LA22_0<='+')||LA22_0==':'||LA22_0=='<'||(LA22_0>='>' && LA22_0<='@')||LA22_0=='\\'||LA22_0=='`'||LA22_0=='|'||(LA22_0>='~' && LA22_0<='\uFFFF')) ) {s = 22;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA22_16 = input.LA(1);

                        s = -1;
                        if ( (LA22_16=='\\') ) {s = 41;}

                        else if ( ((LA22_16>='\u0000' && LA22_16<='!')||(LA22_16>='#' && LA22_16<='[')||(LA22_16>=']' && LA22_16<='\uFFFF')) ) {s = 42;}

                        else if ( (LA22_16=='\"') ) {s = 43;}

                        else s = 22;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA22_19 = input.LA(1);

                        s = -1;
                        if ( ((LA22_19>='\u0000' && LA22_19<='\uFFFF')) ) {s = 45;}

                        else s = 22;

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA22_67 = input.LA(1);

                        s = -1;
                        if ( (LA22_67=='\"') ) {s = 43;}

                        else if ( (LA22_67=='\\') ) {s = 41;}

                        else if ( ((LA22_67>='\u0000' && LA22_67<='!')||(LA22_67>='#' && LA22_67<='[')||(LA22_67>=']' && LA22_67<='\uFFFF')) ) {s = 42;}

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA22_58 = input.LA(1);

                        s = -1;
                        if ( (LA22_58=='\"') ) {s = 43;}

                        else if ( (LA22_58=='\\') ) {s = 41;}

                        else if ( ((LA22_58>='\u0000' && LA22_58<='!')||(LA22_58>='#' && LA22_58<='[')||(LA22_58>=']' && LA22_58<='\uFFFF')) ) {s = 42;}

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA22_41 = input.LA(1);

                        s = -1;
                        if ( (LA22_41=='\"') ) {s = 55;}

                        else if ( (LA22_41=='u') ) {s = 56;}

                        else if ( (LA22_41=='\\') ) {s = 57;}

                        else if ( (LA22_41=='\''||LA22_41=='b'||LA22_41=='f'||LA22_41=='n'||LA22_41=='r'||LA22_41=='t') ) {s = 58;}

                        else if ( ((LA22_41>='\u0000' && LA22_41<='!')||(LA22_41>='#' && LA22_41<='&')||(LA22_41>='(' && LA22_41<='[')||(LA22_41>=']' && LA22_41<='a')||(LA22_41>='c' && LA22_41<='e')||(LA22_41>='g' && LA22_41<='m')||(LA22_41>='o' && LA22_41<='q')||LA22_41=='s'||(LA22_41>='v' && LA22_41<='\uFFFF')) ) {s = 25;}

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA22_42 = input.LA(1);

                        s = -1;
                        if ( (LA22_42=='\"') ) {s = 43;}

                        else if ( (LA22_42=='\\') ) {s = 41;}

                        else if ( ((LA22_42>='\u0000' && LA22_42<='!')||(LA22_42>='#' && LA22_42<='[')||(LA22_42>=']' && LA22_42<='\uFFFF')) ) {s = 42;}

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA22_56 = input.LA(1);

                        s = -1;
                        if ( (LA22_56=='\"') ) {s = 43;}

                        else if ( (LA22_56=='\\') ) {s = 41;}

                        else if ( ((LA22_56>='\u0000' && LA22_56<='!')||(LA22_56>='#' && LA22_56<='[')||(LA22_56>=']' && LA22_56<='\uFFFF')) ) {s = 42;}

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA22_66 = input.LA(1);

                        s = -1;
                        if ( (LA22_66=='\"') ) {s = 55;}

                        else if ( (LA22_66=='u') ) {s = 56;}

                        else if ( (LA22_66=='\\') ) {s = 57;}

                        else if ( (LA22_66=='\''||LA22_66=='b'||LA22_66=='f'||LA22_66=='n'||LA22_66=='r'||LA22_66=='t') ) {s = 58;}

                        else if ( ((LA22_66>='\u0000' && LA22_66<='!')||(LA22_66>='#' && LA22_66<='&')||(LA22_66>='(' && LA22_66<='[')||(LA22_66>=']' && LA22_66<='a')||(LA22_66>='c' && LA22_66<='e')||(LA22_66>='g' && LA22_66<='m')||(LA22_66>='o' && LA22_66<='q')||LA22_66=='s'||(LA22_66>='v' && LA22_66<='\uFFFF')) ) {s = 25;}

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA22_57 = input.LA(1);

                        s = -1;
                        if ( (LA22_57=='\"') ) {s = 65;}

                        else if ( (LA22_57=='\\') ) {s = 66;}

                        else if ( (LA22_57=='\''||LA22_57=='b'||LA22_57=='f'||LA22_57=='n'||LA22_57=='r'||LA22_57=='t') ) {s = 67;}

                        else if ( ((LA22_57>='\u0000' && LA22_57<='!')||(LA22_57>='#' && LA22_57<='&')||(LA22_57>='(' && LA22_57<='[')||(LA22_57>=']' && LA22_57<='a')||(LA22_57>='c' && LA22_57<='e')||(LA22_57>='g' && LA22_57<='m')||(LA22_57>='o' && LA22_57<='q')||LA22_57=='s'||(LA22_57>='u' && LA22_57<='\uFFFF')) ) {s = 42;}

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA22_55 = input.LA(1);

                        s = -1;
                        if ( (LA22_55=='\"') ) {s = 43;}

                        else if ( (LA22_55=='\\') ) {s = 41;}

                        else if ( ((LA22_55>='\u0000' && LA22_55<='!')||(LA22_55>='#' && LA22_55<='[')||(LA22_55>=']' && LA22_55<='\uFFFF')) ) {s = 42;}

                        else s = 25;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 22, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}