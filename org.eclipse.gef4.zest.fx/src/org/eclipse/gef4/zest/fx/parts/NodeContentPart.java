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

import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

/**
 * The {@link NodeContentPart} is the controller for a
 * {@link org.eclipse.gef4.graph.Node} content object.
 *
 * @author mwienand
 *
 */
public class NodeContentPart extends AbstractFXContentPart<Group> {

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

	// CSS classes for styling nodes
	/**
	 * The CSS class that is applied to the {@link #getVisual() visual} of this
	 * {@link NodeContentPart}.
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

	private static final String NODE_LABEL_EMPTY = "-";

	private MapChangeListener<String, Object> nodeAttributesObserver = new MapChangeListener<String, Object>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
			refreshVisual();
		}

	};
	private Text labelText;
	private ImageView iconImageView;
	private Node nestedGraphIcon;
	private StackPane nestedContentStackPane;
	private Pane nestedChildrenPane;
	private Pane nestedChildrenPaneScaled;
	private int originalIndex = -1;
	private Bounds originalBounds = null;
	private Tooltip tooltipNode;
	private HBox hbox;
	private VBox vbox;
	private Rectangle rect;
	private EventHandler<? super MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			EventType<? extends Event> type = event.getEventType();
			if (type.equals(MouseEvent.MOUSE_ENTERED) || type.equals(MouseEvent.MOUSE_EXITED)) {
				refreshVisual();
			} else if (type.equals(MouseEvent.MOUSE_MOVED) || type.equals(MouseEvent.MOUSE_DRAGGED)) {
				if (originalBounds != null) {
					if (!originalBounds.contains(event.getSceneX(), event.getSceneY())) {
						// unhover the visual by making it mouse transparent
						getVisual().setMouseTransparent(true);
						// this will result in a MOUSE_EXITED event being fired,
						// which will lead to a refreshVisual() call, which will
						// update the visualization
					}
				}
			}
		}
	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		nestedChildrenPaneScaled.getChildren().add(index, child.getVisual());
	}

	/**
	 * Creates the {@link Pane} that is used to display nested content.
	 *
	 * @return The {@link Pane} that is used to display nested content.
	 */
	protected Pane createNestedContentPane() {
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
	 * {@link NodeContentPart}. The given {@link Pane} is inserted into the
	 * children list of the created {@link StackPane}.
	 *
	 * @param nestedContentPane
	 *            The nested content {@link Pane}.
	 * @return The created {@link StackPane}.
	 */
	protected StackPane createNestedContentStackPane(Pane nestedContentPane) {
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(nestedContentPane);
		return stackPane;
	}

	/**
	 * Creates the node visual. The given {@link ImageView}, {@link Text}, and
	 * {@link StackPane} are inserted into that node visual to display the
	 * node's icon, label and nested children, respectively. The node visual
	 * needs to be inserted into the given {@link Group}.
	 *
	 * @param group
	 *            This node's visual.
	 * @param rect
	 *            The {@link Rectangle} displaying border and background.
	 * @param iconImageView
	 *            The {@link ImageView} displaying the node's icon.
	 * @param labelText
	 *            The {@link Text} displaying the node's label.
	 * @param nestedContentStackPane
	 *            The {@link StackPane} displaying the node's nested content.
	 */
	protected void createNodeVisual(final Group group, final Rectangle rect, final ImageView iconImageView,
			final Text labelText, final StackPane nestedContentStackPane) {
		// put image and text next to each other at the top of the node
		hbox = new HBox();
		hbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		hbox.getChildren().addAll(iconImageView, labelText);

		// put nested content stack pane below image and text
		vbox = new VBox();
		vbox.setMouseTransparent(true);
		vbox.getChildren().addAll(hbox, nestedContentStackPane);
		VBox.setVgrow(nestedContentStackPane, Priority.ALWAYS);

		// expand box depending on content size
		vbox.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				vbox.setTranslateX(getPadding());
				vbox.setTranslateY(getPadding());
				rect.setWidth(vbox.getWidth() + 2 * getPadding());
				rect.setHeight(vbox.getHeight() + 2 * getPadding());
			}
		});

		// place the box below the other visuals
		group.getChildren().addAll(rect, vbox);
	}

	@Override
	protected Group createVisual() {
		// container set-up
		final Group group = new Group() {
			@Override
			public boolean isResizable() {
				// every node is resizable when it contains a graph
				return isNesting();
			}

			@Override
			public void resize(double w, double h) {
				if (!isResizable()) {
					return;
				}

				// compute new size, based on layout bounds
				Bounds layoutBounds = getLayoutBounds();
				Bounds vboxBounds = vbox.getLayoutBounds();
				double vw = vboxBounds.getWidth() + w - layoutBounds.getWidth();
				double vh = vboxBounds.getHeight() + h - layoutBounds.getHeight();
				vbox.setPrefSize(vw, vh);
				vbox.resize(vw, vh);
			}
		};

		// create box for border and background
		rect = new Rectangle();
		rect.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT,
				Arrays.asList(new Stop(0, new Color(1, 1, 1, 1)))));
		rect.setStroke(new Color(0, 0, 0, 1));
		rect.getStyleClass().add(CSS_CLASS_SHAPE);

		nestedChildrenPane = createNestedContentPane();
		nestedContentStackPane = createNestedContentStackPane(nestedChildrenPane);
		nestedChildrenPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		nestedContentStackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		// initialize image view
		iconImageView = new ImageView();
		iconImageView.setImage(null);
		iconImageView.getStyleClass().add(CSS_CLASS_ICON);

		// initialize text
		labelText = new Text();
		labelText.setTextOrigin(VPos.TOP);
		labelText.setText(NODE_LABEL_EMPTY);
		labelText.getStyleClass().add(CSS_CLASS_LABEL);

		// build node visual
		createNodeVisual(group, rect, iconImageView, labelText, nestedContentStackPane);

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
			hideNestedGraphIcon();
			return Collections.singletonList(nestedGraph);
		}
		// show an icon as a replacement when the zoom threshold is not reached
		showNestedGraphIcon();
		return Collections.emptyList();
	}

	@Override
	public void doRefreshVisual(Group visual) {
		if (getContent() == null) {
			throw new IllegalStateException();
		}

		// set CSS class
		visual.getStyleClass().clear();
		visual.getStyleClass().add(CSS_CLASS);
		org.eclipse.gef4.graph.Node node = getContent();
		Map<String, Object> attrs = node.attributesProperty();
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_CLASS)) {
			refreshCssClass(visual, ZestProperties.getCssClass(node));
		}

		// set CSS id
		String id = null;
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_ID)) {
			id = ZestProperties.getCssId(node);
		}
		visual.setId(id);

		// set CSS style
		if (attrs.containsKey(ZestProperties.NODE_RECT_CSS_STYLE)) {
			rect.setStyle(ZestProperties.getNodeRectCssStyle(node));
		}
		if (attrs.containsKey(ZestProperties.NODE_LABEL_CSS_STYLE)) {
			labelText.setStyle(ZestProperties.getNodeLabelCssStyle(node));
		}

		// determine label
		Object label = attrs.get(ZestProperties.ELEMENT_LABEL);
		// use id if no label is set
		if (label == null) {
			label = id;
		}
		// use the the DEFAULT_LABEL if no label is set
		String str = label instanceof String ? (String) label : label == null ? NODE_LABEL_EMPTY : label.toString();
		// eventually let the fisheye mode trim the label
		str = refreshFisheye(visual, attrs, str);
		refreshLabel(visual, str);

		refreshIcon(visual, attrs.get(ZestProperties.NODE_ICON));
		refreshNestedGraphArea(visual, isNesting());
		refreshTooltip(visual, attrs.get(ZestProperties.NODE_TOOLTIP));
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
	public Pane getNestedChildrenPane() {
		return nestedChildrenPaneScaled;
	}

	/**
	 * Returns the {@link StackPane} that either displays nested content or an
	 * icon indicating that nested content exists for this
	 * {@link NodeContentPart}.
	 *
	 * @return The {@link StackPane} that wraps nested content.
	 */
	protected StackPane getNestedContentStackPane() {
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
	protected Node getNestedGraphIcon() {
		return nestedGraphIcon;
	}

	/**
	 * Returns the {@link Rectangle} that displays the node's border and
	 * background.
	 *
	 * @return The {@link Rectangle} that displays the node's border and
	 *         background.
	 */
	protected Rectangle getNodeRect() {
		return rect;
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
	 * Removes the {@link #getNestedGraphIcon()} from the
	 * {@link #getNestedContentStackPane()} and {@link #setNestedGraphIcon(Node)
	 * sets} the nested graph icon to <code>null</code>.
	 */
	protected void hideNestedGraphIcon() {
		if (getNestedGraphIcon() != null) {
			getNestedContentStackPane().getChildren().remove(getNestedGraphIcon());
			setNestedGraphIcon(null);
		}
	}

	/**
	 * Returns <code>true</code> if this {@link NodeContentPart} contains a
	 * nested {@link Graph}. Otherwise, <code>false</code> is returned.
	 *
	 * @return <code>true</code> if this {@link NodeContentPart} contains a
	 *         nested {@link Graph}, otherwise <code>false</code>.
	 */
	protected boolean isNesting() {
		return getContent().getNestedGraph() != null;
	}

	/**
	 * Adds the given CSS class to the given visual.
	 *
	 * @param visual
	 *            The visual to which the CSS class is added.
	 * @param cssClass
	 *            The CSS class that is added to the visual.
	 */
	protected void refreshCssClass(Group visual, String cssClass) {
		visual.getStyleClass().add(cssClass);
	}

	/**
	 * Adjusts the visualization to reflect the fisheye state of the node. If
	 * the node is in fisheye state, its label will be reduced to the first
	 * letter.
	 *
	 * @param visual
	 *            The visual of this {@link NodeContentPart}.
	 * @param attrs
	 *            The attributes map that stores the fisheye state of this
	 *            {@link NodeContentPart}.
	 * @param str
	 *            The label of this {@link NodeContentPart}.
	 * @return The adjusted label for this {@link NodeContentPart}.
	 */
	protected String refreshFisheye(Group visual, Map<String, Object> attrs, String str) {
		// limit label to first letter when in fisheye mode (and not hovered)
		Object fisheye = attrs.get(ZestProperties.NODE_FISHEYE);
		if (fisheye instanceof Boolean && (Boolean) fisheye) {
			// register mouse event listeners
			visual.addEventHandler(MouseEvent.ANY, mouseHandler);
			if (!visual.isHover()) {
				// limit label to first letter
				// TODO: hide image, hide children/graph icon
				str = str.substring(0, 1);
				restoreZOrder();
			} else {
				if (originalBounds == null) {
					originalBounds = visual.localToScene(visual.getLayoutBounds());
				}
				// TODO: show image, show children/graph icon
				// highlight this node by moving it to the front
				List<IVisualPart<Node, ? extends Node>> children = getParent().getChildrenUnmodifiable();
				originalIndex = children.indexOf(this); // restore later
				getParent().reorderChild(this, children.size() - 1);
				visual.toFront();
			}
		} else {
			// TODO: show image, show children/graph icon
			restoreZOrder();
			visual.removeEventHandler(MouseEvent.ANY, mouseHandler);
		}
		return str;
	}

	/**
	 * If the given <i>icon</i> is an {@link Image}, that {@link Image} will be
	 * used as the icon of this {@link NodeContentPart}.
	 *
	 * @param visual
	 *            The visual of this {@link NodeContentPart}.
	 * @param icon
	 *            The new icon for this {@link NodeContentPart}.
	 */
	protected void refreshIcon(Group visual, Object icon) {
		if (iconImageView.getImage() != icon && icon instanceof Image) {
			iconImageView.setImage((Image) icon);
		}
	}

	/**
	 * Changes the label of this {@link NodeContentPart} to the given value.
	 *
	 * @param visual
	 *            The visual of this {@link NodeContentPart}.
	 * @param str
	 *            The new label for this {@link NodeContentPart}.
	 */
	protected void refreshLabel(Group visual, String str) {
		if (!labelText.getText().equals(str)) {
			labelText.setText(str);
		}
	}

	/**
	 * When this node has a nested graph, space is reserved for it, so that the
	 * transition from an icon to the real graph will not change the node's
	 * size.
	 *
	 * @param visual
	 *            The visual of this part.
	 * @param isNesting
	 *            <code>true</code> if this node has a nested graph, otherwise
	 *            <code>false</code>.
	 */
	protected void refreshNestedGraphArea(Group visual, boolean isNesting) {
		if (isNesting) {
			if (vbox.getPrefWidth() == 0 && vbox.getPrefHeight() == 0) {
				vbox.setPrefSize(300 / 4, 300 / 4);
				vbox.resize(300 / 4, 300 / 4);
			}
		} else {
			vbox.setPrefSize(0, 0);
			vbox.resize(0, 0);
		}
	}

	/**
	 * Changes the tooltip of this {@link NodeContentPart} to the given value.
	 *
	 * @param visual
	 *            The visual of this {@link NodeContentPart}.
	 * @param tooltip
	 *            The new tooltip for this {@link NodeContentPart}.
	 */
	protected void refreshTooltip(Group visual, Object tooltip) {
		if (tooltip instanceof String) {
			if (tooltipNode == null) {
				tooltipNode = new Tooltip((String) tooltip);
				Tooltip.install(visual, tooltipNode);
			} else {
				tooltipNode.setText((String) tooltip);
			}
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		nestedChildrenPaneScaled.getChildren().remove(index);
	}

	private void restoreZOrder() {
		if (originalIndex >= 0) {
			getParent().reorderChild(this, originalIndex);
		}
		// make the visual hoverable by making it opaque for mouse events again
		getVisual().setMouseTransparent(false);
		// clear original bounds, so that they are recomputed
		originalBounds = null;
	}

	/**
	 * Changes the nested graph icon that is displayed to indicate that nested
	 * content is available to the given value.
	 *
	 * @param nestedGraphIcon
	 *            The new nested graph icon.
	 */
	protected void setNestedGraphIcon(Node nestedGraphIcon) {
		this.nestedGraphIcon = nestedGraphIcon;
	}

	/**
	 * Creates the nested graph icon and adds it to the
	 * {@link #getNestedContentStackPane()}.
	 */
	protected void showNestedGraphIcon() {
		if (getNestedGraphIcon() == null) {
			setNestedGraphIcon(new NestedGraphIcon());
			getNestedContentStackPane().getChildren().add(getNestedGraphIcon());
		}
	}

}
