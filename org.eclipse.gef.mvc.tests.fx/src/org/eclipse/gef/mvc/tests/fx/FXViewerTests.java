/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import javafx.scene.Node;

public class FXViewerTests {

	@SuppressWarnings("serial")
	@Test
	public void properAdapterRegistration() {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new MvcFxModule() {

			protected void bindIContentPartFactory() {
				binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
				}).toInstance(new IContentPartFactory<Node>() {
					@Override
					public IContentPart<Node, ? extends Node> createContentPart(Object content,
							IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
						return null;
					}
				});
			}

			@Override
			protected void configure() {
				super.configure();
				bindIContentPartFactory();
			}
		});
		FXDomain domain = new FXDomain();
		injector.injectMembers(domain);
		FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		IRootPart<Node, ? extends Node> rootPart = viewer.getRootPart();
		assertNotNull(rootPart);
		rootPart = viewer.getAdapter(new TypeToken<IRootPart<Node, ? extends Node>>() {
		});
		assertNotNull(rootPart);

		// selection model is a directly instantiated generic type (so no type
		// information can be inferred from the instance)
		SelectionModel<Node> selectionModel = viewer.getAdapter(SelectionModel.class);
		assertNotNull(selectionModel);
		selectionModel = viewer.getAdapter(new TypeToken<SelectionModel<Node>>() {
		});
		assertNotNull(selectionModel);
		Object s2 = viewer.getAdapter(new TypeToken<SelectionModel<?>>() {
		});
		assertNotNull(s2);

		// we have subclasses selection behavior before, so we might infer some
		// types via generic superclass.
		SelectionBehavior<Node> selectionBehavior = rootPart.getAdapter(new TypeToken<SelectionBehavior<Node>>() {
		});
		assertNotNull(selectionBehavior);
		selectionBehavior = rootPart.getAdapter(SelectionBehavior.class);
		assertNotNull(selectionBehavior);
		selectionBehavior.activate();
	}
}
