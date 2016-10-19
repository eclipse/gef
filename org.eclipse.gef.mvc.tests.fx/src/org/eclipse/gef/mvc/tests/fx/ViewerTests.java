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
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.IBehavior;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Node;

public class ViewerTests {

	@SuppressWarnings("serial")
	@Test
	public void properAdapterRegistration() {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new MvcFxModule() {

			protected void bindIContentPartFactory() {
				binder().bind(IContentPartFactory.class).toInstance(new IContentPartFactory() {
					@Override
					public IContentPart<? extends Node> createContentPart(Object content, IBehavior contextBehavior,
							Map<Object, Object> contextMap) {
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
		IDomain domain = injector.getInstance(IDomain.class);
		IViewer viewer = domain.getAdapter(AdapterKey.get(IViewer.class, IDomain.CONTENT_VIEWER_ROLE));
		IRootPart<? extends Node> rootPart = viewer.getRootPart();
		assertNotNull(rootPart);
		rootPart = viewer.getAdapter(new TypeToken<IRootPart<? extends Node>>() {
		});
		assertNotNull(rootPart);

		// selection model is a directly instantiated generic type (so no type
		// information can be inferred from the instance)
		SelectionModel selectionModel = viewer.getAdapter(SelectionModel.class);
		assertNotNull(selectionModel);
		selectionModel = viewer.getAdapter(SelectionModel.class);
		assertNotNull(selectionModel);

		// we have subclasses selection behavior before, so we might infer some
		// types via generic superclass.
		SelectionBehavior selectionBehavior = rootPart.getAdapter(SelectionBehavior.class);
		assertNotNull(selectionBehavior);
		selectionBehavior = rootPart.getAdapter(SelectionBehavior.class);
		assertNotNull(selectionBehavior);
		selectionBehavior.activate();
	}
}
