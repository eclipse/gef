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
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.tools.FXTypeTool;
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

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotResult;
import javafx.util.Callback;

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

	/**
	 * It is important that a (single) execution transaction (see
	 * {@link IDomain#openExecutionTransaction(org.eclipse.gef.mvc.tools.ITool)}
	 * ) is used for a complete press/drag interaction gesture, because
	 * otherwise the transactional results of the gesture could not be undone.
	 *
	 * @throws Throwable
	 */
	@Test
	public void singleExecutionTransactionUsedForInteraction() throws Throwable {
		ctx.getRobot().mouseMove(1000, 1000);

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

		// repaint
		ctx.getPanel().repaint();
		ctx.waitForIdle();
		ctx.getRobot().waitForIdle();
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				scene.getRoot().snapshot(new Callback<SnapshotResult, Void>() {
					@Override
					public Void call(SnapshotResult param) {
						System.out.println("SNAPSHOT");
						latch.countDown();
						return null;
					}
				}, null, null);
			}
		});
		latch.await();
		ctx.getPanel().repaint();
		ctx.waitForIdle();
		ctx.getRobot().waitForIdle();

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
		ctx.moveTo(sceneCenter.x, sceneCenter.y);

		// click into the viewer to gain keyboard focus
		ctx.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		ctx.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

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
