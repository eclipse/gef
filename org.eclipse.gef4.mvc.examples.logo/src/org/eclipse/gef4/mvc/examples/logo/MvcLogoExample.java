/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo;

import java.util.Collections;
import java.util.List;

import javafx.application.Application;

import org.eclipse.gef4.mvc.examples.AbstractMvcExample;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;

import com.google.inject.Module;

public class MvcLogoExample extends AbstractMvcExample {

	public static List<FXGeometricModel> createDefaultContents() {
		return Collections.singletonList(new FXGeometricModel());
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public MvcLogoExample() {
		super("GEF4 MVC Logo Example");
	}

	@Override
	protected List<? extends Object> createContents() {
		return createDefaultContents();
	}

	@Override
	protected Module createModule() {
		return new MvcLogoExampleModule();
	}
}
