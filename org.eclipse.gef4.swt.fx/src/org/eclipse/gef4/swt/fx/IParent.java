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
package org.eclipse.gef4.swt.fx;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.swt.widgets.Composite;

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
	 * <p>
	 * Transforms the first passed-in {@link Point} from absolute screen
	 * coordinates to SWT control coordinates. The result is stored in the
	 * second passed-in {@link Point} which therefore may not be
	 * <code>null</code>.
	 * </p>
	 * <p>
	 * TODO: Hide all control coordinate related methods.
	 * </p>
	 * 
	 * @param absoluteIn
	 * @param controlOut
	 */
	public void absoluteToControl(Point absoluteIn, Point controlOut);

	/**
	 * Appends the given {@link IFigure}s to this {@link IParent}'s figures
	 * list.
	 * 
	 * @param figures
	 */
	public void addFigures(IFigure... figures);

	/**
	 * Transforms the first passed-in {@link Point} from SWT control coordinates
	 * to local coordinates. The result is stored in the second passed-in
	 * {@link Point} which therefore may not be <code>null</code>.
	 * 
	 * @param controlIn
	 * @param localOut
	 */
	public void controlToLocal(Point controlIn, Point localOut);

	/**
	 * Returns the {@link IFigure} at the specified position (local to this
	 * {@link IParent}), or <code>null</code> if there is no {@link IFigure} at
	 * that position.
	 * 
	 * @param localPosition
	 * @return the {@link IFigure} at the specified position, or
	 *         <code>null</code> if there is no {@link IFigure} at that position
	 */
	public IFigure getFigureAt(Point localPosition);

	/**
	 * Returns the {@link List} of {@link IFigure}s registered as children of
	 * this {@link IParent}.
	 * 
	 * @return the {@link List} of {@link IFigure}s registered as children of
	 *         this {@link IParent}
	 */
	public List<IFigure> getFigures();

	/**
	 * Returns the currently selected focus figure, i.e. the {@link IFigure}
	 * which has keyboard focus, or <code>null</code> if no {@link IFigure}
	 * associated with this {@link IParent} currently has keyboard focus.
	 * 
	 * @return the child {@link IFigure} of this {@link IParent} that currently
	 *         has keyboard focus, or <code>null</code> if no figure has
	 *         keyboard focus
	 */
	public IFigure getFocusFigure();

	/**
	 * Returns the ILayouter responsible for laying out the children of this
	 * {@link IParent}.
	 * 
	 * @return the ILayouter responsible for laying out the children of this
	 *         {@link IParent}
	 */
	public ILayouter getLayouter();

	public Composite getSwtComposite();

	/**
	 * Asks this {@link IParent} to layout its children.
	 */
	public void requestLayout();

	/**
	 * Asks this {@link IParent} to redraw.
	 */
	public void requestRedraw();

	/**
	 * Asks this {@link IParent} to give keyboard focus to the passed-in
	 * {@link IFigure} which has to be a child of this {@link IParent}. Returns
	 * <code>true</code> if keyboard focus could be assigned, otherwise
	 * <code>false</code>.
	 * 
	 * @param focusFigure
	 * @return <code>true</code> if keyboard focus could be assigned, otherwise
	 *         <code>false</code>
	 */
	public boolean setFocusFigure(IFigure focusFigure);

	public void setLayouter(ILayouter layouter);

}
