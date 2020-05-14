/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.PolyBezier;
import org.eclipse.gef.geometry.planar.Polyline;

import javafx.application.Application;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BezierOffsetSnippet extends Application {

	private static final Collection<? extends Point> DEFAULT_POINTS = Arrays
			.asList(new Point(50, 50), new Point(50, 350), new Point(350, 350),
					new Point(350, 50));
	private static final double MIN_OFFSET = -50d;
	private static final double MAX_OFFSET = 50d;
	private static final double DEFAULT_OFFSET = 30d;

	private static Map<String, BezierCurve> testCurves = new HashMap<>();

	static {
		BezierCurve localIntersectionCurve = new BezierCurve(
				new Point(77.0, 36.0), new Point(113.0, 208.0),
				new Point(136.0, 208.0), new Point(177.0, 38.0));
		testCurves.put("local intersection", localIntersectionCurve);
		BezierCurve globalIntersectionCurve = new BezierCurve(
				new Point(246.0, 36.0), new Point(20.0, 212.0),
				new Point(432.0, 211.0), new Point(197.0, 38.0));
		testCurves.put("global intersection", globalIntersectionCurve);
		BezierCurve startClippedCurve = new BezierCurve(new Point(226.0, 145.0),
				new Point(52.0, 18.0), new Point(201.0, 272.0),
				new Point(380.0, 59.0));
		testCurves.put("start clipped", startClippedCurve);
		BezierCurve endClippedCurve = new BezierCurve(new Point(98.0, 29.0),
				new Point(78.0, 136.0), new Point(467.0, 175.0),
				new Point(390.0, 160.0));
		testCurves.put("end clipped", endClippedCurve);
		BezierCurve fullClippedCurve = new BezierCurve(new Point(318.0, 69.0),
				new Point(78.0, 136.0), new Point(467.0, 175.0),
				new Point(298.0, 66.0));
		testCurves.put("full clipped", fullClippedCurve);
		BezierCurve midNotClippedCurve = new BezierCurve(new Point(98.0, 61.0),
				new Point(473.0, 42.0), new Point(299.0, 200.0),
				new Point(281.0, 20.0));
		testCurves.put("mid not clipped", midNotClippedCurve);
		BezierCurve cuspNotWindingCurve = new BezierCurve(
				new Point(115.0, 38.0), new Point(488.0, 170.0),
				new Point(139.0, 179.0), new Point(473.0, 40.0));
		testCurves.put("cusp not winding", cuspNotWindingCurve);
		BezierCurve cuspWindingCurve = new BezierCurve(new Point(115.0, 38.0),
				new Point(489.0, 178.0), new Point(116.0, 173.0),
				new Point(473.0, 40.0));
		testCurves.put("cusp winding", cuspWindingCurve);
		BezierCurve highDegreeLoopCurve = new BezierCurve(new Point(30, 30),
				new Point(450, 30), new Point(450, 200), new Point(30, 200),
				new Point(30, 100), new Point(300, 100), new Point(300, 150),
				new Point(100, 150));
		testCurves.put("high degree loop", highDegreeLoopCurve);
		BezierCurve intersectionErrorCurve = new BezierCurve(
				new Point(115.0, 38.0), new Point(488.0, 170.0),
				new Point(131.0, 175.0), new Point(473.0, 40.0));
		testCurves.put("intersection error", intersectionErrorCurve);
	}

	public static Point EvalOffset(BezierCurve curve, BezierCurve hodograph,
			double distance, double parameter) {
		Point position = curve.get(parameter);
		Point p = hodograph.get(parameter);
		if (p.equals(0, 0)) {
			return null;
		}
		Point direction = new Vector(p).getNormalized()
				.getOrthogonalComplement().getMultiplied(distance).toPoint();
		return position.getTranslated(direction);
	}

	public static void main(String[] args) {
		launch();
	}

	// INPUT
	private List<Point> controlPoints = new ArrayList<>(DEFAULT_POINTS);
	private double offset = DEFAULT_OFFSET;

	// COMPUTED
	private BezierCurve curve;

	// OUTPUT
	private TextArea output;
	private Group controlPointsGroup;
	private Group curveGroup;
	private Group polylineGroup;
	private Group tillerHansonGroup;
	private Group tillerHansonOppositeGroup;
	private Group refinedGroup;
	private Group refinedOppositeGroup;

	// APPEARANCE
	private ObjectProperty<Color> controlPointsColorProperty = new SimpleObjectProperty<>(
			Color.BLUE);
	private ObjectProperty<Color> curveColorProperty = new SimpleObjectProperty<>(
			Color.BLACK);
	private ObjectProperty<Color> polylineColorProperty = new SimpleObjectProperty<>(
			Color.RED);
	private ObjectProperty<Color> tillerHansonColorProperty = new SimpleObjectProperty<>(
			Color.GREEN);
	private ObjectProperty<Color> tillerHansonOppositeColorProperty = new SimpleObjectProperty<>(
			Color.GREEN);
	private ObjectProperty<Color> refinedColorProperty = new SimpleObjectProperty<>(
			Color.PURPLE);
	private ObjectProperty<Color> refinedOppositeColorProperty = new SimpleObjectProperty<>(
			Color.PURPLE);

	private boolean refresh = true;

	ObjectBinding<Color> controlPointsFillColorBinding = new ObjectBinding<Color>() {
		{
			bind(controlPointsColorProperty);
		}

		@Override
		protected Color computeValue() {
			return controlPointsColorProperty.get().deriveColor(0d, 1d, 1d,
					0.25d);
		}
	};
	private Text maxApproxErrorText;
	private Text maxApproxOppositeErrorText;
	private Text refinedContainedPercentageText;
	private Text refinedContainedOppositePercentageText;
	private CheckBox validatorCheckBox;
	private boolean validation = true;

	protected BezierCurve computeCurve() {
		return new BezierCurve(controlPoints.toArray(new Point[0]));
	}

	private Polyline computePolyline() {
		return new Polyline(
				SampleOffset(curve, offset, 1024).toArray(new Point[] {}));
	}

	private List<BezierCurve> computeRefined() {
		return Arrays.asList(curve.getOffset(offset).toBezier());
	}

	private List<BezierCurve> computeRefinedOpposite() {
		return Arrays.asList(curve.getOffset(-offset).toBezier());
	}

	private List<BezierCurve> computeTillerHanson() {
		double pOffset = offset;
		PolyBezier rawOffset = getOffsetRaw(pOffset);
		return Arrays.asList(rawOffset.toBezier());
	}

	private List<BezierCurve> computeTillerHansonOpposite() {
		return Arrays.asList(getOffsetRaw(-offset).toBezier());
	}

	protected PolyBezier getOffsetRaw(double pOffset) {
		Method getOffsetRaw = null;
		try {
			getOffsetRaw = BezierCurve.class.getDeclaredMethod("getOffsetRaw",
					double.class);
			getOffsetRaw.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		PolyBezier rawOffset = null;
		try {
			rawOffset = (PolyBezier) getOffsetRaw.invoke(curve, pOffset);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return rawOffset;
	}

	private void printCurve() {
		// compute output for curve
		List<String> lines = new ArrayList<>();
		lines.add("BezierCurve curve = new BezierCurve(");
		int numPoints = curve.getPoints().length;
		for (int i = 0; i < numPoints; i++) {
			Point p = curve.getPoint(i);
			lines.add("  new Point(" + p.x + ", " + p.y + ")"
					+ (i < numPoints - 1 ? "," : ""));
		}
		lines.add(");");
		// change output text
		output.textProperty().set(String.join("\n", lines));
	}

	private void refresh(IGeometry geom, Group container,
			ObservableValue<? extends Paint> stroke) {
		container.getChildren().clear();
		if (geom instanceof PolyBezier) {
			boolean thin = true;
			boolean halve = false;
			for (BezierCurve partCurve : ((PolyBezier) geom).toBezier()) {
				GeometryNode<BezierCurve> visual = new GeometryNode<>(
						partCurve);
				visual.strokeProperty().bind(stroke);
				visual.setStrokeWidth(thin ? 1 : 2);
				visual.setOpacity(halve ? 0.65 : 1);
				visual.setStrokeLineCap(StrokeLineCap.BUTT);
				visual.setStrokeLineJoin(StrokeLineJoin.BEVEL);
				container.getChildren().add(visual);
				thin = !thin;
				if (thin) {
					halve = !halve;
				}
			}
		} else {
			GeometryNode<IGeometry> visual = new GeometryNode<>(geom);
			visual.strokeProperty().bind(stroke);
			container.getChildren().add(visual);
		}
	}

	protected void refreshAll() {
		if (!refresh) {
			return;
		}
		if (curveGroup.isVisible()) {
			refresh(curve = computeCurve(), curveGroup, curveColorProperty);
		}
		if (polylineGroup.isVisible()) {
			refresh(computePolyline(), polylineGroup, polylineColorProperty);
		}
		if (tillerHansonGroup.isVisible()) {
			PolyBezier polyBezier = new PolyBezier(
					computeTillerHanson().toArray(new BezierCurve[0]));
			refresh(polyBezier, tillerHansonGroup, tillerHansonColorProperty);
			if (validation && validatorCheckBox.isSelected()) {
				updateError(polyBezier);
			} else {
				maxApproxErrorText.setText("Error: -");
			}
		} else {
			maxApproxErrorText.setText("Error: -");
		}
		if (tillerHansonOppositeGroup.isVisible()) {
			PolyBezier polyBezier = new PolyBezier(
					computeTillerHansonOpposite().toArray(new BezierCurve[0]));
			refresh(polyBezier, tillerHansonOppositeGroup,
					tillerHansonOppositeColorProperty);
			if (validation && validatorCheckBox.isSelected()) {
				updateErrorOpposite(polyBezier);
			} else {
				maxApproxOppositeErrorText.setText("Error-O: -");
			}
		} else {
			maxApproxOppositeErrorText.setText("Error-O: -");
		}
		if (refinedGroup.isVisible()) {
			PolyBezier polyBezier = new PolyBezier(
					computeRefined().toArray(new BezierCurve[0]));
			refresh(polyBezier, refinedGroup, refinedColorProperty);
			if (validation && validatorCheckBox.isSelected()) {
				updateInsidePercentage(polyBezier);
			} else {
				refinedContainedPercentageText.setText("Inside: -");
			}
		} else {
			refinedContainedPercentageText.setText("Inside: -");
		}
		if (refinedOppositeGroup.isVisible()) {
			PolyBezier polyBezier = new PolyBezier(
					computeRefinedOpposite().toArray(new BezierCurve[0]));
			refresh(polyBezier, refinedOppositeGroup,
					refinedOppositeColorProperty);
			if (validation && validatorCheckBox.isSelected()) {
				updateInsidePercentageOpposite(polyBezier);
			} else {
				refinedContainedOppositePercentageText.setText("Inside-O: -");
			}
		} else {
			refinedContainedOppositePercentageText.setText("Inside-O: -");
		}
	}

	private Collection<? extends Point> Sample(BezierCurve c, int sampleCount) {
		List<Point> samples = new ArrayList<>();
		for (int i = 0; i < sampleCount; i++) {
			double t = i / (double) (sampleCount - 1);
			samples.add(c.get(t));
		}
		return samples;
	}

	private List<Point> SampleOffset(BezierCurve curve, double distance,
			int sampleCount) {
		BezierCurve derivative = curve.getDerivative();
		List<Point> samples = new ArrayList<>();
		for (int i = 0; i < sampleCount; i++) {
			double t = i / (double) (sampleCount - 1);
			Point sample = EvalOffset(curve, derivative, distance, t);
			if (sample != null) {
				samples.add(sample);
			}
		}
		return samples;
	}

	@Override
	public void start(Stage stage) throws Exception {
		// create drawing canvas
		InfiniteCanvas canvas = new InfiniteCanvas();
		controlPointsGroup = new Group();
		curveGroup = new Group();
		polylineGroup = new Group();
		tillerHansonGroup = new Group();
		tillerHansonOppositeGroup = new Group();
		refinedGroup = new Group();
		refinedOppositeGroup = new Group();
		canvas.getContentGroup().getChildren().addAll(polylineGroup,
				tillerHansonGroup, tillerHansonOppositeGroup, refinedGroup,
				refinedOppositeGroup, curveGroup, controlPointsGroup);

		// create options menu
		VBox vbox = new VBox();
		vbox.setFillWidth(true);
		Slider offsetSlider = new Slider(MIN_OFFSET, MAX_OFFSET,
				DEFAULT_OFFSET);
		Text offsetText = new Text();
		maxApproxErrorText = new Text("Error: ");
		maxApproxOppositeErrorText = new Text("Error-O: ");
		refinedContainedPercentageText = new Text("Inside: ");
		refinedContainedOppositePercentageText = new Text("Inside-O: ");

		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().addAll("local intersection", "global intersection",
				"start clipped", "end clipped", "full clipped",
				"mid not clipped", "cusp not winding", "cusp winding",
				"high degree loop", "intersection error");
		comboBox.setOnAction((ae) -> {
			BezierCurve testCurve = testCurves.get(comboBox.getValue())
					.getCopy();
			controlPoints.clear();
			controlPoints.addAll(Arrays.asList(testCurve.getPoints()));
			updateControlPoints();
			refreshAll();
		});

		validatorCheckBox = new CheckBox("Validation");
		validatorCheckBox.setSelected(false);
		validatorCheckBox.setOnAction((ae) -> {
			refreshAll();
		});

		vbox.getChildren().addAll(new Text("Testcurve:"), comboBox,
				new Separator(), offsetText, offsetSlider, new Separator(),
				new Text("View:"),
				ToggleButtonColor("Control Points", controlPointsColorProperty,
						controlPointsGroup.visibleProperty(), true),
				ToggleButtonColor("Bézier Curve", curveColorProperty,
						curveGroup.visibleProperty(), true),
				ToggleButtonColor("Sampled Polyline", polylineColorProperty,
						polylineGroup.visibleProperty(), false),
				ToggleButtonColor("Approximated", tillerHansonColorProperty,
						tillerHansonGroup.visibleProperty(), false),
				ToggleButtonColor("Approximated-O",
						tillerHansonOppositeColorProperty,
						tillerHansonOppositeGroup.visibleProperty(), false),
				ToggleButtonColor("Refined", refinedColorProperty,
						refinedGroup.visibleProperty(), true),
				ToggleButtonColor("Refined-O", refinedOppositeColorProperty,
						refinedOppositeGroup.visibleProperty(), true),
				new Separator(), validatorCheckBox, maxApproxErrorText,
				maxApproxOppositeErrorText, refinedContainedPercentageText,
				refinedContainedOppositePercentageText);

		// recompute visuals if offset is changed
		offsetText.textProperty()
				.bind(offsetSlider.valueProperty().asString("Offset: %.0f"));
		offsetSlider.valueProperty()
				.addListener((observable, oldValue, newValue) -> {
					offset = newValue.doubleValue();
					refreshAll();
				});

		// create text output window
		HBox outputBox = new HBox();
		VBox buttonsBox = new VBox();
		Button printCurveButton = new Button("Print Curve");
		printCurveButton.setOnAction((actionEvent) -> {
			printCurve();
		});
		buttonsBox.getChildren().addAll(printCurveButton);
		output = new TextArea();
		output.setEditable(false);
		outputBox.getChildren().addAll(output, buttonsBox);

		// prepare layout
		BorderPane borderPane = new BorderPane();
		borderPane.setRight(vbox);
		borderPane.setCenter(canvas);
		borderPane.setBottom(outputBox);
		Scene scene = new Scene(borderPane, 900, 600);

		scene.addEventFilter(ScrollEvent.SCROLL,
				new EventHandler<ScrollEvent>() {
					@Override
					public void handle(ScrollEvent event) {
						canvas.getContentTransform().appendScale(
								event.getDeltaY() < 0 ? 0.9 : 1.1,
								event.getDeltaY() < 0 ? 0.9 : 1.1);
					}
				});

		// refresh view
		updateControlPoints();
		refreshAll();

		// prepare stage
		stage.setScene(scene);
		stage.sizeToScene();

		// start the application
		stage.show();
	}

	public HBox ToggleButtonColor(String text,
			ObjectProperty<Color> colorProperty,
			BooleanProperty toggledProperty, boolean isToggled) {
		HBox hbox = new HBox();
		ToggleButton toggleButton = new ToggleButton(text);
		toggleButton.setOnAction((ae) -> {
			refreshAll();
		});
		ColorPicker colorPicker = new ColorPicker(colorProperty.get());
		colorProperty.bind(colorPicker.valueProperty());
		hbox.getChildren().addAll(toggleButton, colorPicker);
		toggledProperty.bind(toggleButton.selectedProperty());
		toggleButton.setSelected(isToggled);
		return hbox;
	}

	protected void updateControlPoints() {
		controlPointsGroup.getChildren().clear();
		for (int i = 0; i < controlPoints.size(); i++) {
			// get point
			Point cp = controlPoints.get(i);
			// create visual
			Circle cpVisual = new Circle(5);
			cpVisual.setCenterX(cp.x);
			cpVisual.setCenterY(cp.y);
			// bind colors
			cpVisual.strokeProperty().bind(controlPointsColorProperty);
			cpVisual.fillProperty().bind(controlPointsFillColorBinding);
			// add to view
			controlPointsGroup.getChildren().add(cpVisual);
			// register interactions
			final int index = i;
			final double[] cX = new double[] { 0d };
			final double[] cY = new double[] { 0d };
			final double[] initX = new double[] { 0d };
			final double[] initY = new double[] { 0d };
			cpVisual.setOnMousePressed((me) -> {
				validation = false;
				initX[0] = me.getSceneX();
				initY[0] = me.getSceneY();
				cX[0] = cpVisual.getCenterX();
				cY[0] = cpVisual.getCenterY();
			});
			cpVisual.setOnMouseDragged((me) -> {
				Point pos = new Point(cX[0] + me.getSceneX() - initX[0],
						cY[0] + me.getSceneY() - initY[0]);
				controlPoints.set(index, pos);
				cpVisual.setCenterX(pos.x);
				cpVisual.setCenterY(pos.y);
				refreshAll();
			});
			cpVisual.setOnMouseReleased((me) -> {
				validation = true;
				refreshAll();
			});
		}
	}

	private void updateError(PolyBezier polyBezier) {
		List<Point> samples = SampleOffset(curve, offset, 1024);
		double maxError = 0;
		for (Point p : samples) {
			Point proj = polyBezier.getProjection(p);
			double error = proj.getDistance(p);
			if (error > maxError) {
				maxError = error;
			}
		}
		maxApproxErrorText
				.setText("Error: " + Math.floor(10000 * maxError) / 10000);
	}

	private void updateErrorOpposite(PolyBezier polyBezier) {
		List<Point> samples = SampleOffset(curve, -offset, 1024);
		double maxError = 0;
		for (Point p : samples) {
			Point proj = polyBezier.getProjection(p);
			double error = proj.getDistance(p);
			if (error > maxError) {
				maxError = error;
			}
		}
		maxApproxOppositeErrorText
				.setText("Error-O: " + Math.floor(10000 * maxError) / 10000);
	}

	private void updateInsidePercentage(PolyBezier polyBezier) {
		List<Point> offsetSamples = new ArrayList<>();
		for (BezierCurve c : polyBezier.toBezier()) {
			offsetSamples.addAll(Sample(c, 32));
		}
		int insideCount = 0;
		double dMin = Math.abs(offset) - 0.02;
		for (Point p : offsetSamples) {
			double d = curve.getProjection(p).getDistance(p);
			if (d < dMin) {
				insideCount++;
			}
		}
		double insidePercentage = offsetSamples.size() == 0 ? 0
				: Math.floor(
						10000 * (insideCount / (double) offsetSamples.size()))
						/ 100;
		refinedContainedPercentageText
				.setText("Inside: " + insidePercentage + "%");
	}

	private void updateInsidePercentageOpposite(PolyBezier polyBezier) {
		List<Point> offsetSamples = new ArrayList<>();
		for (BezierCurve c : polyBezier.toBezier()) {
			offsetSamples.addAll(Sample(c, 32));
		}
		int insideCount = 0;
		double dMin = Math.abs(offset) - 0.02;
		for (Point p : offsetSamples) {
			double d = curve.getProjection(p).getDistance(p);
			if (d < dMin) {
				insideCount++;
			}
		}
		double insidePercentage = offsetSamples.size() == 0 ? 0
				: Math.floor(
						10000 * (insideCount / (double) offsetSamples.size()))
						/ 100;
		refinedContainedOppositePercentageText
				.setText("Inside-O: " + insidePercentage + "%");
	}
}
