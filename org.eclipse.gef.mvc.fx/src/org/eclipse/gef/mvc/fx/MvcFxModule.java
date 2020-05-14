/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) -
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
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.fx.behaviors.ContentBehavior;
import org.eclipse.gef.mvc.fx.behaviors.ContentPartPool;
import org.eclipse.gef.mvc.fx.behaviors.FocusBehavior;
import org.eclipse.gef.mvc.fx.behaviors.GridBehavior;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.behaviors.HoverIntentBehavior;
import org.eclipse.gef.mvc.fx.behaviors.RevealPrimarySelectionBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SnappingBehavior;
import org.eclipse.gef.mvc.fx.domain.HistoricizingDomain;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.gestures.ClickDragGesture;
import org.eclipse.gef.mvc.fx.gestures.DefaultHandlerResolver;
import org.eclipse.gef.mvc.fx.gestures.HoverGesture;
import org.eclipse.gef.mvc.fx.gestures.IHandlerResolver;
import org.eclipse.gef.mvc.fx.gestures.PinchSpreadGesture;
import org.eclipse.gef.mvc.fx.gestures.RotateGesture;
import org.eclipse.gef.mvc.fx.gestures.ScrollGesture;
import org.eclipse.gef.mvc.fx.gestures.TypeStrokeGesture;
import org.eclipse.gef.mvc.fx.handlers.CursorSupport;
import org.eclipse.gef.mvc.fx.handlers.FocusAndSelectOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.handlers.MarqueeOnDragHandler;
import org.eclipse.gef.mvc.fx.handlers.ConnectedSupport;
import org.eclipse.gef.mvc.fx.handlers.PanOnStrokeHandler;
import org.eclipse.gef.mvc.fx.handlers.PanOrZoomOnScrollHandler;
import org.eclipse.gef.mvc.fx.handlers.PanningSupport;
import org.eclipse.gef.mvc.fx.handlers.SnapToSupport;
import org.eclipse.gef.mvc.fx.handlers.ZoomOnPinchSpreadHandler;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.AbstractFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.AbstractVisualPart;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverIntentHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSnappingFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.fx.parts.LayeredRootPart;
import org.eclipse.gef.mvc.fx.policies.ContentPolicy;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.DeletionPolicy;
import org.eclipse.gef.mvc.fx.policies.FocusTraversalPolicy;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.gef.mvc.fx.providers.TransformProvider;
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
	 * Adds (default) adapter map bindings for {@link AbstractContentPart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
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

		// register default policies
		bindContentPolicyAsAbstractContentPartAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) adapter map bindings for {@link AbstractFeedbackPart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
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
	 * Adds (default) adapter map bindings for {@link AbstractHandlePart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
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
		bindHoverOnHoverHandlerAsAbstractHandlePartAdapter(adapterMapBinder);
	}

	/**
	 * Adds (default) adapter map binding for {@link AbstractVisualPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
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
	 * Adds a binding for {@link ViewportPolicy} to the adapter map binder for
	 * {@link IRootPart}.
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
				.to(ViewportPolicy.class);
	}

	/**
	 * Binds {@link ClickDragGesture} to the {@link IDomain} adaptable scope.
	 */
	protected void bindClickDragGesture() {
		binder().bind(ClickDragGesture.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link ClickDragGesture} to the adapter map binder for
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
	protected void bindClickDragGestureAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ClickDragGesture.class);
	}

	/**
	 * Adds a binding for {@link ContentBehavior}, parameterized by {@link Node}
	 * , to the adapter map binder for {@link IRootPart}.
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
	 * Adds a binding for {@link IViewer} to the adapter map binder for
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
	protected void bindContentPartPoolAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ContentPartPool.class);
	}

	/**
	 * Adds a binding for {@link ContentPolicy}, parameterized by {@link Node} ,
	 * to the adapter map binder for {@link AbstractContentPart}.
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
	 * Adds a binding for {@link CreationPolicy} to the adapter map binder for
	 * {@link IRootPart}.
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
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to register adapter
	 *            bindings.
	 */
	protected void bindCursorSupportAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(CursorSupport.class);
	}

	/**
	 * Adds a binding for {@link DeletionPolicy} to the adapter map binder for
	 * {@link IRootPart}.
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
	 * Adds a binding for {@link FocusAndSelectOnClickHandler} to the adapter
	 * map binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindFocusAndSelectOnClickHandlerAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusAndSelectOnClickHandler.class);
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
	 * the adapter map binder for {@link IViewer}.
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
	 * Adds a binding for {@link FocusTraversalPolicy} to the adapter map binder
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
	protected void bindFocusTraversalPolicyAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusTraversalPolicy.class);
	}

	/**
	 * Adds a binding for {@link GridBehavior} to the adapter map binder for
	 * {@link IRootPart}.
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
	 * Adds a binding for {@link GridModel} to the adapter map binder for
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
	 * Adds a binding for {@link HoverBehavior}, parameterized by {@link Node},
	 * to the adapter map binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverBehavior.class);
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
	 * Binds {@link HoverGesture} to the {@link IDomain} adaptable scope.
	 */
	protected void bindHoverGesture() {
		binder().bind(HoverGesture.class);
	}

	/**
	 * Adds a binding for {@link HoverGesture} to the adapter map binder for
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
	protected void bindHoverGestureAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverGesture.class);
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
						.role(HoverIntentBehavior.HOVER_INTENT_HANDLE_PART_FACTORY))
				.to(DefaultHoverIntentHandlePartFactory.class);
	}

	/**
	 * Adds a binding for {@link HoverIntentBehavior}, parameterized by
	 * {@link Node}, to the adapter map binder for {@link IRootPart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link IRootPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverIntentBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverIntentBehavior.class);
	}

	/**
	 * Adds a binding for {@link HoverModel}, parameterized by {@link Node}, to
	 * the adapter map binder for {@link IViewer}.
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
	 * Adds a binding for {@link HoverOnHoverHandler} to the adapter map binder
	 * for {@link AbstractHandlePart}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link AbstractHandlePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindHoverOnHoverHandlerAsAbstractHandlePartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverHandler.class);
	}

	/**
	 * Adds a binding for {@link HoverOnHoverHandler} to the adapter map binder
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
	protected void bindHoverOnHoverHandlerAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverHandler.class);
	}

	/**
	 * Binds {@link IContentPartFactory} as an adapter for the content viewer.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for the content viewer.
	 */
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
	 * Adds (default) adapter map bindings for {@link IDomain} and all
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
		bindHoverGestureAsDomainAdapter(adapterMapBinder);
		bindClickDragGestureAsDomainAdapter(adapterMapBinder);
		bindTypeGestureAsDomainAdapter(adapterMapBinder);
		bindRotateGestureAsDomainAdapter(adapterMapBinder);
		bindPinchSpreadGestureAsIDomainAdapter(adapterMapBinder);
		bindScrollGestureAsDomainAdapter(adapterMapBinder);
		bindContentIViewerAsIDomainAdapter(adapterMapBinder);
		bindIHandlerResolverAsIDomainAdapter(adapterMapBinder);
	}

	/**
	 * Binds {@link DefaultHandlerResolver} to {@link IHandlerResolver} in
	 * adaptable scope of {@link IDomain}.
	 */
	protected void bindIHandlerResolver() {
		binder().bind(IHandlerResolver.class).to(DefaultHandlerResolver.class);
	}

	/**
	 * Binds {@link DefaultHandlerResolver} as a domain adapter.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to add the binding.
	 */
	protected void bindIHandlerResolverAsIDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(IHandlerResolver.class)
				.in(AdaptableScopes.typed(IDomain.class));
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
	 * Adds (default) adapter map bindings for "content" {@link IRootPart} and
	 * all sub-classes. May be overwritten by sub-classes to change the default
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
	protected void bindIRootPartAdaptersForContentViewer(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		bindFocusAndSelectOnClickHandlerAsIRootPartAdapter(adapterMapBinder);
		bindMarqueeOnDragHandlerAsIRootPartAdapter(adapterMapBinder);
		bindHoverOnHoverHandlerAsIRootPartAdapter(adapterMapBinder);
		bindPanOrZoomOnScrollHandlerAsIRootPartAdapter(adapterMapBinder);
		bindZoomOnPinchSpreadHandlerAsIRootPartAdapter(adapterMapBinder);
		bindPanOnTypeHandlerAsIRootPartAdapter(adapterMapBinder);
		// register change viewport policy
		bindChangeViewportPolicyAsIRootPartAdapter(adapterMapBinder);
		// register default behaviors
		bindContentBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindHoverBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindHoverIntentBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindSelectionBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindRevealPrimarySelectionBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindGridBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindFocusBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindSnappingBehaviorAsIRootPartAdapter(adapterMapBinder);
		// creation and deletion policy
		bindCreationPolicyAsIRootPartAdapter(adapterMapBinder);
		bindDeletionPolicyAsIRootPartAdapter(adapterMapBinder);
		// bind focus traversal policy
		bindFocusTraversalPolicyAsIRootPartAdapter(adapterMapBinder);
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
	 * Adds (default) adapter map bindings for {@link IViewer} and all
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
		bindContentPartPoolAsContentViewerAdapter(adapterMapBinder);

		bindGridModelAsContentViewerAdapter(adapterMapBinder);
		bindFocusModelAsContentViewerAdapter(adapterMapBinder);
		bindHoverModelAsContentViewerAdapter(adapterMapBinder);
		bindSelectionModelAsContentViewerAdapter(adapterMapBinder);
		bindSnappingModelAsContentViewerAdapter(adapterMapBinder);

		bindRootPartAsContentViewerAdapter(adapterMapBinder);

		bindFocusFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindHoverFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSelectionFeedbackPartFactoryAsContentViewerAdapter(
				adapterMapBinder);
		bindHoverHandlePartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSelectionHandlePartFactoryAsContentViewerAdapter(adapterMapBinder);
		bindSnappingFeedbackPartFactoryAsContentViewerAdapter(adapterMapBinder);

		bindCursorSupportAsContentViewerAdapter(adapterMapBinder);
		bindPanningSupportAsContentViewerAdapter(adapterMapBinder);
		bindSnapToSupportAsContentViewerAdapter(adapterMapBinder);
		bindConnectedSupportAsContentViewerAdapter(adapterMapBinder);
	}

	/**
	 * Adds a binding for {@link MarqueeOnDragHandler} to the adapter map binder
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
	protected void bindMarqueeOnDragHandlerAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(MarqueeOnDragHandler.class);
	}

	/**
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to register adapter
	 *            bindings.
	 */
	protected void bindConnectedSupportAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ConnectedSupport.class);
	}

	/**
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to register adapter
	 *            bindings.
	 */
	protected void bindPanningSupportAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(PanningSupport.class);
	}

	/**
	 * Adds a binding for {@link PanOnStrokeHandler} to the adapter map binder
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
	protected void bindPanOnTypeHandlerAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(PanOnStrokeHandler.class);
	}

	/**
	 * Adds a binding for {@link PanOrZoomOnScrollHandler} to the adapter map
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
	protected void bindPanOrZoomOnScrollHandlerAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("panOnScroll"))
				.to(PanOrZoomOnScrollHandler.class);
	}

	/**
	 * Binds {@link PinchSpreadGesture} to the {@link IDomain} adaptable scope.
	 */
	protected void bindPinchSpreadGesture() {
		binder().bind(PinchSpreadGesture.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link PinchSpreadGesture} to the adapter map binder
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
	protected void bindPinchSpreadGestureAsIDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(PinchSpreadGesture.class);
	}

	/**
	 * Adds a binding for {@link RevealPrimarySelectionBehavior}, parameterized
	 * by {@link Node}, to the adapter map binder for {@link IRootPart}.
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
	 * the adapter map binder for {@link IViewer}.
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(IRootPart.class).in(AdaptableScopes.typed(IViewer.class));
	}

	/**
	 * Binds {@link RotateGesture} to the {@link IDomain} adaptable scope.
	 */
	protected void bindRotateGesture() {
		binder().bind(RotateGesture.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link RotateGesture} to the adapter map binder for
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
	protected void bindRotateGestureAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(RotateGesture.class);
	}

	/**
	 * Binds {@link ScrollGesture} to the {@link IDomain} adaptable scope.
	 */
	protected void bindScrollGesture() {
		binder().bind(ScrollGesture.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link ScrollGesture} to the adapter map binder for
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
	protected void bindScrollGestureAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ScrollGesture.class);
	}

	/**
	 * Adds a binding for {@link SelectionBehavior}, parameterized by
	 * {@link Node}, to the adapter map binder for {@link IRootPart}.
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
	 * to the adapter map binder for {@link IViewer}.
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
	 * Adds a binding for the {@link SnappingBehavior} to the given adapter map
	 * binder.
	 *
	 * @param adapterMapBinder
	 *            An adapter map binder for {@link IRootPart}.
	 */
	protected void bindSnappingBehaviorAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SnappingBehavior.class);
	}

	/**
	 * Binds the {@link IFeedbackPartFactory} that is used to generate snapping
	 * feedback.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} for content viewer adapters.
	 */
	protected void bindSnappingFeedbackPartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.role(SnappingBehavior.SNAPPING_FEEDBACK_PART_FACTORY))
				.to(DefaultSnappingFeedbackPartFactory.class);
	}

	/**
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to register adapter
	 *            bindings.
	 */
	protected void bindSnappingModelAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SnappingModel.class);
	}

	/**
	 * @param adapterMapBinder
	 *            The {@link MapBinder} that is used to register adapter
	 *            bindings.
	 */
	protected void bindSnapToSupportAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SnapToSupport.class);
	}

	/**
	 * Adds a binding for {@link TransformProvider} to the adapter map binder
	 * for {@link AbstractContentPart}, using the
	 * {@link ITransformableContentPart#TRANSFORM_PROVIDER_KEY}.
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
		adapterMapBinder.addBinding(AdapterKey.role(
				ITransformableContentPart.TRANSFORM_PROVIDER_KEY.getRole()))
				.to(TransformProvider.class);
	}

	/**
	 * Binds {@link TypeStrokeGesture} to the {@link IDomain} adaptable scope.
	 */
	protected void bindTypeGesture() {
		binder().bind(TypeStrokeGesture.class)
				.in(AdaptableScopes.typed(IDomain.class));
	}

	/**
	 * Adds a binding for {@link TypeStrokeGesture} to the adapter map binder
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
	protected void bindTypeGestureAsDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(TypeStrokeGesture.class);
	}

	/**
	 * Adds a binding for {@link ZoomOnPinchSpreadHandler} to the adapter map
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
	protected void bindZoomOnPinchSpreadHandlerAsIRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ZoomOnPinchSpreadHandler.class);
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
				binder(), IViewer.class,
				AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));

		// bind adapters for RootPart
		bindIRootPartAdaptersForContentViewer(AdapterMaps.getAdapterMapBinder(
				binder(), IRootPart.class,
				AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));

		// bind visual part adapters
		bindAbstractVisualPartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				AbstractVisualPart.class));
		bindAbstractContentPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractContentPart.class));
		bindAbstractFeedbackPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), AbstractFeedbackPart.class));
		bindAbstractHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				AbstractHandlePart.class));

		// bind default target policy resolver for the gestures
		bindIHandlerResolver();

		// bind gestures
		bindClickDragGesture();
		bindHoverGesture();
		bindPinchSpreadGesture();
		bindRotateGesture();
		bindScrollGesture();
		bindTypeGesture();
	}

	/**
	 * Installs an {@link AdapterInjectionSupport} module, which binds an
	 * {@link AdaptableTypeListener} and ensures it gets properly injected.
	 */
	protected void enableAdapterMapInjection() {
		install(new AdapterInjectionSupport());
	}

}
