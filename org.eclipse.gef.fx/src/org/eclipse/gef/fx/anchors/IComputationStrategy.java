/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.anchors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.geometry.planar.Point;

import javafx.beans.binding.Binding;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link IComputationStrategy} is responsible for computing anchor
 * positions based on the anchorage {@link Node}, the anchored {@link Node}, and
 * respective (strategy-specific) {@link Parameter parameters}.
 *
 * @author anyssen
 * @author mwienand
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

		/**
		 * Indicates whether the parameter value can be shared to compute
		 * positions of all attached anchors or not.
		 */
		public enum Kind {
			/**
			 * Indicates that the parameter value may be shared to compute the
			 * position for all attached {@link AnchorKey}, as the value depends
			 * on the anchorage {@link Node} (to which the anchor is bound) and
			 * not on an individual attached anchored {@link Node}.
			 */
			ANCHORAGE,
			/**
			 * Indicates that the parameter value may not be shared, i.e. an
			 * individual value is required to compute the position for each
			 * attached {@link AnchorKey}, e.g. because the value depends on the
			 * anchored node.
			 */
			ANCHORED
		};

		/**
		 * Retrieves a parameter of the respective type from the set of given
		 * parameters.
		 *
		 * @param <T>
		 *            The runtime type of the parameter.
		 * @param parameters
		 *            The set of parameters to search.
		 * @param parameterType
		 *            The parameter type
		 * @return The parameter or <code>null</code>.
		 */
		@SuppressWarnings("unchecked")
		protected static <T extends Parameter<?>> T get(
				Collection<? extends Parameter<?>> parameters,
				Class<T> parameterType) {
			Set<T> parametersOfType = new HashSet<>();
			for (Parameter<?> p : parameters) {
				if (parameterType.equals(p.getClass())) {
					parametersOfType.add((T) p);
				}
			}
			if (parametersOfType.isEmpty()) {
				return null;
			} else if (parametersOfType.size() > 1) {
				// this should already be guarded, but we provide an additional
				// check here
				throw new IllegalArgumentException(
						"The given set of parameters contains "
								+ parameters.size() + " parameters of type "
								+ parameterType.getSimpleName() + ": "
								+ parameters);
			} else {
				// TODO: create one if needed (using default constructor)
				return parametersOfType.iterator().next();
			}
		}

		/**
		 * Returns the {@link Kind} returned by an instance of the given
		 * {@link Parameter} type.
		 *
		 * @param paramType
		 *            The {@link Parameter} type for which to return the
		 *            {@link Kind}.
		 * @return The {@link Kind} for the given {@link Parameter} type.
		 */
		public static Kind getKind(Class<? extends Parameter<?>> paramType) {
			try {
				return paramType.newInstance().getKind();
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Cannot instantiate parameter of type "
								+ paramType.getSimpleName() + ".",
						e);
			}
		}

		/**
		 * Returns <code>true</code> if an instance of the given
		 * {@link Parameter} type is optional. Otherwise returns
		 * <code>false</code>.
		 *
		 * @param paramType
		 *            The {@link Parameter} type for which to determine
		 *            optionality.
		 * @return <code>true</code> if an instance of the given
		 *         {@link Parameter} type is optional, otherwise
		 *         <code>false</code>.
		 */
		public static boolean isOptional(
				Class<? extends Parameter<?>> paramType) {
			try {
				return paramType.newInstance().isOptional();
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Cannot instantiate parameter of type "
								+ paramType.getSimpleName() + ".",
						e);
			}
		}

		private Kind kind;

		private boolean optional;

		private ObservableValue<? extends T> bindingTarget;

		/**
		 * Creates a new mandatory {@link Parameter} of the given kind.
		 *
		 * @param kind
		 *            The parameter kind.
		 */
		public Parameter(Kind kind) {
			this(kind, false);
		}

		/**
		 * Creates a new optional parameter of the given kind.
		 *
		 * @param kind
		 *            The parameter kin.
		 *
		 * @param optional
		 *            Whether this parameter is optional or not.
		 */
		public Parameter(Kind kind, boolean optional) {
			this.kind = kind;
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
		}

		/**
		 * Retrieves the {@link Kind} of this parameter, which indicates whether
		 * a single value may be shared to compute the positions of all attached
		 * {@link AnchorKey}s or not.
		 *
		 * @return The parameter {@link Kind}.
		 */
		public final Kind getKind() {
			return kind;
		}

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
		public final boolean isOptional() {
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