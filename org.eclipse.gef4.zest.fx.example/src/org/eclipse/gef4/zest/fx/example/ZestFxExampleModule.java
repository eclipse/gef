/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.example;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.example.parts.ZestFxExampleFeedbackPartFactory;
import org.eclipse.gef4.zest.fx.example.parts.ZestFxExampleHandlePartFactory;

import com.google.inject.TypeLiteral;

public class ZestFxExampleModule extends ZestFxModule {

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).toInstance(new ZestFxExampleHandlePartFactory());
	}

	@Override
	protected void bindIFeedbackPartFactory() {
		binder().bind(new TypeLiteral<IFeedbackPartFactory<Node>>() {
		}).toInstance(new ZestFxExampleFeedbackPartFactory());
	}
}
