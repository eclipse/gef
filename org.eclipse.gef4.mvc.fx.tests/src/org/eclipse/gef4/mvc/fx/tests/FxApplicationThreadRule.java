package org.eclipse.gef4.mvc.fx.tests;

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
public class FxApplicationThreadRule implements TestRule {

	private static boolean initializedJavaFxToolkit = false;

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				if (!initializedJavaFxToolkit) {
					final CountDownLatch latch = new CountDownLatch(1);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new JFXPanel(); // initializes JavaFX environment
							initializedJavaFxToolkit = true;
							latch.countDown();
						}
					});
					latch.await();
				}

				final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						try {
							base.evaluate();
						} catch (Throwable throwable) {
							throwableRef.set(throwable);
						}
					}

				});
				Throwable thrown = throwableRef.get();
				if (thrown != null) {
					throw thrown;
				}
			}
		};
	}
}
