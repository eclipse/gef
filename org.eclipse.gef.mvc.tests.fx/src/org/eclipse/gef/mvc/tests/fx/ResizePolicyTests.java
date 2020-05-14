/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.tests.fx.rules.FXApplicationThreadRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.multibindings.MapBinder;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

public class ResizePolicyTests {

	static class DimensionPart extends AbstractContentPart<Region> implements IResizableContentPart<Region> {
		private Dimension size = new Dimension(0, 0);

		@Override
		protected Region doCreateVisual() {
			return new Region() {
				@Override
				protected double computeMinHeight(double width) {
					return 35;
				}

				@Override
				protected double computeMinWidth(double height) {
					return 45;
				}
			};
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(Region visual) {
			visual.resize(size.width, size.height);
		}

		@Override
		public Dimension getContent() {
			return size;
		}

		@Override
		public Dimension getContentSize() {
			return size.getCopy();
		}

		@Override
		public void setContentSize(Dimension totalSize) {
			size.setSize(totalSize);
		}
	}

	static class ResizePolicyTestsContentPartFactory implements IContentPartFactory {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
			if (content instanceof Dimension) {
				return injector.getInstance(DimensionPart.class);
			} else {
				throw new IllegalArgumentException(content.getClass().toString());
			}
		}
	}

	private ResizePolicy resizePolicy;

	@Inject
	private IDomain domain;

	/**
	 * Ensure all tests are executed on the JavaFX application thread (and the
	 * JavaFX toolkit is properly initialized).
	 */
	@Rule
	public FXApplicationThreadRule fxApplicationThreadRule = new FXApplicationThreadRule();

	public ResizePolicy createResizePolicy() {
		// create injector
		Injector injector = Guice.createInjector(new MvcFxModule() {
			@Override
			protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
				super.bindAbstractContentPartAdapters(adapterMapBinder);
				adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ResizePolicy.class);
			}

			protected void bindIContentPartFactory() {
				binder().bind(IContentPartFactory.class).to(ResizePolicyTestsContentPartFactory.class);
			}

			@Override
			protected void configure() {
				super.configure();
				bindIContentPartFactory();
			}
		});
		injector.injectMembers(this);
		// get viewer
		IViewer viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
		// hook viewer to scene
		Scene scene = new Scene(viewer.getCanvas(), 100, 100);
		JFXPanel panel = new JFXPanel();
		panel.setScene(scene);
		// set viewer contents
		Dimension content = new Dimension(0, 0);
		viewer.getContents().setAll(Collections.singletonList(content));
		// activate domain
		domain.activate();
		// get content part for the content object
		IContentPart<? extends Node> contentPart = viewer.getContentPartMap().get(content);
		// get transform policy for that part
		return contentPart.getAdapter(ResizePolicy.class);
	}

	@Before
	public void setUp() {
		resizePolicy = createResizePolicy();
		resizePolicy.init();
	}

	@Test
	public void test_minimumSize_Region() {
		/*
		 * This test ensures that Bugzilla #512620 is not re-introduced by a
		 * later change.
		 */

		// check resize policy is reset
		assertEquals(0d, resizePolicy.getDeltaWidth(), 0.01);
		assertEquals(0d, resizePolicy.getDeltaHeight(), 0.01);

		// set region's min-size to USE_COMPUTED_SIZE
		Region region = (Region) resizePolicy.getHost().getVisual();
		region.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		assertEquals(Region.USE_COMPUTED_SIZE, region.getMinWidth(), 0.01);
		assertEquals(Region.USE_COMPUTED_SIZE, region.getMinHeight(), 0.01);

		// resize below minimum size using ResizePolicy
		resizePolicy.resize(region.minWidth(-1) - 5, region.minHeight(-1) - 5);
		// ensure minimum size is respected
		assertEquals(region.minWidth(-1), resizePolicy.getDeltaWidth(), 0.01);
		assertEquals(region.minHeight(-1), resizePolicy.getDeltaHeight(), 0.01);
	}
}
