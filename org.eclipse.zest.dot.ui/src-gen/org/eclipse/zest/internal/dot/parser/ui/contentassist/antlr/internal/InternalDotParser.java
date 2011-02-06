package org.eclipse.zest.internal.dot.parser.ui.contentassist.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.DFA;
import org.eclipse.zest.internal.dot.parser.services.DotGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalDotParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_DOT_ID", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'->'", "'--'", "'graph'", "'digraph'", "'node'", "'edge'", "'{'", "'}'", "';'", "'='", "'['", "']'", "','", "'subgraph'", "'strict'"
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
    public String getGrammarFileName() { return "../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g"; }


     
     	private DotGrammarAccess grammarAccess;
     	
        public void setGrammarAccess(DotGrammarAccess grammarAccess) {
        	this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected Grammar getGrammar() {
        	return grammarAccess.getGrammar();
        }
        
        @Override
        protected String getValueForTokenName(String tokenName) {
        	return tokenName;
        }




    // $ANTLR start "entryRuleGraphvizModel"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:62:1: entryRuleGraphvizModel : ruleGraphvizModel EOF ;
    public final void entryRuleGraphvizModel() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:63:1: ( ruleGraphvizModel EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:64:1: ruleGraphvizModel EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getGraphvizModelRule()); 
            }
            pushFollow(FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel67);
            ruleGraphvizModel();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getGraphvizModelRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGraphvizModel74); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleGraphvizModel"


    // $ANTLR start "ruleGraphvizModel"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:71:1: ruleGraphvizModel : ( ( rule__GraphvizModel__GraphsAssignment )* ) ;
    public final void ruleGraphvizModel() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:75:2: ( ( ( rule__GraphvizModel__GraphsAssignment )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:76:1: ( ( rule__GraphvizModel__GraphsAssignment )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:76:1: ( ( rule__GraphvizModel__GraphsAssignment )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:77:1: ( rule__GraphvizModel__GraphsAssignment )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getGraphvizModelAccess().getGraphsAssignment()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:78:1: ( rule__GraphvizModel__GraphsAssignment )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=14 && LA1_0<=15)||LA1_0==26) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:78:2: rule__GraphvizModel__GraphsAssignment
            	    {
            	    pushFollow(FOLLOW_rule__GraphvizModel__GraphsAssignment_in_ruleGraphvizModel100);
            	    rule__GraphvizModel__GraphsAssignment();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getGraphvizModelAccess().getGraphsAssignment()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleGraphvizModel"


    // $ANTLR start "entryRuleMainGraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:90:1: entryRuleMainGraph : ruleMainGraph EOF ;
    public final void entryRuleMainGraph() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:91:1: ( ruleMainGraph EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:92:1: ruleMainGraph EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphRule()); 
            }
            pushFollow(FOLLOW_ruleMainGraph_in_entryRuleMainGraph128);
            ruleMainGraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMainGraph135); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleMainGraph"


    // $ANTLR start "ruleMainGraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:99:1: ruleMainGraph : ( ( rule__MainGraph__Group__0 ) ) ;
    public final void ruleMainGraph() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:103:2: ( ( ( rule__MainGraph__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:104:1: ( ( rule__MainGraph__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:104:1: ( ( rule__MainGraph__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:105:1: ( rule__MainGraph__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:106:1: ( rule__MainGraph__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:106:2: rule__MainGraph__Group__0
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__0_in_ruleMainGraph161);
            rule__MainGraph__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleMainGraph"


    // $ANTLR start "entryRuleStmt"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:118:1: entryRuleStmt : ruleStmt EOF ;
    public final void entryRuleStmt() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:119:1: ( ruleStmt EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:120:1: ruleStmt EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getStmtRule()); 
            }
            pushFollow(FOLLOW_ruleStmt_in_entryRuleStmt188);
            ruleStmt();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getStmtRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleStmt195); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleStmt"


    // $ANTLR start "ruleStmt"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:127:1: ruleStmt : ( ( rule__Stmt__Group__0 ) ) ;
    public final void ruleStmt() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:131:2: ( ( ( rule__Stmt__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:132:1: ( ( rule__Stmt__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:132:1: ( ( rule__Stmt__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:133:1: ( rule__Stmt__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getStmtAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:134:1: ( rule__Stmt__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:134:2: rule__Stmt__Group__0
            {
            pushFollow(FOLLOW_rule__Stmt__Group__0_in_ruleStmt221);
            rule__Stmt__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getStmtAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleStmt"


    // $ANTLR start "entryRuleEdgeStmtNode"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:146:1: entryRuleEdgeStmtNode : ruleEdgeStmtNode EOF ;
    public final void entryRuleEdgeStmtNode() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:147:1: ( ruleEdgeStmtNode EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:148:1: ruleEdgeStmtNode EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeStmtNode_in_entryRuleEdgeStmtNode248);
            ruleEdgeStmtNode();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStmtNode255); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEdgeStmtNode"


    // $ANTLR start "ruleEdgeStmtNode"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:155:1: ruleEdgeStmtNode : ( ( rule__EdgeStmtNode__Group__0 ) ) ;
    public final void ruleEdgeStmtNode() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:159:2: ( ( ( rule__EdgeStmtNode__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:160:1: ( ( rule__EdgeStmtNode__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:160:1: ( ( rule__EdgeStmtNode__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:161:1: ( rule__EdgeStmtNode__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:162:1: ( rule__EdgeStmtNode__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:162:2: rule__EdgeStmtNode__Group__0
            {
            pushFollow(FOLLOW_rule__EdgeStmtNode__Group__0_in_ruleEdgeStmtNode281);
            rule__EdgeStmtNode__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeStmtNode"


    // $ANTLR start "entryRuleEdgeStmtSubgraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:174:1: entryRuleEdgeStmtSubgraph : ruleEdgeStmtSubgraph EOF ;
    public final void entryRuleEdgeStmtSubgraph() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:175:1: ( ruleEdgeStmtSubgraph EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:176:1: ruleEdgeStmtSubgraph EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_entryRuleEdgeStmtSubgraph308);
            ruleEdgeStmtSubgraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStmtSubgraph315); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEdgeStmtSubgraph"


    // $ANTLR start "ruleEdgeStmtSubgraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:183:1: ruleEdgeStmtSubgraph : ( ( rule__EdgeStmtSubgraph__Group__0 ) ) ;
    public final void ruleEdgeStmtSubgraph() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:187:2: ( ( ( rule__EdgeStmtSubgraph__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:188:1: ( ( rule__EdgeStmtSubgraph__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:188:1: ( ( rule__EdgeStmtSubgraph__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:189:1: ( rule__EdgeStmtSubgraph__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:190:1: ( rule__EdgeStmtSubgraph__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:190:2: rule__EdgeStmtSubgraph__Group__0
            {
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__Group__0_in_ruleEdgeStmtSubgraph341);
            rule__EdgeStmtSubgraph__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeStmtSubgraph"


    // $ANTLR start "entryRuleNodeStmt"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:202:1: entryRuleNodeStmt : ruleNodeStmt EOF ;
    public final void entryRuleNodeStmt() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:203:1: ( ruleNodeStmt EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:204:1: ruleNodeStmt EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeStmtRule()); 
            }
            pushFollow(FOLLOW_ruleNodeStmt_in_entryRuleNodeStmt368);
            ruleNodeStmt();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeStmtRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeStmt375); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleNodeStmt"


    // $ANTLR start "ruleNodeStmt"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:211:1: ruleNodeStmt : ( ( rule__NodeStmt__Group__0 ) ) ;
    public final void ruleNodeStmt() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:215:2: ( ( ( rule__NodeStmt__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:216:1: ( ( rule__NodeStmt__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:216:1: ( ( rule__NodeStmt__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:217:1: ( rule__NodeStmt__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeStmtAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:218:1: ( rule__NodeStmt__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:218:2: rule__NodeStmt__Group__0
            {
            pushFollow(FOLLOW_rule__NodeStmt__Group__0_in_ruleNodeStmt401);
            rule__NodeStmt__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeStmtAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleNodeStmt"


    // $ANTLR start "entryRuleAttribute"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:230:1: entryRuleAttribute : ruleAttribute EOF ;
    public final void entryRuleAttribute() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:231:1: ( ruleAttribute EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:232:1: ruleAttribute EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeRule()); 
            }
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute428);
            ruleAttribute();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute435); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleAttribute"


    // $ANTLR start "ruleAttribute"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:239:1: ruleAttribute : ( ( rule__Attribute__Group__0 ) ) ;
    public final void ruleAttribute() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:243:2: ( ( ( rule__Attribute__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:244:1: ( ( rule__Attribute__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:244:1: ( ( rule__Attribute__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:245:1: ( rule__Attribute__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:246:1: ( rule__Attribute__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:246:2: rule__Attribute__Group__0
            {
            pushFollow(FOLLOW_rule__Attribute__Group__0_in_ruleAttribute461);
            rule__Attribute__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAttribute"


    // $ANTLR start "entryRuleAttrStmt"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:258:1: entryRuleAttrStmt : ruleAttrStmt EOF ;
    public final void entryRuleAttrStmt() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:259:1: ( ruleAttrStmt EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:260:1: ruleAttrStmt EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtRule()); 
            }
            pushFollow(FOLLOW_ruleAttrStmt_in_entryRuleAttrStmt488);
            ruleAttrStmt();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrStmt495); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleAttrStmt"


    // $ANTLR start "ruleAttrStmt"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:267:1: ruleAttrStmt : ( ( rule__AttrStmt__Group__0 ) ) ;
    public final void ruleAttrStmt() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:271:2: ( ( ( rule__AttrStmt__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:272:1: ( ( rule__AttrStmt__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:272:1: ( ( rule__AttrStmt__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:273:1: ( rule__AttrStmt__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:274:1: ( rule__AttrStmt__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:274:2: rule__AttrStmt__Group__0
            {
            pushFollow(FOLLOW_rule__AttrStmt__Group__0_in_ruleAttrStmt521);
            rule__AttrStmt__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAttrStmt"


    // $ANTLR start "entryRuleAttrList"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:286:1: entryRuleAttrList : ruleAttrList EOF ;
    public final void entryRuleAttrList() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:287:1: ( ruleAttrList EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:288:1: ruleAttrList EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrListRule()); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_entryRuleAttrList548);
            ruleAttrList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrListRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttrList555); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleAttrList"


    // $ANTLR start "ruleAttrList"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:295:1: ruleAttrList : ( ( rule__AttrList__Group__0 ) ) ;
    public final void ruleAttrList() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:299:2: ( ( ( rule__AttrList__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:300:1: ( ( rule__AttrList__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:300:1: ( ( rule__AttrList__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:301:1: ( rule__AttrList__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrListAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:302:1: ( rule__AttrList__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:302:2: rule__AttrList__Group__0
            {
            pushFollow(FOLLOW_rule__AttrList__Group__0_in_ruleAttrList581);
            rule__AttrList__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrListAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAttrList"


    // $ANTLR start "entryRuleAList"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:314:1: entryRuleAList : ruleAList EOF ;
    public final void entryRuleAList() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:315:1: ( ruleAList EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:316:1: ruleAList EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListRule()); 
            }
            pushFollow(FOLLOW_ruleAList_in_entryRuleAList608);
            ruleAList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAList615); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleAList"


    // $ANTLR start "ruleAList"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:323:1: ruleAList : ( ( rule__AList__Group__0 ) ) ;
    public final void ruleAList() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:327:2: ( ( ( rule__AList__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:328:1: ( ( rule__AList__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:328:1: ( ( rule__AList__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:329:1: ( rule__AList__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:330:1: ( rule__AList__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:330:2: rule__AList__Group__0
            {
            pushFollow(FOLLOW_rule__AList__Group__0_in_ruleAList641);
            rule__AList__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAList"


    // $ANTLR start "entryRuleSubgraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:342:1: entryRuleSubgraph : ruleSubgraph EOF ;
    public final void entryRuleSubgraph() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:343:1: ( ruleSubgraph EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:344:1: ruleSubgraph EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphRule()); 
            }
            pushFollow(FOLLOW_ruleSubgraph_in_entryRuleSubgraph668);
            ruleSubgraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleSubgraph675); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleSubgraph"


    // $ANTLR start "ruleSubgraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:351:1: ruleSubgraph : ( ( rule__Subgraph__Group__0 ) ) ;
    public final void ruleSubgraph() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:355:2: ( ( ( rule__Subgraph__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:356:1: ( ( rule__Subgraph__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:356:1: ( ( rule__Subgraph__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:357:1: ( rule__Subgraph__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:358:1: ( rule__Subgraph__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:358:2: rule__Subgraph__Group__0
            {
            pushFollow(FOLLOW_rule__Subgraph__Group__0_in_ruleSubgraph701);
            rule__Subgraph__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleSubgraph"


    // $ANTLR start "entryRuleEdgeRhs"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:370:1: entryRuleEdgeRhs : ruleEdgeRhs EOF ;
    public final void entryRuleEdgeRhs() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:371:1: ( ruleEdgeRhs EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:372:1: ruleEdgeRhs EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhs_in_entryRuleEdgeRhs728);
            ruleEdgeRhs();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhs735); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEdgeRhs"


    // $ANTLR start "ruleEdgeRhs"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:379:1: ruleEdgeRhs : ( ( rule__EdgeRhs__Alternatives ) ) ;
    public final void ruleEdgeRhs() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:383:2: ( ( ( rule__EdgeRhs__Alternatives ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:384:1: ( ( rule__EdgeRhs__Alternatives ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:384:1: ( ( rule__EdgeRhs__Alternatives ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:385:1: ( rule__EdgeRhs__Alternatives )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsAccess().getAlternatives()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:386:1: ( rule__EdgeRhs__Alternatives )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:386:2: rule__EdgeRhs__Alternatives
            {
            pushFollow(FOLLOW_rule__EdgeRhs__Alternatives_in_ruleEdgeRhs761);
            rule__EdgeRhs__Alternatives();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsAccess().getAlternatives()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeRhs"


    // $ANTLR start "entryRuleEdgeRhsNode"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:398:1: entryRuleEdgeRhsNode : ruleEdgeRhsNode EOF ;
    public final void entryRuleEdgeRhsNode() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:399:1: ( ruleEdgeRhsNode EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:400:1: ruleEdgeRhsNode EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsNodeRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhsNode_in_entryRuleEdgeRhsNode788);
            ruleEdgeRhsNode();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsNodeRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhsNode795); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEdgeRhsNode"


    // $ANTLR start "ruleEdgeRhsNode"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:407:1: ruleEdgeRhsNode : ( ( rule__EdgeRhsNode__Group__0 ) ) ;
    public final void ruleEdgeRhsNode() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:411:2: ( ( ( rule__EdgeRhsNode__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:412:1: ( ( rule__EdgeRhsNode__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:412:1: ( ( rule__EdgeRhsNode__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:413:1: ( rule__EdgeRhsNode__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsNodeAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:414:1: ( rule__EdgeRhsNode__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:414:2: rule__EdgeRhsNode__Group__0
            {
            pushFollow(FOLLOW_rule__EdgeRhsNode__Group__0_in_ruleEdgeRhsNode821);
            rule__EdgeRhsNode__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsNodeAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeRhsNode"


    // $ANTLR start "entryRuleEdgeRhsSubgraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:426:1: entryRuleEdgeRhsSubgraph : ruleEdgeRhsSubgraph EOF ;
    public final void entryRuleEdgeRhsSubgraph() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:427:1: ( ruleEdgeRhsSubgraph EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:428:1: ruleEdgeRhsSubgraph EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsSubgraphRule()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhsSubgraph_in_entryRuleEdgeRhsSubgraph848);
            ruleEdgeRhsSubgraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsSubgraphRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeRhsSubgraph855); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleEdgeRhsSubgraph"


    // $ANTLR start "ruleEdgeRhsSubgraph"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:435:1: ruleEdgeRhsSubgraph : ( ( rule__EdgeRhsSubgraph__Group__0 ) ) ;
    public final void ruleEdgeRhsSubgraph() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:439:2: ( ( ( rule__EdgeRhsSubgraph__Group__0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:440:1: ( ( rule__EdgeRhsSubgraph__Group__0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:440:1: ( ( rule__EdgeRhsSubgraph__Group__0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:441:1: ( rule__EdgeRhsSubgraph__Group__0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsSubgraphAccess().getGroup()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:442:1: ( rule__EdgeRhsSubgraph__Group__0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:442:2: rule__EdgeRhsSubgraph__Group__0
            {
            pushFollow(FOLLOW_rule__EdgeRhsSubgraph__Group__0_in_ruleEdgeRhsSubgraph881);
            rule__EdgeRhsSubgraph__Group__0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsSubgraphAccess().getGroup()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeRhsSubgraph"


    // $ANTLR start "entryRuleNodeId"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:454:1: entryRuleNodeId : ruleNodeId EOF ;
    public final void entryRuleNodeId() throws RecognitionException {
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:455:1: ( ruleNodeId EOF )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:456:1: ruleNodeId EOF
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeIdRule()); 
            }
            pushFollow(FOLLOW_ruleNodeId_in_entryRuleNodeId908);
            ruleNodeId();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeIdRule()); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeId915); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleNodeId"


    // $ANTLR start "ruleNodeId"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:463:1: ruleNodeId : ( ( rule__NodeId__NameAssignment ) ) ;
    public final void ruleNodeId() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:467:2: ( ( ( rule__NodeId__NameAssignment ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:468:1: ( ( rule__NodeId__NameAssignment ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:468:1: ( ( rule__NodeId__NameAssignment ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:469:1: ( rule__NodeId__NameAssignment )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeIdAccess().getNameAssignment()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:470:1: ( rule__NodeId__NameAssignment )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:470:2: rule__NodeId__NameAssignment
            {
            pushFollow(FOLLOW_rule__NodeId__NameAssignment_in_ruleNodeId941);
            rule__NodeId__NameAssignment();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeIdAccess().getNameAssignment()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleNodeId"


    // $ANTLR start "ruleEdgeOp"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:483:1: ruleEdgeOp : ( ( rule__EdgeOp__Alternatives ) ) ;
    public final void ruleEdgeOp() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:487:1: ( ( ( rule__EdgeOp__Alternatives ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:488:1: ( ( rule__EdgeOp__Alternatives ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:488:1: ( ( rule__EdgeOp__Alternatives ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:489:1: ( rule__EdgeOp__Alternatives )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeOpAccess().getAlternatives()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:490:1: ( rule__EdgeOp__Alternatives )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:490:2: rule__EdgeOp__Alternatives
            {
            pushFollow(FOLLOW_rule__EdgeOp__Alternatives_in_ruleEdgeOp978);
            rule__EdgeOp__Alternatives();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeOpAccess().getAlternatives()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeOp"


    // $ANTLR start "ruleGraphType"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:502:1: ruleGraphType : ( ( rule__GraphType__Alternatives ) ) ;
    public final void ruleGraphType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:506:1: ( ( ( rule__GraphType__Alternatives ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:507:1: ( ( rule__GraphType__Alternatives ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:507:1: ( ( rule__GraphType__Alternatives ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:508:1: ( rule__GraphType__Alternatives )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getGraphTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:509:1: ( rule__GraphType__Alternatives )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:509:2: rule__GraphType__Alternatives
            {
            pushFollow(FOLLOW_rule__GraphType__Alternatives_in_ruleGraphType1014);
            rule__GraphType__Alternatives();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getGraphTypeAccess().getAlternatives()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleGraphType"


    // $ANTLR start "ruleAttributeType"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:521:1: ruleAttributeType : ( ( rule__AttributeType__Alternatives ) ) ;
    public final void ruleAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:525:1: ( ( ( rule__AttributeType__Alternatives ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:526:1: ( ( rule__AttributeType__Alternatives ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:526:1: ( ( rule__AttributeType__Alternatives ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:527:1: ( rule__AttributeType__Alternatives )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:528:1: ( rule__AttributeType__Alternatives )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:528:2: rule__AttributeType__Alternatives
            {
            pushFollow(FOLLOW_rule__AttributeType__Alternatives_in_ruleAttributeType1050);
            rule__AttributeType__Alternatives();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeTypeAccess().getAlternatives()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAttributeType"


    // $ANTLR start "rule__Stmt__Alternatives_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:541:1: rule__Stmt__Alternatives_0 : ( ( ruleAttribute ) | ( ruleEdgeStmtNode ) | ( ruleEdgeStmtSubgraph ) | ( ruleNodeStmt ) | ( ruleAttrStmt ) | ( ruleSubgraph ) );
    public final void rule__Stmt__Alternatives_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:545:1: ( ( ruleAttribute ) | ( ruleEdgeStmtNode ) | ( ruleEdgeStmtSubgraph ) | ( ruleNodeStmt ) | ( ruleAttrStmt ) | ( ruleSubgraph ) )
            int alt2=6;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:546:1: ( ruleAttribute )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:546:1: ( ruleAttribute )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:547:1: ruleAttribute
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getStmtAccess().getAttributeParserRuleCall_0_0()); 
                    }
                    pushFollow(FOLLOW_ruleAttribute_in_rule__Stmt__Alternatives_01087);
                    ruleAttribute();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getStmtAccess().getAttributeParserRuleCall_0_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:552:6: ( ruleEdgeStmtNode )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:552:6: ( ruleEdgeStmtNode )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:553:1: ruleEdgeStmtNode
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getStmtAccess().getEdgeStmtNodeParserRuleCall_0_1()); 
                    }
                    pushFollow(FOLLOW_ruleEdgeStmtNode_in_rule__Stmt__Alternatives_01104);
                    ruleEdgeStmtNode();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getStmtAccess().getEdgeStmtNodeParserRuleCall_0_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:558:6: ( ruleEdgeStmtSubgraph )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:558:6: ( ruleEdgeStmtSubgraph )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:559:1: ruleEdgeStmtSubgraph
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getStmtAccess().getEdgeStmtSubgraphParserRuleCall_0_2()); 
                    }
                    pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_rule__Stmt__Alternatives_01121);
                    ruleEdgeStmtSubgraph();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getStmtAccess().getEdgeStmtSubgraphParserRuleCall_0_2()); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:564:6: ( ruleNodeStmt )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:564:6: ( ruleNodeStmt )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:565:1: ruleNodeStmt
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getStmtAccess().getNodeStmtParserRuleCall_0_3()); 
                    }
                    pushFollow(FOLLOW_ruleNodeStmt_in_rule__Stmt__Alternatives_01138);
                    ruleNodeStmt();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getStmtAccess().getNodeStmtParserRuleCall_0_3()); 
                    }

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:570:6: ( ruleAttrStmt )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:570:6: ( ruleAttrStmt )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:571:1: ruleAttrStmt
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getStmtAccess().getAttrStmtParserRuleCall_0_4()); 
                    }
                    pushFollow(FOLLOW_ruleAttrStmt_in_rule__Stmt__Alternatives_01155);
                    ruleAttrStmt();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getStmtAccess().getAttrStmtParserRuleCall_0_4()); 
                    }

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:576:6: ( ruleSubgraph )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:576:6: ( ruleSubgraph )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:577:1: ruleSubgraph
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getStmtAccess().getSubgraphParserRuleCall_0_5()); 
                    }
                    pushFollow(FOLLOW_ruleSubgraph_in_rule__Stmt__Alternatives_01172);
                    ruleSubgraph();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getStmtAccess().getSubgraphParserRuleCall_0_5()); 
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Stmt__Alternatives_0"


    // $ANTLR start "rule__EdgeRhs__Alternatives"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:587:1: rule__EdgeRhs__Alternatives : ( ( ruleEdgeRhsNode ) | ( ruleEdgeRhsSubgraph ) );
    public final void rule__EdgeRhs__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:591:1: ( ( ruleEdgeRhsNode ) | ( ruleEdgeRhsSubgraph ) )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==12) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==18||LA3_1==25) ) {
                    alt3=2;
                }
                else if ( (LA3_1==RULE_DOT_ID) ) {
                    alt3=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA3_0==13) ) {
                int LA3_2 = input.LA(2);

                if ( (LA3_2==RULE_DOT_ID) ) {
                    alt3=1;
                }
                else if ( (LA3_2==18||LA3_2==25) ) {
                    alt3=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:592:1: ( ruleEdgeRhsNode )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:592:1: ( ruleEdgeRhsNode )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:593:1: ruleEdgeRhsNode
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getEdgeRhsAccess().getEdgeRhsNodeParserRuleCall_0()); 
                    }
                    pushFollow(FOLLOW_ruleEdgeRhsNode_in_rule__EdgeRhs__Alternatives1204);
                    ruleEdgeRhsNode();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getEdgeRhsAccess().getEdgeRhsNodeParserRuleCall_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:598:6: ( ruleEdgeRhsSubgraph )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:598:6: ( ruleEdgeRhsSubgraph )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:599:1: ruleEdgeRhsSubgraph
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getEdgeRhsAccess().getEdgeRhsSubgraphParserRuleCall_1()); 
                    }
                    pushFollow(FOLLOW_ruleEdgeRhsSubgraph_in_rule__EdgeRhs__Alternatives1221);
                    ruleEdgeRhsSubgraph();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getEdgeRhsAccess().getEdgeRhsSubgraphParserRuleCall_1()); 
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhs__Alternatives"


    // $ANTLR start "rule__EdgeOp__Alternatives"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:609:1: rule__EdgeOp__Alternatives : ( ( ( '->' ) ) | ( ( '--' ) ) );
    public final void rule__EdgeOp__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:613:1: ( ( ( '->' ) ) | ( ( '--' ) ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==12) ) {
                alt4=1;
            }
            else if ( (LA4_0==13) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:614:1: ( ( '->' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:614:1: ( ( '->' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:615:1: ( '->' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getEdgeOpAccess().getDirectedEnumLiteralDeclaration_0()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:616:1: ( '->' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:616:3: '->'
                    {
                    match(input,12,FOLLOW_12_in_rule__EdgeOp__Alternatives1254); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getEdgeOpAccess().getDirectedEnumLiteralDeclaration_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:621:6: ( ( '--' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:621:6: ( ( '--' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:622:1: ( '--' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getEdgeOpAccess().getUndirectedEnumLiteralDeclaration_1()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:623:1: ( '--' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:623:3: '--'
                    {
                    match(input,13,FOLLOW_13_in_rule__EdgeOp__Alternatives1275); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getEdgeOpAccess().getUndirectedEnumLiteralDeclaration_1()); 
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeOp__Alternatives"


    // $ANTLR start "rule__GraphType__Alternatives"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:633:1: rule__GraphType__Alternatives : ( ( ( 'graph' ) ) | ( ( 'digraph' ) ) );
    public final void rule__GraphType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:637:1: ( ( ( 'graph' ) ) | ( ( 'digraph' ) ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==14) ) {
                alt5=1;
            }
            else if ( (LA5_0==15) ) {
                alt5=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:638:1: ( ( 'graph' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:638:1: ( ( 'graph' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:639:1: ( 'graph' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:640:1: ( 'graph' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:640:3: 'graph'
                    {
                    match(input,14,FOLLOW_14_in_rule__GraphType__Alternatives1311); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:645:6: ( ( 'digraph' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:645:6: ( ( 'digraph' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:646:1: ( 'digraph' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:647:1: ( 'digraph' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:647:3: 'digraph'
                    {
                    match(input,15,FOLLOW_15_in_rule__GraphType__Alternatives1332); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1()); 
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__GraphType__Alternatives"


    // $ANTLR start "rule__AttributeType__Alternatives"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:657:1: rule__AttributeType__Alternatives : ( ( ( 'graph' ) ) | ( ( 'node' ) ) | ( ( 'edge' ) ) );
    public final void rule__AttributeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:661:1: ( ( ( 'graph' ) ) | ( ( 'node' ) ) | ( ( 'edge' ) ) )
            int alt6=3;
            switch ( input.LA(1) ) {
            case 14:
                {
                alt6=1;
                }
                break;
            case 16:
                {
                alt6=2;
                }
                break;
            case 17:
                {
                alt6=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:662:1: ( ( 'graph' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:662:1: ( ( 'graph' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:663:1: ( 'graph' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:664:1: ( 'graph' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:664:3: 'graph'
                    {
                    match(input,14,FOLLOW_14_in_rule__AttributeType__Alternatives1368); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:669:6: ( ( 'node' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:669:6: ( ( 'node' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:670:1: ( 'node' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:671:1: ( 'node' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:671:3: 'node'
                    {
                    match(input,16,FOLLOW_16_in_rule__AttributeType__Alternatives1389); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:676:6: ( ( 'edge' ) )
                    {
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:676:6: ( ( 'edge' ) )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:677:1: ( 'edge' )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2()); 
                    }
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:678:1: ( 'edge' )
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:678:3: 'edge'
                    {
                    match(input,17,FOLLOW_17_in_rule__AttributeType__Alternatives1410); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2()); 
                    }

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeType__Alternatives"


    // $ANTLR start "rule__MainGraph__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:691:1: rule__MainGraph__Group__0 : rule__MainGraph__Group__0__Impl rule__MainGraph__Group__1 ;
    public final void rule__MainGraph__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:695:1: ( rule__MainGraph__Group__0__Impl rule__MainGraph__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:696:2: rule__MainGraph__Group__0__Impl rule__MainGraph__Group__1
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__0__Impl_in_rule__MainGraph__Group__01444);
            rule__MainGraph__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__MainGraph__Group__1_in_rule__MainGraph__Group__01447);
            rule__MainGraph__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__0"


    // $ANTLR start "rule__MainGraph__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:703:1: rule__MainGraph__Group__0__Impl : ( ( rule__MainGraph__StrictAssignment_0 )? ) ;
    public final void rule__MainGraph__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:707:1: ( ( ( rule__MainGraph__StrictAssignment_0 )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:708:1: ( ( rule__MainGraph__StrictAssignment_0 )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:708:1: ( ( rule__MainGraph__StrictAssignment_0 )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:709:1: ( rule__MainGraph__StrictAssignment_0 )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getStrictAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:710:1: ( rule__MainGraph__StrictAssignment_0 )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==26) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:710:2: rule__MainGraph__StrictAssignment_0
                    {
                    pushFollow(FOLLOW_rule__MainGraph__StrictAssignment_0_in_rule__MainGraph__Group__0__Impl1474);
                    rule__MainGraph__StrictAssignment_0();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getStrictAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__0__Impl"


    // $ANTLR start "rule__MainGraph__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:720:1: rule__MainGraph__Group__1 : rule__MainGraph__Group__1__Impl rule__MainGraph__Group__2 ;
    public final void rule__MainGraph__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:724:1: ( rule__MainGraph__Group__1__Impl rule__MainGraph__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:725:2: rule__MainGraph__Group__1__Impl rule__MainGraph__Group__2
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__1__Impl_in_rule__MainGraph__Group__11505);
            rule__MainGraph__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__MainGraph__Group__2_in_rule__MainGraph__Group__11508);
            rule__MainGraph__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__1"


    // $ANTLR start "rule__MainGraph__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:732:1: rule__MainGraph__Group__1__Impl : ( ( rule__MainGraph__TypeAssignment_1 ) ) ;
    public final void rule__MainGraph__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:736:1: ( ( ( rule__MainGraph__TypeAssignment_1 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:737:1: ( ( rule__MainGraph__TypeAssignment_1 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:737:1: ( ( rule__MainGraph__TypeAssignment_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:738:1: ( rule__MainGraph__TypeAssignment_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getTypeAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:739:1: ( rule__MainGraph__TypeAssignment_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:739:2: rule__MainGraph__TypeAssignment_1
            {
            pushFollow(FOLLOW_rule__MainGraph__TypeAssignment_1_in_rule__MainGraph__Group__1__Impl1535);
            rule__MainGraph__TypeAssignment_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getTypeAssignment_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__1__Impl"


    // $ANTLR start "rule__MainGraph__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:749:1: rule__MainGraph__Group__2 : rule__MainGraph__Group__2__Impl rule__MainGraph__Group__3 ;
    public final void rule__MainGraph__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:753:1: ( rule__MainGraph__Group__2__Impl rule__MainGraph__Group__3 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:754:2: rule__MainGraph__Group__2__Impl rule__MainGraph__Group__3
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__2__Impl_in_rule__MainGraph__Group__21565);
            rule__MainGraph__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__MainGraph__Group__3_in_rule__MainGraph__Group__21568);
            rule__MainGraph__Group__3();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__2"


    // $ANTLR start "rule__MainGraph__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:761:1: rule__MainGraph__Group__2__Impl : ( ( rule__MainGraph__NameAssignment_2 )? ) ;
    public final void rule__MainGraph__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:765:1: ( ( ( rule__MainGraph__NameAssignment_2 )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:766:1: ( ( rule__MainGraph__NameAssignment_2 )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:766:1: ( ( rule__MainGraph__NameAssignment_2 )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:767:1: ( rule__MainGraph__NameAssignment_2 )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getNameAssignment_2()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:768:1: ( rule__MainGraph__NameAssignment_2 )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_DOT_ID) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:768:2: rule__MainGraph__NameAssignment_2
                    {
                    pushFollow(FOLLOW_rule__MainGraph__NameAssignment_2_in_rule__MainGraph__Group__2__Impl1595);
                    rule__MainGraph__NameAssignment_2();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getNameAssignment_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__2__Impl"


    // $ANTLR start "rule__MainGraph__Group__3"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:778:1: rule__MainGraph__Group__3 : rule__MainGraph__Group__3__Impl rule__MainGraph__Group__4 ;
    public final void rule__MainGraph__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:782:1: ( rule__MainGraph__Group__3__Impl rule__MainGraph__Group__4 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:783:2: rule__MainGraph__Group__3__Impl rule__MainGraph__Group__4
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__3__Impl_in_rule__MainGraph__Group__31626);
            rule__MainGraph__Group__3__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__MainGraph__Group__4_in_rule__MainGraph__Group__31629);
            rule__MainGraph__Group__4();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__3"


    // $ANTLR start "rule__MainGraph__Group__3__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:790:1: rule__MainGraph__Group__3__Impl : ( '{' ) ;
    public final void rule__MainGraph__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:794:1: ( ( '{' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:795:1: ( '{' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:795:1: ( '{' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:796:1: '{'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getLeftCurlyBracketKeyword_3()); 
            }
            match(input,18,FOLLOW_18_in_rule__MainGraph__Group__3__Impl1657); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getLeftCurlyBracketKeyword_3()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__3__Impl"


    // $ANTLR start "rule__MainGraph__Group__4"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:809:1: rule__MainGraph__Group__4 : rule__MainGraph__Group__4__Impl rule__MainGraph__Group__5 ;
    public final void rule__MainGraph__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:813:1: ( rule__MainGraph__Group__4__Impl rule__MainGraph__Group__5 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:814:2: rule__MainGraph__Group__4__Impl rule__MainGraph__Group__5
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__4__Impl_in_rule__MainGraph__Group__41688);
            rule__MainGraph__Group__4__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__MainGraph__Group__5_in_rule__MainGraph__Group__41691);
            rule__MainGraph__Group__5();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__4"


    // $ANTLR start "rule__MainGraph__Group__4__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:821:1: rule__MainGraph__Group__4__Impl : ( ( rule__MainGraph__StmtsAssignment_4 )* ) ;
    public final void rule__MainGraph__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:825:1: ( ( ( rule__MainGraph__StmtsAssignment_4 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:826:1: ( ( rule__MainGraph__StmtsAssignment_4 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:826:1: ( ( rule__MainGraph__StmtsAssignment_4 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:827:1: ( rule__MainGraph__StmtsAssignment_4 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getStmtsAssignment_4()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:828:1: ( rule__MainGraph__StmtsAssignment_4 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_DOT_ID||LA9_0==14||(LA9_0>=16 && LA9_0<=18)||LA9_0==25) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:828:2: rule__MainGraph__StmtsAssignment_4
            	    {
            	    pushFollow(FOLLOW_rule__MainGraph__StmtsAssignment_4_in_rule__MainGraph__Group__4__Impl1718);
            	    rule__MainGraph__StmtsAssignment_4();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getStmtsAssignment_4()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__4__Impl"


    // $ANTLR start "rule__MainGraph__Group__5"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:838:1: rule__MainGraph__Group__5 : rule__MainGraph__Group__5__Impl ;
    public final void rule__MainGraph__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:842:1: ( rule__MainGraph__Group__5__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:843:2: rule__MainGraph__Group__5__Impl
            {
            pushFollow(FOLLOW_rule__MainGraph__Group__5__Impl_in_rule__MainGraph__Group__51749);
            rule__MainGraph__Group__5__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__5"


    // $ANTLR start "rule__MainGraph__Group__5__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:849:1: rule__MainGraph__Group__5__Impl : ( '}' ) ;
    public final void rule__MainGraph__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:853:1: ( ( '}' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:854:1: ( '}' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:854:1: ( '}' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:855:1: '}'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getRightCurlyBracketKeyword_5()); 
            }
            match(input,19,FOLLOW_19_in_rule__MainGraph__Group__5__Impl1777); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getRightCurlyBracketKeyword_5()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__Group__5__Impl"


    // $ANTLR start "rule__Stmt__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:880:1: rule__Stmt__Group__0 : rule__Stmt__Group__0__Impl rule__Stmt__Group__1 ;
    public final void rule__Stmt__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:884:1: ( rule__Stmt__Group__0__Impl rule__Stmt__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:885:2: rule__Stmt__Group__0__Impl rule__Stmt__Group__1
            {
            pushFollow(FOLLOW_rule__Stmt__Group__0__Impl_in_rule__Stmt__Group__01820);
            rule__Stmt__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Stmt__Group__1_in_rule__Stmt__Group__01823);
            rule__Stmt__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Stmt__Group__0"


    // $ANTLR start "rule__Stmt__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:892:1: rule__Stmt__Group__0__Impl : ( ( rule__Stmt__Alternatives_0 ) ) ;
    public final void rule__Stmt__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:896:1: ( ( ( rule__Stmt__Alternatives_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:897:1: ( ( rule__Stmt__Alternatives_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:897:1: ( ( rule__Stmt__Alternatives_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:898:1: ( rule__Stmt__Alternatives_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getStmtAccess().getAlternatives_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:899:1: ( rule__Stmt__Alternatives_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:899:2: rule__Stmt__Alternatives_0
            {
            pushFollow(FOLLOW_rule__Stmt__Alternatives_0_in_rule__Stmt__Group__0__Impl1850);
            rule__Stmt__Alternatives_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getStmtAccess().getAlternatives_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Stmt__Group__0__Impl"


    // $ANTLR start "rule__Stmt__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:909:1: rule__Stmt__Group__1 : rule__Stmt__Group__1__Impl ;
    public final void rule__Stmt__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:913:1: ( rule__Stmt__Group__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:914:2: rule__Stmt__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__Stmt__Group__1__Impl_in_rule__Stmt__Group__11880);
            rule__Stmt__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Stmt__Group__1"


    // $ANTLR start "rule__Stmt__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:920:1: rule__Stmt__Group__1__Impl : ( ( ';' )? ) ;
    public final void rule__Stmt__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:924:1: ( ( ( ';' )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:925:1: ( ( ';' )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:925:1: ( ( ';' )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:926:1: ( ';' )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getStmtAccess().getSemicolonKeyword_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:927:1: ( ';' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==20) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:928:2: ';'
                    {
                    match(input,20,FOLLOW_20_in_rule__Stmt__Group__1__Impl1909); if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getStmtAccess().getSemicolonKeyword_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Stmt__Group__1__Impl"


    // $ANTLR start "rule__EdgeStmtNode__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:943:1: rule__EdgeStmtNode__Group__0 : rule__EdgeStmtNode__Group__0__Impl rule__EdgeStmtNode__Group__1 ;
    public final void rule__EdgeStmtNode__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:947:1: ( rule__EdgeStmtNode__Group__0__Impl rule__EdgeStmtNode__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:948:2: rule__EdgeStmtNode__Group__0__Impl rule__EdgeStmtNode__Group__1
            {
            pushFollow(FOLLOW_rule__EdgeStmtNode__Group__0__Impl_in_rule__EdgeStmtNode__Group__01946);
            rule__EdgeStmtNode__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__EdgeStmtNode__Group__1_in_rule__EdgeStmtNode__Group__01949);
            rule__EdgeStmtNode__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Group__0"


    // $ANTLR start "rule__EdgeStmtNode__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:955:1: rule__EdgeStmtNode__Group__0__Impl : ( ( rule__EdgeStmtNode__Node_idAssignment_0 ) ) ;
    public final void rule__EdgeStmtNode__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:959:1: ( ( ( rule__EdgeStmtNode__Node_idAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:960:1: ( ( rule__EdgeStmtNode__Node_idAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:960:1: ( ( rule__EdgeStmtNode__Node_idAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:961:1: ( rule__EdgeStmtNode__Node_idAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getNode_idAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:962:1: ( rule__EdgeStmtNode__Node_idAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:962:2: rule__EdgeStmtNode__Node_idAssignment_0
            {
            pushFollow(FOLLOW_rule__EdgeStmtNode__Node_idAssignment_0_in_rule__EdgeStmtNode__Group__0__Impl1976);
            rule__EdgeStmtNode__Node_idAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getNode_idAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Group__0__Impl"


    // $ANTLR start "rule__EdgeStmtNode__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:972:1: rule__EdgeStmtNode__Group__1 : rule__EdgeStmtNode__Group__1__Impl rule__EdgeStmtNode__Group__2 ;
    public final void rule__EdgeStmtNode__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:976:1: ( rule__EdgeStmtNode__Group__1__Impl rule__EdgeStmtNode__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:977:2: rule__EdgeStmtNode__Group__1__Impl rule__EdgeStmtNode__Group__2
            {
            pushFollow(FOLLOW_rule__EdgeStmtNode__Group__1__Impl_in_rule__EdgeStmtNode__Group__12006);
            rule__EdgeStmtNode__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__EdgeStmtNode__Group__2_in_rule__EdgeStmtNode__Group__12009);
            rule__EdgeStmtNode__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Group__1"


    // $ANTLR start "rule__EdgeStmtNode__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:984:1: rule__EdgeStmtNode__Group__1__Impl : ( ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )* ) ) ;
    public final void rule__EdgeStmtNode__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:988:1: ( ( ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )* ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:989:1: ( ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )* ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:989:1: ( ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:990:1: ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:990:1: ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:991:1: ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:992:1: ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:992:2: rule__EdgeStmtNode__EdgeRHSAssignment_1
            {
            pushFollow(FOLLOW_rule__EdgeStmtNode__EdgeRHSAssignment_1_in_rule__EdgeStmtNode__Group__1__Impl2038);
            rule__EdgeStmtNode__EdgeRHSAssignment_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSAssignment_1()); 
            }

            }

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:995:1: ( ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:996:1: ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:997:1: ( rule__EdgeStmtNode__EdgeRHSAssignment_1 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=12 && LA11_0<=13)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:997:2: rule__EdgeStmtNode__EdgeRHSAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__EdgeStmtNode__EdgeRHSAssignment_1_in_rule__EdgeStmtNode__Group__1__Impl2050);
            	    rule__EdgeStmtNode__EdgeRHSAssignment_1();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSAssignment_1()); 
            }

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Group__1__Impl"


    // $ANTLR start "rule__EdgeStmtNode__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1008:1: rule__EdgeStmtNode__Group__2 : rule__EdgeStmtNode__Group__2__Impl ;
    public final void rule__EdgeStmtNode__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1012:1: ( rule__EdgeStmtNode__Group__2__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1013:2: rule__EdgeStmtNode__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__EdgeStmtNode__Group__2__Impl_in_rule__EdgeStmtNode__Group__22083);
            rule__EdgeStmtNode__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Group__2"


    // $ANTLR start "rule__EdgeStmtNode__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1019:1: rule__EdgeStmtNode__Group__2__Impl : ( ( rule__EdgeStmtNode__AttributesAssignment_2 )* ) ;
    public final void rule__EdgeStmtNode__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1023:1: ( ( ( rule__EdgeStmtNode__AttributesAssignment_2 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1024:1: ( ( rule__EdgeStmtNode__AttributesAssignment_2 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1024:1: ( ( rule__EdgeStmtNode__AttributesAssignment_2 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1025:1: ( rule__EdgeStmtNode__AttributesAssignment_2 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getAttributesAssignment_2()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1026:1: ( rule__EdgeStmtNode__AttributesAssignment_2 )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==22) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1026:2: rule__EdgeStmtNode__AttributesAssignment_2
            	    {
            	    pushFollow(FOLLOW_rule__EdgeStmtNode__AttributesAssignment_2_in_rule__EdgeStmtNode__Group__2__Impl2110);
            	    rule__EdgeStmtNode__AttributesAssignment_2();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getAttributesAssignment_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Group__2__Impl"


    // $ANTLR start "rule__EdgeStmtSubgraph__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1042:1: rule__EdgeStmtSubgraph__Group__0 : rule__EdgeStmtSubgraph__Group__0__Impl rule__EdgeStmtSubgraph__Group__1 ;
    public final void rule__EdgeStmtSubgraph__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1046:1: ( rule__EdgeStmtSubgraph__Group__0__Impl rule__EdgeStmtSubgraph__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1047:2: rule__EdgeStmtSubgraph__Group__0__Impl rule__EdgeStmtSubgraph__Group__1
            {
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__Group__0__Impl_in_rule__EdgeStmtSubgraph__Group__02147);
            rule__EdgeStmtSubgraph__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__Group__1_in_rule__EdgeStmtSubgraph__Group__02150);
            rule__EdgeStmtSubgraph__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__Group__0"


    // $ANTLR start "rule__EdgeStmtSubgraph__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1054:1: rule__EdgeStmtSubgraph__Group__0__Impl : ( ( rule__EdgeStmtSubgraph__SubgraphAssignment_0 ) ) ;
    public final void rule__EdgeStmtSubgraph__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1058:1: ( ( ( rule__EdgeStmtSubgraph__SubgraphAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1059:1: ( ( rule__EdgeStmtSubgraph__SubgraphAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1059:1: ( ( rule__EdgeStmtSubgraph__SubgraphAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1060:1: ( rule__EdgeStmtSubgraph__SubgraphAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getSubgraphAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1061:1: ( rule__EdgeStmtSubgraph__SubgraphAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1061:2: rule__EdgeStmtSubgraph__SubgraphAssignment_0
            {
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__SubgraphAssignment_0_in_rule__EdgeStmtSubgraph__Group__0__Impl2177);
            rule__EdgeStmtSubgraph__SubgraphAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getSubgraphAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__Group__0__Impl"


    // $ANTLR start "rule__EdgeStmtSubgraph__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1071:1: rule__EdgeStmtSubgraph__Group__1 : rule__EdgeStmtSubgraph__Group__1__Impl rule__EdgeStmtSubgraph__Group__2 ;
    public final void rule__EdgeStmtSubgraph__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1075:1: ( rule__EdgeStmtSubgraph__Group__1__Impl rule__EdgeStmtSubgraph__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1076:2: rule__EdgeStmtSubgraph__Group__1__Impl rule__EdgeStmtSubgraph__Group__2
            {
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__Group__1__Impl_in_rule__EdgeStmtSubgraph__Group__12207);
            rule__EdgeStmtSubgraph__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__Group__2_in_rule__EdgeStmtSubgraph__Group__12210);
            rule__EdgeStmtSubgraph__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__Group__1"


    // $ANTLR start "rule__EdgeStmtSubgraph__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1083:1: rule__EdgeStmtSubgraph__Group__1__Impl : ( ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )* ) ) ;
    public final void rule__EdgeStmtSubgraph__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1087:1: ( ( ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )* ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1088:1: ( ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )* ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1088:1: ( ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1089:1: ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 ) ) ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1089:1: ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1090:1: ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1091:1: ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1091:2: rule__EdgeStmtSubgraph__EdgeRHSAssignment_1
            {
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__EdgeRHSAssignment_1_in_rule__EdgeStmtSubgraph__Group__1__Impl2239);
            rule__EdgeStmtSubgraph__EdgeRHSAssignment_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSAssignment_1()); 
            }

            }

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1094:1: ( ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1095:1: ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1096:1: ( rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=12 && LA13_0<=13)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1096:2: rule__EdgeStmtSubgraph__EdgeRHSAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__EdgeStmtSubgraph__EdgeRHSAssignment_1_in_rule__EdgeStmtSubgraph__Group__1__Impl2251);
            	    rule__EdgeStmtSubgraph__EdgeRHSAssignment_1();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSAssignment_1()); 
            }

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__Group__1__Impl"


    // $ANTLR start "rule__EdgeStmtSubgraph__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1107:1: rule__EdgeStmtSubgraph__Group__2 : rule__EdgeStmtSubgraph__Group__2__Impl ;
    public final void rule__EdgeStmtSubgraph__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1111:1: ( rule__EdgeStmtSubgraph__Group__2__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1112:2: rule__EdgeStmtSubgraph__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__EdgeStmtSubgraph__Group__2__Impl_in_rule__EdgeStmtSubgraph__Group__22284);
            rule__EdgeStmtSubgraph__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__Group__2"


    // $ANTLR start "rule__EdgeStmtSubgraph__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1118:1: rule__EdgeStmtSubgraph__Group__2__Impl : ( ( rule__EdgeStmtSubgraph__AttributesAssignment_2 )* ) ;
    public final void rule__EdgeStmtSubgraph__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1122:1: ( ( ( rule__EdgeStmtSubgraph__AttributesAssignment_2 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1123:1: ( ( rule__EdgeStmtSubgraph__AttributesAssignment_2 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1123:1: ( ( rule__EdgeStmtSubgraph__AttributesAssignment_2 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1124:1: ( rule__EdgeStmtSubgraph__AttributesAssignment_2 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getAttributesAssignment_2()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1125:1: ( rule__EdgeStmtSubgraph__AttributesAssignment_2 )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==22) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1125:2: rule__EdgeStmtSubgraph__AttributesAssignment_2
            	    {
            	    pushFollow(FOLLOW_rule__EdgeStmtSubgraph__AttributesAssignment_2_in_rule__EdgeStmtSubgraph__Group__2__Impl2311);
            	    rule__EdgeStmtSubgraph__AttributesAssignment_2();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getAttributesAssignment_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__Group__2__Impl"


    // $ANTLR start "rule__NodeStmt__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1141:1: rule__NodeStmt__Group__0 : rule__NodeStmt__Group__0__Impl rule__NodeStmt__Group__1 ;
    public final void rule__NodeStmt__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1145:1: ( rule__NodeStmt__Group__0__Impl rule__NodeStmt__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1146:2: rule__NodeStmt__Group__0__Impl rule__NodeStmt__Group__1
            {
            pushFollow(FOLLOW_rule__NodeStmt__Group__0__Impl_in_rule__NodeStmt__Group__02348);
            rule__NodeStmt__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__NodeStmt__Group__1_in_rule__NodeStmt__Group__02351);
            rule__NodeStmt__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeStmt__Group__0"


    // $ANTLR start "rule__NodeStmt__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1153:1: rule__NodeStmt__Group__0__Impl : ( ( rule__NodeStmt__NameAssignment_0 ) ) ;
    public final void rule__NodeStmt__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1157:1: ( ( ( rule__NodeStmt__NameAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1158:1: ( ( rule__NodeStmt__NameAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1158:1: ( ( rule__NodeStmt__NameAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1159:1: ( rule__NodeStmt__NameAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeStmtAccess().getNameAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1160:1: ( rule__NodeStmt__NameAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1160:2: rule__NodeStmt__NameAssignment_0
            {
            pushFollow(FOLLOW_rule__NodeStmt__NameAssignment_0_in_rule__NodeStmt__Group__0__Impl2378);
            rule__NodeStmt__NameAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeStmtAccess().getNameAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeStmt__Group__0__Impl"


    // $ANTLR start "rule__NodeStmt__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1170:1: rule__NodeStmt__Group__1 : rule__NodeStmt__Group__1__Impl ;
    public final void rule__NodeStmt__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1174:1: ( rule__NodeStmt__Group__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1175:2: rule__NodeStmt__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__NodeStmt__Group__1__Impl_in_rule__NodeStmt__Group__12408);
            rule__NodeStmt__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeStmt__Group__1"


    // $ANTLR start "rule__NodeStmt__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1181:1: rule__NodeStmt__Group__1__Impl : ( ( rule__NodeStmt__AttributesAssignment_1 )* ) ;
    public final void rule__NodeStmt__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1185:1: ( ( ( rule__NodeStmt__AttributesAssignment_1 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1186:1: ( ( rule__NodeStmt__AttributesAssignment_1 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1186:1: ( ( rule__NodeStmt__AttributesAssignment_1 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1187:1: ( rule__NodeStmt__AttributesAssignment_1 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeStmtAccess().getAttributesAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1188:1: ( rule__NodeStmt__AttributesAssignment_1 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==22) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1188:2: rule__NodeStmt__AttributesAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__NodeStmt__AttributesAssignment_1_in_rule__NodeStmt__Group__1__Impl2435);
            	    rule__NodeStmt__AttributesAssignment_1();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeStmtAccess().getAttributesAssignment_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeStmt__Group__1__Impl"


    // $ANTLR start "rule__Attribute__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1202:1: rule__Attribute__Group__0 : rule__Attribute__Group__0__Impl rule__Attribute__Group__1 ;
    public final void rule__Attribute__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1206:1: ( rule__Attribute__Group__0__Impl rule__Attribute__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1207:2: rule__Attribute__Group__0__Impl rule__Attribute__Group__1
            {
            pushFollow(FOLLOW_rule__Attribute__Group__0__Impl_in_rule__Attribute__Group__02470);
            rule__Attribute__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Attribute__Group__1_in_rule__Attribute__Group__02473);
            rule__Attribute__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__Group__0"


    // $ANTLR start "rule__Attribute__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1214:1: rule__Attribute__Group__0__Impl : ( ( rule__Attribute__NameAssignment_0 ) ) ;
    public final void rule__Attribute__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1218:1: ( ( ( rule__Attribute__NameAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1219:1: ( ( rule__Attribute__NameAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1219:1: ( ( rule__Attribute__NameAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1220:1: ( rule__Attribute__NameAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeAccess().getNameAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1221:1: ( rule__Attribute__NameAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1221:2: rule__Attribute__NameAssignment_0
            {
            pushFollow(FOLLOW_rule__Attribute__NameAssignment_0_in_rule__Attribute__Group__0__Impl2500);
            rule__Attribute__NameAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeAccess().getNameAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__Group__0__Impl"


    // $ANTLR start "rule__Attribute__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1231:1: rule__Attribute__Group__1 : rule__Attribute__Group__1__Impl rule__Attribute__Group__2 ;
    public final void rule__Attribute__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1235:1: ( rule__Attribute__Group__1__Impl rule__Attribute__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1236:2: rule__Attribute__Group__1__Impl rule__Attribute__Group__2
            {
            pushFollow(FOLLOW_rule__Attribute__Group__1__Impl_in_rule__Attribute__Group__12530);
            rule__Attribute__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Attribute__Group__2_in_rule__Attribute__Group__12533);
            rule__Attribute__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__Group__1"


    // $ANTLR start "rule__Attribute__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1243:1: rule__Attribute__Group__1__Impl : ( '=' ) ;
    public final void rule__Attribute__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1247:1: ( ( '=' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1248:1: ( '=' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1248:1: ( '=' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1249:1: '='
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeAccess().getEqualsSignKeyword_1()); 
            }
            match(input,21,FOLLOW_21_in_rule__Attribute__Group__1__Impl2561); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeAccess().getEqualsSignKeyword_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__Group__1__Impl"


    // $ANTLR start "rule__Attribute__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1262:1: rule__Attribute__Group__2 : rule__Attribute__Group__2__Impl ;
    public final void rule__Attribute__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1266:1: ( rule__Attribute__Group__2__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1267:2: rule__Attribute__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__Attribute__Group__2__Impl_in_rule__Attribute__Group__22592);
            rule__Attribute__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__Group__2"


    // $ANTLR start "rule__Attribute__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1273:1: rule__Attribute__Group__2__Impl : ( ( rule__Attribute__ValueAssignment_2 ) ) ;
    public final void rule__Attribute__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1277:1: ( ( ( rule__Attribute__ValueAssignment_2 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1278:1: ( ( rule__Attribute__ValueAssignment_2 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1278:1: ( ( rule__Attribute__ValueAssignment_2 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1279:1: ( rule__Attribute__ValueAssignment_2 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeAccess().getValueAssignment_2()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1280:1: ( rule__Attribute__ValueAssignment_2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1280:2: rule__Attribute__ValueAssignment_2
            {
            pushFollow(FOLLOW_rule__Attribute__ValueAssignment_2_in_rule__Attribute__Group__2__Impl2619);
            rule__Attribute__ValueAssignment_2();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeAccess().getValueAssignment_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__Group__2__Impl"


    // $ANTLR start "rule__AttrStmt__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1296:1: rule__AttrStmt__Group__0 : rule__AttrStmt__Group__0__Impl rule__AttrStmt__Group__1 ;
    public final void rule__AttrStmt__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1300:1: ( rule__AttrStmt__Group__0__Impl rule__AttrStmt__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1301:2: rule__AttrStmt__Group__0__Impl rule__AttrStmt__Group__1
            {
            pushFollow(FOLLOW_rule__AttrStmt__Group__0__Impl_in_rule__AttrStmt__Group__02655);
            rule__AttrStmt__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__AttrStmt__Group__1_in_rule__AttrStmt__Group__02658);
            rule__AttrStmt__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrStmt__Group__0"


    // $ANTLR start "rule__AttrStmt__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1308:1: rule__AttrStmt__Group__0__Impl : ( ( rule__AttrStmt__TypeAssignment_0 ) ) ;
    public final void rule__AttrStmt__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1312:1: ( ( ( rule__AttrStmt__TypeAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1313:1: ( ( rule__AttrStmt__TypeAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1313:1: ( ( rule__AttrStmt__TypeAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1314:1: ( rule__AttrStmt__TypeAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtAccess().getTypeAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1315:1: ( rule__AttrStmt__TypeAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1315:2: rule__AttrStmt__TypeAssignment_0
            {
            pushFollow(FOLLOW_rule__AttrStmt__TypeAssignment_0_in_rule__AttrStmt__Group__0__Impl2685);
            rule__AttrStmt__TypeAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtAccess().getTypeAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrStmt__Group__0__Impl"


    // $ANTLR start "rule__AttrStmt__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1325:1: rule__AttrStmt__Group__1 : rule__AttrStmt__Group__1__Impl ;
    public final void rule__AttrStmt__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1329:1: ( rule__AttrStmt__Group__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1330:2: rule__AttrStmt__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__AttrStmt__Group__1__Impl_in_rule__AttrStmt__Group__12715);
            rule__AttrStmt__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrStmt__Group__1"


    // $ANTLR start "rule__AttrStmt__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1336:1: rule__AttrStmt__Group__1__Impl : ( ( ( rule__AttrStmt__AttributesAssignment_1 ) ) ( ( rule__AttrStmt__AttributesAssignment_1 )* ) ) ;
    public final void rule__AttrStmt__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1340:1: ( ( ( ( rule__AttrStmt__AttributesAssignment_1 ) ) ( ( rule__AttrStmt__AttributesAssignment_1 )* ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1341:1: ( ( ( rule__AttrStmt__AttributesAssignment_1 ) ) ( ( rule__AttrStmt__AttributesAssignment_1 )* ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1341:1: ( ( ( rule__AttrStmt__AttributesAssignment_1 ) ) ( ( rule__AttrStmt__AttributesAssignment_1 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1342:1: ( ( rule__AttrStmt__AttributesAssignment_1 ) ) ( ( rule__AttrStmt__AttributesAssignment_1 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1342:1: ( ( rule__AttrStmt__AttributesAssignment_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1343:1: ( rule__AttrStmt__AttributesAssignment_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtAccess().getAttributesAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1344:1: ( rule__AttrStmt__AttributesAssignment_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1344:2: rule__AttrStmt__AttributesAssignment_1
            {
            pushFollow(FOLLOW_rule__AttrStmt__AttributesAssignment_1_in_rule__AttrStmt__Group__1__Impl2744);
            rule__AttrStmt__AttributesAssignment_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtAccess().getAttributesAssignment_1()); 
            }

            }

            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1347:1: ( ( rule__AttrStmt__AttributesAssignment_1 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1348:1: ( rule__AttrStmt__AttributesAssignment_1 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtAccess().getAttributesAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1349:1: ( rule__AttrStmt__AttributesAssignment_1 )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==22) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1349:2: rule__AttrStmt__AttributesAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__AttrStmt__AttributesAssignment_1_in_rule__AttrStmt__Group__1__Impl2756);
            	    rule__AttrStmt__AttributesAssignment_1();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtAccess().getAttributesAssignment_1()); 
            }

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrStmt__Group__1__Impl"


    // $ANTLR start "rule__AttrList__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1364:1: rule__AttrList__Group__0 : rule__AttrList__Group__0__Impl rule__AttrList__Group__1 ;
    public final void rule__AttrList__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1368:1: ( rule__AttrList__Group__0__Impl rule__AttrList__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1369:2: rule__AttrList__Group__0__Impl rule__AttrList__Group__1
            {
            pushFollow(FOLLOW_rule__AttrList__Group__0__Impl_in_rule__AttrList__Group__02793);
            rule__AttrList__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__AttrList__Group__1_in_rule__AttrList__Group__02796);
            rule__AttrList__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__Group__0"


    // $ANTLR start "rule__AttrList__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1376:1: rule__AttrList__Group__0__Impl : ( '[' ) ;
    public final void rule__AttrList__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1380:1: ( ( '[' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1381:1: ( '[' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1381:1: ( '[' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1382:1: '['
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrListAccess().getLeftSquareBracketKeyword_0()); 
            }
            match(input,22,FOLLOW_22_in_rule__AttrList__Group__0__Impl2824); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrListAccess().getLeftSquareBracketKeyword_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__Group__0__Impl"


    // $ANTLR start "rule__AttrList__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1395:1: rule__AttrList__Group__1 : rule__AttrList__Group__1__Impl rule__AttrList__Group__2 ;
    public final void rule__AttrList__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1399:1: ( rule__AttrList__Group__1__Impl rule__AttrList__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1400:2: rule__AttrList__Group__1__Impl rule__AttrList__Group__2
            {
            pushFollow(FOLLOW_rule__AttrList__Group__1__Impl_in_rule__AttrList__Group__12855);
            rule__AttrList__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__AttrList__Group__2_in_rule__AttrList__Group__12858);
            rule__AttrList__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__Group__1"


    // $ANTLR start "rule__AttrList__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1407:1: rule__AttrList__Group__1__Impl : ( ( rule__AttrList__A_listAssignment_1 )* ) ;
    public final void rule__AttrList__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1411:1: ( ( ( rule__AttrList__A_listAssignment_1 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1412:1: ( ( rule__AttrList__A_listAssignment_1 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1412:1: ( ( rule__AttrList__A_listAssignment_1 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1413:1: ( rule__AttrList__A_listAssignment_1 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrListAccess().getA_listAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1414:1: ( rule__AttrList__A_listAssignment_1 )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==RULE_DOT_ID) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1414:2: rule__AttrList__A_listAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__AttrList__A_listAssignment_1_in_rule__AttrList__Group__1__Impl2885);
            	    rule__AttrList__A_listAssignment_1();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrListAccess().getA_listAssignment_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__Group__1__Impl"


    // $ANTLR start "rule__AttrList__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1424:1: rule__AttrList__Group__2 : rule__AttrList__Group__2__Impl ;
    public final void rule__AttrList__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1428:1: ( rule__AttrList__Group__2__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1429:2: rule__AttrList__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__AttrList__Group__2__Impl_in_rule__AttrList__Group__22916);
            rule__AttrList__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__Group__2"


    // $ANTLR start "rule__AttrList__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1435:1: rule__AttrList__Group__2__Impl : ( ']' ) ;
    public final void rule__AttrList__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1439:1: ( ( ']' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1440:1: ( ']' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1440:1: ( ']' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1441:1: ']'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrListAccess().getRightSquareBracketKeyword_2()); 
            }
            match(input,23,FOLLOW_23_in_rule__AttrList__Group__2__Impl2944); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrListAccess().getRightSquareBracketKeyword_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__Group__2__Impl"


    // $ANTLR start "rule__AList__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1460:1: rule__AList__Group__0 : rule__AList__Group__0__Impl rule__AList__Group__1 ;
    public final void rule__AList__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1464:1: ( rule__AList__Group__0__Impl rule__AList__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1465:2: rule__AList__Group__0__Impl rule__AList__Group__1
            {
            pushFollow(FOLLOW_rule__AList__Group__0__Impl_in_rule__AList__Group__02981);
            rule__AList__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__AList__Group__1_in_rule__AList__Group__02984);
            rule__AList__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group__0"


    // $ANTLR start "rule__AList__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1472:1: rule__AList__Group__0__Impl : ( ( rule__AList__NameAssignment_0 ) ) ;
    public final void rule__AList__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1476:1: ( ( ( rule__AList__NameAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1477:1: ( ( rule__AList__NameAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1477:1: ( ( rule__AList__NameAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1478:1: ( rule__AList__NameAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getNameAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1479:1: ( rule__AList__NameAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1479:2: rule__AList__NameAssignment_0
            {
            pushFollow(FOLLOW_rule__AList__NameAssignment_0_in_rule__AList__Group__0__Impl3011);
            rule__AList__NameAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getNameAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group__0__Impl"


    // $ANTLR start "rule__AList__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1489:1: rule__AList__Group__1 : rule__AList__Group__1__Impl rule__AList__Group__2 ;
    public final void rule__AList__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1493:1: ( rule__AList__Group__1__Impl rule__AList__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1494:2: rule__AList__Group__1__Impl rule__AList__Group__2
            {
            pushFollow(FOLLOW_rule__AList__Group__1__Impl_in_rule__AList__Group__13041);
            rule__AList__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__AList__Group__2_in_rule__AList__Group__13044);
            rule__AList__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group__1"


    // $ANTLR start "rule__AList__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1501:1: rule__AList__Group__1__Impl : ( ( rule__AList__Group_1__0 )? ) ;
    public final void rule__AList__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1505:1: ( ( ( rule__AList__Group_1__0 )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1506:1: ( ( rule__AList__Group_1__0 )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1506:1: ( ( rule__AList__Group_1__0 )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1507:1: ( rule__AList__Group_1__0 )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getGroup_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1508:1: ( rule__AList__Group_1__0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==21) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1508:2: rule__AList__Group_1__0
                    {
                    pushFollow(FOLLOW_rule__AList__Group_1__0_in_rule__AList__Group__1__Impl3071);
                    rule__AList__Group_1__0();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getGroup_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group__1__Impl"


    // $ANTLR start "rule__AList__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1518:1: rule__AList__Group__2 : rule__AList__Group__2__Impl ;
    public final void rule__AList__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1522:1: ( rule__AList__Group__2__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1523:2: rule__AList__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__AList__Group__2__Impl_in_rule__AList__Group__23102);
            rule__AList__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group__2"


    // $ANTLR start "rule__AList__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1529:1: rule__AList__Group__2__Impl : ( ( ',' )? ) ;
    public final void rule__AList__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1533:1: ( ( ( ',' )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1534:1: ( ( ',' )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1534:1: ( ( ',' )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1535:1: ( ',' )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getCommaKeyword_2()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1536:1: ( ',' )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==24) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1537:2: ','
                    {
                    match(input,24,FOLLOW_24_in_rule__AList__Group__2__Impl3131); if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getCommaKeyword_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group__2__Impl"


    // $ANTLR start "rule__AList__Group_1__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1554:1: rule__AList__Group_1__0 : rule__AList__Group_1__0__Impl rule__AList__Group_1__1 ;
    public final void rule__AList__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1558:1: ( rule__AList__Group_1__0__Impl rule__AList__Group_1__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1559:2: rule__AList__Group_1__0__Impl rule__AList__Group_1__1
            {
            pushFollow(FOLLOW_rule__AList__Group_1__0__Impl_in_rule__AList__Group_1__03170);
            rule__AList__Group_1__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__AList__Group_1__1_in_rule__AList__Group_1__03173);
            rule__AList__Group_1__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group_1__0"


    // $ANTLR start "rule__AList__Group_1__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1566:1: rule__AList__Group_1__0__Impl : ( '=' ) ;
    public final void rule__AList__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1570:1: ( ( '=' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1571:1: ( '=' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1571:1: ( '=' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1572:1: '='
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getEqualsSignKeyword_1_0()); 
            }
            match(input,21,FOLLOW_21_in_rule__AList__Group_1__0__Impl3201); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getEqualsSignKeyword_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group_1__0__Impl"


    // $ANTLR start "rule__AList__Group_1__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1585:1: rule__AList__Group_1__1 : rule__AList__Group_1__1__Impl ;
    public final void rule__AList__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1589:1: ( rule__AList__Group_1__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1590:2: rule__AList__Group_1__1__Impl
            {
            pushFollow(FOLLOW_rule__AList__Group_1__1__Impl_in_rule__AList__Group_1__13232);
            rule__AList__Group_1__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group_1__1"


    // $ANTLR start "rule__AList__Group_1__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1596:1: rule__AList__Group_1__1__Impl : ( ( rule__AList__ValueAssignment_1_1 ) ) ;
    public final void rule__AList__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1600:1: ( ( ( rule__AList__ValueAssignment_1_1 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1601:1: ( ( rule__AList__ValueAssignment_1_1 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1601:1: ( ( rule__AList__ValueAssignment_1_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1602:1: ( rule__AList__ValueAssignment_1_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getValueAssignment_1_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1603:1: ( rule__AList__ValueAssignment_1_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1603:2: rule__AList__ValueAssignment_1_1
            {
            pushFollow(FOLLOW_rule__AList__ValueAssignment_1_1_in_rule__AList__Group_1__1__Impl3259);
            rule__AList__ValueAssignment_1_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getValueAssignment_1_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__Group_1__1__Impl"


    // $ANTLR start "rule__Subgraph__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1617:1: rule__Subgraph__Group__0 : rule__Subgraph__Group__0__Impl rule__Subgraph__Group__1 ;
    public final void rule__Subgraph__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1621:1: ( rule__Subgraph__Group__0__Impl rule__Subgraph__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1622:2: rule__Subgraph__Group__0__Impl rule__Subgraph__Group__1
            {
            pushFollow(FOLLOW_rule__Subgraph__Group__0__Impl_in_rule__Subgraph__Group__03293);
            rule__Subgraph__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Subgraph__Group__1_in_rule__Subgraph__Group__03296);
            rule__Subgraph__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__0"


    // $ANTLR start "rule__Subgraph__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1629:1: rule__Subgraph__Group__0__Impl : ( ( rule__Subgraph__Group_0__0 )? ) ;
    public final void rule__Subgraph__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1633:1: ( ( ( rule__Subgraph__Group_0__0 )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1634:1: ( ( rule__Subgraph__Group_0__0 )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1634:1: ( ( rule__Subgraph__Group_0__0 )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1635:1: ( rule__Subgraph__Group_0__0 )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getGroup_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1636:1: ( rule__Subgraph__Group_0__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==25) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1636:2: rule__Subgraph__Group_0__0
                    {
                    pushFollow(FOLLOW_rule__Subgraph__Group_0__0_in_rule__Subgraph__Group__0__Impl3323);
                    rule__Subgraph__Group_0__0();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getGroup_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__0__Impl"


    // $ANTLR start "rule__Subgraph__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1646:1: rule__Subgraph__Group__1 : rule__Subgraph__Group__1__Impl rule__Subgraph__Group__2 ;
    public final void rule__Subgraph__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1650:1: ( rule__Subgraph__Group__1__Impl rule__Subgraph__Group__2 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1651:2: rule__Subgraph__Group__1__Impl rule__Subgraph__Group__2
            {
            pushFollow(FOLLOW_rule__Subgraph__Group__1__Impl_in_rule__Subgraph__Group__13354);
            rule__Subgraph__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Subgraph__Group__2_in_rule__Subgraph__Group__13357);
            rule__Subgraph__Group__2();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__1"


    // $ANTLR start "rule__Subgraph__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1658:1: rule__Subgraph__Group__1__Impl : ( '{' ) ;
    public final void rule__Subgraph__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1662:1: ( ( '{' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1663:1: ( '{' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1663:1: ( '{' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1664:1: '{'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getLeftCurlyBracketKeyword_1()); 
            }
            match(input,18,FOLLOW_18_in_rule__Subgraph__Group__1__Impl3385); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getLeftCurlyBracketKeyword_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__1__Impl"


    // $ANTLR start "rule__Subgraph__Group__2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1677:1: rule__Subgraph__Group__2 : rule__Subgraph__Group__2__Impl rule__Subgraph__Group__3 ;
    public final void rule__Subgraph__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1681:1: ( rule__Subgraph__Group__2__Impl rule__Subgraph__Group__3 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1682:2: rule__Subgraph__Group__2__Impl rule__Subgraph__Group__3
            {
            pushFollow(FOLLOW_rule__Subgraph__Group__2__Impl_in_rule__Subgraph__Group__23416);
            rule__Subgraph__Group__2__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Subgraph__Group__3_in_rule__Subgraph__Group__23419);
            rule__Subgraph__Group__3();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__2"


    // $ANTLR start "rule__Subgraph__Group__2__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1689:1: rule__Subgraph__Group__2__Impl : ( ( rule__Subgraph__StmtsAssignment_2 )* ) ;
    public final void rule__Subgraph__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1693:1: ( ( ( rule__Subgraph__StmtsAssignment_2 )* ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1694:1: ( ( rule__Subgraph__StmtsAssignment_2 )* )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1694:1: ( ( rule__Subgraph__StmtsAssignment_2 )* )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1695:1: ( rule__Subgraph__StmtsAssignment_2 )*
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getStmtsAssignment_2()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1696:1: ( rule__Subgraph__StmtsAssignment_2 )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==RULE_DOT_ID||LA21_0==14||(LA21_0>=16 && LA21_0<=18)||LA21_0==25) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1696:2: rule__Subgraph__StmtsAssignment_2
            	    {
            	    pushFollow(FOLLOW_rule__Subgraph__StmtsAssignment_2_in_rule__Subgraph__Group__2__Impl3446);
            	    rule__Subgraph__StmtsAssignment_2();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getStmtsAssignment_2()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__2__Impl"


    // $ANTLR start "rule__Subgraph__Group__3"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1706:1: rule__Subgraph__Group__3 : rule__Subgraph__Group__3__Impl ;
    public final void rule__Subgraph__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1710:1: ( rule__Subgraph__Group__3__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1711:2: rule__Subgraph__Group__3__Impl
            {
            pushFollow(FOLLOW_rule__Subgraph__Group__3__Impl_in_rule__Subgraph__Group__33477);
            rule__Subgraph__Group__3__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__3"


    // $ANTLR start "rule__Subgraph__Group__3__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1717:1: rule__Subgraph__Group__3__Impl : ( '}' ) ;
    public final void rule__Subgraph__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1721:1: ( ( '}' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1722:1: ( '}' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1722:1: ( '}' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1723:1: '}'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getRightCurlyBracketKeyword_3()); 
            }
            match(input,19,FOLLOW_19_in_rule__Subgraph__Group__3__Impl3505); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getRightCurlyBracketKeyword_3()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group__3__Impl"


    // $ANTLR start "rule__Subgraph__Group_0__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1744:1: rule__Subgraph__Group_0__0 : rule__Subgraph__Group_0__0__Impl rule__Subgraph__Group_0__1 ;
    public final void rule__Subgraph__Group_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1748:1: ( rule__Subgraph__Group_0__0__Impl rule__Subgraph__Group_0__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1749:2: rule__Subgraph__Group_0__0__Impl rule__Subgraph__Group_0__1
            {
            pushFollow(FOLLOW_rule__Subgraph__Group_0__0__Impl_in_rule__Subgraph__Group_0__03544);
            rule__Subgraph__Group_0__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__Subgraph__Group_0__1_in_rule__Subgraph__Group_0__03547);
            rule__Subgraph__Group_0__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group_0__0"


    // $ANTLR start "rule__Subgraph__Group_0__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1756:1: rule__Subgraph__Group_0__0__Impl : ( 'subgraph' ) ;
    public final void rule__Subgraph__Group_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1760:1: ( ( 'subgraph' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1761:1: ( 'subgraph' )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1761:1: ( 'subgraph' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1762:1: 'subgraph'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getSubgraphKeyword_0_0()); 
            }
            match(input,25,FOLLOW_25_in_rule__Subgraph__Group_0__0__Impl3575); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getSubgraphKeyword_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group_0__0__Impl"


    // $ANTLR start "rule__Subgraph__Group_0__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1775:1: rule__Subgraph__Group_0__1 : rule__Subgraph__Group_0__1__Impl ;
    public final void rule__Subgraph__Group_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1779:1: ( rule__Subgraph__Group_0__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1780:2: rule__Subgraph__Group_0__1__Impl
            {
            pushFollow(FOLLOW_rule__Subgraph__Group_0__1__Impl_in_rule__Subgraph__Group_0__13606);
            rule__Subgraph__Group_0__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group_0__1"


    // $ANTLR start "rule__Subgraph__Group_0__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1786:1: rule__Subgraph__Group_0__1__Impl : ( ( rule__Subgraph__NameAssignment_0_1 )? ) ;
    public final void rule__Subgraph__Group_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1790:1: ( ( ( rule__Subgraph__NameAssignment_0_1 )? ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1791:1: ( ( rule__Subgraph__NameAssignment_0_1 )? )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1791:1: ( ( rule__Subgraph__NameAssignment_0_1 )? )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1792:1: ( rule__Subgraph__NameAssignment_0_1 )?
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getNameAssignment_0_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1793:1: ( rule__Subgraph__NameAssignment_0_1 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==RULE_DOT_ID) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1793:2: rule__Subgraph__NameAssignment_0_1
                    {
                    pushFollow(FOLLOW_rule__Subgraph__NameAssignment_0_1_in_rule__Subgraph__Group_0__1__Impl3633);
                    rule__Subgraph__NameAssignment_0_1();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getNameAssignment_0_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__Group_0__1__Impl"


    // $ANTLR start "rule__EdgeRhsNode__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1807:1: rule__EdgeRhsNode__Group__0 : rule__EdgeRhsNode__Group__0__Impl rule__EdgeRhsNode__Group__1 ;
    public final void rule__EdgeRhsNode__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1811:1: ( rule__EdgeRhsNode__Group__0__Impl rule__EdgeRhsNode__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1812:2: rule__EdgeRhsNode__Group__0__Impl rule__EdgeRhsNode__Group__1
            {
            pushFollow(FOLLOW_rule__EdgeRhsNode__Group__0__Impl_in_rule__EdgeRhsNode__Group__03668);
            rule__EdgeRhsNode__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__EdgeRhsNode__Group__1_in_rule__EdgeRhsNode__Group__03671);
            rule__EdgeRhsNode__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsNode__Group__0"


    // $ANTLR start "rule__EdgeRhsNode__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1819:1: rule__EdgeRhsNode__Group__0__Impl : ( ( rule__EdgeRhsNode__OpAssignment_0 ) ) ;
    public final void rule__EdgeRhsNode__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1823:1: ( ( ( rule__EdgeRhsNode__OpAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1824:1: ( ( rule__EdgeRhsNode__OpAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1824:1: ( ( rule__EdgeRhsNode__OpAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1825:1: ( rule__EdgeRhsNode__OpAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsNodeAccess().getOpAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1826:1: ( rule__EdgeRhsNode__OpAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1826:2: rule__EdgeRhsNode__OpAssignment_0
            {
            pushFollow(FOLLOW_rule__EdgeRhsNode__OpAssignment_0_in_rule__EdgeRhsNode__Group__0__Impl3698);
            rule__EdgeRhsNode__OpAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsNodeAccess().getOpAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsNode__Group__0__Impl"


    // $ANTLR start "rule__EdgeRhsNode__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1836:1: rule__EdgeRhsNode__Group__1 : rule__EdgeRhsNode__Group__1__Impl ;
    public final void rule__EdgeRhsNode__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1840:1: ( rule__EdgeRhsNode__Group__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1841:2: rule__EdgeRhsNode__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__EdgeRhsNode__Group__1__Impl_in_rule__EdgeRhsNode__Group__13728);
            rule__EdgeRhsNode__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsNode__Group__1"


    // $ANTLR start "rule__EdgeRhsNode__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1847:1: rule__EdgeRhsNode__Group__1__Impl : ( ( rule__EdgeRhsNode__NodeAssignment_1 ) ) ;
    public final void rule__EdgeRhsNode__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1851:1: ( ( ( rule__EdgeRhsNode__NodeAssignment_1 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1852:1: ( ( rule__EdgeRhsNode__NodeAssignment_1 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1852:1: ( ( rule__EdgeRhsNode__NodeAssignment_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1853:1: ( rule__EdgeRhsNode__NodeAssignment_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsNodeAccess().getNodeAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1854:1: ( rule__EdgeRhsNode__NodeAssignment_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1854:2: rule__EdgeRhsNode__NodeAssignment_1
            {
            pushFollow(FOLLOW_rule__EdgeRhsNode__NodeAssignment_1_in_rule__EdgeRhsNode__Group__1__Impl3755);
            rule__EdgeRhsNode__NodeAssignment_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsNodeAccess().getNodeAssignment_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsNode__Group__1__Impl"


    // $ANTLR start "rule__EdgeRhsSubgraph__Group__0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1868:1: rule__EdgeRhsSubgraph__Group__0 : rule__EdgeRhsSubgraph__Group__0__Impl rule__EdgeRhsSubgraph__Group__1 ;
    public final void rule__EdgeRhsSubgraph__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1872:1: ( rule__EdgeRhsSubgraph__Group__0__Impl rule__EdgeRhsSubgraph__Group__1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1873:2: rule__EdgeRhsSubgraph__Group__0__Impl rule__EdgeRhsSubgraph__Group__1
            {
            pushFollow(FOLLOW_rule__EdgeRhsSubgraph__Group__0__Impl_in_rule__EdgeRhsSubgraph__Group__03789);
            rule__EdgeRhsSubgraph__Group__0__Impl();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_rule__EdgeRhsSubgraph__Group__1_in_rule__EdgeRhsSubgraph__Group__03792);
            rule__EdgeRhsSubgraph__Group__1();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsSubgraph__Group__0"


    // $ANTLR start "rule__EdgeRhsSubgraph__Group__0__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1880:1: rule__EdgeRhsSubgraph__Group__0__Impl : ( ( rule__EdgeRhsSubgraph__OpAssignment_0 ) ) ;
    public final void rule__EdgeRhsSubgraph__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1884:1: ( ( ( rule__EdgeRhsSubgraph__OpAssignment_0 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1885:1: ( ( rule__EdgeRhsSubgraph__OpAssignment_0 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1885:1: ( ( rule__EdgeRhsSubgraph__OpAssignment_0 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1886:1: ( rule__EdgeRhsSubgraph__OpAssignment_0 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsSubgraphAccess().getOpAssignment_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1887:1: ( rule__EdgeRhsSubgraph__OpAssignment_0 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1887:2: rule__EdgeRhsSubgraph__OpAssignment_0
            {
            pushFollow(FOLLOW_rule__EdgeRhsSubgraph__OpAssignment_0_in_rule__EdgeRhsSubgraph__Group__0__Impl3819);
            rule__EdgeRhsSubgraph__OpAssignment_0();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsSubgraphAccess().getOpAssignment_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsSubgraph__Group__0__Impl"


    // $ANTLR start "rule__EdgeRhsSubgraph__Group__1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1897:1: rule__EdgeRhsSubgraph__Group__1 : rule__EdgeRhsSubgraph__Group__1__Impl ;
    public final void rule__EdgeRhsSubgraph__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1901:1: ( rule__EdgeRhsSubgraph__Group__1__Impl )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1902:2: rule__EdgeRhsSubgraph__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__EdgeRhsSubgraph__Group__1__Impl_in_rule__EdgeRhsSubgraph__Group__13849);
            rule__EdgeRhsSubgraph__Group__1__Impl();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsSubgraph__Group__1"


    // $ANTLR start "rule__EdgeRhsSubgraph__Group__1__Impl"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1908:1: rule__EdgeRhsSubgraph__Group__1__Impl : ( ( rule__EdgeRhsSubgraph__SubgraphAssignment_1 ) ) ;
    public final void rule__EdgeRhsSubgraph__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1912:1: ( ( ( rule__EdgeRhsSubgraph__SubgraphAssignment_1 ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1913:1: ( ( rule__EdgeRhsSubgraph__SubgraphAssignment_1 ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1913:1: ( ( rule__EdgeRhsSubgraph__SubgraphAssignment_1 ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1914:1: ( rule__EdgeRhsSubgraph__SubgraphAssignment_1 )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsSubgraphAccess().getSubgraphAssignment_1()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1915:1: ( rule__EdgeRhsSubgraph__SubgraphAssignment_1 )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1915:2: rule__EdgeRhsSubgraph__SubgraphAssignment_1
            {
            pushFollow(FOLLOW_rule__EdgeRhsSubgraph__SubgraphAssignment_1_in_rule__EdgeRhsSubgraph__Group__1__Impl3876);
            rule__EdgeRhsSubgraph__SubgraphAssignment_1();

            state._fsp--;
            if (state.failed) return ;

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsSubgraphAccess().getSubgraphAssignment_1()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsSubgraph__Group__1__Impl"


    // $ANTLR start "rule__GraphvizModel__GraphsAssignment"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1930:1: rule__GraphvizModel__GraphsAssignment : ( ruleMainGraph ) ;
    public final void rule__GraphvizModel__GraphsAssignment() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1934:1: ( ( ruleMainGraph ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1935:1: ( ruleMainGraph )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1935:1: ( ruleMainGraph )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1936:1: ruleMainGraph
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getGraphvizModelAccess().getGraphsMainGraphParserRuleCall_0()); 
            }
            pushFollow(FOLLOW_ruleMainGraph_in_rule__GraphvizModel__GraphsAssignment3915);
            ruleMainGraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getGraphvizModelAccess().getGraphsMainGraphParserRuleCall_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__GraphvizModel__GraphsAssignment"


    // $ANTLR start "rule__MainGraph__StrictAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1945:1: rule__MainGraph__StrictAssignment_0 : ( ( 'strict' ) ) ;
    public final void rule__MainGraph__StrictAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1949:1: ( ( ( 'strict' ) ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1950:1: ( ( 'strict' ) )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1950:1: ( ( 'strict' ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1951:1: ( 'strict' )
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getStrictStrictKeyword_0_0()); 
            }
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1952:1: ( 'strict' )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1953:1: 'strict'
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getStrictStrictKeyword_0_0()); 
            }
            match(input,26,FOLLOW_26_in_rule__MainGraph__StrictAssignment_03951); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getStrictStrictKeyword_0_0()); 
            }

            }

            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getStrictStrictKeyword_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__StrictAssignment_0"


    // $ANTLR start "rule__MainGraph__TypeAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1968:1: rule__MainGraph__TypeAssignment_1 : ( ruleGraphType ) ;
    public final void rule__MainGraph__TypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1972:1: ( ( ruleGraphType ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1973:1: ( ruleGraphType )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1973:1: ( ruleGraphType )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1974:1: ruleGraphType
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getTypeGraphTypeEnumRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleGraphType_in_rule__MainGraph__TypeAssignment_13990);
            ruleGraphType();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getTypeGraphTypeEnumRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__TypeAssignment_1"


    // $ANTLR start "rule__MainGraph__NameAssignment_2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1983:1: rule__MainGraph__NameAssignment_2 : ( RULE_DOT_ID ) ;
    public final void rule__MainGraph__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1987:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1988:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1988:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1989:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getNameDOT_IDTerminalRuleCall_2_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__MainGraph__NameAssignment_24021); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getNameDOT_IDTerminalRuleCall_2_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__NameAssignment_2"


    // $ANTLR start "rule__MainGraph__StmtsAssignment_4"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:1998:1: rule__MainGraph__StmtsAssignment_4 : ( ruleStmt ) ;
    public final void rule__MainGraph__StmtsAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2002:1: ( ( ruleStmt ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2003:1: ( ruleStmt )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2003:1: ( ruleStmt )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2004:1: ruleStmt
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getMainGraphAccess().getStmtsStmtParserRuleCall_4_0()); 
            }
            pushFollow(FOLLOW_ruleStmt_in_rule__MainGraph__StmtsAssignment_44052);
            ruleStmt();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getMainGraphAccess().getStmtsStmtParserRuleCall_4_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__MainGraph__StmtsAssignment_4"


    // $ANTLR start "rule__EdgeStmtNode__Node_idAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2013:1: rule__EdgeStmtNode__Node_idAssignment_0 : ( ruleNodeId ) ;
    public final void rule__EdgeStmtNode__Node_idAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2017:1: ( ( ruleNodeId ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2018:1: ( ruleNodeId )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2018:1: ( ruleNodeId )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2019:1: ruleNodeId
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getNode_idNodeIdParserRuleCall_0_0()); 
            }
            pushFollow(FOLLOW_ruleNodeId_in_rule__EdgeStmtNode__Node_idAssignment_04083);
            ruleNodeId();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getNode_idNodeIdParserRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__Node_idAssignment_0"


    // $ANTLR start "rule__EdgeStmtNode__EdgeRHSAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2028:1: rule__EdgeStmtNode__EdgeRHSAssignment_1 : ( ruleEdgeRhs ) ;
    public final void rule__EdgeStmtNode__EdgeRHSAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2032:1: ( ( ruleEdgeRhs ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2033:1: ( ruleEdgeRhs )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2033:1: ( ruleEdgeRhs )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2034:1: ruleEdgeRhs
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhs_in_rule__EdgeStmtNode__EdgeRHSAssignment_14114);
            ruleEdgeRhs();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__EdgeRHSAssignment_1"


    // $ANTLR start "rule__EdgeStmtNode__AttributesAssignment_2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2043:1: rule__EdgeStmtNode__AttributesAssignment_2 : ( ruleAttrList ) ;
    public final void rule__EdgeStmtNode__AttributesAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2047:1: ( ( ruleAttrList ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2048:1: ( ruleAttrList )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2048:1: ( ruleAttrList )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2049:1: ruleAttrList
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtNodeAccess().getAttributesAttrListParserRuleCall_2_0()); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_rule__EdgeStmtNode__AttributesAssignment_24145);
            ruleAttrList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtNodeAccess().getAttributesAttrListParserRuleCall_2_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtNode__AttributesAssignment_2"


    // $ANTLR start "rule__EdgeStmtSubgraph__SubgraphAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2058:1: rule__EdgeStmtSubgraph__SubgraphAssignment_0 : ( ruleSubgraph ) ;
    public final void rule__EdgeStmtSubgraph__SubgraphAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2062:1: ( ( ruleSubgraph ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2063:1: ( ruleSubgraph )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2063:1: ( ruleSubgraph )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2064:1: ruleSubgraph
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getSubgraphSubgraphParserRuleCall_0_0()); 
            }
            pushFollow(FOLLOW_ruleSubgraph_in_rule__EdgeStmtSubgraph__SubgraphAssignment_04176);
            ruleSubgraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getSubgraphSubgraphParserRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__SubgraphAssignment_0"


    // $ANTLR start "rule__EdgeStmtSubgraph__EdgeRHSAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2073:1: rule__EdgeStmtSubgraph__EdgeRHSAssignment_1 : ( ruleEdgeRhs ) ;
    public final void rule__EdgeStmtSubgraph__EdgeRHSAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2077:1: ( ( ruleEdgeRhs ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2078:1: ( ruleEdgeRhs )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2078:1: ( ruleEdgeRhs )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2079:1: ruleEdgeRhs
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleEdgeRhs_in_rule__EdgeStmtSubgraph__EdgeRHSAssignment_14207);
            ruleEdgeRhs();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getEdgeRHSEdgeRhsParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__EdgeRHSAssignment_1"


    // $ANTLR start "rule__EdgeStmtSubgraph__AttributesAssignment_2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2088:1: rule__EdgeStmtSubgraph__AttributesAssignment_2 : ( ruleAttrList ) ;
    public final void rule__EdgeStmtSubgraph__AttributesAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2092:1: ( ( ruleAttrList ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2093:1: ( ruleAttrList )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2093:1: ( ruleAttrList )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2094:1: ruleAttrList
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeStmtSubgraphAccess().getAttributesAttrListParserRuleCall_2_0()); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_rule__EdgeStmtSubgraph__AttributesAssignment_24238);
            ruleAttrList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeStmtSubgraphAccess().getAttributesAttrListParserRuleCall_2_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeStmtSubgraph__AttributesAssignment_2"


    // $ANTLR start "rule__NodeStmt__NameAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2103:1: rule__NodeStmt__NameAssignment_0 : ( RULE_DOT_ID ) ;
    public final void rule__NodeStmt__NameAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2107:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2108:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2108:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2109:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeStmtAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__NodeStmt__NameAssignment_04269); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeStmtAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeStmt__NameAssignment_0"


    // $ANTLR start "rule__NodeStmt__AttributesAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2118:1: rule__NodeStmt__AttributesAssignment_1 : ( ruleAttrList ) ;
    public final void rule__NodeStmt__AttributesAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2122:1: ( ( ruleAttrList ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2123:1: ( ruleAttrList )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2123:1: ( ruleAttrList )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2124:1: ruleAttrList
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeStmtAccess().getAttributesAttrListParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_rule__NodeStmt__AttributesAssignment_14300);
            ruleAttrList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeStmtAccess().getAttributesAttrListParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeStmt__AttributesAssignment_1"


    // $ANTLR start "rule__Attribute__NameAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2133:1: rule__Attribute__NameAssignment_0 : ( RULE_DOT_ID ) ;
    public final void rule__Attribute__NameAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2137:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2138:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2138:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2139:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__Attribute__NameAssignment_04331); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__NameAssignment_0"


    // $ANTLR start "rule__Attribute__ValueAssignment_2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2148:1: rule__Attribute__ValueAssignment_2 : ( RULE_DOT_ID ) ;
    public final void rule__Attribute__ValueAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2152:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2153:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2153:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2154:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttributeAccess().getValueDOT_IDTerminalRuleCall_2_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__Attribute__ValueAssignment_24362); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttributeAccess().getValueDOT_IDTerminalRuleCall_2_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Attribute__ValueAssignment_2"


    // $ANTLR start "rule__AttrStmt__TypeAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2163:1: rule__AttrStmt__TypeAssignment_0 : ( ruleAttributeType ) ;
    public final void rule__AttrStmt__TypeAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2167:1: ( ( ruleAttributeType ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2168:1: ( ruleAttributeType )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2168:1: ( ruleAttributeType )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2169:1: ruleAttributeType
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtAccess().getTypeAttributeTypeEnumRuleCall_0_0()); 
            }
            pushFollow(FOLLOW_ruleAttributeType_in_rule__AttrStmt__TypeAssignment_04393);
            ruleAttributeType();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtAccess().getTypeAttributeTypeEnumRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrStmt__TypeAssignment_0"


    // $ANTLR start "rule__AttrStmt__AttributesAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2178:1: rule__AttrStmt__AttributesAssignment_1 : ( ruleAttrList ) ;
    public final void rule__AttrStmt__AttributesAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2182:1: ( ( ruleAttrList ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2183:1: ( ruleAttrList )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2183:1: ( ruleAttrList )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2184:1: ruleAttrList
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrStmtAccess().getAttributesAttrListParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleAttrList_in_rule__AttrStmt__AttributesAssignment_14424);
            ruleAttrList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrStmtAccess().getAttributesAttrListParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrStmt__AttributesAssignment_1"


    // $ANTLR start "rule__AttrList__A_listAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2193:1: rule__AttrList__A_listAssignment_1 : ( ruleAList ) ;
    public final void rule__AttrList__A_listAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2197:1: ( ( ruleAList ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2198:1: ( ruleAList )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2198:1: ( ruleAList )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2199:1: ruleAList
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAttrListAccess().getA_listAListParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleAList_in_rule__AttrList__A_listAssignment_14455);
            ruleAList();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAttrListAccess().getA_listAListParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttrList__A_listAssignment_1"


    // $ANTLR start "rule__AList__NameAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2208:1: rule__AList__NameAssignment_0 : ( RULE_DOT_ID ) ;
    public final void rule__AList__NameAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2212:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2213:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2213:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2214:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__AList__NameAssignment_04486); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getNameDOT_IDTerminalRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__NameAssignment_0"


    // $ANTLR start "rule__AList__ValueAssignment_1_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2223:1: rule__AList__ValueAssignment_1_1 : ( RULE_DOT_ID ) ;
    public final void rule__AList__ValueAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2227:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2228:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2228:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2229:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getAListAccess().getValueDOT_IDTerminalRuleCall_1_1_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__AList__ValueAssignment_1_14517); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getAListAccess().getValueDOT_IDTerminalRuleCall_1_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AList__ValueAssignment_1_1"


    // $ANTLR start "rule__Subgraph__NameAssignment_0_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2238:1: rule__Subgraph__NameAssignment_0_1 : ( RULE_DOT_ID ) ;
    public final void rule__Subgraph__NameAssignment_0_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2242:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2243:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2243:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2244:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getNameDOT_IDTerminalRuleCall_0_1_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__Subgraph__NameAssignment_0_14548); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getNameDOT_IDTerminalRuleCall_0_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__NameAssignment_0_1"


    // $ANTLR start "rule__Subgraph__StmtsAssignment_2"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2253:1: rule__Subgraph__StmtsAssignment_2 : ( ruleStmt ) ;
    public final void rule__Subgraph__StmtsAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2257:1: ( ( ruleStmt ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2258:1: ( ruleStmt )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2258:1: ( ruleStmt )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2259:1: ruleStmt
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getSubgraphAccess().getStmtsStmtParserRuleCall_2_0()); 
            }
            pushFollow(FOLLOW_ruleStmt_in_rule__Subgraph__StmtsAssignment_24579);
            ruleStmt();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getSubgraphAccess().getStmtsStmtParserRuleCall_2_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Subgraph__StmtsAssignment_2"


    // $ANTLR start "rule__EdgeRhsNode__OpAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2268:1: rule__EdgeRhsNode__OpAssignment_0 : ( ruleEdgeOp ) ;
    public final void rule__EdgeRhsNode__OpAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2272:1: ( ( ruleEdgeOp ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2273:1: ( ruleEdgeOp )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2273:1: ( ruleEdgeOp )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2274:1: ruleEdgeOp
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsNodeAccess().getOpEdgeOpEnumRuleCall_0_0()); 
            }
            pushFollow(FOLLOW_ruleEdgeOp_in_rule__EdgeRhsNode__OpAssignment_04610);
            ruleEdgeOp();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsNodeAccess().getOpEdgeOpEnumRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsNode__OpAssignment_0"


    // $ANTLR start "rule__EdgeRhsNode__NodeAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2283:1: rule__EdgeRhsNode__NodeAssignment_1 : ( ruleNodeId ) ;
    public final void rule__EdgeRhsNode__NodeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2287:1: ( ( ruleNodeId ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2288:1: ( ruleNodeId )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2288:1: ( ruleNodeId )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2289:1: ruleNodeId
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsNodeAccess().getNodeNodeIdParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleNodeId_in_rule__EdgeRhsNode__NodeAssignment_14641);
            ruleNodeId();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsNodeAccess().getNodeNodeIdParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsNode__NodeAssignment_1"


    // $ANTLR start "rule__EdgeRhsSubgraph__OpAssignment_0"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2298:1: rule__EdgeRhsSubgraph__OpAssignment_0 : ( ruleEdgeOp ) ;
    public final void rule__EdgeRhsSubgraph__OpAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2302:1: ( ( ruleEdgeOp ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2303:1: ( ruleEdgeOp )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2303:1: ( ruleEdgeOp )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2304:1: ruleEdgeOp
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsSubgraphAccess().getOpEdgeOpEnumRuleCall_0_0()); 
            }
            pushFollow(FOLLOW_ruleEdgeOp_in_rule__EdgeRhsSubgraph__OpAssignment_04672);
            ruleEdgeOp();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsSubgraphAccess().getOpEdgeOpEnumRuleCall_0_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsSubgraph__OpAssignment_0"


    // $ANTLR start "rule__EdgeRhsSubgraph__SubgraphAssignment_1"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2313:1: rule__EdgeRhsSubgraph__SubgraphAssignment_1 : ( ruleSubgraph ) ;
    public final void rule__EdgeRhsSubgraph__SubgraphAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2317:1: ( ( ruleSubgraph ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2318:1: ( ruleSubgraph )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2318:1: ( ruleSubgraph )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2319:1: ruleSubgraph
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getEdgeRhsSubgraphAccess().getSubgraphSubgraphParserRuleCall_1_0()); 
            }
            pushFollow(FOLLOW_ruleSubgraph_in_rule__EdgeRhsSubgraph__SubgraphAssignment_14703);
            ruleSubgraph();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getEdgeRhsSubgraphAccess().getSubgraphSubgraphParserRuleCall_1_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeRhsSubgraph__SubgraphAssignment_1"


    // $ANTLR start "rule__NodeId__NameAssignment"
    // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2328:1: rule__NodeId__NameAssignment : ( RULE_DOT_ID ) ;
    public final void rule__NodeId__NameAssignment() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2332:1: ( ( RULE_DOT_ID ) )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2333:1: ( RULE_DOT_ID )
            {
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2333:1: ( RULE_DOT_ID )
            // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:2334:1: RULE_DOT_ID
            {
            if ( state.backtracking==0 ) {
               before(grammarAccess.getNodeIdAccess().getNameDOT_IDTerminalRuleCall_0()); 
            }
            match(input,RULE_DOT_ID,FOLLOW_RULE_DOT_ID_in_rule__NodeId__NameAssignment4734); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               after(grammarAccess.getNodeIdAccess().getNameDOT_IDTerminalRuleCall_0()); 
            }

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__NodeId__NameAssignment"

    // $ANTLR start synpred2_InternalDot
    public final void synpred2_InternalDot_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:546:1: ( ( ruleAttribute ) )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:546:1: ( ruleAttribute )
        {
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:546:1: ( ruleAttribute )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:547:1: ruleAttribute
        {
        if ( state.backtracking==0 ) {
           before(grammarAccess.getStmtAccess().getAttributeParserRuleCall_0_0()); 
        }
        pushFollow(FOLLOW_ruleAttribute_in_synpred2_InternalDot1087);
        ruleAttribute();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred2_InternalDot

    // $ANTLR start synpred3_InternalDot
    public final void synpred3_InternalDot_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:552:6: ( ( ruleEdgeStmtNode ) )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:552:6: ( ruleEdgeStmtNode )
        {
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:552:6: ( ruleEdgeStmtNode )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:553:1: ruleEdgeStmtNode
        {
        if ( state.backtracking==0 ) {
           before(grammarAccess.getStmtAccess().getEdgeStmtNodeParserRuleCall_0_1()); 
        }
        pushFollow(FOLLOW_ruleEdgeStmtNode_in_synpred3_InternalDot1104);
        ruleEdgeStmtNode();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred3_InternalDot

    // $ANTLR start synpred4_InternalDot
    public final void synpred4_InternalDot_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:558:6: ( ( ruleEdgeStmtSubgraph ) )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:558:6: ( ruleEdgeStmtSubgraph )
        {
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:558:6: ( ruleEdgeStmtSubgraph )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:559:1: ruleEdgeStmtSubgraph
        {
        if ( state.backtracking==0 ) {
           before(grammarAccess.getStmtAccess().getEdgeStmtSubgraphParserRuleCall_0_2()); 
        }
        pushFollow(FOLLOW_ruleEdgeStmtSubgraph_in_synpred4_InternalDot1121);
        ruleEdgeStmtSubgraph();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred4_InternalDot

    // $ANTLR start synpred5_InternalDot
    public final void synpred5_InternalDot_fragment() throws RecognitionException {   
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:564:6: ( ( ruleNodeStmt ) )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:564:6: ( ruleNodeStmt )
        {
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:564:6: ( ruleNodeStmt )
        // ../org.eclipse.zest.dot.ui/src-gen/org/eclipse/zest/internal/dot/parser/ui/contentassist/antlr/internal/InternalDot.g:565:1: ruleNodeStmt
        {
        if ( state.backtracking==0 ) {
           before(grammarAccess.getStmtAccess().getNodeStmtParserRuleCall_0_3()); 
        }
        pushFollow(FOLLOW_ruleNodeStmt_in_synpred5_InternalDot1138);
        ruleNodeStmt();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred5_InternalDot

    // Delegated rules

    public final boolean synpred2_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_InternalDot_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_InternalDot_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_InternalDot() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_InternalDot_fragment(); // can never throw exception
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


    protected DFA2 dfa2 = new DFA2(this);
    static final String DFA2_eotS =
        "\14\uffff";
    static final String DFA2_eofS =
        "\14\uffff";
    static final String DFA2_minS =
        "\1\4\3\0\10\uffff";
    static final String DFA2_maxS =
        "\1\31\3\0\10\uffff";
    static final String DFA2_acceptS =
        "\4\uffff\1\5\2\uffff\1\1\1\2\1\4\1\3\1\6";
    static final String DFA2_specialS =
        "\1\uffff\1\0\1\1\1\2\10\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\1\11\uffff\1\4\1\uffff\2\4\1\3\6\uffff\1\2",
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

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "541:1: rule__Stmt__Alternatives_0 : ( ( ruleAttribute ) | ( ruleEdgeStmtNode ) | ( ruleEdgeStmtSubgraph ) | ( ruleNodeStmt ) | ( ruleAttrStmt ) | ( ruleSubgraph ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA2_1 = input.LA(1);

                         
                        int index2_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_InternalDot()) ) {s = 7;}

                        else if ( (synpred3_InternalDot()) ) {s = 8;}

                        else if ( (synpred5_InternalDot()) ) {s = 9;}

                         
                        input.seek(index2_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA2_2 = input.LA(1);

                         
                        int index2_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_InternalDot()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index2_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA2_3 = input.LA(1);

                         
                        int index2_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_InternalDot()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index2_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 2, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel67 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGraphvizModel74 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__GraphvizModel__GraphsAssignment_in_ruleGraphvizModel100 = new BitSet(new long[]{0x000000000400C002L});
    public static final BitSet FOLLOW_ruleMainGraph_in_entryRuleMainGraph128 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMainGraph135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__0_in_ruleMainGraph161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStmt_in_entryRuleStmt188 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStmt195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Stmt__Group__0_in_ruleStmt221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_entryRuleEdgeStmtNode248 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStmtNode255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Group__0_in_ruleEdgeStmtNode281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_entryRuleEdgeStmtSubgraph308 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStmtSubgraph315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__Group__0_in_ruleEdgeStmtSubgraph341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_entryRuleNodeStmt368 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeStmt375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__NodeStmt__Group__0_in_ruleNodeStmt401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute428 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Attribute__Group__0_in_ruleAttribute461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrStmt_in_entryRuleAttrStmt488 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrStmt495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrStmt__Group__0_in_ruleAttrStmt521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrList_in_entryRuleAttrList548 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttrList555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrList__Group__0_in_ruleAttrList581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAList_in_entryRuleAList608 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAList615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group__0_in_ruleAList641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_entryRuleSubgraph668 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSubgraph675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__0_in_ruleSubgraph701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_entryRuleEdgeRhs728 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhs735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhs__Alternatives_in_ruleEdgeRhs761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsNode_in_entryRuleEdgeRhsNode788 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhsNode795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsNode__Group__0_in_ruleEdgeRhsNode821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsSubgraph_in_entryRuleEdgeRhsSubgraph848 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeRhsSubgraph855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsSubgraph__Group__0_in_ruleEdgeRhsSubgraph881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_entryRuleNodeId908 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeId915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__NodeId__NameAssignment_in_ruleNodeId941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeOp__Alternatives_in_ruleEdgeOp978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__GraphType__Alternatives_in_ruleGraphType1014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Alternatives_in_ruleAttributeType1050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_rule__Stmt__Alternatives_01087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_rule__Stmt__Alternatives_01104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_rule__Stmt__Alternatives_01121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_rule__Stmt__Alternatives_01138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrStmt_in_rule__Stmt__Alternatives_01155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_rule__Stmt__Alternatives_01172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsNode_in_rule__EdgeRhs__Alternatives1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhsSubgraph_in_rule__EdgeRhs__Alternatives1221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__EdgeOp__Alternatives1254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__EdgeOp__Alternatives1275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__GraphType__Alternatives1311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__GraphType__Alternatives1332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__AttributeType__Alternatives1368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__AttributeType__Alternatives1389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__AttributeType__Alternatives1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__0__Impl_in_rule__MainGraph__Group__01444 = new BitSet(new long[]{0x000000000400C000L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__1_in_rule__MainGraph__Group__01447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__StrictAssignment_0_in_rule__MainGraph__Group__0__Impl1474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__1__Impl_in_rule__MainGraph__Group__11505 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__2_in_rule__MainGraph__Group__11508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__TypeAssignment_1_in_rule__MainGraph__Group__1__Impl1535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__2__Impl_in_rule__MainGraph__Group__21565 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__3_in_rule__MainGraph__Group__21568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__NameAssignment_2_in_rule__MainGraph__Group__2__Impl1595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__3__Impl_in_rule__MainGraph__Group__31626 = new BitSet(new long[]{0x00000000020F4010L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__4_in_rule__MainGraph__Group__31629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__MainGraph__Group__3__Impl1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__4__Impl_in_rule__MainGraph__Group__41688 = new BitSet(new long[]{0x00000000020F4010L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__5_in_rule__MainGraph__Group__41691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__MainGraph__StmtsAssignment_4_in_rule__MainGraph__Group__4__Impl1718 = new BitSet(new long[]{0x0000000002074012L});
    public static final BitSet FOLLOW_rule__MainGraph__Group__5__Impl_in_rule__MainGraph__Group__51749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__MainGraph__Group__5__Impl1777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Stmt__Group__0__Impl_in_rule__Stmt__Group__01820 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_rule__Stmt__Group__1_in_rule__Stmt__Group__01823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Stmt__Alternatives_0_in_rule__Stmt__Group__0__Impl1850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Stmt__Group__1__Impl_in_rule__Stmt__Group__11880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__Stmt__Group__1__Impl1909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Group__0__Impl_in_rule__EdgeStmtNode__Group__01946 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Group__1_in_rule__EdgeStmtNode__Group__01949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Node_idAssignment_0_in_rule__EdgeStmtNode__Group__0__Impl1976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Group__1__Impl_in_rule__EdgeStmtNode__Group__12006 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Group__2_in_rule__EdgeStmtNode__Group__12009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__EdgeRHSAssignment_1_in_rule__EdgeStmtNode__Group__1__Impl2038 = new BitSet(new long[]{0x0000000000003002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__EdgeRHSAssignment_1_in_rule__EdgeStmtNode__Group__1__Impl2050 = new BitSet(new long[]{0x0000000000003002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__Group__2__Impl_in_rule__EdgeStmtNode__Group__22083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtNode__AttributesAssignment_2_in_rule__EdgeStmtNode__Group__2__Impl2110 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__Group__0__Impl_in_rule__EdgeStmtSubgraph__Group__02147 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__Group__1_in_rule__EdgeStmtSubgraph__Group__02150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__SubgraphAssignment_0_in_rule__EdgeStmtSubgraph__Group__0__Impl2177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__Group__1__Impl_in_rule__EdgeStmtSubgraph__Group__12207 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__Group__2_in_rule__EdgeStmtSubgraph__Group__12210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__EdgeRHSAssignment_1_in_rule__EdgeStmtSubgraph__Group__1__Impl2239 = new BitSet(new long[]{0x0000000000003002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__EdgeRHSAssignment_1_in_rule__EdgeStmtSubgraph__Group__1__Impl2251 = new BitSet(new long[]{0x0000000000003002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__Group__2__Impl_in_rule__EdgeStmtSubgraph__Group__22284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeStmtSubgraph__AttributesAssignment_2_in_rule__EdgeStmtSubgraph__Group__2__Impl2311 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_rule__NodeStmt__Group__0__Impl_in_rule__NodeStmt__Group__02348 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__NodeStmt__Group__1_in_rule__NodeStmt__Group__02351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__NodeStmt__NameAssignment_0_in_rule__NodeStmt__Group__0__Impl2378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__NodeStmt__Group__1__Impl_in_rule__NodeStmt__Group__12408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__NodeStmt__AttributesAssignment_1_in_rule__NodeStmt__Group__1__Impl2435 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_rule__Attribute__Group__0__Impl_in_rule__Attribute__Group__02470 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_rule__Attribute__Group__1_in_rule__Attribute__Group__02473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Attribute__NameAssignment_0_in_rule__Attribute__Group__0__Impl2500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Attribute__Group__1__Impl_in_rule__Attribute__Group__12530 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Attribute__Group__2_in_rule__Attribute__Group__12533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__Attribute__Group__1__Impl2561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Attribute__Group__2__Impl_in_rule__Attribute__Group__22592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Attribute__ValueAssignment_2_in_rule__Attribute__Group__2__Impl2619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrStmt__Group__0__Impl_in_rule__AttrStmt__Group__02655 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__AttrStmt__Group__1_in_rule__AttrStmt__Group__02658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrStmt__TypeAssignment_0_in_rule__AttrStmt__Group__0__Impl2685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrStmt__Group__1__Impl_in_rule__AttrStmt__Group__12715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrStmt__AttributesAssignment_1_in_rule__AttrStmt__Group__1__Impl2744 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_rule__AttrStmt__AttributesAssignment_1_in_rule__AttrStmt__Group__1__Impl2756 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_rule__AttrList__Group__0__Impl_in_rule__AttrList__Group__02793 = new BitSet(new long[]{0x0000000000800010L});
    public static final BitSet FOLLOW_rule__AttrList__Group__1_in_rule__AttrList__Group__02796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__AttrList__Group__0__Impl2824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrList__Group__1__Impl_in_rule__AttrList__Group__12855 = new BitSet(new long[]{0x0000000000800010L});
    public static final BitSet FOLLOW_rule__AttrList__Group__2_in_rule__AttrList__Group__12858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttrList__A_listAssignment_1_in_rule__AttrList__Group__1__Impl2885 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule__AttrList__Group__2__Impl_in_rule__AttrList__Group__22916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__AttrList__Group__2__Impl2944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group__0__Impl_in_rule__AList__Group__02981 = new BitSet(new long[]{0x0000000001200000L});
    public static final BitSet FOLLOW_rule__AList__Group__1_in_rule__AList__Group__02984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__NameAssignment_0_in_rule__AList__Group__0__Impl3011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group__1__Impl_in_rule__AList__Group__13041 = new BitSet(new long[]{0x0000000001200000L});
    public static final BitSet FOLLOW_rule__AList__Group__2_in_rule__AList__Group__13044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group_1__0_in_rule__AList__Group__1__Impl3071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group__2__Impl_in_rule__AList__Group__23102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__AList__Group__2__Impl3131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group_1__0__Impl_in_rule__AList__Group_1__03170 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AList__Group_1__1_in_rule__AList__Group_1__03173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__AList__Group_1__0__Impl3201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__Group_1__1__Impl_in_rule__AList__Group_1__13232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AList__ValueAssignment_1_1_in_rule__AList__Group_1__1__Impl3259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__0__Impl_in_rule__Subgraph__Group__03293 = new BitSet(new long[]{0x0000000002040000L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__1_in_rule__Subgraph__Group__03296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group_0__0_in_rule__Subgraph__Group__0__Impl3323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__1__Impl_in_rule__Subgraph__Group__13354 = new BitSet(new long[]{0x00000000020F4010L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__2_in_rule__Subgraph__Group__13357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__Subgraph__Group__1__Impl3385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__2__Impl_in_rule__Subgraph__Group__23416 = new BitSet(new long[]{0x00000000020F4010L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__3_in_rule__Subgraph__Group__23419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__StmtsAssignment_2_in_rule__Subgraph__Group__2__Impl3446 = new BitSet(new long[]{0x0000000002074012L});
    public static final BitSet FOLLOW_rule__Subgraph__Group__3__Impl_in_rule__Subgraph__Group__33477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__Subgraph__Group__3__Impl3505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group_0__0__Impl_in_rule__Subgraph__Group_0__03544 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Subgraph__Group_0__1_in_rule__Subgraph__Group_0__03547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__Subgraph__Group_0__0__Impl3575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__Group_0__1__Impl_in_rule__Subgraph__Group_0__13606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Subgraph__NameAssignment_0_1_in_rule__Subgraph__Group_0__1__Impl3633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsNode__Group__0__Impl_in_rule__EdgeRhsNode__Group__03668 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__EdgeRhsNode__Group__1_in_rule__EdgeRhsNode__Group__03671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsNode__OpAssignment_0_in_rule__EdgeRhsNode__Group__0__Impl3698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsNode__Group__1__Impl_in_rule__EdgeRhsNode__Group__13728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsNode__NodeAssignment_1_in_rule__EdgeRhsNode__Group__1__Impl3755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsSubgraph__Group__0__Impl_in_rule__EdgeRhsSubgraph__Group__03789 = new BitSet(new long[]{0x0000000002040000L});
    public static final BitSet FOLLOW_rule__EdgeRhsSubgraph__Group__1_in_rule__EdgeRhsSubgraph__Group__03792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsSubgraph__OpAssignment_0_in_rule__EdgeRhsSubgraph__Group__0__Impl3819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsSubgraph__Group__1__Impl_in_rule__EdgeRhsSubgraph__Group__13849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__EdgeRhsSubgraph__SubgraphAssignment_1_in_rule__EdgeRhsSubgraph__Group__1__Impl3876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMainGraph_in_rule__GraphvizModel__GraphsAssignment3915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__MainGraph__StrictAssignment_03951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGraphType_in_rule__MainGraph__TypeAssignment_13990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__MainGraph__NameAssignment_24021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStmt_in_rule__MainGraph__StmtsAssignment_44052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_rule__EdgeStmtNode__Node_idAssignment_04083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_rule__EdgeStmtNode__EdgeRHSAssignment_14114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrList_in_rule__EdgeStmtNode__AttributesAssignment_24145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_rule__EdgeStmtSubgraph__SubgraphAssignment_04176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeRhs_in_rule__EdgeStmtSubgraph__EdgeRHSAssignment_14207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrList_in_rule__EdgeStmtSubgraph__AttributesAssignment_24238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__NodeStmt__NameAssignment_04269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrList_in_rule__NodeStmt__AttributesAssignment_14300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__Attribute__NameAssignment_04331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__Attribute__ValueAssignment_24362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__AttrStmt__TypeAssignment_04393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttrList_in_rule__AttrStmt__AttributesAssignment_14424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAList_in_rule__AttrList__A_listAssignment_14455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__AList__NameAssignment_04486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__AList__ValueAssignment_1_14517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__Subgraph__NameAssignment_0_14548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStmt_in_rule__Subgraph__StmtsAssignment_24579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOp_in_rule__EdgeRhsNode__OpAssignment_04610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeId_in_rule__EdgeRhsNode__NodeAssignment_14641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOp_in_rule__EdgeRhsSubgraph__OpAssignment_04672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_rule__EdgeRhsSubgraph__SubgraphAssignment_14703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOT_ID_in_rule__NodeId__NameAssignment4734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_synpred2_InternalDot1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtNode_in_synpred3_InternalDot1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStmtSubgraph_in_synpred4_InternalDot1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStmt_in_synpred5_InternalDot1138 = new BitSet(new long[]{0x0000000000000002L});

}