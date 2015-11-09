/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ui.ZestFxUiModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

/**
 * The {@link ZestFxUiView} is an extension to the {@link AbstractFXView}. It creates an
 * {@link Injector} based on the {@link ZestFxModule} and {@link ZestFxUiModule}
 * by default. Other than that, it provides a {@link #setGraph(Graph) method} to
 * change the {@link Graph} that is displayed.
 *
 * @author mwienand
 *
 */
public class ZestFxUiView extends AbstractFXView {

	/**
	 * Constructs a new {@link ZestFxUiView}. Uses an {@link Injector} that is
	 * created from the {@link ZestFxModule} and {@link ZestFxUiModule} to
	 * inject its members.
	 */
	public ZestFxUiView() {
		super(Guice.createInjector(Modules.override(new ZestFxModule()).with(new ZestFxUiModule())));
	}

	/**
	 * Constructs a new {@link ZestFxUiView}. Uses the given {@link Injector} to
	 * inject its members.
	 *
	 * @param injector
	 *            The {@link Injector} that is used to inject the members of
	 *            this {@link ZestFxUiView}.
	 */
	public ZestFxUiView(Injector injector) {
		super(injector);
	}

	/**
	 * Changes the contents of the {@link #getViewer()} to the given
	 * {@link Graph}. The contents are changed by changing the
	 * {@link ContentModel}.
	 *
	 * @param graph
	 *            The new contents for the viewer.
	 */
	public void setGraph(Graph graph) {
		// check we have a content viewer
		FXViewer contentViewer = getViewer();
		if (contentViewer == null) {
			throw new IllegalStateException("Invalid configuration: Content viewer could not be retrieved.");
		}
		// check we have a content model
		ContentModel contentModel = getViewer().getAdapter(ContentModel.class);
		if (contentModel == null) {
			throw new IllegalStateException("Invalid configuration: Content model could not be retrieved.");
		}
		// set contents (will wrap graph into contents list)
		List<Object> contents = new ArrayList<Object>(1);
		if (graph != null) {
			contents.add(graph);
		}
		contentModel.setContents(contents);
	}

}
