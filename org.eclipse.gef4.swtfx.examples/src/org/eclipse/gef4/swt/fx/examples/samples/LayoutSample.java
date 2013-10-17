package org.eclipse.gef4.swt.fx.examples.samples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.swt.fx.examples.Application;
import org.eclipse.gef4.swtfx.ControlNode;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.AnchorPaneConstraints;
import org.eclipse.gef4.swtfx.layout.HBoxSimple;
import org.eclipse.gef4.swtfx.layout.VBoxSimple;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class LayoutSample extends Application {

	public static void main(String[] args) {
		new LayoutSample();
	}

	private ControlNode<Button> button(Scene scene, String label) {
		ControlNode<Button> button = new ControlNode<Button>(new Button(scene,
				SWT.PUSH));
		button.getControl().setText(label);
		return button;
	}

	private ControlNode<Label> label(Scene scene, String label) {
		ControlNode<Label> cnode = new ControlNode<Label>(new Label(scene,
				SWT.NONE));
		cnode.getControl().setText(label);
		return cnode;
	}

	private ShapeFigure<?> shape(IShape shape, double r, double g, double b) {
		ShapeFigure<IShape> figure = new ShapeFigure<IShape>(shape);
		figure.setFill(new RgbaColor((int) (255 * r), (int) (255 * g),
				(int) (255 * b)));
		return figure;
	}

	@Override
	public Scene start(Shell shell) {
		VBoxSimple root = new VBoxSimple();

		HBoxSimple hbox = new HBoxSimple();
		AnchorPane anchorPane = new AnchorPane();

		Scene scene = new Scene(shell, root);
		root.add(hbox, true);
		root.add(anchorPane, true);
		root.setGrower(anchorPane);

		// fill HBox
		VBoxSimple col1 = new VBoxSimple();
		VBoxSimple col2 = new VBoxSimple();
		VBoxSimple col3 = new VBoxSimple();
		hbox.addChildNodes(col1, col2, col3);

		col1.addChildNodes(
				button(scene, "abc"),
				shape(new Polygon(50, 0, 100, 100, 0, 100), 0, 1, 0),
				shape(new Pie(0, 0, 100, 100, Angle.fromDeg(15), Angle
						.fromDeg(120)), 0, 1, 1));

		col2.addChildNodes(shape(new Ellipse(0, 0, 70, 80), 1, 0, 0),
				shape(new Rectangle(0, 0, 100, 40), 0, 0, 1),
				button(scene, "test"),
				shape(new RoundedRectangle(0, 0, 100, 100, 10, 10), 1, 0, 1));

		col3.addChildNodes(shape(new Rectangle(0, 0, 100, 100), 1, 1, 0),
				button(scene, "foobar"), button(scene, "gaga"));

		// fill AnchorPane
		anchorPane.add(label(scene, "SWT Label"), new AnchorPaneConstraints(
				10d, 10d, null, null));
		anchorPane.add(new TextFigure("TextFigure"), new AnchorPaneConstraints(
				null, null, 10d, 10d));

		// set root pref size
		root.setPrefWidth(400);
		root.setPrefHeight(400);

		return scene;
	}
}
