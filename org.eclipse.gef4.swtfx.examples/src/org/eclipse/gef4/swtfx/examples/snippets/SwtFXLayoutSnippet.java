package org.eclipse.gef4.swtfx.examples.snippets;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.swtfx.GeometryNode;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.gef4.swtfx.controls.SwtFXButton;
import org.eclipse.gef4.swtfx.examples.SwtFXApplication;

public class SwtFXLayoutSnippet extends SwtFXApplication {

	// color theme
	final public static double[] C0 = { 0.52, 0.49, 0.15 };
	final public static double[] C1 = { 1, 1, 1 };
	final public static double[] C2 = { 0.38, 0.08, 0.03 };
	final public static double[] C3 = { 0.49, 0.36, 0.20 };
	final public static double[] C4 = { 0.87, 0.83, 0.49 };

	private static <T extends IShape> Node gef4shape(T shape, double r,
			double g, double b) {
		GeometryNode<T> node = new GeometryNode<T>(shape);
		node.setFill(new Color(r, g, b, 1));
		node.setStroke(new Color(0, 0, 0, 1));
		return node;
	}

	public static void main(String[] args) {
		new SwtFXLayoutSnippet();
	}

	private static Shape shape(Shape shape, double r, double g, double b) {
		shape.setFill(new Color(r, g, b, 1));
		shape.setStroke(new Color(0, 0, 0, 1));
		return shape;
	}

	@Override
	public SwtFXScene createScene() {
		HBox hbox = new HBox();
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		hbox.getChildren().addAll(col1, col2);
		HBox.setHgrow(col1, Priority.ALWAYS);
		HBox.setHgrow(col2, Priority.ALWAYS);

		col1.getChildren().addAll(
				new Button("JavaFX 1"),
				gef4shape(
						new CurvedPolygon(PolyBezier.interpolateCubic(
								new Point(45, 45), new Point(40, 75),
								new Point(25, 100), new Point(75, 75),
								new Point(115, 100), new Point(115, 50),
								new Point(75, 25), new Point(45, 45))
								.toBezier()), C0[0], C0[1], C0[2]),
				shape(new Arc(0, 0, 50, 50, 15, 120) {
					{
						setType(ArcType.ROUND);
					}
				}, C1[0], C1[1], C1[2]), new SwtFXButton("SwtFX 1"));

		col2.getChildren().addAll(
				gef4shape(new Ellipse(0, 0, 60, 80), C2[0], C2[1], C2[2]),
				shape(new Rectangle(0, 0, 100, 50), C3[0], C3[1], C3[2]),
				new SwtFXButton("SwtFX 2"),
				shape(new Rectangle(0, 0, 100, 100) {
					{
						setArcHeight(20);
						setArcWidth(20);
					}
				}, C4[0], C4[1], C4[2]), new Button("JavaFX 2"));

		return new SwtFXScene(hbox, 400, 400);
	}

}
