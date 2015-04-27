/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.policies;

import java.util.ArrayList;
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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXHoverFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

// TODO: only applicable for FXRootPart and FXViewer
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
	 * The adapter role for the
	 * <code>Provider&lt;List&lt;IFXCreationMenuItem&gt;&gt;</code>.
	 */
	public static final String MENU_ITEM_PROVIDER_ROLE = "Provider<List<IFXCreationMenuItem>>";

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
	 * List of {@link IFXCreationMenuItem}s which can be constructed.
	 */
	private final List<IFXCreationMenuItem> items = new ArrayList<IFXCreationMenuItem>();

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

	/**
	 * The {@link HBox} in which all menu visuals are layed out.
	 */
	private HBox hbox;

	/**
	 * The {@link Group} managing the template visual.
	 */
	private Group templateGroup;

	@Override
	public void click(MouseEvent e) {
		// open menu on right click
		if (MouseButton.SECONDARY.equals(e.getButton())) {
			// close menu if already open
			if (isMenuOpen()) {
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
			if (isMenuOpen()) {
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
		// remove menu visuals
		getViewer().getScrollPane().getScrolledPane().getChildren()
				.remove(hbox);
	}

	private void create(IContentPart<Node, ? extends Node> contentParent,
			Object content) {
		IRootPart<Node, ? extends Node> root = getHost().getRoot();
		IViewer<Node> viewer = root.getViewer();

		// build create operation
		CreationPolicy<Node> creationPolicy = root
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		creationPolicy.init();
		creationPolicy.create(contentParent, content);

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
		templateGroup = new Group();
		refreshMenuItem();

		// highlighting
		templateGroup.setEffect(createDropShadowReflectionEffect(
				DROP_SHADOW_RADIUS, Color.TRANSPARENT));
		effectOnHover(
				templateGroup,
				createDropShadowReflectionEffect(DROP_SHADOW_RADIUS,
						HIGHLIGHT_COLOR));

		// register click action
		templateGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				onItemClick();
			}
		});
		// register scroll action
		templateGroup.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				nextElement(event.getDeltaY() < 0);
			}
		});

		return templateGroup;
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
		return (FXViewer) getHost().getRoot().getViewer();
	}

	public boolean isMenuOpen() {
		return hbox != null && hbox.getParent() != null;
	}

	private void nextElement(final boolean left) {
		if (left) {
			// show previous geometry
			currentItemIndex--;
			if (currentItemIndex < 0) {
				currentItemIndex = items.size() - 1;
			}
		} else {
			// show next geometry
			currentItemIndex++;
			if (currentItemIndex >= items.size()) {
				currentItemIndex = 0;
			}
		}
		refreshMenuItem();
	}

	private void onItemClick() {
		// compute width and height deltas to the content layer
		Node itemVisual = templateGroup.getChildren().get(0);
		Bounds bounds = itemVisual.getLayoutBounds();
		Bounds boundsInContent = ((FXRootPart) getHost().getRoot()).contentLayer
				.sceneToLocal(itemVisual.localToScene(bounds));
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

		// create the new semantic element
		IFXCreationMenuItem item = items.get(currentItemIndex);
		Object toCreate = item.createContent();
		create(item.findContentParent(getHost().getRoot()), toCreate);

		// relocate to final position
		FXTransformPolicy txPolicy = getViewer().getContentPartMap()
				.get(toCreate).getAdapter(FXTransformPolicy.class);
		txPolicy.init();
		txPolicy.setTransform(new AffineTransform(1, 0, 0, 1, x, y));
		getViewer().getDomain().execute(txPolicy.commit());
	}

	private void openMenu(final MouseEvent e) {
		// refresh menu items based on the provider
		refreshMenuItems();

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

	private void refreshMenuItem() {
		// exchange template visual
		templateGroup.getChildren().clear();
		templateGroup.getChildren().add(
				items.get(currentItemIndex).createVisual());
	}

	private void refreshMenuItems() {
		@SuppressWarnings("serial")
		List<IFXCreationMenuItem> menuItems = getHost()
				.<Provider<List<IFXCreationMenuItem>>> getAdapter(
						AdapterKey
								.get(new TypeToken<Provider<List<IFXCreationMenuItem>>>() {
								}, MENU_ITEM_PROVIDER_ROLE)).get();
		this.items.clear();
		this.items.addAll(menuItems);
		// compute max width and height
		maxWidth = 0;
		maxHeight = 0;
		for (IFXCreationMenuItem item : items) {
			Bounds bounds = item.createVisual().getLayoutBounds();
			if (bounds.getWidth() > maxWidth) {
				maxWidth = bounds.getWidth();
			}
			if (bounds.getHeight() > maxHeight) {
				maxHeight = bounds.getHeight();
			}
		}
		// ensure currentItemIndex is in bounds
		if (currentItemIndex >= this.items.size()) {
			currentItemIndex = 0;
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
