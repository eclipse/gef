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
package org.eclipse.gef.mvc.tests.fx.rules;

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

	public final class EventSynchronizer<T extends Event> {

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
		public void await() throws Throwable {
			await(5, TimeUnit.SECONDS);
		}

		/**
		 * Waits for the registered event. If the event is not processed within
		 * the specified timeout, an exception is thrown.
		 *
		 * @throws InterruptedException
		 */
		public void await(long timeout, TimeUnit timeUnit) throws Throwable {
			if (!isRegistered) {
				throw new IllegalStateException("not registered");
			}
			waitForIdle();
			System.out.println(thread() + " AWAIT " + this);
			boolean successful = latch.await(timeout, timeUnit);
			unregisterHandler(type, handler);
			isRegistered = false;
			if (!successful) {
				System.out.println(thread() + " TIMEOUT " + this);
				throw new IllegalStateException("event synchronizer timeout: event was not processed.");
			}
			System.out.println(thread() + " PROCESSED " + this);
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
			final CountDownLatch registrationLatch = new CountDownLatch(1);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					scene.addEventHandler(type, handler);
					System.out.println(thread() + "Handler for " + type + " was set on scene.");
					registrationLatch.countDown();
				}
			});
			try {
				registrationLatch.await();
				System.out.println(thread() + "Handler for " + type + " was registered.");
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

	private synchronized static void initFX() throws InterruptedException {
		System.out.println(thread() + "initFX()");
		if (!initializedJavaFxToolkit) {
			final CountDownLatch latch = new CountDownLatch(1);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new JFXPanel(); // initializes JavaFX
					latch.countDown();
				}
			});
			latch.await();
			initializedJavaFxToolkit = true;
		}
	}

	private static String thread() {
		return Thread.currentThread() + " ";
	}

	private Scene scene;
	private JFXPanel panel;
	private JFrame jFrame;
	private Map<EventType<?>, EventSynchronizer<?>> eventSynchronizers = new HashMap<>();
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
				System.out.println(thread() + "apply " + description.getMethodName());
				initFX();
				try {
					base.evaluate();
				} finally {
					runAndWait(new Runnable() {
						@Override
						public void run() {
							panel.setScene(null);
							jFrame.setVisible(false);
							scene = null;
							panel = null;
						}
					});
				}
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
				jFrame = new JFrame();
				jFrame.setBounds(0, 0, (int) width, (int) height);
				jFrame.setContentPane(panel);
				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jFrame.setLayout(new BorderLayout());
				jFrame.setLocationRelativeTo(null);
				jFrame.setVisible(true);
				return scene;
			}
		});

		runAndWait(new Runnable() {
			@Override
			public void run() {
				scene.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
					@Override
					public void handle(final MouseEvent event) {
						System.out.println(thread() + " -> " + event + this);
					}
				});
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
	public synchronized void keyPress(final int keycode) throws Throwable {
		System.out.println(thread() + "keyPress: (" + keycode + ") ...");
		EventSynchronizer<KeyEvent> eventSynchronizer = getEventSynchronizer(KeyEvent.KEY_PRESSED);
		System.out.println(thread() + "GOT the Synchronizer!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(thread() + "k press now!");
					getRobot().keyPress(keycode);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		eventSynchronizer.await();
		System.out.println(thread() + "... done.");
	}

	/**
	 * Fires a KEY_RELEASED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void keyRelease(final int keycode) throws Throwable {
		System.out.println(thread() + "keyRelease: (" + keycode + ") ...");
		EventSynchronizer<KeyEvent> eventSynchronizer = getEventSynchronizer(KeyEvent.KEY_RELEASED);
		System.out.println(thread() + "GOT the Synchronizer!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(thread() + "k release now!");
					getRobot().keyRelease(keycode);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		eventSynchronizer.await();
		System.out.println(thread() + "... done.");
	}

	/**
	 * Fires a MOUSE_DRAGGED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void mouseDrag(final int sceneX, final int sceneY) throws Throwable {
		System.out.println(thread() + "mouseDrag: (" + sceneX + ", " + sceneY + ") ...");
		EventSynchronizer<MouseEvent> eventSynchronizer = getEventSynchronizer(MouseEvent.MOUSE_DRAGGED);
		System.out.println(thread() + "GOT the Synchronizer!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(thread() + "drag now!");
					getRobot().mouseMove(sceneX, sceneY);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		eventSynchronizer.await();
		System.out.println(thread() + "... done.");
	}

	/**
	 * Fires a MOUSE_PRESSED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void mousePress(final int buttons) throws Throwable {
		System.out.println(thread() + "mousePress: (" + buttons + ") ...");
		EventSynchronizer<MouseEvent> eventSynchronizer = getEventSynchronizer(MouseEvent.MOUSE_PRESSED);
		System.out.println(thread() + "GOT the Synchronizer!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(thread() + "release now!");
					getRobot().mousePress(buttons);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		eventSynchronizer.await();
		System.out.println(thread() + "... done.");
	}

	/**
	 * Fires a MOUSE_RELEASED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void mouseRelease(final int buttons) throws Throwable {
		System.out.println(thread() + "mouseRelease: (" + buttons + ") ...");
		EventSynchronizer<MouseEvent> eventSynchronizer = getEventSynchronizer(MouseEvent.MOUSE_RELEASED);
		System.out.println(thread() + "GOT the Synchronizer!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(thread() + "release now!");
					getRobot().mouseRelease(buttons);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		eventSynchronizer.await();
		System.out.println(thread() + "... done.");
	}

	public synchronized void moveTo(final double sceneX, final double sceneY) throws Throwable {
		System.out.println(thread() + "moveTo: (" + sceneX + ", " + sceneY + ") ...");

		Point position = runAndWait(new RunnableWithResult<Point>() {
			@Override
			public Point run() {
				final double x = scene.getWindow().getX() + sceneX;
				final double y = scene.getWindow().getY() + sceneY;
				return new Point((int) x, (int) y);
			}
		});
		EventSynchronizer<MouseEvent> synchronizer = getEventSynchronizer(MouseEvent.MOUSE_ENTERED_TARGET);
		System.out.println(thread() + "GOT the Synchronizer!");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(thread() + "getRobot().mouseMove() ...");
					getRobot().mouseMove(position.x, position.y);
					System.out.println(thread() + " ...");
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		synchronizer.await();
		System.out.println(thread() + "... done.");
	}

	public synchronized void moveTo(final Node visual, final double localX, final double localY) throws Throwable {
		System.out.println(thread() + "moveTo: " + visual + " (" + localX + ", " + localY + ") ...");
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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					getRobot().mouseMove(position.x, position.y);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});
		synchronizer.await();
		System.out.println(thread() + "... done.");
	}

	/**
	 * Schedules the given {@link Runnable} on the JavaFX application thread and
	 * waits for its execution to finish.
	 *
	 * @param runnable
	 * @throws Throwable
	 */
	public synchronized void runAndWait(final Runnable runnable) throws Throwable {
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
	public synchronized <T> T runAndWait(final RunnableWithResult<T> runnableWithResult) throws Throwable {
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
	public synchronized <T, P1> T runAndWait(final RunnableWithResultAndParam<T, P1> runnableWithResult,
			final P1 param1) throws Throwable {
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

	public synchronized void waitForIdle() throws Throwable {
		runAndWait(new Runnable() {
			@Override
			public void run() {
				// dummy runnable
			}
		});
	}
}
