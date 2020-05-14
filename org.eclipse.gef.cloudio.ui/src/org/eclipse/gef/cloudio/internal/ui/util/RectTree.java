/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Point;

/**
 * A two-dimensional tree structure to store non-overlapping rectangles.
 * 
 * @author sschwieb
 *
 */
public class RectTree {

	private final int minResolution;

	private short xOffset, yOffset;

	private RectNode root;

	private LinkedList<RectNode> leaves;

	public static short EMPTY = -3, MISC = -2, BACKGROUND = -1;

	class RectNode {

		final SmallRect rect;

		private RectNode[] children;

		private final SmallRect[] childAreas;

		short filled = EMPTY;

		public RectNode(SmallRect rect) {
			this.rect = rect;
			final int width = rect.width / 2;
			final int height = rect.height / 2;
			if (rect.width > minResolution) {
				this.childAreas = new SmallRect[4];
				// top left
				childAreas[0] = new SmallRect(rect.x, rect.y, width, height);
				// top right
				childAreas[1] = new SmallRect(rect.x + width, rect.y, width, height);
				// bottom left
				childAreas[2] = new SmallRect(rect.x, rect.y + height, width, height);
				// bottom right
				childAreas[3] = new SmallRect(rect.x + width, rect.y + height, width, height);
			} else {
				this.childAreas = null;
			}
		}

		private int getChildIndex(SmallRect r) {
			int index = 0;
			if (r.y >= childAreas[3].y) {
				if (r.x >= childAreas[3].x) {
					index = 3;
				} else {
					index = 2;
				}
			} else {
				if (r.x >= childAreas[1].x) {
					index = 1;
				}
			}
			return index;
		}

		public boolean insert(SmallRect r, short id) {
			if (rect.width == minResolution) {
				filled = id;
				return true;
			}
			int i = getChildIndex(r);
			if (children == null) {
				children = new RectNode[4];
			}
			if (children[i] == null) {
				children[i] = new RectNode(childAreas[i]);
			}
			boolean filledChild = children[i].insert(r, id);
			if (filledChild) {
				Set<Short> ids = new HashSet<>();
				boolean filled = true;
				for (int j = 0; j < children.length; j++) {
					if (i == j)
						continue;
					if (children[j] == null || children[j].filled == EMPTY) {
						filled = false;
						break;
					}
					ids.add(children[j].filled);
				}
				if (filled) {
					if (ids.size() == 1) {
						this.filled = ids.iterator().next();
						if (this.filled == BACKGROUND) {
							children = null;
						}
					} else {
						this.filled = MISC;
					}
				}
				return filled;
			}
			return false;
		}

		public boolean isAvailable(final SmallRect oRect) {
			if (filled >= MISC)
				return false;
			if (children == null) {
				return filled == EMPTY;
			}
			final int i = getChildIndex(oRect);
			if (children[i] == null)
				return true;
			return children[i].isAvailable(oRect);
		}

		public short getWordId(int x, int y) {
			if (filled > BACKGROUND)
				return filled;
			if (children == null) {
				return filled;
			}
			for (int i = 0; i < childAreas.length; i++) {
				if (childAreas[i].intersects(x, y, minResolution, minResolution) && children[i] != null) {
					return children[i].getWordId(x, y);
				}
			}
			return EMPTY;
		}

		public short getWordId(Point position) {
			return getWordId(position.x, position.y);
		}

	}

	public RectTree(SmallRect root, int minResolution) {
		this.minResolution = minResolution;
		this.root = new RectNode(root);
	}

	public void insert(SmallRect r, short id) {
		root.insert(r, id);
	}

	public void move(int x, int y) {
		this.xOffset = (short) x;
		this.yOffset = (short) y;
	}

	public boolean fits(final CloudMatrix mainTree) {
		LinkedList<RectNode> leaves = getLeaves();
		Iterator<RectNode> nodes = leaves.iterator();
		while (nodes.hasNext()) {
			RectNode node = nodes.next();
			if (!mainTree.isEmpty((node.rect.x + xOffset) / minResolution, (node.rect.y + yOffset) / minResolution)) {
				nodes.remove();
				leaves.addFirst(node);
				return false;
			}
		}
		return true;
	}

	LinkedList<RectNode> getLeaves() {
		if (leaves == null) {
			leaves = new LinkedList<>();
			addLeaves(leaves, root);
		}
		return leaves;
	}

	private void addLeaves(List<RectNode> leaves, RectNode current) {
		if (current.children == null) {
			if (current.filled != EMPTY) {
				leaves.add(current);
			}
		} else {
			for (int i = 0; i < 4; i++) {
				if (current.children[i] == null)
					continue;
				addLeaves(leaves, current.children[i]);
			}
		}
	}

	public void place(final CloudMatrix mainTree, short id) {
		Collection<RectNode> leaves = getLeaves();
		for (RectNode node : leaves) {
			mainTree.set(node, id, xOffset, yOffset, minResolution);
		}
	}

	public void releaseRects() {
		getLeaves();
		root.children = null;
	}

	public RectNode getRoot() {
		return root;
	}

	public void reset() {
		root = new RectNode(root.rect);
	}

}
