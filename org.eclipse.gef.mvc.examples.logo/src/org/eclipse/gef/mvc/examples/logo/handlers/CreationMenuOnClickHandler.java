/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;

/**
 * The {@link CreationMenuOnClickHandler} displays a context menu that can be
 * used to create content.
 *
 * @author wienand
 *
 */
// TODO: only applicable for LayeredRootPart and InfiniteCanvasViewer
public class CreationMenuOnClickHandler extends AbstractHandler implements IOnClickHandler {

	/**
	 * An {@link ICreationMenuItem} can be displayed by an
	 * {@link CreationMenuOnClickHandler}.
	 *
	 * @author wienand
	 *
	 */
	// TODO: re-use content part to retrieve visual
	public static interface ICreationMenuItem {

		/**
		 * Returns a newly created content element that is added to the viewer
		 * when this menu item is selected.
		 *
		 * @return The content element that is created when this menu item is
		 *         selected.
		 */
		public Object createContent();

		/**
		 * Returns the visual for this menu item.
		 *
		 * @return The visual for this menu item.
		 */
		public Node createVisual();

	}

	/**
	 * The adapter role for the
	 * <code>Provider&lt;List&lt;IFXCreationMenuItem&gt;&gt;</code>.
	 */
	public static final String MENU_ITEM_PROVIDER_ROLE = "Provider<List<ICreationMenuItem>>";

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
	private static final Double[] LEFT_ARROW_POINTS = new Double[] { 10d, 0d, 0d, 5d, 10d, 10d };

	/**
	 * Radius of the drop shadow effects.
	 */
	private static final double DROP_SHADOW_RADIUS = 5;

	/**
	 * Set of points used for the right (greater than, <code>&gt;</code>) arrow.
	 */
	private static final Double[] RIGHT_ARROW_POINTS = new Double[] { 0d, 0d, 10d, 5d, 0d, 10d };

	private static Reflection createDropShadowReflectionEffect(double effectRadius, Color color) {
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
	 * List of {@link ICreationMenuItem}s which can be constructed.
	 */
	private final List<ICreationMenuItem> items = new ArrayList<>();

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
	private final double padding = DROP_SHADOW_RADIUS + 1 + ARROW_STROKE_WIDTH * 2 + 1;

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
				initialMousePositionInScene = new Point(e.getSceneX(), e.getSceneY());
				// query menu items and reset index
				refreshMenuItems();
				setCurrentItemIndex(0);
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
		getViewer().getCanvas().getScrolledOverlayGroup().getChildren().remove(hbox);
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
		effectOnHover(arrow, new DropShadow(DROP_SHADOW_RADIUS, getHighlightColor()));
		// action
		arrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				traverse(left);
			}
		});
		return arrow;
	}

	private Node createMenuItem() {
		// create visual
		templateGroup = new Group();
		updateTemplateVisual();

		// highlighting
		templateGroup.setEffect(createDropShadowReflectionEffect(DROP_SHADOW_RADIUS, Color.TRANSPARENT));
		effectOnHover(templateGroup, createDropShadowReflectionEffect(DROP_SHADOW_RADIUS, getHighlightColor()));

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
				traverse(event.getDeltaY() < 0);
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

	/**
	 * Returns the index of the currently displayed menu item.
	 *
	 * @return The index of the currently displayed menu item.
	 */
	protected int getCurrentItemIndex() {
		return currentItemIndex;
	}

	/**
	 * Returns the {@link Color} that is used to stroke hover feedback.
	 *
	 * @return The {@link Color} that is used to stroke hover feedback.
	 */
	@SuppressWarnings("serial")
	protected Color getHighlightColor() {
		Provider<Color> hoverFeedbackColorProvider = getViewer()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Color>>() {
				}, DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_COLOR_PROVIDER));
		return hoverFeedbackColorProvider == null ? DefaultHoverFeedbackPartFactory.DEFAULT_HOVER_FEEDBACK_COLOR
				: hoverFeedbackColorProvider.get();
	}

	/**
	 * Returns the list containing the {@link ICreationMenuItem}s that are
	 * displayed by this policy.
	 *
	 * @return the list containing the {@link ICreationMenuItem}s that are
	 *         displayed by this policy.
	 */
	protected List<ICreationMenuItem> getItems() {
		return items;
	}

	/**
	 * Returns the {@link InfiniteCanvasViewer} in which to open the creation
	 * menu.
	 *
	 * @return The {@link InfiniteCanvasViewer} in which to open the creation
	 *         menu.
	 */
	protected InfiniteCanvasViewer getViewer() {
		return (InfiniteCanvasViewer) getHost().getRoot().getViewer();
	}

	/**
	 * Returns <code>true</code> if the creation menu is currently open.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the creation menu is currently open,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isMenuOpen() {
		return hbox != null && hbox.getParent() != null;
	}

	/**
	 * Callback method called when an item is clicked.
	 */
	protected void onItemClick() {
		// compute width and height deltas to the content layer
		Node itemVisual = templateGroup.getChildren().get(0);
		Bounds bounds = itemVisual.getLayoutBounds();
		Bounds boundsInContent = getHost().getRoot().getVisual().sceneToLocal(itemVisual.localToScene(bounds));
		double dx = bounds.getWidth() - boundsInContent.getWidth();
		double dy = bounds.getHeight() - boundsInContent.getHeight();

		// compute translation based on the bounds, scaling, and width/height
		// deltas
		Affine contentsTransform = getViewer().getCanvas().contentTransformProperty().get();
		double x = boundsInContent.getMinX() - bounds.getMinX() / contentsTransform.getMxx() - dx / 2;
		double y = boundsInContent.getMinY() - bounds.getMinY() / contentsTransform.getMyy() - dy / 2;

		// close the creation menu
		closeMenu();

		// create the new semantic element
		ICreationMenuItem item = items.get(currentItemIndex);
		Object toCreate = item.createContent();

		// build create operation
		IRootPart<? extends Node> root = getHost().getRoot();
		CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
		creationPolicy.init();
		IContentPart<? extends Node> contentPart = creationPolicy.create(toCreate, root,
				root.getContentPartChildren().size(), HashMultimap.<IContentPart<? extends Node>, String> create(),
				false, false);

		// relocate to final position
		TransformPolicy txPolicy = contentPart.getAdapter(TransformPolicy.class);
		txPolicy.init();
		txPolicy.setTransform(new AffineTransform(1, 0, 0, 1, x, y));

		// assemble operations
		ReverseUndoCompositeOperation rev = new ReverseUndoCompositeOperation("CreateOnClick");
		rev.add(creationPolicy.commit());
		rev.add(txPolicy.commit());

		try {
			getViewer().getDomain().execute(rev, new NullProgressMonitor());
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens the creation menu.
	 *
	 * @param e
	 *            The {@link MouseEvent} that activated the creation menu.
	 */
	protected void openMenu(final MouseEvent e) {
		// compute max width and height
		double maxWidth = 0;
		double maxHeight = 0;
		for (ICreationMenuItem item : items) {
			Bounds bounds = item.createVisual().getLayoutBounds();
			if (bounds.getWidth() > maxWidth) {
				maxWidth = bounds.getWidth();
			}
			if (bounds.getHeight() > maxHeight) {
				maxHeight = bounds.getHeight();
			}
		}

		// construct content pane and group
		Node leftArrow = createArrow(true);
		Node menuItem = createMenuItem();
		Node rightArrow = createArrow(false);

		hbox = new HBox();
		hbox.getChildren().addAll(wrapWithPadding(leftArrow, padding),
				wrapWithPadding(menuItem, padding, maxWidth, maxHeight), wrapWithPadding(rightArrow, padding));

		// place into overlay group
		final Group overlayGroup = getViewer().getCanvas().getScrolledOverlayGroup();
		overlayGroup.getChildren().add(hbox);

		hbox.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
				Affine contentTransform = getViewer().getCanvas().getContentTransform();
				hbox.setTranslateX(-newBounds.getWidth() / 2);
				hbox.setTranslateY(-newBounds.getHeight() / 2);
				hbox.setScaleX(contentTransform.getMxx());
				hbox.setScaleY(contentTransform.getMyy());
				Point2D pos = overlayGroup.sceneToLocal(initialMousePositionInScene.x, initialMousePositionInScene.y);
				hbox.setLayoutX(pos.getX());
				hbox.setLayoutY(pos.getY());
			}
		});
	}

	/**
	 * Refreshes the menu. Queries the menu items using the provider that is
	 * registered under the {@link #MENU_ITEM_PROVIDER_ROLE}.
	 */
	protected void refreshMenuItems() {
		@SuppressWarnings("serial")
		List<ICreationMenuItem> menuItems = getHost().<Provider<List<ICreationMenuItem>>> getAdapter(
				AdapterKey.get(new TypeToken<Provider<List<ICreationMenuItem>>>() {
				}, MENU_ITEM_PROVIDER_ROLE)).get();
		List<ICreationMenuItem> items = getItems();
		items.clear();
		items.addAll(menuItems);
	}

	/**
	 * Changes the displayed menu item to the item at the given index.
	 *
	 * @param currentItemIndex
	 *            The index of the menu item to show.
	 */
	protected void setCurrentItemIndex(int currentItemIndex) {
		this.currentItemIndex = currentItemIndex;
	}

	/**
	 * Traverses the menu items.
	 *
	 * @param previous
	 *            <code>true</code> to show the previous item,
	 *            <code>false</code> to show the next item.
	 */
	protected void traverse(final boolean previous) {
		if (previous) {
			// show previous geometry
			setCurrentItemIndex(getCurrentItemIndex() - 1);
			if (getCurrentItemIndex() < 0) {
				setCurrentItemIndex(items.size() - 1);
			}
		} else {
			// show next geometry
			setCurrentItemIndex(getCurrentItemIndex() + 1);
			if (getCurrentItemIndex() >= items.size()) {
				setCurrentItemIndex(0);
			}
		}
		updateTemplateVisual();
	}

	/**
	 * Refreshes the visualization of the item at index
	 * {@link #getCurrentItemIndex()}.
	 */
	protected void updateTemplateVisual() {
		// exchange template visual
		templateGroup.getChildren().clear();
		templateGroup.getChildren().add(getItems().get(getCurrentItemIndex()).createVisual());
	}

	private StackPane wrapWithPadding(Node node, double padding) {
		return wrapWithPadding(node, padding, node.getLayoutBounds().getWidth(), node.getLayoutBounds().getHeight());
	}

	private StackPane wrapWithPadding(Node node, double padding, double width, double height) {
		StackPane stack = new StackPane();
		stack.getChildren()
				.addAll(new Rectangle(width + padding + padding, height + padding + padding, Color.TRANSPARENT), node);
		return stack;
	}

}
