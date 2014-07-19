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
package org.eclipse.gef4.mvc.fx;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.MvcModule;
import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;
import org.eclipse.gef4.mvc.behaviors.AbstractSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXFocusBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXZoomBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXMarqueeOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXFocusTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXPinchSpreadTool;
import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.fx.viewer.ISceneFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class MvcFxModule extends MvcModule<Node> {

	protected void bindAbstractFXContentPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(FXClickDragTool.CLICK_TOOL_POLICY_KEY).to(
				FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(FXHoverTool.TOOL_POLICY_KEY).to(
				FXHoverOnHoverPolicy.class);

		// register default behaviors ( (which are based on viewer models)
		adapterMapBinder.addBinding(AbstractHoverBehavior.class).to(
				FXHoverBehavior.class);
		adapterMapBinder.addBinding(AbstractSelectionBehavior.class).to(
				FXSelectionBehavior.class);
		adapterMapBinder.addBinding(FXFocusBehavior.class).to(
				FXFocusBehavior.class);
	}

	protected void bindAbstractFXFeedbackPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	protected void bindAbstractFXHandlePartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AbstractFXHoverPolicy.class).to(
				FXHoverOnHoverPolicy.class);
		adapterMapBinder.addBinding(AbstractHoverBehavior.class).to(
				FXHoverBehavior.class);
	}

	protected void bindFXDefaultFeedbackPartFactory() {
		binder().bind(new TypeLiteral<IFeedbackPartFactory<Node>>() {
		}).to(FXDefaultFeedbackPartFactory.class);
	}

	protected void bindFXDefaultHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).to(FXDefaultHandlePartFactory.class);
	}

	protected void bindFXDomainAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(FXHoverTool.class).toInstance(
				new FXHoverTool());
		adapterMapBinder.addBinding(FXClickDragTool.class).toInstance(
				new FXClickDragTool());
		adapterMapBinder.addBinding(FXPinchSpreadTool.class).toInstance(
				new FXPinchSpreadTool());
		adapterMapBinder.addBinding(FXScrollTool.class).toInstance(
				new FXScrollTool());
		adapterMapBinder.addBinding(FXFocusTool.class).toInstance(
				new FXFocusTool());
	}

	protected void bindFXRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node>>() {
		}).to(FXRootPart.class);
	}

	protected void bindFXRootPartAdapters(
			MapBinder<Class<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(FXClickDragTool.CLICK_TOOL_POLICY_KEY).to(
				FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(FXClickDragTool.DRAG_TOOL_POLICY_KEY).to(
				FXMarqueeOnDragPolicy.class);
		adapterMapBinder.addBinding(FXHoverTool.TOOL_POLICY_KEY).to(
				FXHoverOnHoverPolicy.class);
		adapterMapBinder.addBinding(FXScrollTool.TOOL_POLICY_KEY).to(
				FXZoomOnScrollPolicy.class);
		adapterMapBinder.addBinding(FXPinchSpreadTool.TOOL_POLICY_KEY).to(
				FXZoomOnPinchSpreadPolicy.class);

		// register (default) behaviors (which are based on viewer models)
		adapterMapBinder.addBinding(FXSelectionBehavior.class).to(
				FXSelectionBehavior.class);
		adapterMapBinder.addBinding(FXHoverBehavior.class).to(
				FXHoverBehavior.class);
		adapterMapBinder.addBinding(FXZoomBehavior.class).to(
				FXZoomBehavior.class);
	}

	protected void bindSceneFactory() {
		// IMPORTANT: we cannot use assisted inject, as Scene constructor may
		// not be annotated
		binder().bind(ISceneFactory.class).toInstance(new ISceneFactory() {
			@Override
			public Scene createScene(Parent rootVisual) {
				return new Scene(rootVisual);
			}
		});
	}

	@Override
	protected void configure() {
		super.configure();

		// install scene factory, used by FXViewer to create scene
		bindSceneFactory();

		// bind root part and default factories for handles and feedback
		bindFXRootPart();
		bindFXDefaultHandlePartFactory();
		bindFXDefaultFeedbackPartFactory();

		// bind additional adapters for FXDomain
		bindFXDomainAdapters(getAdapterMapBinder(FXDomain.class));

		// bind additional adapters for FXRootPart
		bindFXRootPartAdapters(getAdapterMapBinder(FXRootPart.class));

		// bind additional adapters for FX specific visual parts
		bindAbstractFXContentPartAdapters(getAdapterMapBinder(AbstractFXContentPart.class));
		bindAbstractFXFeedbackPartAdapters(getAdapterMapBinder(AbstractFXFeedbackPart.class));
		bindAbstractFXHandlePartAdapters(getAdapterMapBinder(AbstractFXHandlePart.class));
	}

}
