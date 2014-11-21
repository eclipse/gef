/*******************************************************************************
 * Copyright (c) 2011,2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors:
 * Fabian Steeg
 * - initial API and implementation; see bug 277380
 * - custom outline labels, icons, and structure; see bug 452650
 *******************************************************************************/
package org.eclipse.gef4.internal.dot.parser.ui.outline;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef4.internal.dot.parser.dot.AttrStmt;
import org.eclipse.gef4.internal.dot.parser.dot.Attribute;
import org.eclipse.gef4.internal.dot.parser.dot.EdgeRhs;
import org.eclipse.gef4.internal.dot.parser.dot.NodeStmt;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

/**
 * Customization of the default outline structure.
 * 
 */
public class DotOutlineTreeProvider extends DefaultOutlineTreeProvider {
	/**
	 * Treat node statements as leafs if they have no attributes.
	 * 
	 * @param node
	 *            The `NodeStmt` model element
	 * @return true if this node contains no attributes
	 */
	protected boolean _isLeaf(NodeStmt node) {
		return node.getAttrLists().isEmpty();
	}

	/**
	 * `EdgeRhs` elements are displayed as leafs and not expandable.
	 * 
	 * @param edge
	 *            The `EdgeRhs` model element
	 * @return true
	 */
	protected boolean _isLeaf(EdgeRhs edge) {
		return true;
	}

	/**
	 * Skip the `AttrList` wrapper element in the outline structure.
	 * 
	 * @param parent
	 *            The outline parent node
	 * @param flow
	 *            The `AttrStmt` model element
	 */
	protected void _createChildren(IOutlineNode parent, AttrStmt stmt) {
		if (stmt.getAttrLists().size() > 0) {
			EList<Attribute> attributes = stmt.getAttrLists().get(0)
					.getAttributes(); // skip the `AttrList`
			for (Attribute attribute : attributes) {
				createNode(parent, attribute);
			}
		}
	}
}
