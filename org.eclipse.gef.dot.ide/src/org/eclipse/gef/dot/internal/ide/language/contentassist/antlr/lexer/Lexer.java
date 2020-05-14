/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ide.language.contentassist.antlr.lexer;

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
 * https://github.com/eclipse/xtext-eclipse/commit/1575d2c872f7a4f8537cd52ced7a94633384f714
 * available from Xtext Version 2.19 (Eclipse 2019-09).
 * TODO: remove this workaround as soon as Eclipse 2019-09 will be the minimum platform supported by GEF DOT.
 */
public abstract class Lexer
		extends org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer {

	public Lexer() {
		super();
	}

	public Lexer(CharStream input) {
		super(input);
	}

	public Lexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	private final Map<Token, String> tokenErrorMap = new HashMap<Token, String>();

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

	public String getErrorMessage(Token t) {
		return tokenErrorMap.get(t);
	}

}
