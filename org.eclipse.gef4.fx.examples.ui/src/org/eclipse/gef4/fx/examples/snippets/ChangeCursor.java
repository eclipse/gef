package org.eclipse.gef4.fx.examples.snippets;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class ChangeCursor extends AbstractFXUIExample {

	public static void main(String[] args) {
		new ChangeCursor();
	}

	public ChangeCursor() {
		super("Curser Change Example");
	}

	@Override
	public Scene createScene() {
		Rectangle rect = new Rectangle(100, 50, 100, 50);
		Group root = new Group(rect);
		final Scene scene = new Scene(root, 400, 300);
		rect.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				scene.setCursor(Cursor.CROSSHAIR);
			}
		});
		rect.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				scene.setCursor(null);
			}
		});
		return scene;
	}

}