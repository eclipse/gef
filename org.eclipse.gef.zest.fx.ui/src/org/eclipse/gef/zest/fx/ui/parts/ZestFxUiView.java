/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXView;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

/**
 * The {@link ZestFxUiView} is an extension to the {@link AbstractFXView}. It
 * creates an {@link Injector} based on the {@link ZestFxModule} and
 * {@link ZestFxUiModule} by default. Other than that, it provides a
 * {@link #setGraph(Graph) method} to change the {@link Graph} that is
 * displayed.
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
	 * Changes the contents of the {@link #getContentViewer()} to the given
	 * {@link Graph}. The contents are changed by changing the
	 * {@link IViewer#contentsProperty()}.
	 *
	 * @param graph
	 *            The new contents for the viewer.
	 */
	public void setGraph(Graph graph) {
		// check we have a content viewer
		IViewer contentViewer = getContentViewer();
		if (contentViewer == null) {
			throw new IllegalStateException("Invalid configuration: Content viewer could not be retrieved.");
		}
		// set contents (will wrap graph into contents list)
		List<Object> contents = new ArrayList<>(1);
		if (graph != null) {
			contents.add(graph);
		}
		contentViewer.getContents().setAll(contents);
	}

}
