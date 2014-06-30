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
package org.eclipse.gef4.fx.anchors;

import org.eclipse.gef4.geometry.planar.Point;

/**
 * AnchorLink combines IFXAnchor and AnchorKey to represent one anchor target.
 * 
 * @author mwienand
 * 
 */
public class AnchorLink {

	private IFXAnchor anchor;
	private AnchorKey key;

	public AnchorLink(IFXAnchor anchor, AnchorKey key) {
		setAnchor(anchor);
		setKey(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnchorLink) {
			AnchorLink o = (AnchorLink) obj;
			return o.getAnchor().equals(anchor) && o.getKey().equals(key);
		}
		return false;
	}

	public IFXAnchor getAnchor() {
		return anchor;
	}

	public AnchorKey getKey() {
		return key;
	}

	/**
	 * Convenience method to retrieve the anchor position for the particular key
	 * associated with this AnchorLink.
	 * 
	 * @return
	 */
	public Point getPosition() {
		return anchor.getPosition(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anchor.hashCode();
		result = prime * result + key.hashCode();
		return result;
	}

	public void setAnchor(IFXAnchor anchor) {
		if (anchor == null) {
			throw new IllegalArgumentException(
					"The given Anchor may not be <null>.");
		}
		this.anchor = anchor;
	}

	public void setKey(AnchorKey key) {
		if (key == null) {
			throw new IllegalArgumentException(
					"The given Key may not be <null>.");
		}
		this.key = key;
	}

}
