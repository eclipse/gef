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

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author anyssen
 *
 */
public class FXSimpleGradientPicker implements IPropertyChangeSupport {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private LinearGradient simpleGradient;
	private FXColorPicker color1Editor;
	private FXColorPicker color2Editor;

	private Control control;
	
	public FXSimpleGradientPicker(Composite parent){
		control = createControl(parent);
		setSimpleGradient(createSimpleGradient(Color.WHITE, Color.BLACK));
	}
	
	public Control getControl() {
		return control;
	}

	protected Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		color1Editor = new FXColorPicker(composite);
		color1Editor.getControl().setLayoutData(new GridData());
		color1Editor.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSimpleGradient(createSimpleGradient(color1Editor.getColor(), color2Editor.getColor()));
			}
		});
		
		
		color2Editor = new FXColorPicker(composite);
		color2Editor.getControl().setLayoutData(new GridData());
		color2Editor.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setSimpleGradient(createSimpleGradient(color1Editor.getColor(), color2Editor.getColor()));
			}
		});
		return composite;
	}
	
	public void setSimpleGradient(LinearGradient simpleGradient) {
		if(!isSimpleGradient(simpleGradient)){
			throw new IllegalArgumentException("Given value '" + simpleGradient + "' is no simple gradient");
		};
		
		LinearGradient oldSimpleGradient = this.simpleGradient;
        this.simpleGradient = simpleGradient;
        List<Stop> stops = simpleGradient.getStops();
        if(stops.size() != 2){
        	throw new IllegalArgumentException("A simple gradient may only contain two stops.");
        }
        if(!color1Editor.getColor().equals(stops.get(0).getColor())){
        	color1Editor.setColor(stops.get(0).getColor());
        }
        if(!color2Editor.getColor().equals(stops.get(1).getColor())){
        	color2Editor.setColor(stops.get(1).getColor());
        }
        pcs.firePropertyChange("simpleGradient", oldSimpleGradient, simpleGradient);
	}
	
	public LinearGradient getSimpleGradient() {
		return simpleGradient;
	}
	
	protected static LinearGradient createSimpleGradient(Color c1, Color c2) {
		// TODO: add angle
		Stop[] stops = new Stop[] { new Stop(0, c1), new Stop(1, c2)};
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
			return ((LinearGradient)paint).getStops().size() == 2;
		}
		return false;
	}

}
