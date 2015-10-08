package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.policies.FXTransformConnectionPolicy;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;

public class FXTransformCurvePolicy extends FXTransformConnectionPolicy {

	@Override
	public ITransactionalOperation commit() {
		return getHost().chainModelChanges(super.commit());
	}

	@Override
	public FXGeometricCurvePart getHost() {
		return (FXGeometricCurvePart) super.getHost();
	}
}
