package org.eclipse.zest.internal.dot.parser.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.zest.internal.dot.parser.services.DotGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalDotParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_DOT_ID", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'strict'", "'{'", "'}'", "';'", "'='", "'['", "']'", "','", "'subgraph'", "'->'", "'--'", "'graph'", "'digraph'", "'node'", "'edge'"
    };
    public static final int RULE_ID=5;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=11;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int RULE_SL_COMMENT=9;
    public static final int EOF=-1;
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


        public InternalDotParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalDotParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalDotParser.tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */
     
     	private DotGrammarAccess grammarAccess;
     	
        public InternalDotParser(TokenStream input, DotGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "GraphvizModel";	
       	}
       	
       	@Override
       	protected DotGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleGraphvizModel"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:74:1: entryRuleGraphvizModel returns [EObject current=null] : iv_ruleGraphvizModel= ruleGraphvizModel EOF ;
    public final EObject entryRuleGraphvizModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGraphvizModel = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:75:2: (iv_ruleGraphvizModel= ruleGraphvizModel EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:76:2: iv_ruleGraphvizModel= ruleGraphvizModel EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGraphvizModelRule()); 
            }
            pushFollow(FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel81);
            iv_ruleGraphvizModel=ruleGraphvizModel();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGraphvizModel; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGraphvizModel91); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGraphvizModel"


    // $ANTLR start "ruleGraphvizModel"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:83:1: ruleGraphvizModel returns [EObject current=null] : ( (lv_graphs_0_0= ruleMainGraph ) )* ;
    public final EObject ruleGraphvizModel() throws RecognitionException {
        EObject current = null;

        EObject lv_graphs_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:86:28: ( ( (lv_graphs_0_0= ruleMainGraph ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:87:1: ( (lv_graphs_0_0= ruleMainGraph ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:87:1: ( (lv_graphs_0_0= ruleMainGraph ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12||(LA1_0>=23 && LA1_0<=24)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:88:1: (lv_graphs_0_0= ruleMainGraph )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:88:1: (lv_graphs_0_0= ruleMainGraph )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:89:3: lv_graphs_0_0= ruleMainGraph
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGraphvizModelAccess().getGraphsMainGraphParserRuleCall_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleMainGraph_in_ruleGraphvizModel136);
            	    lv_graphs_0_0=ruleMainGraph();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getGraphvizModelRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"graphs",
            	              		lv_graphs_0_0, 
            	              		"MainGraph");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGraphvizModel"


    // $ANTLR start "entryRuleMainGraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:113:1: entryRuleMainGraph returns [EObject current=null] : iv_ruleMainGraph= ruleMainGraph EOF ;
    public final EObject entryRuleMainGraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMainGraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:114:2: (iv_ruleMainGraph= ruleMainGraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:115:2: iv_ruleMainGraph= ruleMainGraph EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMainGraphRule()); 
            }
            pushFollow(FOLLOW_ruleMainGraph_in_entryRuleMainGraph172);
            iv_ruleMainGraph=ruleMainGraph();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMainGraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMainGraph182); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleMainGraph"


    // $ANTLR start "ruleMainGraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:122:1: ruleMainGraph returns [EObject current=null] : ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? otherlv_3= '{' ( (lv_stmts_4_0= ruleStmt ) )* otherlv_5= '}' ) ;
    public final EObject ruleMainGraph() throws RecognitionException {
        EObject current = null;

        Token lv_strict_0_0=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Enumerator lv_type_1_0 = null;

        EObject lv_stmts_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:125:28: ( ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? otherlv_3= '{' ( (lv_stmts_4_0= ruleStmt ) )* otherlv_5= '}' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:126:1: ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? otherlv_3= '{' ( (lv_stmts_4_0= ruleStmt ) )* otherlv_5= '}' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:126:1: ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? otherlv_3= '{' ( (lv_stmts_4_0= ruleStmt ) )* otherlv_5= '}' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:126:2: ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? otherlv_3= '{' ( (lv_stmts_4_0= ruleStmt ) )* otherlv_5= '}'
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:126:2: ( (lv_strict_0_0= 'strict' ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==12) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:127:1: (lv_strict_0_0= 'strict' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:127:1: (lv_strict_0_0= 'strict' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:128:3: lv_strict_0_0= 'strict'
                    {
                    lv_strict_0_0=(Token)match(input,12,FOLLOW_12_in_ruleMainGraph225); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_strict_0_0, grammarAccess.getMainGraphAccess().getStrictStrictKeyword_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getMainGraphRule());
                      	        }
                             		setWithLastConsumed(current, "strict", true, "strict");
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:141:3: ( (lv_type_1_0= ruleGraphType ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:142:1: (lv_type_1_0= ruleGraphType )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:142:1: (lv_type_1_0= ruleGraphType )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:143:3: lv_type_1_0= ruleGraphType
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getMainGraphAccess().getTypeGraphTypeEnumRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleGraphType_in_ruleMainGraph260);
            lv_type_1_0=ruleGraphType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getMainGraphRule());
              	        }
                     		set(
                     			current, 
                     			"type",
                      		lv_type_1_0, 
                      		"GraphType");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:159:2: ( (lv_name_2_0= RULE_DOT_ID ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_DOT_ID) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:160:1: (lv_name_2_0= RULE_DOT_ID )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:160:1: (lv_name_2_0= RULE_DOT_ID )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:161:3: lv_name_2_0= RULE_DOT_ID
                    {
                    lv_name_2_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleMainGraph277); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_2_0, grammarAccess.getMainGraphAccess().getNameDOT_IDTerminalRuleCall_2_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getMainGraphRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_2_0, 
                              		"DOT_ID");
                      	    
                    }

                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,13,FOLLOW_13_in_ruleMainGraph295); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getMainGraphAccess().getLeftCurlyBracketKeyword_3());
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:181:1: ( (lv_stmts_4_0= ruleStmt ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE_DOT_ID||LA4_0==13||LA4_0==20||LA4_0==23||(LA4_0>=25 && LA4_0<=26)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:182:1: (lv_stmts_4_0= ruleStmt )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:182:1: (lv_stmts_4_0= ruleStmt )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:183:3: lv_stmts_4_0= ruleStmt
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMainGraphAccess().getStmtsStmtParserRuleCall_4_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStmt_in_ruleMainGraph316);
            	    lv_stmts_4_0=ruleStmt();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getMainGraphRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"stmts",
            	              		lv_stmts_4_0, 
            	              		"Stmt");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            otherlv_5=(Token)match(input,14,FOLLOW_14_in_ruleMainGraph329); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_5, grammarAccess.getMainGraphAccess().getRightCurlyBracketKeyword_5());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMainGraph"


    // $ANTLR start "entryRuleStmt"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:211:1: entryRuleStmt returns [EObject current=null] : iv_ruleStmt= ruleStmt EOF ;
    public final EObject entryRuleStmt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStmt = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:212:2: (iv_ruleStmt= ruleStmt EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:213:2: iv_ruleStmt= ruleStmt EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStmtRule()); 
            }
            pushFollow(FOLLOW_ruleStmt_in_entryRuleStmt365);
            iv_ruleStmt=ruleStmt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStmt; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleStmt375); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStmt"


    // $ANTLR start "ruleStmt"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:220:1: ruleStmt returns [EObject current=null] : ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) (otherlv_6= ';' )? ) ;
    public final EObject ruleStmt() throws RecognitionException {
        EObject current = null;

        Token otherlv_6=null;
        EObject this_Attribute_0 = null;

        EObject this_EdgeStmtNode_1 = null;

        EObject this_EdgeStmtSubgraph_2 = null;

        EObject this_NodeStmt_3 = null;

        EObject this_AttrStmt_4 = null;

        EObject this_Subgraph_5 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:223:28: ( ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) (otherlv_6= ';' )? ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:224:1: ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) (otherlv_6= ';' )? )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:224:1: ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) (otherlv_6= ';' )? )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:224:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) (otherlv_6= ';' )?
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:224:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )
            int alt5=6;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:225:2: this_Attribute_0= ruleAttribute
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStmtAccess().getAttributeParserRuleCall_0_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAttribute_in_ruleStmt426);
                    this_Attribute_0=ruleAttribute();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Attribute_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:238:2: this_EdgeStmtNode_1= ruleEdgeStmtNode
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStmtAccess().getEdgeStmtNodeParserRuleCall_0_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeStmtNode_in_ruleStmt456);
                    this_EdgeStmtNode_1=ruleEdgeStmtNode();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_EdgeStmtNode_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:251:2: this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStmtAccess().getEdgeStmtSubgraphParserRuleCall_0_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_ruleStmt486);
                    this_EdgeStmtSubgraph_2=ruleEdgeStmtSubgraph();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_EdgeStmtSubgraph_2; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 4 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:264:2: this_NodeStmt_3= ruleNodeStmt
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStmtAccess().getNodeStmtParserRuleCall_0_3()); 
                          
                    }
                    pushFollow(FOLLOW_ruleNodeStmt_in_ruleStmt516);
                    this_NodeStmt_3=ruleNodeStmt();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_NodeStmt_3; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 5 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:277:2: this_AttrStmt_4= ruleAttrStmt
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStmtAccess().getAttrStmtParserRuleCall_0_4()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAttrStmt_in_ruleStmt546);
                    this_AttrStmt_4=ruleAttrStmt();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_AttrStmt_4; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 6 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:290:2: this_Subgraph_5= ruleSubgraph
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStmtAccess().getSubgraphParserRuleCall_0_5()); 
                          
                    }
                    pushFollow(FOLLOW_ruleSubgraph_in_ruleStmt576);
                    this_Subgraph_5=ruleSubgraph();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Subgraph_5; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:301:2: (otherlv_6= ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==15) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:301:4: otherlv_6= ';'
                    {
                    otherlv_6=(Token)match(input,15,FOLLOW_15_in_ruleStmt589); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_6, grammarAccess.getStmtAccess().getSemicolonKeyword_1());
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStmt"


    // $ANTLR start "entryRuleEdgeStmtNode"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:313:1: entryRuleEdgeStmtNode returns [EObject current=null] : iv_ruleEdgeStmtNode= ruleEdgeStmtNode EOF ;
    public final EObject entryRuleEdgeStmtNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeStmtNode = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:314:2: (iv_ruleEdgeStmtNode= ruleEdgeStmtNode EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:315:2: iv_ruleEdgeStmtNode= ruleEdgeStmtNode EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEdgeStmtNodeRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeStmtNode_in_entryRuleEdgeStmtNode627);
            iv_ruleEdgeStmtNode=ruleEdgeStmtNode();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEdgeStmtNode; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStmtNode637); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEdgeStmtNode"


    // $ANTLR start "ruleEdgeStmtNode"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:322:1: ruleEdgeStmtNode returns [EObject current=null] : ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) ;
    public final EObject ruleEdgeStmtNode() throws RecognitionException {
        EObject current = null;

        EObject lv_node_id_0_0 = null;

        EObject lv_edgeRHS_1_0 = null;

        EObject lv_attributes_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:325:28: ( ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:326:1: ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:326:1: ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:326:2: ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:326:2: ( (lv_node_id_0_0= ruleNodeId ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:327:1: (lv_node_id_0_0= ruleNodeId )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:327:1: (lv_node_id_0_0= ruleNodeId )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:328:3: lv_node_id_0_0= ruleNodeId
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEdgeStmtNodeAccess().getNode_idNodeIdParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleNodeId_in_ruleEdgeStmtNode683);
            lv_node_id_0_0=ruleNodeId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEdgeStmtNodeRule());
              	        }
                     		set(
                     			current, 
                     			"node_id",
                      		lv_node_id_0_0, 
                      		"NodeId");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:344:2: ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=21 && LA7_0<=22)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:345:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:345:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:346:3: lv_edgeRHS_1_0= ruleEdgeRhs
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtNode704);
            	    lv_edgeRHS_1_0=ruleEdgeRhs();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getEdgeStmtNodeRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"edgeRHS",
            	              		lv_edgeRHS_1_0, 
            	              		"EdgeRhs");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:362:3: ( (lv_attributes_2_0= ruleAttrList ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==17) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:363:1: (lv_attributes_2_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:363:1: (lv_attributes_2_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:364:3: lv_attributes_2_0= ruleAttrList
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getEdgeStmtNodeAccess().getAttributesAttrListParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleEdgeStmtNode726);
            	    lv_attributes_2_0=ruleAttrList();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getEdgeStmtNodeRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"attributes",
            	              		lv_attributes_2_0, 
            	              		"AttrList");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeStmtNode"


    // $ANTLR start "entryRuleEdgeStmtSubgraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:388:1: entryRuleEdgeStmtSubgraph returns [EObject current=null] : iv_ruleEdgeStmtSubgraph= ruleEdgeStmtSubgraph EOF ;
    public final EObject entryRuleEdgeStmtSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeStmtSubgraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:389:2: (iv_ruleEdgeStmtSubgraph= ruleEdgeStmtSubgraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:390:2: iv_ruleEdgeStmtSubgraph= ruleEdgeStmtSubgraph EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEdgeStmtSubgraphRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_entryRuleEdgeStmtSubgraph763);
            iv_ruleEdgeStmtSubgraph=ruleEdgeStmtSubgraph();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEdgeStmtSubgraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStmtSubgraph773); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEdgeStmtSubgraph"


    // $ANTLR start "ruleEdgeStmtSubgraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:397:1: ruleEdgeStmtSubgraph returns [EObject current=null] : ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) ;
    public final EObject ruleEdgeStmtSubgraph() throws RecognitionException {
        EObject current = null;

        EObject lv_subgraph_0_0 = null;

        EObject lv_edgeRHS_1_0 = null;

        EObject lv_attributes_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:400:28: ( ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:401:1: ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:401:1: ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:401:2: ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:401:2: ( (lv_subgraph_0_0= ruleSubgraph ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:402:1: (lv_subgraph_0_0= ruleSubgraph )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:402:1: (lv_subgraph_0_0= ruleSubgraph )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:403:3: lv_subgraph_0_0= ruleSubgraph
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEdgeStmtSubgraphAccess().getSubgraphSubgraphParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleSubgraph_in_ruleEdgeStmtSubgraph819);
            lv_subgraph_0_0=ruleSubgraph();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEdgeStmtSubgraphRule());
              	        }
                     		set(
                     			current, 
                     			"subgraph",
                      		lv_subgraph_0_0, 
                      		"Subgraph");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:419:2: ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=21 && LA9_0<=22)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:420:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:420:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:421:3: lv_edgeRHS_1_0= ruleEdgeRhs
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtSubgraph840);
            	    lv_edgeRHS_1_0=ruleEdgeRhs();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getEdgeStmtSubgraphRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"edgeRHS",
            	              		lv_edgeRHS_1_0, 
            	              		"EdgeRhs");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:437:3: ( (lv_attributes_2_0= ruleAttrList ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==17) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:438:1: (lv_attributes_2_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:438:1: (lv_attributes_2_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:439:3: lv_attributes_2_0= ruleAttrList
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getEdgeStmtSubgraphAccess().getAttributesAttrListParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleEdgeStmtSubgraph862);
            	    lv_attributes_2_0=ruleAttrList();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getEdgeStmtSubgraphRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"attributes",
            	              		lv_attributes_2_0, 
            	              		"AttrList");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeStmtSubgraph"


    // $ANTLR start "entryRuleNodeStmt"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:463:1: entryRuleNodeStmt returns [EObject current=null] : iv_ruleNodeStmt= ruleNodeStmt EOF ;
    public final EObject entryRuleNodeStmt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNodeStmt = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:464:2: (iv_ruleNodeStmt= ruleNodeStmt EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:465:2: iv_ruleNodeStmt= ruleNodeStmt EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNodeStmtRule()); 
            }
            pushFollow(FOLLOW_ruleNodeStmt_in_entryRuleNodeStmt899);
            iv_ruleNodeStmt=ruleNodeStmt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNodeStmt; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeStmt909); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNodeStmt"


    // $ANTLR start "ruleNodeStmt"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:472:1: ruleNodeStmt returns [EObject current=null] : ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* ) ;
    public final EObject ruleNodeStmt() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        EObject lv_attributes_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:475:28: ( ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:476:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:476:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:476:2: ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:476:2: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:477:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:477:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:478:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleNodeStmt951); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getNodeStmtAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getNodeStmtRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"DOT_ID");
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:494:2: ( (lv_attributes_1_0= ruleAttrList ) )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==17) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:495:1: (lv_attributes_1_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:495:1: (lv_attributes_1_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:496:3: lv_attributes_1_0= ruleAttrList
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getNodeStmtAccess().getAttributesAttrListParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleNodeStmt977);
            	    lv_attributes_1_0=ruleAttrList();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getNodeStmtRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"attributes",
            	              		lv_attributes_1_0, 
            	              		"AttrList");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNodeStmt"


    // $ANTLR start "entryRuleAttribute"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:520:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:521:2: (iv_ruleAttribute= ruleAttribute EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:522:2: iv_ruleAttribute= ruleAttribute EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttributeRule()); 
            }
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute1014);
            iv_ruleAttribute=ruleAttribute();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttribute; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute1024); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttribute"


    // $ANTLR start "ruleAttribute"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:529:1: ruleAttribute returns [EObject current=null] : ( ( (lv_name_0_0= RULE_DOT_ID ) ) otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token lv_value_2_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:532:28: ( ( ( (lv_name_0_0= RULE_DOT_ID ) ) otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:533:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:533:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:533:2: ( (lv_name_0_0= RULE_DOT_ID ) ) otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:533:2: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:534:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:534:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:535:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAttribute1066); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getAttributeAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getAttributeRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"DOT_ID");
              	    
            }

            }


            }

            otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleAttribute1083); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getAttributeAccess().getEqualsSignKeyword_1());
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:555:1: ( (lv_value_2_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:556:1: (lv_value_2_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:556:1: (lv_value_2_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:557:3: lv_value_2_0= RULE_DOT_ID
            {
            lv_value_2_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAttribute1100); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_value_2_0, grammarAccess.getAttributeAccess().getValueDOT_IDTerminalRuleCall_2_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getAttributeRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"value",
                      		lv_value_2_0, 
                      		"DOT_ID");
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttribute"


    // $ANTLR start "entryRuleAttrStmt"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:581:1: entryRuleAttrStmt returns [EObject current=null] : iv_ruleAttrStmt= ruleAttrStmt EOF ;
    public final EObject entryRuleAttrStmt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrStmt = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:582:2: (iv_ruleAttrStmt= ruleAttrStmt EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:583:2: iv_ruleAttrStmt= ruleAttrStmt EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttrStmtRule()); 
            }
            pushFollow(FOLLOW_ruleAttrStmt_in_entryRuleAttrStmt1141);
            iv_ruleAttrStmt=ruleAttrStmt();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttrStmt; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrStmt1151); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrStmt"


    // $ANTLR start "ruleAttrStmt"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:590:1: ruleAttrStmt returns [EObject current=null] : ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ ) ;
    public final EObject ruleAttrStmt() throws RecognitionException {
        EObject current = null;

        Enumerator lv_type_0_0 = null;

        EObject lv_attributes_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:593:28: ( ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:594:1: ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:594:1: ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:594:2: ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:594:2: ( (lv_type_0_0= ruleAttributeType ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:595:1: (lv_type_0_0= ruleAttributeType )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:595:1: (lv_type_0_0= ruleAttributeType )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:596:3: lv_type_0_0= ruleAttributeType
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAttrStmtAccess().getTypeAttributeTypeEnumRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAttributeType_in_ruleAttrStmt1197);
            lv_type_0_0=ruleAttributeType();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAttrStmtRule());
              	        }
                     		set(
                     			current, 
                     			"type",
                      		lv_type_0_0, 
                      		"AttributeType");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:612:2: ( (lv_attributes_1_0= ruleAttrList ) )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==17) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:613:1: (lv_attributes_1_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:613:1: (lv_attributes_1_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:614:3: lv_attributes_1_0= ruleAttrList
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAttrStmtAccess().getAttributesAttrListParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleAttrStmt1218);
            	    lv_attributes_1_0=ruleAttrList();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAttrStmtRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"attributes",
            	              		lv_attributes_1_0, 
            	              		"AttrList");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrStmt"


    // $ANTLR start "entryRuleAttrList"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:638:1: entryRuleAttrList returns [EObject current=null] : iv_ruleAttrList= ruleAttrList EOF ;
    public final EObject entryRuleAttrList() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrList = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:639:2: (iv_ruleAttrList= ruleAttrList EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:640:2: iv_ruleAttrList= ruleAttrList EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAttrListRule()); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_entryRuleAttrList1255);
            iv_ruleAttrList=ruleAttrList();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAttrList; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrList1265); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAttrList"


    // $ANTLR start "ruleAttrList"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:647:1: ruleAttrList returns [EObject current=null] : (otherlv_0= '[' ( (lv_a_list_1_0= ruleAList ) )* otherlv_2= ']' ) ;
    public final EObject ruleAttrList() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        EObject lv_a_list_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:650:28: ( (otherlv_0= '[' ( (lv_a_list_1_0= ruleAList ) )* otherlv_2= ']' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:651:1: (otherlv_0= '[' ( (lv_a_list_1_0= ruleAList ) )* otherlv_2= ']' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:651:1: (otherlv_0= '[' ( (lv_a_list_1_0= ruleAList ) )* otherlv_2= ']' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:651:3: otherlv_0= '[' ( (lv_a_list_1_0= ruleAList ) )* otherlv_2= ']'
            {
            otherlv_0=(Token)match(input,17,FOLLOW_17_in_ruleAttrList1302); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getAttrListAccess().getLeftSquareBracketKeyword_0());
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:655:1: ( (lv_a_list_1_0= ruleAList ) )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==RULE_DOT_ID) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:656:1: (lv_a_list_1_0= ruleAList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:656:1: (lv_a_list_1_0= ruleAList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:657:3: lv_a_list_1_0= ruleAList
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAttrListAccess().getA_listAListParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAList_in_ruleAttrList1323);
            	    lv_a_list_1_0=ruleAList();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAttrListRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"a_list",
            	              		lv_a_list_1_0, 
            	              		"AList");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            otherlv_2=(Token)match(input,18,FOLLOW_18_in_ruleAttrList1336); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getAttrListAccess().getRightSquareBracketKeyword_2());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttrList"


    // $ANTLR start "entryRuleAList"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:685:1: entryRuleAList returns [EObject current=null] : iv_ruleAList= ruleAList EOF ;
    public final EObject entryRuleAList() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAList = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:686:2: (iv_ruleAList= ruleAList EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:687:2: iv_ruleAList= ruleAList EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAListRule()); 
            }
            pushFollow(FOLLOW_ruleAList_in_entryRuleAList1372);
            iv_ruleAList=ruleAList();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAList; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAList1382); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAList"


    // $ANTLR start "ruleAList"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:694:1: ruleAList returns [EObject current=null] : ( ( (lv_name_0_0= RULE_DOT_ID ) ) (otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? (otherlv_3= ',' )? ) ;
    public final EObject ruleAList() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token lv_value_2_0=null;
        Token otherlv_3=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:697:28: ( ( ( (lv_name_0_0= RULE_DOT_ID ) ) (otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? (otherlv_3= ',' )? ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:698:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) (otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? (otherlv_3= ',' )? )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:698:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) (otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? (otherlv_3= ',' )? )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:698:2: ( (lv_name_0_0= RULE_DOT_ID ) ) (otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? (otherlv_3= ',' )?
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:698:2: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:699:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:699:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:700:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAList1424); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getAListAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getAListRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"DOT_ID");
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:716:2: (otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==16) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:716:4: otherlv_1= '=' ( (lv_value_2_0= RULE_DOT_ID ) )
                    {
                    otherlv_1=(Token)match(input,16,FOLLOW_16_in_ruleAList1442); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getAListAccess().getEqualsSignKeyword_1_0());
                          
                    }
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:720:1: ( (lv_value_2_0= RULE_DOT_ID ) )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:721:1: (lv_value_2_0= RULE_DOT_ID )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:721:1: (lv_value_2_0= RULE_DOT_ID )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:722:3: lv_value_2_0= RULE_DOT_ID
                    {
                    lv_value_2_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAList1459); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_value_2_0, grammarAccess.getAListAccess().getValueDOT_IDTerminalRuleCall_1_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAListRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"value",
                              		lv_value_2_0, 
                              		"DOT_ID");
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:738:4: (otherlv_3= ',' )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==19) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:738:6: otherlv_3= ','
                    {
                    otherlv_3=(Token)match(input,19,FOLLOW_19_in_ruleAList1479); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getAListAccess().getCommaKeyword_2());
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAList"


    // $ANTLR start "entryRuleSubgraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:750:1: entryRuleSubgraph returns [EObject current=null] : iv_ruleSubgraph= ruleSubgraph EOF ;
    public final EObject entryRuleSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubgraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:751:2: (iv_ruleSubgraph= ruleSubgraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:752:2: iv_ruleSubgraph= ruleSubgraph EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getSubgraphRule()); 
            }
            pushFollow(FOLLOW_ruleSubgraph_in_entryRuleSubgraph1517);
            iv_ruleSubgraph=ruleSubgraph();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleSubgraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleSubgraph1527); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleSubgraph"


    // $ANTLR start "ruleSubgraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:759:1: ruleSubgraph returns [EObject current=null] : ( (otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? otherlv_2= '{' ( (lv_stmts_3_0= ruleStmt ) )* otherlv_4= '}' ) ;
    public final EObject ruleSubgraph() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_stmts_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:762:28: ( ( (otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? otherlv_2= '{' ( (lv_stmts_3_0= ruleStmt ) )* otherlv_4= '}' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:763:1: ( (otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? otherlv_2= '{' ( (lv_stmts_3_0= ruleStmt ) )* otherlv_4= '}' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:763:1: ( (otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? otherlv_2= '{' ( (lv_stmts_3_0= ruleStmt ) )* otherlv_4= '}' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:763:2: (otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? otherlv_2= '{' ( (lv_stmts_3_0= ruleStmt ) )* otherlv_4= '}'
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:763:2: (otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==20) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:763:4: otherlv_0= 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )?
                    {
                    otherlv_0=(Token)match(input,20,FOLLOW_20_in_ruleSubgraph1565); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_0, grammarAccess.getSubgraphAccess().getSubgraphKeyword_0_0());
                          
                    }
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:767:1: ( (lv_name_1_0= RULE_DOT_ID ) )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==RULE_DOT_ID) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:768:1: (lv_name_1_0= RULE_DOT_ID )
                            {
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:768:1: (lv_name_1_0= RULE_DOT_ID )
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:769:3: lv_name_1_0= RULE_DOT_ID
                            {
                            lv_name_1_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleSubgraph1582); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              			newLeafNode(lv_name_1_0, grammarAccess.getSubgraphAccess().getNameDOT_IDTerminalRuleCall_0_1_0()); 
                              		
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getSubgraphRule());
                              	        }
                                     		setWithLastConsumed(
                                     			current, 
                                     			"name",
                                      		lv_name_1_0, 
                                      		"DOT_ID");
                              	    
                            }

                            }


                            }
                            break;

                    }


                    }
                    break;

            }

            otherlv_2=(Token)match(input,13,FOLLOW_13_in_ruleSubgraph1602); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getSubgraphAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:789:1: ( (lv_stmts_3_0= ruleStmt ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==RULE_DOT_ID||LA18_0==13||LA18_0==20||LA18_0==23||(LA18_0>=25 && LA18_0<=26)) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:790:1: (lv_stmts_3_0= ruleStmt )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:790:1: (lv_stmts_3_0= ruleStmt )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:791:3: lv_stmts_3_0= ruleStmt
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getSubgraphAccess().getStmtsStmtParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStmt_in_ruleSubgraph1623);
            	    lv_stmts_3_0=ruleStmt();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getSubgraphRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"stmts",
            	              		lv_stmts_3_0, 
            	              		"Stmt");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            otherlv_4=(Token)match(input,14,FOLLOW_14_in_ruleSubgraph1636); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_4, grammarAccess.getSubgraphAccess().getRightCurlyBracketKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleSubgraph"


    // $ANTLR start "entryRuleEdgeRhs"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:819:1: entryRuleEdgeRhs returns [EObject current=null] : iv_ruleEdgeRhs= ruleEdgeRhs EOF ;
    public final EObject entryRuleEdgeRhs() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeRhs = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:820:2: (iv_ruleEdgeRhs= ruleEdgeRhs EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:821:2: iv_ruleEdgeRhs= ruleEdgeRhs EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEdgeRhsRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhs_in_entryRuleEdgeRhs1672);
            iv_ruleEdgeRhs=ruleEdgeRhs();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEdgeRhs; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhs1682); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEdgeRhs"


    // $ANTLR start "ruleEdgeRhs"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:828:1: ruleEdgeRhs returns [EObject current=null] : (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph ) ;
    public final EObject ruleEdgeRhs() throws RecognitionException {
        EObject current = null;

        EObject this_EdgeRhsNode_0 = null;

        EObject this_EdgeRhsSubgraph_1 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:831:28: ( (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:832:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:832:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==21) ) {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==RULE_DOT_ID) ) {
                    alt19=1;
                }
                else if ( (LA19_1==13||LA19_1==20) ) {
                    alt19=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA19_0==22) ) {
                int LA19_2 = input.LA(2);

                if ( (LA19_2==RULE_DOT_ID) ) {
                    alt19=1;
                }
                else if ( (LA19_2==13||LA19_2==20) ) {
                    alt19=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:833:2: this_EdgeRhsNode_0= ruleEdgeRhsNode
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getEdgeRhsAccess().getEdgeRhsNodeParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeRhsNode_in_ruleEdgeRhs1732);
                    this_EdgeRhsNode_0=ruleEdgeRhsNode();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_EdgeRhsNode_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:846:2: this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph
                    {
                    if ( state.backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getEdgeRhsAccess().getEdgeRhsSubgraphParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeRhsSubgraph_in_ruleEdgeRhs1762);
                    this_EdgeRhsSubgraph_1=ruleEdgeRhsSubgraph();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_EdgeRhsSubgraph_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeRhs"


    // $ANTLR start "entryRuleEdgeRhsNode"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:865:1: entryRuleEdgeRhsNode returns [EObject current=null] : iv_ruleEdgeRhsNode= ruleEdgeRhsNode EOF ;
    public final EObject entryRuleEdgeRhsNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeRhsNode = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:866:2: (iv_ruleEdgeRhsNode= ruleEdgeRhsNode EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:867:2: iv_ruleEdgeRhsNode= ruleEdgeRhsNode EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEdgeRhsNodeRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhsNode_in_entryRuleEdgeRhsNode1797);
            iv_ruleEdgeRhsNode=ruleEdgeRhsNode();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEdgeRhsNode; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhsNode1807); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEdgeRhsNode"


    // $ANTLR start "ruleEdgeRhsNode"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:874:1: ruleEdgeRhsNode returns [EObject current=null] : ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) ) ;
    public final EObject ruleEdgeRhsNode() throws RecognitionException {
        EObject current = null;

        Enumerator lv_op_0_0 = null;

        EObject lv_node_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:877:28: ( ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:878:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:878:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:878:2: ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:878:2: ( (lv_op_0_0= ruleEdgeOp ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:879:1: (lv_op_0_0= ruleEdgeOp )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:879:1: (lv_op_0_0= ruleEdgeOp )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:880:3: lv_op_0_0= ruleEdgeOp
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEdgeRhsNodeAccess().getOpEdgeOpEnumRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleEdgeOp_in_ruleEdgeRhsNode1853);
            lv_op_0_0=ruleEdgeOp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEdgeRhsNodeRule());
              	        }
                     		set(
                     			current, 
                     			"op",
                      		lv_op_0_0, 
                      		"EdgeOp");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:896:2: ( (lv_node_1_0= ruleNodeId ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:897:1: (lv_node_1_0= ruleNodeId )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:897:1: (lv_node_1_0= ruleNodeId )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:898:3: lv_node_1_0= ruleNodeId
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEdgeRhsNodeAccess().getNodeNodeIdParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleNodeId_in_ruleEdgeRhsNode1874);
            lv_node_1_0=ruleNodeId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEdgeRhsNodeRule());
              	        }
                     		set(
                     			current, 
                     			"node",
                      		lv_node_1_0, 
                      		"NodeId");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeRhsNode"


    // $ANTLR start "entryRuleEdgeRhsSubgraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:922:1: entryRuleEdgeRhsSubgraph returns [EObject current=null] : iv_ruleEdgeRhsSubgraph= ruleEdgeRhsSubgraph EOF ;
    public final EObject entryRuleEdgeRhsSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeRhsSubgraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:923:2: (iv_ruleEdgeRhsSubgraph= ruleEdgeRhsSubgraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:924:2: iv_ruleEdgeRhsSubgraph= ruleEdgeRhsSubgraph EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEdgeRhsSubgraphRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhsSubgraph_in_entryRuleEdgeRhsSubgraph1910);
            iv_ruleEdgeRhsSubgraph=ruleEdgeRhsSubgraph();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEdgeRhsSubgraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhsSubgraph1920); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEdgeRhsSubgraph"


    // $ANTLR start "ruleEdgeRhsSubgraph"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:931:1: ruleEdgeRhsSubgraph returns [EObject current=null] : ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) ) ;
    public final EObject ruleEdgeRhsSubgraph() throws RecognitionException {
        EObject current = null;

        Enumerator lv_op_0_0 = null;

        EObject lv_subgraph_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:934:28: ( ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:935:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:935:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:935:2: ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:935:2: ( (lv_op_0_0= ruleEdgeOp ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:936:1: (lv_op_0_0= ruleEdgeOp )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:936:1: (lv_op_0_0= ruleEdgeOp )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:937:3: lv_op_0_0= ruleEdgeOp
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEdgeRhsSubgraphAccess().getOpEdgeOpEnumRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleEdgeOp_in_ruleEdgeRhsSubgraph1966);
            lv_op_0_0=ruleEdgeOp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEdgeRhsSubgraphRule());
              	        }
                     		set(
                     			current, 
                     			"op",
                      		lv_op_0_0, 
                      		"EdgeOp");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:953:2: ( (lv_subgraph_1_0= ruleSubgraph ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:954:1: (lv_subgraph_1_0= ruleSubgraph )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:954:1: (lv_subgraph_1_0= ruleSubgraph )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:955:3: lv_subgraph_1_0= ruleSubgraph
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEdgeRhsSubgraphAccess().getSubgraphSubgraphParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleSubgraph_in_ruleEdgeRhsSubgraph1987);
            lv_subgraph_1_0=ruleSubgraph();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEdgeRhsSubgraphRule());
              	        }
                     		set(
                     			current, 
                     			"subgraph",
                      		lv_subgraph_1_0, 
                      		"Subgraph");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeRhsSubgraph"


    // $ANTLR start "entryRuleNodeId"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:979:1: entryRuleNodeId returns [EObject current=null] : iv_ruleNodeId= ruleNodeId EOF ;
    public final EObject entryRuleNodeId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNodeId = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:980:2: (iv_ruleNodeId= ruleNodeId EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:981:2: iv_ruleNodeId= ruleNodeId EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNodeIdRule()); 
            }
            pushFollow(FOLLOW_ruleNodeId_in_entryRuleNodeId2023);
            iv_ruleNodeId=ruleNodeId();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNodeId; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeId2033); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNodeId"


    // $ANTLR start "ruleNodeId"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:988:1: ruleNodeId returns [EObject current=null] : ( (lv_name_0_0= RULE_DOT_ID ) ) ;
    public final EObject ruleNodeId() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:991:28: ( ( (lv_name_0_0= RULE_DOT_ID ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:992:1: ( (lv_name_0_0= RULE_DOT_ID ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:992:1: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:993:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:993:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:994:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleNodeId2074); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_0_0, grammarAccess.getNodeIdAccess().getNameDOT_IDTerminalRuleCall_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getNodeIdRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_0_0, 
                      		"DOT_ID");
              	    
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNodeId"


    // $ANTLR start "ruleEdgeOp"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1018:1: ruleEdgeOp returns [Enumerator current=null] : ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) ) ;
    public final Enumerator ruleEdgeOp() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1020:28: ( ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1021:1: ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1021:1: ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==21) ) {
                alt20=1;
            }
            else if ( (LA20_0==22) ) {
                alt20=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1021:2: (enumLiteral_0= '->' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1021:2: (enumLiteral_0= '->' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1021:4: enumLiteral_0= '->'
                    {
                    enumLiteral_0=(Token)match(input,21,FOLLOW_21_in_ruleEdgeOp2128); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getEdgeOpAccess().getDirectedEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_0, grammarAccess.getEdgeOpAccess().getDirectedEnumLiteralDeclaration_0()); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1027:6: (enumLiteral_1= '--' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1027:6: (enumLiteral_1= '--' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1027:8: enumLiteral_1= '--'
                    {
                    enumLiteral_1=(Token)match(input,22,FOLLOW_22_in_ruleEdgeOp2145); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getEdgeOpAccess().getUndirectedEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_1, grammarAccess.getEdgeOpAccess().getUndirectedEnumLiteralDeclaration_1()); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeOp"


    // $ANTLR start "ruleGraphType"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1037:1: ruleGraphType returns [Enumerator current=null] : ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) ) ;
    public final Enumerator ruleGraphType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1039:28: ( ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1040:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1040:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==23) ) {
                alt21=1;
            }
            else if ( (LA21_0==24) ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1040:2: (enumLiteral_0= 'graph' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1040:2: (enumLiteral_0= 'graph' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1040:4: enumLiteral_0= 'graph'
                    {
                    enumLiteral_0=(Token)match(input,23,FOLLOW_23_in_ruleGraphType2190); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_0, grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:6: (enumLiteral_1= 'digraph' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:6: (enumLiteral_1= 'digraph' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:8: enumLiteral_1= 'digraph'
                    {
                    enumLiteral_1=(Token)match(input,24,FOLLOW_24_in_ruleGraphType2207); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_1, grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1()); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGraphType"


    // $ANTLR start "ruleAttributeType"
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1056:1: ruleAttributeType returns [Enumerator current=null] : ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) ) ;
    public final Enumerator ruleAttributeType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1058:28: ( ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1059:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1059:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) )
            int alt22=3;
            switch ( input.LA(1) ) {
            case 23:
                {
                alt22=1;
                }
                break;
            case 25:
                {
                alt22=2;
                }
                break;
            case 26:
                {
                alt22=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1059:2: (enumLiteral_0= 'graph' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1059:2: (enumLiteral_0= 'graph' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1059:4: enumLiteral_0= 'graph'
                    {
                    enumLiteral_0=(Token)match(input,23,FOLLOW_23_in_ruleAttributeType2252); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_0, grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1065:6: (enumLiteral_1= 'node' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1065:6: (enumLiteral_1= 'node' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1065:8: enumLiteral_1= 'node'
                    {
                    enumLiteral_1=(Token)match(input,25,FOLLOW_25_in_ruleAttributeType2269); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_1, grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1()); 
                          
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1071:6: (enumLiteral_2= 'edge' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1071:6: (enumLiteral_2= 'edge' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1071:8: enumLiteral_2= 'edge'
                    {
                    enumLiteral_2=(Token)match(input,26,FOLLOW_26_in_ruleAttributeType2286); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current = grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                              newLeafNode(enumLiteral_2, grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2()); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAttributeType"

    // $ANTLR start synpred5_InternalDot
    public final void synpred5_InternalDot_fragment() throws RecognitionException {   
        EObject this_Attribute_0 = null;


        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:225:2: (this_Attribute_0= ruleAttribute )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:225:2: this_Attribute_0= ruleAttribute
        {
        if ( state.backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleAttribute_in_synpred5_InternalDot426);
        this_Attribute_0=ruleAttribute();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_InternalDot

    // $ANTLR start synpred6_InternalDot
    public final void synpred6_InternalDot_fragment() throws RecognitionException {   
        EObject this_EdgeStmtNode_1 = null;


        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:238:2: (this_EdgeStmtNode_1= ruleEdgeStmtNode )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:238:2: this_EdgeStmtNode_1= ruleEdgeStmtNode
        {
        if ( state.backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleEdgeStmtNode_in_synpred6_InternalDot456);
        this_EdgeStmtNode_1=ruleEdgeStmtNode();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_InternalDot

    // $ANTLR start synpred7_InternalDot
    public final void synpred7_InternalDot_fragment() throws RecognitionException {   
        EObject this_EdgeStmtSubgraph_2 = null;


        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:251:2: (this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:251:2: this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph
        {
        if ( state.backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_synpred7_InternalDot486);
        this_EdgeStmtSubgraph_2=ruleEdgeStmtSubgraph();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_InternalDot

    // $ANTLR start synpred8_InternalDot
    public final void synpred8_InternalDot_fragment() throws RecognitionException {   
        EObject this_NodeStmt_3 = null;


        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:264:2: (this_NodeStmt_3= ruleNodeStmt )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:264:2: this_NodeStmt_3= ruleNodeStmt
        {
        if ( state.backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleNodeStmt_in_synpred8_InternalDot516);
        this_NodeStmt_3=ruleNodeStmt();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_InternalDot

    // Delegated rules

    public final boolean synpred7_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_InternalDot_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_InternalDot_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_InternalDot_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_InternalDot_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\14\uffff";
    static final String DFA5_eofS =
        "\14\uffff";
    static final String DFA5_minS =
        "\1\4\3\0\10\uffff";
    static final String DFA5_maxS =
        "\1\32\3\0\10\uffff";
    static final String DFA5_acceptS =
        "\4\uffff\1\5\2\uffff\1\1\1\2\1\4\1\3\1\6";
    static final String DFA5_specialS =
        "\1\uffff\1\0\1\1\1\2\10\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\10\uffff\1\3\6\uffff\1\2\2\uffff\1\4\1\uffff\2\4",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "224:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_InternalDot()) ) {s = 7;}

                        else if ( (synpred6_InternalDot()) ) {s = 8;}

                        else if ( (synpred8_InternalDot()) ) {s = 9;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_2 = input.LA(1);

                         
                        int index5_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_InternalDot()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index5_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_3 = input.LA(1);

                         
                        int index5_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_InternalDot()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index5_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel81 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGraphvizModel91 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMainGraph_in_ruleGraphvizModel136 = new BitSet(new long[]{0x0000000001801002L});
    public static final BitSet FOLLOW_ruleMainGraph_in_entryRuleMainGraph172 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMainGraph182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleMainGraph225 = new BitSet(new long[]{0x0000000001803010L});
    public static final BitSet FOLLOW_ruleGraphType_in_ruleMainGraph260 = new BitSet(new long[]{0x0000000000002010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleMainGraph277 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleMainGraph295 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_ruleStmt_in_ruleMainGraph316 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_14_in_ruleMainGraph329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStmt_in_entryRuleStmt365 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStmt375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleStmt426 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_ruleStmt456 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_ruleStmt486 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_ruleStmt516 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleAttrStmt_in_ruleStmt546 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleStmt576 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_ruleStmt589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_entryRuleEdgeStmtNode627 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStmtNode637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_ruleEdgeStmtNode683 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtNode704 = new BitSet(new long[]{0x0000000000620002L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleEdgeStmtNode726 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_entryRuleEdgeStmtSubgraph763 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStmtSubgraph773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleEdgeStmtSubgraph819 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtSubgraph840 = new BitSet(new long[]{0x0000000000620002L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleEdgeStmtSubgraph862 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_entryRuleNodeStmt899 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeStmt909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleNodeStmt951 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleNodeStmt977 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute1014 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAttribute1066 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttribute1083 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAttribute1100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrStmt_in_entryRuleAttrStmt1141 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrStmt1151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleAttrStmt1197 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleAttrStmt1218 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleAttrList_in_entryRuleAttrList1255 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrList1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleAttrList1302 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_ruleAList_in_ruleAttrList1323 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_18_in_ruleAttrList1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAList_in_entryRuleAList1372 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAList1382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAList1424 = new BitSet(new long[]{0x0000000000090002L});
    public static final BitSet FOLLOW_16_in_ruleAList1442 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAList1459 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_ruleAList1479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_entryRuleSubgraph1517 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSubgraph1527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_ruleSubgraph1565 = new BitSet(new long[]{0x0000000000002010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleSubgraph1582 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleSubgraph1602 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_ruleStmt_in_ruleSubgraph1623 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_14_in_ruleSubgraph1636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_entryRuleEdgeRhs1672 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhs1682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsNode_in_ruleEdgeRhs1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsSubgraph_in_ruleEdgeRhs1762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsNode_in_entryRuleEdgeRhsNode1797 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhsNode1807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOp_in_ruleEdgeRhsNode1853 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNodeId_in_ruleEdgeRhsNode1874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsSubgraph_in_entryRuleEdgeRhsSubgraph1910 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhsSubgraph1920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOp_in_ruleEdgeRhsSubgraph1966 = new BitSet(new long[]{0x0000000000102000L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleEdgeRhsSubgraph1987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_entryRuleNodeId2023 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeId2033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleNodeId2074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleEdgeOp2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleEdgeOp2145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleGraphType2190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleGraphType2207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleAttributeType2252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType2269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleAttributeType2286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_synpred5_InternalDot426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_synpred6_InternalDot456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_synpred7_InternalDot486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_synpred8_InternalDot516 = new BitSet(new long[]{0x0000000000000002L});

}