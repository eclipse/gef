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

import javafx.embed.swt.SWTFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * An SWT composite that can be used to select a JavaFX color (and indicates the
 * selected color via an image).
 * 
 * @author anyssen
 *
 */
public class FXColorPicker implements IPropertyChangeSupport {

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Color color;
	private Button colorButton;
	private Label imageLabel;

	private Control control;

	public FXColorPicker(final Composite parent) {
		control = createControl(parent);
		setColor(Color.WHITE);
	}

	public Control getControl() {
		return control;
	}

	protected Control createControl(final Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.horizontalSpacing = 1;
		composite.setLayout(layout);
		imageLabel = new Label(composite, SWT.LEFT);
		imageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,
				false, false));
		colorButton = new Button(composite, SWT.ARROW);
		colorButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		colorButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cd = new ColorDialog(parent.getShell());
				RGB rgb = new RGB((int) (255 * color.getRed()),
						(int) (255 * color.getGreen()), (int) (255 * color
								.getBlue()));
				cd.setRGB(rgb);
				RGB newRgb = cd.open();
				if (newRgb != null) {
					setColor(Color.rgb(newRgb.red, newRgb.green, newRgb.blue));
				}
			}
		});
		return composite;
	}

	protected void updateImageLabel() {
		if (color != null) {
			int buttonHeight = colorButton
					.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			ImageData imageData = createPaintImage(64, buttonHeight + 6, color);
			Image image = new Image(imageLabel.getDisplay(), imageData,
					imageData.getTransparencyMask());
			imageLabel.setImage(image);
		}
	}

	// create a rectangular image to visualize the given paint value
	protected static ImageData createPaintImage(int width, int height,
			Paint paint) {
		// use JavaFX canvas to render a rectangle with the given paint
		Canvas canvas = new Canvas(width, height);
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		graphicsContext.setFill(paint);
		graphicsContext.fillRect(0, 0, width, height);
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.strokeRect(0, 0, width, height);
		// handle transparent color separately (we want to differentiate it from
		// transparent fill)
		if (paint instanceof Color && ((Color) paint).getOpacity() == 0) {
			// draw a red line from bottom-left to top-right to indicate a
			// transparent fill color
			graphicsContext.setStroke(Color.RED);
			graphicsContext.strokeLine(0, height - 1, width, 1);
		}
		WritableImage snapshot = canvas
				.snapshot(new SnapshotParameters(), null);
		return SWTFXUtils.fromFXImage(snapshot, null);
	}

	public void setColor(Color color) {
		Color oldColor = this.color;
		this.color = color;
		updateImageLabel();
		pcs.firePropertyChange("color", oldColor, color);
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