package org.eclipse.gef4.swtfx.examples.snippets;

import javafx.embed.swt.FXCanvas;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.gef4.swtfx.controls.SwtFXButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtFXLayoutSnippet {

	private static SwtFXScene createScene() {
		HBox hbox = new HBox();
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		hbox.getChildren().addAll(col1, col2);
		HBox.setHgrow(col1, Priority.ALWAYS);
		HBox.setHgrow(col2, Priority.ALWAYS);

		col1.getChildren().addAll(new Button("abc"),
				shape(new Polygon(50, 0, 100, 100, 0, 100), 0, 1, 0),
				shape(new Arc(0, 0, 50, 50, 15, 120) {
					{
						setType(ArcType.ROUND);
					}
				}, 0, 1, 1), new SwtFXButton("123"));

		col2.getChildren().addAll(shape(new Ellipse(30, 40, 30, 40), 1, 0, 0),
				shape(new Rectangle(0, 0, 100, 50), 0, 0, 1),
				new SwtFXButton("foobar"), shape(new Rectangle(0, 0, 100, 100) {
					{
						setArcHeight(20);
						setArcWidth(20);
					}
				}, 1, 0, 1));

		// create scene (and set scene size)
		return new SwtFXScene(hbox, 400, 300);
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		FXCanvas canvas = new SwtFXCanvas(shell, SWT.NONE);

		SwtFXScene scene = createScene();
		canvas.setScene(scene);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static Shape shape(Shape shape, double r, double g, double b) {
		shape.setFill(new Color(r, g, b, 1));
		shape.setStroke(new Color(0, 0, 0, 1));
		return shape;
	}

}
