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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.event.EventHandlerManager;
import org.eclipse.gef4.swtfx.event.EventType;
import org.eclipse.gef4.swtfx.event.IEventDispatchChain;
import org.eclipse.gef4.swtfx.event.IEventDispatcher;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.SwtEventTargetSelector;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The AbstractParent is the provided abstract {@link IParent} implementation.
 * If you want to create your own {@link INode} which can be embedded into the
 * SWT widget hierarchy, subclassing AbstractParent is the way to go!
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractParent extends Canvas implements IParent,
		PaintListener, DisposeListener {

	private List<IFigure> figures = new LinkedList<IFigure>();
	private EventHandlerManager dispatcher = new EventHandlerManager();
	private SwtEventTargetSelector swtEventDispatcher;
	private boolean focusTraversable = true;
	private double layoutX = 0;
	private double layoutY = 0;
	private Point pivot = new Point();
	private double scaleX = 1;
	private double scaleY = 1;
	private double translateX = 0;
	private double translateY = 0;
	private boolean visible = true;
	private Angle angle = Angle.fromRad(0);
	private List<AffineTransform> transforms = new LinkedList<AffineTransform>();
	private ILayouter layouter;

	public AbstractParent(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(this);
		addDisposeListener(this);
		swtEventDispatcher = new SwtEventTargetSelector(this);
	}

	@Override
	public void absoluteToControl(Point absoluteIn, Point controlOut) {
		org.eclipse.swt.graphics.Point control = toControl((int) absoluteIn.x,
				(int) absoluteIn.y);
		controlOut.setLocation(control.x, control.y);
	}

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
		absoluteToControl(absoluteIn, localOut);
		controlToLocal(localOut, localOut);
	}

	@Override
	public <T extends org.eclipse.gef4.swtfx.event.Event> void addEventFilter(
			EventType<T> type, IEventHandler<T> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <T extends org.eclipse.gef4.swtfx.event.Event> void addEventHandler(
			EventType<T> type, IEventHandler<T> handler) {
		dispatcher.addEventHandler(type, handler);
	}

	@Override
	public void addFigures(IFigure... figures) {
		for (IFigure f : figures) {
			this.figures.add(f);
			f.setParentNode(this);
		}
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain tail) {
		return NodeUtil.buildEventDispatchChain(this, tail);
	}

	@Override
	public double computeMaxHeight(double width) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeMaxWidth(double height) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeMinHeight(double width) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double computePrefWidth(double height) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	@Override
	public void controlToLocal(Point controlIn, Point localOut) {
		parentToLocal(controlIn, localOut);
	}

	@Override
	public Rectangle getBoundsInLocal() {
		return getLayoutBounds();
	}

	@Override
	public Rectangle getBoundsInParent() {
		return NodeUtil.getBoundsInParent(this);
	}

	@Override
	public Orientation getContentBias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public IFigure getFigureAt(Point localPosition) {
		Point nodeLocal = localPosition; // just for the name
		Point figureLocal = new Point(); // to store local positions

		ListIterator<IFigure> it = figures.listIterator(figures.size());
		while (it.hasPrevious()) {
			IFigure figure = it.previous();
			figure.parentToLocal(nodeLocal, figureLocal);
			if (figure.contains(figureLocal.x, figureLocal.y)) {
				return figure;
			}
		}

		return null; // no figure at that position
	}

	@Override
	public List<IFigure> getFigures() {
		return figures;
	}

	@Override
	public IFigure getFocusFigure() {
		return swtEventDispatcher.getFocusTarget();
	}

	@Override
	public Rectangle getLayoutBounds() {
		// TODO
		org.eclipse.swt.graphics.Point size = getSize();
		return new Rectangle(0, 0, size.x, size.y);
	}

	@Override
	public ILayouter getLayouter() {
		return layouter;
	}

	@Override
	public double getLayoutX() {
		return getLocation().x;
		// return layoutX;
	}

	@Override
	public double getLayoutY() {
		return getLocation().y;
		// return layoutY;
	}

	@Override
	public AffineTransform getLocalToAbsoluteTransform() {
		return NodeUtil.getLocalToAbsoluteTransform(this);
	}

	@Override
	public AffineTransform getLocalToParentTransform() {
		return NodeUtil.getLocalToParentTransform(this);
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
		Composite parent = getParent();
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
	public Dimension getPreferredSize() {
		return getLayoutBounds().getSize();
	}

	@Override
	public double getPrefHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPrefWidth() {
		// TODO Auto-generated method stub
		return 0;
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
	public Composite getSwtComposite() {
		return this;
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
		return isFocusControl() && swtEventDispatcher.getFocusTarget() == null;
	}

	@Override
	public boolean isFocusTraversable() {
		return focusTraversable;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void layout() {
		System.out.println("layout()");
		super.layout();
	}

	@Override
	public void layout(boolean changed) {
		System.out.println("layout(" + changed + ")");
		super.layout(changed);
	}

	@Override
	public void layout(boolean changed, boolean all) {
		System.out.println("layout(" + changed + ", " + all + ")");
		super.layout(changed, all);
	}

	@Override
	public void layout(Control[] changed) {
		System.out.println("layout(" + changed.length + " controls)");
		super.layout(changed);
	}

	@Override
	public void layout(Control[] changed, int flags) {
		System.out.println("layout(" + changed.length + " controls, " + flags
				+ ")");
		super.layout(changed, flags);
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
	public void paintControl(PaintEvent e) {
		GraphicsContext g = new GraphicsContext(e.gc);

		// our rendering order is the reverse of SWT's
		for (IFigure figure : getFigures()) {
			// save & guard the gc
			g.save();
			g.setUpGuard(); // TODO: evaluate if we need this, really

			// apply figures paint state
			// XXX: we should not be accessing the paint state, but rather do
			// the appliance in the figure's paint method, which should be final
			// and hookable via doPaint
			g.pushState(figure.getPaintStateByReference().getCopy());

			// apply correct transformations (TODO: review)
			if (getParentNode() == null) {
				// root takes into account its own transformations, too
				g.setTransform(figure.getLocalToParentTransform()
						.preConcatenate(getLocalToParentTransform()));
			} else {
				// transform to here
				g.setTransform(figure.getLocalToParentTransform());
			}

			// actually paint it
			figure.paint(g);

			// restore the gc & take down guard
			g.restore();
			try {
				g.takeDownGuard();
			} catch (IllegalStateException x) {
				throw new IllegalStateException(
						"Did you forget to call restore() in your drawing code?",
						x);
			}
			g.restore();
		}

		g.cleanUp();
	}

	@Override
	public void parentToLocal(Point parentIn, Point localOut) {
		NodeUtil.parentToLocal(this, parentIn, localOut);
	}

	@Override
	public void relocate(double x, double y) {
		NodeUtil.relocate(this, x, y);
	}

	@Override
	public <T extends org.eclipse.gef4.swtfx.event.Event> void removeEventFilter(
			EventType<T> type, IEventHandler<T> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <T extends org.eclipse.gef4.swtfx.event.Event> void removeEventHandler(
			EventType<T> type, IEventHandler<T> handler) {
		dispatcher.removeEventHandler(type, handler);
	}

	@Override
	public boolean requestFocus() {
		return forceFocus();
	}

	@Override
	public void requestLayout() {
		/*
		 * TODO: Figure out which coordinates to use here. I think the local
		 * coordinate system of this IParent might work best. To be able to do
		 * that, we need to transform SWT control coordinates to local
		 * coordinates and transform IFigure coordinates to the parent
		 * coordinate system. (I think.)
		 */

		// collect SWT children (controls)
		LinkedList<Control> layoutableControls = new LinkedList<Control>();
		for (Control c : getChildren()) {
			if (c instanceof IParent) {
				System.err
						.println("error: requestLayout() not implemented for nested IParents");
				/*
				 * TODO: We need to compute the layout through the full
				 * hierarchy, so come up with a fancy recursive solution!
				 */
			}
			layoutableControls.add(c);
			org.eclipse.swt.graphics.Point prefSize = c.computeSize(
					SWT.DEFAULT, SWT.DEFAULT, true);
			layouter.setPreferredLayoutBounds(c, new Rectangle(0, 0,
					prefSize.x, prefSize.y));
		}

		// collect GEF4 children (figures)
		LinkedList<IFigure> layoutableFigures = new LinkedList<IFigure>();
		for (IFigure f : figures) {
			layoutableFigures.add(f);
			Dimension prefSize = f.getPreferredSize();
			layouter.setPreferredLayoutBounds(f, new Rectangle(new Point(),
					prefSize));
		}

		// do the layouting
		layouter.layout();

		for (Control c : layoutableControls) {
			Rectangle bounds = layouter.getComputedLayoutBounds(c);
			c.setLocation((int) bounds.getX(), (int) bounds.getY());
			c.setSize((int) bounds.getWidth(), (int) bounds.getHeight());
		}

		layouter.reset();
	}

	@Override
	public void requestRedraw() {
		super.redraw();
	}

	@Override
	public void resize(double width, double height) {
		setSize((int) width, (int) height);
	}

	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		NodeUtil.resizeRelocate(this, x, y, width, height);
	}

	@Override
	public boolean setFocusFigure(IFigure focusFigure) {
		if (focusFigure == null) {
			swtEventDispatcher.setFocusTarget(null);
			return true;
		}

		if (focusFigure.getParentNode() != this) {
			throw new IllegalArgumentException(
					"The given IFigure is no child of this Group!");
		}
		if (forceFocus()) {
			swtEventDispatcher.setFocusTarget(focusFigure);
			return true;
		}
		return false;
	}

	@Override
	public void setFocusTraversable(boolean focusTraversable) {
		this.focusTraversable = focusTraversable;
	}

	@Override
	public void setLayouter(ILayouter layouter) {
		this.layouter = layouter;
	}

	@Override
	public void setLayoutX(double layoutX) {
		setLocation((int) layoutX, (int) getLayoutY());
		// this.layoutX = layoutX;
	}

	@Override
	public void setLayoutY(double layoutY) {
		setLocation((int) getLayoutX(), (int) layoutY);
		// this.layoutY = layoutY;
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
	public void setPivot(Point p) {
		pivot.setLocation(p);
	}

	@Override
	public void setPrefHeight(double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrefWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRotationAngle(Angle angle) {
		this.angle.setRad(angle.rad());
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

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if (swtEventDispatcher != null) {
			swtEventDispatcher.removeListeners();
		}
	}

}
