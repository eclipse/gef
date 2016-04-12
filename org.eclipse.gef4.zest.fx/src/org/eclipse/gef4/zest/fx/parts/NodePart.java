/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.operations.FXResizeOperation;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IResizableContentPart;
import org.eclipse.gef4.mvc.parts.ITransformableContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

/**
 * The {@link NodePart} is the controller for a
 * {@link org.eclipse.gef4.graph.Node} content object.
 *
 * @author mwienand
 *
 */
public class NodePart extends AbstractFXContentPart<Group>
		implements ITransformableContentPart<Node, Group>, IResizableContentPart<Node, Group> {

	/**
	 * JavaFX Node displaying a small icon representing a nested graph.
	 */
	public static class NestedGraphIcon extends Group {
		{
			Circle n0 = node(-20, -20);
			Circle n1 = node(-10, 10);
			Circle n2 = node(5, -15);
			Circle n3 = node(15, -25);
			Circle n4 = node(20, 5);
			getChildren().addAll(edge(n0, n1), edge(n1, n2), edge(n2, n3), edge(n3, n4), edge(n1, n4), n0, n1, n2, n3,
					n4);
		}

		private Node edge(Circle n, Circle m) {
			Line line = new Line(n.getCenterX(), n.getCenterY(), m.getCenterX(), m.getCenterY());
			line.setStroke(Color.BLACK);
			return line;
		}

		private Circle node(double x, double y) {
			return new Circle(x, y, 5, Color.BLACK);
		}
	}

	// defaults
	/**
	 * The default padding between the node's border and its content.
	 */
	protected static final double DEFAULT_PADDING = 5;

	/**
	 * The zoom level that needs to be reached for the
	 * {@link #doGetContentChildren()} method to return a nested {@link Graph}.
	 */
	protected static final double ZOOMLEVEL_SHOW_NESTED_GRAPH = 2;

	/**
	 * The default width of the nested graph area.
	 */
	protected static final double DEFAULT_CHILDREN_PANE_WIDTH = 300;

	/**
	 * The default height of the nested graph area.
	 */
	protected static final double DEFAULT_CHILDREN_PANE_HEIGHT = 300;

	/**
	 * The minimum width for the nested graph area.
	 */
	protected static final double CHILDREN_PANE_WIDTH_THRESHOLD = 100;

	/**
	 * The minimum height for the nested graph area.
	 */
	protected static final double CHILDREN_PANE_HEIGHT_THRESHOLD = 100;

	/**
	 * The default zoom factor that is applied to the nested graph area.
	 */
	public static final double DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR = 0.25;

	/**
	 * The default width for the outer most layout container of this node in the
	 * case of nested content.
	 */
	public static final double DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_NESTING = DEFAULT_CHILDREN_PANE_WIDTH
			* DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR;

	/**
	 * The default height for the outer most layout container of this node in
	 * the case of nested content.
	 */
	public static final double DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_NESTING = DEFAULT_CHILDREN_PANE_HEIGHT
			* DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR;

	/**
	 * The default width for the outer most layout container of this node in the
	 * case of no nested content.
	 */
	public static final double DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_LEAF = 0;

	/**
	 * The default height for the outer most layout container of this node in
	 * the case of no nested content.
	 */
	public static final double DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_LEAF = 0;

	// CSS classes for styling nodes
	/**
	 * The CSS class that is applied to the {@link #getVisual() visual} of this
	 * {@link NodePart}.
	 */
	public static final String CSS_CLASS = "node";

	/**
	 * The CSS class that is applied to the {@link Rectangle} that displays
	 * border and background.
	 */
	public static final String CSS_CLASS_SHAPE = "shape";

	/**
	 * The CSS class that is applied to the {@link Text} that displays the
	 * label.
	 */
	public static final String CSS_CLASS_LABEL = "label";

	/**
	 * The CSS class that is applied to the {@link Image} that displays the
	 * icon.
	 */
	public static final String CSS_CLASS_ICON = "icon";

	private static final String NODE_LABEL_EMPTY = "";

	private MapChangeListener<String, Object> nodeAttributesObserver = new MapChangeListener<String, Object>() {
		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
			refreshVisual();
		}
	};

	private Text labelText;
	private ImageView iconImageView;
	private Tooltip tooltipNode;
	private VBox outerLayoutContainer;
	private Shape shape;

	private Node nestedGraphIcon;
	private StackPane nestedContentStackPane;
	private Pane nestedChildrenPane;
	private Pane nestedChildrenPaneScaled;

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		nestedChildrenPaneScaled.getChildren().add(index, child.getVisual());
	}

	/**
	 * Creates the {@link Pane} that is used to display nested content.
	 *
	 * @return The {@link Pane} that is used to display nested content.
	 */
	private Pane createNestedContentPane() {
		final AnchorPane pane = new AnchorPane();
		pane.setStyle("-fx-background-color: white;");
		nestedChildrenPaneScaled = new Pane();
		Scale scale = new Scale();
		nestedChildrenPaneScaled.getTransforms().add(scale);
		scale.setX(DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		scale.setY(DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		pane.getChildren().add(nestedChildrenPaneScaled);
		AnchorPane.setLeftAnchor(nestedChildrenPaneScaled, -0.5d);
		AnchorPane.setTopAnchor(nestedChildrenPaneScaled, -0.5d);
		AnchorPane.setRightAnchor(nestedChildrenPaneScaled, 0.5d);
		AnchorPane.setBottomAnchor(nestedChildrenPaneScaled, 0.5d);
		return pane;
	}

	/**
	 * Creates the {@link StackPane} that is used to either display nested
	 * content, or an icon indicating that nested content exists for this
	 * {@link NodePart}. The given {@link Pane} is inserted into the children
	 * list of the created {@link StackPane}.
	 *
	 * @param nestedContentPane
	 *            The nested content {@link Pane}.
	 * @return The created {@link StackPane}.
	 */
	private StackPane createNestedContentStackPane(Pane nestedContentPane) {
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(nestedContentPane);
		return stackPane;
	}

	/**
	 * Creates the shape used to display the node's border and background.
	 *
	 * @return The newly created {@link Shape}.
	 */
	protected Shape createShape() {
		return new Rectangle();
	}

	@Override
	protected Group createVisual() {
		// container set-up
		final Group group = new Group() {
			@Override
			public boolean isResizable() {
				return true;
			}

			@Override
			public void resize(double w, double h) {
				outerLayoutContainer.setPrefSize(w, h);
			}
		};

		// create shape for border and background
		shape = createShape();
		shape.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT,
				Arrays.asList(new Stop(0, new Color(1, 1, 1, 1)))));
		shape.setStroke(new Color(0, 0, 0, 1));
		shape.setStrokeType(StrokeType.INSIDE);
		shape.getStyleClass().add(CSS_CLASS_SHAPE);

		// initialize image view
		iconImageView = new ImageView();
		iconImageView.setImage(null);
		iconImageView.getStyleClass().add(CSS_CLASS_ICON);

		// initialize text
		labelText = new Text();
		labelText.setText(NODE_LABEL_EMPTY);
		labelText.getStyleClass().add(CSS_CLASS_LABEL);

		// put image and text next to each other at the top of the node
		HBox hbox = new HBox();
		hbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		hbox.getChildren().addAll(iconImageView, labelText);
		hbox.setAlignment(Pos.CENTER);

		// put nested content stack pane below image and text
		outerLayoutContainer = new VBox();
		outerLayoutContainer.setMouseTransparent(true);
		outerLayoutContainer.getChildren().add(hbox);
		outerLayoutContainer.setPadding(new Insets(getPadding()));

		outerLayoutContainer.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				resizeShape(outerLayoutContainer.getWidth(), outerLayoutContainer.getHeight());
			}
		});

		// place the box below the other visuals
		group.getChildren().addAll(shape, outerLayoutContainer);
		return group;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().attributesProperty().addListener(nodeAttributesObserver);
	}

	@Override
	protected void doDeactivate() {
		getContent().attributesProperty().removeListener(nodeAttributesObserver);
		super.doDeactivate();
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		Graph nestedGraph = getContent().getNestedGraph();
		if (nestedGraph == null) {
			return Collections.emptyList();
		}
		// only show children when zoomed in
		Transform tx = getVisual().getLocalToSceneTransform();
		double scale = FX2Geometry.toAffineTransform(tx).getScaleX();
		if (scale > ZOOMLEVEL_SHOW_NESTED_GRAPH) {
			return Collections.singletonList(nestedGraph);
		}
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		org.eclipse.gef4.graph.Node node = getContent();
		if (node == null) {
			throw new IllegalStateException();
		}

		// set CSS class
		visual.getStyleClass().clear();
		visual.getStyleClass().add(CSS_CLASS);
		Map<String, Object> attrs = node.attributesProperty();
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_CLASS)) {
			visual.getStyleClass().add(ZestProperties.getCssClass(node));
		}

		// set CSS id
		String id = null;
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_ID)) {
			id = ZestProperties.getCssId(node);
		}
		visual.setId(id);

		// set CSS style
		if (attrs.containsKey(ZestProperties.NODE_RECT_CSS_STYLE)) {
			refreshRectCssStyle(ZestProperties.getNodeRectCssStyle(node));
		}
		if (attrs.containsKey(ZestProperties.ELEMENT_LABEL_CSS_STYLE)) {
			refreshLabelCssStyle(ZestProperties.getNodeLabelCssStyle(node));
		}

		refreshNesting(isNesting());

		refreshLabel(ZestProperties.getLabel(node));
		refreshIcon(ZestProperties.getIcon(node));
		refreshTooltip(ZestProperties.getTooltip(node));
		refreshPosition(ZestProperties.getPosition(node));
		refreshSize(ZestProperties.getSize(node));
	}

	@Override
	public org.eclipse.gef4.graph.Node getContent() {
		return (org.eclipse.gef4.graph.Node) super.getContent();
	}

	/**
	 * Returns the {@link ImageView} that displays the node's icon.
	 *
	 * @return The {@link ImageView} that displays the node's icon.
	 */
	protected ImageView getIconImageView() {
		return iconImageView;
	}

	/**
	 * Returns the {@link Text} that displays the node's label.
	 *
	 * @return The {@link Text} that displays the node's label.
	 */
	protected Text getLabelText() {
		return labelText;
	}

	/**
	 * Returns the {@link Pane} to which nested children are added.
	 *
	 * @return The {@link Pane} to which nested children are added.
	 */
	// TODO: this should not be public
	public Pane getNestedChildrenPane() {
		return nestedChildrenPaneScaled;
	}

	/**
	 * Returns the {@link StackPane} that either displays nested content or an
	 * icon indicating that nested content exists for this {@link NodePart}.
	 *
	 * @return The {@link StackPane} that wraps nested content.
	 */
	private StackPane getNestedContentStackPane() {
		return nestedContentStackPane;
	}

	/**
	 * Returns the {@link Node} that is displayed in the
	 * {@link #getNestedContentStackPane()} when nested content is available,
	 * but not rendered, currently.
	 *
	 * @return The {@link Node} that is displayed in the
	 *         {@link #getNestedContentStackPane()} when nested content is
	 *         available, but not rendered, currently.
	 */
	private Node getNestedGraphIcon() {
		return nestedGraphIcon;
	}

	/**
	 * Returns the outer most layout container that is used to layout the
	 * content of this node (including nested content).
	 *
	 * @return The outer most layout container that is used to layout the
	 *         content of this node (including nested content).
	 */
	protected Region getOuterLayoutContainer() {
		return outerLayoutContainer;
	}

	/**
	 * Returns the padding that is maintained between the node's border and its
	 * content.
	 *
	 * @return The padding that is maintained between the node's border and its
	 *         content.
	 */
	protected double getPadding() {
		return DEFAULT_PADDING;
	}

	/**
	 * Returns the {@link Shape} that displays the node's border and background.
	 *
	 * @return The {@link Shape} that displays the node's border and background.
	 */
	protected Shape getShape() {
		return shape;
	}

	/**
	 * Removes the {@link #getNestedGraphIcon()} from the
	 * {@link #getNestedContentStackPane()} and {@link #setNestedGraphIcon(Node)
	 * sets} the nested graph icon to <code>null</code>.
	 */
	private void hideNestedGraphIcon() {
		if (getNestedGraphIcon() != null) {
			getNestedContentStackPane().getChildren().remove(getNestedGraphIcon());
			setNestedGraphIcon(null);
		}
	}

	/**
	 * Returns <code>true</code> if this {@link NodePart} contains a nested
	 * {@link Graph}. Otherwise, <code>false</code> is returned.
	 *
	 * @return <code>true</code> if this {@link NodePart} contains a nested
	 *         {@link Graph}, otherwise <code>false</code>.
	 */
	protected boolean isNesting() {
		return getContent().getNestedGraph() != null;
	}

	/**
	 * If the given <i>icon</i> is an {@link Image}, that {@link Image} will be
	 * used as the icon of this {@link NodePart}.
	 *
	 * @param icon
	 *            The new icon for this {@link NodePart}.
	 */
	protected void refreshIcon(Image icon) {
		if (getIconImageView() != null && getIconImageView().getImage() != icon && icon instanceof Image) {
			getIconImageView().setImage(icon);
		}
	}

	/**
	 * Changes the label of this {@link NodePart} to the given value.
	 *
	 * @param label
	 *            The new label for this {@link NodePart}.
	 */
	protected void refreshLabel(String label) {
		if (label == null || label.isEmpty()) {
			label = NODE_LABEL_EMPTY;
		}
		if (getLabelText() != null && !getLabelText().getText().equals(label)) {
			getLabelText().setText(label);
		}
	}

	/**
	 * Adjusts the node's label CSS style to the given value.
	 *
	 * @param labelCssStyle
	 *            The new label CSS style for this node.
	 */
	protected void refreshLabelCssStyle(String labelCssStyle) {
		if (getLabelText() != null) {
			getLabelText().setStyle(labelCssStyle);
		}
	}

	/**
	 * When this node has a nested graph, space is reserved for it, so that the
	 * transition from an icon to the real graph will not change the node's
	 * size. Adjusts the space that is reserved depending on the given flag.
	 *
	 * @param isNesting
	 *            <code>true</code> if this node has a nested graph, otherwise
	 *            <code>false</code>.
	 */
	protected void refreshNesting(boolean isNesting) {
		VBox outerLayoutContainer = (VBox) getOuterLayoutContainer();
		if (outerLayoutContainer != null) {
			if (isNesting) {
				// create nested content stack pane if needed
				if (nestedContentStackPane == null) {
					nestedChildrenPane = createNestedContentPane();
					nestedContentStackPane = createNestedContentStackPane(nestedChildrenPane);
					nestedChildrenPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					nestedContentStackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					VBox.setVgrow(nestedContentStackPane, Priority.ALWAYS);
				}
				// add nested content stack pane
				if (!outerLayoutContainer.getChildren().contains(nestedContentStackPane)) {
					outerLayoutContainer.getChildren().add(nestedContentStackPane);
				}
				if (outerLayoutContainer.getPrefWidth() == 0 && outerLayoutContainer.getPrefHeight() == 0) {
					outerLayoutContainer.setPrefSize(DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_NESTING,
							DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_NESTING);
					outerLayoutContainer.resize(DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_NESTING,
							DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_NESTING);
				}
				// show a nested graph icon dependent on the zoom level
				if (!getChildrenUnmodifiable().isEmpty()) {
					hideNestedGraphIcon();
				} else {
					// show an icon as a replacement when the zoom threshold is
					// not reached
					showNestedGraphIcon();
				}
			} else {
				// remove nested content stack pane
				if (outerLayoutContainer.getChildren().contains(nestedContentStackPane)) {
					outerLayoutContainer.getChildren().remove(nestedContentStackPane);
				}
				outerLayoutContainer.setPrefSize(DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_LEAF,
						DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_LEAF);
				outerLayoutContainer.resize(DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_LEAF,
						DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_LEAF);
			}
		}
	}

	/**
	 * Adjusts the node's position to fit the given {@link Point}.
	 *
	 * @param position
	 *            This node's position.
	 */
	protected void refreshPosition(Point position) {
		if (position != null) {
			// translate using a transform operation
			FXTransformOperation refreshPositionOp = new FXTransformOperation(
					getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY).get());
			refreshPositionOp
					.setNewTransform(Geometry2FX.toFXAffine(new AffineTransform(1, 0, 0, 1, position.x, position.y)));
			try {
				refreshPositionOp.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adjusts the node rectangle's CSS style to the given value.
	 *
	 * @param nodeRectCssStyle
	 *            The new node rectangle CSS style.
	 */
	protected void refreshRectCssStyle(String nodeRectCssStyle) {
		if (getShape() != null) {
			getShape().setStyle(nodeRectCssStyle);
		}
	}

	/**
	 * Adjusts the position and size of this part's visual to the given bounds.
	 *
	 * @param size
	 *            The {@link Rectangle} describing the bounds for this part's
	 *            visual.
	 */
	protected void refreshSize(Dimension size) {
		if (size != null) {
			FXResizeOperation resizeOperation = new FXResizeOperation(getVisual());
			if (size.getWidth() != -1) {
				resizeOperation.setDw(size.getWidth() - getVisual().getLayoutBounds().getWidth());
			}
			if (size.getHeight() != -1) {
				resizeOperation.setDh(size.getHeight() - getVisual().getLayoutBounds().getHeight());
			}
			try {
				resizeOperation.execute(null, null);
			} catch (ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Changes the tooltip of this {@link NodePart} to the given value.
	 *
	 * @param tooltip
	 *            The new tooltip for this {@link NodePart}.
	 */
	protected void refreshTooltip(String tooltip) {
		if (tooltipNode == null) {
			tooltipNode = new Tooltip(tooltip);
			Tooltip.install(getVisual(), tooltipNode);
		} else {
			tooltipNode.setText(tooltip);
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		nestedChildrenPaneScaled.getChildren().remove(index);
	}

	@Override
	public void resizeContent(Dimension size) {
		ZestProperties.setSize(getContent(), size);
	}

	/**
	 * Resize the shape to the given width and height.
	 *
	 * @param width
	 *            The new width
	 * @param height
	 *            The new height
	 */
	protected void resizeShape(double width, double height) {
		((Rectangle) getShape()).setWidth(width);
		((Rectangle) getShape()).setHeight(height);
	}

	/**
	 * Changes the nested graph icon that is displayed to indicate that nested
	 * content is available to the given value.
	 *
	 * @param nestedGraphIcon
	 *            The new nested graph icon.
	 */
	private void setNestedGraphIcon(Node nestedGraphIcon) {
		this.nestedGraphIcon = nestedGraphIcon;
	}

	/**
	 * Creates the nested graph icon and adds it to the
	 * {@link #getNestedContentStackPane()}.
	 */
	private void showNestedGraphIcon() {
		if (getNestedGraphIcon() == null) {
			setNestedGraphIcon(new NestedGraphIcon());
			getNestedContentStackPane().getChildren().add(getNestedGraphIcon());
		}
	}

	@Override
	public void transformContent(AffineTransform transform) {
		// transform operation
		Point position = ZestProperties.getPosition(getContent());
		if (position == null) {
			position = new Point();
		}
		ZestProperties.setPosition(getContent(), transform.getTransformed(position));
	}

}
