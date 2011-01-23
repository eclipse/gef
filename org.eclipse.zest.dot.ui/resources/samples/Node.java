/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
/**
 * A recursive, object-oriented node in a binary tree with 
 * references to the parent and the children (left and right).
 <pre> 
 graph {
   node[label=Node]
   root; parent; left; right
   parent -- root[label=parent]
   root -- left[label=left] 
   root -- right[label=right]
 } </pre> */
class Node {
	Node parent;
	Node left;
	Node right;
}