/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
import javafx.scene.transform.Transform;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class NodeContentPart extends AbstractFXContentPart<Group> {

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

	public static final String CSS_CLASS = "node";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_ID = "id";
	public static final String ATTR_STYLE = "style";
	public static final String ATTR_IMAGE = "image";
	public static final Object ATTR_TOOLTIP = "tooltip";
	public static final String DEFAULT_LABEL = "-";

	public static final double DEFAULT_CHILDREN_PANE_WIDTH = 300;
	public static final double DEFAULT_CHILDREN_PANE_HEIGHT = 300;

	public static final double CHILDREN_PANE_WIDTH_THRESHOLD = 100;
	public static final double CHILDREN_PANE_HEIGHT_THRESHOLD = 100;

	protected Rectangle box = new Rectangle();
	protected Text text = new Text();
	protected ImageView imageView = new ImageView();
	protected double padding = 5;
	protected Tooltip tooltipNode;
	private StackPane contentStackPane;
	private Pane childrenPane;
	private HBox hbox;
	private VBox vbox;
	private NestedGraphIcon nestedGraphIcon;

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		childrenPane.getChildren().add(index, child.getVisual());
	}

	@Override
	protected Group createVisual() {
		// container set-up
		final Group group = new Group() {
			@Override
			public boolean isResizable() {
				return childrenPane.getPrefWidth() != 0;
			}

			@Override
			public void resize(double w, double h) {
				if (!isResizable()) {
					return;
				}

				// compute delta size, based on layout bounds
				Bounds layoutBounds = getLayoutBounds();
				double dw = w - layoutBounds.getWidth();
				double dh = h - layoutBounds.getHeight();

				// compute new size, taking into account the childrenPane scale
				double newWidth = childrenPane.getPrefWidth() + dw * 1 / childrenPane.getScaleX();
				double newHeight = childrenPane.getPrefHeight() + dh * 1 / childrenPane.getScaleY();

				// do not resize below threshold
				if (newWidth < CHILDREN_PANE_WIDTH_THRESHOLD) {
					newWidth = CHILDREN_PANE_WIDTH_THRESHOLD;
				}
				if (newHeight < CHILDREN_PANE_HEIGHT_THRESHOLD) {
					newHeight = CHILDREN_PANE_HEIGHT_THRESHOLD;
				}

				// perform the resize
				childrenPane.setPrefSize(newWidth, newHeight);
				childrenPane.resize(newWidth, newHeight);

				// layout other visuals
				hbox.autosize();
				vbox.autosize();
			}
		};
		group.setManaged(false);
		group.setAutoSizeChildren(false);
		hbox = new HBox();
		hbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		hbox.getChildren().addAll(imageView, text);
		vbox = new VBox();
		vbox.setMouseTransparent(true);
		contentStackPane = new StackPane();
		childrenPane = new Pane();
		childrenPane.setStyle("-fx-background-color: white;");
		childrenPane.setScaleX(0.25);
		childrenPane.setScaleY(0.25);
		childrenPane.setPrefSize(0, 0);
		contentStackPane.getChildren().add(new Group(childrenPane));
		vbox.getChildren().addAll(hbox, contentStackPane);
		group.getChildren().addAll(box, vbox);

		// box, label, image
		box.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT, Arrays.asList(new Stop(0, new Color(1, 1,
				1, 1)))));
		box.setStroke(new Color(0, 0, 0, 1));
		text.setTextOrigin(VPos.TOP);
		text.setText(DEFAULT_LABEL);
		ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
				hbox.autosize();
				vbox.autosize();
			}
		};
		text.boundsInLocalProperty().addListener(boundsChangeListener);
		imageView.setImage(null);
		imageView.boundsInLocalProperty().addListener(boundsChangeListener);
		childrenPane.boundsInLocalProperty().addListener(boundsChangeListener);

		// layout
		vbox.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> arg0, Bounds arg1, Bounds arg2) {
				vbox.setTranslateX(padding);
				vbox.setTranslateY(padding);
				box.setWidth(vbox.getWidth() + 2 * padding);
				box.setHeight(vbox.getHeight() + 2 * padding);
				text.setTranslateX(vbox.getWidth() / 2 - text.getLayoutBounds().getWidth() / 2);
			}
		});

		return group;
	}

	@Override
	public void doRefreshVisual(Group visual) {
		if (getContent() == null) {
			throw new IllegalStateException();
		}

		// set CSS properties
		visual.getStyleClass().clear();
		visual.getStyleClass().add(CSS_CLASS);
		Map<String, Object> attrs = getContent().getAttrs();
		if (attrs.containsKey(ATTR_CLASS)) {
			visual.getStyleClass().add((String) attrs.get(ATTR_CLASS));
		}
		if (attrs.containsKey(ATTR_ID)) {
			visual.setId((String) attrs.get(ATTR_ID));
		}
		if (attrs.containsKey(ATTR_STYLE)) {
			visual.setStyle((String) attrs.get(ATTR_STYLE));
		}

		// set label
		Object label = attrs.get(Attr.Key.LABEL.toString());
		// use id if no label is set
		if (label == null) {
			label = attrs.get(Attr.Key.ID.toString());
		}
		String str = label instanceof String ? (String) label : label == null ? DEFAULT_LABEL : label.toString();
		text.setText(str);

		// set image
		Object imageFileUrl = attrs.get(ATTR_IMAGE);
		if (imageFileUrl instanceof String) {
			imageView.setImage(new Image((String) imageFileUrl));
		}

		// show children when we have a nested graph
		if (getContent().getNestedGraph() != null) {
			if (childrenPane.getPrefWidth() == 0 && childrenPane.getPrefHeight() == 0) {
				childrenPane.setPrefSize(DEFAULT_CHILDREN_PANE_WIDTH, DEFAULT_CHILDREN_PANE_HEIGHT);
				childrenPane.resize(DEFAULT_CHILDREN_PANE_WIDTH, DEFAULT_CHILDREN_PANE_HEIGHT);
			}
		} else {
			if (childrenPane.getPrefWidth() != 0 || childrenPane.getPrefHeight() != 0) {
				childrenPane.setPrefSize(0, 0);
				childrenPane.resize(0, 0);
			}
		}

		// set tooltip
		if (tooltipNode != null) {
			Tooltip.uninstall(visual, tooltipNode);
			tooltipNode = null;
		}
		Object tooltip = attrs.get(ATTR_TOOLTIP);
		if (tooltip instanceof String) {
			tooltipNode = new Tooltip((String) tooltip);
			Tooltip.install(getVisual(), tooltipNode);
		}
	}

	public Pane getChildrenPane() {
		return childrenPane;
	}

	@Override
	public org.eclipse.gef4.graph.Node getContent() {
		return (org.eclipse.gef4.graph.Node) super.getContent();
	}

	@Override
	public List<? extends Object> getContentChildren() {
		Graph nestedGraph = getContent().getNestedGraph();
		if (nestedGraph == null) {
			return Collections.emptyList();
		}
		// only show children when zoomed in
		Transform tx = getVisual().getLocalToSceneTransform();
		double scale = JavaFX2Geometry.toAffineTransform(tx).getScaleX();
		if (scale > 2) {
			if (nestedGraphIcon != null) {
				contentStackPane.getChildren().remove(nestedGraphIcon);
				nestedGraphIcon = null;
			}
			return Collections.singletonList(nestedGraph);
		}
		if (nestedGraphIcon == null) {
			nestedGraphIcon = new NestedGraphIcon();
			contentStackPane.getChildren().add(nestedGraphIcon);
		}
		return Collections.emptyList();
	}

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, Group visual) {
		super.registerAtVisualPartMap(viewer, visual);
		Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap = getViewer().getVisualPartMap();
		visualPartMap.put(box, this);
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		childrenPane.getChildren().remove(index);
	}

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer, Group visual) {
		super.unregisterFromVisualPartMap(viewer, visual);
		Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap = getViewer().getVisualPartMap();
		visualPartMap.remove(box);
	}

}
