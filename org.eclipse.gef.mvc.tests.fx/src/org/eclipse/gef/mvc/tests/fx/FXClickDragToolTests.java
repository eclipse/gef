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
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.Map;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule.RunnableWithResult;
import org.eclipse.gef.mvc.tools.ITool;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Guice;
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

	/**
	 * It is important that a single execution transaction (see
	 * {@link IDomain#openExecutionTransaction(org.eclipse.gef.mvc.tools.ITool)}
	 * ) is used for a complete click/drag interaction gesture, because
	 * otherwise the transactional results of the gesture could not be undone in
	 * a single step, as it would result in more than one operation within the
	 * domain's {@link IOperationHistory}.
	 *
	 * @throws Throwable
	 */
	@Test
	public void singleExecutionTransactionUsedForInteraction() throws Throwable {
		System.out.println("###===>>> FXClickDragToolTests");
		System.out.println("# Thread: " + Thread.currentThread().getName());

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

		// inject domain
		final FXDomainDriver domain = injector.getInstance(FXDomainDriver.class);
		final FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));

		final Scene scene = ctx.createScene(viewer.getCanvas(), 100, 100);

		// activate domain, so tool gets activated and can register listeners
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.activate();
				assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
				assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
			}
		});

		// move mouse to viewer center
		Point sceneCenter = ctx.runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				// XXX: It seems to be important to compute the position from
				// within the JavaFX application thread.
				return new Point((int) (scene.getX() + scene.getWidth() / 2),
						(int) (scene.getY() + scene.getHeight() / 2));
			}

		});
		ctx.moveTo(sceneCenter.x + 1, sceneCenter.y + 1);

		// simulate click gesture
		ctx.mousePress(InputEvent.BUTTON1_MASK);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
			}
		});
		ctx.mouseRelease(InputEvent.BUTTON1_MASK);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("A single execution transaction should have been closed", 1,
						domain.closedExecutionTransactions);
			}
		});

		// wait one second so that the next press does not count as a double
		// click
		ctx.delay(500);

		// re-initialize
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				domain.openedExecutionTransactions = 0;
				domain.closedExecutionTransactions = 0;
				assertEquals("No execution transaction should have been opened", 0, domain.openedExecutionTransactions);
				assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
			}
		});

		// simulate click/drag
		ctx.mousePress(InputEvent.BUTTON1_MASK);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
			}
		});
		ctx.mouseDrag(20, 20);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("No execution transaction should have been closed", 0, domain.closedExecutionTransactions);
			}
		});
		ctx.mouseRelease(InputEvent.BUTTON1_MASK);
		ctx.runAndWait(new Runnable() {
			@Override
			public void run() {
				assertEquals("A single execution transaction should have been opened", 1,
						domain.openedExecutionTransactions);
				assertEquals("A single execution transaction should have been closed", 1,
						domain.closedExecutionTransactions);
			}
		});

		System.out.println("###===>>> FXClickDragToolTests EXIT");
		System.out.println("# Thread: " + Thread.currentThread().getName());
	}
}
