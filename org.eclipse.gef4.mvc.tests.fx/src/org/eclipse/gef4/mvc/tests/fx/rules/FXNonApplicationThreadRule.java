/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.tests.fx.rules;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Robot;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * A {@link TestRule} to ensure that the JavaFX toolkit is properly initialized
 * before test execution. This rule does also serve as a context/utility for
 * JavaFX tests:
 * <ul>
 * <li>{@link #createScene(Parent, double, double)} creates a {@link Scene} and
 * shows it inside a {@link JFrame}.
 * <li>{@link #runAndWait(Runnable)} executes the given {@link Runnable} on the
 * JavaFX application thread and waits until the {@link Runnable} has been
 * executed.
 * <li>{@link #getEventSynchronizer(EventType)} registers an
 * {@link EventSynchronizer} for the given {@link EventType}. A corresponding
 * event can be fired after the synchronizer is registered. You can then wait
 * for the event processing by calling {@link EventSynchronizer#await()}.
 * <li>{@link #moveTo(Robot, Node, double, double)} moves the mouse to the
 * specified position within the given {@link Node}. An exception is thrown if
 * the mouse does not enter the node.
 * </ul>
 * Additionally, some convenience methods are provided to fire and wait for
 * specific events: {@link #mousePress(Robot, int)},
 * {@link #mouseDrag(Robot, int, int)}, {@link #mouseRelease(Robot, int)},
 * {@link #keyPress(Robot, int)}, and {@link #keyRelease(Robot, int)}.
 *
 * @author mwienand
 *
 */
public class FXNonApplicationThreadRule implements TestRule {

	public final static class EventSynchronizer<T extends Event> {
		private Scene scene;
		private EventType<T> type;
		private EventHandler<T> handler;
		private CountDownLatch latch;
		private boolean isRegistered;

		public EventSynchronizer(Scene scene, EventType<T> type) {
			this.scene = scene;
			this.type = type;
		}

		/**
		 * Waits for the registered event. If the event is not processed within
		 * 5 seconds, an exception is thrown.
		 *
		 * @see #await(long, TimeUnit)
		 * @throws InterruptedException
		 */
		public void await() throws InterruptedException {
			await(5, TimeUnit.SECONDS);
		}

		/**
		 * Waits for the registered event. If the event is not processed within
		 * the specified timeout, an exception is thrown.
		 *
		 * @throws InterruptedException
		 */
		public void await(long timeout, TimeUnit timeUnit) throws InterruptedException {
			if (!isRegistered) {
				throw new IllegalStateException("not registered");
			}
			if (!latch.await(timeout, timeUnit)) {
				throw new IllegalStateException("event synchronizer timeout: event was not processed.");
			}
			unregisterHandler(type, handler);
			isRegistered = false;
		}

		/**
		 * Registers this {@link EventSynchronizer} for its event type. After
		 * registration, you can wait for the event processing by calling
		 * {@link #await()}.
		 */
		public void register() {
			if (isRegistered) {
				throw new IllegalStateException("already registered");
			}
			latch = new CountDownLatch(1);
			handler = new EventHandler<T>() {
				@Override
				public void handle(T event) {
					latch.countDown();
				}
			};
			registerHandler(type, handler);
			isRegistered = true;
		}

		private void registerHandler(final EventType<T> type, final EventHandler<T> handler) {
			final CountDownLatch regitrationLatch = new CountDownLatch(1);
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					scene.addEventHandler(type, handler);
					regitrationLatch.countDown();
				}
			});
			try {
				regitrationLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void unregisterHandler(final EventType<T> type, final EventHandler<T> handler) {
			final CountDownLatch regitrationLatch = new CountDownLatch(1);
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					scene.removeEventHandler(type, handler);
					regitrationLatch.countDown();
				}
			});
			try {
				regitrationLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public interface RunnableWithResult<T> {
		public T run();
	}

	public interface RunnableWithResultAndParam<T, P1> {
		public T run(P1 param1);
	}

	private static boolean initializedJavaFxToolkit = false;
	private Scene scene;
	private Map<EventType<?>, EventSynchronizer<?>> eventSynchronizers = new HashMap<>();
	private JFXPanel panel;

	private Robot robot;

	@Override
	public Statement apply(final Statement base, Description description) {
		if (Platform.isFxApplicationThread() || SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException(
					"Tests may not be executed from FX application or AWT event dispatching thread.");
		}
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
				base.evaluate();
			}
		};
	}

	/**
	 * Creates a {@link Scene} that wraps the given root visual and shows that
	 * {@link Scene} in a {@link JFrame}.
	 *
	 * @param root
	 *            The root visual.
	 * @param width
	 *            The width of the frame/scene.
	 * @param height
	 *            The height of the frame/scene.
	 * @return The created {@link Scene}.
	 * @throws InterruptedException
	 * @throws AWTException
	 */
	public Scene createScene(final Parent root, final double width, final double height) throws Throwable {
		scene = runAndWait(new RunnableWithResult<Scene>() {
			@Override
			public Scene run() {
				// hook viewer to scene
				Scene scene = new Scene(root, width, height);
				panel = new JFXPanel();
				panel.setScene(scene);
				// create frame to draw the panel
				JFrame jFrame = new JFrame();
				jFrame.setBounds(0, 0, (int) width, (int) height);
				jFrame.setContentPane(panel);
				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jFrame.setLayout(new BorderLayout());
				jFrame.setLocationRelativeTo(null);
				jFrame.setVisible(true);
				return scene;
			}
		});
		return scene;
	}

	public void delay(final int millis) throws AWTException {
		getRobot().delay(millis);
	}

	/**
	 * Returns an {@link EventSynchronizer} for the specified {@link EventType}.
	 * The {@link EventSynchronizer} is automatically registered on the
	 * {@link Scene}. Therefore, you only have to fire the event and call
	 * {@link EventSynchronizer#await()} after that to wait for the event
	 * processing.
	 * <p>
	 * For example, the following snippet waits for the processing of a
	 * MOUSE_PRESSED event to finish:
	 *
	 * <pre>
	 * // simulate mouse press
	 * Robot robot = new Robot();
	 * EventSynchronizer<MouseEvent> es = fxNonApplicationThreadRule.getEventSynchronizer(MouseEvent.MOUSE_PRESSED);
	 * robot.mousePress(InputEvent.BUTTON1_MASK);
	 * es.await();
	 * </pre>
	 *
	 * Convenience methods are provided for standard interactions:
	 * <ul>
	 * <li>{@link #keyPress(Robot, int)}
	 * <li>{@link #keyRelease(Robot, int)}
	 * <li>{@link #mousePress(Robot, int)}
	 * <li>{@link #mouseRelease(Robot, int)}
	 * <li>{@link #mouseDrag(Robot, int, int)}
	 * <li>{@link #moveTo(Robot, Node, double, double)}
	 * </ul>
	 *
	 * @param type
	 * @return An {@link EventSynchronizer} that is registered for the specified
	 *         {@link EventType} on the {@link Scene}.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Event> EventSynchronizer<T> getEventSynchronizer(EventType<T> type) {
		if (!eventSynchronizers.containsKey(type)) {
			eventSynchronizers.put(type, new EventSynchronizer<>(scene, type));
		}
		EventSynchronizer<T> eventSynchronizer = (EventSynchronizer<T>) eventSynchronizers.get(type);
		eventSynchronizer.register();
		return eventSynchronizer;
	}

	public Robot getRobot() throws AWTException {
		if (robot == null) {
			robot = new Robot();
			// XXX: Ensure robot waits for event being processed on AWT event
			// queue
			robot.setAutoWaitForIdle(true);
		}
		return robot;
	}

	/**
	 * Fires a KEY_PRESSED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public void keyPress(final int keycode) throws AWTException, InterruptedException {
		EventSynchronizer<KeyEvent> eventSynchronizer = getEventSynchronizer(KeyEvent.KEY_PRESSED);
		getRobot().keyPress(keycode);
		eventSynchronizer.await();
	}

	/**
	 * Fires a KEY_RELEASED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public void keyRelease(final int keycode) throws AWTException, InterruptedException {
		EventSynchronizer<KeyEvent> eventSynchronizer = getEventSynchronizer(KeyEvent.KEY_RELEASED);
		getRobot().keyRelease(keycode);
		eventSynchronizer.await();
	}

	/**
	 * Fires a MOUSE_DRAGGED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public void mouseDrag(final int x, final int y) throws AWTException, InterruptedException {
		EventSynchronizer<MouseEvent> eventSynchronizer = getEventSynchronizer(MouseEvent.MOUSE_DRAGGED);
		getRobot().mouseMove(x, y);
		eventSynchronizer.await();
	}

	/**
	 * Fires a MOUSE_PRESSED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public void mousePress(final int buttons) throws AWTException, InterruptedException {
		EventSynchronizer<MouseEvent> eventSynchronizer = getEventSynchronizer(MouseEvent.MOUSE_PRESSED);
		getRobot().mousePress(buttons);
		eventSynchronizer.await();
	}

	/**
	 * Fires a MOUSE_RELEASED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public void mouseRelease(final int buttons) throws AWTException, InterruptedException {
		EventSynchronizer<MouseEvent> eventSynchronizer = getEventSynchronizer(MouseEvent.MOUSE_RELEASED);
		getRobot().mouseRelease(buttons);
		eventSynchronizer.await();
	}

	public void moveTo(final double sceneX, final double sceneY) throws Throwable {
		Point position = runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				final double x = scene.getWindow().getX() + sceneX;
				final double y = scene.getWindow().getY() + sceneY;
				return new Point((int) x, (int) y);
			}
		});
		EventSynchronizer<MouseEvent> synchronizer = getEventSynchronizer(MouseEvent.MOUSE_ENTERED_TARGET);
		getRobot().mouseMove(position.x, position.y);
		synchronizer.await();
	}

	public void moveTo(final Node visual, final double localX, final double localY) throws Throwable {
		Point position = runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				Point2D scenePosition = visual.localToScene(localX, localY);
				final double x = scene.getWindow().getX() + scenePosition.getX();
				final double y = scene.getWindow().getY() + scenePosition.getY();
				return new Point((int) x, (int) y);
			}
		});
		EventSynchronizer<MouseEvent> synchronizer = getEventSynchronizer(MouseEvent.MOUSE_ENTERED_TARGET);
		getRobot().mouseMove(position.x, position.y);
		synchronizer.await();
	}

	/**
	 * Schedules the given {@link Runnable} on the JavaFX application thread and
	 * waits for its execution to finish.
	 *
	 * @param runnable
	 * @throws Throwable
	 */
	public void runAndWait(final Runnable runnable) throws Throwable {
		final AtomicReference<Throwable> throwableRef = new AtomicReference<>(null);
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Throwable t) {
					throwableRef.set(t);
				} finally {
					latch.countDown();
				}
			}
		});
		latch.await();
		Throwable throwable = throwableRef.get();
		if (throwable != null) {
			throw throwable;
		}
	}

	/**
	 * Schedules the given {@link RunnableWithResult} on the JavaFX application
	 * thread and waits for its execution to finish.
	 *
	 * @param runnableWithResult
	 * @throws Throwable
	 */
	public <T> T runAndWait(final RunnableWithResult<T> runnableWithResult) throws Throwable {
		final AtomicReference<Throwable> throwableRef = new AtomicReference<>(null);
		final AtomicReference<T> resultRef = new AtomicReference<>(null);
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					resultRef.set(runnableWithResult.run());
				} catch (Throwable t) {
					throwableRef.set(t);
				} finally {
					latch.countDown();
				}
			}
		});
		latch.await();
		Throwable throwable = throwableRef.get();
		if (throwable != null) {
			throw throwable;
		}
		return resultRef.get();
	}

	/**
	 * Schedules the given {@link RunnableWithResult} on the JavaFX application
	 * thread and waits for its execution to finish.
	 *
	 * @param runnableWithResult
	 * @throws Throwable
	 */
	public <T, P1> T runAndWait(final RunnableWithResultAndParam<T, P1> runnableWithResult, final P1 param1)
			throws Throwable {
		final AtomicReference<Throwable> throwableRef = new AtomicReference<>(null);
		final AtomicReference<T> resultRef = new AtomicReference<>(null);
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					resultRef.set(runnableWithResult.run(param1));
				} catch (Throwable t) {
					throwableRef.set(t);
				} finally {
					latch.countDown();
				}
			}
		});
		latch.await();
		Throwable throwable = throwableRef.get();
		if (throwable != null) {
			throw throwable;
		}
		return resultRef.get();
	}

}
