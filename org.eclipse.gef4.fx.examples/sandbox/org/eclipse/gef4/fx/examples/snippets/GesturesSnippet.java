package org.eclipse.gef4.fx.examples.snippets;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.VBox;

public class GesturesSnippet extends AbstractFXExample {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public Scene createScene() {
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().add(new Label("Try some multitouch gestures"));
		final Label display = new Label();
		root.getChildren().add(display);
		root.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				System.out.println("SCROLL " + event);
				// FIXME: inertia is not properly set on JavaFX 2.2 (bug)
				System.out.println(event.isInertia());
				display.setText(event.getEventType().toString());
			}
		});
		root.addEventHandler(ZoomEvent.ANY, new EventHandler<ZoomEvent>() {
			@Override
			public void handle(ZoomEvent event) {
				System.out.println("ZOOM " + event);
				display.setText(event.getEventType().toString());
			}
		});
		root.addEventHandler(RotateEvent.ANY, new EventHandler<RotateEvent>() {
			@Override
			public void handle(RotateEvent event) {
				System.out.println("ROTATE " + event);
				display.setText(event.getEventType().toString());
			}
		});
		root.addEventHandler(SwipeEvent.ANY, new EventHandler<SwipeEvent>() {
			@Override
			public void handle(SwipeEvent event) {
				System.out.println("SWIPE " + event);
				display.setText(event.getEventType().toString());
			}
		});
		return new Scene(root, 300, 300);
	}

}
