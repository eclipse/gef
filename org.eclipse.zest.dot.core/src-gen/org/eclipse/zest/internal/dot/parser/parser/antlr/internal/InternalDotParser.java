package org.eclipse.zest.internal.dot.parser.parser.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.xtext.conversion.ValueConverterException;
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
    public static final int RULE_STRING=7;
    public static final int RULE_DOT_ID=4;
    public static final int RULE_ANY_OTHER=11;
    public static final int RULE_INT=6;
    public static final int RULE_WS=10;
    public static final int RULE_SL_COMMENT=9;
    public static final int EOF=-1;
    public static final int RULE_ML_COMMENT=8;

        public InternalDotParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[60+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g"; }



    /*
      This grammar contains a lot of empty actions to work around a bug in ANTLR.
      Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
    */
     
     	private DotGrammarAccess grammarAccess;
     	
        public InternalDotParser(TokenStream input, IAstFactory factory, DotGrammarAccess grammarAccess) {
            this(input);
            this.factory = factory;
            registerRules(grammarAccess.getGrammar());
            this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected InputStream getTokenFile() {
        	ClassLoader classLoader = getClass().getClassLoader();
        	return classLoader.getResourceAsStream("org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.tokens");
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "GraphvizModel";	
       	}
       	
       	@Override
       	protected DotGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start entryRuleGraphvizModel
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:84:1: entryRuleGraphvizModel returns [EObject current=null] : iv_ruleGraphvizModel= ruleGraphvizModel EOF ;
    public final EObject entryRuleGraphvizModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGraphvizModel = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:85:2: (iv_ruleGraphvizModel= ruleGraphvizModel EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:86:2: iv_ruleGraphvizModel= ruleGraphvizModel EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getGraphvizModelRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel81);
            iv_ruleGraphvizModel=ruleGraphvizModel();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleGraphvizModel; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGraphvizModel91); if (failed) return current;

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
    // $ANTLR end entryRuleGraphvizModel


    // $ANTLR start ruleGraphvizModel
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:93:1: ruleGraphvizModel returns [EObject current=null] : ( (lv_graphs_0_0= ruleMainGraph ) )* ;
    public final EObject ruleGraphvizModel() throws RecognitionException {
        EObject current = null;

        EObject lv_graphs_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:98:6: ( ( (lv_graphs_0_0= ruleMainGraph ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:99:1: ( (lv_graphs_0_0= ruleMainGraph ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:99:1: ( (lv_graphs_0_0= ruleMainGraph ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12||(LA1_0>=23 && LA1_0<=24)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:100:1: (lv_graphs_0_0= ruleMainGraph )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:100:1: (lv_graphs_0_0= ruleMainGraph )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:101:3: lv_graphs_0_0= ruleMainGraph
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getGraphvizModelAccess().getGraphsMainGraphParserRuleCall_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleMainGraph_in_ruleGraphvizModel136);
            	    lv_graphs_0_0=ruleMainGraph();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getGraphvizModelRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"graphs",
            	      	        		lv_graphs_0_0, 
            	      	        		"MainGraph", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleGraphvizModel


    // $ANTLR start entryRuleMainGraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:131:1: entryRuleMainGraph returns [EObject current=null] : iv_ruleMainGraph= ruleMainGraph EOF ;
    public final EObject entryRuleMainGraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMainGraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:132:2: (iv_ruleMainGraph= ruleMainGraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:133:2: iv_ruleMainGraph= ruleMainGraph EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getMainGraphRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleMainGraph_in_entryRuleMainGraph172);
            iv_ruleMainGraph=ruleMainGraph();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleMainGraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMainGraph182); if (failed) return current;

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
    // $ANTLR end entryRuleMainGraph


    // $ANTLR start ruleMainGraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:140:1: ruleMainGraph returns [EObject current=null] : ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? '{' ( (lv_stmts_4_0= ruleStmt ) )* '}' ) ;
    public final EObject ruleMainGraph() throws RecognitionException {
        EObject current = null;

        Token lv_strict_0_0=null;
        Token lv_name_2_0=null;
        Enumerator lv_type_1_0 = null;

        EObject lv_stmts_4_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:145:6: ( ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? '{' ( (lv_stmts_4_0= ruleStmt ) )* '}' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:146:1: ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? '{' ( (lv_stmts_4_0= ruleStmt ) )* '}' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:146:1: ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? '{' ( (lv_stmts_4_0= ruleStmt ) )* '}' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:146:2: ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= RULE_DOT_ID ) )? '{' ( (lv_stmts_4_0= ruleStmt ) )* '}'
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:146:2: ( (lv_strict_0_0= 'strict' ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==12) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:147:1: (lv_strict_0_0= 'strict' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:147:1: (lv_strict_0_0= 'strict' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:148:3: lv_strict_0_0= 'strict'
                    {
                    lv_strict_0_0=(Token)input.LT(1);
                    match(input,12,FOLLOW_12_in_ruleMainGraph225); if (failed) return current;
                    if ( backtracking==0 ) {

                              createLeafNode(grammarAccess.getMainGraphAccess().getStrictStrictKeyword_0_0(), "strict"); 
                          
                    }
                    if ( backtracking==0 ) {

                      	        if (current==null) {
                      	            current = factory.create(grammarAccess.getMainGraphRule().getType().getClassifier());
                      	            associateNodeWithAstElement(currentNode, current);
                      	        }
                      	        
                      	        try {
                      	       		set(current, "strict", true, "strict", lastConsumedNode);
                      	        } catch (ValueConverterException vce) {
                      				handleValueConverterException(vce);
                      	        }
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:167:3: ( (lv_type_1_0= ruleGraphType ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:168:1: (lv_type_1_0= ruleGraphType )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:168:1: (lv_type_1_0= ruleGraphType )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:169:3: lv_type_1_0= ruleGraphType
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getMainGraphAccess().getTypeGraphTypeEnumRuleCall_1_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleGraphType_in_ruleMainGraph260);
            lv_type_1_0=ruleGraphType();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getMainGraphRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"type",
              	        		lv_type_1_0, 
              	        		"GraphType", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:191:2: ( (lv_name_2_0= RULE_DOT_ID ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_DOT_ID) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:192:1: (lv_name_2_0= RULE_DOT_ID )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:192:1: (lv_name_2_0= RULE_DOT_ID )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:193:3: lv_name_2_0= RULE_DOT_ID
                    {
                    lv_name_2_0=(Token)input.LT(1);
                    match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleMainGraph277); if (failed) return current;
                    if ( backtracking==0 ) {

                      			createLeafNode(grammarAccess.getMainGraphAccess().getNameDOT_IDTerminalRuleCall_2_0(), "name"); 
                      		
                    }
                    if ( backtracking==0 ) {

                      	        if (current==null) {
                      	            current = factory.create(grammarAccess.getMainGraphRule().getType().getClassifier());
                      	            associateNodeWithAstElement(currentNode, current);
                      	        }
                      	        try {
                      	       		set(
                      	       			current, 
                      	       			"name",
                      	        		lv_name_2_0, 
                      	        		"DOT_ID", 
                      	        		lastConsumedNode);
                      	        } catch (ValueConverterException vce) {
                      				handleValueConverterException(vce);
                      	        }
                      	    
                    }

                    }


                    }
                    break;

            }

            match(input,13,FOLLOW_13_in_ruleMainGraph293); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getMainGraphAccess().getLeftCurlyBracketKeyword_3(), null); 
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:219:1: ( (lv_stmts_4_0= ruleStmt ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE_DOT_ID||LA4_0==13||LA4_0==20||LA4_0==23||(LA4_0>=25 && LA4_0<=26)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:220:1: (lv_stmts_4_0= ruleStmt )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:220:1: (lv_stmts_4_0= ruleStmt )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:221:3: lv_stmts_4_0= ruleStmt
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getMainGraphAccess().getStmtsStmtParserRuleCall_4_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStmt_in_ruleMainGraph314);
            	    lv_stmts_4_0=ruleStmt();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getMainGraphRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"stmts",
            	      	        		lv_stmts_4_0, 
            	      	        		"Stmt", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match(input,14,FOLLOW_14_in_ruleMainGraph325); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getMainGraphAccess().getRightCurlyBracketKeyword_5(), null); 
                  
            }

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleMainGraph


    // $ANTLR start entryRuleStmt
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:255:1: entryRuleStmt returns [EObject current=null] : iv_ruleStmt= ruleStmt EOF ;
    public final EObject entryRuleStmt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStmt = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:256:2: (iv_ruleStmt= ruleStmt EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:257:2: iv_ruleStmt= ruleStmt EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getStmtRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleStmt_in_entryRuleStmt361);
            iv_ruleStmt=ruleStmt();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleStmt; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleStmt371); if (failed) return current;

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
    // $ANTLR end entryRuleStmt


    // $ANTLR start ruleStmt
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:264:1: ruleStmt returns [EObject current=null] : ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) ( ';' )? ) ;
    public final EObject ruleStmt() throws RecognitionException {
        EObject current = null;

        EObject this_Attribute_0 = null;

        EObject this_EdgeStmtNode_1 = null;

        EObject this_EdgeStmtSubgraph_2 = null;

        EObject this_NodeStmt_3 = null;

        EObject this_AttrStmt_4 = null;

        EObject this_Subgraph_5 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:269:6: ( ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) ( ';' )? ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:270:1: ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) ( ';' )? )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:270:1: ( (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) ( ';' )? )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:270:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph ) ( ';' )?
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:270:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )
            int alt5=6;
            switch ( input.LA(1) ) {
            case RULE_DOT_ID:
                {
                int LA5_1 = input.LA(2);

                if ( (synpred5()) ) {
                    alt5=1;
                }
                else if ( (synpred6()) ) {
                    alt5=2;
                }
                else if ( (synpred8()) ) {
                    alt5=4;
                }
                else {
                    if (backtracking>0) {failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("270:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )", 5, 1, input);

                    throw nvae;
                }
                }
                break;
            case 20:
                {
                int LA5_2 = input.LA(2);

                if ( (synpred7()) ) {
                    alt5=3;
                }
                else if ( (true) ) {
                    alt5=6;
                }
                else {
                    if (backtracking>0) {failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("270:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )", 5, 2, input);

                    throw nvae;
                }
                }
                break;
            case 13:
                {
                int LA5_3 = input.LA(2);

                if ( (synpred7()) ) {
                    alt5=3;
                }
                else if ( (true) ) {
                    alt5=6;
                }
                else {
                    if (backtracking>0) {failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("270:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )", 5, 3, input);

                    throw nvae;
                }
                }
                break;
            case 23:
            case 25:
            case 26:
                {
                alt5=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("270:2: (this_Attribute_0= ruleAttribute | this_EdgeStmtNode_1= ruleEdgeStmtNode | this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph | this_NodeStmt_3= ruleNodeStmt | this_AttrStmt_4= ruleAttrStmt | this_Subgraph_5= ruleSubgraph )", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:271:2: this_Attribute_0= ruleAttribute
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getStmtAccess().getAttributeParserRuleCall_0_0(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleAttribute_in_ruleStmt422);
                    this_Attribute_0=ruleAttribute();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_Attribute_0; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:284:2: this_EdgeStmtNode_1= ruleEdgeStmtNode
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getStmtAccess().getEdgeStmtNodeParserRuleCall_0_1(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeStmtNode_in_ruleStmt452);
                    this_EdgeStmtNode_1=ruleEdgeStmtNode();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_EdgeStmtNode_1; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:297:2: this_EdgeStmtSubgraph_2= ruleEdgeStmtSubgraph
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getStmtAccess().getEdgeStmtSubgraphParserRuleCall_0_2(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_ruleStmt482);
                    this_EdgeStmtSubgraph_2=ruleEdgeStmtSubgraph();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_EdgeStmtSubgraph_2; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;
                case 4 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:310:2: this_NodeStmt_3= ruleNodeStmt
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getStmtAccess().getNodeStmtParserRuleCall_0_3(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleNodeStmt_in_ruleStmt512);
                    this_NodeStmt_3=ruleNodeStmt();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_NodeStmt_3; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;
                case 5 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:323:2: this_AttrStmt_4= ruleAttrStmt
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getStmtAccess().getAttrStmtParserRuleCall_0_4(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleAttrStmt_in_ruleStmt542);
                    this_AttrStmt_4=ruleAttrStmt();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_AttrStmt_4; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;
                case 6 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:336:2: this_Subgraph_5= ruleSubgraph
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getStmtAccess().getSubgraphParserRuleCall_0_5(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleSubgraph_in_ruleStmt572);
                    this_Subgraph_5=ruleSubgraph();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_Subgraph_5; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;

            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:347:2: ( ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==15) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:347:4: ';'
                    {
                    match(input,15,FOLLOW_15_in_ruleStmt583); if (failed) return current;
                    if ( backtracking==0 ) {

                              createLeafNode(grammarAccess.getStmtAccess().getSemicolonKeyword_1(), null); 
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleStmt


    // $ANTLR start entryRuleEdgeStmtNode
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:359:1: entryRuleEdgeStmtNode returns [EObject current=null] : iv_ruleEdgeStmtNode= ruleEdgeStmtNode EOF ;
    public final EObject entryRuleEdgeStmtNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeStmtNode = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:360:2: (iv_ruleEdgeStmtNode= ruleEdgeStmtNode EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:361:2: iv_ruleEdgeStmtNode= ruleEdgeStmtNode EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getEdgeStmtNodeRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleEdgeStmtNode_in_entryRuleEdgeStmtNode621);
            iv_ruleEdgeStmtNode=ruleEdgeStmtNode();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleEdgeStmtNode; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStmtNode631); if (failed) return current;

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
    // $ANTLR end entryRuleEdgeStmtNode


    // $ANTLR start ruleEdgeStmtNode
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:368:1: ruleEdgeStmtNode returns [EObject current=null] : ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) ;
    public final EObject ruleEdgeStmtNode() throws RecognitionException {
        EObject current = null;

        EObject lv_node_id_0_0 = null;

        EObject lv_edgeRHS_1_0 = null;

        EObject lv_attributes_2_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:373:6: ( ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:374:1: ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:374:1: ( ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:374:2: ( (lv_node_id_0_0= ruleNodeId ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:374:2: ( (lv_node_id_0_0= ruleNodeId ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:375:1: (lv_node_id_0_0= ruleNodeId )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:375:1: (lv_node_id_0_0= ruleNodeId )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:376:3: lv_node_id_0_0= ruleNodeId
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getEdgeStmtNodeAccess().getNode_idNodeIdParserRuleCall_0_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleNodeId_in_ruleEdgeStmtNode677);
            lv_node_id_0_0=ruleNodeId();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getEdgeStmtNodeRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"node_id",
              	        		lv_node_id_0_0, 
              	        		"NodeId", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:398:2: ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+
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
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:399:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:399:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:400:3: lv_edgeRHS_1_0= ruleEdgeRhs
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtNode698);
            	    lv_edgeRHS_1_0=ruleEdgeRhs();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getEdgeStmtNodeRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"edgeRHS",
            	      	        		lv_edgeRHS_1_0, 
            	      	        		"EdgeRhs", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (backtracking>0) {failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:422:3: ( (lv_attributes_2_0= ruleAttrList ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==17) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:423:1: (lv_attributes_2_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:423:1: (lv_attributes_2_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:424:3: lv_attributes_2_0= ruleAttrList
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getEdgeStmtNodeAccess().getAttributesAttrListParserRuleCall_2_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleEdgeStmtNode720);
            	    lv_attributes_2_0=ruleAttrList();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getEdgeStmtNodeRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"attributes",
            	      	        		lv_attributes_2_0, 
            	      	        		"AttrList", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
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

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleEdgeStmtNode


    // $ANTLR start entryRuleEdgeStmtSubgraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:454:1: entryRuleEdgeStmtSubgraph returns [EObject current=null] : iv_ruleEdgeStmtSubgraph= ruleEdgeStmtSubgraph EOF ;
    public final EObject entryRuleEdgeStmtSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeStmtSubgraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:455:2: (iv_ruleEdgeStmtSubgraph= ruleEdgeStmtSubgraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:456:2: iv_ruleEdgeStmtSubgraph= ruleEdgeStmtSubgraph EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getEdgeStmtSubgraphRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_entryRuleEdgeStmtSubgraph757);
            iv_ruleEdgeStmtSubgraph=ruleEdgeStmtSubgraph();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleEdgeStmtSubgraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStmtSubgraph767); if (failed) return current;

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
    // $ANTLR end entryRuleEdgeStmtSubgraph


    // $ANTLR start ruleEdgeStmtSubgraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:463:1: ruleEdgeStmtSubgraph returns [EObject current=null] : ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) ;
    public final EObject ruleEdgeStmtSubgraph() throws RecognitionException {
        EObject current = null;

        EObject lv_subgraph_0_0 = null;

        EObject lv_edgeRHS_1_0 = null;

        EObject lv_attributes_2_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:468:6: ( ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:469:1: ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:469:1: ( ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:469:2: ( (lv_subgraph_0_0= ruleSubgraph ) ) ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+ ( (lv_attributes_2_0= ruleAttrList ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:469:2: ( (lv_subgraph_0_0= ruleSubgraph ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:470:1: (lv_subgraph_0_0= ruleSubgraph )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:470:1: (lv_subgraph_0_0= ruleSubgraph )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:471:3: lv_subgraph_0_0= ruleSubgraph
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getEdgeStmtSubgraphAccess().getSubgraphSubgraphParserRuleCall_0_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleSubgraph_in_ruleEdgeStmtSubgraph813);
            lv_subgraph_0_0=ruleSubgraph();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getEdgeStmtSubgraphRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"subgraph",
              	        		lv_subgraph_0_0, 
              	        		"Subgraph", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:493:2: ( (lv_edgeRHS_1_0= ruleEdgeRhs ) )+
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
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:494:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:494:1: (lv_edgeRHS_1_0= ruleEdgeRhs )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:495:3: lv_edgeRHS_1_0= ruleEdgeRhs
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtSubgraph834);
            	    lv_edgeRHS_1_0=ruleEdgeRhs();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getEdgeStmtSubgraphRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		set(
            	      	       			current, 
            	      	       			"edgeRHS",
            	      	        		lv_edgeRHS_1_0, 
            	      	        		"EdgeRhs", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (backtracking>0) {failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:517:3: ( (lv_attributes_2_0= ruleAttrList ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==17) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:518:1: (lv_attributes_2_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:518:1: (lv_attributes_2_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:519:3: lv_attributes_2_0= ruleAttrList
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getEdgeStmtSubgraphAccess().getAttributesAttrListParserRuleCall_2_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleEdgeStmtSubgraph856);
            	    lv_attributes_2_0=ruleAttrList();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getEdgeStmtSubgraphRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"attributes",
            	      	        		lv_attributes_2_0, 
            	      	        		"AttrList", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
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

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleEdgeStmtSubgraph


    // $ANTLR start entryRuleNodeStmt
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:549:1: entryRuleNodeStmt returns [EObject current=null] : iv_ruleNodeStmt= ruleNodeStmt EOF ;
    public final EObject entryRuleNodeStmt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNodeStmt = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:550:2: (iv_ruleNodeStmt= ruleNodeStmt EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:551:2: iv_ruleNodeStmt= ruleNodeStmt EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getNodeStmtRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleNodeStmt_in_entryRuleNodeStmt893);
            iv_ruleNodeStmt=ruleNodeStmt();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleNodeStmt; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeStmt903); if (failed) return current;

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
    // $ANTLR end entryRuleNodeStmt


    // $ANTLR start ruleNodeStmt
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:558:1: ruleNodeStmt returns [EObject current=null] : ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* ) ;
    public final EObject ruleNodeStmt() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        EObject lv_attributes_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:563:6: ( ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:564:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:564:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )* )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:564:2: ( (lv_name_0_0= RULE_DOT_ID ) ) ( (lv_attributes_1_0= ruleAttrList ) )*
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:564:2: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:565:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:565:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:566:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)input.LT(1);
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleNodeStmt945); if (failed) return current;
            if ( backtracking==0 ) {

              			createLeafNode(grammarAccess.getNodeStmtAccess().getNameDOT_IDTerminalRuleCall_0_0(), "name"); 
              		
            }
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getNodeStmtRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode, current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"name",
              	        		lv_name_0_0, 
              	        		"DOT_ID", 
              	        		lastConsumedNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:588:2: ( (lv_attributes_1_0= ruleAttrList ) )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==17) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:589:1: (lv_attributes_1_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:589:1: (lv_attributes_1_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:590:3: lv_attributes_1_0= ruleAttrList
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getNodeStmtAccess().getAttributesAttrListParserRuleCall_1_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleNodeStmt971);
            	    lv_attributes_1_0=ruleAttrList();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getNodeStmtRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"attributes",
            	      	        		lv_attributes_1_0, 
            	      	        		"AttrList", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
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

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleNodeStmt


    // $ANTLR start entryRuleAttribute
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:620:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:621:2: (iv_ruleAttribute= ruleAttribute EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:622:2: iv_ruleAttribute= ruleAttribute EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getAttributeRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute1008);
            iv_ruleAttribute=ruleAttribute();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleAttribute; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute1018); if (failed) return current;

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
    // $ANTLR end entryRuleAttribute


    // $ANTLR start ruleAttribute
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:629:1: ruleAttribute returns [EObject current=null] : ( ( (lv_name_0_0= RULE_DOT_ID ) ) '=' ( (lv_value_2_0= RULE_DOT_ID ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token lv_value_2_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:634:6: ( ( ( (lv_name_0_0= RULE_DOT_ID ) ) '=' ( (lv_value_2_0= RULE_DOT_ID ) ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:635:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:635:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:635:2: ( (lv_name_0_0= RULE_DOT_ID ) ) '=' ( (lv_value_2_0= RULE_DOT_ID ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:635:2: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:636:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:636:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:637:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)input.LT(1);
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAttribute1060); if (failed) return current;
            if ( backtracking==0 ) {

              			createLeafNode(grammarAccess.getAttributeAccess().getNameDOT_IDTerminalRuleCall_0_0(), "name"); 
              		
            }
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getAttributeRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode, current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"name",
              	        		lv_name_0_0, 
              	        		"DOT_ID", 
              	        		lastConsumedNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	    
            }

            }


            }

            match(input,16,FOLLOW_16_in_ruleAttribute1075); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getAttributeAccess().getEqualsSignKeyword_1(), null); 
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:663:1: ( (lv_value_2_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:664:1: (lv_value_2_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:664:1: (lv_value_2_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:665:3: lv_value_2_0= RULE_DOT_ID
            {
            lv_value_2_0=(Token)input.LT(1);
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAttribute1092); if (failed) return current;
            if ( backtracking==0 ) {

              			createLeafNode(grammarAccess.getAttributeAccess().getValueDOT_IDTerminalRuleCall_2_0(), "value"); 
              		
            }
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getAttributeRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode, current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"value",
              	        		lv_value_2_0, 
              	        		"DOT_ID", 
              	        		lastConsumedNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	    
            }

            }


            }


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleAttribute


    // $ANTLR start entryRuleAttrStmt
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:695:1: entryRuleAttrStmt returns [EObject current=null] : iv_ruleAttrStmt= ruleAttrStmt EOF ;
    public final EObject entryRuleAttrStmt() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrStmt = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:696:2: (iv_ruleAttrStmt= ruleAttrStmt EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:697:2: iv_ruleAttrStmt= ruleAttrStmt EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getAttrStmtRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleAttrStmt_in_entryRuleAttrStmt1133);
            iv_ruleAttrStmt=ruleAttrStmt();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleAttrStmt; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrStmt1143); if (failed) return current;

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
    // $ANTLR end entryRuleAttrStmt


    // $ANTLR start ruleAttrStmt
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:704:1: ruleAttrStmt returns [EObject current=null] : ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ ) ;
    public final EObject ruleAttrStmt() throws RecognitionException {
        EObject current = null;

        Enumerator lv_type_0_0 = null;

        EObject lv_attributes_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:709:6: ( ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:710:1: ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:710:1: ( ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+ )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:710:2: ( (lv_type_0_0= ruleAttributeType ) ) ( (lv_attributes_1_0= ruleAttrList ) )+
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:710:2: ( (lv_type_0_0= ruleAttributeType ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:711:1: (lv_type_0_0= ruleAttributeType )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:711:1: (lv_type_0_0= ruleAttributeType )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:712:3: lv_type_0_0= ruleAttributeType
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getAttrStmtAccess().getTypeAttributeTypeEnumRuleCall_0_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleAttributeType_in_ruleAttrStmt1189);
            lv_type_0_0=ruleAttributeType();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getAttrStmtRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"type",
              	        		lv_type_0_0, 
              	        		"AttributeType", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:734:2: ( (lv_attributes_1_0= ruleAttrList ) )+
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
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:735:1: (lv_attributes_1_0= ruleAttrList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:735:1: (lv_attributes_1_0= ruleAttrList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:736:3: lv_attributes_1_0= ruleAttrList
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getAttrStmtAccess().getAttributesAttrListParserRuleCall_1_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAttrList_in_ruleAttrStmt1210);
            	    lv_attributes_1_0=ruleAttrList();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getAttrStmtRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"attributes",
            	      	        		lv_attributes_1_0, 
            	      	        		"AttrList", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (backtracking>0) {failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleAttrStmt


    // $ANTLR start entryRuleAttrList
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:766:1: entryRuleAttrList returns [EObject current=null] : iv_ruleAttrList= ruleAttrList EOF ;
    public final EObject entryRuleAttrList() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttrList = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:767:2: (iv_ruleAttrList= ruleAttrList EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:768:2: iv_ruleAttrList= ruleAttrList EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getAttrListRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_entryRuleAttrList1247);
            iv_ruleAttrList=ruleAttrList();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleAttrList; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrList1257); if (failed) return current;

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
    // $ANTLR end entryRuleAttrList


    // $ANTLR start ruleAttrList
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:775:1: ruleAttrList returns [EObject current=null] : ( '[' ( (lv_a_list_1_0= ruleAList ) )* ']' ) ;
    public final EObject ruleAttrList() throws RecognitionException {
        EObject current = null;

        EObject lv_a_list_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:780:6: ( ( '[' ( (lv_a_list_1_0= ruleAList ) )* ']' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:781:1: ( '[' ( (lv_a_list_1_0= ruleAList ) )* ']' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:781:1: ( '[' ( (lv_a_list_1_0= ruleAList ) )* ']' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:781:3: '[' ( (lv_a_list_1_0= ruleAList ) )* ']'
            {
            match(input,17,FOLLOW_17_in_ruleAttrList1292); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getAttrListAccess().getLeftSquareBracketKeyword_0(), null); 
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:785:1: ( (lv_a_list_1_0= ruleAList ) )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==RULE_DOT_ID) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:786:1: (lv_a_list_1_0= ruleAList )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:786:1: (lv_a_list_1_0= ruleAList )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:787:3: lv_a_list_1_0= ruleAList
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getAttrListAccess().getA_listAListParserRuleCall_1_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAList_in_ruleAttrList1313);
            	    lv_a_list_1_0=ruleAList();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getAttrListRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"a_list",
            	      	        		lv_a_list_1_0, 
            	      	        		"AList", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,18,FOLLOW_18_in_ruleAttrList1324); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getAttrListAccess().getRightSquareBracketKeyword_2(), null); 
                  
            }

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleAttrList


    // $ANTLR start entryRuleAList
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:821:1: entryRuleAList returns [EObject current=null] : iv_ruleAList= ruleAList EOF ;
    public final EObject entryRuleAList() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAList = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:822:2: (iv_ruleAList= ruleAList EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:823:2: iv_ruleAList= ruleAList EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getAListRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleAList_in_entryRuleAList1360);
            iv_ruleAList=ruleAList();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleAList; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAList1370); if (failed) return current;

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
    // $ANTLR end entryRuleAList


    // $ANTLR start ruleAList
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:830:1: ruleAList returns [EObject current=null] : ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? ( ',' )? ) ;
    public final EObject ruleAList() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token lv_value_2_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:835:6: ( ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? ( ',' )? ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:836:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? ( ',' )? )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:836:1: ( ( (lv_name_0_0= RULE_DOT_ID ) ) ( '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? ( ',' )? )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:836:2: ( (lv_name_0_0= RULE_DOT_ID ) ) ( '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )? ( ',' )?
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:836:2: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:837:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:837:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:838:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)input.LT(1);
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAList1412); if (failed) return current;
            if ( backtracking==0 ) {

              			createLeafNode(grammarAccess.getAListAccess().getNameDOT_IDTerminalRuleCall_0_0(), "name"); 
              		
            }
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getAListRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode, current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"name",
              	        		lv_name_0_0, 
              	        		"DOT_ID", 
              	        		lastConsumedNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:860:2: ( '=' ( (lv_value_2_0= RULE_DOT_ID ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==16) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:860:4: '=' ( (lv_value_2_0= RULE_DOT_ID ) )
                    {
                    match(input,16,FOLLOW_16_in_ruleAList1428); if (failed) return current;
                    if ( backtracking==0 ) {

                              createLeafNode(grammarAccess.getAListAccess().getEqualsSignKeyword_1_0(), null); 
                          
                    }
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:864:1: ( (lv_value_2_0= RULE_DOT_ID ) )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:865:1: (lv_value_2_0= RULE_DOT_ID )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:865:1: (lv_value_2_0= RULE_DOT_ID )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:866:3: lv_value_2_0= RULE_DOT_ID
                    {
                    lv_value_2_0=(Token)input.LT(1);
                    match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleAList1445); if (failed) return current;
                    if ( backtracking==0 ) {

                      			createLeafNode(grammarAccess.getAListAccess().getValueDOT_IDTerminalRuleCall_1_1_0(), "value"); 
                      		
                    }
                    if ( backtracking==0 ) {

                      	        if (current==null) {
                      	            current = factory.create(grammarAccess.getAListRule().getType().getClassifier());
                      	            associateNodeWithAstElement(currentNode, current);
                      	        }
                      	        try {
                      	       		set(
                      	       			current, 
                      	       			"value",
                      	        		lv_value_2_0, 
                      	        		"DOT_ID", 
                      	        		lastConsumedNode);
                      	        } catch (ValueConverterException vce) {
                      				handleValueConverterException(vce);
                      	        }
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:888:4: ( ',' )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==19) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:888:6: ','
                    {
                    match(input,19,FOLLOW_19_in_ruleAList1463); if (failed) return current;
                    if ( backtracking==0 ) {

                              createLeafNode(grammarAccess.getAListAccess().getCommaKeyword_2(), null); 
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleAList


    // $ANTLR start entryRuleSubgraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:900:1: entryRuleSubgraph returns [EObject current=null] : iv_ruleSubgraph= ruleSubgraph EOF ;
    public final EObject entryRuleSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubgraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:901:2: (iv_ruleSubgraph= ruleSubgraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:902:2: iv_ruleSubgraph= ruleSubgraph EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getSubgraphRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleSubgraph_in_entryRuleSubgraph1501);
            iv_ruleSubgraph=ruleSubgraph();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleSubgraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleSubgraph1511); if (failed) return current;

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
    // $ANTLR end entryRuleSubgraph


    // $ANTLR start ruleSubgraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:909:1: ruleSubgraph returns [EObject current=null] : ( ( 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? '{' ( (lv_stmts_3_0= ruleStmt ) )* '}' ) ;
    public final EObject ruleSubgraph() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;
        EObject lv_stmts_3_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:914:6: ( ( ( 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? '{' ( (lv_stmts_3_0= ruleStmt ) )* '}' ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:915:1: ( ( 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? '{' ( (lv_stmts_3_0= ruleStmt ) )* '}' )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:915:1: ( ( 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? '{' ( (lv_stmts_3_0= ruleStmt ) )* '}' )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:915:2: ( 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )? '{' ( (lv_stmts_3_0= ruleStmt ) )* '}'
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:915:2: ( 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )? )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==20) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:915:4: 'subgraph' ( (lv_name_1_0= RULE_DOT_ID ) )?
                    {
                    match(input,20,FOLLOW_20_in_ruleSubgraph1547); if (failed) return current;
                    if ( backtracking==0 ) {

                              createLeafNode(grammarAccess.getSubgraphAccess().getSubgraphKeyword_0_0(), null); 
                          
                    }
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:919:1: ( (lv_name_1_0= RULE_DOT_ID ) )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==RULE_DOT_ID) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:920:1: (lv_name_1_0= RULE_DOT_ID )
                            {
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:920:1: (lv_name_1_0= RULE_DOT_ID )
                            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:921:3: lv_name_1_0= RULE_DOT_ID
                            {
                            lv_name_1_0=(Token)input.LT(1);
                            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleSubgraph1564); if (failed) return current;
                            if ( backtracking==0 ) {

                              			createLeafNode(grammarAccess.getSubgraphAccess().getNameDOT_IDTerminalRuleCall_0_1_0(), "name"); 
                              		
                            }
                            if ( backtracking==0 ) {

                              	        if (current==null) {
                              	            current = factory.create(grammarAccess.getSubgraphRule().getType().getClassifier());
                              	            associateNodeWithAstElement(currentNode, current);
                              	        }
                              	        try {
                              	       		set(
                              	       			current, 
                              	       			"name",
                              	        		lv_name_1_0, 
                              	        		"DOT_ID", 
                              	        		lastConsumedNode);
                              	        } catch (ValueConverterException vce) {
                              				handleValueConverterException(vce);
                              	        }
                              	    
                            }

                            }


                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,13,FOLLOW_13_in_ruleSubgraph1582); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getSubgraphAccess().getLeftCurlyBracketKeyword_1(), null); 
                  
            }
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:947:1: ( (lv_stmts_3_0= ruleStmt ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==RULE_DOT_ID||LA18_0==13||LA18_0==20||LA18_0==23||(LA18_0>=25 && LA18_0<=26)) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:948:1: (lv_stmts_3_0= ruleStmt )
            	    {
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:948:1: (lv_stmts_3_0= ruleStmt )
            	    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:949:3: lv_stmts_3_0= ruleStmt
            	    {
            	    if ( backtracking==0 ) {
            	       
            	      	        currentNode=createCompositeNode(grammarAccess.getSubgraphAccess().getStmtsStmtParserRuleCall_2_0(), currentNode); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStmt_in_ruleSubgraph1603);
            	    lv_stmts_3_0=ruleStmt();
            	    _fsp--;
            	    if (failed) return current;
            	    if ( backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = factory.create(grammarAccess.getSubgraphRule().getType().getClassifier());
            	      	            associateNodeWithAstElement(currentNode.getParent(), current);
            	      	        }
            	      	        try {
            	      	       		add(
            	      	       			current, 
            	      	       			"stmts",
            	      	        		lv_stmts_3_0, 
            	      	        		"Stmt", 
            	      	        		currentNode);
            	      	        } catch (ValueConverterException vce) {
            	      				handleValueConverterException(vce);
            	      	        }
            	      	        currentNode = currentNode.getParent();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            match(input,14,FOLLOW_14_in_ruleSubgraph1614); if (failed) return current;
            if ( backtracking==0 ) {

                      createLeafNode(grammarAccess.getSubgraphAccess().getRightCurlyBracketKeyword_3(), null); 
                  
            }

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleSubgraph


    // $ANTLR start entryRuleEdgeRhs
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:983:1: entryRuleEdgeRhs returns [EObject current=null] : iv_ruleEdgeRhs= ruleEdgeRhs EOF ;
    public final EObject entryRuleEdgeRhs() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeRhs = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:984:2: (iv_ruleEdgeRhs= ruleEdgeRhs EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:985:2: iv_ruleEdgeRhs= ruleEdgeRhs EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getEdgeRhsRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleEdgeRhs_in_entryRuleEdgeRhs1650);
            iv_ruleEdgeRhs=ruleEdgeRhs();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleEdgeRhs; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhs1660); if (failed) return current;

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
    // $ANTLR end entryRuleEdgeRhs


    // $ANTLR start ruleEdgeRhs
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:992:1: ruleEdgeRhs returns [EObject current=null] : (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph ) ;
    public final EObject ruleEdgeRhs() throws RecognitionException {
        EObject current = null;

        EObject this_EdgeRhsNode_0 = null;

        EObject this_EdgeRhsSubgraph_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:997:6: ( (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:998:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:998:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==21) ) {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==13||LA19_1==20) ) {
                    alt19=2;
                }
                else if ( (LA19_1==RULE_DOT_ID) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("998:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )", 19, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA19_0==22) ) {
                int LA19_2 = input.LA(2);

                if ( (LA19_2==13||LA19_2==20) ) {
                    alt19=2;
                }
                else if ( (LA19_2==RULE_DOT_ID) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("998:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )", 19, 2, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("998:1: (this_EdgeRhsNode_0= ruleEdgeRhsNode | this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph )", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:999:2: this_EdgeRhsNode_0= ruleEdgeRhsNode
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getEdgeRhsAccess().getEdgeRhsNodeParserRuleCall_0(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeRhsNode_in_ruleEdgeRhs1710);
                    this_EdgeRhsNode_0=ruleEdgeRhsNode();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_EdgeRhsNode_0; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1012:2: this_EdgeRhsSubgraph_1= ruleEdgeRhsSubgraph
                    {
                    if ( backtracking==0 ) {
                       
                      	  /* */ 
                      	
                    }
                    if ( backtracking==0 ) {
                       
                              currentNode=createCompositeNode(grammarAccess.getEdgeRhsAccess().getEdgeRhsSubgraphParserRuleCall_1(), currentNode); 
                          
                    }
                    pushFollow(FOLLOW_ruleEdgeRhsSubgraph_in_ruleEdgeRhs1740);
                    this_EdgeRhsSubgraph_1=ruleEdgeRhsSubgraph();
                    _fsp--;
                    if (failed) return current;
                    if ( backtracking==0 ) {
                       
                              current = this_EdgeRhsSubgraph_1; 
                              currentNode = currentNode.getParent();
                          
                    }

                    }
                    break;

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleEdgeRhs


    // $ANTLR start entryRuleEdgeRhsNode
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1031:1: entryRuleEdgeRhsNode returns [EObject current=null] : iv_ruleEdgeRhsNode= ruleEdgeRhsNode EOF ;
    public final EObject entryRuleEdgeRhsNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeRhsNode = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1032:2: (iv_ruleEdgeRhsNode= ruleEdgeRhsNode EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1033:2: iv_ruleEdgeRhsNode= ruleEdgeRhsNode EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getEdgeRhsNodeRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleEdgeRhsNode_in_entryRuleEdgeRhsNode1775);
            iv_ruleEdgeRhsNode=ruleEdgeRhsNode();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleEdgeRhsNode; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhsNode1785); if (failed) return current;

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
    // $ANTLR end entryRuleEdgeRhsNode


    // $ANTLR start ruleEdgeRhsNode
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1040:1: ruleEdgeRhsNode returns [EObject current=null] : ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) ) ;
    public final EObject ruleEdgeRhsNode() throws RecognitionException {
        EObject current = null;

        Enumerator lv_op_0_0 = null;

        EObject lv_node_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1045:6: ( ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:2: ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_node_1_0= ruleNodeId ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1046:2: ( (lv_op_0_0= ruleEdgeOp ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1047:1: (lv_op_0_0= ruleEdgeOp )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1047:1: (lv_op_0_0= ruleEdgeOp )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1048:3: lv_op_0_0= ruleEdgeOp
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getEdgeRhsNodeAccess().getOpEdgeOpEnumRuleCall_0_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleEdgeOp_in_ruleEdgeRhsNode1831);
            lv_op_0_0=ruleEdgeOp();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getEdgeRhsNodeRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"op",
              	        		lv_op_0_0, 
              	        		"EdgeOp", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1070:2: ( (lv_node_1_0= ruleNodeId ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1071:1: (lv_node_1_0= ruleNodeId )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1071:1: (lv_node_1_0= ruleNodeId )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1072:3: lv_node_1_0= ruleNodeId
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getEdgeRhsNodeAccess().getNodeNodeIdParserRuleCall_1_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleNodeId_in_ruleEdgeRhsNode1852);
            lv_node_1_0=ruleNodeId();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getEdgeRhsNodeRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"node",
              	        		lv_node_1_0, 
              	        		"NodeId", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleEdgeRhsNode


    // $ANTLR start entryRuleEdgeRhsSubgraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1102:1: entryRuleEdgeRhsSubgraph returns [EObject current=null] : iv_ruleEdgeRhsSubgraph= ruleEdgeRhsSubgraph EOF ;
    public final EObject entryRuleEdgeRhsSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeRhsSubgraph = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1103:2: (iv_ruleEdgeRhsSubgraph= ruleEdgeRhsSubgraph EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1104:2: iv_ruleEdgeRhsSubgraph= ruleEdgeRhsSubgraph EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getEdgeRhsSubgraphRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleEdgeRhsSubgraph_in_entryRuleEdgeRhsSubgraph1888);
            iv_ruleEdgeRhsSubgraph=ruleEdgeRhsSubgraph();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleEdgeRhsSubgraph; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhsSubgraph1898); if (failed) return current;

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
    // $ANTLR end entryRuleEdgeRhsSubgraph


    // $ANTLR start ruleEdgeRhsSubgraph
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1111:1: ruleEdgeRhsSubgraph returns [EObject current=null] : ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) ) ;
    public final EObject ruleEdgeRhsSubgraph() throws RecognitionException {
        EObject current = null;

        Enumerator lv_op_0_0 = null;

        EObject lv_subgraph_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1116:6: ( ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1117:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1117:1: ( ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1117:2: ( (lv_op_0_0= ruleEdgeOp ) ) ( (lv_subgraph_1_0= ruleSubgraph ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1117:2: ( (lv_op_0_0= ruleEdgeOp ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1118:1: (lv_op_0_0= ruleEdgeOp )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1118:1: (lv_op_0_0= ruleEdgeOp )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1119:3: lv_op_0_0= ruleEdgeOp
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getEdgeRhsSubgraphAccess().getOpEdgeOpEnumRuleCall_0_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleEdgeOp_in_ruleEdgeRhsSubgraph1944);
            lv_op_0_0=ruleEdgeOp();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getEdgeRhsSubgraphRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"op",
              	        		lv_op_0_0, 
              	        		"EdgeOp", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }

            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1141:2: ( (lv_subgraph_1_0= ruleSubgraph ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1142:1: (lv_subgraph_1_0= ruleSubgraph )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1142:1: (lv_subgraph_1_0= ruleSubgraph )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1143:3: lv_subgraph_1_0= ruleSubgraph
            {
            if ( backtracking==0 ) {
               
              	        currentNode=createCompositeNode(grammarAccess.getEdgeRhsSubgraphAccess().getSubgraphSubgraphParserRuleCall_1_0(), currentNode); 
              	    
            }
            pushFollow(FOLLOW_ruleSubgraph_in_ruleEdgeRhsSubgraph1965);
            lv_subgraph_1_0=ruleSubgraph();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getEdgeRhsSubgraphRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode.getParent(), current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"subgraph",
              	        		lv_subgraph_1_0, 
              	        		"Subgraph", 
              	        		currentNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	        currentNode = currentNode.getParent();
              	    
            }

            }


            }


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleEdgeRhsSubgraph


    // $ANTLR start entryRuleNodeId
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1173:1: entryRuleNodeId returns [EObject current=null] : iv_ruleNodeId= ruleNodeId EOF ;
    public final EObject entryRuleNodeId() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNodeId = null;


        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1174:2: (iv_ruleNodeId= ruleNodeId EOF )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1175:2: iv_ruleNodeId= ruleNodeId EOF
            {
            if ( backtracking==0 ) {
               currentNode = createCompositeNode(grammarAccess.getNodeIdRule(), currentNode); 
            }
            pushFollow(FOLLOW_ruleNodeId_in_entryRuleNodeId2001);
            iv_ruleNodeId=ruleNodeId();
            _fsp--;
            if (failed) return current;
            if ( backtracking==0 ) {
               current =iv_ruleNodeId; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeId2011); if (failed) return current;

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
    // $ANTLR end entryRuleNodeId


    // $ANTLR start ruleNodeId
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1182:1: ruleNodeId returns [EObject current=null] : ( (lv_name_0_0= RULE_DOT_ID ) ) ;
    public final EObject ruleNodeId() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1187:6: ( ( (lv_name_0_0= RULE_DOT_ID ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1188:1: ( (lv_name_0_0= RULE_DOT_ID ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1188:1: ( (lv_name_0_0= RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1189:1: (lv_name_0_0= RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1189:1: (lv_name_0_0= RULE_DOT_ID )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1190:3: lv_name_0_0= RULE_DOT_ID
            {
            lv_name_0_0=(Token)input.LT(1);
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_ruleNodeId2052); if (failed) return current;
            if ( backtracking==0 ) {

              			createLeafNode(grammarAccess.getNodeIdAccess().getNameDOT_IDTerminalRuleCall_0(), "name"); 
              		
            }
            if ( backtracking==0 ) {

              	        if (current==null) {
              	            current = factory.create(grammarAccess.getNodeIdRule().getType().getClassifier());
              	            associateNodeWithAstElement(currentNode, current);
              	        }
              	        try {
              	       		set(
              	       			current, 
              	       			"name",
              	        		lv_name_0_0, 
              	        		"DOT_ID", 
              	        		lastConsumedNode);
              	        } catch (ValueConverterException vce) {
              				handleValueConverterException(vce);
              	        }
              	    
            }

            }


            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleNodeId


    // $ANTLR start ruleEdgeOp
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1220:1: ruleEdgeOp returns [Enumerator current=null] : ( ( '->' ) | ( '--' ) ) ;
    public final Enumerator ruleEdgeOp() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1224:6: ( ( ( '->' ) | ( '--' ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1225:1: ( ( '->' ) | ( '--' ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1225:1: ( ( '->' ) | ( '--' ) )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==21) ) {
                alt20=1;
            }
            else if ( (LA20_0==22) ) {
                alt20=2;
            }
            else {
                if (backtracking>0) {failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("1225:1: ( ( '->' ) | ( '--' ) )", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1225:2: ( '->' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1225:2: ( '->' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1225:4: '->'
                    {
                    match(input,21,FOLLOW_21_in_ruleEdgeOp2104); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getEdgeOpAccess().getDirectedEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getEdgeOpAccess().getDirectedEnumLiteralDeclaration_0(), null); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1231:6: ( '--' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1231:6: ( '--' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1231:8: '--'
                    {
                    match(input,22,FOLLOW_22_in_ruleEdgeOp2119); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getEdgeOpAccess().getUndirectedEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getEdgeOpAccess().getUndirectedEnumLiteralDeclaration_1(), null); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleEdgeOp


    // $ANTLR start ruleGraphType
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1241:1: ruleGraphType returns [Enumerator current=null] : ( ( 'graph' ) | ( 'digraph' ) ) ;
    public final Enumerator ruleGraphType() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1245:6: ( ( ( 'graph' ) | ( 'digraph' ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1246:1: ( ( 'graph' ) | ( 'digraph' ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1246:1: ( ( 'graph' ) | ( 'digraph' ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==23) ) {
                alt21=1;
            }
            else if ( (LA21_0==24) ) {
                alt21=2;
            }
            else {
                if (backtracking>0) {failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("1246:1: ( ( 'graph' ) | ( 'digraph' ) )", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1246:2: ( 'graph' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1246:2: ( 'graph' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1246:4: 'graph'
                    {
                    match(input,23,FOLLOW_23_in_ruleGraphType2162); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0(), null); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1252:6: ( 'digraph' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1252:6: ( 'digraph' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1252:8: 'digraph'
                    {
                    match(input,24,FOLLOW_24_in_ruleGraphType2177); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1(), null); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleGraphType


    // $ANTLR start ruleAttributeType
    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1262:1: ruleAttributeType returns [Enumerator current=null] : ( ( 'graph' ) | ( 'node' ) | ( 'edge' ) ) ;
    public final Enumerator ruleAttributeType() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1266:6: ( ( ( 'graph' ) | ( 'node' ) | ( 'edge' ) ) )
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1267:1: ( ( 'graph' ) | ( 'node' ) | ( 'edge' ) )
            {
            // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1267:1: ( ( 'graph' ) | ( 'node' ) | ( 'edge' ) )
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
                if (backtracking>0) {failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("1267:1: ( ( 'graph' ) | ( 'node' ) | ( 'edge' ) )", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1267:2: ( 'graph' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1267:2: ( 'graph' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1267:4: 'graph'
                    {
                    match(input,23,FOLLOW_23_in_ruleAttributeType2220); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0(), null); 
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1273:6: ( 'node' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1273:6: ( 'node' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1273:8: 'node'
                    {
                    match(input,25,FOLLOW_25_in_ruleAttributeType2235); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1(), null); 
                          
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1279:6: ( 'edge' )
                    {
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1279:6: ( 'edge' )
                    // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:1279:8: 'edge'
                    {
                    match(input,26,FOLLOW_26_in_ruleAttributeType2250); if (failed) return current;
                    if ( backtracking==0 ) {

                              current = grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                              createLeafNode(grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2(), null); 
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( backtracking==0 ) {
               resetLookahead(); 
                  	lastConsumedNode = currentNode;
                  
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
    // $ANTLR end ruleAttributeType

    // $ANTLR start synpred5
    public final void synpred5_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:271:2: ( ruleAttribute )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:271:2: ruleAttribute
        {
        if ( backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleAttribute_in_synpred5422);
        ruleAttribute();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred5

    // $ANTLR start synpred6
    public final void synpred6_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:284:2: ( ruleEdgeStmtNode )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:284:2: ruleEdgeStmtNode
        {
        if ( backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleEdgeStmtNode_in_synpred6452);
        ruleEdgeStmtNode();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred6

    // $ANTLR start synpred7
    public final void synpred7_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:297:2: ( ruleEdgeStmtSubgraph )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:297:2: ruleEdgeStmtSubgraph
        {
        if ( backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_synpred7482);
        ruleEdgeStmtSubgraph();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred7

    // $ANTLR start synpred8
    public final void synpred8_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:310:2: ( ruleNodeStmt )
        // ../org.eclipse.zest.dot.core/src-gen/org/eclipse/zest/internal/dot/parser/parser/antlr/internal/InternalDot.g:310:2: ruleNodeStmt
        {
        if ( backtracking==0 ) {
           
          	  /* */ 
          	
        }
        pushFollow(FOLLOW_ruleNodeStmt_in_synpred8512);
        ruleNodeStmt();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    public final boolean synpred5() {
        backtracking++;
        int start = input.mark();
        try {
            synpred5_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred6() {
        backtracking++;
        int start = input.mark();
        try {
            synpred6_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred7() {
        backtracking++;
        int start = input.mark();
        try {
            synpred7_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred8() {
        backtracking++;
        int start = input.mark();
        try {
            synpred8_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel81 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGraphvizModel91 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMainGraph_in_ruleGraphvizModel136 = new BitSet(new long[]{0x0000000001801002L});
    public static final BitSet FOLLOW_ruleMainGraph_in_entryRuleMainGraph172 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMainGraph182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleMainGraph225 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_ruleGraphType_in_ruleMainGraph260 = new BitSet(new long[]{0x0000000000002010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleMainGraph277 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleMainGraph293 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_ruleStmt_in_ruleMainGraph314 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_14_in_ruleMainGraph325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStmt_in_entryRuleStmt361 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStmt371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleStmt422 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_ruleStmt452 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_ruleStmt482 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_ruleStmt512 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleAttrStmt_in_ruleStmt542 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleStmt572 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_15_in_ruleStmt583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_entryRuleEdgeStmtNode621 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStmtNode631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_ruleEdgeStmtNode677 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtNode698 = new BitSet(new long[]{0x0000000000620002L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleEdgeStmtNode720 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_entryRuleEdgeStmtSubgraph757 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStmtSubgraph767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleEdgeStmtSubgraph813 = new BitSet(new long[]{0x0000000000600000L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_ruleEdgeStmtSubgraph834 = new BitSet(new long[]{0x0000000000620002L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleEdgeStmtSubgraph856 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_entryRuleNodeStmt893 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeStmt903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleNodeStmt945 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleNodeStmt971 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute1008 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute1018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAttribute1060 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttribute1075 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAttribute1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrStmt_in_entryRuleAttrStmt1133 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrStmt1143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleAttrStmt1189 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ruleAttrList_in_ruleAttrStmt1210 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ruleAttrList_in_entryRuleAttrList1247 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrList1257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleAttrList1292 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_ruleAList_in_ruleAttrList1313 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_18_in_ruleAttrList1324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAList_in_entryRuleAList1360 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAList1370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAList1412 = new BitSet(new long[]{0x0000000000090002L});
    public static final BitSet FOLLOW_16_in_ruleAList1428 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleAList1445 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_ruleAList1463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_entryRuleSubgraph1501 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSubgraph1511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_ruleSubgraph1547 = new BitSet(new long[]{0x0000000000002010L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleSubgraph1564 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleSubgraph1582 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_ruleStmt_in_ruleSubgraph1603 = new BitSet(new long[]{0x0000000006906010L});
    public static final BitSet FOLLOW_14_in_ruleSubgraph1614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_entryRuleEdgeRhs1650 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhs1660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsNode_in_ruleEdgeRhs1710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsSubgraph_in_ruleEdgeRhs1740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsNode_in_entryRuleEdgeRhsNode1775 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhsNode1785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOp_in_ruleEdgeRhsNode1831 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNodeId_in_ruleEdgeRhsNode1852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsSubgraph_in_entryRuleEdgeRhsSubgraph1888 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhsSubgraph1898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOp_in_ruleEdgeRhsSubgraph1944 = new BitSet(new long[]{0x0000000000102000L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleEdgeRhsSubgraph1965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_entryRuleNodeId2001 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeId2011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_ruleNodeId2052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleEdgeOp2104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleEdgeOp2119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleGraphType2162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleGraphType2177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleAttributeType2220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType2235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleAttributeType2250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_synpred5422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_synpred6452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_synpred7482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_synpred8512 = new BitSet(new long[]{0x0000000000000002L});

}