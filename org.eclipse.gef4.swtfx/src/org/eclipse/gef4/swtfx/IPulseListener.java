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
package org.eclipse.gef4.swtfx;

/**
 * The IPulseListener interface provides a handler method for pulses generated
 * by a {@link PulseThread}. Many IPulseListeners can be registered on one
 * PulseThread. The PulseThread will invoke the handler method (
 * {@link #handlePulse(long)}) on every pulse.
 * 
 * @author mwienand
 * 
 */
public interface IPulseListener {

	/**
	 * This method is called by a {@link PulseThread} on every pulse. In order
	 * to accommodate inconsistent pulse frequency, you are encouraged to not
	 * expect a fixed pulse rate, but rather use the passed-in elapsed time (in
	 * millis) in your computations, making them frame rate independent.
	 * 
	 * @param elapsedMs
	 *            elapsed time since last pulse (in millis)
	 */
	void handlePulse(long elapsedMs);

}
