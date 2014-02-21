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
package org.eclipse.gef4.mvc.policies;

public interface IPinchPolicy<V> extends IPolicy<V> {

	/**
	 * Empty default implementation.
	 */
	public class Impl<V> extends AbstractPolicy<V> implements
			IPinchPolicy<V> {
		@Override
		public void zoomDetected(double partialFactor) {
		}

		@Override
		public void zoomed(double partialFactor, double totalFactor) {
		}

		@Override
		public void zoomFinished(double totalFactor) {
		}
	}

	/**
	 * Reaction to the detection of pinch or spread gestures.
	 */
	public void zoomDetected(double partialFactor);

	/**
	 * Continuous reaction to pinch or spread gestures. Called continuously on
	 * finger movement after detection and before finish.
	 */
	public void zoomed(double partialFactor, double totalFactor);

	/**
	 * Reaction to the finish of pinch or spread gestures.
	 */
	public void zoomFinished(double totalFactor);

}
