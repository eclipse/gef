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
import org.eclipse.gef4.swtfx.event.FocusTraversalDispatcher;
import org.eclipse.gef4.swtfx.event.IEventDispatchChain;
import org.eclipse.gef4.swtfx.event.IEventDispatcher;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseTrackDispatcher;

abstract public class AbstractNode implements INode {

	private static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	/**
	 * The local rotation angle.
	 */
	private Angle angle = Angle.fromRad(0);

	/**
	 * The content orientation, i.e. whether width depends on height or vice
	 * versa.
	 */
	private Orientation contentBias = Orientation.NONE;

	/**
	 * The {@link EventHandlerManager} dispatches events along an
	 * {@link IEventDispatchChain}.
	 */
	private EventHandlerManager dispatcher = new EventHandlerManager();

	/**
	 * Signals if this node is part of the focus traversal cycle.
	 */
	private boolean focusTraversable = true;

	/**
	 * Horizontal translation used for laying out the node.
	 */
	private double layoutX = 0;

	/**
	 * Vertical translation used for laying out the node.
	 */
	private double layoutY = 0;

	/**
	 * Maximum height constraint used for laying out the node.
	 */
	private double maxHeight = INode.USE_COMPUTED_SIZE;

	/**
	 * Maximum width used for laying out the node.
	 */
	private double maxWidth = INode.USE_COMPUTED_SIZE;

	/**
	 * Minimum height used for laying out the node.
	 */
	private double minHeight = INode.USE_COMPUTED_SIZE;

	/**
	 * Minimum width used for laying out the node.
	 */
	private double minWidth = INode.USE_COMPUTED_SIZE;

	/**
	 * The parent of this node.
	 */
	private IParent parent;

	/**
	 * The anchor point for local rotation and scaling.
	 */
	private Point pivot = new Point();

	/**
	 * Preferred height used for laying out the node.
	 */
	private double prefHeight = INode.USE_COMPUTED_SIZE;

	/**
	 * Preferred width used for laying out the node.
	 */
	private double prefWidth = INode.USE_COMPUTED_SIZE;

	/**
	 * Local horizontal scale factor.
	 */
	private double scaleX = 1;

	/**
	 * Local vertical scale factor.
	 */
	private double scaleY = 1;

	/**
	 * List of local transformations, applied after the other transformation
	 * attributes are applied.
	 */
	private List<AffineTransform> transforms = new LinkedList<AffineTransform>();

	/**
	 * Local horizontal translation.
	 */
	private double translateX = 0;

	/**
	 * Local vertical translation.
	 */
	private double translateY = 0;

	/**
	 * Visibility flag. FIXME: Has no effect, currently.
	 */
	private boolean visible = true;

	@Override
	public Point absoluteToLocal(Point absolute) {
		Point local = new Point();
		absoluteToLocal(absolute, local);
		return local;
	}

	@Override
	public void absoluteToLocal(Point absoluteIn, Point localOut) {
		AffineTransform tx = getLocalToAbsoluteTransform();

		// FIXME: Local coordinates are not exactly correct: when the mouse is
		// at the top of the window the local y coordinate is wrong by -4
		// pixels.

		// TODO: According to the type of transformation matrix (pure
		// translation? pure scaling? pure rotation?) we can transform the
		// coordinates by hand. We have to evaluate if that is actually faster
		// than computing the inverse matrix and multiplying it by the
		// coordinate vector (benchmark!).

		// if (tx.getScaleX() == 1 && tx.getScaleY() == 1 && tx.getShearX() == 0
		// && tx.getShearY() == 0) {
		// // just translation
		// double translateX = tx.getTranslateX();
		// double translateY = tx.getTranslateY();
		// org.eclipse.swt.graphics.Point location = getScene().getLocation();
		// location = getScene().toDisplay(location);
		// localOut.translate(location.x - translateX, location.y - translateY);
		// }

		try {
			localOut.setLocation(tx.inverseTransform(absoluteIn));
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
			// TODO: we have to assure that all transformations are invertible
		}
	}

	@Override
	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		dispatcher.addEventHandler(type, handler);
	}

	@Override
	public void autosize() {
		if (isResizable()) {
			double width, height;

			Orientation orientation = getContentBias();
			switch (orientation) {
			case NONE:
				// no dependence
				width = clamp(computePrefWidth(-1), computeMinWidth(-1),
						computeMaxWidth(-1));
				height = clamp(computePrefHeight(-1), computeMinHeight(-1),
						computeMaxHeight(-1));
				break;
			case HORIZONTAL:
				// height depends on width
				width = clamp(computePrefWidth(-1), computeMinWidth(-1),
						computeMaxWidth(-1));
				height = clamp(computePrefHeight(width),
						computeMinHeight(width), computeMaxHeight(width));
				break;
			case VERTICAL:
				// width depends on height
				height = clamp(computePrefHeight(-1), computeMinHeight(-1),
						computeMaxHeight(-1));
				width = clamp(computePrefWidth(height),
						computeMinWidth(height), computeMaxWidth(height));
				break;
			default:
				throw new IllegalStateException("Unknown Orientation: "
						+ orientation);
			}

			resize(width, height);
		}
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(
			final IEventDispatchChain tail) {
		tail.prepend(getEventDispatcher());
		tail.prepend(new MouseTrackDispatcher(this));
		tail.prepend(new FocusTraversalDispatcher(this));
		INode next = getParentNode();
		if (next != null) {
			return next.buildEventDispatchChain(tail);
		}
		return tail;
	}

	@Override
	public boolean contains(Point local) {
		return contains(local.x, local.y);
	}

	@Override
	public Rectangle getBoundsInParent() {
		Rectangle boundsInLocal = getBoundsInLocal();
		return boundsInLocal.getTransformed(getLocalToParentTransform())
				.getBounds();
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
	public double getLayoutX() {
		return layoutX;
	}

	@Override
	public double getLayoutY() {
		return layoutY;
	}

	@Override
	public AffineTransform getLocalToAbsoluteTransform() {
		AffineTransform tx = getLocalToParentTransform();
		// System.out.println("gLTAT: " + tx.getTranslateX() + ", "
		// + tx.getTranslateY());
		IParent parent = getParentNode();
		if (parent != null) {
			tx.preConcatenate(parent.getLocalToAbsoluteTransform());
		} else {
			org.eclipse.swt.graphics.Point location = getScene().getLocation();
			location = getScene().toDisplay(location);
			tx.translate(location.x, location.y);
		}
		return tx;
	}

	@Override
	public AffineTransform getLocalToParentTransform() {
		AffineTransform localToParentTx = new AffineTransform();

		Point pivot = getPivot();

		if (getParentNode() == null) {
			// we are root
			if (this instanceof IParent) {
				org.eclipse.swt.graphics.Point location = getScene()
						.getLocation();
				pivot = pivot.getTranslated(location.x, location.y);
			} else {
				// we have no parent, and we are no parent?
				throw new IllegalStateException(
						"This node is not part of the scene graph.");
			}
		}

		localToParentTx.translate(getTranslateX() + getLayoutX() + pivot.x,
				getTranslateY() + getLayoutY() + pivot.y);
		localToParentTx.rotate(getRotationAngle().rad());
		localToParentTx.scale(getScaleX(), getScaleY());
		localToParentTx.translate(-pivot.x, -pivot.y);

		for (AffineTransform tx : getTransforms()) {
			localToParentTx.concatenate(tx);
		}

		return localToParentTx;
	}

	@Override
	public double getMaxHeight() {
		return maxHeight;
	}

	@Override
	public double getMaxWidth() {
		return maxWidth;
	}

	@Override
	public double getMinHeight() {
		return minHeight;
	}

	@Override
	public double getMinWidth() {
		return minWidth;
	}

	@Override
	public IParent getParentNode() {
		return parent;
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
	public Scene getScene() {
		return parent.getScene();
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
		return getScene().getFocusTarget() == this;
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
	public boolean isPressed() {
		return getScene().getMouseTarget() == this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public Point localToAbsolute(double localX, double localY) {
		Point p = new Point(localX, localY);
		localToAbsolute(p, p);
		return p;
	}

	@Override
	public void localToAbsolute(double localX, double localY, Point absoluteOut) {
		absoluteOut.setLocation(localToAbsolute(localX, localY));
	}

	@Override
	public Point localToAbsolute(Point local) {
		return localToAbsolute(local.x, local.y);
	}

	@Override
	public void localToAbsolute(Point localIn, Point absoluteOut) {
		AffineTransform tx = getLocalToAbsoluteTransform();
		absoluteOut.setLocation(tx.getTransformed(localIn));
	}

	@Override
	public void localToParent(Point localIn, Point parentOut) {
		AffineTransform tx = getLocalToParentTransform();
		parentOut.setLocation(tx.getTransformed(localIn));
	}

	@Override
	public Point parentToLocal(double parentX, double parentY) {
		Point p = new Point(parentX, parentY);
		parentToLocal(p, p);
		return p;
	}

	@Override
	public void parentToLocal(double parentX, double parentY, Point localOut) {
		localOut.setLocation(parentToLocal(parentX, parentY));
	}

	@Override
	public Point parentToLocal(Point parent) {
		return parentToLocal(parent.x, parent.y);
	}

	@Override
	public void parentToLocal(Point parentIn, Point localOut) {
		AffineTransform localToParentTransform = getLocalToParentTransform();
		try {
			Point transformed = localToParentTransform
					.inverseTransform(parentIn);
			localOut.setLocation(transformed);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
			// TODO: we have to assure that all transformations are invertible
		}
	}

	@Override
	public void relocate(double x, double y) {
		setLayoutX(x - getLayoutBounds().getX());
		setLayoutY(y - getLayoutBounds().getY());
		// System.out.println("after relocate: " + this + "; " + getLayoutX()
		// + ", " + getLayoutY());
	}

	@Override
	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		dispatcher.removeEventHandler(type, handler);
	}

	@Override
	public void requestFocus() {
		getScene().setFocusTarget(this);
	}

	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		relocate(x, y);
		resize(width, height);
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
	public void setMaxHeight(double maxHeight) {
		this.maxHeight = maxHeight;
	}

	@Override
	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	@Override
	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	@Override
	public void setParentNode(IParent parent) {
		this.parent = parent;
	}

	@Override
	public void setPivot(Point pivot) {
		this.pivot = pivot;
	}

	@Override
	public void setPrefHeight(double prefHeight) {
		this.prefHeight = prefHeight;
	}

	@Override
	public void setPrefWidth(double prefWidth) {
		this.prefWidth = prefWidth;
	}

	@Override
	public void setRotationAngle(Angle angle) {
		this.angle.setRad(angle.rad());
	}

	@Override
	public void setScaleX(double scaleX) {
		/*
		 * TODO: Evaluate!
		 * 
		 * Maybe we should warn if scaleX is 0, because the expectation of the
		 * user could be that 0 resets the scale. (The additive identity element
		 * is 0 whereas the multiplicative identity element is 1.)
		 */
		// assert scaleX != 0;
		this.scaleX = scaleX;
	}

	@Override
	public void setScaleY(double scaleY) {
		// assert scaleY != 0; // because nobody wants to scale by 0
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

}
