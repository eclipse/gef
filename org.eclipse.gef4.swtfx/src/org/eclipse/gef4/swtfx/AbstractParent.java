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

	private List<INode> children = new LinkedList<INode>();
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
	private double maxHeight = INode.USE_COMPUTED_SIZE;
	private double maxWidth = INode.USE_COMPUTED_SIZE;
	private double minHeight = INode.USE_COMPUTED_SIZE;
	private double minWidth = INode.USE_COMPUTED_SIZE;
	private double prefHeight = INode.USE_COMPUTED_SIZE;
	private double prefWidth = INode.USE_COMPUTED_SIZE;
	private double width = 0;
	private double height = 0;
	private Orientation contentBias = Orientation.NONE;

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
	public void addChildNodes(INode... nodes) {
		for (INode node : nodes) {
			if (children.contains(node)) {
				throw new IllegalStateException(
						"The given INode ("
								+ node
								+ ") is already registered as a child of this IParent ("
								+ this + ").");
			}
			children.add(node);
			node.setParentNode(this);
		}
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
		// sum of top and bottom padding
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		// sum of left and right padding
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		// sum of top and bottom padding + children height
		Rectangle bbox = getLayoutBounds();
		return bbox.getHeight();
	}

	@Override
	public double computePrefWidth(double height) {
		// sum of left and right padding + children width
		return getLayoutBounds().getWidth();
	}

	@Override
	public boolean contains(double localX, double localY) {
		return getLayoutBounds().contains(localX, localY);
	}

	@Override
	public void controlToLocal(Point controlIn, Point localOut) {
		parentToLocal(controlIn, localOut);
	}

	public void doLayout() {
		doLayoutChildren();
		for (INode node : getChildNodes()) {
			if (node instanceof AbstractParent) {
				((AbstractParent) node).doLayout();
			}
		}
	}

	public void doLayoutChildren() {
		for (INode node : getChildNodes()) {
			if (node.isResizable() && node.isManaged()) {
				node.autosize();
			}
		}
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
	public List<INode> getChildNodes() {
		return children;
	}

	@Override
	public Orientation getContentBias() {
		return contentBias;
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public INode getFocusNode() {
		return swtEventDispatcher.getFocusTarget();
	}

	public double getHeight() {
		return height;
	}

	@Override
	public Rectangle getLayoutBounds() {
		// union children's bounds
		Rectangle unionedChildBounds = null;
		for (INode child : getChildNodes()) {
			Rectangle bounds = child.getBoundsInParent();
			if (unionedChildBounds == null) {
				unionedChildBounds = bounds;
			} else {
				unionedChildBounds.union(bounds);
			}
		}

		if (unionedChildBounds == null) {
			unionedChildBounds = new Rectangle();
		}

		// we do not apply our own transformations here
		return unionedChildBounds;
	}

	@Override
	public ILayouter getLayouter() {
		return layouter;
	}

	@Override
	public double getLayoutX() {
		// return getLocation().x;
		return layoutX;
	}

	@Override
	public double getLayoutY() {
		// return getLocation().y;
		return layoutY;
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
		return maxHeight;
	}

	public Dimension getMaxSize() {
		return new Dimension(getMaxWidth(), getMaxHeight());
	}

	@Override
	public double getMaxWidth() {
		return maxWidth;
	}

	@Override
	public double getMinHeight() {
		return minHeight;
	}

	public Dimension getMinSize() {
		return new Dimension(getMinWidth(), getMinHeight());
	}

	@Override
	public double getMinWidth() {
		return minWidth;
	}

	@Override
	public INode getNodeAt(Point localPosition) {
		Point parentLocal = localPosition; // just for the name
		Point childLocal = new Point(); // to store local positions

		ListIterator<INode> it = children.listIterator(children.size());
		while (it.hasPrevious()) {
			INode node = it.previous();
			node.parentToLocal(parentLocal, childLocal);
			if (node.contains(childLocal.x, childLocal.y)) {
				return node;
			}
		}

		return null; // no figure at that position
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

	public double getWidth() {
		return width;
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
	public void paintControl(PaintEvent e) {
		GraphicsContext g = new GraphicsContext(e.gc);

		// our rendering order is the reverse of SWT's
		for (INode node : getChildNodes()) {
			if (node instanceof IFigure) {
				IFigure figure = (IFigure) node;

				// save & guard the gc
				g.save();
				g.setUpGuard(); // TODO: evaluate if we need this, really

				// apply figures paint state
				// XXX: we should not be accessing the paint state, but rather
				// do
				// the appliance in the figure's paint method, which should be
				// final
				// and hookable via doPaint
				g.pushState(figure.getPaintStateByReference().getCopy());

				g.setTransform(figure.getLocalToAbsoluteTransform());
				org.eclipse.swt.graphics.Point location = getLocation();
				g.translate(-location.x, -location.y);

				// org.eclipse.swt.graphics.Point location = getLocation();
				// g.translate(-location.x, -location.y);

				// // apply correct transformations (TODO: review)
				// if (getParentNode() == null) {
				// // root takes into account its own transformations, too
				// g.setTransform(figure.getLocalToParentTransform()
				// .preConcatenate(getLocalToParentTransform()));
				// } else {
				// // transform to here
				// g.setTransform(figure.getLocalToParentTransform());
				// }

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
		updateSwtBounds();
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
	public void requestRedraw() {
		super.redraw();
	}

	@Override
	public void resize(double width, double height) {
		setWidth(width);
		setHeight(height);
		updateSwtBounds();
	}

	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		NodeUtil.resizeRelocate(this, x, y, width, height);
	}

	@Override
	public boolean setFocusNode(INode focusNode) {
		if (focusNode == null) {
			swtEventDispatcher.setFocusTarget(null);
			return true;
		}

		if (focusNode.getParentNode() != this) {
			throw new IllegalArgumentException(
					"The given IFigure is no child of this Group!");
		}
		if (forceFocus()) {
			swtEventDispatcher.setFocusTarget(focusNode);
			return true;
		}
		return false;
	}

	@Override
	public void setFocusTraversable(boolean focusTraversable) {
		this.focusTraversable = focusTraversable;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public void setLayouter(ILayouter layouter) {
		this.layouter = layouter;
	}

	@Override
	public void setLayoutX(double layoutX) {
		// setLocation((int) layoutX, (int) getLayoutY());
		this.layoutX = layoutX;
	}

	@Override
	public void setLayoutY(double layoutY) {
		// setLocation((int) getLayoutX(), (int) layoutY);
		this.layoutY = layoutY;
	}

	@Override
	public void setMaxHeight(double height) {
		maxHeight = height;
	}

	@Override
	public void setMaxWidth(double width) {
		maxWidth = width;
	}

	@Override
	public void setMinHeight(double height) {
		minHeight = height;
	}

	@Override
	public void setMinWidth(double width) {
		minWidth = width;
	}

	@Override
	public void setPivot(Point p) {
		pivot.setLocation(p);
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

	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return "Pane";
	}

	@Override
	public void updateSwtBounds() {
		// System.out.println("updateSwtBounds(" + this + "@"
		// + System.identityHashCode(this) + ", lxy = " + getLayoutX()
		// + ", " + getLayoutY() + "; lwh = " + getWidth() + " x "
		// + getHeight() + " :: lb = " + getLayoutBounds());

		Rectangle bounds = getBoundsInLocal().getTransformed(
				getLocalToAbsoluteTransform()).getBounds();

		IParent parentNode = getParentNode();
		if (parentNode instanceof AbstractParent) {
			org.eclipse.swt.graphics.Point location = ((AbstractParent) parentNode)
					.getLocation();
			bounds.translate(-location.x, -location.y);
		}

		setBounds((int) Math.ceil(bounds.getX()),
				(int) Math.ceil(bounds.getY()),
				(int) Math.ceil(bounds.getWidth()),
				(int) Math.ceil(bounds.getHeight()));

		// Point location = new Point();
		// Composite parent = getParent();
		// if (parent != null) {
		// org.eclipse.swt.graphics.Point pt = parent.getLocation();
		// location.setLocation(-pt.x, -pt.y);
		// }

		// AffineTransform tx = getLocalToAbsoluteTransform();
		// Rectangle untransformedBounds = new Rectangle(location.x, location.y,
		// getWidth(), getHeight());
		// Rectangle txBounds =
		// untransformedBounds.getTransformed(tx).getBounds();

		// System.out.println("this (" + System.identityHashCode(this)
		// + ") SWT bounds = " + txBounds);

		// setBounds((int) txBounds.getX(), (int) txBounds.getY(),
		// (int) txBounds.getWidth(), (int) txBounds.getHeight());

		/*
		 * 1. getWidth() and getHeight() deliver our width and height.
		 * 
		 * 2. Translate and scale these according to translation and scale
		 * properties.
		 * 
		 * Now the unclear part:
		 * 
		 * 3. Take into account the translation and scaling of all parents?
		 * 
		 * Do we have to take into account their transformations here? Or do we
		 * have to use a separate layout structure?
		 */
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if (swtEventDispatcher != null) {
			swtEventDispatcher.removeListeners();
		}
	}

}
