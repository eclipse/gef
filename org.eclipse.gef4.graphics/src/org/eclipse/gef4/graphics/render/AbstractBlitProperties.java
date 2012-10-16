/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.render;


/**
 * The AbstractBlitProperties partially implements the {@link IBlitProperties}
 * interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractBlitProperties extends AbstractGraphicsProperties
		implements IBlitProperties {

	/**
	 * The current {@link IBlitProperties.InterpolationHint} associated with
	 * this {@link AbstractBlitProperties}.
	 */
	protected InterpolationHint interpolationHint = IBlitProperties.DEFAULT_INTERPOLATION_HINT;

	/**
	 * Default constructor, setting the
	 * {@link IBlitProperties.InterpolationHint} of this
	 * {@link AbstractBlitProperties} to
	 * {@link IBlitProperties#DEFAULT_INTERPOLATION_HINT}.
	 */
	protected AbstractBlitProperties() {
	}

	public InterpolationHint getInterpolationHint() {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IBlitProperties is denied, because it is currently deactivated.");
		}
		return interpolationHint;
	}

	public IBlitProperties setInterpolationHint(
			InterpolationHint interpolationHint) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Access to this IBlitProperties is denied, because it is currently deactivated.");
		}
		this.interpolationHint = interpolationHint;
		return this;
	}

}
