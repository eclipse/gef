package org.eclipse.gef4.mvc.fx.ui.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.embed.swt.SWTFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;

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

public class FXFillSelectionDialog extends Dialog {

	private Paint paint;
	private String title;

	// store the last selection when switching options
	private Combo optionsCombo;
	private Label imageLabel;

	private Paint lastFillColor = Color.WHITE;
	private FXColorPicker colorPicker;

	private Paint lastSimpleGradient = FXSimpleGradientPicker
			.createSimpleGradient(Color.WHITE, Color.BLACK);
	private FXSimpleGradientPicker simpleGradientPicker;

	private Paint lastAdvancedGradient = FXAdvancedGradientPicker
			.createAdvancedLinearGradient(Color.WHITE, Color.GREY, Color.BLACK);
	private FXAdvancedGradientPicker advancedGradientPicker;

	// TODO: add support for image pattern

	public FXFillSelectionDialog(Shell parent, String title) {
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
		imageLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));

		Composite optionsContainer = new Composite(container, SWT.NONE);
		optionsContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		gl = new GridLayout(1, true);
		gl.marginWidth = 3; // align with combo above
		optionsContainer.setLayout(gl);

		optionsCombo = new Combo(optionsContainer, SWT.DROP_DOWN
				| SWT.READ_ONLY | SWT.BORDER);
		optionsCombo.setItems(new String[] { "No Fill", "Color Fill",
				"Gradient Fill", "Advanced Gradient Fill"/*
														 * , "Image Fill"
														 */});
		optionsCombo.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		final Composite optionsComposite = createNoFillComposite(optionsContainer);
		final StackLayout sl = new StackLayout();
		optionsComposite.setLayout(sl);
		optionsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL,
				GridData.BEGINNING, true, false));

		// no fill
		final Composite noFillComposite = createNoFillComposite(optionsComposite);
		final Composite colorFillComposite = createColorFillComposite(optionsComposite);
		final Composite simpleGradientFillComposite = createSimpleGradientFillComposite(optionsComposite);
		final Composite advancedGradientFillComposite = createAdvancedGradientFillComposite(optionsComposite);
		// TODO: others

		optionsCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// store previous option value
				if (paint != null) {
					if (paint instanceof Color
							&& !Color.TRANSPARENT.equals(paint)) {
						lastFillColor = paint;
					} else if (FXSimpleGradientPicker.isSimpleGradient(paint)) {
						lastSimpleGradient = paint;
					} else if (FXAdvancedGradientPicker
							.isAdvancedGradient(paint)) {
						lastAdvancedGradient = paint;
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
					colorPicker.setColor((Color) paint);
					break;
				case 2:
					sl.topControl = simpleGradientFillComposite;
					setPaint(lastSimpleGradient);
					simpleGradientPicker
							.setSimpleGradient((LinearGradient) paint);
					break;
				case 3:
					sl.topControl = advancedGradientFillComposite;
					setPaint(lastAdvancedGradient);
					advancedGradientPicker.setAdvancedGradient(paint);
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
		} else if (FXSimpleGradientPicker.isSimpleGradient(paint)) {
			optionsCombo.select(2);
		} else if (FXAdvancedGradientPicker.isAdvancedGradient(paint)) {
			optionsCombo.select(3);
		} else if (paint instanceof ImagePattern) {
			optionsCombo.select(4);
		}
		return container;
	}

	protected Composite createNoFillComposite(final Composite optionsComposite) {
		final Composite noFillComposite = new Composite(optionsComposite,
				SWT.NONE); // dummy for no-fill
		return noFillComposite;
	}

	public Composite createColorFillComposite(Composite optionsComposite) {
		Composite composite = new Composite(optionsComposite, SWT.NONE);
		composite.setLayout(new GridLayout());
		colorPicker = new FXColorPicker(composite);
		colorPicker.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setPaint(colorPicker.getColor());
			}
		});
		return composite;
	}

	protected Composite createSimpleGradientFillComposite(
			Composite optionsComposite) {
		Composite composite = new Composite(optionsComposite, SWT.NONE);
		composite.setLayout(new GridLayout());
		simpleGradientPicker = new FXSimpleGradientPicker(composite);
		simpleGradientPicker.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		simpleGradientPicker
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setPaint(simpleGradientPicker.getSimpleGradient());
					}
				});
		return composite;
	}

	protected Composite createAdvancedGradientFillComposite(
			Composite optionsComposite) {
		Composite composite = new Composite(optionsComposite, SWT.NONE);
		composite.setLayout(new GridLayout());
		advancedGradientPicker = new FXAdvancedGradientPicker(composite);
		advancedGradientPicker.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		advancedGradientPicker
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setPaint(advancedGradientPicker.getAdvancedGradient());
					}
				});
		return composite;
	}

	protected void updateImageLabel() {
		if (optionsCombo != null && imageLabel != null && paint != null) {
			ImageData imageData = createPaintImage(64,
					((Combo) optionsCombo).getItemHeight() - 1, paint);
			imageLabel.setImage(new Image(imageLabel.getDisplay(), imageData,
					imageData.getTransparencyMask()));
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