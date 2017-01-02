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
package org.eclipse.gef.mvc.examples.logo;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdaptableScopes;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport;
import org.eclipse.gef.common.adapt.inject.AdapterInjectionSupport.LoggingMode;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.examples.logo.behaviors.PaletteFocusBehavior;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricCurveCreationHoverHandlePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricCurvePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricElementDeletionHandlePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricModelPart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricShapePart;
import org.eclipse.gef.mvc.examples.logo.parts.MvcLogoExampleContentPartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.MvcLogoExampleHoverHandlePartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.MvcLogoExampleSelectionHandlePartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.PaletteRootPart;
import org.eclipse.gef.mvc.examples.logo.policies.CloneCurvePolicy;
import org.eclipse.gef.mvc.examples.logo.policies.CloneShapePolicy;
import org.eclipse.gef.mvc.examples.logo.policies.ContentRestrictedChangeViewportPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.CreateAndTranslateShapeOnDragPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.CreationMenuOnClickPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXCloneOnClickPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXCreateCurveOnDragPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXCreationMenuItemProvider;
import org.eclipse.gef.mvc.examples.logo.policies.FXDeleteFirstAnchorageOnClickPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXRelocateLinkedOnDragPolicy;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.ConnectionClickableAreaBehavior;
import org.eclipse.gef.mvc.fx.behaviors.ContentPartPool;
import org.eclipse.gef.mvc.fx.behaviors.FocusBehavior;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.CircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.RectangleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.SquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.BendConnectionPolicy;
import org.eclipse.gef.mvc.fx.policies.BendFirstAnchorageOnSegmentHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.BendOnSegmentDragPolicy;
import org.eclipse.gef.mvc.fx.policies.DeleteSelectedOnTypePolicy;
import org.eclipse.gef.mvc.fx.policies.FocusAndSelectOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.HoverOnHoverPolicy;
import org.eclipse.gef.mvc.fx.policies.NormalizeConnectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.policies.ResizeTransformSelectedOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.ResizeTranslateFirstAnchorageOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.RotateSelectedOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.RotateSelectedOnRotatePolicy;
import org.eclipse.gef.mvc.fx.policies.SelectFocusedOnTypePolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.policies.TranslateSelectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.TraverseFocusOnTypePolicy;
import org.eclipse.gef.mvc.fx.providers.DefaultAnchorProvider;
import org.eclipse.gef.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusAndSelectOnClickPolicy.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SelectFocusedOnTypePolicy.class);
	}

	protected void bindCircleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendFirstAnchorageOnSegmentHandleDragPolicy.class);
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCreateCurveOnDragPolicy.class);
	}

	protected void bindDeleteHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXDeleteFirstAnchorageOnClickPolicy.class);
	}

	protected void bindFocusFeedbackFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FocusBehavior.FOCUS_FEEDBACK_PART_FACTORY))
				.to(DefaultFocusFeedbackPartFactory.class);
	}

	protected void bindFocusModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusModel.class);
	}

	/**
	 * Binds adapters for {@link GeometricModelPart}.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the bindings are added.
	 */
	protected void bindFXGeometricModelPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendFirstAnchorageOnSegmentHandleDragPolicy.class);
	}

	protected void bindFXSquareSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// single selection: resize relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(RotateSelectedOnHandleDragPolicy.class);

		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizeTransformSelectedOnHandleDragPolicy.class);
	}

	protected void bindGeometricCurvePartAdaptersInContentViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverPolicy.class);

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

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendConnectionPolicy.class);

		// interaction policy to relocate on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedOnDragPolicy.class);

		// drag individual segments
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendOnSegmentDragPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TransformPolicy.class);

		// cloning
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneCurvePolicy.class);

		// clickable area resizing
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ConnectionClickableAreaBehavior.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCloneOnClickPolicy.class);
	}

	protected void bindGeometricShapePartAdapterInPaletteViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateAndTranslateShapeOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
	}

	protected void bindGeometricShapePartAdaptersInContentViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverPolicy.class);

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
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER))
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TranslateSelectedOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXRelocateLinkedOnDragPolicy.class);

		// clone
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneShapePolicy.class);

		// bind dynamic anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DefaultAnchorProvider.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCloneOnClickPolicy.class);

		// normalize connected on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NormalizeConnectedOnDragPolicy.class);
	}

	protected void bindHoverFeedbackFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_FEEDBACK_PART_FACTORY))
				.to(DefaultHoverFeedbackPartFactory.class);
	}

	protected void bindHoverHandleFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY))
				.to(DefaultHoverHandlePartFactory.class);
	}

	@Override
	protected void bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY))
				.to(MvcLogoExampleHoverHandlePartFactory.class);
	}

	protected void bindHoverModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverModel.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(IContentPartFactory.class).toInstance(new MvcLogoExampleContentPartFactory());
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreationMenuOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role(CreationMenuOnClickPolicy.MENU_ITEM_PROVIDER_ROLE))
				.to(FXCreationMenuItemProvider.class);
		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DeleteSelectedOnTypePolicy.class);
		// interaction policy to rotate selected through rotate gesture
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(RotateSelectedOnRotatePolicy.class);
		// keyboard focus traversal through key navigation
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(TraverseFocusOnTypePolicy.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SelectFocusedOnTypePolicy.class);
		// hover behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverBehavior.class);
	}

	protected void bindPaletteFocusBehaviorAsFXRootPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(PaletteFocusBehavior.class);
	}

	protected void bindPaletteRootPartAdaptersInPaletteViewerContext(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {

		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		bindHoverOnHoverPolicyAsIRootPartAdapter(adapterMapBinder);
		bindPanOrZoomOnScrollPolicyAsIRootPartAdapter(adapterMapBinder);
		bindPanOnTypePolicyAsIRootPartAdapter(adapterMapBinder);
		// register change viewport policy
		bindContentRestrictedChangeViewportPolicyAsFXRootPartAdapter(adapterMapBinder);
		// register default behaviors
		bindContentBehaviorAsIRootPartAdapter(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverBehavior.class);
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(BendFirstAnchorageOnSegmentHandleDragPolicy.class);
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
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(RotateSelectedOnHandleDragPolicy.class);

		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizeTransformSelectedOnHandleDragPolicy.class);
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
		bindFXGeometricModelPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), GeometricModelPart.class));

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