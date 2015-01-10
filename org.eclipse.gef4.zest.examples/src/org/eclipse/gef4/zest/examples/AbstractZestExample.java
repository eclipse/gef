/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.examples;

import java.util.Collections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXStageSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.models.LayoutModel;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class AbstractZestExample extends Application {

	protected static Edge e(org.eclipse.gef4.graph.Node n,
			org.eclipse.gef4.graph.Node m) {
		String label = (String) n.getAttrs().get(Key.LABEL.toString())
				+ (String) m.getAttrs().get(Key.LABEL.toString());
		return new Edge.Builder(n, m).attr(Key.LABEL, label).build();
	}

	protected static org.eclipse.gef4.graph.Node n(String... attr) {
		org.eclipse.gef4.graph.Node.Builder builder = new org.eclipse.gef4.graph.Node.Builder();
		for (int i = 0; i < attr.length; i += 2) {
			builder.attr(attr[i], attr[i + 1]);
		}
		return builder.build();
	}

	private String title;

	public AbstractZestExample(String title) {
		this.title = title;
	}

	protected abstract Graph createGraph();

	protected abstract LayoutAlgorithm createLayoutAlgorithm();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// configure application
		Injector injector = Guice.createInjector(createModule());
		final FXDomain domain = new FXDomain();
		injector.injectMembers(domain);

		final FXViewer viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXStageSceneContainer(primaryStage));

		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);

		// activate domain only after viewers have been hooked
		domain.activate();

		// set contents and layout algorithm in JavaFX thread because it alters
		// the scene graph
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// set contents
				viewer.getAdapter(ContentModel.class).setContents(
						Collections.singletonList(createGraph()));

				// TODO: we need to ensure the default algorithm is not used
				// before the custom is set

				// set layout algorithm
				domain.getAdapter(LayoutModel.class).getLayoutContext()
						.setStaticLayoutAlgorithm(createLayoutAlgorithm());
			}
		});

		// set initial size
		viewer.getAdapter(ViewportModel.class).setWidth(
				viewer.getScene().getWidth());
		viewer.getAdapter(ViewportModel.class).setHeight(
				viewer.getScene().getHeight());

		viewer.getScene().widthProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, final Number newValue) {
						if (newValue != null) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									viewer.getAdapter(ViewportModel.class)
											.setWidth(newValue.doubleValue());
								}
							});
						}
					}
				});
		viewer.getScene().heightProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, final Number newValue) {
						if (newValue != null) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									viewer.getAdapter(ViewportModel.class)
											.setHeight(newValue.doubleValue());
								}
							});
						}
					}
				});

		primaryStage.setTitle(title);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	protected Module createModule() {
		return new ZestFxModule();
	}

}