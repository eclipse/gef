/*******************************************************************************
 * Copyright (c) 2005, 2015 The Chisel Group and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ian Bull (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;

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

	private ILayoutContext context;

	public void applyLayout(boolean clean) {
		if (!clean)
			return;
		ArrayList<List<INodeLayout>> rowsList = new ArrayList<>();
		INodeLayout[] entities = context.getNodes();

		for (int i = 0; i < entities.length; i++) {
			addToRowList(entities[i], rowsList);
		}

		Collections.sort(rowsList, new Comparator<List<INodeLayout>>() {
			public int compare(List<INodeLayout> o1, List<INodeLayout> o2) {
				INodeLayout entity0 = o1.get(0);
				INodeLayout entity1 = o2.get(0);
				return (int) (LayoutProperties.getLocation(entity0).y
						- LayoutProperties.getLocation(entity1).y);
			}
		});

		Comparator<INodeLayout> entityComparator = new Comparator<INodeLayout>() {
			public int compare(INodeLayout o1, INodeLayout o2) {
				return (int) (LayoutProperties.getLocation(o1).y
						- LayoutProperties.getLocation(o2).y);
			}
		};
		Rectangle bounds = LayoutProperties.getBounds(context);
		int heightSoFar = 0;

		for (Iterator<List<INodeLayout>> iterator = rowsList
				.iterator(); iterator.hasNext();) {
			List<INodeLayout> currentRow = iterator.next();
			Collections.sort(currentRow, entityComparator);

			int i = 0;
			int width = (int) (bounds.getWidth() / 2 - currentRow.size() * 75);

			heightSoFar += LayoutProperties.getSize(currentRow.get(0)).height
					+ VSPACING;
			for (Iterator<INodeLayout> iterator2 = currentRow
					.iterator(); iterator2.hasNext();) {
				INodeLayout entity = (INodeLayout) iterator2.next();
				Dimension size = LayoutProperties.getSize(entity);
				LayoutProperties.setLocation(entity,
						width + 10 * ++i + size.width / 2,
						heightSoFar + size.height / 2);
				width += size.width;
			}
		}
	}

	public void setLayoutContext(ILayoutContext context) {
		this.context = context;
	}

	public ILayoutContext getLayoutContext() {
		return context;
	}

	private void addToRowList(INodeLayout entity,
			ArrayList<List<INodeLayout>> rowsList) {
		double layoutY = LayoutProperties.getLocation(entity).y;

		for (Iterator<List<INodeLayout>> iterator = rowsList
				.iterator(); iterator.hasNext();) {
			List<INodeLayout> currentRow = iterator.next();
			INodeLayout currentRowEntity = currentRow.get(0);
			double currentRowY = LayoutProperties
					.getLocation(currentRowEntity).y;
			if (layoutY >= currentRowY - DELTA
					&& layoutY <= currentRowY + DELTA) {
				currentRow.add(entity);
				return;
			}
		}
		List<INodeLayout> newRow = new ArrayList<>();
		newRow.add(entity);
		rowsList.add(newRow);
	}
}
