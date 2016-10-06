package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.parts.ITransformableVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 *
 *
 * @author wienand
 *
 * @param <V>
 *            the visual type parameter.
 */
// FIXME: no-implement
public interface IFXTransformableVisualPart<V extends Node>
		extends ITransformableVisualPart<Node, V> {

	/**
	 * The role for the adapter key of the <code>Provider&lt;Affine&gt;</code>
	 * that will be used to obtain the part's {@link Affine} transformation.
	 */
	public static final String TRANSFORMATION_PROVIDER_ROLE = "transformationProvider";

	/**
	 * The adapter key for the <code>Provider&lt;Affine&gt;</code> that will be
	 * used to obtain the host's {@link Affine} transformation.
	 */
	@SuppressWarnings("serial")
	public static final AdapterKey<Provider<? extends Affine>> TRANSFORM_PROVIDER_KEY = AdapterKey
			.get(new TypeToken<Provider<? extends Affine>>() {
			}, IFXTransformableVisualPart.TRANSFORMATION_PROVIDER_ROLE);

	@Override
	default Node getTransformableVisual() {
		return getVisual();
	}

	@Override
	default AffineTransform getVisualTransform() {
		return FX2Geometry.toAffineTransform(
				getAdapter(IFXTransformableVisualPart.TRANSFORM_PROVIDER_KEY)
						.get());
	}

	@Override
	default void transformVisual(AffineTransform transformation) {
	}

}
