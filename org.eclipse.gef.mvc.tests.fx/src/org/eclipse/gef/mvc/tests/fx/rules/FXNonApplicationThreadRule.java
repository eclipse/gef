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
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.Set;
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
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

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

	/**
	 * Saves data for the last keyboard interaction so that it can be reused for
	 * subsequent keyboard interaction.
	 */
	public static class KeyInteraction {
		public Node target;
		public KeyCode keyCode;
	}

	/**
	 * An instance of {@link Modifiers} represents a set of modifier keys (shift,
	 * control, alt, meta). For convenience, {@link Modifiers#NONE} can be used to
	 * specify that no modifier keys are pressed, and {@link Modifiers#ALL} can be
	 * used to specify that all modifier keys are pressed.
	 * <p>
	 * Starting with a set of modifier keys, you can use the
	 * {@link Modifiers#alt(boolean)}, {@link Modifiers#control(boolean)},
	 * {@link Modifiers#meta(boolean)}, and {@link Modifiers#shift(boolean)} methods
	 * to create new sets of modifier keys with the changed attribute. Moreover, the
	 * {@link Modifiers#talt()}, {@link Modifiers#tcontrol()},
	 * {@link Modifiers#tmeta()}, {@link Modifiers#tshift()} methods can be used to
	 * create a set of modifier keys with the respective value toggled, i.e. <quote>
	 *
	 * <pre>
	 * // press "A" while control, meta, and shift modifier keys are pressed
	 * keyPress(targetNode, KeyCode.A, Modifiers.ALL.talt());
	 * // press "B" while meta and shift modifier keys are pressed
	 * keyPress(targetNode, KeyCode.B, Modifiers.NONE.tmeta().tshift());
	 * // move mouse while alt and meta modifier keys are pressed
	 * mouseMove(targetNode, sceneX, sceneY, Modifiers.ALL.tcontrol().tshift());
	 * </pre>
	 *
	 * </quote>
	 */
	public static class Modifiers {
		public static final Modifiers NONE = new Modifiers();
		public static final Modifiers ALL = new Modifiers(true, true, true, true);

		public boolean shift;
		public boolean control;
		public boolean alt;
		public boolean meta;

		public Modifiers() {
		}

		public Modifiers(boolean alt, boolean control, boolean meta, boolean shift) {
			this.shift = shift;
			this.control = control;
			this.alt = alt;
			this.meta = meta;
		}

		public Modifiers(Modifiers o) {
			this.shift = o.shift;
			this.control = o.control;
			this.alt = o.alt;
			this.meta = o.meta;
		}

		public Modifiers alt(boolean alt) {
			Modifiers copy = getCopy();
			copy.alt = alt;
			return copy;
		}

		public Modifiers control(boolean control) {
			Modifiers copy = getCopy();
			copy.control = control;
			return copy;
		}

		public Modifiers getCopy() {
			return new Modifiers(this);
		}

		public Modifiers meta(boolean meta) {
			Modifiers copy = getCopy();
			copy.meta = meta;
			return copy;
		}

		public Modifiers shift(boolean shift) {
			Modifiers copy = getCopy();
			copy.shift = shift;
			return copy;
		}

		public Modifiers shortcut(boolean shortcut) {
			Modifiers copy = getCopy();
			if (System.getProperty("os.name").startsWith("Mac")) {
				copy.meta = shortcut;
			} else {
				copy.control = shortcut;
			}
			return copy;
		}

		public Modifiers talt() {
			return alt(!alt);
		}

		public Modifiers tcontrol() {
			return control(!control);
		}

		public Modifiers tmeta() {
			return meta(!meta);
		}

		public Modifiers tshift() {
			return shift(!shift);
		}
	}

	/**
	 * Saves mouse interaction data, so that it can be reused for subsequent mouse
	 * interaction.
	 */
	public static class MouseInteraction {
		public Node target;
		public double sceneX;
		public double sceneY;
	}

	public interface RunnableWithResult<T> {
		public T run();
	}

	public interface RunnableWithResultAndParam<T, P1> {
		public T run(P1 param1);
	}

	/**
	 * A {@link SyncEvent} can be used to ensure that all events have been
	 * processed.
	 * <ol>
	 * <li>Register an event filter for {@link SyncEvent#SYNC} that notifies when it
	 * is executed.
	 * <li>Fire a {@link SyncEvent#SYNC}.
	 * <li>Wait for the notification from the event filter.
	 * </ol>
	 * This mechanism is implemented by {@link FxSynthRobot#waitForIdle()}.
	 */
	public static class SyncEvent extends Event {
		private static final long serialVersionUID = 1L;

		/**
		 * A {@link SyncEvent#SYNC} event can be used to ensure that all events have
		 * been processed.
		 */
		public static final EventType<SyncEvent> SYNC = new EventType<>(EventType.ROOT);

		/**
		 * Creates a new {@link SyncEvent#SYNC}.
		 */
		public SyncEvent() {
			super(SYNC);
		}

		/**
		 * Creates a new {@link SyncEvent} of the given {@link EventType}.
		 *
		 * @param eventType
		 *            The {@link EventType} of the newly created {@link SyncEvent}.
		 */
		public SyncEvent(EventType<? extends SyncEvent> eventType) {
			super(eventType);
		}
	}

	private static final long TIMEOUT_MILLIS = 5000;

	private static boolean initializedJavaFxToolkit = false;

	private synchronized static void initFX() throws InterruptedException {
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

	private MouseInteraction lastMouseInteraction;
	private KeyInteraction lastKeyInteraction;
	private Scene scene;
	private JFXPanel panel;
	private JFrame jFrame;
	private Set<Event> testEvents = new HashSet<>();

	@Override
	public Statement apply(final Statement base, Description description) {
		if (Platform.isFxApplicationThread() || SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException(
					"Tests may not be executed from FX application or AWT event dispatching thread.");
		}
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				initFX();
				try {
					base.evaluate();
				} finally {
					runAndWait(() -> {
						if (panel != null) {
							panel.setScene(null);
						}
						if (jFrame != null) {
							jFrame.setVisible(false);
							jFrame = null;
						}
						scene = null;
						panel = null;
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
	 * @throws Throwable
	 * @throws InterruptedException
	 * @throws AWTException
	 */
	public Scene createScene(final Parent root, final double width, final double height) throws Throwable {
		scene = runAndWait(new RunnableWithResult<Scene>() {
			@Override
			public Scene run() {
				// hook viewer to scene
				panel = new JFXPanel();
				Scene scene = new Scene(root, width, height);
				panel.setScene(scene);
				jFrame = new JFrame();
				jFrame.setBounds(0, 0, (int) width, (int) height);
				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jFrame.setLayout(new BorderLayout());
				jFrame.setLocationRelativeTo(null);
				jFrame.setVisible(true);
				jFrame.setContentPane(panel);

				Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						if (e instanceof RuntimeException) {
							throw ((RuntimeException) e);
						}
						throw new RuntimeException(e);
					}
				});

				return scene;
			}
		});
		scene.addEventFilter(Event.ANY, e -> {
			if (!testEvents.contains(e)) {
				e.consume();
			} else {
				testEvents.remove(e);
			}
		});
		return scene;
	}

	private void fireEvent(EventTarget target, Event event) {
		testEvents.add(event);
		Event.fireEvent(target, event);
	}

	public JFXPanel getPanel() {
		return panel;
	}

	/**
	 * Fires a KEY_PRESSED event and waits for its processing to finish. See
	 * {@link Robot#keyPress(int)} for more information.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void keyPress(final Node target, final KeyCode keycode) throws Throwable {
		keyPress(target, keycode, Modifiers.NONE);
	}

	/**
	 * Fires a newly created {@link KeyEvent} of type {@link KeyEvent#KEY_PRESSED}
	 * to the given target {@link Node}. The given {@link KeyCode} and
	 * {@link Modifiers} are used to construct the {@link KeyEvent}.
	 * <p>
	 * The target {@link Node} and the {@link KeyCode} is saved for subsequent
	 * {@link #keyRelease(Modifiers)} calls.
	 *
	 * @param target
	 *            The target {@link Node} that the event is send to.
	 * @param key
	 *            The {@link KeyCode} for the {@link KeyEvent}.
	 * @param mods
	 *            The {@link Modifiers} for the {@link KeyEvent}.
	 */
	public void keyPress(final Node target, final KeyCode key, final Modifiers mods) {
		waitForIdle();
		// save key interaction data
		lastKeyInteraction = new KeyInteraction();
		lastKeyInteraction.target = target;
		lastKeyInteraction.keyCode = key;
		run(() -> {
			fireEvent(target, new KeyEvent(target, target, KeyEvent.KEY_PRESSED, key.toString(), key.toString(), key,
					mods.shift, mods.control, mods.alt, mods.meta));
		});
		waitForIdle();
	}

	/**
	 * Fires a KEY_RELEASED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void keyRelease() throws Throwable {
		keyRelease(Modifiers.NONE);
	}

	/**
	 * Fires a newly created {@link KeyEvent} of type {@link KeyEvent#KEY_RELEASED}
	 * to the target {@link Node} of the last
	 * {@link #keyPress(Node, KeyCode, Modifiers)}. The {@link KeyCode} of the last
	 * {@link #keyPress(Node, KeyCode, Modifiers)} is reused, however, the given
	 * {@link Modifiers} are used for the new {@link KeyEvent}.
	 *
	 * @param mods
	 *            The {@link Modifiers} for the {@link KeyEvent}.
	 */
	public void keyRelease(final Modifiers mods) {
		waitForIdle();
		run(() -> {
			fireEvent(lastKeyInteraction.target,
					new KeyEvent(lastKeyInteraction.target, lastKeyInteraction.target, KeyEvent.KEY_RELEASED,
							lastKeyInteraction.keyCode.toString(), lastKeyInteraction.keyCode.toString(),
							lastKeyInteraction.keyCode, mods.shift, mods.control, mods.alt, mods.meta));
		});
		waitForIdle();
		lastKeyInteraction = null;
	}

	/**
	 * Fires a MOUSE_DRAGGED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void mouseDrag(final double sceneX, final double sceneY) throws Throwable {
		mouseDrag(sceneX, sceneY, Modifiers.NONE);
	}

	/**
	 * Fires a newly created {@link MouseEvent} of type
	 * {@link MouseEvent#MOUSE_DRAGGED} to the target {@link Node} of the last mouse
	 * interaction.
	 *
	 * @param sceneX
	 *            The final x-coordinate (in scene) for the drag.
	 * @param sceneY
	 *            The final y-coordinate (in scene) for the drag.
	 * @param mods
	 *            The {@link Modifiers} for the {@link MouseEvent}.
	 */
	public void mouseDrag(final double sceneX, final double sceneY, final Modifiers mods) {
		waitForIdle();
		// save mouse interaction data
		lastMouseInteraction.sceneX = sceneX;
		lastMouseInteraction.sceneY = sceneY;
		run(() -> {
			Point2D local = lastMouseInteraction.target.sceneToLocal(sceneX, sceneY);
			Point2D screen = lastMouseInteraction.target.localToScreen(local.getX(), local.getY());
			fireEvent(lastMouseInteraction.target,
					new MouseEvent(lastMouseInteraction.target, lastMouseInteraction.target, MouseEvent.MOUSE_DRAGGED,
							local.getX(), local.getY(), screen.getX(), screen.getY(), MouseButton.PRIMARY, 0,
							mods.shift, mods.control, mods.alt, mods.meta, true, false, false, false, false, false,
							new PickResult(lastMouseInteraction.target, sceneX, sceneY)));
		});
		waitForIdle();
	}

	public synchronized void mouseMove(final Node target, final double sceneX, final double sceneY) throws Throwable {
		mouseMove(target, sceneX, sceneY, Modifiers.NONE);
		waitForIdle();
	}

	/**
	 * Fires a newly created {@link MouseEvent} of type
	 * {@link MouseEvent#MOUSE_MOVED} to the given target {@link Node}.
	 *
	 * @param sceneX
	 *            The final x-coordinate (in scene) for the drag.
	 * @param sceneY
	 *            The final y-coordinate (in scene) for the drag.
	 * @param mods
	 *            The {@link Modifiers} for the {@link MouseEvent}.
	 */
	public void mouseMove(final Node target, final double sceneX, final double sceneY, final Modifiers mods) {
		waitForIdle();
		// save mouse interaction data
		lastMouseInteraction = new MouseInteraction();
		lastMouseInteraction.target = target;
		lastMouseInteraction.sceneX = sceneX;
		lastMouseInteraction.sceneY = sceneY;
		run(() -> {
			Point2D local = target.sceneToLocal(sceneX, sceneY);
			Point2D screen = target.localToScreen(local.getX(), local.getY());
			fireEvent(target,
					new MouseEvent(target, target, MouseEvent.MOUSE_MOVED, local.getX(), local.getY(), screen.getX(),
							screen.getY(), MouseButton.NONE, 0, mods.shift, mods.control, mods.alt, mods.meta, false,
							false, false, false, false, false, new PickResult(target, sceneX, sceneY)));
		});
		waitForIdle();
	}

	/**
	 * Fires a MOUSE_PRESSED event and waits for its processing to finish. The given
	 * buttons mask can be composed by adding the following constants:
	 * <ul>
	 * <li>{@link InputEvent#BUTTON1_DOWN_MASK}
	 * <li>{@link InputEvent#BUTTON2_DOWN_MASK}
	 * <li>{@link InputEvent#BUTTON3_DOWN_MASK}
	 * </ul>
	 * See {@link Robot#mousePress(int)} for more information.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void mousePress() throws Throwable {
		mousePress(Modifiers.NONE);
	}

	/**
	 * Fires a newly created {@link MouseEvent} of type
	 * {@link MouseEvent#MOUSE_PRESSED} to the target {@link Node} of the last mouse
	 * interaction.
	 *
	 * @param mods
	 *            The {@link Modifiers} for the {@link MouseEvent}.
	 */
	public void mousePress(final Modifiers mods) {
		waitForIdle();
		run(() -> {
			Point2D local = lastMouseInteraction.target.sceneToLocal(lastMouseInteraction.sceneX,
					lastMouseInteraction.sceneY);
			Point2D screen = lastMouseInteraction.target.localToScreen(local.getX(), local.getY());
			fireEvent(lastMouseInteraction.target,
					new MouseEvent(lastMouseInteraction.target, lastMouseInteraction.target, MouseEvent.MOUSE_PRESSED,
							local.getX(), local.getY(), screen.getX(), screen.getY(), MouseButton.PRIMARY, 1,
							mods.shift, mods.control, mods.alt, mods.meta, true, false, false, false, false, false,
							new PickResult(lastMouseInteraction.target, lastMouseInteraction.sceneX,
									lastMouseInteraction.sceneY)));
		});
		waitForIdle();
	}

	/**
	 * Fires a MOUSE_RELEASED event and waits for its processing to finish.
	 *
	 * @param robot
	 * @param keycode
	 * @throws InterruptedException
	 */
	public synchronized void mouseRelease() throws Throwable {
		mouseRelease(Modifiers.NONE);
	}

	/**
	 * Fires a newly created {@link MouseEvent} of type
	 * {@link MouseEvent#MOUSE_RELEASED} to the target {@link Node} of the last
	 * mouse interaction.
	 *
	 * @param mods
	 *            The {@link Modifiers} for the {@link MouseEvent}.
	 */
	public void mouseRelease(final Modifiers mods) {
		waitForIdle();
		run(() -> {
			Point2D local = lastMouseInteraction.target.sceneToLocal(lastMouseInteraction.sceneX,
					lastMouseInteraction.sceneY);
			Point2D screen = lastMouseInteraction.target.localToScreen(local.getX(), local.getY());
			fireEvent(lastMouseInteraction.target,
					new MouseEvent(lastMouseInteraction.target, lastMouseInteraction.target, MouseEvent.MOUSE_RELEASED,
							local.getX(), local.getY(), screen.getX(), screen.getY(), MouseButton.PRIMARY, 1,
							mods.shift, mods.control, mods.alt, mods.meta, false, false, false, false, false, false,
							new PickResult(lastMouseInteraction.target, lastMouseInteraction.sceneX,
									lastMouseInteraction.sceneY)));
		});
		waitForIdle();
	}

	/**
	 * Executes the given {@link Runnable} on the JavaFX application thread. If the
	 * current thread is the JavaFX application thread, then the given
	 * {@link Runnable} is executed in-line. Otherwise, the given {@link Runnable}
	 * is passed to a {@link Platform#runLater(Runnable)}.
	 *
	 * @param r
	 *            The {@link Runnable} to execute on the JavaFX application thread.
	 */
	protected void run(Runnable r) {
		if (Platform.isFxApplicationThread()) {
			// run right now
			r.run();
		} else {
			// run later
			Platform.runLater(r);
		}
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
		run(() -> {
			try {
				runnable.run();
			} catch (Throwable t) {
				throwableRef.set(t);
			} finally {
				latch.countDown();
			}
		});
		wait(latch);
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
		run(() -> {
			try {
				resultRef.set(runnableWithResult.run());
			} catch (Throwable t) {
				throwableRef.set(t);
			} finally {
				latch.countDown();
			}
		});
		wait(latch);
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
		run(() -> {
			try {
				resultRef.set(runnableWithResult.run(param1));
			} catch (Throwable t) {
				throwableRef.set(t);
			} finally {
				latch.countDown();
			}
		});
		wait(latch);
		Throwable throwable = throwableRef.get();
		if (throwable != null) {
			throw throwable;
		}
		return resultRef.get();
	}

	/**
	 * If the current thread is the JavaFX application thread, busy waiting is
	 * performed, i.e. {@link Thread#sleep(long)} is called in-between checking of a
	 * {@link CountDownLatch}. Otherwise,
	 * {@link CountDownLatch#await(long, TimeUnit)} is used to wait. In either case,
	 * an exception is thrown if the timeout of {@link #TIMEOUT_MILLIS} is exceeded.
	 *
	 * @param latch
	 *            The {@link CountDownLatch} that is waited for.
	 */
	protected void wait(CountDownLatch latch) {
		try {
			if (Platform.isFxApplicationThread()) {
				// busy waiting
				long startMillis = System.currentTimeMillis();
				while (latch.getCount() > 0) {
					Thread.sleep(100);
					if ((System.currentTimeMillis() - startMillis) > TIMEOUT_MILLIS) {
						throw new IllegalStateException("TIMEOUT");
					}
				}
			} else {
				// sleepy waiting
				latch.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ensures that processing of previously fired events is finished by firing and
	 * waiting for a {@link SyncEvent}.
	 * <p>
	 * This method should only be called after firing an {@link Event} that is not
	 * supported by this {@link FXNonApplicationThreadRule}, e.g. by using
	 * {@link Event#fireEvent(javafx.event.EventTarget, Event)}.
	 */
	public void waitForIdle() {
		final CountDownLatch latch = new CountDownLatch(1);
		EventHandler<? super SyncEvent> eventFilter = event -> {
			latch.countDown();
		};
		run(() -> {
			scene.addEventFilter(SyncEvent.SYNC, eventFilter);
		});
		run(() -> {
			fireEvent(scene, new SyncEvent());
		});
		wait(latch);
		run(() -> {
			scene.removeEventFilter(SyncEvent.SYNC, eventFilter);
		});
	}

}
