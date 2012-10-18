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
package org.eclipse.gef4.graphics.render;

import java.util.Stack;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.Image;

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

	private void activateProperties() {
		State state = currentState();
		state.blitProperties.activate();
		state.canvasProperties.activate();
		state.drawProperties.activate();
		state.fillProperties.activate();
		state.writeProperties.activate();
	}

	public void blit(Image image) {
		currentState().canvasProperties.applyOn(this);
		currentState().blitProperties.applyOn(this, image);
	}

	public IBlitProperties blitProperties() {
		return currentState().blitProperties;
	}

	public ICanvasProperties canvasProperties() {
		return currentState().canvasProperties;
	}

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
		State state = currentState();
		state.blitProperties.cleanUp(this);
		state.canvasProperties.cleanUp(this);
		state.drawProperties.cleanUp(this);
		state.fillProperties.cleanUp(this);
		state.writeProperties.cleanUp(this);
	}

	/**
	 * Returns the current state of this {@link AbstractGraphics}.
	 * 
	 * @return the current state of this {@link AbstractGraphics}
	 */
	protected State currentState() {
		return states.peek();
	}

	private void deactivateProperties(State s) {
		s.blitProperties.deactivate();
		s.canvasProperties.deactivate();
		s.drawProperties.deactivate();
		s.fillProperties.deactivate();
		s.writeProperties.deactivate();
	}

	public void draw(ICurve curve) {
		draw(curve.toPath());
	}

	public void draw(Path path) {
		currentState().canvasProperties.applyOn(this);
		currentState().drawProperties.applyOn(this, path);
	}

	public IDrawProperties drawProperties() {
		return currentState().drawProperties;
	}

	public void fill(IMultiShape multiShape) {
		fill(multiShape.toPath());
	}

	public void fill(IShape shape) {
		fill(shape.toPath());
	}

	public void fill(Path path) {
		currentState().canvasProperties.applyOn(this);
		currentState().fillProperties.applyOn(this, path);
	}

	public IFillProperties fillProperties() {
		return currentState().fillProperties;
	}

	private void initProperties() {
		State state = currentState();
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
		deactivateProperties(currentState());
		states.pop();
		activateProperties();
	}

	/**
	 * <p>
	 * Utility method that constructs an initial {@link State} object from the
	 * given {@link IGraphicsProperties} and pushes it to the {@link #states}
	 * stack.
	 * </p>
	 * 
	 * <p>
	 * This method does also activate all {@link IGraphicsProperties} before
	 * initializing them (see {@link IGraphicsProperties#activate()}.
	 * </p>
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
		// push initial state
		states.push(new State(canvasProperties, drawProperties, fillProperties,
				blitProperties, writeProperties));
		initProperties();
	}

	public void pushState() {
		if (states.isEmpty()) {
			throw new IllegalStateException(
					"No initial State pushed! The IGraphics is responsible for pushing an initial State on construction.");
		}
		State priorState = currentState();
		states.push(priorState.getCopy());
		deactivateProperties(priorState);
		initProperties();
	}

	public void restoreState() {
		popState();
		pushState();
	}

	public void write(String text) {
		currentState().canvasProperties.applyOn(this);
		currentState().writeProperties.applyOn(this, text);
	}

	public IWriteProperties writeProperties() {
		return currentState().writeProperties;
	}

}
