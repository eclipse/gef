/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics;

import java.util.Stack;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;

/**
 * The AbstractGraphics class partially implements the {@link IGraphics}
 * interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractGraphics implements IGraphics {

	/**
	 * The State class contains a set of the various {@link IGraphicsProperties}
	 * . It is used by the {@link AbstractGraphics#pushState()} and
	 * {@link AbstractGraphics#popState()} methods.
	 * 
	 * @author mwienand
	 * 
	 */
	protected static class State {
		/**
		 * The {@link ICanvasProperties} associated with this {@link State}.
		 */
		public ICanvasProperties canvasProperties;

		/**
		 * The {@link IDrawProperties} associated with this {@link State}.
		 */
		public IDrawProperties drawProperties;

		/**
		 * The {@link IBlitProperties} associated with this {@link State}.
		 */
		public IBlitProperties blitProperties;

		/**
		 * The {@link IWriteProperties} associated with this {@link State}.
		 */
		public IWriteProperties writeProperties;

		/**
		 * The {@link IFillProperties} associated with this {@link State}.
		 */
		public IFillProperties fillProperties;

		/**
		 * Crates a new {@link State} object from the passed-in values, copying
		 * the passed-in values to be able to assure, that they are not changed
		 * from the outside.
		 * 
		 * @param canvasProperties
		 * @param drawProperties
		 * @param fillProperties
		 * @param imageProperties
		 * @param textProperties
		 */
		public State(ICanvasProperties canvasProperties,
				IDrawProperties drawProperties, IFillProperties fillProperties,
				IBlitProperties imageProperties, IWriteProperties textProperties) {
			this.canvasProperties = canvasProperties.getCopy();
			this.drawProperties = drawProperties.getCopy();
			this.fillProperties = fillProperties.getCopy();
			this.blitProperties = imageProperties.getCopy();
			this.writeProperties = textProperties.getCopy();
		}

		/**
		 * Returns a copy of this {@link State}, copying its various
		 * {@link IGraphicsProperties}.
		 * 
		 * @return a copy of this {@link State}, copying its various
		 *         {@link IGraphicsProperties}
		 */
		public State getCopy() {
			return new State(canvasProperties, drawProperties, fillProperties,
					blitProperties, writeProperties);
		}
	}

	/**
	 * A {@link Stack} of {@link State}s used to implement the
	 * {@link AbstractGraphics#pushState()} and
	 * {@link AbstractGraphics#popState()} methods.
	 */
	protected Stack<State> states = new Stack<State>();

	public void blit(Image image) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().blitProperties.applyOn(this);
		doBlit(image);
	}

	/**
	 * <p>
	 * Cleans up this {@link AbstractGraphics} so that all the
	 * {@link IGraphicsProperties#cleanUp(IGraphics)} method is called.
	 * </p>
	 */
	public void cleanUp() {
		// clear states stack
		while (states.size() > 1) {
			cleanUpProperties();
			popState();
		}

		// clean up initial state
		cleanUpProperties();
	}

	private void cleanUpProperties() {
		State state = getCurrentState();
		state.blitProperties.cleanUp(this);
		state.canvasProperties.cleanUp(this);
		state.drawProperties.cleanUp(this);
		state.fillProperties.cleanUp(this);
		state.writeProperties.cleanUp(this);
	}

	/**
	 * Does the actual drawing of the given {@link Image}, respecting the
	 * current state of this {@link AbstractGraphics}.
	 * 
	 * @param image
	 */
	protected abstract void doBlit(Image image);

	/**
	 * Does the actual drawing of the given {@link ICurve}, respecting the
	 * current state of this {@link AbstractGraphics}.
	 * 
	 * @param curve
	 */
	protected abstract void doDraw(ICurve curve);

	/**
	 * Does the actual drawing of the given {@link Path}, respecting the current
	 * state of this {@link AbstractGraphics}.
	 * 
	 * @param path
	 */
	protected abstract void doDraw(Path path);

	/**
	 * Does the actual filling of the given {@link IMultiShape}, respecting the
	 * current state of this {@link AbstractGraphics}.
	 * 
	 * @param multishape
	 */
	protected abstract void doFill(IMultiShape multishape);

	/**
	 * Does the actual filling of the given {@link IShape}, respecting the
	 * current state of this {@link AbstractGraphics}.
	 * 
	 * @param shape
	 */
	protected abstract void doFill(IShape shape);

	/**
	 * Does the actual filling of the given {@link Path}, respecting the current
	 * state of this {@link AbstractGraphics}.
	 * 
	 * @param path
	 */
	protected abstract void doFill(Path path);

	/**
	 * Does the actual drawing of the given {@link String}, respecting the
	 * current state of this {@link AbstractGraphics}.
	 * 
	 * @param text
	 */
	protected abstract void doWrite(String text);

	public void draw(ICurve curve) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().drawProperties.applyOn(this);
		doDraw(curve);
	}

	public void draw(Path path) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().drawProperties.applyOn(this);
		doDraw(path);
	}

	public void fill(IMultiShape multiShape) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().fillProperties.applyOn(this);
		doFill(multiShape);
	}

	public void fill(IShape shape) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().fillProperties.applyOn(this);
		doFill(shape);
	}

	public void fill(Path path) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().fillProperties.applyOn(this);
		doFill(path);
	}

	public IBlitProperties getBlitProperties() {
		return getCurrentState().blitProperties;
	}

	public ICanvasProperties getCanvasProperties() {
		return getCurrentState().canvasProperties;
	}

	/**
	 * Returns the current state of this {@link AbstractGraphics}.
	 * 
	 * @return the current state of this {@link AbstractGraphics}
	 */
	protected State getCurrentState() {
		return states.peek();
	}

	public IDrawProperties getDrawProperties() {
		return getCurrentState().drawProperties;
	}

	public IFillProperties getFillProperties() {
		return getCurrentState().fillProperties;
	}

	public IWriteProperties getWriteProperties() {
		return getCurrentState().writeProperties;
	}

	private void initProperties() {
		State state = getCurrentState();
		state.blitProperties.init(this);
		state.canvasProperties.init(this);
		state.drawProperties.init(this);
		state.fillProperties.init(this);
		state.writeProperties.init(this);
	}

	public void popState() {
		if (states.size() == 1) {
			throw new IllegalStateException(
					"You have to push a State first.");
		}
		cleanUpProperties();
		states.pop();
	}

	/**
	 * Utility method that constructs an initial {@link State} object from the
	 * given {@link IGraphicsProperties} and pushes it to the {@link #states}
	 * stack.
	 * 
	 * @param canvasProperties
	 * @param drawProperties
	 * @param fillProperties
	 * @param blitProperties
	 * @param writeProperties
	 */
	protected void pushInitialState(ICanvasProperties canvasProperties,
			IDrawProperties drawProperties, IFillProperties fillProperties,
			IBlitProperties blitProperties, IWriteProperties writeProperties) {
		states.push(new State(canvasProperties, drawProperties, fillProperties,
				blitProperties, writeProperties));
		initProperties();
	}

	public void pushState() {
		if (states.isEmpty()) {
			throw new IllegalStateException(
					"You have to push an initial State when constructing the IGraphics.");
		}
		states.push(getCurrentState().getCopy());
		initProperties();
	}

	public void write(String text) {
		getCurrentState().canvasProperties.applyOn(this);
		getCurrentState().writeProperties.applyOn(this);
		doWrite(text);
	}

}
