/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
import org.eclipse.gef.mvc.fx.parts.FXDefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef.mvc.fx.policies.FXNormalizeConnectedOnDragPolicy;
import org.eclipse.gef.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.behaviors.NodeHidingBehavior;
import org.eclipse.gef.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef.zest.fx.policies.HideOnTypePolicy;
import org.eclipse.gef.zest.fx.policies.HidePolicy;
import org.eclipse.gef.zest.fx.policies.OpenNestedGraphOnDoubleClickPolicy;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsOnTypePolicy;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsPolicy;
import org.eclipse.gef.zest.fx.policies.TranslateSelectedAndRelocateLabelsOnDragPolicy;

import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;

public class DotGraphViewModule extends ZestFxModule {

	@Override
	protected void bindNodePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(NodeLayoutBehavior.class);
		// pruning
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(HidePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(ShowHiddenNeighborsPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(NodeHidingBehavior.class);

		// translate on-drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(TranslateSelectedAndRelocateLabelsOnDragPolicy.class);

		// show hidden neighbors on-type
		adapterMapBinder.addBinding(AdapterKey.role("show-hidden-neighbors")) //$NON-NLS-1$
				.to(ShowHiddenNeighborsOnTypePolicy.class);

		// hide on-type
		adapterMapBinder.addBinding(AdapterKey.role("hide")) //$NON-NLS-1$
				.to(HideOnTypePolicy.class);

		adapterMapBinder.addBinding(AdapterKey.role("open-nested-graph")) //$NON-NLS-1$
				.to(OpenNestedGraphOnDoubleClickPolicy.class);

		// transform policy for relocation
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXTransformPolicy.class);

		// resize policy to resize nesting nodes
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXResizePolicy.class);

		// anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(DotAnchorProvider.class);

		// feedback and handles
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(1);
					}
				});

		// selection feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// selection link feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// focus feedback provider
		adapterMapBinder
				.addBinding(AdapterKey
						.role(FXDefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover on-hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXHoverOnHoverPolicy.class);

		// normalize on drag
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(FXNormalizeConnectedOnDragPolicy.class);
	}

}
