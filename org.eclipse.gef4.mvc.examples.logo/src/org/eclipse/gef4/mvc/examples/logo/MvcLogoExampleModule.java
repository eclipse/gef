/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef4.common.adapt.inject.AdapterMap;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.examples.logo.parts.FXCreateCurveHoverHandlePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXDeleteHoverHandlePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoContentPartFactory;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoHoverHandlePartFactory;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.examples.logo.policies.CloneCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.CloneShapePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCloneOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreateCurveOnDragPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreationMenuItemProvider;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreationMenuOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXDeleteFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXRelocateLinkedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.behaviors.FXConnectionClickableAreaBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFocusFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRectangleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXSquareSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.FXBendConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXBendFirstAnchorageOnSegmentHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXBendOnSegmentDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXNormalizeConnectedOnDrag;
import org.eclipse.gef4.mvc.fx.policies.FXResizeConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTransformSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTranslateFirstAnchorageOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnRotatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectFocusedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTraverseFocusOnTypePolicy;
import org.eclipse.gef4.mvc.fx.providers.DynamicAnchorProvider;
import org.eclipse.gef4.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef4.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

import javafx.scene.Node;

public class MvcLogoExampleModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// select on click
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXFocusAndSelectOnClickPolicy.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXSelectFocusedOnTypePolicy.class);
	}

	@Override
	protected void bindAbstractRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXCreationMenuOnClickPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXCreationMenuOnClickPolicy.MENU_ITEM_PROVIDER_ROLE))
				.to(FXCreationMenuItemProvider.class);
		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXDeleteSelectedOnTypePolicy.class);
		// interaction policy to rotate selected through rotate gesture
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXRotateSelectedOnRotatePolicy.class);
		// keyboard focus traversal through key navigation
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTraverseFocusOnTypePolicy.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXSelectFocusedOnTypePolicy.class);
	}

	protected void bindFXCircleSegmentHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXBendFirstAnchorageOnSegmentHandleDragPolicy.class);
	}

	protected void bindFXCreateCurveHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXCreateCurveOnDragPolicy.class);
	}

	protected void bindFXDeleteHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXDeleteFirstAnchorageOnClickPolicy.class);
	}

	protected void bindFXGeometricCurvePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for focus feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// transaction policy for resize + transform
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXResizeConnectionPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXBendConnectionPolicy.class);

		// interaction policy to relocate on drag
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXTranslateSelectedOnDragPolicy.class);

		// drag individual segments
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXBendOnSegmentDragPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTransformConnectionPolicy.class);

		// cloning
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(CloneCurvePolicy.class);

		// clickable area resizing
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXConnectionClickableAreaBehavior.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXCloneOnClickPolicy.class);
	}

	/**
	 * Binds adapters for {@link FXGeometricModelPart}.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the bindings are added.
	 */
	protected void bindFXGeometricModelPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// geometry provider for focus feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	protected void bindFXGeometricShapePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for hover handles
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultHoverHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for focus feedback
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// register resize/transform policies (writing changes also to model)
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTransformPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXResizePolicy.class);

		// relocate on drag (including anchored elements, which are linked)
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXTranslateSelectedOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role("2"))
				.to(FXRelocateLinkedOnDragPolicy.class);

		// clone
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(CloneShapePolicy.class);

		// bind dynamic anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DynamicAnchorProvider.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXCloneOnClickPolicy.class);

		// normalize connected on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXNormalizeConnectedOnDrag.class);
	}

	/**
	 * Adds a binding for {@link FXPaletteViewer} to the {@link AdapterMap}
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
	protected void bindFXPaletteViewerAsFXDomainAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXPaletteViewer.class);
	}

	protected void bindFXRectangleSegmentHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXBendFirstAnchorageOnSegmentHandleDragPolicy.class);
	}

	protected void bindFXSquareSegmentHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// single selection: resize relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.role("0"))
				.to(FXResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.role("1"))
				.to(FXRotateSelectedOnHandleDragPolicy.class);

		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.role("2"))
				.to(FXResizeTransformSelectedOnHandleDragPolicy.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new FXLogoContentPartFactory());
	}

	@Override
	protected void bindIHandlePartFactories() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).annotatedWith(
				Names.named(SelectionBehavior.PART_FACTORIES_BINDING_NAME))
				.to(FXLogoSelectionHandlePartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).annotatedWith(Names.named(HoverBehavior.PART_FACTORIES_BINDING_NAME))
				.to(FXLogoHoverHandlePartFactory.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();
		bindFXPaletteViewerAsFXDomainAdapter(
				AdapterMaps.getAdapterMapBinder(binder(), FXDomain.class));

		// contents
		bindFXGeometricModelPartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricModelPart.class));
		bindFXGeometricShapePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricShapePart.class));
		bindFXGeometricCurvePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricCurvePart.class));

		// node selection handles and multi selection handles
		bindFXSquareSegmentHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXSquareSegmentHandlePart.class));

		// curve selection handles
		bindFXCircleSegmentHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXCircleSegmentHandlePart.class));
		bindFXRectangleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(),
						FXRectangleSegmentHandlePart.class));

		// hover handles
		bindFXDeleteHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXDeleteHoverHandlePart.class));
		bindFXCreateCurveHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXCreateCurveHoverHandlePart.class));
	}

}