/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import java.util.Set;

import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.binding.Binding;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link IComputationStrategy} is responsible for computing anchor
 * positions based on the anchorage {@link Node}, the anchored {@link Node}, and
 * respective (strategy-specific) {@link Parameter parameters}. ).
 */
public interface IComputationStrategy {

	/**
	 * Base class for all computation parameters that can be passed to an
	 * {@link IComputationStrategy}.
	 *
	 * @param <T>
	 *            The parameter value type.
	 */
	public abstract class Parameter<T> extends ObjectPropertyBase<T> {

		private boolean optional;
		private ObservableValue<? extends T> bindingTarget;

		/**
		 * Creates a new mandatory {@link Parameter}.
		 */
		public Parameter() {
			this(false);
		}

		/**
		 * Creates a new Parameter, which is optional, i.e. whose value may be
		 * <code>null</code>.
		 *
		 * @param optional
		 *            Whether this parameter is optional or not.
		 */
		public Parameter(boolean optional) {
			this.optional = optional;
		}

		@Override
		public void bind(ObservableValue<? extends T> newObservable) {
			super.bind(newObservable);
			this.bindingTarget = newObservable;
		}

		@Override
		public Object getBean() {
			// no bean by default
			return null;
		};

		@Override
		public String getName() {
			// use type name as property name
			return getClass().getSimpleName();
		}

		/**
		 * If this parameter is bound, can be used to invalidate the underlying
		 * binding, so that the value is re-computed.
		 */
		public void invalidateBinding() {
			if (isBound() && bindingTarget instanceof Binding) {
				((Binding<? extends T>) bindingTarget).invalidate();
			}
		}

		/**
		 * Indicates whether this parameter is optional
		 *
		 * @return <code>true</code> if the parameter is optional,
		 *         <code>false</code> otherwise.
		 */
		public boolean isOptional() {
			return optional;
		}

		@Override
		public void unbind() {
			this.bindingTarget = null;
			super.unbind();
		}
	}

	/**
	 * Computes an anchor position based on the given anchorage visual, anchored
	 * visual, and anchored reference point.
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 * @param anchored
	 *            The anchored visual.
	 * @param parameters
	 *            The available computation parameters. strategy.
	 * @return The anchor position.
	 */
	public Point computePositionInScene(Node anchorage, Node anchored,
			Set<Parameter<?>> parameters);

	/**
	 * Returns the types of parameters required by this strategy.
	 *
	 * @return The parameters required by this strategy.
	 */
	public Set<Class<? extends Parameter<?>>> getRequiredParameters();

}