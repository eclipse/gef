/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.common.adapt.inject.AdapterMap;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.mvc.MvcModule;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.behaviors.ContentPartPool;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXFocusBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXGridBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFocusFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.policies.FXChangeViewportPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusTraversalPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXMarqueeOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXPanOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXPanOrZoomOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXZoomOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.providers.FXTransformProvider;
import org.eclipse.gef4.mvc.fx.tools.DefaultTargetPolicyResolver;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXPinchSpreadTool;
import org.eclipse.gef4.mvc.fx.tools.FXRotateTool;
import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.fx.tools.ITargetPolicyResolver;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.policies.ContentPolicy;
import org.eclipse.gef4.mvc.policies.CreationPolicy;
import org.eclipse.gef4.mvc.policies.DeletionPolicy;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Node;

/**
 * The Guice module which contains all (default) bindings related to the MVC.FX
 * bundle. It extends the MVC Guice module of the MVC bundle, which provides
 * JavaFX-unrelated (default) bindings.
 * <p>
 * In an Eclipse UI-integration scenario this module is intended to be
 * overwritten by the MVC.FX.UI Guice module, which is provided by the MVC.FX.UI
 * bundle.
 * <p>
 * Generally, we recommended that all clients should create an own non-UI
 * module, which extends this module, as well as an own UI module, which extends
 * the MVC.FX.UI module, being used to override the non-UI module in an
 * Eclipse-UI integration scenario, as follows:
 *
 * <pre>
 *
 *      MVC   &lt;--extends--    MVC.FX   &lt;--extends--  Client-Non-UI-Module
 *       ^                       ^                           ^
 *       |                       |                           |
 *   overrides               overrides                   overrides
 *       |                       |                           |
 *       |                       |                           |
 *    MVC.UI  &lt;--extends--  MVC.FX.UI  &lt;--extends--   Client-UI-Module
 * </pre>
 *
 * @author anyssen
 */
public class MvcFxModule extends MvcModule<Node> {

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link AbstractFXContentPart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractFXContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register default providers
		bindFXTransformProviderAsAbstractFXContentPartAdapter(adapterMapBinder);

		// register default behaviors
		bindContentBehaviorAsAbstractFXContentPartAdapter(adapterMapBinder);
		bindHoverBehaviorAsAbstractFXContentPartAdapter(adapterMapBinder);
		bindSelectionBehaviorAsAbstractFXContentPartAdapter(adapterMapBinder);
		bindFXFocusBehaviorAsAbstractFXContentPartAdapter(adapterMapBinder);

		// register default policies
		bindContentPolicyAsAbstractFXContentPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link AbstractFXFeedbackPart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXFeedbackPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractFXFeedbackPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link AbstractFXHandlePart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractFXHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindFXHoverOnHoverPolicyAsAbstractFXHandlePartAdapter(adapterMapBinder);
		bindHoverBehaviorAsAbstractFXHandlePartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link ContentBehavior}, parameterized by {@link Node}
	 * , to the {@link AdapterMap} binder for {@link AbstractFXContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentBehaviorAsAbstractFXContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<ContentBehavior<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link ContentBehavior}, parameterized by {@link Node}
	 * , to the {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentBehaviorAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<ContentBehavior<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link ContentModel} to the {@link AdapterMap} binder
	 * for {@link AbstractViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ContentModel.class);
	}

	/**
	 * Binds {@link ContentPartPool}, parameterized by {@link Node}, to the
	 * {@link FXViewer} adaptable scope.
	 */
	protected void bindContentPartPool() {
		binder().bind(new TypeLiteral<ContentPartPool<Node>>() {
		}).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds a binding for {@link ContentPolicy}, parameterized by {@link Node} ,
	 * to the {@link AdapterMap} binder for {@link AbstractFXContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentPolicyAsAbstractFXContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<ContentPolicy<Node>>() {
				});
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link FXViewer} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentViewerAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindIContentPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindContentModelAsContentViewerAdapter(adapterMapBinder);
		bindGridModelAsContentViewerAdapter(adapterMapBinder);
		bindFocusModelAsContentViewerAdapter(adapterMapBinder);
		bindHoverModelAsContentViewerAdapter(adapterMapBinder);
		bindSelectionModelAsContentViewerAdapter(adapterMapBinder);
		bindFXRootPartAsContentViewerAdapter(adapterMapBinder);
		bindFocusFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindHoverFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSelectionFeedbackPartFactoryAsContentViewerAdapter(
				adapterMapBinder);
		bindHoverHandlePartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSelectionHandlePartFactoryAsContentViewerAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link FXRootPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentViewerRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		bindFocusAndSelectOnClickPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXMarqueeOnDragPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXHoverOnHoverPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXPanOrZoomOnScrollPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXZoomOnPinchSpreadPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXPanOnTypePolicyAsFXRootPartAdapter(adapterMapBinder);
		// register change viewport policy
		bindFXChangeViewportPolicyAsFXRootPartAdapter(adapterMapBinder);
		// register default behaviors
		bindContentBehaviorAsFXRootPartAdapter(adapterMapBinder);
		bindSelectionBehaviorAsFXRootPartAdapter(adapterMapBinder);
		bindGridBehaviorAsFXRootPartAdapter(adapterMapBinder);
		bindFXFocusBehaviorAsFXRootPartAdapter(adapterMapBinder);
		// creation and deletion policy
		bindCreationPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindDeletionPolicyAsFXRootPartAdapter(adapterMapBinder);
		// bind focus traversal policy
		bindFocusTraversalPolicyAsFXRootPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link CreationPolicy} to the {@link AdapterMap}
	 * binder for {@link AbstractRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindCreationPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<CreationPolicy<Node>>() {
				})).to(new TypeLiteral<CreationPolicy<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link DeletionPolicy} to the {@link AdapterMap}
	 * binder for {@link AbstractRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindDeletionPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<DeletionPolicy<Node>>() {
				})).to(new TypeLiteral<DeletionPolicy<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link FXFocusAndSelectOnClickPolicy} to the
	 * {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFocusAndSelectOnClickPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXFocusAndSelectOnClickPolicy.class);
	}

	/**
	 * Binds the {@link IFeedbackPartFactory} that is used to generate focus
	 * feedback.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for content viewer adapters.
	 */
	protected void bindFocusFeedbackPartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXFocusBehavior.FOCUS_FEEDBACK_PART_FACTORY))
				.to(FXDefaultFocusFeedbackPartFactory.class);
	}

	/**
	 * Binds {@link FocusModel}, parameterized by {@link Node}, to the
	 * {@link FXViewer} adaptable scope.
	 */
	protected void bindFocusModel() {
		binder().bind(new TypeLiteral<FocusModel<Node>>() {
		}).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds a binding for {@link FocusModel}, parameterized by {@link Node}, to
	 * the {@link AdapterMap} binder for {@link FXViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindFocusModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.get(new TypeToken<FocusModel<Node>>() {
				})).to(new TypeLiteral<FocusModel<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link FXFocusTraversalPolicy} to the
	 * {@link AdapterMap} binder for {@link AbstractRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFocusTraversalPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXFocusTraversalPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXChangeViewportPolicy} to the
	 * {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXChangeViewportPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXChangeViewportPolicy.class);
	}

	/**
	 * Binds {@link FXClickDragTool} to the {@link FXDomain} adaptable scope.
	 */
	protected void bindFXClickDragTool() {
		binder().bind(FXClickDragTool.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link FXClickDragTool} to the {@link AdapterMap}
	 * binder for {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXClickDragToolAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXClickDragTool.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link FXDomain} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXDomainAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindFXHoverToolAsFXDomainAdapter(adapterMapBinder);
		bindFXClickDragToolAsFXDomainAdapter(adapterMapBinder);
		bindFXTypeToolAsFXDomainAdapter(adapterMapBinder);
		bindFXRotateToolAsFXDomainAdapter(adapterMapBinder);
		bindFXPinchSpreadToolAsFXDomainAdapter(adapterMapBinder);
		bindFXScrollToolAsFXDomainAdapter(adapterMapBinder);
		bindFXViewerAsFXDomainAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link FXFocusBehavior} to the {@link AdapterMap}
	 * binder for {@link AbstractFXContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXFocusBehaviorAsAbstractFXContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXFocusBehavior.class);
	}

	/**
	 * Adds a binding for the {@link FXFocusBehavior} to the given adapter map
	 * binder.
	 *
	 * @param adapterMapBinder
	 *            An adapter map binder for {@link FXRootPart}.
	 */
	protected void bindFXFocusBehaviorAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXFocusBehavior.class);
	}

	/**
	 * Adds a binding for {@link FXHoverOnHoverPolicy} to the {@link AdapterMap}
	 * binder for {@link AbstractFXHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXHoverOnHoverPolicyAsAbstractFXHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXHoverOnHoverPolicy} to the {@link AdapterMap}
	 * binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXHoverOnHoverPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);
	}

	/**
	 * Binds {@link FXHoverTool} to the {@link FXDomain} adaptable scope.
	 */
	protected void bindFXHoverTool() {
		binder().bind(FXHoverTool.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link FXHoverTool} to the {@link AdapterMap} binder
	 * for {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXHoverToolAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverTool.class);
	}

	/**
	 * Adds a binding for {@link FXMarqueeOnDragPolicy} to the
	 * {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXMarqueeOnDragPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXMarqueeOnDragPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXPanOnTypePolicy} to the {@link AdapterMap}
	 * binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXPanOnTypePolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXPanOnTypePolicy.class);
	}

	/**
	 * Adds a binding for {@link FXPanOrZoomOnScrollPolicy} to the
	 * {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXPanOrZoomOnScrollPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("panOnScroll"))
				.to(FXPanOrZoomOnScrollPolicy.class);
	}

	/**
	 * Binds {@link FXPinchSpreadTool} to the {@link FXDomain} adaptable scope.
	 */
	protected void bindFXPinchSpreadTool() {
		binder().bind(FXPinchSpreadTool.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link FXPinchSpreadTool} to the {@link AdapterMap}
	 * binder for {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXPinchSpreadToolAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXPinchSpreadTool.class);
	}

	/**
	 * Adds a binding for {@link IRootPart}, parameterized by {@link Node}, to
	 * the {@link AdapterMap} binder for {@link FXViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXRootPartAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDomain.CONTENT_VIEWER_ROLE))
				.to(FXRootPart.class).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Binds {@link FXRotateTool} to the {@link FXDomain} adaptable scope.
	 */
	protected void bindFXRotateTool() {
		binder().bind(FXRotateTool.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link FXRotateTool} to the {@link AdapterMap} binder
	 * for {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXRotateToolAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXRotateTool.class);
	}

	/**
	 * Binds {@link FXScrollTool} to the {@link FXDomain} adaptable scope.
	 */
	protected void bindFXScrollTool() {
		binder().bind(FXScrollTool.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link FXScrollTool} to the {@link AdapterMap} binder
	 * for {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXScrollToolAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXScrollTool.class);
	}

	/**
	 * Adds a binding for {@link FXTransformProvider} to the {@link AdapterMap}
	 * binder for {@link AbstractFXContentPart}, using the
	 * {@link FXTransformPolicy#TRANSFORM_PROVIDER_KEY}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXTransformProviderAsAbstractFXContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(
						FXTransformPolicy.TRANSFORM_PROVIDER_KEY.getRole()))
				.to(FXTransformProvider.class);
	}

	/**
	 * Binds {@link FXTypeTool} to the {@link FXDomain} adaptable scope.
	 */
	protected void bindFXTypeTool() {
		binder().bind(FXTypeTool.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link FXTypeTool} to the {@link AdapterMap} binder
	 * for {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXTypeToolAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTypeTool.class);
	}

	/**
	 * Adds a binding for {@link FXViewer} to the {@link AdapterMap} binder for
	 * {@link FXDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXViewerAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDomain.CONTENT_VIEWER_ROLE))
				.to(FXViewer.class);
	}

	/**
	 * Adds a binding for {@link FXZoomOnPinchSpreadPolicy} to the
	 * {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFXZoomOnPinchSpreadPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXZoomOnPinchSpreadPolicy.class);
	}

	/**
	 * Adds a binding for {@link FXGridBehavior} to the {@link AdapterMap}
	 * binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGridBehaviorAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXGridBehavior.class);
	}

	/**
	 * Adds a binding for {@link GridModel} to the {@link AdapterMap} binder for
	 * {@link AbstractViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGridModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(GridModel.class);
	}

	/**
	 * Binds {@link FXHoverBehavior} to the {@link HoverBehavior}, parameterized
	 * with {@link Node}.
	 */
	protected void bindHoverBehavior() {
		binder().bind(new TypeLiteral<HoverBehavior<Node>>() {
		}).to(FXHoverBehavior.class);
	}

	/**
	 * Adds a binding for {@link HoverBehavior}, parameterized by {@link Node} ,
	 * to the {@link AdapterMap} binder for {@link AbstractFXContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverBehaviorAsAbstractFXContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<HoverBehavior<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link HoverBehavior}, parameterized by {@link Node} ,
	 * to the {@link AdapterMap} binder for {@link AbstractFXHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFXHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverBehaviorAsAbstractFXHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<HoverBehavior<Node>>() {
				});
	}

	/**
	 * Binds the {@link IFeedbackPartFactory} that is used to generate hover
	 * feedback.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for content viewer adapters.
	 */
	protected void bindHoverFeedbackPartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(HoverBehavior.HOVER_FEEDBACK_PART_FACTORY))
				.to(FXDefaultHoverFeedbackPartFactory.class);
	}

	/**
	 * Binds the {@link IHandlePartFactory} that is used to generate hover
	 * handles.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for content viewer adapters.
	 */
	protected void bindHoverHandlePartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY))
				.to(FXDefaultHoverHandlePartFactory.class);
	}

	/**
	 * Binds {@link HoverModel}, parameterized by {@link Node} to the
	 * {@link FXViewer} adaptable scope.
	 */
	protected void bindHoverModel() {
		binder().bind(new TypeLiteral<HoverModel<Node>>() {
		}).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds a binding for {@link HoverModel}, parameterized by {@link Node}, to
	 * the {@link AdapterMap} binder for {@link FXViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindHoverModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.get(new TypeToken<HoverModel<Node>>() {
				})).to(new TypeLiteral<HoverModel<Node>>() {
				});
	}

	/**
	 * Binds {@link IContentPartFactory} as an adapter for the content viewer.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for the content viewer.
	 */
	// TODO: Instead of binding to an interface here, we should require a
	// binding of the actual content part factory.
	protected void bindIContentPartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<IContentPartFactory<Node>>() {
				});
	}

	/**
	 * Binds {@link DefaultTargetPolicyResolver} to
	 * {@link ITargetPolicyResolver} in adaptable scope of {@link FXViewer}.
	 */
	protected void bindITargetPolicyResolver() {
		binder().bind(ITargetPolicyResolver.class)
				.to(DefaultTargetPolicyResolver.class)
				.in(AdaptableScopes.typed(FXDomain.class));
	}

	/**
	 * Adds a binding for {@link SelectionBehavior}, parameterized by
	 * {@link Node}, to the {@link AdapterMap} binder for
	 * {@link AbstractFXContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindSelectionBehaviorAsAbstractFXContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<SelectionBehavior<Node>>() {
				})).to(new TypeLiteral<SelectionBehavior<Node>>() {
				});
	}

	/**
	 * Adds a binding for {@link SelectionBehavior}, parameterized by
	 * {@link Node}, to the {@link AdapterMap} binder for {@link FXRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindSelectionBehaviorAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<SelectionBehavior<Node>>() {
				})).to(new TypeLiteral<SelectionBehavior<Node>>() {
				});
	}

	/**
	 * Binds the {@link IFeedbackPartFactory} that is used to generate selection
	 * feedback.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for content viewer adapters.
	 */
	protected void bindSelectionFeedbackPartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(SelectionBehavior.SELECTION_FEEDBACK_PART_FACTORY))
				.to(FXDefaultSelectionFeedbackPartFactory.class);
	}

	/**
	 * Binds the {@link IHandlePartFactory} that is used to generate selection
	 * handles.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for content viewer adapters.
	 */
	protected void bindSelectionHandlePartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY))
				.to(FXDefaultSelectionHandlePartFactory.class);
	}

	/**
	 * Binds {@link SelectionModel}, parameterized by {@link Node}, in adaptable
	 * scope of {@link FXViewer}.
	 */
	protected void bindSelectionModel() {
		binder().bind(new TypeLiteral<SelectionModel<Node>>() {
		}).in(AdaptableScopes.typed(FXViewer.class));
	}

	/**
	 * Adds a binding for {@link SelectionModel}, parameterized by {@link Node},
	 * to the {@link AdapterMap} binder for {@link FXViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link FXViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	@SuppressWarnings("serial")
	protected void bindSelectionModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<SelectionModel<Node>>() {
				})).to(new TypeLiteral<SelectionModel<Node>>() {
				});
	}

	@Override
	protected void configure() {
		super.configure();

		// bind default viewer models
		bindHoverModel();
		bindSelectionModel();
		bindFocusModel();

		// bind default target policy resolver for the tools
		bindITargetPolicyResolver();

		// bind tools
		bindFXClickDragTool();
		bindFXHoverTool();
		bindFXPinchSpreadTool();
		bindFXRotateTool();
		bindFXScrollTool();
		bindFXTypeTool();

		// bind special behavior implementations
		bindHoverBehavior();

		// bind part pool being used for behaviors
		bindContentPartPool();

		// bind additional adapters for FXDomain
		bindFXDomainAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXDomain.class));

		// bind additional adapters for FXViewer
		bindContentViewerAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));

		// bind additional adapters for FXRootPart
		bindContentViewerRootPartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXRootPart.class, FXDomain.CONTENT_VIEWER_ROLE));

		// bind additional adapters for FX specific visual parts
		bindAbstractFXContentPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractFXContentPart.class));
		bindAbstractFXFeedbackPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractFXFeedbackPart.class));
		bindAbstractFXHandlePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractFXHandlePart.class));
	}

}
