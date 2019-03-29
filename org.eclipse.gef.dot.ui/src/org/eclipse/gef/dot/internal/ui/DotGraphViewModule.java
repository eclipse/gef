/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.ConnectionClickableAreaBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.FocusAndSelectOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.handlers.SelectFocusedOnTypeHandler;
import org.eclipse.gef.mvc.fx.handlers.TraverseFocusOnTypeHandler;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;
import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;
import org.eclipse.gef.zest.fx.parts.EdgePart;
import org.eclipse.gef.zest.fx.parts.GraphPart;
import org.eclipse.gef.zest.fx.parts.NodeLabelPart;
import org.eclipse.gef.zest.fx.parts.NodePart;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;
import org.eclipse.gef.zest.fx.parts.ZestFxRootPart;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;

public class DotGraphViewModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FocusAndSelectOnClickHandler.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SelectFocusedOnTypeHandler.class);
	}

	/**
	 * Adds (default) adapter map bindings for {@link EdgeLabelPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link EdgeLabelPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindEdgeLabelPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// selection link feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// selection feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// hover on-hover policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverHandler.class);
	}

	/**
	 * Adds (default) adapter map bindings for {@link EdgePart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link EdgePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindEdgePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DotEdgeLayoutBehavior.class);

		// selection link feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// selection feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// clickable area behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ConnectionClickableAreaBehavior.class);

		// hover on-hover policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverHandler.class);
	}

	/**
	 * Binds {@link IContentPartFactory} to {@link ZestFxContentPartFactory}.
	 */
	protected void bindIContentPartFactory() {
		binder().bind(IContentPartFactory.class).to(DotContentPartFactory.class)
				.in(AdaptableScopes.typed(IViewer.class));
	}

	@Override
	protected void bindIRootPartAdaptersForContentViewer(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIRootPartAdaptersForContentViewer(adapterMapBinder);

		// keyboard focus traversal
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(TraverseFocusOnTypeHandler.class);

		// select focused on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(SelectFocusedOnTypeHandler.class);
	}

	/**
	 * Adds (default) adapter map bindings for {@link NodeLabelPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link NodeLabelPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindNodeLabelPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// selection link feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// selection feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// hover on-hover policy
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverHandler.class);
	}

	/**
	 * Adds (default) adapter map bindings for {@link GraphPart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link GraphPart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindGraphPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(LayoutContext.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(GraphLayoutBehavior.class);
	}

	/**
	 * Adds (default) adapter map bindings for {@link NodePart} and all
	 * sub-classes. May be overwritten by sub-classes to change the default
	 * bindings.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} to be used for the binding registration.
	 *            In this case, will be obtained from
	 *            {@link AdapterMaps#getAdapterMapBinder(Binder, Class)} using
	 *            {@link NodePart} as a key.
	 *
	 * @see AdapterMaps#getAdapterMapBinder(Binder, Class)
	 */
	protected void bindNodePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DotNodeLayoutBehavior.class);

		// anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DotAnchorProvider.class);

		// selection feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// selection link feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// focus feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(
				DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover on-hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HoverOnHoverHandler.class);
	}

	@Override
	protected void bindRootPartAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.role(IDomain.CONTENT_VIEWER_ROLE))
				.to(ZestFxRootPart.class)
				.in(AdaptableScopes.typed(IViewer.class));
	}

	@Override
	protected void enableAdapterMapInjection() {
		install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		bindGraphPartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), GraphPart.class));
		bindNodePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), NodePart.class));
		bindEdgePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), EdgePart.class));
		bindEdgeLabelPartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), EdgeLabelPart.class));
		bindNodeLabelPartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), NodeLabelPart.class));
	}
}
