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
import javafx.scene.transform.Affine;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.mvc.MvcModule;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.fx.behaviors.FXFocusBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXGridBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXViewportBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.parts.FXTransformProvider;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXMarqueeOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXPanOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXPanOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXFocusTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXPinchSpreadTool;
import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.GraveyardModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class MvcFxModule extends MvcModule<Node> {

	@SuppressWarnings("serial")
	protected void bindAbstractFXContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register default providers
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<Affine>>() {
				}, FXTransformPolicy.TRANSFORMATION_PROVIDER_ROLE)).to(
				FXTransformProvider.class);

		// register default behaviors
		adapterMapBinder.addBinding(AdapterKey.get(ContentBehavior.class)).to(
				new TypeLiteral<ContentBehavior<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(HoverBehavior.class)).to(
				new TypeLiteral<HoverBehavior<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(SelectionBehavior.class))
				.to(new TypeLiteral<SelectionBehavior<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(FXFocusBehavior.class)).to(
				FXFocusBehavior.class);

		// register default policies
		adapterMapBinder.addBinding(AdapterKey.get(ContentPolicy.class)).to(
				new TypeLiteral<ContentPolicy<Node>>() {
				});
	}

	protected void bindAbstractFXFeedbackPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	protected void bindAbstractFXHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register tool interaction policy which delegates hover interaction to
		// hover policy
		adapterMapBinder
				.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(
						FXHoverOnHoverPolicy.class);
		// register behavior which reacts to changes of the hover model and
		// updates selection (and handles)
		adapterMapBinder.addBinding(AdapterKey.get(HoverBehavior.class)).to(
				new TypeLiteral<HoverBehavior<Node>>() {
				});
	}

	protected void bindFXDomainAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.class))
				.toInstance(new FXHoverTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXClickDragTool.class))
				.toInstance(new FXClickDragTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.class))
				.toInstance(new FXTypeTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXPinchSpreadTool.class))
				.toInstance(new FXPinchSpreadTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXScrollTool.class))
				.toInstance(new FXScrollTool());
		adapterMapBinder.addBinding(AdapterKey.get(FXFocusTool.class))
				.toInstance(new FXFocusTool());

		adapterMapBinder.addBinding(AdapterKey.get(IViewer.class)).to(
				new TypeLiteral<IViewer<Node>>() {
				});
	}

	protected void bindFXRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY)).to(
				FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY)).to(
				FXMarqueeOnDragPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY)).to(
						FXHoverOnHoverPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXScrollTool.TOOL_POLICY_KEY, "zoomOnScroll"))
				.to(FXZoomOnScrollPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXScrollTool.TOOL_POLICY_KEY, "panOnScroll"))
				.to(FXPanOnScrollPolicy.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(FXPinchSpreadTool.TOOL_POLICY_KEY)).to(
				FXZoomOnPinchSpreadPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(FXPanOnTypePolicy.class);

		// register default behaviors
		adapterMapBinder.addBinding(AdapterKey.get(ContentBehavior.class)).to(
				new TypeLiteral<ContentBehavior<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(SelectionBehavior.class))
				.to(new TypeLiteral<SelectionBehavior<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(FXGridBehavior.class)).to(
				FXGridBehavior.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXViewportBehavior.class))
				.to(FXViewportBehavior.class);
	}

	protected void bindFXViewerAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// bind root part
		adapterMapBinder.addBinding(AdapterKey.get(IRootPart.class)).to(
				new TypeLiteral<IRootPart<Node, ? extends Node>>() {
				});

		// bind factories
		adapterMapBinder.addBinding(AdapterKey.get(IContentPartFactory.class))
				.to(new TypeLiteral<IContentPartFactory<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(IHandlePartFactory.class))
				.to(new TypeLiteral<IHandlePartFactory<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(IFeedbackPartFactory.class))
				.to(new TypeLiteral<IFeedbackPartFactory<Node>>() {
				});

		// bind parameterized default viewer models (others are already bound in
		// superclass)
		adapterMapBinder.addBinding(AdapterKey.get(GraveyardModel.class)).to(
				new TypeLiteral<GraveyardModel<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(FocusModel.class)).to(
				new TypeLiteral<FocusModel<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(HoverModel.class)).to(
				new TypeLiteral<HoverModel<Node>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.get(SelectionModel.class)).to(
				new TypeLiteral<SelectionModel<Node>>() {
				});
	}

	protected void bindHoverBehavior() {
		binder().bind(new TypeLiteral<HoverBehavior<Node>>() {
		}).to(FXHoverBehavior.class);
	}

	protected void bindIDomain() {
		binder().bind(new TypeLiteral<IDomain<Node>>() {
		}).to(FXDomain.class);
	}

	protected void bindIFeedbackPartFactory() {
		binder().bind(new TypeLiteral<IFeedbackPartFactory<Node>>() {
		}).to(FXDefaultFeedbackPartFactory.class);
	}

	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).to(FXDefaultHandlePartFactory.class);
	}

	protected void bindIRootPart() {
		binder().bind(new TypeLiteral<IRootPart<Node, ? extends Node>>() {
		}).to(FXRootPart.class);
	}

	protected void bindIViewer() {
		binder().bind(new TypeLiteral<IViewer<Node>>() {
		}).to(FXViewer.class);
	}

	@Override
	protected void configure() {
		super.configure();

		// bind default factories for handles and feedback
		bindIHandlePartFactory();
		bindIFeedbackPartFactory();

		// bind root IRootPart<Node>, IViewer<Node> and IDomain<Node> to
		// FXRootPart, FXViewer, and FXDomain
		bindIDomain();
		bindIViewer();
		bindIRootPart();

		// bind special behavior implementations
		bindHoverBehavior();

		// bind additional adapters for FXDomain
		bindFXDomainAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXDomain.class));

		// bind additional adapters for FXViewer
		bindFXViewerAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXViewer.class));

		// bind additional adapters for FXRootPart
		bindFXRootPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXRootPart.class));

		// bind additional adapters for FX specific visual parts
		bindAbstractFXContentPartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), AbstractFXContentPart.class));
		bindAbstractFXFeedbackPartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), AbstractFXFeedbackPart.class));
		bindAbstractFXHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), AbstractFXHandlePart.class));
	}

}
