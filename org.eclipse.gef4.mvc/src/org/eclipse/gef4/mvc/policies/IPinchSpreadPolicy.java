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

public interface IPinchSpreadPolicy<V> extends IPolicy<V> {

	public class Impl<V> extends AbstractPolicy<V> implements IPinchSpreadPolicy<V> {

		@Override
		public void pinchDetected(double partialFactor, double totalFactor) {
		}

		@Override
		public void pinch(double partialFactor, double totalFactor) {
		}

		@Override
		public void pinchFinished(double partialFactor, double totalFactor) {
		}

		@Override
		public void spreadDetected(double partialFactor, double totalFactor) {
		}

		@Override
		public void spread(double partialFactor, double totalFactor) {
		}

		@Override
		public void spreadFinished(double partialFactor, double totalFactor) {
		}
		
	}
	
	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 */
	public void pinchDetected(double partialFactor, double totalFactor);

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	public void pinch(double partialFactor, double totalFactor);

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 */
	public void pinchFinished(double partialFactor, double totalFactor);

	/**
	 * Reaction to the detection of spread (open fingers) gestures.
	 */
	public void spreadDetected(double partialFactor, double totalFactor);

	/**
	 * Continuous reaction to spread (open fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	public void spread(double partialFactor, double totalFactor);

	/**
	 * Reaction to the finish of spread (open fingers) gestures.
	 */
	public void spreadFinished(double partialFactor, double totalFactor);

}
