/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef4.mvc.tests.fx;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef4.mvc.tests.fx.rules.FXNonApplicationThreadRule.RunnableWithResult;
import org.eclipse.gef4.mvc.tools.ITool;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.sun.glass.events.KeyEvent;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class FXTypeToolTests {

	private static class FXDomainDriver extends FXDomain {
		protected int openedExecutionTransactions = 0;
		protected int closedExecutionTransactions = 0;

		@Override
		public void closeExecutionTransaction(ITool<Node> tool) {
			if (tool instanceof FXTypeTool) {
				closedExecutionTransactions++;
			}
			super.closeExecutionTransaction(tool);
		}

		@Override
		public void openExecutionTransaction(ITool<Node> tool) {
			super.openExecutionTransaction(tool);
			if (tool instanceof FXTypeTool) {
				openedExecutionTransactions++;
			}
		};
	}

	/**
	 * Ensure that the JavaFX toolkit is properly initialized.
	 */
	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	@Inject
	private FXDomainDriver domain;

	/**
	 * It is important that a (single) execution transaction (see
	 * {@link IDomain#openExecutionTransaction(org.eclipse.gef4.mvc.tools.ITool)}
	 * ) is used for a complete press/drag interaction gesture, because
	 * otherwise the transactional results of the gesture could not be undone.
	 *
	 * @throws Throwable
	 */
	@Test
	public void singleExecutionTransactionUsedForInteraction() throws Throwable {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new MvcFxModule() {
			protected void bindDomain() {
				// stub the domain to be able to keep track of opened execution
				// transactions
				binder().bind(FXDomain.class).to(FXDomainDriver.class);
			}

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
				bindDomain();
				bindIContentPartFactory();
			}
		});
		injector.injectMembers(this);

		final InfiniteCanvas infiniteCanvas = domain
				.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE)).getCanvas();
		ctx.createScene(infiniteCanvas, 100, 100);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
			}
		});

		double validPosition = ctx.runAndWait(new RunnableWithResult<Double>() {
			@Override
			public Double run() {
				return Math.min(infiniteCanvas.getWidth(), infiniteCanvas.getHeight()) / 2;
			}
		});

		// initialize
		domain.openedExecutionTransactions = 0;
		domain.closedExecutionTransactions = 0;
		assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);

		// move robot to scene
		final EventHandler<MouseEvent> mouseEventFilter = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.err.println("FILTER: " + event);
			}
		};
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				infiniteCanvas.getScene().addEventFilter(MouseEvent.ANY, mouseEventFilter);
			}
		});
		ctx.moveTo(infiniteCanvas, validPosition, validPosition);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				infiniteCanvas.getScene().removeEventFilter(MouseEvent.ANY, mouseEventFilter);
			}
		});

		// simulate press/release gesture
		ctx.keyPress(KeyEvent.VK_K);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
			}
		});
		ctx.keyRelease(KeyEvent.VK_K);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("A single execution transaction should have been closed", 1,
						domain.closedExecutionTransactions);
			}
		});
	}

}
