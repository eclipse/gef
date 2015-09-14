/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.fx.ui.controls.FXControlAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * The {@link FXSimpleLinearGradientPicker} allows the selection of two colors
 * from which a gradient is constructed.
 *
 * @author anyssen
 *
 */
public class FXSimpleLinearGradientPicker extends Composite
		implements IPropertyChangeNotifier {

	/**
	 * Creates a simple color gradient from the given start color to the given
	 * end color.
	 *
	 * @param c1
	 *            The start {@link Color}.
	 * @param c2
	 *            The end {@link Color}.
	 * @return The resulting {@link LinearGradient}.
	 */
	public static LinearGradient createSimpleLinearGradient(Color c1,
			Color c2) {
		// TODO: add angle
		Stop[] stops = new Stop[] { new Stop(0, c1), new Stop(1, c2) };
		return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
				stops);
	}

	/**
	 * Returns <code>true</code> if the given {@link Paint} is a "simple"
	 * gradient, i.e. it has exactly 2 stops. Otherwise returns
	 * <code>false</code>.
	 *
	 * @param paint
	 *            The {@link Paint} in question.
	 * @return <code>true</code> if the given {@link Paint} is a simple
	 *         gradient, otherwise <code>false</code>.
	 */
	public static boolean isSimpleLinearGradient(Paint paint) {
		if (paint instanceof LinearGradient) {
			return ((LinearGradient) paint).getStops().size() == 2;
		}
		return false;
	}

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private LinearGradient simpleLinearGradient;

	private FXColorPicker color1Picker;
	private FXColorPicker color2Picker;

	/**
	 * Constructs a new {@link FXSimpleLinearGradientPicker}.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @param color1
	 *            The first color of the initial simple {@link LinearGradient}.
	 * @param color2
	 *            The second color of the initial simple {@link LinearGradient}.
	 */
	public FXSimpleLinearGradientPicker(Composite parent, Color color1,
			Color color2) {
		super(parent, SWT.NONE);

		setLayout(new FillLayout());

		final FXCanvas canvas = new FXCanvas(this, SWT.NONE);
		HBox root = new HBox();
		root.setStyle("-fx-background-color: transparent;");
		VBox colorEditorsBox = new VBox();
		colorEditorsBox.setSpacing(5);
		root.getChildren().add(colorEditorsBox);

		color1Picker = new FXColorPicker(canvas, color1);
		colorEditorsBox.getChildren()
				.add(new FXControlAdapter<Control>(color1Picker));
		color1Picker.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSimpleGradient(createSimpleLinearGradient(
						color1Picker.getColor(), color2Picker.getColor()));
			}
		});

		color2Picker = new FXColorPicker(canvas, color2);
		colorEditorsBox.getChildren()
				.add(new FXControlAdapter<Control>(color2Picker));
		color2Picker.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSimpleGradient(createSimpleLinearGradient(
						color1Picker.getColor(), color2Picker.getColor()));
			}
		});

		Scene scene = new Scene(root);
		// copy background color from parent composite
		org.eclipse.swt.graphics.Color backgroundColor = parent.getBackground();
		scene.setFill(Color.rgb(backgroundColor.getRed(),
				backgroundColor.getGreen(), backgroundColor.getBlue()));
		canvas.setScene(scene);

		// initialize simple gradient
		setSimpleGradient(createSimpleLinearGradient(color1, color2));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Returns the currently selected simple gradient.
	 *
	 * @return The currently selected simple gradient.
	 */
	public LinearGradient getSimpleLinearGradient() {
		return simpleLinearGradient;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Changes the currently selected gradient to the given value.
	 *
	 * @param simpleLinearGradient
	 *            The new simple {@link LinearGradient} to select.
	 */
	public void setSimpleGradient(LinearGradient simpleLinearGradient) {
		if (!isSimpleLinearGradient(simpleLinearGradient)) {
			throw new IllegalArgumentException("Given value '"
					+ simpleLinearGradient + "' is no simple linear gradient");
		}

		LinearGradient oldSimpleGradient = this.simpleLinearGradient;
		this.simpleLinearGradient = simpleLinearGradient;
		// a simple linear gradient contains two stops
		List<Stop> stops = simpleLinearGradient.getStops();
		if (!color1Picker.getColor().equals(stops.get(0).getColor())) {
			color1Picker.setColor(stops.get(0).getColor());
		}
		if (!color2Picker.getColor().equals(stops.get(1).getColor())) {
			color2Picker.setColor(stops.get(1).getColor());
		}
		pcs.firePropertyChange("simpleLinearGradient", oldSimpleGradient,
				simpleLinearGradient);
	}

}
