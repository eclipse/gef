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

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IResizableContentPart;
import org.eclipse.gef4.mvc.parts.ITransformableContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

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
import javafx.scene.transform.Affine;
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
	protected static final double DEFAULT_SHAPE_PADDING = 5;
	private static final String DEFAULT_SHAPE_ROLE = "defaultShape";

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
	private VBox vbox;
	private Node shape;

	private Node nestedGraphIcon;
	private StackPane nestedContentStackPane;
	private Pane nestedContentPane;
	private AnchorPane nestedContentAnchorPane;

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getNestedContentPane().getChildren().add(index, child.getVisual());
	}

	/**
	 * Creates the shape used to display the node's border and background.
	 *
	 * @return The newly created {@link Shape}.
	 */
	private Node createDefaultShape() {
		GeometryNode<?> shape = new GeometryNode<>(new org.eclipse.gef4.geometry.planar.Rectangle());
		shape.setUserData(DEFAULT_SHAPE_ROLE); // TODO: we need a proper
												// mechanism to
		shape.getStyleClass().add(CSS_CLASS_SHAPE);
		// handle
		// padding
		shape.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT,
				Arrays.asList(new Stop(0, new Color(1, 1, 1, 1)))));
		shape.setStroke(new Color(0, 0, 0, 1));
		shape.setStrokeType(StrokeType.INSIDE);
		return shape;
	}

	/**
	 * Creates the {@link Pane} that is used to display nested content.
	 *
	 * @return The {@link Pane} that is used to display nested content.
	 */
	private Pane createNestedContentPane() {
		Pane nestedChildrenPaneScaled = new Pane();
		Scale scale = new Scale();
		nestedChildrenPaneScaled.getTransforms().add(scale);
		scale.setX(DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		scale.setY(DEFAULT_NESTED_CHILDREN_ZOOM_FACTOR);
		return nestedChildrenPaneScaled;
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
			protected void layoutChildren() {
				// we directly layout our children from within resize
			};

			@Override
			public double maxHeight(double width) {
				return vbox.maxHeight(width);
			}

			@Override
			public double maxWidth(double height) {
				return vbox.maxWidth(height);
			}

			@Override
			public double minHeight(double width) {
				return vbox.minHeight(width);
			}

			@Override
			public double minWidth(double height) {
				return vbox.minWidth(height);
			}

			@Override
			public double prefHeight(double width) {
				return vbox.prefHeight(width);
			}

			@Override
			public double prefWidth(double height) {
				return vbox.prefWidth(height);
			}

			@Override
			public void resize(double w, double h) {
				// for shape we use the exact size
				shape.resize(w, h);
				shape.relocate(0, 0);
				// for vbox we use the preferred size
				vbox.setPrefSize(w, h);
				vbox.autosize();
				// and we relocate it to be horizontally and vertically centered
				// w.r.t. the shape
				Bounds vboxBounds = vbox.getLayoutBounds();
				vbox.relocate((w - vboxBounds.getWidth()) / 2, (h - vboxBounds.getHeight()) / 2);
			};
		};

		// create shape for border and background
		shape = createDefaultShape();

		// initialize image view
		iconImageView = new ImageView();
		iconImageView.setImage(null);
		iconImageView.getStyleClass().add(CSS_CLASS_ICON);

		// initialize text
		labelText = new Text();
		labelText.setText(NODE_LABEL_EMPTY);
		labelText.getStyleClass().add(CSS_CLASS_LABEL);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(iconImageView, labelText);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

		nestedContentPane = createNestedContentPane();
		nestedContentStackPane = new StackPane();
		nestedContentStackPane.getChildren().add(nestedContentPane);
		nestedContentAnchorPane = new AnchorPane();
		nestedContentAnchorPane.setStyle("-fx-background-color: white;");
		nestedContentAnchorPane.getChildren().add(nestedContentStackPane);
		AnchorPane.setLeftAnchor(nestedContentStackPane, -0.5d);
		AnchorPane.setTopAnchor(nestedContentStackPane, -0.5d);
		AnchorPane.setRightAnchor(nestedContentStackPane, 0.5d);
		AnchorPane.setBottomAnchor(nestedContentStackPane, 0.5d);
		VBox.setVgrow(nestedContentAnchorPane, Priority.ALWAYS);

		// put nested content stack pane below image and text
		vbox = new VBox();
		vbox.setMouseTransparent(true);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(hbox);
		vbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

		// place the box below the other visuals
		group.getChildren().addAll(shape, vbox);
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
		if (attrs.containsKey(ZestProperties.CSS_CLASS__NE)) {
			visual.getStyleClass().add(ZestProperties.getCssClass(node));
		}

		// set CSS id
		String id = null;
		if (attrs.containsKey(ZestProperties.CSS_ID__NE)) {
			id = ZestProperties.getCssId(node);
		}
		visual.setId(id);

		refreshShape();

		// set CSS style
		if (attrs.containsKey(ZestProperties.SHAPE_CSS_STYLE__N)) {
			if (getShape() != null) {
				getShape().setStyle(ZestProperties.getShapeCssStyle(node));
			}

		}
		if (attrs.containsKey(ZestProperties.LABEL_CSS_STYLE__E)) {
			if (getLabelText() != null) {
				getLabelText().setStyle(ZestProperties.getLabelCssStyle(node));
			}
		}

		if (vbox != null) {
			if (getShape() != null && DEFAULT_SHAPE_ROLE.equals(getShape().getUserData()) || isNesting()) {
				vbox.setPadding(new Insets(DEFAULT_SHAPE_PADDING));
			} else {
				vbox.setPadding(Insets.EMPTY);
			}
			if (isNesting()) {
				if (!vbox.getChildren().contains(nestedContentAnchorPane)) {
					vbox.getChildren().add(nestedContentAnchorPane);
					if (vbox.getPrefWidth() == Region.USE_COMPUTED_SIZE
							&& vbox.getPrefHeight() == Region.USE_COMPUTED_SIZE) {
						vbox.setPrefSize(DEFAULT_OUTER_LAYOUT_CONTAINER_WIDTH_NESTING,
								DEFAULT_OUTER_LAYOUT_CONTAINER_HEIGHT_NESTING);
						vbox.autosize();
					}
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
				if (vbox.getChildren().contains(nestedContentAnchorPane)) {
					vbox.getChildren().remove(nestedContentAnchorPane);
					vbox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
					vbox.autosize();
				}
			}
		}

		refreshLabel();
		refreshIcon();
		refreshTooltip();

		Point position = ZestProperties.getPosition(node);
		if (position != null) {
			// translate using a transform operation
			Affine transform = getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY).get();
			Affine newTransform = Geometry2FX.toFXAffine(new AffineTransform(1, 0, 0, 1, position.x, position.y));
			if (!NodeUtils.equals(transform, newTransform)) {
				NodeUtils.setAffine(transform, newTransform);
			}
		}

		Dimension size = ZestProperties.getSize(node);
		if (size != null) {
			getVisual().resize(size.width, size.height);
		} else {
			getVisual().autosize();
		}
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
	private Pane getNestedContentPane() {
		return nestedContentPane;
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
	 * Returns the {@link Shape} that displays the node's border and background.
	 *
	 * @return The {@link Shape} that displays the node's border and background.
	 */
	public Node getShape() {
		return shape;
	}

	/**
	 * Removes the {@link #getNestedGraphIcon()} from the
	 * {@link #getNestedContentStackPane()} and {@link #setNestedGraphIcon(Node)
	 * sets} the nested graph icon to <code>null</code>.
	 */
	private void hideNestedGraphIcon() {
		if (nestedGraphIcon != null) {
			getNestedContentStackPane().getChildren().remove(nestedGraphIcon);
			nestedGraphIcon = null;
		}
	}

	/**
	 * Returns <code>true</code> if this {@link NodePart} contains a nested
	 * {@link Graph}. Otherwise, <code>false</code> is returned.
	 *
	 * @return <code>true</code> if this {@link NodePart} contains a nested
	 *         {@link Graph}, otherwise <code>false</code>.
	 */
	private boolean isNesting() {
		return getContent().getNestedGraph() != null;
	}

	/**
	 * If the given <i>icon</i> is an {@link Image}, that {@link Image} will be
	 * used as the icon of this {@link NodePart}.
	 */
	protected void refreshIcon() {
		Image icon = ZestProperties.getIcon(getContent());
		if (getIconImageView() != null && getIconImageView().getImage() != icon && icon instanceof Image) {
			getIconImageView().setImage(icon);
		}
	}

	/**
	 * Changes the label of this {@link NodePart} to the given value.
	 */
	protected void refreshLabel() {
		String label = ZestProperties.getLabel(getContent());
		if (label == null || label.isEmpty()) {
			label = NODE_LABEL_EMPTY;
		}
		if (getLabelText() != null && !getLabelText().getText().equals(label)) {
			getLabelText().setText(label);
		}
	}

	private void refreshShape() {
		Node shape = ZestProperties.getShape(getContent());
		if (this.shape != shape && shape != null) {
			getVisual().getChildren().remove(shape);
			this.shape = shape;
			if (shape instanceof GeometryNode) {
				((GeometryNode<?>) shape).setStrokeType(StrokeType.INSIDE);
			} else if (shape instanceof Shape) {
				((Shape) shape).setStrokeType(StrokeType.INSIDE);
			}
			if (!shape.getStyleClass().contains(CSS_CLASS_SHAPE)) {
				shape.getStyleClass().add(CSS_CLASS_SHAPE);
			}
			getVisual().getChildren().add(0, shape);
		}
	}

	/**
	 * Changes the tooltip of this {@link NodePart} to the given value.
	 *
	 */
	protected void refreshTooltip() {
		String tooltip = ZestProperties.getTooltip(getContent());
		if (tooltipNode == null) {
			tooltipNode = new Tooltip(tooltip);
			Tooltip.install(getVisual(), tooltipNode);
		} else {
			tooltipNode.setText(tooltip);
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getNestedContentPane().getChildren().remove(index);
	}

	@Override
	public void resizeContent(Dimension size) {
		ZestProperties.setSize(getContent(), size);
	}

	/**
	 * Creates the nested graph icon and adds it to the
	 * {@link #getNestedContentStackPane()}.
	 */
	private void showNestedGraphIcon() {
		if (nestedGraphIcon == null) {
			nestedGraphIcon = new NestedGraphIcon();
			getNestedContentStackPane().getChildren().add(nestedGraphIcon);
		}
	}

	@Override
	public void transformContent(AffineTransform transform) {
		// transform operation
		Point position = null;
		if (ZestProperties.getPosition(getContent()) == null) {
			// use visual location (that was already applied)
			Bounds hostBounds = getVisual().getLayoutBounds();
			double minx = hostBounds.getMinX();
			double miny = hostBounds.getMinY();
			Affine tx = getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY).get();
			position = new Point(tx.getTx() + minx, tx.getTy() + miny);
		} else {
			position = transform.getTransformed(ZestProperties.getPosition(getContent()));
		}
		ZestProperties.setPosition(getContent(), position);
	}

}
