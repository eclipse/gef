package org.eclipse.gef4.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.fx.parts.FXHoverFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

// TODO: only applicable for FXRootPart
public class FXCreationMenuOnClickPolicy extends AbstractFXClickPolicy {

	private static Reflection createDropShadowReflectionEffect(
			double effectRadius, Color color) {
		DropShadow dropShadow = new DropShadow(effectRadius, color);
		Reflection reflection = new Reflection();
		reflection.setInput(dropShadow);
		return reflection;
	}

	private static boolean isNested(Parent parent, Node node) {
		while (node != null && node != parent) {
			node = node.getParent();
		}
		return node == parent;
	}

	/**
	 * Default {@link Paint} used for to fill the interior of the arrows.
	 */
	private static final Paint ARROW_FILL = Color.WHITE;

	/**
	 * Default {@link Paint} used for to stroke the outside of the arrows.
	 */
	private static final Paint ARROW_STROKE = Color.web("#5a61af");

	/**
	 * Default stroke width for the arrows.
	 */
	private static final double ARROW_STROKE_WIDTH = 2.5;

	/**
	 * Set of points used for the left (smaller as, <code>&lt;</code>) arrow.
	 */
	private static final Double[] LEFT_ARROW_POINTS = new Double[] { 10d, 0d,
			0d, 5d, 10d, 10d };

	/**
	 * Radius of the drop shadow effects.
	 */
	private static final double DROP_SHADOW_RADIUS = 5;

	/**
	 * Set of points used for the right (greater than, <code>&gt;</code>) arrow.
	 */
	private static final Double[] RIGHT_ARROW_POINTS = new Double[] { 0d, 0d,
			10d, 5d, 0d, 10d };

	/**
	 * The {@link Color} used for highlighting visuals.
	 */
	private static final Color HIGHLIGHT_COLOR = FXHoverFeedbackPart.DEFAULT_STROKE;

	/**
	 * List of {@link AbstractFXGeometricElement}s which can be constructed.
	 */
	private final List<AbstractFXGeometricElement<? extends IGeometry>> elements = new ArrayList<AbstractFXGeometricElement<? extends IGeometry>>();

	/**
	 * Stores the maximum element width.
	 */
	private double maxWidth = 0;

	/**
	 * Stores the maximum element height.
	 */
	private double maxHeight = 0;

	/**
	 * The index of the current item in the list of {@link #geometries}.
	 */
	private int currentItemIndex = 1;

	/**
	 * The visual used to render a geometry.
	 */
	private FXGeometryNode<IGeometry> geometryNode;

	/**
	 * The initial mouse position in the coordinate system of the scene of the
	 * {@link #getHost() host}.
	 */
	private Point initialMousePositionInScene;

	/**
	 * Stores the padding around visuals used to circumvent translation issues
	 * when applying a drop shadow effect.
	 */
	private final double padding = DROP_SHADOW_RADIUS + 1 + ARROW_STROKE_WIDTH
			* 2 + 1;

	private boolean isMenuOpen = false;

	private HBox hbox;

	{
		List<AbstractFXGeometricElement<? extends IGeometry>> defaultElements = new ArrayList<AbstractFXGeometricElement<? extends IGeometry>>();
		defaultElements.add(new FXGeometricShape(FXGeometricModel
				.createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1,
				0, 0), Color.WHITE, FXGeometricModel.GEF_SHADOW_EFFECT));
		defaultElements.add(new FXGeometricShape(FXGeometricModel
				.createEShapeGeometry(), new AffineTransform(1, 0, 0, 1, 100,
				22), FXGeometricModel.GEF_COLOR_BLUE,
				FXGeometricModel.GEF_SHADOW_EFFECT));
		defaultElements.add(new FXGeometricShape(FXGeometricModel
				.createCursorShapeGeometry(), new AffineTransform(1, 0, 0, 1,
				227, 45), Color.WHITE, 2, Color.BLACK,
				FXGeometricModel.GEF_SHADOW_EFFECT));
		setElements(defaultElements);
	}

	@Override
	public void click(MouseEvent e) {
		// open menu on right click
		if (MouseButton.SECONDARY.equals(e.getButton())) {
			// close menu if already open
			if (isMenuOpen) {
				closeMenu();
			}
			EventTarget target = e.getTarget();
			if (target instanceof Node) {
				initialMousePositionInScene = new Point(e.getSceneX(),
						e.getSceneY());
				openMenu(e);
			}
		} else if (MouseButton.PRIMARY.equals(e.getButton())) {
			// close menu if currently opened
			if (isMenuOpen) {
				EventTarget target = e.getTarget();
				if (target instanceof Node) {
					Node targetNode = (Node) target;
					if (!isNested(hbox, targetNode)) {
						closeMenu();
					}
				}
			}
		}
	}

	private void closeMenu() {
		// remove menu items
		FXViewer viewer = (FXViewer) getHost().getRoot().getViewer();
		viewer.getScrollPane().getScrolledPane().getChildren().remove(hbox);
		isMenuOpen = false;
	}

	protected void create(Object content) {
		IRootPart<Node, ? extends Node> root = getHost().getRoot();
		IViewer<Node> viewer = root.getViewer();

		// find model part
		IVisualPart<Node, ? extends Node> modelPart = root.getChildren().get(0);
		if (!(modelPart instanceof FXGeometricModelPart)) {
			throw new IllegalStateException("Cannot find FXGeometricModelPart.");
		}

		// build create operation
		CreationPolicy<Node> creationPolicy = root
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		creationPolicy.init();
		creationPolicy.create((FXGeometricModelPart) modelPart, content);

		// execute on stack
		viewer.getDomain().execute(creationPolicy.commit());

		closeMenu();
	}

	private Node createArrow(final boolean left) {
		// shape
		final Polygon arrow = new Polygon();
		arrow.getPoints().addAll(left ? LEFT_ARROW_POINTS : RIGHT_ARROW_POINTS);
		// style
		arrow.setStrokeWidth(ARROW_STROKE_WIDTH);
		arrow.setStroke(ARROW_STROKE);
		arrow.setFill(ARROW_FILL);
		// effect
		effectOnHover(arrow,
				new DropShadow(DROP_SHADOW_RADIUS, HIGHLIGHT_COLOR));
		// action
		arrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				nextElement(left);
			}
		});
		return arrow;
	}

	private Node createMenuItem() {
		// create visual
		geometryNode = new FXGeometryNode<IGeometry>();
		// geometryNode.setOpacity(0.5);
		// copy attributes from the current semantic element
		updateGeometryNode();

		// wrap geometry into group for the effect
		final Group effectGroup = new Group(geometryNode);

		// highlighting
		effectGroup.setEffect(createDropShadowReflectionEffect(
				DROP_SHADOW_RADIUS, Color.TRANSPARENT));
		effectOnHover(
				effectGroup,
				createDropShadowReflectionEffect(DROP_SHADOW_RADIUS,
						HIGHLIGHT_COLOR));

		// register click action
		effectGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				onItemClick();
			}
		});
		// register scroll action
		effectGroup.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				nextElement(event.getDeltaY() < 0);
			}
		});

		return effectGroup;
	}

	private void effectOnHover(final Node node, final Effect effect) {
		final Effect[] oldEffect = new Effect[1];
		node.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				oldEffect[0] = node.getEffect();
				node.setEffect(effect);
			}
		});
		node.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				node.setEffect(oldEffect[0]);
			}
		});
	}

	private FXViewer getViewer() {
		FXViewer viewer = (FXViewer) getHost().getRoot().getViewer();
		return viewer;
	}

	public boolean isMenuOpen() {
		return isMenuOpen;
	}

	private void nextElement(final boolean left) {
		if (left) {
			// show previous geometry
			currentItemIndex--;
			if (currentItemIndex < 0) {
				currentItemIndex = elements.size() - 1;
			}
		} else {
			// show next geometry
			currentItemIndex++;
			if (currentItemIndex >= elements.size()) {
				currentItemIndex = 0;
			}
		}
		updateGeometryNode();
	}

	private void onItemClick() {
		// get semantic element
		final AbstractFXGeometricElement<? extends IGeometry> geom = elements
				.get(currentItemIndex);

		// copy the semantic element
		AbstractFXGeometricElement<? extends IGeometry> toCreate = null;
		if (geom instanceof FXGeometricShape) {
			FXGeometricShape geomShape = (FXGeometricShape) geom;
			toCreate = new FXGeometricShape(geomShape.getGeometry(),
					geomShape.getTransform(), geomShape.getFill(),
					geomShape.getEffect());
			toCreate.setStroke(geomShape.getStroke());
			toCreate.setStrokeWidth(geomShape.getStrokeWidth());
		} else if (geom instanceof FXGeometricCurve) {
			FXGeometricCurve geomCurve = (FXGeometricCurve) geom;
			toCreate = new FXGeometricCurve(geomCurve.getWayPointsCopy()
					.toArray(new Point[] {}), geomCurve.getStroke(),
					geomCurve.getStrokeWidth(), geomCurve.getDashes(),
					geomCurve.getEffect());
		}

		// compute width and height deltas to the content layer
		Bounds bounds = geometryNode.getLayoutBounds();
		Bounds boundsInContent = ((FXRootPart) getHost().getRoot()).contentLayer
				.sceneToLocal(geometryNode.localToScene(bounds));
		double dx = bounds.getWidth() - boundsInContent.getWidth();
		double dy = bounds.getHeight() - boundsInContent.getHeight();

		// compute translation based on the bounds, scaling, and width/height
		// deltas
		AffineTransform contentsTransform = getViewer().getAdapter(
				ViewportModel.class).getContentsTransform();
		double x = boundsInContent.getMinX() - bounds.getMinX()
				/ contentsTransform.getScaleX() - dx / 2;
		double y = boundsInContent.getMinY() - bounds.getMinY()
				/ contentsTransform.getScaleY() - dy / 2;

		// set translation transformation to position the element
		toCreate.setTransform(new AffineTransform(1, 0, 0, 1, x, y));

		// create the new semantic element
		create(toCreate);
	}

	private void openMenu(final MouseEvent e) {
		// construct content pane and group
		Node leftArrow = createArrow(true);
		Node menuItem = createMenuItem();
		Node rightArrow = createArrow(false);

		hbox = new HBox();
		hbox.getChildren().addAll(wrapWithPadding(leftArrow, padding),
				wrapWithPadding(menuItem, padding, maxWidth, maxHeight),
				wrapWithPadding(rightArrow, padding));

		final Pane scrolledPane = getViewer().getScrollPane().getScrolledPane();
		scrolledPane.getChildren().add(hbox);
		isMenuOpen = true;

		hbox.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable,
					Bounds oldBounds, Bounds newBounds) {
				hbox.setTranslateX(-newBounds.getWidth() / 2);
				hbox.setTranslateY(-newBounds.getHeight() / 2);
				Point2D pos = scrolledPane.sceneToLocal(
						initialMousePositionInScene.x,
						initialMousePositionInScene.y);
				hbox.setLayoutX(pos.getX());
				hbox.setLayoutY(pos.getY());
			}
		});
	}

	public void setElements(
			Collection<AbstractFXGeometricElement<? extends IGeometry>> elements) {
		this.elements.clear();
		this.elements.addAll(elements);
		maxWidth = 0;
		maxHeight = 0;
		for (AbstractFXGeometricElement<? extends IGeometry> element : this.elements) {
			org.eclipse.gef4.geometry.planar.Rectangle bounds = element
					.getGeometry().getBounds();
			if (bounds.getWidth() > maxWidth) {
				maxWidth = bounds.getWidth();
			}
			if (bounds.getHeight() > maxHeight) {
				maxHeight = bounds.getHeight();
			}
		}
	}

	protected void updateGeometryNode() {
		// get semantic element
		final AbstractFXGeometricElement<? extends IGeometry> geom = elements
				.get(currentItemIndex);
		// copy attributes over the the visual
		geometryNode.setGeometry(geom.getGeometry());
		geometryNode.setStroke(geom.getStroke());
		geometryNode.setStrokeWidth(geom.getStrokeWidth());
		geometryNode.setEffect(geom.getEffect());
		if (geom instanceof FXGeometricShape) {
			geometryNode.setFill(((FXGeometricShape) geom).getFill());
		}
	}

	private StackPane wrapWithPadding(Node node, double padding) {
		return wrapWithPadding(node, padding,
				node.getLayoutBounds().getWidth(), node.getLayoutBounds()
						.getHeight());
	}

	private StackPane wrapWithPadding(Node node, double padding, double width,
			double height) {
		StackPane stack = new StackPane();
		stack.getChildren().addAll(
				new Rectangle(width + padding + padding, height + padding
						+ padding, Color.TRANSPARENT), node);
		return stack;
	}

}
