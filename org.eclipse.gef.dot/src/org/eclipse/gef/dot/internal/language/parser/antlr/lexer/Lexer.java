/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.parser.antlr.lexer;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

/**
 * This derived class includes the fix
 * https://github.com/eclipse/xtext-core/commit/34d61d4d80c6992045ed013a0ac4eb337a1f4e87
 * available from Xtext Version 2.9 (MARS).
 */
public abstract class Lexer extends org.eclipse.xtext.parser.antlr.Lexer {

	private final Map<Token, String> tokenErrorMap = new HashMap<Token, String>();

	public Lexer() {
		super();
	}

	public Lexer(CharStream input) {
		super(input);
	}

	public Lexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override
	public Token nextToken() {
		while (true) {
			this.state.token = null;
			this.state.channel = Token.DEFAULT_CHANNEL;
			this.state.tokenStartCharIndex = input.index();
			this.state.tokenStartCharPositionInLine = input
					.getCharPositionInLine();
			this.state.tokenStartLine = input.getLine();
			this.state.text = null;
			if (input.LA(1) == CharStream.EOF) {
				return Token.EOF_TOKEN;
			}
			try {
				mTokens();
				if (this.state.token == null) {
					emit();
				} else if (this.state.token == Token.SKIP_TOKEN) {
					continue;
				}
				return this.state.token;
			} catch (RecognitionException re) {
				reportError(re);
				if (re instanceof NoViableAltException
						|| re instanceof FailedPredicateException) {
					recover(re);
				}
				// create token that holds mismatched char
				Token t = new CommonToken(input, Token.INVALID_TOKEN_TYPE,
						Token.HIDDEN_CHANNEL, this.state.tokenStartCharIndex,
						getCharIndex() - 1);
				t.setLine(this.state.tokenStartLine);
				t.setCharPositionInLine(
						this.state.tokenStartCharPositionInLine);
				tokenErrorMap.put(t, getErrorMessage(re, this.getTokenNames()));
				emit(t);
				return this.state.token;
			}
		}
	}

	@Override
	public String getErrorMessage(Token t) {
		return tokenErrorMap.get(t);
	}

}
