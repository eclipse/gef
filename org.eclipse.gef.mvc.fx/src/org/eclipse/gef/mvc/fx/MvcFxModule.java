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
package org.eclipse.gef.mvc.fx;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdaptableTypeListener;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterMap;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.fx.behaviors.ContentBehavior;
import org.eclipse.gef.mvc.fx.behaviors.ContentPartPool;
import org.eclipse.gef.mvc.fx.behaviors.FocusBehavior;
import org.eclipse.gef.mvc.fx.behaviors.GridBehavior;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.behaviors.RevealPrimarySelectionBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.AbstractFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.AbstractVisualPart;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.LayeredRootPart;
import org.eclipse.gef.mvc.fx.policies.ChangeViewportPolicy;
import org.eclipse.gef.mvc.fx.policies.ContentPolicy;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.policies.FocusAndSelectOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.FocusTraversalPolicy;
import org.eclipse.gef.mvc.fx.policies.HoverOnHoverPolicy;
import org.eclipse.gef.mvc.fx.policies.MarqueeOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.PanOnTypePolicy;
import org.eclipse.gef.mvc.fx.policies.PanOrZoomOnScrollPolicy;
import org.eclipse.gef.mvc.fx.policies.ZoomOnPinchSpreadPolicy;
import org.eclipse.gef.mvc.fx.providers.TransformProvider;
import org.eclipse.gef.mvc.fx.tools.ClickDragTool;
import org.eclipse.gef.mvc.fx.tools.DefaultTargetPolicyResolver;
import org.eclipse.gef.mvc.fx.tools.HoverTool;
import org.eclipse.gef.mvc.fx.tools.ITargetPolicyResolver;
import org.eclipse.gef.mvc.fx.tools.PinchSpreadTool;
import org.eclipse.gef.mvc.fx.tools.RotateTool;
import org.eclipse.gef.mvc.fx.tools.ScrollTool;
import org.eclipse.gef.mvc.fx.tools.TypeTool;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
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
public class MvcFxModule extends AbstractModule {

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link AbstractContentPart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register default providers
		bindTransformProviderAsAbstractContentPartAdapter(adapterMapBinder);

		// register default behaviors
		bindContentBehaviorAsAbstractContentPartAdapter(adapterMapBinder);

		// register default policies
		bindContentPolicyAsAbstractContentPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for
	 * {@link AbstractFeedbackPart} and all sub-classes. May be overwritten by
	 * sub-classes to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractFeedbackPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractFeedbackPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link AbstractHandlePart}
	 * and all sub-classes. May be overwritten by sub-classes to change the
	 * default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindHoverOnHoverPolicyAsAbstractHandlePartAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) {@link AdapterMap} binding for {@link AbstractVisualPart}
	 * and all sub-classes. May be overwritten by sub-classes to change the
	 * default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractVisualPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindAbstractVisualPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// nothing to bind by default
	}

	/**
	 * Adds a binding for {@link ChangeViewportPolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindChangeViewportPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ChangeViewportPolicy.class);
	}

	/**
	 * Binds {@link ClickDragTool} to the {@link IDomain} adaptable scope.
	 */
	protected void bindClickDragTool() {
		binder().bind(ClickDragTool.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link ClickDragTool} to the {@link AdapterMap} binder
	 * for {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindClickDragToolAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ClickDragTool.class);
	}

	/**
	 * Adds a binding for {@link ContentBehavior}, parameterized by {@link Node}
	 * , to the {@link AdapterMap} binder for {@link AbstractContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentBehaviorAsAbstractContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ContentBehavior.class);
	}

	/**
	 * Adds a binding for {@link ContentBehavior}, parameterized by {@link Node}
	 * , to the {@link AdapterMap} binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ContentBehavior.class);
	}

	/**
	 * Adds a binding for {@link IViewer} to the {@link AdapterMap} binder for
	 * {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentIViewerAsIDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(IDomain.CONTENT_VIEWER_ROLE))
				.to(IViewer.class);
	}

	/**
	 * Ensures that {@link ContentPartPool} is injected into {@link IRootPart}
	 * using the given adapter {@link MapBinder}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to establish the binding.
	 */
	protected void bindContentPartPoolAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ContentPartPool.class);
	}

	/**
	 * Adds a binding for {@link ContentPolicy}, parameterized by {@link Node} ,
	 * to the {@link AdapterMap} binder for {@link AbstractContentPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindContentPolicyAsAbstractContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ContentPolicy.class);
	}

	/**
	 * Adds a binding for {@link CreationPolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindCreationPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(CreationPolicy.class);
	}

	/**
	 * Adds a binding for {@link DeletionPolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindDeletionPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DeletionPolicy.class);
	}

	/**
	 * Adds a binding for {@link FocusAndSelectOnClickPolicy} to the
	 * {@link AdapterMap} binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFocusAndSelectOnClickPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusAndSelectOnClickPolicy.class);
	}

	/**
	 * Adds a binding for the {@link FocusBehavior} to the given adapter map
	 * binder.
	 *
	 * @param adapterMapBinder
	 *            An adapter map binder for {@link IRootPart}.
	 */
	protected void bindFocusBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusBehavior.class);
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
						.role(FocusBehavior.FOCUS_FEEDBACK_PART_FACTORY))
				.to(DefaultFocusFeedbackPartFactory.class);
	}

	/**
	 * Adds a binding for {@link FocusModel}, parameterized by {@link Node}, to
	 * the {@link AdapterMap} binder for {@link IViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFocusModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusModel.class);
	}

	/**
	 * Adds a binding for {@link FocusTraversalPolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFocusTraversalPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusTraversalPolicy.class);
	}

	/**
	 * Adds a binding for {@link GridBehavior} to the {@link AdapterMap} binder
	 * for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGridBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(GridBehavior.class);
	}

	/**
	 * Adds a binding for {@link GridModel} to the {@link AdapterMap} binder for
	 * {@link IViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGridModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(GridModel.class);
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
				.to(DefaultHoverFeedbackPartFactory.class);
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
				.to(DefaultHoverHandlePartFactory.class);
	}

	/**
	 * Adds a binding for {@link HoverModel}, parameterized by {@link Node}, to
	 * the {@link AdapterMap} binder for {@link IViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverModel.class);
	}

	/**
	 * Adds a binding for {@link HoverOnHoverPolicy} to the {@link AdapterMap}
	 * binder for {@link AbstractHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverOnHoverPolicyAsAbstractHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverPolicy.class);
	}

	/**
	 * Adds a binding for {@link HoverOnHoverPolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverOnHoverPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverPolicy.class);
	}

	/**
	 * Binds {@link HoverTool} to the {@link IDomain} adaptable scope.
	 */
	protected void bindHoverTool() {
		binder().bind(HoverTool.class);
	}

	/**
	 * Adds a binding for {@link HoverTool} to the {@link AdapterMap} binder for
	 * {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverToolAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverTool.class);
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
				.to(IContentPartFactory.class);
	}

	/**
	 * Binds {@link IDomain} to a respective {@link HistoricizingDomain}
	 * implementation.
	 */
	protected void bindIDomain() {
		binder().bind(IDomain.class).to(HistoricizingDomain.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link IDomain} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindIDomainAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindHoverToolAsDomainAdapter(adapterMapBinder);
		bindClickDragToolAsDomainAdapter(adapterMapBinder);
		bindTypeToolAsDomainAdapter(adapterMapBinder);
		bindRotateToolAsDomainAdapter(adapterMapBinder);
		bindPinchSpreadToolAsIDomainAdapter(adapterMapBinder);
		bindScrollToolAsDomainAdapter(adapterMapBinder);
		bindContentIViewerAsIDomainAdapter(adapterMapBinder);
		bindITargetPolicyResolverAsIDomainAdapter(adapterMapBinder);
	}

	/**
	 * Binds {@link IOperationHistory} to {@link DefaultOperationHistory} in
	 * adaptable scope of {@link IDomain}.
	 */
	protected void bindIOperationHistory() {
		binder().bind(IOperationHistory.class)
				.to(DefaultOperationHistory.class);
	}

	/**
	 * Binds the default implementation of {@link IRootPart}.
	 */
	protected void bindIRootPart() {
		binder().bind(IRootPart.class).to(LayeredRootPart.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link IRootPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindIRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindContentPartPoolAsIRootPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for "content"
	 * {@link IRootPart} and all sub-classes. May be overwritten by sub-classes
	 * to change the default bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindIRootPartAdaptersForContentViewer(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		bindFocusAndSelectOnClickPolicyAsIRootPartAdapter(adapterMapBinder);
		bindMarqueeOnDragPolicyAsIRootPartAdapter(adapterMapBinder);
		bindHoverOnHoverPolicyAsIRootPartAdapter(adapterMapBinder);
		bindPanOrZoomOnScrollPolicyAsIRootPartAdapter(adapterMapBinder);
		bindZoomOnPinchSpreadPolicyAsIRootPartAdapter(adapterMapBinder);
		bindPanOnTypePolicyAsIRootPartAdapter(adapterMapBinder);
		// register change viewport policy
		bindChangeViewportPolicyAsIRootPartAdapter(adapterMapBinder);
		// register default behaviors
		bindContentBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindSelectionBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindRevealPrimarySelectionBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindGridBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindFocusBehaviorAsIRootPartAdapter(adapterMapBinder);
		// creation and deletion policy
		bindCreationPolicyAsIRootPartAdapter(adapterMapBinder);
		bindDeletionPolicyAsIRootPartAdapter(adapterMapBinder);
		// bind focus traversal policy
		bindFocusTraversalPolicyAsIRootPartAdapter(adapterMapBinder);
	}

	/**
	 * Binds {@link DefaultTargetPolicyResolver} to
	 * {@link ITargetPolicyResolver} in adaptable scope of {@link IDomain}.
	 */
	protected void bindITargetPolicyResolver() {
		binder().bind(ITargetPolicyResolver.class)
				.to(DefaultTargetPolicyResolver.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Binds {@link DefaultTargetPolicyResolver} as a domain adapter.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to add the binding.
	 */
	protected void bindITargetPolicyResolverAsIDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// TODO: verify binding or use two level bindings (interface and
		// implementation)
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DefaultTargetPolicyResolver.class);
	}

	/**
	 * Binds {@link IUndoContext} to {@link UndoContext} in adaptable scope of
	 * {@link IDomain}.
	 */
	protected void bindIUndoContext() {
		binder().bind(IUndoContext.class).to(UndoContext.class);
	}

	/**
	 * Binds {@link IViewer} to a respective {@link InfiniteCanvasViewer}
	 * implementation.
	 */
	protected void bindIViewer() {
		binder().bind(IViewer.class).to(InfiniteCanvasViewer.class);
	}

	/**
	 * Adds (default) {@link AdapterMap} bindings for {@link IViewer} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindIViewerAdaptersForContentViewer(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		bindIContentPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindGridModelAsContentViewerAdapter(adapterMapBinder);
		bindFocusModelAsContentViewerAdapter(adapterMapBinder);
		bindHoverModelAsContentViewerAdapter(adapterMapBinder);
		bindSelectionModelAsContentViewerAdapter(adapterMapBinder);
		bindRootPartAsContentViewerAdapter(adapterMapBinder);
		bindFocusFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindHoverFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSelectionFeedbackPartFactoryAsContentViewerAdapter(
				adapterMapBinder);
		bindHoverHandlePartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSelectionHandlePartFactoryAsContentViewerAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link MarqueeOnDragPolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindMarqueeOnDragPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(MarqueeOnDragPolicy.class);
	}

	/**
	 * Adds a binding for {@link PanOnTypePolicy} to the {@link AdapterMap}
	 * binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindPanOnTypePolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(PanOnTypePolicy.class);
	}

	/**
	 * Adds a binding for {@link PanOrZoomOnScrollPolicy} to the
	 * {@link AdapterMap} binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindPanOrZoomOnScrollPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("panOnScroll"))
				.to(PanOrZoomOnScrollPolicy.class);
	}

	/**
	 * Binds {@link PinchSpreadTool} to the {@link IDomain} adaptable scope.
	 */
	protected void bindPinchSpreadTool() {
		binder().bind(PinchSpreadTool.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link PinchSpreadTool} to the {@link AdapterMap}
	 * binder for {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindPinchSpreadToolAsIDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(PinchSpreadTool.class);
	}

	/**
	 * Adds a binding for {@link RevealPrimarySelectionBehavior}, parameterized
	 * by {@link Node}, to the {@link AdapterMap} binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindRevealPrimarySelectionBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(RevealPrimarySelectionBehavior.class);
	}

	/**
	 * Adds a binding for {@link IRootPart}, parameterized by {@link Node}, to
	 * the {@link AdapterMap} binder for {@link IViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindRootPartAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(IDomain.CONTENT_VIEWER_ROLE))
				.to(IRootPart.class).in(AdaptableScopes.typed(IViewer.class));
	}

	/**
	 * Binds {@link RotateTool} to the {@link IDomain} adaptable scope.
	 */
	protected void bindRotateTool() {
		binder().bind(RotateTool.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link RotateTool} to the {@link AdapterMap} binder
	 * for {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindRotateToolAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(RotateTool.class);
	}

	/**
	 * Binds {@link ScrollTool} to the {@link IDomain} adaptable scope.
	 */
	protected void bindScrollTool() {
		binder().bind(ScrollTool.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link ScrollTool} to the {@link AdapterMap} binder
	 * for {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindScrollToolAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ScrollTool.class);
	}

	/**
	 * Adds a binding for {@link SelectionBehavior}, parameterized by
	 * {@link Node}, to the {@link AdapterMap} binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindSelectionBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SelectionBehavior.class);
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
				.to(DefaultSelectionFeedbackPartFactory.class);
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
				.to(DefaultSelectionHandlePartFactory.class);
	}

	/**
	 * Adds a binding for {@link SelectionModel}, parameterized by {@link Node},
	 * to the {@link AdapterMap} binder for {@link IViewer}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IViewer} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindSelectionModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SelectionModel.class)
				.in(AdaptableScopes.typed(IViewer.class));
	}

	/**
	 * Adds a binding for {@link TransformProvider} to the {@link AdapterMap}
	 * binder for {@link AbstractContentPart}, using the
	 * {@link IVisualPart#TRANSFORM_PROVIDER_KEY}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractContentPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindTransformProviderAsAbstractContentPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(IVisualPart.TRANSFORM_PROVIDER_KEY.getRole()))
				.to(TransformProvider.class);
	}

	/**
	 * Binds {@link TypeTool} to the {@link IDomain} adaptable scope.
	 */
	protected void bindTypeTool() {
		binder().bind(TypeTool.class).in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link TypeTool} to the {@link AdapterMap} binder for
	 * {@link IDomain}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IDomain} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindTypeToolAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(TypeTool.class);
	}

	/**
	 * Adds a binding for {@link ZoomOnPinchSpreadPolicy} to the
	 * {@link AdapterMap} binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindZoomOnPinchSpreadPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ZoomOnPinchSpreadPolicy.class);
	}

	@Override
	protected void configure() {
		// TODO: could rather install a module that is provided by
		// org.eclipse.gef.common.inject (which contains the enabling code)
		enableAdapterMapInjection();

		bindIUndoContext();
		bindIOperationHistory();

		bindIViewer();
		bindIDomain();
		bindIRootPart();

		// bind additional adapters for HistoricizingDomain
		bindIDomainAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), IDomain.class));

		// bind additional adapters for InfiniteCanvasViewer
		bindIViewerAdaptersForContentViewer(AdapterMaps.getAdapterMapBinder(
				binder(), IViewer.class, IDomain.CONTENT_VIEWER_ROLE));

		// bind adapters for RootPart
		bindIRootPartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), IRootPart.class));
		bindIRootPartAdaptersForContentViewer(AdapterMaps.getAdapterMapBinder(
				binder(), IRootPart.class, IDomain.CONTENT_VIEWER_ROLE));

		// bind visual part adapters
		bindAbstractVisualPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				AbstractVisualPart.class));
		bindAbstractContentPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractContentPart.class));
		bindAbstractFeedbackPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractFeedbackPart.class));
		bindAbstractHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				AbstractHandlePart.class));

		// bind default target policy resolver for the tools
		bindITargetPolicyResolver();

		// bind tools
		bindClickDragTool();
		bindHoverTool();
		bindPinchSpreadTool();
		bindRotateTool();
		bindScrollTool();
		bindTypeTool();
	}

	/**
	 * Installs an {@link AdapterInjectionSupport} module, which binds an
	 * {@link AdaptableTypeListener} and ensures it gets properly injected.
	 */
	protected void enableAdapterMapInjection() {
		install(new AdapterInjectionSupport());
	}

}
