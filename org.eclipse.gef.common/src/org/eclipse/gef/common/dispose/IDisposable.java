/******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.common.dispose;

/**
 * An {@link IDisposable} needs to be disposed after it is no longer needed.
 * 
 * @author anyssen
 *
 */
public interface IDisposable {

	/**
	 * Called to dispose the {@link IDisposable}.
	 */
	public void dispose();
}
