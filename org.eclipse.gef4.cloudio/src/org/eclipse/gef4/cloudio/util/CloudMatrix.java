/*******************************************************************************
* Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
* the accompanying materials are made available under the terms of the Eclipse
* Public License v1.0 which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.gef4.cloudio.util;

import org.eclipse.gef4.cloudio.util.RectTree.RectNode;

/**
 * This class contains all global information about the drawable area 
 * and the layouted words in form of a {@link RectTree}.
 * 
 * @author sschwieb
 *
 */
public class CloudMatrix {
	
	private RectTree tree;
	
	private final int max;

	private final int minResolution;

	public int getMinResolution() {
		return minResolution;
	}

	public CloudMatrix(int maxSize, int minResolution) {
		this.max = maxSize;
		this.minResolution = minResolution;
		reset();
	}
	
	public short get(int x, int y) {
		return tree.getRoot().getWordId(x*minResolution,y*minResolution);
	}
	
	public boolean isEmpty(int x, int y) {
		short id = tree.getRoot().getWordId(x*minResolution, y*minResolution);
		return id == RectTree.EMPTY;
	}

	public void reset() {
		SmallRect root = new SmallRect(0, 0, max, max);
		tree = new RectTree(root, minResolution);
	}

	public void set(RectNode node, short id, short xOffset, short yOffset, int minResolution) {
		int cleanX = ((xOffset + node.rect.x) / minResolution) * minResolution;
		int cleanY = ((yOffset + node.rect.y) / minResolution) * minResolution;
		SmallRect rect = new SmallRect(cleanX, cleanY, minResolution, minResolution);
		tree.insert(rect, id);
	}
	
}
