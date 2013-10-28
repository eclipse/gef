/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * An instance of the Scene class is the entry point for scene graph related
 * behavior on top of SWT. An SWT Composite has to be provided for a Scene to
 * embed into.
 * 
 * @author mwienand
 * 
 */
public class Scene extends Canvas {

	/**
	 * Default FPS to use for the {@link PulseThread}.
	 */
	public static final int DEFAULT_FPS = 24;

	/**
	 * The root of the scene graph.
	 */
	private IParent root;

	/**
	 * The {@link INode} that currently has mouse focus. If a node has mouse
	 * focus, all {@link MouseEvent}s are dispatched to that node.
	 */
	private INode mouseTarget;

	/**
	 * The {@link INode} that currently has keyboard focus. If a node has
	 * keyboard focus, all {@link KeyEvent}s are dispatched to that node.
	 */
	private INode focusTarget;

	/**
	 * If the mouse pointer is over an {@link INode} we store that node here.
	 */
	private INode mousePointerTarget;

	/*
	 * TODO: pulse-updates
	 * 
	 * We need to create a pulse thread, i.e. a thread that will call pulse
	 * listeners every X millis. JavaFX uses 60Hz frequency for pulses. I do not
	 * think we need such a fine granularity (which is typical for games!). 30Hz
	 * should be enough for SwtFX.
	 * 
	 * Although JavaFX uses the underlying toolkit to generate the pulse, I want
	 * to create one pulse thread per Scene. In the real world, you will rarely
	 * feel the need for more than a couple of Scenes in your application
	 * (main-editor, outline-view).
	 * 
	 * The scene will then register itself as a pulse-listener on the newly
	 * created pulse-thread. Inside that pulse-listener, a layout pass is
	 * performed and SWT is asked to redraw the Scene. Rendering is fully
	 * controlled by SWT, we just tell SWT when we need to redraw.
	 * 
	 * Animations could either register on the Scene or also on the pulse
	 * thread.
	 */

	/**
	 * Synchronizes layout and animation passes.
	 */
	private PulseThread pulseThread;

	/*
	 * TODO: layout-roots
	 * 
	 * The root IParent of the scene graph is coupled to the Scene's width and
	 * height. When the Scene's width or height changes, that change is
	 * propagated to the scene graph root and a layout pass is performed on the
	 * root.
	 * 
	 * Additionally, there are so called layout-roots which are *unmanaged*
	 * IParents anywhere in the scene graph. As you know, an unmanaged node is
	 * not laid out by its parent, i.e. its position and size are not set by its
	 * parent. That's why layout requests will not propagate above unmanaged
	 * parents. But as all parents (unmanaged ones, too) are responsible for
	 * laying out their managed children, we need to trigger their layout()
	 * method in a layout pass if one of their children requested re-layouting.
	 * 
	 * That's why we manage a dirty-layout-roots list. All layout-roots will
	 * mark themselves as needing to re-layout when a child requests
	 * re-layouting. For the sake of simplicity, we can safely add the scene
	 * graph root to the dirty-layout-roots list when the Scene's width or
	 * height changes. So that a layout pass is simply calling the layout()
	 * method on all dirty-layout-roots.
	 * 
	 * JavaFX uses two dirty-layout-roots lists, because they allow one
	 * layout-pass to mark nodes as needing a re-layout. I cannot imagine a
	 * situation where one layout pass is not enough. Maybe we have to run into
	 * bugs before implementing it the same way.
	 */

	// private List<IParent> layoutRoots;

	/**
	 * <p>
	 * Constructs a new {@link Scene}, embedded into the passed-in SWT
	 * {@link Composite}, using the passed-in {@link IParent} as the scene graph
	 * root.
	 * </p>
	 * <p>
	 * If the size of the Composite changes, the scene graph root is updated
	 * accordingly via {@link INode#resize(double, double)} and
	 * {@link IParent#layout()}.
	 * </p>
	 * <p>
	 * All SWT events are wrapped in hierarchical organized GEF4 Event objects
	 * and forwarded to the scene graph root.
	 * </p>
	 * 
	 * @param swtComposite
	 * @param root
	 */
	public Scene(final Composite swtComposite, final IParent root) {
		super(swtComposite, SWT.NONE | SWT.NO_RADIO_GROUP);
		this.root = root;
		root.setScene(this);
		pulseThread = new PulseThread(this, DEFAULT_FPS);
		pulseThread.getListeners().add(new IPulseListener() {
			@Override
			public void handlePulse(long elapsedMs) {
				// TODO: oddity in PulseThread => check for elapsedMs == 0
				if (elapsedMs == 0) {
					return;
				}
				// System.out.println(pulseThread.getRealFps());
				onPulse(elapsedMs);
			}
		});
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle clientArea = getClientArea();
				root.resize(clientArea.width, clientArea.height);
				root.layout();
				redraw();
			}
		});
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GraphicsContext gc = new GraphicsContext(e.gc);
				root.renderFigures(gc);
				gc.cleanUp();
			}
		});
		new SwtEventForwarder(this, this);
		pulseThread.start();
	}

	/**
	 * @return the {@link #focusTarget}
	 */
	public INode getFocusTarget() {
		return focusTarget;
	}

	INode getMousePointerTarget() {
		return mousePointerTarget;
	}

	/**
	 * @return the {@link #mouseTarget}
	 */
	public INode getMouseTarget() {
		return mouseTarget;
	}

	public PulseThread getPulseThread() {
		return pulseThread;
	}

	/**
	 * @return the scene graph root {@link INode node}
	 */
	public IParent getRoot() {
		return root;
	}

	protected void onPulse(long elapsedMs) {
		// if (!dirtyLayoutRoots.isEmpty()) {
		// root.layout();
		// redraw();
		// }
		// System.out.println("FPS: " + pulseThread.getRealFps());

		// guard against SWT disposal
		Display display = getDisplay();
		if (display.isDisposed()) {
			return;
		}

		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				root.layout();
				redraw();
			}
		});
	}

	public void refreshVisuals() {
		// TODO: this should not be a noop!
		// root.layout();
		// redraw();
	}

	/**
	 * Sets the {@link #focusTarget} to the passed-in {@link INode}. You can
	 * pass-in <code>null</code> if you want to disable keyboard focus
	 * dispatching. Note that you can also set focus to a node via
	 * {@link INode#requestFocus()}.
	 * 
	 * @param focusTarget
	 *            the new {@link #focusTarget}, or <code>null</code> to disable
	 *            focus dispatching
	 */
	public void setFocusTarget(INode focusTarget) {
		this.focusTarget = focusTarget;
		if (focusTarget instanceof SwtControlAdapterNode) {
			SwtControlAdapterNode<?> cn = (SwtControlAdapterNode<?>) focusTarget;
			cn.getControl().forceFocus();
		} else {
			forceFocus();
		}
	}

	void setMousePointerTarget(INode n) {
		mousePointerTarget = n;
	}

	/**
	 * Sets the {@link #mouseTarget} to the passed-in {@link INode}. You can
	 * pass-in <code>null</code> if you want to disable mouse focus dispatching.
	 * Note that the {@link #mouseTarget} is assigned automatically if the user
	 * presses a mouse button over an {@link INode}. The {@link #mouseTarget} is
	 * cleared automatically when the user releases the pressed button.
	 * 
	 * @param mouseTarget
	 */
	public void setMouseTarget(INode mouseTarget) {
		this.mouseTarget = mouseTarget;
	}

}
