package org.eclipse.gef4.fx.examples.snippets;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXPolyBezierConnectionRouter;
import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class ScrollPaneExApp extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	private Group contentLayer;

	protected ScrollPaneEx createRoot() {
		contentLayer = new Group();
		contentLayer.setPickOnBounds(true);
		contentLayer.boundsInLocalProperty().addListener(
				new ChangeListener<Bounds>() {
					@Override
					public void changed(
							ObservableValue<? extends Bounds> observable,
							Bounds oldValue, Bounds newValue) {
					}
				});

		Group feedbackLayer = new Group();
		feedbackLayer.setMouseTransparent(true);

		Group handleLayer = new Group();

		Canvas canvas = new Canvas(10, 10);

		Group layers = new Group();
		layers.getChildren().add(canvas); // XXX
		layers.getChildren().addAll(contentLayer, feedbackLayer, handleLayer);

		ScrollPaneEx scrollPane = new ScrollPaneEx();
		scrollPane.getContentGroup().getChildren().add(layers);

		return scrollPane;
	}

	private void refresh(Path visual, IShape geometry, double x, double y) {
		visual.getElements().setAll(
				Geometry2JavaFX.toPathElements(geometry.toPath()));
		visual.relocate(x, y);
		visual.setStrokeWidth(0.5);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		ScrollPaneEx scrollPaneEx = createRoot();
		Scene scene = new Scene(scrollPaneEx);
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);

		// create model group in content layer
		Group modelGroup = new Group();
		modelGroup.setAutoSizeChildren(false);
		contentLayer.getChildren().add(modelGroup);

		// connection
		FXConnection connection = new FXConnection();
		connection.setRouter(new FXPolyBezierConnectionRouter());
		modelGroup.getChildren().add(connection);
		List<Point> wayPoints = Arrays.asList(new Point[] { new Point(250.0,
				70.0) });
		connection.setWayPoints(wayPoints); // XXX
		connection.getCurveNode().setStrokeWidth(3); // XXX
		// 2.9999996423721315.... is the real number

		// anchor at handles
		Path topHandle = new Path();
		connection.setStartAnchor(new FXChopBoxAnchor(topHandle));
		Path botHandle = new Path();
		connection.setEndAnchor(new FXChopBoxAnchor(botHandle));

		// connection.doRefreshGeometry();
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(new Point(0.0, 0.0), new Point(
		// 250.0, 70.0), new Point(0.0, 0.0)));

		// refresh handles
		modelGroup.getChildren().add(topHandle);
		refresh(topHandle, new Rectangle(0, 0, 10, 10), 243, 15);

		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(new Point(11.0, 6.591836929321289),
		// new Point(250.0, 70.0), new Point(0.0, 0.0)));
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.09230041503906, 11.0), new Point(250.0,
		// 70.0), new Point(0.0, 0.0)));
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.12245178222656, 27.0), new Point(250.0,
		// 70.0), new Point(0.0, 0.0)));
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.11734008789062, 26.75), new Point(250.0,
		// 70.0), new Point(0.0, 0.0)));

		modelGroup.getChildren().add(botHandle);
		refresh(botHandle, new Rectangle(0, 0, 10, 10), 243, 109);

		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.11734008789062, 26.75), new Point(250.0,
		// 70.0), new Point(11.0, 6.591836929321289)));
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.11734008789062, 26.75), new Point(250.0,
		// 70.0), new Point(249.09230041503906, 11.0)));
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.11734008789062, 26.75), new Point(250.0,
		// 70.0), new Point(249.13333129882812, 109.0)));
		// connection.getCurveNode().setGeometry(
		// PolyBezier.interpolateCubic(
		// new Point(249.11734008789062, 26.75), new Point(250.0,
		// 70.0), new Point(249.12777709960938, 109.25)));

		primaryStage.setTitle("Debugging");
		primaryStage.sizeToScene();
		primaryStage.show();
	}

}
