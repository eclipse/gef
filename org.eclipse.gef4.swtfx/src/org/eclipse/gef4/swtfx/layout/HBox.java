/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.swtfx.INode;

public class HBox extends Pane {

	private Map<INode, HBoxConstraints> constraints = new HashMap<INode, HBoxConstraints>();
	private Insets margin;
	private double spacing;

	public void add(INode node, HBoxConstraints constraints) {
		addChildNodes(node);
		this.constraints.put(node, constraints);
	}

	private double[] collectPrefWidths(INode[] managed) {
		double[] prefWidths = new double[managed.length];
		for (int i = 0; i < managed.length; i++) {
			prefWidths[i] = managed[i].computePrefWidth(getHeight());
		}
		return prefWidths;
	}

	public HBoxConstraints getConstraints(INode node) {
		if (node.getParentNode() != this) {
			throw new IllegalArgumentException("The given node (" + node
					+ ") is not a child of this HBox.");
		}
		if (!constraints.containsKey(node)) {
			constraints.put(node, new HBoxConstraints());
		}
		return constraints.get(node);
	}

	public Insets getMargin() {
		return margin;
	}

	@Override
	public void layoutChildren() {
		double availableWidth = getWidth();
		double availableHeight = getHeight();

		System.out.println("available space: " + availableWidth + " x "
				+ availableHeight);

		INode[] managed = getManagedChildren();
		double[] prefWidths = collectPrefWidths(managed);
		double prefWidth = sum(prefWidths);

		double d = availableWidth - prefWidth;
		if (d > 0) {
			System.out.println("excess width = " + d);

			int[][] groups = sortByPrio(managed, true);
			for (int i = 0; i < groups.length; i++) {
				double socialPart = d / groups[i].length;

				for (int j = 0; j < groups[i].length; j++) {
					int index = groups[i][j];
					INode n = managed[index];

					double maxWidth = maxWidth(n);
					if (maxWidth >= prefWidths[index] + socialPart) {
						prefWidths[index] += socialPart;
					} else {
						prefWidths[index] = maxWidth;
					}
				}
			}
		} else if (d < 0) {
			System.out.println("  insufficient width = " + d);

			int[][] groups = sortByPrio(managed, false);
			for (int i = 0; i < groups.length; i++) {
				double socialPart = -d / groups[i].length;

				for (int j = 0; j < groups[i].length; j++) {
					int index = groups[i][j];
					INode n = managed[index];

					double minWidth = minWidth(n);
					if (minWidth <= prefWidths[index] - socialPart) {
						prefWidths[index] -= socialPart;
					} else {
						prefWidths[index] = minWidth;
					}
				}
			}
		}

		double x = 0;

		for (int i = 0; i < managed.length; i++) {
			INode child = managed[i];
			double w = prefWidths[i];
			double h = child.computePrefHeight(w);

			// System.out.println("  layout " + child + " to " + x + ", 0"
			// + " sized " + w + " x " + h);

			child.relocate(x, 0);
			if (child.isResizable()) {
				child.resize(w, h);
			}

			x += w;

			/*
			 * TODO: Respect baseline-offset setting, allow padding/spacing
			 * constraints, allow grow-priority constraint.
			 */
		}
	}

	private double maxWidth(INode n) {
		double mw = n.getMaxWidth();
		if (mw == USE_COMPUTED_SIZE) {
			return n.computeMaxWidth(getHeight());
		}
		return mw;
	}

	private double minWidth(INode n) {
		double minWidth = n.getMinWidth();
		// System.out.println("minWidth(" + n + ") = " + minWidth);
		if (minWidth == INode.USE_COMPUTED_SIZE) {
			return n.computeMinWidth(getHeight());
		}
		return minWidth;
	}

	private int[][] sortByPrio(final INode[] managed, boolean growing) {
		if (managed.length < 1) {
			return new int[0][0];
		}

		// sort by grow priority
		Integer[] indices = new Integer[managed.length];
		int lenResizable = 0;
		for (int i = 0; i < indices.length; i++) {
			if (managed[i].isResizable()) {
				indices[i] = i;
				lenResizable++;
			} else {
				indices[i] = -1;
			}
		}

		if (lenResizable == 0) {
			return new int[0][0];
		}

		Integer[] sorted = new Integer[lenResizable];
		for (int i = 0, j = 0; i < indices.length; i++) {
			if (indices[i] >= 0) {
				sorted[j] = indices[i];
				j++;
			}
		}

		// sort by (grow|shrink) priority
		if (growing) {
			Arrays.sort(sorted, new Comparator<Integer>() {
				@Override
				public int compare(Integer a, Integer b) {
					double ca = getConstraints(managed[a]).getGrowPriority();
					double cb = getConstraints(managed[b]).getGrowPriority();
					return ca < cb ? -1 : ca > cb ? 1 : 0;
				}
			});
		} else {
			Arrays.sort(sorted, new Comparator<Integer>() {
				@Override
				public int compare(Integer a, Integer b) {
					double ca = getConstraints(managed[a]).getShrinkPriority();
					double cb = getConstraints(managed[b]).getShrinkPriority();
					return ca < cb ? -1 : ca > cb ? 1 : 0;
				}
			});
		}

		// get indices groups
		List<int[]> groups = new ArrayList<int[]>();

		// assign the first child to a group
		HBoxConstraints first = getConstraints(managed[sorted[0]]);
		double p = growing ? first.getGrowPriority() : first
				.getShrinkPriority();
		List<Integer> group = new ArrayList<Integer>();
		group.add(sorted[0]);

		// assign all children to their groups
		for (int i = 1; i < sorted.length; i++) {
			Integer managedIndex = sorted[i];
			HBoxConstraints c = getConstraints(managed[managedIndex]);
			double gp = growing ? c.getGrowPriority() : c.getShrinkPriority();
			if (gp == p) {
				group.add(managedIndex);
			} else {
				// store group
				int[] g = new int[group.size()];
				for (int j = 0; j < g.length; j++) {
					g[j] = group.get(j);
				}
				groups.add(g);

				// start new group
				group.clear();
				group.add(managedIndex);
				p = gp;
			}
		}

		// store last group
		if (group.size() > 0) {
			int[] g = new int[group.size()];
			for (int j = 0; j < g.length; j++) {
				g[j] = group.get(j);
			}
			groups.add(g);
		}

		// map indices to managed (the array indices)
		int[][] prioGroups = new int[groups.size()][];
		for (int i = 0; i < groups.size(); i++) {
			prioGroups[i] = groups.get(i);
		}
		return prioGroups;
	}

	private double sum(double[] nums) {
		double sum = 0;
		for (double i : nums) {
			sum += i;
		}
		return sum;
	}

}
