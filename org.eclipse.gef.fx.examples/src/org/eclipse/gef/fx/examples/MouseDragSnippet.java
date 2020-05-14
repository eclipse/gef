/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
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
package org.eclipse.gef.fx.examples;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MouseDragSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	private ChangeListener<? super Number> sceneSizeChanged = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			onSceneSizeChange(scene.getWidth(), scene.getHeight());
		}
	};

	private Pane contentLayer;
	private Pane handleLayer;
	private Scene scene;
	private EventHandler<? super MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMousePress(event);
		}
	};
	private Node pressed;
	private EventHandler<? super MouseEvent> mouseFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMouseEvent(event);
		}
	};
	private Point2D startLayoutPosition;
	private Point2D startMousePosition;
	private ObservableSet<Node> nodesUnderMouse = FXCollections
			.observableSet(new HashSet<Node>());
	private Map<Node, IAnchor> anchors = new HashMap<>();
	private Pane feedbackLayer;
	private StackPane stackPane;

	public MouseDragSnippet() {
		super("FX MouseDrag Snippet");
	}

	@Override
	public Scene createScene() {
		// layers
		contentLayer = new Pane();
		contentLayer.setPickOnBounds(true);
		handleLayer = new Pane();
		handleLayer.setPickOnBounds(false);
		feedbackLayer = new Pane();
		feedbackLayer.setPickOnBounds(false);
		feedbackLayer.setMouseTransparent(true);

		stackPane = new StackPane();
		stackPane.getChildren().addAll(contentLayer, handleLayer,
				feedbackLayer);

		// scrolling
		ScrollPane scrollPane = new ScrollPane();
		Group scrollPaneContent = new Group(stackPane);
		scrollPaneContent.setAutoSizeChildren(false);
		scrollPane.setContent(scrollPaneContent);

		// scene
		scene = new Scene(scrollPane, 800, 600);
		scene.widthProperty().addListener(sceneSizeChanged);
		scene.heightProperty().addListener(sceneSizeChanged);
		scene.addEventFilter(MouseEvent.ANY, mouseFilter);

		// initially show contents
		onSceneSizeChange(scene.getWidth(), scene.getHeight());

		return scene;
	}

	private Node draggable(Node node) {
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		return node;
	}

	private Node generate(double w, double h) {
		double rx = Math.random() * (w - 100);
		double ry = Math.random() * (h - 100);
		double rw = Math.random() * 90 + 10;
		double rh = Math.random() * 90 + 10;
		Rectangle rectangle = new Rectangle(0, 0, rw, rh);
		rectangle.setLayoutX(rx);
		rectangle.setLayoutY(ry);
		rectangle.setFill(
				new Color(Math.random(), Math.random(), Math.random(), 0.5));
		rectangle.setStroke(Color.TRANSPARENT);
		return rectangle;
	}

	protected void onMouseEvent(MouseEvent event) {
		if (pressed == null) {
			// no processing if no node is pressed
			return;
		}

		// node is pressed, process all mouse events
		EventType<? extends Event> type = event.getEventType();
		if (type.equals(MouseEvent.MOUSE_RELEASED)) {
			System.out.println("release " + pressed);
			pressed.setEffect(null);
			IAnchor ifxAnchor = anchors.get(pressed);
			if (ifxAnchor != null) {
				Set<AnchorKey> keys = ifxAnchor.positionsUnmodifiableProperty()
						.keySet();
				for (AnchorKey key : keys) {
					key.getAnchored().setEffect(new BoxBlur());
				}
			}
			pressed = null;
			nodesUnderMouse.clear();
		} else if (type.equals(MouseEvent.MOUSE_DRAGGED)) {
			double dx = event.getSceneX() - startMousePosition.getX();
			double dy = event.getSceneY() - startMousePosition.getY();
			pressed.setLayoutX(startLayoutPosition.getX() + dx);
			pressed.setLayoutY(startLayoutPosition.getY() + dy);
			boolean changed = updateNodesUnderMouse(event.getSceneX(),
					event.getSceneY());
			if (changed) {
				System.out.println(
						"targets: " + Arrays.asList(nodesUnderMouse.toArray()));
			}
		}
	}

	protected void onMousePress(MouseEvent event) {
		pressed = (Node) event.getTarget();
		System.out.println("press " + pressed);
		startMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
		startLayoutPosition = new Point2D(pressed.getLayoutX(),
				pressed.getLayoutY());

		// add effect
		pressed.setEffect(new Bloom(0));
		IAnchor ifxAnchor = anchors.get(pressed);
		if (ifxAnchor != null) {
			Set<AnchorKey> keys = ifxAnchor.positionsUnmodifiableProperty()
					.keySet();
			for (AnchorKey key : keys) {
				key.getAnchored().setEffect(null);
			}
		}
	}

	protected void onSceneSizeChange(double width, double height) {
		// clear visuals
		anchors.clear();
		contentLayer.getChildren().clear();
		handleLayer.getChildren().clear();

		// generate contents
		int count = 64;
		for (int i = 0; i < count; i++) {
			handleLayer.getChildren().add(draggable(generate(width, height)));
		}

		// generate random curves between
		for (int i = 0; i < count; i++) {
			Node n = handleLayer.getChildren()
					.get((int) (Math.random() * count / 2));
			Node m = null;
			while (m == null || m == n) {
				m = handleLayer.getChildren()
						.get((int) (Math.random() * count / 2));
			}

			Connection connection = new Connection();

			IAnchor an, am;
			if (anchors.containsKey(n)) {
				an = anchors.get(n);
			} else {
				an = new DynamicAnchor(n);
				anchors.put(n, an);
			}

			if (anchors.containsKey(m)) {
				am = anchors.get(m);
			} else {
				am = new DynamicAnchor(n);
				anchors.put(m, am);
			}

			connection.setStartAnchor(an);
			connection.setEndAnchor(am);

			connection.setEffect(new BoxBlur());

			contentLayer.getChildren().add(connection);
		}
	}

	public Set<Node> pickNodes(double sceneX, double sceneY, Node root) {
		Set<Node> picked = new HashSet<>();

		// start with given root node
		Queue<Node> nodes = new LinkedList<>();
		nodes.add(root);

		while (!nodes.isEmpty()) {
			Node current = nodes.remove();
			if (current.contains(current.sceneToLocal(sceneX, sceneY))) {
				picked.add(current);
				// test all children, too
				if (current instanceof Parent) {
					nodes.addAll(((Parent) current).getChildrenUnmodifiable());
				}
			}
		}

		return picked;
	}

	private boolean updateNodesUnderMouse(double sceneX, double sceneY) {
		boolean changed = false;
		Set<Node> picked = pickNodes(sceneX, sceneY, stackPane);

		// update entered nodes
		for (Node n : picked) {
			if (!nodesUnderMouse.contains(n)) {
				nodesUnderMouse.add(n);
				changed = true;
			}
		}

		// update exited nodes
		List<Node> toRemove = new LinkedList<>();
		for (Node n : nodesUnderMouse) {
			if (!picked.contains(n)) {
				toRemove.add(n);
			}
		}
		if (!toRemove.isEmpty()) {
			changed = true;
		}
		for (Node n : toRemove) {
			nodesUnderMouse.remove(n);
		}

		return changed;
	}

}
