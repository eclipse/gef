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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.scene.paint.Color;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * An SWT control that can be used to select a JavaFX color (and indicates the
 * selected color via an image).
 * 
 * @author anyssen
 *
 */
public class FXColorPicker implements IPropertyChangeNotifier {

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Color color;
	private Control control;
	private AbstractFXColorPicker colorPicker;

	public FXColorPicker(final Composite parent) {
		control = createControl(parent);
		setColor(Color.WHITE);
	}

	public Control getControl() {
		return control;
	}

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
		SwtFXScene scene = new SwtFXScene(colorPicker);
		canvas.setScene(scene);
		return canvas;
	}

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

	public void setColor(Color color) {
		if (this.color == null ? color != null : !this.color.equals(color)) {
			Color oldColor = this.color;
			this.color = color;
			colorPicker.setColor(color);
			pcs.firePropertyChange("color", oldColor, color);
		}
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}