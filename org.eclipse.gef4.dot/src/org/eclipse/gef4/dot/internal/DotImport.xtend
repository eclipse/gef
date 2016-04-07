/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.dot.internal

import com.google.inject.Injector
import java.io.File
import java.io.StringReader
import org.eclipse.gef4.dot.internal.parser.DotStandaloneSetup
import org.eclipse.gef4.dot.internal.parser.dot.DotAst
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotParser
import org.eclipse.gef4.graph.Graph

/**
 * A parser that creates a {@link Graph} with {@link DotAttributes} from a Graphviz DOT string or file.
 * 
 * @author anyssen
 * 
 */
class DotImport {

	private static final Injector dotInjector = new DotStandaloneSetup().createInjectorAndDoEMFRegistration();
	private static final DotParser dotParser = dotInjector.getInstance(typeof(DotParser)) as DotParser;

	// TODO: support a list of graphs
	def Graph importDot(String dotString) {
		var parseResult = dotParser.doParse(new StringReader(dotString))

		if (parseResult.hasSyntaxErrors) {
			throw new IllegalArgumentException(
				"Given DOT string " + dotString + " is not valid: " +
					parseResult.syntaxErrors.map[syntaxErrorMessage.message].join(","))
		}
		// TODO: use validator to semantically validate as well

		// TODO: return list of graphs rather than first one
		new DotInterpreter().interpret(parseResult.rootASTElement as DotAst).head
	}

	// TODO: support a list of graphs
	def Graph importDot(File dotFile) {
		importDot(DotFileUtils.read(dotFile))
	}
}
