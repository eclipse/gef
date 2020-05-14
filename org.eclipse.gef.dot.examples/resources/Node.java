/*******************************************************************************
 * Copyright (c) 2010, 2016 Fabian Steeg and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/
/**
 * A recursive, object-oriented node in a binary tree with
 * references to the parent and the children (left and right).
 <pre>
 graph {
   node[label="Node"]
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