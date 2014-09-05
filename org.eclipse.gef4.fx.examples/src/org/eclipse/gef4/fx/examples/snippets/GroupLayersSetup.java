package org.eclipse.gef4.fx.examples.snippets;

import java.util.Calendar;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXGridLayer;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;

public class GroupLayersSetup extends Application {

	public static void main(final String[] args) {
		launch();
	}

	public StackPane layersStackPane;
	public Group contentLayer;
	public Group handleLayer;
	public Group feedbackLayer;
	public Scene scene;
	private ScrollPane scrollPane;
	private FXGridLayer gridLayer;
	private Group bgLayer;

	private final SimpleObjectProperty<Scale> scaleProperty = new SimpleObjectProperty<Scale>(
			new Scale());

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setScene(createScene());
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public Scene createScene() {
		// scale
		scaleProperty.get().setX(1);
		scaleProperty.get().setY(1);

		// layers
		bgLayer = new Group();
		bgLayer.setManaged(false);
		final Rectangle bgContent = getBGRect(0.5, 0, 0, 0.5);
		final Rectangle bgFeedback = getBGRect(0, 0.5, 0, 0.5);
		final Rectangle bgHandle = getBGRect(0, 0, 0.5, 0.5);

		gridLayer = new FXGridLayer();
		// gridLayer.setStyle("-fx-background-color: rgba(127, 127, 0,
		// 0.5);");

		contentLayer = new Group();
		showBg(contentLayer, bgContent);
		contentLayer.setPickOnBounds(true);

		feedbackLayer = new Group();
		showBg(feedbackLayer, bgFeedback);
		feedbackLayer.setPickOnBounds(false);
		feedbackLayer.setMouseTransparent(true);

		handleLayer = new Group();
		showBg(handleLayer, bgHandle);
		handleLayer.setPickOnBounds(false);

		// scrolling
		final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";
		scrollPane = new ScrollPane();
		final Group spi = new Group(gridLayer, contentLayer,
				feedbackLayer, handleLayer);
		scrollPane.setContent(spi);
		scrollPane.setPannable(false);
		scrollPane.setStyle(SCROLL_PANE_STYLE);

		// scene
		scene = new Scene(scrollPane, 800, 600);

		// register the grid layer
		gridLayer.bindToScale(scaleProperty);
		gridLayer.bindMinSizeToBounds(scrollPane.viewportBoundsProperty());
		gridLayer.bindPrefSizeToUnionedBounds(new ReadOnlyObjectProperty[] {
				contentLayer.boundsInParentProperty(),
				feedbackLayer.boundsInParentProperty(),
				handleLayer.boundsInParentProperty() });
		
		scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {

			@Override
			public void changed(ObservableValue<? extends Bounds> observable,
					Bounds oldValue, Bounds newValue) {
				System.out.println("viewport width " + newValue.getWidth());
			}
		});
		gridLayer.minWidthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				System.out.println("grid min width (unscaled)" + (newValue.doubleValue()));
			}
		});
		gridLayer.prefWidthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				System.out.println("grid pref width (unscaled)" + (newValue.doubleValue()));
			}
		});
		gridLayer.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {

			@Override
			public void changed(ObservableValue<? extends Bounds> observable,
					Bounds oldValue, Bounds newValue) {
				System.out.println("Bounds in parent " + newValue.getWidth());
			}
		});

		// create content and feedback
		final FXGeometryNode red = new FXGeometryNode<org.eclipse.gef4.geometry.planar.Rectangle>(
				new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 100, 100));
		final FXGeometryNode blue = new FXGeometryNode<org.eclipse.gef4.geometry.planar.Rectangle>(
				new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 100, 100));
		red.setFill(Color.RED);
		blue.setFill(Color.BLUE);
		red.setStrokeType(StrokeType.OUTSIDE);
		blue.setStrokeType(StrokeType.OUTSIDE);
		red.setStrokeWidth(25);
		blue.setStrokeWidth(25);

		Rectangle smallRed = q(50, Color.RED);
		contentLayer.getChildren().addAll(red, smallRed);
		final Rectangle feedbackElement = q(50, Color.BLUE);
		feedbackElement.setLayoutX(200);
		feedbackElement.setLayoutY(0);
		feedbackLayer.getChildren().addAll(blue, feedbackElement);

		// scale content layer
		bgContent.getTransforms().add(scaleProperty.get());
		contentLayer.getTransforms().add(scaleProperty.get());

		// move to initial positions
		move(red, 100, 0);
		move(blue, 200, 100);

		new VisualChangeListener() {
			@Override
			protected void localToParentTransformChanged(final Node source,
					final Transform oldTransform, final Transform newTransform) {
				update();
			}

			private void update() {
				final IGeometry geomInRed = red.getGeometry();
				final IGeometry geomInScene = FXUtils.localToScene(red,
						geomInRed);
				final IGeometry geomInBlue = FXUtils.sceneToLocal(blue,
						geomInScene);
				final IGeometry transformedInBlue = geomInBlue
						.getTransformed(new AffineTransform(1, 0, 0, 1, 100,
								100));
				blue.setGeometry(transformedInBlue);
			}

			@Override
			protected void boundsInLocalChanged(final Bounds oldBounds,
					final Bounds newBounds) {
				update();
			}
		}.register(red, blue);

		draggable(red);
		scalable(smallRed);

		return scene;
	}

	private void scalable(Node n) {
		n.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
					Scale s = Scale.scale(scaleProperty.get().getX() + 0.1, scaleProperty.get().getY() + 0.1);
					scaleProperty.get().setX(s.getX());
					scaleProperty.get().setY(s.getY());
				}
			}
		});
	}

	private void showBg(final Node layer, final Rectangle bg) {
		layer.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(
					final ObservableValue<? extends Bounds> observable,
					final Bounds oldValue, final Bounds newValue) {
				bg.setX(newValue.getMinX() + layer.getLayoutX());
				bg.setY(newValue.getMinY() + layer.getLayoutY());
				bg.setWidth(newValue.getWidth());
				bg.setHeight(newValue.getHeight());
			}
		});
	}

	private Rectangle getBGRect(final double r, final double g, final double b,
			final double a) {
		final Rectangle bg = new Rectangle();
		bg.setStrokeWidth(0);
		bg.setFill(new Color(r, g, b, a));
		bgLayer.getChildren().add(bg);
		return bg;
	}

	private void draggable(final Node n) {
		n.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			double x = 0, y = 0;

			@Override
			public void handle(final MouseEvent event) {
				if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
					move(n, event.getSceneX() - x, event.getSceneY() - y);
				}
				x = event.getSceneX();
				y = event.getSceneY();
			}
		});
	}

	private <T extends Node> T move(final T n, final double byX,
			final double byY) {
		n.relocate(n.getLayoutX() + n.getLayoutBounds().getMinX() + byX,
				n.getLayoutY() + n.getLayoutBounds().getMinY() + byY);
		return n;
	}

	private Rectangle q(final double size, final Color fill) {
		final Rectangle q = new Rectangle(size, size);
		q.setFill(fill);
		q.setStroke(Color.BLACK);
		return q;
	}

}
