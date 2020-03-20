/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API & implementation (bug #561084)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.wizard

import org.eclipse.core.runtime.Status
import org.eclipse.xtext.ui.wizard.template.FileTemplate
import org.eclipse.xtext.ui.wizard.template.IFileGenerator
import org.eclipse.xtext.ui.wizard.template.IFileTemplateProvider

import static org.eclipse.core.runtime.IStatus.*

/**
 * Create a list with all file templates to be shown in the template new file wizard.
 * 
 * Each template is able to generate one or more files.
 */
class DotFileTemplateProvider implements IFileTemplateProvider {
	override getFileTemplates() {
		#[new ParameterisedDotFile]
	}
}

@FileTemplate(label="DOT Graph", icon="file_template.png", description="Create a dot graph.")
final class ParameterisedDotFile {
	val type = combo("Graph Type:", #["directed graph", "undirected graph"])
	val digraphName = text("Graph name (optional):", "", "Optional name of your graph")
	val nodes = text("Number of nodes:", "1")
	
	override protected validate() {
		if (digraphName.value.contains(" ")) {
			new Status(ERROR, "Wizard", " The name'" + name + "' may not contain spaces")
		}
		else if (!nodes.value.matches("[1-9][0-9]*")) {
			new Status(ERROR, "Wizard", " The number of nodes '" + nodes + "' is not a positive integer")
		}
		else null
	}
	 
	override generateFiles(IFileGenerator generator) {
		val nodesBuilder = new StringBuilder
		val edgesBuilder = new StringBuilder
		
		for (i : 0 ..< Integer.parseInt(nodes.value)) {
			nodesBuilder.append('''«i+1» [label="label of node \N"]
								''')
			if (i > 0) {
				edgesBuilder.append('''«i»«type.value == "directed graph" ? "->" : "--"»«i+1» [label="an edge between nodes \T and \H" fontcolor="red"]
									''')
			}
		}
		
		generator.generate('''«folder»/«name»«!name.matches(".*\\.(gv|dot)\\Z") ? ".dot"»''', '''
			/*
			 * This is a graph stub
			 */
			«type.value == "directed graph" ? "digraph" : "graph"» «digraphName» {
				// global attribute statement
				node [shape="ellipse"]
				// nodes
				«nodesBuilder»
				// edges
				«edgesBuilder»
			}
		''')
	}
}