/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - implement additional validation checks (bug #477980)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.dot.internal.language.arrowtype.AbstractArrowShape;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowShape;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage;
import org.eclipse.gef.dot.internal.language.arrowtype.DeprecatedArrowShape;
import org.eclipse.gef.dot.internal.language.arrowtype.PrimitiveShape;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotArrowTypeValidator extends AbstractDotArrowTypeValidator {

	/**
	 * Issue code for a deprecated arrow shape.
	 */
	public static final String DEPRECATED_ARROW_SHAPE = "deprecated_arrow_shape";

	/**
	 * Issue code for an invalid modifier.
	 */
	public static final String INVALID_ARROW_SHAPE_MODIFIER = "invalid_arrow_shape_modifier";

	/**
	 * Issue code for an invalid arrow shape (none should not be the last arrow
	 * shape).
	 */
	public static final String INVALID_ARROW_SHAPE_NONE_IS_THE_LAST = "invalid_arrow_shape_none_is_the_last";

	/**
	 * Checks that the open modifier is not used in combination with illegal
	 * primitive shapes.
	 *
	 * @param arrowShape
	 *            The arrowShape element to check.
	 */
	@Check
	public void checkOpenModifier(ArrowShape arrowShape) {
		PrimitiveShape shape = arrowShape.getShape();
		if (arrowShape.isOpen() && (PrimitiveShape.CROW.equals(shape)
				|| PrimitiveShape.CURVE.equals(shape)
				|| PrimitiveShape.ICURVE.equals(shape)
				|| PrimitiveShape.NONE.equals(shape)
				|| PrimitiveShape.TEE.equals(shape)
				|| PrimitiveShape.VEE.equals(shape))) {
			reportRangeBasedWarning(INVALID_ARROW_SHAPE_MODIFIER,
					"The open modifier 'o' may not be combined with primitive shape '"
							+ shape + "'.",
					arrowShape, ArrowtypePackage.Literals.ARROW_SHAPE__OPEN);
		}
	}

	/**
	 * Checks that the side modifier is not used in combination with illegal
	 * primitive shapes.
	 *
	 * @param arrowShape
	 *            The arrowShape element to check.
	 */
	@Check
	public void checkSideModifier(ArrowShape arrowShape) {
		PrimitiveShape shape = arrowShape.getShape();
		if (arrowShape.getSide() != null && (PrimitiveShape.DOT.equals(shape)
				|| PrimitiveShape.NONE.equals(shape))) {
			reportRangeBasedWarning(INVALID_ARROW_SHAPE_MODIFIER,
					"The side modifier '" + arrowShape.getSide()
							+ "' may not be combined with primitive shape '"
							+ shape + "'.",
					arrowShape, ArrowtypePackage.Literals.ARROW_SHAPE__SIDE);
		}
	}

	/**
	 * Checks that no deprecated arrow shapes are used
	 *
	 * @param arrowShape
	 *            The arrowShape element to check.
	 */
	@Check
	public void checkDeprecatedArrowShape(DeprecatedArrowShape arrowShape) {
		reportRangeBasedWarning(DEPRECATED_ARROW_SHAPE,
				"The shape '" + arrowShape.getShape() + "' is deprecated.",
				arrowShape,
				ArrowtypePackage.Literals.DEPRECATED_ARROW_SHAPE__SHAPE);
	}

	/**
	 * Checks whether none is the last arrow shape, since this would create a
	 * redundant shape
	 *
	 * @param arrowType
	 *            The arrowType element to check.
	 */
	@Check
	public void checkIfNoneIsTheLastArrowShape(ArrowType arrowType) {
		int numberOfArrowShapes = arrowType.getArrowShapes().size();
		if (numberOfArrowShapes > 1) {
			AbstractArrowShape lastShape = arrowType.getArrowShapes()
					.get(numberOfArrowShapes - 1);
			if (lastShape instanceof ArrowShape && ((ArrowShape) lastShape)
					.getShape() == PrimitiveShape.NONE) {
				reportRangeBasedWarning(INVALID_ARROW_SHAPE_NONE_IS_THE_LAST,
						"The shape '" + PrimitiveShape.NONE
								+ "' may not be the last shape.",
						lastShape,
						ArrowtypePackage.Literals.ARROW_SHAPE__SHAPE);
			}
		}
	}

	private void reportRangeBasedWarning(String issueCode, String message,
			EObject object, EStructuralFeature feature) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(object, feature);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		String code = null;
		// the issueData will be evaluated by the quickfixes
		List<String> issueData = new ArrayList<>();
		issueData.add(issueCode);
		switch (issueCode) {
		case DEPRECATED_ARROW_SHAPE:
			DeprecatedArrowShape arrowShape = (DeprecatedArrowShape) object;
			issueData.add(arrowShape.getShape().toString());
			break;
		case INVALID_ARROW_SHAPE_MODIFIER:
			if (ArrowtypePackage.Literals.ARROW_SHAPE__OPEN == feature) {
				issueData.add("o");
			}
			if (ArrowtypePackage.Literals.ARROW_SHAPE__SIDE == feature) {
				issueData.add(((ArrowShape) object).getSide());
			}
			issueData.add(Integer.toString(offset));
		default:
			break;
		}

		getMessageAcceptor().acceptWarning(message, object, offset, length,
				code, issueData.toArray(new String[0]));
	}

}
