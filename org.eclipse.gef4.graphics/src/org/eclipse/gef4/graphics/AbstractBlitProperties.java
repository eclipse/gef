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
package org.eclipse.gef4.graphics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.graphics.filters.IImageFilter;

/**
 * The AbstractBlitProperties partially implements the {@link IBlitProperties}
 * interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractBlitProperties implements IBlitProperties {

	/**
	 * The current {@link IBlitProperties.InterpolationHint} associated with
	 * this {@link AbstractBlitProperties}.
	 */
	protected InterpolationHint interpolationHint = IBlitProperties.DEFAULT_INTERPOLATION_HINT;

	protected List<IImageFilter> filters = new ArrayList<IImageFilter>();

	/**
	 * Default constructor, setting the
	 * {@link IBlitProperties.InterpolationHint} of this
	 * {@link AbstractBlitProperties} to
	 * {@link IBlitProperties#DEFAULT_INTERPOLATION_HINT}.
	 */
	protected AbstractBlitProperties() {
	}

	public List<IImageFilter> filters() {
		return filters;
	}

	public InterpolationHint getInterpolationHint() {
		return interpolationHint;
	}

	public IBlitProperties setInterpolationHint(
			InterpolationHint interpolationHint) {
		this.interpolationHint = interpolationHint;
		return this;
	}

}
