/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef4.common.beans.property.ReadOnlySetMultimapWrapper;
import org.eclipse.gef4.common.collections.CollectionUtils;
import org.eclipse.gef4.common.collections.ObservableSetMultimap;
import org.eclipse.gef4.common.collections.SetMultimapChangeListener;
import org.eclipse.gef4.fx.anchors.AbstractComputationStrategy.AnchorageReferenceGeometry;
import org.eclipse.gef4.fx.anchors.IComputationStrategy.Parameter;
import org.eclipse.gef4.fx.anchors.IComputationStrategy.Parameter.Kind;
import org.eclipse.gef4.geometry.planar.IGeometry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link DynamicAnchor} computes anchor positions based on a reference
 * position per anchored and one reference position for the anchorage. The
 * anchoreds' reference positions are provided when {@link #attach(AnchorKey)
 * attaching} an {@link AnchorKey}. The computation is carried out by a
 * {@link IComputationStrategy}. The default computation strategy (
 * {@link ProjectionStrategy}) will connect anchored and anchorage reference
 * position and compute the intersection with the outline of the anchorage.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class DynamicAnchor extends AbstractAnchor {

	// TODO: replace with static parameters set
	private AnchorageReferenceGeometry referenceGeometryProperty = new AnchorageReferenceGeometry();

	private Map<AnchorKey, ChangeListener<Object>> computationParameterChangeListeners = new HashMap<>();

	private SetMultimapChangeListener<AnchorKey, IComputationStrategy.Parameter<?>> computationParametersChangeListener = new SetMultimapChangeListener<AnchorKey, IComputationStrategy.Parameter<?>>() {

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
						ChangeListener<Object> l = computationParameterChangeListeners
								.get(key);
						if (l == null) {
							l = new ChangeListener<Object>() {
								@Override
								public void changed(
										ObservableValue<? extends Object> observable,
										Object oldValue, Object newValue) {
									if (isAttached(key)) {
										updatePosition(key);
									}
								}
							};
							computationParameterChangeListeners.put(key, l);
						}
						p.addListener(l);
					}
				} else if (change.wasRemoved()) {
					// unregister change listener from removed parameter
					for (Parameter<?> p : change.getValuesRemoved()) {
						p.removeListener(computationParameterChangeListeners
								.get(change.getKey()));
					}
				}
				// update position for this key
				if (isAttached(change.getKey())) {
					updatePosition(change.getKey());
				}
			}
		}
	};

	private ObservableSetMultimap<AnchorKey, IComputationStrategy.Parameter<?>> dynamicComputationParameters = CollectionUtils
			.observableHashMultimap();

	private ReadOnlySetMultimapWrapper<AnchorKey, IComputationStrategy.Parameter<?>> dynamicComputationParametersProperty = new ReadOnlySetMultimapWrapper<>(
			dynamicComputationParameters);

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual.
	 * Uses the default computation strategy ( {@link ProjectionStrategy} ).
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 */
	public DynamicAnchor(Node anchorage) {
		this(anchorage, new ChopBoxStrategy());
	}

	/**
	 * Constructs a new {@link DynamicAnchor} for the given anchorage visual
	 * using the given {@link IComputationStrategy}.
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 * @param defaultComputationStrategy
	 *            The default {@link IComputationStrategy} to use.
	 */
	public DynamicAnchor(Node anchorage,
			IComputationStrategy defaultComputationStrategy) {
		super(anchorage, defaultComputationStrategy);
		// init static parameters
		staticComputationParametersProperty().add(referenceGeometryProperty);
		dynamicComputationParameters
				.addListener(computationParametersChangeListener);
	}

	@Override
	public void attach(AnchorKey key) {
		initDynamicParameters(key);
		super.attach(key);
	}

	private void clearDynamicParameters(AnchorKey key) {
		dynamicComputationParameters.removeAll(key);
	}

	@Override
	public void detach(AnchorKey key) {
		super.detach(key);
		clearDynamicParameters(key);
	}

	/**
	 * Returns a {@link ReadOnlySetMultimapProperty} that provides the
	 * {@link IComputationStrategy.Parameter computation parameters} per
	 * {@link AnchorKey}. The set of computation parameters for each
	 * {@link AnchorKey} is initialed by the responsible computation strategy.
	 *
	 * @return A {@link ReadOnlySetMultimapProperty} that provides an
	 *         {@link Object} per {@link AnchorKey}.
	 */
	protected ReadOnlySetMultimapProperty<AnchorKey, IComputationStrategy.Parameter<?>> dynamicComputationParametersProperty() {
		return dynamicComputationParametersProperty.getReadOnlyProperty();
	}

	/**
	 * Retrieves a computation parameter of the respective type for the given
	 * {@link AnchorKey}.
	 *
	 * @param <T>
	 *            The value type of the computation parameter.
	 * @param key
	 *            The {@link AnchorKey} for which to retrieve the dynamic
	 *            parameter.
	 * @param parameterType
	 *            The type of computation parameter.
	 * @return The dynamic computation parameter.
	 */
	public <T extends Parameter<?>> T getComputationParameter(AnchorKey key,
			Class<T> parameterType) {

		// check static parameters
		T parameter = AbstractComputationStrategy.getParameter(
				staticComputationParametersProperty(), parameterType);
		if (parameter != null) {
			return parameter;
		}

		// check dynamic parameters
		parameter = AbstractComputationStrategy.getParameter(
				dynamicComputationParametersProperty().get(key), parameterType);
		if (parameter != null) {
			return parameter;
		}

		// create a new parameter instance
		try {
			parameter = parameterType.getDeclaredConstructor().newInstance();
			if (Kind.DYNAMIC.equals(parameter.getKind())) {
				dynamicComputationParametersProperty().put(key, parameter);
			} else {
				staticComputationParametersProperty().add(parameter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameter;
	}

	@Override
	protected Set<Parameter<?>> getParameters(AnchorKey key) {
		Set<Parameter<?>> parameters = new HashSet<>();
		parameters.addAll(super.getParameters(key));
		parameters.addAll(dynamicComputationParameters.get(key));
		return parameters;
	}

	/**
	 * Returns the anchorage reference {@link IGeometry geometry} that is to be
	 * used for computations by this {@link DynamicAnchor}'s
	 * {@link IComputationStrategy computation strategy}, specified within the
	 * local coordinate system of the anchorage.
	 *
	 * @return The anchorage reference geometry to be used for computations,
	 *         which by default is the shape's outline geometry.
	 */
	public IGeometry getReferenceGeometry() {
		return referenceGeometryProperty.get();
	}

	private void initDynamicParameters(AnchorKey key) {
		Set<Parameter<?>> parameters = getParameters(key);
		for (Class<? extends Parameter<?>> paramType : getComputationStrategy()
				.getRequiredParameters()) {
			if (AbstractComputationStrategy.getParameter(parameters,
					paramType) == null) {
				// parameter is not already contained
				try {
					Parameter<?> p = paramType.getDeclaredConstructor()
							.newInstance();
					if (Kind.DYNAMIC.equals(p.getKind())) {
						dynamicComputationParameters.put(key, p);
					} else {
						throw new IllegalArgumentException(
								"Required static parameter of type " + paramType
										+ " is not provided.");
					}
				} catch (Exception e) {
					throw new IllegalStateException(
							"Could not create instance of parameter type "
									+ paramType,
							e);
				}
			}
		}
	}

	/**
	 * Returns the {@link ObjectProperty} that manages the reference geometry of
	 * this {@link DynamicAnchor}.
	 *
	 * @return The {@link ObjectProperty} that manages the reference geometry of
	 *         this {@link DynamicAnchor}.
	 */
	// TODO: this has to be transferred into a (static) computation parameter.
	// TODO: we could turn this into an AnchorageReferenceGeometry
	public AnchorageReferenceGeometry referenceGeometryProperty() {
		return referenceGeometryProperty;
	}

	@Override
	public void setComputationStrategy(
			IComputationStrategy computationStrategy) {
		super.setComputationStrategy(computationStrategy);
		for (AnchorKey key : getKeys()) {
			// init will replace all existing values (in an atomic operation)
			initDynamicParameters(key);
		}
	}

	/**
	 * Sets the reference geometry property to the specified value.
	 *
	 * @param geometry
	 *            The new geoemtry value.
	 */
	public void setReferenceGeometry(IGeometry geometry) {
		referenceGeometryProperty.set(geometry);
	}

}
