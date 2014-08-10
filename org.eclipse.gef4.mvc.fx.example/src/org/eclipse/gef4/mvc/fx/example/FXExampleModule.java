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
package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.inject.TypeLiteral;

public class FXExampleModule extends MvcFxModule {

	@Override
	protected void bindFXDefaultHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).toInstance(new FXExampleHandlePartFactory());
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new FXExampleContentPartFactory());
	}

	@Override
	protected void configure() {
		super.configure();
		bindIContentPartFactory();
	}

}