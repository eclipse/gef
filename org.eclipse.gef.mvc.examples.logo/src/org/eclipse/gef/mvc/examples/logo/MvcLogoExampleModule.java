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
import org.eclipse.gef.common.adapt.inject.AdapterMap;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.examples.logo.behaviors.PaletteFocusBehavior;
import org.eclipse.gef.mvc.examples.logo.parts.FXCreateCurveHoverHandlePart;
import org.eclipse.gef.mvc.examples.logo.parts.FXDeleteHoverHandlePart;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef.mvc.examples.logo.parts.FXLogoContentPartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.FXLogoHoverHandlePartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.FXLogoPaletteContentPartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.FXLogoSelectionHandlePartFactory;
import org.eclipse.gef.mvc.examples.logo.parts.PaletteElementPart;
import org.eclipse.gef.mvc.examples.logo.parts.PaletteModelPart;
import org.eclipse.gef.mvc.examples.logo.policies.CloneCurvePolicy;
import org.eclipse.gef.mvc.examples.logo.policies.CloneShapePolicy;
import org.eclipse.gef.mvc.examples.logo.policies.ContentRestrictedChangeViewportPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.CreateAndTranslateOnDragPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXCloneOnClickPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXCreateCurveOnDragPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXCreationMenuItemProvider;
import org.eclipse.gef.mvc.examples.logo.policies.FXDeleteFirstAnchorageOnClickPolicy;
import org.eclipse.gef.mvc.examples.logo.policies.FXRelocateLinkedOnDragPolicy;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.FXConnectionClickableAreaBehavior;
import org.eclipse.gef.mvc.fx.behaviors.FXFocusBehavior;
import org.eclipse.gef.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.FXDefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultHoverHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.FXRectangleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.FXRootPart;
import org.eclipse.gef.mvc.fx.parts.FXSquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.FXBendConnectionPolicy;
import org.eclipse.gef.mvc.fx.policies.FXBendFirstAnchorageOnSegmentHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXBendOnSegmentDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXCreationMenuOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef.mvc.fx.policies.FXNormalizeConnectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizeConnectionPolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizeTransformSelectedOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizeTranslateFirstAnchorageOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXRotateSelectedOnRotatePolicy;
import org.eclipse.gef.mvc.fx.policies.FXSelectFocusedOnTypePolicy;
import org.eclipse.gef.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXTraverseFocusOnTypePolicy;
import org.eclipse.gef.mvc.fx.providers.DefaultAnchorProvider;
import org.eclipse.gef.mvc.fx.providers.GeometricOutlineProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.ContentModel;
import org.eclipse.gef.mvc.models.FocusModel;
import org.eclipse.gef.mvc.models.HoverModel;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPartFactory;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class MvcLogoExampleModule extends MvcFxModule {

	public static final String PALETTE_VIEWER_ROLE = "paletteViewer";

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// select on click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXFocusAndSelectOnClickPolicy.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXSelectFocusedOnTypePolicy.class);
	}

	protected void bindContentModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ContentModel.class);
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

	@Override
	protected void bindContentViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindContentViewerAdapters(adapterMapBinder);
		// bind content part factory
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXLogoContentPartFactory.class);
	}

	@Override
	protected void bindContentViewerRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindContentViewerRootPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCreationMenuOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role(FXCreationMenuOnClickPolicy.MENU_ITEM_PROVIDER_ROLE))
				.to(FXCreationMenuItemProvider.class);
		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXDeleteSelectedOnTypePolicy.class);
		// interaction policy to rotate selected through rotate gesture
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXRotateSelectedOnRotatePolicy.class);
		// keyboard focus traversal through key navigation
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTraverseFocusOnTypePolicy.class);
		// select on type
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXSelectFocusedOnTypePolicy.class);
		// hover behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverBehavior.class);
	}

	protected void bindFocusFeedbackFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(FXFocusBehavior.FOCUS_FEEDBACK_PART_FACTORY))
				.to(FXDefaultFocusFeedbackPartFactory.class);
	}

	@SuppressWarnings("serial")
	protected void bindFocusModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<FocusModel<Node>>() {
		})).to(new TypeLiteral<FocusModel<Node>>() {
		});
	}

	protected void bindFXCircleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXBendFirstAnchorageOnSegmentHandleDragPolicy.class);
	}

	protected void bindFXCreateCurveHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCreateCurveOnDragPolicy.class);
	}

	protected void bindFXDeleteHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXDeleteFirstAnchorageOnClickPolicy.class);
	}

	@Override
	protected void bindFXDomainAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindFXDomainAdapters(adapterMapBinder);
		bindFXPaletteViewerAsFXDomainAdapter(adapterMapBinder);
	}

	protected void bindFXGeometricCurvePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);

		// transaction policy for resize + transform
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXResizeConnectionPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXBendConnectionPolicy.class);

		// interaction policy to relocate on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);

		// drag individual segments
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXBendOnSegmentDragPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformConnectionPolicy.class);

		// cloning
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneCurvePolicy.class);

		// clickable area resizing
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXConnectionClickableAreaBehavior.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCloneOnClickPolicy.class);
	}

	/**
	 * Binds adapters for {@link FXGeometricModelPart}.
	 *
	 * @param adapterMapBinder
	 *            The adapter map binder to which the bindings are added.
	 */
	protected void bindFXGeometricModelPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
	}

	protected void bindFXGeometricShapePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// hover on hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);

		// geometry provider for selection feedback
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});
		// geometry provider for selection handles
		adapterMapBinder
				.addBinding(AdapterKey.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
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
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for hover handles
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverHandlePartFactory.HOVER_HANDLES_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);
		// geometry provider for focus feedback
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// register resize/transform policies (writing changes also to model)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTransformPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXResizePolicy.class);

		// relocate on drag (including anchored elements, which are linked)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXTranslateSelectedOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXRelocateLinkedOnDragPolicy.class);

		// clone
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CloneShapePolicy.class);

		// bind dynamic anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(DefaultAnchorProvider.class);

		// clone on shift+click
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXCloneOnClickPolicy.class);

		// normalize connected on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXNormalizeConnectedOnDragPolicy.class);
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
	protected void bindFXPaletteViewerAsFXDomainAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(PALETTE_VIEWER_ROLE)).to(FXViewer.class);
	}

	protected void bindFXRectangleSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXBendFirstAnchorageOnSegmentHandleDragPolicy.class);
	}

	protected void bindFXRootPartAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(PALETTE_VIEWER_ROLE)).to(FXRootPart.class)
				.in(AdaptableScopes.typed(FXViewer.class));
	}

	protected void bindFXSquareSegmentHandlePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// single selection: resize relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXResizeTranslateFirstAnchorageOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXRotateSelectedOnHandleDragPolicy.class);

		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXResizeTransformSelectedOnHandleDragPolicy.class);
	}

	protected void bindHoverFeedbackFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_FEEDBACK_PART_FACTORY))
				.to(FXDefaultHoverFeedbackPartFactory.class);
	}

	protected void bindHoverHandleFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY))
				.to(FXDefaultHoverHandlePartFactory.class);
	}

	@Override
	protected void bindHoverHandlePartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(HoverBehavior.HOVER_HANDLE_PART_FACTORY))
				.to(FXLogoHoverHandlePartFactory.class);
	}

	@SuppressWarnings("serial")
	protected void bindHoverModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<HoverModel<Node>>() {
		})).to(new TypeLiteral<HoverModel<Node>>() {
		});
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new FXLogoContentPartFactory());
	}

	protected void bindPaletteElementPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverOnHoverPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CreateAndTranslateOnDragPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(GeometricOutlineProvider.class);
	}

	protected void bindPaletteFocusBehaviorAsFXRootPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(PaletteFocusBehavior.class);
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
	protected void bindPaletteViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// bind models
		bindContentModelAsPaletteViewerAdapter(adapterMapBinder);
		bindFocusModelAsPaletteViewerAdapter(adapterMapBinder);
		bindHoverModelAsPaletteViewerAdapter(adapterMapBinder);
		bindSelectionModelAsPaletteViewerAdapter(adapterMapBinder);
		// bind root part
		bindFXRootPartAsPaletteViewerAdapter(adapterMapBinder);
		// add hover feedback factory
		bindSelectionFeedbackFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindSelectionHandleFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindHoverFeedbackFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindHoverHandleFactoryAsPaletteViewerAdapter(adapterMapBinder);
		bindFocusFeedbackFactoryAsPaletteViewerAdapter(adapterMapBinder);
		// bind content part factory
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXLogoPaletteContentPartFactory.class);
		adapterMapBinder.addBinding(AdapterKey.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_COLOR_PROVIDER))
				.toInstance(new Provider<Color>() {
					@Override
					public Color get() {
						return Color.WHITE;
					}
				});
	}

	protected void bindPaletteViewerRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		bindFXHoverOnHoverPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXPanOrZoomOnScrollPolicyAsFXRootPartAdapter(adapterMapBinder);
		bindFXPanOnTypePolicyAsFXRootPartAdapter(adapterMapBinder);
		// register change viewport policy
		bindContentRestrictedChangeViewportPolicyAsFXRootPartAdapter(adapterMapBinder);
		// register default behaviors
		bindContentBehaviorAsFXRootPartAdapter(adapterMapBinder);
		// XXX: PaletteFocusBehavior only changes the viewer focus and default
		// styles.
		bindPaletteFocusBehaviorAsFXRootPartAdapter(adapterMapBinder);
		// bind focus traversal policy
		bindFocusTraversalPolicyAsFXRootPartAdapter(adapterMapBinder);
		// hover behavior
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FXHoverBehavior.class);
	}

	protected void bindSelectionFeedbackFactoryAsPaletteViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_FEEDBACK_PART_FACTORY))
				.to(FXDefaultSelectionFeedbackPartFactory.class);
	}

	protected void bindSelectionHandleFactoryAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY))
				.to(FXDefaultSelectionHandlePartFactory.class);
	}

	@Override
	protected void bindSelectionHandlePartFactoryAsContentViewerAdapter(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.role(SelectionBehavior.SELECTION_HANDLE_PART_FACTORY))
				.to(FXLogoSelectionHandlePartFactory.class);
	}

	@SuppressWarnings("serial")
	protected void bindSelectionModelAsPaletteViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.get(new TypeToken<SelectionModel<Node>>() {
		})).to(new TypeLiteral<SelectionModel<Node>>() {
		});
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		// contents
		bindFXGeometricModelPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), FXGeometricModelPart.class));
		bindFXGeometricShapePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), FXGeometricShapePart.class));
		bindFXGeometricCurvePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), FXGeometricCurvePart.class));

		// node selection handles and multi selection handles
		bindFXSquareSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXSquareSegmentHandlePart.class));

		// curve selection handles
		bindFXCircleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXCircleSegmentHandlePart.class));
		bindFXRectangleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXRectangleSegmentHandlePart.class));

		// hover handles
		bindFXDeleteHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), FXDeleteHoverHandlePart.class));
		bindFXCreateCurveHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXCreateCurveHoverHandlePart.class));

		// palette
		bindPaletteViewerAdapters(AdapterMaps.getAdapterMapBinder(binder(), FXViewer.class, PALETTE_VIEWER_ROLE));
		bindPaletteViewerRootPartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(), FXRootPart.class, PALETTE_VIEWER_ROLE));
		bindPaletteElementPartAdapters(AdapterMaps.getAdapterMapBinder(binder(), PaletteElementPart.class));
		AdapterMaps.getAdapterMapBinder(binder(), PaletteModelPart.class).addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);
	}

	@Override
	protected void enableAdapterMapInjection() {
		install(new AdapterInjectionSupport(LoggingMode.PRODUCTION));
	}

}