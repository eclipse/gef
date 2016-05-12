/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.tests.stubs;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.inject.AdapterMaps;
import org.eclipse.gef4.mvc.MvcModule;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.tests.stubs.cell.CellContentPartFactory;
import org.eclipse.gef4.mvc.tests.stubs.cell.CellRootPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class MvcTestsModule extends MvcModule<Object> {

	@Override
	protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<ContentBehavior<Object>>() {
		});
	}

	@Override
	protected void bindAbstractDomainAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractDomainAdapters(adapterMapBinder);
		AdapterMaps.getAdapterMapBinder(binder(), MvcTestsDomain.class).addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<IViewer<Object>>() {
				});
	}

	@Override
	protected void bindAbstractRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<ContentBehavior<Object>>() {
		});
	}

	protected void bindAbstractViewerAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ContentModel.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(GridModel.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole())
				.to(new TypeLiteral<IRootPart<Object, ? extends Object>>() {
				});
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<HoverModel<Object>>() {
		});
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<SelectionModel<Object>>() {
		});
		// bind content part factory
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(new TypeLiteral<CellContentPartFactory<Object>>() {
		});
	}

	@Override
	protected void configure() {
		super.configure();

		binder().bind(new TypeLiteral<IDomain<Object>>() {
		}).to(MvcTestsDomain.class);

		binder().bind(new TypeLiteral<IViewer<Object>>() {
		}).to(MvcTestsViewer.class);

		// bind AbstractViewer adapters
		bindAbstractViewerAdapters(AdapterMaps.getAdapterMapBinder(binder(), AbstractViewer.class));

		// bind root part
		binder().bind(new TypeLiteral<IRootPart<Object, ? extends Object>>() {
		}).to(new TypeLiteral<CellRootPart<Object, Object>>() {
		});
	}
}