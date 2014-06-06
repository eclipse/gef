/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IZoomPolicy;
import org.eclipse.gef4.mvc.policies.IPolicy;

public class AbstractZoomTool<VR> extends AbstractTool<VR> {

	@SuppressWarnings("rawtypes")
	public static final Class<? extends IPolicy> TOOL_POLICY_KEY = IZoomPolicy.class;

	@SuppressWarnings({ "unchecked"})
	protected IZoomPolicy<VR> getToolPolicy(IVisualPart<VR> targetPart) {
		return (IZoomPolicy<VR>) targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 */
	protected void zoomDetected(List<IVisualPart<VR>> targetParts,
			double partialFactor, double totalFactor) {
		for (IVisualPart<VR> targetPart : targetParts) {
			IZoomPolicy<VR> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.zoomDetected(partialFactor);
			}
		}
	}

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	protected void zoomed(List<IVisualPart<VR>> targetParts, double partialFactor,
			double totalFactor) {
		for (IVisualPart<VR> targetPart : targetParts) {
			IZoomPolicy<VR> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.zoomed(partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 */
	protected void zoomFinished(List<IVisualPart<VR>> targetParts,
			double partialFactor, double totalFactor) {
		for (IVisualPart<VR> targetPart : targetParts) {
			IZoomPolicy<VR> policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.zoomFinished(totalFactor);
			}
		}
	}

}
