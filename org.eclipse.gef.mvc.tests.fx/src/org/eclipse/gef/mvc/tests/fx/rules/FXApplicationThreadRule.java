/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx.rules;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 * A {@link TestRule} to ensure tests are executed on the JavaFX Application
 * Thread using {@link Platform#runLater(Runnable)}, ensuring that the JavaFX
 * Toolkit is properly initialized before execution.
 *
 * @author anyssen
 *
 */
public class FXApplicationThreadRule implements TestRule {

	private static boolean initializedJavaFxToolkit = false;

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				if (!initializedJavaFxToolkit) {
					final CountDownLatch latch = new CountDownLatch(1);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							new JFXPanel(); // initializes JavaFX environment
							initializedJavaFxToolkit = true;
							latch.countDown();
						}
					});
					latch.await();
				}

				final CountDownLatch countDownLatch = new CountDownLatch(1);
				final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							base.evaluate();
						} catch (Throwable throwable) {
							throwableRef.set(throwable);
						}
						countDownLatch.countDown();
					}
				});
				countDownLatch.await();
				Throwable thrown = throwableRef.get();
				if (thrown != null) {
					throw thrown;
				}
			}
		};
	}

}
