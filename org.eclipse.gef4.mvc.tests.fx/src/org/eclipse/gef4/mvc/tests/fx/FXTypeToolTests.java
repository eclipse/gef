/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Map;

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
import org.eclipse.gef4.mvc.tools.ITool;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.sun.glass.events.KeyEvent;

import javafx.scene.Node;

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
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	@Test
	public void singleExecutionTransactionUsedForInteraction() throws InterruptedException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new MvcFxModule() {
			protected void bindDomain() {
				// stub the domain to be able to keep track of opened execution
				// transactions
				binder().bind(FXDomain.class).to(FXDomainDriver.class);
			}

			@Override
			protected void configure() {
				super.configure();
				bindDomain();
				// bind IContentPartFactory
				binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
				}).toInstance(new IContentPartFactory<Node>() {
					@Override
					public IContentPart<Node, ? extends Node> createContentPart(Object content,
							IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
						return null;
					}
				});
			}
		});
		injector.injectMembers(this);

		InfiniteCanvas infiniteCanvas = domain.getAdapter(FXViewer.class).getCanvas();
		ctx.createScene(infiniteCanvas, 100, 100);

		// activate domain, so tool gets activated and can register listeners
		domain.activate();

		// initialize
		domain.openedExecutionTransactions = 0;
		domain.closedExecutionTransactions = 0;
		assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);

		// create robot to simulate events
		Robot robot = new Robot();

		// move robot to scene
		ctx.moveTo(robot, infiniteCanvas, 50, 50);

		// simulate press/release gesture
		ctx.keyPress(robot, KeyEvent.VK_K);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
		ctx.keyRelease(robot, KeyEvent.VK_K);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("A single execution transaction should have been closed", 1, domain.closedExecutionTransactions);
	}

}
