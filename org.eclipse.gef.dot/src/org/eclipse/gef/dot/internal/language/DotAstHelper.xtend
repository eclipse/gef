/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
package org.eclipse.gef.dot.internal.language

import java.util.List
import org.eclipse.gef.dot.internal.language.dot.AttrList
import org.eclipse.gef.dot.internal.language.dot.Attribute
import org.eclipse.gef.dot.internal.language.dot.DotGraph
import org.eclipse.gef.dot.internal.language.dot.Subgraph
import org.eclipse.gef.dot.internal.language.terminals.ID

/**
 * This class provides helper methods for walking the DOT abstract syntax tree.
 */
class DotAstHelper {

	def static ID getAttributeValue(DotGraph graph, String name) {
		for (stmt : graph.stmts) {
			var ID value = switch stmt {
				//no need to consider AttrStmt here, because the global graph attributes are evaluated somewhere else
				Attribute:
					stmt.getAttributeValue(name)
			}
			if (value !== null) {
				return value
			}
		}
		null
	}
	
	def static ID getAttributeValue(Subgraph subgraph, String name) {
		for (stmt : subgraph.stmts) {
			var ID value = switch stmt {
				//no need to consider AttrStmt here, because the global graph attributes are evaluated somewhere else
				Attribute:
					stmt.getAttributeValue(name)
			}
			if (value !== null) {
				return value
			}
		}
		null
	}

	/**
	 * Returns the value of the first attribute with the give name or
	 * <code>null</code> if no attribute could be found.
	 * 
	 * @param attrLists
	 *            The {@link AttrList}s to search.
	 * @param name
	 *            The name of the attribute whose value is to be retrieved.
	 * @return The attribute value or <code>null</code> in case the attribute
	 *         could not be found.
	 */
	def static ID getAttributeValue(List<AttrList> attrLists, String name) {
		for (AttrList attrList : attrLists) {
			val value = attrList.getAttributeValue(name)
			if (value !== null) {
				return value
			}
		}
		null
	}

	def private static ID getAttributeValue(AttrList attrList, String name) {
		attrList.attributes.findFirst[it.name.toValue == name]?.value
	}

	def private static ID getAttributeValue(Attribute attribute, String name) {
		if (attribute.name.toValue.equals(name)) {
			return attribute.value
		}
		null
	}
	
}