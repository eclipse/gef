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
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo;

import java.util.Arrays;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.examples.logo.behaviors.PaletteFocusBehavior;
import org.eclipse.gef.mvc.examples.logo.handlers.CloneCurveSupport;
import org.eclipse.gef.mvc.examples.logo.handlers.CloneOnClickHandler;
import org.eclipse.gef.mvc.examples.logo.handlers.CloneShapeSupport;
import org.eclipse.gef.mvc.examples.logo.handlers.CreateAndTranslateShapeOnDragHandler;
import org.eclipse.gef.mvc.examples.logo.handlers.CreateCurveOnDragHandler;
import org.eclipse.gef.mvc.examples.logo.handlers.CreationMenuItemProvider;
import org.eclipse.gef.mvc.examples.logo.handlers.CreationMenuOnClickHandler;
import org.eclipse.gef.mvc.examples.logo.handlers.DeleteFirstAnchorageOnClickHandler;
import org.eclipse.gef.mvc.examples.logo.handlers.RelocateLinkedOnDragHandler;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricCurveCreationHoverHandlePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricCurvePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricElementDeletionHandlePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricShapePart;
import org.eclipse.gef.mvc.examples.logo.parts.MvcLogoExampleContentPartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.MvcLogoExampleHoverHandlePartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.MvcLogoExampleSelectionHandlePartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.PaletteRootPart;
import org.eclipse.gef.mvc.examples.logo.policies.ContentRestrictedChangeViewportPolicy;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.ContentPartPool;
import org.eclipse.gef.mvc.fx.behaviors.FocusBehavior;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.behaviors.HoverIntentBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.ConnectedSupport;
import org.eclipse.gef.mvc.fx.handlers.DeleteSelectedOnTypeHandler;
import org.eclipse.gef.mvc.fx.handlers.FocusAndSelectOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.handlers.ResizeTransformSelectedOnHandleDragHandler;
import org.eclipse.gef.mvc.fx.handlers.ResizeTranslateFirstAnchorageOnHandleDragHandler;
import org.eclipse.gef.mvc.fx.handlers.RotateSelectedOnHandleDragHandler;
import org.eclipse.gef.mvc.fx.handlers.RotateSelectedOnRotateHandler;
import org.eclipse.gef.mvc.fx.handlers.SelectAllOnTypeHandler;
import org.eclipse.gef.mvc.fx.handlers.SelectFocusedOnTypeHandler;
import org.eclipse.gef.mvc.fx.handlers.SnapToGeometry;
import org.eclipse.gef.mvc.fx.handlers.SnapToGrid;
import org.eclipse.gef.mvc.fx.handlers.TranslateSelectedOnDragHandler;
import org.eclipse.gef.mvc.fx.handlers.TraverseFocusOnTypeHandler;
import org.eclipse.gef.mvc.fx.internal.behaviors.BendConnectionPolicyEx;
import org.eclipse.gef.mvc.fx.internal.behaviors.BendableCurveClickableAreaBehavior;
import org.eclipse.gef.mvc.fx.internal.handlers.BendFirstAnchorageOnSegmentHandleDragHandlerEx;
import org.eclipse.gef.mvc.fx.internal.handlers.BendOnSegmentDragHandlerEx;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.CircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverIntentHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.RectangleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.SquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.providers.BoundsSnappingLocationProvider;
import org.eclipse.gef.mvc.fx.providers.CenterSnappingLocationProvider;
import org.eclipse.gef.mvc.fx.providers.DefaultAnchorProvider;
import org.eclipse.gef.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef.mvc.fx.providers.ISnappingLocationProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.providers.TopLeftSnappingLocationProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.paint.Color;

public class MvcLogoExampleModule extends MvcFxModule {

	public static final String PALETTE_VIEWER_ROLE = "paletteViewer";

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// select on click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusAndSelectOnClickHandler.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SelectFocusedOnTypeHandler.class);
	}

	protected void bindCircleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendFirstAnchorageOnSegmentHandleDragHandlerEx.class);
	}

	protected void bindContentPartPoolAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ContentPartPool.class);
	}

	/**
	 * Registers the {@link ContentRestrictedChangeViewportPolicy} as an adapter
	 * at the given {@link MapBinder}.
	 *
	 * @param adapterMapBinder
	 *            The {@link MapBinder} where the
	 *            {@link ContentRestrictedChangeViewportPolicy} is registered.
	 */
	protected void bindContentRestrictedChangeViewportPolicyAsFXRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ContentRestrictedChangeViewportPolicy.class);
	}

	protected void bindCreateCurveHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateCurveOnDragHandler.class);
	}

	protected void bindDeleteHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DeleteFirstAnchorageOnClickHandler.class);
	}

	protected void bindFocusFeedbackFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FocusBehavior.FOCUS_FEEDBACK_PART_FACTORY))
				.to(DefaultFocusFeedbackPartFactory.class);
	}

	protected void bindFocusModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusModel.class);
	}

	/**
	 * Adds a binding for {@link FXPaletteViewer} to the {@link AdapterMap}
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
	protected void bindFXPaletteViewerAsFXDomainAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(PALETTE_VIEWER_ROLE)).to(IViewer.class);
	}

	protected void bindFXRectangleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendFirstAnchorageOnSegmentHandleDragHandlerEx.class);
	}

	@SuppressWarnings("restriction")
	protected void bindGeometricCurvePartAdaptersInContentViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		adapterMapBinder
				.addBinding(
						AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// transaction policy for resize + transform
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizePolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendConnectionPolicyEx.class);

		// interaction handler to relocate on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedOnDragHandler.class);

		// drag individual segments
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendOnSegmentDragHandlerEx.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);

		// cloning
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneCurveSupport.class);

		// clickable area resizing
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendableCurveClickableAreaBehavior.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneOnClickHandler.class);
	}

	protected void bindGeometricShapePartAdapterInPaletteViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateAndTranslateShapeOnDragHandler.class);
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
	}

	protected void bindGeometricShapePartAdaptersInContentViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});
		adapterMapBinder
				.addBinding(
						AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for hover handles
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultHoverIntentHandlePartFactory.HOVER_INTENT_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// register resize/transform policies (writing changes also to model)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizePolicy.class);

		// relocate on drag (including anchored elements, which are linked)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(RelocateLinkedOnDragHandler.class);

		// clone
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneShapeSupport.class);

		// bind dynamic anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DefaultAnchorProvider.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneOnClickHandler.class);

		// normalize connected on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ConnectedSupport.class);

		adapterMapBinder.addBinding(AdapterKey.role(SnapToGrid.SOURCE_SNAPPING_LOCATION_PROVIDER))
				.to(TopLeftSnappingLocationProvider.class);
		adapterMapBinder.addBinding(AdapterKey.role(SnapToGeometry.SOURCE_SNAPPING_LOCATION_PROVIDER))
				.toInstance(ISnappingLocationProvider.union(
						Arrays.asList(new CenterSnappingLocationProvider(), new BoundsSnappingLocationProvider())));
		adapterMapBinder.addBinding(AdapterKey.role(SnapToGeometry.TARGET_SNAPPING_LOCATION_PROVIDER))
				.to(BoundsSnappingLocationProvider.class);
	}

	protected void bindHoverFeedbackFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_FEEDBACK_PART_FACTORY))
				.to(DefaultHoverFeedbackPartFactory.class);
	}

	protected void bindHoverHandleFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverIntentBehavior.HOVER_INTENT_HANDLE_PART_FACTORY))
				.to(DefaultHoverIntentHandlePartFactory.class);
	}

	@Override
	protected void bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverIntentBehavior.HOVER_INTENT_HANDLE_PART_FACTORY))
				.to(MvcLogoExampleHoverHandlePartFactory.class);
	}

	protected void bindHoverModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverModel.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(IContentPartFactory.class).to(MvcLogoExampleContentPartFactory.class);
	}

	protected void bindIContentPartFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(IContentPartFactory.class);
	}

	@Override
	protected void bindIDomainAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIDomainAdapters(adapterMapBinder);
		bindPaletteViewerAsDomainAdapter(adapterMapBinder);
	}

	@Override
	protected void bindIRootPartAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindIRootPartAdaptersForContentViewer(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreationMenuOnClickHandler.class);
		adapterMapBinder.addBinding(AdapterKey.role(CreationMenuOnClickHandler.MENU_ITEM_PROVIDER_ROLE))
				.to(CreationMenuItemProvider.class);
		// interaction handler to delete on key type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DeleteSelectedOnTypeHandler.class);
		// interaction handler to rotate selected through rotate gesture
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(RotateSelectedOnRotateHandler.class);
		// keyboard focus traversal through key navigation
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TraverseFocusOnTypeHandler.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SelectFocusedOnTypeHandler.class);
		// select-all on type
		bindSelectAllOnTypeHandlerAsContentViewerRootPartAdapter(adapterMapBinder);
	}

	protected void bindPaletteFocusBehaviorAsFXRootPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(PaletteFocusBehavior.class);
	}

	protected void bindPaletteRootPartAdaptersInPaletteViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		bindHoverOnHoverHandlerAsIRootPartAdapter(adapterMapBinder);
		bindPanOrZoomOnScrollHandlerAsIRootPartAdapter(adapterMapBinder);
		bindPanOnTypeHandlerAsIRootPartAdapter(adapterMapBinder);
		// register change viewport policy
		bindContentRestrictedChangeViewportPolicyAsFXRootPartAdapter(adapterMapBinder);
		// register default behaviors
		bindContentBehaviorAsIRootPartAdapter(adapterMapBinder);
		bindHoverBehaviorAsIRootPartAdapter(adapterMapBinder);
		// XXX: PaletteFocusBehavior only changes the viewer focus and default
		// styles.
		bindPaletteFocusBehaviorAsFXRootPartAdapter(adapterMapBinder);
		// bind focus traversal policy
		bindFocusTraversalPolicyAsIRootPartAdapter(adapterMapBinder);
		// hover behavior
	}

	protected void bindPaletteRootPartAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(PALETTE_VIEWER_ROLE)).to(PaletteRootPart.class)
				.in(AdaptableScopes.typed(IViewer.class));
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
	protected void bindPaletteViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// viewer models
		bindFocusModelAsPaletteViewerAdapter(adapterMapBinder);
		bindHoverModelAsPaletteViewerAdapter(adapterMapBinder);
		bindSelectionModelAsPaletteViewerAdapter(adapterMapBinder);

		// root part
		bindPaletteRootPartAsPaletteViewerAdapter(adapterMapBinder);

		// feedback and handles factories
		bindSelectionFeedbackFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindFocusFeedbackFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindHoverFeedbackFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindSelectionHandleFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindHoverHandleFactoryAsPaletteViewerAdapter(adapterMapBinder);

		// content part factory and content part pool
		bindContentPartPoolAsPaletteViewerAdapter(adapterMapBinder);
		bindIContentPartFactoryAsPaletteViewerAdapter(adapterMapBinder);

		// change hover feedback color by binding a respective provider
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_COLOR_PROVIDER))
				.toInstance(new Provider<Color>() {
					@Override
					public Color get() {
						return Color.WHITE;
					}
				});
	}

	/**
	 * Adds a binding for {@link FXPaletteViewer} to the {@link AdapterMap}
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
	protected void bindPaletteViewerAsDomainAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(PALETTE_VIEWER_ROLE)).to(IViewer.class);
	}

	protected void bindRectangleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendFirstAnchorageOnSegmentHandleDragHandlerEx.class);
	}

	protected void bindSelectAllOnTypeHandlerAsContentViewerRootPartAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SelectAllOnTypeHandler.class);
	}

	protected void bindSelectionFeedbackFactoryAsPaletteViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_FEEDBACK_PART_FACTORY))
				.to(DefaultSelectionFeedbackPartFactory.class);
	}

	protected void bindSelectionHandleFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY))
				.to(DefaultSelectionHandlePartFactory.class);
	}

	@Override
	protected void bindSelectionHandlePartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY))
				.to(MvcLogoExampleSelectionHandlePartFactory.class);
	}

	protected void bindSelectionModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SelectionModel.class);
	}

	protected void bindSquareSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// single selection: resize relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ResizeTranslateFirstAnchorageOnHandleDragHandler.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(RotateSelectedOnHandleDragHandler.class);

		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizeTransformSelectedOnHandleDragHandler.class);
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		// content viewer
		bindGeometricShapePartAdaptersInContentViewerContext(AdapterMaps.getAdapterMapBinder(binder(),
				GeometricShapePart.class, AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));
		bindGeometricCurvePartAdaptersInContentViewerContext(AdapterMaps.getAdapterMapBinder(binder(),
				GeometricCurvePart.class, AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE)));

		// node selection handles and multi selection handles
		bindSquareSegmentHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), SquareSegmentHandlePart.class));

		// curve selection handles
		bindCircleSegmentHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), CircleSegmentHandlePart.class));

		bindRectangleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), RectangleSegmentHandlePart.class));

		// hover handles
		bindDeleteHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), GeometricElementDeletionHandlePart.class));
		bindCreateCurveHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), GeometricCurveCreationHoverHandlePart.class));

		// palette
		bindPaletteViewerAdapters(AdapterMaps.getAdapterMapBinder(binder(), IViewer.class,
				AdapterKey.get(IViewer.class, PALETTE_VIEWER_ROLE)));
		bindPaletteRootPartAdaptersInPaletteViewerContext(AdapterMaps.getAdapterMapBinder(binder(), IRootPart.class,
				AdapterKey.get(IViewer.class, PALETTE_VIEWER_ROLE)));
		bindGeometricShapePartAdapterInPaletteViewerContext(AdapterMaps.getAdapterMapBinder(binder(),
				GeometricShapePart.class, AdapterKey.get(IViewer.class, PALETTE_VIEWER_ROLE)));
	}

	@Override
	protected void enableAdapterMapInjection() {
		install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
	}

}