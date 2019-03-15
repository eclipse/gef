package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.Map
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.Lexer
import org.antlr.runtime.Token
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider
import org.junit.Rule
import org.junit.rules.Timeout

import static extension com.google.common.io.CharStreams.readLines
import static extension org.junit.Assert.assertEquals

/**
 * The implementation of the following helper methods is mainly taken from
 * the org.eclipse.xpect.tests.LexerTest class of the Eclipse Xpect project.
 */
abstract class AbstractLexerTest {

	@Rule public val timeout = new Timeout(2000)

	@Inject extension IAntlrTokenFileProvider

	protected def assertLexing(CharSequence modelAsText, CharSequence expected) {
		val actual = lexer.lex(antlrTokenFile, modelAsText.toString.trim)
		expected.toString.trim.assertEquals(actual.toString.trim)
	}

	def protected abstract Lexer lexer()

	def protected lex(Lexer lexer, InputStream tokensStream, CharSequence text) {
		val tokenNames = tokenNames(new InputStreamReader(tokensStream))
		lexerResult(lexer, tokenNames, text)
	}

	def private lexerResult(Lexer lexer, Map<Integer, String> tokenNames, CharSequence text) {
		lexer.setCharStream(new ANTLRStringStream(text.toString))
		val result = newArrayList
		while (true) {
			val token = lexer.nextToken
			if (token === Token.EOF_TOKEN) {
				return result.join(System.lineSeparator)
			}
			result += (tokenNames.get(token.type) ?: token.type) + " '" + token.text + "'"
		}
	}

	private def tokenNames(Reader tokensReader) {
		val result = <Integer, String>newLinkedHashMap
		val	lines = tokensReader.readLines

		for (line : lines) {
			val delimiterIndex = line.lastIndexOf('=')
			val name = line.substring(0, delimiterIndex)
			val index = Integer.parseInt(line.substring(delimiterIndex+1))
			result.put(index, if(name.startsWith("KEYWORD")) "KEYWORD" else name)
		}

		result
	}
}