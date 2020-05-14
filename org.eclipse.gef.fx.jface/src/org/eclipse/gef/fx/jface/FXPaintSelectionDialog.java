/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.jface;

import java.util.List;

import org.eclipse.gef.fx.swt.controls.FXAdvancedLinearGradientPicker;
import org.eclipse.gef.fx.swt.controls.FXColorPicker;
import org.eclipse.gef.fx.swt.controls.FXSimpleLinearGradientPicker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 * The {@link FXPaintSelectionDialog} is a {@link Dialog} that allows to select
 * a JavaFX {@link Paint}. It provides a simple color picker, a simple gradient
 * picker, and an advanced gradient picker.
 *
 * @author anyssen
 *
 */
public class FXPaintSelectionDialog extends Dialog {

	private Paint paint;

	private String title;
	// store the last selection when switching options
	private Combo optionsCombo;

	private Label imageLabel;
	private Paint lastFillColor = Color.WHITE;

	private FXColorPicker colorPicker;
	private Paint lastSimpleGradient = FXSimpleLinearGradientPicker.createSimpleLinearGradient(Color.WHITE,
			Color.BLACK);

	private FXSimpleLinearGradientPicker simpleGradientPicker;
	private Paint lastAdvancedGradient = FXAdvancedLinearGradientPicker.createAdvancedLinearGradient(Color.WHITE,
			Color.GREY, Color.BLACK);

	// TODO: add support for image pattern

	private FXAdvancedLinearGradientPicker advancedGradientPicker;

	/**
	 * Constructs a new {@link FXPaintSelectionDialog}.
	 *
	 * @param parent
	 *            The parent {@link Shell}.
	 * @param title
	 *            The title for this dialog.
	 */
	public FXPaintSelectionDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/**
	 * Creates a {@link Composite} that contains the advanced gradient picker.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @return The {@link Composite} that contains the advanced gradient picker.
	 */
	protected Control createAdvancedGradientFillControl(Composite parent) {
		advancedGradientPicker = new FXAdvancedLinearGradientPicker(parent, Color.WHITE, Color.GREY, Color.BLACK);
		advancedGradientPicker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		advancedGradientPicker.advancedLinearGradientProperty().addListener(new ChangeListener<LinearGradient>() {

			@Override
			public void changed(ObservableValue<? extends LinearGradient> observable, LinearGradient oldValue,
					LinearGradient newValue) {
				setPaint(newValue);
			}
		});
		return advancedGradientPicker;
	}

	/**
	 * Creates a {@link Composite} that contains the simple color picker.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @return The {@link Composite} that contains the simple color picker.
	 */
	protected Control createColorFillControl(Composite parent) {
		colorPicker = new FXColorPicker(parent, Color.WHITE);
		colorPicker.colorProperty().addListener(new ChangeListener<Color>() {

			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				setPaint(newValue);
			}
		});
		return colorPicker;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setBackground(parent.getBackground());
		container.setFont(parent.getFont());
		GridLayout gl = new GridLayout(1, true);
		gl.marginHeight = 0;
		gl.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		gl.marginTop = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		container.setLayout(gl);

		Composite labelContainer = new Composite(container, SWT.NONE);
		labelContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		gl = new GridLayout(2, true);
		gl.marginWidth = 3; // align with combo below
		labelContainer.setLayout(gl);
		Label fillLabel = new Label(labelContainer, SWT.LEFT);
		fillLabel.setBackground(parent.getBackground());
		fillLabel.setFont(parent.getFont());
		fillLabel.setLayoutData(new GridData());
		fillLabel.setText("Fill:");
		imageLabel = new Label(labelContainer, SWT.RIGHT);
		imageLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));

		Composite optionsContainer = new Composite(container, SWT.NONE);
		optionsContainer.setBackground(parent.getBackground());
		optionsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		gl = new GridLayout(1, true);
		gl.marginWidth = 3; // align with combo above
		optionsContainer.setLayout(gl);

		optionsCombo = new Combo(optionsContainer, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		optionsCombo.setItems(new String[] { "No Fill", "Color Fill", "Gradient Fill",
				"Advanced Gradient Fill"/*
										 * , "Image Fill"
										 */ });
		optionsCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		final Composite optionsComposite = new Composite(optionsContainer, SWT.NONE);
		optionsComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		optionsComposite.setBackground(parent.getBackground());
		final StackLayout sl = new StackLayout();

		optionsComposite.setLayout(sl);

		// no fill
		final Control noFillControl = new Composite(optionsComposite, SWT.NONE);
		final Control colorFillControl = createColorFillControl(optionsComposite);
		final Control simpleGradientFillControl = createSimpleGradientFillControl(optionsComposite);
		final Control advancedGradientFillControl = createAdvancedGradientFillControl(optionsComposite);
		// TODO: others

		optionsCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// store previous option value
				if (paint != null) {
					if (paint instanceof Color && !Color.TRANSPARENT.equals(paint)) {
						lastFillColor = paint;
					} else if (FXSimpleLinearGradientPicker.isSimpleLinearGradient(paint)) {
						lastSimpleGradient = paint;
					} else if (FXAdvancedLinearGradientPicker.isAdvancedLinearGradient(paint)) {
						lastAdvancedGradient = paint;
					}
				}
				// set new option value
				switch (optionsCombo.getSelectionIndex()) {
				case 0:
					sl.topControl = noFillControl;
					paint = Color.TRANSPARENT;
					break;
				case 1:
					sl.topControl = colorFillControl;
					setPaint(lastFillColor); // restore last fill color
					colorPicker.setColor((Color) paint);
					break;
				case 2:
					sl.topControl = simpleGradientFillControl;
					setPaint(lastSimpleGradient);
					simpleGradientPicker.setSimpleLinearGradient((LinearGradient) paint);
					break;
				case 3:
					sl.topControl = advancedGradientFillControl;
					setPaint(lastAdvancedGradient);
					advancedGradientPicker.setAdvancedGradient((LinearGradient) paint);
					break;
				default:
					throw new IllegalArgumentException("Unsupported option");
				}
				updateImageLabel();
				optionsComposite.layout();
			}

		});

		if (Color.TRANSPARENT.equals(paint)) {
			optionsCombo.select(0);
		} else if (paint instanceof Color) {
			optionsCombo.select(1);
		} else if (FXSimpleLinearGradientPicker.isSimpleLinearGradient(paint)) {
			optionsCombo.select(2);
		} else if (FXAdvancedLinearGradientPicker.isAdvancedLinearGradient(paint)) {
			optionsCombo.select(3);
		} else if (paint instanceof ImagePattern) {
			optionsCombo.select(4);
		}
		return container;
	}

	/**
	 * Creates a {@link Composite} that contains the simple gradient picker.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @return The {@link Composite} that contains the simple gradient picker.
	 */
	protected Control createSimpleGradientFillControl(Composite parent) {
		simpleGradientPicker = new FXSimpleLinearGradientPicker(parent, Color.WHITE, Color.BLACK);
		simpleGradientPicker.simpleLinearGradientProperty().addListener(new ChangeListener<LinearGradient>() {

			@Override
			public void changed(ObservableValue<? extends LinearGradient> observable, LinearGradient oldValue,
					LinearGradient newValue) {
				setPaint(newValue);
			}
		});
		return simpleGradientPicker;
	}

	/**
	 * Returns the currently selected {@link Paint}.
	 *
	 * @return The currently selected {@link Paint}.
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * Changes the currently selected {@link Paint} to the given value.
	 *
	 * @param paint
	 *            The new value for the selected {@link Paint}.
	 */
	public void setPaint(Paint paint) {
		// initialize history with initial values (if not initialized before)
		if (this.paint == null) {
			if (paint instanceof Color) {
				if (!Color.TRANSPARENT.equals(paint)) {
					lastFillColor = paint;
					lastSimpleGradient = FXSimpleLinearGradientPicker.createSimpleLinearGradient(Color.WHITE,
							(Color) paint);
					lastAdvancedGradient = FXAdvancedLinearGradientPicker.createAdvancedLinearGradient(Color.WHITE,
							((Color) paint).brighter(), ((Color) paint));
				}
			} else if (FXSimpleLinearGradientPicker.isSimpleLinearGradient(paint)) {
				lastSimpleGradient = paint;
				List<Stop> stops = ((LinearGradient) paint).getStops();
				lastFillColor = stops.get(1).getColor();
				lastAdvancedGradient = FXAdvancedLinearGradientPicker.createAdvancedLinearGradient(
						stops.get(0).getColor(), stops.get(1).getColor().brighter(), stops.get(1).getColor());
			} else if (FXAdvancedLinearGradientPicker.isAdvancedLinearGradient(paint)) {
				lastAdvancedGradient = paint;
				List<Stop> stops = paint instanceof LinearGradient ? ((LinearGradient) paint).getStops()
						: ((RadialGradient) paint).getStops();
				lastFillColor = stops.get(stops.size() - 1).getColor();
				lastSimpleGradient = FXSimpleLinearGradientPicker.createSimpleLinearGradient(stops.get(0).getColor(),
						stops.get(stops.size() - 1).getColor());
			}
		}

		// assign new value
		this.paint = paint;

		// update image label to reflect new value
		updateImageLabel();
	}

	/**
	 * Re-renders the image that visualizes the currently selected {@link Paint}
	 * .
	 */
	protected void updateImageLabel() {
		if (optionsCombo != null && imageLabel != null && paint != null) {
			ImageData imageData = FXPaintUtils.getPaintImageData(64, optionsCombo.getItemHeight() - 1, paint);
			imageLabel.setImage(new Image(imageLabel.getDisplay(), imageData, imageData.getTransparencyMask()));
		}
	}

}