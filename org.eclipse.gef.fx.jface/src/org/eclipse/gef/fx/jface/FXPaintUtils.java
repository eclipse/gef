/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef.fx.swt.controls.FXAdvancedLinearGradientPicker;
import org.eclipse.gef.fx.swt.controls.FXSimpleLinearGradientPicker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import javafx.embed.swt.SWTFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Provides utilities for dealing with {@link Paint} representations.
 * 
 * @author anyssen
 *
 */
public class FXPaintUtils {

	/**
	 * Creates a rectangular {@link Image} to visualize the given {@link Paint}.
	 *
	 * @param width
	 *            The width of the resulting {@link Image}.
	 * @param height
	 *            The height of the resulting {@link Image}.
	 * @param paint
	 *            The {@link Paint} to use for filling the {@link Image}.
	 * @return The resulting (filled) {@link Image}.
	 */
	public static ImageData getPaintImageData(int width, int height, Paint paint) {
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
		WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);
		return SWTFXUtils.fromFXImage(snapshot, null);
	}

	/**
	 * Creates a human-readable string representation for the given
	 * {@link Paint} value.
	 * 
	 * @param paint
	 *            The {@link Paint} value to create a string representation for.
	 * @return A string characterizing the given {@link Paint} value.
	 */
	public static String getPaintDisplayText(Paint paint) {
		if(Color.TRANSPARENT.equals(paint)){
			return "No Color";
		}
		else if (paint instanceof Color) {
			return "Color (" + ((Color) paint).toString() + ")";
		}
		else if (FXSimpleLinearGradientPicker.isSimpleLinearGradient(paint)){
			return "Gradient " + paint.toString().substring(paint.toString().indexOf("("));
		}
		else if(FXAdvancedLinearGradientPicker.isAdvancedLinearGradient(paint)){
			return "Advanced Gradient " + paint.toString().substring(paint.toString().indexOf("("));
		}
		return paint.toString();
	}
}
