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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * The ControlNode class wraps the {@link INode} interface around arbitrary SWT
 * {@link Control}s in order to be able to intermix GEF4 figures and SWT
 * controls. The ControlNode is not considered a ControlFigure, because an
 * {@link IFigure} has the responsibility to paint itself using the
 * {@link GraphicsContext} API, where as a ControlNode cannot paint itself, but
 * rather is painted by SWT.
 * 
 * @author mwienand
 * 
 */
public class ControlNode<T extends Control> extends AbstractNode {

	/**
	 * The wrapped {@link Control}.
	 */
	private T control;

	/**
	 * Used to dispatch events to the {@link Scene}.
	 */
	private SwtEventForwarder swtEventForwarder;

	/**
	 * Width assigned during layout.
	 */
	private double width;

	/**
	 * Height assigned during layout.
	 */
	private double height;

	/**
	 * Constructs a new {@link ControlNode} for the passed-in {@link Control}.
	 * An ControlFigure implements the {@link INode} interface for arbitrary SWT
	 * controls. This wrapper is used, so that the {@link INode} interface can
	 * be used as the central abstraction throughout the API.
	 * 
	 * @param control
	 */
	public ControlNode(T control) {
		this.control = control;
	}

	@Override
	public double computeMaxHeight(double width) {
		return Double.MAX_VALUE;
	}

	@Override
	public double computeMaxWidth(double height) {
		return Double.MAX_VALUE;
	}

	@Override
	public double computeMinHeight(double width) {
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		// TODO: evaluate if we need to check for USE_COMPUTED_SIZE here
		double ph = getPrefHeight();
		if (ph != INode.USE_COMPUTED_SIZE) {
			return ph;
		}

		org.eclipse.swt.graphics.Point size = control.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true);
		return size.y;
	}

	@Override
	public double computePrefWidth(double height) {
		// TODO: evaluate if we need to check for USE_COMPUTED_SIZE here
		double pw = getPrefWidth();
		if (pw != INode.USE_COMPUTED_SIZE) {
			return pw;
		}

		org.eclipse.swt.graphics.Point size = control.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true);
		return size.x;
	}

	@Override
	public boolean contains(double localX, double localY) {
		if (control.isDisposed()) {
			return false;
		}

		Point abs = localToDisplay(localX, localY);

		org.eclipse.swt.graphics.Point controlLocation = control.toControl(
				(int) abs.x, (int) abs.y);

		return controlLocation.x >= 0
				&& controlLocation.x <= control.getBounds().width
				&& controlLocation.y >= 0
				&& controlLocation.y <= control.getBounds().height;
	}

	/**
	 * @return the absolute bounds with
	 *         <code>location = layout-bounds.transformToAbsolute.location</code>
	 *         , and <code>size = layout-bounds.size</code>
	 */
	private Rectangle getAbsoluteBounds() {
		Rectangle layoutBounds = getLayoutBounds();
		return layoutBounds.getTransformed(getLocalToAbsoluteTransform())
				.getBounds();
	}

	@Override
	public Rectangle getBoundsInLocal() {
		return getLayoutBounds();
	}

	/**
	 * Returns the associated SWT {@link Control}.
	 * 
	 * @return the associated SWT {@link Control}
	 */
	public T getControl() {
		return control;
	}

	@Override
	public Rectangle getLayoutBounds() {
		return new Rectangle(0, 0, width, height);
	}

	@Override
	public AffineTransform getLocalToParentTransform() {
		AffineTransform parentToAbsoluteTransform = getParentNode()
				.getLocalToAbsoluteTransform();

		// extract parent-to-absolute rotation angle
		double[] m = parentToAbsoluteTransform.getMatrix();
		double rotRad = Math.atan2(m[1], m[0]);

		// count back that rotation and combine it with the other local
		// transformations
		setPivot(getLayoutBounds().getCenter());
		setRotationAngle(Angle.fromRad(-rotRad));
		return super.getLocalToParentTransform();
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void resize(double width, double height) {
		this.width = width;
		this.height = height;
		updateSwtBounds();
	}

	@Override
	public void setParentNode(IParent parent) {
		super.setParentNode(parent);
		if (swtEventForwarder != null) {
			swtEventForwarder.unregisterListeners();
		}
		swtEventForwarder = new SwtEventForwarder(control, parent.getScene());
	}

	public void updateSwtBounds() {
		Rectangle txBounds = getAbsoluteBounds();

		org.eclipse.swt.graphics.Point location = getScene().toDisplay(0, 0);
		txBounds.translate(-location.x, -location.y);

		control.setBounds((int) Math.ceil(txBounds.getX()),
				(int) Math.ceil(txBounds.getY()),
				(int) Math.ceil(txBounds.getWidth()),
				(int) Math.ceil(txBounds.getHeight()));
	}
}
