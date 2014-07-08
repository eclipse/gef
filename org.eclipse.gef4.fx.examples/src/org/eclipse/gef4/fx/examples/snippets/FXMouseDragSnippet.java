package org.eclipse.gef4.fx.examples.snippets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.fx.examples.FXApplication;

public class FXMouseDragSnippet extends FXApplication {

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

	@Override
	public Scene createScene() {
		// layers
		StackPane stackPane = new StackPane();
		contentLayer = new Pane();
		contentLayer.setPickOnBounds(true);
		handleLayer = new Pane();
		handleLayer.setPickOnBounds(false);
		stackPane.getChildren().addAll(contentLayer, handleLayer);

		// scrolling
		ScrollPane scrollPane = new ScrollPane();
		Group scrollPaneContent = new Group(stackPane);
		scrollPaneContent.setAutoSizeChildren(false);
		scrollPane.setContent(scrollPaneContent);

		// scene
		scene = new Scene(scrollPane, 800, 600);

		// listeners
		scene.widthProperty().addListener(sceneSizeChanged);
		scene.heightProperty().addListener(sceneSizeChanged);
		scene.addEventFilter(MouseEvent.ANY, mouseFilter);

		return scene;
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
			
			pressed = null;
			nodesUnderMouse.clear();
		} else if (type.equals(MouseEvent.MOUSE_DRAGGED)) {
			double dx = event.getSceneX() - startMousePosition.getX();
			double dy = event.getSceneY() - startMousePosition.getY();
			
			pressed.setLayoutX(startLayoutPosition.getX() + dx);
			pressed.setLayoutY(startLayoutPosition.getY() + dy);
			
			boolean changed = updateNodesUnderMouse(event.getSceneX(), event.getSceneY());
			
			if (changed) {
				System.out.println("targets: " + Arrays.asList(nodesUnderMouse.toArray()));
			}
		}
	}

	public static Set<Node> pickNodes(Node root, double x, double y) {
		Bounds bounds;
		double bx1, bx0, by1, by0;
		Set<Node> picked = new HashSet<Node>();

		// start with given root node
		Queue<Node> nodes = new LinkedList<Node>();
		nodes.add(root);

		while (!nodes.isEmpty()) {
			Node current = nodes.remove();

			// get bounds in scene
			bounds = current.getBoundsInLocal();
			bounds = current.localToScene(bounds);
			bx1 = bounds.getMaxX();
			bx0 = bounds.getMinX();
			by1 = bounds.getMaxY();
			by0 = bounds.getMinY();

			if (bx0 <= x && x <= bx1 && by0 <= y && y <= by1) {
				// point is contained
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
		Set<Node> picked = pickNodes(scene.getRoot(), sceneX, sceneY);
		
		// update entered nodes
		for (Node n : picked) {
			if (!nodesUnderMouse.contains(n)) {
				nodesUnderMouse.add(n);
				changed = true;
			}
		}
		
		// update exited nodes
		List<Node> toRemove = new LinkedList<Node>();
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

	protected void onMousePress(MouseEvent event) {
		pressed = (Node) event.getTarget();
		System.out.println("press " + pressed);
		startMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
		startLayoutPosition = new Point2D(pressed.getLayoutX(),
				pressed.getLayoutY());
	}

	protected void onSceneSizeChange(double width, double height) {
		contentLayer.getChildren().clear();
		handleLayer.getChildren().clear();
		for (int i = 0; i < 128; i++) {
			contentLayer.getChildren().add(draggable(generate(width, height)));
			handleLayer.getChildren().add(draggable(generate(width, height)));
		}
	}

	private Node draggable(Node node) {
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		return node;
	}

	private Node generate(double w, double h) {
		double rx = Math.random() * (w - 100);
		double ry = Math.random() * (h - 100);
		double rw = Math.random() * 100;
		double rh = Math.random() * 100;
		Rectangle rectangle = new Rectangle(rx, ry, rw, rh);
		rectangle.setFill(new Color(Math.random(), Math.random(),
				Math.random(), 0.5));
		rectangle.setStroke(Color.TRANSPARENT);
		return rectangle;
	}

}
