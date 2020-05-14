/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others. 
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Casey Best, Ian Bull, Rob Lintern (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

/**
 * The {@link GridLayoutAlgorithm} lays out nodes in a grid.
 * 
 * @author Casey Best
 * @author Ian Bull
 * @author Rob Lintern
 * @author Mateusz Matela
 * @author mwienand
 */
public class GridLayoutAlgorithm implements ILayoutAlgorithm {

	private static final double PADDING_PERCENTAGE = 0.95;
	private static final int MIN_ENTITY_SIZE = 5;

	/**
	 * The width/height ratio.
	 */
	private double aspectRatio = 1.0;

	/**
	 * The padding around rows.
	 */
	private int rowPadding = 0;

	private boolean resize = false;
	/**
	 * The number of rows.
	 */
	private int rows;
	/**
	 * The number of columns.
	 */
	private int cols;
	/**
	 * The number of nodes.
	 */
	private int numChildren;
	/**
	 * The column width.
	 */
	private double colWidth;
	/**
	 * The row height.
	 */
	private double rowHeight;
	/**
	 * The horizontal offset.
	 */
	private double offsetX;
	/**
	 * The vertical offset.
	 */
	private double offsetY;
	/**
	 * The height of a single node.
	 */
	private double childrenHeight;
	/**
	 * The width of a single node.
	 */
	private double childrenWidth;

	/**
	 * Default constructor.
	 */
	public GridLayoutAlgorithm() {
	}

	public void applyLayout(LayoutContext context, boolean clean) {
		if (!clean)
			return;
		numChildren = context.getNodes().length;
		Rectangle bounds = LayoutProperties.getBounds(context.getGraph());
		calculateGrid(bounds);

		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if ((i * cols + j) < numChildren) {
					Node node = context.getNodes()[index++];
					if (resize && LayoutProperties.isResizable(node))
						LayoutProperties.setSize(node, new Dimension(
								Math.max(childrenWidth, MIN_ENTITY_SIZE),
								Math.max(childrenHeight, MIN_ENTITY_SIZE)));
					Dimension size = LayoutProperties.getSize(node);
					double xmove = bounds.getX() + j * colWidth + offsetX
							+ size.width / 2;
					double ymove = bounds.getY() + i * rowHeight + offsetY
							+ size.height / 2;
					if (LayoutProperties.isMovable(node))
						LayoutProperties.setLocation(node,
								new Point(xmove, ymove));
				}
			}
		}
	}

	/**
	 * Calculates all the dimensions of grid that layout entities will be fit
	 * in.
	 * 
	 * @param bounds
	 *            A {@link Rectangle} representing the layout bounds.
	 */
	// The following fields are set by this method: {@link #numChildren},
	// {@link #rows}, {@link #cols}, {@link #colWidth}, {@link #rowHeight},
	// {@link #offsetX}, {@link #offsetY}
	protected void calculateGrid(Rectangle bounds) {
		int[] result = calculateNumberOfRowsAndCols(numChildren, bounds.getX(),
				bounds.getY(), bounds.getWidth(), bounds.getHeight());
		cols = result[0];
		rows = result[1];

		colWidth = bounds.getWidth() / cols;
		rowHeight = bounds.getHeight() / rows;

		double[] nodeSize = calculateNodeSize(colWidth, rowHeight);
		childrenWidth = nodeSize[0];
		childrenHeight = nodeSize[1];
		offsetX = (colWidth - childrenWidth) / 2.0; // half of the space between
													// columns
		offsetY = (rowHeight - childrenHeight) / 2.0; // half of the space
														// between rows
	}

	/**
	 * Calculates and returns an array containing the number of columns and the
	 * number of rows. If the {@link #setAspectRatio(double) aspect ratio} is
	 * set to <code>1</code>, then the
	 * {@link #calculateNumberOfRowsAndCols_square(int, double, double, double, double)}
	 * method is used for the computation. Otherwise, the
	 * {@link #calculateNumberOfRowsAndCols_rectangular(int)} is used for the
	 * computation.
	 * 
	 * @param numChildren
	 *            The number of nodes.
	 * @param boundX
	 *            The horizontal offset.
	 * @param boundY
	 *            The vertical offset.
	 * @param boundWidth
	 *            The bounds' width.
	 * @param boundHeight
	 *            The bounds' height.
	 * @return An array containing the number of columns, and the number of
	 *         rows.
	 */
	protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX,
			double boundY, double boundWidth, double boundHeight) {
		if (aspectRatio == 1.0) {
			return calculateNumberOfRowsAndCols_square(numChildren, boundX,
					boundY, boundWidth, boundHeight);
		} else {
			return calculateNumberOfRowsAndCols_rectangular(numChildren);
		}
	}

	/**
	 * Calculates and returns an array containing the number of columns and the
	 * number of rows, so that the nodes are layed out in squares.
	 * 
	 * @param numChildren
	 *            The number of nodes.
	 * @param boundX
	 *            The horizontal offset.
	 * @param boundY
	 *            The vertical offset.
	 * @param boundWidth
	 *            The bounds' width.
	 * @param boundHeight
	 *            The bounds' height.
	 * @return An array containing the number of columns and the number of rows.
	 */
	protected int[] calculateNumberOfRowsAndCols_square(int numChildren,
			double boundX, double boundY, double boundWidth,
			double boundHeight) {
		int rows = Math.max(1,
				(int) Math.sqrt(numChildren * boundHeight / boundWidth));
		int cols = Math.max(1,
				(int) Math.sqrt(numChildren * boundWidth / boundHeight));

		// if space is taller than wide, adjust rows first
		if (boundWidth <= boundHeight) {
			// decrease number of rows and columns until just enough or not
			// enough
			while (rows * cols > numChildren) {
				if (rows > 1)
					rows--;
				if (rows * cols > numChildren)
					if (cols > 1)
						cols--;
			}
			// increase number of rows and columns until just enough
			while (rows * cols < numChildren) {
				rows++;
				if (rows * cols < numChildren)
					cols++;
			}
		} else {
			// decrease number of rows and columns until just enough or not
			// enough
			while (rows * cols > numChildren) {
				if (cols > 1)
					cols--;
				if (rows * cols > numChildren)
					if (rows > 1)
						rows--;
			}
			// increase number of rows and columns until just enough
			while (rows * cols < numChildren) {
				cols++;
				if (rows * cols < numChildren)
					rows++;
			}
		}
		int[] result = { cols, rows };
		return result;
	}

	/**
	 * Calculates and returns an array containing the number of columns and the
	 * number of rows, so that there is an equal number of rows and columns.
	 * 
	 * @param numChildren
	 *            The number of nodes.
	 * @return An array containing the number of columns and the number of rows.
	 */
	protected int[] calculateNumberOfRowsAndCols_rectangular(int numChildren) {
		int rows = Math.max(1, (int) Math.ceil(Math.sqrt(numChildren)));
		int cols = Math.max(1, (int) Math.ceil(Math.sqrt(numChildren)));
		int[] result = { cols, rows };
		return result;
	}

	/**
	 * Calculates and returns the width and height of a single node depending on
	 * the {@link #setRowPadding(int) padding} (20%), <i>colWidth</i>,
	 * <i>rowHeight</i>, and {@link #setAspectRatio(double) aspect ratio}.
	 * 
	 * @param colWidth
	 *            The width of a column.
	 * @param rowHeight
	 *            The height of a row.
	 * @return An array containing the width and height of a node.
	 */
	protected double[] calculateNodeSize(double colWidth, double rowHeight) {
		double childW = Math.max(MIN_ENTITY_SIZE,
				PADDING_PERCENTAGE * colWidth);
		double childH = Math.max(MIN_ENTITY_SIZE,
				PADDING_PERCENTAGE * (rowHeight - rowPadding));
		double whRatio = colWidth / rowHeight;
		if (whRatio < aspectRatio) {
			childH = childW / aspectRatio;
		} else {
			childW = childH * aspectRatio;
		}
		double[] result = { childW, childH };
		return result;
	}

	/**
	 * Sets the padding between rows in the grid
	 * 
	 * @param rowPadding
	 *            padding - should be greater than or equal to 0
	 */
	public void setRowPadding(int rowPadding) {
		if (rowPadding >= 0) {
			this.rowPadding = rowPadding;
		}
	}

	/**
	 * Sets the preferred aspect ratio for layout entities. The default aspect
	 * ratio is 1.
	 * 
	 * @param aspectRatio
	 *            aspect ratio - should be greater than 0
	 */
	public void setAspectRatio(double aspectRatio) {
		if (aspectRatio > 0) {
			this.aspectRatio = aspectRatio;
		}
	}

	/**
	 * 
	 * @return true if this algorithm is set to resize elements
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 * 
	 * @param resizing
	 *            true if this algorithm should resize elements (default is
	 *            false)
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
	}

}
