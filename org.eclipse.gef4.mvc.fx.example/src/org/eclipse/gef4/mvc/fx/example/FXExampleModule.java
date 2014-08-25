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
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.behaviors.DefaultVisualGeometryProvider;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.policies.FocusPolicy;
import org.eclipse.gef4.mvc.policies.HoverPolicy;
import org.eclipse.gef4.mvc.policies.SelectionPolicy;

import com.google.inject.Provider;
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

		adapterMapBinder.addBinding(AdapterKey.get(HoverPolicy.class))
				.to(new TypeLiteral<HoverPolicy<Node>>() {
				});
		adapterMapBinder.addBinding(
				AdapterKey.get(SelectionPolicy.class)).to(
				new TypeLiteral<SelectionPolicy<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(FocusPolicy.class))
				.to(new TypeLiteral<FocusPolicy<Node>>() {
				});

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(Provider.class,
										SelectionBehavior.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(DefaultVisualGeometryProvider.class);
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(
						AdapterKey
								.get(Provider.class,
										SelectionBehavior.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(DefaultVisualGeometryProvider.class);

		// geometry provider for hover feedback
		adapterMapBinder.addBinding(
				AdapterKey.get(Provider.class,
						HoverBehavior.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(DefaultVisualGeometryProvider.class);
	}

	@Override
	protected void bindIHandlePartFactory() {
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