/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #469491
 *
 *******************************************************************************/
package org.eclipse.gef.fx.swt.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * A picker for multi-stop {@link LinearGradient}s.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXAdvancedLinearGradientPicker extends Composite {

	private class StopPicker extends Group {

		private static final double SIZE = 8;

		private int index = 0;
		private DoubleProperty offsetProperty = new SimpleDoubleProperty();
		private ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(
				Color.WHITE);
		private Polygon tip;
		private Rectangle picker;
		private double initialMouseX;
		private double initialTx;
		private boolean draggable;

		private EventHandler<? super MouseEvent> onDrag = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (draggable) {
					double dx = event.getSceneX() - initialMouseX;
					double newOffset = (initialTx + dx) / preview.getWidth();
					newOffset = Math.max(getPrevOffset(index),
							Math.min(getNextOffset(index), newOffset));
					offsetProperty.set(newOffset);
					updateStop(index, offsetProperty.get(),
							colorProperty.get());
				}
			}
		};

		{
			tip = new Polygon(0, 0, SIZE / 2, SIZE / 2, -SIZE / 2, SIZE / 2);
			tip.setStroke(Color.BLACK);
			tip.setFill(Color.BLACK);
			picker = new Rectangle(-SIZE / 2, SIZE / 2, SIZE, SIZE);
			picker.setStroke(Color.BLACK);
			picker.fillProperty().bind(colorProperty);
			getChildren().addAll(tip, picker);
		}

		public StopPicker(int index) {
			this.index = index;

			// bind translation to offset
			translateXProperty()
					.bind(preview.widthProperty().multiply(offsetProperty));

			// mouse feedback
			setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (draggable) {
						BoxBlur boxBlur = new BoxBlur(0, 0, 1);
						setEffect(boxBlur);
						Timeline timeline = new Timeline(
								new KeyFrame(Duration.millis(0),
										new KeyValue(boxBlur.widthProperty(),
												0),
								new KeyValue(boxBlur.heightProperty(), 0)),
								new KeyFrame(Duration.millis(150),
										new KeyValue(boxBlur.widthProperty(),
												3),
										new KeyValue(boxBlur.heightProperty(),
												3)));
						timeline.play();
					}
				}
			});
			setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (!isPressed()) {
						setEffect(null);
					}
				}
			});

			// make draggable
			setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					initialMouseX = event.getSceneX();
					initialTx = getTranslateX();
				}
			});
			setOnMouseDragged(onDrag);
			setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					onDrag.handle(event);
					if (!isHover()) {
						setEffect(null);
					}
				}
			});

			// copy values from Stop
			refresh();

			// pick color on double click
			picker.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() > 1) {
						// double click
						colorProperty.set(FXColorPicker.pickColor(getShell(),
								colorProperty.get()));
						updateStop(StopPicker.this.index, offsetProperty.get(),
								colorProperty.get());
					} else if (draggable && MouseButton.SECONDARY
							.equals(event.getButton())) {
						removeStop(StopPicker.this.index);
					}
				}
			});
		}

		/**
		 * Refreshes this stop picker by copying offset and color from the stops
		 * list.
		 */
		public void refresh() {
			// copy offset and color from stop
			offsetProperty.set(getStops().get(index).getOffset());
			colorProperty.set(getStops().get(index).getColor());

			// determine if draggable (all but start and end)
			draggable = offsetProperty.get() != 0 && offsetProperty.get() != 1;
		}

	}

	/**
	 * Property name used in change events related to
	 * {@link #advancedLinearGradientProperty()}
	 */
	public static final String ADVANCED_LINEAR_GRADIENT_PROPERTY = "advancedLinearGradient";

	private static final int DIRECTION_RADIUS = 16;

	private static final double OFFSET_THRESHOLD = 0.005;

	/**
	 * Creates an "advanced" linear color gradient with 3 stops from the given
	 * colors.
	 *
	 * @param c1
	 *            The start color.
	 * @param c2
	 *            The middle color (t = 0.5).
	 * @param c3
	 *            The end color.
	 * @return An "advanced" {@link LinearGradient} from the given colors.
	 */
	public static LinearGradient createAdvancedLinearGradient(Color c1,
			Color c2, Color c3) {
		Stop[] stops = new Stop[] { new Stop(0, c1), new Stop(0.5, c2),
				new Stop(1, c3) };
		return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
				stops);
	}

	/**
	 * Returns <code>true</code> if the given {@link Paint} is considered to be
	 * an "advanced" gradient. Otherwise returns <code>false</code>. An advanced
	 * gradient can either be a linear gradient with at least 3 stops, or any
	 * radial gradient.
	 *
	 * @param paint
	 *            The {@link Paint} in question.
	 * @return <code>true</code> if the given {@link Paint} is considered to be
	 *         an "advanced" gradient, othwerise <code>false</code>.
	 */
	public static boolean isAdvancedLinearGradient(Paint paint) {
		if (paint instanceof LinearGradient) {
			return ((LinearGradient) paint).getStops().size() > 2;
		} else if (paint instanceof RadialGradient) {
			return true;
		}
		return false;
	}

	private Property<LinearGradient> advancedLinearGradient = new SimpleObjectProperty<>(
			this, ADVANCED_LINEAR_GRADIENT_PROPERTY);
	private double directionX = 1;
	private double directionY = 0;
	private AnchorPane root;
	private Rectangle preview;
	private Group pickerGroup;
	private Line directionLine;

	/**
	 * Constructs a new {@link FXAdvancedLinearGradientPicker}.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @param color1
	 *            The first color of the initial three-stop
	 *            {@link LinearGradient}.
	 * @param color2
	 *            The second color of the initial three-stop
	 *            {@link LinearGradient}.
	 * @param color3
	 *            The third color of the initial three-stop
	 *            {@link LinearGradient}.
	 */
	public FXAdvancedLinearGradientPicker(Composite parent, Color color1,
			Color color2, Color color3) {
		super(parent, SWT.NONE);

		setLayout(new FillLayout());

		// create a canvas to render the JavaFX controls
		FXCanvas canvas = new FXCanvas(this, SWT.NONE);

		// create preview pane and direction circle
		root = new AnchorPane();
		root.setStyle("-fx-background-color: transparent;");
		final Pane previewPane = new Pane();
		final Circle directionCircle = new Circle(DIRECTION_RADIUS,
				Color.WHITE);
		directionLine = new Line();
		directionLine.setMouseTransparent(true);
		directionLine.setEndX(DIRECTION_RADIUS);
		directionLine.setEndY(0);
		directionLine.startXProperty().bind(directionCircle.centerXProperty());
		directionLine.startYProperty().bind(directionCircle.centerYProperty());
		directionLine.translateXProperty()
				.bind(directionCircle.layoutXProperty());
		directionLine.translateYProperty()
				.bind(directionCircle.layoutYProperty());
		root.getChildren().addAll(previewPane, directionCircle, directionLine);
		// layout preview pane
		AnchorPane.setTopAnchor(previewPane, 2d);
		AnchorPane.setBottomAnchor(previewPane, 20d);
		AnchorPane.setLeftAnchor(previewPane, 15d);
		AnchorPane.setRightAnchor(previewPane, 40d);
		// layout direction circle
		AnchorPane.setTopAnchor(directionCircle, 5d);
		AnchorPane.setRightAnchor(directionCircle, 0d);

		// create a preview rectangle that displays the gradient
		preview = new Rectangle();
		preview.setStroke(Color.DARKGRAY);
		pickerGroup = new Group();
		root.getChildren().addAll(preview, pickerGroup);
		preview.xProperty().bind(previewPane.layoutXProperty());
		preview.yProperty().bind(previewPane.layoutYProperty());
		preview.widthProperty().bind(previewPane.widthProperty());
		preview.heightProperty().bind(previewPane.heightProperty());
		preview.setFill(advancedLinearGradient.getValue());

		// create highlight line for showing where new spots are created
		final Rectangle highlightSpotCreation = new Rectangle();
		highlightSpotCreation.setStroke(Color.TRANSPARENT);
		highlightSpotCreation.setFill(new Color(1, 1, 0, 0.5));
		highlightSpotCreation.heightProperty()
				.bind(preview.heightProperty().add(10));
		highlightSpotCreation.yProperty().bind(preview.yProperty());
		highlightSpotCreation.setWidth(3);
		highlightSpotCreation.setTranslateX(-1.5);
		highlightSpotCreation.setVisible(false);
		highlightSpotCreation.setMouseTransparent(true);
		root.getChildren().add(highlightSpotCreation);

		// update highlighting when the mouse is moved
		preview.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				highlightSpotCreation.setVisible(true);
			}
		});
		preview.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				highlightSpotCreation.setX(event.getX());
			}
		});
		preview.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				highlightSpotCreation.setVisible(false);
			}
		});

		// create a new stop with primary mouse button
		preview.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (MouseButton.PRIMARY.equals(event.getButton())) {
					// create new stop
					Point2D previewPosition = previewPane
							.sceneToLocal(event.getSceneX(), event.getSceneY());
					double offset = previewPosition.getX() / preview.getWidth();
					offset = Math.max(0, Math.min(1, offset));
					createStop(offset);
				}
			}
		});

		// change direction when clicking into the direction circle
		directionCircle.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (directionX == 1) {
					directionX = 0;
					directionY = 1;
				} else {
					directionX = 1;
					directionY = 0;
				}
				updateDirectionLine();
				List<Stop> newStops = new ArrayList<>(getStops());
				updateGradient(newStops);
			}
		});

		Scene scene = new Scene(root);
		// copy background color from parent composite
		org.eclipse.swt.graphics.Color backgroundColor = parent.getBackground();
		scene.setFill(Color.rgb(backgroundColor.getRed(),
				backgroundColor.getGreen(), backgroundColor.getBlue()));
		canvas.setScene(scene);

		// create an initial linear gradient with three stops
		setAdvancedGradient(
				createAdvancedLinearGradient(color1, color2, color3));
	}

	/**
	 * Returns a writable {@link Property} for the advanced gradient.
	 *
	 * @return A writable {@link Property}.
	 */
	public Property<LinearGradient> advancedLinearGradientProperty() {
		return advancedLinearGradient;
	}

	/**
	 * Creates a new spot for the given offset.
	 *
	 * @param offset
	 *            The offset for the new spot.
	 */
	protected void createStop(double offset) {
		List<Stop> newStops = new ArrayList<>(getStops());
		int addIndex = newStops.size();
		for (int i = 0; i < newStops.size(); i++) {
			if (newStops.get(i).getOffset() > offset) {
				addIndex = i;
				break;
			}
		}
		newStops.add(addIndex, new Stop(offset, Color.WHITE));
		updateGradient(newStops);
	}

	/**
	 * Returns the currently selected advanced gradient.
	 *
	 * @return The currently selected advanced gradient.
	 */
	public LinearGradient getAdvancedLinearGradient() {
		return advancedLinearGradient.getValue();
	}

	/**
	 * Computes the maximum offset for the given stop index.
	 *
	 * @param stopIndex
	 *            The index of the stop for which to compute the next offset.
	 * @return The maximum offset for the given stop index.
	 */
	protected double getNextOffset(int stopIndex) {
		if (stopIndex == getStops().size() - 1) {
			return 1 - OFFSET_THRESHOLD;
		}
		return getStops().get(stopIndex + 1).getOffset() - OFFSET_THRESHOLD;
	}

	/**
	 * Computes the minimum offset for the given stop index.
	 *
	 * @param stopIndex
	 *            The index of the stop for which to compute the previous
	 *            offset.
	 * @return The minimum offset for the given stop index.
	 */
	protected double getPrevOffset(int stopIndex) {
		if (stopIndex == 0) {
			return 0 + OFFSET_THRESHOLD;
		}
		return getStops().get(stopIndex - 1).getOffset() + OFFSET_THRESHOLD;
	}

	/**
	 * Returns a list of the {@link Stop}s of the currently selected advanced
	 * gradient.
	 *
	 * @return A list of the {@link Stop}s of the currently selected advanced
	 *         gradient.
	 */
	protected List<Stop> getStops() {
		return advancedLinearGradient.getValue().getStops();
	}

	/**
	 * Removes the spot specified by the given index.
	 *
	 * @param index
	 *            The spot index.
	 */
	protected void removeStop(int index) {
		List<Stop> newStops = new ArrayList<>(getStops());
		newStops.remove(index);
		updateGradient(newStops);
	}

	/**
	 * Sets the gradient managed by this gradient picker to the given value.
	 * Does also update the UI so that the new gradient can be manipulated.
	 *
	 * @param advancedLinearGradient
	 *            The new gradient.
	 */
	public void setAdvancedGradient(LinearGradient advancedLinearGradient) {
		if (!isAdvancedLinearGradient(advancedLinearGradient)) {
			throw new IllegalArgumentException(
					"Given value '" + advancedLinearGradient
							+ "' is no advanced linear gradient");
		}

		this.advancedLinearGradient.setValue(advancedLinearGradient);
		preview.setFill(advancedLinearGradient);

		// adapt direction
		directionX = advancedLinearGradient.getEndX();
		if (directionX == 1) {
			directionY = 0;
		} else {
			directionX = 0;
			directionY = 1;
		}
		updateDirectionLine();
		// adapt stops
		List<Stop> stops = getStops();
		for (int i = 0; i < stops.size(); i++) {
			if (pickerGroup.getChildren().size() > i) {
				// refresh existing stop pickers
				((StopPicker) pickerGroup.getChildren().get(i)).refresh();
			} else {
				// add new stop pickers
				StopPicker stopPicker = new StopPicker(i);
				pickerGroup.getChildren().add(stopPicker);
				stopPicker.layoutXProperty().bind(preview.xProperty());
				stopPicker.layoutYProperty().bind(
						preview.yProperty().add(preview.heightProperty()));
			}
		}
		// remove unused stop pickers
		for (int i = pickerGroup.getChildren().size() - 1; i >= stops
				.size(); i--) {
			pickerGroup.getChildren().remove(i);
		}
	}

	/**
	 * Updates the direction line to display the current direction (specified by
	 * directionX and directionY).
	 */
	protected void updateDirectionLine() {
		directionLine.setEndX(directionX * DIRECTION_RADIUS);
		directionLine.setEndY(directionY * DIRECTION_RADIUS);
	}

	/**
	 * Changes the currently selected advanced gradient to a new linear gradient
	 * that is constructed from the given list of {@link Stop}s.
	 *
	 * @param newStops
	 *            The list of {@link Stop}s from which the newly selected
	 *            advanced gradient is constructed.
	 */
	protected void updateGradient(List<Stop> newStops) {
		setAdvancedGradient(new LinearGradient(0, 0, directionX, directionY,
				true, CycleMethod.NO_CYCLE, newStops));
	}

	/**
	 * Sets the offset and color of the spot specified by the given index to the
	 * given values.
	 *
	 * @param index
	 *            The index of the spot.
	 * @param offset
	 *            The new offset for that spot.
	 * @param color
	 *            The new color for that spot.
	 */
	protected void updateStop(int index, double offset, Color color) {
		List<Stop> newStops = new ArrayList<>(getStops());
		newStops.set(index, new Stop(offset, color));
		updateGradient(newStops);
	}

}
