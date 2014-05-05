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

import java.util.ArrayList;
import java.util.List;

import javafx.embed.swt.SWTFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * An SWT composite that can be used to select a JavaFX color (and indicates the
 * selected color via an image).
 * 
 * @author anyssen
 *
 */
public class FXColorChooser extends Composite {

	private Color color;
	private Button colorButton;
	private Label imageLabel;

	private List<SelectionListener> listeners = new ArrayList<SelectionListener>();

	public FXColorChooser(final Composite parent) {
		super(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.horizontalSpacing = 1;
		setLayout(layout);
		imageLabel = new Label(this, SWT.LEFT);
		colorButton = new Button(this, SWT.ARROW);

		imageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,
				false, false));
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
					// notify listeners that value has changed
					for (SelectionListener l : listeners) {
						// TODO: this is dirty handle this differently
						l.widgetSelected(e);
					}
				}
			}
		});
		setColor(Color.WHITE);
	}

	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void dispose() {
		listeners.clear();
		super.dispose();
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
		this.color = color;
		updateImageLabel();
	}

	public Color getColor() {
		return color;
	}
}