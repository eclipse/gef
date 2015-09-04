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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

/**
 * An SWT control that can be used to select a JavaFX color (and indicates the
 * selected color via an image).
 *
 * @author anyssen
 *
 */
public class FXColorPicker implements IPropertyChangeNotifier {

	/**
	 * Opens a {@link ColorDialog} to let the user pick a {@link Color}. Returns
	 * the picked {@link Color}, or <code>null</code> if no color was picked.
	 *
	 * @param shell
	 *            The {@link Shell} which serves as the parent for the
	 *            {@link ColorDialog}.
	 * @param initial
	 *            The initial {@link Color} to display in the
	 *            {@link ColorDialog}.
	 * @return The picked {@link Color}, or <code>null</code>.
	 */
	protected static Color pickColor(Shell shell, Color initial) {
		ColorDialog cd = new ColorDialog(shell);
		RGB rgb = new RGB((int) (255 * initial.getRed()),
				(int) (255 * initial.getGreen()),
				(int) (255 * initial.getBlue()));
		cd.setRGB(rgb);
		RGB newRgb = cd.open();
		if (newRgb != null) {
			return Color.rgb(newRgb.red, newRgb.green, newRgb.blue);
		}
		return null;
	}

	/**
	 * Supporter for property change notifications/listener registration.
	 */
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Color color;
	private Control control;

	private AbstractFXColorPicker colorPicker;

	/**
	 * Constructs a new {@link FXColorPicker}.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 */
	public FXColorPicker(final Composite parent) {
		control = createControl(parent);
		setColor(Color.WHITE);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Creates the visualization for this {@link FXColorPicker}.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @return The {@link Control} that visualizes this {@link FXColorPicker}.
	 */
	protected Control createControl(final Composite parent) {
		FXCanvas canvas = new FXCanvas(parent, SWT.NONE);
		colorPicker = new AbstractFXColorPicker() {

			@Override
			public Color pickColor() {
				return FXColorPicker.pickColor(parent.getShell(), getColor());
			}
		};
		colorPicker.colorProperty().addListener(new ChangeListener<Color>() {

			@Override
			public void changed(ObservableValue<? extends Color> observable,
					Color oldValue, Color newValue) {
				setColor(newValue);
			}
		});
		Scene scene = new Scene(colorPicker);
		canvas.setScene(scene);
		return canvas;
	}

	/**
	 * Returns the currently selected {@link Color}.
	 *
	 * @return The currently selected {@link Color}.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the {@link Control} that visualizes this {@link FXColorPicker}.
	 *
	 * @return The {@link Control} that visualizes this {@link FXColorPicker}.
	 */
	public Control getControl() {
		return control;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Changes the currently selected {@link Color} to the given value.
	 *
	 * @param color
	 *            The newly selected {@link Color}.
	 */
	public void setColor(Color color) {
		if (this.color == null ? color != null : !this.color.equals(color)) {
			Color oldColor = this.color;
			this.color = color;
			colorPicker.setColor(color);
			pcs.firePropertyChange("color", oldColor, color);
		}
	}
}