/*******************************************************************************
 * Copyright (c) 2005-2010 The Chisel Group and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group - initial API and implementation
 *               Mateusz Matela 
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.LayoutPropertiesHelper;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.LayoutContext;

/**
 * This layout shifts overlapping nodes to the right.
 * 
 * @author Ian Bull
 */
public class HorizontalShiftAlgorithm implements LayoutAlgorithm {

	private static final double DELTA = 10;

	private static final double VSPACING = 16;

	private LayoutContext context;

	public void applyLayout(boolean clean) {
		if (!clean)
			return;
		ArrayList<List<EntityLayout>> rowsList = new ArrayList<List<EntityLayout>>();
		EntityLayout[] entities = context.getEntities();

		for (int i = 0; i < entities.length; i++) {
			addToRowList(entities[i], rowsList);
		}

		Collections.sort(rowsList, new Comparator<List<EntityLayout>>() {
			public int compare(List<EntityLayout> o1, List<EntityLayout> o2) {
				EntityLayout entity0 = o1.get(0);
				EntityLayout entity1 = o2.get(0);
				return (int) (LayoutPropertiesHelper.getLocation(entity0).y - LayoutPropertiesHelper
						.getLocation(entity1).y);
			}
		});

		Comparator<EntityLayout> entityComparator = new Comparator<EntityLayout>() {
			public int compare(EntityLayout o1, EntityLayout o2) {
				return (int) (LayoutPropertiesHelper.getLocation(o1).y - LayoutPropertiesHelper
						.getLocation(o2).y);
			}
		};
		Rectangle bounds = LayoutPropertiesHelper.getBounds(context);
		int heightSoFar = 0;

		for (Iterator<List<EntityLayout>> iterator = rowsList.iterator(); iterator
				.hasNext();) {
			List<EntityLayout> currentRow = iterator.next();
			Collections.sort(currentRow, entityComparator);

			int i = 0;
			int width = (int) (bounds.getWidth() / 2 - currentRow.size() * 75);

			heightSoFar += LayoutPropertiesHelper.getSize(currentRow.get(0)).height
					+ VSPACING;
			for (Iterator<EntityLayout> iterator2 = currentRow.iterator(); iterator2
					.hasNext();) {
				EntityLayout entity = (EntityLayout) iterator2.next();
				Dimension size = LayoutPropertiesHelper.getSize(entity);
				LayoutPropertiesHelper.setLocation(entity, width + 10 * ++i
						+ size.width / 2, heightSoFar + size.height / 2);
				width += size.width;
			}
		}
	}

	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

	public LayoutContext getLayoutContext() {
		return context;
	}

	private void addToRowList(EntityLayout entity,
			ArrayList<List<EntityLayout>> rowsList) {
		double layoutY = LayoutPropertiesHelper.getLocation(entity).y;

		for (Iterator<List<EntityLayout>> iterator = rowsList.iterator(); iterator
				.hasNext();) {
			List<EntityLayout> currentRow = iterator.next();
			EntityLayout currentRowEntity = currentRow.get(0);
			double currentRowY = LayoutPropertiesHelper.getLocation(currentRowEntity).y;
			if (layoutY >= currentRowY - DELTA
					&& layoutY <= currentRowY + DELTA) {
				currentRow.add(entity);
				return;
			}
		}
		List<EntityLayout> newRow = new ArrayList<EntityLayout>();
		newRow.add(entity);
		rowsList.add(newRow);
	}
}
