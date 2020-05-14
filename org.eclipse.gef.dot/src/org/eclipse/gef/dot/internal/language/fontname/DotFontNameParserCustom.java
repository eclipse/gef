/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #542663)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.fontname;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.TokenSource;
import org.eclipse.gef.dot.internal.language.parser.antlr.DotFontNameParser;

/**
 * This custom parser is used to hand down a lowercase only string to the lexer.
 * As the grammar is case-insensitive, we follow the custom character streams
 * approach as described in the antlr4 docs.
 * https://github.com/antlr/antlr4/blob/master/doc/case-insensitive-lexing.md
 */
public class DotFontNameParserCustom extends DotFontNameParser {
	@Override
	protected TokenSource createLexer(CharStream in) {
		return super.createLexer(new CharStream() {
			@Override
			public void consume() {
				in.consume();
			}

			@Override
			public int LA(int i) {
				return Character.toLowerCase(in.LA(i));
			}

			@Override
			public int mark() {
				return in.mark();
			}

			@Override
			public int index() {
				return in.index();
			}

			@Override
			public void rewind(int marker) {
				in.rewind(marker);
			}

			@Override
			public void rewind() {
				in.rewind();
			}

			@Override
			public void release(int marker) {
				in.release(marker);
			}

			@Override
			public void seek(int index) {
				in.seek(index);
			}

			@Override
			public int size() {
				return in.size();
			}

			@Override
			public String getSourceName() {
				return in.getSourceName();
			}

			@Override
			public String substring(int start, int stop) {
				return in.substring(start, stop);
			}

			@Override
			public int LT(int i) {
				return Character.toLowerCase(in.LT(i));
			}

			@Override
			public int getLine() {
				return in.getLine();
			}

			@Override
			public void setLine(int line) {
				in.setLine(line);
			}

			@Override
			public void setCharPositionInLine(int pos) {
				in.setCharPositionInLine(pos);
			}

			@Override
			public int getCharPositionInLine() {
				return in.getCharPositionInLine();
			}

		});
	}
}
