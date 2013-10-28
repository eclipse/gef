/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.animation;

/**
 * <p>
 * An IInterpolator is used to generate interpolation factors in range
 * <code>[0;1]</code> which are used to interpolate attribute values between two
 * states A and B during a transition.
 * </p>
 * <p>
 * The current time fraction of the transition duration is passed-in to the
 * IInterpolator and the basis for the interpolation factor calculation.
 * </p>
 * <p>
 * For example
 * </p>
 * 
 * An IInterpolator is used to describe a curve: usually
 * <code>X element [0;1]</code> and <code>Y element [0;1]</code>.
 * 
 * @author mwienand
 * 
 */
public interface IInterpolator {

	public static final IInterpolator LINEAR = new IInterpolator() {
		@Override
		public double curve(double t) {
			return t;
		}
	};

	public static final IInterpolator SMOOTH_STEP = new IInterpolator() {
		@Override
		public double curve(double t) {
			return t * t * t * (t * (t * 6 - 15) + 10);
		}
	};

	public static final IInterpolator EASE_IN = new IInterpolator() {
		@Override
		public double curve(double t) {
			return t * t * t;
		}
	};

	public static final IInterpolator EASE_OUT = new IInterpolator() {
		@Override
		public double curve(double t) {
			return Math.cbrt(t);
		}
	};

	/**
	 * Returns a value in range <code>[0;1]</code> depending on the elapsed time
	 * <i>t</i>. Attribute interpolation is computed based on the returned
	 * value.
	 * 
	 * @param t
	 *            time in range <code>[0;1]</code>
	 * @return
	 */
	public double curve(double t);

}
