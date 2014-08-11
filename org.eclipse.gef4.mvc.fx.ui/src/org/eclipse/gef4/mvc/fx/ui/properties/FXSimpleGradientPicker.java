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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.gef4.fx.ui.controls.FXControlAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author anyssen
 *
 */
public class FXSimpleGradientPicker implements IPropertyChangeNotifier {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private LinearGradient simpleGradient;
	private AbstractFXColorPicker color1Picker;
	private FXColorPicker color2Picker;

	private Control control;

	public FXSimpleGradientPicker(Composite parent) {
		control = createControl(parent);
		setSimpleGradient(createSimpleGradient(Color.WHITE, Color.BLACK));
	}

	public Control getControl() {
		return control;
	}

	protected Control createControl(final Composite parent) {
		// create an SwtFXCanvas that contains the two color pickers as well as
		// JavaFX controls
		final FXCanvas canvas = new FXCanvas(parent, SWT.NONE);
		HBox root = new HBox();
		VBox colorEditorsBox = new VBox();
		root.getChildren().add(colorEditorsBox);

		color1Picker = new AbstractFXColorPicker() {

			@Override
			public Color pickColor() {
				return FXColorPicker.pickColor(parent.getShell(), getColor());
			}

		};
		colorEditorsBox.getChildren().add(color1Picker);

		// color1Editor.getControl().setLayoutData(new GridData());
		color1Picker.colorProperty().addListener(new ChangeListener<Color>() {

			@Override
			public void changed(ObservableValue<? extends Color> observable,
					Color oldValue, Color newValue) {
				setSimpleGradient(createSimpleGradient(color1Picker.getColor(),
						color2Picker.getColor()));
			}

		});

		color2Picker = new FXColorPicker(canvas);
		FXControlAdapter<Control> color2EditorNode = new FXControlAdapter<Control>(
				color2Picker.getControl());
		colorEditorsBox.getChildren().add(color2EditorNode);
		// color2Editor.getControl().setLayoutData(new GridData());
		color2Picker.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSimpleGradient(createSimpleGradient(color1Picker.getColor(),
						color2Picker.getColor()));
			}
		});

		Scene scene = new Scene(root);
		canvas.setScene(scene);
		return canvas;
	}

	public void setSimpleGradient(LinearGradient simpleGradient) {
		if (!isSimpleGradient(simpleGradient)) {
			throw new IllegalArgumentException("Given value '" + simpleGradient
					+ "' is no simple gradient");
		}
		;

		LinearGradient oldSimpleGradient = this.simpleGradient;
		this.simpleGradient = simpleGradient;
		List<Stop> stops = simpleGradient.getStops();
		if (stops.size() != 2) {
			throw new IllegalArgumentException(
					"A simple gradient may only contain two stops.");
		}
		if (!color1Picker.getColor().equals(stops.get(0).getColor())) {
			color1Picker.setColor(stops.get(0).getColor());
		}
		if (!color2Picker.getColor().equals(stops.get(1).getColor())) {
			color2Picker.setColor(stops.get(1).getColor());
		}
		pcs.firePropertyChange("simpleGradient", oldSimpleGradient,
				simpleGradient);
	}

	public LinearGradient getSimpleGradient() {
		return simpleGradient;
	}

	protected static LinearGradient createSimpleGradient(Color c1, Color c2) {
		// TODO: add angle
		Stop[] stops = new Stop[] { new Stop(0, c1), new Stop(1, c2) };
		return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public static boolean isSimpleGradient(Paint paint) {
		if (paint instanceof LinearGradient) {
			return ((LinearGradient) paint).getStops().size() == 2;
		}
		return false;
	}

}
