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

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A picker for multi-stop {@link LinearGradient}s and {@link RadialGradient}s.
 * 
 * @author anyssen
 *
 */
public class FXAdvancedGradientPicker implements IPropertyChangeNotifier {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Paint advancedGradient;

	private Control control;

	public FXAdvancedGradientPicker(Composite parent) {
		control = createControl(parent);
		// TODO: start with three stops
		setAdvancedGradient(createAdvancedLinearGradient(Color.WHITE,
				Color.GREY, Color.BLACK));
	}

	public Control getControl() {
		return control;
	}

	protected Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		// TODO: use canvas factory
		FXCanvas canvas = new FXCanvas(composite, SWT.NONE);
		HBox root = new HBox();
		Rectangle r = new Rectangle(20, 30);
		r.setFill(Color.RED);
		root.getChildren().add(r);
		Scene scene = new Scene(root);
		canvas.setScene(scene);
		return composite;
	}

	public void setAdvancedGradient(Paint advancedGradient) {
		if (!isAdvancedGradient(advancedGradient)) {
			throw new IllegalArgumentException("Given value '"
					+ advancedGradient + "' is no advanced gradient");
		}
		;

		Paint oldAdvancedGradient = this.advancedGradient;
		this.advancedGradient = advancedGradient;
		// update controls to reflect changes
		pcs.firePropertyChange("simpleGradient", oldAdvancedGradient,
				advancedGradient);
	}

	public Paint getAdvancedGradient() {
		return advancedGradient;
	}

	protected static LinearGradient createAdvancedLinearGradient(Color c1,
			Color c2, Color c3) {
		Stop[] stops = new Stop[] { new Stop(0, c1), new Stop(0.5, c2),
				new Stop(1, c3) };
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

	public static boolean isAdvancedGradient(Paint paint) {
		if (paint instanceof LinearGradient) {
			return ((LinearGradient) paint).getStops().size() > 2;
		} else if (paint instanceof RadialGradient) {
			return true;
		}
		return false;
	}

}
