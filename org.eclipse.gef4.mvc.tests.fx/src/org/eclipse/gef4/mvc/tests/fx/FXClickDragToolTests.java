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
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
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

import javafx.scene.Node;
import javafx.scene.Scene;

public class FXClickDragToolTests {

	private static class FXDomainDriver extends FXDomain {
		protected int openedExecutionTransactions = 0;
		protected int closedExecutionTransactions = 0;

		@Override
		public void closeExecutionTransaction(ITool<Node> tool) {
			if (tool instanceof FXClickDragTool) {
				closedExecutionTransactions++;
			}
			super.closeExecutionTransaction(tool);
		}

		@Override
		public void openExecutionTransaction(ITool<Node> tool) {
			super.openExecutionTransaction(tool);
			if (tool instanceof FXClickDragTool) {
				openedExecutionTransactions++;
			}
		}
	}

	/**
	 * Ensure all tests are executed on the JavaFX application thread (and the
	 * JavaFX toolkit is properly initialized).
	 */
	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	@Inject
	private FXDomainDriver domain;

	/**
	 * It is important that a single execution transaction (see
	 * {@link IDomain#openExecutionTransaction(org.eclipse.gef4.mvc.tools.ITool)}
	 * ) is used for a complete click/drag interaction gesture, because
	 * otherwise the transactional results of the gesture could not be undone in
	 * a single step, as it would result in more than one operation within the
	 * domain's {@link IOperationHistory}.
	 */
	@Test
	public void singleExecutionTransactionUsedForInteraction()
			throws InterruptedException, InvocationTargetException, AWTException {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new MvcFxModule() {
			@Override
			protected void bindIDomain() {
				// stub the domain to be able to keep track of opened execution
				// transactions
				binder().bind(new TypeLiteral<IDomain<Node>>() {
				}).to(FXDomainDriver.class);
			}

			@Override
			protected void configure() {
				super.configure();
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

		// inject domain
		injector.injectMembers(this);

		final Scene scene = ctx.createScene(domain.getAdapter(FXViewer.class).getCanvas(), 100, 100);

		// activate domain, so tool gets activated and can register listeners
		domain.activate();

		// move mouse to viewer center
		Robot robot = new Robot();
		ctx.moveTo(robot, scene.getRoot(), 50, 50);

		// initialize
		domain.openedExecutionTransactions = 0;
		domain.closedExecutionTransactions = 0;
		assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);

		// simulate click gesture
		ctx.mousePress(robot, InputEvent.BUTTON1_MASK);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
		ctx.mouseRelease(robot, InputEvent.BUTTON1_MASK);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("A single execution transaction should have been closed", 1, domain.closedExecutionTransactions);

		// wait one second so that the next press does not count as a double
		// click
		robot.delay(1000);

		// re-initialize
		domain.openedExecutionTransactions = 0;
		domain.closedExecutionTransactions = 0;
		assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);

		// simulate click/drag
		ctx.mousePress(robot, InputEvent.BUTTON1_MASK);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
		ctx.mouseDrag(robot, 20, 20);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
		ctx.mouseRelease(robot, InputEvent.BUTTON1_MASK);
		assertEquals("A single execution transaction should have been opened", 1, domain.openedExecutionTransactions);
		assertEquals("A single execution transaction should have been closed", 1, domain.closedExecutionTransactions);
	}

}
