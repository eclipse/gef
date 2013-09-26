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
package org.eclipse.gef4.swtfx;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;

/**
 * The {@link IParent} interface extends the {@link INode} interface by methods
 * which have to be available on a container. Take a look at the abstract
 * implementation: {@link AbstractParent}.
 * 
 * @author mwienand
 * 
 */
public interface IParent extends INode {

	/**
	 * Appends the given {@link INode}s to this {@link IParent}'s children list.
	 * Besides, this {@link IParent} is set as the parent for all passed-in
	 * nodes.
	 * 
	 * @param nodes
	 */
	public void addChildNodes(INode... nodes);

	/**
	 * Returns the {@link List} of {@link INode}s registered as children of this
	 * {@link IParent}.
	 * 
	 * @return the {@link List} of {@link INode}s registered as children of this
	 *         {@link IParent}
	 */
	public List<INode> getChildNodes();

	/**
	 * Returns the height of this {@link IParent}.
	 * 
	 * @return the height of this {@link IParent}
	 */
	public double getHeight();

	/**
	 * Returns the {@link INode} at the specified position (local to this
	 * {@link IParent}), or <code>null</code> if there is no {@link INode} at
	 * that position.
	 * 
	 * @param localPosition
	 * @return
	 */
	public INode getNodeAt(Point localPosition);

	/**
	 * Returns the width of this {@link IParent}.
	 * 
	 * @return the width of this {@link IParent}
	 */
	public double getWidth();

	/**
	 * Recursive descent layouting.
	 */
	public void layout();

	/**
	 * Reposition and resize children of this IParent.
	 */
	public void layoutChildren();

	/**
	 * Redraw child figures using the passed-in {@link GraphicsContext}.
	 */
	public void renderFigures(GraphicsContext gc);

	/**
	 * Sets the height of this {@link IParent} to the specified value.
	 * 
	 * @param width
	 */
	public void setHeight(double height);

	/**
	 * Sets the {@link Scene} which this {@link INode} belongs to.
	 * 
	 * @param scene
	 */
	public void setScene(Scene scene);

	/**
	 * Sets the width of this {@link IParent} to the specified value.
	 * 
	 * @param width
	 */
	public void setWidth(double width);

}
