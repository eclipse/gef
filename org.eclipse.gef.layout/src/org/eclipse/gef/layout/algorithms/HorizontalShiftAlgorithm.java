/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Ian Bull (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

/**
 * This layout shifts overlapping nodes to the right.
 * 
 * @author Ian Bull
 * @author Mateusz Matela
 * @author mwienand
 */
public class HorizontalShiftAlgorithm implements ILayoutAlgorithm {

	private static final double DELTA = 10;

	private static final double VSPACING = 16;

	public void applyLayout(LayoutContext context, boolean clean) {
		if (!clean)
			return;
		ArrayList<List<Node>> rowsList = new ArrayList<>();
		Node[] entities = context.getNodes();

		for (int i = 0; i < entities.length; i++) {
			addToRowList(entities[i], rowsList);
		}

		Collections.sort(rowsList, new Comparator<List<Node>>() {
			public int compare(List<Node> o1, List<Node> o2) {
				Node entity0 = o1.get(0);
				Node entity1 = o2.get(0);
				return (int) (LayoutProperties.getLocation(entity0).y
						- LayoutProperties.getLocation(entity1).y);
			}
		});

		Comparator<Node> entityComparator = new Comparator<Node>() {
			public int compare(Node o1, Node o2) {
				return (int) (LayoutProperties.getLocation(o1).y
						- LayoutProperties.getLocation(o2).y);
			}
		};
		Rectangle bounds = LayoutProperties.getBounds(context.getGraph());
		int heightSoFar = 0;

		for (Iterator<List<Node>> iterator = rowsList.iterator(); iterator
				.hasNext();) {
			List<Node> currentRow = iterator.next();
			Collections.sort(currentRow, entityComparator);

			int i = 0;
			int width = (int) (bounds.getWidth() / 2 - currentRow.size() * 75);

			heightSoFar += LayoutProperties.getSize(currentRow.get(0)).height
					+ VSPACING;
			for (Iterator<Node> iterator2 = currentRow.iterator(); iterator2
					.hasNext();) {
				Node entity = (Node) iterator2.next();
				Dimension size = LayoutProperties.getSize(entity);
				LayoutProperties.setLocation(entity,
						new Point(width + 10 * ++i + size.width / 2,
								heightSoFar + size.height / 2));
				width += size.width;
			}
		}
	}

	private void addToRowList(Node entity, ArrayList<List<Node>> rowsList) {
		double layoutY = LayoutProperties.getLocation(entity).y;

		for (Iterator<List<Node>> iterator = rowsList.iterator(); iterator
				.hasNext();) {
			List<Node> currentRow = iterator.next();
			Node currentRowEntity = currentRow.get(0);
			double currentRowY = LayoutProperties
					.getLocation(currentRowEntity).y;
			if (layoutY >= currentRowY - DELTA
					&& layoutY <= currentRowY + DELTA) {
				currentRow.add(entity);
				return;
			}
		}
		List<Node> newRow = new ArrayList<>();
		newRow.add(entity);
		rowsList.add(newRow);
	}
}
