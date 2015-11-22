/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.AdapterStore;
import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.ChopBoxAnchor;
import org.eclipse.gef4.fx.gestures.AbstractMouseDragGesture;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

public class ChopBoxELetterSnippet extends AbstractFxExample {

	private static class ComputationStrategy
			extends ChopBoxAnchor.IComputationStrategy.Impl {

		@Override
		protected Point computeAnchorageReferencePointInScene(Node node,
				IGeometry geometryInLocal) {
			return super.computeAnchorageReferencePointInScene(node,
					geometryInLocal);
		}

		@Override
		protected ICurve getOutline(IGeometry geometry) {
			return super.getOutline(geometry);
		}
	}

	private abstract static class OnDrag extends AbstractMouseDragGesture {
		private Node target;

		public OnDrag(Node target) {
			this.target = target;
		}

		@Override
		protected void drag(Node target, MouseEvent event, double dx,
				double dy) {
			// consider only mouse drags on our target
			if (target == this.target) {
				// do not drag outside of scene
				if (event.getX() >= 0 && event.getY() >= 0
						&& event.getX() <= WIDTH && event.getY() <= HEIGHT) {
					dragTo(event.getX(), event.getY());
				}
			}
		}

		public abstract void dragTo(double x, double y);

		@Override
		protected void press(Node target, MouseEvent event) {
		}

		@Override
		protected void release(Node target, MouseEvent event, double dx,
				double dy) {
		}
	}

	// TODO: use CSS for styling
	// configuration (colors and sizes)
	private static final Paint VERTEX_STROKE = Color.web("#5a61af");

	private static final Paint VERTEX_FILL = Color.web("#d5faff");

	private static final double VERTEX_RADIUS = 2.5;

	private static final Paint DISTANCE_LINE_STROKE_NORMAL = Color.GREY;
	private static final Paint DISTANCE_LINE_STROKE_HOVER = Color.BLACK;
	private static final double DISTANCE_LINE_STROKE_WIDTH_NORMAL = 0.5;
	private static final double DISTANCE_LINE_STROKE_WIDTH_HOVER = 2.5;
	private static final double DISTANCE_LINE_SELECTION_STROKE_WIDTH = 5.5;

	private static final double DISTANCE_TEXT_SCALE = 1.5;
	private static final Paint DISTANCE_TEXT_STROKE = Color.TRANSPARENT;
	private static final Paint DISTANCE_TEXT_FILL = Color.BLACK;

	private static final Paint CENTER_POINT_STROKE = Color.BLACK;
	private static final Paint CENTER_POINT_FILL = Color.ORANGE;
	private static final double CENTER_POINT_RADIUS = 3;

	private static final double ELETTER_REFERENCE_POINT_RADIUS = 3;
	private static final Paint ELETTER_REFERENCE_POINT_STROKE = Color.BLACK;
	private static final Paint ELETTER_REFERENCE_POINT_FILL = Color.ORANGE;

	private static final Paint REFERENCE_POINT_FILL = Color.BLUE;
	private static final Paint REFERENCE_POINT_STROKE = Color.BLACK;
	private static final double REFERENCE_POINT_RADIUS = 3;

	private static final Paint CHOP_BOX_POINT_FILL = Color.RED;
	private static final Paint CHOP_BOX_POINT_STROKE = Color.BLACK;
	private static final double CHOP_BOX_POINT_RADIUS = 3;

	private static final double INTERSECTION_RADIUS = 3;
	private static final Paint INTERSECTION_STROKE = Color.BLACK;
	private static final Paint INTERSECTION_FILL = Color.DARKRED;

	private static final Paint CHOP_BOX_LINE_STROKE_REAL = Color.rgb(99, 123,
			71);
	private static final double CHOP_BOX_LINE_STROKE_WIDTH_REAL = 2;
	private static final Paint CHOP_BOX_LINE_STROKE_IMAGINARY = Color.DARKRED;
	private static final double CHOP_BOX_LINE_STROKE_WIDTH_IMAGINARY = 2;
	private static final Paint CHOP_BOX_LINE_STROKE_IMAGINARY_WITH_FILL = Color.LIGHTGREY;

	private static final double PAD = 100;
	private static final double HEIGHT = 480;
	private static final double WIDTH = 640;

	private static final boolean INITIAL_VERTICES_VISIBLE = false;
	private static final boolean INITIAL_LINES_VISIBLE = false;
	private static final boolean INITIAL_FILL_VISIBLE = true;
	private static final boolean INITIAL_MIN_DISTANCE_VISIBLE = false;
	private static final boolean INITIAL_INTERSECTIONS_VISIBLE = true;
	private static final boolean INITIAL_CENTER_VISIBLE = false;
	private static final boolean INITIAL_ELETTER_REFERENCE_VISIBLE = true;

	public static void main(String[] args) {
		launch();
	}

	private Scene scene;

	private BorderPane root;
	private Group markerLayer; // between shape and intersections
	private Group intersectionLayer; // between markers and interaction elements
	private Group interactionLayer; // always on top
	private GeometryNode<CurvedPolygon> eLetterShape;
	private ChopBoxAnchor chopBoxAnchor;
	private ReadOnlyMapWrapper<AnchorKey, Point> referencePointProperty = new ReadOnlyMapWrapper<AnchorKey, Point>(
			FXCollections.<AnchorKey, Point> observableHashMap());
	private Map<AnchorKey, Circle> chopBoxPoints = new HashMap<AnchorKey, Circle>();
	private Map<AnchorKey, Line> chopBoxLinesReal = new HashMap<AnchorKey, Line>();
	private Map<AnchorKey, Line> chopBoxLinesImaginary = new HashMap<AnchorKey, Line>();
	private Map<AnchorKey, List<Node>> intersections = new HashMap<AnchorKey, List<Node>>();
	private List<Node> vertices = new ArrayList<Node>();
	private List<Node> distanceLines = new ArrayList<Node>();
	private List<Node> minDistanceNodes = new ArrayList<Node>();
	private Circle boundsCenterNode;
	private Node eLetterReferenceNode;

	private ComputationStrategy computationStrategy = new ComputationStrategy();
	private MapChangeListener<AnchorKey, Point> anchorPositionListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			Point p = change.getValueAdded();
			if (p != null) {
				onAnchorPositionChange(change.getKey(), p);
			}
		}
	};

	public ChopBoxELetterSnippet() {
		super("FX ChopBox E-Letter Snippet");
	}

	private void attachToChopBoxAnchor(final AnchorKey ak,
			final ReadOnlyMapWrapper<AnchorKey, Point> referencePointProperty) {
		AdapterStore as = new AdapterStore();
		as.setAdapter(
				AdapterKey.get(ChopBoxAnchor.IReferencePointProvider.class),
				new ChopBoxAnchor.IReferencePointProvider() {
					@Override
					public ReadOnlyMapWrapper<AnchorKey, Point> referencePointProperty() {
						return referencePointProperty;
					}
				});
		chopBoxAnchor.attach(ak, as);
		updateChopBoxLines(ak);
	}

	private Circle createBoundsCenterNode(Point2D boundsCenterInScene) {
		Circle centerNode = new Circle(CENTER_POINT_RADIUS);
		centerNode.setFill(CENTER_POINT_FILL);
		centerNode.setStroke(CENTER_POINT_STROKE);
		centerNode.setCenterX(boundsCenterInScene.getX());
		centerNode.setCenterY(boundsCenterInScene.getY());
		return centerNode;
	}

	private Line createChopBoxLineImaginary(AnchorKey ak) {
		Line chopBoxLineImaginary = new Line();
		chopBoxLineImaginary.getStrokeDashArray().addAll(10d, 10d);
		chopBoxLineImaginary
				.setStrokeWidth(CHOP_BOX_LINE_STROKE_WIDTH_IMAGINARY);
		chopBoxLineImaginary.setStroke(CHOP_BOX_LINE_STROKE_IMAGINARY);
		return chopBoxLineImaginary;
	}

	private Line createChopBoxLineReal(AnchorKey ak) {
		Line chopBoxLineReal = new Line();
		chopBoxLineReal.setStrokeLineCap(StrokeLineCap.BUTT);
		chopBoxLineReal.setStrokeWidth(CHOP_BOX_LINE_STROKE_WIDTH_REAL);
		chopBoxLineReal.setStroke(CHOP_BOX_LINE_STROKE_REAL);
		return chopBoxLineReal;
	}

	private Circle createChopBoxNode() {
		Circle chopBoxPointNode = new Circle(CHOP_BOX_POINT_RADIUS);
		chopBoxPointNode.setFill(CHOP_BOX_POINT_FILL);
		chopBoxPointNode.setStroke(CHOP_BOX_POINT_STROKE);
		return chopBoxPointNode;
	}

	private Line createDistanceLine(Point2D boundsCenterInScene,
			Point2D vertexInScene) {
		final Line distanceLine = new Line(vertexInScene.getX(),
				vertexInScene.getY(), boundsCenterInScene.getX(),
				boundsCenterInScene.getY());
		distanceLine.setStrokeWidth(DISTANCE_LINE_STROKE_WIDTH_NORMAL);
		distanceLine.getStrokeDashArray().addAll(5d, 5d);
		distanceLine.setStroke(DISTANCE_LINE_STROKE_NORMAL);
		return distanceLine;
	}

	private Text createDistanceText(double distance) {
		final Text distanceText = new Text(String.format("%.2f", distance));
		// TODO: make configurable
		distanceText.setScaleX(DISTANCE_TEXT_SCALE);
		distanceText.setScaleY(DISTANCE_TEXT_SCALE);
		distanceText.setStroke(DISTANCE_TEXT_STROKE);
		distanceText.setFill(DISTANCE_TEXT_FILL);
		return distanceText;
	}

	private Node createELetterReferenceNode() {
		Circle node = new Circle(ELETTER_REFERENCE_POINT_RADIUS);
		node.setStroke(ELETTER_REFERENCE_POINT_STROKE);
		node.setFill(ELETTER_REFERENCE_POINT_FILL);
		Point p = computationStrategy.computeAnchorageReferencePointInScene(
				eLetterShape, eLetterShape.getGeometry());
		node.setCenterX(p.x);
		node.setCenterY(p.y);
		return node;
	}

	private GeometryNode<CurvedPolygon> createELetterShape() {
		GeometryNode<CurvedPolygon> eLetterShape = new GeometryNode<CurvedPolygon>(
				GeometryNodeSnippet.createEShapeGeometry());
		eLetterShape.relocate(PAD, PAD);
		eLetterShape.resize(WIDTH - PAD - PAD, HEIGHT - PAD - PAD);
		return eLetterShape;
	}

	private Node createIntersectionNode(Point p) {
		Circle c = new Circle(INTERSECTION_RADIUS);
		c.setStroke(INTERSECTION_STROKE);
		c.setFill(INTERSECTION_FILL);
		c.setCenterX(p.x);
		c.setCenterY(p.y);
		return c;
	}

	private void createReferencePoint(final double x, final double y) {
		final Circle referencePointNode = createReferencePointNode(x, y);
		interactionLayer.getChildren().add(referencePointNode);
		Circle chopBoxPointNode = createChopBoxNode();

		// create key for the anchor relation (role is arbitrary)
		final AnchorKey ak = new AnchorKey(referencePointNode, "link");

		// create real and imaginary chop box lines
		Line chopBoxLineReal = createChopBoxLineReal(ak);
		Line chopBoxLineImaginary = createChopBoxLineImaginary(ak);
		intersectionLayer.getChildren().addAll(chopBoxLineImaginary,
				chopBoxPointNode);
		markerLayer.getChildren().add(chopBoxLineReal);
		chopBoxLineReal.toBack();
		chopBoxLineImaginary.toBack();

		// associate the chop box point and line with that key
		chopBoxPoints.put(ak, chopBoxPointNode);
		chopBoxLinesReal.put(ak, chopBoxLineReal);
		chopBoxLinesImaginary.put(ak, chopBoxLineImaginary);

		// put initial reference point
		referencePointProperty.put(ak, new Point(x, y));

		// adjust reference point on drag
		OnDrag dragGesture = new OnDrag(referencePointNode) {
			@Override
			public void dragTo(double x, double y) {
				// update center point
				referencePointNode.setCenterX(x);
				referencePointNode.setCenterY(y);
				// update reference point
				referencePointProperty.put(ak, new Point(x, y));
				updateChopBoxLines(ak);
			}
		};
		dragGesture.setScene(scene);

		attachToChopBoxAnchor(ak, referencePointProperty);
	}

	private Circle createReferencePointNode(final double x, final double y) {
		final Circle referencePointNode = new Circle(REFERENCE_POINT_RADIUS);
		referencePointNode.setFill(REFERENCE_POINT_FILL);
		referencePointNode.setStroke(REFERENCE_POINT_STROKE);
		referencePointNode.setCenterX(x);
		referencePointNode.setCenterY(y);
		referencePointNode.setEffect(GeometryNodeSnippet.createShadowEffect());
		return referencePointNode;
	}

	@Override
	public Scene createScene() {
		root = new BorderPane();
		root.setStyle("-fx-background-color: white;");
		scene = new Scene(root, WIDTH, HEIGHT);

		// description (what is demonstrated)
		Label descriptionLabel = new Label(
				"This example demonstrates the chop box anchor position computation. An ChopBoxAnchor is associated with the E letter shape (anchorage). The computation uses 2 reference points: the anchorage reference point (orange) and the anchored reference point (blue). The red point is the resulting anchor position.");
		descriptionLabel.setStyle("-fx-font-size: 10pt");
		descriptionLabel.setWrapText(true);
		descriptionLabel.resizeRelocate(10, 10, WIDTH - 20, PAD - 20);
		descriptionLabel.setAlignment(Pos.TOP_LEFT);
		root.getChildren().add(descriptionLabel);

		// legend (how to interact)
		Label legendLabelLeft = new Label(
				"You can...\n...drag the blue reference points\n...press <C> to toggle the center point\n...press <V> to toggle the shape vertices\n...press <L> to toggle the distance lines");
		legendLabelLeft.setStyle("-fx-font-size: 10pt");
		legendLabelLeft.resizeRelocate(10, HEIGHT - PAD + 10, WIDTH - 20,
				PAD - 20);
		legendLabelLeft.setAlignment(Pos.BOTTOM_LEFT);
		root.getChildren().add(legendLabelLeft);

		Label legendLabelRight = new Label(
				"...press <D> to toggle the minimum distance\n...press <F> to toggle the shape fill\n...press <I> to toggle the intersection points\n...press <R> to toggle the anchorage reference point");
		legendLabelRight.setStyle("-fx-font-size: 10pt");
		legendLabelRight.resizeRelocate(10, HEIGHT - PAD + 10, WIDTH - 20,
				PAD - 20);
		legendLabelRight.setAlignment(Pos.BOTTOM_RIGHT);
		root.getChildren().add(legendLabelRight);

		eLetterShape = createELetterShape();
		root.getChildren().add(eLetterShape);

		markerLayer = new Group();
		intersectionLayer = new Group();
		interactionLayer = new Group();
		root.getChildren().addAll(markerLayer, intersectionLayer,
				interactionLayer);

		// create chop box anchor and reference point property (so we can access
		// the reference points easily)
		chopBoxAnchor = new ChopBoxAnchor(eLetterShape);
		chopBoxAnchor.positionProperty().addListener(anchorPositionListener);

		// compute bounds center
		Point boundsCenterInLocal = JavaFX2Geometry
				.toRectangle(eLetterShape.getLayoutBounds()).getCenter();
		Point2D boundsCenterInScene = eLetterShape
				.localToScene(boundsCenterInLocal.x, boundsCenterInLocal.y);
		boundsCenterNode = createBoundsCenterNode(boundsCenterInScene);
		markerLayer.getChildren().add(boundsCenterNode);

		Point eLetterShapeReferencePoint = computationStrategy
				.computeAnchorageReferencePointInScene(eLetterShape,
						eLetterShape.getGeometry());

		// show outline vertices and distance to the bounds center
		for (BezierCurve seg : eLetterShape.getGeometry()
				.getOutlineSegments()) {
			// vertex
			Point vertexInLocal = seg.getP1();
			Point2D vertexInScene = eLetterShape.localToScene(vertexInLocal.x,
					vertexInLocal.y);
			boolean isMinDistance = unpreciseEquals(eLetterShapeReferencePoint,
					JavaFX2Geometry.toPoint(vertexInScene));

			Circle vertexNode = createVertexNode(vertexInScene);
			markerLayer.getChildren().add(vertexNode);
			// add to vertices list so we can disable/enable later
			vertices.add(vertexNode);

			// distance to bounds center
			final Line distanceLine = createDistanceLine(boundsCenterInScene,
					vertexInScene);
			markerLayer.getChildren().add(distanceLine);
			distanceLine.toBack();

			// show distance on mouse hover
			double distance = JavaFX2Geometry.toPoint(vertexInScene)
					.getDistance(JavaFX2Geometry.toPoint(boundsCenterInScene));
			final Text distanceText = createDistanceText(distance);
			Vector direction = new Vector(
					JavaFX2Geometry.toPoint(vertexInScene),
					JavaFX2Geometry.toPoint(boundsCenterInScene));
			Point labelPosition = isMinDistance
					? JavaFX2Geometry.toPoint(vertexInScene).getTranslated(
							direction.getMultiplied(0.5).toPoint())
					: JavaFX2Geometry.toPoint(boundsCenterInScene)
							.getTranslated(-15, 15);
			distanceText.relocate(labelPosition.x, labelPosition.y);
			distanceText.setVisible(false);
			markerLayer.getChildren().add(distanceText);

			// invisible selection line
			Line selectionLine = createSelectionLine(distanceLine);
			markerLayer.getChildren().add(selectionLine);

			// add to distance lines list so we can disable/enable later
			distanceLines.add(selectionLine);
			distanceLines.add(distanceLine);

			if (isMinDistance) {
				Line minDistanceLine = createDistanceLine(boundsCenterInScene,
						vertexInScene);
				distanceText.setVisible(true);
				minDistanceLine.setStroke(DISTANCE_LINE_STROKE_HOVER);
				minDistanceLine
						.setStrokeWidth(DISTANCE_LINE_STROKE_WIDTH_HOVER);
				markerLayer.getChildren().add(minDistanceLine);
				minDistanceNodes.add(minDistanceLine);
				minDistanceNodes.add(distanceText);
			} else {
				selectionLine.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						distanceLine.setStroke(DISTANCE_LINE_STROKE_HOVER);
						distanceLine.setStrokeWidth(
								DISTANCE_LINE_STROKE_WIDTH_HOVER);
						distanceText.setVisible(true);
					}
				});
				selectionLine.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						distanceLine.setStroke(DISTANCE_LINE_STROKE_NORMAL);
						distanceLine.setStrokeWidth(
								DISTANCE_LINE_STROKE_WIDTH_NORMAL);
						distanceText.setVisible(false);
					}
				});
			}
		}

		eLetterReferenceNode = createELetterReferenceNode();
		interactionLayer.getChildren().add(eLetterReferenceNode);

		// create draggable reference points around the shape
		// For the purpose of demonstrating intersection points, the following
		// second reference point can be used:
		// createReferencePoint(PAD / 2, HEIGHT / 2);
		createReferencePoint(WIDTH - PAD / 2, HEIGHT / 2);

		// initialize toggle states
		setVisible(vertices, INITIAL_VERTICES_VISIBLE);
		setVisible(distanceLines, INITIAL_LINES_VISIBLE);
		setVisible(minDistanceNodes, INITIAL_MIN_DISTANCE_VISIBLE);
		styleELetterShape(INITIAL_FILL_VISIBLE);
		boundsCenterNode.setVisible(INITIAL_CENTER_VISIBLE);
		eLetterReferenceNode.setVisible(INITIAL_ELETTER_REFERENCE_VISIBLE);
		intersectionLayer.setVisible(INITIAL_INTERSECTIONS_VISIBLE);

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			private boolean verticesVisible = INITIAL_VERTICES_VISIBLE;
			private boolean distanceLinesVisible = INITIAL_LINES_VISIBLE;
			private boolean fillVisible = INITIAL_FILL_VISIBLE;
			private boolean minDistanceVisible = INITIAL_MIN_DISTANCE_VISIBLE;

			@Override
			public void handle(KeyEvent event) {
				String pressedKey = event.getText().toLowerCase();
				if (pressedKey.equals("v")) {
					verticesVisible = !verticesVisible;
					setVisible(vertices, verticesVisible);
				} else if (pressedKey.equals("l")) {
					distanceLinesVisible = !distanceLinesVisible;
					setVisible(distanceLines, distanceLinesVisible);
				} else if (pressedKey.equals("f")) {
					fillVisible = !fillVisible;
					styleELetterShape(fillVisible);
				} else if (pressedKey.equals("i")) {
					intersectionLayer
							.setVisible(!intersectionLayer.isVisible());
				} else if (pressedKey.equals("d")) {
					minDistanceVisible = !minDistanceVisible;
					setVisible(minDistanceNodes, minDistanceVisible);
				} else if (pressedKey.equals("c")) {
					boundsCenterNode.setVisible(!boundsCenterNode.isVisible());
				} else if (pressedKey.equals("r")) {
					eLetterReferenceNode
							.setVisible(!eLetterReferenceNode.isVisible());
				}
			}

		});

		return scene;
	}

	private Line createSelectionLine(final Line distanceLine) {
		Line selectionLine = new Line(distanceLine.getStartX(),
				distanceLine.getStartY(), distanceLine.getEndX(),
				distanceLine.getEndY());
		selectionLine.setStrokeWidth(DISTANCE_LINE_SELECTION_STROKE_WIDTH);
		selectionLine.setStroke(Color.TRANSPARENT);
		return selectionLine;
	}

	private Circle createVertexNode(Point2D vertexInScene) {
		Circle vertexNode = new Circle(VERTEX_RADIUS);
		vertexNode.setFill(VERTEX_FILL);
		vertexNode.setStroke(VERTEX_STROKE);
		vertexNode.setCenterX(vertexInScene.getX());
		vertexNode.setCenterY(vertexInScene.getY());
		return vertexNode;
	}

	protected void onAnchorPositionChange(AnchorKey key, Point anchorPosition) {
		// update chop box point
		Circle chopBoxPoint = chopBoxPoints.get(key);
		chopBoxPoint.setCenterX(anchorPosition.x);
		chopBoxPoint.setCenterY(anchorPosition.y);
	}

	private void setVisible(List<Node> nodes, boolean isVisible) {
		for (Node n : nodes) {
			n.setVisible(isVisible);
		}
	}

	private void styleELetterShape(boolean fillVisible) {
		eLetterShape.setFill(
				fillVisible ? Color.rgb(135, 150, 220) : Color.TRANSPARENT);
		eLetterShape.setEffect(
				fillVisible ? GeometryNodeSnippet.createShadowEffect() : null);
		for (Line l : chopBoxLinesImaginary.values()) {
			if (fillVisible) {
				l.setStroke(CHOP_BOX_LINE_STROKE_IMAGINARY_WITH_FILL);
			} else {
				l.setStroke(CHOP_BOX_LINE_STROKE_IMAGINARY);
			}
		}
	}

	private boolean unpreciseEquals(Point p, Point q) {
		return PrecisionUtils.equal(q.x, p.x, -2)
				&& PrecisionUtils.equal(q.y, p.y, -2);
	}

	private void updateChopBoxLines(AnchorKey ak) {
		// update real line
		Line lineReal = chopBoxLinesReal.get(ak);
		Point referencePosition = referencePointProperty.get(ak);
		Point anchorPosition = chopBoxAnchor.getPosition(ak);
		lineReal.setStartX(referencePosition.x);
		lineReal.setStartY(referencePosition.y);
		lineReal.setEndX(anchorPosition.x);
		lineReal.setEndY(anchorPosition.y);

		// update imaginary line
		Point eLetterReferencePoint = computationStrategy
				.computeAnchorageReferencePointInScene(eLetterShape,
						eLetterShape.getGeometry());
		Line lineImaginary = chopBoxLinesImaginary.get(ak);
		lineImaginary.setStartX(anchorPosition.x);
		lineImaginary.setStartY(anchorPosition.y);
		lineImaginary.setEndX(eLetterReferencePoint.x);
		lineImaginary.setEndY(eLetterReferencePoint.y);

		// update intersection points
		if (intersections.containsKey(ak)) {
			List<Node> toRemove = intersections.remove(ak);
			intersectionLayer.getChildren().removeAll(toRemove);
		}
		List<Node> intersectionNodes = new ArrayList<Node>();
		ICurve eLetterOutline = (ICurve) NodeUtils.localToScene(eLetterShape,
				computationStrategy.getOutline(eLetterShape.getGeometry()));
		org.eclipse.gef4.geometry.planar.Line referenceLine = new org.eclipse.gef4.geometry.planar.Line(
				referencePosition, eLetterReferencePoint);
		Point[] intersectionPoints = eLetterOutline
				.getIntersections(referenceLine);
		for (Point p : intersectionPoints) {
			// TODO: precision problem!
			if (!unpreciseEquals(anchorPosition, p)
					&& !unpreciseEquals(eLetterReferencePoint, p)) {
				Node node = createIntersectionNode(p);
				intersectionNodes.add(node);
				intersectionLayer.getChildren().add(node);
			}
		}
		intersections.put(ak, intersectionNodes);
	}

}
