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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.geometry.Orientation;

/**
 * Abstract base implementation for {@link IComputationStrategy computation
 * strategies}.
 *
 * @author anyssen
 */
public abstract class AbstractComputationStrategy
		implements IComputationStrategy {

	/**
	 * An {@link IComputationStrategy.Parameter} that encapsulates an
	 * (anchorage) reference geometry.
	 */
	public static class AnchorageReferenceGeometry
			extends Parameter<IGeometry> {

		/**
		 * Creates a new {@link AnchorageReferenceGeometry} with no default
		 * value.
		 */
		public AnchorageReferenceGeometry() {
			this(new Rectangle());
		}

		/**
		 * Creates a {@link AnchorageReferenceGeometry} that encapsulates the
		 * given {@link IGeometry}.
		 *
		 * @param defaultValue
		 *            The {@link IGeometry} to use by default.
		 */
		public AnchorageReferenceGeometry(IGeometry defaultValue) {
			super(Kind.STATIC);
			set(defaultValue);
		}
	}

	/**
	 * An {@link IComputationStrategy.Parameter} that encapsulates an
	 * (anchorage) reference point.
	 */
	public static class AnchorageReferencePosition extends Parameter<Point> {

		/**
		 * Creates a new {@link AnchorageReferencePosition} without default
		 * value.
		 */
		public AnchorageReferencePosition() {
			this(null);
		}

		/**
		 * Creates a {@link AnchorageReferencePosition} that encapsulates the
		 * given {@link Point}.
		 *
		 * @param defaultValue
		 *            The {@link Point} to encapsulate.
		 */
		public AnchorageReferencePosition(Point defaultValue) {
			super(Kind.STATIC);
			set(defaultValue);
		}
	}

	/**
	 * An {@link IComputationStrategy.Parameter} that encapsulates a projection
	 * reference point.
	 */
	public static class AnchoredReferencePoint extends Parameter<Point> {

		/**
		 * Creates a new {@link AnchoredReferencePoint} with no default value.
		 */
		public AnchoredReferencePoint() {
			this(new Point());
		}

		/**
		 * Creates a {@link AnchoredReferencePoint} that encapsulates the given
		 * {@link Point}.
		 *
		 * @param defaultValue
		 *            The {@link Point} to encapsulate.
		 */
		public AnchoredReferencePoint(Point defaultValue) {
			super(Kind.DYNAMIC);
			set(defaultValue);
		}
	}

	/**
	 * An {@link IComputationStrategy.Parameter} that encapsulates the preferred
	 * orientation to be used for orthogonal projections.
	 */
	public static class PreferredOrientation extends Parameter<Orientation> {

		/**
		 * Creates a new {@link PreferredOrientation} without default value.
		 */
		public PreferredOrientation() {
			this(Orientation.VERTICAL);
		}

		/**
		 * Creates a {@link PreferredOrientation} that encapsulates the given
		 * {@link Orientation}.
		 *
		 * @param orientation
		 *            The {@link Orientation} to encapsulate.
		 */
		public PreferredOrientation(Orientation orientation) {
			super(Kind.DYNAMIC, true); // optional
			set(orientation);
		}

	}

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
	protected static <T extends Parameter<?>> T getParameter(
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
					"The given set of parameters contains " + parameters.size()
							+ " parameters of type "
							+ parameterType.getSimpleName() + ": "
							+ parameters);
		} else {
			// TODO: create one if needed (using default constructor)
			return parametersOfType.iterator().next();
		}
	}
}