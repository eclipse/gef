/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.policies.AbstractTransformPolicy;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link FXTransformPolicy} is a JavaFX-specific
 * {@link AbstractTransformPolicy} that handles the transformation of its
 * {@link #getHost() host}.
 * <p>
 * When working with transformations, the order in which the individual
 * transformations are concatenated is important. The transformation that is
 * concatenated last will be applied first. For example, the rotation around a
 * pivot point consists of 3 steps:
 * <ol>
 * <li>Translate the coordinate system, so that the pivot point is in the origin
 * <code>(-px, -py)</code>.
 * <li>Rotate the coordinate system.
 * <li>Translate back to the original position <code>(px, py)</code>.
 * </ol>
 * But the corresponding transformations have to be concatenated in reverse
 * order, i.e. translate back first, rotate then, translate pivot to origin
 * last. This is easy to confuse, that's why this policy manages a list of
 * pre-transforms and a list of post-transforms. These transformations (as well
 * as the initial node transformation) are concatenated as follows to yield the
 * new node transformation for the host:
 *
 * <pre>
 *            --&gt; --&gt; --&gt;  direction of concatenation --&gt; --&gt; --&gt;
 *
 *            postTransforms  initialNodeTransform  preTransforms
 *            |------------|                        |-----------|
 * postIndex: n, n-1, ...  0              preIndex: 0, 1,  ...  m
 *
 *            &lt;-- &lt;-- &lt;-- &lt;-- direction of effect &lt;-- &lt;-- &lt;-- &lt;--
 * </pre>
 * <p>
 * As you can see, the last pre-transform is concatenated last, and therefore,
 * will affect the host first. Generally, a post-transform manipulates the
 * transformed node, while a pre-transform manipulates the coordinate system
 * before the node is transformed.
 * <p>
 * You can use the {@link #createPreTransform()} and
 * {@link #createPostTransform()} methods to create a pre- or a post-transform
 * and append it to the respective list. Therefore, the most recently created
 * pre-transform will be applied first, and the most recently created
 * post-transform will be applied last. When creating a pre- or post-transform,
 * the index of that transform within the respective list will be returned. This
 * index can later be used to manipulate the transform.
 * <p>
 * The {@link #setPostRotate(int, Angle)},
 * {@link #setPostScale(int, double, double)},
 * {@link #setPostTransform(int, AffineTransform)},
 * {@link #setPostTranslate(int, double, double)},
 * {@link #setPreRotate(int, Angle)}, {@link #setPreScale(int, double, double)},
 * {@link #setPreTransform(int, AffineTransform)}, and
 * {@link #setPreTranslate(int, double, double)} methods can be used to change a
 * previously created pre- or post-transform.
 *
 * @author mwienand
 *
 */
public class FXTransformPolicy extends AbstractTransformPolicy<Node> {

	private static final String TRANSFORMATION_PROVIDER_ROLE = "transformationProvider";

	/**
	 * The adapter key for the <code>Provider&lt;Affine&gt;</code> that will be
	 * used to obtain the host's {@link Affine} transformation.
	 */
	@SuppressWarnings("serial")
	public static final AdapterKey<Provider<? extends Affine>> TRANSFORM_PROVIDER_KEY = AdapterKey
			.get(new TypeToken<Provider<? extends Affine>>() {
			}, TRANSFORMATION_PROVIDER_ROLE);

	@Override
	protected FXTransformOperation createOperation() {
		return new FXTransformOperation(
				getHost().getAdapter(TRANSFORM_PROVIDER_KEY).get());
	}

	@Override
	public AffineTransform getCurrentNodeTransform() {
		return JavaFX2Geometry.toAffineTransform(
				getHost().getAdapter(TRANSFORM_PROVIDER_KEY).get());
	}

	/**
	 * Returns an {@link FXTransformOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return An {@link FXTransformOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected FXTransformOperation getTransformOperation() {
		return (FXTransformOperation) super.getOperation();
	}

	@Override
	protected void updateOperation(AffineTransform newTransform) {
		getTransformOperation()
				.setNewTransform(Geometry2JavaFX.toFXAffine(newTransform));
	}

}
