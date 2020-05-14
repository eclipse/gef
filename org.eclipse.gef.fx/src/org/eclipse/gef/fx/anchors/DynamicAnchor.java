/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapWrapper;
import org.eclipse.gef.common.beans.property.ReadOnlySetWrapperEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.fx.anchors.IComputationStrategy.Parameter;
import org.eclipse.gef.fx.anchors.IComputationStrategy.Parameter.Kind;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link DynamicAnchor} computes anchor positions through a
 * {@link IComputationStrategy}. The strategy performs the position calculation
 * based on {@link Parameter}s, which are controlled by the
 * {@link DynamicAnchor}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class DynamicAnchor extends AbstractAnchor {

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
			super(Kind.ANCHORAGE);
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
			super(Kind.ANCHORAGE);
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
			super(Kind.ANCHORED);
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
			super(Kind.ANCHORED, true); // optional
			set(orientation);
		}
	}

	private SetMultimapChangeListener<AnchorKey, IComputationStrategy.Parameter<?>> anchoredComputationParametersChangeListener = new SetMultimapChangeListener<AnchorKey, IComputationStrategy.Parameter<?>>() {

		// keep track of the change listeners registered at the individual
		// parameters
		private Map<AnchorKey, ChangeListener<Object>> valueChangeListeners = new HashMap<>();

		@Override
		public void onChanged(
				final SetMultimapChangeListener.Change<? extends AnchorKey, ? extends Parameter<?>> change) {
			while (change.next()) {
				if (change.wasAdded()) {
					// prevent null from being put into the map
					if (change.getKey() == null) {
						throw new IllegalStateException(
								"Attempt to put <null> key into reference point map!");
					}
					if (change.getValuesAdded().contains(null)) {
						throw new IllegalStateException(
								"Attempt to put <null> value for key "
										+ change.getKey()
										+ " into reference point map!");
					}
					for (Parameter<?> p : change.getValuesAdded()) {
						// add change listener to each added parameter, so we
						// can recompute the position upon changes
						final AnchorKey key = change.getKey();
						ChangeListener<Object> l = valueChangeListeners
								.get(key);
						if (l == null) {
							l = new ChangeListener<Object>() {
								@Override
								public void changed(
										ObservableValue<? extends Object> observable,
										Object oldValue, Object newValue) {
									// if (inUpdatePosition) {
									// deferredUpdates.add(key);
									// } else {
									updatePosition(key);
									// }
								}
							};
							valueChangeListeners.put(key, l);
						}
						p.addListener(l);
					}
				} else if (change.wasRemoved()) {
					// unregister change listener from removed parameter
					for (Parameter<?> p : change.getValuesRemoved()) {
						p.removeListener(
								valueChangeListeners.get(change.getKey()));
					}
				}
				// update position for this key
				updatePosition(change.getKey());
			}
		}
	};

	private IComputationStrategy computationStrategy;

	private ObservableSet<IComputationStrategy.Parameter<?>> anchorageComputationParameters = FXCollections
			.observableSet(new HashSet<IComputationStrategy.Parameter<?>>());
	private ReadOnlySetWrapper<IComputationStrategy.Parameter<?>> anchorageComputationParametersProperty = new ReadOnlySetWrapperEx<>(
			anchorageComputationParameters);
	private ObservableSetMultimap<AnchorKey, IComputationStrategy.Parameter<?>> anchoredComputationParameters = CollectionUtils
			.observableHashMultimap();

	private ReadOnlySetMultimapWrapper<AnchorKey, IComputationStrategy.Parameter<?>> anchoredComputationParametersProperty = new ReadOnlySetMultimapWrapper<>(
			anchoredComputationParameters);

	private SetChangeListener<IComputationStrategy.Parameter<?>> anchorageComputationParametersChangeListener = new SetChangeListener<IComputationStrategy.Parameter<?>>() {

		private ChangeListener<Object> valueChangeListener = new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				// recompute positions for all anchor keys
				updatePositions();
			}
		};

		@Override
		public void onChanged(
				final SetChangeListener.Change<? extends Parameter<?>> change) {
			if (change.wasRemoved()) {
				change.getElementRemoved().removeListener(valueChangeListener);
			}
			if (change.wasAdded()) {
				change.getElementAdded().addListener(valueChangeListener);
			}
			// if the list of anchorage parameters was changed, recompute
			// positions
			updatePositions();
		}
	};

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual
	 * that uses a {@link ChopBoxStrategy} as computation strategy. The anchor
	 * will also add a default binding for the
	 * {@link AnchorageReferenceGeometry} computation parameter, which is
	 * required by the {@link ChopBoxStrategy}, that infers the geometry from
	 * the anchorage's shape outline.
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 */
	public DynamicAnchor(final Node anchorage) {
		this(anchorage, new ChopBoxStrategy());
	}

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual
	 * using the given {@link IComputationStrategy}. The anchor will also add a
	 * default binding for the {@link AnchorageReferenceGeometry} computation
	 * parameter, inferring the geometry from the anchorage's shape outline, in
	 * case this parameter is required by the given
	 * {@link IComputationStrategy}.
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} to use.
	 */
	public DynamicAnchor(final Node anchorage,
			IComputationStrategy computationStrategy) {
		super(anchorage);
		anchorageComputationParameters
				.addListener(anchorageComputationParametersChangeListener);
		anchoredComputationParameters
				.addListener(anchoredComputationParametersChangeListener);
		// XXX: Set computation strategy after adding parameter change
		// listeners, because setting the computation strategy does initialize
		// some parameters, for which otherwise no change listeners would be
		// registered.
		setComputationStrategy(computationStrategy);

		// add default binding for the anchorage reference geometry (if required
		// by the given computation strategy)
		if (computationStrategy.getRequiredParameters()
				.contains(AnchorageReferenceGeometry.class)) {
			getComputationParameter(AnchorageReferenceGeometry.class)
					.bind(new ObjectBinding<IGeometry>() {
						{
							bind(anchorage.layoutBoundsProperty());
						}

						@Override
						protected IGeometry computeValue() {
							return NodeUtils.getShapeOutline(anchorage);
						}
					});
		}
	}

	/**
	 * Returns a {@link ReadOnlySetProperty} that provides the
	 * {@link IComputationStrategy.Parameter computation parameters} of kind
	 * {@link Kind#ANCHORAGE}.
	 *
	 * @return A {@link ReadOnlySetProperty} providing the {@link Parameter}s.
	 */
	protected ReadOnlySetProperty<IComputationStrategy.Parameter<?>> anchorageComputationParametersProperty() {
		return anchorageComputationParametersProperty.getReadOnlyProperty();
	}

	/**
	 * Returns a {@link ReadOnlySetMultimapProperty} that provides the
	 * {@link IComputationStrategy.Parameter computation parameters} of kind
	 * {@link Kind#ANCHORED} per {@link AnchorKey}. The set of computation
	 * parameters for each {@link AnchorKey} is initialed by the responsible
	 * computation strategy.
	 *
	 * @return A {@link ReadOnlySetMultimapProperty} that provides an
	 *         {@link Object} per {@link AnchorKey}.
	 */
	protected ReadOnlySetMultimapProperty<AnchorKey, IComputationStrategy.Parameter<?>> anchoredComputationParametersProperty() {
		return anchoredComputationParametersProperty.getReadOnlyProperty();
	}

	@Override
	public void attach(AnchorKey key) {
		initAnchoredParameters(key);
		super.attach(key);
	}

	private void clearAnchoredParameters(AnchorKey key) {
		anchoredComputationParameters.removeAll(key);
	}

	/**
	 * Recomputes the position for the given attached {@link AnchorKey} by
	 * delegating to the respective {@link IComputationStrategy}.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which to compute an anchor position.
	 * @return The point for the given {@link AnchorKey} in local coordinates of
	 *         the anchored {@link Node}.
	 */
	@Override
	protected Point computePosition(AnchorKey key) {
		// check for availability of (anchorage) parameters
		Set<IComputationStrategy.Parameter<?>> parameters = getParameters(key);
		for (Class<? extends Parameter<?>> parameterType : computationStrategy
				.getRequiredParameters()) {
			Parameter<?> p = Parameter.get(parameters, parameterType);
			// check that parameter values are provided
			if (p == null || (p.get() == null && !p.isOptional())) {
				// as long as all required parameters are not provided, we
				// cannot compute a position.
				// System.out.println("Skipping computation of position for key
				// "
				// + key + " because mandatory parameter " + p
				// + " has no value.");
				return null;
			}
		}

		// only invoke strategy if all required parameters are provided
		Point positionInScene = computationStrategy.computePositionInScene(
				getAnchorage(), key.getAnchored(), parameters);
		Point position = FX2Geometry.toPoint(key.getAnchored()
				.sceneToLocal(Geometry2FX.toFXPoint(positionInScene)));
		return position;
	}

	@Override
	public void detach(AnchorKey key) {
		super.detach(key);
		clearAnchoredParameters(key);
	}

	/**
	 * Retrieves a computation parameter of the respective type for the given
	 * {@link AnchorKey}.
	 *
	 * @param <T>
	 *            The value type of the computation parameter.
	 * @param key
	 *            The {@link AnchorKey} for which to retrieve the anchored
	 *            parameter.
	 * @param parameterType
	 *            The type of computation parameter.
	 * @return The anchored computation parameter.
	 */
	public <T extends Parameter<?>> T getComputationParameter(AnchorKey key,
			Class<T> parameterType) {

		// check anchorage parameters
		T parameter = Parameter.get(anchorageComputationParametersProperty(),
				parameterType);
		if (parameter != null) {
			return parameter;
		}

		// check anchored parameters
		parameter = Parameter.get(
				anchoredComputationParametersProperty().get(key),
				parameterType);
		if (parameter != null) {
			return parameter;
		}

		// create a new parameter instance
		try {
			parameter = parameterType.getDeclaredConstructor().newInstance();
			if (Kind.ANCHORED.equals(parameter.getKind())) {
				anchoredComputationParametersProperty().put(key, parameter);
			} else {
				anchorageComputationParametersProperty().add(parameter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameter;
	}

	/**
	 * Retrieves a computation parameter of the respective type.
	 *
	 * @param <T>
	 *            The value type of the computation parameter.
	 * @param parameterType
	 *            The type of computation parameter.
	 * @return The anchored computation parameter.
	 */
	public <T extends Parameter<?>> T getComputationParameter(
			Class<T> parameterType) {

		// check anchorage parameters
		T parameter = Parameter.get(anchorageComputationParametersProperty(),
				parameterType);
		if (parameter != null) {
			return parameter;
		}

		// create a new parameter instance
		try {
			parameter = parameterType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Kind.ANCHORAGE.equals(parameter.getKind())) {
			anchorageComputationParametersProperty().add(parameter);
		} else {
			throw new IllegalArgumentException("Specified parameter type "
					+ parameterType.getSimpleName()
					+ " is anchored, it has to be queried per AdapterKey.");
		}
		return parameter;
	}

	/**
	 * Returns the {@link IComputationStrategy} used by this
	 * {@link DynamicAnchor}.
	 *
	 * @return The {@link IComputationStrategy}.
	 */
	public IComputationStrategy getComputationStrategy() {
		return computationStrategy;
	}

	/**
	 * Retrieves the relevant parameters for the computation of the given
	 * {@link AnchorKey}.
	 *
	 * @param key
	 *            The {@link AnchorKey} of relevance.
	 * @return The parameters required by the computation strategy to compute
	 *         the position for the given {@link AnchorKey}.
	 *
	 */
	protected Set<Parameter<?>> getParameters(AnchorKey key) {
		Set<Parameter<?>> parameters = new HashSet<>();
		parameters.addAll(anchorageComputationParameters);
		parameters.addAll(anchoredComputationParameters.get(key));
		return parameters;
	}

	private void initAnchorageParameters() {
		for (Class<? extends Parameter<?>> paramType : computationStrategy
				.getRequiredParameters()) {
			if (Kind.ANCHORAGE.equals(Parameter.getKind(paramType))) {
				if (Parameter.get(anchorageComputationParameters,
						paramType) == null) {
					// parameter is not already contained
					try {
						Parameter<?> p = paramType.getDeclaredConstructor()
								.newInstance();
						anchorageComputationParameters.add(p);
					} catch (Exception e) {
						throw new IllegalStateException(
								"Could not create instance of parameter type "
										+ paramType,
								e);
					}
				}
			}
		}
	}

	private void initAnchoredParameters(AnchorKey key) {
		Set<Parameter<?>> parameters = getParameters(key);
		for (Class<? extends Parameter<?>> paramType : computationStrategy
				.getRequiredParameters()) {
			if (Kind.ANCHORED.equals(Parameter.getKind(paramType))) {
				if (Parameter.get(parameters, paramType) == null) {
					// parameter is not already contained
					Parameter<?> p;
					try {
						p = paramType.getDeclaredConstructor().newInstance();
						if (Kind.ANCHORED.equals(p.getKind())) {
							anchoredComputationParameters.put(key, p);
						}
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException
							| SecurityException e) {
						throw new IllegalStateException(
								"Could not create instance of parameter type "
										+ paramType,
								e);
					}
				}
			}
		}
	}

	/**
	 * Sets the given {@link IComputationStrategy} to be used by this
	 * {@link IAnchor}.
	 *
	 * @param computationStrategy
	 *            The {@link IComputationStrategy} that will be used to compute
	 *            positions for all attached {@link AnchorKey}s.
	 */
	public void setComputationStrategy(
			IComputationStrategy computationStrategy) {
		for (AnchorKey key : getKeys()) {
			clearAnchoredParameters(key);
		}
		this.computationStrategy = computationStrategy;
		initAnchorageParameters();
		for (AnchorKey key : getKeys()) {
			initAnchoredParameters(key);
		}
	}
}
