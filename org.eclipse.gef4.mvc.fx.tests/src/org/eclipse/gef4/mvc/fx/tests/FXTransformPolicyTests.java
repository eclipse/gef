/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;

public class FXTransformPolicyTests {

	static class PointPart extends AbstractFXContentPart<Rectangle> {
		@Override
		protected Rectangle createVisual() {
			return new Rectangle(10, 10);
		}

		@Override
		protected void doRefreshVisual(Rectangle visual) {
			visual.setX(getContent().x);
			visual.setY(getContent().y);
		}

		@Override
		public Point getContent() {
			return (Point) super.getContent();
		}
	}

	static class TxContentPartFactory implements IContentPartFactory<Node> {
		@Inject
		private Injector injector;

		@Override
		public IContentPart<Node, ? extends Node> createContentPart(Object content, IBehavior<Node> contextBehavior,
				Map<Object, Object> contextMap) {
			if (content instanceof Point) {
				return injector.getInstance(PointPart.class);
			} else {
				throw new IllegalArgumentException(content.getClass().toString());
			}
		}
	}

	private FXTransformPolicy transformPolicy;

	@Inject
	private FXDomain domain;

	/**
	 * Ensure all tests are executed on the JavaFX application thread (and the
	 * JavaFX toolkit is properly initialized).
	 */
	@Rule
	public FxApplicationThreadRule fxApplicationThreadRule = new FxApplicationThreadRule();

	public FXTransformPolicy createTransformPolicy() {
		// create injector
		Injector injector = Guice.createInjector(new MvcFxModule() {
			@Override
			protected void bindAbstractFXContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
				super.bindAbstractFXContentPartAdapters(adapterMapBinder);
				adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class)).to(FXTransformPolicy.class);
			}

			protected void bindIContentPartFactory() {
				binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
				}).toInstance(new TxContentPartFactory());
			}

			@Override
			protected void configure() {
				super.configure();
				bindIContentPartFactory();
			}
		});
		injector.injectMembers(this);
		// get viewer
		FXViewer viewer = domain.<FXViewer> getAdapter(IViewer.class);
		// hook viewer to scene
		Scene scene = new Scene(viewer.getCanvas(), 100, 100);
		JFXPanel panel = new JFXPanel();
		panel.setScene(scene);
		// set viewer contents
		Point content = new Point(0, 0);
		viewer.getAdapter(ContentModel.class).setContents(Collections.singletonList(content));
		// activate domain
		domain.activate();
		// get content part for the content object
		IContentPart<Node, ? extends Node> contentPart = viewer.getContentPartMap().get(content);
		// get transform policy for that part
		return contentPart.getAdapter(FXTransformPolicy.class);
	}

	@Before
	public void setUp() {
		if (transformPolicy == null) {
			transformPolicy = createTransformPolicy();
		}
		// initialize (i.e. reset) transform policy
		transformPolicy.init();
	}

	@Test
	public void test_concat() {
		// test that the initial and current node transformation are identity
		// transforms
		assertEquals(new AffineTransform(), transformPolicy.getInitialNodeTransform());
		assertEquals(transformPolicy.getInitialNodeTransform(),
				JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
		// create pre and post transforms
		assertEquals(0, transformPolicy.createPreTransform());
		assertEquals(1, transformPolicy.createPreTransform());
		assertEquals(2, transformPolicy.createPreTransform());
		assertEquals(0, transformPolicy.createPostTransform());
		assertEquals(1, transformPolicy.createPostTransform());
		assertEquals(2, transformPolicy.createPostTransform());
		// create affine transformations here that are transfered to the
		// transform policy
		AffineTransform t0 = new AffineTransform().setToScale(2, 2);
		AffineTransform t1 = new AffineTransform().setToTranslation(2, 2);
		AffineTransform t2 = new AffineTransform().setToRotation(2);
		AffineTransform t3 = new AffineTransform().setToScale(2, 2);
		AffineTransform t4 = new AffineTransform().setToTranslation(2, 2);
		AffineTransform t5 = new AffineTransform().setToRotation(2);
		// transfer them
		transformPolicy.setPostTransform(2, t0);
		transformPolicy.setPostTransform(1, t1);
		transformPolicy.setPostTransform(0, t2);
		transformPolicy.setPreTransform(0, t3);
		transformPolicy.setPreTransform(1, t4);
		transformPolicy.setPreTransform(2, t5);
		// test the concatenation
		AffineTransform concatenation = t0.getCopy().concatenate(t1).concatenate(t2).concatenate(t3).concatenate(t4)
				.concatenate(t5);
		assertEquals(concatenation, JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
	}

	@Test
	public void test_create() {
		// test that the initial and current node transformation are identity
		// transforms
		assertEquals(new AffineTransform(), transformPolicy.getInitialNodeTransform());
		assertEquals(transformPolicy.getInitialNodeTransform(),
				JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
		// create a new transform that is applied before the node transformation
		int firstPreIndex = transformPolicy.createPreTransform();
		// test that the first created transform has index 0
		assertEquals(0, firstPreIndex);
		// create a new transform that is applied after the node transformation
		int firstPostIndex = transformPolicy.createPostTransform();
		// test that the first created transform has index 0
		assertEquals(0, firstPostIndex);
		// create a new transform that is applied before all transformations
		int secondPreIndex = transformPolicy.createPreTransform();
		// test that the second created transform has index 1
		assertEquals(1, secondPreIndex);
		// create a new transform that is applied after all transformations
		int secondPostIndex = transformPolicy.createPostTransform();
		// test that the second created transform has index 1
		assertEquals(1, secondPostIndex);
		// explicitly set one of the transforms to identity so that the
		// composite transform is computed
		transformPolicy.setPreTranslate(firstPreIndex, 0, 0);
		// test that the composite transformation equals the initial
		// transformation since only identity transforms have been applied
		assertEquals(transformPolicy.getInitialNodeTransform(),
				JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
		// set all transforms to translations
		transformPolicy.setPreTranslate(firstPreIndex, 10, 10);
		transformPolicy.setPreTranslate(secondPreIndex, 10, 10);
		transformPolicy.setPostTranslate(firstPostIndex, 10, 10);
		transformPolicy.setPostTranslate(secondPostIndex, 10, 10);
		// test that the composite transform equals a translation by 40, 40 (4
		// individual translations by 10, 10 => 40, 40)
		assertEquals(new AffineTransform().setToTranslation(40, 40),
				JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
	}

	@Test
	public void test_init() {
		// test that the initial and current node transformation are identity
		// transforms
		assertEquals(new AffineTransform(), transformPolicy.getInitialNodeTransform());
		assertEquals(transformPolicy.getInitialNodeTransform(),
				JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
		// create a new transform that is applied before the node transformation
		int firstIndex = transformPolicy.createPreTransform();
		// test that the first created transform has index 0
		assertEquals(0, firstIndex);
		// set the transform to a translation
		transformPolicy.setPreTranslate(firstIndex, 10, 10);
		// test that the initial node transformation differs from the current
		// node transformation (due to the translation)
		assertFalse(transformPolicy.getInitialNodeTransform()
				.equals(JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform())));
		// call init() again to reset the policy
		transformPolicy.init();
		// test that the initial and current node transformation are identity
		// transforms again
		assertEquals(new AffineTransform(), transformPolicy.getInitialNodeTransform());
		assertEquals(transformPolicy.getInitialNodeTransform(),
				JavaFX2Geometry.toAffineTransform(transformPolicy.getNodeTransform()));
		// create a new transform that is applied before the node transformation
		firstIndex = transformPolicy.createPreTransform();
		// test that the first created transform has index 0
		assertEquals(0, firstIndex);
	}

}
