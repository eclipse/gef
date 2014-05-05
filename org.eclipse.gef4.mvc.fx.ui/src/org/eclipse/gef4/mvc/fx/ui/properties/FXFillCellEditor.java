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

import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

// TODO: maybe move the cell editor to swtfx (and introduce jface dependency there) or swtfx.ui
public class FXFillCellEditor extends DialogCellEditor {

	public class FXPaintDialog extends Dialog {

		private Paint paint;
		private String title;

		// store the last selection when switching options
		private Paint lastAdvancedGradient;
		private Paint lastImagePattern;
		private Combo optionsCombo;
		private Label imageLabel;

		private Paint lastFillColor = Color.WHITE;
		private FXColorChooser fillColorChooser;

		private Paint lastSimpleGradient = createSimpleGradient(Color.WHITE, Color.BLACK);
		private FXColorChooser simpleGradientColor1Chooser;
		private FXColorChooser simpleGradientColor2Chooser;

		public FXPaintDialog(Shell parent, String title) {
			super(parent);
			this.title = title;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			container.setFont(parent.getFont());
			GridLayout gl = new GridLayout(1, true);
			gl.marginHeight = 0;
			gl.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			gl.marginTop = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			container.setLayout(gl);
			container.setBackground(parent.getBackground());

			Composite labelContainer = new Composite(container, SWT.NONE);
			labelContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
					false));
			gl = new GridLayout(2, true);
			gl.marginWidth = 3; // align with combo below
			labelContainer.setLayout(gl);
			Label fillLabel = new Label(labelContainer, SWT.LEFT);
			fillLabel.setBackground(parent.getBackground());
			fillLabel.setFont(parent.getFont());
			fillLabel.setLayoutData(new GridData());
			fillLabel.setText("Fill:");
			imageLabel = new Label(labelContainer, SWT.RIGHT);
			imageLabel
					.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));

			Composite optionsContainer = new Composite(container, SWT.NONE);
			optionsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
					true, false));
			gl = new GridLayout(1, true);
			gl.marginWidth = 3; // align with combo above
			optionsContainer.setLayout(gl);

			optionsCombo = new Combo(optionsContainer, SWT.DROP_DOWN
					| SWT.READ_ONLY | SWT.BORDER);
			optionsCombo.setItems(new String[] { "No Fill", "Color Fill",
					"Gradient Fill", "Advanced Gradient Fill"/*
															 * , "Image Fill"
															 */});
			optionsCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
					true, false));
			final Composite optionsComposite = createNoFillComposite(optionsContainer);
			final StackLayout sl = new StackLayout();
			optionsComposite.setLayout(sl);
			optionsComposite.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL, GridData.BEGINNING, true, false));

			// no fill
			final Composite noFillComposite = createNoFillComposite(optionsComposite);
			final Composite colorFillComposite = createColorFillComposite(optionsComposite);
			final Composite simpleGradientFillComposite = createSimpleGradientFillComposite(optionsComposite);
			// TODO: others

			optionsCombo.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					// store previous option value
					if(paint != null){
						if(isFillColor(paint)){
							lastFillColor = paint;
						}
						else if(isSimpleGradient(paint)){
							lastSimpleGradient = paint;
						}
						else if(isAdvancedGradient(paint)){
							lastAdvancedGradient = paint;
						}
						else {
							lastImagePattern = paint;
						}
					}
					// set new option value
					switch (optionsCombo.getSelectionIndex()) {
					case 0:
						sl.topControl = noFillComposite;
						paint = Color.TRANSPARENT;
						break;
					case 1:
						sl.topControl = colorFillComposite;
						setPaint(lastFillColor); // restore last fill color
						fillColorChooser.setColor((Color) paint);
						break;
					case 2:
						sl.topControl = simpleGradientFillComposite;
						setPaint(lastSimpleGradient);
						List<Stop> stops = ((LinearGradient)paint).getStops();
						simpleGradientColor1Chooser.setColor(stops.get(0).getColor());
						simpleGradientColor2Chooser.setColor(stops.get(1).getColor());
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
			} else if (isFillColor(paint)) {
				optionsCombo.select(1);
			} else if (isSimpleGradient(paint)) {
				optionsCombo.select(2);
			} else if (isAdvancedGradient(paint)) {
				optionsCombo.select(3);
			} else if (paint instanceof ImagePattern) {
				optionsCombo.select(4);
			}
			return container;
		}

		protected Composite createNoFillComposite(
				final Composite optionsComposite) {
			final Composite noFillComposite = new Composite(optionsComposite,
					SWT.NONE); // dummy for no-fill
			return noFillComposite;
		}

		public Composite createColorFillComposite(Composite optionsComposite) {
			new Composite(optionsComposite, SWT.NONE);
			Composite colorFillComposite = new Composite(optionsComposite,
					SWT.NONE);
			colorFillComposite.setLayout(new GridLayout());
			fillColorChooser = new FXColorChooser(colorFillComposite);
			fillColorChooser.setLayoutData(new GridData(SWT.BEGINNING,
					SWT.BEGINNING, false, false));
			fillColorChooser.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setPaint(fillColorChooser.getColor());
				}
			});
			return colorFillComposite;
		}

		protected Composite createSimpleGradientFillComposite(
				Composite optionsComposite) {
			new Composite(optionsComposite, SWT.NONE);
			Composite composite = new Composite(optionsComposite, SWT.NONE);
			composite.setLayout(new GridLayout());
			
			simpleGradientColor1Chooser = new FXColorChooser(
					composite);
			simpleGradientColor1Chooser.setLayoutData(new GridData(
					SWT.BEGINNING, SWT.BEGINNING, false, false));
			simpleGradientColor1Chooser
					.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							updateSimpleGradient();
						}
					});
			simpleGradientColor2Chooser = new FXColorChooser(
					composite);
			simpleGradientColor2Chooser.setLayoutData(new GridData(
					SWT.BEGINNING, SWT.BEGINNING, false, false));
			simpleGradientColor2Chooser
					.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							// TODO: update paint
							updateSimpleGradient();
						}
					});
			return composite;
		}
		
		protected void updateSimpleGradient(){
			LinearGradient lg1 = createSimpleGradient(simpleGradientColor1Chooser.getColor(), simpleGradientColor2Chooser.getColor());
			setPaint(lg1);
		}

		protected LinearGradient createSimpleGradient(Color c1, Color c2) {
			// TODO: add angle
			Stop[] stops = new Stop[] { new Stop(0, c1), new Stop(1, c2)};
			LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
			return lg1;
		}

		protected void updateImageLabel() {
			if (optionsCombo != null && imageLabel != null && paint != null) {
				ImageData imageData = createPaintImage(optionsCombo, paint);
				imageLabel.setImage(new Image(getDefaultLabel().getDisplay(),
						imageData, imageData.getTransparencyMask()));
			}
		}

		// overriding this methods allows you to set the
		// title of the custom dialog
		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(title);
		}

		// @Override
		// protected Point getInitialSize() {
		// return new Point(450, 300);
		// }

		public void setPaint(Paint paint) {
			// assign new value
			this.paint = paint;

			// update image label to reflect new value
			updateImageLabel();
		}

		public Paint getPaint() {
			return paint;
		}

	}

	private Image image;

	public FXFillCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	public boolean isFillColor(Paint paint) {
		return paint != null && paint instanceof Color
				&& !Color.TRANSPARENT.equals(paint);
	}

	public boolean isSimpleGradient(Paint paint) {
		if (!(paint instanceof LinearGradient)) {
			return false;
		} else {
			LinearGradient lg = (LinearGradient) paint;
			// TODO: see if this is enough
			if (lg.getStops().size() == 2) {
				return true;
			}
		}
		return false;
	}

	public boolean isAdvancedGradient(Paint paint) {
		if (paint instanceof LinearGradient || paint instanceof RadialGradient) {
			if (!isSimpleGradient(paint)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public FXFillCellEditor(Composite parent, int style) {
		super(parent, style);
		doSetValue(Color.TRANSPARENT);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		FXPaintDialog dialog = new FXPaintDialog(cellEditorWindow.getShell(),
				"Select Fill");
		Object value = getValue();
		if (value != null) {
			dialog.setPaint((Paint) value);
		}
		dialog.open();
		return dialog.getPaint();
	}

	private ImageData createPaintImage(Control w, Paint paint) {
		int width = 64;
		int height = 16;
		if (w instanceof Table) {
			height = ((Table) w).getItemHeight() - 1;
		} else if (w instanceof Tree) {
			height = ((Tree) w).getItemHeight() - 1;
		} else if (w instanceof TableTree) {
			height = ((TableTree) w).getItemHeight() - 1;
		} else if (w instanceof Combo) {
			height = ((Combo) w).getItemHeight() - 1;
		}
		return FXColorChooser.createPaintImage(width, height, paint);
	}

	@Override
	protected void updateContents(Object value) {
		final Paint paint = value == null ? Color.TRANSPARENT : (Paint) value;

		if (image != null) {
			image.dispose();
		}

		ImageData id = createPaintImage(getDefaultLabel().getParent()
				.getParent(), paint);
		image = new Image(getDefaultLabel().getDisplay(), id,
				id.getTransparencyMask());

		getDefaultLabel().setImage(image);
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
		super.dispose();
	}
}
