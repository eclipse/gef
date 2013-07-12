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

import java.awt.geom.NoninvertibleTransformException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.event.Event;
import org.eclipse.gef4.swtfx.event.EventHandlerManager;
import org.eclipse.gef4.swtfx.event.EventType;
import org.eclipse.gef4.swtfx.event.IEventDispatchChain;
import org.eclipse.gef4.swtfx.event.IEventDispatcher;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.SwtEventForwarder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The ControlFigure class wraps the {@link INode} interface around arbitrary
 * SWT {@link Control}s.
 * 
 * @author mwienand
 * 
 */
public class ControlNode<T extends Control> implements INode {

	/**
	 * Registers SWT event listeners and wraps the SWT events in GEF4 event
	 * objects.
	 */
	private SwtEventForwarder swtEventDispatcher;

	/**
	 * The wrapped {@link Control}.
	 */
	private T control;

	/**
	 * The {@link EventHandlerManager} manages the registering of event handlers
	 * and event filters. What's more, it dispatches incoming events to the
	 * registered handlers and filters.
	 */
	private EventHandlerManager dispatcher = new EventHandlerManager();

	/**
	 * Focus-traversable property.
	 */
	private boolean focusTraversable = true;

	/**
	 * Horizontal translation used for layouting the node.
	 */
	private double layoutX = 0;

	/**
	 * Vertical translation used for layouting the node.
	 */
	private double layoutY = 0;

	/**
	 * Pivot point for all local transformations (layout-x/y, translate-x/y,
	 * scale-x/y, rotation-angle). Before those transformations are applied, a
	 * translation by the pivot point is executed first. And after those
	 * transformations are applied, the translation by the pivot point is
	 * inverted.
	 */
	private Point pivot = new Point();

	/**
	 * Horizontal scale factor.
	 */
	private double scaleX = 1;

	/**
	 * Vertical scale factor.
	 */
	private double scaleY = 1;

	/**
	 * Horizontal translation.
	 */
	private double translateX = 0;

	/**
	 * Vertical translation.
	 */
	private double translateY = 0;

	/**
	 * Visibility property.
	 */
	private boolean visible = true;

	/**
	 * Rotation angle.
	 */
	private Angle angle = Angle.fromRad(0);

	/**
	 * List of additional transformations.
	 */
	private List<AffineTransform> transforms = new LinkedList<AffineTransform>();

	private double prefWidth = INode.USE_COMPUTED_SIZE;

	private double prefHeight = INode.USE_COMPUTED_SIZE;

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
		swtEventDispatcher = new SwtEventForwarder(this);
	}

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
		NodeUtil.absoluteToLocal(this, absoluteIn, localOut);
	}

	@Override
	public <E extends Event> void addEventFilter(EventType<E> type,
			IEventHandler<E> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <E extends Event> void addEventHandler(EventType<E> type,
			IEventHandler<E> handler) {
		dispatcher.addEventHandler(type, handler);
	}

	@Override
	public void autosize() {
		NodeUtil.autosize(this);
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain tail) {
		return NodeUtil.buildEventDispatchChain(this, tail);
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
		return getLayoutBounds().getHeight();
	}

	@Override
	public double computePrefWidth(double height) {
		return getLayoutBounds().getWidth();
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	@Override
	public Rectangle getBoundsInLocal() {
		Rectangle layoutBounds = getLayoutBounds();
		return layoutBounds;
	}

	@Override
	public Rectangle getBoundsInParent() {
		return NodeUtil.getBoundsInParent(this);
	}

	@Override
	public Orientation getContentBias() {
		return Orientation.NONE;
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
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public Rectangle getLayoutBounds() {
		double w, h;
		org.eclipse.swt.graphics.Point size = control.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true);

		// check preferred width
		if (prefWidth != INode.USE_COMPUTED_SIZE) {
			w = prefWidth;
		} else {
			w = size.x;
		}

		// check preferred height
		if (prefHeight != INode.USE_COMPUTED_SIZE) {
			h = prefHeight;
		} else {
			h = size.y;
		}

		// System.out.println("### " + size + " ### " + prefWidth + " x "
		// + prefHeight + " ### " + w + " x " + h);

		return new Rectangle(0, 0, w, h);
	}

	@Override
	public double getLayoutX() {
		return layoutX;
	}

	@Override
	public double getLayoutY() {
		return layoutY;
	}

	@Override
	public AffineTransform getLocalToAbsoluteTransform() {
		return NodeUtil.getLocalToAbsoluteTransform(this);
	}

	@Override
	public AffineTransform getLocalToParentTransform() {
		if (true) {
			return NodeUtil.getLocalToParentTransform(this);
		}

		AffineTransform localToAbsoluteTransform = getParentNode()
				.getLocalToAbsoluteTransform();
		try {
			return localToAbsoluteTransform.invert();
		} catch (NoninvertibleTransformException e) {
			// FIXME
			throw new IllegalStateException(
					"TODO: Assure that all transformations are invertable.");
		}
	}

	@Override
	public double getMaxHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaxWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMinHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMinWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IParent getParentNode() {
		Composite parent = control.getParent();
		if (parent instanceof IParent) {
			return (IParent) parent;
		}
		return null;
	}

	@Override
	public Point getPivot() {
		return pivot;
	}

	@Override
	public double getPrefHeight() {
		return prefHeight;
	}

	@Override
	public double getPrefWidth() {
		return prefWidth;
	}

	@Override
	public Angle getRotationAngle() {
		return angle;
	}

	@Override
	public double getScaleX() {
		return scaleX;
	}

	@Override
	public double getScaleY() {
		return scaleY;
	}

	@Override
	public List<AffineTransform> getTransforms() {
		return transforms;
	}

	@Override
	public double getTranslateX() {
		return translateX;
	}

	@Override
	public double getTranslateY() {
		return translateY;
	}

	@Override
	public boolean isFocused() {
		// TODO
		return false;
	}

	@Override
	public boolean isFocusTraversable() {
		return focusTraversable;
	}

	@Override
	public boolean isManaged() {
		return true;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void localToAbsolute(Point localIn, Point absoluteOut) {
		NodeUtil.localToAbsolute(this, localIn, absoluteOut);
	}

	@Override
	public void localToParent(Point localIn, Point parentOut) {
		NodeUtil.localToParent(this, localIn, parentOut);
	}

	@Override
	public void parentToLocal(Point parentIn, Point localOut) {
		NodeUtil.parentToLocal(this, parentIn, localOut);
	}

	@Override
	public void relocate(double x, double y) {
		NodeUtil.relocate(this, x, y);
		updateSwtBounds();
	}

	@Override
	public <E extends Event> void removeEventFilter(EventType<E> type,
			IEventHandler<E> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <E extends Event> void removeEventHandler(EventType<E> type,
			IEventHandler<E> handler) {
		dispatcher.removeEventHandler(type, handler);
	}

	@Override
	public boolean requestFocus() {
		return control.forceFocus();
	}

	@Override
	public void resize(double width, double height) {
		// FIXME: use separate width and height for the layout
		setPrefWidth(width);
		setPrefHeight(height);
		updateSwtBounds();
	}

	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		NodeUtil.resizeRelocate(this, x, y, width, height);
		updateSwtBounds();
	}

	@Override
	public void setFocusTraversable(boolean focusTraversable) {
		this.focusTraversable = focusTraversable;
	}

	@Override
	public void setLayoutX(double layoutX) {
		this.layoutX = layoutX;
	}

	@Override
	public void setLayoutY(double layoutY) {
		this.layoutY = layoutY;
	}

	@Override
	public void setMaxHeight(double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMinHeight(double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMinWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParentNode(IParent parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPivot(Point pivot) {
		this.pivot = pivot;
	}

	@Override
	public void setPrefHeight(double height) {
		prefHeight = height;
	}

	@Override
	public void setPrefWidth(double width) {
		prefWidth = width;
	}

	@Override
	public void setRotationAngle(Angle angle) {
		this.angle = angle;
	}

	@Override
	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	@Override
	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	@Override
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}

	@Override
	public void setTranslateY(double translateY) {
		this.translateY = translateY;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void updateSwtBounds() {
		Rectangle bounds = getLayoutBounds();
		/*
		 * Most probably, the bounds are located at 0, 0. But we use the top
		 * left corner so that the user can subclass ControlNode.
		 */
		Point offset = getParentNode().getLocalToAbsoluteTransform()
				.getTransformed(
						NodeUtil.getLocalToParentTransform(this)
								.getTransformed(bounds.getTopLeft()));

		IParent parentNode = getParentNode();
		if (parentNode instanceof AbstractParent) {
			org.eclipse.swt.graphics.Point location = ((AbstractParent) parentNode)
					.getLocation();
			offset.translate(-location.x, -location.y);
		}

		// System.out.println("update control bounds: " + offset + ", "
		// + bounds.getSize());

		// TODO: compute real width and height dependent on translation and
		// scaling

		// ceil because we rather like to have a very small gap between
		// controls, then not being able to fully see'em
		control.setBounds((int) Math.ceil(offset.x), (int) Math.ceil(offset.y),
				(int) Math.ceil(bounds.getWidth()),
				(int) Math.ceil(bounds.getHeight()));
	}

}
