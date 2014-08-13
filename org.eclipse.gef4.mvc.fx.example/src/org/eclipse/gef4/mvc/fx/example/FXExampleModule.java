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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.policies.DefaultFocusPolicy;
import org.eclipse.gef4.mvc.policies.DefaultHoverPolicy;
import org.eclipse.gef4.mvc.policies.DefaultSelectionPolicy;
import org.eclipse.gef4.mvc.policies.DefaultZoomPolicy;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class FXExampleModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY)).to(
						FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder
		.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(
				FXHoverOnHoverPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.get(DefaultHoverPolicy.class))
		.to(new TypeLiteral<DefaultHoverPolicy<Node>>() {
		});
		adapterMapBinder.addBinding(
				AdapterKey.get(DefaultSelectionPolicy.class)).to(
						new TypeLiteral<DefaultSelectionPolicy<Node>>() {
						});
		adapterMapBinder.addBinding(AdapterKey.get(DefaultZoomPolicy.class))
		.to(new TypeLiteral<DefaultZoomPolicy<Node>>() {
		});
		adapterMapBinder.addBinding(AdapterKey.get(DefaultFocusPolicy.class))
		.to(new TypeLiteral<DefaultFocusPolicy<Node>>() {
		});
	}

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